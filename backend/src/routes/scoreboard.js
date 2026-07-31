const express = require('express');
const db = require('../config/database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

// GET /api/scoreboard
router.get('/', async (req, res) => {
  try {
    const limit = Math.min(parseInt(req.query.limit) || 50, 200);
    const offset = parseInt(req.query.offset) || 0;

    const result = await db.query(
      `SELECT id, display_name, disability_type, total_points
       FROM users
       ORDER BY total_points DESC
       LIMIT $1 OFFSET $2`,
      [limit, offset]
    );

    const countResult = await db.query('SELECT COUNT(*) FROM users');
    const total = parseInt(countResult.rows[0].count);

    res.json({
      leaderboard: result.rows,
      total,
      limit,
      offset
    });
  } catch (error) {
    console.error('Scoreboard error:', error);
    res.status(500).json({ error: 'Failed to fetch leaderboard' });
  }
});

// GET /api/scoreboard/rank/:userId
router.get('/rank/:userId', async (req, res) => {
  try {
    const { userId } = req.params;

    const result = await db.query(
      `SELECT id, total_points FROM users WHERE id = $1`,
      [userId]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'User not found' });
    }

    const rankResult = await db.query(
      `SELECT COUNT(*) + 1 as rank FROM users
       WHERE total_points > (SELECT total_points FROM users WHERE id = $1)`,
      [userId]
    );

    const user = result.rows[0];
    const rank = parseInt(rankResult.rows[0].rank);

    res.json({
      userId: user.id,
      totalPoints: user.total_points,
      rank
    });
  } catch (error) {
    console.error('Rank error:', error);
    res.status(500).json({ error: 'Failed to fetch rank' });
  }
});

// GET /api/scoreboard/my-rank
router.get('/my-rank', authenticateToken, async (req, res) => {
  try {
    const rankResult = await db.query(
      `SELECT COUNT(*) + 1 as rank FROM users
       WHERE total_points > (SELECT total_points FROM users WHERE id = $1)`,
      [req.user.id]
    );

    const userResult = await db.query(
      'SELECT total_points FROM users WHERE id = $1',
      [req.user.id]
    );

    const rank = parseInt(rankResult.rows[0].rank);
    const totalPoints = userResult.rows[0].total_points;

    // Get nearby users
    const aboveResult = await db.query(
      `SELECT id, display_name, total_points FROM users
       WHERE total_points > $1
       ORDER BY total_points ASC
       LIMIT 2`,
      [totalPoints]
    );

    const belowResult = await db.query(
      `SELECT id, display_name, total_points FROM users
       WHERE total_points < $1
       ORDER BY total_points DESC
       LIMIT 2`,
      [totalPoints]
    );

    res.json({
      rank,
      totalPoints,
      nearbyAbove: aboveResult.rows.reverse(),
      nearbyBelow: belowResult.rows
    });
  } catch (error) {
    console.error('My rank error:', error);
    res.status(500).json({ error: 'Failed to fetch rank' });
  }
});

// GET /api/scoreboard/history
router.get('/history', authenticateToken, async (req, res) => {
  try {
    const result = await db.query(
      `SELECT id, points, reason, created_at
       FROM score_events
       WHERE user_id = $1
       ORDER BY created_at DESC
       LIMIT 100`,
      [req.user.id]
    );

    res.json({ events: result.rows });
  } catch (error) {
    console.error('Score history error:', error);
    res.status(500).json({ error: 'Failed to fetch score history' });
  }
});

// POST /api/scoreboard/add-points
router.post('/add-points', authenticateToken, async (req, res) => {
  try {
    const { points, reason } = req.body;

    if (!points || points <= 0) {
      return res.status(400).json({ error: 'Points must be a positive number' });
    }

    await db.query(
      'UPDATE users SET total_points = total_points + $1 WHERE id = $2',
      [points, req.user.id]
    );

    await db.query(
      `INSERT INTO score_events (user_id, points, reason)
       VALUES ($1, $2, $3)`,
      [req.user.id, points, reason || 'activity']
    );

    const result = await db.query(
      'SELECT total_points FROM users WHERE id = $1',
      [req.user.id]
    );

    res.json({ totalPoints: result.rows[0].total_points, pointsAdded: points });
  } catch (error) {
    console.error('Add points error:', error);
    res.status(500).json({ error: 'Failed to add points' });
  }
});

module.exports = router;
