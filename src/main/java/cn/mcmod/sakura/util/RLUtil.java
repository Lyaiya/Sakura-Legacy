package cn.mcmod.sakura.util;

import cn.mcmod.sakura.SakuraMain;
import net.minecraft.util.ResourceLocation;

public class RLUtil {
    private RLUtil() {
    }

    public static ResourceLocation of(String path) {
        return new ResourceLocation(SakuraMain.MODID, path);
    }

    public static ResourceLocation of(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }
}
