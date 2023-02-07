-- liquibase formatted sql
-- changeset npoliakoff:202302072320

CREATE TABLE IF NOT EXISTS socialnetwork.post
(
    id      INT UNIQUE NOT NULL AUTO_INCREMENT,
    profile_id INT,
    text TEXT,
    creation_date datetime,
    CONSTRAINT FK_OwnerOfPost FOREIGN KEY (profile_id) REFERENCES profile (id)
);
