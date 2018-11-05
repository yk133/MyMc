/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.client.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import static net.minecraftforge.fml.client.config.GuiUtils.RESET_CHAR;
import static net.minecraftforge.fml.client.config.GuiUtils.UNDO_CHAR;

import net.minecraftforge.fml.common.FMLLog;
import org.lwjgl.input.Keyboard;

/**
 * This class is the base screen used for editing an array-type property. It provides a list of array entries for the user to edit.
 * This screen is invoked from a GuiConfig screen by controls that use the EditListPropEntry IGuiConfigListEntry object.
 *
 * @author bspkrs
 */
public class GuiEditArray extends GuiScreen
{
    protected GuiScreen parentScreen;
    protected IConfigElement configElement;
    protected GuiEditArrayEntries entryList;
    protected GuiButtonExt btnUndoChanges, btnDefault, btnDone;
    protected String title;
    protected String titleLine2;
    protected String titleLine3;
    protected int slotIndex;
    protected final Object[] beforeValues;
    protected Object[] currentValues;
    protected HoverChecker tooltipHoverChecker;
    protected List<String> toolTip;
    protected boolean enabled;

    public GuiEditArray(GuiScreen parentScreen, IConfigElement configElement, int slotIndex, Object[] currentValues, boolean enabled)
    {
        this.mc = Minecraft.getInstance();
        this.parentScreen = parentScreen;
        this.configElement = configElement;
        this.slotIndex = slotIndex;
        this.beforeValues = currentValues;
        this.currentValues = currentValues;
        this.toolTip = new ArrayList<String>();
        this.enabled = enabled;
        String propName = I18n.format(configElement.getLanguageKey());
        String comment;

        comment = I18n.format(configElement.getLanguageKey() + ".tooltip",
                "\n" + TextFormatting.AQUA, configElement.getDefault(), configElement.getMinValue(), configElement.getMaxValue());

        if (!comment.equals(configElement.getLanguageKey() + ".tooltip"))
            Collections.addAll(toolTip, (TextFormatting.GREEN + propName + "\n" + TextFormatting.YELLOW + comment).split("\n"));
        else if (configElement.getComment() != null && !configElement.getComment().trim().isEmpty())
            Collections.addAll(toolTip, (TextFormatting.GREEN + propName + "\n" + TextFormatting.YELLOW + configElement.getComment()).split("\n"));
        else
            Collections.addAll(toolTip, (TextFormatting.GREEN + propName + "\n" + TextFormatting.RED + "No tooltip defined.").split("\n"));

        if (parentScreen instanceof GuiConfig)
        {
            this.title = ((GuiConfig) parentScreen).title;
            if (((GuiConfig) parentScreen).titleLine2 != null)
            {
                this.titleLine2 = ((GuiConfig) parentScreen).titleLine2;
                this.titleLine3 = I18n.format(configElement.getLanguageKey());
            }
            else
                this.titleLine2 = I18n.format(configElement.getLanguageKey());
            this.tooltipHoverChecker = new HoverChecker(28, 37, 0, parentScreen.width, 800);
        }
        else
        {
            this.title = I18n.format(configElement.getLanguageKey());
            this.tooltipHoverChecker = new HoverChecker(8, 17, 0, parentScreen.width, 800);
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    @Override
    public void initGui()
    {
        this.entryList = createEditArrayEntries();

        int undoGlyphWidth = mc.fontRenderer.getStringWidth(UNDO_CHAR) * 2;
        int resetGlyphWidth = mc.fontRenderer.getStringWidth(RESET_CHAR) * 2;
        int doneWidth = Math.max(mc.fontRenderer.getStringWidth(I18n.format("gui.done")) + 20, 100);
        int undoWidth = mc.fontRenderer.getStringWidth(" " + I18n.format("fml.configgui.tooltip.undoChanges")) + undoGlyphWidth + 20;
        int resetWidth = mc.fontRenderer.getStringWidth(" " + I18n.format("fml.configgui.tooltip.resetToDefault")) + resetGlyphWidth + 20;
        int buttonWidthHalf = (doneWidth + 5 + undoWidth + 5 + resetWidth) / 2;
        this.buttons.add(btnDone = new GuiButtonExt(2000, this.width / 2 - buttonWidthHalf, this.height - 29, doneWidth, 20, I18n.format("gui.done")));
        this.buttons.add(btnDefault = new GuiUnicodeGlyphButton(2001, this.width / 2 - buttonWidthHalf + doneWidth + 5 + undoWidth + 5,
                this.height - 29, resetWidth, 20, " " + I18n.format("fml.configgui.tooltip.resetToDefault"), RESET_CHAR, 2.0F));
        this.buttons.add(btnUndoChanges = new GuiUnicodeGlyphButton(2002, this.width / 2 - buttonWidthHalf + doneWidth + 5,
                this.height - 29, undoWidth, 20, " " + I18n.format("fml.configgui.tooltip.undoChanges"), UNDO_CHAR, 2.0F));
    }

    @Override
    protected void func_146284_a(GuiButton button)
    {
        if (button.id == 2000)
        {
            try
            {
                this.entryList.saveListChanges();
            }
            catch (Throwable e)
            {
                FMLLog.log.error("Error performing GuiEditArray action:", e);
            }
            this.mc.displayGuiScreen(this.parentScreen);
        }
        else if (button.id == 2001)
        {
            this.currentValues = configElement.getDefaults();
            this.entryList = createEditArrayEntries();
        }
        else if (button.id == 2002)
        {
            this.currentValues = Arrays.copyOf(beforeValues, beforeValues.length);
            this.entryList = createEditArrayEntries();
        }
    }

    protected GuiEditArrayEntries createEditArrayEntries()
    {
        return new GuiEditArrayEntries(this, this.mc, this.configElement, this.beforeValues, this.currentValues);
    }

    @Override
    public void func_146274_d() throws IOException
    {
        super.func_146274_d();
        this.entryList.func_178039_p();
    }

    @Override
    protected void func_73864_a(int x, int y, int mouseEvent) throws IOException
    {
        if (mouseEvent != 0 || !this.entryList.func_148179_a(x, y, mouseEvent))
        {
            this.entryList.mouseClickedPassThru(x, y, mouseEvent);
            super.func_73864_a(x, y, mouseEvent);
        }
    }

    @Override
    protected void func_146286_b(int x, int y, int mouseEvent)
    {
        if (mouseEvent != 0 || !this.entryList.func_148181_b(x, y, mouseEvent))
        {
            super.func_146286_b(x, y, mouseEvent);
        }
    }

    @Override
    protected void func_73869_a(char eventChar, int eventKey)
    {
        if (eventKey == Keyboard.KEY_ESCAPE)
            this.mc.displayGuiScreen(parentScreen);
        else
            this.entryList.keyTyped(eventChar, eventKey);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void tick()
    {
        super.tick();
        this.entryList.updateScreen();
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void render(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.entryList.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 8, 16777215);

        if (this.titleLine2 != null)
            this.drawCenteredString(this.fontRenderer, this.titleLine2, this.width / 2, 18, 16777215);

        if (this.titleLine3 != null)
            this.drawCenteredString(this.fontRenderer, this.titleLine3, this.width / 2, 28, 16777215);

        this.btnDone.enabled = this.entryList.isListSavable();
        this.btnDefault.enabled = enabled && !this.entryList.isDefault();
        this.btnUndoChanges.enabled = enabled && this.entryList.isChanged();
        super.render(par1, par2, par3);
        this.entryList.drawScreenPost(par1, par2, par3);

        if (this.tooltipHoverChecker != null && this.tooltipHoverChecker.checkHover(par1, par2))
            drawToolTip(this.toolTip, par1, par2);
    }

    public void drawToolTip(List<String> stringList, int x, int y)
    {
        GuiUtils.drawHoveringText(stringList, x, y, width, height, 300, fontRenderer);
    }
}