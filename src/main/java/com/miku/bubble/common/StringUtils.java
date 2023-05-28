package com.miku.bubble.common;

import java.util.UUID;

/**
 * @author gmj23
 */
public class StringUtils {
        /**
     * 获取随机字符串
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        return UUID.randomUUID().toString().
                replaceAll("-", "").substring(0, length);
    }

    public static String getRandomString() {
        return UUID.randomUUID().toString().
                replaceAll("-", "");
    }

}
