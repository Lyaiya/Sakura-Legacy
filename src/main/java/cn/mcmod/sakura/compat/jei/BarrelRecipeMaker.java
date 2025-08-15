package cn.mcmod.sakura.compat.jei;

import cn.mcmod.sakura.api.recipes.BarrelRecipes;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public final class BarrelRecipeMaker {
    public static List<BarrelRecipe> getRecipes(IJeiHelpers helpers) {
        IStackHelper stackHelper = helpers.getStackHelper();

        List<BarrelRecipe> recipes = new ArrayList<>();

        for (Entry<Pair<FluidStack, Object[]>, List<FluidStack>> entry : BarrelRecipes.INSTANCE.recipes.entrySet()) {
            List<List<ItemStack>> inputs = new ArrayList<>();
            List<List<FluidStack>> fluidlist = new ArrayList<>();

            for (Object obj : entry.getKey().getRight()) {
                List<ItemStack> subInputs = stackHelper.toItemStackList(obj);
                inputs.add(subInputs);
            }

            fluidlist.add(entry.getValue());

            BarrelRecipe newRecipe = new BarrelRecipe(inputs, fluidlist, entry.getKey().getLeft());
            recipes.add(newRecipe);
        }
        return recipes;
    }
}
