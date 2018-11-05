package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockSlab extends Block
{
    public static final PropertyEnum<BlockSlab.EnumBlockHalf> field_176554_a = PropertyEnum.<BlockSlab.EnumBlockHalf>create("half", BlockSlab.EnumBlockHalf.class);
    protected static final AxisAlignedBB field_185676_b = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    protected static final AxisAlignedBB field_185677_c = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);

    public BlockSlab(Material p_i45714_1_)
    {
        this(p_i45714_1_, p_i45714_1_.getColor());
    }

    public BlockSlab(Material p_i47249_1_, MapColor p_i47249_2_)
    {
        super(p_i47249_1_, p_i47249_2_);
        this.field_149787_q = this.func_176552_j();
        this.func_149713_g(255);
    }

    protected boolean canSilkHarvest()
    {
        return false;
    }

    public AxisAlignedBB func_185496_a(IBlockState p_185496_1_, IBlockAccess p_185496_2_, BlockPos p_185496_3_)
    {
        if (this.func_176552_j())
        {
            return field_185505_j;
        }
        else
        {
            return p_185496_1_.get(field_176554_a) == BlockSlab.EnumBlockHalf.TOP ? field_185677_c : field_185676_b;
        }
    }

    /**
     * Determines if the block is solid enough on the top side to support other blocks, like redstone components.
     * @deprecated prefer calling {@link IBlockState#isTopSolid()} wherever possible
     */
    public boolean isTopSolid(IBlockState state)
    {
        return ((BlockSlab)state.getBlock()).func_176552_j() || state.get(field_176554_a) == BlockSlab.EnumBlockHalf.TOP;
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
        if (((BlockSlab)state.getBlock()).func_176552_j())
        {
            return BlockFaceShape.SOLID;
        }
        else if (face == EnumFacing.UP && state.get(field_176554_a) == BlockSlab.EnumBlockHalf.TOP)
        {
            return BlockFaceShape.SOLID;
        }
        else
        {
            return face == EnumFacing.DOWN && state.get(field_176554_a) == BlockSlab.EnumBlockHalf.BOTTOM ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
        }
    }

    public boolean func_149662_c(IBlockState p_149662_1_)
    {
        return this.func_176552_j();
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        if (net.minecraftforge.common.ForgeModContainer.disableStairSlabCulling)
            return super.doesSideBlockRendering(state, world, pos, face);

        if ( state.func_185914_p() )
            return true;

        EnumBlockHalf side = state.get(field_176554_a);
        return (side == EnumBlockHalf.TOP && face == EnumFacing.UP) || (side == EnumBlockHalf.BOTTOM && face == EnumFacing.DOWN);
    }

    public IBlockState func_180642_a(World p_180642_1_, BlockPos p_180642_2_, EnumFacing p_180642_3_, float p_180642_4_, float p_180642_5_, float p_180642_6_, int p_180642_7_, EntityLivingBase p_180642_8_)
    {
        IBlockState iblockstate = super.func_180642_a(p_180642_1_, p_180642_2_, p_180642_3_, p_180642_4_, p_180642_5_, p_180642_6_, p_180642_7_, p_180642_8_).func_177226_a(field_176554_a, BlockSlab.EnumBlockHalf.BOTTOM);

        if (this.func_176552_j())
        {
            return iblockstate;
        }
        else
        {
            return p_180642_3_ != EnumFacing.DOWN && (p_180642_3_ == EnumFacing.UP || (double)p_180642_5_ <= 0.5D) ? iblockstate : iblockstate.func_177226_a(field_176554_a, BlockSlab.EnumBlockHalf.TOP);
        }
    }

    public int func_149745_a(Random p_149745_1_)
    {
        return this.func_176552_j() ? 2 : 1;
    }

    /**
     * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
     */
    public boolean isFullCube(IBlockState state)
    {
        return this.func_176552_j();
    }

    /**
     * @deprecated call via {@link IBlockState#shouldSideBeRendered(IBlockAccess,BlockPos,EnumFacing)} whenever
     * possible. Implementing/overriding is fine.
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing p_176225_4_)
    {
        if (this.func_176552_j())
        {
            return super.shouldSideBeRendered(blockState, blockAccess, pos, p_176225_4_);
        }
        else if (p_176225_4_ != EnumFacing.UP && p_176225_4_ != EnumFacing.DOWN && !super.shouldSideBeRendered(blockState, blockAccess, pos, p_176225_4_))
        {
            return false;
        }
        else if (false) // Forge: Additional logic breaks doesSideBlockRendering and is no longer useful.
        {
            IBlockState iblockstate = blockAccess.getBlockState(pos.offset(p_176225_4_));
            boolean flag = func_185675_i(iblockstate) && iblockstate.get(field_176554_a) == BlockSlab.EnumBlockHalf.TOP;
            boolean flag1 = func_185675_i(blockState) && blockState.get(field_176554_a) == BlockSlab.EnumBlockHalf.TOP;

            if (flag1)
            {
                if (p_176225_4_ == EnumFacing.DOWN)
                {
                    return true;
                }
                else if (p_176225_4_ == EnumFacing.UP && super.shouldSideBeRendered(blockState, blockAccess, pos, p_176225_4_))
                {
                    return true;
                }
                else
                {
                    return !func_185675_i(iblockstate) || !flag;
                }
            }
            else if (p_176225_4_ == EnumFacing.UP)
            {
                return true;
            }
            else if (p_176225_4_ == EnumFacing.DOWN && super.shouldSideBeRendered(blockState, blockAccess, pos, p_176225_4_))
            {
                return true;
            }
            else
            {
                return !func_185675_i(iblockstate) || flag;
            }
        }
        return super.shouldSideBeRendered(blockState, blockAccess, pos, p_176225_4_);
    }

    @SideOnly(Side.CLIENT)
    protected static boolean func_185675_i(IBlockState p_185675_0_)
    {
        Block block = p_185675_0_.getBlock();
        return block == Blocks.STONE_SLAB || block == Blocks.field_150376_bx || block == Blocks.field_180389_cP || block == Blocks.PURPUR_SLAB;
    }

    public abstract String func_150002_b(int p_150002_1_);

    public abstract boolean func_176552_j();

    public abstract IProperty<?> func_176551_l();

    public abstract Comparable<?> func_185674_a(ItemStack p_185674_1_);

    public static enum EnumBlockHalf implements IStringSerializable
    {
        TOP("top"),
        BOTTOM("bottom");

        private final String field_176988_c;

        private EnumBlockHalf(String p_i45713_3_)
        {
            this.field_176988_c = p_i45713_3_;
        }

        public String toString()
        {
            return this.field_176988_c;
        }

        public String getName()
        {
            return this.field_176988_c;
        }
    }
}