package cn.mcmod.sakura.api.recipes;

import cn.mcmod_mmf.mmlib.util.RecipesUtil;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Map.Entry;

public class LiquidToItemRecipes {
    public static final LiquidToItemRecipes INSTANCE = new LiquidToItemRecipes();

    // key: InputFluidStack, value: (key: FluidContainer, value: OutputItemStack)
    public final Map<FluidStack, Map<Object, ItemStack>> recipes = Maps.newHashMap();

    private LiquidToItemRecipes() {
    }

    public void addRecipes(Object fluidContainer, ItemStack outputItemStack, FluidStack inputFluidStack) {
        Map<Object, ItemStack> items;
        if (!recipes.containsKey(inputFluidStack)) {
            items = Maps.newHashMap();
            recipes.put(inputFluidStack, items);
        } else {
            items = recipes.get(inputFluidStack);
        }
        items.put(fluidContainer, outputItemStack);
    }

    public ItemStack getOutput(FluidStack fluidStack, ItemStack itemStack) {
        for (Entry<FluidStack, Map<Object, ItemStack>> entry : recipes.entrySet()) {
            if (!entry.getKey().isFluidEqual(fluidStack)) continue;

            for (Entry<Object, ItemStack> entry2 : entry.getValue().entrySet()) {
                if (entry2.getKey() instanceof ItemStack itemStack1) {
                    if (ItemStack.areItemsEqual(itemStack, itemStack1)) {
                        return entry2.getValue();
                    }
                } else if (entry2.getKey() instanceof String name) {
                    NonNullList<ItemStack> ore = OreDictionary.getOres(name);
                    if (!ore.isEmpty()
                            && RecipesUtil.getInstance().containsMatch(true, ore, itemStack)) {
                        return entry2.getValue();
                    }
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Nullable
    public FluidStack getInput(FluidStack output) {
        for (FluidStack entry : recipes.keySet()) {
            if (entry.isFluidEqual(output)) {
                return entry;
            }
        }
        return null;
    }

    public boolean hasInput(ItemStack input) {
        for (Map<Object, ItemStack> value : recipes.values()) {
            for (Object object : value.keySet()) {
                if (object instanceof ItemStack itemStack) {
                    if (ItemStack.areItemsEqual(itemStack, input)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void clearRecipe(FluidStack fluid, Object input) {
        recipes.get(fluid).remove(input);
    }

    public void clearAllRecipe() {
        recipes.clear();
    }
}