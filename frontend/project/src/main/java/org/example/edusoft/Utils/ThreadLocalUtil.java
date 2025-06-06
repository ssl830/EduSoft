package com.bugvictims.demo11.Utils;

public class ThreadLocalUtil {
    //提供一个ThreadLocal对象
    private static final ThreadLocal THREAD_LOCAL = new ThreadLocal();

    //根据键获取值
    public static <T> T get() {
        return (T) THREAD_LOCAL.get();
    }

    //存储键值对
    public static void set(Object value) {
        THREAD_LOCAL.set(value);
    }

    //清除键值对
    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
