package cn.mcmod.sakura.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerUtil {
    private ItemHandlerUtil() {
    }

    public static ActionResult<ItemStack> insertItem(IItemHandler handler, int slot, ItemStack stack, boolean simulate) {
        EnumActionResult result;
        ItemStack value;
        run:
        {
            if (!handler.isItemValid(slot, stack)) {
                result = EnumActionResult.FAIL;
                value = ItemStack.EMPTY;
                break run;
            }
            final ItemStack simulatedStack = handler.insertItem(slot, stack, true);

            if (simulatedStack.isEmpty()) {
                result = EnumActionResult.SUCCESS;
                if (simulate) {
                    value = simulatedStack;
                } else {
                    value = handler.insertItem(slot, stack, false);
                }
                break run;
            }

            int count = stack.getCount();
            int newCount = simulatedStack.getCount();

            if (newCount >= count) {
                result = EnumActionResult.FAIL;
                value = simulatedStack;
                break run;
            }

            result = EnumActionResult.SUCCESS;
            if (simulate) {
                value = simulatedStack;
            } else {
                value = handler.insertItem(slot, stack, false);
            }
        }

        return ActionResult.newResult(result, value);
    }

    public static boolean canExtractItem(IItemHandler handler, int slot, int amount) {
        final ItemStack stack = handler.extractItem(slot, amount, true);
        return !stack.isEmpty();
    }

    public static ItemStack extractAllItem(IItemHandler handler, int slot, boolean simulate) {
        return handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), simulate);
    }
}
