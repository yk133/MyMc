package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSnow extends Block
{
    public static final PropertyInteger LAYERS = PropertyInteger.create("layers", 1, 8);
    protected static final AxisAlignedBB[] field_185702_b = new AxisAlignedBB[] {new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};

    protected BlockSnow()
    {
        super(Material.SNOW);
        this.setDefaultState(this.stateContainer.getBaseState().func_177226_a(LAYERS, Integer.valueOf(1)));
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.DECORATIONS);
    }

    public AxisAlignedBB func_185496_a(IBlockState p_185496_1_, IBlockAccess p_185496_2_, BlockPos p_185496_3_)
    {
        return field_185702_b[((Integer)p_185496_1_.get(LAYERS)).intValue()];
    }

    public boolean func_176205_b(IBlockAccess p_176205_1_, BlockPos p_176205_2_)
    {
        return ((Integer)p_176205_1_.getBlockState(p_176205_2_).get(LAYERS)).intValue() < 5;
    }

    /**
     * Determines if the block is solid enough on the top side to support other blocks, like redstone components.
     * @deprecated prefer calling {@link IBlockState#isTopSolid()} wherever possible
     */
    public boolean isTopSolid(IBlockState state)
    {
        return ((Integer)state.get(LAYERS)).intValue() == 8;
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

    @Nullable
    public AxisAlignedBB func_180646_a(IBlockState p_180646_1_, IBlockAccess p_180646_2_, BlockPos p_180646_3_)
    {
        int i = ((Integer)p_180646_1_.get(LAYERS)).intValue() - 1;
        float f = 0.125F;
        AxisAlignedBB axisalignedbb = p_180646_1_.func_185900_c(p_180646_2_, p_180646_3_);
        return new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.maxX, (double)((float)i * 0.125F), axisalignedbb.maxZ);
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
        IBlockState iblockstate = p_176196_1_.getBlockState(p_176196_2_.down());
        Block block = iblockstate.getBlock();

        if (block != Blocks.ICE && block != Blocks.PACKED_ICE && block != Blocks.BARRIER)
        {
            BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(p_176196_1_, p_176196_2_.down(), EnumFacing.UP);
            return blockfaceshape == BlockFaceShape.SOLID || iblockstate.getBlock().isLeaves(iblockstate, p_176196_1_, p_176196_2_.down()) || block == this && ((Integer)iblockstate.get(LAYERS)).intValue() == 8;
        }
        else
        {
            return false;
        }
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        this.func_176314_e(worldIn, pos, state);
    }

    private boolean func_176314_e(World p_176314_1_, BlockPos p_176314_2_, IBlockState p_176314_3_)
    {
        if (!this.func_176196_c(p_176314_1_, p_176314_2_))
        {
            p_176314_1_.removeBlock(p_176314_2_);
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
     * Block.removedByPlayer
     */
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        worldIn.removeBlock(pos);
    }

    public Item func_180660_a(IBlockState p_180660_1_, Random p_180660_2_, int p_180660_3_)
    {
        return Items.SNOWBALL;
    }

    public int func_149745_a(Random p_149745_1_)
    {
        return 1;
    }

    public void func_180650_b(World p_180650_1_, BlockPos p_180650_2_, IBlockState p_180650_3_, Random p_180650_4_)
    {
        if (p_180650_1_.getLightFor(EnumSkyBlock.BLOCK, p_180650_2_) > 11)
        {
            p_180650_1_.removeBlock(p_180650_2_);
        }
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
            IBlockState iblockstate = blockAccess.getBlockState(pos.offset(p_176225_4_));
            return iblockstate.getBlock() == this && ((Integer)iblockstate.get(LAYERS)).intValue() >= ((Integer)blockState.get(LAYERS)).intValue() ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, p_176225_4_);
        }
    }

    public IBlockState func_176203_a(int p_176203_1_)
    {
        return this.getDefaultState().func_177226_a(LAYERS, Integer.valueOf((p_176203_1_ & 7) + 1));
    }

    public boolean func_176200_f(IBlockAccess p_176200_1_, BlockPos p_176200_2_)
    {
        return ((Integer)p_176200_1_.getBlockState(p_176200_2_).get(LAYERS)).intValue() == 1;
    }

    public int func_176201_c(IBlockState p_176201_1_)
    {
        return ((Integer)p_176201_1_.get(LAYERS)).intValue() - 1;
    }

    @Override public int quantityDropped(IBlockState state, int fortune, Random random){ return ((Integer)state.get(LAYERS)) + 1; }

    protected BlockStateContainer func_180661_e()
    {
        return new BlockStateContainer(this, new IProperty[] {LAYERS});
    }
}