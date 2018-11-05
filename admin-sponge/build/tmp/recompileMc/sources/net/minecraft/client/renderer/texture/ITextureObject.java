package net.minecraft.client.renderer.texture;

import java.io.IOException;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ITextureObject
{
    void setBlurMipmap(boolean blurIn, boolean mipmapIn);

    void restoreLastBlurMipmap();

    void func_110551_a(IResourceManager p_110551_1_) throws IOException;

    int getGlTextureId();
}