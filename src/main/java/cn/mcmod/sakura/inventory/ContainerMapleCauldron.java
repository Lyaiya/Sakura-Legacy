package cn.mcmod.sakura.inventory;

import cn.mcmod.sakura.tileentity.TileEntityMapleCauldron;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerMapleCauldron extends Container {
    private final TileEntityMapleCauldron teMapleCauldron;

    private int cookTime;
    private int mapleTime;

    public ContainerMapleCauldron(InventoryPlayer inventory, TileEntityMapleCauldron te) {
        teMapleCauldron = te;
        addSlotToContainer(new Slot(te, 0, 114, 36) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });
        int i, j;

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
        listener.sendAllWindowProperties(this, teMapleCauldron);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (IContainerListener listener : listeners) {
            if (mapleTime != teMapleCauldron.getField(TileEntityMapleCauldron.ID_MAPLE_TIME)) {
                listener.sendWindowProperty(this, 0, teMapleCauldron.getField(TileEntityMapleCauldron.ID_MAPLE_TIME));
            }

            if (cookTime != teMapleCauldron.getField(TileEntityMapleCauldron.ID_COOK_TIME)) {
                listener.sendWindowProperty(this, 1, teMapleCauldron.getField(TileEntityMapleCauldron.ID_COOK_TIME));
            }
        }

        mapleTime = teMapleCauldron.getField(TileEntityMapleCauldron.ID_MAPLE_TIME);
        cookTime = teMapleCauldron.getField(TileEntityMapleCauldron.ID_COOK_TIME);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        teMapleCauldron.setField(id, value);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return teMapleCauldron.isUsableByPlayer(player);
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int index) {
        // 0: Contain inventory
        // 1-27: Player inventory
        // 28-36: Hot bar in the player inventory

        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if (index == 0) {
                if (!mergeItemStack(itemStack1, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemStack1, itemStack);
            } else if (index >= 1) {
                if (index >= 1 && index < 28) {
                    if (!mergeItemStack(itemStack1, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 28 && index < 37 && !mergeItemStack(itemStack1, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(itemStack1, 1, 37, false)) {
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
