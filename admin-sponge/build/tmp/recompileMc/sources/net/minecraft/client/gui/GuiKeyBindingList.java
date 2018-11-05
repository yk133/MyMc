package net.minecraft.client.gui;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

@SideOnly(Side.CLIENT)
public class GuiKeyBindingList extends GuiListExtended
{
    private final GuiControls controlsScreen;
    private final Minecraft mc;
    private final GuiListExtended.IGuiListEntry[] field_148190_m;
    private int maxListLabelWidth;

    public GuiKeyBindingList(GuiControls controls, Minecraft mcIn)
    {
        super(mcIn, controls.width + 45, controls.height, 63, controls.height - 32, 20);
        this.controlsScreen = controls;
        this.mc = mcIn;
        KeyBinding[] akeybinding = (KeyBinding[])ArrayUtils.clone(mcIn.gameSettings.keyBindings);
        this.field_148190_m = new GuiListExtended.IGuiListEntry[akeybinding.length + KeyBinding.func_151467_c().size()];
        Arrays.sort((Object[])akeybinding);
        int i = 0;
        String s = null;

        for (KeyBinding keybinding : akeybinding)
        {
            String s1 = keybinding.getKeyCategory();

            if (!s1.equals(s))
            {
                s = s1;
                this.field_148190_m[i++] = new GuiKeyBindingList.CategoryEntry(s1);
            }

            int j = mcIn.fontRenderer.getStringWidth(I18n.format(keybinding.getKeyDescription()));

            if (j > this.maxListLabelWidth)
            {
                this.maxListLabelWidth = j;
            }

            this.field_148190_m[i++] = new GuiKeyBindingList.KeyEntry(keybinding);
        }
    }

    protected int getSize()
    {
        return this.field_148190_m.length;
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiListExtended.IGuiListEntry getListEntry(int index)
    {
        return this.field_148190_m[index];
    }

    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 35;
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth()
    {
        return super.getListWidth() + 32;
    }

    @SideOnly(Side.CLIENT)
    public class CategoryEntry implements GuiListExtended.IGuiListEntry
    {
        private final String labelText;
        private final int labelWidth;

        public CategoryEntry(String name)
        {
            this.labelText = I18n.format(name);
            this.labelWidth = GuiKeyBindingList.this.mc.fontRenderer.getStringWidth(this.labelText);
        }

        public void func_192634_a(int p_192634_1_, int p_192634_2_, int p_192634_3_, int p_192634_4_, int p_192634_5_, int p_192634_6_, int p_192634_7_, boolean p_192634_8_, float p_192634_9_)
        {
            GuiKeyBindingList.this.mc.fontRenderer.func_78276_b(this.labelText, GuiKeyBindingList.this.mc.currentScreen.width / 2 - this.labelWidth / 2, p_192634_3_ + p_192634_5_ - GuiKeyBindingList.this.mc.fontRenderer.FONT_HEIGHT - 1, 16777215);
        }

        public boolean func_148278_a(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
        {
            return false;
        }

        public void func_148277_b(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_)
        {
        }

        public void func_192633_a(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_)
        {
        }
    }

    @SideOnly(Side.CLIENT)
    public class KeyEntry implements GuiListExtended.IGuiListEntry
    {
        /** The keybinding specified for this KeyEntry */
        private final KeyBinding keybinding;
        /** The localized key description for this KeyEntry */
        private final String keyDesc;
        private final GuiButton btnChangeKeyBinding;
        private final GuiButton btnReset;

        private KeyEntry(KeyBinding name)
        {
            this.keybinding = name;
            this.keyDesc = I18n.format(name.getKeyDescription());
            this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 95, 20, I18n.format(name.getKeyDescription()));
            this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset"));
        }

        public void func_192634_a(int p_192634_1_, int p_192634_2_, int p_192634_3_, int p_192634_4_, int p_192634_5_, int p_192634_6_, int p_192634_7_, boolean p_192634_8_, float p_192634_9_)
        {
            boolean flag = GuiKeyBindingList.this.controlsScreen.buttonId == this.keybinding;
            GuiKeyBindingList.this.mc.fontRenderer.func_78276_b(this.keyDesc, p_192634_2_ + 90 - GuiKeyBindingList.this.maxListLabelWidth, p_192634_3_ + p_192634_5_ / 2 - GuiKeyBindingList.this.mc.fontRenderer.FONT_HEIGHT / 2, 16777215);
            this.btnReset.x = p_192634_2_ + 210;
            this.btnReset.y = p_192634_3_;
            this.btnReset.enabled = !this.keybinding.isSetToDefaultValue();
            this.btnReset.func_191745_a(GuiKeyBindingList.this.mc, p_192634_6_, p_192634_7_, p_192634_9_);
            this.btnChangeKeyBinding.x = p_192634_2_ + 105;
            this.btnChangeKeyBinding.y = p_192634_3_;
            this.btnChangeKeyBinding.displayString = this.keybinding.getDisplayName();
            boolean flag1 = false;
            boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G

            if (this.keybinding.func_151463_i() != 0)
            {
                for (KeyBinding keybinding : GuiKeyBindingList.this.mc.gameSettings.keyBindings)
                {
                    if (keybinding != this.keybinding && keybinding.conflicts(this.keybinding))
                    {
                        flag1 = true;
                        keyCodeModifierConflict &= keybinding.hasKeyCodeModifierConflict(this.keybinding);
                    }
                }
            }

            if (flag)
            {
                this.btnChangeKeyBinding.displayString = TextFormatting.WHITE + "> " + TextFormatting.YELLOW + this.btnChangeKeyBinding.displayString + TextFormatting.WHITE + " <";
            }
            else if (flag1)
            {
                this.btnChangeKeyBinding.displayString = (keyCodeModifierConflict ? TextFormatting.GOLD : TextFormatting.RED) + this.btnChangeKeyBinding.displayString;
            }

            this.btnChangeKeyBinding.func_191745_a(GuiKeyBindingList.this.mc, p_192634_6_, p_192634_7_, p_192634_9_);
        }

        public boolean func_148278_a(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
        {
            if (this.btnChangeKeyBinding.func_146116_c(GuiKeyBindingList.this.mc, p_148278_2_, p_148278_3_))
            {
                GuiKeyBindingList.this.controlsScreen.buttonId = this.keybinding;
                return true;
            }
            else if (this.btnReset.func_146116_c(GuiKeyBindingList.this.mc, p_148278_2_, p_148278_3_))
            {
                this.keybinding.setToDefault();
                GuiKeyBindingList.this.mc.gameSettings.func_151440_a(this.keybinding, this.keybinding.func_151469_h());
                KeyBinding.resetKeyBindingArrayAndHash();
                return true;
            }
            else
            {
                return false;
            }
        }

        public void func_148277_b(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_)
        {
            this.btnChangeKeyBinding.func_146118_a(p_148277_2_, p_148277_3_);
            this.btnReset.func_146118_a(p_148277_2_, p_148277_3_);
        }

        public void func_192633_a(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_)
        {
        }
    }
}