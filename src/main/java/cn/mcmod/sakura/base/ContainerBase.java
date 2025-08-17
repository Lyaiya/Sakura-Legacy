package cn.mcmod.sakura.base;

import cn.mcmod.sakura.util.CapabilityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

public abstract class ContainerBase<T extends TileEntity> extends Container {
    protected InventoryPlayer playerInventory;
    protected T te;

    public ContainerBase(InventoryPlayer playerInventory, T te) {
        this.playerInventory = playerInventory;
        this.te = te;

        final IItemHandler handler = CapabilityUtil.getItemHandler(te, null);
        if (handler != null) {
            addSlots(handler);
            addPlayerSlots();
        }
    }

    protected abstract void addSlots(IItemHandler itemHandler);

    private void addPlayerSlots() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return canInteractWith(playerIn, te);
    }

    private static boolean canInteractWith(EntityPlayer player, TileEntity te) {
        final BlockPos pos = te.getPos();
        if (te.getWorld().getTileEntity(pos) != te) {
            return false;
        }
        return player.getDistanceSq(
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D) <= 64.0D;
    }
}
