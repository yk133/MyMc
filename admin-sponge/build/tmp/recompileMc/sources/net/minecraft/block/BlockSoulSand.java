package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSoulSand extends Block
{
    protected static final AxisAlignedBB field_185703_a = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D);

    public BlockSoulSand()
    {
        super(Material.SAND, MapColor.BROWN);
        this.func_149647_a(CreativeTabs.BUILDING_BLOCKS);
    }

    @Nullable
    public AxisAlignedBB func_180646_a(IBlockState p_180646_1_, IBlockAccess p_180646_2_, BlockPos p_180646_3_)
    {
        return field_185703_a;
    }

    public void func_180634_a(World p_180634_1_, BlockPos p_180634_2_, IBlockState p_180634_3_, Entity p_180634_4_)
    {
        p_180634_4_.motionX *= 0.4D;
        p_180634_4_.motionZ *= 0.4D;
    }
}