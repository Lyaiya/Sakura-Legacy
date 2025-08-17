package cn.mcmod.sakura.tileentity;

import cn.mcmod.sakura.api.recipes.PotRecipes;
import cn.mcmod.sakura.base.ItemStackHandlerBase;
import cn.mcmod.sakura.base.TileEntityBase;
import cn.mcmod.sakura.base.wrapper.ItemHandlerWrapper;
import cn.mcmod.sakura.block.BlockCampfirePot;
import cn.mcmod.sakura.util.CapabilityUtil;
import cn.mcmod_mmf.mmlib.item.ItemMetaDurability;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
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

public class TileEntityCampfirePot extends TileEntityBase {
    private static final String KEY_ITEMS = "Items";
    private static final String KEY_INPUT_TANK = "InputTank";
    private static final String KEY_BURN_TIME = "BurnTime";
    private static final String KEY_COOK_TIME = "CookTime";

    @Nullable
    private FluidStack liquidForRendering = null;

    private int burnTime;
    private int currentItemBurnTime;
    private int cookTime;
    private int totalCookTime = 200;

    private final CampfirePotItemHandler itemHandler = new CampfirePotItemHandler() {
        @Override
        protected void onContentsChanged(int slot) {
            tryMarkAndNotify();
        }
    };

    private final FluidTank inputTank = new FluidTank(2000) {
        @Override
        protected void onContentsChanged() {
            tryMarkAndNotify();
        }
    };

    public TileEntityCampfirePot() {
        setName("campfirepot");
    }

    public FluidTank getInputTank() {
        return inputTank;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }

    public void addBurnTime(int burnTime) {
        this.burnTime += burnTime;
        tryMarkAndNotify();
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getTotalCookTime() {
        return totalCookTime;
    }

    public void setTotalCookTime(int totalCookTime) {
        this.totalCookTime = totalCookTime;
    }

    // Render only
    @Nullable
    @SideOnly(Side.CLIENT)
    public FluidStack getFluidForRendering(float partialTicks) {
        final FluidStack actual = inputTank.getFluid();
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

    @Override
    public void update() {
        boolean flag = isBurning();
        boolean flag1 = false;

        if (isBurning()) {
            --burnTime;
        }
        if (world.isRemote) return;

        List<ItemStack> inputs = Lists.newArrayList();
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = itemHandler.getStackInSlot(i);
            if (itemStack.isEmpty()) continue;
            inputs.add(itemStack.copy());
        }

        boolean updateCook = false;
        run:
        {
            FluidStack inputTankFluidStack = inputTank.getFluid();
            if (inputTankFluidStack == null) break run;

            final ItemStack output = PotRecipes.INSTANCE.getOutput(inputTankFluidStack, inputs);
            if (output.isEmpty()) break run;

            final ItemStack simulated = itemHandler.insertItem(9, output, true);

            if (simulated.isEmpty() && isBurning()) {
                cookTime += 1;
                updateCook = true;
            } else {
                break run;
            }

            if (cookTime >= totalCookTime) {
                cookTime = 0;
                itemHandler.insertItem(9, output, false);

                // If pot is a recipe that uses a liquid, it consumes only that amount of liquid
                FluidStack inputFluid = PotRecipes.INSTANCE.getInputFluid(inputTankFluidStack, inputs);
                if (inputFluid != null && inputFluid.amount > 0) {
                    inputTank.drain(inputFluid, true);
                }

                ItemStack itemStack;
                Item item;
                for (int i = 0; i < 9; i++) {
                    itemStack = itemHandler.getStackInSlot(i);
                    item = itemStack.getItem();

                    if (item.hasContainerItem(itemStack)) {
                        final ItemStack containerItem = item.getContainerItem(itemStack);
                        
                        if (itemStack.getCount() == 1) {
                            itemHandler.setStackInSlot(i, containerItem.copy());
                        } else {
                            itemHandler.extractItem(i, 1, false);
                        }

                        if (!(item instanceof ItemMetaDurability)) {
                            Block.spawnAsEntity(world, pos, containerItem.copy());
                        }
                    } else {
                        itemHandler.extractItem(i, 1, false);
                    }
                }
                flag1 = true;
            }
        }

        if (!updateCook) {
            cookTime = 0;
        }

        if (flag != isBurning()) {
            flag1 = true;
            world.setBlockState(pos, world.getBlockState(pos)
                    .withProperty(BlockCampfirePot.LIT, false));
        }
        if (flag1) {
            markDirty();
        }
    }

    @SideOnly(Side.CLIENT)
    public int getBurnTimeRemainingScaled(int par1) {
        if (currentItemBurnTime == 0) {
            currentItemBurnTime = 200;
        }

        return burnTime * par1 / currentItemBurnTime;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (CapabilityUtil.isItemHandler(capability)) {
            return itemHandler.hasHandler(facing);
        }

        if (CapabilityUtil.isFluidHandler(capability)) {
            return true;
        }

        return super.hasCapability(capability, facing);
    }

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (CapabilityUtil.isItemHandler(capability)) {
            return itemHandler.getHandler(facing);
        }

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
    protected void onReadFromNBT(NBTTagCompound compound) {
        itemHandler.deserializeNBT(compound.getCompoundTag(KEY_ITEMS));
        inputTank.readFromNBT(compound.getCompoundTag(KEY_INPUT_TANK));
        burnTime = compound.getInteger(KEY_BURN_TIME);
        cookTime = compound.getInteger(KEY_COOK_TIME);
    }

    @Override
    protected void onWriteToNBT(NBTTagCompound compound) {
        compound.setTag(KEY_ITEMS, itemHandler.serializeNBT());
        compound.setTag(KEY_INPUT_TANK, inputTank.writeToNBT(new NBTTagCompound()));
        compound.setInteger(KEY_BURN_TIME, burnTime);
        compound.setInteger(KEY_COOK_TIME, cookTime);
        if (inputTank.getFluid() != null) {
            liquidForRendering = inputTank.getFluid().copy();
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        onWriteToNBT(compound);
        return new SPacketUpdateTileEntity(pos, -999, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        onReadFromNBT(pkt.getNbtCompound());
    }

    private static class CampfirePotItemHandler extends ItemStackHandlerBase {
        private final Supplier<ItemHandlerWrapper> upWrapper = Suppliers.memoize(
                () -> new ItemHandlerWrapper(this, 0)
        );

        private final Supplier<ItemHandlerWrapper> sideWrapper = Suppliers.memoize(
                () -> new ItemHandlerWrapper(this, 1, 2, 3, 4, 5, 6, 7, 8)
        );

        private final Supplier<ItemHandlerWrapper> downWrapper = Suppliers.memoize(
                () -> new ItemHandlerWrapper(this, 9)
        );

        public CampfirePotItemHandler() {
            super(10);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot >= 0 && slot < 9;
        }

        @Nullable
        @Override
        public <T> T getHandler(@Nullable EnumFacing facing) {
            if (facing == null) {
                return CapabilityUtil.castItemHandler(this);
            }
            return switch (facing) {
                case UP -> CapabilityUtil.castItemHandler(upWrapper.get());
                case DOWN -> CapabilityUtil.castItemHandler(downWrapper.get());
                default -> CapabilityUtil.castItemHandler(sideWrapper.get());
            };
        }
    }

}