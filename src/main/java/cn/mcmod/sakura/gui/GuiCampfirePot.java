package cn.mcmod.sakura.gui;

import cn.mcmod.sakura.inventory.ContainerCampfirePot;
import cn.mcmod.sakura.tileentity.TileEntityCampfirePot;
import cn.mcmod.sakura.util.RLUtil;
import cn.mcmod_mmf.mmlib.util.ClientUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiCampfirePot extends GuiContainer {
    private static final ResourceLocation TEXTURES = RLUtil.of("textures/gui/pot.png");

    private final IInventory playerInventory;
    private final TileEntityCampfirePot teCampfirePot;

    public GuiCampfirePot(InventoryPlayer inventory, TileEntityCampfirePot te) {
        super(new ContainerCampfirePot(inventory, te));

        playerInventory = inventory;
        teCampfirePot = te;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        fontRenderer.drawString(playerInventory.getDisplayName().getUnformattedText(), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURES);

        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;

        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);

        int var7;

        // Flame
        if (teCampfirePot.isBurning()) {
            var7 = teCampfirePot.getBurnTimeRemainingScaled(12);

            drawTexturedModalRect(k + 100, l + 67 - var7, 176, 12 - var7, 14, var7 + 2);
        }

        int l2 = getCookProgressScaled(24);
        drawTexturedModalRect(k + 96, l + 37, 176, 14, l2 + 1, 16);

        if (teCampfirePot.getInputTank().getFluid() != null) {
            FluidTank fluidTank = teCampfirePot.getInputTank();
            int heightInd = (int) (72 * ((float) fluidTank.getFluidAmount() / (float) fluidTank.getCapacity()));
            if (heightInd > 0) {
                ClientUtils.getInstance().drawRepeatedFluidSprite(fluidTank.getFluid(), k + 167 - heightInd, l + 11, heightInd, 16f);
            }
        }
    }

    private int getCookProgressScaled(int pixels) {
        int cookTime = teCampfirePot.getCookTime();
        if (cookTime == 0) return 0;
        int totalCookTime = teCampfirePot.getTotalCookTime();
        if (totalCookTime == 0) return 0;
        return cookTime * pixels / totalCookTime;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

}