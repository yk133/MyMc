package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class LayeredTexture extends AbstractTexture
{
    private static final Logger LOGGER = LogManager.getLogger();
    public final List<String> layeredTextureNames;

    public LayeredTexture(String... textureNames)
    {
        this.layeredTextureNames = Lists.newArrayList(textureNames);
    }

    public void func_110551_a(IResourceManager p_110551_1_) throws IOException
    {
        this.deleteGlTexture();
        BufferedImage bufferedimage = null;

        for (String s : this.layeredTextureNames)
        {
            IResource iresource = null;

            try
            {
                if (s != null)
                {
                    iresource = p_110551_1_.func_110536_a(new ResourceLocation(s));
                    BufferedImage bufferedimage1 = TextureUtil.func_177053_a(iresource.func_110527_b());

                    if (bufferedimage == null)
                    {
                        bufferedimage = new BufferedImage(bufferedimage1.getWidth(), bufferedimage1.getHeight(), 2);
                    }

                    bufferedimage.getGraphics().drawImage(bufferedimage1, 0, 0, (ImageObserver)null);
                }

                continue;
            }
            catch (IOException ioexception)
            {
                LOGGER.error("Couldn't load layered image", (Throwable)ioexception);
            }
            finally
            {
                IOUtils.closeQuietly((Closeable)iresource);
            }

            return;
        }

        TextureUtil.func_110987_a(this.getGlTextureId(), bufferedimage);
    }
}