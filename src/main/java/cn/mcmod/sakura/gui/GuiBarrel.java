package cn.mcmod.sakura.gui;

import cn.mcmod.sakura.inventory.ContainerBarrel;
import cn.mcmod.sakura.tileentity.TileEntityBarrel;
import cn.mcmod.sakura.util.RLUtil;
import cn.mcmod_mmf.mmlib.util.ClientUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiBarrel extends GuiContainer {
    private static final ResourceLocation TEXTURES = RLUtil.of("textures/gui/barrel.png");

    private final TileEntityBarrel teBarrel;

    public GuiBarrel(InventoryPlayer inventory, TileEntityBarrel te) {
        super(new ContainerBarrel(inventory, te));
        teBarrel = te;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURES);

        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;

        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        int l2 = getCookProgressScaled(24);
        drawTexturedModalRect(k + 63, l + 35, 176, 0, l2 + 1, 16);

        if (teBarrel.getInputTank().getFluid() != null) {
            FluidTank fluidTank = teBarrel.getInputTank();
            int heightInd = (int) (68 * ((float) fluidTank.getFluidAmount() / (float) fluidTank.getCapacity()));

            if (heightInd > 0) {
                ClientUtils.getInstance().drawRepeatedFluidSprite(fluidTank.getFluid(), k + 18, l + 78 - heightInd, 16f, heightInd);
            }
        }

        if (teBarrel.getOutputTank().getFluid() != null) {
            FluidTank fluidTank = teBarrel.getOutputTank();
            int heightInd = (int) (68 * ((float) fluidTank.getFluidAmount() / (float) fluidTank.getCapacity()));
            // if (heightInd > 0) {
            //     ClientUtils.drawRepeatedFluidSprite(fluidTank.getFluid(), k + 167 - heightInd, l + 11, heightInd, 16f);
            // }

            if (heightInd > 0) {
                ClientUtils.getInstance().drawRepeatedFluidSprite(fluidTank.getFluid(), k + 89, l + 78 - heightInd, 16f, heightInd);
            }
        }
    }

    private int getCookProgressScaled(int pixels) {
        int i = teBarrel.getField(TileEntityBarrel.ID_PROCESS_TIMER);
        return i != 0 ? i * pixels / 1000 : 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

}