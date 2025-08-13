package cn.mcmod.sakura.api.recipes;

import cn.mcmod_mmf.mmlib.recipe.ItemToItemRecipeBase;
import com.google.common.collect.Maps;

public class WebRecipe extends ItemToItemRecipeBase {
    private WebRecipe() {
        this.RecipesList = Maps.newHashMap();
    }

    private static final WebRecipe RECIPE_BASE = new WebRecipe();

    public static WebRecipe getInstance() {
        return RECIPE_BASE;
    }
}
