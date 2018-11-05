package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleBreaking extends Particle
{
    protected ParticleBreaking(World p_i1195_1_, double p_i1195_2_, double p_i1195_4_, double p_i1195_6_, Item p_i1195_8_)
    {
        this(p_i1195_1_, p_i1195_2_, p_i1195_4_, p_i1195_6_, p_i1195_8_, 0);
    }

    protected ParticleBreaking(World p_i1197_1_, double p_i1197_2_, double p_i1197_4_, double p_i1197_6_, double p_i1197_8_, double p_i1197_10_, double p_i1197_12_, Item p_i1197_14_, int p_i1197_15_)
    {
        this(p_i1197_1_, p_i1197_2_, p_i1197_4_, p_i1197_6_, p_i1197_14_, p_i1197_15_);
        this.motionX *= 0.10000000149011612D;
        this.motionY *= 0.10000000149011612D;
        this.motionZ *= 0.10000000149011612D;
        this.motionX += p_i1197_8_;
        this.motionY += p_i1197_10_;
        this.motionZ += p_i1197_12_;
    }

    protected ParticleBreaking(World p_i1196_1_, double p_i1196_2_, double p_i1196_4_, double p_i1196_6_, Item p_i1196_8_, int p_i1196_9_)
    {
        super(p_i1196_1_, p_i1196_2_, p_i1196_4_, p_i1196_6_, 0.0D, 0.0D, 0.0D);
        this.setParticleTexture(Minecraft.getInstance().getItemRenderer().getItemModelMesher().func_178087_a(p_i1196_8_, p_i1196_9_));
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.particleGravity = Blocks.SNOW.field_149763_I;
        this.particleScale /= 2.0F;
    }

    /**
     * Retrieve what effect layer (what texture) the particle should be rendered with. 0 for the particle sprite sheet,
     * 1 for the main Texture atlas, and 3 for a custom texture
     */
    public int getFXLayer()
    {
        return 1;
    }

    /**
     * Renders the particle
     */
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        float f = ((float)this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
        float f1 = f + 0.015609375F;
        float f2 = ((float)this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
        float f3 = f2 + 0.015609375F;
        float f4 = 0.1F * this.particleScale;

        if (this.particleTexture != null)
        {
            f = this.particleTexture.getInterpolatedU((double)(this.particleTextureJitterX / 4.0F * 16.0F));
            f1 = this.particleTexture.getInterpolatedU((double)((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F));
            f2 = this.particleTexture.getInterpolatedV((double)(this.particleTextureJitterY / 4.0F * 16.0F));
            f3 = this.particleTexture.getInterpolatedV((double)((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F));
        }

        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        buffer.pos((double)(f5 - rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 - rotationYZ * f4 - rotationXZ * f4)).tex((double)f, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
        buffer.pos((double)(f5 - rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 - rotationYZ * f4 + rotationXZ * f4)).tex((double)f, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
        buffer.pos((double)(f5 + rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 + rotationYZ * f4 + rotationXZ * f4)).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
        buffer.pos((double)(f5 + rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 + rotationYZ * f4 - rotationXZ * f4)).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory
        {
            public Particle func_178902_a(int p_178902_1_, World p_178902_2_, double p_178902_3_, double p_178902_5_, double p_178902_7_, double p_178902_9_, double p_178902_11_, double p_178902_13_, int... p_178902_15_)
            {
                int i = p_178902_15_.length > 1 ? p_178902_15_[1] : 0;
                return new ParticleBreaking(p_178902_2_, p_178902_3_, p_178902_5_, p_178902_7_, p_178902_9_, p_178902_11_, p_178902_13_, Item.getItemById(p_178902_15_[0]), i);
            }
        }

    @SideOnly(Side.CLIENT)
    public static class SlimeFactory implements IParticleFactory
        {
            public Particle func_178902_a(int p_178902_1_, World p_178902_2_, double p_178902_3_, double p_178902_5_, double p_178902_7_, double p_178902_9_, double p_178902_11_, double p_178902_13_, int... p_178902_15_)
            {
                return new ParticleBreaking(p_178902_2_, p_178902_3_, p_178902_5_, p_178902_7_, Items.SLIME_BALL);
            }
        }

    @SideOnly(Side.CLIENT)
    public static class SnowballFactory implements IParticleFactory
        {
            public Particle func_178902_a(int p_178902_1_, World p_178902_2_, double p_178902_3_, double p_178902_5_, double p_178902_7_, double p_178902_9_, double p_178902_11_, double p_178902_13_, int... p_178902_15_)
            {
                return new ParticleBreaking(p_178902_2_, p_178902_3_, p_178902_5_, p_178902_7_, Items.SNOWBALL);
            }
        }
}