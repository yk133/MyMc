package net.minecraft.client.gui;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDisconnected extends GuiScreen
{
    private final String reason;
    private final ITextComponent message;
    private List<String> multilineMessage;
    private final GuiScreen parentScreen;
    private int textHeight;

    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, ITextComponent chatComp)
    {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey);
        this.message = chatComp;
    }

    protected void func_73869_a(char p_73869_1_, int p_73869_2_) throws IOException
    {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttons.clear();
        this.multilineMessage = this.fontRenderer.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.textHeight = this.multilineMessage.size() * this.fontRenderer.FONT_HEIGHT;
        this.buttons.add(new GuiButton(0, this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + this.fontRenderer.FONT_HEIGHT, this.height - 30), I18n.format("gui.toMenu")));
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        if (p_146284_1_.id == 0)
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.reason, this.width / 2, this.height / 2 - this.textHeight / 2 - this.fontRenderer.FONT_HEIGHT * 2, 11184810);
        int i = this.height / 2 - this.textHeight / 2;

        if (this.multilineMessage != null)
        {
            for (String s : this.multilineMessage)
            {
                this.drawCenteredString(this.fontRenderer, s, this.width / 2, i, 16777215);
                i += this.fontRenderer.FONT_HEIGHT;
            }
        }

        super.render(mouseX, mouseY, partialTicks);
    }
}