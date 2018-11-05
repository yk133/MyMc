package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSnooper extends GuiScreen
{
    private final GuiScreen lastScreen;
    /** Reference to the GameSettings object. */
    private final GameSettings game_settings_2;
    private final java.util.List<String> keys = Lists.<String>newArrayList();
    private final java.util.List<String> values = Lists.<String>newArrayList();
    private String title;
    private String[] desc;
    private GuiSnooper.List list;
    private GuiButton toggleButton;

    public GuiSnooper(GuiScreen p_i1061_1_, GameSettings p_i1061_2_)
    {
        this.lastScreen = p_i1061_1_;
        this.game_settings_2 = p_i1061_2_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.title = I18n.format("options.snooper.title");
        String s = I18n.format("options.snooper.desc");
        java.util.List<String> list = Lists.<String>newArrayList();

        for (String s1 : this.fontRenderer.listFormattedStringToWidth(s, this.width - 30))
        {
            list.add(s1);
        }

        this.desc = (String[])list.toArray(new String[list.size()]);
        this.keys.clear();
        this.values.clear();
        this.toggleButton = this.addButton(new GuiButton(1, this.width / 2 - 152, this.height - 30, 150, 20, this.game_settings_2.getKeyBinding(GameSettings.Options.SNOOPER_ENABLED)));
        this.buttons.add(new GuiButton(2, this.width / 2 + 2, this.height - 30, 150, 20, I18n.format("gui.done")));
        boolean flag = this.mc.getIntegratedServer() != null && this.mc.getIntegratedServer().getSnooper() != null;

        for (Entry<String, String> entry : (new TreeMap<String, String>(this.mc.getSnooper().getCurrentStats())).entrySet())
        {
            this.keys.add((flag ? "C " : "") + (String)entry.getKey());
            this.values.add(this.fontRenderer.trimStringToWidth(entry.getValue(), this.width - 220));
        }

        if (flag)
        {
            for (Entry<String, String> entry1 : (new TreeMap<String, String>(this.mc.getIntegratedServer().getSnooper().getCurrentStats())).entrySet())
            {
                this.keys.add("S " + (String)entry1.getKey());
                this.values.add(this.fontRenderer.trimStringToWidth(entry1.getValue(), this.width - 220));
            }
        }

        this.list = new GuiSnooper.List();
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
            if (p_146284_1_.id == 2)
            {
                this.game_settings_2.saveOptions();
                this.game_settings_2.saveOptions();
                this.mc.displayGuiScreen(this.lastScreen);
            }

            if (p_146284_1_.id == 1)
            {
                this.game_settings_2.setOptionValue(GameSettings.Options.SNOOPER_ENABLED, 1);
                this.toggleButton.displayString = this.game_settings_2.getKeyBinding(GameSettings.Options.SNOOPER_ENABLED);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 8, 16777215);
        int i = 22;

        for (String s : this.desc)
        {
            this.drawCenteredString(this.fontRenderer, s, this.width / 2, i, 8421504);
            i += this.fontRenderer.FONT_HEIGHT;
        }

        super.render(mouseX, mouseY, partialTicks);
    }

    @SideOnly(Side.CLIENT)
    class List extends GuiSlot
    {
        public List()
        {
            super(GuiSnooper.this.mc, GuiSnooper.this.width, GuiSnooper.this.height, 80, GuiSnooper.this.height - 40, GuiSnooper.this.fontRenderer.FONT_HEIGHT + 1);
        }

        protected int getSize()
        {
            return GuiSnooper.this.keys.size();
        }

        protected void func_148144_a(int p_148144_1_, boolean p_148144_2_, int p_148144_3_, int p_148144_4_)
        {
        }

        /**
         * Returns true if the element passed in is currently selected
         */
        protected boolean isSelected(int slotIndex)
        {
            return false;
        }

        protected void drawBackground()
        {
        }

        protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks)
        {
            GuiSnooper.this.fontRenderer.func_78276_b(GuiSnooper.this.keys.get(slotIndex), 10, yPos, 16777215);
            GuiSnooper.this.fontRenderer.func_78276_b(GuiSnooper.this.values.get(slotIndex), 230, yPos, 16777215);
        }

        protected int getScrollBarX()
        {
            return this.width - 10;
        }
    }
}