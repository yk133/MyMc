package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonToggle extends GuiButton
{
    protected ResourceLocation resourceLocation;
    protected boolean stateTriggered;
    protected int xTexStart;
    protected int yTexStart;
    protected int xDiffTex;
    protected int yDiffTex;

    public GuiButtonToggle(int buttonId, int xIn, int yIn, int widthIn, int heightIn, boolean buttonText)
    {
        super(buttonId, xIn, yIn, widthIn, heightIn, "");
        this.stateTriggered = buttonText;
    }

    public void initTextureValues(int xTexStartIn, int yTexStartIn, int xDiffTexIn, int yDiffTexIn, ResourceLocation resourceLocationIn)
    {
        this.xTexStart = xTexStartIn;
        this.yTexStart = yTexStartIn;
        this.xDiffTex = xDiffTexIn;
        this.yDiffTex = yDiffTexIn;
        this.resourceLocation = resourceLocationIn;
    }

    public void setStateTriggered(boolean p_191753_1_)
    {
        this.stateTriggered = p_191753_1_;
    }

    public boolean isStateTriggered()
    {
        return this.stateTriggered;
    }

    public void setPosition(int xIn, int yIn)
    {
        this.x = xIn;
        this.y = yIn;
    }

    public void func_191745_a(Minecraft p_191745_1_, int p_191745_2_, int p_191745_3_, float p_191745_4_)
    {
        if (this.visible)
        {
            this.hovered = p_191745_2_ >= this.x && p_191745_3_ >= this.y && p_191745_2_ < this.x + this.width && p_191745_3_ < this.y + this.height;
            p_191745_1_.getTextureManager().bindTexture(this.resourceLocation);
            GlStateManager.disableDepthTest();
            int i = this.xTexStart;
            int j = this.yTexStart;

            if (this.stateTriggered)
            {
                i += this.xDiffTex;
            }

            if (this.hovered)
            {
                j += this.yDiffTex;
            }

            this.drawTexturedModalRect(this.x, this.y, i, j, this.width, this.height);
            GlStateManager.enableDepthTest();
        }
    }
}