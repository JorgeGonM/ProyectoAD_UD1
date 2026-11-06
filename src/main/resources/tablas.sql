-- ============================================
-- Base de datos del proyecto Game of Thrones
-- ============================================
CREATE DATABASE IF NOT EXISTS got_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE got_db;

-- ---------- CASAS ----------
CREATE TABLE IF NOT EXISTS house (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    region        VARCHAR(255),
    coat_of_arms  TEXT,
    words         VARCHAR(255),
    current_lord  VARCHAR(255),
    heir          VARCHAR(255),
    overlord      VARCHAR(255),
    founded       VARCHAR(255),
    founder       VARCHAR(255),
    died_out      VARCHAR(255),
    url           VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS house_title (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    house_id  INT NOT NULL,
    title     VARCHAR(255) NOT NULL,
    FOREIGN KEY (house_id) REFERENCES house(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS house_seat (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    house_id  INT NOT NULL,
    seat      VARCHAR(255) NOT NULL,
    FOREIGN KEY (house_id) REFERENCES house(id) ON DELETE CASCADE
);

-- ---------- PERSONAJES ----------
CREATE TABLE IF NOT EXISTS got_character (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(255),
    gender   VARCHAR(50),
    culture  VARCHAR(255),
    born     VARCHAR(255),
    died     VARCHAR(255),
    father   VARCHAR(255),
    mother   VARCHAR(255),
    spouse   VARCHAR(255),
    url      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS character_title (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    character_id  INT NOT NULL,
    title         VARCHAR(255) NOT NULL,
    FOREIGN KEY (character_id) REFERENCES got_character(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS character_alias (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    character_id  INT NOT NULL,
    alias         VARCHAR(255) NOT NULL,
    FOREIGN KEY (character_id) REFERENCES got_character(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS character_actor (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    character_id  INT NOT NULL,
    actor         VARCHAR(255) NOT NULL,
    FOREIGN KEY (character_id) REFERENCES got_character(id) ON DELETE CASCADE
);

-- ---------- RELACIÓN N:M personaje <-> casa (allegiances) ----------
CREATE TABLE IF NOT EXISTS character_house (
    character_id  INT NOT NULL,
    house_id      INT NOT NULL,
    PRIMARY KEY (character_id, house_id),
    FOREIGN KEY (character_id) REFERENCES got_character(id) ON DELETE CASCADE,
    FOREIGN KEY (house_id)     REFERENCES house(id)         ON DELETE CASCADE
);