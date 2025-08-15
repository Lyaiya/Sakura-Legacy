package cn.mcmod.sakura.base;

import cn.mcmod.sakura.SakuraMain;
import cn.mcmod.sakura.util.CapabilityUtil;
import cn.mcmod.sakura.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public abstract class BlockContainerBase extends BlockContainer implements ITileEntityProvider {
    private static final int GUI_ID_NONE = -1;

    private int guiId = GUI_ID_NONE;
    private boolean checkPlaceFullBlock = false;

    private final Class<? extends TileEntity> teClass;

    protected BlockContainerBase(Material materialIn, Class<? extends TileEntity> teClass) {
        super(materialIn);
        this.teClass = teClass;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return true;
        final TileEntity te = getTileEntity(worldIn, pos);
        if (onBlockActivatedExt(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ, te)) {
            return true;
        }
        if (te != null) {
            if (tryOpenGui(playerIn, worldIn, pos)) {
                return true;
            }
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        breadBlockExt(worldIn, pos, state);
        final TileEntity te = getTileEntity(worldIn, pos);
        if (te != null) {
            dropInventoryItems(worldIn, pos, te);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        final TileEntity te = getTileEntity(worldIn, pos);
        onBlockPlacedByExt(worldIn, pos, state, placer, stack, te);
        if (te != null) {
            trySetCustomName(stack, te);
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && canPlaceFullBlock(worldIn, pos);
    }

    private boolean canPlaceFullBlock(World worldIn, BlockPos pos) {
        if (!checkPlaceFullBlock) return true;
        IBlockState downState = worldIn.getBlockState(pos.down());
        return downState.isTopSolid()
                && downState.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID;
    }

    protected boolean onBlockActivatedExt(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                          EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ,
                                          @Nullable TileEntity te) {
        return false;
    }

    protected void breadBlockExt(World worldIn, BlockPos pos, IBlockState state) {
    }

    protected void onBlockPlacedByExt(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
                                      ItemStack stack, @Nullable TileEntity te) {
    }

    protected void setGuiId(int guiId) {
        this.guiId = guiId;
    }

    private boolean hasGui() {
        return guiId != GUI_ID_NONE;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private <T extends TileEntity> T getTileEntity(World world, BlockPos pos) {
        return (T) WorldUtil.getTileEntity(world, pos, teClass);
    }

    private boolean tryOpenGui(EntityPlayer player, World world, BlockPos pos) {
        if (!hasGui()) return false;
        openGui(player, guiId, world, pos);
        return true;
    }

    private void trySetCustomName(ItemStack stack, TileEntity te) {
        if (!stack.hasDisplayName()) return;
        if (!(te instanceof TileEntityBase teBase)) return;
        teBase.setCustomName(stack.getDisplayName());
    }

    private static void openGui(EntityPlayer player, int guiId, World world, BlockPos pos) {
        player.openGui(SakuraMain.INSTANCE, guiId, world, pos.getX(), pos.getY(), pos.getZ());
    }

    private static void dropInventoryItems(World world, BlockPos pos, TileEntity te) {
        final IItemHandler itemHandler = CapabilityUtil.getItemHandler(te, null);
        if (itemHandler != null) {
            ItemStack itemStack;
            for (int i = 0, len = itemHandler.getSlots(); i < len; i++) {
                itemStack = itemHandler.getStackInSlot(i);
                if (itemStack.isEmpty()) continue;
                Block.spawnAsEntity(world, pos, itemStack);
            }
        }
    }
}
