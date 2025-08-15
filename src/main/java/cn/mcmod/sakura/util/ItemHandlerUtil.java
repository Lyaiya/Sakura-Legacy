package cn.mcmod.sakura.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerUtil {
    private ItemHandlerUtil() {
    }

    public static boolean canInsertItem(IItemHandler handler, int slot, ItemStack stack) {
        return handler.insertItem(slot, stack, true).isEmpty();
    }

    public static boolean canExtractItem(IItemHandler handler, int slot, int amount) {
        return !handler.extractItem(slot, amount, true).isEmpty();
    }
}
