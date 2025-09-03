"""
文档解析服务，支持多种格式的文档内容提取
"""
import os
from typing import List, Dict
import pdfplumber
from docx import Document
import pandas as pd
import markdown
from bs4 import BeautifulSoup
import json
from utils.logger import doc_parser_logger as logger
import tempfile

class DocumentParser:
    """文档解析器，支持多种文档格式的内容提取"""
    
    SUPPORTED_EXTENSIONS = {'.pdf', '.docx', '.doc', '.txt', '.md', '.json', '.csv', '.xlsx', '.html'}
    
    @staticmethod
    def parse_pdf(file_path: str) -> List[Dict[str, str]]:
        """解析PDF文件"""
        try:
            chunks = []
            with pdfplumber.open(file_path) as pdf:
                for page_num, page in enumerate(pdf.pages, 1):
                    text = page.extract_text()
                    if text:
                        paragraphs = text.split('\n\n')
                        for para in paragraphs:
                            if para.strip():
                                chunks.append({
                                    'content': para.strip(),
                                    'source': f"{os.path.basename(file_path)} 第{page_num}页"
                                })
                    else:
                        logger.warning(f"PDF {file_path} 第{page_num}页无可提取文本")
            
            if not chunks:
                logger.warning(f"PDF文件 {file_path} 未提取到有效内容")
                return []
            
            logger.info(f"成功解析PDF文件 {file_path}，提取到 {len(chunks)} 个文本块")
            return chunks
        except Exception as e:
            logger.error(f"解析PDF文件失败: {file_path}, 错误: {str(e)}")
            raise

    @staticmethod
    def parse_docx(file_path: str) -> List[Dict[str, str]]:
        """解析DOCX文件"""
        try:
            doc = Document(file_path)
            chunks = []
            para_count = 0
            
            for paragraph in doc.paragraphs:
                text = paragraph.text.strip()
                if text:
                    para_count += 1
                    chunks.append({
                        'content': text,
                        'source': f"{os.path.basename(file_path)} 第{para_count}段"
                    })
            
            if not chunks:
                logger.error(f"DOCX文件 {file_path} 无可提取文本")
                raise ValueError("DOCX文件无可提取文本")
            
            logger.info(f"成功解析DOCX文件 {file_path}，共 {len(chunks)} 个段落")
            return chunks
        except Exception as e:
            logger.error(f"解析DOCX文件失败: {file_path}, 错误: {str(e)}")
            raise

    @staticmethod
    def parse_txt(file_path: str) -> List[Dict[str, str]]:
        """解析TXT文件"""
        try:
            with open(file_path, 'r', encoding='utf-8') as file:
                content = file.read()
            
            if not content.strip():
                logger.error(f"TXT文件 {file_path} 为空")
                raise ValueError("TXT文件为空")
            
            # 按段落分割
            paragraphs = content.split('\n\n')
            chunks = []
            for i, para in enumerate(paragraphs, 1):
                if para.strip():
                    chunks.append({
                        'content': para.strip(),
                        'source': f"{os.path.basename(file_path)} 第{i}段"
                    })
            
            logger.info(f"成功解析TXT文件 {file_path}，共 {len(chunks)} 个段落")
            return chunks
        except Exception as e:
            logger.error(f"解析TXT文件失败: {file_path}, 错误: {str(e)}")
            raise

    @staticmethod
    def parse_markdown(file_path: str) -> List[Dict[str, str]]:
        """解析Markdown文件"""
        try:
            with open(file_path, 'r', encoding='utf-8') as file:
                md_content = file.read()
            
            # 转换为HTML以便解析
            html = markdown.markdown(md_content)
            soup = BeautifulSoup(html, 'html.parser')
            
            chunks = []
            # 提取标题和内容
            for i, element in enumerate(soup.find_all(['h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'p']), 1):
                text = element.get_text().strip()
                if text:
                    chunks.append({
                        'content': text,
                        'source': f"{os.path.basename(file_path)} 第{i}块"
                    })
            
            if not chunks:
                logger.error(f"Markdown文件 {file_path} 无可提取内容")
                raise ValueError("Markdown文件无可提取内容")
            
            logger.info(f"成功解析Markdown文件 {file_path}，共 {len(chunks)} 个内容块")
            return chunks
        except Exception as e:
            logger.error(f"解析Markdown文件失败: {file_path}, 错误: {str(e)}")
            raise

    @staticmethod
    def parse_json(file_path: str) -> List[Dict[str, str]]:
        """解析JSON文件"""
        try:
            with open(file_path, 'r', encoding='utf-8') as file:
                data = json.load(file)
            
            # 将JSON转换为可读文本
            text = json.dumps(data, ensure_ascii=False, indent=2)
            
            chunks = [{
                'content': text,
                'source': f"{os.path.basename(file_path)} JSON内容"
            }]
            
            logger.info(f"成功解析JSON文件 {file_path}")
            return chunks
        except Exception as e:
            logger.error(f"解析JSON文件失败: {file_path}, 错误: {str(e)}")
            raise

    @staticmethod
    def parse_excel(file_path: str) -> List[Dict[str, str]]:
        """解析Excel文件"""
        try:
            # 读取所有工作表
            excel_file = pd.ExcelFile(file_path)
            chunks = []
            
            for sheet_name in excel_file.sheet_names:
                df = pd.read_excel(file_path, sheet_name=sheet_name)
                
                # 转换为CSV格式的字符串
                csv_content = df.to_csv(index=False)
                chunks.append({
                    'content': csv_content,
                    'source': f"{os.path.basename(file_path)} 工作表:{sheet_name}"
                })
            
            if not chunks:
                logger.error(f"Excel文件 {file_path} 无可提取数据")
                raise ValueError("Excel文件无可提取数据")
            
            logger.info(f"成功解析Excel文件 {file_path}，共 {len(chunks)} 个工作表")
            return chunks
        except Exception as e:
            logger.error(f"解析Excel文件失败: {file_path}, 错误: {str(e)}")
            raise

    @staticmethod
    def parse_csv(file_path: str) -> List[Dict[str, str]]:
        """解析CSV文件"""
        try:
            df = pd.read_csv(file_path)
            csv_content = df.to_csv(index=False)
            
            chunks = [{
                'content': csv_content,
                'source': f"{os.path.basename(file_path)} CSV数据"
            }]
            
            logger.info(f"成功解析CSV文件 {file_path}")
            return chunks
        except Exception as e:
            logger.error(f"解析CSV文件失败: {file_path}, 错误: {str(e)}")
            raise

    @staticmethod
    def parse_html(file_path: str) -> List[Dict[str, str]]:
        """解析HTML文件"""
        try:
            with open(file_path, 'r', encoding='utf-8') as file:
                html_content = file.read()
            
            soup = BeautifulSoup(html_content, 'html.parser')
            
            # 移除脚本和样式标签
            for script in soup(["script", "style"]):
                script.decompose()
            
            # 提取文本内容
            text = soup.get_text()
            
            # 清理和分段
            lines = (line.strip() for line in text.splitlines())
            chunks = []
            for i, line in enumerate(lines, 1):
                if line:
                    chunks.append({
                        'content': line,
                        'source': f"{os.path.basename(file_path)} 第{i}行"
                    })
            
            if not chunks:
                logger.error(f"HTML文件 {file_path} 无可提取文本")
                raise ValueError("HTML文件无可提取文本")
            
            logger.info(f"成功解析HTML文件 {file_path}，共 {len(chunks)} 行文本")
            return chunks
        except Exception as e:
            logger.error(f"解析HTML文件失败: {file_path}, 错误: {str(e)}")
            raise

    def parse_file(self, file_path: str) -> List[Dict[str, str]]:
        """根据文件类型调用对应的解析方法"""
        ext = os.path.splitext(file_path)[1].lower()
        
        if ext not in self.SUPPORTED_EXTENSIONS:
            error_msg = f"Unsupported file type: {ext}"
            logger.error(error_msg)
            raise ValueError(error_msg)
        
        try:
            if ext == '.pdf':
                return self.parse_pdf(file_path)
            elif ext == '.docx':
                return self.parse_docx(file_path)
            elif ext == '.txt':
                return self.parse_txt(file_path)
            elif ext == '.md':
                return self.parse_markdown(file_path)
            elif ext == '.json':
                return self.parse_json(file_path)
            elif ext in ['.xlsx', '.xls']:
                return self.parse_excel(file_path)
            elif ext == '.csv':
                return self.parse_csv(file_path)
            elif ext == '.html':
                return self.parse_html(file_path)
        except Exception as e:
            logger.error(f"Error parsing file {file_path}: {str(e)}")
            raise

    def chunk_text(self, text: str, chunk_size: int = 500) -> List[Dict[str, str]]:
        """将长文本分块"""
        try:
            chunks = []
            words = text.split()
            current_chunk = []
            current_size = 0
            
            for word in words:
                current_size += len(word) + 1  # +1 for space
                if current_size > chunk_size:
                    if current_chunk:
                        chunks.append({
                            'content': ' '.join(current_chunk),
                            'source': 'Text Input'
                        })
                    current_chunk = [word]
                    current_size = len(word)
                else:
                    current_chunk.append(word)
            
            # 添加最后一个chunk
            if current_chunk:
                chunks.append({
                    'content': ' '.join(current_chunk),
                    'source': 'Text Input'
                })
            
            logger.info(f"成功分块文本，共 {len(chunks)} 个块")
            return chunks
        except Exception as e:
            logger.error(f"文本分块失败: {str(e)}")
            raise
