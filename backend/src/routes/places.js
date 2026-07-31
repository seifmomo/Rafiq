const express = require('express');
const { v4: uuidv4 } = require('uuid');
const db = require('../config/database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

// GET /api/places
router.get('/', authenticateToken, async (req, res) => {
  try {
    const includePublic = req.query.public === 'true';
    let result;

    if (includePublic) {
      result = await db.query(
        `SELECT * FROM places WHERE user_id = $1 OR is_public = true
         ORDER BY created_at DESC`,
        [req.user.id]
      );
    } else {
      result = await db.query(
        'SELECT * FROM places WHERE user_id = $1 ORDER BY created_at DESC',
        [req.user.id]
      );
    }

    res.json({ places: result.rows });
  } catch (error) {
    console.error('Get places error:', error);
    res.status(500).json({ error: 'Failed to fetch places' });
  }
});

// POST /api/places
router.post('/', authenticateToken, async (req, res) => {
  try {
    const {
      name, description, latitude, longitude,
      isWheelchairAccessible, hasSignLanguageSupport,
      hasBrailleSignage, isPublic
    } = req.body;

    if (!name || latitude == null || longitude == null) {
      return res.status(400).json({ error: 'Name, latitude, and longitude are required' });
    }

    const id = uuidv4();
    const result = await db.query(
      `INSERT INTO places (id, user_id, name, description, latitude, longitude,
        is_wheelchair_accessible, has_sign_language_support, has_braille_signage, is_public)
       VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)
       RETURNING *`,
      [
        id, req.user.id, name, description || '', latitude, longitude,
        isWheelchairAccessible || false, hasSignLanguageSupport || false,
        hasBrailleSignage || false, isPublic !== false
      ]
    );

    // Award points for adding a place
    await db.query(
      'UPDATE users SET total_points = total_points + 50 WHERE id = $1',
      [req.user.id]
    );
    await db.query(
      `INSERT INTO score_events (user_id, points, reason)
       VALUES ($1, 50, 'added_place')`,
      [req.user.id]
    );

    res.status(201).json({ place: result.rows[0] });
  } catch (error) {
    console.error('Create place error:', error);
    res.status(500).json({ error: 'Failed to create place' });
  }
});

// PUT /api/places/:id
router.put('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    const {
      name, description, latitude, longitude,
      isWheelchairAccessible, hasSignLanguageSupport,
      hasBrailleSignage, isPublic
    } = req.body;

    const result = await db.query(
      `UPDATE places SET
        name = COALESCE($1, name),
        description = COALESCE($2, description),
        latitude = COALESCE($3, latitude),
        longitude = COALESCE($4, longitude),
        is_wheelchair_accessible = COALESCE($5, is_wheelchair_accessible),
        has_sign_language_support = COALESCE($6, has_sign_language_support),
        has_braille_signage = COALESCE($7, has_braille_signage),
        is_public = COALESCE($8, is_public)
       WHERE id = $9 AND user_id = $10
       RETURNING *`,
      [name, description, latitude, longitude, isWheelchairAccessible,
       hasSignLanguageSupport, hasBrailleSignage, isPublic, id, req.user.id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Place not found' });
    }

    res.json({ place: result.rows[0] });
  } catch (error) {
    console.error('Update place error:', error);
    res.status(500).json({ error: 'Failed to update place' });
  }
});

// DELETE /api/places/:id
router.delete('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    const result = await db.query(
      'DELETE FROM places WHERE id = $1 AND user_id = $2 RETURNING id',
      [id, req.user.id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Place not found' });
    }

    res.json({ message: 'Place deleted' });
  } catch (error) {
    console.error('Delete place error:', error);
    res.status(500).json({ error: 'Failed to delete place' });
  }
});

module.exports = router;
