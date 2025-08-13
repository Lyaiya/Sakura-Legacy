package cn.mcmod.sakura.world.biome;

import cn.mcmod.sakura.block.BlockLoader;
import cn.mcmod.sakura.entity.EntityDeer;
import cn.mcmod.sakura.world.gen.WorldGenBigMaple;
import cn.mcmod.sakura.world.gen.WorldGenMapleTree;
import cn.mcmod.sakura.world.gen.WorldGenMapleTreeGreen;
import net.minecraft.block.BlockLeaves;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class BiomeMapleForest extends Biome {
    public static final WorldGenerator RED_MAPLETREE = new WorldGenMapleTree(false, 5, BlockLoader.MAPLE_LOG.getDefaultState(), BlockLoader.MAPLE_LEAVE_RED.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, true), BlockLoader.FALLEN_LEAVES_MAPLE_RED.getDefaultState(), false);
    public static final WorldGenerator RED_MAPLETREE_SAP = new WorldGenMapleTree(false, 5, BlockLoader.MAPLE_LOG.getDefaultState(), BlockLoader.MAPLE_LEAVE_RED.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, true), BlockLoader.FALLEN_LEAVES_MAPLE_RED.getDefaultState(), true);
    public static final WorldGenerator YELLOW_MAPLETREE = new WorldGenMapleTree(false, 5, BlockLoader.MAPLE_LOG.getDefaultState(), BlockLoader.MAPLE_LEAVE_YELLOW.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, true), BlockLoader.FALLEN_LEAVES_MAPLE_YELLOW.getDefaultState(), false);
    public static final WorldGenerator YELLOW_MAPLETREE_SAP = new WorldGenMapleTree(false, 5, BlockLoader.MAPLE_LOG.getDefaultState(), BlockLoader.MAPLE_LEAVE_YELLOW.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, true), BlockLoader.FALLEN_LEAVES_MAPLE_YELLOW.getDefaultState(), true);
    public static final WorldGenerator ORANGE_MAPLETREE = new WorldGenMapleTree(false, 5, BlockLoader.MAPLE_LOG.getDefaultState(), BlockLoader.MAPLE_LEAVE_ORANGE.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, true), BlockLoader.FALLEN_LEAVES_MAPLE_ORANGE.getDefaultState(), false);
    public static final WorldGenerator ORANGE_MAPLETREE_SAP = new WorldGenMapleTree(false, 5, BlockLoader.MAPLE_LOG.getDefaultState(), BlockLoader.MAPLE_LEAVE_ORANGE.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, true), BlockLoader.FALLEN_LEAVES_MAPLE_ORANGE.getDefaultState(), true);
    public static final WorldGenerator GREEN_MAPLETREE = new WorldGenMapleTreeGreen(false, 5, BlockLoader.MAPLE_LOG.getDefaultState(), BlockLoader.MAPLE_LEAVE_GREEN.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, true), BlockLoader.FALLEN_LEAVES_MAPLE_GREEN.getDefaultState(), false);
    public static final WorldGenerator GREEN_MAPLETREE_SAP = new WorldGenMapleTreeGreen(false, 5, BlockLoader.MAPLE_LOG.getDefaultState(), BlockLoader.MAPLE_LEAVE_GREEN.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, true), BlockLoader.FALLEN_LEAVES_MAPLE_GREEN.getDefaultState(), true);

    public static final WorldGenerator BIG_RED_MAPLETREE = new WorldGenBigMaple(false, BlockLoader.MAPLE_LOG.getDefaultState(), BlockLoader.MAPLE_SAPLING_RED.getDefaultState(), BlockLoader.MAPLE_LEAVE_RED.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, true), BlockLoader.FALLEN_LEAVES_MAPLE_RED.getDefaultState(), false);
    public static final WorldGenerator BIG_RED_MAPLETREE_SAP = new WorldGenBigMaple(false, BlockLoader.MAPLE_LOG.getDefaultState(), BlockLoader.MAPLE_SAPLING_RED.getDefaultState(), BlockLoader.MAPLE_LEAVE_RED.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, true), BlockLoader.FALLEN_LEAVES_MAPLE_RED.getDefaultState(), true);
    public static final WorldGenerator BIG_YELLOW_MAPLETREE = new WorldGenBigMaple(false, BlockLoader.MAPLE_LOG.getDefaultState(), BlockLoader.MAPLE_SAPLING_YELLOW.getDefaultState(), BlockLoader.MAPLE_LEAVE_YELLOW.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, true), BlockLoader.FALLEN_LEAVES_MAPLE_YELLOW.getDefaultState(), false);
    public static final WorldGenerator BIG_YELLOW_MAPLETREE_SAP = new WorldGenBigMaple(false, BlockLoader.MAPLE_LOG.getDefaultState(), BlockLoader.MAPLE_SAPLING_YELLOW.getDefaultState(), BlockLoader.MAPLE_LEAVE_YELLOW.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, true), BlockLoader.FALLEN_LEAVES_MAPLE_YELLOW.getDefaultState(), true);
    public static final WorldGenerator BIG_ORANGE_MAPLETREE = new WorldGenBigMaple(false, BlockLoader.MAPLE_LOG.getDefaultState(), BlockLoader.MAPLE_SAPLING_ORANGE.getDefaultState(), BlockLoader.MAPLE_LEAVE_ORANGE.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, true), BlockLoader.FALLEN_LEAVES_MAPLE_ORANGE.getDefaultState(), false);
    public static final WorldGenerator BIG_ORANGE_MAPLETREE_SAP = new WorldGenBigMaple(false, BlockLoader.MAPLE_LOG.getDefaultState(), BlockLoader.MAPLE_SAPLING_ORANGE.getDefaultState(), BlockLoader.MAPLE_LEAVE_ORANGE.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockLeaves.DECAYABLE, true), BlockLoader.FALLEN_LEAVES_MAPLE_ORANGE.getDefaultState(), true);

    public BiomeMapleForest(BiomeProperties mapleForest) {
        super(mapleForest);
        decorator.treesPerChunk = 10;
        decorator.grassPerChunk = 2;

        spawnableCreatureList.add(new SpawnListEntry(EntityDeer.class, 10, 3, 4));
    }

    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
        return switch (rand.nextInt(32)) {
            case 0 -> (WorldGenAbstractTree) RED_MAPLETREE_SAP;
            case 1 -> (WorldGenAbstractTree) RED_MAPLETREE;
            case 2 -> (WorldGenAbstractTree) YELLOW_MAPLETREE_SAP;
            case 3 -> (WorldGenAbstractTree) YELLOW_MAPLETREE;
            case 4 -> (WorldGenAbstractTree) ORANGE_MAPLETREE_SAP;
            case 5 -> (WorldGenAbstractTree) ORANGE_MAPLETREE;
            case 6 -> (WorldGenAbstractTree) GREEN_MAPLETREE_SAP;
            case 7 -> (WorldGenAbstractTree) GREEN_MAPLETREE;
            case 8 -> (WorldGenAbstractTree) RED_MAPLETREE_SAP;
            case 9 -> (WorldGenAbstractTree) RED_MAPLETREE;
            case 10 -> (WorldGenAbstractTree) YELLOW_MAPLETREE_SAP;
            case 11 -> (WorldGenAbstractTree) YELLOW_MAPLETREE;
            case 12 -> (WorldGenAbstractTree) ORANGE_MAPLETREE_SAP;
            case 13 -> (WorldGenAbstractTree) ORANGE_MAPLETREE;
            case 14 -> (WorldGenAbstractTree) GREEN_MAPLETREE_SAP;
            case 15 -> (WorldGenAbstractTree) GREEN_MAPLETREE;
            case 16 -> (WorldGenAbstractTree) BIG_RED_MAPLETREE_SAP;
            case 17 -> (WorldGenAbstractTree) RED_MAPLETREE;
            case 18 -> (WorldGenAbstractTree) BIG_YELLOW_MAPLETREE_SAP;
            case 19 -> (WorldGenAbstractTree) BIG_YELLOW_MAPLETREE;
            case 20 -> (WorldGenAbstractTree) BIG_ORANGE_MAPLETREE_SAP;
            case 21 -> (WorldGenAbstractTree) BIG_ORANGE_MAPLETREE;
            case 22 -> (WorldGenAbstractTree) GREEN_MAPLETREE_SAP;
            case 23 -> (WorldGenAbstractTree) GREEN_MAPLETREE;
            case 24 -> (WorldGenAbstractTree) RED_MAPLETREE_SAP;
            case 25 -> (WorldGenAbstractTree) RED_MAPLETREE;
            case 26 -> (WorldGenAbstractTree) YELLOW_MAPLETREE_SAP;
            case 27 -> (WorldGenAbstractTree) YELLOW_MAPLETREE;
            case 28 -> (WorldGenAbstractTree) ORANGE_MAPLETREE_SAP;
            case 29 -> (WorldGenAbstractTree) ORANGE_MAPLETREE;
            case 30 -> (WorldGenAbstractTree) GREEN_MAPLETREE_SAP;
            case 31 -> (WorldGenAbstractTree) GREEN_MAPLETREE;
            default -> (WorldGenAbstractTree) RED_MAPLETREE;
        };
    }
}
