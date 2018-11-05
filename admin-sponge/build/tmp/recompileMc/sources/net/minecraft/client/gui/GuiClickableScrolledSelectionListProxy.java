package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.Tezzelator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class GuiClickableScrolledSelectionListProxy extends GuiSlot
{
    private final RealmsClickableScrolledSelectionList field_178046_u;

    public GuiClickableScrolledSelectionListProxy(RealmsClickableScrolledSelectionList p_i45526_1_, int p_i45526_2_, int p_i45526_3_, int p_i45526_4_, int p_i45526_5_, int p_i45526_6_)
    {
        super(Minecraft.getInstance(), p_i45526_2_, p_i45526_3_, p_i45526_4_, p_i45526_5_, p_i45526_6_);
        this.field_178046_u = p_i45526_1_;
    }

    protected int getSize()
    {
        return this.field_178046_u.getItemCount();
    }

    protected void func_148144_a(int p_148144_1_, boolean p_148144_2_, int p_148144_3_, int p_148144_4_)
    {
        this.field_178046_u.selectItem(p_148144_1_, p_148144_2_, p_148144_3_, p_148144_4_);
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int slotIndex)
    {
        return this.field_178046_u.isSelectedItem(slotIndex);
    }

    protected void drawBackground()
    {
        this.field_178046_u.renderBackground();
    }

    protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks)
    {
        this.field_178046_u.renderItem(slotIndex, xPos, yPos, heightIn, mouseXIn, mouseYIn);
    }

    public int func_178044_e()
    {
        return this.width;
    }

    public int func_178042_f()
    {
        return this.field_148162_h;
    }

    public int func_178045_g()
    {
        return this.field_148150_g;
    }

    /**
     * Return the height of the content being scrolled
     */
    protected int getContentHeight()
    {
        return this.field_178046_u.getMaxPosition();
    }

    protected int getScrollBarX()
    {
        return this.field_178046_u.getScrollbarPosition();
    }

    public void func_178039_p()
    {
        super.func_178039_p();

        if (this.field_148170_p > 0.0F && Mouse.getEventButtonState())
        {
            this.field_178046_u.customMouseEvent(this.top, this.bottom, this.headerPadding, this.amountScrolled, this.slotHeight);
        }
    }

    public void func_178043_a(int p_178043_1_, int p_178043_2_, int p_178043_3_, Tezzelator p_178043_4_)
    {
        this.field_178046_u.renderSelected(p_178043_1_, p_178043_2_, p_178043_3_, p_178043_4_);
    }

    /**
     * Draws the selection box around the selected slot element.
     */
    protected void drawSelectionBox(int insideLeft, int insideTop, int mouseXIn, int mouseYIn, float partialTicks)
    {
        int i = this.getSize();

        for (int j = 0; j < i; ++j)
        {
            int k = insideTop + j * this.slotHeight + this.headerPadding;
            int l = this.slotHeight - 4;

            if (k > this.bottom || k + l < this.top)
            {
                this.updateItemPos(j, insideLeft, k, partialTicks);
            }

            if (this.showSelectionBox && this.isSelected(j))
            {
                this.func_178043_a(this.width, k, l, Tezzelator.instance);
            }

            this.drawSlot(j, insideLeft, k, l, mouseXIn, mouseYIn, partialTicks);
        }
    }
}