package cn.mcmod.sakura.inventory;

import cn.mcmod.sakura.tileentity.TileEntityBarrel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBarrel extends Container {
    private final TileEntityBarrel teBarrel;

    private int processTime;

    public ContainerBarrel(InventoryPlayer inventory, TileEntityBarrel te) {
        teBarrel = te;
        int i, j, k;
        for (k = 0; k < 3; ++k) {
            addSlotToContainer(new Slot(te, k, 42, 36 + (k - 1) * 18));
        }
        addSlotToContainer(new Slot(te, 3, 131, 12));
        addSlotToContainer(new Slot(te, 4, 130, 56) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });

        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
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
        listener.sendAllWindowProperties(this, teBarrel);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (IContainerListener listener : listeners) {
            if (processTime != teBarrel.getField(TileEntityBarrel.ID_PROCESS_TIMER)) {
                listener.sendWindowProperty(this, 0, teBarrel.getField(TileEntityBarrel.ID_PROCESS_TIMER));
            }
        }

        processTime = teBarrel.getField(TileEntityBarrel.ID_PROCESS_TIMER);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int value) {
        teBarrel.setField(id, value);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return teBarrel.isUsableByPlayer(player);
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int index) {
        // 0-4: Contain inventory
        // 5-31: Player inventory
        // 32-41: Hot bar in the player inventory

        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if (index >= 0 && index <= 4) {
                if (!mergeItemStack(itemStack1, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemStack1, itemStack);
            } else if (index >= 5) {
                if (index >= 5 && index < 32) {
                    if (!mergeItemStack(itemStack1, 32, 41, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 32 && index < 41 && !mergeItemStack(itemStack1, 5, 32, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(itemStack1, 5, 41, false)) {
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
