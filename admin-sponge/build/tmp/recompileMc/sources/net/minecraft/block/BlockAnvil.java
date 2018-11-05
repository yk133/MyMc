package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockAnvil extends BlockFalling
{
    public static final PropertyDirection FACING = BlockHorizontal.HORIZONTAL_FACING;
    public static final PropertyInteger field_176505_b = PropertyInteger.create("damage", 0, 2);
    protected static final AxisAlignedBB X_AXIS_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.125D, 1.0D, 1.0D, 0.875D);
    protected static final AxisAlignedBB Z_AXIS_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.0D, 0.875D, 1.0D, 1.0D);
    protected static final Logger LOGGER = LogManager.getLogger();

    protected BlockAnvil()
    {
        super(Material.ANVIL);
        this.setDefaultState(this.stateContainer.getBaseState().func_177226_a(FACING, EnumFacing.NORTH).func_177226_a(field_176505_b, Integer.valueOf(0)));
        this.func_149713_g(0);
        this.func_149647_a(CreativeTabs.DECORATIONS);
    }

    /**
     * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
     */
    public boolean isFullCube(IBlockState state)
    {
        return false;
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

    public boolean func_149662_c(IBlockState p_149662_1_)
    {
        return false;
    }

    public IBlockState func_180642_a(World p_180642_1_, BlockPos p_180642_2_, EnumFacing p_180642_3_, float p_180642_4_, float p_180642_5_, float p_180642_6_, int p_180642_7_, EntityLivingBase p_180642_8_)
    {
        EnumFacing enumfacing = p_180642_8_.getHorizontalFacing().rotateY();

        try
        {
            return super.func_180642_a(p_180642_1_, p_180642_2_, p_180642_3_, p_180642_4_, p_180642_5_, p_180642_6_, p_180642_7_, p_180642_8_).func_177226_a(FACING, enumfacing).func_177226_a(field_176505_b, Integer.valueOf(p_180642_7_ >> 2));
        }
        catch (IllegalArgumentException var11)
        {
            if (!p_180642_1_.isRemote)
            {
                LOGGER.warn(String.format("Invalid damage property for anvil at %s. Found %d, must be in [0, 1, 2]", p_180642_2_, p_180642_7_ >> 2));

                if (p_180642_8_ instanceof EntityPlayer)
                {
                    p_180642_8_.sendMessage(new TextComponentTranslation("Invalid damage property. Please pick in [0, 1, 2]", new Object[0]));
                }
            }

            return super.func_180642_a(p_180642_1_, p_180642_2_, p_180642_3_, p_180642_4_, p_180642_5_, p_180642_6_, 0, p_180642_8_).func_177226_a(FACING, enumfacing).func_177226_a(field_176505_b, Integer.valueOf(0));
        }
    }

    public boolean func_180639_a(World p_180639_1_, BlockPos p_180639_2_, IBlockState p_180639_3_, EntityPlayer p_180639_4_, EnumHand p_180639_5_, EnumFacing p_180639_6_, float p_180639_7_, float p_180639_8_, float p_180639_9_)
    {
        if (!p_180639_1_.isRemote)
        {
            p_180639_4_.displayGui(new BlockAnvil.Anvil(p_180639_1_, p_180639_2_));
        }

        return true;
    }

    public int func_180651_a(IBlockState p_180651_1_)
    {
        return ((Integer)p_180651_1_.get(field_176505_b)).intValue();
    }

    public AxisAlignedBB func_185496_a(IBlockState p_185496_1_, IBlockAccess p_185496_2_, BlockPos p_185496_3_)
    {
        EnumFacing enumfacing = (EnumFacing)p_185496_1_.get(FACING);
        return enumfacing.getAxis() == EnumFacing.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void fillItemGroup(CreativeTabs group, NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(this));
        items.add(new ItemStack(this, 1, 1));
        items.add(new ItemStack(this, 1, 2));
    }

    protected void onStartFalling(EntityFallingBlock fallingEntity)
    {
        fallingEntity.setHurtEntities(true);
    }

    public void onEndFalling(World worldIn, BlockPos pos, IBlockState fallingState, IBlockState hitState)
    {
        worldIn.playEvent(1031, pos, 0);
    }

    public void onBroken(World worldIn, BlockPos pos)
    {
        worldIn.playEvent(1029, pos, 0);
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
        return this.getDefaultState().func_177226_a(FACING, EnumFacing.byHorizontalIndex(p_176203_1_ & 3)).func_177226_a(field_176505_b, Integer.valueOf((p_176203_1_ & 15) >> 2));
    }

    public int func_176201_c(IBlockState p_176201_1_)
    {
        int i = 0;
        i = i | ((EnumFacing)p_176201_1_.get(FACING)).getHorizontalIndex();
        i = i | ((Integer)p_176201_1_.get(field_176505_b)).intValue() << 2;
        return i;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
     * fine.
     */
    public IBlockState rotate(IBlockState state, Rotation rot)
    {
        return state.getBlock() != this ? state : state.func_177226_a(FACING, rot.rotate((EnumFacing)state.get(FACING)));
    }

    protected BlockStateContainer func_180661_e()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, field_176505_b});
    }

    public static class Anvil implements IInteractionObject
        {
            private final World world;
            private final BlockPos position;

            public Anvil(World worldIn, BlockPos pos)
            {
                this.world = worldIn;
                this.position = pos;
            }

            public String func_70005_c_()
            {
                return "anvil";
            }

            public boolean hasCustomName()
            {
                return false;
            }

            public ITextComponent getDisplayName()
            {
                return new TextComponentTranslation(Blocks.ANVIL.getTranslationKey() + ".name", new Object[0]);
            }

            public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
            {
                return new ContainerRepair(playerInventory, this.world, this.position, playerIn);
            }

            public String getGuiID()
            {
                return "minecraft:anvil";
            }
        }
}