package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSlider extends GuiButton
{
    private float field_175227_p = 1.0F;
    public boolean field_175228_o;
    private final String field_175226_q;
    private final float field_175225_r;
    private final float field_175224_s;
    private final GuiPageButtonList.GuiResponder field_175223_t;
    private GuiSlider.FormatHelper field_175222_u;

    public GuiSlider(GuiPageButtonList.GuiResponder p_i45541_1_, int p_i45541_2_, int p_i45541_3_, int p_i45541_4_, String p_i45541_5_, float p_i45541_6_, float p_i45541_7_, float p_i45541_8_, GuiSlider.FormatHelper p_i45541_9_)
    {
        super(p_i45541_2_, p_i45541_3_, p_i45541_4_, 150, 20, "");
        this.field_175226_q = p_i45541_5_;
        this.field_175225_r = p_i45541_6_;
        this.field_175224_s = p_i45541_7_;
        this.field_175227_p = (p_i45541_8_ - p_i45541_6_) / (p_i45541_7_ - p_i45541_6_);
        this.field_175222_u = p_i45541_9_;
        this.field_175223_t = p_i45541_1_;
        this.displayString = this.func_175221_e();
    }

    public float func_175220_c()
    {
        return this.field_175225_r + (this.field_175224_s - this.field_175225_r) * this.field_175227_p;
    }

    public void func_175218_a(float p_175218_1_, boolean p_175218_2_)
    {
        this.field_175227_p = (p_175218_1_ - this.field_175225_r) / (this.field_175224_s - this.field_175225_r);
        this.displayString = this.func_175221_e();

        if (p_175218_2_)
        {
            this.field_175223_t.func_175320_a(this.id, this.func_175220_c());
        }
    }

    public float func_175217_d()
    {
        return this.field_175227_p;
    }

    private String func_175221_e()
    {
        return this.field_175222_u == null ? I18n.format(this.field_175226_q) + ": " + this.func_175220_c() : this.field_175222_u.func_175318_a(this.id, I18n.format(this.field_175226_q), this.func_175220_c());
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean mouseOver)
    {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void renderBg(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            if (this.field_175228_o)
            {
                this.field_175227_p = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);

                if (this.field_175227_p < 0.0F)
                {
                    this.field_175227_p = 0.0F;
                }

                if (this.field_175227_p > 1.0F)
                {
                    this.field_175227_p = 1.0F;
                }

                this.displayString = this.func_175221_e();
                this.field_175223_t.func_175320_a(this.id, this.func_175220_c());
            }

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.x + (int)(this.field_175227_p * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.x + (int)(this.field_175227_p * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
        }
    }

    public void func_175219_a(float p_175219_1_)
    {
        this.field_175227_p = p_175219_1_;
        this.displayString = this.func_175221_e();
        this.field_175223_t.func_175320_a(this.id, this.func_175220_c());
    }

    public boolean func_146116_c(Minecraft p_146116_1_, int p_146116_2_, int p_146116_3_)
    {
        if (super.func_146116_c(p_146116_1_, p_146116_2_, p_146116_3_))
        {
            this.field_175227_p = (float)(p_146116_2_ - (this.x + 4)) / (float)(this.width - 8);

            if (this.field_175227_p < 0.0F)
            {
                this.field_175227_p = 0.0F;
            }

            if (this.field_175227_p > 1.0F)
            {
                this.field_175227_p = 1.0F;
            }

            this.displayString = this.func_175221_e();
            this.field_175223_t.func_175320_a(this.id, this.func_175220_c());
            this.field_175228_o = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    public void func_146118_a(int p_146118_1_, int p_146118_2_)
    {
        this.field_175228_o = false;
    }

    @SideOnly(Side.CLIENT)
    public interface FormatHelper
    {
        String func_175318_a(int p_175318_1_, String p_175318_2_, float p_175318_3_);
    }
}