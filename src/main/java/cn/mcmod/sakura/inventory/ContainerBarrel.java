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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerBarrel extends Container {
    private static final int ID_PROCESS_TIME = 0;

    private final TileEntityBarrel teBarrel;

    private int processTime;

    public ContainerBarrel(InventoryPlayer inventory, TileEntityBarrel te) {
        teBarrel = te;

        final IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        for (int i = 0; i < 3; ++i) {
            addSlotToContainer(new SlotItemHandler(itemHandler, i, 42, 36 + (i - 1) * 18));
        }
        addSlotToContainer(new SlotItemHandler(itemHandler, 3, 131, 12));
        addSlotToContainer(new SlotItemHandler(itemHandler, 4, 130, 56) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, ID_PROCESS_TIME, teBarrel.getProcessTime());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (IContainerListener listener : listeners) {
            if (processTime != teBarrel.getProcessTime()) {
                listener.sendWindowProperty(this, ID_PROCESS_TIME, teBarrel.getProcessTime());
            }
        }

        processTime = teBarrel.getProcessTime();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int value) {
        if (id == ID_PROCESS_TIME) {
            teBarrel.setProcessTime(value);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return ContainerUtil.canInteractWith(player, teBarrel);
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
