# 企业知识库问答平台 - 技术方案设计

> **项目定位**：面试核心项目，展示 Java + AI 工程化落地能力
> **设计原则**：企业级架构、可扩展、有技术深度、面试能讲清楚每个决策
> **版本**：v2.0（融入多智能体架构 + 增强文档处理引擎）

---

## 一、项目概述

### 1.1 项目名称
**KnowBase** - 企业级智能知识库问答平台

### 1.2 核心功能
- 企业文档管理（上传、解析、分块、索引）
- **多模态文档处理**（图文混排、公式、代码、视频、音频）
- 基于向量检索的智能问答（**多智能体 RAG** 架构）
- 多租户支持（不同企业/部门独立知识库）
- 对话管理（多轮对话、历史记录）
- 知识库权限控制
- 后台管理（用户、知识库、统计）

### 1.3 面试亮点（为什么这个项目能打动面试官）
| 亮点 | 说明 |
|------|------|
| **多智能体 RAG** | 不只是简单 RAG，而是用多 Agent 协作完成意图识别、检索、生成、质检全流程，这是 2025 最前沿的架构 |
| **多模态文档处理** | 支持图文公式代码视频音频，展示对复杂文档场景的工程化解决能力 |
| **RAG 架构** | 2024-2025 最热门的 AI 应用架构，展示对前沿技术的理解 |
| **Java + AI** | 证明你不是只会调 API，而是能把 AI 能力工程化落地 |
| **企业级设计** | 多租户、权限、审计日志、RocketMQ 异步管道，展示架构能力 |
| **完整链路** | 从文档解析到向量存储到检索到生成，全链路打通 |
| **性能优化** | 异步处理、缓存、连接池，展示工程功底 |

---

## 二、技术选型

### 2.1 后端技术栈

| 层次 | 技术 | 选型理由（面试话术） |
|------|------|---------------------|
| **框架** | Spring Boot 3.2 + Spring AI | Spring AI 是 Spring 官方 AI 集成框架，生态成熟，面试官认可度高 |
| **ORM** | MyBatis-Plus | 老大熟悉，企业主流，比 JPA 更灵活可控 |
| **数据库** | MySQL 8.0 | 关系型数据存储，企业标配 |
| **向量数据库** | Milvus（或 PGVector） | Milvus 是国产开源向量数据库，面试加分；PGVector 轻量级备选 |
| **缓存** | Redis | 会话缓存、热点问题缓存、分布式锁 |
| **消息队列** | **RocketMQ** | 阿里开源，Java 生态第一梯队，支持延迟消息、事务消息、顺序消息，比 RabbitMQ 更适合文档处理这种重消息场景 |
| **搜索引擎** | Elasticsearch | 关键词检索 + 向量检索混合搜索 |
| **认证** | Spring Security + JWT | 企业标准方案 |
| **API 文档** | Knife4j (Swagger 增强) | 国产增强版，界面更好 |
| **任务调度** | XXL-JOB | 文档处理、索引重建等定时任务 |
| **文件存储** | MinIO | 兼容 S3 协议的对象存储，企业常用 |
| **监控** | Spring Boot Actuator + Prometheus | 企业级可观测性 |

### 2.2 AI 相关技术

| 组件 | 技术 | 说明 |
|------|------|------|
| **LLM 接入** | Spring AI + OpenAI API / 通义千问 | Spring AI 统一抽象，可切换模型 |
| **Embedding** | 通义千问 Embedding / OpenAI text-embedding-3 | 文本向量化 |
| **多模态 Embedding** | 通义千问 VL / CLIP | 图片向量化，支持图文混合检索 |
| **RAG 框架** | Spring AI + 自研多智能体框架 | 自研 Agent 编排框架，展示架构设计能力 |
| **文档解析** | Apache Tika + Apache PDFBox + docx4j + 自研 | 多解析器组合，结构化提取 |
| **OCR** | PaddleOCR（Java 调用） | 图片中的文字识别 |
| **公式识别** | Mathpix API / LaTeX 解析 | 数学公式识别与 LaTeX 转换 |
| **ASR（语音转文字）** | 阿里云 ASR / FunASR | 音频/视频语音转文字 |
| **视频处理** | FFmpeg + 关键帧提取 | 视频转码、关键帧截图、音频分离 |
| **分块策略** | 语义分块 + 固定长度分块 + 结构感知分块 | 多种分块策略可配置 |

### 2.3 前端技术栈（可选，看精力）

| 技术 | 说明 |
|------|------|
| Vue 3 + TypeScript | 主流前端框架 |
| Element Plus | UI 组件库 |
| Pinia | 状态管理 |
| Markdown 渲染 + KaTeX | 对话内容展示（支持公式渲染） |
| highlight.js | 代码高亮 |

### 2.4 基础设施

| 组件 | 技术 |
|------|------|
| 容器化 | Docker + Docker Compose |
| CI/CD | GitHub Actions（或 Jenkins） |
| 日志 | SLF4J + Logback + ELK |

---

## 三、系统架构

### 3.1 整体架构图

