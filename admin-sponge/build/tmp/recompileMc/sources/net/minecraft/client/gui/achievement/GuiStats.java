package net.minecraft.client.gui.achievement;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class GuiStats extends GuiScreen implements IProgressMeter
{
    protected GuiScreen parentScreen;
    protected String screenTitle = "Select world";
    private GuiStats.StatsGeneral generalStats;
    private GuiStats.StatsItem itemStats;
    private GuiStats.StatsBlock field_146548_r;
    private GuiStats.StatsMobsList mobStats;
    private final StatisticsManager stats;
    private GuiSlot displaySlot;
    /** When true, the game will be paused when the gui is shown */
    private boolean doesGuiPauseGame = true;

    public GuiStats(GuiScreen parent, StatisticsManager manager)
    {
        this.parentScreen = parent;
        this.stats = manager;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.screenTitle = I18n.format("gui.stats");
        this.doesGuiPauseGame = true;
        this.mc.getConnection().sendPacket(new CPacketClientStatus(CPacketClientStatus.State.REQUEST_STATS));
    }

    public void func_146274_d() throws IOException
    {
        super.func_146274_d();

        if (this.displaySlot != null)
        {
            this.displaySlot.func_178039_p();
        }
    }

    public void initLists()
    {
        this.generalStats = new GuiStats.StatsGeneral(this.mc);
        this.generalStats.func_148134_d(1, 1);
        this.itemStats = new GuiStats.StatsItem(this.mc);
        this.itemStats.func_148134_d(1, 1);
        this.field_146548_r = new GuiStats.StatsBlock(this.mc);
        this.field_146548_r.func_148134_d(1, 1);
        this.mobStats = new GuiStats.StatsMobsList(this.mc);
        this.mobStats.func_148134_d(1, 1);
    }

    public void initButtons()
    {
        this.buttons.add(new GuiButton(0, this.width / 2 + 4, this.height - 28, 150, 20, I18n.format("gui.done")));
        this.buttons.add(new GuiButton(1, this.width / 2 - 160, this.height - 52, 80, 20, I18n.format("stat.generalButton")));
        GuiButton guibutton = this.addButton(new GuiButton(2, this.width / 2 - 80, this.height - 52, 80, 20, I18n.format("stat.blocksButton")));
        GuiButton guibutton1 = this.addButton(new GuiButton(3, this.width / 2, this.height - 52, 80, 20, I18n.format("stat.itemsButton")));
        GuiButton guibutton2 = this.addButton(new GuiButton(4, this.width / 2 + 80, this.height - 52, 80, 20, I18n.format("stat.mobsButton")));

        if (this.field_146548_r.getSize() == 0)
        {
            guibutton.enabled = false;
        }

        if (this.itemStats.getSize() == 0)
        {
            guibutton1.enabled = false;
        }

        if (this.mobStats.getSize() == 0)
        {
            guibutton2.enabled = false;
        }
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        if (p_146284_1_.enabled)
        {
            if (p_146284_1_.id == 0)
            {
                this.mc.displayGuiScreen(this.parentScreen);
            }
            else if (p_146284_1_.id == 1)
            {
                this.displaySlot = this.generalStats;
            }
            else if (p_146284_1_.id == 3)
            {
                this.displaySlot = this.itemStats;
            }
            else if (p_146284_1_.id == 2)
            {
                this.displaySlot = this.field_146548_r;
            }
            else if (p_146284_1_.id == 4)
            {
                this.displaySlot = this.mobStats;
            }
            else
            {
                this.displaySlot.func_148147_a(p_146284_1_);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (this.doesGuiPauseGame)
        {
            this.drawDefaultBackground();
            this.drawCenteredString(this.fontRenderer, I18n.format("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
            this.drawCenteredString(this.fontRenderer, LOADING_STRINGS[(int)(Minecraft.func_71386_F() / 150L % (long)LOADING_STRINGS.length)], this.width / 2, this.height / 2 + this.fontRenderer.FONT_HEIGHT * 2, 16777215);
        }
        else
        {
            this.displaySlot.drawScreen(mouseX, mouseY, partialTicks);
            this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 20, 16777215);
            super.render(mouseX, mouseY, partialTicks);
        }
    }

    public void onStatsUpdated()
    {
        if (this.doesGuiPauseGame)
        {
            this.initLists();
            this.initButtons();
            this.displaySlot = this.generalStats;
            this.doesGuiPauseGame = false;
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return !this.doesGuiPauseGame;
    }

    private void drawStatsScreen(int x, int y, Item itemIn)
    {
        this.drawButtonBackground(x + 1, y + 1);
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        this.itemRender.renderItemIntoGUI(itemIn.getDefaultInstance(), x + 2, y + 2);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
    }

    /**
     * Draws a gray box that serves as a button background.
     */
    private void drawButtonBackground(int x, int y)
    {
        this.drawSprite(x, y, 0, 0);
    }

    /**
     * Draws a sprite from assets/textures/gui/container/stats_icons.png
     */
    private void drawSprite(int x, int y, int u, int v)
    {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(STAT_ICONS);
        float f = 0.0078125F;
        float f1 = 0.0078125F;
        int i = 18;
        int j = 18;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)(x + 0), (double)(y + 18), (double)this.zLevel).tex((double)((float)(u + 0) * 0.0078125F), (double)((float)(v + 18) * 0.0078125F)).endVertex();
        bufferbuilder.pos((double)(x + 18), (double)(y + 18), (double)this.zLevel).tex((double)((float)(u + 18) * 0.0078125F), (double)((float)(v + 18) * 0.0078125F)).endVertex();
        bufferbuilder.pos((double)(x + 18), (double)(y + 0), (double)this.zLevel).tex((double)((float)(u + 18) * 0.0078125F), (double)((float)(v + 0) * 0.0078125F)).endVertex();
        bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(u + 0) * 0.0078125F), (double)((float)(v + 0) * 0.0078125F)).endVertex();
        tessellator.draw();
    }

    @SideOnly(Side.CLIENT)
    abstract class Stats extends GuiSlot
    {
        protected int field_148218_l = -1;
        protected List<StatCrafting> field_148219_m;
        protected Comparator<StatCrafting> field_148216_n;
        protected int field_148217_o = -1;
        protected int field_148215_p;

        protected Stats(Minecraft p_i47550_2_)
        {
            super(p_i47550_2_, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, 20);
            this.setShowSelectionBox(false);
            this.setHasListHeader(true, 20);
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

        /**
         * Gets the width of the list
         */
        public int getListWidth()
        {
            return 375;
        }

        protected int getScrollBarX()
        {
            return this.width / 2 + 140;
        }

        protected void drawBackground()
        {
            GuiStats.this.drawDefaultBackground();
        }

        /**
         * Handles drawing a list's header row.
         */
        protected void drawListHeader(int insideLeft, int insideTop, Tessellator tessellatorIn)
        {
            if (!Mouse.isButtonDown(0))
            {
                this.field_148218_l = -1;
            }

            if (this.field_148218_l == 0)
            {
                GuiStats.this.drawSprite(insideLeft + 115 - 18, insideTop + 1, 0, 0);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 115 - 18, insideTop + 1, 0, 18);
            }

            if (this.field_148218_l == 1)
            {
                GuiStats.this.drawSprite(insideLeft + 165 - 18, insideTop + 1, 0, 0);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 165 - 18, insideTop + 1, 0, 18);
            }

            if (this.field_148218_l == 2)
            {
                GuiStats.this.drawSprite(insideLeft + 215 - 18, insideTop + 1, 0, 0);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 215 - 18, insideTop + 1, 0, 18);
            }

            if (this.field_148218_l == 3)
            {
                GuiStats.this.drawSprite(insideLeft + 265 - 18, insideTop + 1, 0, 0);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 265 - 18, insideTop + 1, 0, 18);
            }

            if (this.field_148218_l == 4)
            {
                GuiStats.this.drawSprite(insideLeft + 315 - 18, insideTop + 1, 0, 0);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 315 - 18, insideTop + 1, 0, 18);
            }

            if (this.field_148217_o != -1)
            {
                int i = 79;
                int j = 18;

                if (this.field_148217_o == 1)
                {
                    i = 129;
                }
                else if (this.field_148217_o == 2)
                {
                    i = 179;
                }
                else if (this.field_148217_o == 3)
                {
                    i = 229;
                }
                else if (this.field_148217_o == 4)
                {
                    i = 279;
                }

                if (this.field_148215_p == 1)
                {
                    j = 36;
                }

                GuiStats.this.drawSprite(insideLeft + i, insideTop + 1, j, 0);
            }
        }

        /**
         * Called when the mouse left-clicks the header or anywhere else that isn't on an entry.
         */
        protected void clickedHeader(int p_148132_1_, int p_148132_2_)
        {
            this.field_148218_l = -1;

            if (p_148132_1_ >= 79 && p_148132_1_ < 115)
            {
                this.field_148218_l = 0;
            }
            else if (p_148132_1_ >= 129 && p_148132_1_ < 165)
            {
                this.field_148218_l = 1;
            }
            else if (p_148132_1_ >= 179 && p_148132_1_ < 215)
            {
                this.field_148218_l = 2;
            }
            else if (p_148132_1_ >= 229 && p_148132_1_ < 265)
            {
                this.field_148218_l = 3;
            }
            else if (p_148132_1_ >= 279 && p_148132_1_ < 315)
            {
                this.field_148218_l = 4;
            }

            if (this.field_148218_l >= 0)
            {
                this.func_148212_h(this.field_148218_l);
                this.mc.getSoundHandler().play(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }

        protected final int getSize()
        {
            return this.field_148219_m.size();
        }

        protected final StatCrafting func_148211_c(int p_148211_1_)
        {
            return this.field_148219_m.get(p_148211_1_);
        }

        protected abstract String func_148210_b(int p_148210_1_);

        protected void func_148209_a(StatBase p_148209_1_, int p_148209_2_, int p_148209_3_, boolean p_148209_4_)
        {
            if (p_148209_1_ != null)
            {
                String s = p_148209_1_.format(GuiStats.this.stats.getValue(p_148209_1_));
                GuiStats.this.drawString(GuiStats.this.fontRenderer, s, p_148209_2_ - GuiStats.this.fontRenderer.getStringWidth(s), p_148209_3_ + 5, p_148209_4_ ? 16777215 : 9474192);
            }
            else
            {
                String s1 = "-";
                GuiStats.this.drawString(GuiStats.this.fontRenderer, "-", p_148209_2_ - GuiStats.this.fontRenderer.getStringWidth("-"), p_148209_3_ + 5, p_148209_4_ ? 16777215 : 9474192);
            }
        }

        protected void renderDecorations(int mouseXIn, int mouseYIn)
        {
            if (mouseYIn >= this.top && mouseYIn <= this.bottom)
            {
                int i = this.func_148124_c(mouseXIn, mouseYIn);
                int j = (this.width - this.getListWidth()) / 2;

                if (i >= 0)
                {
                    if (mouseXIn < j + 40 || mouseXIn > j + 40 + 20)
                    {
                        return;
                    }

                    StatCrafting statcrafting = this.func_148211_c(i);
                    this.func_148213_a(statcrafting, mouseXIn, mouseYIn);
                }
                else
                {
                    String s;

                    if (mouseXIn >= j + 115 - 18 && mouseXIn <= j + 115)
                    {
                        s = this.func_148210_b(0);
                    }
                    else if (mouseXIn >= j + 165 - 18 && mouseXIn <= j + 165)
                    {
                        s = this.func_148210_b(1);
                    }
                    else if (mouseXIn >= j + 215 - 18 && mouseXIn <= j + 215)
                    {
                        s = this.func_148210_b(2);
                    }
                    else if (mouseXIn >= j + 265 - 18 && mouseXIn <= j + 265)
                    {
                        s = this.func_148210_b(3);
                    }
                    else
                    {
                        if (mouseXIn < j + 315 - 18 || mouseXIn > j + 315)
                        {
                            return;
                        }

                        s = this.func_148210_b(4);
                    }

                    s = ("" + I18n.format(s)).trim();

                    if (!s.isEmpty())
                    {
                        int k = mouseXIn + 12;
                        int l = mouseYIn - 12;
                        int i1 = GuiStats.this.fontRenderer.getStringWidth(s);
                        GuiStats.this.drawGradientRect(k - 3, l - 3, k + i1 + 3, l + 8 + 3, -1073741824, -1073741824);
                        GuiStats.this.fontRenderer.drawStringWithShadow(s, (float)k, (float)l, -1);
                    }
                }
            }
        }

        protected void func_148213_a(StatCrafting p_148213_1_, int p_148213_2_, int p_148213_3_)
        {
            if (p_148213_1_ != null)
            {
                Item item = p_148213_1_.func_150959_a();
                ItemStack itemstack = new ItemStack(item);
                String s = itemstack.getTranslationKey();
                String s1 = ("" + I18n.format(s + ".name")).trim();

                if (!s1.isEmpty())
                {
                    int i = p_148213_2_ + 12;
                    int j = p_148213_3_ - 12;
                    int k = GuiStats.this.fontRenderer.getStringWidth(s1);
                    GuiStats.this.drawGradientRect(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
                    GuiStats.this.fontRenderer.drawStringWithShadow(s1, (float)i, (float)j, -1);
                }
            }
        }

        protected void func_148212_h(int p_148212_1_)
        {
            if (p_148212_1_ != this.field_148217_o)
            {
                this.field_148217_o = p_148212_1_;
                this.field_148215_p = -1;
            }
            else if (this.field_148215_p == -1)
            {
                this.field_148215_p = 1;
            }
            else
            {
                this.field_148217_o = -1;
                this.field_148215_p = 0;
            }

            Collections.sort(this.field_148219_m, this.field_148216_n);
        }
    }

    @SideOnly(Side.CLIENT)
    class StatsBlock extends GuiStats.Stats
    {
        public StatsBlock(Minecraft p_i47554_2_)
        {
            super(p_i47554_2_);
            this.field_148219_m = Lists.<StatCrafting>newArrayList();

            for (StatCrafting statcrafting : StatList.field_188096_e)
            {
                boolean flag = false;
                Item item = statcrafting.func_150959_a();

                if (GuiStats.this.stats.getValue(statcrafting) > 0)
                {
                    flag = true;
                }
                else if (StatList.func_188057_b(item) != null && GuiStats.this.stats.getValue(StatList.func_188057_b(item)) > 0)
                {
                    flag = true;
                }
                else if (StatList.func_188060_a(item) != null && GuiStats.this.stats.getValue(StatList.func_188060_a(item)) > 0)
                {
                    flag = true;
                }
                else if (StatList.func_188056_d(item) != null && GuiStats.this.stats.getValue(StatList.func_188056_d(item)) > 0)
                {
                    flag = true;
                }
                else if (StatList.func_188058_e(item) != null && GuiStats.this.stats.getValue(StatList.func_188058_e(item)) > 0)
                {
                    flag = true;
                }

                if (flag)
                {
                    this.field_148219_m.add(statcrafting);
                }
            }

            this.field_148216_n = new Comparator<StatCrafting>()
            {
                public int compare(StatCrafting p_compare_1_, StatCrafting p_compare_2_)
                {
                    Item item1 = p_compare_1_.func_150959_a();
                    Item item2 = p_compare_2_.func_150959_a();
                    StatBase statbase = null;
                    StatBase statbase1 = null;

                    if (StatsBlock.this.field_148217_o == 2)
                    {
                        statbase = StatList.func_188055_a(Block.getBlockFromItem(item1));
                        statbase1 = StatList.func_188055_a(Block.getBlockFromItem(item2));
                    }
                    else if (StatsBlock.this.field_148217_o == 0)
                    {
                        statbase = StatList.func_188060_a(item1);
                        statbase1 = StatList.func_188060_a(item2);
                    }
                    else if (StatsBlock.this.field_148217_o == 1)
                    {
                        statbase = StatList.func_188057_b(item1);
                        statbase1 = StatList.func_188057_b(item2);
                    }
                    else if (StatsBlock.this.field_148217_o == 3)
                    {
                        statbase = StatList.func_188056_d(item1);
                        statbase1 = StatList.func_188056_d(item2);
                    }
                    else if (StatsBlock.this.field_148217_o == 4)
                    {
                        statbase = StatList.func_188058_e(item1);
                        statbase1 = StatList.func_188058_e(item2);
                    }

                    if (statbase != null || statbase1 != null)
                    {
                        if (statbase == null)
                        {
                            return 1;
                        }

                        if (statbase1 == null)
                        {
                            return -1;
                        }

                        int i = GuiStats.this.stats.getValue(statbase);
                        int j = GuiStats.this.stats.getValue(statbase1);

                        if (i != j)
                        {
                            return (i - j) * StatsBlock.this.field_148215_p;
                        }
                    }

                    return Item.getIdFromItem(item1) - Item.getIdFromItem(item2);
                }
            };
        }

        /**
         * Handles drawing a list's header row.
         */
        protected void drawListHeader(int insideLeft, int insideTop, Tessellator tessellatorIn)
        {
            super.drawListHeader(insideLeft, insideTop, tessellatorIn);

            if (this.field_148218_l == 0)
            {
                GuiStats.this.drawSprite(insideLeft + 115 - 18 + 1, insideTop + 1 + 1, 18, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 115 - 18, insideTop + 1, 18, 18);
            }

            if (this.field_148218_l == 1)
            {
                GuiStats.this.drawSprite(insideLeft + 165 - 18 + 1, insideTop + 1 + 1, 36, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 165 - 18, insideTop + 1, 36, 18);
            }

            if (this.field_148218_l == 2)
            {
                GuiStats.this.drawSprite(insideLeft + 215 - 18 + 1, insideTop + 1 + 1, 54, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 215 - 18, insideTop + 1, 54, 18);
            }

            if (this.field_148218_l == 3)
            {
                GuiStats.this.drawSprite(insideLeft + 265 - 18 + 1, insideTop + 1 + 1, 90, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 265 - 18, insideTop + 1, 90, 18);
            }

            if (this.field_148218_l == 4)
            {
                GuiStats.this.drawSprite(insideLeft + 315 - 18 + 1, insideTop + 1 + 1, 108, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 315 - 18, insideTop + 1, 108, 18);
            }
        }

        protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks)
        {
            StatCrafting statcrafting = this.func_148211_c(slotIndex);
            Item item = statcrafting.func_150959_a();
            GuiStats.this.drawStatsScreen(xPos + 40, yPos, item);
            this.func_148209_a(StatList.func_188060_a(item), xPos + 115, yPos, slotIndex % 2 == 0);
            this.func_148209_a(StatList.func_188057_b(item), xPos + 165, yPos, slotIndex % 2 == 0);
            this.func_148209_a(statcrafting, xPos + 215, yPos, slotIndex % 2 == 0);
            this.func_148209_a(StatList.func_188056_d(item), xPos + 265, yPos, slotIndex % 2 == 0);
            this.func_148209_a(StatList.func_188058_e(item), xPos + 315, yPos, slotIndex % 2 == 0);
        }

        protected String func_148210_b(int p_148210_1_)
        {
            if (p_148210_1_ == 0)
            {
                return "stat.crafted";
            }
            else if (p_148210_1_ == 1)
            {
                return "stat.used";
            }
            else if (p_148210_1_ == 3)
            {
                return "stat.pickup";
            }
            else
            {
                return p_148210_1_ == 4 ? "stat.dropped" : "stat.mined";
            }
        }
    }

    @SideOnly(Side.CLIENT)
    class StatsGeneral extends GuiSlot
    {
        public StatsGeneral(Minecraft mcIn)
        {
            super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, 10);
            this.setShowSelectionBox(false);
        }

        protected int getSize()
        {
            return StatList.field_188094_c.size();
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

        /**
         * Return the height of the content being scrolled
         */
        protected int getContentHeight()
        {
            return this.getSize() * 10;
        }

        protected void drawBackground()
        {
            GuiStats.this.drawDefaultBackground();
        }

        protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks)
        {
            StatBase statbase = StatList.field_188094_c.get(slotIndex);
            GuiStats.this.drawString(GuiStats.this.fontRenderer, statbase.func_150951_e().func_150260_c(), xPos + 2, yPos + 1, slotIndex % 2 == 0 ? 16777215 : 9474192);
            String s = statbase.format(GuiStats.this.stats.getValue(statbase));
            GuiStats.this.drawString(GuiStats.this.fontRenderer, s, xPos + 2 + 213 - GuiStats.this.fontRenderer.getStringWidth(s), yPos + 1, slotIndex % 2 == 0 ? 16777215 : 9474192);
        }
    }

    @SideOnly(Side.CLIENT)
    class StatsItem extends GuiStats.Stats
    {
        public StatsItem(Minecraft mcIn)
        {
            super(mcIn);
            this.field_148219_m = Lists.<StatCrafting>newArrayList();

            for (StatCrafting statcrafting : StatList.field_188095_d)
            {
                boolean flag = false;
                Item item = statcrafting.func_150959_a();

                if (GuiStats.this.stats.getValue(statcrafting) > 0)
                {
                    flag = true;
                }
                else if (StatList.func_188059_c(item) != null && GuiStats.this.stats.getValue(StatList.func_188059_c(item)) > 0)
                {
                    flag = true;
                }
                else if (StatList.func_188060_a(item) != null && GuiStats.this.stats.getValue(StatList.func_188060_a(item)) > 0)
                {
                    flag = true;
                }
                else if (StatList.func_188056_d(item) != null && GuiStats.this.stats.getValue(StatList.func_188056_d(item)) > 0)
                {
                    flag = true;
                }
                else if (StatList.func_188058_e(item) != null && GuiStats.this.stats.getValue(StatList.func_188058_e(item)) > 0)
                {
                    flag = true;
                }

                if (flag)
                {
                    this.field_148219_m.add(statcrafting);
                }
            }

            this.field_148216_n = new Comparator<StatCrafting>()
            {
                public int compare(StatCrafting p_compare_1_, StatCrafting p_compare_2_)
                {
                    Item item1 = p_compare_1_.func_150959_a();
                    Item item2 = p_compare_2_.func_150959_a();
                    int i = Item.getIdFromItem(item1);
                    int j = Item.getIdFromItem(item2);
                    StatBase statbase = null;
                    StatBase statbase1 = null;

                    if (StatsItem.this.field_148217_o == 0)
                    {
                        statbase = StatList.func_188059_c(item1);
                        statbase1 = StatList.func_188059_c(item2);
                    }
                    else if (StatsItem.this.field_148217_o == 1)
                    {
                        statbase = StatList.func_188060_a(item1);
                        statbase1 = StatList.func_188060_a(item2);
                    }
                    else if (StatsItem.this.field_148217_o == 2)
                    {
                        statbase = StatList.func_188057_b(item1);
                        statbase1 = StatList.func_188057_b(item2);
                    }
                    else if (StatsItem.this.field_148217_o == 3)
                    {
                        statbase = StatList.func_188056_d(item1);
                        statbase1 = StatList.func_188056_d(item2);
                    }
                    else if (StatsItem.this.field_148217_o == 4)
                    {
                        statbase = StatList.func_188058_e(item1);
                        statbase1 = StatList.func_188058_e(item2);
                    }

                    if (statbase != null || statbase1 != null)
                    {
                        if (statbase == null)
                        {
                            return 1;
                        }

                        if (statbase1 == null)
                        {
                            return -1;
                        }

                        int k = GuiStats.this.stats.getValue(statbase);
                        int l = GuiStats.this.stats.getValue(statbase1);

                        if (k != l)
                        {
                            return (k - l) * StatsItem.this.field_148215_p;
                        }
                    }

                    return i - j;
                }
            };
        }

        /**
         * Handles drawing a list's header row.
         */
        protected void drawListHeader(int insideLeft, int insideTop, Tessellator tessellatorIn)
        {
            super.drawListHeader(insideLeft, insideTop, tessellatorIn);

            if (this.field_148218_l == 0)
            {
                GuiStats.this.drawSprite(insideLeft + 115 - 18 + 1, insideTop + 1 + 1, 72, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 115 - 18, insideTop + 1, 72, 18);
            }

            if (this.field_148218_l == 1)
            {
                GuiStats.this.drawSprite(insideLeft + 165 - 18 + 1, insideTop + 1 + 1, 18, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 165 - 18, insideTop + 1, 18, 18);
            }

            if (this.field_148218_l == 2)
            {
                GuiStats.this.drawSprite(insideLeft + 215 - 18 + 1, insideTop + 1 + 1, 36, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 215 - 18, insideTop + 1, 36, 18);
            }

            if (this.field_148218_l == 3)
            {
                GuiStats.this.drawSprite(insideLeft + 265 - 18 + 1, insideTop + 1 + 1, 90, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 265 - 18, insideTop + 1, 90, 18);
            }

            if (this.field_148218_l == 4)
            {
                GuiStats.this.drawSprite(insideLeft + 315 - 18 + 1, insideTop + 1 + 1, 108, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 315 - 18, insideTop + 1, 108, 18);
            }
        }

        protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks)
        {
            StatCrafting statcrafting = this.func_148211_c(slotIndex);
            Item item = statcrafting.func_150959_a();
            GuiStats.this.drawStatsScreen(xPos + 40, yPos, item);
            this.func_148209_a(StatList.func_188059_c(item), xPos + 115, yPos, slotIndex % 2 == 0);
            this.func_148209_a(StatList.func_188060_a(item), xPos + 165, yPos, slotIndex % 2 == 0);
            this.func_148209_a(statcrafting, xPos + 215, yPos, slotIndex % 2 == 0);
            this.func_148209_a(StatList.func_188056_d(item), xPos + 265, yPos, slotIndex % 2 == 0);
            this.func_148209_a(StatList.func_188058_e(item), xPos + 315, yPos, slotIndex % 2 == 0);
        }

        protected String func_148210_b(int p_148210_1_)
        {
            if (p_148210_1_ == 1)
            {
                return "stat.crafted";
            }
            else if (p_148210_1_ == 2)
            {
                return "stat.used";
            }
            else if (p_148210_1_ == 3)
            {
                return "stat.pickup";
            }
            else
            {
                return p_148210_1_ == 4 ? "stat.dropped" : "stat.depleted";
            }
        }
    }

    @SideOnly(Side.CLIENT)
    class StatsMobsList extends GuiSlot
    {
        private final List<EntityList.EntityEggInfo> mobs = Lists.<EntityList.EntityEggInfo>newArrayList();

        public StatsMobsList(Minecraft mcIn)
        {
            super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, GuiStats.this.fontRenderer.FONT_HEIGHT * 4);
            this.setShowSelectionBox(false);

            for (EntityList.EntityEggInfo entitylist$entityegginfo : EntityList.field_75627_a.values())
            {
                if (GuiStats.this.stats.getValue(entitylist$entityegginfo.field_151512_d) > 0 || GuiStats.this.stats.getValue(entitylist$entityegginfo.field_151513_e) > 0)
                {
                    this.mobs.add(entitylist$entityegginfo);
                }
            }
        }

        protected int getSize()
        {
            return this.mobs.size();
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

        /**
         * Return the height of the content being scrolled
         */
        protected int getContentHeight()
        {
            return this.getSize() * GuiStats.this.fontRenderer.FONT_HEIGHT * 4;
        }

        protected void drawBackground()
        {
            GuiStats.this.drawDefaultBackground();
        }

        protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks)
        {
            EntityList.EntityEggInfo entitylist$entityegginfo = this.mobs.get(slotIndex);
            String s = I18n.format("entity." + EntityList.func_191302_a(entitylist$entityegginfo.field_75613_a) + ".name");
            int i = GuiStats.this.stats.getValue(entitylist$entityegginfo.field_151512_d);
            int j = GuiStats.this.stats.getValue(entitylist$entityegginfo.field_151513_e);
            String s1 = I18n.format("stat.entityKills", i, s);
            String s2 = I18n.format("stat.entityKilledBy", s, j);

            if (i == 0)
            {
                s1 = I18n.format("stat.entityKills.none", s);
            }

            if (j == 0)
            {
                s2 = I18n.format("stat.entityKilledBy.none", s);
            }

            GuiStats.this.drawString(GuiStats.this.fontRenderer, s, xPos + 2 - 10, yPos + 1, 16777215);
            GuiStats.this.drawString(GuiStats.this.fontRenderer, s1, xPos + 2, yPos + 1 + GuiStats.this.fontRenderer.FONT_HEIGHT, i == 0 ? 6316128 : 9474192);
            GuiStats.this.drawString(GuiStats.this.fontRenderer, s2, xPos + 2, yPos + 1 + GuiStats.this.fontRenderer.FONT_HEIGHT * 2, j == 0 ? 6316128 : 9474192);
        }
    }
}