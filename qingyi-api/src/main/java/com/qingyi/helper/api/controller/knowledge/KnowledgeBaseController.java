package com.qingyi.helper.api.controller.knowledge;

import com.qingyi.helper.api.dto.common.PageRequest;
import com.qingyi.helper.api.dto.knowledge.*;
import com.qingyi.helper.auth.model.LoginUser;
import com.qingyi.helper.common.R;
import com.qingyi.helper.common.entity.PageResult;
import com.qingyi.helper.common.util.WebUtil;
import com.qingyi.helper.knowledge.entity.DocumentEntity;
import com.qingyi.helper.knowledge.entity.KnowledgeBaseEntity;
import com.qingyi.helper.knowledge.service.DocumentService;
import com.qingyi.helper.knowledge.service.KnowledgeBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识库管理 Controller
 */
@Tag(name = "知识库管理")
@RestController
@RequestMapping("/v1/knowledge-bases")
@RequiredArgsConstructor
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;
    private final DocumentService documentService;

    @Operation(summary = "创建知识库")
    @PostMapping
    public R<KnowledgeBaseVO> create(@Valid @RequestBody CreateKnowledgeBaseRequest request,
                                     @AuthenticationPrincipal LoginUser loginUser) {
        KnowledgeBaseEntity entity = new KnowledgeBaseEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setIcon(request.getIcon());
        entity.setEmbeddingModel(request.getEmbeddingModel());
        entity.setEmbeddingDimension(request.getEmbeddingDimension());
        entity.setChunkStrategy(request.getChunkStrategy());
        entity.setChunkSize(request.getChunkSize());
        entity.setChunkOverlap(request.getChunkOverlap());
        entity.setTopK(request.getTopK());
        entity.setSimilarityThreshold(request.getSimilarityThreshold());

        KnowledgeBaseEntity created = knowledgeBaseService.createKnowledgeBase(entity, loginUser.getTenantId());
        return R.ok(toVO(created));
    }

    @Operation(summary = "知识库列表")
    @GetMapping
    public R<PageResult<KnowledgeBaseVO>> list(
            @RequestParam(required = false) String keyword,
            @ModelAttribute PageRequest pageRequest,
            @AuthenticationPrincipal LoginUser loginUser) {
        PageResult<KnowledgeBaseEntity> result = knowledgeBaseService.pageKnowledgeBase(
                loginUser.getTenantId(), pageRequest.getCurrent(), pageRequest.getSize(), keyword);
        PageResult<KnowledgeBaseVO> voResult = new PageResult<>(
                result.getRecords().stream().map(this::toVO).collect(Collectors.toList()),
                result.getTotal(), result.getCurrent(), result.getSize());
        return R.ok(voResult);
    }

    @Operation(summary = "知识库详情")
    @GetMapping("/{id}")
    public R<KnowledgeBaseVO> detail(@PathVariable Long id,
                                     @AuthenticationPrincipal LoginUser loginUser) {
        KnowledgeBaseEntity entity = knowledgeBaseService.getById(id);
        if (entity == null || !entity.getTenantId().equals(loginUser.getTenantId())) {
            return R.failed(404, "知识库不存在");
        }
        return R.ok(toVO(entity));
    }

    @Operation(summary = "更新知识库")
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id,
                          @Valid @RequestBody UpdateKnowledgeBaseRequest request,
                          @AuthenticationPrincipal LoginUser loginUser) {
        KnowledgeBaseEntity entity = new KnowledgeBaseEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setIcon(request.getIcon());
        entity.setEmbeddingModel(request.getEmbeddingModel());
        entity.setChunkStrategy(request.getChunkStrategy());
        entity.setChunkSize(request.getChunkSize());
        entity.setChunkOverlap(request.getChunkOverlap());
        entity.setTopK(request.getTopK());
        entity.setSimilarityThreshold(request.getSimilarityThreshold());

        knowledgeBaseService.updateKnowledgeBase(id, entity, loginUser.getTenantId());
        return R.ok();
    }

    @Operation(summary = "删除知识库")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id,
                          @AuthenticationPrincipal LoginUser loginUser) {
        knowledgeBaseService.deleteKnowledgeBase(id, loginUser.getTenantId());
        return R.ok();
    }

    @Operation(summary = "上传文档")
    @PostMapping("/{id}/documents")
    public R<DocumentVO> uploadDocument(@PathVariable Long id,
                                        @RequestParam("file") MultipartFile file,
                                        @AuthenticationPrincipal LoginUser loginUser) {
        DocumentEntity doc = documentService.uploadDocument(id, file, loginUser.getTenantId());
        return R.ok(toDocVO(doc));
    }

    @Operation(summary = "文档列表")
    @GetMapping("/{id}/documents")
    public R<PageResult<DocumentVO>> listDocuments(@PathVariable Long id,
                                                   @RequestParam(required = false) String status,
                                                   @ModelAttribute PageRequest pageRequest) {
        PageResult<DocumentEntity> result = documentService.pageDocument(id, pageRequest.getCurrent(), pageRequest.getSize(), status);
        PageResult<DocumentVO> voResult = new PageResult<>(
                result.getRecords().stream().map(this::toDocVO).collect(Collectors.toList()),
                result.getTotal(), result.getCurrent(), result.getSize());
        return R.ok(voResult);
    }

    @Operation(summary = "删除文档")
    @DeleteMapping("/{id}/documents/{documentId}")
    public R<Void> deleteDocument(@PathVariable Long id,
                                  @PathVariable Long documentId,
                                  @AuthenticationPrincipal LoginUser loginUser) {
        documentService.deleteDocument(documentId, id, loginUser.getTenantId());
        return R.ok();
    }

    @Operation(summary = "重试失败文档")
    @PostMapping("/{id}/documents/{documentId}/retry")
    public R<Void> retryDocument(@PathVariable Long id,
                                 @PathVariable Long documentId,
                                 @AuthenticationPrincipal LoginUser loginUser) {
        documentService.retryDocument(documentId, id, loginUser.getTenantId());
        return R.ok();
    }

    // ==================== 转换方法 ====================

    private KnowledgeBaseVO toVO(KnowledgeBaseEntity entity) {
        return KnowledgeBaseVO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .icon(entity.getIcon())
                .embeddingModel(entity.getEmbeddingModel())
                .embeddingDimension(entity.getEmbeddingDimension())
                .chunkStrategy(entity.getChunkStrategy())
                .chunkSize(entity.getChunkSize())
                .chunkOverlap(entity.getChunkOverlap())
                .topK(entity.getTopK())
                .similarityThreshold(entity.getSimilarityThreshold())
                .documentCount(entity.getDocumentCount())
                .totalChunks(entity.getTotalChunks())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .build();
    }

    private DocumentVO toDocVO(DocumentEntity entity) {
        return DocumentVO.builder()
                .id(entity.getId())
                .fileName(entity.getFileName())
                .fileSize(entity.getFileSize())
                .fileType(entity.getFileType())
                .mediaType(entity.getMediaType())
                .duration(entity.getDuration())
                .chunkCount(entity.getChunkCount())
                .parseProgress(entity.getParseProgress())
                .status(entity.getStatus())
                .errorMsg(entity.getErrorMsg())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .build();
    }
}
