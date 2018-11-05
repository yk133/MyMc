package net.minecraft.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWallSign extends BlockSign
{
    public static final PropertyDirection FACING = BlockHorizontal.HORIZONTAL_FACING;
    protected static final AxisAlignedBB field_185578_c = new AxisAlignedBB(0.0D, 0.28125D, 0.0D, 0.125D, 0.78125D, 1.0D);
    protected static final AxisAlignedBB field_185579_d = new AxisAlignedBB(0.875D, 0.28125D, 0.0D, 1.0D, 0.78125D, 1.0D);
    protected static final AxisAlignedBB field_185580_e = new AxisAlignedBB(0.0D, 0.28125D, 0.0D, 1.0D, 0.78125D, 0.125D);
    protected static final AxisAlignedBB field_185581_f = new AxisAlignedBB(0.0D, 0.28125D, 0.875D, 1.0D, 0.78125D, 1.0D);

    public BlockWallSign()
    {
        this.setDefaultState(this.stateContainer.getBaseState().func_177226_a(FACING, EnumFacing.NORTH));
    }

    public AxisAlignedBB func_185496_a(IBlockState p_185496_1_, IBlockAccess p_185496_2_, BlockPos p_185496_3_)
    {
        switch ((EnumFacing)p_185496_1_.get(FACING))
        {
            case NORTH:
            default:
                return field_185581_f;
            case SOUTH:
                return field_185580_e;
            case WEST:
                return field_185579_d;
            case EAST:
                return field_185578_c;
        }
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        EnumFacing enumfacing = (EnumFacing)state.get(FACING);

        if (!worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getMaterial().isSolid())
        {
            this.func_176226_b(worldIn, pos, state, 0);
            worldIn.removeBlock(pos);
        }

        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    public IBlockState func_176203_a(int p_176203_1_)
    {
        EnumFacing enumfacing = EnumFacing.byIndex(p_176203_1_);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().func_177226_a(FACING, enumfacing);
    }

    public int func_176201_c(IBlockState p_176201_1_)
    {
        return ((EnumFacing)p_176201_1_.get(FACING)).getIndex();
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
     * fine.
     */
    public IBlockState rotate(IBlockState state, Rotation rot)
    {
        return state.func_177226_a(FACING, rot.rotate((EnumFacing)state.get(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
     */
    public IBlockState mirror(IBlockState state, Mirror mirrorIn)
    {
        return state.rotate(mirrorIn.toRotation((EnumFacing)state.get(FACING)));
    }

    protected BlockStateContainer func_180661_e()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }
}