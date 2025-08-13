package cn.mcmod.sakura.client.render.tileentity;

import cn.mcmod.sakura.tileentity.TileEntityMapleCauldron;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderTileEntityMapleCauldron extends TileEntitySpecialRenderer<TileEntityMapleCauldron> {
    @Override
    public void render(TileEntityMapleCauldron te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        FluidStack fluidStack = te.getFluidForRendering(partialTicks);
        if (fluidStack == null) return;

        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluidStack.getFluid().getStill(fluidStack).toString());
        if (still == null) {
            still = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        }

        int brightness = Minecraft.getMinecraft().world.getCombinedLight(te.getPos(), fluidStack.getFluid().getLuminosity(fluidStack));
        int lx = brightness >> 0x10 & 0xFFFF;
        int ly = brightness & 0xFFFF;

        int color = fluidStack.getFluid().getColor(fluidStack);
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        int a = color >> 24 & 0xFF;

        double height = 0.6 + 0.4 * ((double) fluidStack.amount / te.tank.getCapacity());

        GlStateManager.translate(x + 0.1, y, z + 0.1);
        GlStateManager.scale(0.8, 0.8, 0.8);

        buffer.pos(0.0625, height, 0.0625).color(r, g, b, a).tex(still.getMinU(), still.getMinV()).lightmap(lx, ly).endVertex();
        buffer.pos(0.0625, height, 0.9375).color(r, g, b, a).tex(still.getMinU(), still.getMaxV()).lightmap(lx, ly).endVertex();
        buffer.pos(0.9375, height, 0.9375).color(r, g, b, a).tex(still.getMaxU(), still.getMaxV()).lightmap(lx, ly).endVertex();
        buffer.pos(0.9375, height, 0.0625).color(r, g, b, a).tex(still.getMaxU(), still.getMinV()).lightmap(lx, ly).endVertex();

        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }
}