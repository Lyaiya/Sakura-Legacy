package cn.mcmod.sakura.block;

import cn.mcmod_mmf.mmlib.block.BlockFacing;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockTatami extends BlockFacing {
    private final boolean isNS;

    public BlockTatami(boolean ns) {
        super(Material.CLOTH, true);
        this.setTickRandomly(true);
        this.setSoundType(SoundType.PLANT);
        setHardness(0.5F).setResistance(0.5F);
        isNS = ns;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.canBlockSeeSky(pos) && worldIn.isDaytime()) {
            worldIn.setBlockState(pos, (isNS ? BlockLoader.TATAMI_TAN_NS : BlockLoader.TATAMI_TAN).getDefaultState().withProperty(FACING, state.getValue(FACING)));
        }
        super.updateTick(worldIn, pos, state, rand);
    }
}