```
┌──────────────────────────────────────────────────────────────────┐
│                          前端 (Vue 3)                            │
│   知识库管理 │ 文档上传 │ 对话界面 │ 后台管理 │ 数据统计          │
└──────────────────────────┬───────────────────────────────────────┘
                           │ HTTP / WebSocket / SSE
┌──────────────────────────▼───────────────────────────────────────┐
│                      API Gateway (Nginx)                         │
└──────────────────────────┬───────────────────────────────────────┘
                           │
┌──────────────────────────▼───────────────────────────────────────┐
│                    Spring Boot 应用层                             │
│                                                                  │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │
│  │ 认证模块  │ │ 知识库模块│ │ 对话模块  │ │ 管理模块  │           │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘           │
│                                                                  │
│  ┌──────────────────────────────────────────────────┐           │
│  │          多智能体 RAG 引擎 (Multi-Agent RAG)       │           │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌────────┐ │           │
│  │  │意图Agent │ │检索Agent │ │生成Agent │ │质检Agent│ │           │
│  │  └─────────┘ └─────────┘ └─────────┘ └────────┘ │           │
│  │  ┌──────────────────────────────────────────┐   │           │
│  │  │         Agent 编排器 (Orchestrator)        │   │           │
│  │  └──────────────────────────────────────────┘   │           │
│  └──────────────────────────────────────────────────┘           │
│                                                                  │
│  ┌──────────────────────────────────────────────────┐           │
│  │          多模态文档处理引擎                         │           │
│  │  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐   │           │
│  │  │PDF解析器│ │Word解析│ │媒体处理│ │OCR引擎 │   │           │
│  │  └────────┘ └────────┘ └────────┘ └────────┘   │           │
│  │  ┌────────┐ ┌────────┐ ┌────────┐              │           │
│  │  │公式识别│ │代码提取│ │ASR引擎 │              │           │
│  │  └────────┘ └────────┘ └────────┘              │           │
│  └──────────────────────────────────────────────────┘           │
└──────────────────────────┬───────────────────────────────────────┘
                           │
┌──────────────────────────▼───────────────────────────────────────┐
│                        基础设施层                                │
│  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐       │
│  │ MySQL  │ │ Redis  │ │Milvus  │ │  ES    │ │ MinIO  │       │
│  └────────┘ └────────┘ └────────┘ └────────┘ └────────┘       │
│  ┌──────────┐ ┌────────┐                                       │
│  │RocketMQ  │ │  LLM   │                                       │
│  └──────────┘ └────────┘                                       │
└──────────────────────────────────────────────────────────────────┘
```

### 3.2 多智能体 RAG 核心流程

这是整个项目**最核心的面试亮点**。不是简单的"检索→生成"流水线，而是多个 Agent 协作完成。

```
用户提问
    │
    ▼
┌─────────────────────────────────────────────────────────┐
│                  Agent 编排器 (Orchestrator)              │
│                                                          │
│  Step 1: ┌──────────────┐                                │
│          │  意图识别 Agent │ ← 判断问题类型、选择知识库     │
│          └──────┬───────┘                                │
│                 │                                         │
│  Step 2: ┌──────▼───────┐                                │
│          │  Query 改写    │ ← LLM 改写问题，提高检索质量    │
│          │  Agent        │                                │
│          └──────┬───────┘                                │
│                 │                                         │
│  Step 3: ┌──────▼───────┐                                │
│          │  检索 Agent    │ ← 路由到不同检索策略            │
│          │              │   向量检索 / 关键词检索 / 混合    │
│          └──────┬───────┘                                │
│                 │                                         │
│  Step 4: ┌──────▼───────┐                                │
│          │  重排序 Agent  │ ← Reranker 精排（可选）        │
│          └──────┬───────┘                                │
│                 │                                         │
│  Step 5: ┌──────▼───────┐                                │
│          │  生成 Agent    │ ← 构建上下文 + LLM 生成回答     │
│          └──────┬───────┘                                │
│                 │                                         │
│  Step 6: ┌──────▼───────┐                                │
│          │  质检 Agent    │ ← 检查回答质量、引用准确性       │
│          │              │   不合格则触发重试/补充检索       │
│          └──────┬───────┘                                │
│                 │                                         │
└─────────────────┼─────────────────────────────────────────┘
                  │
                  ▼
            返回最终回答（含引用来源）
```

#### 多智能体设计细节

