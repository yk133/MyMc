package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBed extends BlockHorizontal implements ITileEntityProvider
{
    public static final PropertyEnum<BlockBed.EnumPartType> PART = PropertyEnum.<BlockBed.EnumPartType>create("part", BlockBed.EnumPartType.class);
    public static final PropertyBool OCCUPIED = PropertyBool.create("occupied");
    protected static final AxisAlignedBB field_185513_c = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5625D, 1.0D);

    public BlockBed()
    {
        super(Material.CLOTH);
        this.setDefaultState(this.stateContainer.getBaseState().func_177226_a(PART, BlockBed.EnumPartType.FOOT).func_177226_a(OCCUPIED, Boolean.valueOf(false)));
        this.field_149758_A = true;
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     * @deprecated call via {@link IBlockState#getMapColor(IBlockAccess,BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if (state.get(PART) == BlockBed.EnumPartType.FOOT)
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityBed)
            {
                EnumDyeColor enumdyecolor = ((TileEntityBed)tileentity).getColor();
                return MapColor.func_193558_a(enumdyecolor);
            }
        }

        return MapColor.WOOL;
    }

    public boolean func_180639_a(World p_180639_1_, BlockPos p_180639_2_, IBlockState p_180639_3_, EntityPlayer p_180639_4_, EnumHand p_180639_5_, EnumFacing p_180639_6_, float p_180639_7_, float p_180639_8_, float p_180639_9_)
    {
        if (p_180639_1_.isRemote)
        {
            return true;
        }
        else
        {
            if (p_180639_3_.get(PART) != BlockBed.EnumPartType.HEAD)
            {
                p_180639_2_ = p_180639_2_.offset((EnumFacing)p_180639_3_.get(HORIZONTAL_FACING));
                p_180639_3_ = p_180639_1_.getBlockState(p_180639_2_);

                if (p_180639_3_.getBlock() != this)
                {
                    return true;
                }
            }

            net.minecraft.world.WorldProvider.WorldSleepResult sleepResult = p_180639_1_.dimension.canSleepAt(p_180639_4_, p_180639_2_);
            if (sleepResult != net.minecraft.world.WorldProvider.WorldSleepResult.BED_EXPLODES)
            {
                if (sleepResult == net.minecraft.world.WorldProvider.WorldSleepResult.DENY) return true;
                if (((Boolean)p_180639_3_.get(OCCUPIED)).booleanValue())
                {
                    EntityPlayer entityplayer = this.getPlayerInBed(p_180639_1_, p_180639_2_);

                    if (entityplayer != null)
                    {
                        p_180639_4_.sendStatusMessage(new TextComponentTranslation("tile.bed.occupied", new Object[0]), true);
                        return true;
                    }

                    p_180639_3_ = p_180639_3_.func_177226_a(OCCUPIED, Boolean.valueOf(false));
                    p_180639_1_.setBlockState(p_180639_2_, p_180639_3_, 4);
                }

                EntityPlayer.SleepResult entityplayer$sleepresult = p_180639_4_.trySleep(p_180639_2_);

                if (entityplayer$sleepresult == EntityPlayer.SleepResult.OK)
                {
                    p_180639_3_ = p_180639_3_.func_177226_a(OCCUPIED, Boolean.valueOf(true));
                    p_180639_1_.setBlockState(p_180639_2_, p_180639_3_, 4);
                    return true;
                }
                else
                {
                    if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW)
                    {
                        p_180639_4_.sendStatusMessage(new TextComponentTranslation("tile.bed.noSleep", new Object[0]), true);
                    }
                    else if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_SAFE)
                    {
                        p_180639_4_.sendStatusMessage(new TextComponentTranslation("tile.bed.notSafe", new Object[0]), true);
                    }
                    else if (entityplayer$sleepresult == EntityPlayer.SleepResult.TOO_FAR_AWAY)
                    {
                        p_180639_4_.sendStatusMessage(new TextComponentTranslation("tile.bed.tooFarAway", new Object[0]), true);
                    }

                    return true;
                }
            }
            else
            {
                p_180639_1_.removeBlock(p_180639_2_);
                BlockPos blockpos = p_180639_2_.offset(((EnumFacing)p_180639_3_.get(HORIZONTAL_FACING)).getOpposite());

                if (p_180639_1_.getBlockState(blockpos).getBlock() == this)
                {
                    p_180639_1_.removeBlock(blockpos);
                }

                p_180639_1_.newExplosion((Entity)null, (double)p_180639_2_.getX() + 0.5D, (double)p_180639_2_.getY() + 0.5D, (double)p_180639_2_.getZ() + 0.5D, 5.0F, true, true);
                return true;
            }
        }
    }

    @Nullable
    private EntityPlayer getPlayerInBed(World worldIn, BlockPos pos)
    {
        for (EntityPlayer entityplayer : worldIn.playerEntities)
        {
            if (entityplayer.isPlayerSleeping() && entityplayer.bedLocation.equals(pos))
            {
                return entityplayer;
            }
        }

        return null;
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

    /**
     * Block's chance to react to a living entity falling on it.
     */
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance * 0.5F);
    }

    /**
     * Called when an Entity lands on this Block. This method *must* update motionY because the entity will not do that
     * on its own
     */
    public void onLanded(World worldIn, Entity entityIn)
    {
        if (entityIn.isSneaking())
        {
            super.onLanded(worldIn, entityIn);
        }
        else if (entityIn.motionY < 0.0D)
        {
            entityIn.motionY = -entityIn.motionY * 0.6600000262260437D;

            if (!(entityIn instanceof EntityLivingBase))
            {
                entityIn.motionY *= 0.8D;
            }
        }
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        EnumFacing enumfacing = (EnumFacing)state.get(HORIZONTAL_FACING);

        if (state.get(PART) == BlockBed.EnumPartType.FOOT)
        {
            if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() != this)
            {
                worldIn.removeBlock(pos);
            }
        }
        else if (worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock() != this)
        {
            if (!worldIn.isRemote)
            {
                this.func_176226_b(worldIn, pos, state, 0);
            }

            worldIn.removeBlock(pos);
        }
    }

    public Item func_180660_a(IBlockState p_180660_1_, Random p_180660_2_, int p_180660_3_)
    {
        return p_180660_1_.get(PART) == BlockBed.EnumPartType.FOOT ? Items.AIR : Items.field_151104_aV;
    }

    public AxisAlignedBB func_185496_a(IBlockState p_185496_1_, IBlockAccess p_185496_2_, BlockPos p_185496_3_)
    {
        return field_185513_c;
    }

    /**
     * @deprecated call via {@link IBlockState#hasCustomBreakingProgress()} whenever possible. Implementing/overriding
     * is fine.
     */
    @SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress(IBlockState state)
    {
        return true;
    }

    /**
     * Returns a safe BlockPos to disembark the bed
     */
    @Nullable
    public static BlockPos getSafeExitLocation(World worldIn, BlockPos pos, int tries)
    {
        EnumFacing enumfacing = (EnumFacing)worldIn.getBlockState(pos).get(HORIZONTAL_FACING);
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        for (int l = 0; l <= 1; ++l)
        {
            int i1 = i - enumfacing.getXOffset() * l - 1;
            int j1 = k - enumfacing.getZOffset() * l - 1;
            int k1 = i1 + 2;
            int l1 = j1 + 2;

            for (int i2 = i1; i2 <= k1; ++i2)
            {
                for (int j2 = j1; j2 <= l1; ++j2)
                {
                    BlockPos blockpos = new BlockPos(i2, j, j2);

                    if (hasRoomForPlayer(worldIn, blockpos))
                    {
                        if (tries <= 0)
                        {
                            return blockpos;
                        }

                        --tries;
                    }
                }
            }
        }

        return null;
    }

    protected static boolean hasRoomForPlayer(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).isTopSolid() && !worldIn.getBlockState(pos).getMaterial().isSolid() && !worldIn.getBlockState(pos.up()).getMaterial().isSolid();
    }

    public void func_180653_a(World p_180653_1_, BlockPos p_180653_2_, IBlockState p_180653_3_, float p_180653_4_, int p_180653_5_)
    {
        if (p_180653_3_.get(PART) == BlockBed.EnumPartType.HEAD)
        {
            TileEntity tileentity = p_180653_1_.getTileEntity(p_180653_2_);
            EnumDyeColor enumdyecolor = tileentity instanceof TileEntityBed ? ((TileEntityBed)tileentity).getColor() : EnumDyeColor.RED;
            spawnAsEntity(p_180653_1_, p_180653_2_, new ItemStack(Items.field_151104_aV, 1, enumdyecolor.func_176765_a()));
        }
    }

    /**
     * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
     */
    public EnumPushReaction getPushReaction(IBlockState state)
    {
        return EnumPushReaction.DESTROY;
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
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
     */
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        BlockPos blockpos = pos;

        if (state.get(PART) == BlockBed.EnumPartType.FOOT)
        {
            blockpos = pos.offset((EnumFacing)state.get(HORIZONTAL_FACING));
        }

        TileEntity tileentity = worldIn.getTileEntity(blockpos);
        EnumDyeColor enumdyecolor = tileentity instanceof TileEntityBed ? ((TileEntityBed)tileentity).getColor() : EnumDyeColor.RED;
        return new ItemStack(Items.field_151104_aV, 1, enumdyecolor.func_176765_a());
    }

    /**
     * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually
     * collect this block
     */
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (player.abilities.isCreativeMode && state.get(PART) == BlockBed.EnumPartType.FOOT)
        {
            BlockPos blockpos = pos.offset((EnumFacing)state.get(HORIZONTAL_FACING));

            if (worldIn.getBlockState(blockpos).getBlock() == this)
            {
                worldIn.removeBlock(blockpos);
            }
        }
    }

    /**
     * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
     * Block.removedByPlayer
     */
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack)
    {
        if (state.get(PART) == BlockBed.EnumPartType.HEAD && te instanceof TileEntityBed)
        {
            TileEntityBed tileentitybed = (TileEntityBed)te;
            ItemStack itemstack = tileentitybed.func_193049_f();
            spawnAsEntity(worldIn, pos, itemstack);
        }
        else
        {
            super.harvestBlock(worldIn, player, pos, state, (TileEntity)null, stack);
        }
    }

    public void func_180663_b(World p_180663_1_, BlockPos p_180663_2_, IBlockState p_180663_3_)
    {
        super.func_180663_b(p_180663_1_, p_180663_2_, p_180663_3_);
        p_180663_1_.removeTileEntity(p_180663_2_);
    }

    public IBlockState func_176203_a(int p_176203_1_)
    {
        EnumFacing enumfacing = EnumFacing.byHorizontalIndex(p_176203_1_);
        return (p_176203_1_ & 8) > 0 ? this.getDefaultState().func_177226_a(PART, BlockBed.EnumPartType.HEAD).func_177226_a(HORIZONTAL_FACING, enumfacing).func_177226_a(OCCUPIED, Boolean.valueOf((p_176203_1_ & 4) > 0)) : this.getDefaultState().func_177226_a(PART, BlockBed.EnumPartType.FOOT).func_177226_a(HORIZONTAL_FACING, enumfacing);
    }

    public IBlockState func_176221_a(IBlockState p_176221_1_, IBlockAccess p_176221_2_, BlockPos p_176221_3_)
    {
        if (p_176221_1_.get(PART) == BlockBed.EnumPartType.FOOT)
        {
            IBlockState iblockstate = p_176221_2_.getBlockState(p_176221_3_.offset((EnumFacing)p_176221_1_.get(HORIZONTAL_FACING)));

            if (iblockstate.getBlock() == this)
            {
                p_176221_1_ = p_176221_1_.func_177226_a(OCCUPIED, iblockstate.get(OCCUPIED));
            }
        }

        return p_176221_1_;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
     * fine.
     */
    public IBlockState rotate(IBlockState state, Rotation rot)
    {
        return state.func_177226_a(HORIZONTAL_FACING, rot.rotate((EnumFacing)state.get(HORIZONTAL_FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
     */
    public IBlockState mirror(IBlockState state, Mirror mirrorIn)
    {
        return state.rotate(mirrorIn.toRotation((EnumFacing)state.get(HORIZONTAL_FACING)));
    }

    public int func_176201_c(IBlockState p_176201_1_)
    {
        int i = 0;
        i = i | ((EnumFacing)p_176201_1_.get(HORIZONTAL_FACING)).getHorizontalIndex();

        if (p_176201_1_.get(PART) == BlockBed.EnumPartType.HEAD)
        {
            i |= 8;

            if (((Boolean)p_176201_1_.get(OCCUPIED)).booleanValue())
            {
                i |= 4;
            }
        }

        return i;
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

    protected BlockStateContainer func_180661_e()
    {
        return new BlockStateContainer(this, new IProperty[] {HORIZONTAL_FACING, PART, OCCUPIED});
    }

    public TileEntity func_149915_a(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityBed();
    }

    @SideOnly(Side.CLIENT)
    public static boolean func_193385_b(int p_193385_0_)
    {
        return (p_193385_0_ & 8) != 0;
    }

    public static enum EnumPartType implements IStringSerializable
    {
        HEAD("head"),
        FOOT("foot");

        private final String name;

        private EnumPartType(String p_i45735_3_)
        {
            this.name = p_i45735_3_;
        }

        public String toString()
        {
            return this.name;
        }

        public String getName()
        {
            return this.name;
        }
    }
}