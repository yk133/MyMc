package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenTrees extends WorldGenAbstractTree
{
    private static final IBlockState DEFAULT_TRUNK = Blocks.field_150364_r.getDefaultState().func_177226_a(BlockOldLog.field_176301_b, BlockPlanks.EnumType.OAK);
    private static final IBlockState DEFAULT_LEAF = Blocks.field_150362_t.getDefaultState().func_177226_a(BlockOldLeaf.field_176239_P, BlockPlanks.EnumType.OAK).func_177226_a(BlockLeaves.field_176236_b, Boolean.valueOf(false));
    /** The minimum height of a generated tree. */
    private final int minTreeHeight;
    /** True if this tree should grow Vines. */
    private final boolean vinesGrow;
    /** The metadata value of the wood to use in tree generation. */
    private final IBlockState metaWood;
    /** The metadata value of the leaves to use in tree generation. */
    private final IBlockState metaLeaves;

    public WorldGenTrees(boolean p_i2027_1_)
    {
        this(p_i2027_1_, 4, DEFAULT_TRUNK, DEFAULT_LEAF, false);
    }

    public WorldGenTrees(boolean notify, int minTreeHeightIn, IBlockState woodMeta, IBlockState p_i46446_4_, boolean growVines)
    {
        super(notify);
        this.minTreeHeight = minTreeHeightIn;
        this.metaWood = woodMeta;
        this.metaLeaves = p_i46446_4_;
        this.vinesGrow = growVines;
    }

    public boolean func_180709_b(World p_180709_1_, Random p_180709_2_, BlockPos p_180709_3_)
    {
        int i = p_180709_2_.nextInt(3) + this.minTreeHeight;
        boolean flag = true;

        if (p_180709_3_.getY() >= 1 && p_180709_3_.getY() + i + 1 <= p_180709_1_.getHeight())
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
                            if (!this.isReplaceable(p_180709_1_,blockpos$mutableblockpos.setPos(l, j, i1)))
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
                IBlockState state = p_180709_1_.getBlockState(p_180709_3_.down());

                if (state.getBlock().canSustainPlant(state, p_180709_1_, p_180709_3_.down(), net.minecraft.util.EnumFacing.UP, (net.minecraft.block.BlockSapling)Blocks.field_150345_g) && p_180709_3_.getY() < p_180709_1_.getHeight() - i - 1)
                {
                    state.getBlock().onPlantGrow(state, p_180709_1_, p_180709_3_.down(), p_180709_3_);
                    int k2 = 3;
                    int l2 = 0;

                    for (int i3 = p_180709_3_.getY() - 3 + i; i3 <= p_180709_3_.getY() + i; ++i3)
                    {
                        int i4 = i3 - (p_180709_3_.getY() + i);
                        int j1 = 1 - i4 / 2;

                        for (int k1 = p_180709_3_.getX() - j1; k1 <= p_180709_3_.getX() + j1; ++k1)
                        {
                            int l1 = k1 - p_180709_3_.getX();

                            for (int i2 = p_180709_3_.getZ() - j1; i2 <= p_180709_3_.getZ() + j1; ++i2)
                            {
                                int j2 = i2 - p_180709_3_.getZ();

                                if (Math.abs(l1) != j1 || Math.abs(j2) != j1 || p_180709_2_.nextInt(2) != 0 && i4 != 0)
                                {
                                    BlockPos blockpos = new BlockPos(k1, i3, i2);
                                    state = p_180709_1_.getBlockState(blockpos);

                                    if (state.getBlock().isAir(state, p_180709_1_, blockpos) || state.getBlock().isLeaves(state, p_180709_1_, blockpos) || state.getMaterial() == Material.VINE)
                                    {
                                        this.func_175903_a(p_180709_1_, blockpos, this.metaLeaves);
                                    }
                                }
                            }
                        }
                    }

                    for (int j3 = 0; j3 < i; ++j3)
                    {
                        BlockPos upN = p_180709_3_.up(j3);
                        state = p_180709_1_.getBlockState(upN);

                        if (state.getBlock().isAir(state, p_180709_1_, upN) || state.getBlock().isLeaves(state, p_180709_1_, upN) || state.getMaterial() == Material.VINE)
                        {
                            this.func_175903_a(p_180709_1_, p_180709_3_.up(j3), this.metaWood);

                            if (this.vinesGrow && j3 > 0)
                            {
                                if (p_180709_2_.nextInt(3) > 0 && p_180709_1_.isAirBlock(p_180709_3_.add(-1, j3, 0)))
                                {
                                    this.addVine(p_180709_1_, p_180709_3_.add(-1, j3, 0), BlockVine.EAST);
                                }

                                if (p_180709_2_.nextInt(3) > 0 && p_180709_1_.isAirBlock(p_180709_3_.add(1, j3, 0)))
                                {
                                    this.addVine(p_180709_1_, p_180709_3_.add(1, j3, 0), BlockVine.WEST);
                                }

                                if (p_180709_2_.nextInt(3) > 0 && p_180709_1_.isAirBlock(p_180709_3_.add(0, j3, -1)))
                                {
                                    this.addVine(p_180709_1_, p_180709_3_.add(0, j3, -1), BlockVine.SOUTH);
                                }

                                if (p_180709_2_.nextInt(3) > 0 && p_180709_1_.isAirBlock(p_180709_3_.add(0, j3, 1)))
                                {
                                    this.addVine(p_180709_1_, p_180709_3_.add(0, j3, 1), BlockVine.NORTH);
                                }
                            }
                        }
                    }

                    if (this.vinesGrow)
                    {
                        for (int k3 = p_180709_3_.getY() - 3 + i; k3 <= p_180709_3_.getY() + i; ++k3)
                        {
                            int j4 = k3 - (p_180709_3_.getY() + i);
                            int k4 = 2 - j4 / 2;
                            BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

                            for (int l4 = p_180709_3_.getX() - k4; l4 <= p_180709_3_.getX() + k4; ++l4)
                            {
                                for (int i5 = p_180709_3_.getZ() - k4; i5 <= p_180709_3_.getZ() + k4; ++i5)
                                {
                                    blockpos$mutableblockpos1.setPos(l4, k3, i5);

                                    state = p_180709_1_.getBlockState(blockpos$mutableblockpos1);
                                    if (state.getBlock().isLeaves(state, p_180709_1_, blockpos$mutableblockpos1))
                                    {
                                        BlockPos blockpos2 = blockpos$mutableblockpos1.west();
                                        BlockPos blockpos3 = blockpos$mutableblockpos1.east();
                                        BlockPos blockpos4 = blockpos$mutableblockpos1.north();
                                        BlockPos blockpos1 = blockpos$mutableblockpos1.south();

                                        if (p_180709_2_.nextInt(4) == 0 && p_180709_1_.isAirBlock(blockpos2))
                                        {
                                            this.addHangingVine(p_180709_1_, blockpos2, BlockVine.EAST);
                                        }

                                        if (p_180709_2_.nextInt(4) == 0 && p_180709_1_.isAirBlock(blockpos3))
                                        {
                                            this.addHangingVine(p_180709_1_, blockpos3, BlockVine.WEST);
                                        }

                                        if (p_180709_2_.nextInt(4) == 0 && p_180709_1_.isAirBlock(blockpos4))
                                        {
                                            this.addHangingVine(p_180709_1_, blockpos4, BlockVine.SOUTH);
                                        }

                                        if (p_180709_2_.nextInt(4) == 0 && p_180709_1_.isAirBlock(blockpos1))
                                        {
                                            this.addHangingVine(p_180709_1_, blockpos1, BlockVine.NORTH);
                                        }
                                    }
                                }
                            }
                        }

                        if (p_180709_2_.nextInt(5) == 0 && i > 5)
                        {
                            for (int l3 = 0; l3 < 2; ++l3)
                            {
                                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                                {
                                    if (p_180709_2_.nextInt(4 - l3) == 0)
                                    {
                                        EnumFacing enumfacing1 = enumfacing.getOpposite();
                                        this.placeCocoa(p_180709_1_, p_180709_2_.nextInt(3), p_180709_3_.add(enumfacing1.getXOffset(), i - 5 + l3, enumfacing1.getZOffset()), enumfacing);
                                    }
                                }
                            }
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

    private void placeCocoa(World worldIn, int p_181652_2_, BlockPos pos, EnumFacing side)
    {
        this.func_175903_a(worldIn, pos, Blocks.COCOA.getDefaultState().func_177226_a(BlockCocoa.AGE, Integer.valueOf(p_181652_2_)).func_177226_a(BlockCocoa.HORIZONTAL_FACING, side));
    }

    private void addVine(World worldIn, BlockPos pos, PropertyBool prop)
    {
        this.func_175903_a(worldIn, pos, Blocks.VINE.getDefaultState().func_177226_a(prop, Boolean.valueOf(true)));
    }

    private void addHangingVine(World worldIn, BlockPos pos, PropertyBool prop)
    {
        this.addVine(worldIn, pos, prop);
        int i = 4;

        for (BlockPos blockpos = pos.down(); worldIn.isAirBlock(blockpos) && i > 0; --i)
        {
            this.addVine(worldIn, blockpos, prop);
            blockpos = blockpos.down();
        }
    }
}