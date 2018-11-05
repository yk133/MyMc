package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class TabCompleter
{
    protected final GuiTextField field_186844_a;
    protected final boolean field_186845_b;
    protected boolean field_186846_c;
    protected boolean field_186847_d;
    protected int field_186848_e;
    protected List<String> field_186849_f = Lists.<String>newArrayList();

    public TabCompleter(GuiTextField p_i46597_1_, boolean p_i46597_2_)
    {
        this.field_186844_a = p_i46597_1_;
        this.field_186845_b = p_i46597_2_;
    }

    public void func_186841_a()
    {
        if (this.field_186846_c)
        {
            this.field_186844_a.deleteFromCursor(0);
            this.field_186844_a.deleteFromCursor(this.field_186844_a.getNthWordFromPosWS(-1, this.field_186844_a.getCursorPosition(), false) - this.field_186844_a.getCursorPosition());

            if (this.field_186848_e >= this.field_186849_f.size())
            {
                this.field_186848_e = 0;
            }
        }
        else
        {
            int i = this.field_186844_a.getNthWordFromPosWS(-1, this.field_186844_a.getCursorPosition(), false);
            this.field_186849_f.clear();
            this.field_186848_e = 0;
            String s = this.field_186844_a.getText().substring(0, this.field_186844_a.getCursorPosition());
            this.func_186838_a(s);

            if (this.field_186849_f.isEmpty())
            {
                return;
            }

            this.field_186846_c = true;
            this.field_186844_a.deleteFromCursor(i - this.field_186844_a.getCursorPosition());
        }

        this.field_186844_a.writeText(net.minecraft.util.text.TextFormatting.getTextWithoutFormattingCodes(this.field_186849_f.get(this.field_186848_e++)));
    }

    private void func_186838_a(String p_186838_1_)
    {
        if (p_186838_1_.length() >= 1)
        {
            net.minecraftforge.client.ClientCommandHandler.instance.autoComplete(p_186838_1_);
            Minecraft.getInstance().player.connection.sendPacket(new CPacketTabComplete(p_186838_1_, this.func_186839_b(), this.field_186845_b));
            this.field_186847_d = true;
        }
    }

    @Nullable
    public abstract BlockPos func_186839_b();

    public void func_186840_a(String... p_186840_1_)
    {
        if (this.field_186847_d)
        {
            this.field_186846_c = false;
            this.field_186849_f.clear();

            String[] complete = net.minecraftforge.client.ClientCommandHandler.instance.latestAutoComplete;
            if (complete != null)
            {
                p_186840_1_ = com.google.common.collect.ObjectArrays.concat(complete, p_186840_1_, String.class);
            }

            for (String s : p_186840_1_)
            {
                if (!s.isEmpty())
                {
                    this.field_186849_f.add(s);
                }
            }

            String s1 = this.field_186844_a.getText().substring(this.field_186844_a.getNthWordFromPosWS(-1, this.field_186844_a.getCursorPosition(), false));
            String s2 = org.apache.commons.lang3.StringUtils.getCommonPrefix(p_186840_1_);
            s2 = net.minecraft.util.text.TextFormatting.getTextWithoutFormattingCodes(s2);

            if (!s2.isEmpty() && !s1.equalsIgnoreCase(s2))
            {
                this.field_186844_a.deleteFromCursor(0);
                this.field_186844_a.deleteFromCursor(this.field_186844_a.getNthWordFromPosWS(-1, this.field_186844_a.getCursorPosition(), false) - this.field_186844_a.getCursorPosition());
                this.field_186844_a.writeText(s2);
            }
            else if (!this.field_186849_f.isEmpty())
            {
                this.field_186846_c = true;
                this.func_186841_a();
            }
        }
    }

    public void func_186842_c()
    {
        this.field_186846_c = false;
    }

    public void func_186843_d()
    {
        this.field_186847_d = false;
    }
}