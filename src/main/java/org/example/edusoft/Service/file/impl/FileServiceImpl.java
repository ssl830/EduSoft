package org.example.edusoft.Service.file.impl;

import cn.dev33.satoken.stp.StpUtil;
import org.example.edusoft.common.constant.CommonConstant;
import org.example.edusoft.common.domain.FileBo;
import org.example.edusoft.common.domain.Result;
import org.example.edusoft.exception.BusinessException;
import org.example.edusoft.common.storage.IFileStorage;
import org.example.edusoft.common.storage.IFileStorageProvider;
import org.example.edusoft.entity.file.FileInfo;
import org.example.edusoft.common.dto.UpdateFileNameDTO;
import org.example.edusoft.Mapper.file.FileMapper;
import org.example.edusoft.Service.file.FileService;
import org.example.edusoft.common.domain.Dtree;
import org.apache.ibatis.annotations.Param;
// import org.apache.tomcat.jni.FileInfo;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
// import com.mybatisflex.core.query.QueryWrapper;
// import com.mybatisflex.spring.service.impl.ServiceImpl;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import java.io.ByteArrayOutputStream;

// import static com.free.fs.core.domain.table.FileInfoTableDef.FILE_INFO;
// import static com.mybatisflex.core.query.QueryMethods.noCondition;

/**
 * 文件管理接口实现
 *
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileMapper fileMapper;
    private final IFileStorageProvider storageProvider;

    // 1. 获取用户所有文件
    @Override
    public List<FileInfo> getAllFilesByUserId(Long userId) {
        return fileMapper.getRootFoldersByUserId(userId)
                .stream()
                .flatMap(folder -> fileMapper.getAllNodesUnder(folder.getId()).stream())
                .collect(Collectors.toList());
    }

    // 2. 获取用户在某课程下的文件
    @Override
    public List<FileInfo> getFilesByUserAndCourse(Long userId, Long courseId) {
        FileInfo root = fileMapper.getRootFolderByCourseAndClass(courseId, userId);
        if (root == null) return Collections.emptyList();
        return fileMapper.getAllNodesUnder(root.getId());
    }

    // 3. 获取某个文件夹下的内容
    @Override
    public List<FileInfo> getChildrenFiles(Long folderId) {
        FileInfo folder = fileMapper.selectById(folderId);
        if (folder == null || !folder.getIsDir()) {
            throw new IllegalArgumentException("无效的文件夹ID");
        }
        return fileMapper.getChildren(folderId);
    }

    // 4. 下载文件或文件夹
    @Override
    public void downloadFileOrFolder(Long fileId, HttpServletResponse response) {
        try {
            FileInfo fileInfo = fileMapper.selectById(fileId);
            if (fileInfo == null) {
                throw new IllegalArgumentException("文件不存在");
            }

            IFileStorage storage = storageProvider.getStorage();

            
            if (!fileInfo.getIsDir()) {
                // 下载单个文件
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileInfo.getName());
                storage.download(fileInfo.getUrl(), response);
            } else {
                // 下载整个文件夹（打包为ZIP）
                List<FileInfo> files = fileMapper.getAllNodesUnder(fileId);
                String zipName = fileInfo.getName() + ".zip";
                response.setContentType("application/zip");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + zipName + "\"");

                try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
                    for (FileInfo f : files) {
                        if (!f.getIsDir()) {
                            String relativePath = f.getPath().replaceFirst("/" + fileId, "") + "/" + f.getName();
                            zos.putNextEntry(new ZipEntry(relativePath));

                            // 使用 ByteArrayOutputStream 缓冲下载内容
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            storage.download(f.getUrl(), baos); // 假设 IFileStorage 支持 OutputStream
                            byte[] bytes = baos.toByteArray();
                            zos.write(bytes, 0, bytes.length);

                            zos.closeEntry();
                        }
                    }
                }
            }
        } catch (IOException e) {
            // 可记录日志
            throw new RuntimeException("文件下载失败", e);
        }
    }

    // 5. 上传文件
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<?> upload(MultipartFile file, Long parentId, Long courseId, Long classId, String visibility) {
        if (file.isEmpty()) {
            return Result.error("上传文件为空");
        }

        // 检查父节点是否为目录
        if (parentId != null && !fileMapper.isDir(parentId)) {
            return Result.error("父节点不是文件夹");
        }

        String fileName = file.getOriginalFilename();
        int flag = 0;
        String uniqueName = recursionFindName(fileName, fileName, parentId, flag);

        // 上传到OSS
        FileBo bo = storageProvider.getStorage().upload(file);
        FileInfo info = new FileInfo();
        BeanUtils.copyProperties(info, bo);
        info.setName(uniqueName);
        info.setIsDir(false);
        info.setParentId(parentId);
        info.setCourseId(courseId);
        info.setClassId(classId);
        info.setUploaderId(StpUtil.getLoginIdAsLong());
        info.setVisibility(visibility);
        info.setCreatedAt(new Date());

        fileMapper.insertNode(info);
        return Result.ok("上传成功");
    }

    // 文件重名检测
    private String recursionFindName(String originalName, String name, Long parentId, int flag) {
        while (fileMapper.existsByNameAndParent(name, parentId)) {
            flag++;
            name = originalName + "(" + flag + ")";
        }
        return flag > 0 ? originalName + "(" + flag + ")" : originalName;
    }

    @Override
    public boolean createFolder(String name, Long parentId, Long courseId, Long classId, Long uploaderId) {
        if (fileMapper.existsByNameAndParent(name, parentId)) {
            throw new RuntimeException("名称已存在");
        }

        FileInfo folder = new FileInfo();
        folder.setName(name);
        folder.setIsDir(true);
        folder.setParentId(parentId);
        folder.setCourseId(courseId);
        folder.setClassId(classId);
        folder.setUploaderId(uploaderId);
        folder.setVisibility("CLASS_ONLY");
        folder.setCreatedAt(new Date());
        fileMapper.insertNode(folder);
        return true;
    }

}

