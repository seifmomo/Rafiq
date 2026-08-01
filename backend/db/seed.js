require('dotenv').config();

const bcrypt = require('bcryptjs');
const { v4: uuidv4 } = require('uuid');
const { pool } = require('../src/config/database');

async function seed() {
  const client = await pool.connect();
  try {
    await client.query('BEGIN');

    const existing = await client.query('SELECT id FROM users WHERE email = $1', ['demo@rafiq.app']);
    if (existing.rows.length > 0) {
      console.log('Demo user already exists, skipping user seed.');
    } else {
      const userId = uuidv4();
      const passwordHash = await bcrypt.hash('demo1234', 12);
      await client.query(
        `INSERT INTO users (id, email, password_hash, display_name, disability_type, emergency_contact)
         VALUES ($1, $2, $3, $4, $5, $6)`,
        [userId, 'demo@rafiq.app', passwordHash, 'Demo User', 'Mobility impaired', '+1234567890']
      );

      await client.query(
        `INSERT INTO places (id, user_id, name, description, latitude, longitude,
          is_wheelchair_accessible, has_sign_language_support, has_braille_signage, is_public)
         VALUES ($1, $2, $3, $4, $5, $6, true, true, true, true)`,
        [uuidv4(), userId, 'Central Library', 'Wheelchair ramp and braille signage available', 24.7136, 46.6753]
      );

      await client.query(
        `INSERT INTO places (id, user_id, name, description, latitude, longitude,
          is_wheelchair_accessible, has_sign_language_support, has_braille_signage, is_public)
         VALUES ($1, $2, $3, $4, $5, $6, false, true, false, true)`,
        [uuidv4(), userId, 'City Mall', 'Sign language interpreters available at help desks', 24.7356, 46.6853]
      );

      await client.query(
        `INSERT INTO contacts (id, user_id, name, phone_number)
         VALUES ($1, $2, $3, $4)`,
        [uuidv4(), userId, 'Emergency Service', '+1999']
      );

      await client.query(
        `INSERT INTO medications (id, user_id, name, dosage, time)
         VALUES ($1, $2, $3, $4, $5)`,
        [uuidv4(), userId, 'Vitamin D', '5000 IU', '08:00']
      );

      console.log('Seeded demo user and sample data.');
    }

    await client.query('COMMIT');
    console.log('Seed completed successfully!');
  } catch (error) {
    await client.query('ROLLBACK');
    console.error('Seed failed:', error.message);
    process.exit(1);
  } finally {
    client.release();
    await pool.end();
  }
}

seed();
