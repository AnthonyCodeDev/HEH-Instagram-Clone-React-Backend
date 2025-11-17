-- Drop favorites table (replaced by likes and bookmarks system)
-- The favorites table was redundant with the likes table
-- This cleanup is part of consolidating to two systems: likes (hearts) and bookmarks (saved posts)
DROP TABLE IF EXISTS favorites CASCADE;
