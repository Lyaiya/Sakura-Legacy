package cn.mcmod.sakura.api.armor;

import cn.mcmod.sakura.SakuraMain;
import cn.mcmod.sakura.item.ItemLoader;
import cn.mcmod.sakura.item.armors.ItemHaori;
import cn.mcmod.sakura.item.armors.ItemKimono;
import cn.mcmod.sakura.item.armors.ItemSamuraiArmors;
import cn.mcmod.sakura.util.RLUtil;
import cn.mcmod_mmf.mmlib.util.RecipesUtil;
import com.google.common.collect.HashMultimap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ArmorLoader {
    public static final ArmorLoader INSTANCE = new ArmorLoader();

    private ArmorLoader() {
    }

    public void register() {
        registerKimono("kimono_1");
        registerKimono("kimono_2");
        registerKimono("kimono_3");
        registerKimono("kimono_4");
        registerKimono("kimono_5");
        registerKimono("kimono_6");

        registerKimono("kimono_ene");

        registerKimono("kimono_miko");
        registerKimono("yukata_0");
        registerKimono("yukata_1");
        registerKimono("yukata_2");
        registerKimono("yukata_3");
        registerKimono("yukata_4");

        registerHaori("haori_1");
        registerHaori("haori_2");
        registerHaori("haori_3");
        registerHaori("haori_4");

        registerArmor(false, "samurai_armor_1");
        registerArmor(false, "samurai_armor_2");
        registerArmor(true, "soldier_armor_1");
    }

    public void registerKimono(String textureName) {
        ItemStack kimono = new ItemStack(ItemLoader.KIMONO);
        ItemKimono.ALL_KIMONO_ID.add(textureName);
        NBTTagCompound tag = RecipesUtil.getInstance().getItemTagCompound(kimono);
        ItemKimono.TEXTURE_NAME.set(tag, textureName);
        registerCustomItemStack(textureName, kimono);
    }

    public void registerHaori(String textureName) {
        ItemStack kimono = new ItemStack(ItemLoader.HAORI);
        ItemHaori.ALL_KIMONO_NAME.add(textureName);
        NBTTagCompound tag = RecipesUtil.getInstance().getItemTagCompound(kimono);
        ItemKimono.TEXTURE_NAME.set(tag, textureName);
        registerCustomItemStack(textureName, kimono);
    }

    public void registerArmor(boolean soldier, String texture_name) {
        ItemStack head = soldier ? new ItemStack(ItemLoader.SOLDIER_HELMET) : new ItemStack(ItemLoader.SAMURAI_HELMET);
        ItemStack body = soldier ? new ItemStack(ItemLoader.SOLDIER_CHEST) : new ItemStack(ItemLoader.SAMURAI_CHEST);
        ItemStack pants = soldier ? new ItemStack(ItemLoader.SOLDIER_PANTS) : new ItemStack(ItemLoader.SAMURAI_PANTS);
        ItemStack feet = soldier ? new ItemStack(ItemLoader.SOLDIER_SHOES) : new ItemStack(ItemLoader.SAMURAI_SHOES);
        ItemSamuraiArmors.ALL_ARMOR_ID.put(texture_name, soldier);
        NBTTagCompound tag = RecipesUtil.getInstance().getItemTagCompound(head);
        NBTTagCompound tag1 = RecipesUtil.getInstance().getItemTagCompound(body);
        NBTTagCompound tag2 = RecipesUtil.getInstance().getItemTagCompound(pants);
        NBTTagCompound tag3 = RecipesUtil.getInstance().getItemTagCompound(feet);
        ItemSamuraiArmors.TEXTURE_NAME.set(tag, texture_name);
        ItemSamuraiArmors.TEXTURE_NAME.set(tag1, texture_name);
        ItemSamuraiArmors.TEXTURE_NAME.set(tag2, texture_name);
        ItemSamuraiArmors.TEXTURE_NAME.set(tag3, texture_name);
        registerCustomItemStack(texture_name, head);
        registerCustomItemStack(texture_name, body);
        registerCustomItemStack(texture_name, pants);
        registerCustomItemStack(texture_name, feet);
    }

    public HashMultimap<ResourceLocation, ItemStack> ArmorRegistry = HashMultimap.create();

    public void registerCustomItemStack(String name, ItemStack stack) {
        ArmorRegistry.put(RLUtil.of(name), stack);
    }

    public ItemStack findItemStack(String modid, String name, Item item, int count) {
        ResourceLocation key = RLUtil.of(modid, name);
        ItemStack stack = ItemStack.EMPTY;

        if (ArmorRegistry.containsKey(key)) {
            for (ItemStack stack_item : ArmorRegistry.get(RLUtil.of(modid, name))) {
                if (stack_item.getItem() == item)
                    stack = stack_item.copy();
            }
        } else {
            Item new_item = ForgeRegistries.ITEMS.getValue(RLUtil.of(modid, name));
            if (new_item != null)
                stack = new ItemStack(new_item);
        }
        if (!stack.isEmpty()) {
            stack.setCount(count);
        }
        return stack;
    }

    public ItemStack getCustomArmor(String key, Item item) {
        String modid;
        String name;
        {
            String[] str = key.split(":", 2);
            if (str.length == 2) {
                modid = str[0];
                name = str[1];
            } else {
                modid = SakuraMain.MODID;
                name = key;
            }
        }

        return findItemStack(modid, name, item, 1);
    }
}
