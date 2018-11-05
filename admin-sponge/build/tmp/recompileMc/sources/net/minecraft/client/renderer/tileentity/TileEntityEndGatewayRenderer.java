package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityEndGatewayRenderer extends TileEntityEndPortalRenderer
{
    private static final ResourceLocation END_GATEWAY_BEAM_TEXTURE = new ResourceLocation("textures/entity/end_gateway_beam.png");

    public void func_192841_a(TileEntityEndPortal p_192841_1_, double p_192841_2_, double p_192841_4_, double p_192841_6_, float p_192841_8_, int p_192841_9_, float p_192841_10_)
    {
        GlStateManager.disableFog();
        TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway)p_192841_1_;

        if (tileentityendgateway.func_184309_b() || tileentityendgateway.func_184310_d())
        {
            GlStateManager.alphaFunc(516, 0.1F);
            this.bindTexture(END_GATEWAY_BEAM_TEXTURE);
            float f = tileentityendgateway.func_184309_b() ? tileentityendgateway.func_184302_e(p_192841_8_) : tileentityendgateway.func_184305_g(p_192841_8_);
            double d0 = tileentityendgateway.func_184309_b() ? 256.0D - p_192841_4_ : 50.0D;
            f = MathHelper.sin(f * (float)Math.PI);
            int i = MathHelper.floor((double)f * d0);
            float[] afloat = tileentityendgateway.func_184309_b() ? EnumDyeColor.MAGENTA.getColorComponentValues() : EnumDyeColor.PURPLE.getColorComponentValues();
            TileEntityBeaconRenderer.renderBeamSegment(p_192841_2_, p_192841_4_, p_192841_6_, (double)p_192841_8_, (double)f, (double)tileentityendgateway.getWorld().getGameTime(), 0, i, afloat, 0.15D, 0.175D);
            TileEntityBeaconRenderer.renderBeamSegment(p_192841_2_, p_192841_4_, p_192841_6_, (double)p_192841_8_, (double)f, (double)tileentityendgateway.getWorld().getGameTime(), 0, -i, afloat, 0.15D, 0.175D);
        }

        super.func_192841_a(p_192841_1_, p_192841_2_, p_192841_4_, p_192841_6_, p_192841_8_, p_192841_9_, p_192841_10_);
        GlStateManager.enableFog();
    }

    protected int getPasses(double p_191286_1_)
    {
        return super.getPasses(p_191286_1_) + 1;
    }

    protected float getOffset()
    {
        return 1.0F;
    }
}