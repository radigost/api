-- liquibase formatted sql
-- changeset npoliakoff:202210020805

ALTER TABLE profile
    ADD gender VARCHAR(50);
