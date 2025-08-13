package cn.mcmod.sakura.block.crop;

import cn.mcmod.sakura.item.ItemLoader;
import net.minecraft.item.Item;

public class BlockTomatoCrop extends BlockHighCrop {
    public BlockTomatoCrop() {
        super();
    }

    @Override
    protected Item getCrop() {
        return ItemLoader.TOMATO;
    }

    @Override
    protected Item getSeed() {
        return ItemLoader.TOMATO_SEEDS;
    }
}
