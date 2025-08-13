package cn.mcmod.sakura.block;

import cn.mcmod.sakura.item.ItemLoader;
import cn.mcmod.sakura.proxy.CommonProxy;
import cn.mcmod.sakura.tileentity.TileEntityCampfire;
import cn.mcmod_mmf.mmlib.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Random;

public class BlockCampfire extends BlockContainer implements ITileEntityProvider {
    private static final AxisAlignedBB CAMPFIRE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D);

    private static boolean keepInventory;

    private final boolean isBurning;

    public BlockCampfire(boolean isBurning) {
        super(Material.WOOD);
        setHardness(0.5F);
        setSoundType(SoundType.WOOD);
        this.isBurning = isBurning;

        if (isBurning) {
            setLightLevel(0.85F);
        } else {
            setCreativeTab(CommonProxy.TAB);
        }
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return true;

        final ItemStack heldItemStack = playerIn.getHeldItem(hand);
        final TileEntity tile = worldIn.getTileEntity(pos);
        if (hand == EnumHand.MAIN_HAND) {
            if (!(tile instanceof TileEntityCampfire teCampfire)) return true;
            final ItemStackHandler inventory = teCampfire.getInventory();
            final Item heldItem = heldItemStack.getItem();
            if (inventory.isItemValid(0, heldItemStack)
                    && inventory.getStackInSlot(0).getCount() < 16) {
                ItemStack campfireStack = new ItemStack(heldItem, 1, heldItemStack.getMetadata());
                heldItemStack.shrink(1);
                inventory.insertItem(0, campfireStack, false);
                return true;
            }

            if (heldItem == ItemLoader.POT) {
                worldIn.setBlockToAir(pos);
                worldIn.removeTileEntity(pos);
                worldIn.setBlockState(pos, BlockLoader.CAMPFIRE_POT_IDLE.getDefaultState());
                heldItemStack.shrink(1);
                return true;
            }

            if (WorldUtil.getInstance().isItemFuel(heldItemStack)) {
                teCampfire.setBurningTime(teCampfire.getBurningTime() + TileEntityFurnace.getItemBurnTime(heldItemStack));
                setState(true, worldIn, pos);
                if (!heldItem.hasContainerItem(heldItemStack)) {
                    heldItemStack.shrink(1);
                }
                return true;
            }

            if (heldItem == Items.FLINT_AND_STEEL) {
                teCampfire.setBurningTime(teCampfire.getBurningTime() + 10000);
                setState(true, worldIn, pos);
                heldItemStack.damageItem(1, playerIn);
                return true;
            }

            if (heldItemStack.isEmpty()) {
                Block.spawnAsEntity(worldIn, pos, inventory.getStackInSlot(0));
                inventory.setStackInSlot(0, ItemStack.EMPTY);
                return true;
            }
        }

        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.getBlockState(pos.up()).getBlock().onNeighborChange(worldIn, pos.up(), pos);
    }

    public static void setState(boolean active, World worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        keepInventory = true;

        if (active) {
            worldIn.setBlockState(pos, BlockLoader.CAMPFIRE_LIT.getDefaultState());
        } else {
            worldIn.setBlockState(pos, BlockLoader.CAMPFIRE_IDLE.getDefaultState());
        }

        keepInventory = false;

        if (tileentity != null) {
            tileentity.validate();
            worldIn.setTileEntity(pos, tileentity);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        double d0 = pos.getX() + 0.5D;
        double d2 = pos.getZ() + 0.5D;
        double d4 = rand.nextDouble() * 0.4D - 0.2D;
        if (isBurning) {
            worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, pos.getY() + 0.2D, d2 + d4, 0.0D, 0.0D, 0.0D);
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, pos.getY() + 0.2D, d2 + d4, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!keepInventory) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te != null) {
                IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

                if (inventory != null && inventory.getStackInSlot(0) != ItemStack.EMPTY) {
                    Block.spawnAsEntity(worldIn, pos, inventory.getStackInSlot(0));
                    ((IItemHandlerModifiable) inventory).setStackInSlot(0, ItemStack.EMPTY);
                }
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(BlockLoader.CAMPFIRE_IDLE);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCampfire();
    }

}