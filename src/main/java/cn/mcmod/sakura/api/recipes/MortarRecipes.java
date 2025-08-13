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

    public final Map<Object[], ItemStack[]> recipesList = Maps.newHashMap();

    private MortarRecipes() {
    }

    public void addMortarRecipes(ItemStack[] result, Object[] main) {
        recipesList.put(main, result);
    }

    public ItemStack[] getResult(List<ItemStack> inputs) {
        ItemStack[] retStack = new ItemStack[0];

        for (Entry<Object[], ItemStack[]> entry : recipesList.entrySet()) {
            boolean flg1 = true;
            if (inputs.size() != entry.getKey().length) continue;

            for (Object object : entry.getKey()) {
                boolean flg2 = false;

                for (ItemStack inputItemStack : inputs) {
                    if (inputItemStack.isEmpty()) break;
                    if (object instanceof ItemStack itemStack) {
                        if (ItemStack.areItemsEqual(itemStack, inputItemStack)) {
                            inputs.remove(inputItemStack);
                            flg2 = true;
                            break;
                        }
                    } else if (object instanceof String name) {
                        NonNullList<ItemStack> ore = OreDictionary.getOres(name);
                        if (!ore.isEmpty() && RecipesUtil.getInstance().containsMatch(false, ore, inputItemStack)) {
                            inputs.remove(inputItemStack);
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

        return retStack;
    }

    public void clearRecipe(Object[] inputs) {
        recipesList.remove(inputs);
    }

    public void clearAllRecipe() {
        recipesList.clear();
    }
}
