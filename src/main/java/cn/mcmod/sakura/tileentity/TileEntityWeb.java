package cn.mcmod.sakura.tileentity;

import cn.mcmod.sakura.api.recipes.WebRecipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class TileEntityWeb extends TileEntity implements ITickable {
    private static final String KEY_INVENTORY = "Inventory";

    private int cookTime;

    public TileEntityWeb() {
    }

    public int getCookTime() {
        return cookTime;
    }

    private final ItemStackHandler inventory = new ItemStackHandler() {
        @Override
        protected void onContentsChanged(int slot) {
            refresh();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return !(WebRecipe.INSTANCE.getResultItemStack(stack).isEmpty());
        }
    };

    public ItemStackHandler getInventory() {
        return inventory;
    }

    protected void refresh() {
        if (hasWorld() && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.markAndNotifyBlock(pos, world.getChunk(pos), state, state, 11);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound ret = super.writeToNBT(compound);
        writePacketNBT(ret);
        return ret;
    }

    @Override
    public final NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readPacketNBT(compound);
    }

    private void writePacketNBT(NBTTagCompound cmp) {
        cmp.setTag(KEY_INVENTORY, inventory.serializeNBT());
    }

    private void readPacketNBT(NBTTagCompound cmp) {
        inventory.deserializeNBT(cmp.getCompoundTag(KEY_INVENTORY));
    }

    @Override
    public final SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writePacketNBT(tag);
        return new SPacketUpdateTileEntity(pos, -999, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        readPacketNBT(packet.getNbtCompound());
    }

    @Override
    public void update() {
        if (world.isRemote) return;
        boolean flag = false;
        final ItemStack resultItemStack = WebRecipe.INSTANCE.getResultItemStack(inventory.getStackInSlot(0));
        if (resultItemStack.isEmpty()) {
            cookTime = 0;
            return;
        }
        cookTime += (1 * calcAdaptation(getWorld(), getPos()));
        if (cookTime >= 32000) {
            cookTime = 0;
            ItemStack copied = resultItemStack.copy();
            copied.setCount(copied.getCount() * inventory.getStackInSlot(0).getCount());
            inventory.setStackInSlot(0, copied);
            flag = true;
        }
        if (flag) {
            markDirty();
        }
    }

    private float calcAdaptation(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        boolean isUnderTheSun = world.canBlockSeeSky(pos);
        boolean isRaining = world.isRaining();
        boolean isDaytime = world.getWorldTime() % 24000 < 12000;
        float humidity = biome.getRainfall();
        float temperature = biome.getTemperature(pos);
        float rate;

        if (!isUnderTheSun || isRaining) {
            rate = 0.0F;
        } else {
            rate = isDaytime ? 2.0F : 1.0F;
            rate *= humidity < 0.2D ? 4.0D : humidity < 0.7D ? 2.0D : humidity < 0.9 ? 1.0D : 0.5D;
            rate *= temperature < 0.0D ? 1.0D : temperature < 0.6D ? 1.5D : temperature < 1.0D ? 2.0D : 4.0D;
        }
        return rate;
    }
}