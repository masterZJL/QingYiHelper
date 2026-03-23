package com.qingyi.helper.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingyi.helper.common.BizException;
import com.qingyi.helper.common.Constants;
import com.qingyi.helper.common.ErrorCode;
import com.qingyi.helper.common.entity.PageResult;
import com.qingyi.helper.knowledge.entity.DocumentEntity;
import com.qingyi.helper.knowledge.mapper.DocumentMapper;
import com.qingyi.helper.knowledge.service.DocumentService;
import com.qingyi.helper.mq.message.DocumentProcessMessage;
import com.qingyi.helper.mq.producer.DocumentProcessProducer;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

/**
 * 文档 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, DocumentEntity>
        implements DocumentService {

    private final MinioClient minioClient;
    private final DocumentProcessProducer documentProcessProducer;

    @Value("${minio.bucket:qingyi-helper}")
    private String bucket;

    /** 允许上传的文件类型 */
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "pdf", "docx", "doc", "txt", "md",
            "png", "jpg", "jpeg", "gif", "bmp", "webp",
            "mp3", "wav", "m4a", "flac",
            "mp4", "avi", "mov", "mkv", "webm"
    );

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentEntity uploadDocument(Long knowledgeBaseId, MultipartFile file, Long tenantId) {
        // 1. 校验文件
        if (file == null || file.isEmpty()) {
            throw new BizException(ErrorCode.DOC_FILE_EMPTY);
        }
        if (file.getSize() > Constants.MAX_FILE_SIZE) {
            throw new BizException(ErrorCode.DOC_FILE_TOO_LARGE);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_TYPES.contains(extension.toLowerCase())) {
            throw new BizException(ErrorCode.DOC_FILE_TYPE_UNSUPPORTED);
        }

        // 2. 上传到 MinIO
        String objectName = buildObjectName(knowledgeBaseId, extension);
        try {
            ensureBucketExists();

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            log.error("文件上传MinIO失败: {}", e.getMessage(), e);
            throw new BizException(ErrorCode.INTERNAL_ERROR.getCode(), "文件上传失败");
        }

        // 3. 保存文档记录
        DocumentEntity document = new DocumentEntity();
        document.setKnowledgeBaseId(knowledgeBaseId);
        document.setFileName(originalFilename);
        document.setFilePath(objectName);
        document.setFileSize(file.getSize());
        document.setFileType(extension.toUpperCase());
        document.setMediaType(determineMediaType(extension));
        document.setStatus(Constants.DocStatus.PENDING.name());
        document.setParseProgress(0);
        document.setChunkCount(0);

        this.save(document);

        // 4. 发送 MQ 消息，触发异步文档处理
        DocumentProcessMessage message = DocumentProcessMessage.builder()
                .documentId(document.getId())
                .knowledgeBaseId(document.getKnowledgeBaseId())
                .fileName(document.getFileName())
                .filePath(document.getFilePath())
                .fileType(document.getFileType())
                .mediaType(document.getMediaType())
                .retryCount(0)
                .build();
        documentProcessProducer.sendParseMessage(message);

        log.info("文档上传成功: id={}, fileName={}, kbId={}, tenantId={}",
                document.getId(), originalFilename, knowledgeBaseId, tenantId);
        return document;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDocument(Long documentId, Long knowledgeBaseId, Long tenantId) {
        DocumentEntity document = this.getById(documentId);
        if (document == null || !document.getKnowledgeBaseId().equals(knowledgeBaseId)) {
            throw new BizException(ErrorCode.DOC_NOT_FOUND);
        }

        // TODO: 删除 MinIO 文件、Milvus 向量、ES 索引（Phase 2 实现）
        // 删除 MinIO 文件
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(document.getFilePath())
                    .build());
        } catch (Exception e) {
            log.warn("删除MinIO文件失败: {}", e.getMessage());
        }

        this.removeById(documentId);
        log.info("删除文档: id={}, fileName={}", documentId, document.getFileName());
    }

    @Override
    public void retryDocument(Long documentId, Long knowledgeBaseId, Long tenantId) {
        DocumentEntity document = this.getById(documentId);
        if (document == null || !document.getKnowledgeBaseId().equals(knowledgeBaseId)) {
            throw new BizException(ErrorCode.DOC_NOT_FOUND);
        }
        if (!Constants.DocStatus.FAILED.name().equals(document.getStatus())) {
            throw new BizException(ErrorCode.DOC_STATUS_INVALID);
        }

        // 重置状态，重新发送处理消息
        document.setStatus(Constants.DocStatus.PENDING.name());
        document.setParseProgress(0);
        document.setErrorMsg(null);
        this.updateById(document);
        DocumentProcessMessage message = DocumentProcessMessage.builder()
                .documentId(document.getId())
                .knowledgeBaseId(document.getKnowledgeBaseId())
                .fileName(document.getFileName())
                .filePath(document.getFilePath())
                .fileType(document.getFileType())
                .mediaType(document.getMediaType())
                .retryCount(0)
                .build();
        documentProcessProducer.sendParseMessage(message);
        log.info("重试文档处理: id={}", documentId);
    }

    @Override
    public PageResult<DocumentEntity> pageDocument(Long knowledgeBaseId, int current, int size, String status) {
        LambdaQueryWrapper<DocumentEntity> wrapper = new LambdaQueryWrapper<DocumentEntity>()
                .eq(DocumentEntity::getKnowledgeBaseId, knowledgeBaseId)
                .eq(StringUtils.hasText(status), DocumentEntity::getStatus, status)
                .orderByDesc(DocumentEntity::getCreatedAt);

        Page<DocumentEntity> page = this.page(new Page<>(current, size), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public void updateStatus(Long documentId, String status, String errorMsg) {
        this.update(new LambdaUpdateWrapper<DocumentEntity>()
                .eq(DocumentEntity::getId, documentId)
                .set(DocumentEntity::getStatus, status)
                .set(DocumentEntity::getErrorMsg, errorMsg));
    }

    @Override
    public void updateProgress(Long documentId, int progress) {
        this.update(new LambdaUpdateWrapper<DocumentEntity>()
                .eq(DocumentEntity::getId, documentId)
                .set(DocumentEntity::getParseProgress, progress));
    }

    // ==================== 私有方法 ====================

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    private String buildObjectName(Long knowledgeBaseId, String extension) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return String.format("documents/%d/%s/%s.%s", knowledgeBaseId, date, uuid, extension);
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private String determineMediaType(String extension) {
        Set<String> multimediaTypes = Set.of("png", "jpg", "jpeg", "gif", "bmp", "webp",
                "mp3", "wav", "m4a", "flac", "mp4", "avi", "mov", "mkv", "webm");
        return multimediaTypes.contains(extension.toLowerCase()) ? "MULTIMODAL" : "TEXT";
    }
}
