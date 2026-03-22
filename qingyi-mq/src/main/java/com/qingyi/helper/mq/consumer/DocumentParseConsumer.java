package com.qingyi.helper.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingyi.helper.common.Constants;
import com.qingyi.helper.mq.message.DocumentProcessMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 文档解析消息消费者
 * 面试话术：消费文档解析消息，根据文件类型路由到对应的解析器。
 *          Phase 2 会实现具体的解析逻辑，这里先搭好消费框架
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = Constants.DOC_PARSE_TOPIC,
        consumerGroup = Constants.DOC_PARSE_GROUP,
        selectorExpression = "*"  // 订阅所有 Tag
)
public class DocumentParseConsumer implements RocketMQListener<String> {

    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(String json) {
        try {
            DocumentProcessMessage message = objectMapper.readValue(json, DocumentProcessMessage.class);
            log.info("收到文档解析消息: docId={}, fileName={}, fileType={}",
                    message.getDocumentId(), message.getFileName(), message.getFileType());

            // TODO: Phase 2 实现具体的文档解析逻辑
            // 1. 根据 fileType 路由到对应解析器
            // 2. 解析完成后发送分块消息到 DOC_CHUNK_TOPIC
            // 3. 异常时发送延迟重试消息

            log.info("文档解析消息处理完成(Phase 2 实现): docId={}", message.getDocumentId());
        } catch (Exception e) {
            log.error("文档解析消息处理失败: {}", e.getMessage(), e);
            throw new RuntimeException("文档解析失败", e);
        }
    }
}
