package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeHills extends Biome
{
    private final WorldGenerator field_82915_S = new WorldGenMinable(Blocks.field_150418_aU.getDefaultState().func_177226_a(BlockSilverfish.field_176378_a, BlockSilverfish.EnumType.STONE), 9);
    private final WorldGenTaiga2 field_150634_aD = new WorldGenTaiga2(false);
    private final BiomeHills.Type field_150638_aH;

    public BiomeHills(BiomeHills.Type p_i46710_1_, Biome.BiomeProperties p_i46710_2_)
    {
        super(p_i46710_2_);

        if (p_i46710_1_ == BiomeHills.Type.EXTRA_TREES)
        {
            this.field_76760_I.field_76832_z = 3;
        }

        this.field_76762_K.add(new Biome.SpawnListEntry(EntityLlama.class, 5, 4, 6));
        this.field_150638_aH = p_i46710_1_;
    }

    public WorldGenAbstractTree func_150567_a(Random p_150567_1_)
    {
        return (WorldGenAbstractTree)(p_150567_1_.nextInt(3) > 0 ? this.field_150634_aD : super.func_150567_a(p_150567_1_));
    }

    public void func_180624_a(World p_180624_1_, Random p_180624_2_, BlockPos p_180624_3_)
    {
        super.func_180624_a(p_180624_1_, p_180624_2_, p_180624_3_);

        net.minecraftforge.common.MinecraftForge.ORE_GEN_BUS.post(new net.minecraftforge.event.terraingen.OreGenEvent.Pre(p_180624_1_, p_180624_2_, p_180624_3_));
        WorldGenerator emeralds = new EmeraldGenerator();
        if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(p_180624_1_, p_180624_2_, emeralds, p_180624_3_, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.EMERALD))
            emeralds.func_180709_b(p_180624_1_, p_180624_2_, p_180624_3_);

        for (int j1 = 0; j1 < 7; ++j1)
        {
            int k1 = p_180624_2_.nextInt(16);
            int l1 = p_180624_2_.nextInt(64);
            int i2 = p_180624_2_.nextInt(16);
            if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(p_180624_1_, p_180624_2_, field_82915_S, p_180624_3_.add(j1, k1, l1), net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.SILVERFISH))
            this.field_82915_S.func_180709_b(p_180624_1_, p_180624_2_, p_180624_3_.add(k1, l1, i2));
        }
        net.minecraftforge.common.MinecraftForge.ORE_GEN_BUS.post(new net.minecraftforge.event.terraingen.OreGenEvent.Post(p_180624_1_, p_180624_2_, p_180624_3_));
    }

    public void func_180622_a(World p_180622_1_, Random p_180622_2_, ChunkPrimer p_180622_3_, int p_180622_4_, int p_180622_5_, double p_180622_6_)
    {
        this.field_76752_A = Blocks.GRASS.getDefaultState();
        this.field_76753_B = Blocks.DIRT.getDefaultState();

        if ((p_180622_6_ < -1.0D || p_180622_6_ > 2.0D) && this.field_150638_aH == BiomeHills.Type.MUTATED)
        {
            this.field_76752_A = Blocks.GRAVEL.getDefaultState();
            this.field_76753_B = Blocks.GRAVEL.getDefaultState();
        }
        else if (p_180622_6_ > 1.0D && this.field_150638_aH != BiomeHills.Type.EXTRA_TREES)
        {
            this.field_76752_A = Blocks.STONE.getDefaultState();
            this.field_76753_B = Blocks.STONE.getDefaultState();
        }

        this.func_180628_b(p_180622_1_, p_180622_2_, p_180622_3_, p_180622_4_, p_180622_5_, p_180622_6_);
    }

    public static enum Type
    {
        NORMAL,
        EXTRA_TREES,
        MUTATED;
    }

    private static class EmeraldGenerator extends WorldGenerator
    {
        @Override
        public boolean func_180709_b(World worldIn, Random rand, BlockPos pos)
        {
            int count = 3 + rand.nextInt(6);
            for (int i = 0; i < count; i++)
            {
                int offset = net.minecraftforge.common.ForgeModContainer.fixVanillaCascading ? 8 : 0; // MC-114332
                BlockPos blockpos = pos.add(rand.nextInt(16) + offset, rand.nextInt(28) + 4, rand.nextInt(16) + offset);

                net.minecraft.block.state.IBlockState state = worldIn.getBlockState(blockpos);
                if (state.getBlock().isReplaceableOreGen(state, worldIn, blockpos, net.minecraft.block.state.pattern.BlockMatcher.forBlock(Blocks.STONE)))
                {
                    worldIn.setBlockState(blockpos, Blocks.EMERALD_ORE.getDefaultState(), 16 | 2);
                }
            }
            return true;
        }
    }
}