```java
// ==================== Agent 基础抽象 ====================

/**
 * Agent 基础接口 - 所有 Agent 的统一抽象
 * 面试话术：借鉴了 AutoGen/CrewAI 的思想，但用 Java 自研实现
 */
public interface Agent {
    /** Agent 名称 */
    String getName();
    /** Agent 职责描述（用于 LLM System Prompt） */
    String getDescription();
    /** 执行任务 */
    AgentResult execute(AgentContext context);
}

/**
 * Agent 上下文 - Agent 之间通过 Context 传递信息
 * 面试话术：类似 Chain of Thought，每个 Agent 的输出作为下一个 Agent 的输入
 */
public class AgentContext {
    private String userQuery;           // 用户原始问题
    private String rewrittenQuery;      // 改写后的问题
    private List<RetrievalResult> retrievalResults;  // 检索结果
    private String generatedAnswer;     // 生成的回答
    private List<String> sourceReferences;  // 引用来源
    private Map<String, Object> metadata;  // 元数据（Agent 间传递）
    // ...
}

// ==================== 各 Agent 实现 ====================

/**
 * 意图识别 Agent
 * 职责：判断用户意图（知识库问答 / 闲聊 / 多知识库路由）
 */
public class IntentRecognitionAgent implements Agent {
    // 用 LLM 做意图分类，输出结构化 JSON
    // 支持多知识库场景下自动路由到最相关的知识库
}

/**
 * Query 改写 Agent
 * 职责：将用户口语化问题改写为更适合检索的 query
 * 面试话术：用户问"那个报销流程怎么走"，改写为"企业员工费用报销流程及审批规则"
 */
public class QueryRewriteAgent implements Agent {
    // 支持：同义改写、拆分子问题、补充上下文（结合对话历史）
}

/**
 * 检索 Agent
 * 职责：根据问题类型选择最优检索策略并执行
 * 面试话术：这是策略模式 + Agent 的结合，Agent 负责"决策"，策略负责"执行"
 */
public class RetrievalAgent implements Agent {
    private Map<String, RetrievalStrategy> strategies;  // 注入多种检索策略
    
    // Agent 内部逻辑：根据问题特征选择策略
    // - 包含专有名词/编号 → 优先关键词检索
    // - 概念性问题 → 优先向量检索
    // - 复杂问题 → 混合检索
}

/**
 * 生成 Agent
 * 职责：基于检索结果构建 Prompt 并调用 LLM 生成回答
 */
public class GenerationAgent implements Agent {
    // Prompt 模板管理
    // 支持引用来源标注
    // 支持流式输出（SSE）
}

/**
 * 质检 Agent
 * 职责：评估生成回答的质量
 * 面试话术：这是"自我反思"机制，回答不合格会触发重试，
 *          体现了对输出质量的工程化保障
 */
public class QualityCheckAgent implements Agent {
    // 检查维度：
    // 1. 回答是否回答了用户问题（相关性）
    // 2. 引用来源是否准确（事实性）
    // 3. 是否包含"我不知道"的兜底（安全性）
    // 质检不通过 → 触发补充检索 → 重新生成（最多重试 N 次）
}

// ==================== Agent 编排器 ====================

/**
 * Agent 编排器 - 控制多 Agent 的执行流程
 * 面试话术：借鉴了工作流引擎的思想，支持顺序执行、条件分支、循环重试
 */
public class AgentOrchestrator {
    private List<Agent> pipeline;       // Agent 管道
    private int maxRetryCount;          // 最大重试次数
    
    public RagResponse execute(String userQuery, AgentContext context) {
        for (Agent agent : pipeline) {
            AgentResult result = agent.execute(context);
            if (result.needsRetry() && retryCount < maxRetryCount) {
                // 质检不通过，回退到检索步骤重试
                retryFrom(context, result.getRetryFromStep());
            }
            context.merge(result);  // 合并结果到上下文
        }
        return buildResponse(context);
    }
}
```

### 3.3 多模态文档处理流程

这是项目的**第二大面试亮点**——不只是处理纯文本，而是能处理包含图片、公式、代码、视频、音频的复杂文档。

```
文档上传
    │
    ▼
┌─────────────────────────────────────────────────────────┐
│              文件类型路由器 (FileRouter)                   │
│                                                          │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │
│  │ PDF/Word │ │  图片    │ │  音频    │ │  视频    │   │
│  │ 文档处理  │ │  处理    │ │  处理    │ │  处理    │   │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘   │
└───────┼────────────┼────────────┼────────────┼──────────┘
        │            │            │            │
        ▼            ▼            ▼            ▼
┌─────────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐
│ 文档结构解析 │ │  OCR     │ │   ASR    │ │  视频处理     │
│             │ │ 文字识别  │ │ 语音转文字│ │              │
│ • 文本提取  │ │          │ │          │ │ • FFmpeg抽帧  │
│ • 图片提取  │ │          │ │          │ │ • 音频分离    │
│ • 表格提取  │ │          │ │          │ │ • 关键帧截图  │
│ • 公式识别  │ │          │ │          │ │ • ASR转文字   │
│ • 代码块提取│ │          │ │          │ │ • OCR帧文字   │
└──────┬──────┘ └────┬─────┘ └────┬─────┘ └──────┬───────┘
       │             │            │               │
       └─────────────┴────────────┴───────────────┘
                           │
                           ▼
              ┌────────────────────────┐
              │   统一内容模型转换       │
              │                        │
              │  将所有模态内容转换为    │
              │  统一的 DocumentNode    │
              │  结构化表示             │
              └────────────┬───────────┘
                           │
                           ▼
              ┌────────────────────────┐
              │   结构感知分块          │
              │                        │
              │  • 按标题层级分块       │
              │  • 保持表格完整性       │
              │  • 公式作为独立块       │
              │  • 代码块不拆分         │
              │  • 图片+描述作为一组    │
              └────────────┬───────────┘
                           │
                           ▼
              ┌────────────────────────┐
              │   多模态向量化          │
              │                        │
              │  • 文本 → Text Embed   │
              │  • 图片 → Image Embed  │
              │  • 混合 → 拼接向量     │
              └────────────┬───────────┘
                           │
                           ▼
              ┌────────────────────────┐
              │   双写索引              │
              │                        │
              │  Milvus + ES + MySQL   │
              └────────────────────────┘
```

#### 多模态文档处理引擎核心设计

