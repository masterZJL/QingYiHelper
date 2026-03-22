package com.qingyi.helper.knowledge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingyi.helper.common.entity.PageResult;
import com.qingyi.helper.knowledge.entity.KnowledgeBaseEntity;

/**
 * 知识库 Service 接口
 */
public interface KnowledgeBaseService extends IService<KnowledgeBaseEntity> {

    /**
     * 创建知识库
     */
    KnowledgeBaseEntity createKnowledgeBase(KnowledgeBaseEntity entity, Long tenantId);

    /**
     * 更新知识库
     */
    void updateKnowledgeBase(Long id, KnowledgeBaseEntity entity, Long tenantId);

    /**
     * 删除知识库（级联删除文档和向量数据）
     */
    void deleteKnowledgeBase(Long id, Long tenantId);

    /**
     * 分页查询知识库
     */
    PageResult<KnowledgeBaseEntity> pageKnowledgeBase(Long tenantId, int current, int size, String keyword);
}
