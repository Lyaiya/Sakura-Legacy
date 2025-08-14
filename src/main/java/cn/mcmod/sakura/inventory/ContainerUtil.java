package cn.mcmod.sakura.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class ContainerUtil {
    private ContainerUtil() {
    }

    public static boolean canInteractWith(EntityPlayer player, TileEntity te) {
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
