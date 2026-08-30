-- SQL Schema
CREATE TABLE users (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    photo VARCHAR(255),
    organization VARCHAR(150),
    password VARCHAR(255) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE texts (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    file_name VARCHAR(255),
    file_type VARCHAR(20),
    content LONGTEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE text_statistics (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    text_id INT NOT NULL,
    statistics_data LONGTEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (text_id) REFERENCES texts(id)
);

CREATE TABLE search_results (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    text_id INT NOT NULL,
    query VARCHAR(500) NOT NULL,
    result_text LONGTEXT NOT NULL,
    location VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (text_id) REFERENCES texts(id)
);

CREATE TABLE quotations (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    text_id INT NOT NULL,
    quotation_text TEXT NOT NULL,
    location VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (text_id) REFERENCES texts(id)
);