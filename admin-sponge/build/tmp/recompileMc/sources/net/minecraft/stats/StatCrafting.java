package net.minecraft.stats;

import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class StatCrafting extends StatBase
{
    private final Item field_150960_a;

    public StatCrafting(String p_i45910_1_, String p_i45910_2_, ITextComponent p_i45910_3_, Item p_i45910_4_)
    {
        super(p_i45910_1_ + p_i45910_2_, p_i45910_3_);
        this.field_150960_a = p_i45910_4_;
    }

    @SideOnly(Side.CLIENT)
    public Item func_150959_a()
    {
        return this.field_150960_a;
    }
}