package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorSettings;

public class GenLayerBiome extends GenLayer
{
    @SuppressWarnings("unchecked")
    private java.util.List<net.minecraftforge.common.BiomeManager.BiomeEntry>[] biomes = new java.util.ArrayList[net.minecraftforge.common.BiomeManager.BiomeType.values().length];
    private final ChunkGeneratorSettings settings;

    public GenLayerBiome(long p_i45560_1_, GenLayer p_i45560_3_, WorldType p_i45560_4_, ChunkGeneratorSettings p_i45560_5_)
    {
        super(p_i45560_1_);
        this.field_75909_a = p_i45560_3_;

        for (net.minecraftforge.common.BiomeManager.BiomeType type : net.minecraftforge.common.BiomeManager.BiomeType.values())
        {
            com.google.common.collect.ImmutableList<net.minecraftforge.common.BiomeManager.BiomeEntry> biomesToAdd = net.minecraftforge.common.BiomeManager.getBiomes(type);
            int idx = type.ordinal();

            if (biomes[idx] == null) biomes[idx] = new java.util.ArrayList<net.minecraftforge.common.BiomeManager.BiomeEntry>();
            if (biomesToAdd != null) biomes[idx].addAll(biomesToAdd);
        }

        int desertIdx = net.minecraftforge.common.BiomeManager.BiomeType.DESERT.ordinal();

        biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.DESERT, 30));
        biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.SAVANNA, 20));
        biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.PLAINS, 10));

        if (p_i45560_4_ == WorldType.DEFAULT_1_1)
        {
            biomes[desertIdx].clear();
            biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.DESERT, 10));
            biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.FOREST, 10));
            biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.MOUNTAINS, 10));
            biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.SWAMP, 10));
            biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.PLAINS, 10));
            biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.TAIGA, 10));
            this.settings = null;
        }
        else
        {
            this.settings = p_i45560_5_;
        }
    }

    public int[] func_75904_a(int p_75904_1_, int p_75904_2_, int p_75904_3_, int p_75904_4_)
    {
        int[] aint = this.field_75909_a.func_75904_a(p_75904_1_, p_75904_2_, p_75904_3_, p_75904_4_);
        int[] aint1 = IntCache.func_76445_a(p_75904_3_ * p_75904_4_);

        for (int i = 0; i < p_75904_4_; ++i)
        {
            for (int j = 0; j < p_75904_3_; ++j)
            {
                this.func_75903_a((long)(j + p_75904_1_), (long)(i + p_75904_2_));
                int k = aint[j + i * p_75904_3_];
                int l = (k & 3840) >> 8;
                k = k & -3841;

                if (this.settings != null && this.settings.field_177779_F >= 0)
                {
                    aint1[j + i * p_75904_3_] = this.settings.field_177779_F;
                }
                else if (func_151618_b(k))
                {
                    aint1[j + i * p_75904_3_] = k;
                }
                else if (k == Biome.getIdForBiome(Biomes.MUSHROOM_FIELDS))
                {
                    aint1[j + i * p_75904_3_] = k;
                }
                else if (k == 1)
                {
                    if (l > 0)
                    {
                        if (this.func_75902_a(3) == 0)
                        {
                            aint1[j + i * p_75904_3_] = Biome.getIdForBiome(Biomes.BADLANDS_PLATEAU);
                        }
                        else
                        {
                            aint1[j + i * p_75904_3_] = Biome.getIdForBiome(Biomes.WOODED_BADLANDS_PLATEAU);
                        }
                    }
                    else
                    {
                        aint1[j + i * p_75904_3_] = Biome.getIdForBiome(getWeightedBiomeEntry(net.minecraftforge.common.BiomeManager.BiomeType.DESERT).biome);
                    }
                }
                else if (k == 2)
                {
                    if (l > 0)
                    {
                        aint1[j + i * p_75904_3_] = Biome.getIdForBiome(Biomes.JUNGLE);
                    }
                    else
                    {
                        aint1[j + i * p_75904_3_] = Biome.getIdForBiome(getWeightedBiomeEntry(net.minecraftforge.common.BiomeManager.BiomeType.WARM).biome);
                    }
                }
                else if (k == 3)
                {
                    if (l > 0)
                    {
                        aint1[j + i * p_75904_3_] = Biome.getIdForBiome(Biomes.GIANT_TREE_TAIGA);
                    }
                    else
                    {
                        aint1[j + i * p_75904_3_] = Biome.getIdForBiome(getWeightedBiomeEntry(net.minecraftforge.common.BiomeManager.BiomeType.COOL).biome);
                    }
                }
                else if (k == 4)
                {
                    aint1[j + i * p_75904_3_] = Biome.getIdForBiome(getWeightedBiomeEntry(net.minecraftforge.common.BiomeManager.BiomeType.ICY).biome);
                }
                else
                {
                    aint1[j + i * p_75904_3_] = Biome.getIdForBiome(Biomes.MUSHROOM_FIELDS);
                }
            }
        }

        return aint1;
    }

    protected net.minecraftforge.common.BiomeManager.BiomeEntry getWeightedBiomeEntry(net.minecraftforge.common.BiomeManager.BiomeType type)
    {
        java.util.List<net.minecraftforge.common.BiomeManager.BiomeEntry> biomeList = biomes[type.ordinal()];
        int totalWeight = net.minecraft.util.WeightedRandom.getTotalWeight(biomeList);
        int weight = net.minecraftforge.common.BiomeManager.isTypeListModded(type)?func_75902_a(totalWeight):func_75902_a(totalWeight / 10) * 10;
        return (net.minecraftforge.common.BiomeManager.BiomeEntry)net.minecraft.util.WeightedRandom.getRandomItem(biomeList, weight);
    }
}