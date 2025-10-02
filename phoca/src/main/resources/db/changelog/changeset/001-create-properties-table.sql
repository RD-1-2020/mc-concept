-- liquibase formatted sql

-- changeset liquibase:1
CREATE TABLE properties (
  application VARCHAR(255) NOT NULL,
  profile VARCHAR(255) NOT NULL,
  label VARCHAR(255) NOT NULL,
  key VARCHAR(255) NOT NULL,
  value VARCHAR(255) NOT NULL,
  PRIMARY KEY (application, profile, label, key)
);
