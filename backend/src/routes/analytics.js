const express = require('express');
const db = require('../config/database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

// POST /api/analytics/event
router.post('/event', authenticateToken, async (req, res) => {
  try {
    const { activityType, description, metadata } = req.body;

    if (!activityType) {
      return res.status(400).json({ error: 'Activity type is required' });
    }

    await db.query(
      `INSERT INTO activity_log (user_id, activity_type, description, metadata)
       VALUES ($1, $2, $3, $4)`,
      [req.user.id, activityType, description || '', JSON.stringify(metadata || {})]
    );

    res.status(201).json({ message: 'Event logged' });
  } catch (error) {
    console.error('Log event error:', error);
    res.status(500).json({ error: 'Failed to log event' });
  }
});

// GET /api/analytics/stats
router.get('/stats', authenticateToken, async (req, res) => {
  try {
    const result = await db.query(
      `SELECT
        (SELECT COUNT(*) FROM chat_messages WHERE user_id = $1 AND sender = 'user') as total_chat_messages,
        (SELECT COALESCE(SUM(points), 0) FROM score_events WHERE user_id = $1 AND reason = 'chat_message') as chat_points_earned,
        (SELECT COUNT(*) FROM sos_alerts WHERE user_id = $1 AND status != 'cancelled') as total_sos_alerts,
        (SELECT COUNT(*) FROM places WHERE user_id = $1) as total_places,
        (SELECT COUNT(*) FROM contacts WHERE user_id = $1) as total_contacts,
        (SELECT COUNT(*) FROM medications WHERE user_id = $1) as total_medications,
        (SELECT COUNT(*) FROM score_events WHERE user_id = $1) as total_score_events,
        (SELECT COALESCE(total_points, 0) FROM users WHERE id = $1) as current_points`,
      [req.user.id]
    );

    res.json({ stats: result.rows[0] || {} });
  } catch (error) {
    console.error('Get stats error:', error);
    res.status(500).json({ error: 'Failed to fetch stats' });
  }
});

module.exports = router;
