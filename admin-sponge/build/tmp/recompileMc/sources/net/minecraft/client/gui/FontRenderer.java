package net.minecraft.client.gui;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

@SideOnly(Side.CLIENT)
public class FontRenderer implements IResourceManagerReloadListener
{
    private static final ResourceLocation[] field_111274_c = new ResourceLocation[256];
    protected final int[] field_78286_d = new int[256];
    /** the height in pixels of default text */
    public int FONT_HEIGHT = 9;
    public Random fontRandom = new Random();
    protected final byte[] field_78287_e = new byte[65536];
    private final int[] field_78285_g = new int[32];
    protected final ResourceLocation field_111273_g;
    /** The RenderEngine used to load and setup glyph textures. */
    private final TextureManager textureManager;
    protected float field_78295_j;
    protected float field_78296_k;
    private boolean field_78293_l;
    /** If true, the Unicode Bidirectional Algorithm should be run before rendering any string. */
    private boolean bidiFlag;
    private float field_78291_n;
    private float field_78292_o;
    private float field_78306_p;
    private float field_78305_q;
    private int field_78304_r;
    private boolean field_78303_s;
    private boolean field_78302_t;
    private boolean field_78301_u;
    private boolean field_78300_v;
    private boolean field_78299_w;

    public FontRenderer(GameSettings p_i1035_1_, ResourceLocation p_i1035_2_, TextureManager p_i1035_3_, boolean p_i1035_4_)
    {
        this.field_111273_g = p_i1035_2_;
        this.textureManager = p_i1035_3_;
        this.field_78293_l = p_i1035_4_;
        bindTexture(this.field_111273_g);

        for (int i = 0; i < 32; ++i)
        {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i >> 0 & 1) * 170 + j;

            if (i == 6)
            {
                k += 85;
            }

            if (p_i1035_1_.field_74337_g)
            {
                int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
                int k1 = (k * 30 + l * 70) / 100;
                int l1 = (k * 30 + i1 * 70) / 100;
                k = j1;
                l = k1;
                i1 = l1;
            }

            if (i >= 16)
            {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }

            this.field_78285_g[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
        }

        this.func_98306_d();
    }

    public void func_110549_a(IResourceManager p_110549_1_)
    {
        this.func_111272_d();
        this.func_98306_d();
    }

    private void func_111272_d()
    {
        IResource iresource = null;
        BufferedImage bufferedimage;

        try
        {
            iresource = getResource(this.field_111273_g);
            bufferedimage = TextureUtil.func_177053_a(iresource.func_110527_b());
        }
        catch (IOException ioexception)
        {
            throw new RuntimeException(ioexception);
        }
        finally
        {
            IOUtils.closeQuietly((Closeable)iresource);
        }

        int lvt_3_2_ = bufferedimage.getWidth();
        int lvt_4_1_ = bufferedimage.getHeight();
        int[] lvt_5_1_ = new int[lvt_3_2_ * lvt_4_1_];
        bufferedimage.getRGB(0, 0, lvt_3_2_, lvt_4_1_, lvt_5_1_, 0, lvt_3_2_);
        int lvt_6_1_ = lvt_4_1_ / 16;
        int lvt_7_1_ = lvt_3_2_ / 16;
        boolean lvt_8_1_ = true;
        float lvt_9_1_ = 8.0F / (float)lvt_7_1_;

        for (int lvt_10_1_ = 0; lvt_10_1_ < 256; ++lvt_10_1_)
        {
            int j1 = lvt_10_1_ % 16;
            int k1 = lvt_10_1_ / 16;

            if (lvt_10_1_ == 32)
            {
                this.field_78286_d[lvt_10_1_] = 4;
            }

            int l1;

            for (l1 = lvt_7_1_ - 1; l1 >= 0; --l1)
            {
                int i2 = j1 * lvt_7_1_ + l1;
                boolean flag1 = true;

                for (int j2 = 0; j2 < lvt_6_1_ && flag1; ++j2)
                {
                    int k2 = (k1 * lvt_7_1_ + j2) * lvt_3_2_;

                    if ((lvt_5_1_[i2 + k2] >> 24 & 255) != 0)
                    {
                        flag1 = false;
                    }
                }

                if (!flag1)
                {
                    break;
                }
            }

            ++l1;
            this.field_78286_d[lvt_10_1_] = (int)(0.5D + (double)((float)l1 * lvt_9_1_)) + 1;
        }
    }

