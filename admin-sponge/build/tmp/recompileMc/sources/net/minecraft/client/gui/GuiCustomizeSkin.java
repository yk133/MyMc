package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCustomizeSkin extends GuiScreen
{
    /** The parent GUI for this GUI */
    private final GuiScreen parentScreen;
    /** The title of the GUI. */
    private String title;

    public GuiCustomizeSkin(GuiScreen parentScreenIn)
    {
        this.parentScreen = parentScreenIn;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        int i = 0;
        this.title = I18n.format("options.skinCustomisation.title");

        for (EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values())
        {
            this.buttons.add(new GuiCustomizeSkin.ButtonPart(enumplayermodelparts.getPartId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, enumplayermodelparts));
            ++i;
        }

        this.buttons.add(new GuiOptionButton(199, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), GameSettings.Options.MAIN_HAND, this.mc.gameSettings.getKeyBinding(GameSettings.Options.MAIN_HAND)));
        ++i;

        if (i % 2 == 1)
        {
            ++i;
        }

        this.buttons.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), I18n.format("gui.done")));
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
                this.mc.displayGuiScreen(this.parentScreen);
            }
            else if (p_146284_1_.id == 199)
            {
                this.mc.gameSettings.setOptionValue(GameSettings.Options.MAIN_HAND, 1);
                p_146284_1_.displayString = this.mc.gameSettings.getKeyBinding(GameSettings.Options.MAIN_HAND);
                this.mc.gameSettings.sendSettingsToServer();
            }
            else if (p_146284_1_ instanceof GuiCustomizeSkin.ButtonPart)
            {
                EnumPlayerModelParts enumplayermodelparts = ((GuiCustomizeSkin.ButtonPart)p_146284_1_).playerModelParts;
                this.mc.gameSettings.switchModelPartEnabled(enumplayermodelparts);
                p_146284_1_.displayString = this.getMessage(enumplayermodelparts);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 20, 16777215);
        super.render(mouseX, mouseY, partialTicks);
    }

    private String getMessage(EnumPlayerModelParts playerModelParts)
    {
        String s;

        if (this.mc.gameSettings.getModelParts().contains(playerModelParts))
        {
            s = I18n.format("options.on");
        }
        else
        {
            s = I18n.format("options.off");
        }

        return playerModelParts.getName().getFormattedText() + ": " + s;
    }

    @SideOnly(Side.CLIENT)
    class ButtonPart extends GuiButton
    {
        private final EnumPlayerModelParts playerModelParts;

        private ButtonPart(int p_i45514_2_, int p_i45514_3_, int p_i45514_4_, int p_i45514_5_, int p_i45514_6_, EnumPlayerModelParts playerModelParts)
        {
            super(p_i45514_2_, p_i45514_3_, p_i45514_4_, p_i45514_5_, p_i45514_6_, GuiCustomizeSkin.this.getMessage(playerModelParts));
            this.playerModelParts = playerModelParts;
        }
    }
}