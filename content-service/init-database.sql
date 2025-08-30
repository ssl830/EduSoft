-- Content Service 数据库初始化脚本
-- 此脚本用于快速初始化content-service的数据库

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `content-db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `content-db`;

-- 注意：完整的表结构和数据请执行 src/main/resources/sql/content.sql
-- 此脚本仅用于创建数据库

-- 验证数据库创建
SELECT 'Content Service 数据库创建成功！' AS message;
