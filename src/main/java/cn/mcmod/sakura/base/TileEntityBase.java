package cn.mcmod.sakura.base;

import cn.mcmod.sakura.SakuraMain;
import cn.mcmod.sakura.util.StringUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldNameable;
import org.jetbrains.annotations.Nullable;

public abstract class TileEntityBase extends TileEntity implements ITickable, IWorldNameable {
    public static final String KEY_CUSTOM_NAME = "CustomName";

    @Nullable
    private String name;

    @Nullable
    private String customName;

    public TileEntityBase() {
    }

    @Nullable
    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    @Override
    public boolean hasCustomName() {
        return customName != null && !customName.isEmpty();
    }

    @Override
    public String getName() {
        return "container." + SakuraMain.MODID + "." + name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readCustomName(compound);
        onReadFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeCustomName(compound);
        onWriteToNBT(compound);
        return compound;
    }

    protected abstract void onReadFromNBT(NBTTagCompound compound);

    protected abstract void onWriteToNBT(NBTTagCompound compound);

    private void readCustomName(NBTTagCompound compound) {
        if (!compound.hasKey(KEY_CUSTOM_NAME)) return;
        customName = compound.getString(KEY_CUSTOM_NAME);
    }

    private void writeCustomName(NBTTagCompound compound) {
        final String name = customName;
        if (StringUtil.isNullOrEmpty(name)) return;
        compound.setString(KEY_CUSTOM_NAME, name);
    }

}
