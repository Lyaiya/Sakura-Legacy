package cn.mcmod.sakura.inventory;

import cn.mcmod.sakura.tileentity.TileEntityCampfirePot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCampfirePot extends Container {
    private final TileEntityCampfirePot teCampfirePot;

    private int processTime;
    private int maxProcessTime;
    private int burnTime;

    public ContainerCampfirePot(InventoryPlayer inventory, TileEntityCampfirePot te) {
        teCampfirePot = te;
        addSlotToContainer(new Slot(te, 0, 45, 19));
        int i, j, k, l;
        for (k = 1; k < 5; ++k) {
            addSlotToContainer(new Slot(te, k, 18 + (k - 1) * 18, 37));
        }
        for (l = 5; l < 9; ++l) {
            addSlotToContainer(new Slot(te, l, 18 + (l - 5) * 18, 55));
        }
        addSlotToContainer(new Slot(te, 9, 130, 46) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });

        for (i = 0; i < 3; ++i)
            for (j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        for (i = 0; i < 9; ++i)
            addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.teCampfirePot);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (IContainerListener icontainerlistener : this.listeners) {
            if (this.burnTime != this.teCampfirePot.getField(TileEntityCampfirePot.ID_BURN_TIME)) {
                icontainerlistener.sendWindowProperty(this, 0, this.teCampfirePot.getField(TileEntityCampfirePot.ID_BURN_TIME));
            }

            if (this.processTime != this.teCampfirePot.getField(TileEntityCampfirePot.ID_COOK_TIME)) {
                icontainerlistener.sendWindowProperty(this, 1, this.teCampfirePot.getField(TileEntityCampfirePot.ID_COOK_TIME));
            }

            if (this.maxProcessTime != this.teCampfirePot.getField(TileEntityCampfirePot.ID_MAX_COOK_TIMER)) {
                icontainerlistener.sendWindowProperty(this, 2, this.teCampfirePot.getField(TileEntityCampfirePot.ID_MAX_COOK_TIMER));
            }
        }

        this.burnTime = this.teCampfirePot.getField(TileEntityCampfirePot.ID_BURN_TIME);
        this.processTime = this.teCampfirePot.getField(TileEntityCampfirePot.ID_COOK_TIME);
        this.maxProcessTime = this.teCampfirePot.getField(TileEntityCampfirePot.ID_MAX_COOK_TIMER);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int value) {
        this.teCampfirePot.setField(id, value);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return teCampfirePot.isUsableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int index) {
        // 0-9: Contain inventory
        // 10-36: Player inventory
        // 37-46: Hot bar in the player inventory

        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if (index >= 0 && index <= 9) {
                if (!this.mergeItemStack(itemStack1, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemStack1, itemStack);
            } else if (index >= 10) {
                if (index >= 10 && index < 37) {
                    if (!this.mergeItemStack(itemStack1, 37, 46, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 37 && index < 46 && !this.mergeItemStack(itemStack1, 10, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemStack1, 10, 46, false)) {
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
