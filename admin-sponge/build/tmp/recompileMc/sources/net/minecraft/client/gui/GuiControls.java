package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiControls extends GuiScreen
{
    private static final GameSettings.Options[] OPTIONS_ARR = new GameSettings.Options[] {GameSettings.Options.INVERT_MOUSE, GameSettings.Options.SENSITIVITY, GameSettings.Options.TOUCHSCREEN, GameSettings.Options.AUTO_JUMP};
    /** A reference to the screen object that created this. Used for navigating between screens. */
    private final GuiScreen parentScreen;
    protected String screenTitle = "Controls";
    /** Reference to the GameSettings object. */
    private final GameSettings options;
    /** The ID of the button that has been pressed. */
    public KeyBinding buttonId;
    public long time;
    private GuiKeyBindingList keyBindingList;
    private GuiButton buttonReset;

    public GuiControls(GuiScreen screen, GameSettings settings)
    {
        this.parentScreen = screen;
        this.options = settings;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.keyBindingList = new GuiKeyBindingList(this, this.mc);
        this.buttons.add(new GuiButton(200, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("gui.done")));
        this.buttonReset = this.addButton(new GuiButton(201, this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("controls.resetAll")));
        this.screenTitle = I18n.format("controls.title");
        int i = 0;

        for (GameSettings.Options gamesettings$options : OPTIONS_ARR)
        {
            if (gamesettings$options.isFloat())
            {
                this.buttons.add(new GuiOptionSlider(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), gamesettings$options));
            }
            else
            {
                this.buttons.add(new GuiOptionButton(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), gamesettings$options, this.options.getKeyBinding(gamesettings$options)));
            }

            ++i;
        }
    }

    public void func_146274_d() throws IOException
    {
        super.func_146274_d();
        this.keyBindingList.func_178039_p();
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        if (p_146284_1_.id == 200)
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }
        else if (p_146284_1_.id == 201)
        {
            for (KeyBinding keybinding : this.mc.gameSettings.keyBindings)
            {
                keybinding.setToDefault();
            }

            KeyBinding.resetKeyBindingArrayAndHash();
        }
        else if (p_146284_1_.id < 100 && p_146284_1_ instanceof GuiOptionButton)
        {
            this.options.setOptionValue(((GuiOptionButton)p_146284_1_).getOption(), 1);
            p_146284_1_.displayString = this.options.getKeyBinding(GameSettings.Options.byOrdinal(p_146284_1_.id));
        }
    }

    protected void func_73864_a(int p_73864_1_, int p_73864_2_, int p_73864_3_) throws IOException
    {
        if (this.buttonId != null)
        {
            this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), -100 + p_73864_3_);
            this.options.func_151440_a(this.buttonId, -100 + p_73864_3_);
            this.buttonId = null;
            KeyBinding.resetKeyBindingArrayAndHash();
        }
        else if (p_73864_3_ != 0 || !this.keyBindingList.func_148179_a(p_73864_1_, p_73864_2_, p_73864_3_))
        {
            super.func_73864_a(p_73864_1_, p_73864_2_, p_73864_3_);
        }
    }

    protected void func_146286_b(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        if (p_146286_3_ != 0 || !this.keyBindingList.func_148181_b(p_146286_1_, p_146286_2_, p_146286_3_))
        {
            super.func_146286_b(p_146286_1_, p_146286_2_, p_146286_3_);
        }
    }

    protected void func_73869_a(char p_73869_1_, int p_73869_2_) throws IOException
    {
        if (this.buttonId != null)
        {
            if (p_73869_2_ == 1)
            {
                this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.NONE, 0);
                this.options.func_151440_a(this.buttonId, 0);
            }
            else if (p_73869_2_ != 0)
            {
                this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), p_73869_2_);
                this.options.func_151440_a(this.buttonId, p_73869_2_);
            }
            else if (p_73869_1_ > 0)
            {
                this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), p_73869_1_ + 256);
                this.options.func_151440_a(this.buttonId, p_73869_1_ + 256);
            }

            if (!net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(p_73869_2_))
            this.buttonId = null;
            this.time = Minecraft.func_71386_F();
            KeyBinding.resetKeyBindingArrayAndHash();
        }
        else
        {
            super.func_73869_a(p_73869_1_, p_73869_2_);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 8, 16777215);
        boolean flag = false;

        for (KeyBinding keybinding : this.options.keyBindings)
        {
            if (!keybinding.isSetToDefaultValue())
            {
                flag = true;
                break;
            }
        }

        this.buttonReset.enabled = flag;
        super.render(mouseX, mouseY, partialTicks);
    }
}