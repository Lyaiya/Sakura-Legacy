package cn.mcmod.sakura.compat.jei;

import cn.mcmod.sakura.api.recipes.WebRecipe;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

public final class WebRecipeMaker {
    public static List<SimpleRecipe> getRecipes(IJeiHelpers helpers) {
        IStackHelper stackHelper = helpers.getStackHelper();
        List<SimpleRecipe> recipes = new ArrayList<>();

        for (Entry<Object, ItemStack> entry : WebRecipe.INSTANCE.RecipesList.entrySet()) {
            List<ItemStack> inputs = stackHelper.toItemStackList(entry.getKey());
            List<ItemStack> outputs = stackHelper.toItemStackList(entry.getValue());

            SimpleRecipe newRecipe = new SimpleRecipe(Collections.singletonList(inputs), Collections.singletonList(outputs));
            recipes.add(newRecipe);
        }
        return recipes;
    }
}
