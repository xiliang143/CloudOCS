-- =============================================
-- 移除租户功能 - 数据库结构修改
-- =============================================

-- 1. 删除 tenants 表
DROP TABLE IF EXISTS tenants;

-- 2. 从 users 表移除 tenant_id 字段和索引
ALTER TABLE users DROP COLUMN tenant_id;
ALTER TABLE users DROP INDEX idx_tenant_id;

-- 3. 从 documents 表移除 tenant_id 字段和索引
ALTER TABLE documents DROP COLUMN tenant_id;
ALTER TABLE documents DROP INDEX idx_tenant_id;

-- 4. 从 folders 表移除 tenant_id 字段和索引
ALTER TABLE folders DROP COLUMN tenant_id;
ALTER TABLE folders DROP INDEX idx_tenant_id;

-- 5. 从 email_folders 表移除 tenant_id 字段和索引
ALTER TABLE email_folders DROP COLUMN tenant_id;
ALTER TABLE email_folders DROP INDEX idx_tenant_id;

-- 6. 从 email_tags 表移除 tenant_id 字段和索引
ALTER TABLE email_tags DROP COLUMN tenant_id;
ALTER TABLE email_tags DROP INDEX idx_tenant_id;

-- 7. 从 document_attachments 表移除 tenant_id 字段和索引
ALTER TABLE document_attachments DROP COLUMN tenant_id;
ALTER TABLE document_attachments DROP INDEX idx_tenant_id;

-- 8. 从 sms_codes 表移除 tenant_id 字段（如果存在）
ALTER TABLE sms_codes DROP COLUMN tenant_id IF EXISTS;

-- =============================================
-- 确认修改后的表结构
-- =============================================
DESC users;
DESC documents;
DESC folders;
DESC email_folders;
DESC email_tags;
DESC document_attachments;
