package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockRailBase extends Block
{
    protected static final AxisAlignedBB FLAT_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
    protected static final AxisAlignedBB ASCENDING_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    protected final boolean field_150053_a;

    public static boolean func_176562_d(World p_176562_0_, BlockPos p_176562_1_)
    {
        return func_176563_d(p_176562_0_.getBlockState(p_176562_1_));
    }

    public static boolean func_176563_d(IBlockState p_176563_0_)
    {
        Block block = p_176563_0_.getBlock();
        return block instanceof BlockRailBase;
    }

    protected BlockRailBase(boolean p_i45389_1_)
    {
        super(Material.CIRCUITS);
        this.field_150053_a = p_i45389_1_;
        this.func_149647_a(CreativeTabs.TRANSPORTATION);
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

    public AxisAlignedBB func_185496_a(IBlockState p_185496_1_, IBlockAccess p_185496_2_, BlockPos p_185496_3_)
    {
        BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = p_185496_1_.getBlock() == this ? getRailDirection(p_185496_2_, p_185496_3_, p_185496_1_, null) : null;
        return blockrailbase$enumraildirection != null && blockrailbase$enumraildirection.func_177018_c() ? ASCENDING_AABB : FLAT_AABB;
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

    /**
     * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
     */
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public boolean func_176196_c(World p_176196_1_, BlockPos p_176196_2_)
    {
        return p_176196_1_.getBlockState(p_176196_2_.down()).isSideSolid(p_176196_1_, p_176196_2_.down(), EnumFacing.UP);
    }

    public void func_176213_c(World p_176213_1_, BlockPos p_176213_2_, IBlockState p_176213_3_)
    {
        if (!p_176213_1_.isRemote)
        {
            p_176213_3_ = this.func_176564_a(p_176213_1_, p_176213_2_, p_176213_3_, true);

            if (this.field_150053_a)
            {
                p_176213_3_.neighborChanged(p_176213_1_, p_176213_2_, this, p_176213_2_);
            }
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
            BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = getRailDirection(worldIn, pos, worldIn.getBlockState(pos), null);
            boolean flag = false;

            if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP))
            {
                flag = true;
            }

            if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.ASCENDING_EAST && !worldIn.getBlockState(pos.east()).isSideSolid(worldIn, pos.east(), EnumFacing.UP))
            {
                flag = true;
            }
            else if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.ASCENDING_WEST && !worldIn.getBlockState(pos.west()).isSideSolid(worldIn, pos.west(), EnumFacing.UP))
            {
                flag = true;
            }
            else if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.ASCENDING_NORTH && !worldIn.getBlockState(pos.north()).isSideSolid(worldIn, pos.north(), EnumFacing.UP))
            {
                flag = true;
            }
            else if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.ASCENDING_SOUTH && !worldIn.getBlockState(pos.south()).isSideSolid(worldIn, pos.south(), EnumFacing.UP))
            {
                flag = true;
            }

            if (flag && !worldIn.isAirBlock(pos))
            {
                this.func_176226_b(worldIn, pos, state, 0);
                worldIn.removeBlock(pos);
            }
            else
            {
                this.updateState(state, worldIn, pos, blockIn);
            }
        }
    }

    protected void updateState(IBlockState state, World worldIn, BlockPos pos, Block blockIn)
    {
    }

    protected IBlockState func_176564_a(World p_176564_1_, BlockPos p_176564_2_, IBlockState p_176564_3_, boolean p_176564_4_)
    {
        return p_176564_1_.isRemote ? p_176564_3_ : (new BlockRailBase.Rail(p_176564_1_, p_176564_2_, p_176564_3_)).func_180364_a(p_176564_1_.isBlockPowered(p_176564_2_), p_176564_4_).func_180362_b();
    }

    /**
     * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
     */
    public EnumPushReaction getPushReaction(IBlockState state)
    {
        return EnumPushReaction.NORMAL;
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

    public void func_180663_b(World p_180663_1_, BlockPos p_180663_2_, IBlockState p_180663_3_)
    {
        super.func_180663_b(p_180663_1_, p_180663_2_, p_180663_3_);

        if (getRailDirection(p_180663_1_, p_180663_2_, p_180663_3_, null).func_177018_c())
        {
            p_180663_1_.func_175685_c(p_180663_2_.up(), this, false);
        }

        if (this.field_150053_a)
        {
            p_180663_1_.func_175685_c(p_180663_2_, this, false);
            p_180663_1_.func_175685_c(p_180663_2_.down(), this, false);
        }
    }

    //Forge: Use getRailDirection(IBlockAccess, BlockPos, IBlockState, EntityMinecart) for enhanced ability
    public abstract IProperty<BlockRailBase.EnumRailDirection> getShapeProperty();

    /* ======================================== FORGE START =====================================*/
    /**
     * Return true if the rail can make corners.
     * Used by placement logic.
     * @param world The world.
     * @param pos Block's position in world
     * @return True if the rail can make corners.
     */
    public boolean isFlexibleRail(IBlockAccess world, BlockPos pos)
    {
        return !this.field_150053_a;
    }

    /**
     * Returns true if the rail can make up and down slopes.
     * Used by placement logic.
     * @param world The world.
     * @param pos Block's position in world
     * @return True if the rail can make slopes.
     */
    public boolean canMakeSlopes(IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    /**
     * Return the rail's direction.
     * Can be used to make the cart think the rail is a different shape,
     * for example when making diamond junctions or switches.
     * The cart parameter will often be null unless it it called from EntityMinecart.
     *
     * @param world The world.
     * @param pos Block's position in world
     * @param state The BlockState
     * @param cart The cart asking for the metadata, null if it is not called by EntityMinecart.
     * @return The direction.
     */
    public EnumRailDirection getRailDirection(IBlockAccess world, BlockPos pos, IBlockState state, @javax.annotation.Nullable net.minecraft.entity.item.EntityMinecart cart)
    {
        return state.get(getShapeProperty());
    }

    /**
     * Returns the max speed of the rail at the specified position.
     * @param world The world.
     * @param cart The cart on the rail, may be null.
     * @param pos Block's position in world
     * @return The max speed of the current rail.
     */
    public float getRailMaxSpeed(World world, net.minecraft.entity.item.EntityMinecart cart, BlockPos pos)
    {
        return 0.4f;
    }

    /**
     * This function is called by any minecart that passes over this rail.
     * It is called once per update tick that the minecart is on the rail.
     * @param world The world.
     * @param cart The cart on the rail.
     * @param pos Block's position in world
     */
    public void onMinecartPass(World world, net.minecraft.entity.item.EntityMinecart cart, BlockPos pos)
    {
    }

    /**
     * Rotate the block. For vanilla blocks this rotates around the axis passed in (generally, it should be the "face" that was hit).
     * Note: for mod blocks, this is up to the block and modder to decide. It is not mandated that it be a rotation around the
     * face, but could be a rotation to orient *to* that face, or a visiting of possible rotations.
     * The method should return true if the rotation was successful though.
     *
     * @param world The world
     * @param pos Block position in world
     * @param axis The axis to rotate around
     * @return True if the rotation was successful, False if the rotation failed, or is not possible
     */
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
        IBlockState state = world.getBlockState(pos);
        for (IProperty prop : state.func_177228_b().keySet())
        {
            if (prop.getName().equals("shape"))
            {
                world.setBlockState(pos, state.cycle(prop));
                return true;
            }
        }
        return false;
    }

    /* ======================================== FORGE END =====================================*/

    public static enum EnumRailDirection implements IStringSerializable
    {
        NORTH_SOUTH(0, "north_south"),
        EAST_WEST(1, "east_west"),
        ASCENDING_EAST(2, "ascending_east"),
        ASCENDING_WEST(3, "ascending_west"),
        ASCENDING_NORTH(4, "ascending_north"),
        ASCENDING_SOUTH(5, "ascending_south"),
        SOUTH_EAST(6, "south_east"),
        SOUTH_WEST(7, "south_west"),
        NORTH_WEST(8, "north_west"),
        NORTH_EAST(9, "north_east");

        private static final BlockRailBase.EnumRailDirection[] field_177030_k = new BlockRailBase.EnumRailDirection[values().length];
        private final int meta;
        private final String name;

        private EnumRailDirection(int p_i45738_3_, String p_i45738_4_)
        {
            this.meta = p_i45738_3_;
            this.name = p_i45738_4_;
        }

        public int func_177015_a()
        {
            return this.meta;
        }

        public String toString()
        {
            return this.name;
        }

        public boolean func_177018_c()
        {
            return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
        }

        public static BlockRailBase.EnumRailDirection func_177016_a(int p_177016_0_)
        {
            if (p_177016_0_ < 0 || p_177016_0_ >= field_177030_k.length)
            {
                p_177016_0_ = 0;
            }

            return field_177030_k[p_177016_0_];
        }

        public String getName()
        {
            return this.name;
        }

        static
        {
            for (BlockRailBase.EnumRailDirection blockrailbase$enumraildirection : values())
            {
                field_177030_k[blockrailbase$enumraildirection.func_177015_a()] = blockrailbase$enumraildirection;
            }
        }
    }

    public class Rail
    {
        private final World field_150660_b;
        private final BlockPos field_180367_c;
        private final BlockRailBase field_180365_d;
        private IBlockState field_180366_e;
        private final boolean field_150656_f;
        private final List<BlockPos> field_150657_g = Lists.<BlockPos>newArrayList();
        private final boolean canMakeSlopes;

        public Rail(World p_i45739_2_, BlockPos p_i45739_3_, IBlockState p_i45739_4_)
        {
            this.field_150660_b = p_i45739_2_;
            this.field_180367_c = p_i45739_3_;
            this.field_180366_e = p_i45739_4_;
            this.field_180365_d = (BlockRailBase)p_i45739_4_.getBlock();
            BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = field_180365_d.getRailDirection(p_i45739_2_, p_i45739_3_, p_i45739_4_, null);
            this.field_150656_f = !this.field_180365_d.isFlexibleRail(p_i45739_2_, p_i45739_3_);
            this.canMakeSlopes = this.field_180365_d.canMakeSlopes(p_i45739_2_, p_i45739_3_);
            this.func_180360_a(blockrailbase$enumraildirection);
        }

        public List<BlockPos> func_185763_a()
        {
            return this.field_150657_g;
        }

        private void func_180360_a(BlockRailBase.EnumRailDirection p_180360_1_)
        {
            this.field_150657_g.clear();

            switch (p_180360_1_)
            {
                case NORTH_SOUTH:
                    this.field_150657_g.add(this.field_180367_c.north());
                    this.field_150657_g.add(this.field_180367_c.south());
                    break;
                case EAST_WEST:
                    this.field_150657_g.add(this.field_180367_c.west());
                    this.field_150657_g.add(this.field_180367_c.east());
                    break;
                case ASCENDING_EAST:
                    this.field_150657_g.add(this.field_180367_c.west());
                    this.field_150657_g.add(this.field_180367_c.east().up());
                    break;
                case ASCENDING_WEST:
                    this.field_150657_g.add(this.field_180367_c.west().up());
                    this.field_150657_g.add(this.field_180367_c.east());
                    break;
                case ASCENDING_NORTH:
                    this.field_150657_g.add(this.field_180367_c.north().up());
                    this.field_150657_g.add(this.field_180367_c.south());
                    break;
                case ASCENDING_SOUTH:
                    this.field_150657_g.add(this.field_180367_c.north());
                    this.field_150657_g.add(this.field_180367_c.south().up());
                    break;
                case SOUTH_EAST:
                    this.field_150657_g.add(this.field_180367_c.east());
                    this.field_150657_g.add(this.field_180367_c.south());
                    break;
                case SOUTH_WEST:
                    this.field_150657_g.add(this.field_180367_c.west());
                    this.field_150657_g.add(this.field_180367_c.south());
                    break;
                case NORTH_WEST:
                    this.field_150657_g.add(this.field_180367_c.west());
                    this.field_150657_g.add(this.field_180367_c.north());
                    break;
                case NORTH_EAST:
                    this.field_150657_g.add(this.field_180367_c.east());
                    this.field_150657_g.add(this.field_180367_c.north());
            }
        }

        private void func_150651_b()
        {
            for (int i = 0; i < this.field_150657_g.size(); ++i)
            {
                BlockRailBase.Rail blockrailbase$rail = this.func_180697_b(this.field_150657_g.get(i));

                if (blockrailbase$rail != null && blockrailbase$rail.func_150653_a(this))
                {
                    this.field_150657_g.set(i, blockrailbase$rail.field_180367_c);
                }
                else
                {
                    this.field_150657_g.remove(i--);
                }
            }
        }

        private boolean func_180359_a(BlockPos p_180359_1_)
        {
            return BlockRailBase.func_176562_d(this.field_150660_b, p_180359_1_) || BlockRailBase.func_176562_d(this.field_150660_b, p_180359_1_.up()) || BlockRailBase.func_176562_d(this.field_150660_b, p_180359_1_.down());
        }

        @Nullable
        private BlockRailBase.Rail func_180697_b(BlockPos p_180697_1_)
        {
            IBlockState iblockstate = this.field_150660_b.getBlockState(p_180697_1_);

            if (BlockRailBase.func_176563_d(iblockstate))
            {
                return BlockRailBase.this.new Rail(this.field_150660_b, p_180697_1_, iblockstate);
            }
            else
            {
                BlockPos lvt_2_1_ = p_180697_1_.up();
                iblockstate = this.field_150660_b.getBlockState(lvt_2_1_);

                if (BlockRailBase.func_176563_d(iblockstate))
                {
                    return BlockRailBase.this.new Rail(this.field_150660_b, lvt_2_1_, iblockstate);
                }
                else
                {
                    lvt_2_1_ = p_180697_1_.down();
                    iblockstate = this.field_150660_b.getBlockState(lvt_2_1_);
                    return BlockRailBase.func_176563_d(iblockstate) ? BlockRailBase.this.new Rail(this.field_150660_b, lvt_2_1_, iblockstate) : null;
                }
            }
        }

        private boolean func_150653_a(BlockRailBase.Rail p_150653_1_)
        {
            return this.func_180363_c(p_150653_1_.field_180367_c);
        }

        private boolean func_180363_c(BlockPos p_180363_1_)
        {
            for (int i = 0; i < this.field_150657_g.size(); ++i)
            {
                BlockPos blockpos = this.field_150657_g.get(i);

                if (blockpos.getX() == p_180363_1_.getX() && blockpos.getZ() == p_180363_1_.getZ())
                {
                    return true;
                }
            }

            return false;
        }

        protected int func_150650_a()
        {
            int i = 0;

            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
            {
                if (this.func_180359_a(this.field_180367_c.offset(enumfacing)))
                {
                    ++i;
                }
            }

            return i;
        }

        private boolean func_150649_b(BlockRailBase.Rail p_150649_1_)
        {
            return this.func_150653_a(p_150649_1_) || this.field_150657_g.size() != 2;
        }

        private void func_150645_c(BlockRailBase.Rail p_150645_1_)
        {
            this.field_150657_g.add(p_150645_1_.field_180367_c);
            BlockPos blockpos = this.field_180367_c.north();
            BlockPos blockpos1 = this.field_180367_c.south();
            BlockPos blockpos2 = this.field_180367_c.west();
            BlockPos blockpos3 = this.field_180367_c.east();
            boolean flag = this.func_180363_c(blockpos);
            boolean flag1 = this.func_180363_c(blockpos1);
            boolean flag2 = this.func_180363_c(blockpos2);
            boolean flag3 = this.func_180363_c(blockpos3);
            BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = null;

            if (flag || flag1)
            {
                blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            if (flag2 || flag3)
            {
                blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.EAST_WEST;
            }

            if (!this.field_150656_f)
            {
                if (flag1 && flag3 && !flag && !flag2)
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                }

                if (flag1 && flag2 && !flag && !flag3)
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                }

                if (flag && flag2 && !flag1 && !flag3)
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
                }

                if (flag && flag3 && !flag1 && !flag2)
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
                }
            }

            if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.NORTH_SOUTH && canMakeSlopes)
            {
                if (BlockRailBase.func_176562_d(this.field_150660_b, blockpos.up()))
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.ASCENDING_NORTH;
                }

                if (BlockRailBase.func_176562_d(this.field_150660_b, blockpos1.up()))
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.ASCENDING_SOUTH;
                }
            }

            if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.EAST_WEST && canMakeSlopes)
            {
                if (BlockRailBase.func_176562_d(this.field_150660_b, blockpos3.up()))
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.ASCENDING_EAST;
                }

                if (BlockRailBase.func_176562_d(this.field_150660_b, blockpos2.up()))
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.ASCENDING_WEST;
                }
            }

            if (blockrailbase$enumraildirection == null)
            {
                blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            this.field_180366_e = this.field_180366_e.func_177226_a(this.field_180365_d.getShapeProperty(), blockrailbase$enumraildirection);
            this.field_150660_b.setBlockState(this.field_180367_c, this.field_180366_e, 3);
        }

        private boolean func_180361_d(BlockPos p_180361_1_)
        {
            BlockRailBase.Rail blockrailbase$rail = this.func_180697_b(p_180361_1_);

            if (blockrailbase$rail == null)
            {
                return false;
            }
            else
            {
                blockrailbase$rail.func_150651_b();
                return blockrailbase$rail.func_150649_b(this);
            }
        }

        public BlockRailBase.Rail func_180364_a(boolean p_180364_1_, boolean p_180364_2_)
        {
            BlockPos blockpos = this.field_180367_c.north();
            BlockPos blockpos1 = this.field_180367_c.south();
            BlockPos blockpos2 = this.field_180367_c.west();
            BlockPos blockpos3 = this.field_180367_c.east();
            boolean flag = this.func_180361_d(blockpos);
            boolean flag1 = this.func_180361_d(blockpos1);
            boolean flag2 = this.func_180361_d(blockpos2);
            boolean flag3 = this.func_180361_d(blockpos3);
            BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = null;

            if ((flag || flag1) && !flag2 && !flag3)
            {
                blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            if ((flag2 || flag3) && !flag && !flag1)
            {
                blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.EAST_WEST;
            }

            if (!this.field_150656_f)
            {
                if (flag1 && flag3 && !flag && !flag2)
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                }

                if (flag1 && flag2 && !flag && !flag3)
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                }

                if (flag && flag2 && !flag1 && !flag3)
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
                }

                if (flag && flag3 && !flag1 && !flag2)
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
                }
            }

            if (blockrailbase$enumraildirection == null)
            {
                if (flag || flag1)
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                }

                if (flag2 || flag3)
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.EAST_WEST;
                }

                if (!this.field_150656_f)
                {
                    if (p_180364_1_)
                    {
                        if (flag1 && flag3)
                        {
                            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                        }

                        if (flag2 && flag1)
                        {
                            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                        }

                        if (flag3 && flag)
                        {
                            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
                        }

                        if (flag && flag2)
                        {
                            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
                        }
                    }
                    else
                    {
                        if (flag && flag2)
                        {
                            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
                        }

                        if (flag3 && flag)
                        {
                            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
                        }

                        if (flag2 && flag1)
                        {
                            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                        }

                        if (flag1 && flag3)
                        {
                            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                        }
                    }
                }
            }

            if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.NORTH_SOUTH && canMakeSlopes)
            {
                if (BlockRailBase.func_176562_d(this.field_150660_b, blockpos.up()))
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.ASCENDING_NORTH;
                }

                if (BlockRailBase.func_176562_d(this.field_150660_b, blockpos1.up()))
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.ASCENDING_SOUTH;
                }
            }

            if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.EAST_WEST && canMakeSlopes)
            {
                if (BlockRailBase.func_176562_d(this.field_150660_b, blockpos3.up()))
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.ASCENDING_EAST;
                }

                if (BlockRailBase.func_176562_d(this.field_150660_b, blockpos2.up()))
                {
                    blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.ASCENDING_WEST;
                }
            }

            if (blockrailbase$enumraildirection == null)
            {
                blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            this.func_180360_a(blockrailbase$enumraildirection);
            this.field_180366_e = this.field_180366_e.func_177226_a(this.field_180365_d.getShapeProperty(), blockrailbase$enumraildirection);

            if (p_180364_2_ || this.field_150660_b.getBlockState(this.field_180367_c) != this.field_180366_e)
            {
                this.field_150660_b.setBlockState(this.field_180367_c, this.field_180366_e, 3);

                for (int i = 0; i < this.field_150657_g.size(); ++i)
                {
                    BlockRailBase.Rail blockrailbase$rail = this.func_180697_b(this.field_150657_g.get(i));

                    if (blockrailbase$rail != null)
                    {
                        blockrailbase$rail.func_150651_b();

                        if (blockrailbase$rail.func_150649_b(this))
                        {
                            blockrailbase$rail.func_150645_c(this);
                        }
                    }
                }
            }

            return this;
        }

        public IBlockState func_180362_b()
        {
            return this.field_180366_e;
        }
    }
}