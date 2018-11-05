package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class WorldGenHugeTrees extends WorldGenAbstractTree
{
    protected final int baseHeight;
    protected final IBlockState trunk;
    protected final IBlockState leaf;
    protected int extraRandomHeight;

    public WorldGenHugeTrees(boolean notify, int baseHeightIn, int extraRandomHeightIn, IBlockState woodMetadataIn, IBlockState leavesMetadataIn)
    {
        super(notify);
        this.baseHeight = baseHeightIn;
        this.extraRandomHeight = extraRandomHeightIn;
        this.trunk = woodMetadataIn;
        this.leaf = leavesMetadataIn;
    }

    /**
     * calculates the height based on this trees base height and its extra random height
     */
    protected int getHeight(Random rand)
    {
        int i = rand.nextInt(3) + this.baseHeight;

        if (this.extraRandomHeight > 1)
        {
            i += rand.nextInt(this.extraRandomHeight);
        }

        return i;
    }

    /**
     * returns whether or not there is space for a tree to grow at a certain position
     */
    private boolean isSpaceAt(World worldIn, BlockPos leavesPos, int height)
    {
        boolean flag = true;

        if (leavesPos.getY() >= 1 && leavesPos.getY() + height + 1 <= 256)
        {
            for (int i = 0; i <= 1 + height; ++i)
            {
                int j = 2;

                if (i == 0)
                {
                    j = 1;
                }
                else if (i >= 1 + height - 2)
                {
                    j = 2;
                }

                for (int k = -j; k <= j && flag; ++k)
                {
                    for (int l = -j; l <= j && flag; ++l)
                    {
                        if (leavesPos.getY() + i < 0 || leavesPos.getY() + i >= 256 || !this.isReplaceable(worldIn,leavesPos.add(k, i, l)))
                        {
                            flag = false;
                        }
                    }
                }
            }

            return flag;
        }
        else
        {
            return false;
        }
    }

    private boolean func_175927_a(BlockPos p_175927_1_, World p_175927_2_)
    {
        BlockPos blockpos = p_175927_1_.down();
        IBlockState state = p_175927_2_.getBlockState(blockpos);
        boolean isSoil = state.getBlock().canSustainPlant(state, p_175927_2_, blockpos, net.minecraft.util.EnumFacing.UP, ((net.minecraft.block.BlockSapling)Blocks.field_150345_g));

        if (isSoil && p_175927_1_.getY() >= 2)
        {
            this.onPlantGrow(p_175927_2_, blockpos, p_175927_1_);
            this.onPlantGrow(p_175927_2_, blockpos.east(), p_175927_1_);
            this.onPlantGrow(p_175927_2_, blockpos.south(), p_175927_1_);
            this.onPlantGrow(p_175927_2_, blockpos.south().east(), p_175927_1_);
            return true;
        }
        else
        {
            return false;
        }
    }

    protected boolean func_175929_a(World p_175929_1_, Random p_175929_2_, BlockPos p_175929_3_, int p_175929_4_)
    {
        return this.isSpaceAt(p_175929_1_, p_175929_3_, p_175929_4_) && this.func_175927_a(p_175929_3_, p_175929_1_);
    }

    /**
     * grow leaves in a circle with the outsides being within the circle
     */
    protected void growLeavesLayerStrict(World worldIn, BlockPos layerCenter, int width)
    {
        int i = width * width;

        for (int j = -width; j <= width + 1; ++j)
        {
            for (int k = -width; k <= width + 1; ++k)
            {
                int l = j - 1;
                int i1 = k - 1;

                if (j * j + k * k <= i || l * l + i1 * i1 <= i || j * j + i1 * i1 <= i || l * l + k * k <= i)
                {
                    BlockPos blockpos = layerCenter.add(j, 0, k);
                    IBlockState state = worldIn.getBlockState(blockpos);

                    if (state.getBlock().isAir(state, worldIn, blockpos) || state.getBlock().isLeaves(state, worldIn, blockpos))
                    {
                        this.func_175903_a(worldIn, blockpos, this.leaf);
                    }
                }
            }
        }
    }

    /**
     * grow leaves in a circle
     */
    protected void growLeavesLayer(World worldIn, BlockPos layerCenter, int width)
    {
        int i = width * width;

        for (int j = -width; j <= width; ++j)
        {
            for (int k = -width; k <= width; ++k)
            {
                if (j * j + k * k <= i)
                {
                    BlockPos blockpos = layerCenter.add(j, 0, k);
                    IBlockState state = worldIn.getBlockState(blockpos);

                    if (state.getBlock().isAir(state, worldIn, blockpos) || state.getBlock().isLeaves(state, worldIn, blockpos))
                    {
                        this.func_175903_a(worldIn, blockpos, this.leaf);
                    }
                }
            }
        }
    }

    //Just a helper macro
    private void onPlantGrow(World world, BlockPos pos, BlockPos source)
    {
        IBlockState state = world.getBlockState(pos);
        state.getBlock().onPlantGrow(state, world, pos, source);
    }
}