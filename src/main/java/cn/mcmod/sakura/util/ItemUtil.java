package cn.mcmod.sakura.util;

import cn.mcmod.sakura.SakuraMain;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ItemUtil {
    private ItemUtil() {
    }

    public static void setTranslationKey(Item item, String key) {
        item.setTranslationKey(SakuraMain.MODID + "." + key);
    }

    public static ItemStack copy(ItemStack stack, @Nullable Consumer<ItemStack> consumer) {
        final ItemStack copiedStack = stack.copy();
        if (consumer != null) {
            consumer.accept(copiedStack);
        }
        return copiedStack;
    }

    public static ItemStack copy(ItemStack stack) {
        return copy(stack, null);
    }
}
