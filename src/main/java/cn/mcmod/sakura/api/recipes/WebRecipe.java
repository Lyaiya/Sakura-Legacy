package cn.mcmod.sakura.api.recipes;

import cn.mcmod_mmf.mmlib.recipe.ItemToItemRecipeBase;
import com.google.common.collect.Maps;

public class WebRecipe extends ItemToItemRecipeBase {
    public static final WebRecipe INSTANCE = new WebRecipe();

    private WebRecipe() {
        RecipesList = Maps.newHashMap();
    }
}
