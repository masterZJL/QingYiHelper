package com.qingyi.helper.knowledge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qingyi.helper.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_base")
public class KnowledgeBaseEntity extends BaseEntity {

    private Long tenantId;
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
    private Integer status;
    private Integer documentCount;
    private Integer totalChunks;
}
