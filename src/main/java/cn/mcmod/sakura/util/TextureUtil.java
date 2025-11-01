package cn.mcmod.sakura.util;

import cn.mcmod.sakura.SakuraMain;

public class TextureUtil {
    private TextureUtil() {
    }

    public static String of(String path) {
        return SakuraMain.MODID + ":textures/" + path;
    }
}
