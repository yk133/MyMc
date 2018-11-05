package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;

public class EntitySpectralArrow extends EntityArrow
{
    private int duration = 200;

    public EntitySpectralArrow(World worldIn)
    {
        super(worldIn);
    }

    public EntitySpectralArrow(World worldIn, EntityLivingBase shooter)
    {
        super(worldIn, shooter);
    }

    public EntitySpectralArrow(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick()
    {
        super.tick();

        if (this.world.isRemote && !this.inGround)
        {
            this.world.func_175688_a(EnumParticleTypes.SPELL_INSTANT, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    protected ItemStack getArrowStack()
    {
        return new ItemStack(Items.SPECTRAL_ARROW);
    }

    protected void arrowHit(EntityLivingBase living)
    {
        super.arrowHit(living);
        PotionEffect potioneffect = new PotionEffect(MobEffects.GLOWING, this.duration, 0);
        living.func_70690_d(potioneffect);
    }

    public static void func_189659_b(DataFixer p_189659_0_)
    {
        EntityArrow.func_189657_a(p_189659_0_, "SpectralArrow");
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(NBTTagCompound compound)
    {
        super.readAdditional(compound);

        if (compound.contains("Duration"))
        {
            this.duration = compound.getInt("Duration");
        }
    }

    /**
     * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
     * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
     */
    public void writeAdditional(NBTTagCompound compound)
    {
        super.writeAdditional(compound);
        compound.putInt("Duration", this.duration);
    }
}