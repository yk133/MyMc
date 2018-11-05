package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiOptionsRowList extends GuiListExtended
{
    private final List<GuiOptionsRowList.Row> field_148184_k = Lists.<GuiOptionsRowList.Row>newArrayList();

    public GuiOptionsRowList(Minecraft mcIn, int p_i45015_2_, int p_i45015_3_, int p_i45015_4_, int p_i45015_5_, int p_i45015_6_, GameSettings.Options... p_i45015_7_)
    {
        super(mcIn, p_i45015_2_, p_i45015_3_, p_i45015_4_, p_i45015_5_, p_i45015_6_);
        this.centerListVertically = false;

        for (int i = 0; i < p_i45015_7_.length; i += 2)
        {
            GameSettings.Options gamesettings$options = p_i45015_7_[i];
            GameSettings.Options gamesettings$options1 = i < p_i45015_7_.length - 1 ? p_i45015_7_[i + 1] : null;
            GuiButton guibutton = this.func_148182_a(mcIn, p_i45015_2_ / 2 - 155, 0, gamesettings$options);
            GuiButton guibutton1 = this.func_148182_a(mcIn, p_i45015_2_ / 2 - 155 + 160, 0, gamesettings$options1);
            this.field_148184_k.add(new GuiOptionsRowList.Row(guibutton, guibutton1));
        }
    }

    private GuiButton func_148182_a(Minecraft p_148182_1_, int p_148182_2_, int p_148182_3_, GameSettings.Options p_148182_4_)
    {
        if (p_148182_4_ == null)
        {
            return null;
        }
        else
        {
            int i = p_148182_4_.getOrdinal();
            return (GuiButton)(p_148182_4_.isFloat() ? new GuiOptionSlider(i, p_148182_2_, p_148182_3_, p_148182_4_) : new GuiOptionButton(i, p_148182_2_, p_148182_3_, p_148182_4_, p_148182_1_.gameSettings.getKeyBinding(p_148182_4_)));
        }
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiOptionsRowList.Row getListEntry(int index)
    {
        return this.field_148184_k.get(index);
    }

    protected int getSize()
    {
        return this.field_148184_k.size();
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth()
    {
        return 400;
    }

    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 32;
    }

    @SideOnly(Side.CLIENT)
    public static class Row implements GuiListExtended.IGuiListEntry
        {
            private final Minecraft field_148325_a = Minecraft.getInstance();
            private final GuiButton buttonA;
            private final GuiButton buttonB;

            public Row(GuiButton p_i45014_1_, GuiButton p_i45014_2_)
            {
                this.buttonA = p_i45014_1_;
                this.buttonB = p_i45014_2_;
            }

            public void func_192634_a(int p_192634_1_, int p_192634_2_, int p_192634_3_, int p_192634_4_, int p_192634_5_, int p_192634_6_, int p_192634_7_, boolean p_192634_8_, float p_192634_9_)
            {
                if (this.buttonA != null)
                {
                    this.buttonA.y = p_192634_3_;
                    this.buttonA.func_191745_a(this.field_148325_a, p_192634_6_, p_192634_7_, p_192634_9_);
                }

                if (this.buttonB != null)
                {
                    this.buttonB.y = p_192634_3_;
                    this.buttonB.func_191745_a(this.field_148325_a, p_192634_6_, p_192634_7_, p_192634_9_);
                }
            }

            public boolean func_148278_a(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
            {
                if (this.buttonA.func_146116_c(this.field_148325_a, p_148278_2_, p_148278_3_))
                {
                    if (this.buttonA instanceof GuiOptionButton)
                    {
                        this.field_148325_a.gameSettings.setOptionValue(((GuiOptionButton)this.buttonA).getOption(), 1);
                        this.buttonA.displayString = this.field_148325_a.gameSettings.getKeyBinding(GameSettings.Options.byOrdinal(this.buttonA.id));
                    }

                    return true;
                }
                else if (this.buttonB != null && this.buttonB.func_146116_c(this.field_148325_a, p_148278_2_, p_148278_3_))
                {
                    if (this.buttonB instanceof GuiOptionButton)
                    {
                        this.field_148325_a.gameSettings.setOptionValue(((GuiOptionButton)this.buttonB).getOption(), 1);
                        this.buttonB.displayString = this.field_148325_a.gameSettings.getKeyBinding(GameSettings.Options.byOrdinal(this.buttonB.id));
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }

            public void func_148277_b(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_)
            {
                if (this.buttonA != null)
                {
                    this.buttonA.func_146118_a(p_148277_2_, p_148277_3_);
                }

                if (this.buttonB != null)
                {
                    this.buttonB.func_146118_a(p_148277_2_, p_148277_3_);
                }
            }

            public void func_192633_a(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_)
            {
            }
        }
}