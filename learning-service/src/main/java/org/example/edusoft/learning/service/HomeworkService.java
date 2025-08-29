package org.example.edusoft.learning.service;

import org.example.edusoft.learning.dto.HomeworkDTO;
import org.example.edusoft.learning.dto.HomeworkSubmissionDTO;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface HomeworkService {
    Long createHomework(Long classId, String title, String description, 
                       String endTime, MultipartFile file, Long createdBy);

    HomeworkDTO getHomework(Long id);

    List<HomeworkDTO> getHomeworkList(Long classId);

    Long submitHomework(Long homeworkId, Long studentId, MultipartFile file);

    List<HomeworkSubmissionDTO> getSubmissionList(Long homeworkId);

    HomeworkSubmissionDTO getStudentSubmission(Long homeworkId, Long studentId);

    void downloadHomeworkFile(Long homeworkId, HttpServletResponse response);

    void downloadSubmissionFile(Long submissionId, HttpServletResponse response);

    void deleteHomework(Long homeworkId);
}
