package net.minecraft.client.renderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class ThreadDownloadImageData extends SimpleTexture
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger TEXTURE_DOWNLOADER_THREAD_ID = new AtomicInteger(0);
    @Nullable
    private final File cacheFile;
    private final String imageUrl;
    @Nullable
    private final IImageBuffer imageBuffer;
    @Nullable
    private BufferedImage field_110560_d;
    @Nullable
    private Thread imageThread;
    private boolean textureUploaded;

    public ThreadDownloadImageData(@Nullable File cacheFileIn, String imageUrlIn, ResourceLocation textureResourceLocation, @Nullable IImageBuffer imageBufferIn)
    {
        super(textureResourceLocation);
        this.cacheFile = cacheFileIn;
        this.imageUrl = imageUrlIn;
        this.imageBuffer = imageBufferIn;
    }

    private void func_147640_e()
    {
        if (!this.textureUploaded)
        {
            if (this.field_110560_d != null)
            {
                if (this.textureLocation != null)
                {
                    this.deleteGlTexture();
                }

                TextureUtil.func_110987_a(super.getGlTextureId(), this.field_110560_d);
                this.textureUploaded = true;
            }
        }
    }

    public int getGlTextureId()
    {
        this.func_147640_e();
        return super.getGlTextureId();
    }

    public void func_147641_a(BufferedImage p_147641_1_)
    {
        this.field_110560_d = p_147641_1_;

        if (this.imageBuffer != null)
        {
            this.imageBuffer.skinAvailable();
        }
    }

    public void func_110551_a(IResourceManager p_110551_1_) throws IOException
    {
        if (this.field_110560_d == null && this.textureLocation != null)
        {
            super.func_110551_a(p_110551_1_);
        }

        if (this.imageThread == null)
        {
            if (this.cacheFile != null && this.cacheFile.isFile())
            {
                LOGGER.debug("Loading http texture from local cache ({})", (Object)this.cacheFile);

                try
                {
                    this.field_110560_d = ImageIO.read(this.cacheFile);

                    if (this.imageBuffer != null)
                    {
                        this.func_147641_a(this.imageBuffer.func_78432_a(this.field_110560_d));
                    }
                }
                catch (IOException ioexception)
                {
                    LOGGER.error("Couldn't load skin {}", this.cacheFile, ioexception);
                    this.loadTextureFromServer();
                }
            }
            else
            {
                this.loadTextureFromServer();
            }
        }
    }

    protected void loadTextureFromServer()
    {
        this.imageThread = new Thread("Texture Downloader #" + TEXTURE_DOWNLOADER_THREAD_ID.incrementAndGet())
        {
            public void run()
            {
                HttpURLConnection httpurlconnection = null;
                ThreadDownloadImageData.LOGGER.debug("Downloading http texture from {} to {}", ThreadDownloadImageData.this.imageUrl, ThreadDownloadImageData.this.cacheFile);

                try
                {
                    httpurlconnection = (HttpURLConnection)(new URL(ThreadDownloadImageData.this.imageUrl)).openConnection(Minecraft.getInstance().getProxy());
                    httpurlconnection.setDoInput(true);
                    httpurlconnection.setDoOutput(false);
                    httpurlconnection.connect();

                    if (httpurlconnection.getResponseCode() / 100 == 2)
                    {
                        BufferedImage bufferedimage;

                        if (ThreadDownloadImageData.this.cacheFile != null)
                        {
                            FileUtils.copyInputStreamToFile(httpurlconnection.getInputStream(), ThreadDownloadImageData.this.cacheFile);
                            bufferedimage = ImageIO.read(ThreadDownloadImageData.this.cacheFile);
                        }
                        else
                        {
                            bufferedimage = TextureUtil.func_177053_a(httpurlconnection.getInputStream());
                        }

                        if (ThreadDownloadImageData.this.imageBuffer != null)
                        {
                            bufferedimage = ThreadDownloadImageData.this.imageBuffer.func_78432_a(bufferedimage);
                        }

                        ThreadDownloadImageData.this.func_147641_a(bufferedimage);
                        return;
                    }
                }
                catch (Exception exception)
                {
                    ThreadDownloadImageData.LOGGER.error("Couldn't download http texture", (Throwable)exception);
                    return;
                }
                finally
                {
                    if (httpurlconnection != null)
                    {
                        httpurlconnection.disconnect();
                    }
                }
            }
        };
        this.imageThread.setDaemon(true);
        this.imageThread.start();
    }
}