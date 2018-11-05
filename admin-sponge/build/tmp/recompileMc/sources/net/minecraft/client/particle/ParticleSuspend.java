package net.minecraft.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleSuspend extends Particle
{
    protected ParticleSuspend(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn - 0.125D, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.particleRed = 0.4F;
        this.particleGreen = 0.4F;
        this.particleBlue = 0.7F;
        this.setParticleTextureIndex(0);
        this.setSize(0.01F, 0.01F);
        this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
        this.motionX = xSpeedIn * 0.0D;
        this.motionY = ySpeedIn * 0.0D;
        this.motionZ = zSpeedIn * 0.0D;
        this.maxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
    }

    public void tick()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.move(this.motionX, this.motionY, this.motionZ);

        if (this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getMaterial() != Material.WATER)
        {
            this.setExpired();
        }

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
                return new ParticleSuspend(p_178902_2_, p_178902_3_, p_178902_5_, p_178902_7_, p_178902_9_, p_178902_11_, p_178902_13_);
            }
        }
}