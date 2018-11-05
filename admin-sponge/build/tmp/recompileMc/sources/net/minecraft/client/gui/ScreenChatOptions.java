package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ScreenChatOptions extends GuiScreen
{
    private static final GameSettings.Options[] CHAT_OPTIONS = new GameSettings.Options[] {GameSettings.Options.CHAT_VISIBILITY, GameSettings.Options.CHAT_COLOR, GameSettings.Options.CHAT_LINKS, GameSettings.Options.CHAT_OPACITY, GameSettings.Options.CHAT_LINKS_PROMPT, GameSettings.Options.CHAT_SCALE, GameSettings.Options.CHAT_HEIGHT_FOCUSED, GameSettings.Options.CHAT_HEIGHT_UNFOCUSED, GameSettings.Options.CHAT_WIDTH, GameSettings.Options.REDUCED_DEBUG_INFO, GameSettings.Options.NARRATOR};
    private final GuiScreen parentScreen;
    private final GameSettings game_settings;
    private String chatTitle;
    private GuiOptionButton narratorButton;

    public ScreenChatOptions(GuiScreen parentScreenIn, GameSettings gameSettingsIn)
    {
        this.parentScreen = parentScreenIn;
        this.game_settings = gameSettingsIn;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.chatTitle = I18n.format("options.chat.title");
        int i = 0;

        for (GameSettings.Options gamesettings$options : CHAT_OPTIONS)
        {
            if (gamesettings$options.isFloat())
            {
                this.buttons.add(new GuiOptionSlider(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), gamesettings$options));
            }
            else
            {
                GuiOptionButton guioptionbutton = new GuiOptionButton(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), gamesettings$options, this.game_settings.getKeyBinding(gamesettings$options));
                this.buttons.add(guioptionbutton);

                if (gamesettings$options == GameSettings.Options.NARRATOR)
                {
                    this.narratorButton = guioptionbutton;
                    guioptionbutton.enabled = NarratorChatListener.INSTANCE.isActive();
                }
            }

            ++i;
        }

        this.buttons.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 144, I18n.format("gui.done")));
    }

    protected void func_73869_a(char p_73869_1_, int p_73869_2_) throws IOException
    {
        if (p_73869_2_ == 1)
        {
            this.mc.gameSettings.saveOptions();
        }

        super.func_73869_a(p_73869_1_, p_73869_2_);
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        if (p_146284_1_.enabled)
        {
            if (p_146284_1_.id < 100 && p_146284_1_ instanceof GuiOptionButton)
            {
                this.game_settings.setOptionValue(((GuiOptionButton)p_146284_1_).getOption(), 1);
                p_146284_1_.displayString = this.game_settings.getKeyBinding(GameSettings.Options.byOrdinal(p_146284_1_.id));
            }

            if (p_146284_1_.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.chatTitle, this.width / 2, 20, 16777215);
        super.render(mouseX, mouseY, partialTicks);
    }

    public void updateNarratorButton()
    {
        this.narratorButton.displayString = this.game_settings.getKeyBinding(GameSettings.Options.byOrdinal(this.narratorButton.id));
    }
}