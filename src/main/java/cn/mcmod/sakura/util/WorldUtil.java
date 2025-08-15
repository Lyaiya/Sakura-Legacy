package cn.mcmod.sakura.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WorldUtil {
    private WorldUtil() {
    }

    @Nullable
    public static <T extends TileEntity> T getTileEntity(World world, BlockPos pos, Class<T> teClass) {
        final TileEntity tileEntity = world.getTileEntity(pos);
        if (teClass.isInstance(tileEntity)) {
            return teClass.cast(tileEntity);
        }
        return null;
    }
}
