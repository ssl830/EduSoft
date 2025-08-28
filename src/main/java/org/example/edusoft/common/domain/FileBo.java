package org.example.edusoft.common.domain;

import org.example.edusoft.common.constant.CommonConstant;
import org.example.edusoft.entity.file.FileType;
import org.example.edusoft.exception.BusinessException;
import org.example.edusoft.utils.file.FileUtil;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID; 
 

@Data
public class FileBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    private Long id;

    /**
     * 文件路径
     */
    private String url;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件uuid后的新名称
     */
    private String fileName; // 不同1

    /**
     * 文件后缀名
     */
    private String suffix;

    /**
     * 是否图片 0否 1是
     */
    private Boolean isImg;

    /**
     * 是否文件夹 0否 1是
     */
    private Boolean isDir;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件展示类型
     */
    private FileType fileType;

    /**
     * 上传时间
     */
    private Date createdAt;

    /**
     * 父id
     */
    private Long parentId;

    /**
     * 来源
     */
    private String source;

    /**
     * 用户id
     */
    private Long uploaderId;

    /**
     * 重命名的名称值
     */
    private String rename;

    /**
     * 目录id拼接
     */
    private String dirIds;

    public static FileBo build(MultipartFile file, String uniqueName, FileType type) {

        //判断文件是否为空
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        // 获取原始文件名并校验
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BusinessException("文件名称不合法");
        }

        // 判断文件后缀名是否合法
        int dotPos = originalFilename.lastIndexOf(CommonConstant.SUFFIX_SPLIT);
        if (dotPos < 0) {
            throw new BusinessException("文件名称不合法");
        }
        //获取文件大小
        Long size = file.getSize();
        //文件名
        String orgName = file.getOriginalFilename();
        //mysql数据库中文件名，也是前端展示的文件名，不包含后缀，同一个文件夹中是不重复的
        //String name = FileUtil.getFileName(orgName);
        String name = uniqueName;   // 比如“新建(2)”
        //文件后缀名
        String fileExt = FileUtil.getFileSuffix(orgName);
        // 判断是否是合法的文件后缀
        if (!FileUtil.isFileAllowed(fileExt)) {
            throw new BusinessException("文件类型不符合要求");
        }
        // 文件类型
        /* 
        String type;
        if (FileUtil.isCode(fileExt)) {
            type = "code";
        } else {
            type = fileExt;
        }
        */
        //生成新的文件名，用于不重复地存储在OSS库中，包含后缀
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
        // 生成上传文件对象时的文件数据对象，保持单一职责
        FileBo fileBo = new FileBo();
        fileBo.setSuffix(fileExt);
        fileBo.setFileSize(size);
        // 设置对象库中的不重复的文件名，随机生成的文件名，包含后缀
        fileBo.setFileName(fileName);
        // 经过service层处理后的文件名，前端展示的文件名
        fileBo.setName(name);
        fileBo.setIsImg(FileUtil.isImg(fileExt));
        fileBo.setIsDir(Boolean.FALSE);
        fileBo.setCreatedAt(new Date());
        fileBo.setFileType(type);
        return fileBo;
    }
}
