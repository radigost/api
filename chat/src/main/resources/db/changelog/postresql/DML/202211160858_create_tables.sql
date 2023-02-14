-- liquibase formatted sql
-- changeset npoliakoff:202211160858
CREATE TABLE rooms
(
    id           serial NOT NULL,
    participants integer[]

);


CREATE TABLE messages
(
    id        serial NOT NULL,
    roomId   int not null,
    text      varchar(500),
    timestamp timestamptz
);

ALTER TABLE rooms add primary key (id);
ALTER TABLE messages add primary key (id,roomId);


SELECT create_distributed_table('rooms', 'id');
SELECT create_distributed_table('messages', 'roomid');


