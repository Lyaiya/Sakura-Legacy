package cn.mcmod.sakura.block;

import cn.mcmod.sakura.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockBambooFence extends BlockFence {

    public static final PropertyBool UP = PropertyBool.create("up");

    public BlockBambooFence() {
        super(Material.WOOD, MapColor.WOOD);
        setCreativeTab(CommonProxy.TAB);
        setHardness(1.2F);
        setResistance(5.0F);
        setDefaultState(blockState.getBaseState()
                .withProperty(UP, Boolean.FALSE)
                .withProperty(NORTH, Boolean.FALSE)
                .withProperty(EAST, Boolean.FALSE)
                .withProperty(SOUTH, Boolean.FALSE)
                .withProperty(WEST, Boolean.FALSE));
    }

    private boolean canFenceConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        BlockPos other = pos.offset(facing);
        Block block = world.getBlockState(other).getBlock();
        return block.canBeConnectedTo(world, other, facing.getOpposite()) || canConnectTo(world, other, facing.getOpposite());
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        boolean flag = canFenceConnectTo(worldIn, pos, EnumFacing.NORTH);
        boolean flag1 = canFenceConnectTo(worldIn, pos, EnumFacing.EAST);
        boolean flag2 = canFenceConnectTo(worldIn, pos, EnumFacing.SOUTH);
        boolean flag3 = canFenceConnectTo(worldIn, pos, EnumFacing.WEST);
        boolean flag4 = flag && !flag1 && flag2 && !flag3 || !flag && flag1 && !flag2 && flag3;
        return state.withProperty(UP, !flag4 || !worldIn.isAirBlock(pos.up()))
                .withProperty(NORTH, flag)
                .withProperty(EAST, flag1)
                .withProperty(SOUTH, flag2)
                .withProperty(WEST, flag3);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, UP, NORTH, EAST, WEST, SOUTH);
    }

}
