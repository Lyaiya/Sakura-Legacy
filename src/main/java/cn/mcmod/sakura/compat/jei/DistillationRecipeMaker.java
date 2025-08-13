package cn.mcmod.sakura.compat.jei;

import cn.mcmod.sakura.api.recipes.DistillationRecipes;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public final class DistillationRecipeMaker {
    public static List<BarrelRecipe> getRecipes(IJeiHelpers helpers) {
        IStackHelper stackHelper = helpers.getStackHelper();

        List<BarrelRecipe> recipes = new ArrayList<>();

        for (Entry<Pair<FluidStack, Object[]>, List<FluidStack>> entry : DistillationRecipes.INSTANCE.recipesList.entrySet()) {
            List<List<ItemStack>> inputs = new ArrayList<>();
            List<List<FluidStack>> fluidStacks = new ArrayList<>();

            for (Object obj : entry.getKey().getRight()) {
                List<ItemStack> subInputs = stackHelper.toItemStackList(obj);
                inputs.add(subInputs);
            }

            fluidStacks.add(entry.getValue());

            BarrelRecipe newRecipe = new BarrelRecipe(inputs, fluidStacks, entry.getKey().getLeft());
            recipes.add(newRecipe);
        }
        return recipes;
    }
}
