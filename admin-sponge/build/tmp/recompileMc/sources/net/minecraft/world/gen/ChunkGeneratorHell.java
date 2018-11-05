package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenBush;
import net.minecraft.world.gen.feature.WorldGenFire;
import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import net.minecraft.world.gen.feature.WorldGenGlowStone2;
import net.minecraft.world.gen.feature.WorldGenHellLava;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenNetherBridge;

public class ChunkGeneratorHell implements IChunkGenerator
{
    protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
    protected static final IBlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
    protected static final IBlockState field_185942_c = Blocks.BEDROCK.getDefaultState();
    protected static final IBlockState LAVA = Blocks.LAVA.getDefaultState();
    protected static final IBlockState field_185944_e = Blocks.GRAVEL.getDefaultState();
    protected static final IBlockState field_185945_f = Blocks.SOUL_SAND.getDefaultState();
    private final World field_185952_n;
    private final boolean field_185953_o;
    private final Random field_185954_p;
    private double[] field_73185_q = new double[256];
    private double[] field_73184_r = new double[256];
    private double[] field_185955_s = new double[256];
    private double[] field_185956_t;
    private NoiseGeneratorOctaves lperlinNoise1;
    private NoiseGeneratorOctaves lperlinNoise2;
    private NoiseGeneratorOctaves perlinNoise1;
    /** Determines whether slowsand or gravel can be generated at a location */
    private NoiseGeneratorOctaves slowsandGravelNoiseGen;
    private NoiseGeneratorOctaves field_73174_n;
    public NoiseGeneratorOctaves scaleNoise;
    public NoiseGeneratorOctaves depthNoise;
    private final WorldGenFire field_177470_t = new WorldGenFire();
    private final WorldGenGlowStone1 field_177469_u = new WorldGenGlowStone1();
    private final WorldGenGlowStone2 field_177468_v = new WorldGenGlowStone2();
    private final WorldGenerator field_177467_w = new WorldGenMinable(Blocks.field_150449_bY.getDefaultState(), 14, BlockMatcher.forBlock(Blocks.NETHERRACK));
    private final WorldGenerator field_189888_D = new WorldGenMinable(Blocks.field_189877_df.getDefaultState(), 33, BlockMatcher.forBlock(Blocks.NETHERRACK));
    private final WorldGenHellLava field_177473_x = new WorldGenHellLava(Blocks.field_150356_k, true);
    private final WorldGenHellLava field_177472_y = new WorldGenHellLava(Blocks.field_150356_k, false);
    private final WorldGenBush field_177471_z = new WorldGenBush(Blocks.BROWN_MUSHROOM);
    private final WorldGenBush field_177465_A = new WorldGenBush(Blocks.RED_MUSHROOM);
    private MapGenNetherBridge field_73172_c = new MapGenNetherBridge();
    private MapGenBase field_185939_I = new MapGenCavesHell();
    double[] field_185948_i;
    double[] field_185949_j;
    double[] field_185950_k;
    double[] field_73168_g;
    double[] field_185951_m;

