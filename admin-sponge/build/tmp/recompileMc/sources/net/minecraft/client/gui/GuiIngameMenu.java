package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiIngameMenu extends GuiScreen
{
    private int field_146445_a;
    private int field_146444_f;

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.field_146445_a = 0;
        this.buttons.clear();
        int i = -16;
        int j = 98;
        this.buttons.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + -16, I18n.format("menu.returnToMenu")));

        if (!this.mc.isIntegratedServerRunning())
        {
            (this.buttons.get(0)).displayString = I18n.format("menu.disconnect");
        }

        this.buttons.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + -16, I18n.format("menu.returnToGame")));
        this.buttons.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + -16, 98, 20, I18n.format("menu.options")));
        this.buttons.add(new GuiButton(12, this.width / 2 + 2, this.height / 4 + 96 + i, 98, 20, I18n.format("fml.menu.modoptions")));
        GuiButton guibutton = this.addButton(new GuiButton(7, this.width / 2 - 100, this.height / 4 + 72 + -16, 200, 20, I18n.format("menu.shareToLan", new Object[0])));
        guibutton.enabled = this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic();
        this.buttons.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + -16, 98, 20, I18n.format("gui.advancements")));
        this.buttons.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + -16, 98, 20, I18n.format("gui.stats")));
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        switch (p_146284_1_.id)
        {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            case 1:
                boolean flag = this.mc.isIntegratedServerRunning();
                boolean flag1 = this.mc.isConnectedToRealms();
                p_146284_1_.enabled = false;
                this.mc.world.sendQuittingDisconnectingPacket();
                this.mc.loadWorld((WorldClient)null);

                if (flag)
                {
                    this.mc.displayGuiScreen(new GuiMainMenu());
                }
                else if (flag1)
                {
                    RealmsBridge realmsbridge = new RealmsBridge();
                    realmsbridge.switchToRealms(new GuiMainMenu());
                }
                else
                {
                    this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
                }

            case 2:
            case 3:
            default:
                break;
            case 4:
                this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.func_71381_h();
                break;
            case 5:
                if (this.mc.player != null)
                this.mc.displayGuiScreen(new GuiScreenAdvancements(this.mc.player.connection.getAdvancementManager()));
                break;
            case 6:
                if (this.mc.player != null)
                this.mc.displayGuiScreen(new GuiStats(this, this.mc.player.getStats()));
                break;
            case 7:
                this.mc.displayGuiScreen(new GuiShareToLan(this));
                break;
            case 12:
                net.minecraftforge.fml.client.FMLClientHandler.instance().showInGameModOptions(this);
                break;
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void tick()
    {
        super.tick();
        ++this.field_146444_f;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format("menu.game"), this.width / 2, 40, 16777215);
        super.render(mouseX, mouseY, partialTicks);
    }
}