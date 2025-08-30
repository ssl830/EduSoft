"""
Embedding服务，使用Qwen text-embedding-v4 API生成文本向量
"""
import os
from typing import List
import numpy as np
from openai import OpenAI
from dotenv import load_dotenv
import logging

load_dotenv()

logger = logging.getLogger("embedding")

class EmbeddingService:
    """文本向量生成服务（Qwen/通义千问 OpenAI 兼容接口）"""

    def __init__(self):
        """初始化OpenAI客户端"""
        self.api_key = os.getenv("DASHSCOPE_API_KEY")
        self.base_url = os.getenv("QWEN_EMBEDDING_URL", "https://dashscope.aliyuncs.com/compatible-mode/v1")
        self.model = os.getenv("QWEN_EMBEDDING_MODEL", "text-embedding-v4")
        logger.info(self.api_key)
        # 根据模型自动适配合法的dimension
        v3_dims = [64, 128, 256, 512, 768, 1024]
        v4_dims = [1024, 1536, 2048, 3072, 4096]
        if self.model == "text-embedding-v3":
            valid_dims = v3_dims
            default_dim = 1024
        else:
            valid_dims = v4_dims
            default_dim = 1536
        env_dim = os.getenv("QWEN_EMBEDDING_DIM")
        if env_dim is not None:
            try:
                dim = int(env_dim)
                if dim not in valid_dims:
                    logger.warning(f"QWEN_EMBEDDING_DIM={dim} 非法，{self.model}支持: {valid_dims}，将使用默认: {default_dim}")
                    dim = default_dim
            except Exception:
                logger.warning(f"QWEN_EMBEDDING_DIM={env_dim} 解析失败，将使用默认: {default_dim}")
                dim = default_dim
        else:
            dim = default_dim
        self.dimensions = dim

        if not self.api_key:
            logger.error("DASHSCOPE_API_KEY 未设置！")
            raise ValueError("DASHSCOPE_API_KEY 未设置！")
        logger.info(f"Qwen API base_url: {self.base_url}")
        logger.info(f"Qwen API key: {'已加载' if self.api_key else '未加载'}")
        logger.info(f"Qwen embedding model: {self.model}, dimensions: {self.dimensions}")
        self.client = OpenAI(
            api_key=self.api_key,
            base_url=self.base_url
        )

    def get_embedding(self, text: str) -> List[float]:
        """
        获取单个文本的向量表示
        """
        logger.info(f"请求 embedding，模型: {self.model}, 文本长度: {len(text)}")
        try:
            response = self.client.embeddings.create(
                model=self.model,
                input=text,
                dimensions=self.dimensions,
                encoding_format="float"
            )
            return response.data[0].embedding
        except Exception as e:
            # 增强日志，输出完整异常内容
            import traceback
            logger.error(f"调用 Qwen embedding API 失败: {e}\n{traceback.format_exc()}")
            raise

    def get_embeddings(self, texts: List[str]) -> np.ndarray:
        """
        批量获取文本向量
        """
        embeddings = []
        for text in texts:
            embedding = self.get_embedding(text)
            embeddings.append(embedding)
        return np.array(embeddings)

    def get_chunks_embeddings(self, chunks: List[dict]) -> tuple:
        """
        获取文档分块的向量表示
        返回: (embeddings, contents, sources)
        """
        if not chunks:
            # 保证返回三个空对象，避免解包错误
            return np.array([]), [], []
        contents = [chunk['content'] for chunk in chunks]
        sources = [chunk['source'] for chunk in chunks]
        embeddings = self.get_embeddings(contents)
        return embeddings, contents, sources
