package cn.mcmod.sakura.block;

import cn.mcmod.sakura.base.BlockContainerBase;
import cn.mcmod.sakura.item.ItemLoader;
import cn.mcmod.sakura.proxy.CommonProxy;
import cn.mcmod.sakura.tileentity.TileEntityCampfire;
import cn.mcmod.sakura.tileentity.TileEntityCampfirePot;
import cn.mcmod.sakura.util.CapabilityUtil;
import cn.mcmod.sakura.util.ItemHandlerUtil;
import cn.mcmod.sakura.util.ItemUtil;
import cn.mcmod_mmf.mmlib.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BlockCampfire extends BlockContainerBase<TileEntityCampfire> {
    public static final PropertyBool LIT = PropertyBool.create("lit");
    private static final int LIT_OFF = 0;
    private static final int LIT_ON = 1;

    private static final AxisAlignedBB CAMPFIRE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D);

    public BlockCampfire() {
        super(Material.WOOD, TileEntityCampfire.class);
        setHardness(0.5F);
        setSoundType(SoundType.WOOD);
        setCreativeTab(CommonProxy.TAB);
        setDefaultState(blockState.getBaseState().withProperty(LIT, false));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCampfire();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LIT);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(LIT) ? LIT_ON : LIT_OFF;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(LIT, meta == 1);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(LIT) ? 12 : 0;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CAMPFIRE_AABB;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
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

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        IBlockState downState = worldIn.getBlockState(pos.down());
        return (downState.isTopSolid()
                || downState.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID)
                && super.canPlaceBlockAt(worldIn, pos);
    }

    private boolean canBlockStay(World worldIn, BlockPos pos) {
        IBlockState downState = worldIn.getBlockState(pos.down());
        return downState.isTopSolid()
                || downState.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canBlockStay(worldIn, pos)) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public boolean onBlockActivatedExt(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                       EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ,
                                       @Nullable TileEntityCampfire te) {
        if (te == null) return true;
        final boolean isServer = !worldIn.isRemote;
        final ItemStack heldStack = playerIn.getHeldItem(hand);
        final IItemHandler handler = CapabilityUtil.getItemHandler(te, null);
        if (handler == null) return true;

        if (heldStack.isEmpty()) {
            if (isServer) {
                final ItemStack itemStack = ItemHandlerUtil.extractAllItem(handler, 0, false);
                Block.spawnAsEntity(worldIn, pos, itemStack);
            }
            return true;
        }

        if (isServer && tryInsertItem(handler, 0, heldStack)) {
            return true;
        }
        final Item heldItem = heldStack.getItem();

        if (heldItem == ItemLoader.POT) {
            if (isServer) {
                worldIn.setBlockToAir(pos);
                worldIn.removeTileEntity(pos);

                if (state.getValue(LIT)) {
                    final int burnTime = te.getBurnTime();
                    worldIn.setBlockState(pos, BlockLoader.CAMPFIRE_POT.getDefaultState()
                            .withProperty(BlockCampfirePot.LIT, true));
                    if (worldIn.getTileEntity(pos) instanceof TileEntityCampfirePot teCampfirePot) {
                        teCampfirePot.setBurnTime(burnTime);
                    }
                } else {
                    worldIn.setBlockState(pos, BlockLoader.CAMPFIRE_POT.getDefaultState());
                }

                heldStack.shrink(1);
            }
            return true;
        }

        if (WorldUtil.getInstance().isItemFuel(heldStack)) {
            if (isServer) {
                te.addBurnTime(TileEntityFurnace.getItemBurnTime(heldStack));
                if (!state.getValue(LIT)) {
                    worldIn.setBlockState(pos, state.withProperty(LIT, true));
                }
                if (!heldItem.hasContainerItem(heldStack)) {
                    heldStack.shrink(1);
                }
            }
            return true;
        }

        if (heldItem == Items.FLINT_AND_STEEL) {
            if (isServer) {
                te.addBurnTime(10000);
                if (!state.getValue(LIT)) {
                    worldIn.setBlockState(pos, state.withProperty(LIT, true));
                }
                heldStack.damageItem(1, playerIn);
            } else {
                worldIn.playSound(playerIn, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return true;
        }

        return true;
    }

    private boolean tryInsertItem(IItemHandler handler, int slot, ItemStack heldStack) {
        final ItemStack copiedStack = ItemUtil.copy(heldStack, stack -> stack.setCount(1));
        if (!handler.isItemValid(slot, copiedStack)) return false;
        if (!handler.insertItem(slot, copiedStack, true).isEmpty()) return false;
        final ItemStack splitStack = heldStack.splitStack(1);
        handler.insertItem(slot, splitStack, false);
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.getBlockState(pos.up()).getBlock().onNeighborChange(worldIn, pos.up(), pos);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        double d0 = pos.getX() + 0.5D;
        double d2 = pos.getZ() + 0.5D;
        double d4 = rand.nextDouble() * 0.4D - 0.2D;
        if (stateIn.getValue(LIT)) {
            worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, pos.getY() + 0.2D, d2 + d4, 0.0D, 0.0D, 0.0D);
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, pos.getY() + 0.2D, d2 + d4, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(BlockLoader.CAMPFIRE);
    }

}