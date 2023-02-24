-- liquibase formatted sql
-- changeset npoliakoff:202211160858

CREATE TYPE message_status_type AS ENUM ('RECEIVED_PENDING', 'RECEIVED', 'RECEIVED_REJECTED');

ALTER TABLE messages ADD COLUMN status message_status_type;
ALTER TABLE messages ADD COLUMN poster_id int ;

CREATE TABLE last_read_message
(
    id        serial NOT NULL,
    profile_id int not null,
    room_id   int not null,
    last_read_message_id int not null
);

