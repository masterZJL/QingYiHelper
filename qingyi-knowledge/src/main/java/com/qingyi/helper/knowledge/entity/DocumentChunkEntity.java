package com.qingyi.helper.knowledge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qingyi.helper.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文档分块实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("document_chunk")
public class DocumentChunkEntity extends BaseEntity {

    private Long documentId;
    private Long knowledgeBaseId;
    private String content;
    private String contentType;
    private Integer chunkIndex;
    private Integer tokenCount;
    private String metadata;
}
