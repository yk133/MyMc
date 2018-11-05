package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityLargeFireball extends EntityFireball
{
    public int explosionPower = 1;

    public EntityLargeFireball(World worldIn)
    {
        super(worldIn);
    }

    @SideOnly(Side.CLIENT)
    public EntityLargeFireball(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ)
    {
        super(worldIn, x, y, z, accelX, accelY, accelZ);
    }

    public EntityLargeFireball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ)
    {
        super(worldIn, shooter, accelX, accelY, accelZ);
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
                result.entity.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 6.0F);
                this.applyEnchantments(this.shootingEntity, result.entity);
            }

            boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
            this.world.newExplosion((Entity)null, this.posX, this.posY, this.posZ, (float)this.explosionPower, flag, flag);
            this.remove();
        }
    }

    public static void func_189744_a(DataFixer p_189744_0_)
    {
        EntityFireball.func_189743_a(p_189744_0_, "Fireball");
    }

    /**
     * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
     * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
     */
    public void writeAdditional(NBTTagCompound compound)
    {
        super.writeAdditional(compound);
        compound.putInt("ExplosionPower", this.explosionPower);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(NBTTagCompound compound)
    {
        super.readAdditional(compound);

        if (compound.contains("ExplosionPower", 99))
        {
            this.explosionPower = compound.getInt("ExplosionPower");
        }
    }
}