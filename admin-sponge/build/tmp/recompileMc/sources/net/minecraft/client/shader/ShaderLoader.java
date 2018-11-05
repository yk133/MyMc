package net.minecraft.client.shader;

import com.google.common.collect.Maps;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;

@SideOnly(Side.CLIENT)
public class ShaderLoader
{
    private final ShaderLoader.ShaderType shaderType;
    private final String shaderFilename;
    private final int shader;
    private int shaderAttachCount;

    private ShaderLoader(ShaderLoader.ShaderType type, int shaderId, String filename)
    {
        this.shaderType = type;
        this.shader = shaderId;
        this.shaderFilename = filename;
    }

    public void attachShader(ShaderManager manager)
    {
        ++this.shaderAttachCount;
        OpenGlHelper.glAttachShader(manager.getProgram(), this.shader);
    }

    public void func_148054_b(ShaderManager p_148054_1_)
    {
        --this.shaderAttachCount;

        if (this.shaderAttachCount <= 0)
        {
            OpenGlHelper.glDeleteShader(this.shader);
            this.shaderType.getLoadedShaders().remove(this.shaderFilename);
        }
    }

    public String getShaderFilename()
    {
        return this.shaderFilename;
    }

    public static ShaderLoader func_148057_a(IResourceManager p_148057_0_, ShaderLoader.ShaderType p_148057_1_, String p_148057_2_) throws IOException
    {
        ShaderLoader shaderloader = (ShaderLoader)p_148057_1_.getLoadedShaders().get(p_148057_2_);

        if (shaderloader == null)
        {
            String[] rl = ResourceLocation.func_177516_a(p_148057_2_);
            ResourceLocation resourcelocation = new ResourceLocation(rl[0], "shaders/program/" + rl[1] + p_148057_1_.getShaderExtension());
            IResource iresource = p_148057_0_.func_110536_a(resourcelocation);

            try
            {
                byte[] abyte = IOUtils.toByteArray(new BufferedInputStream(iresource.func_110527_b()));
                ByteBuffer bytebuffer = BufferUtils.createByteBuffer(abyte.length);
                bytebuffer.put(abyte);
                bytebuffer.position(0);
                int i = OpenGlHelper.glCreateShader(p_148057_1_.getShaderMode());
                OpenGlHelper.func_153169_a(i, bytebuffer);
                OpenGlHelper.glCompileShader(i);

                if (OpenGlHelper.glGetShaderi(i, OpenGlHelper.GL_COMPILE_STATUS) == 0)
                {
                    String s = StringUtils.trim(OpenGlHelper.glGetShaderInfoLog(i, 32768));
                    JsonException jsonexception = new JsonException("Couldn't compile " + p_148057_1_.getShaderName() + " program: " + s);
                    jsonexception.setFilenameAndFlush(resourcelocation.getPath());
                    throw jsonexception;
                }

                shaderloader = new ShaderLoader(p_148057_1_, i, p_148057_2_);
                p_148057_1_.getLoadedShaders().put(p_148057_2_, shaderloader);
            }
            finally
            {
                IOUtils.closeQuietly((Closeable)iresource);
            }
        }

        return shaderloader;
    }

    @SideOnly(Side.CLIENT)
    public static enum ShaderType
    {
        VERTEX("vertex", ".vsh", OpenGlHelper.GL_VERTEX_SHADER),
        FRAGMENT("fragment", ".fsh", OpenGlHelper.GL_FRAGMENT_SHADER);

        private final String shaderName;
        private final String shaderExtension;
        private final int shaderMode;
        private final Map<String, ShaderLoader> loadedShaders = Maps.<String, ShaderLoader>newHashMap();

        private ShaderType(String shaderNameIn, String shaderExtensionIn, int shaderModeIn)
        {
            this.shaderName = shaderNameIn;
            this.shaderExtension = shaderExtensionIn;
            this.shaderMode = shaderModeIn;
        }

        public String getShaderName()
        {
            return this.shaderName;
        }

        private String getShaderExtension()
        {
            return this.shaderExtension;
        }

        private int getShaderMode()
        {
            return this.shaderMode;
        }

        /**
         * gets a map of loaded shaders for the ShaderType.
         */
        private Map<String, ShaderLoader> getLoadedShaders()
        {
            return this.loadedShaders;
        }
    }
}