package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCactus extends Block implements net.minecraftforge.common.IPlantable
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
    protected static final AxisAlignedBB field_185593_b = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.9375D, 0.9375D);
    protected static final AxisAlignedBB field_185594_c = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1.0D, 0.9375D);

    protected BlockCactus()
    {
        super(Material.CACTUS);
        this.setDefaultState(this.stateContainer.getBaseState().func_177226_a(AGE, Integer.valueOf(0)));
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.DECORATIONS);
    }

    public void func_180650_b(World p_180650_1_, BlockPos p_180650_2_, IBlockState p_180650_3_, Random p_180650_4_)
    {
        if (!p_180650_1_.func_175697_a(p_180650_2_, 1)) return; // Forge: prevent growing cactus from loading unloaded chunks with block update
        BlockPos blockpos = p_180650_2_.up();

        if (p_180650_1_.isAirBlock(blockpos))
        {
            int i;

            for (i = 1; p_180650_1_.getBlockState(p_180650_2_.down(i)).getBlock() == this; ++i)
            {
                ;
            }

            if (i < 3)
            {
                int j = ((Integer)p_180650_3_.get(AGE)).intValue();

                if(net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_180650_1_, blockpos, p_180650_3_, true))
                {
                if (j == 15)
                {
                    p_180650_1_.setBlockState(blockpos, this.getDefaultState());
                    IBlockState iblockstate = p_180650_3_.func_177226_a(AGE, Integer.valueOf(0));
                    p_180650_1_.setBlockState(p_180650_2_, iblockstate, 4);
                    iblockstate.neighborChanged(p_180650_1_, blockpos, this, p_180650_2_);
                }
                else
                {
                    p_180650_1_.setBlockState(p_180650_2_, p_180650_3_.func_177226_a(AGE, Integer.valueOf(j + 1)), 4);
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_180650_1_, p_180650_2_, p_180650_3_, p_180650_1_.getBlockState(p_180650_2_));
                }
            }
        }
    }

    public AxisAlignedBB func_180646_a(IBlockState p_180646_1_, IBlockAccess p_180646_2_, BlockPos p_180646_3_)
    {
        return field_185593_b;
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB func_180640_a(IBlockState p_180640_1_, World p_180640_2_, BlockPos p_180640_3_)
    {
        return field_185594_c.offset(p_180640_3_);
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
        return super.func_176196_c(p_176196_1_, p_176196_2_) ? this.func_176586_d(p_176196_1_, p_176196_2_) : false;
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!this.func_176586_d(worldIn, pos))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    public boolean func_176586_d(World p_176586_1_, BlockPos p_176586_2_)
    {
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
        {
            Material material = p_176586_1_.getBlockState(p_176586_2_.offset(enumfacing)).getMaterial();

            if (material.isSolid() || material == Material.LAVA)
            {
                return false;
            }
        }

        IBlockState state = p_176586_1_.getBlockState(p_176586_2_.down());
        return state.getBlock().canSustainPlant(state, p_176586_1_, p_176586_2_.down(), EnumFacing.UP, this) && !p_176586_1_.getBlockState(p_176586_2_.up()).getMaterial().isLiquid();
    }

    public void func_180634_a(World p_180634_1_, BlockPos p_180634_2_, IBlockState p_180634_3_, Entity p_180634_4_)
    {
        p_180634_4_.attackEntityFrom(DamageSource.CACTUS, 1.0F);
    }

    public IBlockState func_176203_a(int p_176203_1_)
    {
        return this.getDefaultState().func_177226_a(AGE, Integer.valueOf(p_176203_1_));
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
        return ((Integer)p_176201_1_.get(AGE)).intValue();
    }

    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
        return net.minecraftforge.common.EnumPlantType.Desert;
    }

    @Override
    public IBlockState getPlant(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
        return getDefaultState();
    }

    protected BlockStateContainer func_180661_e()
    {
        return new BlockStateContainer(this, new IProperty[] {AGE});
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