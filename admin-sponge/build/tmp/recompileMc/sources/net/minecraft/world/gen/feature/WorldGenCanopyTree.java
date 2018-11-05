package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenCanopyTree extends WorldGenAbstractTree
{
    private static final IBlockState DARK_OAK_LOG = Blocks.field_150363_s.getDefaultState().func_177226_a(BlockNewLog.field_176300_b, BlockPlanks.EnumType.DARK_OAK);
    private static final IBlockState DARK_OAK_LEAVES = Blocks.field_150361_u.getDefaultState().func_177226_a(BlockNewLeaf.field_176240_P, BlockPlanks.EnumType.DARK_OAK).func_177226_a(BlockLeaves.field_176236_b, Boolean.valueOf(false));

    public WorldGenCanopyTree(boolean notify)
    {
        super(notify);
    }

    public boolean func_180709_b(World p_180709_1_, Random p_180709_2_, BlockPos p_180709_3_)
    {
        int i = p_180709_2_.nextInt(3) + p_180709_2_.nextInt(2) + 6;
        int j = p_180709_3_.getX();
        int k = p_180709_3_.getY();
        int l = p_180709_3_.getZ();

        if (k >= 1 && k + i + 1 < 256)
        {
            BlockPos blockpos = p_180709_3_.down();
            IBlockState state = p_180709_1_.getBlockState(blockpos);
            boolean isSoil = state.getBlock().canSustainPlant(state, p_180709_1_, blockpos, net.minecraft.util.EnumFacing.UP, ((net.minecraft.block.BlockSapling)Blocks.field_150345_g));

            if (!(isSoil && p_180709_3_.getY() < p_180709_1_.getHeight() - i - 1))
            {
                return false;
            }
            else if (!this.placeTreeOfHeight(p_180709_1_, p_180709_3_, i))
            {
                return false;
            }
            else
            {
                this.onPlantGrow(p_180709_1_, blockpos, p_180709_3_);
                this.onPlantGrow(p_180709_1_, blockpos.east(), p_180709_3_);
                this.onPlantGrow(p_180709_1_, blockpos.south(), p_180709_3_);
                this.onPlantGrow(p_180709_1_, blockpos.south().east(), p_180709_3_);
                EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(p_180709_2_);
                int i1 = i - p_180709_2_.nextInt(4);
                int j1 = 2 - p_180709_2_.nextInt(3);
                int k1 = j;
                int l1 = l;
                int i2 = k + i - 1;

                for (int j2 = 0; j2 < i; ++j2)
                {
                    if (j2 >= i1 && j1 > 0)
                    {
                        k1 += enumfacing.getXOffset();
                        l1 += enumfacing.getZOffset();
                        --j1;
                    }

                    int k2 = k + j2;
                    BlockPos blockpos1 = new BlockPos(k1, k2, l1);
                    state = p_180709_1_.getBlockState(blockpos1);

                    if (state.getBlock().isAir(state, p_180709_1_, blockpos1) || state.getBlock().isLeaves(state, p_180709_1_, blockpos1))
                    {
                        this.func_181639_b(p_180709_1_, blockpos1);
                        this.func_181639_b(p_180709_1_, blockpos1.east());
                        this.func_181639_b(p_180709_1_, blockpos1.south());
                        this.func_181639_b(p_180709_1_, blockpos1.east().south());
                    }
                }

                for (int i3 = -2; i3 <= 0; ++i3)
                {
                    for (int l3 = -2; l3 <= 0; ++l3)
                    {
                        int k4 = -1;
                        this.func_150526_a(p_180709_1_, k1 + i3, i2 + k4, l1 + l3);
                        this.func_150526_a(p_180709_1_, 1 + k1 - i3, i2 + k4, l1 + l3);
                        this.func_150526_a(p_180709_1_, k1 + i3, i2 + k4, 1 + l1 - l3);
                        this.func_150526_a(p_180709_1_, 1 + k1 - i3, i2 + k4, 1 + l1 - l3);

                        if ((i3 > -2 || l3 > -1) && (i3 != -1 || l3 != -2))
                        {
                            k4 = 1;
                            this.func_150526_a(p_180709_1_, k1 + i3, i2 + k4, l1 + l3);
                            this.func_150526_a(p_180709_1_, 1 + k1 - i3, i2 + k4, l1 + l3);
                            this.func_150526_a(p_180709_1_, k1 + i3, i2 + k4, 1 + l1 - l3);
                            this.func_150526_a(p_180709_1_, 1 + k1 - i3, i2 + k4, 1 + l1 - l3);
                        }
                    }
                }

                if (p_180709_2_.nextBoolean())
                {
                    this.func_150526_a(p_180709_1_, k1, i2 + 2, l1);
                    this.func_150526_a(p_180709_1_, k1 + 1, i2 + 2, l1);
                    this.func_150526_a(p_180709_1_, k1 + 1, i2 + 2, l1 + 1);
                    this.func_150526_a(p_180709_1_, k1, i2 + 2, l1 + 1);
                }

                for (int j3 = -3; j3 <= 4; ++j3)
                {
                    for (int i4 = -3; i4 <= 4; ++i4)
                    {
                        if ((j3 != -3 || i4 != -3) && (j3 != -3 || i4 != 4) && (j3 != 4 || i4 != -3) && (j3 != 4 || i4 != 4) && (Math.abs(j3) < 3 || Math.abs(i4) < 3))
                        {
                            this.func_150526_a(p_180709_1_, k1 + j3, i2, l1 + i4);
                        }
                    }
                }

                for (int k3 = -1; k3 <= 2; ++k3)
                {
                    for (int j4 = -1; j4 <= 2; ++j4)
                    {
                        if ((k3 < 0 || k3 > 1 || j4 < 0 || j4 > 1) && p_180709_2_.nextInt(3) <= 0)
                        {
                            int l4 = p_180709_2_.nextInt(3) + 2;

                            for (int i5 = 0; i5 < l4; ++i5)
                            {
                                this.func_181639_b(p_180709_1_, new BlockPos(j + k3, i2 - i5 - 1, l + j4));
                            }

                            for (int j5 = -1; j5 <= 1; ++j5)
                            {
                                for (int l2 = -1; l2 <= 1; ++l2)
                                {
                                    this.func_150526_a(p_180709_1_, k1 + k3 + j5, i2, l1 + j4 + l2);
                                }
                            }

                            for (int k5 = -2; k5 <= 2; ++k5)
                            {
                                for (int l5 = -2; l5 <= 2; ++l5)
                                {
                                    if (Math.abs(k5) != 2 || Math.abs(l5) != 2)
                                    {
                                        this.func_150526_a(p_180709_1_, k1 + k3 + k5, i2 - 1, l1 + j4 + l5);
                                    }
                                }
                            }
                        }
                    }
                }

                return true;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean placeTreeOfHeight(World worldIn, BlockPos pos, int height)
    {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int l = 0; l <= height + 1; ++l)
        {
            int i1 = 1;

            if (l == 0)
            {
                i1 = 0;
            }

            if (l >= height - 1)
            {
                i1 = 2;
            }

            for (int j1 = -i1; j1 <= i1; ++j1)
            {
                for (int k1 = -i1; k1 <= i1; ++k1)
                {
                    if (!this.isReplaceable(worldIn, blockpos$mutableblockpos.setPos(i + j1, j + l, k + k1)))
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void func_181639_b(World p_181639_1_, BlockPos p_181639_2_)
    {
        if (this.canGrowInto(p_181639_1_.getBlockState(p_181639_2_).getBlock()))
        {
            this.func_175903_a(p_181639_1_, p_181639_2_, DARK_OAK_LOG);
        }
    }

    private void func_150526_a(World p_150526_1_, int p_150526_2_, int p_150526_3_, int p_150526_4_)
    {
        BlockPos blockpos = new BlockPos(p_150526_2_, p_150526_3_, p_150526_4_);
        IBlockState state = p_150526_1_.getBlockState(blockpos);

        if (state.getBlock().isAir(state, p_150526_1_, blockpos))
        {
            this.func_175903_a(p_150526_1_, blockpos, DARK_OAK_LEAVES);
        }
    }

    //Just a helper macro
    private void onPlantGrow(World world, BlockPos pos, BlockPos source)
    {
        IBlockState state = world.getBlockState(pos);
        state.getBlock().onPlantGrow(state, world, pos, source);
    }
}