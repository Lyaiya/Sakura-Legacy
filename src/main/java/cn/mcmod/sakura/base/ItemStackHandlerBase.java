package cn.mcmod.sakura.base;

import cn.mcmod.sakura.util.CapabilityUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class ItemStackHandlerBase extends ItemStackHandler {
    public ItemStackHandlerBase() {
    }

    public ItemStackHandlerBase(int size) {
        super(size);
    }

    public ItemStackHandlerBase(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    public boolean hasHandler(@Nullable EnumFacing facing) {
        return true;
    }

    @Nullable
    public <T> T getHandler(@Nullable EnumFacing facing) {
        return CapabilityUtil.castItemHandler(this);
    }
}
