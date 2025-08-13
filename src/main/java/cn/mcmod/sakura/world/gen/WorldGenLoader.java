package cn.mcmod.sakura.world.gen;

import cn.mcmod.sakura.SakuraConfig;
import cn.mcmod.sakura.block.BlockLoader;
import cn.mcmod.sakura.compat.CompatConst;
import cn.mcmod.sakura.world.biome.SakuraBiomes;
import cn.mcmod_mmf.mmlib.util.WorldUtil;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class WorldGenLoader {
    public static final WorldGenLoader INSTANCE = new WorldGenLoader();

    private WorldGenLoader() {
    }

    public void register() {
        GameRegistry.registerWorldGenerator(new WorldGenBambooShot(), 1);
        GameRegistry.registerWorldGenerator(new WorldGenPepper(), 1);
        GameRegistry.registerWorldGenerator(new WorldGenVanilla(), 1);
        GameRegistry.registerWorldGenerator(new WorldGenUmeSaping(), 1);
        GameRegistry.registerWorldGenerator(new WorldGenIronSand(SakuraConfig.IRON_SAND_AMOUNT), 1);
    }

    @SubscribeEvent
    public void onOreGen(OreGenEvent.Post event) {
        final World worldIn = event.getWorld();
        final int genY = 10 + event.getRand().nextInt(24);
        final Biome biome = worldIn.getBiome(new BlockPos(event.getPos().getX(), 0, event.getPos().getZ()));
        if (SakuraConfig.EVERY_WHERE_SAKURA_DIAMOND
                || (biome == SakuraBiomes.BAMBOOFOREST || biome == SakuraBiomes.MAPLEFOREST)) {
            final BlockPos pos = new BlockPos(event.getPos().getX(), genY, event.getPos().getZ());
            for (int i = 0; i < 2; i++) {
                new WorldGenMinable(BlockLoader.SAKURA_DIAMOND_ORE.getDefaultState(), 3 + event.getRand().nextInt(5)).generate(worldIn, event.getRand(), pos);
            }
        }
    }

    @SubscribeEvent
    public void HotSpringGen(Decorate event) {
        if (Loader.isModLoaded(CompatConst.TFC)) return;
        BlockPos pos = event.getChunkPos().getBlock(event.getRand().nextInt(16) + 8, 0, event.getRand().nextInt(16) + 8);
        BlockPos newPos = WorldUtil.getInstance().findGround(event.getWorld(), pos, true, false, true);
        Biome biome = event.getWorld().getBiome(pos);

        if (newPos != null
                && event.getWorld().provider instanceof WorldProviderSurface
                && biome != Biomes.DESERT
                && biome != Biomes.DESERT_HILLS
                && event.getRand().nextFloat() < SakuraConfig.HOTSPRING_WEIGHT / 10000.0F) {
            new WorldGenHotSpring().generate(event.getWorld(), event.getRand(), newPos);
        }
    }

}