```java
// ==================== 统一内容模型 ====================

/**
 * 文档节点 - 统一表示文档中的各种内容元素
 * 面试话术：这是整个文档处理引擎的核心数据结构，
 *          不管原始文档是 PDF、Word 还是视频，
 *          最终都转换为 DocumentNode 树结构
 */
public class DocumentNode {
    private String id;
    private NodeType type;  // TEXT / IMAGE / TABLE / FORMULA / CODE / HEADING / PARAGRAPH
    private String content;
    private Map<String, Object> attributes;  // 类型特有属性
    
    // 图片特有属性
    // attributes: { "imageUrl": "...", "ocrText": "...", "description": "..." }
    
    // 公式特有属性
    // attributes: { "latex": "E=mc^2", "plainText": "能量等于质量乘以光速的平方" }
    
    // 代码特有属性
    // attributes: { "language": "java", "code": "public class..." }
    
    // 表格特有属性
    // attributes: { "headers": [...], "rows": [[...]], "markdown": "|...|" }
    
    private List<DocumentNode> children;  // 子节点（树结构）
    private int level;  // 层级（用于标题层级）
}

// ==================== 文档解析器 ====================

/**
 * 文档解析器接口 - 策略模式
 */
public interface DocumentParser {
    /** 支持的文件类型 */
    Set<String> supportedTypes();
    /** 解析文档为 DocumentNode 树 */
    DocumentParseResult parse(InputStream input, ParseConfig config);
}

/**
 * PDF 增强解析器 - 核心解析器
 * 面试话术：Tika 只能提取纯文本，丢失了结构信息。
 *          我用 PDFBox 做底层操作，自研了版面分析逻辑，
 *          能识别标题层级、表格、图片位置、公式区域
 */
public class EnhancedPdfParser implements DocumentParser {
    // 1. PDFBox 提取页面元素（文本块、图片、表格）
    // 2. 版面分析：判断元素类型（标题/段落/表格/公式/代码）
    // 3. 图片区域 → 调用 OCR 提取文字
    // 4. 公式区域 → 调用公式识别 API 转换为 LaTeX
    // 5. 代码区域 → 识别语言、保留格式
    // 6. 构建 DocumentNode 树
}

/**
 * Word 解析器
 */
public class DocxParser implements DocumentParser {
    // docx4j 解析 OOXML
    // 提取段落、表格、图片、公式（Word 内嵌公式）
    // 保留文档结构层级
}

/**
 * Markdown 解析器
 */
public class MarkdownParser implements DocumentParser {
    // 解析 Markdown 语法
    // 识别代码块（```）、公式（$...$）、表格、图片
    // 天然结构化，直接转 DocumentNode
}

// ==================== 媒体处理 ====================

/**
 * 音频处理器
 * 面试话术：音频先转文字（ASR），再按时间戳分块，
 *          每个分块关联时间戳，回答时可定位到具体时间点
 */
public class AudioProcessor {
    // 1. 音频格式转换（FFmpeg）
    // 2. 调用 ASR 服务（阿里云 ASR / FunASR）
    // 3. 获取带时间戳的文字稿
    // 4. 按语义段落分块
    // 5. 每个分块记录 { startTime, endTime, text }
}

/**
 * 视频处理器
 * 面试话术：视频是最复杂的模态，需要多步处理：
 *          先用 FFmpeg 分离音轨，ASR 转文字；
 *          再按场景切换提取关键帧，OCR 识别帧内文字；
 *          最后将语音文字和帧文字融合
 */
public class VideoProcessor {
    // 1. FFmpeg 分离音轨 → ASR 转文字（带时间戳）
    // 2. 场景检测 → 提取关键帧截图
    // 3. 关键帧 → OCR 识别文字
    // 4. 融合：语音文字 + 帧内文字 + 时间戳
    // 5. 构建带时间线的 DocumentNode
}

// ==================== 结构感知分块 ====================

/**
 * 结构感知分块器
 * 面试话术：普通分块（固定 512 token 一刀切）会破坏表格、
 *          拆开公式、截断代码。结构感知分块基于 DocumentNode
 *          的语义结构来切分，保证每个块的内容完整性
 */
public class StructureAwareChunker implements ChunkStrategy {
    public List<DocumentChunk> chunk(List<DocumentNode> nodes, ChunkConfig config) {
        // 规则：
        // 1. 表格 → 整体作为一个块（转为 Markdown 格式）
        // 2. 公式 → 独立成块，附带 LaTeX + 自然语言描述
        // 3. 代码块 → 整体作为一个块，附带语言类型
        // 4. 图片 → 独立成块，附带 OCR 文字 + 描述
        // 5. 文本段落 → 按标题层级分组，组内按 token 限制切分
        // 6. 每个块附加元数据：来源文件、页码、章节标题、块类型
    }
}
```

### 3.4 RocketMQ 异步处理管道

```
文档上传成功
    │
    ▼
