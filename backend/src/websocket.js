const jwt = require('jsonwebtoken');

const clients = new Map(); // userId -> Set of WebSocket connections

function setupWebSocket(server) {
  const { WebSocketServer } = require('ws');
  
  const wss = new WebSocketServer({ 
    server,
    path: '/ws',
    maxPayload: 1024 * 1024, // 1MB
  });

  wss.on('connection', (ws, req) => {
    console.log('WebSocket client connected');

    let userId = null;

    // Send welcome message
    ws.send(JSON.stringify({ type: 'connected', message: 'Connected to RAFIQ WebSocket' }));

    ws.on('message', (data) => {
      try {
        const message = JSON.parse(data.toString());

        switch (message.type) {
          case 'auth':
            handleAuth(ws, message, (id) => { userId = id; });
            break;

          case 'sos_alert':
            handleSosAlert(ws, message, userId);
            break;

          case 'sos_cancel':
            handleSosCancel(ws, message, userId);
            break;

          case 'guardian_alert':
            handleGuardianAlert(ws, message);
            break;

          case 'typing':
            handleTyping(ws, message, userId);
            break;

          case 'ping':
            ws.send(JSON.stringify({ type: 'pong' }));
            break;

          default:
            ws.send(JSON.stringify({ type: 'error', message: 'Unknown message type' }));
        }
      } catch (error) {
        console.error('WebSocket message error:', error.message);
        ws.send(JSON.stringify({ type: 'error', message: 'Invalid message format' }));
      }
    });

    ws.on('close', () => {
      console.log('WebSocket client disconnected', userId ? `(user: ${userId})` : '');
      if (userId && clients.has(userId)) {
        const userClients = clients.get(userId);
        userClients.delete(ws);
        if (userClients.size === 0) {
          clients.delete(userId);
        }
      }
    });

    ws.on('error', (error) => {
      console.error('WebSocket error:', error.message);
    });
  });

  return wss;
}

function handleAuth(ws, message, setUserId) {
  try {
    const { token } = message;
    if (!token) {
      ws.send(JSON.stringify({ type: 'auth_error', message: 'Token required' }));
      return;
    }

    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    const userId = decoded.id;
    setUserId(userId);

    if (!clients.has(userId)) {
      clients.set(userId, new Set());
    }
    clients.get(userId).add(ws);

    ws.send(JSON.stringify({ type: 'auth_success', userId }));
    console.log(`User ${userId} authenticated via WebSocket`);
  } catch (error) {
    ws.send(JSON.stringify({ type: 'auth_error', message: 'Invalid token' }));
  }
}

function handleSosAlert(ws, message, userId) {
  if (!userId) {
    ws.send(JSON.stringify({ type: 'error', message: 'Not authenticated' }));
    return;
  }

  const alert = {
    type: 'sos_alert',
    userId,
    latitude: message.latitude || 0,
    longitude: message.longitude || 0,
    locationUrl: message.latitude ? `https://maps.google.com/?q=${message.latitude},${message.longitude}` : '',
    timestamp: new Date().toISOString(),
    targetContact: message.targetContact || '',
  };

  // Broadcast SOS to the user's own clients
  broadcastToUser(userId, alert);

  // Also broadcast to guardians in the system
  broadcastToAll({
    type: 'sos_broadcast',
    ...alert,
  });

  ws.send(JSON.stringify({ type: 'sos_sent', alertId: message.alertId }));
}

function handleSosCancel(ws, message, userId) {
  if (!userId) return;

  broadcastToUser(userId, {
    type: 'sos_cancelled',
    userId,
    timestamp: new Date().toISOString(),
  });
}

function handleGuardianAlert(ws, message) {
  // Forward guardian alert to specific user
  const { targetUserId, alert } = message;
  if (targetUserId) {
    broadcastToUser(targetUserId, {
      type: 'guardian_alert',
      from: message.fromUserId,
      alert,
      timestamp: new Date().toISOString(),
    });
  }
}

function handleTyping(ws, message, userId) {
  if (!userId) return;
  
  broadcastToUser(userId, {
    type: 'typing',
    userId,
    isTyping: message.isTyping || false,
  });
}

function broadcastToUser(userId, message) {
  const userClients = clients.get(userId);
  if (userClients) {
    const data = JSON.stringify(message);
    for (const client of userClients) {
      if (client.readyState === 1) { // OPEN
        client.send(data);
      }
    }
  }
}

function broadcastToAll(message) {
  const data = JSON.stringify(message);
  for (const [, userClients] of clients) {
    for (const client of userClients) {
      if (client.readyState === 1) { // OPEN
        client.send(data);
      }
    }
  }
}

module.exports = { setupWebSocket };
