package cn.mcmod.sakura.inventory;

import cn.mcmod.sakura.base.ContainerBase;
import cn.mcmod.sakura.tileentity.TileEntityStoneMortar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerStoneMortar extends ContainerBase<TileEntityStoneMortar> {
    private static final int ID_PROCESS_TIME = 0;
    private static final int ID_TOTAL_PROCESS_TIME = 1;

    private int processTime;
    private int totalProcessTime;

    public ContainerStoneMortar(InventoryPlayer playerInventory, TileEntityStoneMortar te) {
        super(playerInventory, te);
    }

    @Override
    protected void addSlots(IItemHandler itemHandler) {
        // Input
        addSlotToContainer(new SlotItemHandler(itemHandler, 0, 40, 26));
        addSlotToContainer(new SlotItemHandler(itemHandler, 1, 58, 26));
        addSlotToContainer(new SlotItemHandler(itemHandler, 2, 40, 44));
        addSlotToContainer(new SlotItemHandler(itemHandler, 3, 58, 44));

        // Output
        addSlotToContainer(new SlotItemHandler(itemHandler, 4, 108, 37));
        addSlotToContainer(new SlotItemHandler(itemHandler, 5, 132, 37));
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, ID_PROCESS_TIME, te.getProcessTime());
        listener.sendWindowProperty(this, ID_TOTAL_PROCESS_TIME, te.getTotalProcessTime());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (IContainerListener listener : listeners) {
            if (processTime != te.getProcessTime()) {
                listener.sendWindowProperty(this, ID_PROCESS_TIME, te.getProcessTime());
            }

            if (totalProcessTime != te.getTotalProcessTime()) {
                listener.sendWindowProperty(this, ID_TOTAL_PROCESS_TIME, te.getTotalProcessTime());
            }
        }

        processTime = te.getProcessTime();
        totalProcessTime = te.getTotalProcessTime();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int value) {
        switch (id) {
            case ID_PROCESS_TIME:
                te.setProcessTime(value);
                break;
            case ID_TOTAL_PROCESS_TIME:
                te.setTotalProcessTime(value);
                break;
        }
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
