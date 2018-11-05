package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenBirchTree extends WorldGenAbstractTree
{
    private static final IBlockState LOG = Blocks.field_150364_r.getDefaultState().func_177226_a(BlockOldLog.field_176301_b, BlockPlanks.EnumType.BIRCH);
    private static final IBlockState LEAF = Blocks.field_150362_t.getDefaultState().func_177226_a(BlockOldLeaf.field_176239_P, BlockPlanks.EnumType.BIRCH).func_177226_a(BlockOldLeaf.field_176236_b, Boolean.valueOf(false));
    private final boolean useExtraRandomHeight;

    public WorldGenBirchTree(boolean notify, boolean useExtraRandomHeightIn)
    {
        super(notify);
        this.useExtraRandomHeight = useExtraRandomHeightIn;
    }

    public boolean func_180709_b(World p_180709_1_, Random p_180709_2_, BlockPos p_180709_3_)
    {
        int i = p_180709_2_.nextInt(3) + 5;

        if (this.useExtraRandomHeight)
        {
            i += p_180709_2_.nextInt(7);
        }

        boolean flag = true;

        if (p_180709_3_.getY() >= 1 && p_180709_3_.getY() + i + 1 <= 256)
        {
            for (int j = p_180709_3_.getY(); j <= p_180709_3_.getY() + 1 + i; ++j)
            {
                int k = 1;

                if (j == p_180709_3_.getY())
                {
                    k = 0;
                }

                if (j >= p_180709_3_.getY() + 1 + i - 2)
                {
                    k = 2;
                }

                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                for (int l = p_180709_3_.getX() - k; l <= p_180709_3_.getX() + k && flag; ++l)
                {
                    for (int i1 = p_180709_3_.getZ() - k; i1 <= p_180709_3_.getZ() + k && flag; ++i1)
                    {
                        if (j >= 0 && j < p_180709_1_.getHeight())
                        {
                            if (!this.isReplaceable(p_180709_1_, blockpos$mutableblockpos.setPos(l, j, i1)))
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
                boolean isSoil = state.getBlock().canSustainPlant(state, p_180709_1_, down, net.minecraft.util.EnumFacing.UP, (net.minecraft.block.BlockSapling)Blocks.field_150345_g);

                if (isSoil && p_180709_3_.getY() < p_180709_1_.getHeight() - i - 1)
                {
                    state.getBlock().onPlantGrow(state, p_180709_1_, down, p_180709_3_);

                    for (int i2 = p_180709_3_.getY() - 3 + i; i2 <= p_180709_3_.getY() + i; ++i2)
                    {
                        int k2 = i2 - (p_180709_3_.getY() + i);
                        int l2 = 1 - k2 / 2;

                        for (int i3 = p_180709_3_.getX() - l2; i3 <= p_180709_3_.getX() + l2; ++i3)
                        {
                            int j1 = i3 - p_180709_3_.getX();

                            for (int k1 = p_180709_3_.getZ() - l2; k1 <= p_180709_3_.getZ() + l2; ++k1)
                            {
                                int l1 = k1 - p_180709_3_.getZ();

                                if (Math.abs(j1) != l2 || Math.abs(l1) != l2 || p_180709_2_.nextInt(2) != 0 && k2 != 0)
                                {
                                    BlockPos blockpos = new BlockPos(i3, i2, k1);
                                    IBlockState state2 = p_180709_1_.getBlockState(blockpos);

                                    if (state2.getBlock().isAir(state2, p_180709_1_, blockpos) || state2.getBlock().isAir(state2, p_180709_1_, blockpos))
                                    {
                                        this.func_175903_a(p_180709_1_, blockpos, LEAF);
                                    }
                                }
                            }
                        }
                    }

                    for (int j2 = 0; j2 < i; ++j2)
                    {
                        BlockPos upN = p_180709_3_.up(j2);
                        IBlockState state2 = p_180709_1_.getBlockState(upN);

                        if (state2.getBlock().isAir(state2, p_180709_1_, upN) || state2.getBlock().isLeaves(state2, p_180709_1_, upN))
                        {
                            this.func_175903_a(p_180709_1_, p_180709_3_.up(j2), LOG);
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