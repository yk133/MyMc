package net.minecraft.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MinecraftError;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LoadingScreenRenderer implements IProgressUpdate
{
    private String field_73727_a = "";
    private final Minecraft field_73725_b;
    private String field_73726_c = "";
    private long field_73723_d = Minecraft.func_71386_F();
    private boolean field_73724_e;
    private final ScaledResolution field_146587_f;
    private final Framebuffer field_146588_g;

    public LoadingScreenRenderer(Minecraft p_i1017_1_)
    {
        this.field_73725_b = p_i1017_1_;
        this.field_146587_f = new ScaledResolution(p_i1017_1_);
        this.field_146588_g = new Framebuffer(p_i1017_1_.field_71443_c, p_i1017_1_.field_71440_d, false);
        this.field_146588_g.setFramebufferFilter(9728);
    }

    public void func_73721_b(String p_73721_1_)
    {
        this.field_73724_e = false;
        this.func_73722_d(p_73721_1_);
    }

    public void func_73720_a(String p_73720_1_)
    {
        this.field_73724_e = true;
        this.func_73722_d(p_73720_1_);
    }

    private void func_73722_d(String p_73722_1_)
    {
        this.field_73726_c = p_73722_1_;

        if (!this.field_73725_b.running)
        {
            if (!this.field_73724_e)
            {
                throw new MinecraftError();
            }
        }
        else
        {
            GlStateManager.clear(256);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();

            if (OpenGlHelper.isFramebufferEnabled())
            {
                int i = this.field_146587_f.func_78325_e();
                GlStateManager.ortho(0.0D, (double)(this.field_146587_f.func_78326_a() * i), (double)(this.field_146587_f.func_78328_b() * i), 0.0D, 100.0D, 300.0D);
            }
            else
            {
                ScaledResolution scaledresolution = new ScaledResolution(this.field_73725_b);
                GlStateManager.ortho(0.0D, scaledresolution.func_78327_c(), scaledresolution.func_78324_d(), 0.0D, 100.0D, 300.0D);
            }

            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translatef(0.0F, 0.0F, -200.0F);
        }
    }

    public void func_73719_c(String p_73719_1_)
    {
        if (!this.field_73725_b.running)
        {
            if (!this.field_73724_e)
            {
                throw new MinecraftError();
            }
        }
        else
        {
            this.field_73723_d = 0L;
            this.field_73727_a = p_73719_1_;
            this.setLoadingProgress(-1);
            this.field_73723_d = 0L;
        }
    }

    /**
     * Updates the progress bar on the loading screen to the specified amount.
     */
    public void setLoadingProgress(int progress)
    {
        if (!this.field_73725_b.running)
        {
            if (!this.field_73724_e)
            {
                throw new MinecraftError();
            }
        }
        else
        {
            long i = Minecraft.func_71386_F();

            if (i - this.field_73723_d >= 100L)
            {
                this.field_73723_d = i;
                ScaledResolution scaledresolution = new ScaledResolution(this.field_73725_b);
                int j = scaledresolution.func_78325_e();
                int k = scaledresolution.func_78326_a();
                int l = scaledresolution.func_78328_b();

                if (OpenGlHelper.isFramebufferEnabled())
                {
                    this.field_146588_g.framebufferClear();
                }
                else
                {
                    GlStateManager.clear(256);
                }

                this.field_146588_g.bindFramebuffer(false);
                GlStateManager.matrixMode(5889);
                GlStateManager.loadIdentity();
                GlStateManager.ortho(0.0D, scaledresolution.func_78327_c(), scaledresolution.func_78324_d(), 0.0D, 100.0D, 300.0D);
                GlStateManager.matrixMode(5888);
                GlStateManager.loadIdentity();
                GlStateManager.translatef(0.0F, 0.0F, -200.0F);

                if (!OpenGlHelper.isFramebufferEnabled())
                {
                    GlStateManager.clear(16640);
                }

                try
                {
                if (!net.minecraftforge.fml.client.FMLClientHandler.instance().handleLoadingScreen(scaledresolution)) //FML Don't render while FML's pre-screen is rendering
                {
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                this.field_73725_b.getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);
                float f = 32.0F;
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos(0.0D, (double)l, 0.0D).tex(0.0D, (double)((float)l / 32.0F)).color(64, 64, 64, 255).endVertex();
                bufferbuilder.pos((double)k, (double)l, 0.0D).tex((double)((float)k / 32.0F), (double)((float)l / 32.0F)).color(64, 64, 64, 255).endVertex();
                bufferbuilder.pos((double)k, 0.0D, 0.0D).tex((double)((float)k / 32.0F), 0.0D).color(64, 64, 64, 255).endVertex();
                bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(64, 64, 64, 255).endVertex();
                tessellator.draw();

                if (progress >= 0)
                {
                    int i1 = 100;
                    int j1 = 2;
                    int k1 = k / 2 - 50;
                    int l1 = l / 2 + 16;
                    GlStateManager.disableTexture2D();
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    bufferbuilder.pos((double)k1, (double)l1, 0.0D).color(128, 128, 128, 255).endVertex();
                    bufferbuilder.pos((double)k1, (double)(l1 + 2), 0.0D).color(128, 128, 128, 255).endVertex();
                    bufferbuilder.pos((double)(k1 + 100), (double)(l1 + 2), 0.0D).color(128, 128, 128, 255).endVertex();
                    bufferbuilder.pos((double)(k1 + 100), (double)l1, 0.0D).color(128, 128, 128, 255).endVertex();
                    bufferbuilder.pos((double)k1, (double)l1, 0.0D).color(128, 255, 128, 255).endVertex();
                    bufferbuilder.pos((double)k1, (double)(l1 + 2), 0.0D).color(128, 255, 128, 255).endVertex();
                    bufferbuilder.pos((double)(k1 + progress), (double)(l1 + 2), 0.0D).color(128, 255, 128, 255).endVertex();
                    bufferbuilder.pos((double)(k1 + progress), (double)l1, 0.0D).color(128, 255, 128, 255).endVertex();
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                }

                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                this.field_73725_b.fontRenderer.drawStringWithShadow(this.field_73726_c, (float)((k - this.field_73725_b.fontRenderer.getStringWidth(this.field_73726_c)) / 2), (float)(l / 2 - 4 - 16), 16777215);
                this.field_73725_b.fontRenderer.drawStringWithShadow(this.field_73727_a, (float)((k - this.field_73725_b.fontRenderer.getStringWidth(this.field_73727_a)) / 2), (float)(l / 2 - 4 + 8), 16777215);
                }
                }
                catch (java.io.IOException e)
                {
                    throw new RuntimeException(e);
                } //FML End
                this.field_146588_g.unbindFramebuffer();

                if (OpenGlHelper.isFramebufferEnabled())
                {
                    this.field_146588_g.framebufferRender(k * j, l * j);
                }

                this.field_73725_b.func_175601_h();

                try
                {
                    Thread.yield();
                }
                catch (Exception var15)
                {
                    ;
                }
            }
        }
    }

    public void setDoneWorking()
    {
    }
}