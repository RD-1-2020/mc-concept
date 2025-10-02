--liquibase formatted sql

--changeset mestr:2
INSERT INTO PROPERTIES (APPLICATION, PROFILE, LABEL, KEY, VALUE)
VALUES ('pusa', 'default', 'main', 'password.policy.min-length', '8'),
       ('pusa', 'default', 'main', 'password.policy.require-uppercase', 'true'),
       ('pusa', 'default', 'main', 'password.policy.require-lowercase', 'true'),
       ('pusa', 'default', 'main', 'password.policy.require-numbers', 'true'),
       ('pusa', 'default', 'main', 'password.policy.require-special', 'false');
