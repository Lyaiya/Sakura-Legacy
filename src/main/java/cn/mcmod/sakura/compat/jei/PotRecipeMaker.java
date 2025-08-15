package cn.mcmod.sakura.compat.jei;

import cn.mcmod.sakura.api.recipes.PotRecipes;
import com.google.common.collect.Lists;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public final class PotRecipeMaker {
    public static List<ItemFluidRecipe> getRecipes(IJeiHelpers helpers) {
        IStackHelper stackHelper = helpers.getStackHelper();
        List<ItemFluidRecipe> recipes = new ArrayList<>();
        for (Entry<Pair<Object[], ItemStack>, List<FluidStack>> entry : PotRecipes.INSTANCE.recipes.entrySet()) {
            List<List<ItemStack>> inputs = new ArrayList<>();
            List<List<FluidStack>> fluidList = new ArrayList<>();
            for (Object obj : entry.getKey().getLeft()) {
                List<ItemStack> subInputs = stackHelper.toItemStackList(obj);
                inputs.add(subInputs);
            }
            if (!entry.getValue().isEmpty())
                fluidList.add(entry.getValue());
            else
                fluidList.add(Lists.newArrayList(new FluidStack(FluidRegistry.WATER, 0)));
            ItemFluidRecipe newrecipe = new ItemFluidRecipe(inputs, fluidList, entry.getKey().getRight());
            recipes.add(newrecipe);
        }
        return recipes;

    }
}
