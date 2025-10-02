--liquibase formatted sql

--changeset mestr:2
INSERT INTO properties (application, profile, label, key, value)
VALUES ('pusa', 'default', '1.0', 'password.policy.min-length', '8'),
       ('pusa', 'default', '1.0', 'password.policy.require-uppercase', 'true'),
       ('pusa', 'default', '1.0', 'password.policy.require-lowercase', 'true'),
       ('pusa', 'default', '1.0', 'password.policy.require-numbers', 'true'),
       ('pusa', 'default', '1.0', 'password.policy.require-special', 'false');
