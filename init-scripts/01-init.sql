-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS defaultmanager
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE defaultmanager;

-- 创建客户表
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    industry VARCHAR(50),
    region VARCHAR(50),
    external_rating VARCHAR(20),
    is_breached BOOLEAN DEFAULT FALSE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) DEFAULT 'USER',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建违约记录表
CREATE TABLE IF NOT EXISTS breach_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    reason VARCHAR(500) NOT NULL,
    severity ENUM('HIGH', 'MEDIUM', 'LOW') NOT NULL,
    applicant_id BIGINT NOT NULL,
    reviewer_id BIGINT,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    review_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE,
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status),
    INDEX idx_severity (severity),
    INDEX idx_create_time (create_time)
);

-- 创建重生记录表
CREATE TABLE IF NOT EXISTS rebirth_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    breach_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    reason VARCHAR(500) NOT NULL,
    applicant_id BIGINT NOT NULL,
    reviewer_id BIGINT,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    review_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (breach_id) REFERENCES breach_record(id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE,
    INDEX idx_breach_id (breach_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
);
