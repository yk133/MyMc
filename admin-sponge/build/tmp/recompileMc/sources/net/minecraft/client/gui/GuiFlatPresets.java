package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiFlatPresets extends GuiScreen
{
    private static final List<GuiFlatPresets.LayerItem> FLAT_WORLD_PRESETS = Lists.<GuiFlatPresets.LayerItem>newArrayList();
    /** The parent GUI */
    private final GuiCreateFlatWorld parentScreen;
    private String presetsTitle;
    private String presetsShare;
    private String listText;
    private GuiFlatPresets.ListSlot list;
    private GuiButton btnSelect;
    private GuiTextField export;

    public GuiFlatPresets(GuiCreateFlatWorld parent)
    {
        this.parentScreen = parent;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttons.clear();
        Keyboard.enableRepeatEvents(true);
        this.presetsTitle = I18n.format("createWorld.customize.presets.title");
        this.presetsShare = I18n.format("createWorld.customize.presets.share");
        this.listText = I18n.format("createWorld.customize.presets.list");
        this.export = new GuiTextField(2, this.fontRenderer, 50, 40, this.width - 100, 20);
        this.list = new GuiFlatPresets.ListSlot();
        this.export.setMaxStringLength(1230);
        this.export.setText(this.parentScreen.func_146384_e());
        this.btnSelect = this.addButton(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("createWorld.customize.presets.select")));
        this.buttons.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
        this.updateButtonValidity();
    }

    public void func_146274_d() throws IOException
    {
        super.func_146274_d();
        this.list.func_178039_p();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void func_73864_a(int p_73864_1_, int p_73864_2_, int p_73864_3_) throws IOException
    {
        this.export.func_146192_a(p_73864_1_, p_73864_2_, p_73864_3_);
        super.func_73864_a(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    protected void func_73869_a(char p_73869_1_, int p_73869_2_) throws IOException
    {
        if (!this.export.func_146201_a(p_73869_1_, p_73869_2_))
        {
            super.func_73869_a(p_73869_1_, p_73869_2_);
        }
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        if (p_146284_1_.id == 0 && this.hasValidSelection())
        {
            this.parentScreen.func_146383_a(this.export.getText());
            this.mc.displayGuiScreen(this.parentScreen);
        }
        else if (p_146284_1_.id == 1)
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, this.presetsTitle, this.width / 2, 8, 16777215);
        this.drawString(this.fontRenderer, this.presetsShare, 50, 30, 10526880);
        this.drawString(this.fontRenderer, this.listText, 50, 70, 10526880);
        this.export.func_146194_f();
        super.render(mouseX, mouseY, partialTicks);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void tick()
    {
        this.export.tick();
        super.tick();
    }

    public void updateButtonValidity()
    {
        this.btnSelect.enabled = this.hasValidSelection();
    }

    private boolean hasValidSelection()
    {
        return this.list.selected > -1 && this.list.selected < FLAT_WORLD_PRESETS.size() || this.export.getText().length() > 1;
    }

    private static void func_146421_a(String p_146421_0_, Item p_146421_1_, Biome p_146421_2_, List<String> p_146421_3_, FlatLayerInfo... p_146421_4_)
    {
        func_175354_a(p_146421_0_, p_146421_1_, 0, p_146421_2_, p_146421_3_, p_146421_4_);
    }

    private static void func_175354_a(String p_175354_0_, Item p_175354_1_, int p_175354_2_, Biome p_175354_3_, List<String> p_175354_4_, FlatLayerInfo... p_175354_5_)
    {
        FlatGeneratorInfo flatgeneratorinfo = new FlatGeneratorInfo();

        for (int i = p_175354_5_.length - 1; i >= 0; --i)
        {
            flatgeneratorinfo.getFlatLayers().add(p_175354_5_[i]);
        }

        flatgeneratorinfo.setBiome(Biome.getIdForBiome(p_175354_3_));
        flatgeneratorinfo.updateLayers();

        for (String s : p_175354_4_)
        {
            flatgeneratorinfo.getWorldFeatures().put(s, Maps.newHashMap());
        }

        FLAT_WORLD_PRESETS.add(new GuiFlatPresets.LayerItem(p_175354_1_, p_175354_2_, p_175354_0_, flatgeneratorinfo.toString()));
    }

    static
    {
        func_146421_a(I18n.format("createWorld.customize.preset.classic_flat"), Item.getItemFromBlock(Blocks.GRASS), Biomes.PLAINS, Arrays.asList("village"), new FlatLayerInfo(1, Blocks.GRASS), new FlatLayerInfo(2, Blocks.DIRT), new FlatLayerInfo(1, Blocks.BEDROCK));
        func_146421_a(I18n.format("createWorld.customize.preset.tunnelers_dream"), Item.getItemFromBlock(Blocks.STONE), Biomes.MOUNTAINS, Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"), new FlatLayerInfo(1, Blocks.GRASS), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(230, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        func_146421_a(I18n.format("createWorld.customize.preset.water_world"), Items.WATER_BUCKET, Biomes.DEEP_OCEAN, Arrays.asList("biome_1", "oceanmonument"), new FlatLayerInfo(90, Blocks.WATER), new FlatLayerInfo(5, Blocks.SAND), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(5, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        func_175354_a(I18n.format("createWorld.customize.preset.overworld"), Item.getItemFromBlock(Blocks.field_150329_H), BlockTallGrass.EnumType.GRASS.func_177044_a(), Biomes.PLAINS, Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake"), new FlatLayerInfo(1, Blocks.GRASS), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        func_146421_a(I18n.format("createWorld.customize.preset.snowy_kingdom"), Item.getItemFromBlock(Blocks.field_150431_aC), Biomes.SNOWY_TUNDRA, Arrays.asList("village", "biome_1"), new FlatLayerInfo(1, Blocks.field_150431_aC), new FlatLayerInfo(1, Blocks.GRASS), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        func_146421_a(I18n.format("createWorld.customize.preset.bottomless_pit"), Items.FEATHER, Biomes.PLAINS, Arrays.asList("village", "biome_1"), new FlatLayerInfo(1, Blocks.GRASS), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(2, Blocks.COBBLESTONE));
        func_146421_a(I18n.format("createWorld.customize.preset.desert"), Item.getItemFromBlock(Blocks.SAND), Biomes.DESERT, Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"), new FlatLayerInfo(8, Blocks.SAND), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        func_146421_a(I18n.format("createWorld.customize.preset.redstone_ready"), Items.REDSTONE, Biomes.DESERT, Collections.emptyList(), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        func_146421_a(I18n.format("createWorld.customize.preset.the_void"), Item.getItemFromBlock(Blocks.BARRIER), Biomes.THE_VOID, Arrays.asList("decoration"), new FlatLayerInfo(1, Blocks.AIR));
    }

    @SideOnly(Side.CLIENT)
    static class LayerItem
        {
            public Item icon;
            public int field_179037_b;
            public String name;
            public String generatorInfo;

            public LayerItem(Item p_i45518_1_, int p_i45518_2_, String p_i45518_3_, String p_i45518_4_)
            {
                this.icon = p_i45518_1_;
                this.field_179037_b = p_i45518_2_;
                this.name = p_i45518_3_;
                this.generatorInfo = p_i45518_4_;
            }
        }

    @SideOnly(Side.CLIENT)
    class ListSlot extends GuiSlot
    {
        public int selected = -1;

        public ListSlot()
        {
            super(GuiFlatPresets.this.mc, GuiFlatPresets.this.width, GuiFlatPresets.this.height, 80, GuiFlatPresets.this.height - 37, 24);
        }

        private void func_178054_a(int p_178054_1_, int p_178054_2_, Item p_178054_3_, int p_178054_4_)
        {
            this.blitSlotBg(p_178054_1_ + 1, p_178054_2_ + 1);
            GlStateManager.enableRescaleNormal();
            RenderHelper.enableGUIStandardItemLighting();
            GuiFlatPresets.this.itemRender.renderItemIntoGUI(new ItemStack(p_178054_3_, 1, p_178054_3_.func_77614_k() ? p_178054_4_ : 0), p_178054_1_ + 2, p_178054_2_ + 2);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
        }

        private void blitSlotBg(int p_148173_1_, int p_148173_2_)
        {
            this.blitSlotIcon(p_148173_1_, p_148173_2_, 0, 0);
        }

        private void blitSlotIcon(int p_148171_1_, int p_148171_2_, int p_148171_3_, int p_148171_4_)
        {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(Gui.STAT_ICONS);
            float f = 0.0078125F;
            float f1 = 0.0078125F;
            int i = 18;
            int j = 18;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos((double)(p_148171_1_ + 0), (double)(p_148171_2_ + 18), (double)GuiFlatPresets.this.zLevel).tex((double)((float)(p_148171_3_ + 0) * 0.0078125F), (double)((float)(p_148171_4_ + 18) * 0.0078125F)).endVertex();
            bufferbuilder.pos((double)(p_148171_1_ + 18), (double)(p_148171_2_ + 18), (double)GuiFlatPresets.this.zLevel).tex((double)((float)(p_148171_3_ + 18) * 0.0078125F), (double)((float)(p_148171_4_ + 18) * 0.0078125F)).endVertex();
            bufferbuilder.pos((double)(p_148171_1_ + 18), (double)(p_148171_2_ + 0), (double)GuiFlatPresets.this.zLevel).tex((double)((float)(p_148171_3_ + 18) * 0.0078125F), (double)((float)(p_148171_4_ + 0) * 0.0078125F)).endVertex();
            bufferbuilder.pos((double)(p_148171_1_ + 0), (double)(p_148171_2_ + 0), (double)GuiFlatPresets.this.zLevel).tex((double)((float)(p_148171_3_ + 0) * 0.0078125F), (double)((float)(p_148171_4_ + 0) * 0.0078125F)).endVertex();
            tessellator.draw();
        }

        protected int getSize()
        {
            return GuiFlatPresets.FLAT_WORLD_PRESETS.size();
        }

        protected void func_148144_a(int p_148144_1_, boolean p_148144_2_, int p_148144_3_, int p_148144_4_)
        {
            this.selected = p_148144_1_;
            GuiFlatPresets.this.updateButtonValidity();
            GuiFlatPresets.this.export.setText((GuiFlatPresets.FLAT_WORLD_PRESETS.get(GuiFlatPresets.this.list.selected)).generatorInfo);
        }

        /**
         * Returns true if the element passed in is currently selected
         */
        protected boolean isSelected(int slotIndex)
        {
            return slotIndex == this.selected;
        }

        protected void drawBackground()
        {
        }

        protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks)
        {
            GuiFlatPresets.LayerItem guiflatpresets$layeritem = GuiFlatPresets.FLAT_WORLD_PRESETS.get(slotIndex);
            this.func_178054_a(xPos, yPos, guiflatpresets$layeritem.icon, guiflatpresets$layeritem.field_179037_b);
            GuiFlatPresets.this.fontRenderer.func_78276_b(guiflatpresets$layeritem.name, xPos + 18 + 5, yPos + 6, 16777215);
        }
    }
}