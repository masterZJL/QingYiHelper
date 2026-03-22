package com.qingyi.helper.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 通用错误 1xxxx
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),
    INTERNAL_ERROR(500, "服务内部错误"),

    // 认证错误 10xxx
    TOKEN_EXPIRED(10001, "Token已过期"),
    TOKEN_INVALID(10002, "Token无效"),
    LOGIN_FAILED(10003, "用户名或密码错误"),
    USER_DISABLED(10004, "用户已被禁用"),
    USER_NOT_FOUND(10005, "用户不存在"),

    // 知识库错误 20xxx
    KB_NOT_FOUND(20001, "知识库不存在"),
    KB_NAME_DUPLICATE(20002, "知识库名称已存在"),
    KB_LIMIT_EXCEEDED(20003, "知识库数量已达上限"),
    KB_DOC_LIMIT_EXCEEDED(20004, "文档数量已达上限"),

    // 文档错误 21xxx
    DOC_NOT_FOUND(21001, "文档不存在"),
    DOC_FILE_EMPTY(21002, "上传文件为空"),
    DOC_FILE_TOO_LARGE(21003, "文件大小超出限制"),
    DOC_FILE_TYPE_UNSUPPORTED(21004, "不支持的文件类型"),
    DOC_PARSE_FAILED(21005, "文档解析失败"),
    DOC_STATUS_INVALID(21006, "文档状态异常"),

    // 对话错误 30xxx
    CONVERSATION_NOT_FOUND(30001, "对话不存在"),
    MESSAGE_EMPTY(30002, "消息内容为空"),
    LLM_CALL_FAILED(30003, "LLM调用失败"),
    RETRIEVAL_NO_RESULT(30004, "未检索到相关内容"),

    // Agent 错误 40xxx
    AGENT_EXECUTION_FAILED(40001, "Agent执行失败"),
    AGENT_RETRY_EXCEEDED(40002, "Agent重试次数超限"),
    AGENT_QUALITY_CHECK_FAILED(40003, "回答质量不达标"),

    // 租户错误 50xxx
    TENANT_NOT_FOUND(50001, "租户不存在"),
    TENANT_DISABLED(50002, "租户已被禁用"),
    TENANT_STORAGE_EXCEEDED(50003, "存储空间不足"),

    // MQ 错误 60xxx
    MQ_SEND_FAILED(60001, "消息发送失败"),
    MQ_CONSUME_FAILED(60002, "消息消费失败");

    private final int code;
    private final String message;
}
