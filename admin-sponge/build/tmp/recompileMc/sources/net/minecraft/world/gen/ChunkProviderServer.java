package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer implements IChunkProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final Set<Long> droppedChunks = Sets.<Long>newHashSet();
    public final IChunkGenerator chunkGenerator;
    public final IChunkLoader chunkLoader;
    /** map of chunk Id's to Chunk instances */
    public final Long2ObjectMap<Chunk> loadedChunks = new Long2ObjectOpenHashMap<Chunk>(8192);
    public final WorldServer world;
    private final Set<Long> loadingChunks = com.google.common.collect.Sets.newHashSet();

    public ChunkProviderServer(WorldServer p_i46838_1_, IChunkLoader p_i46838_2_, IChunkGenerator p_i46838_3_)
    {
        this.world = p_i46838_1_;
        this.chunkLoader = p_i46838_2_;
        this.chunkGenerator = p_i46838_3_;
    }

    public Collection<Chunk> getLoadedChunks()
    {
        return this.loadedChunks.values();
    }

    /**
     * Marks the chunk for unload if the {@link WorldProvider} allows it.
     *  
     * Queueing a chunk for unload does <b>not</b> guarantee that it will be unloaded, as any request for the chunk will
     * unqueue the chunk.
     */
    public void queueUnload(Chunk chunkIn)
    {
        if (this.world.dimension.canDropChunk(chunkIn.x, chunkIn.z))
        {
            this.droppedChunks.add(Long.valueOf(ChunkPos.asLong(chunkIn.x, chunkIn.z)));
            chunkIn.unloadQueued = true;
        }
    }

    /**
     * Marks all chunks for unload
     *  
     * @see #queueUnload(Chunk)
     */
    public void queueUnloadAll()
    {
        ObjectIterator objectiterator = this.loadedChunks.values().iterator();

        while (objectiterator.hasNext())
        {
            Chunk chunk = (Chunk)objectiterator.next();
            this.queueUnload(chunk);
        }
    }

    @Nullable
    public Chunk getLoadedChunk(int x, int z)
    {
        long i = ChunkPos.asLong(x, z);
        Chunk chunk = (Chunk)this.loadedChunks.get(i);

        if (chunk != null)
        {
            chunk.unloadQueued = false;
        }

        return chunk;
    }

    @Nullable
    public Chunk func_186028_c(int p_186028_1_, int p_186028_2_)
    {
        return loadChunk(p_186028_1_, p_186028_2_, null);
    }

    @Nullable
    public Chunk loadChunk(int p_186028_1_, int p_186028_2_, @Nullable Runnable runnable)
    {
        Chunk chunk = this.getLoadedChunk(p_186028_1_, p_186028_2_);
        if (chunk == null)
        {
            long pos = ChunkPos.asLong(p_186028_1_, p_186028_2_);
            chunk = net.minecraftforge.common.ForgeChunkManager.fetchDormantChunk(pos, this.world);
            if (chunk != null || !(this.chunkLoader instanceof net.minecraft.world.chunk.storage.AnvilChunkLoader))
            {
                if (!loadingChunks.add(pos)) net.minecraftforge.fml.common.FMLLog.bigWarning("There is an attempt to load a chunk ({},{}) in dimension {} that is already being loaded. This will cause weird chunk breakages.", p_186028_1_, p_186028_2_, this.world.dimension.getDimension());
                if (chunk == null) chunk = this.func_73239_e(p_186028_1_, p_186028_2_);

                if (chunk != null)
                {
                this.loadedChunks.put(ChunkPos.asLong(p_186028_1_, p_186028_2_), chunk);
                chunk.onLoad();
                chunk.func_186030_a(this, this.chunkGenerator);
                }

                loadingChunks.remove(pos);
            }
            else
            {
                net.minecraft.world.chunk.storage.AnvilChunkLoader loader = (net.minecraft.world.chunk.storage.AnvilChunkLoader) this.chunkLoader;
                if (runnable == null || !net.minecraftforge.common.ForgeChunkManager.asyncChunkLoading)
                    chunk = net.minecraftforge.common.chunkio.ChunkIOExecutor.syncChunkLoad(this.world, loader, this, p_186028_1_, p_186028_2_);
                else if (loader.isChunkGeneratedAt(p_186028_1_, p_186028_2_))
                {
                    // We can only use the async queue for already generated chunks
                    net.minecraftforge.common.chunkio.ChunkIOExecutor.queueChunkLoad(this.world, loader, this, p_186028_1_, p_186028_2_, runnable);
                    return null;
                }
            }
        }

        // If we didn't load the chunk async and have a callback run it now
        if (runnable != null) runnable.run();
        return chunk;
    }

    public Chunk provideChunk(int x, int z)
    {
        Chunk chunk = this.func_186028_c(x, z);

        if (chunk == null)
        {
            long i = ChunkPos.asLong(x, z);

            try
            {
                chunk = this.chunkGenerator.func_185932_a(x, z);
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
                crashreportcategory.addDetail("Location", String.format("%d,%d", x, z));
                crashreportcategory.addDetail("Position hash", Long.valueOf(i));
                crashreportcategory.addDetail("Generator", this.chunkGenerator);
                throw new ReportedException(crashreport);
            }

            this.loadedChunks.put(i, chunk);
            chunk.onLoad();
            chunk.func_186030_a(this, this.chunkGenerator);
        }

        return chunk;
    }

    @Nullable
    private Chunk func_73239_e(int p_73239_1_, int p_73239_2_)
    {
        try
        {
            Chunk chunk = this.chunkLoader.func_75815_a(this.world, p_73239_1_, p_73239_2_);

            if (chunk != null)
            {
                chunk.setLastSaveTime(this.world.getGameTime());
                this.chunkGenerator.func_180514_a(chunk, p_73239_1_, p_73239_2_);
            }

            return chunk;
        }
        catch (Exception exception)
        {
            LOGGER.error("Couldn't load chunk", (Throwable)exception);
            return null;
        }
    }

    private void saveChunkExtraData(Chunk chunkIn)
    {
        try
        {
            this.chunkLoader.saveExtraChunkData(this.world, chunkIn);
        }
        catch (Exception exception)
        {
            LOGGER.error("Couldn't save entities", (Throwable)exception);
        }
    }

    private void saveChunkData(Chunk chunkIn)
    {
        try
        {
            chunkIn.setLastSaveTime(this.world.getGameTime());
            this.chunkLoader.saveChunk(this.world, chunkIn);
        }
        catch (IOException ioexception)
        {
            LOGGER.error("Couldn't save chunk", (Throwable)ioexception);
        }
        catch (MinecraftException minecraftexception)
        {
            LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", (Throwable)minecraftexception);
        }
    }

    public boolean saveChunks(boolean all)
    {
        int i = 0;
        List<Chunk> list = Lists.newArrayList(this.loadedChunks.values());

        for (int j = 0; j < list.size(); ++j)
        {
            Chunk chunk = list.get(j);

            if (all)
            {
                this.saveChunkExtraData(chunk);
            }

            if (chunk.needsSaving(all))
            {
                this.saveChunkData(chunk);
                chunk.setModified(false);
                ++i;

                if (i == 24 && !all)
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Flushes all pending chunks fully back to disk
     */
    public void flushToDisk()
    {
        this.chunkLoader.flush();
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     *  
     * The return value is ignored, and is always false.
     */
    public boolean tick()
    {
        if (!this.world.disableLevelSaving)
        {
            if (!this.droppedChunks.isEmpty())
            {
                for (ChunkPos forced : this.world.getPersistentChunks().keySet())
                {
                    this.droppedChunks.remove(ChunkPos.asLong(forced.x, forced.z));
                }

                Iterator<Long> iterator = this.droppedChunks.iterator();

                for (int i = 0; i < 100 && iterator.hasNext(); iterator.remove())
                {
                    Long olong = iterator.next();
                    Chunk chunk = (Chunk)this.loadedChunks.get(olong);

                    if (chunk != null && chunk.unloadQueued)
                    {
                        chunk.onUnload();
                        net.minecraftforge.common.ForgeChunkManager.putDormantChunk(ChunkPos.asLong(chunk.x, chunk.z), chunk);
                        this.saveChunkData(chunk);
                        this.saveChunkExtraData(chunk);
                        this.loadedChunks.remove(olong);
                        ++i;
                    }
                }
            }

            if (this.loadedChunks.isEmpty()) net.minecraftforge.common.DimensionManager.unloadWorld(this.world.dimension.getDimension());

            this.chunkLoader.chunkTick();
        }

        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave()
    {
        return !this.world.disableLevelSaving;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString()
    {
        return "ServerChunkCache: " + this.loadedChunks.size() + " Drop: " + this.droppedChunks.size();
    }

    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        return this.chunkGenerator.getPossibleCreatures(creatureType, pos);
    }

    @Nullable
    public BlockPos func_180513_a(World p_180513_1_, String p_180513_2_, BlockPos p_180513_3_, boolean p_180513_4_)
    {
        return this.chunkGenerator.func_180513_a(p_180513_1_, p_180513_2_, p_180513_3_, p_180513_4_);
    }

    public boolean func_193413_a(World p_193413_1_, String p_193413_2_, BlockPos p_193413_3_)
    {
        return this.chunkGenerator.func_193414_a(p_193413_1_, p_193413_2_, p_193413_3_);
    }

    public int getLoadedChunkCount()
    {
        return this.loadedChunks.size();
    }

    /**
     * Checks to see if a chunk exists at x, z
     */
    public boolean chunkExists(int x, int z)
    {
        return this.loadedChunks.containsKey(ChunkPos.asLong(x, z));
    }

    public boolean isChunkGeneratedAt(int x, int z)
    {
        return this.loadedChunks.containsKey(ChunkPos.asLong(x, z)) || this.chunkLoader.isChunkGeneratedAt(x, z);
    }
}