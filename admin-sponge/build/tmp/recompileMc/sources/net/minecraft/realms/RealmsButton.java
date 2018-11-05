package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonRealmsProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RealmsButton
{
    protected static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    private final GuiButtonRealmsProxy proxy;

    public RealmsButton(int buttonId, int x, int y, String text)
    {
        this.proxy = new GuiButtonRealmsProxy(this, buttonId, x, y, text);
    }

    public RealmsButton(int buttonId, int x, int y, int widthIn, int heightIn, String text)
    {
        this.proxy = new GuiButtonRealmsProxy(this, buttonId, x, y, text, widthIn, heightIn);
    }

    public GuiButton getProxy()
    {
        return this.proxy;
    }

    public int id()
    {
        return this.proxy.func_154314_d();
    }

    public boolean active()
    {
        return this.proxy.func_154315_e();
    }

    public void active(boolean p_active_1_)
    {
        this.proxy.func_154313_b(p_active_1_);
    }

    public void msg(String p_msg_1_)
    {
        this.proxy.func_154311_a(p_msg_1_);
    }

    public int getWidth()
    {
        return this.proxy.getWidth();
    }

    public int getHeight()
    {
        return this.proxy.func_175232_g();
    }

    public int y()
    {
        return this.proxy.func_154316_f();
    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_)
    {
        this.proxy.func_191745_a(Minecraft.getInstance(), p_render_1_, p_render_2_, p_render_3_);
    }

    public void clicked(int p_clicked_1_, int p_clicked_2_)
    {
    }

    public void released(int p_released_1_, int p_released_2_)
    {
    }

    public void blit(int p_blit_1_, int p_blit_2_, int p_blit_3_, int p_blit_4_, int p_blit_5_, int p_blit_6_)
    {
        this.proxy.drawTexturedModalRect(p_blit_1_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_);
    }

    public void renderBg(int p_renderBg_1_, int p_renderBg_2_)
    {
    }

    public int getYImage(boolean p_getYImage_1_)
    {
        return this.proxy.getYImage(p_getYImage_1_);
    }
}