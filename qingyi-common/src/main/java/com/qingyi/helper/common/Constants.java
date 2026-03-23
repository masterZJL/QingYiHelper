package com.qingyi.helper.common;

/**
 * QingYiHelper - 企业级智能知识库问答平台
 * 多智能体 RAG 架构
 */
public final class Constants {

    private Constants() {}

    // ==================== Tenant ====================
    public static final String TENANT_HEADER = "X-Tenant-Id";
    public static final String DEFAULT_TENANT_ID = "1";

    // ==================== Document ====================
    public static final long MAX_FILE_SIZE = 100 * 1024 * 1024L; // 100MB

    public enum FileType {
        PDF, DOCX, DOC, TXT, MD,
        PNG, JPG, JPEG, GIF, BMP, WEBP,
        MP3, WAV, M4A, FLAC,
        MP4, AVI, MOV, MKV, WEBM
    }

    public enum MediaType {
        TEXT, MULTIMODAL
    }

    // ==================== Document Status ====================
    public enum DocStatus {
        PENDING, PARSING, CHUNKING, EMBEDDING, INDEXED, FAILED
    }

    // ==================== Chunk Strategy ====================
    public enum ChunkStrategy {
        FIXED, SEMANTIC, RECURSIVE, STRUCTURE_AWARE
    }

    // ==================== Content Type ====================
    public enum ContentType {
        TEXT, IMAGE, TABLE, FORMULA, CODE, HEADING, PARAGRAPH, MIXED
    }

    // ==================== Agent ====================
    public enum AgentName {
        INTENT_RECOGNITION("意图识别Agent"),
        QUERY_REWRITE("Query改写Agent"),
        RETRIEVAL("检索Agent"),
        GENERATION("生成Agent"),
        QUALITY_CHECK("质检Agent");

        private final String description;

        AgentName(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==================== Agent Status ====================
    public enum AgentStatus {
        SUCCESS, FAILED, RETRY, SKIPPED
    }

    // ==================== Conversation ====================
    public enum MessageRole {
        USER, ASSISTANT, SYSTEM
    }

    // ==================== User Role ====================
    public enum UserRole {
        ADMIN, MEMBER, VISITOR
    }

    // ==================== RocketMQ ====================
    public static final String DOC_PARSE_TOPIC = "DOC_PARSE_TOPIC";
    public static final String DOC_CHUNK_TOPIC = "DOC_CHUNK_TOPIC";
    public static final String DOC_EMBED_TOPIC = "DOC_EMBED_TOPIC";
    public static final String DOC_PARSE_GROUP = "DOC_PARSE_GROUP";
    public static final String DOC_CHUNK_GROUP = "DOC_CHUNK_GROUP";
    public static final String DOC_EMBED_GROUP = "DOC_EMBED_GROUP";

    // ==================== Redis ====================
    public static final String REDIS_CHAT_CACHE_PREFIX = "chat:cache:";
    public static final String REDIS_TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    public static final String REDIS_DOC_PROGRESS_PREFIX = "doc:progress:";
    public static final String REDIS_RATE_LIMIT_PREFIX = "rate:limit:";

    // ==================== Embedding ====================
    public static final int DEFAULT_EMBEDDING_DIMENSION = 1536;
    public static final int DEFAULT_CHUNK_SIZE = 512;
    public static final int DEFAULT_CHUNK_OVERLAP = 50;
    public static final int DEFAULT_TOP_K = 5;
    public static final float DEFAULT_SIMILARITY_THRESHOLD = 0.7f;
}
