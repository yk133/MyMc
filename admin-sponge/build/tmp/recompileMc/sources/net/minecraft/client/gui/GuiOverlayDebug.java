package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.Display;

@SideOnly(Side.CLIENT)
public class GuiOverlayDebug extends Gui
{
    private final Minecraft mc;
    private final FontRenderer fontRenderer;

    public GuiOverlayDebug(Minecraft mc)
    {
        this.mc = mc;
        this.fontRenderer = mc.fontRenderer;
    }

    public void func_175237_a(ScaledResolution p_175237_1_)
    {
        this.mc.profiler.startSection("debug");
        GlStateManager.pushMatrix();
        this.renderDebugInfoLeft();
        this.func_175239_b(p_175237_1_);
        GlStateManager.popMatrix();

        if (this.mc.gameSettings.showLagometer)
        {
            this.renderLagometer();
        }

        this.mc.profiler.endSection();
    }

    protected void renderDebugInfoLeft()
    {
        List<String> list = this.call();
        list.add("");
        list.add("Debug: Pie [shift]: " + (this.mc.gameSettings.showDebugProfilerChart ? "visible" : "hidden") + " FPS [alt]: " + (this.mc.gameSettings.showLagometer ? "visible" : "hidden"));
        list.add("For help: press F3 + Q");

        for (int i = 0; i < list.size(); ++i)
        {
            String s = list.get(i);

            if (!Strings.isNullOrEmpty(s))
            {
                int j = this.fontRenderer.FONT_HEIGHT;
                int k = this.fontRenderer.getStringWidth(s);
                int l = 2;
                int i1 = 2 + j * i;
                drawRect(1, i1 - 1, 2 + k + 1, i1 + j - 1, -1873784752);
                this.fontRenderer.func_78276_b(s, 2, i1, 14737632);
            }
        }
    }

    protected void func_175239_b(ScaledResolution p_175239_1_)
    {
        List<String> list = this.getDebugInfoRight();

        for (int i = 0; i < list.size(); ++i)
        {
            String s = list.get(i);

            if (!Strings.isNullOrEmpty(s))
            {
                int j = this.fontRenderer.FONT_HEIGHT;
                int k = this.fontRenderer.getStringWidth(s);
                int l = p_175239_1_.func_78326_a() - 2 - k;
                int i1 = 2 + j * i;
                drawRect(l - 1, i1 - 1, l + k + 1, i1 + j - 1, -1873784752);
                this.fontRenderer.func_78276_b(s, l, i1, 14737632);
            }
        }
    }

