package cn.mcmod.sakura.tileentity;

import cn.mcmod.sakura.block.BlockCampfire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class TileEntityCampfire extends TileEntity implements ITickable {
    private static final String KEY_BURN_TIME = "BurnTime";
    private static final String KEY_COOK_TIME = "CookTime";
    private static final String KEY_INVENTORY = "Inventory";

    private int burnTime;
    /**
     * The number of ticks that a fresh copy of the currently-burning item would keep the furnace burning for
     */
    private int cookTime;

    private final ItemStackHandler inventory = new ItemStackHandler() {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return FurnaceRecipes.instance().getSmeltingResult(stack).getItem() instanceof ItemFood;
        }

        @Override
        protected void onContentsChanged(int slot) {
            refresh();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 16;
        }
    };

    public TileEntityCampfire() {
    }

    private void refresh() {
        if (hasWorld() && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.markAndNotifyBlock(pos, world.getChunk(pos), state, state, 11);
        }
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    public void setBurningTime(int tick) {
        burnTime = tick;
    }

    public int getBurningTime() {
        return burnTime;
    }

    public int getCookTime() {
        return cookTime;
    }

    public ItemStack getItemBurning() {
        return inventory.getStackInSlot(0);
    }

    @Override
    public void update() {
        boolean flag = isBurning();
        boolean flag1 = false;

        if (isBurning()) {
            --burnTime;
        }
        if (!world.isRemote) {
            // check can cook
            if (isBurning()) {
                ItemStack itemStackBurning = getItemBurning();
                ItemStack resultItemStack = FurnaceRecipes.instance().getSmeltingResult(itemStackBurning);
                if (!itemStackBurning.isEmpty() && !(resultItemStack.isEmpty())) {
                    ++cookTime;
                    if (cookTime >= 700) {
                        inventory.setStackInSlot(0, new ItemStack(resultItemStack.getItem(), itemStackBurning.getCount(), resultItemStack.getMetadata()));

                        cookTime = 0;
                        flag1 = true;
                    }
                } else {
                    cookTime = 0;
                }
            }

            if (flag != isBurning()) {
                flag1 = true;

                BlockCampfire.setState(isBurning(), world, pos);
            }
        }

        if (flag1) {
            markDirty();
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) inventory : super.getCapability(capability, facing);
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
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readPacketNBT(compound);
    }

    private void writePacketNBT(NBTTagCompound cmp) {
        cmp.setInteger(KEY_BURN_TIME, burnTime);
        cmp.setInteger(KEY_COOK_TIME, cookTime);
        cmp.setTag(KEY_INVENTORY, inventory.serializeNBT());
    }

    private void readPacketNBT(NBTTagCompound cmp) {
        burnTime = cmp.getInteger(KEY_BURN_TIME);
        cookTime = cmp.getInteger(KEY_COOK_TIME);
        inventory.deserializeNBT(cmp.getCompoundTag(KEY_INVENTORY));
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writePacketNBT(tag);
        return new SPacketUpdateTileEntity(pos, -999, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        readPacketNBT(pkt.getNbtCompound());
    }
}