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

    // key: Input Items, Output Item, value: Input Fluid
    public final Map<Pair<Object[], ItemStack>, List<FluidStack>> recipes = Maps.newHashMap();

    private int recipeCount = 0;

    private PotRecipes() {
    }

    public void addRecipes(ItemStack output, Object[] inputs, FluidStack inputFluid) {
        addRecipes(output, inputs, Lists.newArrayList(inputFluid));
    }

    public void addRecipes(ItemStack output, Object[] inputs) {
        addRecipes(output, inputs, RecipesUtil.getInstance().EMPTY_FLUID);
    }

    public void addRecipes(ItemStack output, Object[] inputs, List<FluidStack> inputFluids) {
        if (inputFluids.isEmpty()) {
            SakuraMain.LOGGER.warn("Some one using an empty fluid list!!! When Craft{}", output.getDisplayName());
            return;
        }
        Pair<Object[], ItemStack> items = Pair.of(inputs, output);
        recipes.put(items, inputFluids);
        if (Loader.isModLoaded(CompatConst.PROJECT_POTMAN)) {
            registerPotmanRecipe(output, inputs, inputFluids);
        }
        recipeCount++;
    }

    @Method(modid = CompatConst.PROJECT_POTMAN)
    private void registerPotmanRecipe(ItemStack output, Object[] inputs, List<FluidStack> inputFluids) {
        PotmanRegistry.POT_RECIPE.register(
                new BasicPotRecipe(inputs, inputFluids, output, 200, 8000, 18000)
                        .setRegistryName(SakuraMain.MODID, String.format("sakura_pot_recipe_%d", recipeCount)));
    }

    @Nullable
    public FluidStack getInputFluid(FluidStack inputFluid, List<ItemStack> inputs) {
        return getInputFluid(inputFluid, checkItems(inputs));
    }

    @Nullable
    public FluidStack getInputFluid(FluidStack inputFluid, @Nullable Pair<Object[], ItemStack> pair) {
        if (pair == null) return null;

        for (FluidStack recipeInput : recipes.get(pair)) {
            if (!recipeInput.isFluidEqual(inputFluid)) continue;
            return recipeInput;
        }

        return null;
    }

    @Nullable
    private Pair<Object[], ItemStack> checkItems(List<ItemStack> inputs) {
        for (Entry<Pair<Object[], ItemStack>, List<FluidStack>> entry : recipes.entrySet()) {
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

    public ItemStack getOutput(FluidStack inputFluid, List<ItemStack> inputs) {
        Pair<Object[], ItemStack> recipe = checkItems(inputs);
        if (recipe != null) {
            if (getInputFluid(inputFluid, recipe) != null || recipes.get(recipe).isEmpty()) {
                return recipe.getRight();
            }
        }
        return ItemStack.EMPTY;
    }

    public void clearRecipe(ItemStack output) {
        Pair<Object[], ItemStack> keyToRemove = null;
        for (Entry<Pair<Object[], ItemStack>, List<FluidStack>> entry : recipes.entrySet()) {
            Pair<Object[], ItemStack> recipe = entry.getKey();
            if (RecipesUtil.getInstance().compareItems(output, recipe.getRight())) {
                keyToRemove = recipe;
                break;
            }
        }
        if (keyToRemove == null) {
            throw new NullPointerException(output + " NO RECIPE HERE");
        }
        recipes.remove(keyToRemove);
    }

    public void clearAllRecipe() {
        recipes.clear();
    }

}