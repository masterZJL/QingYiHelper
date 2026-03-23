package com.qingyi.helper.mq.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingyi.helper.common.BizException;
import com.qingyi.helper.common.Constants;
import com.qingyi.helper.common.ErrorCode;
import com.qingyi.helper.mq.message.DocumentProcessMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 文档处理消息生产者
 * 面试话术：文档上传后通过 RocketMQ 异步触发处理，
 *          使用 Tag 区分文件类型，Key 存 documentId 方便消息追踪
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentProcessProducer {

    private final RocketMQTemplate rocketMQTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 发送文档解析消息
     */
    public void sendParseMessage(DocumentProcessMessage message) {

        try {
            String json = objectMapper.writeValueAsString(message);

            // Tag = 文件类型，Key = documentId
            Message<String> mqMsg = MessageBuilder.withPayload(json)
                    .setHeader("KEYS", message.getDocumentId().toString())
                    .build();

            SendResult result = rocketMQTemplate.syncSend(
                    Constants.DOC_PARSE_TOPIC + ":" + message.getFileType(),
                    mqMsg
            );

            log.info("发送文档解析消息: docId={}, tag={}, result={}",
                    message.getDocumentId(), message.getFileType(), result.getSendStatus());
        } catch (JsonProcessingException e) {
            log.error("消息序列化失败: {}", e.getMessage());
            throw new BizException(ErrorCode.MQ_SEND_FAILED);
        }
    }

    /**
     * 发送延迟重试消息
     * 面试话术：利用 RocketMQ 的延迟消息能力，文档处理失败后延迟 30s 重试
     */
    public void sendDelayRetryMessage(DocumentProcessMessage message, int delayLevel) {
        try {
            String json = objectMapper.writeValueAsString(message);
            // delayLevel 4 = 30s（RocketMQ 延迟级别：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m ...）
            rocketMQTemplate.syncSend(
                    Constants.DOC_PARSE_TOPIC + ":" + message.getFileType(),
                    MessageBuilder.withPayload(json).build(),
                    3000,
                    delayLevel
            );
            log.info("发送延迟重试消息: docId={}, delayLevel={}", message.getDocumentId(), delayLevel);
        } catch (JsonProcessingException e) {
            log.error("延迟消息序列化失败: {}", e.getMessage());
        }
    }
}
