package cn.mcmod.sakura.api.recipes;

import cn.mcmod.ppot.PotmanRegistry;
import cn.mcmod.ppot.recipe.BasicPotRecipe;
import cn.mcmod.sakura.SakuraMain;
import cn.mcmod.sakura.compat.CompatConst;
import cn.mcmod_mmf.mmlib.util.RecipesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PotRecipes {
    public static final PotRecipes INSTANCE = new PotRecipes();

    public final Map<Pair<Object[], ItemStack>, List<FluidStack>> recipesList = Maps.newHashMap();

    private int recipeCount = 0;

    private PotRecipes() {
    }

    public void addRecipes(ItemStack result, Object[] list, FluidStack fluidStack) {
        addRecipes(result, list, Lists.newArrayList(fluidStack));
    }

    public void addRecipes(ItemStack result, Object[] list) {
        addRecipes(result, list, RecipesUtil.getInstance().EMPTY_FLUID);
    }

    public void addRecipes(ItemStack result, Object[] list, List<FluidStack> fluidList) {
        if (fluidList.isEmpty()) {
            SakuraMain.logger.warn("Some one using an empty fluid list!!! When Craft{}", result.getDisplayName());
            return;
        }
        Pair<Object[], ItemStack> items = Pair.of(list, result);
        recipesList.put(items, fluidList);
        if (Loader.isModLoaded(CompatConst.PROJECT_POTMAN)) {
            registerPotmanRecipe(result, list, fluidList);
        }
        recipeCount++;
    }

    @Method(modid = CompatConst.PROJECT_POTMAN)
    private void registerPotmanRecipe(ItemStack result, Object[] list, List<FluidStack> fluidList) {
        PotmanRegistry.POT_RECIPE.register(new BasicPotRecipe(list, fluidList, result, 200, 8000, 18000).setRegistryName(SakuraMain.MODID, String.format("sakura_pot_recipe_%d", recipeCount)));
    }

    @Nullable
    public FluidStack getResultFluid(FluidStack fluid, List<ItemStack> inputs) {
        return getResultFluid(fluid, checkItems(inputs));
    }

    @Nullable
    public FluidStack getResultFluid(FluidStack fluid, @Nullable Pair<Object[], ItemStack> recipe) {
        if (recipe != null) {
            for (FluidStack k : recipesList.get(recipe)) {
                if (k.isFluidEqual(fluid)) {
                    return k;
                }
            }
        }
        return null;
    }

    @Nullable
    private Pair<Object[], ItemStack> checkItems(List<ItemStack> inputs) {
        for (Entry<Pair<Object[], ItemStack>, List<FluidStack>> entry : recipesList.entrySet()) {
            boolean flg1 = true;
            if ((inputs.size() != entry.getKey().getLeft().length)) continue;

            for (Object obj1 : entry.getKey().getLeft()) {
                boolean flg2 = false;
                for (ItemStack input : inputs) {
                    if (obj1 instanceof ItemStack stack1) {
                        if (ItemStack.areItemsEqual(stack1, input)) {
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
                return entry.getKey();
            }
        }
        return null;
    }

    public ItemStack getResultItemStack(FluidStack fluid, List<ItemStack> inputs) {
        Pair<Object[], ItemStack> recipe = checkItems(inputs);
        if (recipe != null) {
            if (getResultFluid(fluid, recipe) != null || recipesList.get(recipe).isEmpty()) {
                return recipe.getRight();
            }
        }
        return ItemStack.EMPTY;
    }

    public void clearRecipe(ItemStack itemOutput) {
        for (Entry<Pair<Object[], ItemStack>, List<FluidStack>> entry : recipesList.entrySet()) {
            Pair<Object[], ItemStack> recipe = entry.getKey();
            if (RecipesUtil.getInstance().compareItems(itemOutput, recipe.getRight())) {
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