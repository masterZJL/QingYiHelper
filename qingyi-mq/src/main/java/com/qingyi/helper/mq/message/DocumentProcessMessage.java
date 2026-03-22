package com.qingyi.helper.mq.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文档处理消息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentProcessMessage implements Serializable {

    private Long documentId;
    private Long knowledgeBaseId;
    private String fileName;
    private String filePath;
    private String fileType;
    private String mediaType;
    private Long tenantId;
    private Integer retryCount;
}
