package cn.mcmod.sakura.client.render.tileentity;

import cn.mcmod.sakura.client.model.tileentity.ModelPot;
import cn.mcmod.sakura.tileentity.TileEntityCampfirePot;
import cn.mcmod.sakura.util.CapabilityUtil;
import cn.mcmod.sakura.util.RLUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderTileEntityCampfirePot extends TileEntitySpecialRenderer<TileEntityCampfirePot> {
    private static final ResourceLocation TEXTURES = RLUtil.of("textures/entity/tileentity/pot.png");

    private final ModelPot model = new ModelPot();

    @Override
    public void render(TileEntityCampfirePot te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        GlStateManager.disableCull();

        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        } else {
            this.bindTexture(TEXTURES);
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();

        if (destroyStage < 0) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        }

        GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.0F, 1.0F, 0.0F);
        GlStateManager.scale(0.9995F, 0.9995F, 0.9995F);
        GlStateManager.translate(0.0F, -1.0F, 0.0F);

        this.model.ironBar3.render(0.0625F);
        this.model.ironBar2.render(0.0625F);
        this.model.base.render(0.0625F);
        this.model.ironBar.render(0.0625F);
        this.model.ironBar4.render(0.0625F);
        this.model.pot1.render(0.0625F);
        GlStateManager.enableCull();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y, (float) z + 0.5F);
        renderItem(te, x, y, z, partialTicks);
        GlStateManager.popMatrix();

        FluidStack fluid = te.getFluidForRendering(partialTicks);

        if (fluid != null) {
            RenderHelper.disableStandardItemLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.pushMatrix();
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
            if (still == null) {
                still = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
            }

            int brightness = Minecraft.getMinecraft().world.getCombinedLight(te.getPos(), fluid.getFluid().getLuminosity(fluid));
            int lx = brightness >> 0x10 & 0xFFFF;
            int ly = brightness & 0xFFFF;

            int color = fluid.getFluid().getColor(fluid);
            int r = color >> 16 & 0xFF;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;
            int a = color >> 24 & 0xFF;

            double height = 0.6 + 0.4 * ((double) fluid.amount / te.getInputTank().getCapacity());

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

    protected void renderItem(TileEntityCampfirePot te, double posX, double posY, double posZ, float partialTicks) {
        final IItemHandler itemHandler = CapabilityUtil.getItemHandler(te, null);
        if (itemHandler == null) return;

        ItemStack itemStack1 = itemHandler.getStackInSlot(0);
        if (itemStack1 != ItemStack.EMPTY) {
            GlStateManager.pushMatrix();
            float scale = 0.3F;

            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(0.1F, 2.5F, 0.0F);
            float rot = te.getWorld().getTotalWorldTime() % 360;
            rot = rot * 2;
            GlStateManager.rotate(rot, 1.0F, 1.0F, 0.0F);

            Minecraft.getMinecraft().getRenderItem().renderItem(itemStack1, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }

        ItemStack itemStack2 = itemHandler.getStackInSlot(1);
        if (itemStack2 != ItemStack.EMPTY) {
            GlStateManager.pushMatrix();
            float scale = 0.3F;

            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(0.5F, 2.5F, 0.0F);
            float rot = te.getWorld().getTotalWorldTime() % 360;
            rot = rot * 2;
            GlStateManager.rotate(rot, 0.0F, 1.0F, 0.0F);

            Minecraft.getMinecraft().getRenderItem().renderItem(itemStack2, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }

        ItemStack itemStack3 = itemHandler.getStackInSlot(2);
        if (itemStack3 != ItemStack.EMPTY) {
            GlStateManager.pushMatrix();
            float scale = 0.3F;

            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(-0.23F, 2.5F, 0.75F);
            float rot = te.getWorld().getTotalWorldTime() % 360;
            rot = rot * 2;
            GlStateManager.rotate(rot, 0.3F, 1.0F, 0F);

            Minecraft.getMinecraft().getRenderItem().renderItem(itemStack3, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }

        ItemStack itemStack4 = itemHandler.getStackInSlot(3);
        if (itemStack4 != ItemStack.EMPTY) {
            GlStateManager.pushMatrix();
            float scale = 0.3F;

            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(0.5F, 2.5F, -0.75F);
            float rot = te.getWorld().getTotalWorldTime() % 360;
            rot = rot * 2;
            GlStateManager.rotate(rot, 0.0F, 1.0F, 0.0F);

            Minecraft.getMinecraft().getRenderItem().renderItem(itemStack4, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }

        ItemStack itemStack5 = itemHandler.getStackInSlot(4);
        if (itemStack5 != ItemStack.EMPTY) {
            GlStateManager.pushMatrix();
            float scale = 0.3F;

            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(-0.5F, 2.5F, -0.5F);
            float rot = te.getWorld().getTotalWorldTime() % 360;
            rot = rot * 2;
            GlStateManager.rotate(rot, 0.0F, 1.0F, 0.0F);
            Minecraft.getMinecraft().getRenderItem().renderItem(itemStack5, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }

        ItemStack itemStack6 = itemHandler.getStackInSlot(5);
        if (itemStack6 != ItemStack.EMPTY) {
            GlStateManager.pushMatrix();
            float scale = 0.3F;

            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(0.6F, 2.5F, 0.5F);
            float rot = te.getWorld().getTotalWorldTime() % 360;
            rot = rot * 2;
            GlStateManager.rotate(rot, 0.0F, 1.0F, 0.0F);

            Minecraft.getMinecraft().getRenderItem().renderItem(itemStack6, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }

        ItemStack itemStack7 = itemHandler.getStackInSlot(6);
        if (itemStack7 != ItemStack.EMPTY) {
            GlStateManager.pushMatrix();
            float scale = 0.3F;

            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(-0.23F, 2.5F, 0.75F);
            float rot = te.getWorld().getTotalWorldTime() % 360;
            rot = rot * 2;
            GlStateManager.rotate(rot, 0.3F, 1.0F, 0F);

            Minecraft.getMinecraft().getRenderItem().renderItem(itemStack7, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }

        ItemStack itemStack8 = itemHandler.getStackInSlot(7);
        if (itemStack8 != ItemStack.EMPTY) {
            GlStateManager.pushMatrix();
            float scale = 0.3F;

            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(-0.4F, 2.5F, -0.75F);
            float rot = te.getWorld().getTotalWorldTime() % 360;
            rot = rot * 2;
            GlStateManager.rotate(rot, 0.0F, 1.0F, 0.0F);

            Minecraft.getMinecraft().getRenderItem().renderItem(itemStack8, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }

        ItemStack itemStack9 = itemHandler.getStackInSlot(8);
        if (itemStack9 != ItemStack.EMPTY) {
            GlStateManager.pushMatrix();
            float scale = 0.3F;

            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(-0.5F, 2.5F, -0.5F);
            float rot = te.getWorld().getTotalWorldTime() % 360;
            rot = rot * 2;
            GlStateManager.rotate(rot, 0.0F, 1.0F, 0.0F);
            Minecraft.getMinecraft().getRenderItem().renderItem(itemStack9, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }
    }

}