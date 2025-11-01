package cn.mcmod.sakura.item;

import cn.mcmod.sakura.util.I18nUtil;
import net.minecraft.item.Item;

public class ItemPot extends Item {
    public ItemPot() {
        setTranslationKey(I18nUtil.getKey("cooking_pot"));
        setMaxStackSize(1);
    }
}
