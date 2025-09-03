"""
AI服务主入口
负责初始化 FastAPI 应用、配置跨域请求、中间件、加载各个服务模块，以及定义 API 接口。
"""
import os
from typing import List, Dict, Optional, Any
from fastapi import FastAPI, File, UploadFile, HTTPException, Form, Body, Depends, Request
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, ConfigDict
from services.doc_parser import DocumentParser
from services.embedding import EmbeddingService
from services.rag import RAGService
from services.storage import StorageService
from utils.logger import api_logger as logger
from functools import lru_cache
from services.faiss_db import FAISSDatabase

app = FastAPI(title="教学内容生成服务")

# 配置CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 初始化服务
storage_service = StorageService()
doc_parser = DocumentParser()
embedding_service = EmbeddingService()

# 创建一个FAISSDatabase实例并传递给RAGService
rag_service = RAGService(storage_service)

# -------- 按教师隔离的服务映射 --------

# 全局缓存 {user_id: (StorageService, RAGService)}
user_kb_services: dict[str, tuple[StorageService, RAGService]] = {}

def get_services(user_id: str | None):
    """根据 user_id 返回对应 (storage_service, rag_service)
    None 或空串 表示公共服务器库 (全局默认)"""
    if not user_id:
        return storage_service, rag_service
    if user_id not in user_kb_services:
        ss = StorageService()
        rs = RAGService(ss)
        user_kb_services[user_id] = (ss, rs)
    return user_kb_services[user_id]

class TeachingContentRequest(BaseModel):
    course_name: str
    course_outline: str
    expected_hours: int
    constraints: Optional[str] = None

class TimePlanItem(BaseModel):
    content: str
    minutes: int
    step: str

class TeachingContentDetail(BaseModel):
    title: str
    knowledgePoints: List[str]  # array(string)
    practiceContent: str        # string
    teachingGuidance: str       # string
    timePlan: List[TimePlanItem]  # Array(object)
    constraints: Optional[str] = None

class ExerciseGenerationRequest(BaseModel):
    course_name: str
    lesson_content: str
    difficulty: Optional[str] = "medium"
    choose_count: Optional[int] = 5
    fill_blank_count: Optional[int] = 5
    question_count: Optional[int] = 2
    custom_types: Optional[Dict[str, int]] = None

class SubjectiveAnswerEvalRequest(BaseModel):
    question: str
    student_answer: str
    reference_answer: str
    max_score: float

class ExerciseQuestion(BaseModel):
    content: str
    error_rate: float
    type: str
    score: Optional[float] = None
    student_count: Optional[int] = None
    correct_count: Optional[int] = None
    additional_info: Optional[Dict[str, Any]] = None

class ExerciseAnalysisRequest(BaseModel):
    model_config = ConfigDict(arbitrary_types_allowed=True)
    exercise_questions: List[ExerciseQuestion]

# 学生在线学习助手请求模型
class StudentAssistantRequest(BaseModel):
    question: str
    course_name: Optional[str] = None
    chat_history: Optional[List[Dict[str, str]]] = None

# 学生实时练习生成请求模型
class StudentExerciseGenerationRequest(BaseModel):
    requirements: str  # 题目设置要求（题型、数量、难度等）
    knowledge_preferences: str  # 知识点偏好
    wrong_questions: Optional[List[Dict[str, Any]]] = None  # 历史错题列表，可为空

# ---------------- 新增：按学生选题生成自测练习 -----------------
# 该模式下，学生会在前端选择错题库/收藏题库中的特定题目用于练习。
# 与随机历史错题模式不同，此处的 selected_questions 表示"固定题目列表"，
# rag_service 将把这些题目直接放入返回结果的开头。

class SelectedStudentExerciseRequest(BaseModel):
    """根据学生选定题目生成练习的请求模型"""
    selected_questions: List[Dict[str, Any]]  # 学生明确选择的题目列表（题目结构同 wrong_questions）
    # 其余字段可选，用于补充生成新的题目
    requirements: Optional[str] = ""  # 题目设置要求，可为空
    knowledge_preferences: Optional[str] = ""  # 知识点偏好，可为空

