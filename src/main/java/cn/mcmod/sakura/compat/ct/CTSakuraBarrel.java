package cn.mcmod.sakura.compat.ct;

import cn.mcmod.sakura.api.recipes.BarrelRecipes;
import cn.mcmod.sakura.util.SakuraRecipeRegister;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.sakura.Barrel")
@ZenRegister
public class CTSakuraBarrel {
    @ZenMethod
    public static void RemoveRecipe(ILiquidStack input) {
        SakuraRecipeRegister.INSTANCE.addAction(new Removal(CraftTweakerMC.getLiquidStack(input)));
    }

    @ZenMethod
    public static void AddRecipe(ILiquidStack input_fluid, IIngredient[] input, ILiquidStack output) {
        if (input.length == 0) return;

        final Object[] array = new Object[input.length];
        IIngredient ingredient;
        for (int i = 0; i < input.length; i++) {
            ingredient = input[i];
            if (ingredient instanceof IItemStack) {
                array[i] = CraftTweakerMC.getItemStack(ingredient);
            } else if (ingredient instanceof IOreDictEntry oreDictEntry) {
                array[i] = oreDictEntry.getName();
            }
        }

        SakuraRecipeRegister.INSTANCE.addAction(new Addition(array, CraftTweakerMC.getLiquidStack(output), CraftTweakerMC.getLiquidStack(input_fluid)));
    }

    @ZenMethod
    public static void ClearAllRecipe() {
        SakuraRecipeRegister.INSTANCE.addAction(new ClearAllRecipe());
    }

    private static final class Removal implements IAction {
        private final FluidStack itemInput;

        private Removal(FluidStack itemInput) {
            this.itemInput = itemInput;
        }

        @Override
        public void apply() {
            BarrelRecipes.INSTANCE.clearRecipe(itemInput);
        }

        @Override
        public String describe() {
            return "Removing a recipe for Barrel";
        }
    }

    private static final class Addition implements IAction {
        private final Object[] itemInput;
        private final FluidStack fluidOutput;
        private final FluidStack fluidInput;

        private Addition(Object[] itemInput, FluidStack fluidOutput, FluidStack fluidInput) {
            this.itemInput = itemInput;
            this.fluidOutput = fluidOutput;
            this.fluidInput = fluidInput;
        }

        @Override
        public void apply() {
            BarrelRecipes.INSTANCE.register(fluidInput, fluidOutput, itemInput);
        }

        @Override
        public String describe() {
            return "Adding a recipe for Barrel";
        }
    }

    private static final class ClearAllRecipe implements IAction {
        @Override
        public void apply() {
            BarrelRecipes.INSTANCE.clearAllRecipe();
        }

        @Override
        public String describe() {
            return "Removing all recipes from Barrel";
        }
    }
}
