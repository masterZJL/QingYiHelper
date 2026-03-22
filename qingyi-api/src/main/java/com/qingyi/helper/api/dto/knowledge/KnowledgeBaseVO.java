package com.qingyi.helper.api.dto.knowledge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 知识库详情响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBaseVO {

    private Long id;
    private String name;
    private String description;
    private String icon;
    private String embeddingModel;
    private Integer embeddingDimension;
    private String chunkStrategy;
    private Integer chunkSize;
    private Integer chunkOverlap;
    private Integer topK;
    private Float similarityThreshold;
    private Integer documentCount;
    private Integer totalChunks;
    private String createdAt;
    private String updatedAt;
}
