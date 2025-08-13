package cn.mcmod.sakura.tileentity;

import cn.mcmod.sakura.api.recipes.LiquidToItemRecipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
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

public class TileEntityFluidOut extends TileEntity implements ITickable, IInventory {
    protected NonNullList<ItemStack> inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);

    private final FluidTank tank = new FluidTank(10000) {
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

    public TileEntityFluidOut() {
    }

    public FluidTank getTank() {
        return tank;
    }

    protected void refresh() {
        if (hasWorld() && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.markAndNotifyBlock(pos, world.getChunk(pos), state, state, 11);
        }
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            drainInput();
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public String getName() {
        return "container.sakura.barrel_out";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return 2;
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
        return index == 0;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank);
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

    private void writePacketNBT(NBTTagCompound cmp) {
        NBTTagCompound tankTag = tank.writeToNBT(new NBTTagCompound());
        ItemStackHelper.saveAllItems(cmp, inventory);
        cmp.setTag("Tank", tankTag);
    }

    private void readPacketNBT(NBTTagCompound cmp) {
        inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(cmp, inventory);
        tank.readFromNBT(cmp.getCompoundTag("Tank"));
    }

    @Override
    public final SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writePacketNBT(tag);
        return new SPacketUpdateTileEntity(pos, -999, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        readPacketNBT(pkt.getNbtCompound());
    }

    private void drainInput() {
        ItemStack itemstack = inventory.get(0);
        ItemStack itemstack2 = inventory.get(1);
        if (getTank() != null) {
            FluidStack fluid = LiquidToItemRecipe.INSTANCE.getResultFluid(getTank().getFluid());
            if (fluid != null) {
                ItemStack itemstack1 = LiquidToItemRecipe.INSTANCE.getResultItemStack(getTank().getFluid(),
                        itemstack);
                if (itemstack1.isEmpty())
                    return;
                if (getTank().getFluid().amount < fluid.amount)
                    return;
                boolean not_full = (itemstack2.getCount() + itemstack1.getCount() <= getInventoryStackLimit()
                        && itemstack2.getCount() + itemstack1.getCount() <= itemstack2.getMaxStackSize());
                if (!not_full)
                    return;
                if (itemstack2.isEmpty()) {
                    inventory.set(1, itemstack1.copy());
                } else if (itemstack2.getItem() == itemstack1.getItem()) {
                    itemstack2.grow(itemstack1.getCount());
                }

                if (!itemstack.getItem().hasContainerItem(itemstack))
                    itemstack.shrink(1);
                else
                    inventory.set(0, new ItemStack(itemstack.getItem().getContainerItem()));

                getTank().drain(fluid, true);
            }
        }
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

}
