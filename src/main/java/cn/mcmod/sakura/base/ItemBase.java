package cn.mcmod.sakura.base;

import cn.mcmod.sakura.SakuraMain;
import net.minecraft.item.Item;

public class ItemBase extends Item {
    @Override
    public Item setTranslationKey(String key) {
        return super.setTranslationKey(SakuraMain.MODID + "." + key);
    }
}
