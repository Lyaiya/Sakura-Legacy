package cn.mcmod.sakura.entity;

import cn.mcmod.sakura.util.RLUtil;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EntityDeer extends EntityAnimal {
    public EntityDeer(World worldIn) {
        super(worldIn);
        setSize(0.9F, 0.95F);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIPanic(this, 1.25D));
        tasks.addTask(3, new EntityAIMate(this, 1.0D));
        tasks.addTask(4, new EntityAITempt(this, 1.2D, Items.WHEAT, true));
        tasks.addTask(5, new EntityAIFollowParent(this, 1.1D));
        tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0D));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 7.0F));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityAnimal.class, 6.0F));
        tasks.addTask(9, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override
    public float getEyeHeight() {
        return height * 0.98F;
    }

    @Nullable
    @Override
    public EntityDeer createChild(EntityAgeable ageable) {
        return new EntityDeer(world);
    }

    @Override
    protected ResourceLocation getLootTable() {
        return RLUtil.of("entity/deer");
    }
}
