package org.example.edusoft.entity.file;

// 这是一个文件类型的枚举类

public enum FileType {
    VIDEO,
    PPT,
    CODE,
    PDF,
    OTHER;

    /**
     * 根据字符串名称获取对应的 FileType 枚举值（忽略大小写）
     * @param name 枚举名称
     * @return 匹配的 FileType，若无匹配则返回 OTHER
     */
    public static FileType getByName(String name) {
        if (name == null || name.isBlank()) {
            return OTHER;
        }
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}
