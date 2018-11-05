package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ColorizerGrass;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GrassColorReloadListener implements IResourceManagerReloadListener
{
    private static final ResourceLocation GRASS_LOCATION = new ResourceLocation("textures/colormap/grass.png");

    public void func_110549_a(IResourceManager p_110549_1_)
    {
        try
        {
            ColorizerGrass.setGrassBiomeColorizer(TextureUtil.func_110986_a(p_110549_1_, GRASS_LOCATION));
        }
        catch (IOException var3)
        {
            ;
        }
    }
}