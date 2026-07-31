const express = require('express');
const { v4: uuidv4 } = require('uuid');
const db = require('../config/database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

// POST /api/sos/alert
router.post('/alert', authenticateToken, async (req, res) => {
  try {
    const { latitude, longitude, targetContact } = req.body;
    const id = uuidv4();

    const locationUrl = (latitude && longitude)
      ? `https://maps.google.com/?q=${latitude},${longitude}`
      : 'Location unavailable';

    const result = await db.query(
      `INSERT INTO sos_alerts (id, user_id, latitude, longitude, location_url, message, target_contact)
       VALUES ($1, $2, $3, $4, $5, $6, $7)
       RETURNING *`,
      [
        id, req.user.id, latitude || 0, longitude || 0,
        locationUrl,
        `EMERGENCY: RAFIQ user needs help! ${locationUrl}`,
        targetContact || ''
      ]
    );

    // Log activity
    await db.query(
      `INSERT INTO activity_log (user_id, activity_type, description, metadata)
       VALUES ($1, 'sos_alert', 'SOS alert triggered', $2)`,
      [req.user.id, JSON.stringify({ alertId: id, latitude, longitude })]
    );

    res.status(201).json({ alert: result.rows[0] });
  } catch (error) {
    console.error('Create SOS alert error:', error);
    res.status(500).json({ error: 'Failed to create SOS alert' });
  }
});

// GET /api/sos/alerts
router.get('/alerts', authenticateToken, async (req, res) => {
  try {
    const result = await db.query(
      `SELECT * FROM sos_alerts WHERE user_id = $1
       ORDER BY created_at DESC LIMIT 50`,
      [req.user.id]
    );
    res.json({ alerts: result.rows });
  } catch (error) {
    console.error('Get SOS alerts error:', error);
    res.status(500).json({ error: 'Failed to fetch alerts' });
  }
});

// PUT /api/sos/alerts/:id/resolve
router.put('/alerts/:id/resolve', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    const result = await db.query(
      `UPDATE sos_alerts SET status = 'resolved', resolved_at = NOW()
       WHERE id = $1 AND user_id = $2
       RETURNING *`,
      [id, req.user.id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Alert not found' });
    }

    res.json({ alert: result.rows[0] });
  } catch (error) {
    console.error('Resolve alert error:', error);
    res.status(500).json({ error: 'Failed to resolve alert' });
  }
});

// GET /api/sos/active
router.get('/active', authenticateToken, async (req, res) => {
  try {
    const result = await db.query(
      `SELECT * FROM sos_alerts
       WHERE user_id = $1 AND status = 'active'
       ORDER BY created_at DESC
       LIMIT 1`,
      [req.user.id]
    );

    res.json({ activeAlert: result.rows[0] || null });
  } catch (error) {
    console.error('Get active alert error:', error);
    res.status(500).json({ error: 'Failed to fetch active alert' });
  }
});

module.exports = router;
