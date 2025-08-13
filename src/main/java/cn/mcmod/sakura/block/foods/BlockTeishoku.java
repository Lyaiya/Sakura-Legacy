package cn.mcmod.sakura.block.foods;

import cn.mcmod.sakura.block.BlockLoader;
import cn.mcmod.sakura.compat.CompatConst;
import cn.mcmod_mmf.mmlib.block.BlockFacing;
import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.FoodHandler;
import net.dries007.tfc.api.capability.food.IFoodStatsTFC;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Method;
import toughasnails.api.thirst.ThirstHelper;

import java.util.Random;

public class BlockTeishoku extends BlockFacing {
    private static final PropertyInteger BITES = PropertyInteger.create("bites", 0, 3);
    private final int amount;
    private final float saturation;
    private final boolean isPlate;

    public BlockTeishoku(int amount, float saturation, boolean isPlate) {
        super(Material.WOOD, false);
        this.setSoundType(SoundType.WOOD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(BITES, 0));
        this.amount = amount;
        this.saturation = saturation;
        this.isPlate = isPlate;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);
    }

    /**
     * Called when the block is right clicked by a player.
     */
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            return this.eatGohan(worldIn, pos, state, playerIn);
        }
        ItemStack itemstack = playerIn.getHeldItem(hand);
        return this.eatGohan(worldIn, pos, state, playerIn) || itemstack.isEmpty();
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        if (state.getValue(BITES) > 0)
            return new ItemStack(BlockLoader.OBON).getItem();
        return super.getItemDropped(state, rand, fortune);
    }

    private boolean eatGohan(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (!player.canEat(false)) {
            return false;
        }

        int i = state.getValue(BITES);
        if (!worldIn.isRemote) {
            if (Loader.isModLoaded(CompatConst.TFC))
                addTFCStats(player);
            else {
                player.getFoodStats().addStats(amount, saturation);
                if (Loader.isModLoaded(CompatConst.TOUGH_AS_NAILS))
                    addTANThirst(player, 2, 0.5F);
            }
            if (i < 3) {
                worldIn.setBlockState(pos, state.withProperty(BITES, i + 1), 3);
            } else {
                worldIn.setBlockState(pos, BlockLoader.TEISHOKO_FINISHED.getDefaultState().withProperty(BlockTeishokoFinished.isPlate, this.isPlate).withProperty(FACING, state.getValue(FACING)), 3);
            }
        }
        worldIn.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.4F, worldIn.rand.nextFloat() * 0.1F + 0.8F);
        return true;
    }

    @Method(modid = CompatConst.TOUGH_AS_NAILS)
    private void addTANThirst(EntityPlayer player, int i, float f) {
        ThirstHelper.getThirstData(player).addStats(i, f);
    }

    @Method(modid = CompatConst.TFC)
    private void addTFCStats(EntityPlayer player) {
        if (player.getFoodStats() instanceof IFoodStatsTFC foodStats) {
            foodStats.addStats(new FoodHandler(null, new FoodData(amount, 10F, saturation, 5F, 5F, 5F, 5F, 5F, 1F)));
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        int facing_meta = meta & 12;
        EnumFacing facing = switch (facing_meta) {
            case 0 -> EnumFacing.SOUTH;
            case 1 -> EnumFacing.WEST;
            case 2 -> EnumFacing.NORTH;
            case 3 -> EnumFacing.EAST;
            default -> EnumFacing.NORTH;
        };
        return this.getDefaultState().withProperty(FACING, facing).withProperty(BITES, (meta & 3));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(BITES, 0);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(FACING).getHorizontalIndex() << 2) + state.getValue(BITES);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, BITES);
    }
}
