const express = require('express');
const { v4: uuidv4 } = require('uuid');
const db = require('../config/database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

// GET /api/chat/messages
router.get('/messages', authenticateToken, async (req, res) => {
  try {
    const limit = Math.min(parseInt(req.query.limit) || 100, 500);
    const before = req.query.before || null;

    let result;
    if (before) {
      result = await db.query(
        `SELECT * FROM chat_messages
         WHERE user_id = $1 AND created_at < $2
         ORDER BY created_at DESC
         LIMIT $3`,
        [req.user.id, before, limit]
      );
    } else {
      result = await db.query(
        `SELECT * FROM chat_messages
         WHERE user_id = $1
         ORDER BY created_at DESC
         LIMIT $2`,
        [req.user.id, limit]
      );
    }

    res.json({ messages: result.rows.reverse() });
  } catch (error) {
    console.error('Get messages error:', error);
    res.status(500).json({ error: 'Failed to fetch messages' });
  }
});

// POST /api/chat/messages
router.post('/messages', authenticateToken, async (req, res) => {
  try {
    const { id, message, sender, timestamp } = req.body;

    if (!message || !sender) {
      return res.status(400).json({ error: 'Message and sender are required' });
    }

    if (!['user', 'rafiq'].includes(sender)) {
      return res.status(400).json({ error: 'Sender must be "user" or "rafiq"' });
    }

    const msgId = id || uuidv4();
    const result = await db.query(
      `INSERT INTO chat_messages (id, user_id, message, sender, created_at)
       VALUES ($1, $2, $3, $4, $5)
       RETURNING *`,
      [msgId, req.user.id, message, sender, timestamp ? new Date(timestamp) : new Date()]
    );

    res.status(201).json({ message: result.rows[0] });
  } catch (error) {
    console.error('Create message error:', error);
    res.status(500).json({ error: 'Failed to save message' });
  }
});

// POST /api/chat/sync
router.post('/sync', authenticateToken, async (req, res) => {
  try {
    const { messages } = req.body;

    if (!Array.isArray(messages) || messages.length === 0) {
      return res.status(400).json({ error: 'Messages array is required' });
    }

    const saved = [];
    for (const msg of messages) {
      const existing = await db.query(
        'SELECT id FROM chat_messages WHERE id = $1 AND user_id = $2',
        [msg.id, req.user.id]
      );

      if (existing.rows.length === 0) {
        const result = await db.query(
          `INSERT INTO chat_messages (id, user_id, message, sender, created_at)
           VALUES ($1, $2, $3, $4, $5)
           RETURNING *`,
          [msg.id, req.user.id, msg.message, msg.sender, new Date(msg.timestamp)]
        );
        saved.push(result.rows[0]);
      }
    }

    // Award points for chat messages
    const userMessages = saved.filter(m => m.sender === 'user').length;
    if (userMessages > 0) {
      const points = userMessages * 10;
      await db.query(
        'UPDATE users SET total_points = total_points + $1 WHERE id = $2',
        [points, req.user.id]
      );
      await db.query(
        `INSERT INTO score_events (user_id, points, reason)
         VALUES ($1, $2, 'chat_message')`,
        [req.user.id, points]
      );
    }

    res.json({ synced: saved.length, pointsAwarded: userMessages * 10 || 0 });
  } catch (error) {
    console.error('Sync messages error:', error);
    res.status(500).json({ error: 'Failed to sync messages' });
  }
});

// DELETE /api/chat/messages/:id
router.delete('/messages/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    const result = await db.query(
      'DELETE FROM chat_messages WHERE id = $1 AND user_id = $2 RETURNING id',
      [id, req.user.id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Message not found' });
    }

    res.json({ message: 'Message deleted' });
  } catch (error) {
    console.error('Delete message error:', error);
    res.status(500).json({ error: 'Failed to delete message' });
  }
});

module.exports = router;
