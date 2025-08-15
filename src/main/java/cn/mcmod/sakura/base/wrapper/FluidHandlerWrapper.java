package cn.mcmod.sakura.base.wrapper;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.jetbrains.annotations.Nullable;

public class FluidHandlerWrapper implements IFluidHandler {
    private final IFluidHandler handler;
    private final boolean canFill;
    private final boolean canDrain;

    public FluidHandlerWrapper(IFluidHandler handler, boolean canFill, boolean canDrain) {
        this.handler = handler;
        this.canFill = canFill;
        this.canDrain = canDrain;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return handler.getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (!canFill) return 0;
        return handler.fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (!canDrain) return null;
        return handler.drain(resource, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (!canDrain) return null;
        return handler.drain(maxDrain, doDrain);
    }
}
