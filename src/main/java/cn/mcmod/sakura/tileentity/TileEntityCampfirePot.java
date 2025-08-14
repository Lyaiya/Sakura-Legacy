package cn.mcmod.sakura.tileentity;

import cn.mcmod.sakura.api.recipes.PotRecipes;
import cn.mcmod.sakura.block.BlockCampfirePot;
import cn.mcmod_mmf.mmlib.item.ItemMetaDurability;
import cn.mcmod_mmf.mmlib.util.RecipesUtil;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileEntityCampfirePot extends TileEntity implements ITickable, IInventory {
    public static final int ID_BURN_TIME = 0;
    public static final int ID_COOK_TIME = 1;
    public static final int ID_MAX_COOK_TIMER = 2;

    private static final String KEY_BURN_TIME = "BurnTime";
    private static final String KEY_COOK_TIME = "CookTime";
    private static final String KEY_TANK = "Tank";

    private NonNullList<ItemStack> inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);

    @Nullable
    private FluidStack liquidForRendering = null;

    private int burnTime;
    /**
     * The number of ticks that a fresh copy of the currently-burning item would keep the furnace burning for
     */
    private int currentItemBurnTime;
    private int cookTime;
    private int maxCookTimer = 200;

    private final FluidTank tank = new FluidTank(2000) {
        @Override
        protected void onContentsChanged() {
            refresh();
        }
    };

    public TileEntityCampfirePot() {
    }

    public FluidTank getTank() {
        return tank;
    }

    // Render only
    @Nullable
    @SideOnly(Side.CLIENT)
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
        return burnTime > 0;
    }

    private void refresh() {
        if (hasWorld() && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.markAndNotifyBlock(pos, world.getChunk(pos), state, state, 11);
        }
    }

    @Override
    public void update() {
        boolean flag = isBurning();
        boolean flag1 = false;

        if (isBurning()) {
            --burnTime;
        }
        // check can cook
        if (!world.isRemote) {
            List<ItemStack> inventoryList = Lists.newArrayList();
            for (int i = 0; i < 9; i++) {
                if (!inventory.get(i).isEmpty()) {
                    inventoryList.add(inventory.get(i).copy());
                }
            }
            ItemStack itemstack = inventory.get(9);
            FluidStack tankFluid = tank.getFluid();
            if (tankFluid != null && isRecipes(tankFluid, inventoryList)) {
                ItemStack result = PotRecipes.INSTANCE.getResultItemStack(tankFluid, inventoryList);
                FluidStack fluidStack = PotRecipes.INSTANCE.getResultFluid(tankFluid, inventoryList);
                if (RecipesUtil.getInstance().canIncrease(result, itemstack) && isBurning()) {
                    cookTime += 1;
                } else {
                    cookTime = 0;
                }

                if (cookTime >= maxCookTimer) {
                    cookTime = 0;
                    if (itemstack.isEmpty()) {
                        inventory.set(9, result.copy());
                    } else if (itemstack.isItemEqual(result)) {
                        itemstack.grow(result.getCount());
                    }

                    // If pot is a recipe that uses a liquid, it consumes only that amount of liquid
                    if (fluidStack != null && fluidStack.amount > 0) {
                        tank.drain(fluidStack, true);
                    }

                    ItemStack itemStack;
                    Item item;
                    for (int i = 0; i < 9; i++) {
                        itemStack = inventory.get(i);
                        item = itemStack.getItem();
                        if (!(item.getContainerItem(itemStack).isEmpty())) {
                            if (itemStack.getCount() == 1) {
                                inventory.set(i, item.getContainerItem(itemStack).copy());
                            } else {
                                decrStackSize(i, 1);
                            }

                            if (!(item instanceof ItemMetaDurability)) {
                                Block.spawnAsEntity(getWorld(), getPos(), item.getContainerItem(itemStack.copy()));
                            }
                        } else {
                            decrStackSize(i, 1);
                        }
                    }
                    flag1 = true;
                }
            } else {
                cookTime = 0;
            }
            if (flag != isBurning()) {
                flag1 = true;
                BlockCampfirePot.setState(isBurning(), world, pos);
            }
            if (flag1) {
                markDirty();
            }
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public String getName() {
        return "container.sakura.campfirepot";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return 10;
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
        return index < 9;
    }

    @Override
    public int getField(int id) {
        return switch (id) {
            case ID_BURN_TIME -> burnTime;
            case ID_COOK_TIME -> cookTime;
            case ID_MAX_COOK_TIMER -> maxCookTimer;
            default -> 0;
        };
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case ID_BURN_TIME:
                burnTime = value;
                break;
            case ID_COOK_TIME:
                cookTime = value;
                break;
            case ID_MAX_COOK_TIMER:
                maxCookTimer = value;
                break;
        }
    }

    @Override
    public int getFieldCount() {
        return 3;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    @SideOnly(Side.CLIENT)
    public int getBurnTimeRemainingScaled(int par1) {
        if (currentItemBurnTime == 0) {
            currentItemBurnTime = 200;
        }

        return burnTime * par1 / currentItemBurnTime;
    }

    private boolean isRecipes(FluidStack fluid, List<ItemStack> items) {
        ItemStack result = PotRecipes.INSTANCE.getResultItemStack(fluid, items);
        return !result.isEmpty();
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
        ItemStackHelper.saveAllItems(cmp, inventory);
        cmp.setInteger(KEY_BURN_TIME, burnTime);
        cmp.setInteger(KEY_COOK_TIME, cookTime);
        NBTTagCompound tankTag = tank.writeToNBT(new NBTTagCompound());
        cmp.setTag(KEY_TANK, tankTag);
        if (tank.getFluid() != null) {
            liquidForRendering = tank.getFluid().copy();
        }
    }

    private void readPacketNBT(NBTTagCompound cmp) {
        inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(cmp, inventory);
        burnTime = cmp.getInteger(KEY_BURN_TIME);
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