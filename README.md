
# EduSoft 教育平台

EduSoft 是一个基于微服务架构的智能教育平台，集成了前端、后端、AI 服务和自动化部署，支持课程管理、作业练习、智能问答、视频摘要等多种功能，适用于高校和在线教育场景。

## 主要功能
- 课程与班级管理
- 作业与在线练习模块
- 讨论区与点赞互动
- 智能 AI 服务（文档解析、嵌入、RAG、视频摘要等）
- 前后端分离架构，前端基于 Vue3 + TypeScript
- 后端基于 Spring Boot + MyBatis-Plus
- AI 服务基于 Python，支持多种大模型
- 支持 Docker、Kubernetes 部署，CI/CD 自动化

## 目录结构
```
EduSoft/
	├── frontend/         # 前端项目（Vue3）
	├── ai_service/       # AI 微服务（Python）
	├── src/              # 后端主代码（Java）
	├── file/             # 设计文档、接口文档、SQL 脚本等
	├── sql/              # 数据库表结构
	├── .github/workflows # CI/CD 工作流
	└── ...
```

## 快速开始

### 1. 克隆项目
```bash
git clone https://github.com/ssl830/EduSoft_workflow.git
cd EduSoft
```

### 2. 数据库初始化
- 使用 `courseplatform.sql` 脚本初始化 MySQL 数据库
- 默认数据库名：`courseplatform`

### 3. 完成环境配置
- 补充 `application.properties` 文件的以下内容：
```ini
OSS_ACCESS_KEY=你的key
OSS_SECRET_KEY=你的secret
OSS_ENDPOINT=oss-cn-beijing.aliyuncs.com
OSS_BUCKET=edusoft-file
DB_PASSWORD=你的数据库密码
DB_USERNAME=你的数据库用户名
```

在ai_service目录下新建.env文件，并填写以下内容：
```ini
DEEPSEEK_API_KEY=
DASHSCOPE_API_KEY=
QWEN_EMBEDDING_URL=https://dashscope.aliyuncs.com/compatible-mode/v1  # 推荐，默认已是此值
QWEN_EMBEDDING_MODEL=text-embedding-v3
QWEN_EMBEDDING_DIM=1024         # v3支持: 64,128,256,512,768,1024
# QWEN_EMBEDDING_MODEL=text-embedding-v4
# QWEN_EMBEDDING_DIM=1536       # v4支持: 1024,1536,2048,3072,4096

# Storage Configuration
STORAGE_ROOT=storage
VECTOR_DB_PATH=data/vector_db
TEMP_DIR=storage/temp

# Server Configuration
HOST=0.0.0.0
PORT=8000
DEBUG=True

# OpenMP Configuration
KMP_DUPLICATE_LIB_OK=TRUE
```

### 4. 启动后端服务
```bash
cd EduSoft
mvn clean package
java -jar target/*.jar
```

### 5. 启动前端服务
```bash
cd frontend
npm install
npm run dev
```

### 6. 启动 AI 服务
```bash
cd ai_service
python -m venv venv
.\venv\Scripts\activate
pip install -r requirements.txt
uvicorn main:app --host 0.0.0.0 --port 8000 --reload   
```
在运行网站下的/docs可以看到Swapper网站查看接口文档
如：http://127.0.0.1:8000/docs


### 7. Docker & K8s 部署
- 提供 Dockerfile 和 Kubernetes YAML 文件，支持一键部署
- 可参考 `配置说明和部署说明.md` 文档

### 8. CI/CD 自动化
- 已集成 GitHub Actions 自动构建、推送镜像、自动部署到 K8s
- 需配置阿里云镜像仓库和 SSH 密钥

## 文档与支持
- 详细接口文档、部署说明、产品设计文档见 `file/` 目录
- 主要文档：
	- `file/ai-assistant.md`  智能助手说明
	- `file/ai-microservice.md`  AI 微服务说明
	- `file/resource-api.md`  资源接口文档
	- `配置说明和部署说明.md`  部署与环境配置

## 贡献
欢迎提交 issue 和 PR，参与 EduSoft 的开发与优化！

## License
MIT
