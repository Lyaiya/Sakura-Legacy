package cn.mcmod.sakura.util;

import cn.mcmod.sakura.SakuraMain;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;

public class I18nUtil {
    private I18nUtil() {
    }

    public static String getKey(String key) {
        return SakuraMain.MODID + "." + key;
    }

    public static String format(String translateKey, Object... parameters) {
        return I18n.format(getKey(translateKey), parameters);
    }

    public static void setTranslationKey(Item item, String key) {
        item.setTranslationKey(getKey(key));
    }
}
