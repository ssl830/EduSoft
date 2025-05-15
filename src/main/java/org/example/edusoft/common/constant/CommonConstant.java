package org.example.edusoft.common.constant;


public class CommonConstant {
    /**
     * 路径目录分隔符
     */
    public static final String DIR_SPLIT = "/";

    /**
     * 字符串分隔符
     */
    public static final String STRING_SPLIT = ",";

    /**
     * 后缀分隔符
     */
    public static final String SUFFIX_SPLIT = ".";

    /**
     * 目录默认类型
     */
    public static final String DEFAULT_DIR_TYPE = "dir";

    /**
     * 默认管理员角色code
     */
    public static final String ROLE_ADMIN = "admin";

    /**
     * 默认普通角色code
     */
    public static final String ROLE_USER = "user";

    /**
     * 默认树顶级id
     */
    public static final Long ROOT_PARENT_ID = -1L;

    /**
     * dtree指定图标
     */
    public static final String DTREE_ICON_1 = "dtree-icon-weibiaoti5";

    /**
     * dtree指定图标
     */
    public static final String DTREE_ICON_2 = "dtree-icon-normal-file";

    public static final String X_REQUESTED_WITH = "X-Requested-With";
    public static final String XML_HTTP_REQUEST = "XMLHttpRequest";

    /**
     * 存储类型-本地
     */
    public static final String FILE_TYPE_LOCAL = "local";

    /**
     * 云存储类型-oss
     */
    public static final String FILE_TYPE_OSS = "oss";

    /**
     * 云存储类型-七牛
     */
    public static final String FILE_TYPE_QINIU = "qiniu";

    /**
     * 云存储类型-Minio
     */
    public static final String FILE_TYPE_MINIO = "minio";

    /**
     * 本地目录映射
     */
    public static final String LOCAL_DIRECTORY_MAPPING = "/uploads/";

    /**
     * 需要租户过滤的字段
     */
    public static final String STORAGE_TENANT_COLUMN = "source";
}