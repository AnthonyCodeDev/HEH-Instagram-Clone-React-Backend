-- Allow NULL values for image_path in posts table
ALTER TABLE posts ALTER COLUMN image_path DROP NOT NULL;
