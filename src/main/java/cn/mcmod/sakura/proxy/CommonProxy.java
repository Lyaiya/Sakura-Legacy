package cn.mcmod.sakura.proxy;

import cn.mcmod.sakura.CreativeTabsSakura;
import cn.mcmod.sakura.SakuraMain;
import cn.mcmod.sakura.SakuraOreDictLoader;
import cn.mcmod.sakura.api.armor.ArmorLoader;
import cn.mcmod.sakura.block.BlockLoader;
import cn.mcmod.sakura.compat.CompatConst;
import cn.mcmod.sakura.compat.tfc.TFCCompat;
import cn.mcmod.sakura.compat.waila.CampfirePlugin;
import cn.mcmod.sakura.compat.waila.CampfirePotPlugin;
import cn.mcmod.sakura.entity.SakuraEntityRegister;
import cn.mcmod.sakura.entity.villager.VillagerCreationWA;
import cn.mcmod.sakura.item.ItemLoader;
import cn.mcmod.sakura.item.drinks.DrinksLoader;
import cn.mcmod.sakura.packet.PacketKeyMessage;
import cn.mcmod.sakura.packet.PacketKeyMessageHandler;
import cn.mcmod.sakura.potion.PotionLoader;
import cn.mcmod.sakura.tileentity.TileEntityRegistry;
import cn.mcmod.sakura.util.RLUtil;
import cn.mcmod.sakura.util.SakuraRecipeRegister;
import cn.mcmod.sakura.world.gen.WorldGenLoader;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber
public class CommonProxy implements IProxy {
    public static final SoundEvent TAIKO = new SoundEvent(RLUtil.of("taiko"));

    public static final CreativeTabs TAB = new CreativeTabsSakura();

    private static SimpleNetworkWrapper NETWORK;

    public static SimpleNetworkWrapper getNetwork() {
        return NETWORK;
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {
        PotionLoader.registerPotionEvent();
        BlockLoader.INSTANCE.registerBlock();
        ItemLoader.INSTANCE.registerItem();
        DrinksLoader.INSTANCE.registerItems();
        SakuraEntityRegister.registerEntity();
        SakuraEntityRegister.addSpawn();
        SakuraOreDictLoader.INSTANCE.registerOre();
        ArmorLoader.INSTANCE.register();
        VillagerCreationWA.registerComponents();
        VillagerRegistry.instance().registerVillageCreationHandler(new VillagerCreationWA());
    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.ORE_GEN_BUS.register(WorldGenLoader.INSTANCE);
        MinecraftForge.TERRAIN_GEN_BUS.register(WorldGenLoader.INSTANCE);
        WorldGenLoader.INSTANCE.register();

        TileEntityRegistry.INSTANCE.init();

        SakuraRecipeRegister.INSTANCE.init();

        if (Loader.isModLoaded(CompatConst.TFC)) {
            TFCCompat.registerTFCFuel();
        }
        if (Loader.isModLoaded(CompatConst.WAILA)) {
            final String campfirePluginRegisterName = CampfirePlugin.class.getName();
            final String campfirePotPluginRegisterName = CampfirePotPlugin.class.getName();
            FMLInterModComms.sendMessage(CompatConst.WAILA, "register", campfirePluginRegisterName);
            FMLInterModComms.sendMessage(CompatConst.WAILA, "register", campfirePotPluginRegisterName);
        }

        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(SakuraMain.MODID);
        NETWORK.registerMessage(new PacketKeyMessageHandler(), PacketKeyMessage.class, 0, Side.SERVER);
    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {
    }

    @SubscribeEvent
    public static void onSoundEventRegistration(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(TAIKO.setRegistryName(RLUtil.of("taiko")));
    }
}
