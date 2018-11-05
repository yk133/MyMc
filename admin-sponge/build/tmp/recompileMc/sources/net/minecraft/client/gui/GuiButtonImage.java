package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonImage extends GuiButton
{
    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffText;

    public GuiButtonImage(int buttonId, int xIn, int yIn, int widthIn, int heightIn, int textureOffestX, int textureOffestY, int p_i47392_8_, ResourceLocation resource)
    {
        super(buttonId, xIn, yIn, widthIn, heightIn, "");
        this.xTexStart = textureOffestX;
        this.yTexStart = textureOffestY;
        this.yDiffText = p_i47392_8_;
        this.resourceLocation = resource;
    }

    public void setPosition(int p_191746_1_, int p_191746_2_)
    {
        this.x = p_191746_1_;
        this.y = p_191746_2_;
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

            if (this.hovered)
            {
                j += this.yDiffText;
            }

            this.drawTexturedModalRect(this.x, this.y, i, j, this.width, this.height);
            GlStateManager.enableDepthTest();
        }
    }
}