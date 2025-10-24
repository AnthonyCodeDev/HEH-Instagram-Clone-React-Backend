-- Migration V4: add social media columns to users
-- Adds tiktok, instagram and twitter columns to users table

ALTER TABLE users
    ADD COLUMN tiktok VARCHAR(255),
    ADD COLUMN instagram VARCHAR(255),
    ADD COLUMN twitter VARCHAR(255);
