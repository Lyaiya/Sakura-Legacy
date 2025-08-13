package cn.mcmod.sakura.tileentity;

import cn.mcmod.sakura.block.BlockLoader;
import cn.mcmod.sakura.block.BlockMapleSpile;
import cn.mcmod.sakura.item.ItemLoader;
import cn.mcmod.sakura.util.HeatUtil;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

public class TileEntityMapleCauldron extends TileEntity implements ITickable, IInventory {
    public static final int ID_MAPLE_TIME = 0;
    public static final int ID_COOK_TIME = 1;

    private static final String KEY_MAPLE_TIME = "MapleTime";
    private static final String KEY_COOK_TIME = "CookTime";
    private static final String KEY_TANK = "Tank";

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);

    @Nullable
    private FluidStack liquidForRendering = null;
    /**
     * The number of ticks that a fresh copy of the currently-burning item would keep the furnace burning for
     */
    private int cookTime;
    private int mapleTime;

    public FluidTank tank = new FluidTank(5000) {
        @Override
        protected void onContentsChanged() {
            refresh();
        }
    };

    public TileEntityMapleCauldron() {
    }

    public FluidTank getTank() {
        return tank;
    }

    // Render only
    @SideOnly(Side.CLIENT)
    @Nullable
    public FluidStack getFluidForRendering(float partialTicks) {
        final FluidStack actual = tank.getFluid();
        int actualAmount;
        if (actual != null && !actual.equals(liquidForRendering)) {
            liquidForRendering = new FluidStack(actual, 0);
        }

        if (liquidForRendering == null) {
            return null;
        }

        actualAmount = actual == null ? 0 : actual.amount;
        int delta = actualAmount - liquidForRendering.amount;
        if (Math.abs(delta) <= 40) {
            liquidForRendering.amount = actualAmount;
        } else {
            int i = (int) (delta * partialTicks * 0.1);
            if (i == 0) {
                i = delta > 0 ? 1 : -1;
            }
            liquidForRendering.amount += i;
        }
        if (liquidForRendering.amount == 0) {
            liquidForRendering = null;
        }
        return liquidForRendering;
    }

    public boolean isBurning() {
        return tank.canDrainFluidType(new FluidStack(BlockLoader.MAPLE_SYRUP_FLUID, 500))
                && tank.getFluidAmount() >= 500
                && HeatUtil.getHeatStrength(getWorld(), getPos()) > 0;
    }

    public boolean canDraw() {
        if (getWorld().getBlockState(getPos().up()).getBlock() instanceof BlockMapleSpile) {
            return BlockMapleSpile.canWork(getWorld(), getPos().up(), getWorld().getBlockState(getPos().up()));
        }
        return false;
    }

    public int getCookTime() {
        return cookTime;
    }

    public int getMapleTime() {
        return mapleTime;
    }

    private void refresh() {
        if (hasWorld() && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.markAndNotifyBlock(pos, world.getChunk(pos), state, state, 11);
        }
    }

    @Override
    public void update() {
        // check can cook
        if (!world.isRemote) {
            drawing();
            cooking();
        }
    }

    private void drawing() {
        boolean flag = canDraw();
        boolean flag1 = false;

        if (canDraw()) {
            mapleTime += getWorld().rand.nextInt(9) + 1;
        }
        if (mapleTime >= 20) {
            mapleTime = 0;
            if (tank.canFill()) {
                tank.fill(new FluidStack(BlockLoader.MAPLE_SYRUP_FLUID, 10), true);
            }
            flag1 = true;
        }
        if (flag != canDraw()) {
            flag1 = true;
        }
        if (flag1) {
            markDirty();
        }
    }

    private void cooking() {
        boolean flag = isBurning();
        boolean flag1 = false;
        ItemStack itemstack = inventory.get(0);
        if (isBurning() && (itemstack.getCount() < itemstack.getMaxStackSize())) {
            cookTime += 1;
        }
        if (cookTime >= 1200) {
            cookTime = 0;

            if (itemstack.isEmpty()) {
                inventory.set(0, new ItemStack(ItemLoader.MATERIAL, 8, 49).copy());
            } else {
                itemstack.grow(8);
            }
            tank.drainInternal(500, true);
            flag1 = true;
        }
        if (flag != isBurning()) {
            flag1 = true;
        }
        if (flag1) {
            markDirty();
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public String getName() {
        return "container.sakura.maple_cauldron";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return 1;
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
        return false;
    }

    @Override
    public int getField(int id) {
        return switch (id) {
            case ID_MAPLE_TIME -> mapleTime;
            case ID_COOK_TIME -> cookTime;
            default -> 0;
        };
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case ID_MAPLE_TIME:
                mapleTime = value;
                break;
            case ID_COOK_TIME:
                cookTime = value;
                break;
        }
    }

    @Override
    public int getFieldCount() {
        return 2;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
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
    public NBTTagCompound writeToNBT(NBTTagCompound par1nbtTagCompound) {
        NBTTagCompound ret = super.writeToNBT(par1nbtTagCompound);
        writePacketNBT(ret);
        return ret;
    }

    @Override
    public final NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound par1nbtTagCompound) {
        super.readFromNBT(par1nbtTagCompound);
        readPacketNBT(par1nbtTagCompound);
    }

    private void writePacketNBT(NBTTagCompound cmp) {
        ItemStackHelper.saveAllItems(cmp, inventory);
        cmp.setInteger(KEY_MAPLE_TIME, mapleTime);
        cmp.setInteger(KEY_COOK_TIME, cookTime);
        NBTTagCompound tankTag = tank.writeToNBT(new NBTTagCompound());
        cmp.setTag(KEY_TANK, tankTag);
        if (tank.getFluid() != null) {
            liquidForRendering = tank.getFluid().copy();
        }
    }

    private void readPacketNBT(NBTTagCompound cmp) {
        inventory.clear();
        ItemStackHelper.loadAllItems(cmp, inventory);
        mapleTime = cmp.getInteger(KEY_MAPLE_TIME);
        cookTime = cmp.getInteger(KEY_COOK_TIME);
        tank.readFromNBT(cmp.getCompoundTag(KEY_TANK));
    }

    @Override
    public final SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writePacketNBT(tag);
        return new SPacketUpdateTileEntity(pos, -999, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        readPacketNBT(packet.getNbtCompound());
    }

}