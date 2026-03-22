package com.qingyi.helper.knowledge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingyi.helper.common.entity.PageResult;
import com.qingyi.helper.knowledge.entity.DocumentEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档 Service 接口
 */
public interface DocumentService extends IService<DocumentEntity> {

    /**
     * 上传文档
     */
    DocumentEntity uploadDocument(Long knowledgeBaseId, MultipartFile file, Long tenantId);

    /**
     * 删除文档
     */
    void deleteDocument(Long documentId, Long knowledgeBaseId, Long tenantId);

    /**
     * 重试失败的文档处理
     */
    void retryDocument(Long documentId, Long knowledgeBaseId, Long tenantId);

    /**
     * 分页查询文档
     */
    PageResult<DocumentEntity> pageDocument(Long knowledgeBaseId, int current, int size, String status);

    /**
     * 更新文档状态
     */
    void updateStatus(Long documentId, String status, String errorMsg);

    /**
     * 更新解析进度
     */
    void updateProgress(Long documentId, int progress);
}