class CourseOptimizationRequest(BaseModel):
    courseName: str
    sectionName: str
    averageScore: float
    errorRate: float
    studentCount: int

class TeachingContentFeedbackRequest(BaseModel):
    originalPlan: Dict[str, Any]
    feedback: str

class StepDetailRequest(BaseModel):
    lessonTitle: str
    stepName: str
    currentContent: Optional[str] = ""
    knowledgePoints: Optional[List[str]] = None

# ---------------------- 存储路径动态配置 ----------------------

class BasePathRequest(BaseModel):
    base_path: str

@app.post("/storage/base_path")
async def set_storage_base_path(request: BasePathRequest):
    """设置知识库等文件的本地存储根目录"""
    try:
        storage_service.set_base_path(request.base_path)
        # 路径切换后，重新加载向量数据库
        rag_service.reload_vector_db()
        return {"status": "success", "message": "Base path updated"}
    except Exception as e:
        logger.error(f"Error setting base path: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# 兼容路径：/embedding/base_path (供 API 网关转发)
@app.post("/embedding/base_path")
async def set_storage_base_path_alt(request: BasePathRequest):
    return await set_storage_base_path(request)

@app.post("/storage/base_path/reset")
async def reset_storage_base_path():
    """恢复默认的 storage 目录路径"""
    try:
        default_path = os.path.abspath(".")
        storage_service.set_base_path(default_path)
        rag_service.reload_vector_db()
        return {"status": "success", "message": "Base path reset to default", "base_path": default_path}
    except Exception as e:
        logger.error(f"Error resetting base path: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# 兼容路径：/embedding/base_path/reset
@app.post("/embedding/base_path/reset")
async def reset_storage_base_path_alt():
    return await reset_storage_base_path()

@app.get("/storage/document_exists")
async def check_document_exists(filename: str, request: Request, course_id: Optional[str] = None):
    user_id = request.query_params.get("user_id")
    ss, _ = get_services(user_id)
    try:
        exists = ss.document_exists(filename, course_id)
        return {"exists": exists}
    except Exception as e:
        logger.error(f"Error checking document exists ({user_id}): {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/embedding/upload")
async def upload_file(
    request: Request,
    file: UploadFile = File(...),
    course_id: Optional[str] = Form(None)
):
    """
    上传文件并处理入库
    """
    user_id = request.query_params.get("user_id")
    ss, rs = get_services(user_id)
    try:
        # 重名检查（避免重复上传浪费 Token）
        if ss.document_exists(file.filename, course_id):
            raise HTTPException(status_code=400, detail="文件已存在于知识库中")

        # 保存到临时目录
        temp_path = os.path.join(ss.get_temp_dir(), file.filename)
        content = await file.read()
        with open(temp_path, "wb") as f:
            f.write(content)

        try:
            # 保存文档
            doc_path = ss.save_document(temp_path, course_id)
            logger.info(f"Saved document to {doc_path}")

            # 解析文件为文本块（chunks)
            chunks = doc_parser.parse_file(doc_path)
            logger.info(f"Successfully parsed file {file.filename} into {len(chunks)} chunks")

            # 添加到RAG知识库
            rs.add_to_knowledge_base(chunks)
            logger.info(f"Successfully added {len(chunks)} chunks to knowledge base")

            return {
                "status": "success",
                "message": f"文件 {file.filename} 处理成功",
                "chunks_count": len(chunks),
                "file_path": doc_path
            }

        finally:
            # 清理临时文件
            if os.path.exists(temp_path):
                os.remove(temp_path)

    except Exception as e:
        logger.error(f"Error processing file {file.filename}: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/rag/generate")
async def generate_teaching_content(request: TeachingContentRequest):
    """
    生成教学内容
    """
    try:
        result = rag_service.generate_teaching_content(
            course_outline=request.course_outline,
            course_name=request.course_name,
            expected_hours=request.expected_hours,
            constraints=request.constraints
        )
        logger.info(f"Successfully generated teaching content for {request.course_name}")
        return result
    except Exception as e:
        logger.error(f"Error generating teaching content: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/rag/detail")
async def generate_teaching_content_detail(request: TeachingContentDetail):
    """
    生成教学内容详细信息
    """
    try:
        result = rag_service.generate_teaching_content_detail(
            title=request.title,
            knowledgePoints=request.knowledgePoints,
            practiceContent=request.practiceContent,
            teachingGuidance=request.teachingGuidance,
            timePlan=request.timePlan,
            constraints=request.constraints,
        )
        logger.info(f"Successfully generated teaching content detail for {request.title}")
        return result
    except Exception as e:
        logger.error(f"Error generating teaching content detail: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/rag/regenerate")
async def regenerate_teaching_content_detail(request: TeachingContentDetail):
    """
    重新生成教学内容
    """
    try:
        result = rag_service.regenerate_teaching_content_detail(
            title=request.title,
            knowledgePoints=request.knowledgePoints,
            practiceContent=request.practiceContent,
            teachingGuidance=request.teachingGuidance,
            timePlan=request.timePlan,
            constraints=request.constraints,
        )
        logger.info(f"Successfully regenerated teaching content for {request.title}")
        return result
    except Exception as e:
        logger.error(f"Error generating teaching content detail: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/rag/generate_exercise")
async def generate_exercise(request: ExerciseGenerationRequest):
    """
    生成练习题
    """
    try:
        result = rag_service.generate_exercises(
            course_name=request.course_name,
            lesson_content=request.lesson_content,
            difficulty=request.difficulty,
            choose_count=request.choose_count,
            fill_blank_count=request.fill_blank_count,
            question_count=request.question_count,
            custom_types=request.custom_types
        )
        logger.info(f"Successfully generated exercises for {request.course_name}")
        return result
    except Exception as e:
        logger.error(f"Error generating exercises: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/rag/evaluate_subjective")
async def evaluate_subjective_answer(request: SubjectiveAnswerEvalRequest):
    """
    评估主观题答案
    """
    try:
        result = rag_service.evaluate_subjective_answer(
            question=request.question,
            student_answer=request.student_answer,
            reference_answer=request.reference_answer,
            max_score=request.max_score
        )
        logger.info(f"Successfully evaluated subjective answer")
        return result
    except Exception as e:
        logger.error(f"Error evaluating subjective answer: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/rag/analyze_exercise")
async def analyze_exercise(request: ExerciseAnalysisRequest):
    """
    分析练习整体情况
    """
    try:
        #先打印request里面是否有题目
        logger.info(f"Analyzing exercise with {len(request.exercise_questions)} questions")
        logger.info(f"Exercise questions: {request.exercise_questions}")
        result = rag_service.analyze_exercise(exercise_questions=request.exercise_questions)
        logger.info(f"Successfully analyzed exercise")
        return result
    except Exception as e:
        logger.error(f"Error analyzing exercise: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/rag/assistant")
async def online_learning_assistant(request: Request, body: StudentAssistantRequest):
    """
    在线学习助手 - 回答学生问题
    """
    try:
        user_id = request.query_params.get("user_id")
        _, rs = get_services(user_id)
        result = rs.answer_student_question(
            question=body.question,
            course_name=body.course_name,
            chat_history=body.chat_history
        )
        logger.info("Successfully answered student question")
        return result
    except Exception as e:
        logger.error(f"Error answering student question: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/health")
async def health_check():
    """
    健康检查接口
    """
    return {"status": "healthy"}

# -------------------- 学生实时练习生成 --------------------

@app.post("/rag/generate_student_exercise")
async def generate_student_exercise(request: StudentExerciseGenerationRequest):
    """生成学生自测练习"""
    try:
        result = rag_service.generate_student_exercise(
            requirements=request.requirements,
            knowledge_preferences=request.knowledge_preferences,
            wrong_questions=request.wrong_questions
        )
        logger.info("Successfully generated student exercise")
        return result
    except Exception as e:
        logger.error(f"Error generating student exercise: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/rag/optimize_course")
async def optimize_course(request: CourseOptimizationRequest):
    """
    生成课程优化建议
    """
    try:
        result = rag_service.generate_course_optimization(
            course_name=request.courseName,
            section_name=request.sectionName,
            average_score=request.averageScore,
            error_rate=request.errorRate,
            student_count=request.studentCount
        )
        logger.info(f"Successfully generated optimization suggestions for {request.courseName}")
        return result
    except Exception as e:
        logger.error(f"Error generating optimization suggestions: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/rag/feedback")
async def revise_teaching_content(request: TeachingContentFeedbackRequest):
    """根据教师反馈修改教学大纲"""
    try:
        result = rag_service.revise_teaching_content(
            original_plan=request.originalPlan,
            feedback=request.feedback
        )
        logger.info("Successfully revised teaching content via feedback")
        return result
    except Exception as e:
        logger.error(f"Error revising teaching content: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/rag/step_detail")
async def generate_step_detail(request: StepDetailRequest):
    """生成课时中某一环节的详细内容"""
    try:
        result = rag_service.generate_step_detail(
            lesson_title=request.lessonTitle,
            step_name=request.stepName,
            current_content=request.currentContent or "",
            knowledge_points=request.knowledgePoints or []
        )
        logger.info("Successfully generated step detail for %s-%s", request.lessonTitle, request.stepName)
        return result
    except Exception as e:
        logger.error(f"Error generating step detail: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/rag/generate_section")
async def generate_section_teaching_content(
    file: UploadFile = File(...),
    course_name: str = Form(...),
    section_title: str = Form(...),
    expected_hours: int = Form(...),
    constraints: Optional[str] = Form(None)
):
    """上传章节大纲文件并生成该章节教案"""
    try:
        # 保存文件到临时
        temp_path = os.path.join(storage_service.get_temp_dir(), file.filename)
        content_bytes = await file.read()
        with open(temp_path, "wb") as f:
            f.write(content_bytes)

        # 校验文件扩展名，仅支持 pdf/docx
        _, ext = os.path.splitext(file.filename.lower())
        if ext not in [".pdf", ".docx"]:
            if os.path.exists(temp_path):
                os.remove(temp_path)
            raise HTTPException(status_code=400, detail=f"Unsupported file type: {ext}. 仅支持 PDF 与 DOCX 文件")

        try:
            # 解析文本块
            chunks = doc_parser.parse_file(temp_path)
            outline_text = "\n".join([c["content"] for c in chunks])
            if not outline_text.strip():
                raise ValueError("解析失败，文件内容为空或无法读取")
            # 调用生成
            result = rag_service.generate_teaching_content(
                course_outline=outline_text,
                course_name=f"{course_name}-{section_title}",
                expected_hours=expected_hours,
                constraints=constraints
            )
            return result
        finally:
            if os.path.exists(temp_path):
                os.remove(temp_path)
    except HTTPException:
        # 已经抛出的 HTTPException 直接向上抛
        raise
    except Exception as e:
        logger.error(f"Error generating section teaching content: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

# ---------------- 新增接口：根据学生选题生成自测练习 -----------------

@app.post("/rag/generate_selected_student_exercise")
async def generate_selected_student_exercise(request: SelectedStudentExerciseRequest):
    """根据学生选定的题目列表生成自测练习"""
    try:
        result = rag_service.generate_exercise_from_selected(
            selected_questions=request.selected_questions,
            requirements=request.requirements or "",
            knowledge_preferences=request.knowledge_preferences or ""
        )
        logger.info("Successfully generated student exercise based on selected questions")
        return result
    except Exception as e:
        logger.error(f"Error generating selected student exercise: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

# =================== 联合知识库管理API ===================

# 联合知识库：获取当前激活列表

@app.get("/storage/list")
async def get_storage_base_paths(request: Request):
    user_id = request.query_params.get("user_id")
    ss, _ = get_services(user_id)
    return ss.get_base_paths()

# 设置激活的知识库列表（数组）

@app.post("/storage/selected")
async def set_storage_selected_paths(paths: List[str] = Body(...), request: Request = None):
    user_id = request.query_params.get("user_id") if request else None
    ss, rs = get_services(user_id)
    try:
        ss.set_base_paths(paths)
        rs.reload_vector_db()
        return {"status": "success", "paths": ss.get_base_paths()}
    except Exception as e:
        logger.error(f"Error setting selected KB paths for {user_id}: {e}")
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
