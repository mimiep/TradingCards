CREATE DATABASE mtcg;
--\c mtcg;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        username VARCHAR(255) UNIQUE NOT NULL,
        password VARCHAR(255) NOT NULL,
        token VARCHAR(255)
);
