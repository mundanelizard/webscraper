CREATE DATABASE webscraper;

USE webscraper;


DROP TABLE cameras;

CREATE TABLE cameras (
    title TEXT NOT NULL,
    images TEXT NOT NULL,
    specs TEXT NOT NULL,
    camera_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT
);


DROP TABLE listings;

CREATE TABLE listings (
    listing_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    camera_id INT UNSIGNED NOT NULL, -- make foreign key on cameras
    url TEXT NOT NULL,
    price FLOAT NOT NULL,
    date DATETIME NOT NULL,
    provider_id INT UNSIGNED NOT NULL,
    features TEXT NOT NULL,

    FOREIGN KEY (camera_id) REFERENCES cameras(camera_id),
    FOREIGN KEY (provider_id) REFERENCES providers(provider_id)
);

CREATE TABLE providers (
                           provider_id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
                           provider TEXT NOT NULL UNIQUE
);

INSERT INTO providers (provider) VALUES ('amazon.co.uk'), ('bhphotovideo.com'), ('ebay.co.uk'), ('wilkinson.co.uk'), ('jessops.com');

DESCRIBE cameras;

CREATE USER 'penguin'@'localhost' IDENTIFIED BY 'all-tux-on-board';

GRANT ALL PRIVILEGES ON webscraper TO 'penguin'@'localhost' IDENTIFIED  BY 'all-tux-on-board';

-- End DB Set Up