package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleRedstone extends Particle
{
    float reddustParticleScale;

    protected ParticleRedstone(World p_i46349_1_, double p_i46349_2_, double p_i46349_4_, double p_i46349_6_, float p_i46349_8_, float p_i46349_9_, float p_i46349_10_)
    {
        this(p_i46349_1_, p_i46349_2_, p_i46349_4_, p_i46349_6_, 1.0F, p_i46349_8_, p_i46349_9_, p_i46349_10_);
    }

    protected ParticleRedstone(World p_i46350_1_, double p_i46350_2_, double p_i46350_4_, double p_i46350_6_, float p_i46350_8_, float p_i46350_9_, float p_i46350_10_, float p_i46350_11_)
    {
        super(p_i46350_1_, p_i46350_2_, p_i46350_4_, p_i46350_6_, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.10000000149011612D;
        this.motionY *= 0.10000000149011612D;
        this.motionZ *= 0.10000000149011612D;

        if (p_i46350_9_ == 0.0F)
        {
            p_i46350_9_ = 1.0F;
        }

        float f = (float)Math.random() * 0.4F + 0.6F;
        this.particleRed = ((float)(Math.random() * 0.20000000298023224D) + 0.8F) * p_i46350_9_ * f;
        this.particleGreen = ((float)(Math.random() * 0.20000000298023224D) + 0.8F) * p_i46350_10_ * f;
        this.particleBlue = ((float)(Math.random() * 0.20000000298023224D) + 0.8F) * p_i46350_11_ * f;
        this.particleScale *= 0.75F;
        this.particleScale *= p_i46350_8_;
        this.reddustParticleScale = this.particleScale;
        this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
        this.maxAge = (int)((float)this.maxAge * p_i46350_8_);
    }

    /**
     * Renders the particle
     */
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        float f = ((float)this.age + partialTicks) / (float)this.maxAge * 32.0F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        this.particleScale = this.reddustParticleScale * f;
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    public void tick()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.age++ >= this.maxAge)
        {
            this.setExpired();
        }

        this.setParticleTextureIndex(7 - this.age * 8 / this.maxAge);
        this.move(this.motionX, this.motionY, this.motionZ);

        if (this.posY == this.prevPosY)
        {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }

        this.motionX *= 0.9599999785423279D;
        this.motionY *= 0.9599999785423279D;
        this.motionZ *= 0.9599999785423279D;

        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory
        {
            public Particle func_178902_a(int p_178902_1_, World p_178902_2_, double p_178902_3_, double p_178902_5_, double p_178902_7_, double p_178902_9_, double p_178902_11_, double p_178902_13_, int... p_178902_15_)
            {
                return new ParticleRedstone(p_178902_2_, p_178902_3_, p_178902_5_, p_178902_7_, (float)p_178902_9_, (float)p_178902_11_, (float)p_178902_13_);
            }
        }
}