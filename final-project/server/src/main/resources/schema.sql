SET
    search_path TO public;
CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA public;

DROP TABLE IF EXISTS Good;
DROP TABLE IF EXISTS "Group";
DROP TABLE IF EXISTS "User";

CREATE TABLE IF NOT EXISTS "Group"
(
    id          UUID DEFAULT public.uuid_generate_v4() PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    createdAt   TIMESTAMP,
    updatedAt   TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Good
(
    id          UUID           DEFAULT public.uuid_generate_v4() PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    producer    VARCHAR(255) NOT NULL,
    price       NUMERIC(10, 2) DEFAULT 0.00,
    quantity    INTEGER        DEFAULT 0,
    createdAt   TIMESTAMP,
    updatedAt   TIMESTAMP,
    groupId     UUID,
    FOREIGN KEY (groupId) REFERENCES "Group" (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "User"
(
    id        UUID DEFAULT public.uuid_generate_v4() PRIMARY KEY,
    fullName  VARCHAR(255) NOT NULL,
    username  VARCHAR(255) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    role      VARCHAR(5)   NOT NULL,
    updatedAt TIMESTAMP,
    createdAt TIMESTAMP
);