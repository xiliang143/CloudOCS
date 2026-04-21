-- =============================================
-- CloudOCS 数据库重构 - 分离文件夹和文档表
-- =============================================

-- 1. 创建 folders 表
CREATE TABLE IF NOT EXISTS folders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父文件夹ID，0表示根目录',
    name VARCHAR(255) NOT NULL COMMENT '文件夹名称',
    creator_id BIGINT NOT NULL COMMENT '创建者ID',
    creator_name VARCHAR(100) COMMENT '创建者名称',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件夹表';

-- 2. 从 documents 表迁移文件夹数据到 folders 表
-- 将 type=1 的文档迁移为文件夹
INSERT INTO folders (id, tenant_id, parent_id, name, creator_id, creator_name, created_at, updated_at, deleted)
SELECT id, tenant_id,
       CASE WHEN parent_id IS NULL THEN 0 ELSE parent_id END,
       title,
       creator_id,
       creator_name,
       created_at,
       updated_at,
       is_deleted
FROM documents WHERE type = 1;

-- 3. 修改 documents 表结构
-- 先检查 type 列是否存在，存在则删除
SET @dbname = DATABASE();
SET @tablename = 'documents';
SET @columnname = 'type';
SET @preparedStatement = (SELECT IF(
    (
        SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = @dbname
        AND TABLE_NAME = @tablename
        AND COLUMN_NAME = @columnname
    ) > 0,
    'ALTER TABLE documents DROP COLUMN type',
    'SELECT 1'
));
PREPARE alterIfExists FROM @preparedStatement;
EXECUTE alterIfExists;
DEALLOCATE PREPARE alterIfExists;

-- 4. 将 documents 表的 parent_id 重命名为 folder_id
SET @dbname = DATABASE();
SET @tablename = 'documents';
SET @columnname = 'parent_id';
SET @preparedStatement = (SELECT IF(
    (
        SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = @dbname
        AND TABLE_NAME = @tablename
        AND COLUMN_NAME = @columnname
    ) > 0,
    'ALTER TABLE documents CHANGE COLUMN parent_id folder_id BIGINT DEFAULT 0 COMMENT ''所属文件夹ID，0表示根目录''',
    'SELECT 1'
));
PREPARE alterIfExists FROM @preparedStatement;
EXECUTE alterIfExists;
DEALLOCATE PREPARE alterIfExists;

-- 5. 确认 documents 表结构
DESC documents;

-- 6. 确认 folders 表数据
SELECT COUNT(*) as folder_count FROM folders;

-- =============================================
-- 7. 创建 document_attachments 表（文档附件）
-- =============================================
CREATE TABLE IF NOT EXISTS document_attachments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    document_id BIGINT NOT NULL COMMENT '文档ID',
    filename VARCHAR(255) NOT NULL COMMENT '文件名',
    url VARCHAR(500) NOT NULL COMMENT '文件路径',
    size BIGINT COMMENT '文件大小（字节）',
    content_type VARCHAR(100) COMMENT '文件类型',
    uploader_id BIGINT COMMENT '上传者ID',
    uploader_name VARCHAR(100) COMMENT '上传者名称',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
    tenant_id BIGINT DEFAULT 0 COMMENT '租户ID',
    INDEX idx_document_id (document_id),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档附件表';

-- =============================================
-- 8. 创建邮箱文件夹表（邮箱功能）
-- =============================================
CREATE TABLE IF NOT EXISTS email_folders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type VARCHAR(20) NOT NULL COMMENT 'INBOX/SENT/DRAFT/ARCHIVE/SPAM/TRASH',
    name VARCHAR(100) NOT NULL COMMENT '文件夹名称',
    icon VARCHAR(50) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    unread_count INT DEFAULT 0 COMMENT '未读数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_user_id (user_id),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮箱文件夹表';

-- =============================================
-- 9. 创建邮箱标签表
-- =============================================
CREATE TABLE IF NOT EXISTS email_tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    name VARCHAR(50) NOT NULL COMMENT '标签名称',
    color VARCHAR(20) DEFAULT '#409eff' COMMENT '标签颜色',
    creator_id BIGINT COMMENT '创建者ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮箱标签表';

-- =============================================
-- 10. 修改 documents 表添加邮箱相关字段
-- =============================================
ALTER TABLE documents ADD COLUMN mailbox_type VARCHAR(20) DEFAULT 'INBOX' COMMENT '邮件类型';
ALTER TABLE documents ADD COLUMN priority INT DEFAULT 1 COMMENT '优先级:1低/2普通/3高/4紧急';
ALTER TABLE documents ADD COLUMN starred TINYINT DEFAULT 0 COMMENT '星标:0否/1是';
ALTER TABLE documents ADD COLUMN tags VARCHAR(500) COMMENT '标签(JSON数组)';
ALTER TABLE documents ADD COLUMN from_user_id BIGINT COMMENT '发件人ID';
ALTER TABLE documents ADD COLUMN from_user_name VARCHAR(100) COMMENT '发件人名称';
ALTER TABLE documents ADD COLUMN to_user_ids VARCHAR(500) COMMENT '收件人ID列表(JSON)';
ALTER TABLE documents ADD COLUMN to_user_names VARCHAR(500) COMMENT '收件人名称列表';
ALTER TABLE documents ADD COLUMN cc_user_ids VARCHAR(500) COMMENT '抄送人ID列表(JSON)';
ALTER TABLE documents ADD COLUMN is_read TINYINT DEFAULT 0 COMMENT '已读:0否/1是';
ALTER TABLE documents ADD COLUMN sender_deleted TINYINT DEFAULT 0 COMMENT '发件人删除标记';
