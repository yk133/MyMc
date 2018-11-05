package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaPineTree extends WorldGenHugeTrees
{
    private static final IBlockState TRUNK = Blocks.field_150364_r.getDefaultState().func_177226_a(BlockOldLog.field_176301_b, BlockPlanks.EnumType.SPRUCE);
    private static final IBlockState LEAF = Blocks.field_150362_t.getDefaultState().func_177226_a(BlockOldLeaf.field_176239_P, BlockPlanks.EnumType.SPRUCE).func_177226_a(BlockLeaves.field_176236_b, Boolean.valueOf(false));
    private static final IBlockState PODZOL = Blocks.DIRT.getDefaultState().func_177226_a(BlockDirt.field_176386_a, BlockDirt.DirtType.PODZOL);
    private final boolean useBaseHeight;

    public WorldGenMegaPineTree(boolean notify, boolean p_i45457_2_)
    {
        super(notify, 13, 15, TRUNK, LEAF);
        this.useBaseHeight = p_i45457_2_;
    }

    public boolean func_180709_b(World p_180709_1_, Random p_180709_2_, BlockPos p_180709_3_)
    {
        int i = this.getHeight(p_180709_2_);

        if (!this.func_175929_a(p_180709_1_, p_180709_2_, p_180709_3_, i))
        {
            return false;
        }
        else
        {
            this.createCrown(p_180709_1_, p_180709_3_.getX(), p_180709_3_.getZ(), p_180709_3_.getY() + i, 0, p_180709_2_);

            for (int j = 0; j < i; ++j)
            {
                if (isAirLeaves(p_180709_1_, p_180709_3_.up(j)))
                {
                    this.func_175903_a(p_180709_1_, p_180709_3_.up(j), this.trunk);
                }

                if (j < i - 1)
                {
                    if (isAirLeaves(p_180709_1_, p_180709_3_.add(1, j, 0)))
                    {
                        this.func_175903_a(p_180709_1_, p_180709_3_.add(1, j, 0), this.trunk);
                    }

                    if (isAirLeaves(p_180709_1_, p_180709_3_.add(1, j, 1)))
                    {
                        this.func_175903_a(p_180709_1_, p_180709_3_.add(1, j, 1), this.trunk);
                    }


                    if (isAirLeaves(p_180709_1_, p_180709_3_.add(0, j, 1)))
                    {
                        this.func_175903_a(p_180709_1_, p_180709_3_.add(0, j, 1), this.trunk);
                    }
                }
            }

            return true;
        }
    }

    private void createCrown(World worldIn, int x, int z, int y, int p_150541_5_, Random rand)
    {
        int i = rand.nextInt(5) + (this.useBaseHeight ? this.baseHeight : 3);
        int j = 0;

        for (int k = y - i; k <= y; ++k)
        {
            int l = y - k;
            int i1 = p_150541_5_ + MathHelper.floor((float)l / (float)i * 3.5F);
            this.growLeavesLayerStrict(worldIn, new BlockPos(x, k, z), i1 + (l > 0 && i1 == j && (k & 1) == 0 ? 1 : 0));
            j = i1;
        }
    }

    public void generateSaplings(World worldIn, Random random, BlockPos pos)
    {
        this.placePodzolCircle(worldIn, pos.west().north());
        this.placePodzolCircle(worldIn, pos.east(2).north());
        this.placePodzolCircle(worldIn, pos.west().south(2));
        this.placePodzolCircle(worldIn, pos.east(2).south(2));

        for (int i = 0; i < 5; ++i)
        {
            int j = random.nextInt(64);
            int k = j % 8;
            int l = j / 8;

            if (k == 0 || k == 7 || l == 0 || l == 7)
            {
                this.placePodzolCircle(worldIn, pos.add(-3 + k, 0, -3 + l));
            }
        }
    }

    private void placePodzolCircle(World worldIn, BlockPos center)
    {
        for (int i = -2; i <= 2; ++i)
        {
            for (int j = -2; j <= 2; ++j)
            {
                if (Math.abs(i) != 2 || Math.abs(j) != 2)
                {
                    this.placePodzolAt(worldIn, center.add(i, 0, j));
                }
            }
        }
    }

    private void placePodzolAt(World worldIn, BlockPos pos)
    {
        for (int i = 2; i >= -3; --i)
        {
            BlockPos blockpos = pos.up(i);
            IBlockState iblockstate = worldIn.getBlockState(blockpos);
            Block block = iblockstate.getBlock();

            if (block.canSustainPlant(iblockstate, worldIn, blockpos, net.minecraft.util.EnumFacing.UP, ((net.minecraft.block.BlockSapling)Blocks.field_150345_g)))
            {
                this.func_175903_a(worldIn, blockpos, PODZOL);
                break;
            }

            if (iblockstate.getMaterial() != Material.AIR && i < 0)
            {
                break;
            }
        }
    }

    //Helper macro
    private boolean isAirLeaves(World world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isAir(state, world, pos) || state.getBlock().isLeaves(state, world, pos);
    }
}