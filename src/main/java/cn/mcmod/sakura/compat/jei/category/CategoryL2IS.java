package cn.mcmod.sakura.compat.jei.category;

import cn.mcmod.sakura.SakuraMain;
import cn.mcmod.sakura.util.RLUtil;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CategoryL2IS implements IRecipeCategory<IRecipeWrapper> {
    private final IDrawable background;
    private final IDrawable icon;

    public CategoryL2IS(IGuiHelper helper) {
        final var bgRL = RLUtil.of("textures/gui/jei_compat.png");
        icon = helper.createDrawableIngredient(new ItemStack(Items.BUCKET));
        background = helper.createDrawable(bgRL, 101, 0, 93, 80);
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
        return I18n.format("jei.sakura.category.liquid_item");
    }

    @Override
    public String getUid() {
        return "sakura.liquid_item";
    }

    @Override
    public void setRecipe(IRecipeLayout arg0, IRecipeWrapper arg1, IIngredients arg2) {
        IGuiItemStackGroup items = arg0.getItemStacks();
        items.init(0, true, 13, 14);
        items.init(1, false, 62, 14);
        items.set(arg2);
        IGuiFluidStackGroup group = arg0.getFluidStacks();
        group.init(0, true, 14, 47, 65, 20, arg2.getInputs(VanillaTypes.FLUID).get(0).get(0).amount, false, null);
        group.set(0, arg2.getInputs(VanillaTypes.FLUID).get(0));
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

}
