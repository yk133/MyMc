package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStaticLiquid extends BlockLiquid
{
    protected BlockStaticLiquid(Material p_i45429_1_)
    {
        super(p_i45429_1_);
        this.func_149675_a(false);

        if (p_i45429_1_ == Material.LAVA)
        {
            this.func_149675_a(true);
        }
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!this.func_176365_e(worldIn, pos, state))
        {
            this.func_176370_f(worldIn, pos, state);
        }
    }

    private void func_176370_f(World p_176370_1_, BlockPos p_176370_2_, IBlockState p_176370_3_)
    {
        BlockDynamicLiquid blockdynamicliquid = func_176361_a(this.material);
        p_176370_1_.setBlockState(p_176370_2_, blockdynamicliquid.getDefaultState().func_177226_a(LEVEL, p_176370_3_.get(LEVEL)), 2);
        p_176370_1_.func_175684_a(p_176370_2_, blockdynamicliquid, this.tickRate(p_176370_1_));
    }

    public void func_180650_b(World p_180650_1_, BlockPos p_180650_2_, IBlockState p_180650_3_, Random p_180650_4_)
    {
        if (this.material == Material.LAVA)
        {
            if (p_180650_1_.getGameRules().getBoolean("doFireTick"))
            {
                int i = p_180650_4_.nextInt(3);

                if (i > 0)
                {
                    BlockPos blockpos = p_180650_2_;

                    for (int j = 0; j < i; ++j)
                    {
                        blockpos = blockpos.add(p_180650_4_.nextInt(3) - 1, 1, p_180650_4_.nextInt(3) - 1);

                        if (blockpos.getY() >= 0 && blockpos.getY() < p_180650_1_.getHeight() && !p_180650_1_.isBlockLoaded(blockpos))
                        {
                            return;
                        }

                        IBlockState block = p_180650_1_.getBlockState(blockpos);

                        if (block.getBlock().isAir(block, p_180650_1_, blockpos))
                        {
                            if (this.isSurroundingBlockFlammable(p_180650_1_, blockpos))
                            {
                                p_180650_1_.setBlockState(blockpos, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(p_180650_1_, blockpos, p_180650_2_, Blocks.FIRE.getDefaultState()));
                                return;
                            }
                        }
                        else if (block.getMaterial().blocksMovement())
                        {
                            return;
                        }
                    }
                }
                else
                {
                    for (int k = 0; k < 3; ++k)
                    {
                        BlockPos blockpos1 = p_180650_2_.add(p_180650_4_.nextInt(3) - 1, 0, p_180650_4_.nextInt(3) - 1);

                        if (blockpos1.getY() >= 0 && blockpos1.getY() < 256 && !p_180650_1_.isBlockLoaded(blockpos1))
                        {
                            return;
                        }

                        if (p_180650_1_.isAirBlock(blockpos1.up()) && this.getCanBlockBurn(p_180650_1_, blockpos1))
                        {
                            p_180650_1_.setBlockState(blockpos1.up(), net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(p_180650_1_, blockpos1.up(), p_180650_2_, Blocks.FIRE.getDefaultState()));
                        }
                    }
                }
            }
        }
    }

    protected boolean isSurroundingBlockFlammable(World worldIn, BlockPos pos)
    {
        for (EnumFacing enumfacing : EnumFacing.values())
        {
            if (this.getCanBlockBurn(worldIn, pos.offset(enumfacing)))
            {
                return true;
            }
        }

        return false;
    }

    private boolean getCanBlockBurn(World worldIn, BlockPos pos)
    {
        return pos.getY() >= 0 && pos.getY() < 256 && !worldIn.isBlockLoaded(pos) ? false : worldIn.getBlockState(pos).getMaterial().isFlammable();
    }
}