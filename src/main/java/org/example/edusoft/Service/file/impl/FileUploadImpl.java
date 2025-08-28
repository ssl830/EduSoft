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
import org.example.edusoft.ai.AIServiceClient;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@RequiredArgsConstructor
public class FileUploadImpl implements FileUpload {

    private final FileMapper fileMapper;
    private final IFileStorageProvider storageProvider;
    private final FolderService folderService;
    
    @Autowired
    private AIServiceClient aiServiceClient;

    @Override
    public Result<?> uploadFile(MultipartFile file, String title, Long courseId, Long sectionId, String visibility, Long uploaderId, String type) {
        return uploadFile(file, title, courseId, sectionId, visibility, uploaderId, type, false);
    }
    
    @Override
    public Result<?> uploadFile(MultipartFile file, String title, Long courseId, Long sectionId, String visibility, Long uploaderId, String type, boolean uploadToKnowledgeBase) {
        // 转换前端传来的type为枚举类，便于之后进行筛选 
        FileType filetype = FileType.getByName(type);
        Result<?> result = null;

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
                System.out.println("uploaderId: " + uploaderId);
                Result<?> uploadResult = upload(file, title, targetParentId, courseId, classId, visibility, sectionId, filetype, uploaderId);
                if (uploadResult.getCode() != 200) {
                    failedClasses.append("班级 ").append(classId).append(" 上传失败: ").append(uploadResult.getMsg()).append("；");
                    allSuccess = false;
                }
            }

            if (!allSuccess) {
                return Result.error("部分班级上传失败: " + failedClasses);
            }
            
            result = Result.ok("上传成功");

        } else if("CLASS_ONLY".equals(visibility)) {
            // 从请求参数中获取 classId，必须传递
            Long classId = null;
            try {
                // 这里使用根据课程ID和用户ID获取班级ID的方法，确保用户在该班级中
                classId = fileMapper.getClassIdByUserandCourse(uploaderId, courseId);
                if (classId == null) {
                    // 尝试获取课程的默认班级
                    List<Long> classIds = fileMapper.getAllClassIdsByCourseId(courseId);
                    if (!CollectionUtils.isEmpty(classIds)) {
                        classId = classIds.get(0);
                    }
                }
            } catch (Exception e) {
                return Result.error("获取班级失败: " + e.getMessage());
            }

            if (classId == null) {
                return Result.error("未指定班级");
            }

            // 获取班级的根文件夹
            FileInfo rootFolder = fileMapper.getRootFolderByClassId(classId);
            if (rootFolder == null) {
                // 如果根文件夹不存在，则创建一个新的根文件夹
                rootFolder = createClassFolder(courseId, classId, uploaderId);
                if (rootFolder == null) {
                    return Result.error("班级根文件夹创建失败");
                }
            }

            if(sectionId == -1){
                // 如果 sectionId 为 -1，表示上传到根目录
                System.out.println("uploaderId: " + uploaderId);
                result = upload(
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
                result = upload(
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
        } else {
            return Result.error("无效的可见性设置");
        }
        
        // 如果请求指定了上传到知识库，则执行同步
        if (result != null && result.getCode() == 200 && uploadToKnowledgeBase) {
            syncToKnowledgeBaseIfRequired(file, courseId, uploadToKnowledgeBase);
        }
        
        return result != null ? result : Result.error("上传失败");
    }

    /*  移除重复的重载方法，避免编译冲突  */

    

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
        System.out.println("uploaderId: " + uploaderId);
        // 检查父节点是否为目录
        if (parentId != null && !fileMapper.isDir(parentId)) {
            return Result.error("父节点不是文件夹");
        }
        // 如果没有传入文件title，则使用原文件名
        String fileName = file.getOriginalFilename();
        if(!title.isEmpty()) fileName = title;
        
        int flag = 0;
        // 防止前端展示时文件夹内重名，更新文件命名
        NameResult nr = recursionFindName(fileName, fileName, parentId, flag);

        String uniqueName = nr.getName();
        flag = nr.getCount();

        // 上传到OSS
        // 返回了用于文件上传对象库过程管理的数据结构
        FileBo bo = storageProvider.getStorage().upload(file, uniqueName, type);
        int isnewversion = 0; // 不是版本管理上传的新版本
        if(flag > 0){
            isnewversion = 1; // 是版本管理上传的新版本
        }
        // 获取原始文件名
        String baseName = FileUtil.getFileBaseName(uniqueName);
        System.out.println("baseName: " + baseName);
        System.out.println("uniqueName: " + uniqueName);
        System.out.println("parentId: " + parentId);
        
        // 获取上一个版本的文件 ID
        Long lastVersionId = null;
        if (flag > 0) {  // 只有在是新版本的情况下才查找上一个版本
            lastVersionId = fileMapper.getLastVersionId(parentId, baseName, uniqueName);
            System.out.println("lastVersionId: " + lastVersionId);
        }
        
        FileInfo info = convertToInfo(bo, uniqueName, parentId, courseId, classId, visibility, sectionId, uploaderId);
        
        // 设置版本信息
        if (flag > 0) {  // 是新版本
            info.setVersion(flag);
            info.setLastVersionId(lastVersionId == null ? 0L : lastVersionId);
            info.setIsCurrentVersion(true);
        } else {  // 不是新版本
            info.setVersion(0);
            info.setLastVersionId(0L);
            info.setIsCurrentVersion(true);
        }

        fileMapper.insertNode(info);

        // 如果有旧版本，则更新其为非当前版本
        if (lastVersionId != null && lastVersionId > 0) {
            FileInfo oldInfo = fileMapper.selectById(lastVersionId);
            if (oldInfo == null) {
                return Result.error("旧版本文件不存在，版本管理出错");
            }
            oldInfo.setIsCurrentVersion(false);
            System.out.println("Found old version: " + oldInfo.getName() + ", setting is_current_version to false");
            fileMapper.updateNode(oldInfo);
        }

        return Result.ok("上传成功");
    }

    //  同文件夹中重名文件加标号，用于前端展示，OSS库中会自动生成不重复的文件名，不需要这里处理
    private NameResult recursionFindName(String originalName, String name, Long parentId, int flag) {
        while (fileMapper.existsByNameAndParent(name, parentId)) {
            flag++;
            name = originalName + "(" + flag + ")";
        }
        //return flag > 0 ? originalName + "(" + flag + ")" : originalName;
        return new NameResult(name, flag);
    }

    // 在上传文件成功后，如有需要，同步到知识库
    private void syncToKnowledgeBaseIfRequired(MultipartFile file, Long courseId, boolean uploadToKnowledgeBase) {
        if (uploadToKnowledgeBase && courseId != null) {
            try {
                String courseIdStr = String.valueOf(courseId);
                aiServiceClient.uploadMaterial(file, courseIdStr);
            } catch (Exception e) {
                // 记录错误但不影响主流程
                System.err.println("同步到知识库失败: " + e.getMessage());
            }
        }
    }

}
