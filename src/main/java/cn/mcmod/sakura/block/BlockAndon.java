package cn.mcmod.sakura.block;

import cn.mcmod.sakura.proxy.CommonProxy;
import cn.mcmod_mmf.mmlib.block.BlockFacing;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;

public class BlockAndon extends BlockFacing {

    public BlockAndon() {
        super(Material.WOOD, false);
        setSoundType(SoundType.WOOD);
        setCreativeTab(CommonProxy.TAB);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        setHardness(1.0F);
        setResistance(4.0F);
        setLightLevel(1F);
    }

}