    @SuppressWarnings("incomplete-switch")
    protected List<String> call()
    {
        BlockPos blockpos = new BlockPos(this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getBoundingBox().minY, this.mc.getRenderViewEntity().posZ);

        if (this.mc.isReducedDebug())
        {
            return Lists.newArrayList("Minecraft 1.12.2 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.mc.debug, this.mc.renderGlobal.getDebugInfoRenders(), this.mc.renderGlobal.getDebugInfoEntities(), "P: " + this.mc.particles.getStatistics() + ". T: " + this.mc.world.getDebugLoadedEntities(), this.mc.world.getProviderName(), "", String.format("Chunk-relative: %d %d %d", blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15));
        }
        else
        {
            Entity entity = this.mc.getRenderViewEntity();
            EnumFacing enumfacing = entity.getHorizontalFacing();
            String s = "Invalid";

            switch (enumfacing)
            {
                case NORTH:
                    s = "Towards negative Z";
                    break;
                case SOUTH:
                    s = "Towards positive Z";
                    break;
                case WEST:
                    s = "Towards negative X";
                    break;
                case EAST:
                    s = "Towards positive X";
            }

            List<String> list = Lists.newArrayList("Minecraft 1.12.2 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.mc.getVersionType()) ? "" : "/" + this.mc.getVersionType()) + ")", this.mc.debug, this.mc.renderGlobal.getDebugInfoRenders(), this.mc.renderGlobal.getDebugInfoEntities(), "P: " + this.mc.particles.getStatistics() + ". T: " + this.mc.world.getDebugLoadedEntities(), this.mc.world.getProviderName(), "", String.format("XYZ: %.3f / %.5f / %.3f", this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getBoundingBox().minY, this.mc.getRenderViewEntity().posZ), String.format("Block: %d %d %d", blockpos.getX(), blockpos.getY(), blockpos.getZ()), String.format("Chunk: %d %d %d in %d %d %d", blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15, blockpos.getX() >> 4, blockpos.getY() >> 4, blockpos.getZ() >> 4), String.format("Facing: %s (%s) (%.1f / %.1f)", enumfacing, s, MathHelper.wrapDegrees(entity.rotationYaw), MathHelper.wrapDegrees(entity.rotationPitch)));

            if (this.mc.world != null)
            {
                Chunk chunk = this.mc.world.getChunk(blockpos);

                if (this.mc.world.isBlockLoaded(blockpos) && blockpos.getY() >= 0 && blockpos.getY() < 256)
                {
                    if (!chunk.isEmpty())
                    {
                        list.add("Biome: " + chunk.func_177411_a(blockpos, this.mc.world.func_72959_q()).func_185359_l());
                        list.add("Light: " + chunk.getLightSubtracted(blockpos, 0) + " (" + chunk.getLightFor(EnumSkyBlock.SKY, blockpos) + " sky, " + chunk.getLightFor(EnumSkyBlock.BLOCK, blockpos) + " block)");
                        DifficultyInstance difficultyinstance = this.mc.world.getDifficultyForLocation(blockpos);

                        if (this.mc.isIntegratedServerRunning() && this.mc.getIntegratedServer() != null)
                        {
                            EntityPlayerMP entityplayermp = this.mc.getIntegratedServer().getPlayerList().getPlayerByUUID(this.mc.player.getUniqueID());

                            if (entityplayermp != null)
                            {
                                difficultyinstance = entityplayermp.world.getDifficultyForLocation(new BlockPos(entityplayermp));
                            }
                        }

                        list.add(String.format("Local Difficulty: %.2f // %.2f (Day %d)", difficultyinstance.getAdditionalDifficulty(), difficultyinstance.getClampedAdditionalDifficulty(), this.mc.world.getDayTime() / 24000L));
                    }
                    else
                    {
                        list.add("Waiting for chunk...");
                    }
                }
                else
                {
                    list.add("Outside of world...");
                }
            }

            if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive())
            {
                list.add("Shader: " + this.mc.entityRenderer.getShaderGroup().getShaderGroupName());
            }

            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.type == RayTraceResult.Type.BLOCK && this.mc.objectMouseOver.getBlockPos() != null)
            {
                BlockPos blockpos1 = this.mc.objectMouseOver.getBlockPos();
                list.add(String.format("Looking at: %d %d %d", blockpos1.getX(), blockpos1.getY(), blockpos1.getZ()));
            }

            return list;
        }
    }

    protected <T extends Comparable<T>> List<String> getDebugInfoRight()
    {
        long i = Runtime.getRuntime().maxMemory();
        long j = Runtime.getRuntime().totalMemory();
        long k = Runtime.getRuntime().freeMemory();
        long l = j - k;
        List<String> list = Lists.newArrayList(String.format("Java: %s %dbit", System.getProperty("java.version"), this.mc.isJava64bit() ? 64 : 32), String.format("Mem: % 2d%% %03d/%03dMB", l * 100L / i, bytesToMb(l), bytesToMb(i)), String.format("Allocated: % 2d%% %03dMB", j * 100L / i, bytesToMb(j)), "", String.format("CPU: %s", OpenGlHelper.getCpu()), "", String.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), GlStateManager.getString(7936)), GlStateManager.getString(7937), GlStateManager.getString(7938));

        list.add("");
        list.addAll(net.minecraftforge.fml.common.FMLCommonHandler.instance().getBrandings(false));

        if (this.mc.isReducedDebug())
        {
            return list;
        }
        else
        {
            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.type == RayTraceResult.Type.BLOCK && this.mc.objectMouseOver.getBlockPos() != null)
            {
                BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
                IBlockState iblockstate = this.mc.world.getBlockState(blockpos);

                if (this.mc.world.getWorldType() != WorldType.DEBUG_ALL_BLOCK_STATES)
                {
                    iblockstate = iblockstate.func_185899_b(this.mc.world, blockpos);
                }

                list.add("");
                list.add(String.valueOf(Block.REGISTRY.getKey(iblockstate.getBlock())));
                IProperty<T> iproperty;
                String s;

                for (UnmodifiableIterator unmodifiableiterator = iblockstate.func_177228_b().entrySet().iterator(); unmodifiableiterator.hasNext(); list.add(iproperty.getName() + ": " + s))
                {
                    Entry < IProperty<?>, Comparable<? >> entry = (Entry)unmodifiableiterator.next();
                    iproperty = (IProperty)entry.getKey();
                    T t = (T)entry.getValue();
                    s = iproperty.getName(t);

                    if (Boolean.TRUE.equals(t))
                    {
                        s = TextFormatting.GREEN + s;
                    }
                    else if (Boolean.FALSE.equals(t))
                    {
                        s = TextFormatting.RED + s;
                    }
                }
            }

            return list;
        }
    }

    public void renderLagometer()
    {
        GlStateManager.disableDepthTest();
        FrameTimer frametimer = this.mc.getFrameTimer();
        int i = frametimer.getLastIndex();
        int j = frametimer.getIndex();
        long[] along = frametimer.getFrames();
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int k = i;
        int l = 0;
        drawRect(0, scaledresolution.func_78328_b() - 60, 240, scaledresolution.func_78328_b(), -1873784752);

        while (k != j)
        {
            int i1 = frametimer.getLagometerValue(along[k], 30);
            int j1 = this.getFrameColor(MathHelper.clamp(i1, 0, 60), 0, 30, 60);
            this.drawVerticalLine(l, scaledresolution.func_78328_b(), scaledresolution.func_78328_b() - i1, j1);
            ++l;
            k = frametimer.parseIndex(k + 1);
        }

        drawRect(1, scaledresolution.func_78328_b() - 30 + 1, 14, scaledresolution.func_78328_b() - 30 + 10, -1873784752);
        this.fontRenderer.func_78276_b("60", 2, scaledresolution.func_78328_b() - 30 + 2, 14737632);
        this.drawHorizontalLine(0, 239, scaledresolution.func_78328_b() - 30, -1);
        drawRect(1, scaledresolution.func_78328_b() - 60 + 1, 14, scaledresolution.func_78328_b() - 60 + 10, -1873784752);
        this.fontRenderer.func_78276_b("30", 2, scaledresolution.func_78328_b() - 60 + 2, 14737632);
        this.drawHorizontalLine(0, 239, scaledresolution.func_78328_b() - 60, -1);
        this.drawHorizontalLine(0, 239, scaledresolution.func_78328_b() - 1, -1);
        this.drawVerticalLine(0, scaledresolution.func_78328_b() - 60, scaledresolution.func_78328_b(), -1);
        this.drawVerticalLine(239, scaledresolution.func_78328_b() - 60, scaledresolution.func_78328_b(), -1);

        if (this.mc.gameSettings.limitFramerate <= 120)
        {
            this.drawHorizontalLine(0, 239, scaledresolution.func_78328_b() - 60 + this.mc.gameSettings.limitFramerate / 2, -16711681);
        }

        GlStateManager.enableDepthTest();
    }

    private int getFrameColor(int height, int heightMin, int heightMid, int heightMax)
    {
        return height < heightMid ? this.blendColors(-16711936, -256, (float)height / (float)heightMid) : this.blendColors(-256, -65536, (float)(height - heightMid) / (float)(heightMax - heightMid));
    }

    private int blendColors(int col1, int col2, float factor)
    {
        int i = col1 >> 24 & 255;
        int j = col1 >> 16 & 255;
        int k = col1 >> 8 & 255;
        int l = col1 & 255;
        int i1 = col2 >> 24 & 255;
        int j1 = col2 >> 16 & 255;
        int k1 = col2 >> 8 & 255;
        int l1 = col2 & 255;
        int i2 = MathHelper.clamp((int)((float)i + (float)(i1 - i) * factor), 0, 255);
        int j2 = MathHelper.clamp((int)((float)j + (float)(j1 - j) * factor), 0, 255);
        int k2 = MathHelper.clamp((int)((float)k + (float)(k1 - k) * factor), 0, 255);
        int l2 = MathHelper.clamp((int)((float)l + (float)(l1 - l) * factor), 0, 255);
        return i2 << 24 | j2 << 16 | k2 << 8 | l2;
    }

    private static long bytesToMb(long bytes)
    {
        return bytes / 1024L / 1024L;
    }
}