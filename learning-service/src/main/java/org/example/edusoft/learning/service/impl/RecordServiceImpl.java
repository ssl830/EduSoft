package org.example.edusoft.learning.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import org.example.edusoft.learning.service.RecordService;
import org.example.edusoft.learning.mapper.*;
import org.example.edusoft.learning.entity.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import java.time.LocalDateTime;
import com.itextpdf.io.font.PdfEncodings;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

import org.example.edusoft.learning.client.UserServiceClient;
import org.example.edusoft.learning.client.ContentClient;
import org.example.edusoft.learning.client.CourseClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Transactional(readOnly = true)
public class RecordServiceImpl implements RecordService {
    @Autowired
    private StudyRecordMapper studyRecordMapper;

    @Autowired
    private PracticeRecordMapper practiceRecordMapper;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private ContentClient contentClient;

    @Autowired
    private CourseClient courseClient;

    @Value("${services.course.base-url:http://localhost:8082}")
    private String courseBaseUrl;

    @Value("${services.user.base-url:http://localhost:8081}")
    private String userBaseUrl;

    @Value("${services.content.base-url:http://localhost:8083}")
    private String contentBaseUrl;
 
    private String resolveOutboundToken() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servlet) {
            String satoken = servlet.getRequest().getHeader("satoken");
            if (satoken != null && !satoken.isEmpty())
                return satoken;
            String cookie = servlet.getRequest().getHeader("Cookie");
            if (cookie != null) {
                for (String part : cookie.split(";")) {
                    String p = part.trim();
                    if (p.startsWith("satoken="))
                        return p.substring("satoken=".length());
                }
            }
            String auth = servlet.getRequest().getHeader("Authorization");
            if (auth != null && !auth.isEmpty())
                return auth;
        }
        return null;
    }

    private String fetchUsernameByUserId(Long userId, String token) {
        if (userId == null || token == null) {
            System.out.println("fetchUsernameByUserId: userId=" + userId + ", token=" + token + " - 参数无效");
            return null;
        }
    
        System.out.println("fetchUsernameByUserId: 开始调用用户微服务，userId=" + userId + ", token=" + token);
        System.out.println("fetchUsernameByUserId: 用户微服务地址=" + userBaseUrl);
    
        try {
            // fetchUserById 已经处理了 SaResult 格式，直接返回用户数据
            Map<String, Object> userData = userServiceClient.fetchUserById(
                    userBaseUrl, token, String.valueOf(userId));
            System.out.println("fetchUsernameByUserId: 用户微服务返回数据=" + userData);
    
            if (userData != null) {
                Object name = userData.get("username");
                String result = name == null ? null : String.valueOf(name);
                System.out.println("fetchUsernameByUserId: 提取的用户名=" + result);
                return result;
            } else {
                System.out.println("fetchUsernameByUserId: 用户微服务返回null");
            }
        } catch (Exception e) {
            System.out.println("fetchUsernameByUserId: 调用用户微服务异常=" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // 完成微服务化改造
    @Override
    public List<StudyRecord> getStudyRecords(Long studentId) {
        System.out.println("=============================================");
        System.out.println(studentId);
        // 通过内容服务获取学习记录
        List<StudyRecord> records = contentClient.getStudyRecordsByStudentId(studentId);
        if (records == null || records.isEmpty()) return records;
        System.out.println("records:"+records);

        // 批量获取resourceId 
        List<Long> resourceIds = records.stream()
                .map(StudyRecord::getResourceId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
        String resourceIdsStr = resourceIds.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("");
        List<Map<String, Object>> resourceList = contentClient.getResourcesByIds(resourceIdsStr);

        System.out.println("resourceList:"+resourceList);

        Map<Long, Map<String, Object>> resourceMap = new HashMap<>();
        for (Map<String, Object> res : resourceList) {
            // 资源信息在data字段
            Object dataObj = res.get("data");
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            if (dataObj instanceof Map<?, ?> data) {
                Object idObj = data.get("id");
                System.out.println("HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                if (idObj != null) resourceMap.put(Long.valueOf(idObj.toString()), (Map<String, Object>) data);
            }
        }

        System.out.println("resourceMap" + resourceMap);

        // 批量获取sectionId
        List<Long> sectionIds = resourceList.stream()
                .map(r -> r.get("chapterId"))
                .filter(java.util.Objects::nonNull)
                .map(id -> Long.valueOf(id.toString()))
                .distinct()
                .toList();
        System.out.println("sectionIds:"+sectionIds);

        Map<Long, Map<String, Object>> sectionMap = new HashMap<>();
        if (!sectionIds.isEmpty()) {
            String sectionIdsStr = sectionIds.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("");
            List<Map<String, Object>> sectionList = courseClient.getSectionsByCourseId(null); // 需根据实际API调整为批量查section
            for (Map<String, Object> sec : sectionList) {
                Object idObj = sec.get("id");
                if (idObj != null) sectionMap.put(Long.valueOf(idObj.toString()), sec);
            }
        }

        // 批量获取courseId
        List<Long> courseIds = resourceMap.values().stream()
                .map(sec -> sec.get("courseId"))
                .filter(java.util.Objects::nonNull)
                .map(id -> Long.valueOf(id.toString()))
                .distinct()
                .toList();
        System.out.println("courseIds:"+courseIds);

        Map<Long, Map<String, Object>> courseMap = new HashMap<>();
        if (!courseIds.isEmpty()) {
            String courseIdsStr = courseIds.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("");
            List<Map<String, Object>> courseList = courseClient.getCoursesByIds(courseIdsStr);
            System.out.println("courseList"+courseList);
            for (Map<String, Object> c : courseList) {
                Object idObj = c.get("id");
                if (idObj != null) courseMap.put(Long.valueOf(idObj.toString()), c);
            }
        }

        // 组装信息到StudyRecord
        for (StudyRecord rec : records) {
            Map<String, Object> res = resourceMap.get(rec.getResourceId());
            if (res != null) {
                rec.setResourceTitle((String) res.get("title"));
                Long sectionId = res.get("chapterId") != null ? Long.valueOf(res.get("chapterId").toString()) : null;
                if (sectionId != null) {
                    Map<String, Object> sec = sectionMap.get(sectionId);
                    if (sec != null) {
                        rec.setSectionTitle((String) sec.get("title"));
                    }
                }
                // 只要有courseId就设置courseName
                Long courseId = res.get("courseId") != null ? Long.valueOf(res.get("courseId").toString()) : null;
                if (courseId != null) {
                    Map<String, Object> course = courseMap.get(courseId);
                    if (course != null) {
                        rec.setCourseName((String) course.get("name"));
                    }
                }
            }
        }
        System.out.println("records:"+records);

        return records;
    }

    // 已完成��服务化改造
    @Override
    public List<StudyRecord> getStudyRecordsByCourse(Long studentId, Long courseId) {
        List<StudyRecord> records = getStudyRecords(studentId);
        if (records == null || records.isEmpty()) return records;

        // 通过courseId获取课程名
        String courseIdsStr = String.valueOf(courseId);
        List<Map<String, Object>> courseList = courseClient.getCoursesByIds(courseIdsStr);
        String targetCourseName = null;
        for (Map<String, Object> course : courseList) {
            Object idObj = course.get("id");
            Object nameObj = course.get("name");
            if (idObj != null && nameObj != null && Long.valueOf(idObj.toString()).equals(courseId)) {
                targetCourseName = nameObj.toString();
                break;
            }
        }
        if (targetCourseName == null) return new ArrayList<>();

        // 筛选courseName相同的部分
        List<StudyRecord> filtered = new ArrayList<>();
        for (StudyRecord rec : records) {
            if (rec.getCourseName() != null && rec.getCourseName().equals(targetCourseName)) {
                filtered.add(rec);
            }
        }
        return filtered;
    }

    // 查询所有练习记录，完成微服务化改造
    @Override
    public List<PracticeRecord> getPracticeRecords(Long studentId) {
        // 1. 只查本地submission/practice主表（不跨库join）
        List<PracticeRecord> records = practiceRecordMapper.findPracticeRecords(studentId); // 只查id, practice_id, student_id, submitted_at, score, feedback, course_id, practice_title, class_id
        if (records == null || records.isEmpty()) return records;

        // 2. 批量获取courseId
        List<Long> courseIds = records.stream().map(PracticeRecord::getCourseId).filter(java.util.Objects::nonNull).distinct().toList();

        // 3. 批量获取课程名
        String courseIdsStr = courseIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        List<Map<String, Object>> courseList = courseClient.getCoursesByIds(courseIdsStr);
        Map<Long, String> courseNameMap = new HashMap<>();
        for (Map<String, Object> course : courseList) {
            Object idObj = course.get("id");
            Object nameObj = course.get("name");
            if (idObj != null && nameObj != null) {
                courseNameMap.put(Long.valueOf(idObj.toString()), nameObj.toString());
            }
        }

        // 4. 批量获取班级名
        List<Map<String, Object>> classList = courseClient.getClassesByUserIdAndCourseIds(studentId, courseIds);
        // 组装Map<Long, String>，每个课程下的班级名用逗号拼接
        Map<Long, String> classNameMap = new HashMap<>();
        
        // 处理班级��息，按课程ID分组
        Map<Long, List<String>> courseClassMap = new HashMap<>();
        for (Map<String, Object> classInfo : classList) {
            Object courseIdObj = classInfo.get("courseId");
            Object classNameObj = classInfo.get("name");
            if (courseIdObj != null && classNameObj != null) {
                Long cId = Long.valueOf(courseIdObj.toString());
                String className = classNameObj.toString();
                courseClassMap.computeIfAbsent(cId, k -> new ArrayList<>()).add(className);
            }
        }
        
        // 将班级名列表转换为逗号分隔的字符串
        for (Map.Entry<Long, List<String>> entry : courseClassMap.entrySet()) {
            String names = String.join(",", entry.getValue());
            classNameMap.put(entry.getKey(), names);
        }

        // 5. 组装信息
        for (PracticeRecord record : records) {
            record.setCourseName(courseNameMap.get(record.getCourseId()));
            record.setClassName(classNameMap.get(record.getCourseId()));
            // 保持 questions 逻辑不变
            List<QuestionRecord> questions = practiceRecordMapper.findQuestionsBySubmissionId(record.getId());
            if (questions != null && !questions.isEmpty()) {
                record.setQuestions(questions);
            }
        }
        return records;
    }

    // 完成微服务化改造
    @Override
    public List<PracticeRecord> getPracticeRecordsByCourse(Long studentId, Long courseId) {
        List<PracticeRecord> records = practiceRecordMapper.findByStudentIdAndCourseId(studentId, courseId);
        if (records == null || records.isEmpty()) return records;

        // 批量获取courseId和classId
        List<Long> courseIds = records.stream().map(PracticeRecord::getCourseId).filter(java.util.Objects::nonNull).distinct().toList();
        List<Long> classIds = records.stream().map(PracticeRecord::getClassId).filter(java.util.Objects::nonNull).distinct().toList();

        // 批量获取课程名
        String courseIdsStr = courseIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        List<Map<String, Object>> courseList = courseClient.getCoursesByIds(courseIdsStr);
        Map<Long, String> courseNameMap = new HashMap<>();
        for (Map<String, Object> course : courseList) {
            Object idObj = course.get("id");
            Object nameObj = course.get("name");
            if (idObj != null && nameObj != null) {
                courseNameMap.put(Long.valueOf(idObj.toString()), nameObj.toString());
            }
        }

        // 批量获取班级信息
        List<Map<String, Object>> classList = courseClient.getClassesByUserIdAndCourseIds(studentId, courseIds);
        Map<Long, String> classNameMap = new HashMap<>();
        
        // 处理班级信息，按课程ID分组
        Map<Long, List<String>> courseClassMap = new HashMap<>();
        for (Map<String, Object> classInfo : classList) {
            Object courseIdObj = classInfo.get("courseId");
            Object classNameObj = classInfo.get("name");
            if (courseIdObj != null && classNameObj != null) {
                Long cId = Long.valueOf(courseIdObj.toString());
                String className = classNameObj.toString();
                courseClassMap.computeIfAbsent(cId, k -> new ArrayList<>()).add(className);
            }
        }
        
        // 将班级名列表转换为逗号分隔的字符串
        for (Map.Entry<Long, List<String>> entry : courseClassMap.entrySet()) {
            String names = String.join(",", entry.getValue());
            classNameMap.put(entry.getKey(), names);
        }

        // 组装信息
        for (PracticeRecord record : records) {
            record.setCourseName(courseNameMap.get(record.getCourseId()));
            record.setClassName(classNameMap.get(record.getCourseId()));
            List<QuestionRecord> questions = practiceRecordMapper.findQuestionsBySubmissionId(record.getId());
            if (questions != null && !questions.isEmpty()) {
                record.setQuestions(questions);
            }
        }
        return records;
    }

    // 完成微服务化改造
    @Override
    public byte[] exportRecordsToExcel(Long studentId) {
        List<StudyRecord> studyRecords = getStudyRecords(studentId);
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("学习记录");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("课程");
            headerRow.createCell(1).setCellValue("章节");
            headerRow.createCell(2).setCellValue("资源标题");
            headerRow.createCell(3).setCellValue("学习进度");
            headerRow.createCell(4).setCellValue("观看次数");
            headerRow.createCell(5).setCellValue("最后观看位置(秒)");

            int rowNum = 1;
            for (StudyRecord record : studyRecords) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(record.getCourseName() != null ? record.getCourseName() : "");
                row.createCell(1).setCellValue(record.getSectionTitle() != null ? record.getSectionTitle() : "");
                row.createCell(2).setCellValue(record.getResourceTitle() != null ? record.getResourceTitle() : "");
                row.createCell(3).setCellValue(record.getFormattedProgress());
                row.createCell(4).setCellValue(record.getWatchCount() != null ? record.getWatchCount() : 0);
                row.createCell(5).setCellValue(record.getLastPosition() != null ? record.getLastPosition() : 0);
            }

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    // 完成微服务化改造
    @Override
    public byte[] exportStudyRecordsByCourseToExcel(Long studentId, Long courseId) {
        System.out.println("导出指定课程学习记录，studentId=" + studentId + ", courseId=" + courseId);
        List<StudyRecord> studyRecords = getStudyRecordsByCourse(studentId, courseId);
        System.out.println("查询到的学习记录数量aaa: " + (studyRecords != null ? studyRecords.size() : 0));
        if (studyRecords != null && !studyRecords.isEmpty()) {
            System.out.println("第一条记录: " + studyRecords.get(0));
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // 创建学习记录sheet
            Sheet studySheet = workbook.createSheet("学习记录");
            Row headerRow = studySheet.createRow(0);
            headerRow.createCell(0).setCellValue("课程");
            headerRow.createCell(1).setCellValue("章节");
            headerRow.createCell(2).setCellValue("资源标题");
            headerRow.createCell(3).setCellValue("学习进度");
            headerRow.createCell(4).setCellValue("观看次数");
            headerRow.createCell(5).setCellValue("最后观看位置(秒)");
            
            int rowNum = 1;
            for (StudyRecord record : studyRecords) {
                Row row = studySheet.createRow(rowNum++);
                row.createCell(0).setCellValue(record.getCourseName() != null ? record.getCourseName() : "");
                row.createCell(1).setCellValue(record.getSectionTitle() != null ? record.getSectionTitle() : "");
                row.createCell(2).setCellValue(record.getResourceTitle() != null ? record.getResourceTitle() : "");
                row.createCell(3).setCellValue(record.getFormattedProgress());
                row.createCell(4).setCellValue(record.getWatchCount() != null ? record.getWatchCount() : 0);
                row.createCell(5).setCellValue(record.getLastPosition() != null ? record.getLastPosition() : 0);
            }
            
            // 自动调整列宽
            for (int i = 0; i < 6; i++) {
                studySheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    // 完成微服务化改造
    @Override
    public byte[] exportPracticeRecordsToExcel(Long studentId) {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // 获取所有练习记录（包含题目信息）
            List<PracticeRecord> records = getPracticeRecords(studentId);
            // 创建概览sheet
            Sheet sheet = workbook.createSheet("练习记录概览");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("练习标题");
            headerRow.createCell(1).setCellValue("课程名称");
            headerRow.createCell(2).setCellValue("总分");
            headerRow.createCell(3).setCellValue("提交时间");
            // 填充概览数据
            int rowNum = 1;
            for (PracticeRecord record : records) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(record.getPracticeTitle());
                row.createCell(1).setCellValue(record.getCourseName());
                row.createCell(2).setCellValue(record.getScore());
                row.createCell(3).setCellValue(
                        record.getSubmittedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                // 为每个练习创建一个详细sheet
                if (record.getQuestions() != null && !record.getQuestions().isEmpty()) {
                    // 使用练习标题作为sheet名（去除特殊字符）
                    String sheetName = record.getPracticeTitle()
                            .replaceAll("[\\\\/:*?\\[\\]]", "") // 移除Excel不允许的字符
                            .substring(0, Math.min(31, record.getPracticeTitle().length())); // Excel sheet名最大31字符
                    Sheet detailSheet = workbook.createSheet(sheetName);

                    // 创建详细sheet的表头
                    Row detailHeader = detailSheet.createRow(0);
                    detailHeader.createCell(0).setCellValue("题目内容");
                    detailHeader.createCell(1).setCellValue("题目类型");
                    detailHeader.createCell(2).setCellValue("题目选项");
                    detailHeader.createCell(3).setCellValue("我的答案");
                    detailHeader.createCell(4).setCellValue("正确答案");
                    detailHeader.createCell(5).setCellValue("得分");
                    detailHeader.createCell(6).setCellValue("是否正确");
                    detailHeader.createCell(7).setCellValue("解析");

                    // 填充题目详情
                    int detailRowNum = 1;
                    for (QuestionRecord question : record.getQuestions()) {
                        Row detailRow = detailSheet.createRow(detailRowNum++);
                        detailRow.createCell(0).setCellValue(question.getContent());
                        detailRow.createCell(1).setCellValue(question.getType());
                        detailRow.createCell(2).setCellValue(question.getOptions());
                        detailRow.createCell(3).setCellValue(question.getStudentAnswer());
                        detailRow.createCell(4).setCellValue(question.getCorrectAnswer());
                        detailRow.createCell(5).setCellValue(question.getScore());
                        detailRow.createCell(6).setCellValue(question.getIsCorrect() ? "正确" : "错误");
                        detailRow.createCell(7).setCellValue(question.getAnalysis());
                        // 设置自动换行
                        detailRow.setHeight((short) -1); // 自动行高
                    }

                    // 设置列宽和样式
                    detailSheet.setColumnWidth(0, 256 * 50); // 题目内容列���
                    detailSheet.setColumnWidth(2, 256 * 30); // 选项列宽
                    for (int i = 1; i < 8; i++) {
                        if (i != 2) {
                            detailSheet.autoSizeColumn(i);
                        }
                    }
                }
            }

            // 调整概览sheet的列宽
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("生成Excel文件失败", e);
        }
    }

    // 完成微服务化改造
    @Override
    public byte[] exportPracticeRecordsByCourseToExcel(Long studentId, Long courseId) {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // 获取所有练习记录（包含题目信息）
            List<PracticeRecord> records = getPracticeRecordsByCourse(studentId, courseId);
            if (records == null || records.isEmpty()) {
                throw new RuntimeException("没有找到练习记录");
            }

            // 创建概览sheet
            Sheet sheet = workbook.createSheet("练习记录概览");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("练习标题");
            headerRow.createCell(1).setCellValue("课程名称");
            headerRow.createCell(2).setCellValue("总分");
            headerRow.createCell(3).setCellValue("提交时间");
            headerRow.createCell(4).setCellValue("班级");

            // 填充概览数据
            int rowNum = 1;
            Set<String> usedSheetNames = new HashSet<>();
            for (PracticeRecord record : records) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(record.getPracticeTitle());
                row.createCell(1).setCellValue(record.getCourseName());
                row.createCell(2).setCellValue(record.getScore());
                row.createCell(3).setCellValue(record.getSubmittedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                row.createCell(4).setCellValue(record.getClassName());

                // 为每个练习创建一个详细sheet
                if (record.getQuestions() != null && !record.getQuestions().isEmpty()) {
                    // 使用练习标题作为sheet名（去除特殊字符）
                    String baseSheetName = record.getPracticeTitle().replaceAll("[\\\\/:*?\\[\\]]", "");
                    String sheetName = baseSheetName;
                    int idx = 1;
                    // 保证sheet名唯一
                    while (usedSheetNames.contains(sheetName) || workbook.getSheet(sheetName) != null) {
                        sheetName = baseSheetName + "_" + idx;
                        idx++;
                    }
                    usedSheetNames.add(sheetName);
                    Sheet detailSheet = workbook.createSheet(sheetName);

                    // 创建详细sheet的表头
                    Row detailHeader = detailSheet.createRow(0);
                    detailHeader.createCell(0).setCellValue("题目内容");
                    detailHeader.createCell(1).setCellValue("题目类型");
                    detailHeader.createCell(2).setCellValue("题目选项");
                    detailHeader.createCell(3).setCellValue("我的答案");
                    detailHeader.createCell(4).setCellValue("正确答案");
                    detailHeader.createCell(5).setCellValue("得分");
                    detailHeader.createCell(6).setCellValue("是否正确");
                    detailHeader.createCell(7).setCellValue("解析");

                    // 填充题目详情
                    int detailRowNum = 1;
                    for (QuestionRecord q : record.getQuestions()) {
                        Row detailRow = detailSheet.createRow(detailRowNum++);
                        detailRow.createCell(0).setCellValue(q.getContent());
                        detailRow.createCell(1).setCellValue(q.getType());
                        detailRow.createCell(2).setCellValue(q.getOptions());
                        detailRow.createCell(3).setCellValue(q.getStudentAnswer());
                        detailRow.createCell(4).setCellValue(q.getCorrectAnswer());
                        detailRow.createCell(5).setCellValue(q.getScore());
                        detailRow.createCell(6).setCellValue(q.getIsCorrect() ? "是" : "否");
                        detailRow.createCell(7).setCellValue(q.getAnalysis());
                    }
                }
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("导出练习记录失败: " + e.getMessage(), e);
        }
    }

    // 完成微服务化改造
    @Override
    public Map<String, Object> getSubmissionReport(Long submissionId, Long studentId) {
        System.out.println("=== getSubmissionReport 开始 ===");
        System.out.println("submissionId: " + submissionId);
        System.out.println("studentId: " + studentId);

        try {
            Map<String, Object> report = new HashMap<>();

            // 获取提交基本信息
            System.out.println("正在调用 findSubmissionDetail...");
            PracticeRecord submission = practiceRecordMapper.findSubmissionDetail(submissionId, studentId);
            System.out.println("findSubmissionDetail 结果: " + (submission != null ? "找到" : "未找到"));

            if (submission == null) {
                // 记录不存在，返回null
                System.out.println("提交记录不存在，返回null");
                return null;
            }

            // 课程名称
            if (submission.getCourseId() != null) {
                try {
                    System.out.println("正在获取课程信息...");
                    Map<String, Object> course = courseClient.getCourseById(submission.getCourseId());
                    Object dataObj = (course != null) ? course.get("data") : null;
                    Map<String, Object> courseData;
                    if (dataObj instanceof Map<?, ?> m) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> tmp = (Map<String, Object>) m;
                        courseData = tmp;
                    } else {
                        courseData = course;
                    }
                    String courseName = null;
                    if (courseData != null) {
                        Object n = courseData.get("name");
                        if (n != null) courseName = String.valueOf(n);
                    }
                    if (courseName != null && !courseName.isEmpty()) {
                        submission.setCourseName(courseName);
                        System.out.println("课程名称: " + submission.getCourseName());
                    } else {
                        submission.setCourseName("未知课程");
                        System.out.println("课程名称: 未知课程");
                    }
                } catch (Exception e) {
                    System.out.println("获取课程信息失败: " + e.getMessage());
                    submission.setCourseName("未知课程");
                }
            } else {
                submission.setCourseName("未知课程");
                System.out.println("课程ID为空，设置课程名称为: 未知课程");
            }

            // 班级名称
            if (submission.getClassId() != null) {
                try {
                    System.out.println("正在获取班级信息...");
                    Map<String, Object> classInfo = courseClient.getClassById(submission.getClassId());
                    Object dataObj = (classInfo != null) ? classInfo.get("data") : null;
                    Map<String, Object> classData;
                    if (dataObj instanceof Map<?, ?> m) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> tmp = (Map<String, Object>) m;
                        classData = tmp;
                    } else {
                        classData = classInfo;
                    }
                    String className = null;
                    if (classData != null) {
                        Object n = classData.get("name");
                        if (n == null) n = classData.get("className");
                        if (n != null) className = String.valueOf(n);
                    }
                    if (className != null && !className.isEmpty()) {
                        submission.setClassName(className);
                        System.out.println("班级名称: " + submission.getClassName());
                    } else {
                        submission.setClassName("未知班级");
                        System.out.println("班级名称: 未知班级");
                    }
                } catch (Exception e) {
                    System.out.println("获取班级信息失败: " + e.getMessage());
                    submission.setClassName("未知班级");
                }
            } else {
                submission.setClassName("未知班级");
                System.out.println("班级ID为空，设置班级名称为: 未知班级");
            }

            // 写入提交信息
            report.put("submissionInfo", submission);

            // 获取题目和答案信息
            try {
                System.out.println("正在获取题目信息...");
                List<QuestionRecord> questions = practiceRecordMapper.findSubmissionQuestions(submissionId);
                report.put("questions", questions != null ? questions : new ArrayList<>());
                System.out.println("题目数量: " + (questions != null ? questions.size() : 0));
            } catch (Exception e) {
                System.out.println("获取题目信息失败: " + e.getMessage());
                report.put("questions", new ArrayList<>());
            }

            // 获取班级排名
            try {
                System.out.println("正在获取排名信息...");
                int rank = practiceRecordMapper.getSubmissionRank(submissionId, studentId);
                int totalStudents = practiceRecordMapper.getTotalStudentsInPractice(submission.getPracticeId());
                report.put("rank", rank);
                report.put("totalStudents", totalStudents);
                report.put("percentile", totalStudents > 0 ? ((totalStudents - rank) * 100.0) / totalStudents : 0);
                System.out.println("排名: " + rank + ", 总人数: " + totalStudents);
            } catch (Exception e) {
                System.out.println("获取排名信息失败: " + e.getMessage());
                report.put("rank", 0);
                report.put("totalStudents", 0);
                report.put("percentile", 0.0);
            }

            // 获取得分分布
            try {
                System.out.println("正在获取得分分布...");
                List<Map<String, Object>> scoreDistribution = practiceRecordMapper.getSubmissionScoreDistribution(submissionId);
                report.put("scoreDistribution", scoreDistribution != null ? scoreDistribution : new ArrayList<>());
                System.out.println("得分分布数量: " + (scoreDistribution != null ? scoreDistribution.size() : 0));
            } catch (Exception e) {
                System.out.println("获取得分分布失败: " + e.getMessage());
                report.put("scoreDistribution", new ArrayList<>());
            }

            System.out.println("=== getSubmissionReport 成功完成 ===");
            return report;
        } catch (Exception e) {
            // 记录错误日志但不抛出异常，返回null让上层处理
            System.out.println("=== getSubmissionReport 出��异常 ===");
            System.out.println("异常信息: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // 完成微服务化改造
    @Override
    public byte[] generateSubmissionReportPdf(Map<String, Object> reportData) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            // 使用系统字体，确保中文能显���
            String fontPath = "C:/Windows/Fonts/simsun.ttc,0";
            PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
            // 标题
            Paragraph title = new Paragraph("练习报告")
                    .setFont(font)
                    .setFontSize(20);
            document.add(title);

            // 基本信息
            @SuppressWarnings("unchecked")
            PracticeRecord submissionInfo = (PracticeRecord) reportData.get("submissionInfo");

            document.add(new Paragraph("\n练习信息").setFont(font).setFontSize(16));
            document.add(new Paragraph("练习标题：" + submissionInfo.getPracticeTitle()).setFont(font));
            document.add(new Paragraph("课程名称：" + submissionInfo.getCourseName()).setFont(font));
            document.add(new Paragraph("班级：" + submissionInfo.getClassName()).setFont(font));
            document.add(new Paragraph("得分：" + submissionInfo.getScore() + "分").setFont(font));
            document.add(new Paragraph("提交时间：" + submissionInfo.getSubmittedAt()).setFont(font));

            // 排名信息
            document.add(new Paragraph("\n排名信息").setFont(font).setFontSize(16));
            document.add(new Paragraph("班级排名：第" + reportData.get("rank") + "名").setFont(font));
            document.add(new Paragraph("总人数：" + reportData.get("totalStudents") + "人").setFont(font));
            document.add(
                    new Paragraph("超过：" + String.format("%.1f", reportData.get("percentile")) + "%的同学").setFont(font));

            // 分数分布表格
            document.add(new Paragraph("\n分数分布").setFont(font).setFontSize(16));
            Table distributionTable = new Table(new float[] { 150f, 150f, 150f });
            distributionTable.addCell(new Cell().add(new Paragraph("分数段").setFont(font)));
            distributionTable.addCell(new Cell().add(new Paragraph("人数").setFont(font)));
            distributionTable.addCell(new Cell().add(new Paragraph("占比").setFont(font)));

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> distribution = (List<Map<String, Object>>) reportData.get("scoreDistribution");
            for (Map<String, Object> item : distribution) {
                distributionTable
                        .addCell(new Cell().add(new Paragraph(item.get("score_range").toString()).setFont(font)));
                distributionTable.addCell(new Cell().add(new Paragraph(item.get("count").toString()).setFont(font)));
                distributionTable.addCell(new Cell().add(new Paragraph(item.get("percentage") + "%").setFont(font)));
            }
            document.add(distributionTable);

            // 题目详情
            document.add(new Paragraph("\n题目详情").setFont(font).setFontSize(16));
            @SuppressWarnings("unchecked")
            List<QuestionRecord> questions = (List<QuestionRecord>) reportData.get("questions");
            for (int i = 0; i < questions.size(); i++) {
                QuestionRecord question = questions.get(i);
                document.add(new Paragraph("\n第" + (i + 1) + "题").setFont(font).setFontSize(14));
                document.add(new Paragraph("题目内容：" + question.getContent()).setFont(font));
                document.add(new Paragraph("题目类型：" + question.getType()).setFont(font));
                document.add(new Paragraph("选项：" + question.getOptions()).setFont(font));
                document.add(new Paragraph("我的答案：" + question.getStudentAnswer()).setFont(font));
                document.add(new Paragraph("正确答案：" + question.getCorrectAnswer()).setFont(font));
                document.add(new Paragraph("得分：" + question.getScore()).setFont(font));
                document.add(new Paragraph("是否正确：" + (question.getIsCorrect() ? "正确" : "错误")).setFont(font));
                if (question.getAnalysis() != null) {
                    document.add(new Paragraph("解析：" + question.getAnalysis()).setFont(font));
                }
            }

            // 生成时间
            document.add(new Paragraph("\n\n生成时间：" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .setFont(font));

            document.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("生成PDF报告失败", e);
        }
    }
}
