package cn.mcmod.sakura.util;

import cn.mcmod.sakura.SakuraConfig;
import cn.mcmod.sakura.SakuraMain;
import cn.mcmod.sakura.api.recipes.*;
import cn.mcmod.sakura.block.BlockLoader;
import cn.mcmod.sakura.compat.CompatConst;
import cn.mcmod.sakura.item.ItemLoader;
import cn.mcmod.sakura.item.drinks.DrinksLoader;
import cn.mcmod_mmf.mmlib.recipe.UniversalFluid;
import cn.mcmod_mmf.mmlib.register.MMLibRegistries;
import cn.mcmod_mmf.mmlib.util.RecipesUtil;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.fluids.CuisineFluids;

import java.util.ArrayList;
import java.util.List;

public class SakuraRecipeRegister {
    public static final SakuraRecipeRegister INSTANCE = new SakuraRecipeRegister();

    @Nullable
    private List<IAction> actions = new ArrayList<>();

    private SakuraRecipeRegister() {
    }

    public void init() {
        UniversalFluid.get("food_oil").addFluid(BlockLoader.FOODOIL_FLUID);

        if (Loader.isModLoaded(CompatConst.TFC)) {
            UniversalFluid.get(SakuraMain.MODID, "alcohol_liqueur").addFluid(FluidsTFC.WHISKEY.get());
            UniversalFluid.get(SakuraMain.MODID, "alcohol_liqueur").addFluid(FluidsTFC.CORN_WHISKEY.get());
            UniversalFluid.get(SakuraMain.MODID, "alcohol_liqueur").addFluid(FluidsTFC.RYE_WHISKEY.get());
            UniversalFluid.get(SakuraMain.MODID, "alcohol_liqueur").addFluid(FluidsTFC.VODKA.get());
            UniversalFluid.get(SakuraMain.MODID, "alcohol_liqueur").addFluid(FluidsTFC.RUM.get());
            UniversalFluid.get(SakuraMain.MODID, "alcohol_liqueur").addFluid(FluidsTFC.CIDER.get());
        }

        if (Loader.isModLoaded(CompatConst.CUISINE)) {
            registerCuisineCompat();
        }

        registerBarrel();
        registerFurnace();
        registerLiquidToItem();
        registerMortar();
        registerPot();

        if (Loader.isModLoaded(CompatConst.CRAFT_TWEAKER)) {
            registerCraftTweakerCompat();
        }
        actions = null;
    }

    @Method(modid = CompatConst.CUISINE)
    public void registerCuisineCompat() {
        OreDictionary.registerOre("bamboo", CuisineRegistry.BAMBOO);
        UniversalFluid.get("food_oil").addFluid(CuisineFluids.EDIBLE_OIL);
        UniversalFluid.get("food_oil").addFluid(CuisineFluids.SESAME_OIL);
        UniversalFluid.get("soy_sauce").addFluid(CuisineFluids.SOY_SAUCE);
    }

    public void addAction(IAction action) {
        if (actions == null) return;
        actions.add(action);
    }

    @Method(modid = CompatConst.CRAFT_TWEAKER)
    public void registerCraftTweakerCompat() {
        for (IAction act : actions) {
            CraftTweakerAPI.apply(act);
            if (act.describe() != null) {
                SakuraMain.logger.log(Level.INFO, act.describe());
            }
        }
        actions.clear();
    }

