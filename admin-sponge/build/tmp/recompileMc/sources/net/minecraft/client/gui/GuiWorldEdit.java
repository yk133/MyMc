package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiWorldEdit extends GuiScreen
{
    private final GuiScreen lastScreen;
    private GuiTextField nameEdit;
    private final String worldId;

    public GuiWorldEdit(GuiScreen parent, String worldName)
    {
        this.lastScreen = parent;
        this.worldId = worldName;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void tick()
    {
        this.nameEdit.tick();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        GuiButton guibutton = this.addButton(new GuiButton(3, this.width / 2 - 100, this.height / 4 + 24 + 12, I18n.format("selectWorld.edit.resetIcon")));
        this.buttons.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 48 + 12, I18n.format("selectWorld.edit.openFolder")));
        this.buttons.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("selectWorld.edit.save")));
        this.buttons.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
        guibutton.enabled = this.mc.getSaveLoader().getFile(this.worldId, "icon.png").isFile();
        ISaveFormat isaveformat = this.mc.getSaveLoader();
        WorldInfo worldinfo = isaveformat.getWorldInfo(this.worldId);
        String s = worldinfo == null ? "" : worldinfo.getWorldName();
        this.nameEdit = new GuiTextField(2, this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
        this.nameEdit.setFocused(true);
        this.nameEdit.setText(s);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        if (p_146284_1_.enabled)
        {
            if (p_146284_1_.id == 1)
            {
                this.mc.displayGuiScreen(this.lastScreen);
            }
            else if (p_146284_1_.id == 0)
            {
                ISaveFormat isaveformat = this.mc.getSaveLoader();
                isaveformat.renameWorld(this.worldId, this.nameEdit.getText().trim());
                this.mc.displayGuiScreen(this.lastScreen);
            }
            else if (p_146284_1_.id == 3)
            {
                ISaveFormat isaveformat1 = this.mc.getSaveLoader();
                FileUtils.deleteQuietly(isaveformat1.getFile(this.worldId, "icon.png"));
                p_146284_1_.enabled = false;
            }
            else if (p_146284_1_.id == 4)
            {
                ISaveFormat isaveformat2 = this.mc.getSaveLoader();
                OpenGlHelper.func_188786_a(isaveformat2.getFile(this.worldId, "icon.png").getParentFile());
            }
        }
    }

    protected void func_73869_a(char p_73869_1_, int p_73869_2_) throws IOException
    {
        this.nameEdit.func_146201_a(p_73869_1_, p_73869_2_);
        (this.buttons.get(2)).enabled = !this.nameEdit.getText().trim().isEmpty();

        if (p_73869_2_ == 28 || p_73869_2_ == 156)
        {
            this.func_146284_a(this.buttons.get(2));
        }
    }

    protected void func_73864_a(int p_73864_1_, int p_73864_2_, int p_73864_3_) throws IOException
    {
        super.func_73864_a(p_73864_1_, p_73864_2_, p_73864_3_);
        this.nameEdit.func_146192_a(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format("selectWorld.edit.title"), this.width / 2, 20, 16777215);
        this.drawString(this.fontRenderer, I18n.format("selectWorld.enterName"), this.width / 2 - 100, 47, 10526880);
        this.nameEdit.func_146194_f();
        super.render(mouseX, mouseY, partialTicks);
    }
}