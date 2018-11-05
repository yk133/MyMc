package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntitySmallFireball extends EntityFireball
{
    public EntitySmallFireball(World worldIn)
    {
        super(worldIn);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntitySmallFireball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ)
    {
        super(worldIn, shooter, accelX, accelY, accelZ);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntitySmallFireball(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ)
    {
        super(worldIn, x, y, z, accelX, accelY, accelZ);
        this.setSize(0.3125F, 0.3125F);
    }

    public static void func_189745_a(DataFixer p_189745_0_)
    {
        EntityFireball.func_189743_a(p_189745_0_, "SmallFireball");
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onImpact(RayTraceResult result)
    {
        if (!this.world.isRemote)
        {
            if (result.entity != null)
            {
                if (!result.entity.isImmuneToFire())
                {
                    boolean flag = result.entity.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5.0F);

                    if (flag)
                    {
                        this.applyEnchantments(this.shootingEntity, result.entity);
                        result.entity.setFire(5);
                    }
                }
            }
            else
            {
                boolean flag1 = true;

                if (this.shootingEntity != null && this.shootingEntity instanceof EntityLiving)
                {
                    flag1 = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
                }

                if (flag1)
                {
                    BlockPos blockpos = result.getBlockPos().offset(result.sideHit);

                    if (this.world.isAirBlock(blockpos))
                    {
                        this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
                    }
                }
            }

            this.remove();
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return false;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return false;
    }
}