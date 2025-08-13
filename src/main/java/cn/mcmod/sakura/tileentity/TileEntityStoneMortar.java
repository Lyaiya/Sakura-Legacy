package cn.mcmod.sakura.tileentity;

import cn.mcmod.sakura.api.recipes.MortarRecipes;
import cn.mcmod.sakura.inventory.ContainerStoneMortar;
import cn.mcmod_mmf.mmlib.util.RecipesUtil;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileEntityStoneMortar extends TileEntity implements ITickable, ISidedInventory {
    public static final int ID_PROCESS_TIMER = 0;
    public static final int ID_MAX_PROCESS_TIMER = 1;

    private static final String KEY_PROCESS_TIMER = "processTimer";
    private static final String KEY_MAX_PROCESS_TIMER = "maxProcessTimer";

    private static final int[] SLOTS_INPUT = {0, 1, 2, 3};
    private static final int[] SLOTS_OUTPUT = {4, 5};

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);

    private int processTimer = 0;
    private int maxProcessTimer = 200;

    public TileEntityStoneMortar() {
    }

    public int getProcessTimer() {
        return processTimer;
    }

    @Override
    public void update() {
        if (world.isRemote) return;
        ItemStack input1 = inventory.get(0);
        ItemStack input2 = inventory.get(1);
        ItemStack input3 = inventory.get(2);
        ItemStack input4 = inventory.get(3);
        ItemStack output1 = inventory.get(4);
        ItemStack output2 = inventory.get(5);

        List<ItemStack> inventoryList = Lists.newArrayList();
        for (int i = 0; i < 4; i++) {
            if (!inventory.get(i).isEmpty()) {
                inventoryList.add(inventory.get(i).copy());
            }
        }

        ItemStack[] result = MortarRecipes.INSTANCE.getResult(inventoryList);
        if (result.length > 0) {
            if (RecipesUtil.getInstance().canIncrease(result[0], output1)) {
                if (result.length > 1) {
                    if (RecipesUtil.getInstance().canIncrease(result[1], output2)) {
                        processTimer += 1;
                    } else {
                        processTimer = 0;
                    }
                } else {
                    processTimer += 1;
                }
            } else {
                processTimer = 0;
            }
        } else {
            processTimer = 0;
        }

        if (processTimer >= maxProcessTimer) {
            processTimer = 0;

            if (output1.isEmpty()) {
                inventory.set(4, result[0].copy());
            } else if (output1.getItem() == result[0].getItem()) {
                output1.grow(result[0].getCount());
            }
            if (result.length > 1) {
                if (output2.isEmpty()) {
                    inventory.set(5, result[1].copy());
                } else if (output2.getItem() == result[1].getItem()) {
                    output2.grow(result[1].getCount());
                }
            }

            input1.shrink(1);
            input2.shrink(1);
            input3.shrink(1);
            input4.shrink(1);

            markDirty();
        }
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
    public String getName() {
        return "container.sakura.stonemortar";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return 6;
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
        return index < 4;
    }

    @Override
    public int getField(int id) {
        return switch (id) {
            case ID_PROCESS_TIMER -> processTimer;
            case ID_MAX_PROCESS_TIMER -> maxProcessTimer;
            default -> 0;
        };
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case ID_PROCESS_TIMER:
                processTimer = value;
                break;
            case ID_MAX_PROCESS_TIMER:
                maxProcessTimer = value;
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

    @Override
    public int[] getSlotsForFace(EnumFacing enumFacing) {
        return switch (enumFacing) {
            case DOWN -> SLOTS_OUTPUT;
            case UP -> SLOTS_INPUT;
            default -> new int[0];
        };
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemStack, EnumFacing enumFacing) {
        return true;
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemStack, EnumFacing enumFacing) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.clear();
        ItemStackHelper.loadAllItems(compound, inventory);

        maxProcessTimer = compound.getInteger(KEY_MAX_PROCESS_TIMER);
        processTimer = compound.getInteger(KEY_PROCESS_TIMER);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, inventory);
        compound.setInteger(KEY_MAX_PROCESS_TIMER, maxProcessTimer);
        compound.setInteger(KEY_PROCESS_TIMER, processTimer);
        return compound;
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

}