┌─────────────────────────────────────────────────────────┐
│                  RocketMQ 消息流                         │
│                                                          │
│  Topic: DOC_PARSE_TOPIC                                  │
│  ┌──────────────────────────────────────────────┐        │
│  │  Producer → 发送文档解析消息                   │        │
│  │  Tag: PDF / DOCX / AUDIO / VIDEO / IMAGE     │        │
│  │  Key: documentId                             │        │
│  └──────────────────┬───────────────────────────┘        │
│                     │                                    │
│  ┌──────────────────▼───────────────────────────┐        │
│  │  Consumer Group: DOC_PARSE_GROUP              │        │
│  │  消费消息 → 路由到对应解析器 → 解析完成        │        │
│  └──────────────────┬───────────────────────────┘        │
│                     │                                    │
│  Topic: DOC_CHUNK_TOPIC                                 │
│  ┌──────────────────▼───────────────────────────┐        │
│  │  Consumer Group: DOC_CHUNK_GROUP              │        │
│  │  消费消息 → 结构感知分块 → 完成                │        │
│  └──────────────────┬───────────────────────────┘        │
│                     │                                    │
│  Topic: DOC_EMBED_TOPIC                                 │
│  ┌──────────────────▼───────────────────────────┐        │
│  │  Consumer Group: DOC_EMBED_GROUP              │        │
│  │  消费消息 → 批量向量化 → 写入 Milvus + ES     │        │
│  └──────────────────────────────────────────────┘        │
│                                                          │
│  ★ 延迟消息：文档上传后 30s 检查解析状态，失败则重试      │
│  ★ 顺序消息：同一文档的分块按顺序处理                      │
│  ★ 事务消息：文档状态更新与消息发送的一致性保障            │
└─────────────────────────────────────────────────────────┘
```

#### RocketMQ 选型面试话术

> 为什么选 RocketMQ 而不是 RabbitMQ/Kafka？
> 
> 1. **Java 原生**：RocketMQ 是阿里用 Java 写的，和我们的 Spring Boot 技术栈天然契合，排查问题方便
> 2. **延迟消息**：文档处理失败后需要延迟重试，RocketMQ 原生支持 18 个延迟级别，RabbitMQ 需要装插件
> 3. **顺序消息**：同一文档的分块需要按顺序向量化，RocketMQ 的顺序消息保证消费顺序
> 4. **事务消息**：文档状态更新（MySQL）和消息发送需要一致性，RocketMQ 的事务消息能保证
> 5. **消息回溯**：出问题时可以重新消费历史消息，方便排查和恢复

---

## 四、数据库设计

### 4.1 核心表结构

```sql
-- 租户表
CREATE TABLE tenant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '租户名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '租户编码',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    max_knowledge_bases INT DEFAULT 5 COMMENT '最大知识库数量',
    max_documents INT DEFAULT 100 COMMENT '最大文档数量',
    max_storage_mb BIGINT DEFAULT 1024 COMMENT '最大存储空间(MB)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL COMMENT '所属租户',
    username VARCHAR(50) NOT NULL,
    password VARCHAR(200) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    avatar VARCHAR(500),
    role VARCHAR(20) NOT NULL COMMENT 'ADMIN/MEMBER/VISITOR',
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant (tenant_id),
    UNIQUE KEY uk_tenant_username (tenant_id, username)
);

-- 知识库表
CREATE TABLE knowledge_base (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(200),
    embedding_model VARCHAR(50) DEFAULT 'text-embedding-v2' COMMENT '向量模型',
    embedding_dimension INT DEFAULT 1536 COMMENT '向量维度',
    chunk_strategy VARCHAR(30) DEFAULT 'STRUCTURE_AWARE' COMMENT '分块策略',
    chunk_size INT DEFAULT 512 COMMENT '分块大小',
    chunk_overlap INT DEFAULT 50 COMMENT '分块重叠',
    top_k INT DEFAULT 5 COMMENT '检索返回数量',
    similarity_threshold FLOAT DEFAULT 0.7 COMMENT '相似度阈值',
    status TINYINT DEFAULT 1,
    document_count INT DEFAULT 0,
    total_chunks INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant (tenant_id)
);

-- 文档表（增强版，支持多模态）
CREATE TABLE document (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    knowledge_base_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) COMMENT 'MinIO 路径',
    file_size BIGINT COMMENT '文件大小(bytes)',
    file_type VARCHAR(20) NOT NULL COMMENT 'PDF/DOCX/TXT/MD/IMAGE/AUDIO/VIDEO',
    media_type VARCHAR(20) COMMENT 'TEXT/MULTIMODAL 模态类型',
    duration INT COMMENT '音视频时长(秒)',
    chunk_count INT DEFAULT 0 COMMENT '分块数量',
    parse_progress INT DEFAULT 0 COMMENT '解析进度(0-100)',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING/PARSING/CHUNKING/EMBEDDING/INDEXED/FAILED',
    error_msg TEXT COMMENT '处理失败原因',
    metadata JSON COMMENT '文档元数据(页数/作者/创建时间等)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_kb (knowledge_base_id),
    INDEX idx_status (status)
);

-- 文档分块表（增强版，支持多模态内容）
CREATE TABLE document_chunk (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_id BIGINT NOT NULL,
    knowledge_base_id BIGINT NOT NULL,
    content TEXT NOT NULL COMMENT '分块文本内容',
    content_type VARCHAR(20) DEFAULT 'TEXT' COMMENT 'TEXT/IMAGE/TABLE/FORMULA/CODE/MIXED',
    chunk_index INT NOT NULL COMMENT '分块序号',
    token_count INT COMMENT 'token 数量',
    metadata JSON COMMENT '元数据',
    -- metadata 示例：
    -- 文本块: {"page": 3, "heading": "第三章", "level": 2}
    -- 图片块: {"imageUrl": "...", "ocrText": "...", "description": "..."}
    -- 公式块: {"latex": "E=mc^2", "plainText": "..."}
    -- 代码块: {"language": "java", "code": "..."}
    -- 表格块: {"markdown": "|...|", "rowCount": 10}
    -- 音频块: {"startTime": "00:01:23", "endTime": "00:02:45"}
    -- 视频块: {"timestamp": "00:05:30", "frameUrl": "...", "ocrText": "..."}
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_document (document_id),
    INDEX idx_kb (knowledge_base_id)
);

