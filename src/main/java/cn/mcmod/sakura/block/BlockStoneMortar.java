package cn.mcmod.sakura.block;

import cn.mcmod.sakura.base.BlockContainerBase;
import cn.mcmod.sakura.gui.SakuraGuiHandler;
import cn.mcmod.sakura.proxy.CommonProxy;
import cn.mcmod.sakura.tileentity.TileEntityStoneMortar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

public class BlockStoneMortar extends BlockContainerBase<TileEntityStoneMortar> {
    protected BlockStoneMortar() {
        super(Material.ROCK, TileEntityStoneMortar.class);
        setHardness(2.0F);
        setResistance(11.0F);
        setSoundType(SoundType.STONE);
        setCreativeTab(CommonProxy.TAB);
        setGuiId(SakuraGuiHandler.ID_STONE_MORTAR);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityStoneMortar();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
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

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return true;
    }
}
