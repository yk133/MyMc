package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomePlains extends Biome
{
    protected boolean field_150628_aC;

    public BiomePlains(boolean p_i46699_1_, Biome.BiomeProperties p_i46699_2_)
    {
        super(p_i46699_2_);
        this.field_150628_aC = p_i46699_1_;
        this.field_76762_K.add(new Biome.SpawnListEntry(EntityHorse.class, 5, 2, 6));
        this.field_76762_K.add(new Biome.SpawnListEntry(EntityDonkey.class, 1, 1, 3));
        this.field_76760_I.field_76832_z = 0;
        this.field_76760_I.field_189870_A = 0.05F;
        this.field_76760_I.field_76802_A = 4;
        this.field_76760_I.field_76803_B = 10;
    }

    public BlockFlower.EnumFlowerType func_180623_a(Random p_180623_1_, BlockPos p_180623_2_)
    {
        double d0 = INFO_NOISE.getValue((double)p_180623_2_.getX() / 200.0D, (double)p_180623_2_.getZ() / 200.0D);

        if (d0 < -0.8D)
        {
            int j = p_180623_1_.nextInt(4);

            switch (j)
            {
                case 0:
                    return BlockFlower.EnumFlowerType.ORANGE_TULIP;
                case 1:
                    return BlockFlower.EnumFlowerType.RED_TULIP;
                case 2:
                    return BlockFlower.EnumFlowerType.PINK_TULIP;
                case 3:
                default:
                    return BlockFlower.EnumFlowerType.WHITE_TULIP;
            }
        }
        else if (p_180623_1_.nextInt(3) > 0)
        {
            int i = p_180623_1_.nextInt(3);

            if (i == 0)
            {
                return BlockFlower.EnumFlowerType.POPPY;
            }
            else
            {
                return i == 1 ? BlockFlower.EnumFlowerType.HOUSTONIA : BlockFlower.EnumFlowerType.OXEYE_DAISY;
            }
        }
        else
        {
            return BlockFlower.EnumFlowerType.DANDELION;
        }
    }

    public void func_180624_a(World p_180624_1_, Random p_180624_2_, BlockPos p_180624_3_)
    {
        double d0 = INFO_NOISE.getValue((double)(p_180624_3_.getX() + 8) / 200.0D, (double)(p_180624_3_.getZ() + 8) / 200.0D);

        if (d0 < -0.8D)
        {
            this.field_76760_I.field_76802_A = 15;
            this.field_76760_I.field_76803_B = 5;
        }
        else
        {
            this.field_76760_I.field_76802_A = 4;
            this.field_76760_I.field_76803_B = 10;
            field_180280_ag.func_180710_a(BlockDoublePlant.EnumPlantType.GRASS);

            if(net.minecraftforge.event.terraingen.TerrainGen.decorate(p_180624_1_, p_180624_2_, new net.minecraft.util.math.ChunkPos(p_180624_3_), net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.GRASS))
            for (int i = 0; i < 7; ++i)
            {
                int j = p_180624_2_.nextInt(16) + 8;
                int k = p_180624_2_.nextInt(16) + 8;
                int l = p_180624_2_.nextInt(p_180624_1_.func_175645_m(p_180624_3_.add(j, 0, k)).getY() + 32);
                field_180280_ag.func_180709_b(p_180624_1_, p_180624_2_, p_180624_3_.add(j, l, k));
            }
        }

        if (this.field_150628_aC && net.minecraftforge.event.terraingen.TerrainGen.decorate(p_180624_1_, p_180624_2_, new net.minecraft.util.math.ChunkPos(p_180624_3_), net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.FLOWERS))
        {
            field_180280_ag.func_180710_a(BlockDoublePlant.EnumPlantType.SUNFLOWER);

            for (int i1 = 0; i1 < 10; ++i1)
            {
                int j1 = p_180624_2_.nextInt(16) + 8;
                int k1 = p_180624_2_.nextInt(16) + 8;
                int l1 = p_180624_2_.nextInt(p_180624_1_.func_175645_m(p_180624_3_.add(j1, 0, k1)).getY() + 32);
                field_180280_ag.func_180709_b(p_180624_1_, p_180624_2_, p_180624_3_.add(j1, l1, k1));
            }
        }

        super.func_180624_a(p_180624_1_, p_180624_2_, p_180624_3_);
    }

    @Override
    public void addDefaultFlowers()
    {
        BlockFlower red = net.minecraft.init.Blocks.field_150328_O;
        BlockFlower yel = net.minecraft.init.Blocks.field_150327_N;
        addFlower(red.getDefaultState().func_177226_a(red.func_176494_l(), BlockFlower.EnumFlowerType.ORANGE_TULIP), 3);
        addFlower(red.getDefaultState().func_177226_a(red.func_176494_l(), BlockFlower.EnumFlowerType.RED_TULIP), 3);
        addFlower(red.getDefaultState().func_177226_a(red.func_176494_l(), BlockFlower.EnumFlowerType.PINK_TULIP), 3);
        addFlower(red.getDefaultState().func_177226_a(red.func_176494_l(), BlockFlower.EnumFlowerType.WHITE_TULIP), 3);
        addFlower(red.getDefaultState().func_177226_a(red.func_176494_l(), BlockFlower.EnumFlowerType.POPPY), 20);
        addFlower(red.getDefaultState().func_177226_a(red.func_176494_l(), BlockFlower.EnumFlowerType.HOUSTONIA), 20);
        addFlower(red.getDefaultState().func_177226_a(red.func_176494_l(), BlockFlower.EnumFlowerType.OXEYE_DAISY), 20);
        addFlower(yel.getDefaultState().func_177226_a(yel.func_176494_l(), BlockFlower.EnumFlowerType.DANDELION), 30);
    }

    public WorldGenAbstractTree func_150567_a(Random p_150567_1_)
    {
        return (WorldGenAbstractTree)(p_150567_1_.nextInt(3) == 0 ? field_76758_O : field_76757_N);
    }
}