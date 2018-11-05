package net.minecraft.entity.monster;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySilverfish extends EntityMob
{
    private EntitySilverfish.AISummonSilverfish summonSilverfish;

    public EntitySilverfish(World worldIn)
    {
        super(worldIn);
        this.setSize(0.4F, 0.3F);
    }

    public static void func_189767_b(DataFixer p_189767_0_)
    {
        EntityLiving.func_189752_a(p_189767_0_, EntitySilverfish.class);
    }

    protected void initEntityAI()
    {
        this.summonSilverfish = new EntitySilverfish.AISummonSilverfish(this);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(3, this.summonSilverfish);
        this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(5, new EntitySilverfish.AIHideInStone(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return 0.1D;
    }

    public float getEyeHeight()
    {
        return 0.1F;
    }

    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_SILVERFISH_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_SILVERFISH_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_SILVERFISH_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound(SoundEvents.ENTITY_SILVERFISH_STEP, 0.15F, 1.0F);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isInvulnerableTo(source))
        {
            return false;
        }
        else
        {
            if ((source instanceof EntityDamageSource || source == DamageSource.MAGIC) && this.summonSilverfish != null)
            {
                this.summonSilverfish.notifyHurt();
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTableList.ENTITIES_SILVERFISH;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick()
    {
        this.renderYawOffset = this.rotationYaw;
        super.tick();
    }

    /**
     * Set the render yaw offset
     */
    public void setRenderYawOffset(float offset)
    {
        this.rotationYaw = offset;
        super.setRenderYawOffset(offset);
    }

    public float getBlockPathWeight(BlockPos pos)
    {
        return this.world.getBlockState(pos.down()).getBlock() == Blocks.STONE ? 10.0F : super.getBlockPathWeight(pos);
    }

    /**
     * Checks to make sure the light is not too bright where the mob is spawning
     */
    protected boolean isValidLightLevel()
    {
        return true;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        if (super.getCanSpawnHere())
        {
            EntityPlayer entityplayer = this.world.getNearestPlayerNotCreative(this, 5.0D);
            return entityplayer == null;
        }
        else
        {
            return false;
        }
    }

    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    static class AIHideInStone extends EntityAIWander
        {
            private EnumFacing facing;
            private boolean doMerge;

            public AIHideInStone(EntitySilverfish silverfishIn)
            {
                super(silverfishIn, 1.0D, 10);
                this.setMutexBits(1);
            }

            /**
             * Returns whether the EntityAIBase should begin execution.
             */
            public boolean shouldExecute()
            {
                if (this.entity.getAttackTarget() != null)
                {
                    return false;
                }
                else if (!this.entity.getNavigator().noPath())
                {
                    return false;
                }
                else
                {
                    Random random = this.entity.getRNG();

                    if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.entity.world, this.entity) && random.nextInt(10) == 0)
                    {
                        this.facing = EnumFacing.random(random);
                        BlockPos blockpos = (new BlockPos(this.entity.posX, this.entity.posY + 0.5D, this.entity.posZ)).offset(this.facing);
                        IBlockState iblockstate = this.entity.world.getBlockState(blockpos);

                        if (BlockSilverfish.func_176377_d(iblockstate))
                        {
                            this.doMerge = true;
                            return true;
                        }
                    }

                    this.doMerge = false;
                    return super.shouldExecute();
                }
            }

            /**
             * Returns whether an in-progress EntityAIBase should continue executing
             */
            public boolean shouldContinueExecuting()
            {
                return this.doMerge ? false : super.shouldContinueExecuting();
            }

            /**
             * Execute a one shot task or start executing a continuous task
             */
            public void startExecuting()
            {
                if (!this.doMerge)
                {
                    super.startExecuting();
                }
                else
                {
                    World world = this.entity.world;
                    BlockPos blockpos = (new BlockPos(this.entity.posX, this.entity.posY + 0.5D, this.entity.posZ)).offset(this.facing);
                    IBlockState iblockstate = world.getBlockState(blockpos);

                    if (BlockSilverfish.func_176377_d(iblockstate))
                    {
                        world.setBlockState(blockpos, Blocks.field_150418_aU.getDefaultState().func_177226_a(BlockSilverfish.field_176378_a, BlockSilverfish.EnumType.func_176878_a(iblockstate)), 3);
                        this.entity.spawnExplosionParticle();
                        this.entity.remove();
                    }
                }
            }
        }

    static class AISummonSilverfish extends EntityAIBase
        {
            private final EntitySilverfish silverfish;
            private int lookForFriends;

            public AISummonSilverfish(EntitySilverfish silverfishIn)
            {
                this.silverfish = silverfishIn;
            }

            public void notifyHurt()
            {
                if (this.lookForFriends == 0)
                {
                    this.lookForFriends = 20;
                }
            }

            /**
             * Returns whether the EntityAIBase should begin execution.
             */
            public boolean shouldExecute()
            {
                return this.lookForFriends > 0;
            }

            /**
             * Keep ticking a continuous task that has already been started
             */
            public void tick()
            {
                --this.lookForFriends;

                if (this.lookForFriends <= 0)
                {
                    World world = this.silverfish.world;
                    Random random = this.silverfish.getRNG();
                    BlockPos blockpos = new BlockPos(this.silverfish);

                    for (int i = 0; i <= 5 && i >= -5; i = (i <= 0 ? 1 : 0) - i)
                    {
                        for (int j = 0; j <= 10 && j >= -10; j = (j <= 0 ? 1 : 0) - j)
                        {
                            for (int k = 0; k <= 10 && k >= -10; k = (k <= 0 ? 1 : 0) - k)
                            {
                                BlockPos blockpos1 = blockpos.add(j, i, k);
                                IBlockState iblockstate = world.getBlockState(blockpos1);

                                if (iblockstate.getBlock() == Blocks.field_150418_aU)
                                {
                                    if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, this.silverfish))
                                    {
                                        world.destroyBlock(blockpos1, true);
                                    }
                                    else
                                    {
                                        world.setBlockState(blockpos1, ((BlockSilverfish.EnumType)iblockstate.get(BlockSilverfish.field_176378_a)).func_176883_d(), 3);
                                    }

                                    if (random.nextBoolean())
                                    {
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
}