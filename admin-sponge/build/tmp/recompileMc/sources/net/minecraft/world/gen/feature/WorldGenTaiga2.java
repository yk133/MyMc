package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenTaiga2 extends WorldGenAbstractTree
{
    private static final IBlockState TRUNK = Blocks.field_150364_r.getDefaultState().func_177226_a(BlockOldLog.field_176301_b, BlockPlanks.EnumType.SPRUCE);
    private static final IBlockState LEAF = Blocks.field_150362_t.getDefaultState().func_177226_a(BlockOldLeaf.field_176239_P, BlockPlanks.EnumType.SPRUCE).func_177226_a(BlockLeaves.field_176236_b, Boolean.valueOf(false));

    public WorldGenTaiga2(boolean p_i2025_1_)
    {
        super(p_i2025_1_);
    }

    public boolean func_180709_b(World p_180709_1_, Random p_180709_2_, BlockPos p_180709_3_)
    {
        int i = p_180709_2_.nextInt(4) + 6;
        int j = 1 + p_180709_2_.nextInt(2);
        int k = i - j;
        int l = 2 + p_180709_2_.nextInt(2);
        boolean flag = true;

        if (p_180709_3_.getY() >= 1 && p_180709_3_.getY() + i + 1 <= p_180709_1_.getHeight())
        {
            for (int i1 = p_180709_3_.getY(); i1 <= p_180709_3_.getY() + 1 + i && flag; ++i1)
            {
                int j1;

                if (i1 - p_180709_3_.getY() < j)
                {
                    j1 = 0;
                }
                else
                {
                    j1 = l;
                }

                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                for (int k1 = p_180709_3_.getX() - j1; k1 <= p_180709_3_.getX() + j1 && flag; ++k1)
                {
                    for (int l1 = p_180709_3_.getZ() - j1; l1 <= p_180709_3_.getZ() + j1 && flag; ++l1)
                    {
                        if (i1 >= 0 && i1 < p_180709_1_.getHeight())
                        {
                            IBlockState state = p_180709_1_.getBlockState(blockpos$mutableblockpos.setPos(k1, i1, l1));

                            if (!state.getBlock().isAir(state, p_180709_1_, blockpos$mutableblockpos.setPos(k1, i1, l1)) && !state.getBlock().isLeaves(state, p_180709_1_, blockpos$mutableblockpos.setPos(k1, i1, l1)))
                            {
                                flag = false;
                            }
                        }
                        else
                        {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag)
            {
                return false;
            }
            else
            {
                BlockPos down = p_180709_3_.down();
                IBlockState state = p_180709_1_.getBlockState(down);

                if (state.getBlock().canSustainPlant(state, p_180709_1_, down, net.minecraft.util.EnumFacing.UP, (net.minecraft.block.BlockSapling)Blocks.field_150345_g) && p_180709_3_.getY() < p_180709_1_.getHeight() - i - 1)
                {
                    state.getBlock().onPlantGrow(state, p_180709_1_, down, p_180709_3_);
                    int i3 = p_180709_2_.nextInt(2);
                    int j3 = 1;
                    int k3 = 0;

                    for (int l3 = 0; l3 <= k; ++l3)
                    {
                        int j4 = p_180709_3_.getY() + i - l3;

                        for (int i2 = p_180709_3_.getX() - i3; i2 <= p_180709_3_.getX() + i3; ++i2)
                        {
                            int j2 = i2 - p_180709_3_.getX();

                            for (int k2 = p_180709_3_.getZ() - i3; k2 <= p_180709_3_.getZ() + i3; ++k2)
                            {
                                int l2 = k2 - p_180709_3_.getZ();

                                if (Math.abs(j2) != i3 || Math.abs(l2) != i3 || i3 <= 0)
                                {
                                    BlockPos blockpos = new BlockPos(i2, j4, k2);
                                    state = p_180709_1_.getBlockState(blockpos);

                                    if (state.getBlock().canBeReplacedByLeaves(state, p_180709_1_, blockpos))
                                    {
                                        this.func_175903_a(p_180709_1_, blockpos, LEAF);
                                    }
                                }
                            }
                        }

                        if (i3 >= j3)
                        {
                            i3 = k3;
                            k3 = 1;
                            ++j3;

                            if (j3 > l)
                            {
                                j3 = l;
                            }
                        }
                        else
                        {
                            ++i3;
                        }
                    }

                    int i4 = p_180709_2_.nextInt(3);

                    for (int k4 = 0; k4 < i - i4; ++k4)
                    {
                        BlockPos upN = p_180709_3_.up(k4);
                        state = p_180709_1_.getBlockState(upN);

                        if (state.getBlock().isAir(state, p_180709_1_, upN) || state.getBlock().isLeaves(state, p_180709_1_, upN))
                        {
                            this.func_175903_a(p_180709_1_, p_180709_3_.up(k4), TRUNK);
                        }
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }
}