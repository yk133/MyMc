package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLanguage extends GuiScreen
{
    /** The parent Gui screen */
    protected GuiScreen parentScreen;
    /** The List GuiSlot object reference. */
    private GuiLanguage.List list;
    /** Reference to the GameSettings object. */
    private final GameSettings game_settings_3;
    /** Reference to the LanguageManager object. */
    private final LanguageManager languageManager;
    private GuiOptionButton field_146455_i;
    /** The button to confirm the current settings. */
    private GuiOptionButton confirmSettingsBtn;

    public GuiLanguage(GuiScreen screen, GameSettings gameSettingsObj, LanguageManager manager)
    {
        this.parentScreen = screen;
        this.game_settings_3 = gameSettingsObj;
        this.languageManager = manager;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.field_146455_i = (GuiOptionButton)this.addButton(new GuiOptionButton(100, this.width / 2 - 155, this.height - 38, GameSettings.Options.FORCE_UNICODE_FONT, this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT)));
        this.confirmSettingsBtn = (GuiOptionButton)this.addButton(new GuiOptionButton(6, this.width / 2 - 155 + 160, this.height - 38, I18n.format("gui.done")));
        this.list = new GuiLanguage.List(this.mc);
        this.list.func_148134_d(7, 8);
    }

    public void func_146274_d() throws IOException
    {
        super.func_146274_d();
        this.list.func_178039_p();
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        if (p_146284_1_.enabled)
        {
            switch (p_146284_1_.id)
            {
                case 5:
                    break;
                case 6:
                    this.mc.displayGuiScreen(this.parentScreen);
                    break;
                case 100:

                    if (p_146284_1_ instanceof GuiOptionButton)
                    {
                        this.game_settings_3.setOptionValue(((GuiOptionButton)p_146284_1_).getOption(), 1);
                        p_146284_1_.displayString = this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
                        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
                        int i = scaledresolution.func_78326_a();
                        int j = scaledresolution.func_78328_b();
                        this.setWorldAndResolution(this.mc, i, j);
                    }

                    break;
                default:
                    this.list.func_148147_a(p_146284_1_);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, I18n.format("options.language"), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.fontRenderer, "(" + I18n.format("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
        super.render(mouseX, mouseY, partialTicks);
    }

    @SideOnly(Side.CLIENT)
    class List extends GuiSlot
    {
        /** A list containing the many different locale language codes. */
        private final java.util.List<String> langCodeList = Lists.<String>newArrayList();
        /** The map containing the Locale-Language pairs. */
        private final Map<String, Language> languageMap = Maps.<String, Language>newHashMap();

        public List(Minecraft mcIn)
        {
            super(mcIn, GuiLanguage.this.width, GuiLanguage.this.height, 32, GuiLanguage.this.height - 65 + 4, 18);

            for (Language language : GuiLanguage.this.languageManager.getLanguages())
            {
                this.languageMap.put(language.getLanguageCode(), language);
                this.langCodeList.add(language.getLanguageCode());
            }
        }

        protected int getSize()
        {
            return this.langCodeList.size();
        }

        protected void func_148144_a(int p_148144_1_, boolean p_148144_2_, int p_148144_3_, int p_148144_4_)
        {
            Language language = this.languageMap.get(this.langCodeList.get(p_148144_1_));
            GuiLanguage.this.languageManager.setCurrentLanguage(language);
            GuiLanguage.this.game_settings_3.language = language.getLanguageCode();
            net.minecraftforge.fml.client.FMLClientHandler.instance().refreshResources(net.minecraftforge.client.resource.VanillaResourceType.LANGUAGES);
            GuiLanguage.this.fontRenderer.func_78264_a(GuiLanguage.this.languageManager.func_135042_a() || GuiLanguage.this.game_settings_3.field_151455_aw);
            GuiLanguage.this.fontRenderer.setBidiFlag(GuiLanguage.this.languageManager.isCurrentLanguageBidirectional());
            GuiLanguage.this.confirmSettingsBtn.displayString = I18n.format("gui.done");
            GuiLanguage.this.field_146455_i.displayString = GuiLanguage.this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
            GuiLanguage.this.game_settings_3.saveOptions();
        }

        /**
         * Returns true if the element passed in is currently selected
         */
        protected boolean isSelected(int slotIndex)
        {
            return ((String)this.langCodeList.get(slotIndex)).equals(GuiLanguage.this.languageManager.getCurrentLanguage().getLanguageCode());
        }

        /**
         * Return the height of the content being scrolled
         */
        protected int getContentHeight()
        {
            return this.getSize() * 18;
        }

        protected void drawBackground()
        {
            GuiLanguage.this.drawDefaultBackground();
        }

        protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks)
        {
            GuiLanguage.this.fontRenderer.setBidiFlag(true);
            GuiLanguage.this.drawCenteredString(GuiLanguage.this.fontRenderer, ((Language)this.languageMap.get(this.langCodeList.get(slotIndex))).toString(), this.width / 2, yPos + 1, 16777215);
            GuiLanguage.this.fontRenderer.setBidiFlag(GuiLanguage.this.languageManager.getCurrentLanguage().isBidirectional());
        }
    }
}