    private void registerLiquidToItem() {
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(Items.BUCKET), FluidUtil.getFilledBucket(new FluidStack(BlockLoader.BEER_FLUID, 1000)), new FluidStack(BlockLoader.BEER_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(Items.BUCKET), FluidUtil.getFilledBucket(new FluidStack(BlockLoader.DOBUROKU_FLUID, 1000)), new FluidStack(BlockLoader.DOBUROKU_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(Items.BUCKET), FluidUtil.getFilledBucket(new FluidStack(BlockLoader.SAKE_FLUID, 1000)), new FluidStack(BlockLoader.SAKE_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(Items.BUCKET), FluidUtil.getFilledBucket(new FluidStack(BlockLoader.SHOUCHU_FLUID, 1000)), new FluidStack(BlockLoader.SHOUCHU_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(Items.BUCKET), FluidUtil.getFilledBucket(new FluidStack(BlockLoader.RED_WINE_FLUID, 1000)), new FluidStack(BlockLoader.RED_WINE_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(Items.BUCKET), FluidUtil.getFilledBucket(new FluidStack(BlockLoader.WHITE_WINE_FLUID, 1000)), new FluidStack(BlockLoader.WHITE_WINE_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(Items.BUCKET), FluidUtil.getFilledBucket(new FluidStack(BlockLoader.CHAMPAGNE_FLUID, 1000)), new FluidStack(BlockLoader.CHAMPAGNE_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(Items.BUCKET), FluidUtil.getFilledBucket(new FluidStack(BlockLoader.RUM_FLUID, 1000)), new FluidStack(BlockLoader.RUM_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(Items.BUCKET), FluidUtil.getFilledBucket(new FluidStack(BlockLoader.VODKA_FLUID, 1000)), new FluidStack(BlockLoader.VODKA_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(Items.BUCKET), FluidUtil.getFilledBucket(new FluidStack(BlockLoader.BRANDY_FLUID, 1000)), new FluidStack(BlockLoader.BRANDY_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(Items.BUCKET), FluidUtil.getFilledBucket(new FluidStack(BlockLoader.WHISKEY_FLUID, 1000)), new FluidStack(BlockLoader.WHISKEY_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(Items.BUCKET), FluidUtil.getFilledBucket(new FluidStack(BlockLoader.YEAST_FLUID, 1000)), new FluidStack(BlockLoader.YEAST_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(Items.BUCKET), FluidUtil.getFilledBucket(new FluidStack(BlockLoader.LIQUEUR_FLUID, 1000)), new FluidStack(BlockLoader.LIQUEUR_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(Items.BUCKET), FluidUtil.getFilledBucket(new FluidStack(BlockLoader.COCOA_LIQUEUR_FLUID, 1000)), new FluidStack(BlockLoader.COCOA_LIQUEUR_FLUID, 1000));

        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.cup, 1, 0), new ItemStack(DrinksLoader.alcoholic, 1, 0), new FluidStack(BlockLoader.BEER_FLUID, 400));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.cup, 1, 0), new ItemStack(DrinksLoader.alcoholic, 1, 1), new FluidStack(BlockLoader.DOBUROKU_FLUID, 200));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.cup, 1, 0), new ItemStack(DrinksLoader.alcoholic, 1, 2), new FluidStack(BlockLoader.SAKE_FLUID, 200));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.cup, 1, 0), new ItemStack(DrinksLoader.alcoholic, 1, 3), new FluidStack(BlockLoader.SHOUCHU_FLUID, 200));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.cup, 1, 0), new ItemStack(DrinksLoader.alcoholic, 1, 4), new FluidStack(BlockLoader.RED_WINE_FLUID, 200));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.cup, 1, 0), new ItemStack(DrinksLoader.alcoholic, 1, 5), new FluidStack(BlockLoader.WHITE_WINE_FLUID, 200));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.cup, 1, 0), new ItemStack(DrinksLoader.alcoholic, 1, 6), new FluidStack(BlockLoader.CHAMPAGNE_FLUID, 200));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.cup, 1, 0), new ItemStack(DrinksLoader.alcoholic, 1, 7), new FluidStack(BlockLoader.RUM_FLUID, 200));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.cup, 1, 0), new ItemStack(DrinksLoader.alcoholic, 1, 8), new FluidStack(BlockLoader.VODKA_FLUID, 200));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.cup, 1, 0), new ItemStack(DrinksLoader.alcoholic, 1, 9), new FluidStack(BlockLoader.WHISKEY_FLUID, 200));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.cup, 1, 0), new ItemStack(DrinksLoader.alcoholic, 1, 10), new FluidStack(BlockLoader.BRANDY_FLUID, 200));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.cup, 1, 0), new ItemStack(DrinksLoader.alcoholic, 1, 13), new FluidStack(BlockLoader.LIQUEUR_FLUID, 200));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.cup, 1, 0), new ItemStack(DrinksLoader.alcoholic, 1, 14), new FluidStack(BlockLoader.COCOA_LIQUEUR_FLUID, 200));

        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 1, 47), new ItemStack(DrinksLoader.bottle_alcoholic, 1, 0), new FluidStack(BlockLoader.BEER_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 1, 47), new ItemStack(DrinksLoader.bottle_alcoholic, 1, 1), new FluidStack(BlockLoader.DOBUROKU_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 1, 47), new ItemStack(DrinksLoader.bottle_alcoholic, 1, 2), new FluidStack(BlockLoader.SAKE_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 1, 47), new ItemStack(DrinksLoader.bottle_alcoholic, 1, 3), new FluidStack(BlockLoader.SHOUCHU_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 1, 47), new ItemStack(DrinksLoader.bottle_alcoholic, 1, 4), new FluidStack(BlockLoader.RED_WINE_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 1, 47), new ItemStack(DrinksLoader.bottle_alcoholic, 1, 5), new FluidStack(BlockLoader.WHITE_WINE_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 1, 47), new ItemStack(DrinksLoader.bottle_alcoholic, 1, 6), new FluidStack(BlockLoader.CHAMPAGNE_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 1, 47), new ItemStack(DrinksLoader.bottle_alcoholic, 1, 7), new FluidStack(BlockLoader.RUM_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 1, 47), new ItemStack(DrinksLoader.bottle_alcoholic, 1, 8), new FluidStack(BlockLoader.VODKA_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 1, 47), new ItemStack(DrinksLoader.bottle_alcoholic, 1, 9), new FluidStack(BlockLoader.WHISKEY_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 1, 47), new ItemStack(DrinksLoader.bottle_alcoholic, 1, 10), new FluidStack(BlockLoader.BRANDY_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 1, 47), new ItemStack(DrinksLoader.bottle_alcoholic, 1, 13), new FluidStack(BlockLoader.LIQUEUR_FLUID, 1000));
        LiquidToItemRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 1, 47), new ItemStack(DrinksLoader.bottle_alcoholic, 1, 14), new FluidStack(BlockLoader.COCOA_LIQUEUR_FLUID, 1000));
    }

    private void registerFurnace() {
        WebRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.FOODSET, 1, 143), new ItemStack(ItemLoader.FOODSET, 1, 144));
        WebRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.SEAWEED_RAW), new ItemStack(ItemLoader.MATERIAL, 1, 34));

        WebRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.FOODSET, 1, 162), new ItemStack(ItemLoader.FOODSET, 1, 163));
        WebRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.FOODSET, 1, 7), new ItemStack(ItemLoader.FOODSET, 1, 164));
        WebRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 1, 63), new ItemStack(ItemLoader.MATERIAL, 1, 64));

        WebRecipe.INSTANCE.addRecipes(new ItemStack(ItemLoader.FOODSET, 1, 166), new ItemStack(ItemLoader.FOODSET, 1, 167));

        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemLoader.FOODSET, 1, 135), new ItemStack(ItemLoader.FOODSET, 1, 138), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemLoader.FOODSET, 1, 147), new ItemStack(ItemLoader.FOODSET, 1, 148), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemLoader.FOODSET, 1, 144), new ItemStack(ItemLoader.FOODSET, 1, 145), 0.1F);

        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemLoader.TARO), new ItemStack(ItemLoader.FOODSET, 1, 170), 0.1F);

        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(BlockLoader.IRON_SAND), new ItemStack(Items.IRON_INGOT), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemLoader.MATERIAL, 1, 38), new ItemStack(ItemLoader.FOODSET, 1, 165), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemLoader.MATERIAL, 1, 21), new ItemStack(ItemLoader.MATERIAL, 1, 22), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemLoader.EGGPLANT, 1), new ItemStack(ItemLoader.FOODSET, 1, 87), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemLoader.MATERIAL, 1, 17), new ItemStack(ItemLoader.FOODSET, 1, 2), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemLoader.MATERIAL, 1, 15), new ItemStack(ItemLoader.MATERIAL, 1, 31), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemLoader.MATERIAL, 1, 7), new ItemStack(ItemLoader.FOODSET, 1, 131), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemLoader.FOODSET, 1, 48), new ItemStack(ItemLoader.FOODSET, 1, 49), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemLoader.FOODSET, 1, 58), new ItemStack(ItemLoader.FOODSET, 1, 59), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemLoader.MATERIAL, 1, 6), new ItemStack(ItemLoader.FOODSET, 1, 4), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemLoader.FOODSET, 1, 112), new ItemStack(ItemLoader.FOODSET, 1, 113), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(ItemLoader.MATERIAL, 1, 31), new ItemStack(ItemLoader.FOODSET, 1, 73), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(BlockLoader.MAPLE_LOG, 1), new ItemStack(Items.COAL, 1, 1), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(BlockLoader.SAKURA_LOG, 1), new ItemStack(Items.COAL, 1, 1), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(BlockLoader.BAMBOO_BLOCK, 1), new ItemStack(BlockLoader.BAMBOO_CHARCOAL_BLOCK), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(BlockLoader.BAMBOO, 1), new ItemStack(ItemLoader.MATERIAL, 1, 51), 0.1F);

        if (SakuraConfig.HARDER_IRON_RECIPE) {
            switch (SakuraConfig.HARDER_IRON_DIFFICULT) {
                case 1:
                    FurnaceRecipes.instance().getSmeltingList().forEach((key, value) -> {
                        if (RecipesUtil.getInstance().containsMatch(false, OreDictionary.getOres("ingotIron"), value))
                            FurnaceRecipes.instance().getSmeltingList().put(key, new ItemStack(ItemLoader.MATERIAL, 1, 54));
                    });
                    break;
                case 2:
                    FurnaceRecipes.instance().getSmeltingList().forEach((key, value) -> {
                        if (RecipesUtil.getInstance().containsMatch(false, OreDictionary.getOres("ingotIron"), value))
                            FurnaceRecipes.instance().getSmeltingList().put(key, new ItemStack(ItemLoader.MATERIAL, 1, 53));
                    });
                    break;
                default:
                    FurnaceRecipes.instance().getSmeltingList().forEach((key, value) -> {
                        if (RecipesUtil.getInstance().containsMatch(false, OreDictionary.getOres("ingotIron"), value))
                            FurnaceRecipes.instance().getSmeltingList().put(key, new ItemStack(ItemLoader.MATERIAL, 1, 52));
                    });
                    break;
            }
        }
    }

    private void registerBarrel() {
        BarrelRecipes.INSTANCE.register(
                getUniversalFluid("water", 1000),
                new FluidStack(BlockLoader.YEAST_FLUID, 500),
                new Object[]{
                        "listAllfruit",
                        "listAllgrain",
                        "listAllsugar"
                });
        BarrelRecipes.INSTANCE.register(
                getUniversalFluid("water", 200),
                new FluidStack(BlockLoader.BEER_FLUID, 200),
                new Object[]{
                        "cropWheat",
                        "foodYeast",
                        "cropHops"
                });
        BarrelRecipes.INSTANCE.register(
                new FluidStack(BlockLoader.GRAPE_FLUID, 200),
                new FluidStack(BlockLoader.RED_WINE_FLUID, 100),
                new Object[]{
                        "listAllsugar",
                        "foodYeast"
                });
        BarrelRecipes.INSTANCE.register(
                new FluidStack(BlockLoader.GREEN_GRAPE_FLUID, 200),
                new FluidStack(BlockLoader.WHITE_WINE_FLUID, 100),
                new Object[]{
                        "listAllsugar",
                        "foodYeast"
                });
        BarrelRecipes.INSTANCE.register(
                new FluidStack(BlockLoader.WHITE_WINE_FLUID, 200),
                new FluidStack(BlockLoader.CHAMPAGNE_FLUID, 100),
                new Object[]{
                        "listAllsugar",
                        "foodYeast"
                });
        BarrelRecipes.INSTANCE.register(
                getUniversalFluid("water", 200),
                new FluidStack(BlockLoader.DOBUROKU_FLUID, 100),
                new Object[]{
                        new ItemStack(ItemLoader.FOODSET, 1, 7),
                        "foodYeast"
                });
        BarrelRecipes.INSTANCE.register(
                new FluidStack(BlockLoader.DOBUROKU_FLUID, 200),
                new FluidStack(BlockLoader.SAKE_FLUID, 100));
        DistillationRecipes.INSTANCE.register(
                new FluidStack(BlockLoader.SAKE_FLUID, 200),
                new FluidStack(BlockLoader.SHOUCHU_FLUID, 100));
        DistillationRecipes.INSTANCE.register(
                getUniversalFluid("water", 200),
                new FluidStack(BlockLoader.WHISKEY_FLUID, 100),
                new Object[]{
                        "listAllgrain",
                        "listAllgrain",
                        "listAllgrain"
                });
        DistillationRecipes.INSTANCE.register(
                new FluidStack(BlockLoader.GRAPE_FLUID, 200),
                new FluidStack(BlockLoader.BRANDY_FLUID, 100),
                new Object[]{
                        "listAllsugar"
                });
        DistillationRecipes.INSTANCE.register(
                new FluidStack(BlockLoader.GREEN_GRAPE_FLUID, 200),
                new FluidStack(BlockLoader.BRANDY_FLUID, 100),
                new Object[]{
                        "listAllsugar"
                });
        DistillationRecipes.INSTANCE.register(
                getUniversalFluid("water", 200),
                new FluidStack(BlockLoader.VODKA_FLUID, 100),
                new Object[]{
                        "cropPotato",
                        "cropPotato",
                        "cropPotato"
                });
        DistillationRecipes.INSTANCE.register(
                getUniversalFluid("water", 200),
                new FluidStack(BlockLoader.RUM_FLUID, 100),
                new Object[]{
                        "sugarcane",
                        "sugarcane",
                        "sugarcane"
                });
        DistillationRecipes.INSTANCE.register(
                getUniversalFluid("alcohol_liqueur", SakuraMain.MODID, 200),
                new FluidStack(BlockLoader.LIQUEUR_FLUID, 200),
                new Object[]{
                        "listAllfruit",
                        "listAllfruit",
                        "listAllsugar"
                });
        DistillationRecipes.INSTANCE.register(
                getUniversalFluid("alcohol_liqueur", SakuraMain.MODID, 200),
                new FluidStack(BlockLoader.COCOA_LIQUEUR_FLUID, 200),
                new Object[]{
                        new ItemStack(Items.DYE, 1, 3),
                        new ItemStack(Items.DYE, 1, 3),
                        "listAllsugar"
                });
    }

    private void registerMortar() {
        MortarRecipes.INSTANCE.addMortarRecipes(
                new ItemStack[]{new ItemStack(ItemLoader.FOODSET, 32, 146)},
                new ItemStack[]{
                        new ItemStack(ItemLoader.FOODSET, 1, 145),
                        new ItemStack(ItemLoader.FOODSET, 1, 145),
                        new ItemStack(ItemLoader.FOODSET, 1, 145),
                        new ItemStack(ItemLoader.FOODSET, 1, 145)
                });
        MortarRecipes.INSTANCE.addMortarRecipes(
                new ItemStack[]{new ItemStack(ItemLoader.MATERIAL, 6, 43)},
                new Object[]{
                        "cropTea",
                        "cropTea",
                        "cropTea",
                        "cropTea"
                });
        MortarRecipes.INSTANCE.addMortarRecipes(
                new ItemStack[]{new ItemStack(ItemLoader.MATERIAL, 2, 38)},
                new ItemStack[]{
                        new ItemStack(ItemLoader.RICE_SEEDS),
                        new ItemStack(ItemLoader.RICE_SEEDS),
                        new ItemStack(ItemLoader.RICE_SEEDS),
                        new ItemStack(ItemLoader.RICE_SEEDS)
                });
        MortarRecipes.INSTANCE.addMortarRecipes(
                new ItemStack[]{new ItemStack(ItemLoader.MATERIAL, 4, 1)},
                new ItemStack[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 38),
                        new ItemStack(ItemLoader.MATERIAL, 1, 38),
                        new ItemStack(ItemLoader.MATERIAL, 1, 38),
                        new ItemStack(ItemLoader.MATERIAL, 1, 38)
                });
        MortarRecipes.INSTANCE.addMortarRecipes(
                new ItemStack[]{new ItemStack(ItemLoader.MATERIAL, 2, 16)},
                new ItemStack[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 1),
                        new ItemStack(ItemLoader.MATERIAL, 1, 1)
                });
        MortarRecipes.INSTANCE.addMortarRecipes(
                new ItemStack[]{new ItemStack(ItemLoader.MATERIAL, 2, 5)},
                new Object[]{
                        "cropBuckwheat",
                        "cropBuckwheat"
                });
        MortarRecipes.INSTANCE.addMortarRecipes(
                new ItemStack[]{new ItemStack(ItemLoader.MATERIAL, 4, 4)},
                new Object[]{
                        "listAllgrain",
                        "listAllgrain",
                        "listAllgrain",
                        "listAllgrain"
                });
        MortarRecipes.INSTANCE.addMortarRecipes(
                new ItemStack[]{new ItemStack(Blocks.GRAVEL), new ItemStack(ItemLoader.MATERIAL, 1, 2)},
                new Object[]{
                        "cobblestone"
                });
        MortarRecipes.INSTANCE.addMortarRecipes(
                new ItemStack[]{new ItemStack(Blocks.COBBLESTONE), new ItemStack(ItemLoader.MATERIAL, 2, 35)},
                new Object[]{
                        "stone"
                });
        MortarRecipes.INSTANCE.addMortarRecipes(
                new ItemStack[]{new ItemStack(Blocks.SAND), new ItemStack(Items.FLINT)},
                new Object[]{
                        "gravel"
                });
        MortarRecipes.INSTANCE.addMortarRecipes(
                new ItemStack[]{new ItemStack(Items.DYE, 6, 2)},
                new Object[]{
                        "treeLeaves",
                        "treeLeaves",
                        "treeLeaves",
                        "treeLeaves"
                });
        MortarRecipes.INSTANCE.addMortarRecipes(
                new ItemStack[]{new ItemStack(Items.BLAZE_POWDER, 3), new ItemStack(Items.BLAZE_POWDER, 2)},
                new Object[]{
                        new ItemStack(Items.BLAZE_ROD)
                });
        MortarRecipes.INSTANCE.addMortarRecipes(
                new ItemStack[]{new ItemStack(Items.DYE, 3, 15), new ItemStack(Items.DYE, 2, 15)},
                new Object[]{
                        "bone"
                });

        MortarRecipes.INSTANCE.addMortarRecipes(
                new ItemStack[]{new ItemStack(ItemLoader.MATERIAL, 4, 30)},
                new Object[]{
                        new ItemStack(ItemLoader.FOODSET, 1, 4)
                });
        MortarRecipes.INSTANCE.addMortarRecipes(
                new ItemStack[]{new ItemStack(ItemLoader.MATERIAL, 2, 30)},
                new Object[]{
                        "bread"
                });

        MortarRecipes.INSTANCE.addMortarRecipes(
                new ItemStack[]{new ItemStack(ItemLoader.FOODSET, 8, 80)},
                new Object[]{
                        "cropPotato",
                        "listAllfishfresh",
                        "listAllfishfresh",
                        "listAllegg"
                });
    }

    private void registerPot() {
        PotRecipes.INSTANCE.addRecipes(new ItemStack(ItemLoader.IMOGARANAWA),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 64),
                        "foodMiso",
                },
                getUniversalFluid("water", 50));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 5, 66),
                new Object[]{
                        "foodMiso",
                        "foodMiso",
                        "foodMiso",
                },
                getUniversalFluid("water", 50));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 2, 61),
                new Object[]{
                        "listAllsugar",
                        "cropLemon",
                        "listAllegg",
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(ItemLoader.MATERIAL, 2, 60),
                new Object[]{
                        "listAllsugar",
                        "listAllfruit",
                        "foodSoysauce",
                        "foodSoysauce",
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(ItemLoader.FOODSET, 2, 129),
                new Object[]{
                        "listAllegg",
                        "listAllegg",
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(ItemLoader.FOODSET, 2, 130),
                new Object[]{
                        "listAllegg",
                        "listAllegg",
                        "foodSoysauce",
                        "listAllsugar",
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(ItemLoader.FOODSET, 2, 76),
                new Object[]{
                        "foodSoysauce",
                        "listAllsugar",
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(ItemLoader.FOODSET, 1, 126),
                new Object[]{
                        "foodPasta",
                        "foodKetchup"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(ItemLoader.FOODSET, 1, 127),
                new Object[]{
                        "foodPasta",
                        "listAllmushroom",
                        "listAllmushroom"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(ItemLoader.FOODSET, 1, 128),
                new Object[]{
                        "foodPasta",
                        new ItemStack(ItemLoader.MATERIAL, 1, 13)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(DrinksLoader.tea, 1, 0),
                new Object[]{
                        new ItemStack(ItemLoader.cup, 1, 0),
                        new ItemStack(ItemLoader.MATERIAL, 1, 40),
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(DrinksLoader.tea, 1, 1),
                new Object[]{
                        new ItemStack(ItemLoader.cup, 1, 0),
                        "cropTea"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(DrinksLoader.tea, 1, 2),
                new Object[]{
                        new ItemStack(ItemLoader.cup, 1, 0),
                        new ItemStack(ItemLoader.MATERIAL, 1, 40),
                        "listAllmilk"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(DrinksLoader.tea, 1, 3),
                new Object[]{
                        new ItemStack(ItemLoader.cup, 1, 0),
                        "cropTea",
                        "listAllmilk"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(DrinksLoader.tea, 1, 4),
                new Object[]{
                        new ItemStack(ItemLoader.cup, 1, 0),
                        new ItemStack(ItemLoader.MATERIAL, 1, 41)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(DrinksLoader.tea, 1, 5),
                new Object[]{
                        new ItemStack(ItemLoader.cup, 1, 0),
                        new ItemStack(ItemLoader.MATERIAL, 1, 41),
                        "listAllmilk"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(DrinksLoader.tea, 1, 6),
                new Object[]{
                        new ItemStack(ItemLoader.cup, 1, 0),
                        new ItemStack(ItemLoader.MATERIAL, 1, 42)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(DrinksLoader.tea, 1, 7),
                new Object[]{
                        new ItemStack(ItemLoader.cup, 1, 0),
                        new ItemStack(ItemLoader.MATERIAL, 1, 42),
                        "listAllmilk"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(DrinksLoader.tea, 1, 8),
                new Object[]{
                        new ItemStack(ItemLoader.cup, 1, 0),
                        new ItemStack(ItemLoader.MATERIAL, 1, 40),
                        "cropLemon"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(DrinksLoader.tea, 1, 9),
                new Object[]{
                        new ItemStack(ItemLoader.cup, 1, 0),
                        "cropTea",
                        "cropLemon"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(DrinksLoader.tea, 1, 10),
                new Object[]{
                        new ItemStack(ItemLoader.cup, 1, 0),
                        new ItemStack(ItemLoader.MATERIAL, 1, 46)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(DrinksLoader.tea, 1, 11),
                new Object[]{
                        new ItemStack(ItemLoader.cup, 1, 0),
                        "listAllgrain",
                        "listAllgrain"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(new ItemStack(DrinksLoader.tea, 1, 12),
                new Object[]{
                        new ItemStack(ItemLoader.cup, 1, 0),
                        new ItemStack(ItemLoader.MATERIAL, 1, 44)
                },
                getUniversalFluid("water", 200));

        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 1, 82),
                new Object[]{"listAlltofu"},
                getUniversalFluid("food_oil", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 99),
                new Object[]{
                        "listAllegg",
                        "listAllegg",
                        "listAllsugar"
                },
                getUniversalFluid("food_oil", 100));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.MATERIAL, 1, 12),
                new Object[]{
                        "cropTomato",
                        "cropTomato",
                        "listAllsugar"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 90),
                new Object[]{
                        "listAllbeefraw",
                        "bamboo"
                });
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 91),
                new Object[]{
                        "listAllchickenraw",
                        "bamboo"
                });
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 92),
                new Object[]{
                        "listAllporkraw",
                        "bamboo"
                });
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 162),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 38)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 7),
                new Object[]{
                        "cropRice"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 10),
                new Object[]{
                        "cropRice",
                        "listAllbeefraw"
                },
                getUniversalFluid("water", 200));

        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 139),
                new Object[]{
                        "cropRice",
                        new ItemStack(ItemLoader.FOODSET, 1, 135),
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 8),
                new Object[]{
                        "cropRice",
                        "cropBambooshoot"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 9),
                new Object[]{
                        "cropRice",
                        "listAllfishraw"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 11),
                new Object[]{
                        "cropRice",
                        "listAllporkraw"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 12),
                new Object[]{
                        "cropRice",
                        "listAllmushroom"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 13),
                new Object[]{
                        "cropRice",
                        "listAllegg"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 14),
                new Object[]{
                        "cropRice",
                        "listAllbeefraw",
                        "listAllegg"
                },
                getUniversalFluid("water", 200));

        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 15),
                new Object[]{
                        "cropRice",
                        "listAllporkraw",
                        "listAllegg"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 16),
                new Object[]{
                        "cropRice",
                        new ItemStack(ItemLoader.FOODSET, 1, 6),
                        "listAllegg"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 17),
                new Object[]{
                        "cropRice",
                        "listAllchickenraw",
                        "listAllegg"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 18),
                new Object[]{
                        "cropRice",
                        "listAllfishraw",
                        "listAllegg"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 65),
                new Object[]{
                        "cropRice",
                        "foodNatto",
                        "listAllegg"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 64),
                new Object[]{
                        "cropRice",
                        "foodNatto"
                },
                getUniversalFluid("water", 200));

        // fantuan
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 140),
                new Object[]{
                        "cropRice",
                        new ItemStack(ItemLoader.FOODSET, 1, 135),
                        "cropSeaweed"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 42),
                new Object[]{
                        "cropRice",
                        "cropSeaweed"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 43),
                new Object[]{
                        "cropRice",
                        "cropSeaweed",
                        "cropBambooshoot"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 44),
                new Object[]{
                        "cropRice",
                        "cropSeaweed",
                        "listAllfishraw"
                },
                getUniversalFluid("water", 200));

        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 45),
                new Object[]{
                        "cropRice",
                        "cropSeaweed",
                        "listAllmushroom"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 46),
                new Object[]{
                        "cropRice",
                        "cropSeaweed",
                        "cropSeaweed"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 47),
                new Object[]{
                        "cropRice",
                        "cropSeaweed",
                        new ItemStack(ItemLoader.FOODSET, 1, 79),
                },
                getUniversalFluid("water", 200));
        //
        /*
         * RAMEN
         */
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 19),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 8),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 20),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 8),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        "listAllbeefraw"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 21),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 8),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        "listAllegg"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 22),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 8),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        new ItemStack(ItemLoader.FOODSET, 1, 79)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 23),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 8),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        "listAlltofuFried"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 24),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 8),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        new ItemStack(ItemLoader.FOODSET, 1, 6)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 25),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 8),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        "listAllmeatraw",
                        "listAllveggie",
                        "listAllegg"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 93),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 8),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        new ItemStack(ItemLoader.FOODSET, 1, 62),
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 96),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 8),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        new ItemStack(ItemLoader.FOODSET, 1, 88),
                },
                getUniversalFluid("water", 200));
        /*
         * UDON
         */
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 94),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 9),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        new ItemStack(ItemLoader.FOODSET, 1, 62),
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 26),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 9),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 27),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 9),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        "listAllbeefraw"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 28),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 9),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        "listAllegg"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 29),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 9),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        new ItemStack(ItemLoader.FOODSET, 1, 79)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 30),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 9),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        "listAlltofuFried"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 31),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 9),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        new ItemStack(ItemLoader.FOODSET, 1, 6)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 32),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 9),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        "listAllmeatraw",
                        "listAllveggie",
                        "listAllegg"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 97),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 9),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        new ItemStack(ItemLoader.FOODSET, 1, 88),
                },
                getUniversalFluid("water", 200));
        /*
         * SOBA
         */
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 95),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 10),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        new ItemStack(ItemLoader.FOODSET, 1, 62),
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 98),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 10),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        new ItemStack(ItemLoader.FOODSET, 1, 88),
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 34),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 10),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 35),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 10),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        "listAllbeefraw"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 36),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 10),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        "listAllegg"
                },
                getUniversalFluid("water", 200));

        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 37),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 10),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        new ItemStack(ItemLoader.FOODSET, 1, 79)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 38),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 10),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        "listAlltofuFried"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 39),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 10),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        new ItemStack(ItemLoader.FOODSET, 1, 6)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 40),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 10),
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),
                        "listAllmeatraw",
                        "listAllveggie",
                        "listAllegg"
                },
                getUniversalFluid("water", 200));

        // others
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 3),
                new Object[]{
                        new ItemStack(ItemLoader.RED_BEAN, 1),
                        "listAllsugar",
                        "listAllsugar"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 6),
                new Object[]{
                        "listAllporkraw",
                        "foodFlour",
                        "listAllegg",
                        "foodBreadcrumbs",
                        "dustSalt",
                        "foodBlackpepper"
                },
                getUniversalFluid("food_oil", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 3, 88),
                new Object[]{
                        "listAllchickencooked",
                        "foodFlour",
                        "listAllegg",
                        "foodBreadcrumbs",
                        "dustSalt",
                        "foodBlackpepper"
                },
                getUniversalFluid("food_oil", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 4, 62),
                new Object[]{
                        new ItemStack(ItemLoader.FOODSET, 1, 48),
                        new ItemStack(ItemLoader.FOODSET, 1, 57),
                        "listAllegg",
                        "dustSalt",
                        "foodBlackpepper"
                },
                getUniversalFluid("food_oil", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 79),
                new Object[]{
                        "foodShrimpraw",
                        new ItemStack(ItemLoader.MATERIAL, 1, 14),
                },
                getUniversalFluid("food_oil", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 68),
                new Object[]{
                        "cropRice",
                        "listAllmeatraw",
                        "listAllegg",
                        "listAllveggie"
                },
                getUniversalFluid("food_oil", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 33),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 9),
                        "listAllmeatraw",
                        "listAllveggie",
                        "foodHoisinsause"
                },
                getUniversalFluid("food_oil", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 155),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 8),
                        "listAllmeatraw",
                        "listAllveggie",
                        "foodHoisinsause"
                },
                getUniversalFluid("food_oil", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 155),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 10),
                        "listAllmeatraw",
                        "listAllveggie",
                        "foodHoisinsause"
                },
                getUniversalFluid("food_oil", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 156),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 57),
                        "listAllmeatraw",
                        "listAllveggie",
                        "foodHoisinsause"
                },
                getUniversalFluid("food_oil", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 107),
                new Object[]{
                        "cropPotato",
                        "dustSalt",
                        "foodBlackpepper"
                },
                getUniversalFluid("food_oil", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 4, 57),
                new Object[]{
                        "cropPotato",
                        "cropPotato",
                        "dustSalt"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 105),
                new Object[]{
                        "cropRice",
                        new ItemStack(ItemLoader.FOODSET, 1, 3),
                        "dustSalt",
                        "listAllsugar"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 67),
                new Object[]{
                        "cropRice",
                        "cropTomato",
                        "listAllmeatraw",
                        "foodKetchup"
                },
                getUniversalFluid("food_oil", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 77),
                new Object[]{
                        "cropRice",
                        "listAllegg",
                        "foodWhitepepper",
                        "foodSoysauce",
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 66),
                new Object[]{
                        "listAllbeefraw",
                        "cropPotato",
                        "cropCarrot",
                        "foodSoysauce",
                        "listAllsugar"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 74),
                new Object[]{
                        new ItemStack(ItemLoader.FOODSET, 1, 59),
                        new ItemStack(ItemLoader.RED_BEAN, 1)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 83),
                new Object[]{
                        "stickWood",
                        new ItemStack(ItemLoader.FOODSET, 1, 80),
                        "cropRadish",
                        "listAllegg"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.MATERIAL, 2, 11),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 3),
                        "cropOnion",
                        "cropPotato",
                        "cropCarrot",
                        "listAllmeatraw"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.MATERIAL, 2, 11),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 3),
                        "cropOnion",
                        "cropPotato",
                        "cropCarrot",
                        "listAllmeatraw"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.MATERIAL, 2, 13),
                new Object[]{
                        "listAllmilk",
                        "foodFlour",
                        "dustSalt",
                        "foodWhitepepper"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 110),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 13),
                        "listAllchickenraw",
                        "cropCarrot",
                        "cropPotato",
                        "listAllmushroom"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 151),
                new Object[]{
                        "cropPumpkin",
                        "cropPumpkin",
                        "listAllsugar",
                        "foodSoysause"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 152),
                new Object[]{
                        "cropRadish",
                        "cropRadish",
                        "listAllsugar",
                        "foodSoysause"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 153),
                new Object[]{
                        "listAllfishraw",
                        "dustSalt",
                        "foodMiso",
                        "foodSoysause"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 154),
                new Object[]{
                        "listAllchickenraw",
                        "listAllveggie",
                        "listAllveggie",
                        "dustSalt",
                        "foodSoysause"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 109),
                new Object[]{
                        "cropCabbage",
                        new ItemStack(ItemLoader.FOODSET, 1, 48),
                        "dustSalt",
                        "foodKetchup",
                        "foodWhitepepper"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 108),
                new Object[]{
                        "cropRadish",
                        "foodMiso",
                        "dustSalt"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 103),
                new Object[]{
                        "listAllmilk",
                        "listAllsugar",
                        "listAllegg",
                        "foodVanilla"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 159),
                new Object[]{
                        "listAllmilk",
                        "listAllsugar",
                        "listAllegg",
                        "foodVanilla",
                        "foodMocha"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 104),
                new Object[]{
                        "listAllmilk",
                        "listAllsugar",
                        "listAllegg",
                        "foodVanilla",
                        "cropMaplesyrup"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 106),
                new Object[]{
                        "listAllfishfresh",
                        "foodSoysauce",
                        "listAllsugar",
                        "dustSalt"
                },
                getUniversalFluid("food_oil", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 175),
                new Object[]{
                        "listAllfishfresh",
                        "cropRadish",
                        "dustSalt",
                        "dustSalt"
                },
                getUniversalFluid("food_oil", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.MATERIAL, 4, 32),
                new Object[]{
                        new ItemStack(ItemLoader.FOODSET, 1, 146),
                        "foodSoysauce",
                        "cropSeaweed",
                        "dustSalt"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 111),
                new Object[]{
                        new ItemStack(Items.APPLE),
                        "listAllsugar",
                        "listAllfruit"
                },
                getUniversalFluid("water", 100));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 111),
                new Object[]{
                        new ItemStack(Items.CHORUS_FRUIT),
                        "listAllsugar",
                        "listAllfruit"
                },
                getUniversalFluid("water", 100));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 47),
                new Object[]{
                        "cropRice",
                        "cropSeaweed",
                        new ItemStack(ItemLoader.FOODSET, 1, 79),
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 84),
                new Object[]{
                        new ItemStack(ItemLoader.FOODSET, 1, 7),
                        "bamboo",
                        new ItemStack(ItemLoader.FOODSET, 1, 3),
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 85),
                new Object[]{
                        new ItemStack(ItemLoader.FOODSET, 1, 7),
                        "bamboo",
                        "listAllsugar",
                        "listAllsugar"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 86),
                new Object[]{
                        new ItemStack(ItemLoader.FOODSET, 1, 7),
                        "bamboo",
                        "listAllsugar",
                        new ItemStack(BlockLoader.SAKURA_LEAVES),
                        new ItemStack(Blocks.TALLGRASS, 1, 1)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 86),
                new Object[]{
                        new ItemStack(ItemLoader.FOODSET, 1, 7),
                        "bamboo",
                        "listAllsugar",
                        new ItemStack(BlockLoader.SAKURA_LEAVES),
                        new ItemStack(Blocks.TALLGRASS, 1, 2)
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 161),
                new Object[]{
                        "listAlltofu",
                        "foodMiso",
                        "dustSalt"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 161),
                new Object[]{
                        "listAlltofu",
                        new ItemStack(ItemLoader.MATERIAL, 1, 66),
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 1, 143),
                new Object[]{
                        new ItemStack(ItemLoader.FOODSET, 1, 142)
                },
                getUniversalFluid("water", 100));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 4, 58),
                new Object[]{
                        new ItemStack(ItemLoader.MATERIAL, 1, 17),
                        new ItemStack(ItemLoader.MATERIAL, 1, 17),
                        "listAllsugar"
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 132),
                new Object[]{
                        "listAllegg",
                        "listAllveggie",
                        new ItemStack(ItemLoader.MATERIAL, 1, 32),

                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 169),
                new Object[]{
                        "cropRice",
                        "listAllmeatraw",
                        "listAllveggie",
                        "foodSoysauce",
                },
                getUniversalFluid("water", 200));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 1, 168),
                new Object[]{
                        "cropRice",
                        new ItemStack(ItemLoader.MATERIAL, 1, 65)
                },
                getUniversalFluid("water", 100));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 3, 173),
                new Object[]{
                        "foodFlour",
                        "cropRice",
                        "listAllsugar",
                        "cropTaro",
                },
                getUniversalFluid("water", 50));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.FOODSET, 2, 174),
                new Object[]{
                        "foodFlour",
                        new ItemStack(ItemLoader.FOODSET, 1, 167),
                        "listAllsugar"
                },
                getUniversalFluid("water", 0));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.MATERIAL, 2, 33),
                new Object[]{
                        "listAllsugar"
                },
                getUniversalFluid("soy_sauce", 100));
        PotRecipes.INSTANCE.addRecipes(
                new ItemStack(ItemLoader.MATERIAL, 2, 29),
                new Object[]{
                        "foodFlour",
                        "cropSoybean",
                        "dustSalt"
                },
                getUniversalFluid("water", 100));
    }

    private static List<FluidStack> getUniversalFluid(String name, int amount) {
        return getUniversalFluid(name, "mm_lib", amount);
    }

    private static List<FluidStack> getUniversalFluid(String name, String modid, int amount) {
        return MMLibRegistries.UNIVERSAL_FLUID.getValue(RLUtil.of(modid, name)).getFluidList(amount);
    }
}
