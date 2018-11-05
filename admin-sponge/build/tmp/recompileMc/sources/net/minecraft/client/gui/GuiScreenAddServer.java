package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import java.io.IOException;
import java.net.IDN;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenAddServer extends GuiScreen
{
    private final GuiScreen parentScreen;
    private final ServerData serverData;
    private GuiTextField serverIPField;
    private GuiTextField serverNameField;
    private GuiButton serverResourcePacks;
    private final Predicate<String> addressFilter = new Predicate<String>()
    {
        public boolean apply(@Nullable String p_apply_1_)
        {
            if (StringUtils.isNullOrEmpty(p_apply_1_))
            {
                return true;
            }
            else
            {
                String[] astring = p_apply_1_.split(":");

                if (astring.length == 0)
                {
                    return true;
                }
                else
                {
                    try
                    {
                        String s = IDN.toASCII(astring[0]);
                        return true;
                    }
                    catch (IllegalArgumentException var4)
                    {
                        return false;
                    }
                }
            }
        }
    };

    public GuiScreenAddServer(GuiScreen parentScreenIn, ServerData serverDataIn)
    {
        this.parentScreen = parentScreenIn;
        this.serverData = serverDataIn;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void tick()
    {
        this.serverNameField.tick();
        this.serverIPField.tick();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.buttons.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 18, I18n.format("addServer.add")));
        this.buttons.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 18, I18n.format("gui.cancel")));
        this.serverResourcePacks = this.addButton(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 72, I18n.format("addServer.resourcePack") + ": " + this.serverData.getResourceMode().getMotd().getFormattedText()));
        this.serverNameField = new GuiTextField(0, this.fontRenderer, this.width / 2 - 100, 66, 200, 20);
        this.serverNameField.setFocused(true);
        this.serverNameField.setText(this.serverData.serverName);
        this.serverIPField = new GuiTextField(1, this.fontRenderer, this.width / 2 - 100, 106, 200, 20);
        this.serverIPField.setMaxStringLength(128);
        this.serverIPField.setText(this.serverData.serverIP);
        this.serverIPField.func_175205_a(this.addressFilter);
        (this.buttons.get(0)).enabled = !this.serverIPField.getText().isEmpty() && this.serverIPField.getText().split(":").length > 0 && !this.serverNameField.getText().isEmpty();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        if (p_146284_1_.enabled)
        {
            if (p_146284_1_.id == 2)
            {
                this.serverData.setResourceMode(ServerData.ServerResourceMode.values()[(this.serverData.getResourceMode().ordinal() + 1) % ServerData.ServerResourceMode.values().length]);
                this.serverResourcePacks.displayString = I18n.format("addServer.resourcePack") + ": " + this.serverData.getResourceMode().getMotd().getFormattedText();
            }
            else if (p_146284_1_.id == 1)
            {
                this.parentScreen.func_73878_a(false, 0);
            }
            else if (p_146284_1_.id == 0)
            {
                this.serverData.serverName = this.serverNameField.getText();
                this.serverData.serverIP = this.serverIPField.getText();
                this.parentScreen.func_73878_a(true, 0);
            }
        }
    }

    protected void func_73869_a(char p_73869_1_, int p_73869_2_) throws IOException
    {
        this.serverNameField.func_146201_a(p_73869_1_, p_73869_2_);
        this.serverIPField.func_146201_a(p_73869_1_, p_73869_2_);

        if (p_73869_2_ == 15)
        {
            this.serverNameField.setFocused(!this.serverNameField.isFocused());
            this.serverIPField.setFocused(!this.serverIPField.isFocused());
        }

        if (p_73869_2_ == 28 || p_73869_2_ == 156)
        {
            this.func_146284_a(this.buttons.get(0));
        }

        (this.buttons.get(0)).enabled = !this.serverIPField.getText().isEmpty() && this.serverIPField.getText().split(":").length > 0 && !this.serverNameField.getText().isEmpty();
    }

    protected void func_73864_a(int p_73864_1_, int p_73864_2_, int p_73864_3_) throws IOException
    {
        super.func_73864_a(p_73864_1_, p_73864_2_, p_73864_3_);
        this.serverIPField.func_146192_a(p_73864_1_, p_73864_2_, p_73864_3_);
        this.serverNameField.func_146192_a(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format("addServer.title"), this.width / 2, 17, 16777215);
        this.drawString(this.fontRenderer, I18n.format("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
        this.drawString(this.fontRenderer, I18n.format("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
        this.serverNameField.func_146194_f();
        this.serverIPField.func_146194_f();
        super.render(mouseX, mouseY, partialTicks);
    }
}