package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockRedstoneDiode extends BlockHorizontal
{
    protected static final AxisAlignedBB field_185548_c = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
    protected final boolean field_149914_a;

    protected BlockRedstoneDiode(boolean p_i45400_1_)
    {
        super(Material.CIRCUITS);
        this.field_149914_a = p_i45400_1_;
    }

    public AxisAlignedBB func_185496_a(IBlockState p_185496_1_, IBlockAccess p_185496_2_, BlockPos p_185496_3_)
    {
        return field_185548_c;
    }

    /**
     * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
     */
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public boolean func_176196_c(World p_176196_1_, BlockPos p_176196_2_)
    {
        IBlockState downState = p_176196_1_.getBlockState(p_176196_2_.down());
        return (downState.isTopSolid() || downState.getBlockFaceShape(p_176196_1_, p_176196_2_.down(), EnumFacing.UP) == BlockFaceShape.SOLID) ? super.func_176196_c(p_176196_1_, p_176196_2_) : false;
    }

    public boolean func_176409_d(World p_176409_1_, BlockPos p_176409_2_)
    {
        IBlockState downState = p_176409_1_.getBlockState(p_176409_2_.down());
        return downState.isTopSolid() || downState.getBlockFaceShape(p_176409_1_, p_176409_2_.down(), EnumFacing.UP) == BlockFaceShape.SOLID;
    }

    public void func_180645_a(World p_180645_1_, BlockPos p_180645_2_, IBlockState p_180645_3_, Random p_180645_4_)
    {
    }

    public void func_180650_b(World p_180650_1_, BlockPos p_180650_2_, IBlockState p_180650_3_, Random p_180650_4_)
    {
        if (!this.isLocked(p_180650_1_, p_180650_2_, p_180650_3_))
        {
            boolean flag = this.shouldBePowered(p_180650_1_, p_180650_2_, p_180650_3_);

            if (this.field_149914_a && !flag)
            {
                p_180650_1_.setBlockState(p_180650_2_, this.func_180675_k(p_180650_3_), 2);
            }
            else if (!this.field_149914_a)
            {
                p_180650_1_.setBlockState(p_180650_2_, this.func_180674_e(p_180650_3_), 2);

                if (!flag)
                {
                    p_180650_1_.func_175654_a(p_180650_2_, this.func_180674_e(p_180650_3_).getBlock(), this.func_176399_m(p_180650_3_), -1);
                }
            }
        }
    }

    /**
     * @deprecated call via {@link IBlockState#shouldSideBeRendered(IBlockAccess,BlockPos,EnumFacing)} whenever
     * possible. Implementing/overriding is fine.
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing p_176225_4_)
    {
        return p_176225_4_.getAxis() != EnumFacing.Axis.Y;
    }

    protected boolean func_176406_l(IBlockState p_176406_1_)
    {
        return this.field_149914_a;
    }

    /**
     * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return blockState.getWeakPower(blockAccess, pos, side);
    }

    /**
     * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        if (!this.func_176406_l(blockState))
        {
            return 0;
        }
        else
        {
            return blockState.get(HORIZONTAL_FACING) == side ? this.getActiveSignal(blockAccess, pos, blockState) : 0;
        }
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (this.func_176409_d(worldIn, pos))
        {
            this.updateState(worldIn, pos, state);
        }
        else
        {
            this.func_176226_b(worldIn, pos, state, 0);
            worldIn.removeBlock(pos);

            for (EnumFacing enumfacing : EnumFacing.values())
            {
                worldIn.func_175685_c(pos.offset(enumfacing), this, false);
            }
        }
    }

    protected void updateState(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.isLocked(worldIn, pos, state))
        {
            boolean flag = this.shouldBePowered(worldIn, pos, state);

            if (this.field_149914_a != flag && !worldIn.func_175691_a(pos, this))
            {
                int i = -1;

                if (this.isFacingTowardsRepeater(worldIn, pos, state))
                {
                    i = -3;
                }
                else if (this.field_149914_a)
                {
                    i = -2;
                }

                worldIn.func_175654_a(pos, this, this.func_176403_d(state), i);
            }
        }
    }

    public boolean isLocked(IBlockAccess worldIn, BlockPos pos, IBlockState state)
    {
        return false;
    }

    protected boolean shouldBePowered(World worldIn, BlockPos pos, IBlockState state)
    {
        return this.calculateInputStrength(worldIn, pos, state) > 0;
    }

    protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing enumfacing = (EnumFacing)state.get(HORIZONTAL_FACING);
        BlockPos blockpos = pos.offset(enumfacing);
        int i = worldIn.getRedstonePower(blockpos, enumfacing);

        if (i >= 15)
        {
            return i;
        }
        else
        {
            IBlockState iblockstate = worldIn.getBlockState(blockpos);
            return Math.max(i, iblockstate.getBlock() == Blocks.REDSTONE_WIRE ? ((Integer)iblockstate.get(BlockRedstoneWire.POWER)).intValue() : 0);
        }
    }

    protected int getPowerOnSides(IBlockAccess worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing enumfacing = (EnumFacing)state.get(HORIZONTAL_FACING);
        EnumFacing enumfacing1 = enumfacing.rotateY();
        EnumFacing enumfacing2 = enumfacing.rotateYCCW();
        return Math.max(this.getPowerOnSide(worldIn, pos.offset(enumfacing1), enumfacing1), this.getPowerOnSide(worldIn, pos.offset(enumfacing2), enumfacing2));
    }

    protected int getPowerOnSide(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (this.isAlternateInput(iblockstate))
        {
            if (block == Blocks.REDSTONE_BLOCK)
            {
                return 15;
            }
            else
            {
                return block == Blocks.REDSTONE_WIRE ? ((Integer)iblockstate.get(BlockRedstoneWire.POWER)).intValue() : worldIn.getStrongPower(pos, side);
            }
        }
        else
        {
            return 0;
        }
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
     */
    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }

    public IBlockState func_180642_a(World p_180642_1_, BlockPos p_180642_2_, EnumFacing p_180642_3_, float p_180642_4_, float p_180642_5_, float p_180642_6_, int p_180642_7_, EntityLivingBase p_180642_8_)
    {
        return this.getDefaultState().func_177226_a(HORIZONTAL_FACING, p_180642_8_.getHorizontalFacing().getOpposite());
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (this.shouldBePowered(worldIn, pos, state))
        {
            worldIn.func_175684_a(pos, this, 1);
        }
    }

    public void func_176213_c(World p_176213_1_, BlockPos p_176213_2_, IBlockState p_176213_3_)
    {
        this.notifyNeighbors(p_176213_1_, p_176213_2_, p_176213_3_);
    }

    protected void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing enumfacing = (EnumFacing)state.get(HORIZONTAL_FACING);
        BlockPos blockpos = pos.offset(enumfacing.getOpposite());
        if(net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(worldIn, pos, worldIn.getBlockState(pos), java.util.EnumSet.of(enumfacing.getOpposite()), false).isCanceled())
            return;
        worldIn.neighborChanged(blockpos, this, pos);
        worldIn.notifyNeighborsOfStateExcept(blockpos, this, enumfacing);
    }

    /**
     * Called after a player destroys this Block - the posiiton pos may no longer hold the state indicated.
     */
    public void onPlayerDestroy(World worldIn, BlockPos pos, IBlockState state)
    {
        if (this.field_149914_a)
        {
            for (EnumFacing enumfacing : EnumFacing.values())
            {
                worldIn.func_175685_c(pos.offset(enumfacing), this, false);
            }
        }

        super.onPlayerDestroy(worldIn, pos, state);
    }

    public boolean func_149662_c(IBlockState p_149662_1_)
    {
        return false;
    }

    protected boolean isAlternateInput(IBlockState state)
    {
        return state.canProvidePower();
    }

    protected int getActiveSignal(IBlockAccess worldIn, BlockPos pos, IBlockState state)
    {
        return 15;
    }

    public static boolean isDiode(IBlockState state)
    {
        return Blocks.field_150413_aR.func_185547_C(state) || Blocks.field_150441_bU.func_185547_C(state);
    }

    public boolean func_185547_C(IBlockState p_185547_1_)
    {
        Block block = p_185547_1_.getBlock();
        return block == this.func_180674_e(this.getDefaultState()).getBlock() || block == this.func_180675_k(this.getDefaultState()).getBlock();
    }

    public boolean isFacingTowardsRepeater(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing enumfacing = ((EnumFacing)state.get(HORIZONTAL_FACING)).getOpposite();
        BlockPos blockpos = pos.offset(enumfacing);

        if (isDiode(worldIn.getBlockState(blockpos)))
        {
            return worldIn.getBlockState(blockpos).get(HORIZONTAL_FACING) != enumfacing;
        }
        else
        {
            return false;
        }
    }

    protected int func_176399_m(IBlockState p_176399_1_)
    {
        return this.func_176403_d(p_176399_1_);
    }

    protected abstract int func_176403_d(IBlockState p_176403_1_);

    protected abstract IBlockState func_180674_e(IBlockState p_180674_1_);

    protected abstract IBlockState func_180675_k(IBlockState p_180675_1_);

    public boolean func_149667_c(Block p_149667_1_)
    {
        return this.func_185547_C(p_149667_1_.getDefaultState());
    }

    /**
     * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
     * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
     */
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    /* ======================================== FORGE START =====================================*/
    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
        if (super.rotateBlock(world, pos, axis))
        {
            IBlockState state = world.getBlockState(pos);
            state = func_180675_k(state);
            world.setBlockState(pos, state);

            if (shouldBePowered(world, pos, state))
            {
                world.func_175684_a(pos, this, 1);
            }
            return true;
        }
        return false;
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
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
}