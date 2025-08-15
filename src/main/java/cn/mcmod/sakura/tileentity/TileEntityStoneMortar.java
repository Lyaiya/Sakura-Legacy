package cn.mcmod.sakura.tileentity;

import cn.mcmod.sakura.api.recipes.MortarRecipes;
import cn.mcmod.sakura.base.TileEntityBase;
import cn.mcmod.sakura.base.wrapper.ItemHandlerWrapper;
import cn.mcmod.sakura.inventory.ContainerStoneMortar;
import cn.mcmod.sakura.util.CapabilityUtil;
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
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileEntityStoneMortar extends TileEntityBase {
    private static final String KEY_INVENTORY = "Inventory";
    private static final String KEY_PROCESS_TIME = "ProcessTime";
    private static final String KEY_TOTAL_PROCESS_TIME = "ProcessTimeTotal";

    private int processTime = 0;
    private int totalProcessTime = 200;

    private final ItemStackHandler itemStackHandler = new ItemStackHandler(6) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case 0, 1, 2, 3 -> true;
                case 4, 5 -> false;
                default -> false;
            };
        }
    };

    private final ItemHandlerWrapper inputWrapper = new ItemHandlerWrapper(itemStackHandler, 0, 1, 2, 3);
    private final ItemHandlerWrapper outputWrapper = new ItemHandlerWrapper(itemStackHandler, 4, 5);

    public TileEntityStoneMortar() {
        super("stonemortar");
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
            ItemStack itemStack = itemStackHandler.getStackInSlot(i);
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
            if (!itemStackHandler.insertItem(4, output1, true).isEmpty()) {
                break run;
            }

            if (output2 != null) {
                if (!itemStackHandler.insertItem(5, output2, true).isEmpty()) {
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

        itemStackHandler.insertItem(4, output1, false);
        if (output2 != null) {
            itemStackHandler.insertItem(5, output2, false);
        }

        itemStackHandler.extractItem(0, 1, false);
        itemStackHandler.extractItem(1, 1, false);
        itemStackHandler.extractItem(2, 1, false);
        itemStackHandler.extractItem(3, 1, false);

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
        itemStackHandler.deserializeNBT(compound.getCompoundTag(KEY_INVENTORY));
        totalProcessTime = compound.getInteger(KEY_TOTAL_PROCESS_TIME);
        processTime = compound.getInteger(KEY_PROCESS_TIME);
    }

    @Override
    protected void onWriteToNBT(NBTTagCompound compound) {
        compound.setTag(KEY_INVENTORY, itemStackHandler.serializeNBT());
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
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (CapabilityUtil.isItemHandler(capability)) {
            if (facing == null) {
                return CapabilityUtil.castItemHandler(itemStackHandler);
            }

            if (facing == EnumFacing.DOWN) {
                return CapabilityUtil.castItemHandler(outputWrapper);
            } else {
                return CapabilityUtil.castItemHandler(inputWrapper);
            }
        }
        return super.getCapability(capability, facing);
    }
}