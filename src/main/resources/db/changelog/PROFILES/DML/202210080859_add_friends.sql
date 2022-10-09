-- liquibase formatted sql
-- changeset npoliakoff:202210080859

CREATE TABLE IF NOT EXISTS socialnetwork.friend
(
    id      INT UNIQUE NOT NULL,
    friends JSON
);
