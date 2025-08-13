package cn.mcmod.sakura.api.recipes;

import cn.mcmod_mmf.mmlib.util.RecipesUtil;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Map.Entry;

public class LiquidToItemRecipe {
    public static final LiquidToItemRecipe INSTANCE = new LiquidToItemRecipe();

    public final Map<FluidStack, Map<Object, ItemStack>> recipesList = Maps.newHashMap();

    private LiquidToItemRecipe() {
    }

    public void addRecipes(Object main, ItemStack result, FluidStack fluidStack) {
        Map<Object, ItemStack> items;
        if (!recipesList.containsKey(fluidStack)) {
            items = Maps.newHashMap();
            recipesList.put(fluidStack, items);
        } else {
            items = recipesList.get(fluidStack);
        }
        items.put(main, result);
    }

    public ItemStack getResultItemStack(FluidStack fluid, ItemStack stack) {
        for (Entry<FluidStack, Map<Object, ItemStack>> entry : recipesList.entrySet()) {
            if (entry.getKey().isFluidEqual(fluid))
                for (Entry<Object, ItemStack> entry2 : entry.getValue().entrySet()) {
                    if (entry2.getKey() instanceof ItemStack itemStack) {
                        if (ItemStack.areItemsEqual(stack, itemStack)) {
                            return entry2.getValue();
                        }
                    } else if (entry2.getKey() instanceof String dict) {
                        NonNullList<ItemStack> ore = OreDictionary.getOres(dict);
                        if (!ore.isEmpty()
                                && RecipesUtil.getInstance().containsMatch(true, ore, stack)) {
                            return entry2.getValue();
                        }
                    }
                }
        }

        return ItemStack.EMPTY;
    }

    @Nullable
    public FluidStack getResultFluid(FluidStack fluid) {
        for (FluidStack entry : recipesList.keySet()) {
            if (entry.isFluidEqual(fluid)) {
                return entry;
            }
        }
        return null;
    }

    public void clearRecipe(FluidStack fluid, Object input) {
        recipesList.get(fluid).remove(input);
    }

    public void clearAllRecipe() {
        recipesList.clear();
    }
}