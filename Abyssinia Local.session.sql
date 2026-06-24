CREATE DATABASE abyssinia_market;
USE abyssinia_market;

CREATE TABLE users (
    id       INT PRIMARY KEY AUTO_INCREMENT,
    name     VARCHAR(100) NOT NULL,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role     VARCHAR(20)  NOT NULL
);

CREATE TABLE menu_items (
    id       INT PRIMARY KEY AUTO_INCREMENT,
    name     VARCHAR(100)  NOT NULL,
    category VARCHAR(20)   NOT NULL,
    price    DECIMAL(10,2) NOT NULL
);

CREATE TABLE orders (
    id                INT PRIMARY KEY AUTO_INCREMENT,
    customer_username VARCHAR(50)   NOT NULL,
    order_time        DATETIME      NOT NULL,
    total             DECIMAL(10,2) NOT NULL
);

CREATE TABLE order_items (
    order_id     INT NOT NULL,
    menu_item_id INT NOT NULL,
    FOREIGN KEY (order_id)     REFERENCES orders(id)     ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);

CREATE TABLE login_history (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(50) NOT NULL,
    login_time DATETIME    NOT NULL
);
