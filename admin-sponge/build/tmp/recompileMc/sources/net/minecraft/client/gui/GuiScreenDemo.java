package net.minecraft.client.gui;

import java.io.IOException;
import java.net.URI;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class GuiScreenDemo extends GuiScreen
{
    private static final Logger field_146349_a = LogManager.getLogger();
    private static final ResourceLocation DEMO_BACKGROUND_LOCATION = new ResourceLocation("textures/gui/demo_background.png");

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttons.clear();
        int i = -16;
        this.buttons.add(new GuiButton(1, this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20, I18n.format("demo.help.buy")));
        this.buttons.add(new GuiButton(2, this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20, I18n.format("demo.help.later")));
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        switch (p_146284_1_.id)
        {
            case 1:
                p_146284_1_.enabled = false;

                try
                {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop").invoke((Object)null);
                    oclass.getMethod("browse", URI.class).invoke(object, new URI("http://www.minecraft.net/store?source=demo"));
                }
                catch (Throwable throwable)
                {
                    field_146349_a.error("Couldn't open link", throwable);
                }

                break;
            case 2:
                this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.func_71381_h();
        }
    }

    /**
     * Draws either a gradient over the background world (if there is a world), or a dirt screen if there is no world.
     *  
     * This method should usually be called before doing any other rendering; otherwise weird results will occur if
     * there is no world, and the world will not be tinted if there is.
     *  
     * Do not call after having already done other rendering, as it will draw over it.
     */
    public void drawDefaultBackground()
    {
        super.drawDefaultBackground();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(DEMO_BACKGROUND_LOCATION);
        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, 248, 166);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        int i = (this.width - 248) / 2 + 10;
        int j = (this.height - 166) / 2 + 8;
        this.fontRenderer.func_78276_b(I18n.format("demo.help.title"), i, j, 2039583);
        j = j + 12;
        GameSettings gamesettings = this.mc.gameSettings;
        this.fontRenderer.func_78276_b(I18n.format("demo.help.movementShort", gamesettings.keyBindForward.getDisplayName(), gamesettings.keyBindLeft.getDisplayName(), gamesettings.keyBindBack.getDisplayName(), gamesettings.keyBindRight.getDisplayName()), i, j, 5197647);
        this.fontRenderer.func_78276_b(I18n.format("demo.help.movementMouse"), i, j + 12, 5197647);
        this.fontRenderer.func_78276_b(I18n.format("demo.help.jump", gamesettings.keyBindJump.getDisplayName()), i, j + 24, 5197647);
        this.fontRenderer.func_78276_b(I18n.format("demo.help.inventory", gamesettings.keyBindInventory.getDisplayName()), i, j + 36, 5197647);
        this.fontRenderer.drawSplitString(I18n.format("demo.help.fullWrapped"), i, j + 68, 218, 2039583);
        super.render(mouseX, mouseY, partialTicks);
    }
}