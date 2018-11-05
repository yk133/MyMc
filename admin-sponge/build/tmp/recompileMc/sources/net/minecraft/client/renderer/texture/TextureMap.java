package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class TextureMap extends AbstractTexture implements ITickableTextureObject
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ResourceLocation field_174945_f = new ResourceLocation("missingno");
    public static final ResourceLocation LOCATION_BLOCKS_TEXTURE = new ResourceLocation("textures/atlas/blocks.png");
    private final List<TextureAtlasSprite> listAnimatedSprites;
    private final Map<String, TextureAtlasSprite> field_110574_e;
    private final Map<String, TextureAtlasSprite> mapUploadedSprites;
    private final String basePath;
    private final ITextureMapPopulator field_174946_m;
    private int mipmapLevels;
    private final TextureAtlasSprite missingImage;

    public TextureMap(String basePathIn)
    {
        this(basePathIn, (ITextureMapPopulator)null);
    }

    public TextureMap(String p_i46100_1_, @Nullable ITextureMapPopulator p_i46100_2_)
    {
        this(p_i46100_1_, p_i46100_2_, false);
    }

    public TextureMap(String basePathIn, boolean skipFirst)
    {
        this(basePathIn, null, skipFirst);
    }

    public TextureMap(String p_i46100_1_, @Nullable ITextureMapPopulator p_i46100_2_, boolean skipFirst)
    {
        this.listAnimatedSprites = Lists.<TextureAtlasSprite>newArrayList();
        this.field_110574_e = Maps.<String, TextureAtlasSprite>newHashMap();
        this.mapUploadedSprites = Maps.<String, TextureAtlasSprite>newHashMap();
        this.missingImage = new TextureAtlasSprite("missingno");
        this.basePath = p_i46100_1_;
        this.field_174946_m = p_i46100_2_;
    }

    private void func_110569_e()
    {
        int[] aint = TextureUtil.field_110999_b;
        this.missingImage.func_110966_b(16);
        this.missingImage.func_110969_c(16);
        int[][] aint1 = new int[this.mipmapLevels + 1][];
        aint1[0] = aint;
        this.missingImage.func_110968_a(Lists.<int[][]>newArrayList(aint1));
    }

    public void func_110551_a(IResourceManager p_110551_1_) throws IOException
    {
        if (this.field_174946_m != null)
        {
            this.func_174943_a(p_110551_1_, this.field_174946_m);
        }
    }

    public void func_174943_a(IResourceManager p_174943_1_, ITextureMapPopulator p_174943_2_)
    {
        this.field_110574_e.clear();
        net.minecraftforge.client.ForgeHooksClient.onTextureStitchedPre(this);
        p_174943_2_.func_177059_a(this);
        this.func_110569_e();
        this.deleteGlTexture();
        this.func_110571_b(p_174943_1_);
    }

    public void func_110571_b(IResourceManager p_110571_1_)
    {
        int i = Minecraft.getGLMaximumTextureSize();
        Stitcher stitcher = new Stitcher(i, i, 0, this.mipmapLevels);
        this.mapUploadedSprites.clear();
        this.listAnimatedSprites.clear();
        int j = Integer.MAX_VALUE;
        int k = 1 << this.mipmapLevels;
        net.minecraftforge.fml.common.FMLLog.log.info("Max texture size: {}", i);
        net.minecraftforge.fml.common.ProgressManager.ProgressBar bar = net.minecraftforge.fml.common.ProgressManager.push("Texture stitching", this.field_110574_e.size());
        loadedSprites.clear();

        for (Entry<String, TextureAtlasSprite> entry : Maps.newHashMap(this.field_110574_e).entrySet())
        {
            final ResourceLocation location = new ResourceLocation(entry.getKey());
            bar.step(location.toString());
            j = loadTexture(stitcher, p_110571_1_, location, entry.getValue(), bar, j, k);
        }
        finishLoading(stitcher, bar, j, k);
    }

    private int loadTexture(Stitcher stitcher, IResourceManager p_110571_1_, ResourceLocation location, TextureAtlasSprite textureatlassprite, net.minecraftforge.fml.common.ProgressManager.ProgressBar bar, int j, int k)
    {
        if (loadedSprites.contains(location)) {
            return j;
        }
        ResourceLocation resourcelocation = this.func_184396_a(textureatlassprite);
        IResource iresource = null;

        for(ResourceLocation loading : loadingSprites)
        {
            if(location.equals(loading))
            {
                final String error = "circular texture dependencies, stack: [" + com.google.common.base.Joiner.on(", ").join(loadingSprites) + "]";
                net.minecraftforge.fml.client.FMLClientHandler.instance().trackBrokenTexture(resourcelocation, error);
                return j;
            }
        }
        loadingSprites.addLast(location);
        try
        {
            for (ResourceLocation dependency : textureatlassprite.getDependencies())
            {
                if (!field_110574_e.containsKey(dependency.toString()))
                {
                    func_174942_a(dependency);
                }
                TextureAtlasSprite depSprite = field_110574_e.get(dependency.toString());
                j = loadTexture(stitcher, p_110571_1_, dependency, depSprite, bar, j, k);
            }
            try
            {
            if (textureatlassprite.hasCustomLoader(p_110571_1_, resourcelocation))
            {
                if (textureatlassprite.load(p_110571_1_, resourcelocation, l -> field_110574_e.get(l.toString())))
                {
                    return j;
                }
            }
            else
            {
                PngSizeInfo pngsizeinfo = PngSizeInfo.func_188532_a(p_110571_1_.func_110536_a(resourcelocation));
                iresource = p_110571_1_.func_110536_a(resourcelocation);
                boolean flag = iresource.func_110526_a("animation") != null;
                textureatlassprite.func_188538_a(pngsizeinfo, flag);
            }
            }
            catch (RuntimeException runtimeexception)
            {
                net.minecraftforge.fml.client.FMLClientHandler.instance().trackBrokenTexture(resourcelocation, runtimeexception.getMessage());
                return j;
            }
            catch (IOException ioexception)
            {
                net.minecraftforge.fml.client.FMLClientHandler.instance().trackMissingTexture(resourcelocation);
                return j;
            }
            finally
            {
                IOUtils.closeQuietly((Closeable)iresource);
            }

            j = Math.min(j, Math.min(textureatlassprite.getWidth(), textureatlassprite.getHeight()));
            int j1 = Math.min(Integer.lowestOneBit(textureatlassprite.getWidth()), Integer.lowestOneBit(textureatlassprite.getHeight()));

            if (j1 < k)
            {
                // FORGE: do not lower the mipmap level, just log the problematic textures
                LOGGER.warn("Texture {} with size {}x{} will have visual artifacts at mip level {}, it can only support level {}. Please report to the mod author that the texture should be some multiple of 16x16.", resourcelocation, Integer.valueOf(textureatlassprite.getWidth()), Integer.valueOf(textureatlassprite.getHeight()), Integer.valueOf(MathHelper.log2(k)), Integer.valueOf(MathHelper.log2(j1)));
            }

            if (func_184397_a(p_110571_1_, textureatlassprite))
            stitcher.addSprite(textureatlassprite);
            return j;
        }
        finally
        {
            loadingSprites.removeLast();
            loadedSprites.add(location);
        }
    }

    private void finishLoading(Stitcher stitcher, net.minecraftforge.fml.common.ProgressManager.ProgressBar bar, int j, int k)
    {
        net.minecraftforge.fml.common.ProgressManager.pop(bar);
        int l = Math.min(j, k);
        int i1 = MathHelper.log2(l);

        if (false) // FORGE: do not lower the mipmap level
        if (i1 < this.mipmapLevels)
        {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.basePath, Integer.valueOf(this.mipmapLevels), Integer.valueOf(i1), Integer.valueOf(l));
            this.mipmapLevels = i1;
        }

        this.missingImage.generateMipmaps(this.mipmapLevels);
        stitcher.addSprite(this.missingImage);
        bar = net.minecraftforge.fml.common.ProgressManager.push("Texture creation", 2);

        try
        {
            bar.step("Stitching");
            stitcher.doStitch();
        }
        catch (StitcherException stitcherexception)
        {
            throw stitcherexception;
        }

        LOGGER.info("Created: {}x{} {}-atlas", Integer.valueOf(stitcher.getCurrentWidth()), Integer.valueOf(stitcher.getCurrentHeight()), this.basePath);
        bar.step("Allocating GL texture");
        TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight());
        Map<String, TextureAtlasSprite> map = Maps.<String, TextureAtlasSprite>newHashMap(this.field_110574_e);

        net.minecraftforge.fml.common.ProgressManager.pop(bar);
        bar = net.minecraftforge.fml.common.ProgressManager.push("Texture mipmap and upload", stitcher.getStichSlots().size());

        for (TextureAtlasSprite textureatlassprite1 : stitcher.getStichSlots())
        {
            bar.step(textureatlassprite1.func_94215_i());
            {
                String s = textureatlassprite1.func_94215_i();
                map.remove(s);
                this.mapUploadedSprites.put(s, textureatlassprite1);

                try
                {
                    TextureUtil.func_147955_a(textureatlassprite1.func_147965_a(0), textureatlassprite1.getWidth(), textureatlassprite1.getHeight(), textureatlassprite1.func_130010_a(), textureatlassprite1.func_110967_i(), false, false);
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Texture being stitched together");
                    crashreportcategory.addDetail("Atlas path", this.basePath);
                    crashreportcategory.addDetail("Sprite", textureatlassprite1);
                    throw new ReportedException(crashreport);
                }

                if (textureatlassprite1.hasAnimationMetadata())
                {
                    this.listAnimatedSprites.add(textureatlassprite1);
                }
            }
        }

        for (TextureAtlasSprite textureatlassprite2 : map.values())
        {
            textureatlassprite2.func_94217_a(this.missingImage);
        }
        net.minecraftforge.client.ForgeHooksClient.onTextureStitchedPost(this);
        net.minecraftforge.fml.common.ProgressManager.pop(bar);
    }

    private boolean func_184397_a(IResourceManager p_184397_1_, final TextureAtlasSprite p_184397_2_)
    {
        ResourceLocation resourcelocation = this.func_184396_a(p_184397_2_);
        IResource iresource = null;
        label62:
        {
            boolean flag;
            if (p_184397_2_.hasCustomLoader(p_184397_1_, resourcelocation)) break label62;
            try
            {
                iresource = p_184397_1_.func_110536_a(resourcelocation);
                p_184397_2_.func_188539_a(iresource, this.mipmapLevels + 1);
                break label62;
            }
            catch (RuntimeException runtimeexception)
            {
                LOGGER.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
                flag = false;
            }
            catch (IOException ioexception)
            {
                LOGGER.error("Using missing texture, unable to load {}", resourcelocation, ioexception);
                flag = false;
                return flag;
            }
            finally
            {
                IOUtils.closeQuietly((Closeable)iresource);
            }

            return flag;
        }

        try
        {
            p_184397_2_.generateMipmaps(this.mipmapLevels);
            return true;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Applying mipmap");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");
            crashreportcategory.addDetail("Sprite name", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    return p_184397_2_.func_94215_i();
                }
            });
            crashreportcategory.addDetail("Sprite size", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    return p_184397_2_.getWidth() + " x " + p_184397_2_.getHeight();
                }
            });
            crashreportcategory.addDetail("Sprite frames", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    return p_184397_2_.getFrameCount() + " frames";
                }
            });
            crashreportcategory.addDetail("Mipmap levels", Integer.valueOf(this.mipmapLevels));
            throw new ReportedException(crashreport);
        }
    }

    private ResourceLocation func_184396_a(TextureAtlasSprite p_184396_1_)
    {
        ResourceLocation resourcelocation = new ResourceLocation(p_184396_1_.func_94215_i());
        return new ResourceLocation(resourcelocation.getNamespace(), String.format("%s/%s%s", this.basePath, resourcelocation.getPath(), ".png"));
    }

    public TextureAtlasSprite getAtlasSprite(String iconName)
    {
        TextureAtlasSprite textureatlassprite = this.mapUploadedSprites.get(iconName);

        if (textureatlassprite == null)
        {
            textureatlassprite = this.missingImage;
        }

        return textureatlassprite;
    }

    public void updateAnimations()
    {
        TextureUtil.bindTexture(this.getGlTextureId());

        for (TextureAtlasSprite textureatlassprite : this.listAnimatedSprites)
        {
            textureatlassprite.updateAnimation();
        }
    }

    public TextureAtlasSprite func_174942_a(ResourceLocation p_174942_1_)
    {
        if (p_174942_1_ == null)
        {
            throw new IllegalArgumentException("Location cannot be null!");
        }
        else
        {
            TextureAtlasSprite textureatlassprite = this.field_110574_e.get(p_174942_1_.toString());

            if (textureatlassprite == null)
            {
                textureatlassprite = TextureAtlasSprite.func_176604_a(p_174942_1_);
                this.field_110574_e.put(p_174942_1_.toString(), textureatlassprite);
            }

            return textureatlassprite;
        }
    }

    public void tick()
    {
        this.updateAnimations();
    }

    public void setMipmapLevels(int mipmapLevelsIn)
    {
        this.mipmapLevels = mipmapLevelsIn;
    }

    public TextureAtlasSprite func_174944_f()
    {
        return this.missingImage;
    }

    //===================================================================================================
    //                                           Forge Start
    //===================================================================================================

    private final java.util.Deque<ResourceLocation> loadingSprites = new java.util.ArrayDeque<>();
    private final java.util.Set<ResourceLocation> loadedSprites = new java.util.HashSet<>();

    /**
     * Grabs the registered entry for the specified name, returning null if there was not a entry.
     * Opposed to registerIcon, this will not instantiate the entry, useful to test if a mapping exists.
     *
     * @param name The name of the entry to find
     * @return The registered entry, null if nothing was registered.
     */
    @Nullable
    public TextureAtlasSprite getTextureExtry(String name)
    {
        return field_110574_e.get(name);
    }

    /**
     * Adds a texture registry entry to this map for the specified name if one does not already exist.
     * Returns false if the map already contains a entry for the specified name.
     *
     * @param entry Entry instance
     * @return True if the entry was added to the map, false otherwise.
     */
    public boolean setTextureEntry(TextureAtlasSprite entry)
    {
        String name = entry.func_94215_i();
        if (!field_110574_e.containsKey(name))
        {
            field_110574_e.put(name, entry);
            return true;
        }
        return false;
    }

    public String getBasePath()
    {
        return basePath;
    }

    public int getMipmapLevels()
    {
        return mipmapLevels;
    }
}