package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiResourcePackList extends GuiListExtended
{
    protected final Minecraft mc;
    protected final List<ResourcePackListEntry> field_148204_l;

    public GuiResourcePackList(Minecraft p_i45055_1_, int p_i45055_2_, int p_i45055_3_, List<ResourcePackListEntry> p_i45055_4_)
    {
        super(p_i45055_1_, p_i45055_2_, p_i45055_3_, 32, p_i45055_3_ - 55 + 4, 36);
        this.mc = p_i45055_1_;
        this.field_148204_l = p_i45055_4_;
        this.centerListVertically = false;
        this.setHasListHeader(true, (int)((float)p_i45055_1_.fontRenderer.FONT_HEIGHT * 1.5F));
    }

    /**
     * Handles drawing a list's header row.
     */
    protected void drawListHeader(int insideLeft, int insideTop, Tessellator tessellatorIn)
    {
        String s = TextFormatting.UNDERLINE + "" + TextFormatting.BOLD + this.getListHeader();
        this.mc.fontRenderer.func_78276_b(s, insideLeft + this.width / 2 - this.mc.fontRenderer.getStringWidth(s) / 2, Math.min(this.top + 3, insideTop), 16777215);
    }

    protected abstract String getListHeader();

    public List<ResourcePackListEntry> func_148201_l()
    {
        return this.field_148204_l;
    }

    protected int getSize()
    {
        return this.func_148201_l().size();
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public ResourcePackListEntry getListEntry(int index)
    {
        return (ResourcePackListEntry)this.func_148201_l().get(index);
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth()
    {
        return this.width;
    }

    protected int getScrollBarX()
    {
        return this.right - 6;
    }
}