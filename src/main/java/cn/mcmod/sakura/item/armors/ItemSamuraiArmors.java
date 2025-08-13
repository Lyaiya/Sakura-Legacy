package cn.mcmod.sakura.item.armors;

import cn.mcmod.sakura.SakuraMain;
import cn.mcmod.sakura.api.armor.ArmorLoader;
import cn.mcmod.sakura.client.model.ModelSamuraiArmors;
import cn.mcmod.sakura.client.model.ModelSamuraiArmors2;
import cn.mcmod_mmf.mmlib.util.ClientUtils;
import cn.mcmod_mmf.mmlib.util.RecipesUtil;
import cn.mcmod_mmf.mmlib.util.TagPropertyAccessor.TagPropertyString;
import com.google.common.collect.Maps;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ItemSamuraiArmors extends ItemArmor {
    public static final Map<String, Boolean> ALL_ARMOR_ID = Maps.newHashMap();
    public static final TagPropertyString TEXTURE_NAME = new TagPropertyString("texture_name");

    private final boolean isNormal;

    public ItemSamuraiArmors(String name, boolean isNormal, ArmorMaterial materialIn, int renderIndexIn,
                             EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
        this.isNormal = isNormal;
        setTranslationKey(SakuraMain.MODID + "." + name);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound nbt = RecipesUtil.getInstance().getItemTagCompound(stack);
        String name = TEXTURE_NAME.get(nbt, isNormal ? "soldier_armor" : "samurai_armor");
        tooltip.add(I18n.format("sakura.armor_samurai.texture.name") + ":" + I18n.format("item.sakura." + name + ".name"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (String name : ALL_ARMOR_ID.keySet()) {
                ItemStack kimono = ArmorLoader.INSTANCE.getCustomArmor(name, this);
                if (!kimono.isEmpty() && (ALL_ARMOR_ID.get(name) == this.isNormal))
                    items.add(kimono);
            }
        }
        super.getSubItems(tab, items);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        NBTTagCompound nbt = RecipesUtil.getInstance().getItemTagCompound(stack);
        String name = TEXTURE_NAME.get(nbt, isNormal ? "soldier_armor" : "samurai_armor");
        return SakuraMain.MODID + ":" + "textures/models/armor/" + name + ".png";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot,
                                    ModelBiped _default) {
        ModelBiped model1 = isNormal ? new ModelSamuraiArmors2(armorSlot, 2, 1.0F) : new ModelSamuraiArmors(armorSlot, 3, 1.0F);
        ModelBiped model2 = isNormal ? new ModelSamuraiArmors2(armorSlot, 2, 0.5F) : new ModelSamuraiArmors(armorSlot, 3, 0.5F);

        return ClientUtils.getInstance().getCustomArmorModel(entityLiving, itemStack, armorSlot, null, model1, model2);
    }
}
