-- 演示用初始数据（仅在需要重置时导入）
-- 注意：会 DELETE 全表后再插入！本地一键脚本默认不再自动执行本文件。
-- 强制灌库：powershell -File scripts/run_building_os.ps1 -InitDb
-- 推荐演示用房：H202607130003（320万，在售，≥300万档位比例92折）
USE building_manos;

DELETE FROM sale_record;
DELETE FROM house;
DELETE FROM building;

INSERT INTO building (id, name, land_area, address, developer, remark, created_at) VALUES
('B202607130001', '阳光花园', 50000.00, '北京市朝阳区阳光路88号', '阳光地产', '地铁旁精品住宅', '2026-01-15 10:00:00'),
('B202607130002', '滨江壹号', 80000.00, '上海市浦东新区滨江大道168号', '滨江集团', '江景豪宅', '2026-02-20 10:00:00');

INSERT INTO house (id, building_id, building_no, room_no, area, unit_price, total_price, status, sold_at) VALUES
-- 阳光花园：低档 <100万
('H202607130001', 'B202607130001', '1', '101', 80.00, 10000.00, 800000.00, 'ON_SALE', NULL),
-- 阳光花园：中档 100~300万
('H202607130002', 'B202607130001', '1', '201', 120.00, 15000.00, 1800000.00, 'ON_SALE', NULL),
-- 阳光花园：高档 ≥300万（答辩演示推荐）
('H202607130003', 'B202607130001', '2', '801', 200.00, 16000.00, 3200000.00, 'ON_SALE', NULL),
-- 阳光花园：已售样本
('H202607130004', 'B202607130001', '3', '301', 90.00, 11000.00, 990000.00, 'SOLD', '2026-06-01 14:30:00'),
-- 滨江壹号：中档
('H202607130005', 'B202607130002', 'A', '1001', 150.00, 18000.00, 2700000.00, 'ON_SALE', NULL),
-- 滨江壹号：高档
('H202607130006', 'B202607130002', 'A', '2001', 220.00, 15000.00, 3300000.00, 'ON_SALE', NULL),
-- 滨江壹号：低档
('H202607130007', 'B202607130002', 'B', '501', 70.00, 12000.00, 840000.00, 'ON_SALE', NULL);

-- 已售样本 H202607130004 对应成交记录（99万档，满减2万）
INSERT INTO sale_record (id, house_id, original_price, discount_type, discount_value, final_price, customer_name, sold_at) VALUES
('S202607130001', 'H202607130004', 990000.00, 'THRESHOLD', 20000.00, 970000.00, '张三', '2026-06-01 14:30:00');
