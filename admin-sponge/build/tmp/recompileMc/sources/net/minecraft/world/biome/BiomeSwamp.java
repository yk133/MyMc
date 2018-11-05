package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenFossils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BiomeSwamp extends Biome
{
    protected static final IBlockState field_185387_y = Blocks.field_150392_bi.getDefaultState();

    protected BiomeSwamp(Biome.BiomeProperties p_i46695_1_)
    {
        super(p_i46695_1_);
        this.field_76760_I.field_76832_z = 2;
        this.field_76760_I.field_76802_A = 1;
        this.field_76760_I.field_76804_C = 1;
        this.field_76760_I.field_76798_D = 8;
        this.field_76760_I.field_76799_E = 10;
        this.field_76760_I.field_76806_I = 1;
        this.field_76760_I.field_76833_y = 4;
        this.field_76760_I.field_76805_H = 0;
        this.field_76760_I.field_76801_G = 0;
        this.field_76760_I.field_76803_B = 5;
        this.field_76761_J.add(new Biome.SpawnListEntry(EntitySlime.class, 1, 1, 1));
    }

    public WorldGenAbstractTree func_150567_a(Random p_150567_1_)
    {
        return field_76763_Q;
    }

    public BlockFlower.EnumFlowerType func_180623_a(Random p_180623_1_, BlockPos p_180623_2_)
    {
        return BlockFlower.EnumFlowerType.BLUE_ORCHID;
    }

    public void func_180622_a(World p_180622_1_, Random p_180622_2_, ChunkPrimer p_180622_3_, int p_180622_4_, int p_180622_5_, double p_180622_6_)
    {
        double d0 = INFO_NOISE.getValue((double)p_180622_4_ * 0.25D, (double)p_180622_5_ * 0.25D);

        if (d0 > 0.0D)
        {
            int i = p_180622_4_ & 15;
            int j = p_180622_5_ & 15;

            for (int k = 255; k >= 0; --k)
            {
                if (p_180622_3_.func_177856_a(j, k, i).getMaterial() != Material.AIR)
                {
                    if (k == 62 && p_180622_3_.func_177856_a(j, k, i).getBlock() != Blocks.WATER)
                    {
                        p_180622_3_.func_177855_a(j, k, i, field_185372_h);

                        if (d0 < 0.12D)
                        {
                            p_180622_3_.func_177855_a(j, k + 1, i, field_185387_y);
                        }
                    }

                    break;
                }
            }
        }

        this.func_180628_b(p_180622_1_, p_180622_2_, p_180622_3_, p_180622_4_, p_180622_5_, p_180622_6_);
    }

    public void func_180624_a(World p_180624_1_, Random p_180624_2_, BlockPos p_180624_3_)
    {
        super.func_180624_a(p_180624_1_, p_180624_2_, p_180624_3_);

        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_180624_1_, p_180624_2_, new net.minecraft.util.math.ChunkPos(p_180624_3_), net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.FOSSIL))
        if (p_180624_2_.nextInt(64) == 0)
        {
            (new WorldGenFossils()).func_180709_b(p_180624_1_, p_180624_2_, p_180624_3_);
        }
    }

    @SideOnly(Side.CLIENT)
    public int getGrassColor(BlockPos pos)
    {
        double d0 = INFO_NOISE.getValue((double)pos.getX() * 0.0225D, (double)pos.getZ() * 0.0225D);
        return getModdedBiomeGrassColor(d0 < -0.1D ? 5011004 : 6975545);
    }

    @SideOnly(Side.CLIENT)
    public int getFoliageColor(BlockPos pos)
    {
        return getModdedBiomeFoliageColor(6975545);
    }

    @Override
    public void addDefaultFlowers()
    {
        addFlower(Blocks.field_150328_O.getDefaultState().func_177226_a(Blocks.field_150328_O.func_176494_l(), BlockFlower.EnumFlowerType.BLUE_ORCHID), 10);
    }
}