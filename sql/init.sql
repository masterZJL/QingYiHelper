-- ============================================================
-- QingYiHelper - 企业级智能知识库问答平台
-- 数据库初始化脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS `qingyi_helper` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `qingyi_helper`;

-- ----------------------------
-- 租户表
-- ----------------------------
DROP TABLE IF EXISTS `tenant`;
CREATE TABLE `tenant` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL COMMENT '租户名称',
    `code` VARCHAR(50) NOT NULL COMMENT '租户编码',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    `max_knowledge_bases` INT NOT NULL DEFAULT 5 COMMENT '最大知识库数量',
    `max_documents` INT NOT NULL DEFAULT 100 COMMENT '最大文档数量',
    `max_storage_mb` BIGINT NOT NULL DEFAULT 1024 COMMENT '最大存储空间(MB)',
    `used_storage_mb` BIGINT NOT NULL DEFAULT 0 COMMENT '已用存储空间(MB)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户表';

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `tenant_id` BIGINT NOT NULL COMMENT '所属租户',
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(200) NOT NULL,
    `nickname` VARCHAR(50) DEFAULT NULL,
    `email` VARCHAR(100) DEFAULT NULL,
    `avatar` VARCHAR(500) DEFAULT NULL,
    `role` VARCHAR(20) NOT NULL DEFAULT 'MEMBER' COMMENT 'ADMIN/MEMBER/VISITOR',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '0-禁用 1-启用',
    `last_login_at` DATETIME DEFAULT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_username` (`tenant_id`, `username`),
    KEY `idx_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- 知识库表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_base`;
CREATE TABLE `knowledge_base` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `tenant_id` BIGINT NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT DEFAULT NULL,
    `icon` VARCHAR(200) DEFAULT NULL,
    `embedding_model` VARCHAR(50) NOT NULL DEFAULT 'text-embedding-v2' COMMENT '向量模型',
    `embedding_dimension` INT NOT NULL DEFAULT 1536 COMMENT '向量维度',
    `chunk_strategy` VARCHAR(30) NOT NULL DEFAULT 'STRUCTURE_AWARE' COMMENT '分块策略: FIXED/SEMANTIC/RECURSIVE/STRUCTURE_AWARE',
    `chunk_size` INT NOT NULL DEFAULT 512 COMMENT '分块大小',
    `chunk_overlap` INT NOT NULL DEFAULT 50 COMMENT '分块重叠',
    `top_k` INT NOT NULL DEFAULT 5 COMMENT '检索返回数量',
    `similarity_threshold` FLOAT NOT NULL DEFAULT 0.7 COMMENT '相似度阈值',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '0-禁用 1-启用',
    `document_count` INT NOT NULL DEFAULT 0,
    `total_chunks` INT NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库表';

-- ----------------------------
-- 文档表
-- ----------------------------
DROP TABLE IF EXISTS `document`;
CREATE TABLE `document` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `knowledge_base_id` BIGINT NOT NULL,
    `file_name` VARCHAR(255) NOT NULL,
    `file_path` VARCHAR(500) DEFAULT NULL COMMENT 'MinIO路径',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(bytes)',
    `file_type` VARCHAR(20) NOT NULL COMMENT 'PDF/DOCX/TXT/MD/IMAGE/AUDIO/VIDEO',
    `media_type` VARCHAR(20) NOT NULL DEFAULT 'TEXT' COMMENT 'TEXT/MULTIMODAL',
    `duration` INT DEFAULT NULL COMMENT '音视频时长(秒)',
    `chunk_count` INT NOT NULL DEFAULT 0 COMMENT '分块数量',
    `parse_progress` INT NOT NULL DEFAULT 0 COMMENT '解析进度(0-100)',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/PARSING/CHUNKING/EMBEDDING/INDEXED/FAILED',
    `error_msg` TEXT DEFAULT NULL COMMENT '处理失败原因',
    `metadata` JSON DEFAULT NULL COMMENT '文档元数据',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_kb` (`knowledge_base_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档表';

-- ----------------------------
-- 文档分块表
-- ----------------------------
DROP TABLE IF EXISTS `document_chunk`;
CREATE TABLE `document_chunk` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `document_id` BIGINT NOT NULL,
    `knowledge_base_id` BIGINT NOT NULL,
    `content` TEXT NOT NULL COMMENT '分块文本内容',
    `content_type` VARCHAR(20) NOT NULL DEFAULT 'TEXT' COMMENT 'TEXT/IMAGE/TABLE/FORMULA/CODE/MIXED',
    `chunk_index` INT NOT NULL COMMENT '分块序号',
    `token_count` INT DEFAULT NULL COMMENT 'token数量',
    `metadata` JSON DEFAULT NULL COMMENT '元数据',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_document` (`document_id`),
    KEY `idx_kb` (`knowledge_base_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档分块表';

-- ----------------------------
-- 对话表
-- ----------------------------
DROP TABLE IF EXISTS `conversation`;
CREATE TABLE `conversation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `knowledge_base_id` BIGINT DEFAULT NULL COMMENT '关联知识库',
    `title` VARCHAR(200) DEFAULT NULL,
    `agent_trace` JSON DEFAULT NULL COMMENT 'Agent执行轨迹',
    `message_count` INT NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话表';

-- ----------------------------
-- 对话消息表
-- ----------------------------
DROP TABLE IF EXISTS `conversation_message`;
CREATE TABLE `conversation_message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `conversation_id` BIGINT NOT NULL,
    `role` VARCHAR(20) NOT NULL COMMENT 'USER/ASSISTANT/SYSTEM',
    `content` TEXT NOT NULL,
    `sources` JSON DEFAULT NULL COMMENT '引用来源',
    `agent_steps` JSON DEFAULT NULL COMMENT 'Agent执行步骤记录',
    `token_count` INT DEFAULT NULL COMMENT '消耗token数',
    `latency_ms` INT DEFAULT NULL COMMENT '响应耗时(ms)',
    `model` VARCHAR(50) DEFAULT NULL COMMENT '使用的模型',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_conversation` (`conversation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话消息表';

-- ----------------------------
-- Agent执行日志
-- ----------------------------
DROP TABLE IF EXISTS `agent_execution_log`;
CREATE TABLE `agent_execution_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `conversation_id` BIGINT DEFAULT NULL,
    `message_id` BIGINT DEFAULT NULL,
    `agent_name` VARCHAR(50) NOT NULL COMMENT 'Agent名称',
    `step_order` INT NOT NULL COMMENT '执行顺序',
    `input_text` TEXT DEFAULT NULL COMMENT '输入内容',
    `output_text` TEXT DEFAULT NULL COMMENT '输出内容',
    `token_count` INT DEFAULT NULL,
    `latency_ms` INT DEFAULT NULL COMMENT '执行耗时',
    `status` VARCHAR(20) NOT NULL COMMENT 'SUCCESS/FAILED/RETRY',
    `retry_count` INT NOT NULL DEFAULT 0,
    `error_msg` TEXT DEFAULT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_message` (`message_id`),
    KEY `idx_agent` (`agent_name`)
) ExecutionLog=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent执行日志';

-- ----------------------------
-- 操作审计日志
-- ----------------------------
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE `audit_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `tenant_id` BIGINT DEFAULT NULL,
    `user_id` BIGINT DEFAULT NULL,
    `operation` VARCHAR(50) NOT NULL COMMENT '操作类型',
    `resource_type` VARCHAR(50) DEFAULT NULL COMMENT '资源类型',
    `resource_id` BIGINT DEFAULT NULL COMMENT '资源ID',
    `detail` TEXT DEFAULT NULL COMMENT '操作详情',
    `ip` VARCHAR(50) DEFAULT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_time` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作审计日志';

-- ----------------------------
-- 初始数据
-- ----------------------------
INSERT INTO `tenant` (`id`, `name`, `code`, `status`, `max_knowledge_bases`, `max_documents`, `max_storage_mb`) VALUES
(1, '默认租户', 'default', 1, 100, 10000, 10240);

-- 默认管理员密码: admin123 (BCrypt加密)
INSERT INTO `sys_user` (`id`, `tenant_id`, `username`, `password`, `nickname`, `role`, `status`) VALUES
(1, 1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', 'ADMIN', 1);
