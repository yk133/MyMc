package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureAtlasSprite
{
    private final String iconName;
    protected List<int[][]> field_110976_a = Lists.<int[][]>newArrayList();
    protected int[][] interpolatedFrameData;
    private AnimationMetadataSection animationMetadata;
    protected boolean rotated;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    private float minU;
    private float maxU;
    private float minV;
    private float maxV;
    protected int frameCounter;
    protected int tickCounter;

    protected TextureAtlasSprite(String p_i1282_1_)
    {
        this.iconName = p_i1282_1_;
    }

    protected static TextureAtlasSprite func_176604_a(ResourceLocation p_176604_0_)
    {
        return new TextureAtlasSprite(p_176604_0_.toString());
    }

    public void initSprite(int inX, int inY, int originInX, int originInY, boolean rotatedIn)
    {
        this.x = originInX;
        this.y = originInY;
        this.rotated = rotatedIn;
        this.minU = (float)originInX / (float)inX;
        this.maxU = (float)(originInX + this.width) / (float)inX;
        this.minV = (float)originInY / (float)inY;
        this.maxV = (float)(originInY + this.height) / (float)inY;
    }

    public void func_94217_a(TextureAtlasSprite p_94217_1_)
    {
        this.x = p_94217_1_.x;
        this.y = p_94217_1_.y;
        this.width = p_94217_1_.width;
        this.height = p_94217_1_.height;
        this.rotated = p_94217_1_.rotated;
        this.minU = p_94217_1_.minU;
        this.maxU = p_94217_1_.maxU;
        this.minV = p_94217_1_.minV;
        this.maxV = p_94217_1_.maxV;
    }

    public int func_130010_a()
    {
        return this.x;
    }

    public int func_110967_i()
    {
        return this.y;
    }

    /**
     * Returns the width of the icon, in pixels.
     */
    public int getWidth()
    {
        return this.width;
    }

    /**
     * Returns the height of the icon, in pixels.
     */
    public int getHeight()
    {
        return this.height;
    }

    /**
     * Returns the minimum U coordinate to use when rendering with this icon.
     */
    public float getMinU()
    {
        return this.minU;
    }

    /**
     * Returns the maximum U coordinate to use when rendering with this icon.
     */
    public float getMaxU()
    {
        return this.maxU;
    }

    /**
     * Gets a U coordinate on the icon. 0 returns uMin and 16 returns uMax. Other arguments return in-between values.
     */
    public float getInterpolatedU(double u)
    {
        float f = this.maxU - this.minU;
        return this.minU + f * (float)u / 16.0F;
    }

    /**
     * The opposite of getInterpolatedU. Takes the return value of that method and returns the input to it.
     */
    public float getUnInterpolatedU(float u)
    {
        float f = this.maxU - this.minU;
        return (u - this.minU) / f * 16.0F;
    }

    /**
     * Returns the minimum V coordinate to use when rendering with this icon.
     */
    public float getMinV()
    {
        return this.minV;
    }

    /**
     * Returns the maximum V coordinate to use when rendering with this icon.
     */
    public float getMaxV()
    {
        return this.maxV;
    }

    /**
     * Gets a V coordinate on the icon. 0 returns vMin and 16 returns vMax. Other arguments return in-between values.
     */
    public float getInterpolatedV(double v)
    {
        float f = this.maxV - this.minV;
        return this.minV + f * (float)v / 16.0F;
    }

    /**
     * The opposite of getInterpolatedV. Takes the return value of that method and returns the input to it.
     */
    public float getUnInterpolatedV(float v)
    {
        float f = this.maxV - this.minV;
        return (v - this.minV) / f * 16.0F;
    }

    public String func_94215_i()
    {
        return this.iconName;
    }

    public void updateAnimation()
    {
        ++this.tickCounter;

        if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter))
        {
            int i = this.animationMetadata.getFrameIndex(this.frameCounter);
            int j = this.animationMetadata.getFrameCount() == 0 ? this.field_110976_a.size() : this.animationMetadata.getFrameCount();
            this.frameCounter = (this.frameCounter + 1) % j;
            this.tickCounter = 0;
            int k = this.animationMetadata.getFrameIndex(this.frameCounter);

            if (i != k && k >= 0 && k < this.field_110976_a.size())
            {
                TextureUtil.func_147955_a(this.field_110976_a.get(k), this.width, this.height, this.x, this.y, false, false);
            }
        }
        else if (this.animationMetadata.isInterpolate())
        {
            this.updateAnimationInterpolated();
        }
    }

    private void updateAnimationInterpolated()
    {
        double d0 = 1.0D - (double)this.tickCounter / (double)this.animationMetadata.getFrameTimeSingle(this.frameCounter);
        int i = this.animationMetadata.getFrameIndex(this.frameCounter);
        int j = this.animationMetadata.getFrameCount() == 0 ? this.field_110976_a.size() : this.animationMetadata.getFrameCount();
        int k = this.animationMetadata.getFrameIndex((this.frameCounter + 1) % j);

        if (i != k && k >= 0 && k < this.field_110976_a.size())
        {
            int[][] aint = this.field_110976_a.get(i);
            int[][] aint1 = this.field_110976_a.get(k);

            if (this.interpolatedFrameData == null || this.interpolatedFrameData.length != aint.length)
            {
                this.interpolatedFrameData = new int[aint.length][];
            }

            for (int l = 0; l < aint.length; ++l)
            {
                if (this.interpolatedFrameData[l] == null)
                {
                    this.interpolatedFrameData[l] = new int[aint[l].length];
                }

                if (l < aint1.length && aint1[l].length == aint[l].length)
                {
                    for (int i1 = 0; i1 < aint[l].length; ++i1)
                    {
                        int j1 = aint[l][i1];
                        int k1 = aint1[l][i1];
                        int l1 = this.interpolateColor(d0, j1 >> 16 & 255, k1 >> 16 & 255);
                        int i2 = this.interpolateColor(d0, j1 >> 8 & 255, k1 >> 8 & 255);
                        int j2 = this.interpolateColor(d0, j1 & 255, k1 & 255);
                        this.interpolatedFrameData[l][i1] = j1 & -16777216 | l1 << 16 | i2 << 8 | j2;
                    }
                }
            }

            TextureUtil.func_147955_a(this.interpolatedFrameData, this.width, this.height, this.x, this.y, false, false);
        }
    }

    private int interpolateColor(double factor, int to, int from)
    {
        return (int)(factor * (double)to + (1.0D - factor) * (double)from);
    }

    public int[][] func_147965_a(int p_147965_1_)
    {
        return this.field_110976_a.get(p_147965_1_);
    }

    public int getFrameCount()
    {
        return this.field_110976_a.size();
    }

    public void func_110966_b(int p_110966_1_)
    {
        this.width = p_110966_1_;
    }

    public void func_110969_c(int p_110969_1_)
    {
        this.height = p_110969_1_;
    }

    public void func_188538_a(PngSizeInfo p_188538_1_, boolean p_188538_2_) throws IOException
    {
        this.func_130102_n();
        this.width = p_188538_1_.width;
        this.height = p_188538_1_.height;

        if (p_188538_2_)
        {
            this.height = this.width;
        }
        else if (p_188538_1_.height != p_188538_1_.width)
        {
            throw new RuntimeException("broken aspect ratio and not an animation");
        }
    }

    public void func_188539_a(IResource p_188539_1_, int p_188539_2_) throws IOException
    {
        BufferedImage bufferedimage = TextureUtil.func_177053_a(p_188539_1_.func_110527_b());
        AnimationMetadataSection animationmetadatasection = (AnimationMetadataSection)p_188539_1_.func_110526_a("animation");
        int[][] aint = new int[p_188539_2_][];
        aint[0] = new int[bufferedimage.getWidth() * bufferedimage.getHeight()];
        bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), aint[0], 0, bufferedimage.getWidth());

        if (animationmetadatasection == null)
        {
            this.field_110976_a.add(aint);
        }
        else
        {
            int i = bufferedimage.getHeight() / this.width;

            if (animationmetadatasection.getFrameCount() > 0)
            {
                Iterator lvt_7_1_ = animationmetadatasection.getFrameIndexSet().iterator();

                while (lvt_7_1_.hasNext())
                {
                    int j = ((Integer)lvt_7_1_.next()).intValue();

                    if (j >= i)
                    {
                        throw new RuntimeException("invalid frameindex " + j);
                    }

                    this.func_130099_d(j);
                    this.field_110976_a.set(j, func_147962_a(aint, this.width, this.width, j));
                }

                this.animationMetadata = animationmetadatasection;
            }
            else
            {
                List<AnimationFrame> list = Lists.<AnimationFrame>newArrayList();

                for (int k = 0; k < i; ++k)
                {
                    this.field_110976_a.add(func_147962_a(aint, this.width, this.width, k));
                    list.add(new AnimationFrame(k, -1));
                }

                this.animationMetadata = new AnimationMetadataSection(list, this.width, this.height, animationmetadatasection.getFrameTime(), animationmetadatasection.isInterpolate());
            }
        }
    }

    public void generateMipmaps(int level)
    {
        List<int[][]> list = Lists.<int[][]>newArrayList();

        for (int i = 0; i < this.field_110976_a.size(); ++i)
        {
            final int[][] aint = this.field_110976_a.get(i);

            if (aint != null)
            {
                try
                {
                    list.add(TextureUtil.func_147949_a(level, this.width, aint));
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Generating mipmaps for frame");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Frame being iterated");
                    crashreportcategory.addDetail("Frame index", Integer.valueOf(i));
                    crashreportcategory.addDetail("Frame sizes", new ICrashReportDetail<String>()
                    {
                        public String call() throws Exception
                        {
                            StringBuilder stringbuilder = new StringBuilder();

                            for (int[] aint1 : aint)
                            {
                                if (stringbuilder.length() > 0)
                                {
                                    stringbuilder.append(", ");
                                }

                                stringbuilder.append(aint1 == null ? "null" : aint1.length);
                            }

                            return stringbuilder.toString();
                        }
                    });
                    throw new ReportedException(crashreport);
                }
            }
        }

        this.func_110968_a(list);
    }

    private void func_130099_d(int p_130099_1_)
    {
        if (this.field_110976_a.size() <= p_130099_1_)
        {
            for (int i = this.field_110976_a.size(); i <= p_130099_1_; ++i)
            {
                this.field_110976_a.add(null);
            }
        }
    }

    private static int[][] func_147962_a(int[][] p_147962_0_, int p_147962_1_, int p_147962_2_, int p_147962_3_)
    {
        int[][] aint = new int[p_147962_0_.length][];

        for (int i = 0; i < p_147962_0_.length; ++i)
        {
            int[] aint1 = p_147962_0_[i];

            if (aint1 != null)
            {
                aint[i] = new int[(p_147962_1_ >> i) * (p_147962_2_ >> i)];
                System.arraycopy(aint1, p_147962_3_ * aint[i].length, aint[i], 0, aint[i].length);
            }
        }

        return aint;
    }

    public void clearFramesTextureData()
    {
        this.field_110976_a.clear();
    }

    public boolean hasAnimationMetadata()
    {
        return this.animationMetadata != null;
    }

    public void func_110968_a(List<int[][]> p_110968_1_)
    {
        this.field_110976_a = p_110968_1_;
    }

    private void func_130102_n()
    {
        this.animationMetadata = null;
        this.func_110968_a(Lists.newArrayList());
        this.frameCounter = 0;
        this.tickCounter = 0;
    }

    public String toString()
    {
        return "TextureAtlasSprite{name='" + this.iconName + '\'' + ", frameCount=" + this.field_110976_a.size() + ", rotated=" + this.rotated + ", x=" + this.x + ", y=" + this.y + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.minU + ", u1=" + this.maxU + ", v0=" + this.minV + ", v1=" + this.maxV + '}';
    }

    /*===================================== FORGE START =====================================*/
    /**
     * The result of this function determines is the below 'load' function is called, and the
     * default vanilla loading code is bypassed completely.
     * @param manager Main resource manager
     * @param location File resource location
     * @return True to use your own custom load code and bypass vanilla loading.
     */
    public boolean hasCustomLoader(net.minecraft.client.resources.IResourceManager manager, net.minecraft.util.ResourceLocation location)
    {
        return false;
    }

    /**
     * Load the specified resource as this sprite's data.
     * Returning false from this function will prevent this icon from being stitched onto the master texture.
     * @param manager Main resource manager
     * @param location File resource location
     * @param textureGetter accessor for dependencies. All of them will be loaded before this one
     * @return False to prevent this Icon from being stitched
     */
    public boolean load(net.minecraft.client.resources.IResourceManager manager, net.minecraft.util.ResourceLocation location, java.util.function.Function<ResourceLocation, TextureAtlasSprite> textureGetter)
    {
        return true;
    }

    /**
     * @return all textures that should be loaded before this texture.
     */
    public java.util.Collection<ResourceLocation> getDependencies() {
        return com.google.common.collect.ImmutableList.of();
    }

    /*===================================== FORGE END ======================================*/
}