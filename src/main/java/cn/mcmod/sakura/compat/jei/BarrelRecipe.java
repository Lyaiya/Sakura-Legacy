package cn.mcmod.sakura.compat.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class BarrelRecipe implements IRecipeWrapper {
    private final List<List<ItemStack>> inputItemStacks;
    private final List<List<FluidStack>> inputFluidStacks;
    private final FluidStack outputFluidStack;

    public BarrelRecipe(List<List<ItemStack>> inputItemStacks, List<List<FluidStack>> inputFluidStacks, FluidStack outputFluidStack) {
        this.inputItemStacks = inputItemStacks;
        this.inputFluidStacks = inputFluidStacks;
        this.outputFluidStack = outputFluidStack;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, this.inputItemStacks);
        ingredients.setInputLists(VanillaTypes.FLUID, this.inputFluidStacks);
        ingredients.setOutput(VanillaTypes.FLUID, this.outputFluidStack);
    }

}
