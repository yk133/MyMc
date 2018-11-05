package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockStainedHardenedClay extends BlockColored
{
    private static final MapColor[] field_193389_b = new MapColor[] {MapColor.WHITE_TERRACOTTA, MapColor.ORANGE_TERRACOTTA, MapColor.MAGENTA_TERRACOTTA, MapColor.LIGHT_BLUE_TERRACOTTA, MapColor.YELLOW_TERRACOTTA, MapColor.LIME_TERRACOTTA, MapColor.PINK_TERRACOTTA, MapColor.GRAY_TERRACOTTA, MapColor.field_193569_U, MapColor.CYAN_TERRACOTTA, MapColor.PURPLE_TERRACOTTA, MapColor.BLUE_TERRACOTTA, MapColor.BROWN_TERRACOTTA, MapColor.GREEN_TERRACOTTA, MapColor.RED_TERRACOTTA, MapColor.BLACK_TERRACOTTA};

    public BlockStainedHardenedClay()
    {
        super(Material.ROCK);
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     * @deprecated call via {@link IBlockState#getMapColor(IBlockAccess,BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return field_193389_b[((EnumDyeColor)state.get(field_176581_a)).func_176765_a()];
    }
}