package net.minecraft.block.state;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockWorldState
{
    private final World world;
    private final BlockPos pos;
    private final boolean forceLoad;
    private IBlockState state;
    private TileEntity tileEntity;
    private boolean tileEntityInitialized;

    public BlockWorldState(World p_i46451_1_, BlockPos p_i46451_2_, boolean p_i46451_3_)
    {
        this.world = p_i46451_1_;
        this.pos = p_i46451_2_;
        this.forceLoad = p_i46451_3_;
    }

    /**
     * Gets the block state as currently held, or (if it has not gotten it from the world) loads it from the world.
     *  This will only look up the state from the world if {@link #forceLoad} is true or the block position is loaded.
     */
    public IBlockState getBlockState()
    {
        if (this.state == null && (this.forceLoad || this.world.isBlockLoaded(this.pos)))
        {
            this.state = this.world.getBlockState(this.pos);
        }

        return this.state;
    }

    /**
     * Gets the tile entity as currently held, or (if it has not gotten it from the world) loads it from the world.
     */
    @Nullable
    public TileEntity getTileEntity()
    {
        if (this.tileEntity == null && !this.tileEntityInitialized)
        {
            this.tileEntity = this.world.getTileEntity(this.pos);
            this.tileEntityInitialized = true;
        }

        return this.tileEntity;
    }

    public BlockPos getPos()
    {
        return this.pos;
    }

    /**
     * Creates a new {@link Predicate} that will match when the given {@link IBlockState} predicate matches.
     */
    public static Predicate<BlockWorldState> hasState(final Predicate<IBlockState> predicatesIn)
    {
        return new Predicate<BlockWorldState>()
        {
            public boolean apply(@Nullable BlockWorldState p_apply_1_)
            {
                return p_apply_1_ != null && predicatesIn.apply(p_apply_1_.getBlockState());
            }
        };
    }
}