# QingYiHelper - 企业级智能知识库问答平台

> 基于多智能体 RAG 架构的企业级知识库问答平台，支持多模态文档处理

## 🏗️ 技术架构

### 核心架构
- **多智能体 RAG**：意图识别 → Query改写 → 检索 → 生成 → 质检，多 Agent 协作
- **多模态文档处理**：支持 PDF/Word/Markdown 中的图文公式代码，以及音频(ASR)和视频(FFmpeg+ASR+OCR)
- **混合检索**：向量检索(Milvus) + 关键词检索(Elasticsearch) + RRF 融合排序

### 技术栈
| 层次 | 技术 |
|------|------|
| 框架 | Spring Boot 3.2 + Spring AI |
| ORM | MyBatis-Plus |
| 数据库 | MySQL 8.0 |
| 向量数据库 | Milvus |
| 搜索引擎 | Elasticsearch |
| 缓存 | Redis |
| 消息队列 | RocketMQ |
| 文件存储 | MinIO |
| 认证 | Spring Security + JWT |
| API文档 | Knife4j |
| JDK | 21 |

## 📦 模块结构

```
qingyi-helper/
├── qingyi-common/              # 公共模块（工具类、常量、统一响应）
├── qingyi-auth/                # 认证授权（JWT、Spring Security）
├── qingyi-knowledge/           # 知识库核心（CRUD、文档管理）
├── qingyi-docproc/             # 多模态文档处理引擎
│   ├── qingyi-docproc-api/     #   接口定义
│   ├── qingyi-docproc-pdf/     #   PDF增强解析
│   ├── qingyi-docproc-docx/    #   Word解析
│   ├── qingyi-docproc-markdown/#   Markdown解析
│   ├── qingyi-docproc-image/   #   图片OCR
│   ├── qingyi-docproc-audio/   #   音频ASR
│   ├── qingyi-docproc-video/   #   视频处理
│   ├── qingyi-docproc-formula/ #   公式识别
│   ├── qingyi-docproc-chunker/ #   结构感知分块
│   └── qingyi-docproc-embedding/#  多模态向量化
├── qingyi-agent/               # 多智能体框架
│   ├── qingyi-agent-api/       #   Agent接口定义
│   ├── qingyi-agent-orchestrator/#  Agent编排器
│   ├── qingyi-agent-intent/    #   意图识别Agent
│   ├── qingyi-agent-query/     #   Query改写Agent
│   ├── qingyi-agent-retrieval/ #   检索Agent
│   ├── qingyi-agent-generation/#   生成Agent
│   └── qingyi-agent-quality/   #   质检Agent
├── qingyi-rag/                 # RAG引擎（检索、Prompt、LLM）
├── qingyi-chat/                # 对话模块（SSE流式响应）
├── qingyi-admin/               # 后台管理
├── qingyi-mq/                  # RocketMQ消息模块
├── qingyi-api/                 # API层（Controller、DTO）
├── qingyi-start/               # 启动模块
└── sql/                        # 数据库脚本
```

## 🚀 快速开始

### 环境要求
- JDK 21+
- Maven 3.9+
- MySQL 8.0+
- Redis 7.0+
- RocketMQ 5.x
- Milvus 2.x
- Elasticsearch 8.x
- MinIO

### 1. 初始化数据库
```bash
mysql -u root -p < sql/init.sql
```

### 2. 配置环境变量
```bash
export MYSQL_HOST=localhost
export MYSQL_PASSWORD=your_password
export REDIS_HOST=localhost
export ROCKETMQ_NAMESRV=localhost:9876
export OPENAI_API_KEY=your_api_key
```

### 3. 编译运行
```bash
mvn clean install -DskipTests
cd qingyi-start
mvn spring-boot:run
```

### 4. 访问
- API: http://localhost:8080/api
- API文档: http://localhost:8080/api/doc.html

## 📖 项目亮点

### 1. 多智能体协作
不是简单的 RAG 流水线，而是多个 Agent 协作完成问答：
- **意图识别Agent**：判断问题类型，路由到最相关的知识库
- **Query改写Agent**：将口语化问题改写为检索友好的 query
- **检索Agent**：动态选择最优检索策略（向量/关键词/混合）
- **生成Agent**：构建上下文 + LLM 生成回答
- **质检Agent**：评估回答质量，不合格触发重试

### 2. 多模态文档处理
- PDF/Word 中的图文公式代码结构化提取
- 音频 ASR 语音转文字（带时间戳）
- 视频 FFmpeg + ASR + 关键帧 OCR
- 结构感知分块：表格不拆、公式完整、代码不断

### 3. 企业级特性
- 多租户数据隔离
- RocketMQ 异步文档处理管道
- Redis 多级缓存
- 审计日志
- Agent 执行可观测性

## 📄 License

MIT
