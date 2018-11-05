package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRedstoneLight extends Block
{
    private final boolean field_150171_a;

    public BlockRedstoneLight(boolean p_i45421_1_)
    {
        super(Material.REDSTONE_LIGHT);
        this.field_150171_a = p_i45421_1_;

        if (p_i45421_1_)
        {
            this.func_149715_a(1.0F);
        }
    }

    public void func_176213_c(World p_176213_1_, BlockPos p_176213_2_, IBlockState p_176213_3_)
    {
        if (!p_176213_1_.isRemote)
        {
            if (this.field_150171_a && !p_176213_1_.isBlockPowered(p_176213_2_))
            {
                p_176213_1_.setBlockState(p_176213_2_, Blocks.REDSTONE_LAMP.getDefaultState(), 2);
            }
            else if (!this.field_150171_a && p_176213_1_.isBlockPowered(p_176213_2_))
            {
                p_176213_1_.setBlockState(p_176213_2_, Blocks.field_150374_bv.getDefaultState(), 2);
            }
        }
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            if (this.field_150171_a && !worldIn.isBlockPowered(pos))
            {
                worldIn.func_175684_a(pos, this, 4);
            }
            else if (!this.field_150171_a && worldIn.isBlockPowered(pos))
            {
                worldIn.setBlockState(pos, Blocks.field_150374_bv.getDefaultState(), 2);
            }
        }
    }

    public void func_180650_b(World p_180650_1_, BlockPos p_180650_2_, IBlockState p_180650_3_, Random p_180650_4_)
    {
        if (!p_180650_1_.isRemote)
        {
            if (this.field_150171_a && !p_180650_1_.isBlockPowered(p_180650_2_))
            {
                p_180650_1_.setBlockState(p_180650_2_, Blocks.REDSTONE_LAMP.getDefaultState(), 2);
            }
        }
    }

    public Item func_180660_a(IBlockState p_180660_1_, Random p_180660_2_, int p_180660_3_)
    {
        return Item.getItemFromBlock(Blocks.REDSTONE_LAMP);
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(Blocks.REDSTONE_LAMP);
    }

    protected ItemStack getSilkTouchDrop(IBlockState state)
    {
        return new ItemStack(Blocks.REDSTONE_LAMP);
    }
}