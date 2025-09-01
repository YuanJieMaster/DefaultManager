-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS defaultmanager
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE defaultmanager;

-- 创建示例表（根据你的实际需求修改）
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入示例数据（可选）
INSERT INTO users (username, email) VALUES
                                        ('admin', 'admin@example.com'),
                                        ('user1', 'user1@example.com')
    ON DUPLICATE KEY UPDATE email = VALUES(email);

-- 创建交易对手表
CREATE TABLE IF NOT EXISTS counterparty (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    rating VARCHAR(20),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO counterparty (code, name, rating) VALUES
                                                  ('CP001', '中国银行', 'AA'),
                                                  ('CP002', '工商银行', 'A'),
                                                  ('CP003', '建设银行', 'BBB'),
                                                  ('CP004', '农业银行', 'BBB'),
                                                  ('CP005', '交通银行', 'BB')
    ON DUPLICATE KEY UPDATE name = VALUES(name), rating = VALUES(rating);

-- 创建其他表...
-- CREATE TABLE IF NOT EXISTS your_table (...);
