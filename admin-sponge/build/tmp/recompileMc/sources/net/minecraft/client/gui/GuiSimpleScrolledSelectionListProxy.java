package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSimpleScrolledSelectionListProxy extends GuiSlot
{
    private final RealmsSimpleScrolledSelectionList field_178050_u;

    public GuiSimpleScrolledSelectionListProxy(RealmsSimpleScrolledSelectionList p_i45525_1_, int p_i45525_2_, int p_i45525_3_, int p_i45525_4_, int p_i45525_5_, int p_i45525_6_)
    {
        super(Minecraft.getInstance(), p_i45525_2_, p_i45525_3_, p_i45525_4_, p_i45525_5_, p_i45525_6_);
        this.field_178050_u = p_i45525_1_;
    }

    protected int getSize()
    {
        return this.field_178050_u.getItemCount();
    }

    protected void func_148144_a(int p_148144_1_, boolean p_148144_2_, int p_148144_3_, int p_148144_4_)
    {
        this.field_178050_u.selectItem(p_148144_1_, p_148144_2_, p_148144_3_, p_148144_4_);
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int slotIndex)
    {
        return this.field_178050_u.isSelectedItem(slotIndex);
    }

    protected void drawBackground()
    {
        this.field_178050_u.renderBackground();
    }

    protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks)
    {
        this.field_178050_u.renderItem(slotIndex, xPos, yPos, heightIn, mouseXIn, mouseYIn);
    }

    public int func_178048_e()
    {
        return this.width;
    }

    public int func_178047_f()
    {
        return this.field_148162_h;
    }

    public int func_178049_g()
    {
        return this.field_148150_g;
    }

    /**
     * Return the height of the content being scrolled
     */
    protected int getContentHeight()
    {
        return this.field_178050_u.getMaxPosition();
    }

    protected int getScrollBarX()
    {
        return this.field_178050_u.getScrollbarPosition();
    }

    public void func_178039_p()
    {
        super.func_178039_p();
    }

    public void drawScreen(int mouseXIn, int mouseYIn, float partialTicks)
    {
        if (this.visible)
        {
            this.field_148150_g = mouseXIn;
            this.field_148162_h = mouseYIn;
            this.drawBackground();
            int i = this.getScrollBarX();
            int j = i + 6;
            this.bindAmountScrolled();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            int k = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int l = this.top + 4 - (int)this.amountScrolled;

            if (this.hasListHeader)
            {
                this.drawListHeader(k, l, tessellator);
            }

            this.drawSelectionBox(k, l, mouseXIn, mouseYIn, partialTicks);
            GlStateManager.disableDepthTest();
            this.overlayBackground(0, this.top, 255, 255);
            this.overlayBackground(this.bottom, this.height, 255, 255);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            GlStateManager.disableAlphaTest();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableTexture2D();
            int i1 = this.getMaxScroll();

            if (i1 > 0)
            {
                int j1 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
                j1 = MathHelper.clamp(j1, 32, this.bottom - this.top - 8);
                int k1 = (int)this.amountScrolled * (this.bottom - this.top - j1) / i1 + this.top;

                if (k1 < this.top)
                {
                    k1 = this.top;
                }

                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos((double)i, (double)this.bottom, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos((double)j, (double)this.bottom, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos((double)j, (double)this.top, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos((double)i, (double)this.top, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                tessellator.draw();
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos((double)i, (double)(k1 + j1), 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos((double)j, (double)(k1 + j1), 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos((double)j, (double)k1, 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos((double)i, (double)k1, 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
                tessellator.draw();
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos((double)i, (double)(k1 + j1 - 1), 0.0D).tex(0.0D, 1.0D).color(192, 192, 192, 255).endVertex();
                bufferbuilder.pos((double)(j - 1), (double)(k1 + j1 - 1), 0.0D).tex(1.0D, 1.0D).color(192, 192, 192, 255).endVertex();
                bufferbuilder.pos((double)(j - 1), (double)k1, 0.0D).tex(1.0D, 0.0D).color(192, 192, 192, 255).endVertex();
                bufferbuilder.pos((double)i, (double)k1, 0.0D).tex(0.0D, 0.0D).color(192, 192, 192, 255).endVertex();
                tessellator.draw();
            }

            this.renderDecorations(mouseXIn, mouseYIn);
            GlStateManager.enableTexture2D();
            GlStateManager.shadeModel(7424);
            GlStateManager.enableAlphaTest();
            GlStateManager.disableBlend();
        }
    }
}