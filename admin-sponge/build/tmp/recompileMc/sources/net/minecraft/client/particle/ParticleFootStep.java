package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleFootStep extends Particle
{
    private static final ResourceLocation field_110126_a = new ResourceLocation("textures/particle/footprint.png");
    private int field_70576_a;
    private final int field_70578_aq;
    private final TextureManager field_70577_ar;

    protected ParticleFootStep(TextureManager p_i1210_1_, World p_i1210_2_, double p_i1210_3_, double p_i1210_5_, double p_i1210_7_)
    {
        super(p_i1210_2_, p_i1210_3_, p_i1210_5_, p_i1210_7_, 0.0D, 0.0D, 0.0D);
        this.field_70577_ar = p_i1210_1_;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.field_70578_aq = 200;
    }

    /**
     * Renders the particle
     */
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        float f = ((float)this.field_70576_a + partialTicks) / (float)this.field_70578_aq;
        f = f * f;
        float f1 = 2.0F - f * 2.0F;

        if (f1 > 1.0F)
        {
            f1 = 1.0F;
        }

        f1 = f1 * 0.2F;
        GlStateManager.disableLighting();
        float f2 = 0.125F;
        float f3 = (float)(this.posX - interpPosX);
        float f4 = (float)(this.posY - interpPosY);
        float f5 = (float)(this.posZ - interpPosZ);
        float f6 = this.world.func_175724_o(new BlockPos(this.posX, this.posY, this.posZ));
        this.field_70577_ar.bindTexture(field_110126_a);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos((double)(f3 - 0.125F), (double)f4, (double)(f5 + 0.125F)).tex(0.0D, 1.0D).color(f6, f6, f6, f1).endVertex();
        buffer.pos((double)(f3 + 0.125F), (double)f4, (double)(f5 + 0.125F)).tex(1.0D, 1.0D).color(f6, f6, f6, f1).endVertex();
        buffer.pos((double)(f3 + 0.125F), (double)f4, (double)(f5 - 0.125F)).tex(1.0D, 0.0D).color(f6, f6, f6, f1).endVertex();
        buffer.pos((double)(f3 - 0.125F), (double)f4, (double)(f5 - 0.125F)).tex(0.0D, 0.0D).color(f6, f6, f6, f1).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
    }

    public void tick()
    {
        ++this.field_70576_a;

        if (this.field_70576_a == this.field_70578_aq)
        {
            this.setExpired();
        }
    }

    /**
     * Retrieve what effect layer (what texture) the particle should be rendered with. 0 for the particle sprite sheet,
     * 1 for the main Texture atlas, and 3 for a custom texture
     */
    public int getFXLayer()
    {
        return 3;
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory
        {
            public Particle func_178902_a(int p_178902_1_, World p_178902_2_, double p_178902_3_, double p_178902_5_, double p_178902_7_, double p_178902_9_, double p_178902_11_, double p_178902_13_, int... p_178902_15_)
            {
                return new ParticleFootStep(Minecraft.getInstance().getTextureManager(), p_178902_2_, p_178902_3_, p_178902_5_, p_178902_7_);
            }
        }
}