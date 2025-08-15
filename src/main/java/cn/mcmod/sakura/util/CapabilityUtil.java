package cn.mcmod.sakura.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class CapabilityUtil {
    private CapabilityUtil() {
    }

    // Item Handler

    public static Capability<IItemHandler> itemHandlerCap() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    public static boolean hasItemHandler(TileEntity te, @Nullable EnumFacing facing) {
        return te.hasCapability(itemHandlerCap(), facing);
    }

    @Nullable
    public static IItemHandler getItemHandler(TileEntity te, @Nullable EnumFacing facing) {
        if (!hasItemHandler(te, facing)) return null;
        return te.getCapability(itemHandlerCap(), facing);
    }

    public static boolean isItemHandler(Capability<?> capability) {
        return capability == itemHandlerCap();
    }

    public static <T> T castItemHandler(IItemHandler itemHandler) {
        return itemHandlerCap().cast(itemHandler);
    }

    // Fluid Handler

    public static Capability<IFluidHandler> fluidHandlerCap() {
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    public static boolean hasFluidHandler(TileEntity te, @Nullable EnumFacing facing) {
        return te.hasCapability(fluidHandlerCap(), facing);
    }

    @Nullable
    public static IFluidHandler getFluidHandler(TileEntity te, @Nullable EnumFacing facing) {
        if (!hasFluidHandler(te, facing)) return null;
        return te.getCapability(fluidHandlerCap(), facing);
    }

    public static boolean isFluidHandler(Capability<?> capability) {
        return capability == fluidHandlerCap();
    }

    public static <T> T castFluidHandler(IFluidHandler fluidHandler) {
        return fluidHandlerCap().cast(fluidHandler);
    }
}