    public ChunkGeneratorHell(World p_i45637_1_, boolean p_i45637_2_, long p_i45637_3_)
    {
        this.field_185952_n = p_i45637_1_;
        this.field_185953_o = p_i45637_2_;
        this.field_185954_p = new Random(p_i45637_3_);
        this.lperlinNoise1 = new NoiseGeneratorOctaves(this.field_185954_p, 16);
        this.lperlinNoise2 = new NoiseGeneratorOctaves(this.field_185954_p, 16);
        this.perlinNoise1 = new NoiseGeneratorOctaves(this.field_185954_p, 8);
        this.slowsandGravelNoiseGen = new NoiseGeneratorOctaves(this.field_185954_p, 4);
        this.field_73174_n = new NoiseGeneratorOctaves(this.field_185954_p, 4);
        this.scaleNoise = new NoiseGeneratorOctaves(this.field_185954_p, 10);
        this.depthNoise = new NoiseGeneratorOctaves(this.field_185954_p, 16);
        p_i45637_1_.setSeaLevel(63);

        net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextHell ctx =
                new net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextHell(lperlinNoise1, lperlinNoise2, perlinNoise1, slowsandGravelNoiseGen, field_73174_n, scaleNoise, depthNoise);
        ctx = net.minecraftforge.event.terraingen.TerrainGen.getModdedNoiseGenerators(p_i45637_1_, this.field_185954_p, ctx);
        this.lperlinNoise1 = ctx.getLPerlin1();
        this.lperlinNoise2 = ctx.getLPerlin2();
        this.perlinNoise1 = ctx.getPerlin();
        this.slowsandGravelNoiseGen = ctx.getPerlin2();
        this.field_73174_n = ctx.getPerlin3();
        this.scaleNoise = ctx.getScale();
        this.depthNoise = ctx.getDepth();
        this.field_73172_c = (MapGenNetherBridge)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(field_73172_c, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.NETHER_BRIDGE);
        this.field_185939_I = net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(field_185939_I, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.NETHER_CAVE);
    }

