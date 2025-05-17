package org.example.edusoft.service.file;

import org.example.edusoft.common.domain.Dtree;
import org.example.edusoft.common.domain.Result;
import org.example.edusoft.entity.file.FileInfo;
import org.example.edusoft.common.dto.UpdateFileNameDTO;
// import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 文件服务接口
 *
 * @Author: hao.ding@insentek.com
 * @Date: 2024/6/7 11:09
 */
public interface FileService {
    /**
     * 获取用户所有可见的文件（按班级划分）
     */
    List<FileInfo> getAllFilesByUserId(Long userId);

    /**
     * 获取用户在某个课程下的文件
     */
    List<FileInfo> getFilesByUserandCourse(Long userId, Long courseId);

    /**
     * 获取某个文件夹下的内容
     */
    List<FileInfo> getChildrenFiles(Long folderId);

    /**
     * 下载文件或文件夹（如果是文件夹则打包成 ZIP）
     */
    void downloadFileOrFolder(Long fileId, HttpServletResponse response);

    public Result<?> uploadFile(Long courseId, Long sectionId, Long uploaderId, MultipartFile file, String visibility);
    public FileInfo findSectionFolder(Long parentId, Long sectionId);
    /**
     * 上传文件
     */
    Result<?> upload(MultipartFile file, Long parentId, Long courseId, Long classId, String visibility);

    /**
     * 创建文件夹
     */
    public boolean createFolder(String name, Long parentId, Long courseId, Long classId, Long uploaderId);
}
