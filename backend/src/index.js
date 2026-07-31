require('dotenv').config();

const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const rateLimit = require('express-rate-limit');
const http = require('http');
const { WebSocketServer } = require('ws');
const { setupWebSocket } = require('./websocket');

const authRoutes = require('./routes/auth');
const userRoutes = require('./routes/users');
const scoreboardRoutes = require('./routes/scoreboard');
const placesRoutes = require('./routes/places');
const contactsRoutes = require('./routes/contacts');
const medicationsRoutes = require('./routes/medications');
const chatRoutes = require('./routes/chat');
const sosRoutes = require('./routes/sos');
const analyticsRoutes = require('./routes/analytics');

const app = express();
const server = http.createServer(app);

// Security middleware
app.use(helmet({ contentSecurityPolicy: false }));
app.use(cors({ origin: process.env.CORS_ORIGIN || '*', credentials: true }));
app.use(express.json({ limit: '10mb' }));

// Rate limiting
const limiter = rateLimit({
  windowMs: parseInt(process.env.RATE_LIMIT_WINDOW_MS) || 900000,
  max: parseInt(process.env.RATE_LIMIT_MAX) || 100,
  message: { error: 'Too many requests, please try again later' },
});
app.use('/api/auth', limiter);

// Health check
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok', timestamp: new Date().toISOString(), version: '1.0.0' });
});

// API routes
app.use('/api/auth', authRoutes);
app.use('/api/users', userRoutes);
app.use('/api/scoreboard', scoreboardRoutes);
app.use('/api/places', placesRoutes);
app.use('/api/contacts', contactsRoutes);
app.use('/api/medications', medicationsRoutes);
app.use('/api/chat', chatRoutes);
app.use('/api/sos', sosRoutes);
app.use('/api/analytics', analyticsRoutes);

// 404 handler
app.use((req, res) => {
  res.status(404).json({ error: 'Route not found' });
});

// Global error handler
app.use((err, req, res, next) => {
  console.error('Unhandled error:', err);
  res.status(500).json({ error: 'Internal server error' });
});

// WebSocket setup
const wss = setupWebSocket(server);

const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
  console.log('');
  console.log('╔════════════════════════════════════╗');
  console.log(`║   RAFIQ Backend Server v1.0.0      ║`);
  console.log(`║   Port: ${PORT}                       ║`);
  console.log(`║   Environment: ${process.env.NODE_ENV || 'development'}            ║`);
  console.log('╚════════════════════════════════════╝');
  console.log('');
  console.log(`REST API:   http://localhost:${PORT}/api`);
  console.log(`WebSocket:  ws://localhost:${PORT}/ws`);
  console.log(`Health:     http://localhost:${PORT}/api/health`);
  console.log('');
});

// Graceful shutdown
process.on('SIGTERM', () => {
  console.log('SIGTERM received. Shutting down gracefully...');
  wss.close(() => {
    server.close(() => {
      console.log('Server closed');
      process.exit(0);
    });
  });
});

process.on('SIGINT', () => {
  console.log('SIGINT received. Shutting down...');
  wss.close(() => {
    server.close(() => {
      console.log('Server closed');
      process.exit(0);
    });
  });
});

module.exports = app;
