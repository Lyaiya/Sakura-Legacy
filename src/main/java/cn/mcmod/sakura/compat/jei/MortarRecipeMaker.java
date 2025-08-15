package cn.mcmod.sakura.compat.jei;

import cn.mcmod.sakura.api.recipes.MortarRecipes;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public final class MortarRecipeMaker {
    public static List<SimpleRecipe> getRecipes(IJeiHelpers helpers) {
        IStackHelper stackHelper = helpers.getStackHelper();
        List<SimpleRecipe> recipes = new ArrayList<>();

        for (Entry<Object[], ItemStack[]> entry : MortarRecipes.INSTANCE.recipes.entrySet()) {
            List<List<ItemStack>> inputs = new ArrayList<>();
            for (Object obj : entry.getKey()) {
                List<ItemStack> subInputs = stackHelper.toItemStackList(obj);
                inputs.add(subInputs);
            }
            List<List<ItemStack>> outputs = new ArrayList<>();
            for (ItemStack obj : entry.getValue()) {
                List<ItemStack> subOutputs = stackHelper.toItemStackList(obj);
                outputs.add(subOutputs);
            }
            SimpleRecipe newRecipe = new SimpleRecipe(inputs, outputs);
            recipes.add(newRecipe);
        }
        return recipes;
    }
}
