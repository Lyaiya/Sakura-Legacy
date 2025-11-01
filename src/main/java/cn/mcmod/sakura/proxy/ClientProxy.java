package cn.mcmod.sakura.proxy;

import cn.mcmod.sakura.block.BlockLoader;
import cn.mcmod.sakura.client.SakuraParticleType;
import cn.mcmod.sakura.client.particle.*;
import cn.mcmod.sakura.entity.SakuraEntityRegister;
import cn.mcmod.sakura.item.ItemLoader;
import cn.mcmod.sakura.item.drinks.DrinksLoader;
import cn.mcmod.sakura.tileentity.TileEntityRegistry;
import cn.mcmod.sakura.util.RLUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.UnknownNullability;
import org.lwjglx.input.Keyboard;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    public static ResourceLocation TEXTURE_LEAF = RLUtil.of("textures/particles/particles.png");
    @UnknownNullability
    public static KeyBinding CHANGE_MODE;

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {
        super.onPreInit(event);
        BlockLoader.INSTANCE.registerRenders();
        ItemLoader.INSTANCE.registerRenders();
        DrinksLoader.INSTANCE.registerRender();
        SakuraEntityRegister.registerEntityRender();

        TileEntityRegistry.INSTANCE.render();
    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        super.onInit(event);
        CHANGE_MODE = new KeyBinding("key.sakura.sheath_in", Keyboard.KEY_V, "key.categories.sakura");
        ClientRegistry.registerKeyBinding(CHANGE_MODE);
    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {
        super.onPostInit(event);
    }

    @Override
    public void spawnParticle(SakuraParticleType particleType, double x, double y, double z,
                              double velX, double velY, double velZ) {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.world;
        if (world == null) return;

        if (mc.effectRenderer == null) return;
        Particle particle = null;
        switch (particleType) {
            case MAPLE_RED:
                particle = new ParticleMapleRedLeaf(world, x, y, z, velX, velY, velZ);
                break;
            case MAPLE_GREEN:
                particle = new ParticleMapleGreenLeaf(world, x, y, z, velX, velY, velZ);
                break;
            case MAPLE_ORANGE:
                particle = new ParticleMapleOrangeLeaf(world, x, y, z, velX, velY, velZ);
                break;
            case MAPLE_YELLOW:
                particle = new ParticleMapleYellowLeaf(world, x, y, z, velX, velY, velZ);
                break;
            case LEAVES_SAKURA:
                particle = new ParticleSakuraLeaf(world, x, y, z, velX, velY, velZ);
                break;
            case SYRUP_DROP:
                particle = new ParticleSyrupDrop(world, x, y, z, velX, velY, velZ);
                break;
            default:
                break;
        }

        if (particle != null) {
            mc.effectRenderer.addEffect(particle);
        }
    }

}
