package cn.mcmod.sakura.inventory;

import cn.mcmod.sakura.tileentity.TileEntityStoneMortar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerStoneMortar extends Container {
    private final TileEntityStoneMortar teStoneMortar;

    private int processTime;
    private int maxProcessTime;

    public ContainerStoneMortar(InventoryPlayer inventory, TileEntityStoneMortar te) {
        teStoneMortar = te;
        addSlotToContainer(new Slot(te, 0, 40, 26));
        addSlotToContainer(new Slot(te, 1, 58, 26));
        addSlotToContainer(new Slot(te, 2, 40, 44));
        addSlotToContainer(new Slot(te, 3, 58, 44));
        addSlotToContainer(new Slot(te, 4, 108, 37) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });
        addSlotToContainer(new Slot(te, 5, 132, 37) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });
        int i;

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, teStoneMortar);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (IContainerListener listener : listeners) {
            if (processTime != teStoneMortar.getField(TileEntityStoneMortar.ID_PROCESS_TIMER)) {
                listener.sendWindowProperty(this, 0, teStoneMortar.getField(TileEntityStoneMortar.ID_PROCESS_TIMER));
            }

            if (maxProcessTime != teStoneMortar.getField(TileEntityStoneMortar.ID_MAX_PROCESS_TIMER)) {
                listener.sendWindowProperty(this, 1, teStoneMortar.getField(TileEntityStoneMortar.ID_MAX_PROCESS_TIMER));
            }
        }

        processTime = teStoneMortar.getField(TileEntityStoneMortar.ID_PROCESS_TIMER);
        maxProcessTime = teStoneMortar.getField(TileEntityStoneMortar.ID_MAX_PROCESS_TIMER);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        teStoneMortar.setField(id, value);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return teStoneMortar.isUsableByPlayer(player);
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int index) {
        // 0-5: Contain inventory
        // 6-32: Player inventory
        // 33-42: Hot bar in the player inventory

        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if (index >= 0 && index <= 5) {
                if (!mergeItemStack(itemStack1, 6, 42, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemStack1, itemStack);
            } else if (index >= 6) {
                if (index >= 6 && index < 33) {
                    if (!mergeItemStack(itemStack1, 33, 42, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 33 && index < 42 && !mergeItemStack(itemStack1, 6, 32, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(itemStack1, 6, 42, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemStack1.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(par1EntityPlayer, itemStack1);
        }

        return itemStack;
    }
}
