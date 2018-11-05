package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiVideoSettings extends GuiScreen
{
    private final GuiScreen parentGuiScreen;
    protected String screenTitle = "Video Settings";
    private final GameSettings guiGameSettings;
    private GuiListExtended optionsRowList;
    /** An array of all of GameSettings.Options's video options. */
    private static final GameSettings.Options[] VIDEO_OPTIONS = new GameSettings.Options[] {GameSettings.Options.GRAPHICS, GameSettings.Options.RENDER_DISTANCE, GameSettings.Options.AMBIENT_OCCLUSION, GameSettings.Options.FRAMERATE_LIMIT, GameSettings.Options.ANAGLYPH, GameSettings.Options.VIEW_BOBBING, GameSettings.Options.GUI_SCALE, GameSettings.Options.ATTACK_INDICATOR, GameSettings.Options.GAMMA, GameSettings.Options.RENDER_CLOUDS, GameSettings.Options.PARTICLES, GameSettings.Options.USE_FULLSCREEN, GameSettings.Options.ENABLE_VSYNC, GameSettings.Options.MIPMAP_LEVELS, GameSettings.Options.USE_VBO, GameSettings.Options.ENTITY_SHADOWS};

    public GuiVideoSettings(GuiScreen parentScreenIn, GameSettings gameSettingsIn)
    {
        this.parentGuiScreen = parentScreenIn;
        this.guiGameSettings = gameSettingsIn;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.screenTitle = I18n.format("options.videoTitle");
        this.buttons.clear();
        this.buttons.add(new GuiButton(200, this.width / 2 - 100, this.height - 27, I18n.format("gui.done")));

        if (OpenGlHelper.vboSupported)
        {
            this.optionsRowList = new GuiOptionsRowList(this.mc, this.width, this.height, 32, this.height - 32, 25, VIDEO_OPTIONS);
        }
        else
        {
            GameSettings.Options[] agamesettings$options = new GameSettings.Options[VIDEO_OPTIONS.length - 1];
            int i = 0;

            for (GameSettings.Options gamesettings$options : VIDEO_OPTIONS)
            {
                if (gamesettings$options == GameSettings.Options.USE_VBO)
                {
                    break;
                }

                agamesettings$options[i] = gamesettings$options;
                ++i;
            }

            this.optionsRowList = new GuiOptionsRowList(this.mc, this.width, this.height, 32, this.height - 32, 25, agamesettings$options);
        }
    }

    public void func_146274_d() throws IOException
    {
        super.func_146274_d();
        this.optionsRowList.func_178039_p();
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
            if (p_146284_1_.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }
        }
    }

    protected void func_73864_a(int p_73864_1_, int p_73864_2_, int p_73864_3_) throws IOException
    {
        int i = this.guiGameSettings.guiScale;
        super.func_73864_a(p_73864_1_, p_73864_2_, p_73864_3_);
        this.optionsRowList.func_148179_a(p_73864_1_, p_73864_2_, p_73864_3_);

        if (this.guiGameSettings.guiScale != i)
        {
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int j = scaledresolution.func_78326_a();
            int k = scaledresolution.func_78328_b();
            this.setWorldAndResolution(this.mc, j, k);
        }
    }

    protected void func_146286_b(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        int i = this.guiGameSettings.guiScale;
        super.func_146286_b(p_146286_1_, p_146286_2_, p_146286_3_);
        this.optionsRowList.func_148181_b(p_146286_1_, p_146286_2_, p_146286_3_);

        if (this.guiGameSettings.guiScale != i)
        {
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int j = scaledresolution.func_78326_a();
            int k = scaledresolution.func_78328_b();
            this.setWorldAndResolution(this.mc, j, k);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.optionsRowList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 5, 16777215);
        super.render(mouseX, mouseY, partialTicks);
    }

    // FORGE: fix for MC-64581 very laggy mipmap slider
    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        this.mc.gameSettings.onGuiClosed();
    }
}