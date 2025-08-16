package cn.mcmod.sakura.tileentity;

import cn.mcmod.sakura.base.ItemStackHandlerBase;
import cn.mcmod.sakura.base.TileEntityBase;
import cn.mcmod.sakura.block.BlockCampfire;
import cn.mcmod.sakura.util.CapabilityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

public class TileEntityCampfire extends TileEntityBase {
    private static final String KEY_ITEMS = "Items";
    private static final String KEY_BURN_TIME = "BurnTime";
    private static final String KEY_COOK_TIME = "CookTime";

    private int burnTime;
    private int cookTime;
    private final int maxCookTime = 700;

    private final ItemStackHandlerBase itemHandler = new ItemStackHandlerBase() {
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

    public boolean isBurning() {
        return burnTime > 0;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }

    public void addBurnTime(int burnTime) {
        this.burnTime += burnTime;
        markDirty();
        refresh();
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getCookTime() {
        return cookTime;
    }

    public ItemStack getItemBurning() {
        return itemHandler.getStackInSlot(0);
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
                ItemStack burningStack = getItemBurning();
                ItemStack resultStack = FurnaceRecipes.instance().getSmeltingResult(burningStack);
                if (!burningStack.isEmpty() && !resultStack.isEmpty()) {
                    ++cookTime;
                    if (cookTime >= maxCookTime) {
                        // TODO: 是否可以改成直接弹出？
                        itemHandler.setStackInSlot(0, resultStack.copy());

                        cookTime = 0;
                        flag1 = true;
                    }
                } else {
                    cookTime = 0;
                }
            }

            if (flag != isBurning()) {
                flag1 = true;

                world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockCampfire.LIT, false));
            }
        }

        if (flag1) {
            markDirty();
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (CapabilityUtil.isItemHandler(capability)) {
            return itemHandler.hasHandler(facing);
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (CapabilityUtil.isItemHandler(capability)) {
            return itemHandler.getHandler(facing);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    protected void onReadFromNBT(NBTTagCompound compound) {
        itemHandler.deserializeNBT(compound.getCompoundTag(KEY_ITEMS));
        burnTime = compound.getInteger(KEY_BURN_TIME);
        cookTime = compound.getInteger(KEY_COOK_TIME);
    }

    @Override
    protected void onWriteToNBT(NBTTagCompound compound) {
        compound.setTag(KEY_ITEMS, itemHandler.serializeNBT());
        compound.setInteger(KEY_BURN_TIME, burnTime);
        compound.setInteger(KEY_COOK_TIME, cookTime);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        onWriteToNBT(tag);
        return new SPacketUpdateTileEntity(pos, -999, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        onReadFromNBT(pkt.getNbtCompound());
    }
}