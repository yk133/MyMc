package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockChorusPlant extends Block
{
    public static final PropertyBool field_185609_a = PropertyBool.create("north");
    public static final PropertyBool field_185610_b = PropertyBool.create("east");
    public static final PropertyBool field_185611_c = PropertyBool.create("south");
    public static final PropertyBool field_185612_d = PropertyBool.create("west");
    public static final PropertyBool field_185613_e = PropertyBool.create("up");
    public static final PropertyBool field_185614_f = PropertyBool.create("down");

    protected BlockChorusPlant()
    {
        super(Material.PLANTS, MapColor.PURPLE);
        this.func_149647_a(CreativeTabs.DECORATIONS);
        this.setDefaultState(this.stateContainer.getBaseState().func_177226_a(field_185609_a, Boolean.valueOf(false)).func_177226_a(field_185610_b, Boolean.valueOf(false)).func_177226_a(field_185611_c, Boolean.valueOf(false)).func_177226_a(field_185612_d, Boolean.valueOf(false)).func_177226_a(field_185613_e, Boolean.valueOf(false)).func_177226_a(field_185614_f, Boolean.valueOf(false)));
    }

    public IBlockState func_176221_a(IBlockState p_176221_1_, IBlockAccess p_176221_2_, BlockPos p_176221_3_)
    {
        Block block = p_176221_2_.getBlockState(p_176221_3_.down()).getBlock();
        Block block1 = p_176221_2_.getBlockState(p_176221_3_.up()).getBlock();
        Block block2 = p_176221_2_.getBlockState(p_176221_3_.north()).getBlock();
        Block block3 = p_176221_2_.getBlockState(p_176221_3_.east()).getBlock();
        Block block4 = p_176221_2_.getBlockState(p_176221_3_.south()).getBlock();
        Block block5 = p_176221_2_.getBlockState(p_176221_3_.west()).getBlock();
        return p_176221_1_.func_177226_a(field_185614_f, Boolean.valueOf(block == this || block == Blocks.CHORUS_FLOWER || block == Blocks.END_STONE)).func_177226_a(field_185613_e, Boolean.valueOf(block1 == this || block1 == Blocks.CHORUS_FLOWER)).func_177226_a(field_185609_a, Boolean.valueOf(block2 == this || block2 == Blocks.CHORUS_FLOWER)).func_177226_a(field_185610_b, Boolean.valueOf(block3 == this || block3 == Blocks.CHORUS_FLOWER)).func_177226_a(field_185611_c, Boolean.valueOf(block4 == this || block4 == Blocks.CHORUS_FLOWER)).func_177226_a(field_185612_d, Boolean.valueOf(block5 == this || block5 == Blocks.CHORUS_FLOWER));
    }

    public AxisAlignedBB func_185496_a(IBlockState p_185496_1_, IBlockAccess p_185496_2_, BlockPos p_185496_3_)
    {
        p_185496_1_ = p_185496_1_.func_185899_b(p_185496_2_, p_185496_3_);
        float f = 0.1875F;
        float f1 = ((Boolean)p_185496_1_.get(field_185612_d)).booleanValue() ? 0.0F : 0.1875F;
        float f2 = ((Boolean)p_185496_1_.get(field_185614_f)).booleanValue() ? 0.0F : 0.1875F;
        float f3 = ((Boolean)p_185496_1_.get(field_185609_a)).booleanValue() ? 0.0F : 0.1875F;
        float f4 = ((Boolean)p_185496_1_.get(field_185610_b)).booleanValue() ? 1.0F : 0.8125F;
        float f5 = ((Boolean)p_185496_1_.get(field_185613_e)).booleanValue() ? 1.0F : 0.8125F;
        float f6 = ((Boolean)p_185496_1_.get(field_185611_c)).booleanValue() ? 1.0F : 0.8125F;
        return new AxisAlignedBB((double)f1, (double)f2, (double)f3, (double)f4, (double)f5, (double)f6);
    }

    public void func_185477_a(IBlockState p_185477_1_, World p_185477_2_, BlockPos p_185477_3_, AxisAlignedBB p_185477_4_, List<AxisAlignedBB> p_185477_5_, @Nullable Entity p_185477_6_, boolean p_185477_7_)
    {
        if (!p_185477_7_)
        {
            p_185477_1_ = p_185477_1_.func_185899_b(p_185477_2_, p_185477_3_);
        }

        float f = 0.1875F;
        float f1 = 0.8125F;
        func_185492_a(p_185477_3_, p_185477_4_, p_185477_5_, new AxisAlignedBB(0.1875D, 0.1875D, 0.1875D, 0.8125D, 0.8125D, 0.8125D));

        if (((Boolean)p_185477_1_.get(field_185612_d)).booleanValue())
        {
            func_185492_a(p_185477_3_, p_185477_4_, p_185477_5_, new AxisAlignedBB(0.0D, 0.1875D, 0.1875D, 0.1875D, 0.8125D, 0.8125D));
        }

        if (((Boolean)p_185477_1_.get(field_185610_b)).booleanValue())
        {
            func_185492_a(p_185477_3_, p_185477_4_, p_185477_5_, new AxisAlignedBB(0.8125D, 0.1875D, 0.1875D, 1.0D, 0.8125D, 0.8125D));
        }

        if (((Boolean)p_185477_1_.get(field_185613_e)).booleanValue())
        {
            func_185492_a(p_185477_3_, p_185477_4_, p_185477_5_, new AxisAlignedBB(0.1875D, 0.8125D, 0.1875D, 0.8125D, 1.0D, 0.8125D));
        }

        if (((Boolean)p_185477_1_.get(field_185614_f)).booleanValue())
        {
            func_185492_a(p_185477_3_, p_185477_4_, p_185477_5_, new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.1875D, 0.8125D));
        }

        if (((Boolean)p_185477_1_.get(field_185609_a)).booleanValue())
        {
            func_185492_a(p_185477_3_, p_185477_4_, p_185477_5_, new AxisAlignedBB(0.1875D, 0.1875D, 0.0D, 0.8125D, 0.8125D, 0.1875D));
        }

        if (((Boolean)p_185477_1_.get(field_185611_c)).booleanValue())
        {
            func_185492_a(p_185477_3_, p_185477_4_, p_185477_5_, new AxisAlignedBB(0.1875D, 0.1875D, 0.8125D, 0.8125D, 0.8125D, 1.0D));
        }
    }

    public int func_176201_c(IBlockState p_176201_1_)
    {
        return 0;
    }

    public void func_180650_b(World p_180650_1_, BlockPos p_180650_2_, IBlockState p_180650_3_, Random p_180650_4_)
    {
        if (!this.func_185608_b(p_180650_1_, p_180650_2_))
        {
            p_180650_1_.destroyBlock(p_180650_2_, true);
        }
    }

    public Item func_180660_a(IBlockState p_180660_1_, Random p_180660_2_, int p_180660_3_)
    {
        return Items.CHORUS_FRUIT;
    }

    public int func_149745_a(Random p_149745_1_)
    {
        return p_149745_1_.nextInt(2);
    }

    /**
     * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
     */
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public boolean func_149662_c(IBlockState p_149662_1_)
    {
        return false;
    }

    public boolean func_176196_c(World p_176196_1_, BlockPos p_176196_2_)
    {
        return super.func_176196_c(p_176196_1_, p_176196_2_) ? this.func_185608_b(p_176196_1_, p_176196_2_) : false;
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!this.func_185608_b(worldIn, pos))
        {
            worldIn.func_175684_a(pos, this, 1);
        }
    }

    public boolean func_185608_b(World p_185608_1_, BlockPos p_185608_2_)
    {
        boolean flag = p_185608_1_.isAirBlock(p_185608_2_.up());
        boolean flag1 = p_185608_1_.isAirBlock(p_185608_2_.down());

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos blockpos = p_185608_2_.offset(enumfacing);
            Block block = p_185608_1_.getBlockState(blockpos).getBlock();

            if (block == this)
            {
                if (!flag && !flag1)
                {
                    return false;
                }

                Block block1 = p_185608_1_.getBlockState(blockpos.down()).getBlock();

                if (block1 == this || block1 == Blocks.END_STONE)
                {
                    return true;
                }
            }
        }

        Block block2 = p_185608_1_.getBlockState(p_185608_2_.down()).getBlock();
        return block2 == this || block2 == Blocks.END_STONE;
    }

    protected BlockStateContainer func_180661_e()
    {
        return new BlockStateContainer(this, new IProperty[] {field_185609_a, field_185610_b, field_185611_c, field_185612_d, field_185613_e, field_185614_f});
    }

    public boolean func_176205_b(IBlockAccess p_176205_1_, BlockPos p_176205_2_)
    {
        return false;
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

    /**
     * @deprecated call via {@link IBlockState#shouldSideBeRendered(IBlockAccess,BlockPos,EnumFacing)} whenever
     * possible. Implementing/overriding is fine.
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing p_176225_4_)
    {
        Block block = blockAccess.getBlockState(pos.offset(p_176225_4_)).getBlock();
        return block != this && block != Blocks.CHORUS_FLOWER && (p_176225_4_ != EnumFacing.DOWN || block != Blocks.END_STONE);
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
}