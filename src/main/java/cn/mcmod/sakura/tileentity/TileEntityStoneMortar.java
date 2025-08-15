package cn.mcmod.sakura.tileentity;

import cn.mcmod.sakura.api.recipes.MortarRecipes;
import cn.mcmod.sakura.base.ItemStackHandlerBase;
import cn.mcmod.sakura.base.TileEntityBase;
import cn.mcmod.sakura.base.wrapper.ItemHandlerWrapper;
import cn.mcmod.sakura.inventory.ContainerStoneMortar;
import cn.mcmod.sakura.util.CapabilityUtil;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileEntityStoneMortar extends TileEntityBase {
    private static final String KEY_ITEMS = "Items";
    private static final String KEY_PROCESS_TIME = "ProcessTime";
    private static final String KEY_TOTAL_PROCESS_TIME = "ProcessTimeTotal";

    private int processTime = 0;
    private int totalProcessTime = 200;

    private final MyItemHandler itemHandler = new MyItemHandler();

    public TileEntityStoneMortar() {
        setName("stonemortar");
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

    public void setTotalProcessTime(int totalProcessTime) {
        this.totalProcessTime = totalProcessTime;
    }

    @Override
    public void update() {
        if (world.isRemote) return;

        final List<ItemStack> inputs = Lists.newArrayList();
        for (int i = 0; i < 4; i++) {
            ItemStack itemStack = itemHandler.getStackInSlot(i);
            if (itemStack.isEmpty()) continue;
            inputs.add(itemStack.copy());
        }

        final ItemStack[] output = MortarRecipes.INSTANCE.getOutput(inputs);

        if (output.length == 0) {
            processTime = 0;
            return;
        }

        final ItemStack output1 = output[0];
        final ItemStack output2;
        if (output.length == 2) {
            output2 = output[1];
        } else {
            output2 = null;
        }

        boolean canInsert = false;

        run:
        {
            if (!itemHandler.insertItem(4, output1, true).isEmpty()) {
                break run;
            }

            if (output2 != null) {
                if (!itemHandler.insertItem(5, output2, true).isEmpty()) {
                    break run;
                }
            }

            canInsert = true;
        }
        if (canInsert) {
            processTime++;
        } else {
            processTime = 0;
        }

        if (processTime < totalProcessTime) return;
        processTime = 0;

        itemHandler.insertItem(4, output1, false);
        if (output2 != null) {
            itemHandler.insertItem(5, output2, false);
        }

        itemHandler.extractItem(0, 1, false);
        itemHandler.extractItem(1, 1, false);
        itemHandler.extractItem(2, 1, false);
        itemHandler.extractItem(3, 1, false);

        markDirty();
    }

    private void refresh() {
        if (hasWorld() && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.markAndNotifyBlock(pos, world.getChunk(pos), state, state, 11);
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    protected void onReadFromNBT(NBTTagCompound compound) {
        itemHandler.deserializeNBT(compound.getCompoundTag(KEY_ITEMS));
        totalProcessTime = compound.getInteger(KEY_TOTAL_PROCESS_TIME);
        processTime = compound.getInteger(KEY_PROCESS_TIME);
    }

    @Override
    protected void onWriteToNBT(NBTTagCompound compound) {
        compound.setTag(KEY_ITEMS, itemHandler.serializeNBT());
        compound.setInteger(KEY_TOTAL_PROCESS_TIME, totalProcessTime);
        compound.setInteger(KEY_PROCESS_TIME, processTime);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerStoneMortar(playerInventory, this);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (CapabilityUtil.isItemHandler(capability)) {
            return itemHandler.hasHandler(facing);
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (CapabilityUtil.isItemHandler(capability)) {
            return itemHandler.getHandler(facing);
        }
        return super.getCapability(capability, facing);
    }

    private static class MyItemHandler extends ItemStackHandlerBase {
        private final Supplier<ItemHandlerWrapper> inputWrapper = Suppliers.memoize(
                () -> new ItemHandlerWrapper(this, 0, 1, 2, 3)
        );

        private final Supplier<ItemHandlerWrapper> outputWrapper = Suppliers.memoize(
                () -> new ItemHandlerWrapper(this, 4, 5)
        );

        public MyItemHandler() {
            super(6);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case 0, 1, 2, 3 -> true;
                case 4, 5 -> false;
                default -> false;
            };
        }

        @Nullable
        @Override
        public <T> T getHandler(@Nullable EnumFacing facing) {
            if (facing == null) {
                return CapabilityUtil.castItemHandler(this);
            }

            if (facing == EnumFacing.DOWN) {
                return CapabilityUtil.castItemHandler(outputWrapper.get());
            } else {
                return CapabilityUtil.castItemHandler(inputWrapper.get());
            }
        }
    }
}