package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockLog extends BlockRotatedPillar
{
    public static final PropertyEnum<BlockLog.EnumAxis> field_176299_a = PropertyEnum.<BlockLog.EnumAxis>create("axis", BlockLog.EnumAxis.class);

    public BlockLog()
    {
        super(Material.WOOD);
        this.func_149647_a(CreativeTabs.BUILDING_BLOCKS);
        this.func_149711_c(2.0F);
        this.func_149672_a(SoundType.WOOD);
    }

    public void func_180663_b(World p_180663_1_, BlockPos p_180663_2_, IBlockState p_180663_3_)
    {
        int i = 4;
        int j = 5;

        if (p_180663_1_.isAreaLoaded(p_180663_2_.add(-5, -5, -5), p_180663_2_.add(5, 5, 5)))
        {
            for (BlockPos blockpos : BlockPos.getAllInBox(p_180663_2_.add(-4, -4, -4), p_180663_2_.add(4, 4, 4)))
            {
                IBlockState iblockstate = p_180663_1_.getBlockState(blockpos);

                if (iblockstate.getBlock().isLeaves(iblockstate, p_180663_1_, blockpos))
                {
                    iblockstate.getBlock().beginLeavesDecay(iblockstate, p_180663_1_, blockpos);
                }
            }
        }
    }

    public IBlockState func_180642_a(World p_180642_1_, BlockPos p_180642_2_, EnumFacing p_180642_3_, float p_180642_4_, float p_180642_5_, float p_180642_6_, int p_180642_7_, EntityLivingBase p_180642_8_)
    {
        return this.func_176203_a(p_180642_7_).func_177226_a(field_176299_a, BlockLog.EnumAxis.func_176870_a(p_180642_3_.getAxis()));
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
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:

                switch ((BlockLog.EnumAxis)state.get(field_176299_a))
                {
                    case X:
                        return state.func_177226_a(field_176299_a, BlockLog.EnumAxis.Z);
                    case Z:
                        return state.func_177226_a(field_176299_a, BlockLog.EnumAxis.X);
                    default:
                        return state;
                }

            default:
                return state;
        }
    }

    @Override public boolean canSustainLeaves(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos){ return true; }
    @Override public boolean isWood(net.minecraft.world.IBlockAccess world, BlockPos pos){ return true; }

    public static enum EnumAxis implements IStringSerializable
    {
        X("x"),
        Y("y"),
        Z("z"),
        NONE("none");

        private final String field_176874_e;

        private EnumAxis(String p_i45708_3_)
        {
            this.field_176874_e = p_i45708_3_;
        }

        public String toString()
        {
            return this.field_176874_e;
        }

        public static BlockLog.EnumAxis func_176870_a(EnumFacing.Axis p_176870_0_)
        {
            switch (p_176870_0_)
            {
                case X:
                    return X;
                case Y:
                    return Y;
                case Z:
                    return Z;
                default:
                    return NONE;
            }
        }

        public String getName()
        {
            return this.field_176874_e;
        }
    }
}