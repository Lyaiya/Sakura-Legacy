package cn.mcmod.sakura.gui;

import cn.mcmod.sakura.inventory.*;
import cn.mcmod.sakura.tileentity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import org.jetbrains.annotations.Nullable;

public class SakuraGuiHandler implements IGuiHandler {
    public static final int ID_STONE_MORTAR = 0;
    public static final int ID_CAMPFIRE_POT = 1;
    public static final int ID_BARREL = 2;
    public static final int ID_DISTILLATION = 3;
    public static final int ID_MAPLE_CAULDRON = 4;
    public static final int ID_OUT = 5;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        final TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        switch (ID) {
            case ID_STONE_MORTAR: {
                if (tileEntity instanceof TileEntityStoneMortar te) {
                    return new ContainerStoneMortar(player.inventory, te);
                }
            }
            case ID_CAMPFIRE_POT: {
                if (tileEntity instanceof TileEntityCampfirePot te) {
                    return new ContainerCampfirePot(player.inventory, te);
                }
            }
            case ID_BARREL: {
                if (tileEntity instanceof TileEntityBarrel te) {
                    return new ContainerBarrel(player.inventory, te);
                }
            }
            case ID_DISTILLATION: {
                if (tileEntity instanceof TileEntityDistillation te) {
                    return new ContainerDistillation(player.inventory, te);
                }
            }
            case ID_MAPLE_CAULDRON: {
                if (tileEntity instanceof TileEntityMapleCauldron te) {
                    return new ContainerMapleCauldron(player.inventory, te);
                }
            }
            case ID_OUT: {
                if (tileEntity instanceof TileEntityFluidOut te) {
                    return new ContainerFluidOut(player.inventory, te);
                }
            }
            default: {
                return null;
            }
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        final TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        switch (ID) {
            case ID_STONE_MORTAR: {
                if (tileEntity instanceof TileEntityStoneMortar te) {
                    return new GuiStoneMortar(player.inventory, te);
                }
            }
            case ID_CAMPFIRE_POT: {
                if (tileEntity instanceof TileEntityCampfirePot te) {
                    return new GuiCampfirePot(player.inventory, te);
                }
            }
            case ID_BARREL: {
                if (tileEntity instanceof TileEntityBarrel te) {
                    return new GuiBarrel(player.inventory, te);
                }
            }
            case ID_DISTILLATION: {
                if (tileEntity instanceof TileEntityDistillation te) {
                    return new GuiDistillation(player.inventory, te);
                }
            }
            case ID_MAPLE_CAULDRON: {
                if (tileEntity instanceof TileEntityMapleCauldron te) {
                    return new GuiMapleCauldron(player.inventory, te);
                }
            }
            case ID_OUT: {
                if (tileEntity instanceof TileEntityFluidOut te) {
                    return new GuiFluidOut(player.inventory, te);
                }
            }
            default: {
                return null;
            }
        }
    }
}