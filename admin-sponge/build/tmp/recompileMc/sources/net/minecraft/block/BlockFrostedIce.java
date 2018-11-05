package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlockFrostedIce extends BlockIce
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);

    public BlockFrostedIce()
    {
        this.setDefaultState(this.stateContainer.getBaseState().func_177226_a(AGE, Integer.valueOf(0)));
    }

    public int func_176201_c(IBlockState p_176201_1_)
    {
        return ((Integer)p_176201_1_.get(AGE)).intValue();
    }

    public IBlockState func_176203_a(int p_176203_1_)
    {
        return this.getDefaultState().func_177226_a(AGE, Integer.valueOf(MathHelper.clamp(p_176203_1_, 0, 3)));
    }

    public void func_180650_b(World p_180650_1_, BlockPos p_180650_2_, IBlockState p_180650_3_, Random p_180650_4_)
    {
        if ((p_180650_4_.nextInt(3) == 0 || this.func_185680_c(p_180650_1_, p_180650_2_) < 4) && p_180650_1_.func_175671_l(p_180650_2_) > 11 - ((Integer)p_180650_3_.get(AGE)).intValue() - p_180650_3_.func_185891_c())
        {
            this.func_185681_a(p_180650_1_, p_180650_2_, p_180650_3_, p_180650_4_, true);
        }
        else
        {
            p_180650_1_.func_175684_a(p_180650_2_, this, MathHelper.nextInt(p_180650_4_, 20, 40));
        }
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (blockIn == this)
        {
            int i = this.func_185680_c(worldIn, pos);

            if (i < 2)
            {
                this.func_185679_b(worldIn, pos);
            }
        }
    }

    private int func_185680_c(World p_185680_1_, BlockPos p_185680_2_)
    {
        int i = 0;

        for (EnumFacing enumfacing : EnumFacing.values())
        {
            if (p_185680_1_.getBlockState(p_185680_2_.offset(enumfacing)).getBlock() == this)
            {
                ++i;

                if (i >= 4)
                {
                    return i;
                }
            }
        }

        return i;
    }

    protected void func_185681_a(World p_185681_1_, BlockPos p_185681_2_, IBlockState p_185681_3_, Random p_185681_4_, boolean p_185681_5_)
    {
        int i = ((Integer)p_185681_3_.get(AGE)).intValue();

        if (i < 3)
        {
            p_185681_1_.setBlockState(p_185681_2_, p_185681_3_.func_177226_a(AGE, Integer.valueOf(i + 1)), 2);
            p_185681_1_.func_175684_a(p_185681_2_, this, MathHelper.nextInt(p_185681_4_, 20, 40));
        }
        else
        {
            this.func_185679_b(p_185681_1_, p_185681_2_);

            if (p_185681_5_)
            {
                for (EnumFacing enumfacing : EnumFacing.values())
                {
                    BlockPos blockpos = p_185681_2_.offset(enumfacing);
                    IBlockState iblockstate = p_185681_1_.getBlockState(blockpos);

                    if (iblockstate.getBlock() == this)
                    {
                        this.func_185681_a(p_185681_1_, blockpos, iblockstate, p_185681_4_, false);
                    }
                }
            }
        }
    }

    protected BlockStateContainer func_180661_e()
    {
        return new BlockStateContainer(this, new IProperty[] {AGE});
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return ItemStack.EMPTY;
    }
}