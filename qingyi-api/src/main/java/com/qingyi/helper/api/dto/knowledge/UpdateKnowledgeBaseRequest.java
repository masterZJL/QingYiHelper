package com.qingyi.helper.api.dto.knowledge;

import lombok.Data;

/**
 * 更新知识库请求
 */
@Data
public class UpdateKnowledgeBaseRequest {

    private String name;
    private String description;
    private String icon;
    private String embeddingModel;
    private String chunkStrategy;
    private Integer chunkSize;
    private Integer chunkOverlap;
    private Integer topK;
    private Float similarityThreshold;
}
