package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityVindicator extends AbstractIllager
{
    private boolean johnny;
    private static final Predicate<Entity> JOHNNY_SELECTOR = new Predicate<Entity>()
    {
        public boolean apply(@Nullable Entity p_apply_1_)
        {
            return p_apply_1_ instanceof EntityLivingBase && ((EntityLivingBase)p_apply_1_).attackable();
        }
    };

    public EntityVindicator(World worldIn)
    {
        super(worldIn);
        this.setSize(0.6F, 1.95F);
    }

    public static void func_190641_b(DataFixer p_190641_0_)
    {
        EntityLiving.func_189752_a(p_190641_0_, EntityVindicator.class);
    }

    protected void initEntityAI()
    {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(8, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[] {EntityVindicator.class}));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
        this.targetTasks.addTask(4, new EntityVindicator.AIJohnnyAttack(this));
    }

    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3499999940395355D);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
    }

    protected void registerData()
    {
        super.registerData();
    }

    protected ResourceLocation getLootTable()
    {
        return LootTableList.ENTITIES_VINDICATION_ILLAGER;
    }

    @SideOnly(Side.CLIENT)
    public boolean isAggressive()
    {
        return this.isAggressive(1);
    }

    public void setAggressive(boolean p_190636_1_)
    {
        this.setAggressive(1, p_190636_1_);
    }

    /**
     * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
     * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
     */
    public void writeAdditional(NBTTagCompound compound)
    {
        super.writeAdditional(compound);

        if (this.johnny)
        {
            compound.putBoolean("Johnny", true);
        }
    }

    @SideOnly(Side.CLIENT)
    public AbstractIllager.IllagerArmPose getArmPose()
    {
        return this.isAggressive() ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.CROSSED;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(NBTTagCompound compound)
    {
        super.readAdditional(compound);

        if (compound.contains("Johnny", 99))
        {
            this.johnny = compound.getBoolean("Johnny");
        }
    }

    @Nullable
    public IEntityLivingData func_180482_a(DifficultyInstance p_180482_1_, @Nullable IEntityLivingData p_180482_2_)
    {
        IEntityLivingData ientitylivingdata = super.func_180482_a(p_180482_1_, p_180482_2_);
        this.setEquipmentBasedOnDifficulty(p_180482_1_);
        this.setEnchantmentBasedOnDifficulty(p_180482_1_);
        return ientitylivingdata;
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
    {
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
    }

    protected void updateAITasks()
    {
        super.updateAITasks();
        this.setAggressive(this.getAttackTarget() != null);
    }

    /**
     * Returns whether this Entity is on the same team as the given Entity.
     */
    public boolean isOnSameTeam(Entity entityIn)
    {
        if (super.isOnSameTeam(entityIn))
        {
            return true;
        }
        else if (entityIn instanceof EntityLivingBase && ((EntityLivingBase)entityIn).getCreatureAttribute() == EnumCreatureAttribute.ILLAGER)
        {
            return this.getTeam() == null && entityIn.getTeam() == null;
        }
        else
        {
            return false;
        }
    }

    public void func_96094_a(String p_96094_1_)
    {
        super.func_96094_a(p_96094_1_);

        if (!this.johnny && "Johnny".equals(p_96094_1_))
        {
            this.johnny = true;
        }
    }

    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_VINDICATOR_AMBIENT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_VINDICATOR_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_VINDICATOR_HURT;
    }

    static class AIJohnnyAttack extends EntityAINearestAttackableTarget<EntityLivingBase>
        {
            public AIJohnnyAttack(EntityVindicator vindicator)
            {
                super(vindicator, EntityLivingBase.class, 0, true, true, EntityVindicator.JOHNNY_SELECTOR);
            }

            /**
             * Returns whether the EntityAIBase should begin execution.
             */
            public boolean shouldExecute()
            {
                return ((EntityVindicator)this.taskOwner).johnny && super.shouldExecute();
            }
        }
}