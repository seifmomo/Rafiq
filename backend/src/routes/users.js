const express = require('express');
const db = require('../config/database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

// GET /api/users/profile
router.get('/profile', authenticateToken, async (req, res) => {
  try {
    const result = await db.query(
      `SELECT id, email, display_name, disability_type, disability_other,
              emergency_contact, guardian_mode, language, dark_theme,
              font_size, font_family, speech_rate, total_points, is_guest,
              created_at, last_login_at
       FROM users WHERE id = $1`,
      [req.user.id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'User not found' });
    }

    res.json({ user: result.rows[0] });
  } catch (error) {
    console.error('Get profile error:', error);
    res.status(500).json({ error: 'Failed to fetch profile' });
  }
});

// PUT /api/users/profile
router.put('/profile', authenticateToken, async (req, res) => {
  try {
    const {
      displayName, disabilityType, disabilityOther,
      emergencyContact, language, darkTheme,
      fontSize, fontFamily, speechRate
    } = req.body;

    const result = await db.query(
      `UPDATE users SET
        display_name = COALESCE($1, display_name),
        disability_type = COALESCE($2, disability_type),
        disability_other = COALESCE($3, disability_other),
        emergency_contact = COALESCE($4, emergency_contact),
        language = COALESCE($5, language),
        dark_theme = COALESCE($6, dark_theme),
        font_size = COALESCE($7, font_size),
        font_family = COALESCE($8, font_family),
        speech_rate = COALESCE($9, speech_rate),
        updated_at = NOW()
       WHERE id = $10
       RETURNING id, email, display_name, disability_type, disability_other,
                 emergency_contact, guardian_mode, language, dark_theme,
                 font_size, font_family, speech_rate, total_points, is_guest`,
      [
        displayName, disabilityType, disabilityOther,
        emergencyContact, language, darkTheme,
        fontSize, fontFamily, speechRate,
        req.user.id
      ]
    );

    res.json({ user: result.rows[0] });
  } catch (error) {
    console.error('Update profile error:', error);
    res.status(500).json({ error: 'Failed to update profile' });
  }
});

// PUT /api/users/disability-type
router.put('/disability-type', authenticateToken, async (req, res) => {
  try {
    const { disabilityType } = req.body;

    if (!disabilityType) {
      return res.status(400).json({ error: 'Disability type is required' });
    }

    const result = await db.query(
      `UPDATE users SET disability_type = $1, updated_at = NOW()
       WHERE id = $2
       RETURNING id, disability_type, total_points`,
      [disabilityType, req.user.id]
    );

    const user = result.rows[0];

    // Award points for completing disability survey
    await db.query(
      'UPDATE users SET total_points = total_points + 50 WHERE id = $1',
      [req.user.id]
    );
    await db.query(
      `INSERT INTO score_events (user_id, points, reason)
       VALUES ($1, 50, 'disability_survey')`,
      [req.user.id]
    );

    res.json({ user: { ...user, total_points: user.total_points + 50 } });
  } catch (error) {
    console.error('Update disability error:', error);
    res.status(500).json({ error: 'Failed to update disability type' });
  }
});

// PUT /api/users/fcm-token
router.put('/fcm-token', authenticateToken, async (req, res) => {
  try {
    const { fcmToken } = req.body;
    await db.query('UPDATE users SET fcm_token = $1 WHERE id = $2', [fcmToken, req.user.id]);
    res.json({ message: 'FCM token updated' });
  } catch (error) {
    console.error('Update FCM error:', error);
    res.status(500).json({ error: 'Failed to update FCM token' });
  }
});

// PUT /api/users/guardian-mode
router.put('/guardian-mode', authenticateToken, async (req, res) => {
  try {
    const { enabled } = req.body;
    const result = await db.query(
      `UPDATE users SET guardian_mode = $1, updated_at = NOW()
       WHERE id = $2
       RETURNING id, guardian_mode`,
      [enabled, req.user.id]
    );
    res.json({ guardianMode: result.rows[0].guardian_mode });
  } catch (error) {
    console.error('Update guardian mode error:', error);
    res.status(500).json({ error: 'Failed to update guardian mode' });
  }
});

module.exports = router;
