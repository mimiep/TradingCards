CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE DATABASE mtcg;
--\c mtcg;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS decks;
DROP TABLE IF EXISTS packages;


CREATE TABLE users (
        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        username VARCHAR(255) UNIQUE NOT NULL,
        password VARCHAR(255) NOT NULL,
        token VARCHAR(255)
);

CREATE TABLE cards (
        card_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        package_id UUID REFERENCES packages(package_id),
        user_id UUID REFERENCES users(id),
        name VARCHAR(255) NOT NULL,
        damage INT NOT NULL,
        type VARCHAR(50) NOT NULL,     /*card type*/
        element_type VARCHAR(50) NOT NULL
);


CREATE TABLE decks (
        user_id UUID REFERENCES users(id) ON DELETE CASCADE,
        card_id UUID REFERENCES cards(card_id) ON DELETE CASCADE,
        PRIMARY KEY (user_id, card_id)

);

CREATE TABLE packages (
        package_id UUID PRIMARY KEY DEFAULT uuid_generate_v4()
);



/*
 CREATE TABLE battles (
        battle_id UUID PRIMARY KEY DEFAULT uuid_generate_v4()
        player1_id UUID NOT NULL,
        player2_id UUID NOT NULL,
        winner_id UUID,
        battle_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (player1_id) REFERENCES users(user_id) ON DELETE CASCADE,
        FOREIGN KEY (player2_id) REFERENCES users(user_id) ON DELETE CASCADE,
        FOREIGN KEY (winner_id) REFERENCES users(user_id) ON DELETE CASCADE
);

 CREATE TABLE scoreboard (
        user_id UUID PRIMARY KEY,
        elo INT DEFAULT 100,
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

 */




