package cn.mcmod.sakura.gui;

import cn.mcmod.sakura.inventory.ContainerStoneMortar;
import cn.mcmod.sakura.tileentity.TileEntityStoneMortar;
import cn.mcmod.sakura.util.RLUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiStoneMortar extends GuiContainer {
    private static final ResourceLocation TEXTURES = RLUtil.of("textures/gui/stonemortar.png");

    private final IInventory playerInventory;
    private final TileEntityStoneMortar teStoneMortar;

    public GuiStoneMortar(InventoryPlayer playerInventory, TileEntityStoneMortar te) {
        super(new ContainerStoneMortar(playerInventory, te));
        this.playerInventory = playerInventory;
        this.teStoneMortar = te;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        String name = teStoneMortar.getDisplayName().getUnformattedText();
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 4210752);
        fontRenderer.drawString(playerInventory.getDisplayName().getUnformattedText(), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURES);

        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;

        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        int l2 = getCookProgressScaled(24);
        drawTexturedModalRect(k + 79, l + 34, 176, 14, l2 + 1, 16);
    }

    private int getCookProgressScaled(int pixels) {
        int processTime = teStoneMortar.getProcessTime();
        if (processTime == 0) return 0;
        int totalProcessTime = teStoneMortar.getTotalProcessTime();
        if (totalProcessTime == 0) return 0;
        return processTime * pixels / totalProcessTime;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

}