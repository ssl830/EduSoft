package org.example.edusoft.service.record.impl;

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
import org.example.edusoft.service.record.RecordService;
import org.example.edusoft.mapper.record.*;
import org.example.edusoft.entity.record.*;
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
import com.itextpdf.io.font.PdfEncodings; // 添加这个导入
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class RecordServiceImpl implements RecordService {

    @Autowired
    private StudyRecordMapper studyRecordMapper;

    @Autowired
    private PracticeRecordMapper practiceRecordMapper;

    @Override
    public List<StudyRecord> getStudyRecords(Long studentId) {
        return studyRecordMapper.findStudyRecords(studentId);
    }

    @Override
    public List<StudyRecord> getStudyRecordsByCourse(Long studentId, Long courseId) {
        return studyRecordMapper.findByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    public List<PracticeRecord> getPracticeRecords(Long studentId) {
        List<PracticeRecord> records = practiceRecordMapper.findPracticeRecords(studentId);
        for (PracticeRecord record : records) {
            List<QuestionRecord> questions = practiceRecordMapper.findQuestionsBySubmissionId(record.getId());
            if (questions != null && !questions.isEmpty()) {
                record.setQuestions(questions);
            }
        }
        return records;
    }

    @Override
    public List<PracticeRecord> getPracticeRecordsByCourse(Long studentId, Long courseId) {
        List<PracticeRecord> records = practiceRecordMapper.findByStudentIdAndCourseId(studentId, courseId);
        if (records != null) {
            for (PracticeRecord record : records) {
                List<QuestionRecord> questions = practiceRecordMapper.findQuestionsBySubmissionId(record.getId());
                if (questions != null && !questions.isEmpty()) {
                    record.setQuestions(questions);
                }
            }
        }
        return records;
    }

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

    @Override
    public byte[] exportStudyRecordsByCourseToExcel(Long studentId, Long courseId) {
        List<StudyRecord> studyRecords = getStudyRecordsByCourse(studentId, courseId);
        System.out.println("查询到的学习记录数量: " + (studyRecords != null ? studyRecords.size() : 0));
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
                    detailSheet.setColumnWidth(0, 256 * 50); // 题目内容列宽
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

    @Override
    public Map<String, Object> getSubmissionReport(Long submissionId, Long studentId) {
        Map<String, Object> report = new HashMap<>();
        // 获取提交基本信息
        PracticeRecord submission = practiceRecordMapper.findSubmissionDetail(submissionId, studentId);
        if (submission == null) {
            return null;
        }
        report.put("submissionInfo", submission);
        // 获取题目和答案信息
        List<QuestionRecord> questions = practiceRecordMapper.findSubmissionQuestions(submissionId);
        report.put("questions", questions);
        // 获取班级排名
        int rank = practiceRecordMapper.getSubmissionRank(submissionId, studentId);
        int totalStudents = practiceRecordMapper.getTotalStudentsInPractice(submission.getPracticeId());
        report.put("rank", rank);
        report.put("totalStudents", totalStudents);
        report.put("percentile", ((totalStudents - rank) * 100.0) / totalStudents);

        // 获取得分分布
        List<Map<String, Object>> scoreDistribution = practiceRecordMapper.getSubmissionScoreDistribution(submissionId);
        report.put("scoreDistribution", scoreDistribution);
        return report;
    }

    @Override
    public byte[] generateSubmissionReportPdf(Map<String, Object> reportData) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            // 使用系统字体，确保中文能显示
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