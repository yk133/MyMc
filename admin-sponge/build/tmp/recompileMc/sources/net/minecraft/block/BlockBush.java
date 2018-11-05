package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBush extends Block implements net.minecraftforge.common.IPlantable
{
    protected static final AxisAlignedBB field_185515_b = new AxisAlignedBB(0.30000001192092896D, 0.0D, 0.30000001192092896D, 0.699999988079071D, 0.6000000238418579D, 0.699999988079071D);

    protected BlockBush()
    {
        this(Material.PLANTS);
    }

    protected BlockBush(Material p_i45395_1_)
    {
        this(p_i45395_1_, p_i45395_1_.getColor());
    }

    protected BlockBush(Material p_i46452_1_, MapColor p_i46452_2_)
    {
        super(p_i46452_1_, p_i46452_2_);
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.DECORATIONS);
    }

    public boolean func_176196_c(World p_176196_1_, BlockPos p_176196_2_)
    {
        IBlockState soil = p_176196_1_.getBlockState(p_176196_2_.down());
        return super.func_176196_c(p_176196_1_, p_176196_2_) && soil.getBlock().canSustainPlant(soil, p_176196_1_, p_176196_2_.down(), net.minecraft.util.EnumFacing.UP, this);
    }

    protected boolean func_185514_i(IBlockState p_185514_1_)
    {
        return p_185514_1_.getBlock() == Blocks.GRASS || p_185514_1_.getBlock() == Blocks.DIRT || p_185514_1_.getBlock() == Blocks.FARMLAND;
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        this.func_176475_e(worldIn, pos, state);
    }

    public void func_180650_b(World p_180650_1_, BlockPos p_180650_2_, IBlockState p_180650_3_, Random p_180650_4_)
    {
        this.func_176475_e(p_180650_1_, p_180650_2_, p_180650_3_);
    }

    protected void func_176475_e(World p_176475_1_, BlockPos p_176475_2_, IBlockState p_176475_3_)
    {
        if (!this.func_180671_f(p_176475_1_, p_176475_2_, p_176475_3_))
        {
            this.func_176226_b(p_176475_1_, p_176475_2_, p_176475_3_, 0);
            p_176475_1_.setBlockState(p_176475_2_, Blocks.AIR.getDefaultState(), 3);
        }
    }

    public boolean func_180671_f(World p_180671_1_, BlockPos p_180671_2_, IBlockState p_180671_3_)
    {
        if (p_180671_3_.getBlock() == this) //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
        {
            IBlockState soil = p_180671_1_.getBlockState(p_180671_2_.down());
            return soil.getBlock().canSustainPlant(soil, p_180671_1_, p_180671_2_.down(), net.minecraft.util.EnumFacing.UP, this);
        }
        return this.func_185514_i(p_180671_1_.getBlockState(p_180671_2_.down()));
    }

    public AxisAlignedBB func_185496_a(IBlockState p_185496_1_, IBlockAccess p_185496_2_, BlockPos p_185496_3_)
    {
        return field_185515_b;
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

    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
        if (this == Blocks.WHEAT)          return net.minecraftforge.common.EnumPlantType.Crop;
        if (this == Blocks.CARROTS)        return net.minecraftforge.common.EnumPlantType.Crop;
        if (this == Blocks.POTATOES)       return net.minecraftforge.common.EnumPlantType.Crop;
        if (this == Blocks.BEETROOTS)      return net.minecraftforge.common.EnumPlantType.Crop;
        if (this == Blocks.MELON_STEM)     return net.minecraftforge.common.EnumPlantType.Crop;
        if (this == Blocks.PUMPKIN_STEM)   return net.minecraftforge.common.EnumPlantType.Crop;
        if (this == Blocks.field_150330_I)       return net.minecraftforge.common.EnumPlantType.Desert;
        if (this == Blocks.field_150392_bi)      return net.minecraftforge.common.EnumPlantType.Water;
        if (this == Blocks.RED_MUSHROOM)   return net.minecraftforge.common.EnumPlantType.Cave;
        if (this == Blocks.BROWN_MUSHROOM) return net.minecraftforge.common.EnumPlantType.Cave;
        if (this == Blocks.NETHER_WART)    return net.minecraftforge.common.EnumPlantType.Nether;
        if (this == Blocks.field_150345_g)        return net.minecraftforge.common.EnumPlantType.Plains;
        if (this == Blocks.field_150329_H)      return net.minecraftforge.common.EnumPlantType.Plains;
        if (this == Blocks.field_150398_cm)   return net.minecraftforge.common.EnumPlantType.Plains;
        if (this == Blocks.field_150328_O)     return net.minecraftforge.common.EnumPlantType.Plains;
        if (this == Blocks.field_150327_N)  return net.minecraftforge.common.EnumPlantType.Plains;
        return net.minecraftforge.common.EnumPlantType.Plains;
    }

    @Override
    public IBlockState getPlant(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != this) return getDefaultState();
        return state;
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