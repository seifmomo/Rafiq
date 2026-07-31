const express = require('express');
const { v4: uuidv4 } = require('uuid');
const db = require('../config/database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

// GET /api/medications
router.get('/', authenticateToken, async (req, res) => {
  try {
    const result = await db.query(
      'SELECT * FROM medications WHERE user_id = $1 ORDER BY time ASC',
      [req.user.id]
    );
    res.json({ medications: result.rows });
  } catch (error) {
    console.error('Get medications error:', error);
    res.status(500).json({ error: 'Failed to fetch medications' });
  }
});

// POST /api/medications
router.post('/', authenticateToken, async (req, res) => {
  try {
    const { name, dosage, time } = req.body;

    if (!name || !time) {
      return res.status(400).json({ error: 'Name and time are required' });
    }

    const id = uuidv4();
    const result = await db.query(
      `INSERT INTO medications (id, user_id, name, dosage, time)
       VALUES ($1, $2, $3, $4, $5)
       RETURNING *`,
      [id, req.user.id, name, dosage || '', time]
    );

    res.status(201).json({ medication: result.rows[0] });
  } catch (error) {
    console.error('Create medication error:', error);
    res.status(500).json({ error: 'Failed to create medication' });
  }
});

// PUT /api/medications/:id
router.put('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    const { name, dosage, time } = req.body;

    const result = await db.query(
      `UPDATE medications SET
        name = COALESCE($1, name),
        dosage = COALESCE($2, dosage),
        time = COALESCE($3, time)
       WHERE id = $4 AND user_id = $5
       RETURNING *`,
      [name, dosage, time, id, req.user.id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Medication not found' });
    }

    res.json({ medication: result.rows[0] });
  } catch (error) {
    console.error('Update medication error:', error);
    res.status(500).json({ error: 'Failed to update medication' });
  }
});

// DELETE /api/medications/:id
router.delete('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    const result = await db.query(
      'DELETE FROM medications WHERE id = $1 AND user_id = $2 RETURNING id',
      [id, req.user.id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Medication not found' });
    }

    res.json({ message: 'Medication deleted' });
  } catch (error) {
    console.error('Delete medication error:', error);
    res.status(500).json({ error: 'Failed to delete medication' });
  }
});

module.exports = router;
