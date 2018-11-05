package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenBush;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenClay;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenSand;
import net.minecraft.world.gen.feature.WorldGenWaterlily;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeDecorator
{
    public boolean field_185425_a;
    public BlockPos field_180294_c;
    public ChunkGeneratorSettings field_180293_d;
    public WorldGenerator field_76809_f = new WorldGenClay(4);
    public WorldGenerator field_76810_g = new WorldGenSand(Blocks.SAND, 7);
    public WorldGenerator field_76822_h = new WorldGenSand(Blocks.GRAVEL, 6);
    public WorldGenerator field_76823_i;
    public WorldGenerator field_76820_j;
    public WorldGenerator field_180296_j;
    public WorldGenerator field_180297_k;
    public WorldGenerator field_180295_l;
    public WorldGenerator field_76821_k;
    public WorldGenerator field_76818_l;
    public WorldGenerator field_76819_m;
    public WorldGenerator field_180299_p;
    public WorldGenerator field_180298_q;
    public WorldGenerator field_76831_p;
    public WorldGenFlowers field_150514_p = new WorldGenFlowers(Blocks.field_150327_N, BlockFlower.EnumFlowerType.DANDELION);
    public WorldGenerator field_76828_s = new WorldGenBush(Blocks.BROWN_MUSHROOM);
    public WorldGenerator field_76827_t = new WorldGenBush(Blocks.RED_MUSHROOM);
    public WorldGenerator field_76826_u = new WorldGenBigMushroom();
    public WorldGenerator field_76825_v = new WorldGenReed();
    public WorldGenerator field_76824_w = new WorldGenCactus();
    public WorldGenerator field_76834_x = new WorldGenWaterlily();
    public int field_76833_y;
    public int field_76832_z;
    public float field_189870_A = 0.1F;
    public int field_76802_A = 2;
    public int field_76803_B = 1;
    public int field_76804_C;
    public int field_76798_D;
    public int field_76799_E;
    public int field_76800_F;
    public int field_76801_G = 1;
    public int field_76805_H = 3;
    public int field_76806_I = 1;
    public int field_76807_J;
    public boolean field_76808_K = true;

    public void func_180292_a(World p_180292_1_, Random p_180292_2_, Biome p_180292_3_, BlockPos p_180292_4_)
    {
        if (this.field_185425_a)
        {
            throw new RuntimeException("Already decorating");
        }
        else
        {
            this.field_180293_d = ChunkGeneratorSettings.Factory.func_177865_a(p_180292_1_.getWorldInfo().func_82571_y()).func_177864_b();
            this.field_180294_c = p_180292_4_;
            this.field_76823_i = new WorldGenMinable(Blocks.DIRT.getDefaultState(), this.field_180293_d.field_177789_I);
            this.field_76820_j = new WorldGenMinable(Blocks.GRAVEL.getDefaultState(), this.field_180293_d.field_177785_M);
            this.field_180296_j = new WorldGenMinable(Blocks.STONE.getDefaultState().func_177226_a(BlockStone.field_176247_a, BlockStone.EnumType.GRANITE), this.field_180293_d.field_177796_Q);
            this.field_180297_k = new WorldGenMinable(Blocks.STONE.getDefaultState().func_177226_a(BlockStone.field_176247_a, BlockStone.EnumType.DIORITE), this.field_180293_d.field_177792_U);
            this.field_180295_l = new WorldGenMinable(Blocks.STONE.getDefaultState().func_177226_a(BlockStone.field_176247_a, BlockStone.EnumType.ANDESITE), this.field_180293_d.field_177800_Y);
            this.field_76821_k = new WorldGenMinable(Blocks.COAL_ORE.getDefaultState(), this.field_180293_d.field_177844_ac);
            this.field_76818_l = new WorldGenMinable(Blocks.IRON_ORE.getDefaultState(), this.field_180293_d.field_177848_ag);
            this.field_76819_m = new WorldGenMinable(Blocks.GOLD_ORE.getDefaultState(), this.field_180293_d.field_177828_ak);
            this.field_180299_p = new WorldGenMinable(Blocks.REDSTONE_ORE.getDefaultState(), this.field_180293_d.field_177836_ao);
            this.field_180298_q = new WorldGenMinable(Blocks.DIAMOND_ORE.getDefaultState(), this.field_180293_d.field_177814_as);
            this.field_76831_p = new WorldGenMinable(Blocks.LAPIS_ORE.getDefaultState(), this.field_180293_d.field_177822_aw);
            this.func_150513_a(p_180292_3_, p_180292_1_, p_180292_2_);
            this.field_185425_a = false;
        }
    }

    protected void func_150513_a(Biome p_150513_1_, World p_150513_2_, Random p_150513_3_)
    {
        net.minecraft.util.math.ChunkPos forgeChunkPos = new net.minecraft.util.math.ChunkPos(field_180294_c); // actual ChunkPos instead of BlockPos, used for events
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.terraingen.DecorateBiomeEvent.Pre(p_150513_2_, p_150513_3_, forgeChunkPos));
        this.func_76797_b(p_150513_2_, p_150513_3_);

        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_150513_2_, p_150513_3_, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.SAND))
        for (int i = 0; i < this.field_76805_H; ++i)
        {
            int j = p_150513_3_.nextInt(16) + 8;
            int k = p_150513_3_.nextInt(16) + 8;
            this.field_76810_g.func_180709_b(p_150513_2_, p_150513_3_, p_150513_2_.func_175672_r(this.field_180294_c.add(j, 0, k)));
        }

        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_150513_2_, p_150513_3_, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.CLAY))
        for (int i1 = 0; i1 < this.field_76806_I; ++i1)
        {
            int l1 = p_150513_3_.nextInt(16) + 8;
            int i6 = p_150513_3_.nextInt(16) + 8;
            this.field_76809_f.func_180709_b(p_150513_2_, p_150513_3_, p_150513_2_.func_175672_r(this.field_180294_c.add(l1, 0, i6)));
        }

        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_150513_2_, p_150513_3_, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.SAND_PASS2))
        for (int j1 = 0; j1 < this.field_76801_G; ++j1)
        {
            int i2 = p_150513_3_.nextInt(16) + 8;
            int j6 = p_150513_3_.nextInt(16) + 8;
            this.field_76822_h.func_180709_b(p_150513_2_, p_150513_3_, p_150513_2_.func_175672_r(this.field_180294_c.add(i2, 0, j6)));
        }

        int k1 = this.field_76832_z;

        if (p_150513_3_.nextFloat() < this.field_189870_A)
        {
            ++k1;
        }

        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_150513_2_, p_150513_3_, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.TREE))
        for (int j2 = 0; j2 < k1; ++j2)
        {
            int k6 = p_150513_3_.nextInt(16) + 8;
            int l = p_150513_3_.nextInt(16) + 8;
            WorldGenAbstractTree worldgenabstracttree = p_150513_1_.func_150567_a(p_150513_3_);
            worldgenabstracttree.func_175904_e();
            BlockPos blockpos = p_150513_2_.func_175645_m(this.field_180294_c.add(k6, 0, l));

            if (worldgenabstracttree.func_180709_b(p_150513_2_, p_150513_3_, blockpos))
            {
                worldgenabstracttree.generateSaplings(p_150513_2_, p_150513_3_, blockpos);
            }
        }

        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_150513_2_, p_150513_3_, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.BIG_SHROOM))
        for (int k2 = 0; k2 < this.field_76807_J; ++k2)
        {
            int l6 = p_150513_3_.nextInt(16) + 8;
            int k10 = p_150513_3_.nextInt(16) + 8;
            this.field_76826_u.func_180709_b(p_150513_2_, p_150513_3_, p_150513_2_.func_175645_m(this.field_180294_c.add(l6, 0, k10)));
        }

        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_150513_2_, p_150513_3_, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.FLOWERS))
        for (int l2 = 0; l2 < this.field_76802_A; ++l2)
        {
            int i7 = p_150513_3_.nextInt(16) + 8;
            int l10 = p_150513_3_.nextInt(16) + 8;
            int j14 = p_150513_2_.func_175645_m(this.field_180294_c.add(i7, 0, l10)).getY() + 32;

            if (j14 > 0)
            {
                int k17 = p_150513_3_.nextInt(j14);
                BlockPos blockpos1 = this.field_180294_c.add(i7, k17, l10);
                BlockFlower.EnumFlowerType blockflower$enumflowertype = p_150513_1_.func_180623_a(p_150513_3_, blockpos1);
                BlockFlower blockflower = blockflower$enumflowertype.func_176964_a().func_180346_a();

                if (blockflower.getDefaultState().getMaterial() != Material.AIR)
                {
                    this.field_150514_p.func_175914_a(blockflower, blockflower$enumflowertype);
                    this.field_150514_p.func_180709_b(p_150513_2_, p_150513_3_, blockpos1);
                }
            }
        }

        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_150513_2_, p_150513_3_, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.GRASS))
        for (int i3 = 0; i3 < this.field_76803_B; ++i3)
        {
            int j7 = p_150513_3_.nextInt(16) + 8;
            int i11 = p_150513_3_.nextInt(16) + 8;
            int k14 = p_150513_2_.func_175645_m(this.field_180294_c.add(j7, 0, i11)).getY() * 2;

            if (k14 > 0)
            {
                int l17 = p_150513_3_.nextInt(k14);
                p_150513_1_.func_76730_b(p_150513_3_).func_180709_b(p_150513_2_, p_150513_3_, this.field_180294_c.add(j7, l17, i11));
            }
        }

        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_150513_2_, p_150513_3_, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.DEAD_BUSH))
        for (int j3 = 0; j3 < this.field_76804_C; ++j3)
        {
            int k7 = p_150513_3_.nextInt(16) + 8;
            int j11 = p_150513_3_.nextInt(16) + 8;
            int l14 = p_150513_2_.func_175645_m(this.field_180294_c.add(k7, 0, j11)).getY() * 2;

            if (l14 > 0)
            {
                int i18 = p_150513_3_.nextInt(l14);
                (new WorldGenDeadBush()).func_180709_b(p_150513_2_, p_150513_3_, this.field_180294_c.add(k7, i18, j11));
            }
        }

        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_150513_2_, p_150513_3_, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.LILYPAD))
        for (int k3 = 0; k3 < this.field_76833_y; ++k3)
        {
            int l7 = p_150513_3_.nextInt(16) + 8;
            int k11 = p_150513_3_.nextInt(16) + 8;
            int i15 = p_150513_2_.func_175645_m(this.field_180294_c.add(l7, 0, k11)).getY() * 2;

            if (i15 > 0)
            {
                int j18 = p_150513_3_.nextInt(i15);
                BlockPos blockpos4;
                BlockPos blockpos7;

                for (blockpos4 = this.field_180294_c.add(l7, j18, k11); blockpos4.getY() > 0; blockpos4 = blockpos7)
                {
                    blockpos7 = blockpos4.down();

                    if (!p_150513_2_.isAirBlock(blockpos7))
                    {
                        break;
                    }
                }

                this.field_76834_x.func_180709_b(p_150513_2_, p_150513_3_, blockpos4);
            }
        }

        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_150513_2_, p_150513_3_, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.SHROOM))
        {
        for (int l3 = 0; l3 < this.field_76798_D; ++l3)
        {
            if (p_150513_3_.nextInt(4) == 0)
            {
                int i8 = p_150513_3_.nextInt(16) + 8;
                int l11 = p_150513_3_.nextInt(16) + 8;
                BlockPos blockpos2 = p_150513_2_.func_175645_m(this.field_180294_c.add(i8, 0, l11));
                this.field_76828_s.func_180709_b(p_150513_2_, p_150513_3_, blockpos2);
            }

            if (p_150513_3_.nextInt(8) == 0)
            {
                int j8 = p_150513_3_.nextInt(16) + 8;
                int i12 = p_150513_3_.nextInt(16) + 8;
                int j15 = p_150513_2_.func_175645_m(this.field_180294_c.add(j8, 0, i12)).getY() * 2;

                if (j15 > 0)
                {
                    int k18 = p_150513_3_.nextInt(j15);
                    BlockPos blockpos5 = this.field_180294_c.add(j8, k18, i12);
                    this.field_76827_t.func_180709_b(p_150513_2_, p_150513_3_, blockpos5);
                }
            }
        }

        if (p_150513_3_.nextInt(4) == 0)
        {
            int i4 = p_150513_3_.nextInt(16) + 8;
            int k8 = p_150513_3_.nextInt(16) + 8;
            int j12 = p_150513_2_.func_175645_m(this.field_180294_c.add(i4, 0, k8)).getY() * 2;

            if (j12 > 0)
            {
                int k15 = p_150513_3_.nextInt(j12);
                this.field_76828_s.func_180709_b(p_150513_2_, p_150513_3_, this.field_180294_c.add(i4, k15, k8));
            }
        }

        if (p_150513_3_.nextInt(8) == 0)
        {
            int j4 = p_150513_3_.nextInt(16) + 8;
            int l8 = p_150513_3_.nextInt(16) + 8;
            int k12 = p_150513_2_.func_175645_m(this.field_180294_c.add(j4, 0, l8)).getY() * 2;

            if (k12 > 0)
            {
                int l15 = p_150513_3_.nextInt(k12);
                this.field_76827_t.func_180709_b(p_150513_2_, p_150513_3_, this.field_180294_c.add(j4, l15, l8));
            }
        }
        } // End of Mushroom generation
        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_150513_2_, p_150513_3_, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.REED))
        {
        for (int k4 = 0; k4 < this.field_76799_E; ++k4)
        {
            int i9 = p_150513_3_.nextInt(16) + 8;
            int l12 = p_150513_3_.nextInt(16) + 8;
            int i16 = p_150513_2_.func_175645_m(this.field_180294_c.add(i9, 0, l12)).getY() * 2;

            if (i16 > 0)
            {
                int l18 = p_150513_3_.nextInt(i16);
                this.field_76825_v.func_180709_b(p_150513_2_, p_150513_3_, this.field_180294_c.add(i9, l18, l12));
            }
        }

        for (int l4 = 0; l4 < 10; ++l4)
        {
            int j9 = p_150513_3_.nextInt(16) + 8;
            int i13 = p_150513_3_.nextInt(16) + 8;
            int j16 = p_150513_2_.func_175645_m(this.field_180294_c.add(j9, 0, i13)).getY() * 2;

            if (j16 > 0)
            {
                int i19 = p_150513_3_.nextInt(j16);
                this.field_76825_v.func_180709_b(p_150513_2_, p_150513_3_, this.field_180294_c.add(j9, i19, i13));
            }
        }
        } // End of Reed generation
        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_150513_2_, p_150513_3_, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.PUMPKIN))
        if (p_150513_3_.nextInt(32) == 0)
        {
            int i5 = p_150513_3_.nextInt(16) + 8;
            int k9 = p_150513_3_.nextInt(16) + 8;
            int j13 = p_150513_2_.func_175645_m(this.field_180294_c.add(i5, 0, k9)).getY() * 2;

            if (j13 > 0)
            {
                int k16 = p_150513_3_.nextInt(j13);
                (new WorldGenPumpkin()).func_180709_b(p_150513_2_, p_150513_3_, this.field_180294_c.add(i5, k16, k9));
            }
        }

        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_150513_2_, p_150513_3_, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.CACTUS))
        for (int j5 = 0; j5 < this.field_76800_F; ++j5)
        {
            int l9 = p_150513_3_.nextInt(16) + 8;
            int k13 = p_150513_3_.nextInt(16) + 8;
            int l16 = p_150513_2_.func_175645_m(this.field_180294_c.add(l9, 0, k13)).getY() * 2;

            if (l16 > 0)
            {
                int j19 = p_150513_3_.nextInt(l16);
                this.field_76824_w.func_180709_b(p_150513_2_, p_150513_3_, this.field_180294_c.add(l9, j19, k13));
            }
        }

        if (this.field_76808_K)
        {
            if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_150513_2_, p_150513_3_, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.LAKE_WATER))
            for (int k5 = 0; k5 < 50; ++k5)
            {
                int i10 = p_150513_3_.nextInt(16) + 8;
                int l13 = p_150513_3_.nextInt(16) + 8;
                int i17 = p_150513_3_.nextInt(248) + 8;

                if (i17 > 0)
                {
                    int k19 = p_150513_3_.nextInt(i17);
                    BlockPos blockpos6 = this.field_180294_c.add(i10, k19, l13);
                    (new WorldGenLiquids(Blocks.field_150358_i)).func_180709_b(p_150513_2_, p_150513_3_, blockpos6);
                }
            }

            if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_150513_2_, p_150513_3_, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.LAKE_LAVA))
            for (int l5 = 0; l5 < 20; ++l5)
            {
                int j10 = p_150513_3_.nextInt(16) + 8;
                int i14 = p_150513_3_.nextInt(16) + 8;
                int j17 = p_150513_3_.nextInt(p_150513_3_.nextInt(p_150513_3_.nextInt(240) + 8) + 8);
                BlockPos blockpos3 = this.field_180294_c.add(j10, j17, i14);
                (new WorldGenLiquids(Blocks.field_150356_k)).func_180709_b(p_150513_2_, p_150513_3_, blockpos3);
            }
        }
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.terraingen.DecorateBiomeEvent.Post(p_150513_2_, p_150513_3_, forgeChunkPos));
    }

    protected void func_76797_b(World p_76797_1_, Random p_76797_2_)
    {
        net.minecraftforge.common.MinecraftForge.ORE_GEN_BUS.post(new net.minecraftforge.event.terraingen.OreGenEvent.Pre(p_76797_1_, p_76797_2_, field_180294_c));
        if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(p_76797_1_, p_76797_2_, field_76823_i, field_180294_c, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.DIRT))
        this.func_76795_a(p_76797_1_, p_76797_2_, this.field_180293_d.field_177790_J, this.field_76823_i, this.field_180293_d.field_177791_K, this.field_180293_d.field_177784_L);
        if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(p_76797_1_, p_76797_2_, field_76820_j, field_180294_c, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.GRAVEL))
        this.func_76795_a(p_76797_1_, p_76797_2_, this.field_180293_d.field_177786_N, this.field_76820_j, this.field_180293_d.field_177787_O, this.field_180293_d.field_177797_P);
        if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(p_76797_1_, p_76797_2_, field_180297_k, field_180294_c, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.DIORITE))
        this.func_76795_a(p_76797_1_, p_76797_2_, this.field_180293_d.field_177795_V, this.field_180297_k, this.field_180293_d.field_177794_W, this.field_180293_d.field_177801_X);
        if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(p_76797_1_, p_76797_2_, field_180296_j, field_180294_c, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.GRANITE))
        this.func_76795_a(p_76797_1_, p_76797_2_, this.field_180293_d.field_177799_R, this.field_180296_j, this.field_180293_d.field_177798_S, this.field_180293_d.field_177793_T);
        if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(p_76797_1_, p_76797_2_, field_180295_l, field_180294_c, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.ANDESITE))
        this.func_76795_a(p_76797_1_, p_76797_2_, this.field_180293_d.field_177802_Z, this.field_180295_l, this.field_180293_d.field_177846_aa, this.field_180293_d.field_177847_ab);
        if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(p_76797_1_, p_76797_2_, field_76821_k, field_180294_c, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.COAL))
        this.func_76795_a(p_76797_1_, p_76797_2_, this.field_180293_d.field_177845_ad, this.field_76821_k, this.field_180293_d.field_177851_ae, this.field_180293_d.field_177853_af);
        if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(p_76797_1_, p_76797_2_, field_76818_l, field_180294_c, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.IRON))
        this.func_76795_a(p_76797_1_, p_76797_2_, this.field_180293_d.field_177849_ah, this.field_76818_l, this.field_180293_d.field_177832_ai, this.field_180293_d.field_177834_aj);
        if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(p_76797_1_, p_76797_2_, field_76819_m, field_180294_c, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.GOLD))
        this.func_76795_a(p_76797_1_, p_76797_2_, this.field_180293_d.field_177830_al, this.field_76819_m, this.field_180293_d.field_177840_am, this.field_180293_d.field_177842_an);
        if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(p_76797_1_, p_76797_2_, field_180299_p, field_180294_c, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.REDSTONE))
        this.func_76795_a(p_76797_1_, p_76797_2_, this.field_180293_d.field_177838_ap, this.field_180299_p, this.field_180293_d.field_177818_aq, this.field_180293_d.field_177816_ar);
        if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(p_76797_1_, p_76797_2_, field_180298_q, field_180294_c, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.DIAMOND))
        this.func_76795_a(p_76797_1_, p_76797_2_, this.field_180293_d.field_177812_at, this.field_180298_q, this.field_180293_d.field_177826_au, this.field_180293_d.field_177824_av);
        if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(p_76797_1_, p_76797_2_, field_76831_p, field_180294_c, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.LAPIS))
        this.func_76793_b(p_76797_1_, p_76797_2_, this.field_180293_d.field_177820_ax, this.field_76831_p, this.field_180293_d.field_177807_ay, this.field_180293_d.field_177805_az);
        net.minecraftforge.common.MinecraftForge.ORE_GEN_BUS.post(new net.minecraftforge.event.terraingen.OreGenEvent.Post(p_76797_1_, p_76797_2_, field_180294_c));
    }

    protected void func_76795_a(World p_76795_1_, Random p_76795_2_, int p_76795_3_, WorldGenerator p_76795_4_, int p_76795_5_, int p_76795_6_)
    {
        if (p_76795_6_ < p_76795_5_)
        {
            int i = p_76795_5_;
            p_76795_5_ = p_76795_6_;
            p_76795_6_ = i;
        }
        else if (p_76795_6_ == p_76795_5_)
        {
            if (p_76795_5_ < 255)
            {
                ++p_76795_6_;
            }
            else
            {
                --p_76795_5_;
            }
        }

        for (int j = 0; j < p_76795_3_; ++j)
        {
            BlockPos blockpos = this.field_180294_c.add(p_76795_2_.nextInt(16), p_76795_2_.nextInt(p_76795_6_ - p_76795_5_) + p_76795_5_, p_76795_2_.nextInt(16));
            p_76795_4_.func_180709_b(p_76795_1_, p_76795_2_, blockpos);
        }
    }

    protected void func_76793_b(World p_76793_1_, Random p_76793_2_, int p_76793_3_, WorldGenerator p_76793_4_, int p_76793_5_, int p_76793_6_)
    {
        for (int i = 0; i < p_76793_3_; ++i)
        {
            BlockPos blockpos = this.field_180294_c.add(p_76793_2_.nextInt(16), p_76793_2_.nextInt(p_76793_6_) + p_76793_2_.nextInt(p_76793_6_) + p_76793_5_ - p_76793_6_, p_76793_2_.nextInt(16));
            p_76793_4_.func_180709_b(p_76793_1_, p_76793_2_, blockpos);
        }
    }
}