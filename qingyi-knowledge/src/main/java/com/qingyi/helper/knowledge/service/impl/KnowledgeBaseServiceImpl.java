package com.qingyi.helper.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingyi.helper.common.BizException;
import com.qingyi.helper.common.ErrorCode;
import com.qingyi.helper.common.entity.PageResult;
import com.qingyi.helper.knowledge.entity.KnowledgeBaseEntity;
import com.qingyi.helper.knowledge.mapper.KnowledgeBaseMapper;
import com.qingyi.helper.knowledge.service.KnowledgeBaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 知识库 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseServiceImpl extends ServiceImpl<KnowledgeBaseMapper, KnowledgeBaseEntity>
        implements KnowledgeBaseService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KnowledgeBaseEntity createKnowledgeBase(KnowledgeBaseEntity entity, Long tenantId) {
        // 校验名称唯一性
        long count = this.count(new LambdaQueryWrapper<KnowledgeBaseEntity>()
                .eq(KnowledgeBaseEntity::getTenantId, tenantId)
                .eq(KnowledgeBaseEntity::getName, entity.getName()));
        if (count > 0) {
            throw new BizException(ErrorCode.KB_NAME_DUPLICATE);
        }

        entity.setTenantId(tenantId);
        // 设置默认值
        if (entity.getEmbeddingModel() == null) {
            entity.setEmbeddingModel("text-embedding-v2");
        }
        if (entity.getEmbeddingDimension() == null) {
            entity.setEmbeddingDimension(1536);
        }
        if (entity.getChunkStrategy() == null) {
            entity.setChunkStrategy("STRUCTURE_AWARE");
        }
        if (entity.getChunkSize() == null) {
            entity.setChunkSize(512);
        }
        if (entity.getChunkOverlap() == null) {
            entity.setChunkOverlap(50);
        }
        if (entity.getTopK() == null) {
            entity.setTopK(5);
        }
        if (entity.getSimilarityThreshold() == null) {
            entity.setSimilarityThreshold(0.7f);
        }
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        if (entity.getDocumentCount() == null) {
            entity.setDocumentCount(0);
        }
        if (entity.getTotalChunks() == null) {
            entity.setTotalChunks(0);
        }

        this.save(entity);
        log.info("创建知识库: id={}, name={}, tenantId={}", entity.getId(), entity.getName(), tenantId);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateKnowledgeBase(Long id, KnowledgeBaseEntity entity, Long tenantId) {
        KnowledgeBaseEntity existing = this.getById(id);
        if (existing == null || !existing.getTenantId().equals(tenantId)) {
            throw new BizException(ErrorCode.KB_NOT_FOUND);
        }

        // 如果改了名字，检查唯一性
        if (StringUtils.hasText(entity.getName()) && !entity.getName().equals(existing.getName())) {
            long count = this.count(new LambdaQueryWrapper<KnowledgeBaseEntity>()
                    .eq(KnowledgeBaseEntity::getTenantId, tenantId)
                    .eq(KnowledgeBaseEntity::getName, entity.getName())
                    .ne(KnowledgeBaseEntity::getId, id));
            if (count > 0) {
                throw new BizException(ErrorCode.KB_NAME_DUPLICATE);
            }
        }

        // 只更新非空字段
        if (StringUtils.hasText(entity.getName())) existing.setName(entity.getName());
        if (entity.getDescription() != null) existing.setDescription(entity.getDescription());
        if (entity.getIcon() != null) existing.setIcon(entity.getIcon());
        if (entity.getEmbeddingModel() != null) existing.setEmbeddingModel(entity.getEmbeddingModel());
        if (entity.getChunkStrategy() != null) existing.setChunkStrategy(entity.getChunkStrategy());
        if (entity.getChunkSize() != null) existing.setChunkSize(entity.getChunkSize());
        if (entity.getChunkOverlap() != null) existing.setChunkOverlap(entity.getChunkOverlap());
        if (entity.getTopK() != null) existing.setTopK(entity.getTopK());
        if (entity.getSimilarityThreshold() != null) existing.setSimilarityThreshold(entity.getSimilarityThreshold());

        this.updateById(existing);
        log.info("更新知识库: id={}, tenantId={}", id, tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteKnowledgeBase(Long id, Long tenantId) {
        KnowledgeBaseEntity existing = this.getById(id);
        if (existing == null || !existing.getTenantId().equals(tenantId)) {
            throw new BizException(ErrorCode.KB_NOT_FOUND);
        }

        // TODO: 级联删除文档、向量数据（Phase 2 实现）
        // 1. 删除 Milvus 中的向量数据
        // 2. 删除 ES 中的索引数据
        // 3. 删除 MinIO 中的文件
        // 4. 删除文档记录
        // 5. 删除知识库

        this.removeById(id);
        log.info("删除知识库: id={}, name={}, tenantId={}", id, existing.getName(), tenantId);
    }

    @Override
    public PageResult<KnowledgeBaseEntity> pageKnowledgeBase(Long tenantId, int current, int size, String keyword) {
        LambdaQueryWrapper<KnowledgeBaseEntity> wrapper = new LambdaQueryWrapper<KnowledgeBaseEntity>()
                .eq(KnowledgeBaseEntity::getTenantId, tenantId)
                .eq(KnowledgeBaseEntity::getStatus, 1)
                .and(StringUtils.hasText(keyword), w -> w
                        .like(KnowledgeBaseEntity::getName, keyword)
                        .or()
                        .like(KnowledgeBaseEntity::getDescription, keyword))
                .orderByDesc(KnowledgeBaseEntity::getUpdatedAt);

        Page<KnowledgeBaseEntity> page = this.page(new Page<>(current, size), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }
}
