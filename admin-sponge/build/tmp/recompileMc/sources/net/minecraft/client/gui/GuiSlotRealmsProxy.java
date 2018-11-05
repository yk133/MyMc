package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsScrolledSelectionList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSlotRealmsProxy extends GuiSlot
{
    private final RealmsScrolledSelectionList field_154340_k;

    public GuiSlotRealmsProxy(RealmsScrolledSelectionList p_i1085_1_, int p_i1085_2_, int p_i1085_3_, int p_i1085_4_, int p_i1085_5_, int p_i1085_6_)
    {
        super(Minecraft.getInstance(), p_i1085_2_, p_i1085_3_, p_i1085_4_, p_i1085_5_, p_i1085_6_);
        this.field_154340_k = p_i1085_1_;
    }

    protected int getSize()
    {
        return this.field_154340_k.getItemCount();
    }

    protected void func_148144_a(int p_148144_1_, boolean p_148144_2_, int p_148144_3_, int p_148144_4_)
    {
        this.field_154340_k.selectItem(p_148144_1_, p_148144_2_, p_148144_3_, p_148144_4_);
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int slotIndex)
    {
        return this.field_154340_k.isSelectedItem(slotIndex);
    }

    protected void drawBackground()
    {
        this.field_154340_k.renderBackground();
    }

    protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks)
    {
        this.field_154340_k.renderItem(slotIndex, xPos, yPos, heightIn, mouseXIn, mouseYIn);
    }

    public int func_154338_k()
    {
        return this.width;
    }

    public int func_154339_l()
    {
        return this.field_148162_h;
    }

    public int func_154337_m()
    {
        return this.field_148150_g;
    }

    /**
     * Return the height of the content being scrolled
     */
    protected int getContentHeight()
    {
        return this.field_154340_k.getMaxPosition();
    }

    protected int getScrollBarX()
    {
        return this.field_154340_k.getScrollbarPosition();
    }

    public void func_178039_p()
    {
        super.func_178039_p();
    }
}