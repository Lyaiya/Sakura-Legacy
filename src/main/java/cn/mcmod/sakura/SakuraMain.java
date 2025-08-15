package cn.mcmod.sakura;

import cn.mcmod.sakura.gui.SakuraGuiHandler;
import cn.mcmod.sakura.proxy.IProxy;
import cn.mcmod.sakura.sakura.Tags;
import cn.mcmod.sakura.world.biome.SakuraBiomes;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.UnknownNullability;

@Mod(modid = SakuraMain.MODID, name = SakuraMain.NAME, version = SakuraMain.VERSION, dependencies = "required-after:mm_lib@[2.2.0,);")
public class SakuraMain {
    public static final String MODID = Tags.MOD_ID;
    public static final String NAME = Tags.MOD_NAME;
    public static final String VERSION = Tags.VERSION;

    @UnknownNullability
    @Instance(SakuraMain.MODID)
    public static SakuraMain INSTANCE;

    @UnknownNullability
    public static Logger LOGGER;

    @UnknownNullability
    @SidedProxy(
            clientSide = "cn.mcmod.sakura.proxy.ClientProxy",
            serverSide = "cn.mcmod.sakura.proxy.CommonProxy"
    )
    public static IProxy PROXY;

    @EventHandler
    public void onConstruct(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        FluidRegistry.enableUniversalBucket();
    }

    @SubscribeEvent
    public void onBiomesRegister(RegistryEvent.Register<Biome> event) {
        IForgeRegistry<Biome> registry = event.getRegistry();
        SakuraBiomes.register(registry);
    }

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        PROXY.onPreInit(event);
        LOGGER = event.getModLog();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new SakuraGuiHandler());
    }

    @EventHandler
    public void onInit(FMLInitializationEvent event) {
        PROXY.onInit(event);
    }

    @EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        PROXY.onPostInit(event);
    }
}
