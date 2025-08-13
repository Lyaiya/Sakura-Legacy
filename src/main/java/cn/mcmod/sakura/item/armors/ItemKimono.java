package cn.mcmod.sakura.item.armors;

import cn.mcmod.sakura.SakuraMain;
import cn.mcmod.sakura.api.armor.ArmorLoader;
import cn.mcmod.sakura.client.model.ModelKimono;
import cn.mcmod.sakura.item.ItemLoader;
import cn.mcmod_mmf.mmlib.util.RecipesUtil;
import cn.mcmod_mmf.mmlib.util.TagPropertyAccessor.TagPropertyString;
import com.google.common.collect.Lists;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemKimono extends ItemArmor {
    public static final List<String> ALL_KIMONO_ID = Lists.newArrayList();
    public static final TagPropertyString TEXTURE_NAME = new TagPropertyString("texture_name");

    public ItemKimono() {
        super(ItemLoader.KIMONO_MATERIAL, 0, EntityEquipmentSlot.LEGS);
        setTranslationKey(SakuraMain.MODID + "." + "kimono");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, net.minecraft.client.model.ModelBiped _default) {
        return getKimonoModel(entityLiving, itemStack, new ModelKimono());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound nbt = RecipesUtil.getInstance().getItemTagCompound(stack);
        String name = TEXTURE_NAME.get(nbt, "kimono_base");
        tooltip.add(I18n.format("sakura.kimono.texture.name") + ":" + I18n.format("item.sakura." + name + ".name"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (String name : ALL_KIMONO_ID) {
                ItemStack kimono = ArmorLoader.INSTANCE.getCustomArmor(name, this);
                if (!kimono.isEmpty()) items.add(kimono);
            }
        }
        super.getSubItems(tab, items);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        NBTTagCompound nbt = RecipesUtil.getInstance().getItemTagCompound(stack);
        String name = TEXTURE_NAME.get(nbt, "kimono_base");
        return SakuraMain.MODID + ":" + "textures/models/armor/" + name + ".png";
    }

    @SideOnly(Side.CLIENT)
    public static ModelBiped getKimonoModel(EntityLivingBase entityLiving, ItemStack itemStack, ModelBiped model) {
        model.setVisible(true);

        model.isSneak = entityLiving.isSneaking();

        model.isRiding = entityLiving.isRiding();
        model.isChild = entityLiving.isChild();
        ItemStack mainhand = entityLiving.getHeldItemMainhand();
        ItemStack offhand = entityLiving.getHeldItemOffhand();
        ModelBiped.ArmPose mainhandArmPose = ModelBiped.ArmPose.EMPTY;
        ModelBiped.ArmPose offhandArmPose = ModelBiped.ArmPose.EMPTY;

        if (!mainhand.isEmpty()) {
            mainhandArmPose = ModelBiped.ArmPose.ITEM;
            if (entityLiving.getItemInUseCount() > 0) {
                EnumAction enumaction = mainhand.getItemUseAction();
                if (enumaction == EnumAction.BLOCK) {
                    mainhandArmPose = ModelBiped.ArmPose.BLOCK;
                } else if (enumaction == EnumAction.BOW) {
                    mainhandArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
                }
            }
        }

        if (!offhand.isEmpty()) {
            offhandArmPose = ModelBiped.ArmPose.ITEM;
            if (entityLiving.getItemInUseCount() > 0) {
                EnumAction enumaction = offhand.getItemUseAction();
                if (enumaction == EnumAction.BLOCK) {
                    offhandArmPose = ModelBiped.ArmPose.BLOCK;
                }
            }
        }

        if (entityLiving.getPrimaryHand() == EnumHandSide.RIGHT) {
            model.rightArmPose = mainhandArmPose;
            model.leftArmPose = offhandArmPose;
        } else {
            model.rightArmPose = offhandArmPose;
            model.leftArmPose = mainhandArmPose;
        }
        return model;
    }

}
