package cn.mcmod.sakura;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = SakuraMain.MODID)
@Mod.EventBusSubscriber(modid = SakuraMain.MODID)
public class SakuraConfig {
    private final static String PREFIX = "sakura.config.";

    @Config.LangKey(PREFIX + "vanilla_weight")
    @Config.RequiresMcRestart
    @Config.RangeInt(min = 0, max = 2000)
    public static int VANILLA_WEIGHT = 90;

    @Config.LangKey(PREFIX + "pepper_weight")
    @Config.RequiresMcRestart
    @Config.RangeInt(min = 0, max = 2000)
    public static int PEPPER_WEIGHT = 90;

    @Config.LangKey(PREFIX + "bambooshot_weight")
    @Config.RequiresMcRestart
    @Config.RangeInt(min = 0, max = 2000)
    public static int BAMBOOSHOT_WEIGHT = 90;

    @Config.LangKey(PREFIX + "ume_weight")
    @Config.RequiresMcRestart
    @Config.RangeInt(min = 0, max = 2000)
    public static int UME_WEIGHT = 90;

    @Config.LangKey(PREFIX + "iron_sand_amount")
    @Config.RequiresMcRestart
    @Config.RangeInt(min = 1, max = 5120)
    public static int IRON_SAND_AMOUNT = 128;

    @Config.LangKey(PREFIX + "hotspring_weight")
    @Config.RequiresMcRestart
    @Config.RangeInt(min = 0, max = 5000)
    public static int HOTSPRING_WEIGHT = 10;

    @Config.LangKey(PREFIX + "harder_iron_recipe")
    @Config.RequiresMcRestart
    public static boolean HARDER_IRON_RECIPE = false;

    @Config.LangKey(PREFIX + "harder_iron_difficult")
    @Config.RequiresMcRestart
    @Config.RangeInt(min = 1, max = 3)
    public static int HARDER_IRON_DIFFICULT = 1;

    @Config.LangKey(PREFIX + "every_where_sakura_diamond")
    @Config.RequiresMcRestart
    public static boolean EVERY_WHERE_SAKURA_DIAMOND = false;

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(SakuraMain.MODID)) {
            ConfigManager.sync(SakuraMain.MODID, Config.Type.INSTANCE);
        }
    }
}