    private void func_98306_d()
    {
        IResource iresource = null;

        try
        {
            iresource = getResource(new ResourceLocation("font/glyph_sizes.bin"));
            iresource.func_110527_b().read(this.field_78287_e);
        }
        catch (IOException ioexception)
        {
            throw new RuntimeException(ioexception);
        }
        finally
        {
            IOUtils.closeQuietly((Closeable)iresource);
        }
    }

    private float func_181559_a(char p_181559_1_, boolean p_181559_2_)
    {
        if (p_181559_1_ == 160) return 4.0F; // forge: display nbsp as space. MC-2595
        if (p_181559_1_ == ' ')
        {
            return 4.0F;
        }
        else
        {
            int i = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(p_181559_1_);
            return i != -1 && !this.field_78293_l ? this.func_78266_a(i, p_181559_2_) : this.func_78277_a(p_181559_1_, p_181559_2_);
        }
    }

    protected float func_78266_a(int p_78266_1_, boolean p_78266_2_)
    {
        int i = p_78266_1_ % 16 * 8;
        int j = p_78266_1_ / 16 * 8;
        int k = p_78266_2_ ? 1 : 0;
        bindTexture(this.field_111273_g);
        int l = this.field_78286_d[p_78266_1_];
        float f = (float)l - 0.01F;
        GlStateManager.func_187447_r(5);
        GlStateManager.func_187426_b((float)i / 128.0F, (float)j / 128.0F);
        GlStateManager.func_187435_e(this.field_78295_j + (float)k, this.field_78296_k, 0.0F);
        GlStateManager.func_187426_b((float)i / 128.0F, ((float)j + 7.99F) / 128.0F);
        GlStateManager.func_187435_e(this.field_78295_j - (float)k, this.field_78296_k + 7.99F, 0.0F);
        GlStateManager.func_187426_b(((float)i + f - 1.0F) / 128.0F, (float)j / 128.0F);
        GlStateManager.func_187435_e(this.field_78295_j + f - 1.0F + (float)k, this.field_78296_k, 0.0F);
        GlStateManager.func_187426_b(((float)i + f - 1.0F) / 128.0F, ((float)j + 7.99F) / 128.0F);
        GlStateManager.func_187435_e(this.field_78295_j + f - 1.0F - (float)k, this.field_78296_k + 7.99F, 0.0F);
        GlStateManager.func_187437_J();
        return (float)l;
    }

    private ResourceLocation func_111271_a(int p_111271_1_)
    {
        if (field_111274_c[p_111271_1_] == null)
        {
            field_111274_c[p_111271_1_] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", p_111271_1_));
        }

        return field_111274_c[p_111271_1_];
    }

    private void func_78257_a(int p_78257_1_)
    {
        bindTexture(this.func_111271_a(p_78257_1_));
    }

