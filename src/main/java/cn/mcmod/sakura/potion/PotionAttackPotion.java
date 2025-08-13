package cn.mcmod.sakura.potion;

import cn.mcmod.sakura.SakuraMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionAttackPotion extends Potion {
    private final int lvl, x, y, w, h;
    private final Potion potion;

    protected PotionAttackPotion(Potion effect, String name, int poisonLevel, int liquidColorIn, int ix, int iy, int iw, int ih) {
        super(false, liquidColorIn);
        setPotionName("sakura.effect." + name);
        setRegistryName(SakuraMain.MODID, name);
        potion = effect;
        lvl = poisonLevel;
        x = ix;
        y = iy;
        w = iw;
        h = ih;
    }

    @SubscribeEvent
    public void onAttacking(AttackEntityEvent event) {
        if (event.getTarget() instanceof EntityLivingBase target) {
            EntityPlayer player = event.getEntityPlayer();
            if (player.isPotionActive(this)) {
                target.addPotionEffect(new PotionEffect(potion, lvl * 300, lvl));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(int ix, int iy, PotionEffect effect, Minecraft mc) {
        mc.getTextureManager().bindTexture(PotionLoader.RES);
        mc.currentScreen.drawTexturedModalRect(ix + 6, iy + 7, x, y, w, h);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUDEffect(int ix, int iy, PotionEffect effect, Minecraft mc, float alpha) {
        mc.getTextureManager().bindTexture(PotionLoader.RES);
        Gui.drawModalRectWithCustomSizedTexture(ix + 3, iy + 3, x, y, w, h, 256.0F, 256.0F);
    }
}
