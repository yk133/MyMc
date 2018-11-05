package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRedstoneWire extends Block
{
    public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> NORTH = PropertyEnum.<BlockRedstoneWire.EnumAttachPosition>create("north", BlockRedstoneWire.EnumAttachPosition.class);
    public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> EAST = PropertyEnum.<BlockRedstoneWire.EnumAttachPosition>create("east", BlockRedstoneWire.EnumAttachPosition.class);
    public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> SOUTH = PropertyEnum.<BlockRedstoneWire.EnumAttachPosition>create("south", BlockRedstoneWire.EnumAttachPosition.class);
    public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> WEST = PropertyEnum.<BlockRedstoneWire.EnumAttachPosition>create("west", BlockRedstoneWire.EnumAttachPosition.class);
    public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
    protected static final AxisAlignedBB[] field_185700_f = new AxisAlignedBB[] {new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D)};
    private boolean canProvidePower = true;
    /** List of blocks to update with redstone. */
    private final Set<BlockPos> blocksNeedingUpdate = Sets.<BlockPos>newHashSet();

    public BlockRedstoneWire()
    {
        super(Material.CIRCUITS);
        this.setDefaultState(this.stateContainer.getBaseState().func_177226_a(NORTH, BlockRedstoneWire.EnumAttachPosition.NONE).func_177226_a(EAST, BlockRedstoneWire.EnumAttachPosition.NONE).func_177226_a(SOUTH, BlockRedstoneWire.EnumAttachPosition.NONE).func_177226_a(WEST, BlockRedstoneWire.EnumAttachPosition.NONE).func_177226_a(POWER, Integer.valueOf(0)));
    }

    public AxisAlignedBB func_185496_a(IBlockState p_185496_1_, IBlockAccess p_185496_2_, BlockPos p_185496_3_)
    {
        return field_185700_f[getAABBIndex(p_185496_1_.func_185899_b(p_185496_2_, p_185496_3_))];
    }

    private static int getAABBIndex(IBlockState state)
    {
        int i = 0;
        boolean flag = state.get(NORTH) != BlockRedstoneWire.EnumAttachPosition.NONE;
        boolean flag1 = state.get(EAST) != BlockRedstoneWire.EnumAttachPosition.NONE;
        boolean flag2 = state.get(SOUTH) != BlockRedstoneWire.EnumAttachPosition.NONE;
        boolean flag3 = state.get(WEST) != BlockRedstoneWire.EnumAttachPosition.NONE;

        if (flag || flag2 && !flag && !flag1 && !flag3)
        {
            i |= 1 << EnumFacing.NORTH.getHorizontalIndex();
        }

        if (flag1 || flag3 && !flag && !flag1 && !flag2)
        {
            i |= 1 << EnumFacing.EAST.getHorizontalIndex();
        }

        if (flag2 || flag && !flag1 && !flag2 && !flag3)
        {
            i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
        }

        if (flag3 || flag1 && !flag && !flag2 && !flag3)
        {
            i |= 1 << EnumFacing.WEST.getHorizontalIndex();
        }

        return i;
    }

    public IBlockState func_176221_a(IBlockState p_176221_1_, IBlockAccess p_176221_2_, BlockPos p_176221_3_)
    {
        p_176221_1_ = p_176221_1_.func_177226_a(WEST, this.func_176341_c(p_176221_2_, p_176221_3_, EnumFacing.WEST));
        p_176221_1_ = p_176221_1_.func_177226_a(EAST, this.func_176341_c(p_176221_2_, p_176221_3_, EnumFacing.EAST));
        p_176221_1_ = p_176221_1_.func_177226_a(NORTH, this.func_176341_c(p_176221_2_, p_176221_3_, EnumFacing.NORTH));
        p_176221_1_ = p_176221_1_.func_177226_a(SOUTH, this.func_176341_c(p_176221_2_, p_176221_3_, EnumFacing.SOUTH));
        return p_176221_1_;
    }

    private BlockRedstoneWire.EnumAttachPosition func_176341_c(IBlockAccess p_176341_1_, BlockPos p_176341_2_, EnumFacing p_176341_3_)
    {
        BlockPos blockpos = p_176341_2_.offset(p_176341_3_);
        IBlockState iblockstate = p_176341_1_.getBlockState(p_176341_2_.offset(p_176341_3_));

        if (!canConnectTo(p_176341_1_.getBlockState(blockpos), p_176341_3_, p_176341_1_, blockpos) && (iblockstate.isNormalCube() || !canConnectUpwardsTo(p_176341_1_, blockpos.down())))
        {
            IBlockState iblockstate1 = p_176341_1_.getBlockState(p_176341_2_.up());

            if (!iblockstate1.isNormalCube())
            {
                boolean flag = p_176341_1_.getBlockState(blockpos).isSideSolid(p_176341_1_, blockpos, EnumFacing.UP) || p_176341_1_.getBlockState(blockpos).getBlock() == Blocks.GLOWSTONE;

                if (flag && canConnectUpwardsTo(p_176341_1_, blockpos.up()))
                {
                    if (iblockstate.isBlockNormalCube())
                    {
                        return BlockRedstoneWire.EnumAttachPosition.UP;
                    }

                    return BlockRedstoneWire.EnumAttachPosition.SIDE;
                }
            }

            return BlockRedstoneWire.EnumAttachPosition.NONE;
        }
        else
        {
            return BlockRedstoneWire.EnumAttachPosition.SIDE;
        }
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

    public boolean func_176196_c(World p_176196_1_, BlockPos p_176196_2_)
    {
        IBlockState downState = p_176196_1_.getBlockState(p_176196_2_.down());
        return downState.isTopSolid() || downState.getBlockFaceShape(p_176196_1_, p_176196_2_.down(), EnumFacing.UP) == BlockFaceShape.SOLID || p_176196_1_.getBlockState(p_176196_2_.down()).getBlock() == Blocks.GLOWSTONE;
    }

    private IBlockState updateSurroundingRedstone(World worldIn, BlockPos pos, IBlockState state)
    {
        state = this.calculateCurrentChanges(worldIn, pos, pos, state);
        List<BlockPos> list = Lists.newArrayList(this.blocksNeedingUpdate);
        this.blocksNeedingUpdate.clear();

        for (BlockPos blockpos : list)
        {
            worldIn.func_175685_c(blockpos, this, false);
        }

        return state;
    }

    private IBlockState calculateCurrentChanges(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state)
    {
        IBlockState iblockstate = state;
        int i = ((Integer)state.get(POWER)).intValue();
        int j = 0;
        j = this.getMaxCurrentStrength(worldIn, pos2, j);
        this.canProvidePower = false;
        int k = worldIn.getRedstonePowerFromNeighbors(pos1);
        this.canProvidePower = true;

        if (k > 0 && k > j - 1)
        {
            j = k;
        }

        int l = 0;

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos blockpos = pos1.offset(enumfacing);
            boolean flag = blockpos.getX() != pos2.getX() || blockpos.getZ() != pos2.getZ();

            if (flag)
            {
                l = this.getMaxCurrentStrength(worldIn, blockpos, l);
            }

            if (worldIn.getBlockState(blockpos).isNormalCube() && !worldIn.getBlockState(pos1.up()).isNormalCube())
            {
                if (flag && pos1.getY() >= pos2.getY())
                {
                    l = this.getMaxCurrentStrength(worldIn, blockpos.up(), l);
                }
            }
            else if (!worldIn.getBlockState(blockpos).isNormalCube() && flag && pos1.getY() <= pos2.getY())
            {
                l = this.getMaxCurrentStrength(worldIn, blockpos.down(), l);
            }
        }

        if (l > j)
        {
            j = l - 1;
        }
        else if (j > 0)
        {
            --j;
        }
        else
        {
            j = 0;
        }

        if (k > j - 1)
        {
            j = k;
        }

        if (i != j)
        {
            state = state.func_177226_a(POWER, Integer.valueOf(j));

            if (worldIn.getBlockState(pos1) == iblockstate)
            {
                worldIn.setBlockState(pos1, state, 2);
            }

            this.blocksNeedingUpdate.add(pos1);

            for (EnumFacing enumfacing1 : EnumFacing.values())
            {
                this.blocksNeedingUpdate.add(pos1.offset(enumfacing1));
            }
        }

        return state;
    }

    /**
     * Calls World.notifyNeighborsOfStateChange() for all neighboring blocks, but only if the given block is a redstone
     * wire.
     */
    private void notifyWireNeighborsOfStateChange(World worldIn, BlockPos pos)
    {
        if (worldIn.getBlockState(pos).getBlock() == this)
        {
            worldIn.func_175685_c(pos, this, false);

            for (EnumFacing enumfacing : EnumFacing.values())
            {
                worldIn.func_175685_c(pos.offset(enumfacing), this, false);
            }
        }
    }

    public void func_176213_c(World p_176213_1_, BlockPos p_176213_2_, IBlockState p_176213_3_)
    {
        if (!p_176213_1_.isRemote)
        {
            this.updateSurroundingRedstone(p_176213_1_, p_176213_2_, p_176213_3_);

            for (EnumFacing enumfacing : EnumFacing.Plane.VERTICAL)
            {
                p_176213_1_.func_175685_c(p_176213_2_.offset(enumfacing), this, false);
            }

            for (EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL)
            {
                this.notifyWireNeighborsOfStateChange(p_176213_1_, p_176213_2_.offset(enumfacing1));
            }

            for (EnumFacing enumfacing2 : EnumFacing.Plane.HORIZONTAL)
            {
                BlockPos blockpos = p_176213_2_.offset(enumfacing2);

                if (p_176213_1_.getBlockState(blockpos).isNormalCube())
                {
                    this.notifyWireNeighborsOfStateChange(p_176213_1_, blockpos.up());
                }
                else
                {
                    this.notifyWireNeighborsOfStateChange(p_176213_1_, blockpos.down());
                }
            }
        }
    }

    public void func_180663_b(World p_180663_1_, BlockPos p_180663_2_, IBlockState p_180663_3_)
    {
        super.func_180663_b(p_180663_1_, p_180663_2_, p_180663_3_);

        if (!p_180663_1_.isRemote)
        {
            for (EnumFacing enumfacing : EnumFacing.values())
            {
                p_180663_1_.func_175685_c(p_180663_2_.offset(enumfacing), this, false);
            }

            this.updateSurroundingRedstone(p_180663_1_, p_180663_2_, p_180663_3_);

            for (EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL)
            {
                this.notifyWireNeighborsOfStateChange(p_180663_1_, p_180663_2_.offset(enumfacing1));
            }

            for (EnumFacing enumfacing2 : EnumFacing.Plane.HORIZONTAL)
            {
                BlockPos blockpos = p_180663_2_.offset(enumfacing2);

                if (p_180663_1_.getBlockState(blockpos).isNormalCube())
                {
                    this.notifyWireNeighborsOfStateChange(p_180663_1_, blockpos.up());
                }
                else
                {
                    this.notifyWireNeighborsOfStateChange(p_180663_1_, blockpos.down());
                }
            }
        }
    }

    private int getMaxCurrentStrength(World worldIn, BlockPos pos, int strength)
    {
        if (worldIn.getBlockState(pos).getBlock() != this)
        {
            return strength;
        }
        else
        {
            int i = ((Integer)worldIn.getBlockState(pos).get(POWER)).intValue();
            return i > strength ? i : strength;
        }
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            if (this.func_176196_c(worldIn, pos))
            {
                this.updateSurroundingRedstone(worldIn, pos, state);
            }
            else
            {
                this.func_176226_b(worldIn, pos, state, 0);
                worldIn.removeBlock(pos);
            }
        }
    }

    public Item func_180660_a(IBlockState p_180660_1_, Random p_180660_2_, int p_180660_3_)
    {
        return Items.REDSTONE;
    }

    /**
     * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return !this.canProvidePower ? 0 : blockState.getWeakPower(blockAccess, pos, side);
    }

    /**
     * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        if (!this.canProvidePower)
        {
            return 0;
        }
        else
        {
            int i = ((Integer)blockState.get(POWER)).intValue();

            if (i == 0)
            {
                return 0;
            }
            else if (side == EnumFacing.UP)
            {
                return i;
            }
            else
            {
                EnumSet<EnumFacing> enumset = EnumSet.<EnumFacing>noneOf(EnumFacing.class);

                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                {
                    if (this.isPowerSourceAt(blockAccess, pos, enumfacing))
                    {
                        enumset.add(enumfacing);
                    }
                }

                if (side.getAxis().isHorizontal() && enumset.isEmpty())
                {
                    return i;
                }
                else if (enumset.contains(side) && !enumset.contains(side.rotateYCCW()) && !enumset.contains(side.rotateY()))
                {
                    return i;
                }
                else
                {
                    return 0;
                }
            }
        }
    }

    private boolean isPowerSourceAt(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        BlockPos blockpos = pos.offset(side);
        IBlockState iblockstate = worldIn.getBlockState(blockpos);
        boolean flag = iblockstate.isNormalCube();
        boolean flag1 = worldIn.getBlockState(pos.up()).isNormalCube();

        if (!flag1 && flag && canConnectUpwardsTo(worldIn, blockpos.up()))
        {
            return true;
        }
        else if (canConnectTo(iblockstate, side, worldIn, pos))
        {
            return true;
        }
        else if (iblockstate.getBlock() == Blocks.field_150416_aS && iblockstate.get(BlockRedstoneDiode.HORIZONTAL_FACING) == side)
        {
            return true;
        }
        else
        {
            return !flag && canConnectUpwardsTo(worldIn, blockpos.down());
        }
    }

    protected static boolean canConnectUpwardsTo(IBlockAccess worldIn, BlockPos pos)
    {
        return canConnectTo(worldIn.getBlockState(pos), null, worldIn, pos);
    }

    protected static boolean canConnectTo(IBlockState blockState, @Nullable EnumFacing side, IBlockAccess world, BlockPos pos)
    {
        Block block = blockState.getBlock();

        if (block == Blocks.REDSTONE_WIRE)
        {
            return true;
        }
        else if (Blocks.field_150413_aR.func_185547_C(blockState))
        {
            EnumFacing enumfacing = (EnumFacing)blockState.get(BlockRedstoneRepeater.HORIZONTAL_FACING);
            return enumfacing == side || enumfacing.getOpposite() == side;
        }
        else if (Blocks.OBSERVER == blockState.getBlock())
        {
            return side == blockState.get(BlockObserver.FACING);
        }
        else
        {
            return blockState.getBlock().canConnectRedstone(blockState, world, pos, side);
        }
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
     */
    public boolean canProvidePower(IBlockState state)
    {
        return this.canProvidePower;
    }

    @SideOnly(Side.CLIENT)
    public static int colorMultiplier(int p_176337_0_)
    {
        float f = (float)p_176337_0_ / 15.0F;
        float f1 = f * 0.6F + 0.4F;

        if (p_176337_0_ == 0)
        {
            f1 = 0.3F;
        }

        float f2 = f * f * 0.7F - 0.5F;
        float f3 = f * f * 0.6F - 0.7F;

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }

        if (f3 < 0.0F)
        {
            f3 = 0.0F;
        }

        int i = MathHelper.clamp((int)(f1 * 255.0F), 0, 255);
        int j = MathHelper.clamp((int)(f2 * 255.0F), 0, 255);
        int k = MathHelper.clamp((int)(f3 * 255.0F), 0, 255);
        return -16777216 | i << 16 | j << 8 | k;
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    @SideOnly(Side.CLIENT)
    public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        int i = ((Integer)stateIn.get(POWER)).intValue();

        if (i != 0)
        {
            double d0 = (double)pos.getX() + 0.5D + ((double)rand.nextFloat() - 0.5D) * 0.2D;
            double d1 = (double)((float)pos.getY() + 0.0625F);
            double d2 = (double)pos.getZ() + 0.5D + ((double)rand.nextFloat() - 0.5D) * 0.2D;
            float f = (float)i / 15.0F;
            float f1 = f * 0.6F + 0.4F;
            float f2 = Math.max(0.0F, f * f * 0.7F - 0.5F);
            float f3 = Math.max(0.0F, f * f * 0.6F - 0.7F);
            worldIn.func_175688_a(EnumParticleTypes.REDSTONE, d0, d1, d2, (double)f1, (double)f2, (double)f3);
        }
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(Items.REDSTONE);
    }

    public IBlockState func_176203_a(int p_176203_1_)
    {
        return this.getDefaultState().func_177226_a(POWER, Integer.valueOf(p_176203_1_));
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

    public int func_176201_c(IBlockState p_176201_1_)
    {
        return ((Integer)p_176201_1_.get(POWER)).intValue();
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
     * fine.
     */
    public IBlockState rotate(IBlockState state, Rotation rot)
    {
        switch (rot)
        {
            case CLOCKWISE_180:
                return state.func_177226_a(NORTH, state.get(SOUTH)).func_177226_a(EAST, state.get(WEST)).func_177226_a(SOUTH, state.get(NORTH)).func_177226_a(WEST, state.get(EAST));
            case COUNTERCLOCKWISE_90:
                return state.func_177226_a(NORTH, state.get(EAST)).func_177226_a(EAST, state.get(SOUTH)).func_177226_a(SOUTH, state.get(WEST)).func_177226_a(WEST, state.get(NORTH));
            case CLOCKWISE_90:
                return state.func_177226_a(NORTH, state.get(WEST)).func_177226_a(EAST, state.get(NORTH)).func_177226_a(SOUTH, state.get(EAST)).func_177226_a(WEST, state.get(SOUTH));
            default:
                return state;
        }
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
     */
    public IBlockState mirror(IBlockState state, Mirror mirrorIn)
    {
        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                return state.func_177226_a(NORTH, state.get(SOUTH)).func_177226_a(SOUTH, state.get(NORTH));
            case FRONT_BACK:
                return state.func_177226_a(EAST, state.get(WEST)).func_177226_a(WEST, state.get(EAST));
            default:
                return super.mirror(state, mirrorIn);
        }
    }

    protected BlockStateContainer func_180661_e()
    {
        return new BlockStateContainer(this, new IProperty[] {NORTH, EAST, SOUTH, WEST, POWER});
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

    static enum EnumAttachPosition implements IStringSerializable
    {
        UP("up"),
        SIDE("side"),
        NONE("none");

        private final String name;

        private EnumAttachPosition(String p_i45689_3_)
        {
            this.name = p_i45689_3_;
        }

        public String toString()
        {
            return this.getName();
        }

        public String getName()
        {
            return this.name;
        }
    }
}