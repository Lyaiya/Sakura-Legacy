package cn.mcmod.sakura.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TileEntityShoji extends TileEntity implements ITickable {
    private static final String KEY_TYPE = "type";
    private static final String KEY_FACING = "facing";
    private static final String KEY_OPEN = "open";
    private static final String KEY_ANIMATION = "animation";

    private int type = 0;
    private EnumFacing facing = EnumFacing.SOUTH;
    private boolean open = false;
    private int animation = 0;

    public TileEntityShoji() {
    }

    @Override
    public void update() {
        if (animation <= 0) {
            animation = 0;
        } else {
            animation -= 1;
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(KEY_TYPE)) {
            type = compound.getInteger(KEY_TYPE);
        }
        if (compound.hasKey(KEY_FACING)) {
            facing = EnumFacing.byName(compound.getString(KEY_FACING));
        }
        if (compound.hasKey(KEY_OPEN)) {
            open = compound.getBoolean(KEY_OPEN);
        }
        if (compound.hasKey(KEY_ANIMATION)) {
            animation = compound.getInteger(KEY_ANIMATION);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(KEY_TYPE, type);
        compound.setString(KEY_FACING, facing.getName());
        compound.setBoolean(KEY_OPEN, open);
        compound.setInteger(KEY_ANIMATION, animation);
        return compound;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        markDirty();
        if (world != null) {
            IBlockState state = world.getBlockState(getPos());
            world.notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
        markDirty();
        if (world != null) {
            IBlockState state = world.getBlockState(getPos());
            world.notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        markDirty();
        if (world != null) {
            IBlockState state = world.getBlockState(getPos());
            world.notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    public int getAnimation() {
        return animation;
    }

    public void setAnimation(int animation) {
        this.animation = animation;
    }
}
