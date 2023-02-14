-- liquibase formatted sql
-- changeset npoliakoff:202209241215
CREATE TABLE user
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50),
    password VARCHAR(250)
);


CREATE TABLE profile
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50),
    last_name  VARCHAR(50),
    age        INTEGER,
    interests  VARCHAR(250),
    city       VARCHAR(50),
    owner_id   INT,
    CONSTRAINT FK_OwnerOfProfile FOREIGN KEY (owner_id) REFERENCES user (id)
);
