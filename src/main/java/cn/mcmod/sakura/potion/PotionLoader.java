package cn.mcmod.sakura.potion;

import cn.mcmod.sakura.util.RLUtil;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class PotionLoader {
    public static final ResourceLocation RES = RLUtil.of("textures/gui/potion.png");

    public static final Potion EXP = new PotionExp();
    public static final Potion CANNON = new PotionCannon();
    public static final Potion FIRE_BLADE = new PotionFireBlade();
    public static final Potion GOLDEN_HEART = new PotionGoldenHeart();
    public static final Potion POISOM = new PotionAttackPotion(MobEffects.POISON, "poisom", 1, 0x2eb025, 0, 18, 18, 18);
    public static final Potion POISOM_BIG = new PotionAttackPotion(MobEffects.POISON, "scorpion", 3, 0x2D123D, 18, 18, 18, 18);

    public static void registerPotionEvent() {
        ForgeRegistries.POTIONS.register(EXP);
        ForgeRegistries.POTIONS.register(CANNON);
        ForgeRegistries.POTIONS.register(GOLDEN_HEART);
        ForgeRegistries.POTIONS.register(POISOM);
        ForgeRegistries.POTIONS.register(POISOM_BIG);
        ForgeRegistries.POTIONS.register(FIRE_BLADE);

        MinecraftForge.EVENT_BUS.register(EXP);
        MinecraftForge.EVENT_BUS.register(CANNON);
        MinecraftForge.EVENT_BUS.register(POISOM);
        MinecraftForge.EVENT_BUS.register(POISOM_BIG);
        MinecraftForge.EVENT_BUS.register(FIRE_BLADE);
    }

}
