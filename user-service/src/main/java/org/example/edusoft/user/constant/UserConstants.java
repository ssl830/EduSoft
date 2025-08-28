package org.example.edusoft.user.constant;

/**
 * 用户服务常量类
 */
public class UserConstants {
    
    // 角色常量
    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_TUTOR = "tutor";
    
    // 密码相关常量
    public static final String PASSWORD_SALT = "edusoft";
    public static final int MIN_PASSWORD_LENGTH = 6;
    
    // 用户ID相关常量
    public static final int MIN_USER_ID_LENGTH = 3;
    public static final int MAX_USER_ID_LENGTH = 15;
    
    // 用户名相关常量
    public static final int MIN_USERNAME_LENGTH = 2;
    public static final int MAX_USERNAME_LENGTH = 50;
    
    // 邮箱相关常量
    public static final int MAX_EMAIL_LENGTH = 100;
    
    // 分页相关常量
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    
    // 缓存相关常量
    public static final String USER_CACHE_PREFIX = "user:";
    public static final int USER_CACHE_TTL = 3600; // 1小时
    
    // 错误码常量
    public static final String ERROR_USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String ERROR_USER_ID_EXISTS = "USER_ID_EXISTS";
    public static final String ERROR_EMAIL_EXISTS = "EMAIL_EXISTS";
    public static final String ERROR_INVALID_PASSWORD = "INVALID_PASSWORD";
    public static final String ERROR_INVALID_ROLE = "INVALID_ROLE";
    
    // 成功消息常量
    public static final String MSG_USER_CREATED = "用户创建成功";
    public static final String MSG_USER_UPDATED = "用户信息更新成功";
    public static final String MSG_USER_DELETED = "用户删除成功";
    public static final String MSG_LOGIN_SUCCESS = "登录成功";
    public static final String MSG_LOGOUT_SUCCESS = "退出登录成功";
}

