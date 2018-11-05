package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public class GenLayerBiomeEdge extends GenLayer
{
    public GenLayerBiomeEdge(long p_i45475_1_, GenLayer p_i45475_3_)
    {
        super(p_i45475_1_);
        this.field_75909_a = p_i45475_3_;
    }

    public int[] func_75904_a(int p_75904_1_, int p_75904_2_, int p_75904_3_, int p_75904_4_)
    {
        int[] aint = this.field_75909_a.func_75904_a(p_75904_1_ - 1, p_75904_2_ - 1, p_75904_3_ + 2, p_75904_4_ + 2);
        int[] aint1 = IntCache.func_76445_a(p_75904_3_ * p_75904_4_);

        for (int i = 0; i < p_75904_4_; ++i)
        {
            for (int j = 0; j < p_75904_3_; ++j)
            {
                this.func_75903_a((long)(j + p_75904_1_), (long)(i + p_75904_2_));
                int k = aint[j + 1 + (i + 1) * (p_75904_3_ + 2)];

                if (!this.func_151636_a(aint, aint1, j, i, p_75904_3_, k, Biome.getIdForBiome(Biomes.MOUNTAINS), Biome.getIdForBiome(Biomes.MOUNTAIN_EDGE)) && !this.replaceBiomeEdge(aint, aint1, j, i, p_75904_3_, k, Biome.getIdForBiome(Biomes.WOODED_BADLANDS_PLATEAU), Biome.getIdForBiome(Biomes.BADLANDS)) && !this.replaceBiomeEdge(aint, aint1, j, i, p_75904_3_, k, Biome.getIdForBiome(Biomes.BADLANDS_PLATEAU), Biome.getIdForBiome(Biomes.BADLANDS)) && !this.replaceBiomeEdge(aint, aint1, j, i, p_75904_3_, k, Biome.getIdForBiome(Biomes.GIANT_TREE_TAIGA), Biome.getIdForBiome(Biomes.TAIGA)))
                {
                    if (k == Biome.getIdForBiome(Biomes.DESERT))
                    {
                        int l1 = aint[j + 1 + (i + 1 - 1) * (p_75904_3_ + 2)];
                        int i2 = aint[j + 1 + 1 + (i + 1) * (p_75904_3_ + 2)];
                        int j2 = aint[j + 1 - 1 + (i + 1) * (p_75904_3_ + 2)];
                        int k2 = aint[j + 1 + (i + 1 + 1) * (p_75904_3_ + 2)];

                        if (l1 != Biome.getIdForBiome(Biomes.SNOWY_TUNDRA) && i2 != Biome.getIdForBiome(Biomes.SNOWY_TUNDRA) && j2 != Biome.getIdForBiome(Biomes.SNOWY_TUNDRA) && k2 != Biome.getIdForBiome(Biomes.SNOWY_TUNDRA))
                        {
                            aint1[j + i * p_75904_3_] = k;
                        }
                        else
                        {
                            aint1[j + i * p_75904_3_] = Biome.getIdForBiome(Biomes.WOODED_MOUNTAINS);
                        }
                    }
                    else if (k == Biome.getIdForBiome(Biomes.SWAMP))
                    {
                        int l = aint[j + 1 + (i + 1 - 1) * (p_75904_3_ + 2)];
                        int i1 = aint[j + 1 + 1 + (i + 1) * (p_75904_3_ + 2)];
                        int j1 = aint[j + 1 - 1 + (i + 1) * (p_75904_3_ + 2)];
                        int k1 = aint[j + 1 + (i + 1 + 1) * (p_75904_3_ + 2)];

                        if (l != Biome.getIdForBiome(Biomes.DESERT) && i1 != Biome.getIdForBiome(Biomes.DESERT) && j1 != Biome.getIdForBiome(Biomes.DESERT) && k1 != Biome.getIdForBiome(Biomes.DESERT) && l != Biome.getIdForBiome(Biomes.SNOWY_TAIGA) && i1 != Biome.getIdForBiome(Biomes.SNOWY_TAIGA) && j1 != Biome.getIdForBiome(Biomes.SNOWY_TAIGA) && k1 != Biome.getIdForBiome(Biomes.SNOWY_TAIGA) && l != Biome.getIdForBiome(Biomes.SNOWY_TUNDRA) && i1 != Biome.getIdForBiome(Biomes.SNOWY_TUNDRA) && j1 != Biome.getIdForBiome(Biomes.SNOWY_TUNDRA) && k1 != Biome.getIdForBiome(Biomes.SNOWY_TUNDRA))
                        {
                            if (l != Biome.getIdForBiome(Biomes.JUNGLE) && k1 != Biome.getIdForBiome(Biomes.JUNGLE) && i1 != Biome.getIdForBiome(Biomes.JUNGLE) && j1 != Biome.getIdForBiome(Biomes.JUNGLE))
                            {
                                aint1[j + i * p_75904_3_] = k;
                            }
                            else
                            {
                                aint1[j + i * p_75904_3_] = Biome.getIdForBiome(Biomes.JUNGLE_EDGE);
                            }
                        }
                        else
                        {
                            aint1[j + i * p_75904_3_] = Biome.getIdForBiome(Biomes.PLAINS);
                        }
                    }
                    else
                    {
                        aint1[j + i * p_75904_3_] = k;
                    }
                }
            }
        }

        return aint1;
    }

    private boolean func_151636_a(int[] p_151636_1_, int[] p_151636_2_, int p_151636_3_, int p_151636_4_, int p_151636_5_, int p_151636_6_, int p_151636_7_, int p_151636_8_)
    {
        if (!func_151616_a(p_151636_6_, p_151636_7_))
        {
            return false;
        }
        else
        {
            int i = p_151636_1_[p_151636_3_ + 1 + (p_151636_4_ + 1 - 1) * (p_151636_5_ + 2)];
            int j = p_151636_1_[p_151636_3_ + 1 + 1 + (p_151636_4_ + 1) * (p_151636_5_ + 2)];
            int k = p_151636_1_[p_151636_3_ + 1 - 1 + (p_151636_4_ + 1) * (p_151636_5_ + 2)];
            int l = p_151636_1_[p_151636_3_ + 1 + (p_151636_4_ + 1 + 1) * (p_151636_5_ + 2)];

            if (this.canBiomesBeNeighbors(i, p_151636_7_) && this.canBiomesBeNeighbors(j, p_151636_7_) && this.canBiomesBeNeighbors(k, p_151636_7_) && this.canBiomesBeNeighbors(l, p_151636_7_))
            {
                p_151636_2_[p_151636_3_ + p_151636_4_ * p_151636_5_] = p_151636_6_;
            }
            else
            {
                p_151636_2_[p_151636_3_ + p_151636_4_ * p_151636_5_] = p_151636_8_;
            }

            return true;
        }
    }

    /**
     * Creates a border around a biome.
     */
    private boolean replaceBiomeEdge(int[] p_151635_1_, int[] p_151635_2_, int p_151635_3_, int p_151635_4_, int p_151635_5_, int p_151635_6_, int p_151635_7_, int p_151635_8_)
    {
        if (p_151635_6_ != p_151635_7_)
        {
            return false;
        }
        else
        {
            int i = p_151635_1_[p_151635_3_ + 1 + (p_151635_4_ + 1 - 1) * (p_151635_5_ + 2)];
            int j = p_151635_1_[p_151635_3_ + 1 + 1 + (p_151635_4_ + 1) * (p_151635_5_ + 2)];
            int k = p_151635_1_[p_151635_3_ + 1 - 1 + (p_151635_4_ + 1) * (p_151635_5_ + 2)];
            int l = p_151635_1_[p_151635_3_ + 1 + (p_151635_4_ + 1 + 1) * (p_151635_5_ + 2)];

            if (func_151616_a(i, p_151635_7_) && func_151616_a(j, p_151635_7_) && func_151616_a(k, p_151635_7_) && func_151616_a(l, p_151635_7_))
            {
                p_151635_2_[p_151635_3_ + p_151635_4_ * p_151635_5_] = p_151635_6_;
            }
            else
            {
                p_151635_2_[p_151635_3_ + p_151635_4_ * p_151635_5_] = p_151635_8_;
            }

            return true;
        }
    }

    /**
     * Returns if two biomes can logically be neighbors. If one is hot and the other cold, for example, it returns
     * false.
     */
    private boolean canBiomesBeNeighbors(int p_151634_1_, int p_151634_2_)
    {
        if (func_151616_a(p_151634_1_, p_151634_2_))
        {
            return true;
        }
        else
        {
            Biome biome = Biome.getBiome(p_151634_1_);
            Biome biome1 = Biome.getBiome(p_151634_2_);

            if (biome != null && biome1 != null)
            {
                Biome.TempCategory biome$tempcategory = biome.getTempCategory();
                Biome.TempCategory biome$tempcategory1 = biome1.getTempCategory();
                return biome$tempcategory == biome$tempcategory1 || biome$tempcategory == Biome.TempCategory.MEDIUM || biome$tempcategory1 == Biome.TempCategory.MEDIUM;
            }
            else
            {
                return false;
            }
        }
    }
}