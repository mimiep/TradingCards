CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS decks;
DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS packages;
DROP TABLE IF EXISTS scoreboard;
DROP TABLE IF EXISTS users;


CREATE TABLE packages (
                          package_id UUID PRIMARY KEY DEFAULT uuid_generate_v4()
);


CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       username VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       token VARCHAR(255),
                       coins INT DEFAULT 20,
                       name VARCHAR(100) DEFAULT NULL,
                       bio TEXT DEFAULT NULL,
                       image TEXT DEFAULT NULL
);

CREATE TABLE cards (
                       card_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       package_id UUID REFERENCES packages(package_id),
                       user_id UUID REFERENCES users(id),
                       name VARCHAR(255) NOT NULL,
                       damage INT NOT NULL,
                       type VARCHAR(50) NOT NULL,
                       element_type VARCHAR(50) NOT NULL
);


CREATE TABLE decks (
                       user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                       card_id UUID REFERENCES cards(card_id) ON DELETE CASCADE,
                       PRIMARY KEY (user_id, card_id)
);

CREATE TABLE scoreboard (
                            user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
                            elo INT DEFAULT 100
);




GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE users TO mtcg_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE decks TO mtcg_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE packages TO mtcg_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE cards TO mtcg_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE scoreboard TO mtcg_user;










