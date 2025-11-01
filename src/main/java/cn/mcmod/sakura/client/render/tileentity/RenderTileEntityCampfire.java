package cn.mcmod.sakura.client.render.tileentity;

import cn.mcmod.sakura.tileentity.TileEntityCampfire;
import cn.mcmod.sakura.util.CapabilityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileEntityCampfire extends TileEntitySpecialRenderer<TileEntityCampfire> {
    public RenderTileEntityCampfire() {
    }

    @Override
    public void render(TileEntityCampfire te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te.getItemBurning().isEmpty()) return;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y - 0.25F, (float) z + 0.5F);
        renderItem(te, x, y, z, partialTicks);
        GlStateManager.popMatrix();
    }

    private void renderItem(TileEntityCampfire te, double posX, double posY, double posZ, float partialTicks) {
        final var itemHandler = CapabilityUtil.getItemHandler(te, null);
        if (itemHandler == null) return;
        ItemStack stack = itemHandler.getStackInSlot(0);
        GlStateManager.scale(0.65F, 0.65F, 0.65F);
        GlStateManager.translate(0.0F, 1.8F, 0.0F);

        float rot = te.getWorld().getTotalWorldTime() % 360;
        rot = rot * 2;
        GlStateManager.rotate(rot, 0.0F, 1.0F, 0.0F);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
    }
}