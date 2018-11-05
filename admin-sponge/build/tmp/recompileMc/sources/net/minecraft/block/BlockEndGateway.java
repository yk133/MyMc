package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEndGateway extends BlockContainer
{
    protected BlockEndGateway(Material p_i46687_1_)
    {
        super(p_i46687_1_);
        this.func_149715_a(1.0F);
    }

    public TileEntity func_149915_a(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityEndGateway();
    }

    /**
     * @deprecated call via {@link IBlockState#shouldSideBeRendered(IBlockAccess,BlockPos,EnumFacing)} whenever
     * possible. Implementing/overriding is fine.
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing p_176225_4_)
    {
        IBlockState iblockstate = blockAccess.getBlockState(pos.offset(p_176225_4_));
        Block block = iblockstate.getBlock();
        return !iblockstate.func_185914_p() && block != Blocks.END_GATEWAY;
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

    public int func_149745_a(Random p_149745_1_)
    {
        return 0;
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    @SideOnly(Side.CLIENT)
    public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityEndGateway)
        {
            int i = ((TileEntityEndGateway)tileentity).func_184304_i();

            for (int j = 0; j < i; ++j)
            {
                double d0 = (double)((float)pos.getX() + rand.nextFloat());
                double d1 = (double)((float)pos.getY() + rand.nextFloat());
                double d2 = (double)((float)pos.getZ() + rand.nextFloat());
                double d3 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
                double d4 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
                double d5 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
                int k = rand.nextInt(2) * 2 - 1;

                if (rand.nextBoolean())
                {
                    d2 = (double)pos.getZ() + 0.5D + 0.25D * (double)k;
                    d5 = (double)(rand.nextFloat() * 2.0F * (float)k);
                }
                else
                {
                    d0 = (double)pos.getX() + 0.5D + 0.25D * (double)k;
                    d3 = (double)(rand.nextFloat() * 2.0F * (float)k);
                }

                worldIn.func_175688_a(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
            }
        }
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return ItemStack.EMPTY;
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     * @deprecated call via {@link IBlockState#getMapColor(IBlockAccess,BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return MapColor.BLACK;
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