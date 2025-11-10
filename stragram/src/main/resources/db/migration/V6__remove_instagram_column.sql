-- Migration V6: swap instagram column to youtube
-- Removes youtube column if exists, then renames instagram to youtube

ALTER TABLE users
    DROP COLUMN IF EXISTS youtube;

ALTER TABLE users
    RENAME COLUMN instagram TO youtube;