    public void prepareHeights(int p_185936_1_, int p_185936_2_, ChunkPrimer primer)
    {
        int i = 4;
        int j = this.field_185952_n.getSeaLevel() / 2 + 1;
        int k = 5;
        int l = 17;
        int i1 = 5;
        this.field_185956_t = this.func_185938_a(this.field_185956_t, p_185936_1_ * 4, 0, p_185936_2_ * 4, 5, 17, 5);

        for (int j1 = 0; j1 < 4; ++j1)
        {
            for (int k1 = 0; k1 < 4; ++k1)
            {
                for (int l1 = 0; l1 < 16; ++l1)
                {
                    double d0 = 0.125D;
                    double d1 = this.field_185956_t[((j1 + 0) * 5 + k1 + 0) * 17 + l1 + 0];
                    double d2 = this.field_185956_t[((j1 + 0) * 5 + k1 + 1) * 17 + l1 + 0];
                    double d3 = this.field_185956_t[((j1 + 1) * 5 + k1 + 0) * 17 + l1 + 0];
                    double d4 = this.field_185956_t[((j1 + 1) * 5 + k1 + 1) * 17 + l1 + 0];
                    double d5 = (this.field_185956_t[((j1 + 0) * 5 + k1 + 0) * 17 + l1 + 1] - d1) * 0.125D;
                    double d6 = (this.field_185956_t[((j1 + 0) * 5 + k1 + 1) * 17 + l1 + 1] - d2) * 0.125D;
                    double d7 = (this.field_185956_t[((j1 + 1) * 5 + k1 + 0) * 17 + l1 + 1] - d3) * 0.125D;
                    double d8 = (this.field_185956_t[((j1 + 1) * 5 + k1 + 1) * 17 + l1 + 1] - d4) * 0.125D;

                    for (int i2 = 0; i2 < 8; ++i2)
                    {
                        double d9 = 0.25D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * 0.25D;
                        double d13 = (d4 - d2) * 0.25D;

                        for (int j2 = 0; j2 < 4; ++j2)
                        {
                            double d14 = 0.25D;
                            double d15 = d10;
                            double d16 = (d11 - d10) * 0.25D;

                            for (int k2 = 0; k2 < 4; ++k2)
                            {
                                IBlockState iblockstate = null;

                                if (l1 * 8 + i2 < j)
                                {
                                    iblockstate = LAVA;
                                }

                                if (d15 > 0.0D)
                                {
                                    iblockstate = NETHERRACK;
                                }

                                int l2 = j2 + j1 * 4;
                                int i3 = i2 + l1 * 8;
                                int j3 = k2 + k1 * 4;
                                primer.func_177855_a(l2, i3, j3, iblockstate);
                                d15 += d16;
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }

    public void func_185937_b(int p_185937_1_, int p_185937_2_, ChunkPrimer p_185937_3_)
    {
        if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, p_185937_1_, p_185937_2_, p_185937_3_, this.field_185952_n)) return;
        int i = this.field_185952_n.getSeaLevel() + 1;
        double d0 = 0.03125D;
        this.field_73185_q = this.slowsandGravelNoiseGen.func_76304_a(this.field_73185_q, p_185937_1_ * 16, p_185937_2_ * 16, 0, 16, 16, 1, 0.03125D, 0.03125D, 1.0D);
        this.field_73184_r = this.slowsandGravelNoiseGen.func_76304_a(this.field_73184_r, p_185937_1_ * 16, 109, p_185937_2_ * 16, 16, 1, 16, 0.03125D, 1.0D, 0.03125D);
        this.field_185955_s = this.field_73174_n.func_76304_a(this.field_185955_s, p_185937_1_ * 16, p_185937_2_ * 16, 0, 16, 16, 1, 0.0625D, 0.0625D, 0.0625D);

        for (int j = 0; j < 16; ++j)
        {
            for (int k = 0; k < 16; ++k)
            {
                boolean flag = this.field_73185_q[j + k * 16] + this.field_185954_p.nextDouble() * 0.2D > 0.0D;
                boolean flag1 = this.field_73184_r[j + k * 16] + this.field_185954_p.nextDouble() * 0.2D > 0.0D;
                int l = (int)(this.field_185955_s[j + k * 16] / 3.0D + 3.0D + this.field_185954_p.nextDouble() * 0.25D);
                int i1 = -1;
                IBlockState iblockstate = NETHERRACK;
                IBlockState iblockstate1 = NETHERRACK;

                for (int j1 = 127; j1 >= 0; --j1)
                {
                    if (j1 < 127 - this.field_185954_p.nextInt(5) && j1 > this.field_185954_p.nextInt(5))
                    {
                        IBlockState iblockstate2 = p_185937_3_.func_177856_a(k, j1, j);

                        if (iblockstate2.getBlock() != null && iblockstate2.getMaterial() != Material.AIR)
                        {
                            if (iblockstate2.getBlock() == Blocks.NETHERRACK)
                            {
                                if (i1 == -1)
                                {
                                    if (l <= 0)
                                    {
                                        iblockstate = AIR;
                                        iblockstate1 = NETHERRACK;
                                    }
                                    else if (j1 >= i - 4 && j1 <= i + 1)
                                    {
                                        iblockstate = NETHERRACK;
                                        iblockstate1 = NETHERRACK;

                                        if (flag1)
                                        {
                                            iblockstate = field_185944_e;
                                            iblockstate1 = NETHERRACK;
                                        }

                                        if (flag)
                                        {
                                            iblockstate = field_185945_f;
                                            iblockstate1 = field_185945_f;
                                        }
                                    }

                                    if (j1 < i && (iblockstate == null || iblockstate.getMaterial() == Material.AIR))
                                    {
                                        iblockstate = LAVA;
                                    }

                                    i1 = l;

                                    if (j1 >= i - 1)
                                    {
                                        p_185937_3_.func_177855_a(k, j1, j, iblockstate);
                                    }
                                    else
                                    {
                                        p_185937_3_.func_177855_a(k, j1, j, iblockstate1);
                                    }
                                }
                                else if (i1 > 0)
                                {
                                    --i1;
                                    p_185937_3_.func_177855_a(k, j1, j, iblockstate1);
                                }
                            }
                        }
                        else
                        {
                            i1 = -1;
                        }
                    }
                    else
                    {
                        p_185937_3_.func_177855_a(k, j1, j, field_185942_c);
                    }
                }
            }
        }
    }

    public Chunk func_185932_a(int p_185932_1_, int p_185932_2_)
    {
        this.field_185954_p.setSeed((long)p_185932_1_ * 341873128712L + (long)p_185932_2_ * 132897987541L);
        ChunkPrimer chunkprimer = new ChunkPrimer();
        this.prepareHeights(p_185932_1_, p_185932_2_, chunkprimer);
        this.func_185937_b(p_185932_1_, p_185932_2_, chunkprimer);
        this.field_185939_I.func_186125_a(this.field_185952_n, p_185932_1_, p_185932_2_, chunkprimer);

        if (this.field_185953_o)
        {
            this.field_73172_c.func_186125_a(this.field_185952_n, p_185932_1_, p_185932_2_, chunkprimer);
        }

        Chunk chunk = new Chunk(this.field_185952_n, chunkprimer, p_185932_1_, p_185932_2_);
        Biome[] abiome = this.field_185952_n.func_72959_q().func_76933_b((Biome[])null, p_185932_1_ * 16, p_185932_2_ * 16, 16, 16);
        byte[] abyte = chunk.func_76605_m();

        for (int i = 0; i < abyte.length; ++i)
        {
            abyte[i] = (byte)Biome.getIdForBiome(abiome[i]);
        }

        chunk.resetRelightChecks();
        return chunk;
    }

    private double[] func_185938_a(double[] p_185938_1_, int p_185938_2_, int p_185938_3_, int p_185938_4_, int p_185938_5_, int p_185938_6_, int p_185938_7_)
    {
        if (p_185938_1_ == null)
        {
            p_185938_1_ = new double[p_185938_5_ * p_185938_6_ * p_185938_7_];
        }

        net.minecraftforge.event.terraingen.ChunkGeneratorEvent.InitNoiseField event = new net.minecraftforge.event.terraingen.ChunkGeneratorEvent.InitNoiseField(this, p_185938_1_, p_185938_2_, p_185938_3_, p_185938_4_, p_185938_5_, p_185938_6_, p_185938_7_);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == net.minecraftforge.fml.common.eventhandler.Event.Result.DENY) return event.getNoisefield();

        double d0 = 684.412D;
        double d1 = 2053.236D;
        this.field_73168_g = this.scaleNoise.func_76304_a(this.field_73168_g, p_185938_2_, p_185938_3_, p_185938_4_, p_185938_5_, 1, p_185938_7_, 1.0D, 0.0D, 1.0D);
        this.field_185951_m = this.depthNoise.func_76304_a(this.field_185951_m, p_185938_2_, p_185938_3_, p_185938_4_, p_185938_5_, 1, p_185938_7_, 100.0D, 0.0D, 100.0D);
        this.field_185948_i = this.perlinNoise1.func_76304_a(this.field_185948_i, p_185938_2_, p_185938_3_, p_185938_4_, p_185938_5_, p_185938_6_, p_185938_7_, 8.555150000000001D, 34.2206D, 8.555150000000001D);
        this.field_185949_j = this.lperlinNoise1.func_76304_a(this.field_185949_j, p_185938_2_, p_185938_3_, p_185938_4_, p_185938_5_, p_185938_6_, p_185938_7_, 684.412D, 2053.236D, 684.412D);
        this.field_185950_k = this.lperlinNoise2.func_76304_a(this.field_185950_k, p_185938_2_, p_185938_3_, p_185938_4_, p_185938_5_, p_185938_6_, p_185938_7_, 684.412D, 2053.236D, 684.412D);
        int i = 0;
        double[] adouble = new double[p_185938_6_];

        for (int j = 0; j < p_185938_6_; ++j)
        {
            adouble[j] = Math.cos((double)j * Math.PI * 6.0D / (double)p_185938_6_) * 2.0D;
            double d2 = (double)j;

            if (j > p_185938_6_ / 2)
            {
                d2 = (double)(p_185938_6_ - 1 - j);
            }

            if (d2 < 4.0D)
            {
                d2 = 4.0D - d2;
                adouble[j] -= d2 * d2 * d2 * 10.0D;
            }
        }

        for (int l = 0; l < p_185938_5_; ++l)
        {
            for (int i1 = 0; i1 < p_185938_7_; ++i1)
            {
                double d3 = 0.0D;

                for (int k = 0; k < p_185938_6_; ++k)
                {
                    double d4 = adouble[k];
                    double d5 = this.field_185949_j[i] / 512.0D;
                    double d6 = this.field_185950_k[i] / 512.0D;
                    double d7 = (this.field_185948_i[i] / 10.0D + 1.0D) / 2.0D;
                    double d8;

                    if (d7 < 0.0D)
                    {
                        d8 = d5;
                    }
                    else if (d7 > 1.0D)
                    {
                        d8 = d6;
                    }
                    else
                    {
                        d8 = d5 + (d6 - d5) * d7;
                    }

                    d8 = d8 - d4;

                    if (k > p_185938_6_ - 4)
                    {
                        double d9 = (double)((float)(k - (p_185938_6_ - 4)) / 3.0F);
                        d8 = d8 * (1.0D - d9) + -10.0D * d9;
                    }

                    if ((double)k < 0.0D)
                    {
                        double d10 = (0.0D - (double)k) / 4.0D;
                        d10 = MathHelper.clamp(d10, 0.0D, 1.0D);
                        d8 = d8 * (1.0D - d10) + -10.0D * d10;
                    }

                    p_185938_1_[i] = d8;
                    ++i;
                }
            }
        }

        return p_185938_1_;
    }

    public void func_185931_b(int p_185931_1_, int p_185931_2_)
    {
        BlockFalling.fallInstantly = true;
        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.field_185952_n, this.field_185954_p, p_185931_1_, p_185931_2_, false);
        int i = p_185931_1_ * 16;
        int j = p_185931_2_ * 16;
        BlockPos blockpos = new BlockPos(i, 0, j);
        Biome biome = this.field_185952_n.getBiome(blockpos.add(16, 0, 16));
        ChunkPos chunkpos = new ChunkPos(p_185931_1_, p_185931_2_);
        this.field_73172_c.func_175794_a(this.field_185952_n, this.field_185954_p, chunkpos);

        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.field_185952_n, this.field_185954_p, p_185931_1_, p_185931_2_, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.NETHER_LAVA))
        for (int k = 0; k < 8; ++k)
        {
            this.field_177472_y.func_180709_b(this.field_185952_n, this.field_185954_p, blockpos.add(this.field_185954_p.nextInt(16) + 8, this.field_185954_p.nextInt(120) + 4, this.field_185954_p.nextInt(16) + 8));
        }

        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.field_185952_n, this.field_185954_p, p_185931_1_, p_185931_2_, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.FIRE))
        for (int i1 = 0; i1 < this.field_185954_p.nextInt(this.field_185954_p.nextInt(10) + 1) + 1; ++i1)
        {
            this.field_177470_t.func_180709_b(this.field_185952_n, this.field_185954_p, blockpos.add(this.field_185954_p.nextInt(16) + 8, this.field_185954_p.nextInt(120) + 4, this.field_185954_p.nextInt(16) + 8));
        }

        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.field_185952_n, this.field_185954_p, p_185931_1_, p_185931_2_, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.GLOWSTONE))
        {
        for (int j1 = 0; j1 < this.field_185954_p.nextInt(this.field_185954_p.nextInt(10) + 1); ++j1)
        {
            this.field_177469_u.func_180709_b(this.field_185952_n, this.field_185954_p, blockpos.add(this.field_185954_p.nextInt(16) + 8, this.field_185954_p.nextInt(120) + 4, this.field_185954_p.nextInt(16) + 8));
        }

        for (int k1 = 0; k1 < 10; ++k1)
        {
            this.field_177468_v.func_180709_b(this.field_185952_n, this.field_185954_p, blockpos.add(this.field_185954_p.nextInt(16) + 8, this.field_185954_p.nextInt(128), this.field_185954_p.nextInt(16) + 8));
        }
        }//Forge: End doGLowstone

        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.field_185952_n, this.field_185954_p, p_185931_1_, p_185931_2_, false);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.terraingen.DecorateBiomeEvent.Pre(this.field_185952_n, this.field_185954_p, chunkpos));

        if (net.minecraftforge.event.terraingen.TerrainGen.decorate(this.field_185952_n, this.field_185954_p, chunkpos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.SHROOM))
        {
        if (this.field_185954_p.nextBoolean())
        {
            this.field_177471_z.func_180709_b(this.field_185952_n, this.field_185954_p, blockpos.add(this.field_185954_p.nextInt(16) + 8, this.field_185954_p.nextInt(128), this.field_185954_p.nextInt(16) + 8));
        }

        if (this.field_185954_p.nextBoolean())
        {
            this.field_177465_A.func_180709_b(this.field_185952_n, this.field_185954_p, blockpos.add(this.field_185954_p.nextInt(16) + 8, this.field_185954_p.nextInt(128), this.field_185954_p.nextInt(16) + 8));
        }
        }


        if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(this.field_185952_n, this.field_185954_p, field_177467_w, blockpos, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.QUARTZ))
        for (int l1 = 0; l1 < 16; ++l1)
        {
            this.field_177467_w.func_180709_b(this.field_185952_n, this.field_185954_p, blockpos.add(this.field_185954_p.nextInt(16), this.field_185954_p.nextInt(108) + 10, this.field_185954_p.nextInt(16)));
        }

        int i2 = this.field_185952_n.getSeaLevel() / 2 + 1;

        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.field_185952_n, this.field_185954_p, p_185931_1_, p_185931_2_, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.NETHER_MAGMA))
        for (int l = 0; l < 4; ++l)
        {
            this.field_189888_D.func_180709_b(this.field_185952_n, this.field_185954_p, blockpos.add(this.field_185954_p.nextInt(16), i2 - 5 + this.field_185954_p.nextInt(10), this.field_185954_p.nextInt(16)));
        }

        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.field_185952_n, this.field_185954_p, p_185931_1_, p_185931_2_, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.NETHER_LAVA2))
        for (int j2 = 0; j2 < 16; ++j2)
        {
            int offset = net.minecraftforge.common.ForgeModContainer.fixVanillaCascading ? 8 : 0; // MC-117810
            this.field_177473_x.func_180709_b(this.field_185952_n, this.field_185954_p, blockpos.add(this.field_185954_p.nextInt(16) + offset, this.field_185954_p.nextInt(108) + 10, this.field_185954_p.nextInt(16) + offset));
        }

        biome.func_180624_a(this.field_185952_n, this.field_185954_p, new BlockPos(i, 0, j));

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.terraingen.DecorateBiomeEvent.Post(this.field_185952_n, this.field_185954_p, blockpos));

        BlockFalling.fallInstantly = false;
    }

    public boolean func_185933_a(Chunk p_185933_1_, int p_185933_2_, int p_185933_3_)
    {
        return false;
    }

    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        if (creatureType == EnumCreatureType.MONSTER)
        {
            if (this.field_73172_c.func_175795_b(pos))
            {
                return this.field_73172_c.func_75059_a();
            }

            if (this.field_73172_c.isPositionInStructure(this.field_185952_n, pos) && this.field_185952_n.getBlockState(pos.down()).getBlock() == Blocks.field_150385_bj)
            {
                return this.field_73172_c.func_75059_a();
            }
        }

        Biome biome = this.field_185952_n.getBiome(pos);
        return biome.getSpawns(creatureType);
    }

    @Nullable
    public BlockPos func_180513_a(World p_180513_1_, String p_180513_2_, BlockPos p_180513_3_, boolean p_180513_4_)
    {
        return "Fortress".equals(p_180513_2_) && this.field_73172_c != null ? this.field_73172_c.func_180706_b(p_180513_1_, p_180513_3_, p_180513_4_) : null;
    }

    public boolean func_193414_a(World p_193414_1_, String p_193414_2_, BlockPos p_193414_3_)
    {
        return "Fortress".equals(p_193414_2_) && this.field_73172_c != null ? this.field_73172_c.func_175795_b(p_193414_3_) : false;
    }

    public void func_180514_a(Chunk p_180514_1_, int p_180514_2_, int p_180514_3_)
    {
        this.field_73172_c.func_186125_a(this.field_185952_n, p_180514_2_, p_180514_3_, (ChunkPrimer)null);
    }
}