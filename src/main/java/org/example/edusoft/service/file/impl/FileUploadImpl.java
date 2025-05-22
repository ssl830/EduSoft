package org.example.edusoft.service.file.impl;

import org.example.edusoft.common.domain.FileBo;
import org.example.edusoft.common.domain.Result;
import org.example.edusoft.mapper.file.FileMapper;
import org.example.edusoft.service.file.FileUpload;
import org.example.edusoft.common.storage.IFileStorageProvider;
import org.example.edusoft.entity.file.FileInfo;
import org.example.edusoft.entity.file.FileType;
import org.example.edusoft.service.file.FolderService;
import org.example.edusoft.utils.file.FileUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileUploadImpl implements FileUpload {

    private final FileMapper fileMapper;
    private final IFileStorageProvider storageProvider;
    private final FolderService folderService;

    @Override
    public Result<?> uploadFile(MultipartFile file, String title, Long courseId, Long sectionId, String visibility, Long uploaderId, String type) {
        // 转换前端传来的type为枚举类，便于之后进行筛选 
        FileType filetype = FileType.getByName(type);

        if("PUBLIC".equals(visibility)){
            // Step 1: 获取该课程下的所有班级 ID
            List<Long> classIds = fileMapper.getAllClassIdsByCourseId(courseId);
            if (CollectionUtils.isEmpty(classIds)) {
                return Result.error("未找到对应班级");
            }

            boolean allSuccess = true;
            StringBuilder failedClasses = new StringBuilder();

            // Step 2: 遍历每个班级，上传文件到其根文件夹
            for (Long classId : classIds) {
                FileInfo rootFolder = fileMapper.getRootFolderByClassId(classId);
                if (rootFolder == null) {
                    // 如果根文件夹不存在，则创建一个新的根文件夹
                    rootFolder = createClassFolder(courseId, classId, uploaderId);
                    if (rootFolder == null) {
                        failedClasses.append("班级 ").append(classId).append(" 根文件夹创建失败；");
                        allSuccess = false;
                        continue;
                    }
                }
                Long targetParentId;
                // 如果 sectionId 为 -1，无章节属性，直接上传到根目录
                if (sectionId == -1) {
                    targetParentId = rootFolder.getId();
                } else {
                    FileInfo sectionFolder = folderService.findSectionFolder(rootFolder.getId(), sectionId);
                    if (sectionFolder == null) {
                        sectionFolder = createSectionFolder(rootFolder.getId(), sectionId, courseId, classId, uploaderId);
                        if (sectionFolder == null) {
                            failedClasses.append("班级 ").append(classId).append(" 章节文件夹创建失败；");
                            allSuccess = false;
                            continue;
                        }
                    }
                    targetParentId = sectionFolder.getId();
                }
                // 这个upload方法需要哪些参数？？还需要查看逻辑再确定
                Result<?> result = upload(file, title, targetParentId, courseId, classId, visibility, sectionId, filetype, uploaderId);
                if (result.getCode() != 200) {
                    failedClasses.append("班级 ").append(classId).append(" 上传失败: ").append(result.getMsg()).append("；");
                    allSuccess = false;
                }
            }

            if (allSuccess) {
                return Result.ok("文件已成功上传至所有班级");
            } else {
                return Result.error("部分班级上传失败：" + failedClasses.toString());
            }

        }else if("CLASS_ONLY".equals(visibility)){
            // Step 1: 获取用户所在课程的班级 ID
            Long userId = uploaderId; // 假设 uploaderId 等同于 userId
            Long classId = fileMapper.getClassIdByUserandCourse(userId, courseId);
            if (classId == null) {
                return Result.error("未找到对应班级");
            }

            // Step 2: 获取该班级的根文件夹
            FileInfo rootFolder = fileMapper.getRootFolderByClassId(classId);
            if (rootFolder == null) {
                // 如果根文件夹不存在，则创建一个新的根文件夹
                rootFolder = createClassFolder(courseId, classId, uploaderId);
                if (rootFolder == null) {
                    return Result.error("班级根文件夹创建失败");
                }
            }

            // Step 3: 查找对应 section 的文件夹
            if(sectionId == -1){
                // 如果 sectionId 为 -1，表示上传到根目录
                return upload(
                    file,
                    title,
                    rootFolder.getId(), // parent_id
                    courseId,
                    classId,
                    visibility,
                    sectionId,
                    filetype,
                    uploaderId
                );
            } else {
                FileInfo sectionFolder = folderService.findSectionFolder(rootFolder.getId(), sectionId);
                if (sectionFolder == null) {
                    sectionFolder = createSectionFolder(rootFolder.getId(), sectionId, courseId, classId, uploaderId);
                    if (sectionFolder == null) {
                        return Result.error("章节文件夹创建失败");
                    }
                }
                return upload(
                    file,
                    title,
                    sectionFolder.getId(), // parent_id
                    courseId,
                    classId,
                    visibility,
                    sectionId,
                    filetype,
                    uploaderId
                );
            }
        }else{
            return Result.error("无效的可见性设置");
        }
        
    }

    

    private FileInfo convertToInfo(FileBo bo, String uniqueName, Long parentId, Long courseId, Long classId, String visibility, Long sectionId, Long uploaderId) {
        FileInfo info = new FileInfo();
        //BeanUtils.copyProperties(info, bo);
        
        info.setName(bo.getName());
        info.setIsDir(false);
        info.setParentId(parentId);
        info.setCourseId(courseId);
        info.setClassId(classId);
        info.setUploaderId(uploaderId);
        info.setVisibility(visibility);
        info.setCreatedAt(bo.getCreatedAt());
        info.setUpdatedAt(bo.getCreatedAt());
        info.setVersion(0);
        info.setFileType(bo.getFileType());
        info.setSectiondirId(sectionId);  // 是班级文件夹中的章节文件夹时，这个值与它之中的文件的 sectionId 相同
        // 如果不是班级章节文件夹，sectionId为-1
        info.setSectionId(sectionId);
        info.setLastVersionId(0L);
        // 在完成了版本管理的功能之后，在插入新的文件node前会做改动
        info.setIsCurrentVersion(true);
        // 同样，完成了版本管理的功能之后，这个值应该需要从upload传入
        info.setFileSize(bo.getFileSize());
        info.setUrl(bo.getUrl());
        info.setObjectName(bo.getFileName());
        
        return info;
    }

    private FileInfo createClassFolder(Long courseId, Long classId, Long uploaderId) {
        String folderName = "课程-" + courseId + "班级-" + classId;
        boolean success = folderService.createFolder(folderName, -1L, null, courseId, classId, uploaderId);
        if (!success) {
            return null;
        }
        return fileMapper.getRootFolderByClassId(classId);
    }

    private FileInfo createSectionFolder(Long parentId, Long sectionId, Long courseId, Long classId, Long uploaderId) {
        String folderName = "章节-" + sectionId;
        boolean success = folderService.createFolder(folderName, sectionId, parentId, courseId, classId, uploaderId);
        if (!success) {
            return null;
        }
        return folderService.findSectionFolder(parentId, sectionId);
    }

    // 5. 上传文件，未来需要融合版本管理上传的逻辑
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<?> upload(MultipartFile file, String title, Long parentId, Long courseId, Long classId, String visibility, Long sectionId, FileType type, Long uploaderId) {
        if (file.isEmpty()) { 
            return Result.error("上传文件为空");
        }

        // 检查父节点是否为目录
        if (parentId != null && !fileMapper.isDir(parentId)) {
            return Result.error("父节点不是文件夹");
        }
        // 如果没有传入文件title，则使用原文件名
        String fileName = file.getOriginalFilename();
        if(!title.isEmpty()) fileName = title;
        
        int flag = 0;
        // 防止前端展示时文件夹内重名，更新文件命名
        String uniqueName = recursionFindName(fileName, fileName, parentId, flag);

        // 上传到OSS
        // 返回了用于文件上传对象库过程管理的数据结构
        FileBo bo = storageProvider.getStorage().upload(file, uniqueName, type);
        int isnewversion = 0; // 是版本管理上传的新版本
        if(flag > 0){
            isnewversion = 1;
        }
        // 获取原始文件名
        String baseName = FileUtil.getFileBaseName(uniqueName);
        // 获取上一个版本的文件 ID
        Long lastVersionId = fileMapper.getLastVersionId(parentId, baseName, uniqueName);

        FileInfo info = convertToInfo(bo, uniqueName, parentId, courseId, classId, visibility, sectionId, uploaderId);
        // 这里应该传入什么信息，后面可能会需要改动
        // 设置版本信息
        if(isnewversion == 1){
            info.setVersion(flag);
            info.setLastVersionId(lastVersionId == null ? 0L : lastVersionId);
            info.setIsCurrentVersion(true); // 新上传的默认为当前版本
        }

        fileMapper.insertNode(info);

        // 如果有旧版本，则更新其为非当前版本
        if (lastVersionId != null && lastVersionId > 0 && isnewversion == 1) {
            FileInfo oldInfo = fileMapper.selectById(lastVersionId);
            if (oldInfo == null) {
                return Result.error("旧版本文件不存在，版本管理出错");
            }
            oldInfo.setIsCurrentVersion(false);
            fileMapper.updateNode(oldInfo);
        }

        return Result.ok("上传成功");
    }

    //  同文件夹中重名文件加标号，用于前端展示，OSS库中会自动生成不重复的文件名，不需要这里处理
    private String recursionFindName(String originalName, String name, Long parentId, int flag) {
        while (fileMapper.existsByNameAndParent(name, parentId)) {
            flag++;
            name = originalName + "(" + flag + ")";
        }
        return flag > 0 ? originalName + "(" + flag + ")" : originalName;
    }

}
