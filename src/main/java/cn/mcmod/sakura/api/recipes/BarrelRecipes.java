package cn.mcmod.sakura.api.recipes;

import cn.mcmod_mmf.mmlib.util.RecipesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BarrelRecipes {
    public static final BarrelRecipes INSTANCE = new BarrelRecipes();

    public final Map<Pair<FluidStack, Object[]>, List<FluidStack>> recipesList = Maps.newHashMap();

    private BarrelRecipes() {
    }

    public void register(FluidStack input, FluidStack output) {
        register(input, output, new Object[]{ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY});
    }

    public void register(FluidStack input, FluidStack output, Object[] additives) {
        Pair<FluidStack, Object[]> items = Pair.of(output, additives);
        recipesList.put(items, Lists.newArrayList(input));
    }

    public void register(List<FluidStack> input, FluidStack output) {
        register(input, output, new Object[]{ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY});
    }

    public void register(List<FluidStack> input, FluidStack output, Object[] additives) {
        Pair<FluidStack, Object[]> items = Pair.of(output, additives);
        recipesList.put(items, input);
    }

    @Nullable
    public FluidStack getFluidStack(FluidStack fluid) {
        for (List<FluidStack> entry : recipesList.values()) {
            for (FluidStack fluidStack : entry) {
                if (fluidStack.isFluidEqual(fluid)) {
                    return fluidStack;
                }
            }
        }
        return null;
    }

    @Nullable
    public FluidStack getResultFluidStack(FluidStack fluid, ItemStack[] inputs) {
        for (Entry<Pair<FluidStack, Object[]>, List<FluidStack>> entry : recipesList.entrySet()) {
            for (FluidStack fluidStack : entry.getValue()) {
                if (fluidStack.isFluidEqual(fluid)) {
                    boolean flg1 = true;
                    for (Object obj1 : entry.getKey().getRight()) {
                        boolean flg2 = false;
                        for (ItemStack input : inputs) {
                            if (obj1 instanceof ItemStack itemStack) {
                                if (ItemStack.areItemsEqual(itemStack, input)) {
                                    flg2 = true;
                                    break;
                                }
                            } else if (obj1 instanceof String name) {
                                NonNullList<ItemStack> ore = OreDictionary.getOres(name);
                                if (!ore.isEmpty() && RecipesUtil.getInstance().containsMatch(false, ore, input)) {
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
                        return entry.getKey().getKey();
                    }
                }
            }
        }

        return null;
    }

    public void clearRecipe(FluidStack inputFluidStack) {
        for (Entry<Pair<FluidStack, Object[]>, List<FluidStack>> entry : recipesList.entrySet()) {
            Pair<FluidStack, Object[]> recipe = entry.getKey();
            if (recipe.getLeft().isFluidEqual(inputFluidStack)) {
                recipesList.remove(entry.getKey());
                return;
            }
        }
        throw new NullPointerException("NO RECIPE HERE");
    }

    public void clearAllRecipe() {
        recipesList.clear();
    }
}
