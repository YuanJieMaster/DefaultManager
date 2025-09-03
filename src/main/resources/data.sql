-- 创建初始超级管理员用户 (密码: user123)
INSERT INTO users (username, password, email, first_name, last_name, role, enabled, created_at)
VALUES (
           'admin',
           '$2a$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm', -- 这是 "user123" 的 BCrypt 哈希
           'admin@xquant.com',
           'Super',
           'Admin',
           'ADMIN',
           true,
           CURRENT_TIMESTAMP
       );

-- 插入客户数据
INSERT INTO customer (name, industry, region, external_rating, is_breached, create_time)
VALUES
    ('阿里巴巴集团', '电子商务', '亚洲', 'AA', false, CURRENT_TIMESTAMP),
    ('腾讯控股有限公司', '互联网服务', '亚洲', 'AA+', false, CURRENT_TIMESTAMP),
    ('百度公司', '人工智能', '亚洲', 'A+', false, CURRENT_TIMESTAMP),
    ('京东集团', '电子商务', '亚洲', 'A', false, CURRENT_TIMESTAMP),
    ('美团点评', '本地生活服务', '亚洲', 'A-', false, CURRENT_TIMESTAMP),
    ('字节跳动', '社交媒体', '亚洲', 'AA-', false, CURRENT_TIMESTAMP),
    ('小米集团', '消费电子', '亚洲', 'A', false, CURRENT_TIMESTAMP),
    ('华为技术有限公司', '通信设备', '亚洲', 'AA', false, CURRENT_TIMESTAMP),
    ('比亚迪股份有限公司', '新能源汽车', '亚洲', 'A+', false, CURRENT_TIMESTAMP),
    ('宁德时代新能源科技', '动力电池', '亚洲', 'AA-', false, CURRENT_TIMESTAMP);

-- 插入一些违约客户示例
INSERT INTO customer (name, industry, region, external_rating, is_breached, create_time)
VALUES
    ('某房地产公司', '房地产', '亚洲', 'CCC', true, CURRENT_TIMESTAMP),
    ('某P2P平台', '互联网金融', '亚洲', 'D', true, CURRENT_TIMESTAMP);
