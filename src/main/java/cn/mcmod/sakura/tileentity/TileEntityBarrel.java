package cn.mcmod.sakura.tileentity;

import cn.mcmod.sakura.api.recipes.BarrelRecipes;
import cn.mcmod.sakura.api.recipes.LiquidToItemRecipes;
import cn.mcmod.sakura.base.ItemStackHandlerWrapper;
import cn.mcmod.sakura.util.FluidStackUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class TileEntityBarrel extends TileEntity implements ITickable {
    private static final String KEY_INVENTORY = "Inventory";
    private static final String KEY_INPUT_TANK = "InputTank";
    private static final String KEY_OUTPUT_TANK = "OutputTank";
    private static final String KEY_PROCESS_TIME = "ProcessTime";
    // TODO: Total Process Time
    private static final String KEY_PROCESS_TIME_TOTAL = "ProcessTimeTotal";

    private int processTime = 0;
    private final int totalProcessTime = 1200;

    private final FluidTank inputTank = new FluidTank(3000) {
        @Override
        protected void onContentsChanged() {
            refresh();
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            if (!canFill() || fluid.getFluid().isGaseous(fluid) || fluid.getFluid().isLighterThanAir()) {
                return false;
            }
            return fluid.getFluid().getTemperature(fluid) < 500;
        }
    };

    private final FluidTank outputTank = new FluidTank(3000) {
        @Override
        protected void onContentsChanged() {
            refresh();
        }
    };

    private final ItemStackHandler itemStackHandler = new ItemStackHandler(5) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case 0, 1, 2 -> true;
                case 3 -> LiquidToItemRecipes.INSTANCE.hasInput(stack);
                default -> false;
            };
        }
    };

    @Nullable
    private ItemStackHandlerWrapper inputWrapper;

    @Nullable
    private ItemStackHandlerWrapper containerWrapper;

    @Nullable
    private ItemStackHandlerWrapper outputWrapper;

    public TileEntityBarrel() {
    }

    public boolean isProcess() {
        return processTime > 0;
    }

    public int getProcessTime() {
        return processTime;
    }

    public void setProcessTime(int processTime) {
        this.processTime = processTime;
    }

    public int getTotalProcessTime() {
        return totalProcessTime;
    }

    public FluidTank getInputTank() {
        return inputTank;
    }

    public FluidTank getOutputTank() {
        return outputTank;
    }

    private void refresh() {
        if (hasWorld() && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.markAndNotifyBlock(pos, world.getChunk(pos), state, state, 11);
        }
    }

    @Override
    public void update() {
        if (world.isRemote) return;
        drainOutputTank();

        FluidStack inputTankFluidStack = inputTank.getFluid();
        if (inputTankFluidStack == null) return;

        ItemStack[] inputItemStacks = new ItemStack[]{
                itemStackHandler.getStackInSlot(0),
                itemStackHandler.getStackInSlot(1),
                itemStackHandler.getStackInSlot(2)};

        FluidStack outputReceipeFluidStack = BarrelRecipes.INSTANCE.getOutput(inputTankFluidStack, inputItemStacks);
        if (outputReceipeFluidStack == null) {
            processTime = 0;
            return;
        }

        final FluidStack outputTankFluidStack = outputTank.getFluid();
        if (outputTankFluidStack == null
                || outputTank.canFill()
                && outputReceipeFluidStack.getFluid().equals(outputTankFluidStack.getFluid())
        ) {
            processTime++;
        } else {
            processTime = 0;
        }

        if (processTime >= totalProcessTime) {
            processTime = 0;
            outputTank.fill(outputReceipeFluidStack, true);

            // If pot is a recipe that uses a liquid, it consumes
            // only that amount of liquid
            FluidStack inputFluidStack = BarrelRecipes.INSTANCE.getInput(inputTankFluidStack);
            if (inputFluidStack != null && inputFluidStack.amount > 0) {
                inputTank.drain(inputFluidStack, true);
            }

            for (int i = 0; i < 3; i++) {
                itemStackHandler.extractItem(i, 1, false);
            }
            markDirty();
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) return true;
            switch (facing) {
                case DOWN, UP, NORTH, SOUTH -> {
                    return true;
                }
                case WEST, EAST -> {
                    return false;
                }
            }
        }

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (facing == EnumFacing.WEST
                    || facing == EnumFacing.EAST) {
                return true;
            }
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);
            }

            switch (facing) {
                case UP -> {
                    return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getContainerWrapper());
                }
                case DOWN -> {
                    return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getOutputWrapper());
                }
                case NORTH, SOUTH -> {
                    return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getInputWrapper());
                }
            }
        }

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (facing == EnumFacing.WEST) {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(outputTank);
            } else if (facing == EnumFacing.EAST) {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(inputTank);
            }
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readPacketNBT(compound);
    }

    private void readPacketNBT(NBTTagCompound compound) {
        itemStackHandler.deserializeNBT(compound.getCompoundTag(KEY_INVENTORY));
        inputTank.readFromNBT(compound.getCompoundTag(KEY_INPUT_TANK));
        outputTank.readFromNBT(compound.getCompoundTag(KEY_OUTPUT_TANK));
        processTime = compound.getInteger(KEY_PROCESS_TIME);
        // totalProcessTime = compound.getInteger(KEY_TOTAL_PROCESS_TIME);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound ret = super.writeToNBT(compound);
        writePacketNBT(ret);
        return ret;
    }

    private void writePacketNBT(NBTTagCompound compound) {
        compound.setTag(KEY_INVENTORY, itemStackHandler.serializeNBT());
        compound.setTag(KEY_INPUT_TANK, inputTank.writeToNBT(new NBTTagCompound()));
        compound.setTag(KEY_OUTPUT_TANK, outputTank.writeToNBT(new NBTTagCompound()));
        compound.setInteger(KEY_PROCESS_TIME, processTime);
        // compound.setInteger(KEY_TOTAL_PROCESS_TIME, totalProcessTime);
    }

    @Override
    public final NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writePacketNBT(tag);
        return new SPacketUpdateTileEntity(pos, -999, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        readPacketNBT(packet.getNbtCompound());
    }

    private void drainOutputTank() {
        final FluidStack outputTankFluidStack = outputTank.getFluid();
        if (FluidStackUtil.isEmpty(outputTankFluidStack)) return;

        final FluidStack inputRecipeFluidStack = LiquidToItemRecipes.INSTANCE.getInput(outputTankFluidStack);
        if (FluidStackUtil.isEmpty(inputRecipeFluidStack)) return;

        // Input
        final ItemStack itemStack3 = itemStackHandler.getStackInSlot(3);
        final ItemStack outputRecipeItemStack = LiquidToItemRecipes.INSTANCE.getOutput(outputTankFluidStack, itemStack3);
        if (outputRecipeItemStack.isEmpty()) return;

        if (outputTankFluidStack.amount < inputRecipeFluidStack.amount) return;

        // Output
        final ItemStack itemStack4 = itemStackHandler.getStackInSlot(4);

        final int count = itemStack4.getCount() + outputRecipeItemStack.getCount();
        boolean notFull = count <= itemStackHandler.getSlotLimit(4)
                && count <= itemStack4.getMaxStackSize();
        if (!notFull) return;

        itemStackHandler.extractItem(3, outputRecipeItemStack.getCount(), false);
        itemStackHandler.insertItem(4, outputRecipeItemStack, false);
        outputTank.drain(inputRecipeFluidStack, true);
    }

    private IItemHandler getInputWrapper() {
        if (inputWrapper == null) {
            inputWrapper = new ItemStackHandlerWrapper(itemStackHandler, 0, 1, 2);
        }
        return inputWrapper;
    }

    private IItemHandler getContainerWrapper() {
        if (containerWrapper == null) {
            containerWrapper = new ItemStackHandlerWrapper(itemStackHandler, 3);
        }
        return containerWrapper;
    }

    private IItemHandler getOutputWrapper() {
        if (outputWrapper == null) {
            outputWrapper = new ItemStackHandlerWrapper(itemStackHandler, 4);
        }
        return outputWrapper;
    }

}
