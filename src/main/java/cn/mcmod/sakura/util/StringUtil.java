package cn.mcmod.sakura.util;

import org.jetbrains.annotations.Nullable;

public class StringUtil {
    private StringUtil() {
    }

    public static boolean isNullOrEmpty(@Nullable String str) {
        return str == null || str.isEmpty();
    }
}
