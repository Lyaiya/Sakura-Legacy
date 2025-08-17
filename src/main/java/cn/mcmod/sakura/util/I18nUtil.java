package cn.mcmod.sakura.util;

import cn.mcmod.sakura.SakuraMain;
import net.minecraft.client.resources.I18n;

public class I18nUtil {
    private I18nUtil() {
    }

    public static String format(String translateKey, Object... parameters) {
        return I18n.format(SakuraMain.MODID + "." + translateKey, parameters);
    }
}
