package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiOptionSlider extends GuiButton
{
    private float sliderValue;
    public boolean dragging;
    private final GameSettings.Options options;
    private final float minValue;
    private final float maxValue;

    public GuiOptionSlider(int buttonId, int x, int y, GameSettings.Options optionIn)
    {
        this(buttonId, x, y, optionIn, 0.0F, 1.0F);
    }

    public GuiOptionSlider(int p_i45017_1_, int p_i45017_2_, int p_i45017_3_, GameSettings.Options p_i45017_4_, float p_i45017_5_, float p_i45017_6_)
    {
        super(p_i45017_1_, p_i45017_2_, p_i45017_3_, 150, 20, "");
        this.sliderValue = 1.0F;
        this.options = p_i45017_4_;
        this.minValue = p_i45017_5_;
        this.maxValue = p_i45017_6_;
        Minecraft minecraft = Minecraft.getInstance();
        this.sliderValue = p_i45017_4_.func_148266_c(minecraft.gameSettings.func_74296_a(p_i45017_4_));
        this.displayString = minecraft.gameSettings.getKeyBinding(p_i45017_4_);
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
            if (this.dragging)
            {
                this.sliderValue = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
                this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);
                float f = this.options.func_148262_d(this.sliderValue);
                mc.gameSettings.func_74304_a(this.options, f);
                this.sliderValue = this.options.func_148266_c(f);
                this.displayString = mc.gameSettings.getKeyBinding(this.options);
            }

            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
        }
    }

    public boolean func_146116_c(Minecraft p_146116_1_, int p_146116_2_, int p_146116_3_)
    {
        if (super.func_146116_c(p_146116_1_, p_146116_2_, p_146116_3_))
        {
            this.sliderValue = (float)(p_146116_2_ - (this.x + 4)) / (float)(this.width - 8);
            this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);
            p_146116_1_.gameSettings.func_74304_a(this.options, this.options.func_148262_d(this.sliderValue));
            this.displayString = p_146116_1_.gameSettings.getKeyBinding(this.options);
            this.dragging = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    public void func_146118_a(int p_146118_1_, int p_146118_2_)
    {
        this.dragging = false;
    }
}