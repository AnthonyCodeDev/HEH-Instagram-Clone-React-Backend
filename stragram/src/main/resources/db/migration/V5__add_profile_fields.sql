-- Migration V5: add extended profile fields
-- Adds name, banner_url, phone, location, birthdate columns

ALTER TABLE users
    ADD COLUMN name VARCHAR(100),
    ADD COLUMN banner_url VARCHAR(255),
    ADD COLUMN phone VARCHAR(30),
    ADD COLUMN location VARCHAR(100),
    ADD COLUMN birthdate DATE;
