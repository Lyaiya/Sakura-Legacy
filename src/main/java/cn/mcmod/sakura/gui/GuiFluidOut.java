package cn.mcmod.sakura.gui;

import cn.mcmod.sakura.inventory.ContainerFluidOut;
import cn.mcmod.sakura.tileentity.TileEntityFluidOut;
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
public class GuiFluidOut extends GuiContainer {
    private static final ResourceLocation mortarGuiTextures = RLUtil.of("textures/gui/barrel_out.png");

    private final TileEntityFluidOut teFluidOut;

    public GuiFluidOut(InventoryPlayer inventory, TileEntityFluidOut te) {
        super(new ContainerFluidOut(inventory, te));
        this.teFluidOut = te;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(mortarGuiTextures);

        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;

        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        if (this.teFluidOut.getTank().getFluid() != null) {
            FluidTank fluidTank = this.teFluidOut.getTank();
            int heightInd = (int) (162 * ((float) fluidTank.getFluidAmount() / (float) fluidTank.getCapacity()));
            if (heightInd > 0) {
                ClientUtils.getInstance().drawRepeatedFluidSprite(fluidTank.getFluid(), k + 168 - heightInd, l + 60, heightInd, 16F);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

}