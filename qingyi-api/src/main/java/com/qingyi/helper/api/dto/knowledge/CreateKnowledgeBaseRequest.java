package com.qingyi.helper.api.dto.knowledge;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建知识库请求
 */
@Data
public class CreateKnowledgeBaseRequest {

    @NotBlank(message = "知识库名称不能为空")
    @Size(max = 100, message = "名称最长100个字符")
    private String name;

    @Size(max = 500, message = "描述最长500个字符")
    private String description;

    private String icon;

    private String embeddingModel;
    private Integer embeddingDimension;
    private String chunkStrategy;
    private Integer chunkSize;
    private Integer chunkOverlap;
    private Integer topK;
    private Float similarityThreshold;
}
