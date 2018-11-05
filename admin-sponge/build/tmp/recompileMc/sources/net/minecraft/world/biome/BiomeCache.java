package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import net.minecraft.server.MinecraftServer;

public class BiomeCache
{
    /** Reference to the WorldChunkManager */
    private final BiomeProvider provider;
    private long field_76842_b;
    /** The map of keys to BiomeCacheBlocks. Keys are based on the chunk x, z coordinates as (x | z << 32). */
    private final Long2ObjectMap<BiomeCache.Block> cacheMap = new Long2ObjectOpenHashMap<BiomeCache.Block>(4096);
    private final List<BiomeCache.Block> field_76841_d = Lists.<BiomeCache.Block>newArrayList();

    public BiomeCache(BiomeProvider provider)
    {
        this.provider = provider;
    }

    /**
     * Returns a biome cache block at location specified.
     */
    public BiomeCache.Block getEntry(int x, int z)
    {
        x = x >> 4;
        z = z >> 4;
        long i = (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
        BiomeCache.Block biomecache$block = (BiomeCache.Block)this.cacheMap.get(i);

        if (biomecache$block == null)
        {
            biomecache$block = new BiomeCache.Block(x, z);
            this.cacheMap.put(i, biomecache$block);
            this.field_76841_d.add(biomecache$block);
        }

        biomecache$block.field_76886_f = MinecraftServer.func_130071_aq();
        return biomecache$block;
    }

    public Biome getBiome(int x, int z, Biome defaultValue)
    {
        Biome biome = this.getEntry(x, z).getBiome(x, z);
        return biome == null ? defaultValue : biome;
    }

    /**
     * Removes BiomeCacheBlocks from this cache that haven't been accessed in at least 30 seconds.
     */
    public void cleanupCache()
    {
        long i = MinecraftServer.func_130071_aq();
        long j = i - this.field_76842_b;

        if (j > 7500L || j < 0L)
        {
            this.field_76842_b = i;

            for (int k = 0; k < this.field_76841_d.size(); ++k)
            {
                BiomeCache.Block biomecache$block = this.field_76841_d.get(k);
                long l = i - biomecache$block.field_76886_f;

                if (l > 30000L || l < 0L)
                {
                    this.field_76841_d.remove(k--);
                    long i1 = (long)biomecache$block.field_76888_d & 4294967295L | ((long)biomecache$block.field_76889_e & 4294967295L) << 32;
                    this.cacheMap.remove(i1);
                }
            }
        }
    }

    /**
     * Returns the array of cached biome types in the BiomeCacheBlock at the given location.
     */
    public Biome[] getCachedBiomes(int x, int z)
    {
        return this.getEntry(x, z).biomes;
    }

    public class Block
    {
        /** Flattened 16 * 16 array of the biomes in this chunk */
        public Biome[] biomes = new Biome[256];
        public int field_76888_d;
        public int field_76889_e;
        public long field_76886_f;

        public Block(int x, int z)
        {
            this.field_76888_d = x;
            this.field_76889_e = z;
            BiomeCache.this.provider.func_76931_a(this.biomes, x << 4, z << 4, 16, 16, false);
        }

        /**
         * Returns the BiomeGenBase related to the x, z position from the cache block.
         */
        public Biome getBiome(int x, int z)
        {
            return this.biomes[x & 15 | (z & 15) << 4];
        }
    }
}