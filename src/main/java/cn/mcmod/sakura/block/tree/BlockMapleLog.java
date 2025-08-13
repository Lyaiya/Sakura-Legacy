package cn.mcmod.sakura.block.tree;

import cn.mcmod.sakura.block.BlockLoader;
import cn.mcmod.sakura.proxy.CommonProxy;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMapleLog extends BlockLog {
    public BlockMapleLog() {
        super();
        setCreativeTab(CommonProxy.TAB);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LOG_AXIS);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return switch (state.getValue(LOG_AXIS)) {
            case X -> 4;
            case Y -> 0;
            case Z -> 8;
            default -> 12;
        };
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (worldIn.isAreaLoaded(pos.add(-5, -5, -5), pos.add(5, 5, 5))) {
            for (BlockPos blockpos : BlockPos.getAllInBox(pos.add(-4, -4, -4), pos.add(4, 4, 4))) {
                IBlockState iblockstate = worldIn.getBlockState(blockpos);

                if (iblockstate.getBlock().isLeaves(iblockstate, worldIn, blockpos)
                        && iblockstate.getBlock() != BlockLoader.UME_LEAVES) {
                    iblockstate.getBlock().beginLeavesDecay(iblockstate, worldIn, blockpos);
                }
            }
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return switch (meta) {
            case 0 -> getDefaultState().withProperty(LOG_AXIS, EnumAxis.Y);
            case 4 -> getDefaultState().withProperty(LOG_AXIS, EnumAxis.X);
            case 8 -> getDefaultState().withProperty(LOG_AXIS, EnumAxis.Z);
            default -> getDefaultState().withProperty(LOG_AXIS, EnumAxis.NONE);
        };
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return true;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 5;
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 5;
    }
}
