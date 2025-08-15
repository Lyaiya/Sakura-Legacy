package cn.mcmod.sakura.base.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.ArrayUtils;

public class ItemHandlerWrapper implements IItemHandler {
    private final IItemHandler handler;
    private final int[] slots;

    public ItemHandlerWrapper(IItemHandler handler, int... slots) {
        this.handler = handler;
        this.slots = slots;
    }

    @Override
    public int getSlots() {
        return handler.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return handler.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (isSlotInvalid(slot)) return stack;
        return handler.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (isSlotInvalid(slot)) return ItemStack.EMPTY;
        return handler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (isSlotInvalid(slot)) return false;
        return handler.isItemValid(slot, stack);
    }

    private boolean isSlotInvalid(int slot) {
        return !ArrayUtils.contains(this.slots, slot);
    }
}
