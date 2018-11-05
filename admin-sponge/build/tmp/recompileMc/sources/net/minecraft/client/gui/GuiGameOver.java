package net.minecraft.client.gui;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGameOver extends GuiScreen
{
    /** The integer value containing the number of ticks that have passed since the player's death */
    private int enableButtonsTimer;
    private final ITextComponent causeOfDeath;

    public GuiGameOver(@Nullable ITextComponent causeOfDeathIn)
    {
        this.causeOfDeath = causeOfDeathIn;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttons.clear();
        this.enableButtonsTimer = 0;

        if (this.mc.world.getWorldInfo().isHardcore())
        {
            this.buttons.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 72, I18n.format("deathScreen.spectate")));
            this.buttons.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen." + (this.mc.isIntegratedServerRunning() ? "deleteWorld" : "leaveServer"))));
        }
        else
        {
            this.buttons.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 72, I18n.format("deathScreen.respawn")));
            this.buttons.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.titleScreen")));

            if (this.mc.getSession() == null)
            {
                (this.buttons.get(1)).enabled = false;
            }
        }

        for (GuiButton guibutton : this.buttons)
        {
            guibutton.enabled = false;
        }
    }

    protected void func_73869_a(char p_73869_1_, int p_73869_2_) throws IOException
    {
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        switch (p_146284_1_.id)
        {
            case 0:
                this.mc.player.respawnPlayer();
                this.mc.displayGuiScreen((GuiScreen)null);
                break;
            case 1:

                if (this.mc.world.getWorldInfo().isHardcore())
                {
                    this.mc.displayGuiScreen(new GuiMainMenu());
                }
                else
                {
                    GuiYesNo guiyesno = new GuiYesNo(this, I18n.format("deathScreen.quit.confirm"), "", I18n.format("deathScreen.titleScreen"), I18n.format("deathScreen.respawn"), 0);
                    this.mc.displayGuiScreen(guiyesno);
                    guiyesno.setButtonDelay(20);
                }
        }
    }

    public void func_73878_a(boolean p_73878_1_, int p_73878_2_)
    {
        if (p_73878_1_)
        {
            if (this.mc.world != null)
            {
                this.mc.world.sendQuittingDisconnectingPacket();
            }

            this.mc.loadWorld((WorldClient)null);
            this.mc.displayGuiScreen(new GuiMainMenu());
        }
        else
        {
            this.mc.player.respawnPlayer();
            this.mc.displayGuiScreen((GuiScreen)null);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        boolean flag = this.mc.world.getWorldInfo().isHardcore();
        this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(2.0F, 2.0F, 2.0F);
        this.drawCenteredString(this.fontRenderer, I18n.format(flag ? "deathScreen.title.hardcore" : "deathScreen.title"), this.width / 2 / 2, 30, 16777215);
        GlStateManager.popMatrix();

        if (this.causeOfDeath != null)
        {
            this.drawCenteredString(this.fontRenderer, this.causeOfDeath.getFormattedText(), this.width / 2, 85, 16777215);
        }

        this.drawCenteredString(this.fontRenderer, I18n.format("deathScreen.score") + ": " + TextFormatting.YELLOW + this.mc.player.getScore(), this.width / 2, 100, 16777215);

        if (this.causeOfDeath != null && mouseY > 85 && mouseY < 85 + this.fontRenderer.FONT_HEIGHT)
        {
            ITextComponent itextcomponent = this.getClickedComponentAt(mouseX);

            if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null)
            {
                this.handleComponentHover(itextcomponent, mouseX, mouseY);
            }
        }

        super.render(mouseX, mouseY, partialTicks);
    }

    @Nullable
    public ITextComponent getClickedComponentAt(int p_184870_1_)
    {
        if (this.causeOfDeath == null)
        {
            return null;
        }
        else
        {
            int i = this.mc.fontRenderer.getStringWidth(this.causeOfDeath.getFormattedText());
            int j = this.width / 2 - i / 2;
            int k = this.width / 2 + i / 2;
            int l = j;

            if (p_184870_1_ >= j && p_184870_1_ <= k)
            {
                for (ITextComponent itextcomponent : this.causeOfDeath)
                {
                    l += this.mc.fontRenderer.getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured(itextcomponent.getUnformattedComponentText(), false));

                    if (l > p_184870_1_)
                    {
                        return itextcomponent;
                    }
                }

                return null;
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void tick()
    {
        super.tick();
        ++this.enableButtonsTimer;

        if (this.enableButtonsTimer == 20)
        {
            for (GuiButton guibutton : this.buttons)
            {
                guibutton.enabled = true;
            }
        }
    }
}