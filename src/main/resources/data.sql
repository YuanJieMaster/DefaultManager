-- 插入金融机构类交易对手
INSERT INTO counterparty (is_active, code, created_at, credit_rating, industry, name, region, updated_at)
VALUES
    (1, 'GS', CURRENT_TIMESTAMP, 'AA', 'Investment Banking', 'Goldman Sachs', 'North America', CURRENT_TIMESTAMP),
    (1, 'JPM', CURRENT_TIMESTAMP, 'AA-', 'Commercial Banking', 'JPMorgan Chase', 'North America', CURRENT_TIMESTAMP),
    (1, 'MS', CURRENT_TIMESTAMP, 'A+', 'Investment Banking', 'Morgan Stanley', 'North America', CURRENT_TIMESTAMP),
    (1, 'BAC', CURRENT_TIMESTAMP, 'A', 'Commercial Banking', 'Bank of America', 'North America', CURRENT_TIMESTAMP),
    (1, 'C', CURRENT_TIMESTAMP, 'BBB+', 'Commercial Banking', 'Citigroup', 'North America', CURRENT_TIMESTAMP);

-- 插入科技公司类交易对手
INSERT INTO counterparty (is_active, code, created_at, credit_rating, industry, name, region, updated_at)
VALUES
    (1, 'AAPL', CURRENT_TIMESTAMP, 'AA+', 'Technology', 'Apple Inc.', 'North America', CURRENT_TIMESTAMP),
    (1, 'MSFT', CURRENT_TIMESTAMP, 'AAA', 'Technology', 'Microsoft Corporation', 'North America', CURRENT_TIMESTAMP),
    (1, 'GOOGL', CURRENT_TIMESTAMP, 'AA', 'Technology', 'Alphabet Inc.', 'North America', CURRENT_TIMESTAMP),
    (1, 'AMZN', CURRENT_TIMESTAMP, 'AA-', 'E-Commerce', 'Amazon.com Inc.', 'North America', CURRENT_TIMESTAMP),
    (1, 'TSLA', CURRENT_TIMESTAMP, 'BB+', 'Automotive', 'Tesla Inc.', 'North America', CURRENT_TIMESTAMP);

-- 插入欧洲地区交易对手
INSERT INTO counterparty (is_active, code, created_at, credit_rating, industry, name, region, updated_at)
VALUES
    (1, 'HSBC', CURRENT_TIMESTAMP, 'A', 'Commercial Banking', 'HSBC Holdings', 'Europe', CURRENT_TIMESTAMP),
    (1, 'UBS', CURRENT_TIMESTAMP, 'A+', 'Investment Banking', 'UBS Group AG', 'Europe', CURRENT_TIMESTAMP),
    (1, 'DB', CURRENT_TIMESTAMP, 'BBB', 'Commercial Banking', 'Deutsche Bank', 'Europe', CURRENT_TIMESTAMP),
    (1, 'BNP', CURRENT_TIMESTAMP, 'A-', 'Commercial Banking', 'BNP Paribas', 'Europe', CURRENT_TIMESTAMP),
    (1, 'SAN', CURRENT_TIMESTAMP, 'BBB+', 'Commercial Banking', 'Santander', 'Europe', CURRENT_TIMESTAMP);

-- 插入亚洲地区交易对手
INSERT INTO counterparty (is_active, code, created_at, credit_rating, industry, name, region, updated_at)
VALUES
    (1, 'ICBC', CURRENT_TIMESTAMP, 'A', 'Commercial Banking', 'Industrial and Commercial Bank of China', 'Asia', CURRENT_TIMESTAMP),
    (1, 'MUFG', CURRENT_TIMESTAMP, 'A+', 'Commercial Banking', 'Mitsubishi UFJ Financial Group', 'Asia', CURRENT_TIMESTAMP),
    (1, 'HDFC', CURRENT_TIMESTAMP, 'BBB+', 'Commercial Banking', 'HDFC Bank', 'Asia', CURRENT_TIMESTAMP),
    (1, 'SMSNG', CURRENT_TIMESTAMP, 'AA-', 'Technology', 'Samsung Electronics', 'Asia', CURRENT_TIMESTAMP),
    (1, 'TENCENT', CURRENT_TIMESTAMP, 'A+', 'Technology', 'Tencent Holdings', 'Asia', CURRENT_TIMESTAMP);

-- 插入一些非活跃的交易对手
INSERT INTO counterparty (is_active, code, created_at, credit_rating, industry, name, region, updated_at)
VALUES
    (0, 'LEH', CURRENT_TIMESTAMP, 'D', 'Investment Banking', 'Lehman Brothers', 'North America', CURRENT_TIMESTAMP),
    (0, 'BEAR', CURRENT_TIMESTAMP, 'D', 'Investment Banking', 'Bear Stearns', 'North America', CURRENT_TIMESTAMP),
    (0, 'WCOM', CURRENT_TIMESTAMP, 'D', 'Telecom', 'WorldCom', 'North America', CURRENT_TIMESTAMP);

-- 插入不同行业的多样化交易对手
INSERT INTO counterparty (is_active, code, created_at, credit_rating, industry, name, region, updated_at)
VALUES
    (1, 'XOM', CURRENT_TIMESTAMP, 'AA', 'Energy', 'Exxon Mobil', 'North America', CURRENT_TIMESTAMP),
    (1, 'JNJ', CURRENT_TIMESTAMP, 'AAA', 'Pharmaceuticals', 'Johnson & Johnson', 'North America', CURRENT_TIMESTAMP),
    (1, 'WMT', CURRENT_TIMESTAMP, 'AA-', 'Retail', 'Walmart', 'North America', CURRENT_TIMESTAMP),
    (1, 'PG', CURRENT_TIMESTAMP, 'A+', 'Consumer Goods', 'Procter & Gamble', 'North America', CURRENT_TIMESTAMP),
    (1, 'V', CURRENT_TIMESTAMP, 'AA+', 'Financial Services', 'Visa Inc.', 'North America', CURRENT_TIMESTAMP);

-- 创建初始管理员用户 (密码: admin123)
INSERT INTO users (username, password, email, first_name, last_name, role, enabled, created_at)
VALUES (
           'admin',
           '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- 这是 "admin123" 的 BCrypt 哈希
           'admin@xquant.com',
           'System',
           'Administrator',
           'ROLE_ADMIN',
           true,
           CURRENT_TIMESTAMP
       );

-- 创建初始普通用户 (密码: user123)
INSERT INTO users (username, password, email, first_name, last_name, role, enabled, created_at)
VALUES (
           'user1',
           '$2a$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm', -- 这是 "user123" 的 BCrypt 哈希
           'user1@xquant.com',
           'John',
           'Doe',
           'ROLE_USER',
           true,
           CURRENT_TIMESTAMP
       );
