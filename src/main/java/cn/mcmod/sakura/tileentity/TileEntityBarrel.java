package cn.mcmod.sakura.tileentity;

import cn.mcmod.sakura.api.recipes.BarrelRecipes;
import cn.mcmod.sakura.api.recipes.LiquidToItemRecipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.jetbrains.annotations.Nullable;

public class TileEntityBarrel extends TileEntity implements ITickable, ISidedInventory {
    private static final String KEY_TANK = "Tank";
    private static final String KEY_RESULT_TANK = "ResultTank";
    private static final String KEY_PROCESS_TIMER = "processTimer";

    public static final int ID_PROCESS_TIMER = 1;

    private final int[] SLOTS_INPUTS = {0, 1, 2, 3};
    private final int[] SLOTS_OUTPUTS = {4};

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);

    private int processTimer = 0;

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

    public TileEntityBarrel() {
    }

    public int getProcessTimer() {
        return processTimer;
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
        drainInput();

        FluidStack inputFluidStack = inputTank.getFluid();
        if (inputFluidStack == null) return;

        ItemStack[] inputItemStacks = new ItemStack[]{inventory.get(0), inventory.get(1), inventory.get(2)};

        FluidStack result = BarrelRecipes.INSTANCE.getResultFluidStack(inputFluidStack, inputItemStacks);
        if (result == null) return;
        FluidStack fluidStack = BarrelRecipes.INSTANCE.getFluidStack(inputFluidStack);
        final FluidStack outputFluidStack = outputTank.getFluid();
        if (outputFluidStack == null
                || outputTank.canFill()
                && result.getFluid().equals(outputFluidStack.getFluid())
        ) {
            processTimer += 1;
        } else {
            processTimer = 0;
        }
        if (processTimer >= 1200) {
            processTimer = 0;
            outputTank.fill(result, true);

            // If pot is a recipe that uses a liquid, it consumes
            // only that amount of liquid
            if (fluidStack != null && fluidStack.amount > 0) {
                inputTank.drain(fluidStack, true);
            }

            ItemStack itemStack;
            for (int i = 0; i < 3; i++) {
                itemStack = inventory.get(i);
                if (itemStack.getCount() == 1) {
                    inventory.set(i, itemStack.getItem().getContainerItem(itemStack).copy());
                } else {
                    decrStackSize(i, 1);
                }
            }
            markDirty();
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public String getName() {
        return "container.sakura.barrel";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return 5;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : inventory) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = ItemStackHelper.getAndSplit(inventory, index, count);

        if (!itemstack.isEmpty()) {
            markDirty();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(inventory, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.set(index, stack);
        if (stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (world.getTileEntity(pos) != this) {
            return false;
        }
        return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
        markDirty();
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        markDirty();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index < 3;
    }

    @Override
    public int getField(int id) {
        if (id == ID_PROCESS_TIMER) {
            return processTimer;
        }
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        if (id == ID_PROCESS_TIMER) {
            processTimer = value;
        }
    }

    @Override
    public int getFieldCount() {
        return 1;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return switch (side) {
            case DOWN -> SLOTS_OUTPUTS;
            case UP -> SLOTS_INPUTS;
            default -> new int[0];
        };
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        switch (index) {
            case 0, 1, 2, 3:
                return true;
            case 4:
                return false;
        }
        return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }

    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(inputTank);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound ret = super.writeToNBT(compound);
        writePacketNBT(ret);
        return ret;
    }

    @Override
    public final NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readPacketNBT(compound);
    }

    private void writePacketNBT(NBTTagCompound compound) {
        NBTTagCompound tankTag = inputTank.writeToNBT(new NBTTagCompound());
        ItemStackHelper.saveAllItems(compound, inventory);

        compound.setTag(KEY_TANK, tankTag);
        NBTTagCompound resultTankTag = outputTank.writeToNBT(new NBTTagCompound());
        compound.setTag(KEY_RESULT_TANK, resultTankTag);
        compound.setInteger(KEY_PROCESS_TIMER, processTimer);
    }

    private void readPacketNBT(NBTTagCompound compound) {
        inventory.clear();
        ItemStackHelper.loadAllItems(compound, inventory);

        processTimer = compound.getInteger(KEY_PROCESS_TIMER);

        inputTank.readFromNBT(compound.getCompoundTag(KEY_TANK));
        outputTank.readFromNBT(compound.getCompoundTag(KEY_RESULT_TANK));
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

    private void drainInput() {
        final FluidStack resultTankFluid = outputTank.getFluid();
        if (resultTankFluid == null) return;

        ItemStack itemStack3 = inventory.get(3);
        ItemStack itemStack4 = inventory.get(4);

        FluidStack resultFluid = LiquidToItemRecipe.INSTANCE.getResultFluid(resultTankFluid);
        if (resultFluid == null) return;

        ItemStack resultItemStack = LiquidToItemRecipe.INSTANCE.getResultItemStack(resultTankFluid, itemStack3);
        if (resultItemStack.isEmpty()) return;
        if (resultTankFluid.amount < resultFluid.amount) return;

        boolean notFull = (itemStack4.getCount() + resultItemStack.getCount() <= getInventoryStackLimit()
                && itemStack4.getCount() + resultItemStack.getCount() <= itemStack4.getMaxStackSize());
        if (!notFull) return;

        if (itemStack4.isEmpty()) {
            inventory.set(4, resultItemStack.copy());
        } else if (itemStack4.getItem() == resultItemStack.getItem()) {
            itemStack4.grow(resultItemStack.getCount());
        }

        final Item item3 = itemStack3.getItem();
        if (item3.hasContainerItem(itemStack3)) {
            inventory.set(3, new ItemStack(item3.getContainerItem()));
        } else {
            itemStack3.shrink(1);
        }

        outputTank.drain(resultFluid, true);
    }

}
