package cn.mcmod.sakura.proxy;

import cn.mcmod.sakura.client.SakuraParticleType;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy {
    void onPreInit(FMLPreInitializationEvent event);

    void onInit(FMLInitializationEvent event);

    void onPostInit(FMLPostInitializationEvent event);

    default void spawnParticle(SakuraParticleType particleType, double x, double y, double z, double velX, double velY, double velZ) {
    }
}
