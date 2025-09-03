# 数据库检查说明

## 检查 notification 表

```sql
-- 连接到 content_db 数据库
USE content_db;

-- 查看 notification 表结构
DESCRIBE notification;

-- 查看所有通知数据
SELECT * FROM notification;

-- 查看特定用户的通知
SELECT * FROM notification WHERE user_id = '20200207';

-- 查看通知数量
SELECT COUNT(*) FROM notification;
SELECT COUNT(*) FROM notification WHERE user_id = '20200207';
```

## 检查 user 表

```sql
-- 连接到 user_db 数据库
USE user_db;

-- 查看 user 表结构
DESCRIBE user;

-- 查看用户数据
SELECT * FROM user WHERE user_id = '20200207';
```

## 可能的问题

1. **表名不匹配** - 确认表名是 `notification` 还是 `notifications`
2. **字段名不匹配** - 确认字段名是 `user_id` 还是 `userId`
3. **数据类型不匹配** - 确认 `user_id` 字段是 `varchar(15)` 类型
4. **数据为空** - 确认表中确实有数据