-- 对话表
CREATE TABLE conversation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    knowledge_base_id BIGINT COMMENT '关联知识库(可选)',
    title VARCHAR(200),
    agent_trace JSON COMMENT 'Agent 执行轨迹（调试/审计用）',
    message_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id)
);

-- 对话消息表
CREATE TABLE conversation_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    conversation_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL COMMENT 'USER/ASSISTANT/SYSTEM',
    content TEXT NOT NULL,
    sources JSON COMMENT '引用来源',
    agent_steps JSON COMMENT 'Agent 执行步骤记录',
    token_count INT COMMENT '消耗 token 数',
    latency_ms INT COMMENT '响应耗时(ms)',
    model VARCHAR(50) COMMENT '使用的模型',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_conversation (conversation_id)
);

-- Agent 执行日志（面试亮点：可观测性）
CREATE TABLE agent_execution_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    conversation_id BIGINT,
    message_id BIGINT,
    agent_name VARCHAR(50) NOT NULL COMMENT 'Agent 名称',
    step_order INT NOT NULL COMMENT '执行顺序',
    input_text TEXT COMMENT '输入内容',
    output_text TEXT COMMENT '输出内容',
    token_count INT,
    latency_ms INT COMMENT '执行耗时',
    status VARCHAR(20) NOT NULL COMMENT 'SUCCESS/FAILED/RETRY',
    retry_count INT DEFAULT 0,
    error_msg TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_message (message_id),
    INDEX idx_agent (agent_name)
);

-- 操作审计日志
CREATE TABLE audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT,
    user_id BIGINT,
    operation VARCHAR(50) NOT NULL COMMENT '操作类型',
    resource_type VARCHAR(50) COMMENT '资源类型',
    resource_id BIGINT COMMENT '资源ID',
    detail TEXT COMMENT '操作详情',
    ip VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tenant (tenant_id),
    INDEX idx_user (user_id),
    INDEX idx_time (created_at)
);
```

---

## 五、模块划分

### 5.1 Maven 多模块结构

```
knowbase/
├── knowbase-common/              # 公共模块（工具类、常量、基础实体、统一响应）
├── knowbase-auth/                # 认证授权模块（JWT、Spring Security、多租户拦截器）
│
├── knowbase-knowledge/           # 知识库核心模块
│   ├── 知识库 CRUD
│   ├── 文档管理（上传、状态跟踪）
│   └── 向量索引管理
│
├── knowbase-docproc/             # ★ 多模态文档处理引擎（核心亮点模块）
│   ├── docproc-api/              #   解析器接口定义
│   ├── docproc-pdf/              #   PDF 增强解析器（PDFBox + 版面分析）
│   ├── docproc-docx/             #   Word 解析器（docx4j）
│   ├── docproc-markdown/         #   Markdown 解析器
│   ├── docproc-image/            #   图片处理（OCR）
│   ├── docproc-audio/            #   音频处理（ASR）
│   ├── docproc-video/            #   视频处理（FFmpeg + ASR + OCR）
│   ├── docproc-formula/          #   公式识别（LaTeX）
│   ├── docproc-chunker/          #   结构感知分块器
│   └── docproc-embedding/        #   多模态向量化
│
├── knowbase-agent/               # ★ 多智能体框架（核心亮点模块）
│   ├── agent-api/                #   Agent 接口定义
│   ├── agent-orchestrator/       #   Agent 编排器
│   ├── agent-intent/             #   意图识别 Agent
│   ├── agent-query/              #   Query 改写 Agent
│   ├── agent-retrieval/          #   检索 Agent
│   ├── agent-generation/         #   生成 Agent
│   └── agent-quality/            #   质检 Agent
│
├── knowbase-rag/                 # RAG 引擎模块
│   ├── Embedding 服务
│   ├── 检索服务（向量+关键词混合）
│   ├── 重排序
│   ├── Prompt 模板管理
│   └── RAG Pipeline（整合 Agent + 检索 + 生成）
│
├── knowbase-chat/                # 对话模块
│   ├── 对话管理
│   ├── 流式响应（SSE）
│   ├── 历史记录
│   └── Agent 执行轨迹记录
│
├── knowbase-admin/               # 后台管理模块
│   ├── 用户管理
│   ├── 租户管理
│   ├── 统计报表
│   ├── Agent 监控面板
│   └── 系统配置
│
├── knowbase-mq/                  # RocketMQ 消息模块
│   ├── 文档解析消息
│   ├── 文档分块消息
│   ├── 文档向量化消息
│   └── 延迟重试消息
│
├── knowbase-api/                 # API 层（Controller、DTO、参数校验）
└── knowbase-start/               # 启动模块（配置、启动类、Dockerfile）
```

---

## 六、核心 API 设计

### 6.1 知识库管理

```
POST   /api/v1/knowledge-bases                    # 创建知识库
GET    /api/v1/knowledge-bases                    # 知识库列表
GET    /api/v1/knowledge-bases/{id}               # 知识库详情
PUT    /api/v1/knowledge-bases/{id}               # 更新知识库
DELETE /api/v1/knowledge-bases/{id}               # 删除知识库

