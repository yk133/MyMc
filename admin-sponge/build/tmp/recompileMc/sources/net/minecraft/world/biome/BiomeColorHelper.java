package net.minecraft.world.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BiomeColorHelper
{
    private static final BiomeColorHelper.ColorResolver GRASS_COLOR = new BiomeColorHelper.ColorResolver()
    {
        public int func_180283_a(Biome p_180283_1_, BlockPos p_180283_2_)
        {
            return p_180283_1_.getGrassColor(p_180283_2_);
        }
    };
    private static final BiomeColorHelper.ColorResolver FOLIAGE_COLOR = new BiomeColorHelper.ColorResolver()
    {
        public int func_180283_a(Biome p_180283_1_, BlockPos p_180283_2_)
        {
            return p_180283_1_.getFoliageColor(p_180283_2_);
        }
    };
    private static final BiomeColorHelper.ColorResolver WATER_COLOR = new BiomeColorHelper.ColorResolver()
    {
        public int func_180283_a(Biome p_180283_1_, BlockPos p_180283_2_)
        {
            return p_180283_1_.getWaterColor();
        }
    };

    private static int getColor(IBlockAccess worldIn, BlockPos pos, BiomeColorHelper.ColorResolver resolver)
    {
        int i = 0;
        int j = 0;
        int k = 0;

        for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(pos.add(-1, 0, -1), pos.add(1, 0, 1)))
        {
            int l = resolver.func_180283_a(worldIn.getBiome(blockpos$mutableblockpos), blockpos$mutableblockpos);
            i += (l & 16711680) >> 16;
            j += (l & 65280) >> 8;
            k += l & 255;
        }

        return (i / 9 & 255) << 16 | (j / 9 & 255) << 8 | k / 9 & 255;
    }

    public static int getGrassColor(IBlockAccess worldIn, BlockPos pos)
    {
        return getColor(worldIn, pos, GRASS_COLOR);
    }

    public static int getFoliageColor(IBlockAccess worldIn, BlockPos pos)
    {
        return getColor(worldIn, pos, FOLIAGE_COLOR);
    }

    public static int getWaterColor(IBlockAccess worldIn, BlockPos pos)
    {
        return getColor(worldIn, pos, WATER_COLOR);
    }

    @SideOnly(Side.CLIENT)
    interface ColorResolver
    {
        int func_180283_a(Biome p_180283_1_, BlockPos p_180283_2_);
    }
}