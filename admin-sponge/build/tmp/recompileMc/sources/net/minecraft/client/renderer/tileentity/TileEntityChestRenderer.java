package net.minecraft.client.renderer.tileentity;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityChestRenderer extends TileEntitySpecialRenderer<TileEntityChest>
{
    private static final ResourceLocation TEXTURE_TRAPPED_DOUBLE = new ResourceLocation("textures/entity/chest/trapped_double.png");
    private static final ResourceLocation TEXTURE_CHRISTMAS_DOUBLE = new ResourceLocation("textures/entity/chest/christmas_double.png");
    private static final ResourceLocation TEXTURE_NORMAL_DOUBLE = new ResourceLocation("textures/entity/chest/normal_double.png");
    private static final ResourceLocation TEXTURE_TRAPPED = new ResourceLocation("textures/entity/chest/trapped.png");
    private static final ResourceLocation TEXTURE_CHRISTMAS = new ResourceLocation("textures/entity/chest/christmas.png");
    private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("textures/entity/chest/normal.png");
    private final ModelChest simpleChest = new ModelChest();
    private final ModelChest largeChest = new ModelLargeChest();
    private boolean isChristmas;

    public TileEntityChestRenderer()
    {
        Calendar calendar = Calendar.getInstance();

        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26)
        {
            this.isChristmas = true;
        }
    }

    public void func_192841_a(TileEntityChest p_192841_1_, double p_192841_2_, double p_192841_4_, double p_192841_6_, float p_192841_8_, int p_192841_9_, float p_192841_10_)
    {
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        int i;

        if (p_192841_1_.hasWorld())
        {
            Block block = p_192841_1_.func_145838_q();
            i = p_192841_1_.func_145832_p();

            if (block instanceof BlockChest && i == 0)
            {
                ((BlockChest)block).func_176455_e(p_192841_1_.getWorld(), p_192841_1_.getPos(), p_192841_1_.getWorld().getBlockState(p_192841_1_.getPos()));
                i = p_192841_1_.func_145832_p();
            }

            p_192841_1_.func_145979_i();
        }
        else
        {
            i = 0;
        }

        if (p_192841_1_.field_145992_i == null && p_192841_1_.field_145991_k == null)
        {
            ModelChest modelchest;

            if (p_192841_1_.field_145990_j == null && p_192841_1_.field_145988_l == null)
            {
                modelchest = this.simpleChest;

                if (p_192841_9_ >= 0)
                {
                    this.bindTexture(DESTROY_STAGES[p_192841_9_]);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.pushMatrix();
                    GlStateManager.scalef(4.0F, 4.0F, 1.0F);
                    GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
                    GlStateManager.matrixMode(5888);
                }
                else if (this.isChristmas)
                {
                    this.bindTexture(TEXTURE_CHRISTMAS);
                }
                else if (p_192841_1_.func_145980_j() == BlockChest.Type.TRAP)
                {
                    this.bindTexture(TEXTURE_TRAPPED);
                }
                else
                {
                    this.bindTexture(TEXTURE_NORMAL);
                }
            }
            else
            {
                modelchest = this.largeChest;

                if (p_192841_9_ >= 0)
                {
                    this.bindTexture(DESTROY_STAGES[p_192841_9_]);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.pushMatrix();
                    GlStateManager.scalef(8.0F, 4.0F, 1.0F);
                    GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
                    GlStateManager.matrixMode(5888);
                }
                else if (this.isChristmas)
                {
                    this.bindTexture(TEXTURE_CHRISTMAS_DOUBLE);
                }
                else if (p_192841_1_.func_145980_j() == BlockChest.Type.TRAP)
                {
                    this.bindTexture(TEXTURE_TRAPPED_DOUBLE);
                }
                else
                {
                    this.bindTexture(TEXTURE_NORMAL_DOUBLE);
                }
            }

            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();

            if (p_192841_9_ < 0)
            {
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, p_192841_10_);
            }

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

            if (i == 2 && p_192841_1_.field_145990_j != null)
            {
                GlStateManager.translatef(1.0F, 0.0F, 0.0F);
            }

            if (i == 5 && p_192841_1_.field_145988_l != null)
            {
                GlStateManager.translatef(0.0F, 0.0F, -1.0F);
            }

            GlStateManager.rotatef((float)j, 0.0F, 1.0F, 0.0F);
            GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
            float f = p_192841_1_.prevLidAngle + (p_192841_1_.lidAngle - p_192841_1_.prevLidAngle) * p_192841_8_;

            if (p_192841_1_.field_145992_i != null)
            {
                float f1 = p_192841_1_.field_145992_i.prevLidAngle + (p_192841_1_.field_145992_i.lidAngle - p_192841_1_.field_145992_i.prevLidAngle) * p_192841_8_;

                if (f1 > f)
                {
                    f = f1;
                }
            }

            if (p_192841_1_.field_145991_k != null)
            {
                float f2 = p_192841_1_.field_145991_k.prevLidAngle + (p_192841_1_.field_145991_k.lidAngle - p_192841_1_.field_145991_k.prevLidAngle) * p_192841_8_;

                if (f2 > f)
                {
                    f = f2;
                }
            }

            f = 1.0F - f;
            f = 1.0F - f * f * f;
            modelchest.chestLid.rotateAngleX = -(f * ((float)Math.PI / 2F));
            modelchest.renderAll();
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
}