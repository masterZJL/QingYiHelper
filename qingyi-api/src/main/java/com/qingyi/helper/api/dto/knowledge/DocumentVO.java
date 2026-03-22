package com.qingyi.helper.api.dto.knowledge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档详情响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVO {

    private Long id;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String mediaType;
    private Integer duration;
    private Integer chunkCount;
    private Integer parseProgress;
    private String status;
    private String errorMsg;
    private String createdAt;
}
