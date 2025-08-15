package cn.mcmod.sakura.api.recipes;

import cn.mcmod_mmf.mmlib.util.RecipesUtil;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MortarRecipes {
    public static final MortarRecipes INSTANCE = new MortarRecipes();

    // key: Input, value: Output
    public final Map<Object[], ItemStack[]> recipes = Maps.newHashMap();

    private MortarRecipes() {
    }

    public void addMortarRecipes(ItemStack[] result, Object[] main) {
        recipes.put(main, result);
    }

    public ItemStack[] getOutput(List<ItemStack> inputs) {
        Object[] objects;
        for (Entry<Object[], ItemStack[]> entry : recipes.entrySet()) {
            boolean flg1 = true;

            objects = entry.getKey();
            if (inputs.size() != objects.length) continue;

            for (Object object : objects) {
                boolean flg2 = false;

                for (ItemStack input : inputs) {
                    if (input.isEmpty()) break;

                    if (object instanceof ItemStack itemStack) {
                        if (ItemStack.areItemsEqual(itemStack, input)) {
                            inputs.remove(input);
                            flg2 = true;
                            break;
                        }
                    } else if (object instanceof String name) {
                        NonNullList<ItemStack> ore = OreDictionary.getOres(name);
                        if (!ore.isEmpty() && RecipesUtil.getInstance().containsMatch(false, ore, input)) {
                            inputs.remove(input);
                            flg2 = true;
                            break;
                        }
                    }
                }

                if (!flg2) {
                    flg1 = false;
                    break;
                }
            }

            if (flg1) {
                return entry.getValue();
            }
        }

        return new ItemStack[0];
    }

    public void clearRecipe(Object[] inputs) {
        recipes.remove(inputs);
    }

    public void clearAllRecipe() {
        recipes.clear();
    }
}
