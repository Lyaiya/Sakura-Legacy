package cn.mcmod.sakura.util;

import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class FluidStackUtil {
    private FluidStackUtil() {
    }

    public static boolean isEmpty(@Nullable FluidStack fluidStack) {
        return fluidStack == null;
    }
}
