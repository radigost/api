-- liquibase formatted sql
-- changeset npoliakoff:202302202127


CREATE TABLE counter
(
    id        serial NOT NULL ,
    profile_id   int not null,
    count int
);

ALTER TABLE counter DROP column  profile_id;
ALTER TABLE counter ADD column  chat_id int ;
ALTER TABLE counter add column  profile_id int;

