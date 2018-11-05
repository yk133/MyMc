package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelShulker;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderShulker;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityShulkerBoxRenderer extends TileEntitySpecialRenderer<TileEntityShulkerBox>
{
    private final ModelShulker model;

    public TileEntityShulkerBoxRenderer(ModelShulker modelIn)
    {
        this.model = modelIn;
    }

    public void func_192841_a(TileEntityShulkerBox p_192841_1_, double p_192841_2_, double p_192841_4_, double p_192841_6_, float p_192841_8_, int p_192841_9_, float p_192841_10_)
    {
        EnumFacing enumfacing = EnumFacing.UP;

        if (p_192841_1_.hasWorld())
        {
            IBlockState iblockstate = this.getWorld().getBlockState(p_192841_1_.getPos());

            if (iblockstate.getBlock() instanceof BlockShulkerBox)
            {
                enumfacing = (EnumFacing)iblockstate.get(BlockShulkerBox.FACING);
            }
        }

        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        GlStateManager.disableCull();

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
            this.bindTexture(RenderShulker.SHULKER_ENDERGOLEM_TEXTURE[p_192841_1_.getColor().func_176765_a()]);
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();

        if (p_192841_9_ < 0)
        {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, p_192841_10_);
        }

        GlStateManager.translatef((float)p_192841_2_ + 0.5F, (float)p_192841_4_ + 1.5F, (float)p_192841_6_ + 0.5F);
        GlStateManager.scalef(1.0F, -1.0F, -1.0F);
        GlStateManager.translatef(0.0F, 1.0F, 0.0F);
        float f = 0.9995F;
        GlStateManager.scalef(0.9995F, 0.9995F, 0.9995F);
        GlStateManager.translatef(0.0F, -1.0F, 0.0F);

        switch (enumfacing)
        {
            case DOWN:
                GlStateManager.translatef(0.0F, 2.0F, 0.0F);
                GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            case UP:
            default:
                break;
            case NORTH:
                GlStateManager.translatef(0.0F, 1.0F, 1.0F);
                GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
                break;
            case SOUTH:
                GlStateManager.translatef(0.0F, 1.0F, -1.0F);
                GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.translatef(-1.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                break;
            case EAST:
                GlStateManager.translatef(1.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
        }

        this.model.base.render(0.0625F);
        GlStateManager.translatef(0.0F, -p_192841_1_.getProgress(p_192841_8_) * 0.5F, 0.0F);
        GlStateManager.rotatef(270.0F * p_192841_1_.getProgress(p_192841_8_), 0.0F, 1.0F, 0.0F);
        this.model.lid.render(0.0625F);
        GlStateManager.enableCull();
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