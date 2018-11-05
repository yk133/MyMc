package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiConfirmOpenLink extends GuiYesNo
{
    /** Text to warn players from opening unsafe links. */
    private final String openLinkWarning;
    /** Label for the Copy to Clipboard button. */
    private final String copyLinkButtonText;
    private final String linkText;
    private boolean showSecurityWarning = true;

    public GuiConfirmOpenLink(GuiYesNoCallback parentScreenIn, String linkTextIn, int parentButtonClickedIdIn, boolean trusted)
    {
        super(parentScreenIn, I18n.format(trusted ? "chat.link.confirmTrusted" : "chat.link.confirm"), linkTextIn, parentButtonClickedIdIn);
        this.confirmButtonText = I18n.format(trusted ? "chat.link.open" : "gui.yes");
        this.cancelButtonText = I18n.format(trusted ? "gui.cancel" : "gui.no");
        this.copyLinkButtonText = I18n.format("chat.copy");
        this.openLinkWarning = I18n.format("chat.link.warning");
        this.linkText = linkTextIn;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        super.initGui();
        this.buttons.clear();
        this.buttons.add(new GuiButton(0, this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.confirmButtonText));
        this.buttons.add(new GuiButton(2, this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copyLinkButtonText));
        this.buttons.add(new GuiButton(1, this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.cancelButtonText));
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        if (p_146284_1_.id == 2)
        {
            this.copyLinkToClipboard();
        }

        this.parentScreen.func_73878_a(p_146284_1_.id == 0, this.parentButtonClickedId);
    }

    /**
     * Copies the link to the system clipboard.
     */
    public void copyLinkToClipboard()
    {
        func_146275_d(this.linkText);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        super.render(mouseX, mouseY, partialTicks);

        if (this.showSecurityWarning)
        {
            this.drawCenteredString(this.fontRenderer, this.openLinkWarning, this.width / 2, 110, 16764108);
        }
    }

    public void disableSecurityWarning()
    {
        this.showSecurityWarning = false;
    }
}