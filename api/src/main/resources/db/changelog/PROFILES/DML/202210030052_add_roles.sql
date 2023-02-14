-- liquibase formatted sql
-- changeset npoliakoff:202210020805

ALTER TABLE socialnetwork.user
    ADD role VARCHAR(50);
