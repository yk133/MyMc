package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBasePressurePlate extends Block
{
    /** The bounding box for the pressure plate pressed state */
    protected static final AxisAlignedBB PRESSED_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.03125D, 0.9375D);
    protected static final AxisAlignedBB UNPRESSED_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.0625D, 0.9375D);
    /** This bounding box is used to check for entities in a certain area and then determine the pressed state. */
    protected static final AxisAlignedBB PRESSURE_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);

    protected BlockBasePressurePlate(Material p_i45740_1_)
    {
        this(p_i45740_1_, p_i45740_1_.getColor());
    }

    protected BlockBasePressurePlate(Material p_i46401_1_, MapColor p_i46401_2_)
    {
        super(p_i46401_1_, p_i46401_2_);
        this.func_149647_a(CreativeTabs.REDSTONE);
        this.func_149675_a(true);
    }

    public AxisAlignedBB func_185496_a(IBlockState p_185496_1_, IBlockAccess p_185496_2_, BlockPos p_185496_3_)
    {
        boolean flag = this.getRedstoneStrength(p_185496_1_) > 0;
        return flag ? PRESSED_AABB : UNPRESSED_AABB;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 20;
    }

    @Nullable
    public AxisAlignedBB func_180646_a(IBlockState p_180646_1_, IBlockAccess p_180646_2_, BlockPos p_180646_3_)
    {
        return field_185506_k;
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

    public boolean func_176205_b(IBlockAccess p_176205_1_, BlockPos p_176205_2_)
    {
        return true;
    }

    /**
     * Return true if an entity can be spawned inside the block (used to get the player's bed spawn location)
     */
    public boolean canSpawnInBlock()
    {
        return true;
    }

    public boolean func_176196_c(World p_176196_1_, BlockPos p_176196_2_)
    {
        return this.func_176577_m(p_176196_1_, p_176196_2_.down());
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!this.func_176577_m(worldIn, pos.down()))
        {
            this.func_176226_b(worldIn, pos, state, 0);
            worldIn.removeBlock(pos);
        }
    }

    private boolean func_176577_m(World p_176577_1_, BlockPos p_176577_2_)
    {
        return p_176577_1_.getBlockState(p_176577_2_).isTopSolid() || p_176577_1_.getBlockState(p_176577_2_).getBlock() instanceof BlockFence;
    }

    public void func_180645_a(World p_180645_1_, BlockPos p_180645_2_, IBlockState p_180645_3_, Random p_180645_4_)
    {
    }

    public void func_180650_b(World p_180650_1_, BlockPos p_180650_2_, IBlockState p_180650_3_, Random p_180650_4_)
    {
        if (!p_180650_1_.isRemote)
        {
            int i = this.getRedstoneStrength(p_180650_3_);

            if (i > 0)
            {
                this.updateState(p_180650_1_, p_180650_2_, p_180650_3_, i);
            }
        }
    }

    public void func_180634_a(World p_180634_1_, BlockPos p_180634_2_, IBlockState p_180634_3_, Entity p_180634_4_)
    {
        if (!p_180634_1_.isRemote)
        {
            int i = this.getRedstoneStrength(p_180634_3_);

            if (i == 0)
            {
                this.updateState(p_180634_1_, p_180634_2_, p_180634_3_, i);
            }
        }
    }

    /**
     * Updates the pressure plate when stepped on
     */
    protected void updateState(World worldIn, BlockPos pos, IBlockState state, int oldRedstoneStrength)
    {
        int i = this.computeRedstoneStrength(worldIn, pos);
        boolean flag = oldRedstoneStrength > 0;
        boolean flag1 = i > 0;

        if (oldRedstoneStrength != i)
        {
            state = this.setRedstoneStrength(state, i);
            worldIn.setBlockState(pos, state, 2);
            this.updateNeighbors(worldIn, pos);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
        }

        if (!flag1 && flag)
        {
            this.playClickOffSound(worldIn, pos);
        }
        else if (flag1 && !flag)
        {
            this.playClickOnSound(worldIn, pos);
        }

        if (flag1)
        {
            worldIn.func_175684_a(new BlockPos(pos), this, this.tickRate(worldIn));
        }
    }

    protected abstract void playClickOnSound(World worldIn, BlockPos pos);

    protected abstract void playClickOffSound(World worldIn, BlockPos pos);

    public void func_180663_b(World p_180663_1_, BlockPos p_180663_2_, IBlockState p_180663_3_)
    {
        if (this.getRedstoneStrength(p_180663_3_) > 0)
        {
            this.updateNeighbors(p_180663_1_, p_180663_2_);
        }

        super.func_180663_b(p_180663_1_, p_180663_2_, p_180663_3_);
    }

    /**
     * Notify block and block below of changes
     */
    protected void updateNeighbors(World worldIn, BlockPos pos)
    {
        worldIn.func_175685_c(pos, this, false);
        worldIn.func_175685_c(pos.down(), this, false);
    }

    /**
     * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return this.getRedstoneStrength(blockState);
    }

    /**
     * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return side == EnumFacing.UP ? this.getRedstoneStrength(blockState) : 0;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
     */
    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }

    /**
     * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
     */
    public EnumPushReaction getPushReaction(IBlockState state)
    {
        return EnumPushReaction.DESTROY;
    }

    protected abstract int computeRedstoneStrength(World worldIn, BlockPos pos);

    protected abstract int getRedstoneStrength(IBlockState state);

    protected abstract IBlockState setRedstoneStrength(IBlockState state, int strength);

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