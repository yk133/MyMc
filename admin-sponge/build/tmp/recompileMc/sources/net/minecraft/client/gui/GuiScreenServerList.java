package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenServerList extends GuiScreen
{
    private final GuiScreen lastScreen;
    private final ServerData serverData;
    private GuiTextField ipEdit;

    public GuiScreenServerList(GuiScreen lastScreenIn, ServerData serverDataIn)
    {
        this.lastScreen = lastScreenIn;
        this.serverData = serverDataIn;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void tick()
    {
        this.ipEdit.tick();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.buttons.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("selectServer.select")));
        this.buttons.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
        this.ipEdit = new GuiTextField(2, this.fontRenderer, this.width / 2 - 100, 116, 200, 20);
        this.ipEdit.setMaxStringLength(128);
        this.ipEdit.setFocused(true);
        this.ipEdit.setText(this.mc.gameSettings.lastServer);
        (this.buttons.get(0)).enabled = !this.ipEdit.getText().isEmpty() && this.ipEdit.getText().split(":").length > 0;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        this.mc.gameSettings.lastServer = this.ipEdit.getText();
        this.mc.gameSettings.saveOptions();
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        if (p_146284_1_.enabled)
        {
            if (p_146284_1_.id == 1)
            {
                this.lastScreen.func_73878_a(false, 0);
            }
            else if (p_146284_1_.id == 0)
            {
                this.serverData.serverIP = this.ipEdit.getText();
                this.lastScreen.func_73878_a(true, 0);
            }
        }
    }

    protected void func_73869_a(char p_73869_1_, int p_73869_2_) throws IOException
    {
        if (this.ipEdit.func_146201_a(p_73869_1_, p_73869_2_))
        {
            (this.buttons.get(0)).enabled = !this.ipEdit.getText().isEmpty() && this.ipEdit.getText().split(":").length > 0;
        }
        else if (p_73869_2_ == 28 || p_73869_2_ == 156)
        {
            this.func_146284_a(this.buttons.get(0));
        }
    }

    protected void func_73864_a(int p_73864_1_, int p_73864_2_, int p_73864_3_) throws IOException
    {
        super.func_73864_a(p_73864_1_, p_73864_2_, p_73864_3_);
        this.ipEdit.func_146192_a(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format("selectServer.direct"), this.width / 2, 20, 16777215);
        this.drawString(this.fontRenderer, I18n.format("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
        this.ipEdit.func_146194_f();
        super.render(mouseX, mouseY, partialTicks);
    }
}