package com.qingyi.helper.knowledge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qingyi.helper.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文档实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("document")
public class DocumentEntity extends BaseEntity {

    private Long knowledgeBaseId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private String mediaType;
    private Integer duration;
    private Integer chunkCount;
    private Integer parseProgress;
    private String status;
    private String errorMsg;
    private String metadata;
}
