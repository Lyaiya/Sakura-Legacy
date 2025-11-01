package cn.mcmod.sakura.event;

import cn.mcmod.sakura.SakuraConfig;
import cn.mcmod.sakura.SakuraMain;
import cn.mcmod.sakura.api.armor.ArmorLoader;
import cn.mcmod.sakura.block.BlockLoader;
import cn.mcmod.sakura.item.ItemLoader;
import cn.mcmod.sakura.packet.PacketKeyMessage;
import cn.mcmod.sakura.proxy.ClientProxy;
import cn.mcmod.sakura.util.RLUtil;
import cn.mcmod_mmf.mmlib.recipe.UniversalFluid;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber
public class SakuraEventLoader {
    private static final Set<String> LOOT_LOCATIONS = ImmutableSet.<String>builder()
            .add(LootTableList.GAMEPLAY_FISHING_FISH.toString())
            .build();

    private final static Set<LootEntry> FISHING_LOOT_POOLS = new HashSet<>();

    @SubscribeEvent
    public static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        if (ItemStack.areItemsEqual(event.getItemStack(), new ItemStack(BlockLoader.BAMBOO))) {
            event.setBurnTime(400);
        }
        if (ItemStack.areItemsEqual(event.getItemStack(), new ItemStack(ItemLoader.MATERIAL, 1, 51))) {
            event.setBurnTime(1600);
        }
        if (ItemStack.areItemsEqual(event.getItemStack(), new ItemStack(BlockLoader.BAMBOO_CHARCOAL_BLOCK))) {
            event.setBurnTime(8000);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUniversalFluidRegister(RegistryEvent.Register<UniversalFluid> event) {
        event.getRegistry().register(new UniversalFluid(BlockLoader.VODKA_FLUID, BlockLoader.RUM_FLUID, BlockLoader.BRANDY_FLUID, BlockLoader.WHISKEY_FLUID).setRegistryName(RLUtil.of("alcohol_liqueur")));
    }

    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event) {
        addFishingLoot(new ItemStack(ItemLoader.FOODSET, 1, 78), 45);
        addFishingLoot(new ItemStack(ItemLoader.FOODSET, 1, 141), 45);
        addFishingLoot(new ItemStack(ItemLoader.SEAWEED_RAW), 15);
    }

    @SubscribeEvent
    public static void onFishingLootLoaded(LootTableLoadEvent event) {
        if (LOOT_LOCATIONS.contains(event.getName().toString())) {
            for (LootEntry entry : FISHING_LOOT_POOLS) {
                event.getTable().getPool("main").addEntry(entry);
            }
        }
    }

    private static void addFishingLoot(ItemStack stack, int weight) {
        LootCondition[] lootConditions = new LootCondition[0];
        LootFunction[] setMeta = new LootFunction[]{new SetMetadata(lootConditions, new RandomValueRange(stack.getMetadata()))};
        LootEntry entry = new LootEntryItem(stack.getItem(), weight, 0, setMeta, lootConditions, stack.getTranslationKey());

        FISHING_LOOT_POOLS.add(entry);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (ClientProxy.CHANGE_MODE.isPressed()) {
            ClientProxy.getNetwork().sendToServer(new PacketKeyMessage(SakuraMain.MODID));
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (SakuraConfig.HARDER_IRON_RECIPE) {
            event.player.sendMessage(new TextComponentTranslation("sakura.warning.harder_iron_recipe_enabled", new Object()));
        }
    }

    @SubscribeEvent
    public static void onPlayerLogginGiveItem(PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP entityPlayerMP) {
            // Thanks ENE
            if (event.player.getName().equalsIgnoreCase("ENE")) {
                giveItem(entityPlayerMP, ArmorLoader.INSTANCE.getCustomArmor("kimono_ene", ItemLoader.KIMONO));
            }
        }
    }

    private static void giveItem(EntityPlayerMP player, ItemStack item) {
        NBTTagCompound playerData = player.getEntityData();
        NBTTagCompound data = playerData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        StringBuilder builder = new StringBuilder(item.getTranslationKey().substring(5)).append("_has");
        if (!data.getBoolean(builder.toString())) {
            ItemHandlerHelper.giveItemToPlayer(player, item);
            data.setBoolean(builder.toString(), true);
            playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
        }
    }
}
