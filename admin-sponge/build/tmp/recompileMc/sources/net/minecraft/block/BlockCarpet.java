package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCarpet extends Block
{
    public static final PropertyEnum<EnumDyeColor> field_176330_a = PropertyEnum.<EnumDyeColor>create("color", EnumDyeColor.class);
    protected static final AxisAlignedBB field_185758_b = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);

    protected BlockCarpet()
    {
        super(Material.CARPET);
        this.setDefaultState(this.stateContainer.getBaseState().func_177226_a(field_176330_a, EnumDyeColor.WHITE));
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.DECORATIONS);
    }

    public AxisAlignedBB func_185496_a(IBlockState p_185496_1_, IBlockAccess p_185496_2_, BlockPos p_185496_3_)
    {
        return field_185758_b;
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     * @deprecated call via {@link IBlockState#getMapColor(IBlockAccess,BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return MapColor.func_193558_a((EnumDyeColor)state.get(field_176330_a));
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
        return super.func_176196_c(p_176196_1_, p_176196_2_) && this.func_176329_d(p_176196_1_, p_176196_2_);
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        this.func_176328_e(worldIn, pos, state);
    }

    private boolean func_176328_e(World p_176328_1_, BlockPos p_176328_2_, IBlockState p_176328_3_)
    {
        if (!this.func_176329_d(p_176328_1_, p_176328_2_))
        {
            this.func_176226_b(p_176328_1_, p_176328_2_, p_176328_3_, 0);
            p_176328_1_.removeBlock(p_176328_2_);
            return false;
        }
        else
        {
            return true;
        }
    }

    private boolean func_176329_d(World p_176329_1_, BlockPos p_176329_2_)
    {
        return !p_176329_1_.isAirBlock(p_176329_2_.down());
    }

    /**
     * @deprecated call via {@link IBlockState#shouldSideBeRendered(IBlockAccess,BlockPos,EnumFacing)} whenever
     * possible. Implementing/overriding is fine.
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing p_176225_4_)
    {
        if (p_176225_4_ == EnumFacing.UP)
        {
            return true;
        }
        else
        {
            return blockAccess.getBlockState(pos.offset(p_176225_4_)).getBlock() == this ? true : super.shouldSideBeRendered(blockState, blockAccess, pos, p_176225_4_);
        }
    }

    public int func_180651_a(IBlockState p_180651_1_)
    {
        return ((EnumDyeColor)p_180651_1_.get(field_176330_a)).func_176765_a();
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void fillItemGroup(CreativeTabs group, NonNullList<ItemStack> items)
    {
        for (int i = 0; i < 16; ++i)
        {
            items.add(new ItemStack(this, 1, i));
        }
    }

    public IBlockState func_176203_a(int p_176203_1_)
    {
        return this.getDefaultState().func_177226_a(field_176330_a, EnumDyeColor.func_176764_b(p_176203_1_));
    }

    public int func_176201_c(IBlockState p_176201_1_)
    {
        return ((EnumDyeColor)p_176201_1_.get(field_176330_a)).func_176765_a();
    }

    protected BlockStateContainer func_180661_e()
    {
        return new BlockStateContainer(this, new IProperty[] {field_176330_a});
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