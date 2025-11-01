package cn.mcmod.sakura.compat.jei.category;

import cn.mcmod.sakura.SakuraMain;
import cn.mcmod.sakura.block.BlockLoader;
import cn.mcmod.sakura.util.RLUtil;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class CategoryBarrel implements IRecipeCategory<IRecipeWrapper> {
    private final IDrawable background;
    private final IDrawable icon;

    public CategoryBarrel(IGuiHelper helper) {
        final var bgRL = RLUtil.of("textures/gui/jei_compat.png");
        icon = helper.createDrawableIngredient(new ItemStack(BlockLoader.BARREL));
        background = helper.createDrawable(bgRL, 0, 0, 100, 80);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public String getModName() {
        return SakuraMain.NAME;
    }

    @Override
    public String getTitle() {
        return I18n.format("jei.sakura.category.barrel");
    }

    @Override
    public String getUid() {
        return "sakura.barrel";
    }

    @Override
    public void setRecipe(IRecipeLayout layout, IRecipeWrapper wrapper, IIngredients ingredients) {
        final var items = layout.getItemStacks();
        items.init(0, true, 29, 13);
        items.init(1, true, 29, 31);
        items.init(2, true, 29, 49);
        items.set(ingredients);
        IGuiFluidStackGroup fiuld = layout.getFluidStacks();
        fiuld.init(0, true, 6, 6, 16, 68, ingredients.getInputs(VanillaTypes.FLUID).get(0).get(0).amount, false, null);
        fiuld.set(0, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        fiuld.init(1, false, 77, 6, 16, 68, ingredients.getOutputs(VanillaTypes.FLUID).get(0).get(0).amount, false, null);
        fiuld.set(1, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

}
