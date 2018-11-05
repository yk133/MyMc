package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDragonEgg extends Block
{
    protected static final AxisAlignedBB field_185660_a = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1.0D, 0.9375D);

    public BlockDragonEgg()
    {
        super(Material.DRAGON_EGG, MapColor.BLACK);
    }

    public AxisAlignedBB func_185496_a(IBlockState p_185496_1_, IBlockAccess p_185496_2_, BlockPos p_185496_3_)
    {
        return field_185660_a;
    }

    public void func_176213_c(World p_176213_1_, BlockPos p_176213_2_, IBlockState p_176213_3_)
    {
        p_176213_1_.func_175684_a(p_176213_2_, this, this.tickRate(p_176213_1_));
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        worldIn.func_175684_a(pos, this, this.tickRate(worldIn));
    }

    public void func_180650_b(World p_180650_1_, BlockPos p_180650_2_, IBlockState p_180650_3_, Random p_180650_4_)
    {
        this.func_180683_d(p_180650_1_, p_180650_2_);
    }

    private void func_180683_d(World p_180683_1_, BlockPos p_180683_2_)
    {
        if (p_180683_1_.isAirBlock(p_180683_2_.down()) && BlockFalling.canFallThrough(p_180683_1_.getBlockState(p_180683_2_.down())) && p_180683_2_.getY() >= 0)
        {
            int i = 32;

            if (!BlockFalling.fallInstantly && p_180683_1_.isAreaLoaded(p_180683_2_.add(-32, -32, -32), p_180683_2_.add(32, 32, 32)))
            {
                p_180683_1_.spawnEntity(new EntityFallingBlock(p_180683_1_, (double)((float)p_180683_2_.getX() + 0.5F), (double)p_180683_2_.getY(), (double)((float)p_180683_2_.getZ() + 0.5F), this.getDefaultState()));
            }
            else
            {
                p_180683_1_.removeBlock(p_180683_2_);
                BlockPos blockpos;

                for (blockpos = p_180683_2_; p_180683_1_.isAirBlock(blockpos) && BlockFalling.canFallThrough(p_180683_1_.getBlockState(blockpos)) && blockpos.getY() > 0; blockpos = blockpos.down())
                {
                    ;
                }

                if (blockpos.getY() > 0)
                {
                    p_180683_1_.setBlockState(blockpos, this.getDefaultState(), 2);
                }
            }
        }
    }

    public boolean func_180639_a(World p_180639_1_, BlockPos p_180639_2_, IBlockState p_180639_3_, EntityPlayer p_180639_4_, EnumHand p_180639_5_, EnumFacing p_180639_6_, float p_180639_7_, float p_180639_8_, float p_180639_9_)
    {
        this.func_180684_e(p_180639_1_, p_180639_2_);
        return true;
    }

    public void func_180649_a(World p_180649_1_, BlockPos p_180649_2_, EntityPlayer p_180649_3_)
    {
        this.func_180684_e(p_180649_1_, p_180649_2_);
    }

    private void func_180684_e(World p_180684_1_, BlockPos p_180684_2_)
    {
        IBlockState iblockstate = p_180684_1_.getBlockState(p_180684_2_);

        if (iblockstate.getBlock() == this)
        {
            for (int i = 0; i < 1000; ++i)
            {
                BlockPos blockpos = p_180684_2_.add(p_180684_1_.rand.nextInt(16) - p_180684_1_.rand.nextInt(16), p_180684_1_.rand.nextInt(8) - p_180684_1_.rand.nextInt(8), p_180684_1_.rand.nextInt(16) - p_180684_1_.rand.nextInt(16));

                if (p_180684_1_.isAirBlock(blockpos))
                {
                    if (p_180684_1_.isRemote)
                    {
                        for (int j = 0; j < 128; ++j)
                        {
                            double d0 = p_180684_1_.rand.nextDouble();
                            float f = (p_180684_1_.rand.nextFloat() - 0.5F) * 0.2F;
                            float f1 = (p_180684_1_.rand.nextFloat() - 0.5F) * 0.2F;
                            float f2 = (p_180684_1_.rand.nextFloat() - 0.5F) * 0.2F;
                            double d1 = (double)blockpos.getX() + (double)(p_180684_2_.getX() - blockpos.getX()) * d0 + (p_180684_1_.rand.nextDouble() - 0.5D) + 0.5D;
                            double d2 = (double)blockpos.getY() + (double)(p_180684_2_.getY() - blockpos.getY()) * d0 + p_180684_1_.rand.nextDouble() - 0.5D;
                            double d3 = (double)blockpos.getZ() + (double)(p_180684_2_.getZ() - blockpos.getZ()) * d0 + (p_180684_1_.rand.nextDouble() - 0.5D) + 0.5D;
                            p_180684_1_.func_175688_a(EnumParticleTypes.PORTAL, d1, d2, d3, (double)f, (double)f1, (double)f2);
                        }
                    }
                    else
                    {
                        p_180684_1_.setBlockState(blockpos, iblockstate, 2);
                        p_180684_1_.removeBlock(p_180684_2_);
                    }

                    return;
                }
            }
        }
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 5;
    }

    public boolean func_149662_c(IBlockState p_149662_1_)
    {
        return false;
    }

    /**
     * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
     */
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    /**
     * @deprecated call via {@link IBlockState#shouldSideBeRendered(IBlockAccess,BlockPos,EnumFacing)} whenever
     * possible. Implementing/overriding is fine.
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing p_176225_4_)
    {
        return true;
    }

    /**
     * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
     * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
     * <p>
     * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that
     * does not fit the other descriptions and will generally cause other things not to connect to the face.
     * 
     * @return an approximation of the form of the given face
     * @deprecated call via {@link IBlockState#getBlockFaceShape(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
}