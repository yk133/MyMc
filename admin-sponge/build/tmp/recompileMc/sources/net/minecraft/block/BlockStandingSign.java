package net.minecraft.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStandingSign extends BlockSign
{
    public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 15);

    public BlockStandingSign()
    {
        this.setDefaultState(this.stateContainer.getBaseState().func_177226_a(ROTATION, Integer.valueOf(0)));
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.getBlockState(pos.down()).getMaterial().isSolid())
        {
            this.func_176226_b(worldIn, pos, state, 0);
            worldIn.removeBlock(pos);
        }

        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    public IBlockState func_176203_a(int p_176203_1_)
    {
        return this.getDefaultState().func_177226_a(ROTATION, Integer.valueOf(p_176203_1_));
    }

    public int func_176201_c(IBlockState p_176201_1_)
    {
        return ((Integer)p_176201_1_.get(ROTATION)).intValue();
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
     * fine.
     */
    public IBlockState rotate(IBlockState state, Rotation rot)
    {
        return state.func_177226_a(ROTATION, Integer.valueOf(rot.rotate(((Integer)state.get(ROTATION)).intValue(), 16)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
     */
    public IBlockState mirror(IBlockState state, Mirror mirrorIn)
    {
        return state.func_177226_a(ROTATION, Integer.valueOf(mirrorIn.mirrorRotation(((Integer)state.get(ROTATION)).intValue(), 16)));
    }

    protected BlockStateContainer func_180661_e()
    {
        return new BlockStateContainer(this, new IProperty[] {ROTATION});
    }
}