package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFarmland extends Block
{
    public static final PropertyInteger MOISTURE = PropertyInteger.create("moisture", 0, 7);
    protected static final AxisAlignedBB field_185665_b = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9375D, 1.0D);
    protected static final AxisAlignedBB field_194405_c = new AxisAlignedBB(0.0D, 0.9375D, 0.0D, 1.0D, 1.0D, 1.0D);

    protected BlockFarmland()
    {
        super(Material.GROUND);
        this.setDefaultState(this.stateContainer.getBaseState().func_177226_a(MOISTURE, Integer.valueOf(0)));
        this.func_149675_a(true);
        this.func_149713_g(255);
    }

    public AxisAlignedBB func_185496_a(IBlockState p_185496_1_, IBlockAccess p_185496_2_, BlockPos p_185496_3_)
    {
        return field_185665_b;
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

    public void func_180650_b(World p_180650_1_, BlockPos p_180650_2_, IBlockState p_180650_3_, Random p_180650_4_)
    {
        int i = ((Integer)p_180650_3_.get(MOISTURE)).intValue();

        if (!this.hasWater(p_180650_1_, p_180650_2_) && !p_180650_1_.isRainingAt(p_180650_2_.up()))
        {
            if (i > 0)
            {
                p_180650_1_.setBlockState(p_180650_2_, p_180650_3_.func_177226_a(MOISTURE, Integer.valueOf(i - 1)), 2);
            }
            else if (!this.hasCrops(p_180650_1_, p_180650_2_))
            {
                func_190970_b(p_180650_1_, p_180650_2_);
            }
        }
        else if (i < 7)
        {
            p_180650_1_.setBlockState(p_180650_2_, p_180650_3_.func_177226_a(MOISTURE, Integer.valueOf(7)), 2);
        }
    }

    /**
     * Block's chance to react to a living entity falling on it.
     */
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        if (net.minecraftforge.common.ForgeHooks.onFarmlandTrample(worldIn, pos, Blocks.DIRT.getDefaultState(), fallDistance, entityIn)) // Forge: Move logic to Entity#canTrample
        {
            func_190970_b(worldIn, pos);
        }

        super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
    }

    protected static void func_190970_b(World p_190970_0_, BlockPos p_190970_1_)
    {
        p_190970_0_.setBlockState(p_190970_1_, Blocks.DIRT.getDefaultState());
        AxisAlignedBB axisalignedbb = field_194405_c.offset(p_190970_1_);

        for (Entity entity : p_190970_0_.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb))
        {
            double d0 = Math.min(axisalignedbb.maxY - axisalignedbb.minY, axisalignedbb.maxY - entity.getBoundingBox().minY);
            entity.setPositionAndUpdate(entity.posX, entity.posY + d0 + 0.001D, entity.posZ);
        }
    }

    private boolean hasCrops(World worldIn, BlockPos p_176529_2_)
    {
        Block block = worldIn.getBlockState(p_176529_2_.up()).getBlock();
        return block instanceof net.minecraftforge.common.IPlantable && canSustainPlant(worldIn.getBlockState(p_176529_2_), worldIn, p_176529_2_, net.minecraft.util.EnumFacing.UP, (net.minecraftforge.common.IPlantable)block);
    }

    private boolean hasWater(World worldIn, BlockPos p_176530_2_)
    {
        for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(p_176530_2_.add(-4, 0, -4), p_176530_2_.add(4, 1, 4)))
        {
            if (worldIn.getBlockState(blockpos$mutableblockpos).getMaterial() == Material.WATER)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

        if (worldIn.getBlockState(pos.up()).getMaterial().isSolid())
        {
            func_190970_b(worldIn, pos);
        }
    }

    public void func_176213_c(World p_176213_1_, BlockPos p_176213_2_, IBlockState p_176213_3_)
    {
        super.func_176213_c(p_176213_1_, p_176213_2_, p_176213_3_);

        if (p_176213_1_.getBlockState(p_176213_2_.up()).getMaterial().isSolid())
        {
            func_190970_b(p_176213_1_, p_176213_2_);
        }
    }

    /**
     * @deprecated call via {@link IBlockState#shouldSideBeRendered(IBlockAccess,BlockPos,EnumFacing)} whenever
     * possible. Implementing/overriding is fine.
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing p_176225_4_)
    {
        switch (p_176225_4_)
        {
            case UP:
                return true;
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                IBlockState iblockstate = blockAccess.getBlockState(pos.offset(p_176225_4_));
                Block block = iblockstate.getBlock();
                return !iblockstate.func_185914_p() && block != Blocks.FARMLAND && block != Blocks.GRASS_PATH;
            default:
                return super.shouldSideBeRendered(blockState, blockAccess, pos, p_176225_4_);
        }
    }

    public Item func_180660_a(IBlockState p_180660_1_, Random p_180660_2_, int p_180660_3_)
    {
        return Blocks.DIRT.func_180660_a(Blocks.DIRT.getDefaultState().func_177226_a(BlockDirt.field_176386_a, BlockDirt.DirtType.DIRT), p_180660_2_, p_180660_3_);
    }

    public IBlockState func_176203_a(int p_176203_1_)
    {
        return this.getDefaultState().func_177226_a(MOISTURE, Integer.valueOf(p_176203_1_ & 7));
    }

    public int func_176201_c(IBlockState p_176201_1_)
    {
        return ((Integer)p_176201_1_.get(MOISTURE)).intValue();
    }

    protected BlockStateContainer func_180661_e()
    {
        return new BlockStateContainer(this, new IProperty[] {MOISTURE});
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