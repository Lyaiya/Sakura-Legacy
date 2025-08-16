package cn.mcmod.sakura.block;

import cn.mcmod.sakura.base.BlockContainerBase;
import cn.mcmod.sakura.gui.SakuraGuiHandler;
import cn.mcmod.sakura.proxy.CommonProxy;
import cn.mcmod.sakura.tileentity.TileEntityBarrel;
import cn.mcmod.sakura.util.CapabilityUtil;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

public class BlockBarrel extends BlockContainerBase<TileEntityBarrel> {
    private static final PropertyDirection FACING = BlockHorizontal.FACING;

    protected BlockBarrel() {
        super(Material.WOOD, TileEntityBarrel.class);
        setHardness(2.5F);
        setResistance(8.0F);
        setSoundType(SoundType.WOOD);
        setCreativeTab(CommonProxy.TAB);
        setGuiId(SakuraGuiHandler.ID_BARREL);
    }

    @Override
    protected boolean onBlockActivatedExt(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                          EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ,
                                          @Nullable TileEntityBarrel te) {
        if (te == null) return false;
        final ItemStack heldItem = playerIn.getHeldItem(hand);
        return tryFillByHandItem(playerIn, hand, facing, te, heldItem);
    }

    private boolean tryFillByHandItem(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, TileEntity te,
                                      ItemStack heldItem) {
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(ItemHandlerHelper.copyStackWithSize(heldItem, 1));
        if (handler == null) return false;

        final IFluidHandler fluidHandler = CapabilityUtil.getFluidHandler(te, facing);
        if (fluidHandler == null) return false;
        return FluidUtil.interactWithFluidHandler(playerIn, hand, fluidHandler);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityBarrel();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        setDefaultFacing(worldIn, pos, state);
    }

    private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
        if (worldIn.isRemote) return;
        IBlockState northState = worldIn.getBlockState(pos.north());
        IBlockState southState = worldIn.getBlockState(pos.south());
        IBlockState westState = worldIn.getBlockState(pos.west());
        IBlockState eastState = worldIn.getBlockState(pos.east());
        EnumFacing enumfacing = state.getValue(FACING);

        if (enumfacing == EnumFacing.NORTH && northState.isFullBlock() && !southState.isFullBlock()) {
            enumfacing = EnumFacing.SOUTH;
        } else if (enumfacing == EnumFacing.SOUTH && southState.isFullBlock() && !northState.isFullBlock()) {
            enumfacing = EnumFacing.NORTH;
        } else if (enumfacing == EnumFacing.WEST && westState.isFullBlock() && !eastState.isFullBlock()) {
            enumfacing = EnumFacing.EAST;
        } else if (enumfacing == EnumFacing.EAST && eastState.isFullBlock() && !westState.isFullBlock()) {
            enumfacing = EnumFacing.WEST;
        }

        worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    @Override
    protected void onBlockPlacedByExt(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
                                      ItemStack stack, @Nullable TileEntityBarrel te) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.byIndex(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        return getDefaultState().withProperty(FACING, enumfacing);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

}
