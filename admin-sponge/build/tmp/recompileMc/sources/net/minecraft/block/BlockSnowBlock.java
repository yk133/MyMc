package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class BlockSnowBlock extends Block
{
    protected BlockSnowBlock()
    {
        super(Material.CRAFTED_SNOW);
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.BUILDING_BLOCKS);
    }

    public Item func_180660_a(IBlockState p_180660_1_, Random p_180660_2_, int p_180660_3_)
    {
        return Items.SNOWBALL;
    }

    public int func_149745_a(Random p_149745_1_)
    {
        return 4;
    }

    public void func_180650_b(World p_180650_1_, BlockPos p_180650_2_, IBlockState p_180650_3_, Random p_180650_4_)
    {
        if (p_180650_1_.getLightFor(EnumSkyBlock.BLOCK, p_180650_2_) > 11)
        {
            this.func_176226_b(p_180650_1_, p_180650_2_, p_180650_1_.getBlockState(p_180650_2_), 0);
            p_180650_1_.removeBlock(p_180650_2_);
        }
    }
}