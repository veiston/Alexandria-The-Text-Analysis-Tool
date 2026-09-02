-- SQL Schema
CREATE TABLE IF NOT EXISTS users (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    photo VARCHAR(255),
    organization VARCHAR(150),
    password VARCHAR(255) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS texts (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    file_name VARCHAR(255),
    file_type VARCHAR(20) NOT NULL,
    content LONGTEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS search_results (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    text_id INT NOT NULL,
    query VARCHAR(500) NOT NULL,
    results_data JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (text_id) REFERENCES texts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS text_statistics (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    text_id INT NOT NULL,
    statistics_data JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (text_id) REFERENCES texts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS term_analysis (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    text_id INT NOT NULL,
    term VARCHAR(500) NOT NULL,
    analysis_data JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (text_id) REFERENCES texts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS text_comparisons (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    comparison_data JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS text_comparison_texts (
    comparison_id INT NOT NULL,
    text_id INT NOT NULL,

    PRIMARY KEY (comparison_id, text_id),
    FOREIGN KEY (comparison_id) REFERENCES text_comparisons(id) ON DELETE CASCADE,
    FOREIGN KEY (text_id) REFERENCES texts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS term_comparisons (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    term VARCHAR(500) NOT NULL,
    comparison_data JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS term_comparison_texts (
    comparison_id INT NOT NULL,
    text_id INT NOT NULL,

    PRIMARY KEY (comparison_id, text_id),
    FOREIGN KEY (comparison_id) REFERENCES term_comparisons(id) ON DELETE CASCADE,
    FOREIGN KEY (text_id) REFERENCES texts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS quotations (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    text_id INT NOT NULL,
    quotation_text TEXT NOT NULL,
    location VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (text_id) REFERENCES texts(id) ON DELETE CASCADE
);