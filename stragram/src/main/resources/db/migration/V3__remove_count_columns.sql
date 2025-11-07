-- Suppression des colonnes like_count et comment_count de la table posts
ALTER TABLE posts DROP COLUMN like_count;
ALTER TABLE posts DROP COLUMN comment_count;
