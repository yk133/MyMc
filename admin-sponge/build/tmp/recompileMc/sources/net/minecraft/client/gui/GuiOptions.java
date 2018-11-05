package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiOptions extends GuiScreen
{
    private static final GameSettings.Options[] SCREEN_OPTIONS = new GameSettings.Options[] {GameSettings.Options.FOV};
    private final GuiScreen lastScreen;
    /** Reference to the GameSettings object. */
    private final GameSettings settings;
    private GuiButton difficultyButton;
    private GuiLockIconButton lockButton;
    protected String title = "Options";

    public GuiOptions(GuiScreen p_i1046_1_, GameSettings p_i1046_2_)
    {
        this.lastScreen = p_i1046_1_;
        this.settings = p_i1046_2_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.title = I18n.format("options.title");
        int i = 0;

        for (GameSettings.Options gamesettings$options : SCREEN_OPTIONS)
        {
            if (gamesettings$options.isFloat())
            {
                this.buttons.add(new GuiOptionSlider(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), gamesettings$options));
            }
            else
            {
                GuiOptionButton guioptionbutton = new GuiOptionButton(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), gamesettings$options, this.settings.getKeyBinding(gamesettings$options));
                this.buttons.add(guioptionbutton);
            }

            ++i;
        }

        if (this.mc.world != null)
        {
            EnumDifficulty enumdifficulty = this.mc.world.getDifficulty();
            this.difficultyButton = new GuiButton(108, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.getDifficultyText(enumdifficulty));
            this.buttons.add(this.difficultyButton);

            if (this.mc.isSingleplayer() && !this.mc.world.getWorldInfo().isHardcore())
            {
                this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
                this.lockButton = new GuiLockIconButton(109, this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y);
                this.buttons.add(this.lockButton);
                this.lockButton.setLocked(this.mc.world.getWorldInfo().isDifficultyLocked());
                this.lockButton.enabled = !this.lockButton.isLocked();
                this.difficultyButton.enabled = !this.lockButton.isLocked();
            }
            else
            {
                this.difficultyButton.enabled = false;
            }
        }
        else
        {
            this.buttons.add(new GuiOptionButton(GameSettings.Options.REALMS_NOTIFICATIONS.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), GameSettings.Options.REALMS_NOTIFICATIONS, this.settings.getKeyBinding(GameSettings.Options.REALMS_NOTIFICATIONS)));
        }

        this.buttons.add(new GuiButton(110, this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.format("options.skinCustomisation")));
        this.buttons.add(new GuiButton(106, this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, I18n.format("options.sounds")));
        this.buttons.add(new GuiButton(101, this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.video")));
        this.buttons.add(new GuiButton(100, this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.controls")));
        this.buttons.add(new GuiButton(102, this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.language")));
        this.buttons.add(new GuiButton(103, this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.chat.title")));
        this.buttons.add(new GuiButton(105, this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.resourcepack")));
        this.buttons.add(new GuiButton(104, this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.snooper.view")));
        this.buttons.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
    }

    public String getDifficultyText(EnumDifficulty p_175355_1_)
    {
        ITextComponent itextcomponent = new TextComponentString("");
        itextcomponent.appendSibling(new TextComponentTranslation("options.difficulty", new Object[0]));
        itextcomponent.appendText(": ");
        itextcomponent.appendSibling(new TextComponentTranslation(p_175355_1_.getTranslationKey(), new Object[0]));
        return itextcomponent.getFormattedText();
    }

    public void func_73878_a(boolean p_73878_1_, int p_73878_2_)
    {
        this.mc.displayGuiScreen(this);

        if (p_73878_2_ == 109 && p_73878_1_ && this.mc.world != null)
        {
            this.mc.world.getWorldInfo().setDifficultyLocked(true);
            this.lockButton.setLocked(true);
            this.lockButton.enabled = false;
            this.difficultyButton.enabled = false;
        }
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
                GameSettings.Options gamesettings$options = ((GuiOptionButton)p_146284_1_).getOption();
                this.settings.setOptionValue(gamesettings$options, 1);
                p_146284_1_.displayString = this.settings.getKeyBinding(GameSettings.Options.byOrdinal(p_146284_1_.id));
            }

            if (p_146284_1_.id == 108)
            {
                this.mc.world.getWorldInfo().setDifficulty(EnumDifficulty.byId(this.mc.world.getDifficulty().getId() + 1));
                this.difficultyButton.displayString = this.getDifficultyText(this.mc.world.getDifficulty());
            }

            if (p_146284_1_.id == 109)
            {
                this.mc.displayGuiScreen(new GuiYesNo(this, (new TextComponentTranslation("difficulty.lock.title", new Object[0])).getFormattedText(), (new TextComponentTranslation("difficulty.lock.question", new Object[] {new TextComponentTranslation(this.mc.world.getWorldInfo().getDifficulty().getTranslationKey(), new Object[0])})).getFormattedText(), 109));
            }

            if (p_146284_1_.id == 110)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiCustomizeSkin(this));
            }

            if (p_146284_1_.id == 101)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiVideoSettings(this, this.settings));
            }

            if (p_146284_1_.id == 100)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiControls(this, this.settings));
            }

            if (p_146284_1_.id == 102)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiLanguage(this, this.settings, this.mc.getLanguageManager()));
            }

            if (p_146284_1_.id == 103)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new ScreenChatOptions(this, this.settings));
            }

            if (p_146284_1_.id == 104)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiSnooper(this, this.settings));
            }

            if (p_146284_1_.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.lastScreen);
            }

            if (p_146284_1_.id == 105)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiScreenResourcePacks(this));
            }

            if (p_146284_1_.id == 106)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiScreenOptionsSounds(this, this.settings));
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 15, 16777215);
        super.render(mouseX, mouseY, partialTicks);
    }
}