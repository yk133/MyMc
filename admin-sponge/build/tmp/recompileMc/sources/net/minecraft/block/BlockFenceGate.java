package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFenceGate extends BlockHorizontal
{
    public static final PropertyBool OPEN = PropertyBool.create("open");
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyBool IN_WALL = PropertyBool.create("in_wall");
    protected static final AxisAlignedBB AABB_HITBOX_ZAXIS = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D);
    protected static final AxisAlignedBB AABB_HITBOX_XAXIS = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_HITBOX_ZAXIS_INWALL = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 0.8125D, 0.625D);
    protected static final AxisAlignedBB AABB_HITBOX_XAXIS_INWALL = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 0.8125D, 1.0D);
    protected static final AxisAlignedBB AABB_COLLISION_BOX_ZAXIS = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.5D, 0.625D);
    protected static final AxisAlignedBB AABB_COLLISION_BOX_XAXIS = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.5D, 1.0D);

    public BlockFenceGate(BlockPlanks.EnumType p_i46394_1_)
    {
        super(Material.WOOD, p_i46394_1_.func_181070_c());
        this.setDefaultState(this.stateContainer.getBaseState().func_177226_a(OPEN, Boolean.valueOf(false)).func_177226_a(POWERED, Boolean.valueOf(false)).func_177226_a(IN_WALL, Boolean.valueOf(false)));
        this.func_149647_a(CreativeTabs.REDSTONE);
    }

    public AxisAlignedBB func_185496_a(IBlockState p_185496_1_, IBlockAccess p_185496_2_, BlockPos p_185496_3_)
    {
        p_185496_1_ = this.func_176221_a(p_185496_1_, p_185496_2_, p_185496_3_);

        if (((Boolean)p_185496_1_.get(IN_WALL)).booleanValue())
        {
            return ((EnumFacing)p_185496_1_.get(HORIZONTAL_FACING)).getAxis() == EnumFacing.Axis.X ? AABB_HITBOX_XAXIS_INWALL : AABB_HITBOX_ZAXIS_INWALL;
        }
        else
        {
            return ((EnumFacing)p_185496_1_.get(HORIZONTAL_FACING)).getAxis() == EnumFacing.Axis.X ? AABB_HITBOX_XAXIS : AABB_HITBOX_ZAXIS;
        }
    }

    public IBlockState func_176221_a(IBlockState p_176221_1_, IBlockAccess p_176221_2_, BlockPos p_176221_3_)
    {
        EnumFacing.Axis enumfacing$axis = ((EnumFacing)p_176221_1_.get(HORIZONTAL_FACING)).getAxis();

        if (enumfacing$axis == EnumFacing.Axis.Z && (p_176221_2_.getBlockState(p_176221_3_.west()).getBlock() instanceof BlockWall || p_176221_2_.getBlockState(p_176221_3_.east()).getBlock() instanceof BlockWall) || enumfacing$axis == EnumFacing.Axis.X && (p_176221_2_.getBlockState(p_176221_3_.north()).getBlock() instanceof BlockWall || p_176221_2_.getBlockState(p_176221_3_.south()).getBlock() instanceof BlockWall))
        {
            p_176221_1_ = p_176221_1_.func_177226_a(IN_WALL, Boolean.valueOf(true));
        }

        return p_176221_1_;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
     * fine.
     */
    public IBlockState rotate(IBlockState state, Rotation rot)
    {
        return state.func_177226_a(HORIZONTAL_FACING, rot.rotate((EnumFacing)state.get(HORIZONTAL_FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
     */
    public IBlockState mirror(IBlockState state, Mirror mirrorIn)
    {
        return state.rotate(mirrorIn.toRotation((EnumFacing)state.get(HORIZONTAL_FACING)));
    }

    public boolean func_176196_c(World p_176196_1_, BlockPos p_176196_2_)
    {
        return p_176196_1_.getBlockState(p_176196_2_.down()).getMaterial().isSolid() ? super.func_176196_c(p_176196_1_, p_176196_2_) : false;
    }

    @Nullable
    public AxisAlignedBB func_180646_a(IBlockState p_180646_1_, IBlockAccess p_180646_2_, BlockPos p_180646_3_)
    {
        if (((Boolean)p_180646_1_.get(OPEN)).booleanValue())
        {
            return field_185506_k;
        }
        else
        {
            return ((EnumFacing)p_180646_1_.get(HORIZONTAL_FACING)).getAxis() == EnumFacing.Axis.Z ? AABB_COLLISION_BOX_ZAXIS : AABB_COLLISION_BOX_XAXIS;
        }
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
        return ((Boolean)p_176205_1_.getBlockState(p_176205_2_).get(OPEN)).booleanValue();
    }

    public IBlockState func_180642_a(World p_180642_1_, BlockPos p_180642_2_, EnumFacing p_180642_3_, float p_180642_4_, float p_180642_5_, float p_180642_6_, int p_180642_7_, EntityLivingBase p_180642_8_)
    {
        boolean flag = p_180642_1_.isBlockPowered(p_180642_2_);
        return this.getDefaultState().func_177226_a(HORIZONTAL_FACING, p_180642_8_.getHorizontalFacing()).func_177226_a(OPEN, Boolean.valueOf(flag)).func_177226_a(POWERED, Boolean.valueOf(flag)).func_177226_a(IN_WALL, Boolean.valueOf(false));
    }

    public boolean func_180639_a(World p_180639_1_, BlockPos p_180639_2_, IBlockState p_180639_3_, EntityPlayer p_180639_4_, EnumHand p_180639_5_, EnumFacing p_180639_6_, float p_180639_7_, float p_180639_8_, float p_180639_9_)
    {
        if (((Boolean)p_180639_3_.get(OPEN)).booleanValue())
        {
            p_180639_3_ = p_180639_3_.func_177226_a(OPEN, Boolean.valueOf(false));
            p_180639_1_.setBlockState(p_180639_2_, p_180639_3_, 10);
        }
        else
        {
            EnumFacing enumfacing = EnumFacing.fromAngle((double)p_180639_4_.rotationYaw);

            if (p_180639_3_.get(HORIZONTAL_FACING) == enumfacing.getOpposite())
            {
                p_180639_3_ = p_180639_3_.func_177226_a(HORIZONTAL_FACING, enumfacing);
            }

            p_180639_3_ = p_180639_3_.func_177226_a(OPEN, Boolean.valueOf(true));
            p_180639_1_.setBlockState(p_180639_2_, p_180639_3_, 10);
        }

        p_180639_1_.playEvent(p_180639_4_, ((Boolean)p_180639_3_.get(OPEN)).booleanValue() ? 1008 : 1014, p_180639_2_, 0);
        return true;
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
            boolean flag = worldIn.isBlockPowered(pos);

            if (((Boolean)state.get(POWERED)).booleanValue() != flag)
            {
                worldIn.setBlockState(pos, state.func_177226_a(POWERED, Boolean.valueOf(flag)).func_177226_a(OPEN, Boolean.valueOf(flag)), 2);

                if (((Boolean)state.get(OPEN)).booleanValue() != flag)
                {
                    worldIn.playEvent((EntityPlayer)null, flag ? 1008 : 1014, pos, 0);
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
        return true;
    }

    public IBlockState func_176203_a(int p_176203_1_)
    {
        return this.getDefaultState().func_177226_a(HORIZONTAL_FACING, EnumFacing.byHorizontalIndex(p_176203_1_)).func_177226_a(OPEN, Boolean.valueOf((p_176203_1_ & 4) != 0)).func_177226_a(POWERED, Boolean.valueOf((p_176203_1_ & 8) != 0));
    }

    public int func_176201_c(IBlockState p_176201_1_)
    {
        int i = 0;
        i = i | ((EnumFacing)p_176201_1_.get(HORIZONTAL_FACING)).getHorizontalIndex();

        if (((Boolean)p_176201_1_.get(POWERED)).booleanValue())
        {
            i |= 8;
        }

        if (((Boolean)p_176201_1_.get(OPEN)).booleanValue())
        {
            i |= 4;
        }

        return i;
    }

    protected BlockStateContainer func_180661_e()
    {
        return new BlockStateContainer(this, new IProperty[] {HORIZONTAL_FACING, OPEN, POWERED, IN_WALL});
    }

    /* ======================================== FORGE START ======================================== */

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockFenceGate &&
            state.getBlockFaceShape(world, pos, facing) == BlockFaceShape.MIDDLE_POLE)
        {
            Block connector = world.getBlockState(pos.offset(facing)).getBlock();
            return connector instanceof BlockFence || connector instanceof BlockWall;
        }
        return false;
    }

    /* ======================================== FORGE END ======================================== */

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
        if (face != EnumFacing.UP && face != EnumFacing.DOWN)
        {
            return ((EnumFacing)state.get(HORIZONTAL_FACING)).getAxis() == face.rotateY().getAxis() ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.UNDEFINED;
        }
        else
        {
            return BlockFaceShape.UNDEFINED;
        }
    }
}