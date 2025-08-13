package cn.mcmod.sakura.client.render;

import cn.mcmod.sakura.client.model.ModelDeer;
import cn.mcmod.sakura.entity.EntityDeer;
import cn.mcmod.sakura.util.RLUtil;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDeer extends RenderLiving<EntityDeer> {
    private static final ResourceLocation DEER_TEXTURES = RLUtil.of("textures/entity/deer.png");

    public RenderDeer(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelDeer(), 0.5F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityDeer entity) {

        return DEER_TEXTURES;
    }
}