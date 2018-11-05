package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenRealmsProxy extends GuiScreen
{
    private final RealmsScreen proxy;

    public GuiScreenRealmsProxy(RealmsScreen proxyIn)
    {
        this.proxy = proxyIn;
        this.buttons = Collections.<GuiButton>synchronizedList(Lists.newArrayList());
    }

    public RealmsScreen getProxy()
    {
        return this.proxy;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.proxy.init();
        super.initGui();
    }

    public void drawCenteredString(String text, int x, int y, int color)
    {
        super.drawCenteredString(this.fontRenderer, text, x, y, color);
    }

    public void func_154322_b(String p_154322_1_, int p_154322_2_, int p_154322_3_, int p_154322_4_, boolean p_154322_5_)
    {
        if (p_154322_5_)
        {
            super.drawString(this.fontRenderer, p_154322_1_, p_154322_2_, p_154322_3_, p_154322_4_);
        }
        else
        {
            this.fontRenderer.func_78276_b(p_154322_1_, p_154322_2_, p_154322_3_, p_154322_4_);
        }
    }

    /**
     * Draws a textured rectangle at the current z-value.
     */
    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        this.proxy.blit(x, y, textureX, textureY, width, height);
        super.drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    /**
     * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     */
    public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
    {
        super.drawGradientRect(left, top, right, bottom, startColor, endColor);
    }

    /**
     * Draws either a gradient over the background world (if there is a world), or a dirt screen if there is no world.
     *  
     * This method should usually be called before doing any other rendering; otherwise weird results will occur if
     * there is no world, and the world will not be tinted if there is.
     *  
     * Do not call after having already done other rendering, as it will draw over it.
     */
    public void drawDefaultBackground()
    {
        super.drawDefaultBackground();
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return super.doesGuiPauseGame();
    }

    /**
     * Draws either a gradient over the background world (if there is a world), or a dirt screen if there is no world.
     *  
     * This method should usually be called before doing any other rendering; otherwise weird results will occur if
     * there is no world, and the world will not be tinted if there is.
     *  
     * Do not call after having already done other rendering, as it will draw over it.
     */
    public void drawWorldBackground(int tint)
    {
        super.drawWorldBackground(tint);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.proxy.render(mouseX, mouseY, partialTicks);
    }

    public void renderToolTip(ItemStack stack, int x, int y)
    {
        super.renderToolTip(stack, x, y);
    }

    /**
     * Draws the given text as a tooltip.
     */
    public void drawHoveringText(String text, int x, int y)
    {
        super.drawHoveringText(text, x, y);
    }

    /**
     * Draws a List of strings as a tooltip. Every entry is drawn on a seperate line.
     */
    public void drawHoveringText(List<String> textLines, int x, int y)
    {
        super.drawHoveringText(textLines, x, y);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void tick()
    {
        this.proxy.tick();
        super.tick();
    }

    public int getFontHeight()
    {
        return this.fontRenderer.FONT_HEIGHT;
    }

    public int func_154326_c(String p_154326_1_)
    {
        return this.fontRenderer.getStringWidth(p_154326_1_);
    }

    public void func_154319_c(String p_154319_1_, int p_154319_2_, int p_154319_3_, int p_154319_4_)
    {
        this.fontRenderer.drawStringWithShadow(p_154319_1_, (float)p_154319_2_, (float)p_154319_3_, p_154319_4_);
    }

    public List<String> fontSplit(String text, int wrapWidth)
    {
        return this.fontRenderer.listFormattedStringToWidth(text, wrapWidth);
    }

    public final void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        this.proxy.buttonClicked(((GuiButtonRealmsProxy)p_146284_1_).getRealmsButton());
    }

    public void func_154324_i()
    {
        this.buttons.clear();
    }

    public void buttonsAdd(RealmsButton button)
    {
        this.buttons.add(button.getProxy());
    }

    public List<RealmsButton> buttons()
    {
        List<RealmsButton> list = Lists.<RealmsButton>newArrayListWithExpectedSize(this.buttons.size());

        for (GuiButton guibutton : this.buttons)
        {
            list.add(((GuiButtonRealmsProxy)guibutton).getRealmsButton());
        }

        return list;
    }

    public void func_154328_b(RealmsButton p_154328_1_)
    {
        this.buttons.remove(p_154328_1_.getProxy());
    }

    public void func_73864_a(int p_73864_1_, int p_73864_2_, int p_73864_3_) throws IOException
    {
        this.proxy.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        super.func_73864_a(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    public void func_146274_d() throws IOException
    {
        this.proxy.mouseEvent();
        super.func_146274_d();
    }

    public void func_146282_l() throws IOException
    {
        this.proxy.keyboardEvent();
        super.func_146282_l();
    }

    public void func_146286_b(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        this.proxy.mouseReleased(p_146286_1_, p_146286_2_, p_146286_3_);
    }

    public void func_146273_a(int p_146273_1_, int p_146273_2_, int p_146273_3_, long p_146273_4_)
    {
        this.proxy.mouseDragged(p_146273_1_, p_146273_2_, p_146273_3_, p_146273_4_);
    }

    public void func_73869_a(char p_73869_1_, int p_73869_2_) throws IOException
    {
        this.proxy.keyPressed(p_73869_1_, p_73869_2_);
    }

    public void func_73878_a(boolean p_73878_1_, int p_73878_2_)
    {
        this.proxy.confirmResult(p_73878_1_, p_73878_2_);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        this.proxy.removed();
        super.onGuiClosed();
    }
}