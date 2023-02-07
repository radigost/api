-- liquibase formatted sql
-- changeset npoliakoff:202302091010

CREATE TABLE IF NOT EXISTS socialnetwork.followers
(
    id      INT UNIQUE NOT NULL AUTO_INCREMENT,
    follower_id INT,
    followee_id int,
    CONSTRAINT FK_Follower FOREIGN KEY (follower_id) REFERENCES profile (id),
    CONSTRAINT FK_Followee FOREIGN KEY (followee_id) REFERENCES profile (id),
    UNIQUE `unique_index`(follower_id,followee_id )
);