    protected float func_78277_a(char p_78277_1_, boolean p_78277_2_)
    {
        int i = this.field_78287_e[p_78277_1_] & 255;

        if (i == 0)
        {
            return 0.0F;
        }
        else
        {
            int j = p_78277_1_ / 256;
            this.func_78257_a(j);
            int k = i >>> 4;
            int l = i & 15;
            float f = (float)k;
            float f1 = (float)(l + 1);
            float f2 = (float)(p_78277_1_ % 16 * 16) + f;
            float f3 = (float)((p_78277_1_ & 255) / 16 * 16);
            float f4 = f1 - f - 0.02F;
            float f5 = p_78277_2_ ? 1.0F : 0.0F;
            GlStateManager.func_187447_r(5);
            GlStateManager.func_187426_b(f2 / 256.0F, f3 / 256.0F);
            GlStateManager.func_187435_e(this.field_78295_j + f5, this.field_78296_k, 0.0F);
            GlStateManager.func_187426_b(f2 / 256.0F, (f3 + 15.98F) / 256.0F);
            GlStateManager.func_187435_e(this.field_78295_j - f5, this.field_78296_k + 7.99F, 0.0F);
            GlStateManager.func_187426_b((f2 + f4) / 256.0F, f3 / 256.0F);
            GlStateManager.func_187435_e(this.field_78295_j + f4 / 2.0F + f5, this.field_78296_k, 0.0F);
            GlStateManager.func_187426_b((f2 + f4) / 256.0F, (f3 + 15.98F) / 256.0F);
            GlStateManager.func_187435_e(this.field_78295_j + f4 / 2.0F - f5, this.field_78296_k + 7.99F, 0.0F);
            GlStateManager.func_187437_J();
            return (f1 - f) / 2.0F + 1.0F;
        }
    }

    /**
     * Draws the specified string with a shadow.
     */
    public int drawStringWithShadow(String text, float x, float y, int color)
    {
        return this.func_175065_a(text, x, y, color, true);
    }

    public int func_78276_b(String p_78276_1_, int p_78276_2_, int p_78276_3_, int p_78276_4_)
    {
        return this.func_175065_a(p_78276_1_, (float)p_78276_2_, (float)p_78276_3_, p_78276_4_, false);
    }

    public int func_175065_a(String p_175065_1_, float p_175065_2_, float p_175065_3_, int p_175065_4_, boolean p_175065_5_)
    {
        enableAlpha();
        this.func_78265_b();
        int i;

        if (p_175065_5_)
        {
            i = this.renderString(p_175065_1_, p_175065_2_ + 1.0F, p_175065_3_ + 1.0F, p_175065_4_, true);
            i = Math.max(i, this.renderString(p_175065_1_, p_175065_2_, p_175065_3_, p_175065_4_, false));
        }
        else
        {
            i = this.renderString(p_175065_1_, p_175065_2_, p_175065_3_, p_175065_4_, false);
        }

        return i;
    }

