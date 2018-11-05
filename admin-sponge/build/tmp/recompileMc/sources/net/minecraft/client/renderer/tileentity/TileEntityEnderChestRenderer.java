package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityEnderChestRenderer extends TileEntitySpecialRenderer<TileEntityEnderChest>
{
    private static final ResourceLocation field_147520_b = new ResourceLocation("textures/entity/chest/ender.png");
    private final ModelChest field_147521_c = new ModelChest();

    public void func_192841_a(TileEntityEnderChest p_192841_1_, double p_192841_2_, double p_192841_4_, double p_192841_6_, float p_192841_8_, int p_192841_9_, float p_192841_10_)
    {
        int i = 0;

        if (p_192841_1_.hasWorld())
        {
            i = p_192841_1_.func_145832_p();
        }

        if (p_192841_9_ >= 0)
        {
            this.bindTexture(DESTROY_STAGES[p_192841_9_]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0F, 4.0F, 1.0F);
            GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        }
        else
        {
            this.bindTexture(field_147520_b);
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, p_192841_10_);
        GlStateManager.translatef((float)p_192841_2_, (float)p_192841_4_ + 1.0F, (float)p_192841_6_ + 1.0F);
        GlStateManager.scalef(1.0F, -1.0F, -1.0F);
        GlStateManager.translatef(0.5F, 0.5F, 0.5F);
        int j = 0;

        if (i == 2)
        {
            j = 180;
        }

        if (i == 3)
        {
            j = 0;
        }

        if (i == 4)
        {
            j = 90;
        }

        if (i == 5)
        {
            j = -90;
        }

        GlStateManager.rotatef((float)j, 0.0F, 1.0F, 0.0F);
        GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
        float f = p_192841_1_.prevLidAngle + (p_192841_1_.lidAngle - p_192841_1_.prevLidAngle) * p_192841_8_;
        f = 1.0F - f;
        f = 1.0F - f * f * f;
        this.field_147521_c.chestLid.rotateAngleX = -(f * ((float)Math.PI / 2F));
        this.field_147521_c.renderAll();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (p_192841_9_ >= 0)
        {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
}