CREATE DATABASE IF NOT EXISTS alexandria
    CHARACTER SET utf8mb4;

CREATE USER IF NOT EXISTS 'alexandria'@'localhost'
    IDENTIFIED BY 'alexandria';

ALTER USER 'alexandria'@'localhost'
    IDENTIFIED BY 'alexandria';

GRANT ALL PRIVILEGES
    ON alexandria.*
    TO 'alexandria'@'localhost';

FLUSH PRIVILEGES;

USE alexandria;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS term_comparison_texts;
DROP TABLE IF EXISTS text_comparison_texts;
DROP TABLE IF EXISTS quotations;
DROP TABLE IF EXISTS term_comparisons;
DROP TABLE IF EXISTS text_comparisons;
DROP TABLE IF EXISTS term_analysis;
DROP TABLE IF EXISTS text_statistics;
DROP TABLE IF EXISTS search_results;
DROP TABLE IF EXISTS texts;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

SOURCE database/schema.sql;
SOURCE database/data.sql;
