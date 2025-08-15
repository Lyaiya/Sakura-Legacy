package cn.mcmod.sakura.inventory;

import cn.mcmod.sakura.base.ContainerBase;
import cn.mcmod.sakura.tileentity.TileEntityCampfirePot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCampfirePot extends ContainerBase<TileEntityCampfirePot> {
    private static final int ID_BURN_TIME = 0;
    private static final int ID_COOK_TIME = 1;
    private static final int ID_TOTAL_COOK_TIMER = 2;

    private int burnTime;
    private int cookTime;
    private int totalCookTime;

    public ContainerCampfirePot(InventoryPlayer playerInventory, TileEntityCampfirePot te) {
        super(playerInventory, te);
    }

    @Override
    protected void addSlots(IItemHandler itemHandler) {
        addSlotToContainer(new SlotItemHandler(itemHandler, 0, 45, 19));
        for (int i = 1; i < 5; ++i) {
            addSlotToContainer(new SlotItemHandler(itemHandler, i, 18 + (i - 1) * 18, 37));
        }
        for (int i = 5; i < 9; ++i) {
            addSlotToContainer(new SlotItemHandler(itemHandler, i, 18 + (i - 5) * 18, 55));
        }
        addSlotToContainer(new SlotItemHandler(itemHandler, 9, 130, 46));
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, ID_BURN_TIME, te.getBurnTime());
        listener.sendWindowProperty(this, ID_COOK_TIME, te.getCookTime());
        listener.sendWindowProperty(this, ID_TOTAL_COOK_TIMER, te.getTotalCookTime());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (IContainerListener listener : listeners) {
            if (burnTime != te.getBurnTime()) {
                listener.sendWindowProperty(this, ID_BURN_TIME, te.getBurnTime());
            }

            if (cookTime != te.getCookTime()) {
                listener.sendWindowProperty(this, ID_COOK_TIME, te.getCookTime());
            }

            if (totalCookTime != te.getTotalCookTime()) {
                listener.sendWindowProperty(this, ID_TOTAL_COOK_TIMER, te.getTotalCookTime());
            }
        }

        burnTime = te.getBurnTime();
        cookTime = te.getCookTime();
        totalCookTime = te.getTotalCookTime();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int value) {
        switch (id) {
            case ID_BURN_TIME:
                te.setBurnTime(value);
                break;
            case ID_COOK_TIME:
                te.setCookTime(value);
                break;
            case ID_TOTAL_COOK_TIMER:
                te.setTotalCookTime(value);
                break;
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int index) {
        // 0-9: Contain inventory
        // 10-36: Player inventory
        // 37-46: Hot bar in the player inventory

        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if (index >= 0 && index <= 9) {
                if (!mergeItemStack(itemStack1, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemStack1, itemStack);
            } else if (index >= 10) {
                if (index >= 10 && index < 37) {
                    if (!mergeItemStack(itemStack1, 37, 46, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 37 && index < 46 && !mergeItemStack(itemStack1, 10, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(itemStack1, 10, 46, false)) {
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