POST   /api/v1/knowledge-bases/{id}/documents           # 上传文档（支持多文件）
GET    /api/v1/knowledge-bases/{id}/documents           # 文档列表
GET    /api/v1/knowledge-bases/{id}/documents/{did}     # 文档详情（含解析进度）
DELETE /api/v1/knowledge-bases/{id}/documents/{did}     # 删除文档
POST   /api/v1/knowledge-bases/{id}/documents/{did}/retry # 重试失败文档
POST   /api/v1/knowledge-bases/{id}/reindex             # 重建索引
```

### 6.2 智能问答

```
POST   /api/v1/chat/completions                     # 发送消息（SSE 流式）
POST   /api/v1/chat/completions/sync                # 发送消息（同步返回）
GET    /api/v1/chat/conversations                   # 对话列表
GET    /api/v1/chat/conversations/{id}              # 对话详情（历史消息）
GET    /api/v1/chat/conversations/{id}/trace        # Agent 执行轨迹
DELETE /api/v1/chat/conversations/{id}              # 删除对话
```

### 6.3 系统管理

```
POST   /api/v1/auth/login                           # 登录
POST   /api/v1/auth/refresh                         # 刷新 Token
GET    /api/v1/users                                # 用户列表
POST   /api/v1/users                                # 创建用户
GET    /api/v1/admin/statistics                     # 统计数据
GET    /api/v1/admin/audit-logs                     # 审计日志
GET    /api/v1/admin/agent-metrics                  # Agent 执行指标
```

---

## 七、开发路线图（分 6 个阶段）

### Phase 1：基础骨架（第 1-2 周）
- [ ] 项目初始化（Maven 多模块、Spring Boot 3.2）
- [ ] 数据库设计与建表
- [ ] 认证模块（JWT + Spring Security）
- [ ] 知识库 CRUD
- [ ] 文档上传（MinIO 集成）
- [ ] 基础 API 框架（统一响应、全局异常处理、参数校验）
- [ ] RocketMQ 基础集成

**里程碑**：能登录、能创建知识库、能上传文档

### Phase 2：文档处理引擎 - 文本篇（第 3-4 周）
- [ ] 统一内容模型（DocumentNode）
- [ ] PDF 增强解析器（PDFBox + 版面分析）
- [ ] Word 解析器（docx4j）
- [ ] Markdown 解析器
- [ ] 公式识别（LaTeX 转换）
- [ ] 代码块提取与识别
- [ ] 结构感知分块器
- [ ] RocketMQ 异步处理管道（解析 → 分块 → 向量化）
- [ ] 文档处理进度跟踪

**里程碑**：上传 PDF/Word/MD 文档后自动解析、结构化分块、向量化入库

### Phase 3：文档处理引擎 - 多模态篇（第 5-6 周）
- [ ] 图片 OCR 处理（PaddleOCR）
- [ ] 音频处理（ASR 语音转文字 + 时间戳）
- [ ] 视频处理（FFmpeg + ASR + 关键帧 OCR）
- [ ] 多模态向量化（文本 Embedding + 图片 Embedding）
- [ ] 图片/音频/视频的独立上传与处理

**里程碑**：支持图片、音频、视频的上传和知识化处理

### Phase 4：多智能体 RAG 引擎（第 7-8 周）
- [ ] Agent 基础框架（接口、上下文、编排器）
- [ ] 意图识别 Agent
- [ ] Query 改写 Agent
- [ ] 检索 Agent（向量 + ES 混合检索）
- [ ] 生成 Agent（Prompt 模板 + LLM 调用）
- [ ] 质检 Agent（回答质量评估 + 重试机制）
- [ ] SSE 流式响应
- [ ] 引用来源标注
- [ ] Agent 执行日志记录

**里程碑**：多智能体协作完成知识库问答，带质检和重试

### Phase 5：对话与企业级特性（第 9-10 周）
- [ ] 多轮对话管理
- [ ] 对话历史存储与查询
- [ ] 热点问题缓存（Redis）
- [ ] 多租户数据隔离
- [ ] 权限控制（知识库级别）
- [ ] 审计日志
- [ ] 统计报表 + Agent 监控面板
- [ ] 前端界面（Vue 3，如精力允许）

**里程碑**：完整的多轮对话体验 + 企业级管理功能

### Phase 6：部署与打磨（第 11-12 周）
- [ ] Docker Compose 一键部署（全部组件）
- [ ] 性能优化（连接池、缓存策略、批量向量化）
- [ ] 单元测试 + 集成测试
- [ ] README + 部署文档 + 架构文档
- [ ] Demo 数据准备（示例知识库）
- [ ] 代码规范与注释完善

**里程碑**：企业级完整系统，可部署演示，文档齐全

---

## 八、面试讲解要点

### 8.1 项目介绍模板（30秒版）

> 我设计并实现了一个企业级知识库问答平台，采用**多智能体 RAG** 架构。
> 
> 系统包含多个协作 Agent：意图识别、Query 改写、检索、生成、质检，
> 通过自研的 Agent 编排器控制执行流程，质检不通过会自动重试。
> 
> 文档处理引擎支持**多模态**：不仅能解析 PDF/Word 中的图文公式代码，
> 还能处理音频（ASR 转文字）和视频（关键帧提取 + 语音识别），
> 通过结构感知分块保证内容完整性。
> 
> 后端基于 Spring Boot 3.2 + Spring AI，RocketMQ 做异步文档处理管道，
> Milvus + ES 混合检索，支持多租户隔离和权限控制。

### 8.2 高频面试问题准备

| 问题 | 回答要点 |
|------|---------|
| **为什么用多智能体？** | 单一 RAG 流水线是"一条路走到黑"，多 Agent 可以在每一步做决策和质检。比如质检 Agent 发现回答不合格，可以触发补充检索重新生成。这比简单 RAG 的鲁棒性强很多 |
| **Agent 之间怎么通信？** | 通过 AgentContext 对象传递，每个 Agent 的输出写入 Context，下一个 Agent 从 Context 读取输入。类似 Chain of Thought 的思想 |
| **Agent 编排器怎么设计的？** | 支持顺序执行和条件分支。正常流程顺序执行，质检不通过时回退到指定步骤重试。用责任链模式实现，每个 Agent 决定是否继续传递 |
| **为什么选 RocketMQ？** | 三个关键能力：延迟消息（文档处理失败延迟重试）、顺序消息（同一文档分块按序处理）、事务消息（状态更新和消息发送一致性）。而且 Java 原生，排查方便 |
| **多模态文档怎么处理的？** | 核心是"统一内容模型"——所有模态最终都转换为 DocumentNode 树结构。PDF 用 PDFBox 做版面分析，图片用 OCR，音频用 ASR，视频用 FFmpeg+ASR+OCR。然后结构感知分块器基于语义结构切分，保证表格不拆、公式完整、代码不断 |
| **结构感知分块和普通分块有什么区别？** | 普通分块是 512 token 一刀切，会破坏表格、截断代码。结构感知分块基于 DocumentNode 的类型来决定：表格整体一块、公式独立一块、代码不拆、文本按标题层级分组后再切。这样检索到的每个块都是语义完整的 |
| **视频怎么向量化？** | 视频先转成多模态 DocumentNode：语音转文字（ASR）+ 关键帧截图（OCR）+ 时间戳。向量化时，文字部分用 Text Embedding，关键帧图片用 Image Embedding，拼接成混合向量 |
| **混合检索怎么做的？** | 向量检索（Milvus）擅长语义匹配，关键词检索（ES）擅长精确匹配。两路并行检索后用 RRF（Reciprocal Rank Fusion）算法融合排序。检索 Agent 根据问题特征动态选择策略 |
| **怎么保证多租户隔离？** | 三层隔离：MySQL 用 tenant_id 字段 + MyBatis-Plus 拦截器自动注入；Milvus 用 Partition 隔离；Redis 用 Key 前缀隔离 |
| **质检 Agent 具体检查什么？** | 三个维度：相关性（是否回答了用户问题）、事实性（引用来源是否准确）、安全性（不确定时是否说了"我不知道"）。不合格则回退到检索步骤补充检索，最多重试 2 次 |

### 8.3 技术深度展示点

1. **设计模式**：
   - 策略模式（检索策略、分块策略、文档解析器）
   - 工厂模式（根据文件类型创建解析器）
   - 责任链模式（Agent 管道）
   - 观察者模式（文档处理事件通知）
   - 模板方法模式（Agent 基类定义执行框架）

2. **并发处理**：
   - RocketMQ 异步文档处理
   - CompletableFuture 并行检索（向量 + 关键词同时查）
   - Redis 分布式锁（防止重复处理）
   - SSE 流式响应（不阻塞等待完整生成）

3. **性能优化**：
   - 多级缓存（热点问题 → Redis → 检索 → 生成）
   - 批量向量化（攒批调用 Embedding API）
   - 连接池调优（MySQL、Redis、Milvus）
   - 文档处理进度实时推送

4. **可扩展性**：
   - 新增 Agent 只需实现 Agent 接口并注册到编排器
   - 新增文件类型只需实现 DocumentParser 接口
   - 新增检索策略只需实现 RetrievalStrategy 接口
   - LLM 模型通过 Spring AI 抽象可随时切换

---

## 九、风险与应对

| 风险 | 应对方案 |
|------|---------|
| LLM API 调用成本 | 缓存高频问题、使用国产模型（通义千问更便宜）、质检 Agent 减少无效生成 |
| 向量数据库部署复杂 | 提供 Docker Compose 一键部署脚本 |
| 文档解析质量差 | 多解析器兜底 + 结构感知分块 + 人工校验入口 |
| 检索准确率不够 | 混合检索 + Query 改写 + 重排序 + 质检重试 四重保障 |
| 多模态处理复杂度高 | 分阶段实现：先文本 → 再图片/公式 → 最后音视频，每阶段都可独立验收 |
| 视频处理耗时长 | FFmpeg 异步处理 + 进度推送 + 超时控制 |
| 时间不够做前端 | 优先保证后端完整性，前端用 Swagger/Postman 演示也行 |

---

## 十、下一步行动

1. **确认开发环境**：JDK 版本（建议 17+）、IDE、是否有 Docker 环境
2. **确认外部服务**：LLM API Key（通义千问/OpenAI）、ASR 服务（阿里云/FunASR）
3. **开始 Phase 1**：确认后直接搭建项目骨架

---

*设计文档版本：v2.0*
*更新时间：2026-03-22*
*更新内容：融入多智能体架构、RocketMQ、多模态文档处理引擎*
*作者：清一*