    /**
     * Apply Unicode Bidirectional Algorithm to string and return a new possibly reordered string for visual rendering.
     */
    private String bidiReorder(String text)
    {
        try
        {
            Bidi bidi = new Bidi((new ArabicShaping(8)).shape(text), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        }
        catch (ArabicShapingException var3)
        {
            return text;
        }
    }

    private void func_78265_b()
    {
        this.field_78303_s = false;
        this.field_78302_t = false;
        this.field_78301_u = false;
        this.field_78300_v = false;
        this.field_78299_w = false;
    }

    private void func_78255_a(String p_78255_1_, boolean p_78255_2_)
    {
        for (int i = 0; i < p_78255_1_.length(); ++i)
        {
            char c0 = p_78255_1_.charAt(i);

            if (c0 == 167 && i + 1 < p_78255_1_.length())
            {
                int i1 = "0123456789abcdefklmnor".indexOf(String.valueOf(p_78255_1_.charAt(i + 1)).toLowerCase(Locale.ROOT).charAt(0));

                if (i1 < 16)
                {
                    this.field_78303_s = false;
                    this.field_78302_t = false;
                    this.field_78299_w = false;
                    this.field_78300_v = false;
                    this.field_78301_u = false;

                    if (i1 < 0 || i1 > 15)
                    {
                        i1 = 15;
                    }

                    if (p_78255_2_)
                    {
                        i1 += 16;
                    }

                    int j1 = this.field_78285_g[i1];
                    this.field_78304_r = j1;
                    setColor((float)(j1 >> 16) / 255.0F, (float)(j1 >> 8 & 255) / 255.0F, (float)(j1 & 255) / 255.0F, this.field_78305_q);
                }
                else if (i1 == 16)
                {
                    this.field_78303_s = true;
                }
                else if (i1 == 17)
                {
                    this.field_78302_t = true;
                }
                else if (i1 == 18)
                {
                    this.field_78299_w = true;
                }
                else if (i1 == 19)
                {
                    this.field_78300_v = true;
                }
                else if (i1 == 20)
                {
                    this.field_78301_u = true;
                }
                else if (i1 == 21)
                {
                    this.field_78303_s = false;
                    this.field_78302_t = false;
                    this.field_78299_w = false;
                    this.field_78300_v = false;
                    this.field_78301_u = false;
                    setColor(this.field_78291_n, this.field_78292_o, this.field_78306_p, this.field_78305_q);
                }

                ++i;
            }
            else
            {
                int j = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(c0);

                if (this.field_78303_s && j != -1)
                {
                    int k = this.func_78263_a(c0);
                    char c1;

                    while (true)
                    {
                        j = this.fontRandom.nextInt("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".length());
                        c1 = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".charAt(j);

                        if (k == this.func_78263_a(c1))
                        {
                            break;
                        }
                    }

                    c0 = c1;
                }

                float f1 = j == -1 || this.field_78293_l ? 0.5f : 1f;
                boolean flag = (c0 == 0 || j == -1 || this.field_78293_l) && p_78255_2_;

                if (flag)
                {
                    this.field_78295_j -= f1;
                    this.field_78296_k -= f1;
                }

                float f = this.func_181559_a(c0, this.field_78301_u);

                if (flag)
                {
                    this.field_78295_j += f1;
                    this.field_78296_k += f1;
                }

                if (this.field_78302_t)
                {
                    this.field_78295_j += f1;

                    if (flag)
                    {
                        this.field_78295_j -= f1;
                        this.field_78296_k -= f1;
                    }

                    this.func_181559_a(c0, this.field_78301_u);
                    this.field_78295_j -= f1;

                    if (flag)
                    {
                        this.field_78295_j += f1;
                        this.field_78296_k += f1;
                    }

                    ++f;
                }
                doDraw(f);
            }
        }
    }

    protected void doDraw(float f)
    {
        {
            {

                if (this.field_78299_w)
                {
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder bufferbuilder = tessellator.getBuffer();
                    GlStateManager.disableTexture2D();
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
                    bufferbuilder.pos((double)this.field_78295_j, (double)(this.field_78296_k + (float)(this.FONT_HEIGHT / 2)), 0.0D).endVertex();
                    bufferbuilder.pos((double)(this.field_78295_j + f), (double)(this.field_78296_k + (float)(this.FONT_HEIGHT / 2)), 0.0D).endVertex();
                    bufferbuilder.pos((double)(this.field_78295_j + f), (double)(this.field_78296_k + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0D).endVertex();
                    bufferbuilder.pos((double)this.field_78295_j, (double)(this.field_78296_k + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0D).endVertex();
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                }

                if (this.field_78300_v)
                {
                    Tessellator tessellator1 = Tessellator.getInstance();
                    BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
                    GlStateManager.disableTexture2D();
                    bufferbuilder1.begin(7, DefaultVertexFormats.POSITION);
                    int l = this.field_78300_v ? -1 : 0;
                    bufferbuilder1.pos((double)(this.field_78295_j + (float)l), (double)(this.field_78296_k + (float)this.FONT_HEIGHT), 0.0D).endVertex();
                    bufferbuilder1.pos((double)(this.field_78295_j + f), (double)(this.field_78296_k + (float)this.FONT_HEIGHT), 0.0D).endVertex();
                    bufferbuilder1.pos((double)(this.field_78295_j + f), (double)(this.field_78296_k + (float)this.FONT_HEIGHT - 1.0F), 0.0D).endVertex();
                    bufferbuilder1.pos((double)(this.field_78295_j + (float)l), (double)(this.field_78296_k + (float)this.FONT_HEIGHT - 1.0F), 0.0D).endVertex();
                    tessellator1.draw();
                    GlStateManager.enableTexture2D();
                }

                this.field_78295_j += (float)((int)f);
            }
        }
    }

    private int func_78274_b(String p_78274_1_, int p_78274_2_, int p_78274_3_, int p_78274_4_, int p_78274_5_, boolean p_78274_6_)
    {
        if (this.bidiFlag)
        {
            int i = this.getStringWidth(this.bidiReorder(p_78274_1_));
            p_78274_2_ = p_78274_2_ + p_78274_4_ - i;
        }

        return this.renderString(p_78274_1_, (float)p_78274_2_, (float)p_78274_3_, p_78274_5_, p_78274_6_);
    }

    /**
     * Render single line string by setting GL color, current (posX,posY), and calling renderStringAtPos()
     */
    private int renderString(String text, float x, float y, int color, boolean dropShadow)
    {
        if (text == null)
        {
            return 0;
        }
        else
        {
            if (this.bidiFlag)
            {
                text = this.bidiReorder(text);
            }

            if ((color & -67108864) == 0)
            {
                color |= -16777216;
            }

            if (dropShadow)
            {
                color = (color & 16579836) >> 2 | color & -16777216;
            }

            this.field_78291_n = (float)(color >> 16 & 255) / 255.0F;
            this.field_78292_o = (float)(color >> 8 & 255) / 255.0F;
            this.field_78306_p = (float)(color & 255) / 255.0F;
            this.field_78305_q = (float)(color >> 24 & 255) / 255.0F;
            setColor(this.field_78291_n, this.field_78292_o, this.field_78306_p, this.field_78305_q);
            this.field_78295_j = x;
            this.field_78296_k = y;
            this.func_78255_a(text, dropShadow);
            return (int)this.field_78295_j;
        }
    }

    /**
     * Returns the width of this string. Equivalent of FontMetrics.stringWidth(String s).
     */
    public int getStringWidth(String text)
    {
        if (text == null)
        {
            return 0;
        }
        else
        {
            int i = 0;
            boolean flag = false;

            for (int j = 0; j < text.length(); ++j)
            {
                char c0 = text.charAt(j);
                int k = this.func_78263_a(c0);

                if (k < 0 && j < text.length() - 1)
                {
                    ++j;
                    c0 = text.charAt(j);

                    if (c0 != 'l' && c0 != 'L')
                    {
                        if (c0 == 'r' || c0 == 'R')
                        {
                            flag = false;
                        }
                    }
                    else
                    {
                        flag = true;
                    }

                    k = 0;
                }

                i += k;

                if (flag && k > 0)
                {
                    ++i;
                }
            }

            return i;
        }
    }

    public int func_78263_a(char p_78263_1_)
    {
        if (p_78263_1_ == 160) return 4; // forge: display nbsp as space. MC-2595
        if (p_78263_1_ == 167)
        {
            return -1;
        }
        else if (p_78263_1_ == ' ')
        {
            return 4;
        }
        else
        {
            int i = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(p_78263_1_);

            if (p_78263_1_ > 0 && i != -1 && !this.field_78293_l)
            {
                return this.field_78286_d[i];
            }
            else if (this.field_78287_e[p_78263_1_] != 0)
            {
                int j = this.field_78287_e[p_78263_1_] & 255;
                int k = j >>> 4;
                int l = j & 15;
                ++l;
                return (l - k) / 2 + 1;
            }
            else
            {
                return 0;
            }
        }
    }

    /**
     * Trims a string to fit a specified Width.
     */
    public String trimStringToWidth(String text, int width)
    {
        return this.trimStringToWidth(text, width, false);
    }

    /**
     * Trims a string to a specified width, optionally starting from the end and working backwards.
     * <h3>Samples:</h3>
     * (Assuming that {@link #getCharWidth(char)} returns <code>6</code> for all of the characters in
     * <code>0123456789</code> on the current resource pack)
     * <table>
     * <tr><th>Input</th><th>Returns</th></tr>
     * <tr><td><code>trimStringToWidth("0123456789", 1, false)</code></td><td><samp>""</samp></td></tr>
     * <tr><td><code>trimStringToWidth("0123456789", 6, false)</code></td><td><samp>"0"</samp></td></tr>
     * <tr><td><code>trimStringToWidth("0123456789", 29, false)</code></td><td><samp>"0123"</samp></td></tr>
     * <tr><td><code>trimStringToWidth("0123456789", 30, false)</code></td><td><samp>"01234"</samp></td></tr>
     * <tr><td><code>trimStringToWidth("0123456789", 9001, false)</code></td><td><samp>"0123456789"</samp></td></tr>
     * <tr><td><code>trimStringToWidth("0123456789", 1, true)</code></td><td><samp>""</samp></td></tr>
     * <tr><td><code>trimStringToWidth("0123456789", 6, true)</code></td><td><samp>"9"</samp></td></tr>
     * <tr><td><code>trimStringToWidth("0123456789", 29, true)</code></td><td><samp>"6789"</samp></td></tr>
     * <tr><td><code>trimStringToWidth("0123456789", 30, true)</code></td><td><samp>"56789"</samp></td></tr>
     * <tr><td><code>trimStringToWidth("0123456789", 9001, true)</code></td><td><samp>"0123456789"</samp></td></tr>
     * </table>
     */
    public String trimStringToWidth(String text, int width, boolean reverse)
    {
        StringBuilder stringbuilder = new StringBuilder();
        int i = 0;
        int j = reverse ? text.length() - 1 : 0;
        int k = reverse ? -1 : 1;
        boolean flag = false;
        boolean flag1 = false;

        for (int l = j; l >= 0 && l < text.length() && i < width; l += k)
        {
            char c0 = text.charAt(l);
            int i1 = this.func_78263_a(c0);

            if (flag)
            {
                flag = false;

                if (c0 != 'l' && c0 != 'L')
                {
                    if (c0 == 'r' || c0 == 'R')
                    {
                        flag1 = false;
                    }
                }
                else
                {
                    flag1 = true;
                }
            }
            else if (i1 < 0)
            {
                flag = true;
            }
            else
            {
                i += i1;

                if (flag1)
                {
                    ++i;
                }
            }

            if (i > width)
            {
                break;
            }

            if (reverse)
            {
                stringbuilder.insert(0, c0);
            }
            else
            {
                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString();
    }

    /**
     * Remove all newline characters from the end of the string
     */
    private String trimStringNewline(String text)
    {
        while (text != null && text.endsWith("\n"))
        {
            text = text.substring(0, text.length() - 1);
        }

        return text;
    }

    /**
     * Splits and draws a String with wordwrap (maximum length is parameter k)
     */
    public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor)
    {
        this.func_78265_b();
        this.field_78304_r = textColor;
        str = this.trimStringNewline(str);
        this.func_78268_b(str, x, y, wrapWidth, false);
    }

    private void func_78268_b(String p_78268_1_, int p_78268_2_, int p_78268_3_, int p_78268_4_, boolean p_78268_5_)
    {
        for (String s : this.listFormattedStringToWidth(p_78268_1_, p_78268_4_))
        {
            this.func_78274_b(s, p_78268_2_, p_78268_3_, p_78268_4_, this.field_78304_r, p_78268_5_);
            p_78268_3_ += this.FONT_HEIGHT;
        }
    }

    /**
     * Returns the height (in pixels) of the given string if it is wordwrapped to the given max width.
     */
    public int getWordWrappedHeight(String str, int maxLength)
    {
        return this.FONT_HEIGHT * this.listFormattedStringToWidth(str, maxLength).size();
    }

    public void func_78264_a(boolean p_78264_1_)
    {
        this.field_78293_l = p_78264_1_;
    }

    public boolean func_82883_a()
    {
        return this.field_78293_l;
    }

    /**
     * Set bidiFlag to control if the Unicode Bidirectional Algorithm should be run before rendering any string.
     */
    public void setBidiFlag(boolean bidiFlagIn)
    {
        this.bidiFlag = bidiFlagIn;
    }

    /**
     * Breaks a string into a list of pieces where the width of each line is always less than or equal to the provided
     * width. Formatting codes will be preserved between lines.
     */
    public List<String> listFormattedStringToWidth(String str, int wrapWidth)
    {
        return Arrays.<String>asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
    }

    /**
     * Inserts newline and formatting into a string to wrap it within the specified width.
     */
    String wrapFormattedStringToWidth(String str, int wrapWidth)
    {
        int i = this.sizeStringToWidth(str, wrapWidth);

        if (str.length() <= i)
        {
            return str;
        }
        else
        {
            String s = str.substring(0, i);
            char c0 = str.charAt(i);
            boolean flag = c0 == ' ' || c0 == '\n';
            String s1 = func_78282_e(s) + str.substring(i + (flag ? 1 : 0));
            return s + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth);
        }
    }

    /**
     * Determines how many characters from the string will fit into the specified width.
     */
    private int sizeStringToWidth(String str, int wrapWidth)
    {
        int i = str.length();
        int j = 0;
        int k = 0;
        int l = -1;

        for (boolean flag = false; k < i; ++k)
        {
            char c0 = str.charAt(k);

            switch (c0)
            {
                case '\n':
                    --k;
                    break;
                case ' ':
                    l = k;
                default:
                    j += this.func_78263_a(c0);

                    if (flag)
                    {
                        ++j;
                    }

                    break;
                case '\u00a7':

                    if (k < i - 1)
                    {
                        ++k;
                        char c1 = str.charAt(k);

                        if (c1 != 'l' && c1 != 'L')
                        {
                            if (c1 == 'r' || c1 == 'R' || func_78272_b(c1))
                            {
                                flag = false;
                            }
                        }
                        else
                        {
                            flag = true;
                        }
                    }
            }

            if (c0 == '\n')
            {
                ++k;
                l = k;
                break;
            }

            if (j > wrapWidth)
            {
                break;
            }
        }

        return k != i && l != -1 && l < k ? l : k;
    }

    private static boolean func_78272_b(char p_78272_0_)
    {
        return p_78272_0_ >= '0' && p_78272_0_ <= '9' || p_78272_0_ >= 'a' && p_78272_0_ <= 'f' || p_78272_0_ >= 'A' && p_78272_0_ <= 'F';
    }

    private static boolean func_78270_c(char p_78270_0_)
    {
        return p_78270_0_ >= 'k' && p_78270_0_ <= 'o' || p_78270_0_ >= 'K' && p_78270_0_ <= 'O' || p_78270_0_ == 'r' || p_78270_0_ == 'R';
    }

    public static String func_78282_e(String p_78282_0_)
    {
        String s = "";
        int i = -1;
        int j = p_78282_0_.length();

        while ((i = p_78282_0_.indexOf(167, i + 1)) != -1)
        {
            if (i < j - 1)
            {
                char c0 = p_78282_0_.charAt(i + 1);

                if (func_78272_b(c0))
                {
                    s = "\u00a7" + c0;
                }
                else if (func_78270_c(c0))
                {
                    s = s + "\u00a7" + c0;
                }
            }
        }

        return s;
    }

    /**
     * Get bidiFlag that controls if the Unicode Bidirectional Algorithm should be run before rendering any string
     */
    public boolean getBidiFlag()
    {
        return this.bidiFlag;
    }

    protected void setColor(float r, float g, float b, float a)
    {
        GlStateManager.color4f(r,g,b,a);
    }

    protected void enableAlpha()
    {
        GlStateManager.enableAlphaTest();
    }

    protected void bindTexture(ResourceLocation location)
    {
        textureManager.bindTexture(location);
    }

    protected IResource getResource(ResourceLocation location) throws IOException
    {
        return Minecraft.getInstance().func_110442_L().func_110536_a(location);
    }

    public int func_175064_b(char p_175064_1_)
    {
        int i = "0123456789abcdef".indexOf(p_175064_1_);
        return i >= 0 && i < this.field_78285_g.length ? this.field_78285_g[i] : -1;
    }
}