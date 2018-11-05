package net.minecraft.client.renderer;

import java.awt.image.BufferedImage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IImageBuffer
{
    BufferedImage func_78432_a(BufferedImage p_78432_1_);

    void skinAvailable();
}