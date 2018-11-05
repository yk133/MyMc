package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiErrorScreen extends GuiScreen
{
    private final String title;
    private final String message;

    public GuiErrorScreen(String titleIn, String messageIn)
    {
        this.title = titleIn;
        this.message = messageIn;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        super.initGui();
        this.buttons.add(new GuiButton(0, this.width / 2 - 100, 140, I18n.format("gui.cancel")));
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawGradientRect(0, 0, this.width, this.height, -12574688, -11530224);
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 90, 16777215);
        this.drawCenteredString(this.fontRenderer, this.message, this.width / 2, 110, 16777215);
        super.render(mouseX, mouseY, partialTicks);
    }

    protected void func_73869_a(char p_73869_1_, int p_73869_2_) throws IOException
    {
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        this.mc.displayGuiScreen((GuiScreen)null);
    }
}