package com.qingyi.helper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * QingYiHelper 启动类
 * 企业级智能知识库问答平台 - 多智能体 RAG 架构
 */
@EnableAsync
@SpringBootApplication
public class QingYiHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(QingYiHelperApplication.class, args);
    }
}
