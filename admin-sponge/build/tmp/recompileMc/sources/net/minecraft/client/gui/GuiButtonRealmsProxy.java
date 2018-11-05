package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonRealmsProxy extends GuiButton
{
    private final RealmsButton realmsButton;

    public GuiButtonRealmsProxy(RealmsButton realmsButtonIn, int buttonId, int x, int y, String text)
    {
        super(buttonId, x, y, text);
        this.realmsButton = realmsButtonIn;
    }

    public GuiButtonRealmsProxy(RealmsButton realmsButtonIn, int buttonId, int x, int y, String text, int widthIn, int heightIn)
    {
        super(buttonId, x, y, widthIn, heightIn, text);
        this.realmsButton = realmsButtonIn;
    }

    public int func_154314_d()
    {
        return this.id;
    }

    public boolean func_154315_e()
    {
        return this.enabled;
    }

    public void func_154313_b(boolean p_154313_1_)
    {
        this.enabled = p_154313_1_;
    }

    public void func_154311_a(String p_154311_1_)
    {
        super.displayString = p_154311_1_;
    }

    public int getWidth()
    {
        return super.getWidth();
    }

    public int func_154316_f()
    {
        return this.y;
    }

    public boolean func_146116_c(Minecraft p_146116_1_, int p_146116_2_, int p_146116_3_)
    {
        if (super.func_146116_c(p_146116_1_, p_146116_2_, p_146116_3_))
        {
            this.realmsButton.clicked(p_146116_2_, p_146116_3_);
        }

        return super.func_146116_c(p_146116_1_, p_146116_2_, p_146116_3_);
    }

    public void func_146118_a(int p_146118_1_, int p_146118_2_)
    {
        this.realmsButton.released(p_146118_1_, p_146118_2_);
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    public void renderBg(Minecraft mc, int mouseX, int mouseY)
    {
        this.realmsButton.renderBg(mouseX, mouseY);
    }

    public RealmsButton getRealmsButton()
    {
        return this.realmsButton;
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    public int getHoverState(boolean mouseOver)
    {
        return this.realmsButton.getYImage(mouseOver);
    }

    public int getYImage(boolean p_154312_1_)
    {
        return super.getHoverState(p_154312_1_);
    }

    public int func_175232_g()
    {
        return this.height;
    }
}