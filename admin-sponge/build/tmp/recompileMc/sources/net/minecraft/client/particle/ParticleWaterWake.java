package net.minecraft.client.particle;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleWaterWake extends Particle
{
    protected ParticleWaterWake(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double p_i45073_8_, double p_i45073_10_, double p_i45073_12_)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.30000001192092896D;
        this.motionY = Math.random() * 0.20000000298023224D + 0.10000000149011612D;
        this.motionZ *= 0.30000001192092896D;
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.setParticleTextureIndex(19);
        this.setSize(0.01F, 0.01F);
        this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
        this.particleGravity = 0.0F;
        this.motionX = p_i45073_8_;
        this.motionY = p_i45073_10_;
        this.motionZ = p_i45073_12_;
    }

    public void tick()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= (double)this.particleGravity;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;
        int i = 60 - this.maxAge;
        float f = (float)i * 0.001F;
        this.setSize(f, f);
        this.setParticleTextureIndex(19 + i % 4);

        if (this.maxAge-- <= 0)
        {
            this.setExpired();
        }
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory
        {
            public Particle func_178902_a(int p_178902_1_, World p_178902_2_, double p_178902_3_, double p_178902_5_, double p_178902_7_, double p_178902_9_, double p_178902_11_, double p_178902_13_, int... p_178902_15_)
            {
                return new ParticleWaterWake(p_178902_2_, p_178902_3_, p_178902_5_, p_178902_7_, p_178902_9_, p_178902_11_, p_178902_13_);
            }
        }
}