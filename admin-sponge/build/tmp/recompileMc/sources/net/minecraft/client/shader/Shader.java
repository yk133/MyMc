package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Matrix4f;

@SideOnly(Side.CLIENT)
public class Shader
{
    private final ShaderManager manager;
    public final Framebuffer framebufferIn;
    public final Framebuffer framebufferOut;
    private final List<Object> listAuxFramebuffers = Lists.<Object>newArrayList();
    private final List<String> listAuxNames = Lists.<String>newArrayList();
    private final List<Integer> listAuxWidths = Lists.<Integer>newArrayList();
    private final List<Integer> listAuxHeights = Lists.<Integer>newArrayList();
    private Matrix4f projectionMatrix;

    public Shader(IResourceManager resourceManager, String programName, Framebuffer framebufferInIn, Framebuffer framebufferOutIn) throws JsonException, IOException
    {
        this.manager = new ShaderManager(resourceManager, programName);
        this.framebufferIn = framebufferInIn;
        this.framebufferOut = framebufferOutIn;
    }

    public void func_148044_b()
    {
        this.manager.func_147988_a();
    }

    public void addAuxFramebuffer(String auxName, Object auxFramebufferIn, int width, int height)
    {
        this.listAuxNames.add(this.listAuxNames.size(), auxName);
        this.listAuxFramebuffers.add(this.listAuxFramebuffers.size(), auxFramebufferIn);
        this.listAuxWidths.add(this.listAuxWidths.size(), Integer.valueOf(width));
        this.listAuxHeights.add(this.listAuxHeights.size(), Integer.valueOf(height));
    }

    private void preRender()
    {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.disableDepthTest();
        GlStateManager.disableAlphaTest();
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableColorMaterial();
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(0);
    }

    public void func_148045_a(Matrix4f p_148045_1_)
    {
        this.projectionMatrix = p_148045_1_;
    }

    public void render(float partialTicks)
    {
        this.preRender();
        this.framebufferIn.unbindFramebuffer();
        float f = (float)this.framebufferOut.framebufferTextureWidth;
        float f1 = (float)this.framebufferOut.framebufferTextureHeight;
        GlStateManager.viewport(0, 0, (int)f, (int)f1);
        this.manager.addSamplerTexture("DiffuseSampler", this.framebufferIn);

        for (int i = 0; i < this.listAuxFramebuffers.size(); ++i)
        {
            this.manager.addSamplerTexture(this.listAuxNames.get(i), this.listAuxFramebuffers.get(i));
            this.manager.func_147984_b("AuxSize" + i).set((float)((Integer)this.listAuxWidths.get(i)).intValue(), (float)((Integer)this.listAuxHeights.get(i)).intValue());
        }

        this.manager.func_147984_b("ProjMat").func_148088_a(this.projectionMatrix);
        this.manager.func_147984_b("InSize").set((float)this.framebufferIn.framebufferTextureWidth, (float)this.framebufferIn.framebufferTextureHeight);
        this.manager.func_147984_b("OutSize").set(f, f1);
        this.manager.func_147984_b("Time").set(partialTicks);
        Minecraft minecraft = Minecraft.getInstance();
        this.manager.func_147984_b("ScreenSize").set((float)minecraft.field_71443_c, (float)minecraft.field_71440_d);
        this.manager.useShader();
        this.framebufferOut.framebufferClear();
        this.framebufferOut.bindFramebuffer(false);
        GlStateManager.depthMask(false);
        GlStateManager.colorMask(true, true, true, true);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(0.0D, (double)f1, 500.0D).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos((double)f, (double)f1, 500.0D).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos((double)f, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
        this.manager.endShader();
        this.framebufferOut.unbindFramebuffer();
        this.framebufferIn.unbindFramebufferTexture();

        for (Object object : this.listAuxFramebuffers)
        {
            if (object instanceof Framebuffer)
            {
                ((Framebuffer)object).unbindFramebufferTexture();
            }
        }
    }

    public ShaderManager getShaderManager()
    {
        return this.manager;
    }
}