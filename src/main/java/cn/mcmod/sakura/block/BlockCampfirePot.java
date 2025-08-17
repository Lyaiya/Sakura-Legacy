package cn.mcmod.sakura.block;

import cn.mcmod.sakura.base.BlockContainerBase;
import cn.mcmod.sakura.gui.SakuraGuiHandler;
import cn.mcmod.sakura.item.ItemLoader;
import cn.mcmod.sakura.tileentity.TileEntityCampfirePot;
import cn.mcmod.sakura.util.CapabilityUtil;
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
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BlockCampfirePot extends BlockContainerBase<TileEntityCampfirePot> {
    public static final PropertyBool LIT = PropertyBool.create("lit");
    private static final int LIT_OFF = 0;
    private static final int LIT_ON = 1;

    public BlockCampfirePot() {
        super(Material.IRON, TileEntityCampfirePot.class);
        setHardness(0.5F);
        setSoundType(SoundType.METAL);
        setGuiId(SakuraGuiHandler.ID_CAMPFIRE_POT);
        setDefaultState(blockState.getBaseState().withProperty(LIT, false));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCampfirePot();
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
        return FULL_BLOCK_AABB;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @SideOnly(Side.CLIENT)
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
        if (face == EnumFacing.UP) {
            return BlockFaceShape.BOWL;
        }
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        IBlockState downState = worldIn.getBlockState(pos.down());
        return (downState.isTopSolid() || downState.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID) && super.canPlaceBlockAt(worldIn, pos);
    }

    private boolean canBlockStay(World worldIn, BlockPos pos) {
        IBlockState downState = worldIn.getBlockState(pos.down());
        return downState.isTopSolid() || downState.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canBlockStay(worldIn, pos)) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    protected boolean onBlockActivatedExt(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                          EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ,
                                          @Nullable TileEntityCampfirePot te) {
        if (te == null) return true;
        final ItemStack heldItem = playerIn.getHeldItem(hand);
        if (hand == EnumHand.MAIN_HAND) {
            if (tryFillByHeldItem(worldIn, playerIn, hand, facing, te, heldItem)) {
                return true;
            }

            if (tryAddFuelByHeldItem(worldIn, pos, state, te, heldItem)) {
                return true;
            }

            if (tryAddFuelByFlintAndSteel(worldIn, pos, state, playerIn, te, heldItem)) {
                return true;
            }
        }
        return false;
    }

    private boolean tryFillByHeldItem(World worldIn, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, TileEntity te,
                                      ItemStack heldItem) {
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(ItemHandlerHelper.copyStackWithSize(heldItem, 1));
        if (handler == null) return false;
        final IFluidHandler fluidHandler = CapabilityUtil.getFluidHandler(te, facing);
        if (fluidHandler == null) return false;

        if (!worldIn.isRemote) {
            return FluidUtil.interactWithFluidHandler(playerIn, hand, fluidHandler);
        }
        return true;
    }

    private boolean tryAddFuelByHeldItem(World worldIn, BlockPos pos, IBlockState state, TileEntityCampfirePot te, ItemStack heldItem) {
        if (!WorldUtil.getInstance().isItemFuel(heldItem)) return false;
        if (!worldIn.isRemote) {
            te.addBurnTime(TileEntityFurnace.getItemBurnTime(heldItem));
            if (!isLit(state)) {
                worldIn.setBlockState(pos, state.withProperty(LIT, true));
            }
            if (!heldItem.getItem().hasContainerItem(heldItem)) {
                heldItem.shrink(1);
            }
        }
        return true;
    }

    private boolean tryAddFuelByFlintAndSteel(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                              TileEntityCampfirePot te, ItemStack heldItem) {
        if (heldItem.getItem() != Items.FLINT_AND_STEEL) return false;
        if (!worldIn.isRemote) {
            te.addBurnTime(10000);
            if (!isLit(state)) {
                worldIn.setBlockState(pos, state.withProperty(LIT, true));
            }
            heldItem.damageItem(1, playerIn);
        } else {
            worldIn.playSound(playerIn, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, worldIn.rand.nextFloat() * 0.4F + 0.8F);
        }
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
        if (isLit(stateIn)) {
            worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, pos.getY() + 0.2D, d2 + d4, 0.0D, 0.0D, 0.0D);
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, pos.getY() + 0.2D, d2 + d4, 0.0D, 0.0D, 0.0D);

            if (rand.nextDouble() < 0.15D) {
                worldIn.playSound(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        spawnAsEntity(worldIn, pos, new ItemStack(Item.getItemFromBlock(BlockLoader.CAMPFIRE)));
        spawnAsEntity(worldIn, pos, new ItemStack(ItemLoader.POT));
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(ItemLoader.POT);
    }

    private boolean isLit(IBlockState state) {
        return state.getValue(LIT);
    }
}