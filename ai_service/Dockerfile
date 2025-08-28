# 基于 Python 官方镜像构建 AI 服务
FROM python:3.10-slim
WORKDIR /app
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt
COPY . .
# 如有模型文件或额外依赖，可在此处 COPY
# 启动命令请根据实际服务类型调整
CMD ["python", "main.py"]