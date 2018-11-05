package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFire extends Block
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool UP = PropertyBool.create("up");
    private final Map<Block, Integer> encouragements = Maps.<Block, Integer>newIdentityHashMap();
    private final Map<Block, Integer> flammabilities = Maps.<Block, Integer>newIdentityHashMap();

    public IBlockState func_176221_a(IBlockState p_176221_1_, IBlockAccess p_176221_2_, BlockPos p_176221_3_)
    {
        if (!p_176221_2_.getBlockState(p_176221_3_.down()).isSideSolid(p_176221_2_, p_176221_3_.down(), EnumFacing.UP) && !Blocks.FIRE.canCatchFire(p_176221_2_, p_176221_3_.down(), EnumFacing.UP))
        {
            return p_176221_1_.func_177226_a(NORTH, this.canCatchFire(p_176221_2_, p_176221_3_.north(), EnumFacing.SOUTH))
                        .func_177226_a(EAST,  this.canCatchFire(p_176221_2_, p_176221_3_.east(), EnumFacing.WEST))
                        .func_177226_a(SOUTH, this.canCatchFire(p_176221_2_, p_176221_3_.south(), EnumFacing.NORTH))
                        .func_177226_a(WEST,  this.canCatchFire(p_176221_2_, p_176221_3_.west(), EnumFacing.EAST))
                        .func_177226_a(UP, this.canCatchFire(p_176221_2_, p_176221_3_.up(), EnumFacing.DOWN));
        }
        return this.getDefaultState();
    }

    protected BlockFire()
    {
        super(Material.FIRE);
        this.setDefaultState(this.stateContainer.getBaseState().func_177226_a(AGE, Integer.valueOf(0)).func_177226_a(NORTH, Boolean.valueOf(false)).func_177226_a(EAST, Boolean.valueOf(false)).func_177226_a(SOUTH, Boolean.valueOf(false)).func_177226_a(WEST, Boolean.valueOf(false)).func_177226_a(UP, Boolean.valueOf(false)));
        this.func_149675_a(true);
    }

    public static void init()
    {
        Blocks.FIRE.setFireInfo(Blocks.field_150344_f, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.field_150373_bw, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.field_150376_bx, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.OAK_FENCE_GATE, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.SPRUCE_FENCE_GATE, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.BIRCH_FENCE_GATE, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.JUNGLE_FENCE_GATE, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.ACACIA_FENCE_GATE, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.OAK_FENCE, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.SPRUCE_FENCE, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.BIRCH_FENCE, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.JUNGLE_FENCE, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.DARK_OAK_FENCE, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.ACACIA_FENCE, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.OAK_STAIRS, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.BIRCH_STAIRS, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.SPRUCE_STAIRS, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.JUNGLE_STAIRS, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.ACACIA_STAIRS, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.DARK_OAK_STAIRS, 5, 20);
        Blocks.FIRE.setFireInfo(Blocks.field_150364_r, 5, 5);
        Blocks.FIRE.setFireInfo(Blocks.field_150363_s, 5, 5);
        Blocks.FIRE.setFireInfo(Blocks.field_150362_t, 30, 60);
        Blocks.FIRE.setFireInfo(Blocks.field_150361_u, 30, 60);
        Blocks.FIRE.setFireInfo(Blocks.BOOKSHELF, 30, 20);
        Blocks.FIRE.setFireInfo(Blocks.TNT, 15, 100);
        Blocks.FIRE.setFireInfo(Blocks.field_150329_H, 60, 100);
        Blocks.FIRE.setFireInfo(Blocks.field_150398_cm, 60, 100);
        Blocks.FIRE.setFireInfo(Blocks.field_150327_N, 60, 100);
        Blocks.FIRE.setFireInfo(Blocks.field_150328_O, 60, 100);
        Blocks.FIRE.setFireInfo(Blocks.field_150330_I, 60, 100);
        Blocks.FIRE.setFireInfo(Blocks.field_150325_L, 30, 60);
        Blocks.FIRE.setFireInfo(Blocks.VINE, 15, 100);
        Blocks.FIRE.setFireInfo(Blocks.COAL_BLOCK, 5, 5);
        Blocks.FIRE.setFireInfo(Blocks.HAY_BLOCK, 60, 20);
        Blocks.FIRE.setFireInfo(Blocks.field_150404_cg, 60, 20);
    }

    public void setFireInfo(Block blockIn, int encouragement, int flammability)
    {
        if (blockIn == Blocks.AIR) throw new IllegalArgumentException("Tried to set air on fire... This is bad.");
        this.encouragements.put(blockIn, Integer.valueOf(encouragement));
        this.flammabilities.put(blockIn, Integer.valueOf(flammability));
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
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 30;
    }

    public void func_180650_b(World p_180650_1_, BlockPos p_180650_2_, IBlockState p_180650_3_, Random p_180650_4_)
    {
        if (p_180650_1_.getGameRules().getBoolean("doFireTick"))
        {
            if (!p_180650_1_.func_175697_a(p_180650_2_, 2)) return; // Forge: prevent loading unloaded chunks when spreading fire
            if (!this.func_176196_c(p_180650_1_, p_180650_2_))
            {
                p_180650_1_.removeBlock(p_180650_2_);
            }

            Block block = p_180650_1_.getBlockState(p_180650_2_.down()).getBlock();
            boolean flag = block.isFireSource(p_180650_1_, p_180650_2_.down(), EnumFacing.UP);

            int i = ((Integer)p_180650_3_.get(AGE)).intValue();

            if (!flag && p_180650_1_.isRaining() && this.canDie(p_180650_1_, p_180650_2_) && p_180650_4_.nextFloat() < 0.2F + (float)i * 0.03F)
            {
                p_180650_1_.removeBlock(p_180650_2_);
            }
            else
            {
                if (i < 15)
                {
                    p_180650_3_ = p_180650_3_.func_177226_a(AGE, Integer.valueOf(i + p_180650_4_.nextInt(3) / 2));
                    p_180650_1_.setBlockState(p_180650_2_, p_180650_3_, 4);
                }

                p_180650_1_.func_175684_a(p_180650_2_, this, this.tickRate(p_180650_1_) + p_180650_4_.nextInt(10));

                if (!flag)
                {
                    if (!this.func_176533_e(p_180650_1_, p_180650_2_))
                    {
                        if (!p_180650_1_.getBlockState(p_180650_2_.down()).isSideSolid(p_180650_1_, p_180650_2_.down(), EnumFacing.UP) || i > 3)
                        {
                            p_180650_1_.removeBlock(p_180650_2_);
                        }

                        return;
                    }

                    if (!this.canCatchFire(p_180650_1_, p_180650_2_.down(), EnumFacing.UP) && i == 15 && p_180650_4_.nextInt(4) == 0)
                    {
                        p_180650_1_.removeBlock(p_180650_2_);
                        return;
                    }
                }

                boolean flag1 = p_180650_1_.isBlockinHighHumidity(p_180650_2_);
                int j = 0;

                if (flag1)
                {
                    j = -50;
                }

                this.tryCatchFire(p_180650_1_, p_180650_2_.east(), 300 + j, p_180650_4_, i, EnumFacing.WEST);
                this.tryCatchFire(p_180650_1_, p_180650_2_.west(), 300 + j, p_180650_4_, i, EnumFacing.EAST);
                this.tryCatchFire(p_180650_1_, p_180650_2_.down(), 250 + j, p_180650_4_, i, EnumFacing.UP);
                this.tryCatchFire(p_180650_1_, p_180650_2_.up(), 250 + j, p_180650_4_, i, EnumFacing.DOWN);
                this.tryCatchFire(p_180650_1_, p_180650_2_.north(), 300 + j, p_180650_4_, i, EnumFacing.SOUTH);
                this.tryCatchFire(p_180650_1_, p_180650_2_.south(), 300 + j, p_180650_4_, i, EnumFacing.NORTH);

                for (int k = -1; k <= 1; ++k)
                {
                    for (int l = -1; l <= 1; ++l)
                    {
                        for (int i1 = -1; i1 <= 4; ++i1)
                        {
                            if (k != 0 || i1 != 0 || l != 0)
                            {
                                int j1 = 100;

                                if (i1 > 1)
                                {
                                    j1 += (i1 - 1) * 100;
                                }

                                BlockPos blockpos = p_180650_2_.add(k, i1, l);
                                int k1 = this.getNeighborEncouragement(p_180650_1_, blockpos);

                                if (k1 > 0)
                                {
                                    int l1 = (k1 + 40 + p_180650_1_.getDifficulty().getId() * 7) / (i + 30);

                                    if (flag1)
                                    {
                                        l1 /= 2;
                                    }

                                    if (l1 > 0 && p_180650_4_.nextInt(j1) <= l1 && (!p_180650_1_.isRaining() || !this.canDie(p_180650_1_, blockpos)))
                                    {
                                        int i2 = i + p_180650_4_.nextInt(5) / 4;

                                        if (i2 > 15)
                                        {
                                            i2 = 15;
                                        }

                                        p_180650_1_.setBlockState(blockpos, p_180650_3_.func_177226_a(AGE, Integer.valueOf(i2)), 3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean canDie(World worldIn, BlockPos pos)
    {
        return worldIn.isRainingAt(pos) || worldIn.isRainingAt(pos.west()) || worldIn.isRainingAt(pos.east()) || worldIn.isRainingAt(pos.north()) || worldIn.isRainingAt(pos.south());
    }

    public boolean func_149698_L()
    {
        return false;
    }

    @Deprecated // Use Block.getFlammability
    public int getFlammability(Block blockIn)
    {
        Integer integer = this.flammabilities.get(blockIn);
        return integer == null ? 0 : integer.intValue();
    }

    @Deprecated // Use Block.getFireSpreadSpeed
    public int getEncouragement(Block blockIn)
    {
        Integer integer = this.encouragements.get(blockIn);
        return integer == null ? 0 : integer.intValue();
    }

    @Deprecated // Use tryCatchFire with face below
    private void catchOnFire(World worldIn, BlockPos pos, int chance, Random random, int age)
    {
        this.tryCatchFire(worldIn, pos, chance, random, age, EnumFacing.UP);
    }

    private void tryCatchFire(World worldIn, BlockPos pos, int chance, Random random, int age, EnumFacing face)
    {
        int i = worldIn.getBlockState(pos).getBlock().getFlammability(worldIn, pos, face);

        if (random.nextInt(chance) < i)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos);

            if (random.nextInt(age + 10) < 5 && !worldIn.isRainingAt(pos))
            {
                int j = age + random.nextInt(5) / 4;

                if (j > 15)
                {
                    j = 15;
                }

                worldIn.setBlockState(pos, this.getDefaultState().func_177226_a(AGE, Integer.valueOf(j)), 3);
            }
            else
            {
                worldIn.removeBlock(pos);
            }

            if (iblockstate.getBlock() == Blocks.TNT)
            {
                Blocks.TNT.onPlayerDestroy(worldIn, pos, iblockstate.func_177226_a(BlockTNT.field_176246_a, Boolean.valueOf(true)));
            }
        }
    }

    private boolean func_176533_e(World p_176533_1_, BlockPos p_176533_2_)
    {
        for (EnumFacing enumfacing : EnumFacing.values())
        {
            if (this.canCatchFire(p_176533_1_, p_176533_2_.offset(enumfacing), enumfacing.getOpposite()))
            {
                return true;
            }
        }

        return false;
    }

    private int getNeighborEncouragement(World worldIn, BlockPos pos)
    {
        if (!worldIn.isAirBlock(pos))
        {
            return 0;
        }
        else
        {
            int i = 0;

            for (EnumFacing enumfacing : EnumFacing.values())
            {
                i = Math.max(worldIn.getBlockState(pos.offset(enumfacing)).getBlock().getFireSpreadSpeed(worldIn, pos.offset(enumfacing), enumfacing.getOpposite()), i);
            }

            return i;
        }
    }

    /**
     * Returns if this block is collidable. Only used by fire, although stairs return that of the block that the stair
     * is made of (though nobody's going to make fire stairs, right?)
     */
    public boolean isCollidable()
    {
        return false;
    }

    @Deprecated // Use canCatchFire with face sensitive version below
    public boolean func_176535_e(IBlockAccess p_176535_1_, BlockPos p_176535_2_)
    {
        return canCatchFire(p_176535_1_, p_176535_2_, EnumFacing.UP);
    }

    public boolean func_176196_c(World p_176196_1_, BlockPos p_176196_2_)
    {
        return p_176196_1_.getBlockState(p_176196_2_.down()).isTopSolid() || this.func_176533_e(p_176196_1_, p_176196_2_);
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.getBlockState(pos.down()).isTopSolid() && !this.func_176533_e(worldIn, pos))
        {
            worldIn.removeBlock(pos);
        }
    }

    public void func_176213_c(World p_176213_1_, BlockPos p_176213_2_, IBlockState p_176213_3_)
    {
        if (p_176213_1_.dimension.getType().getId() > 0 || !Blocks.NETHER_PORTAL.trySpawnPortal(p_176213_1_, p_176213_2_))
        {
            if (!p_176213_1_.getBlockState(p_176213_2_.down()).isTopSolid() && !this.func_176533_e(p_176213_1_, p_176213_2_))
            {
                p_176213_1_.removeBlock(p_176213_2_);
            }
            else
            {
                p_176213_1_.func_175684_a(p_176213_2_, this, this.tickRate(p_176213_1_) + p_176213_1_.rand.nextInt(10));
            }
        }
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     * @deprecated call via {@link IBlockState#getMapColor(IBlockAccess,BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return MapColor.TNT;
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    @SideOnly(Side.CLIENT)
    public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (rand.nextInt(24) == 0)
        {
            worldIn.playSound((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }

        if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP) && !Blocks.FIRE.canCatchFire(worldIn, pos.down(), EnumFacing.UP))
        {
            if (Blocks.FIRE.canCatchFire(worldIn, pos.west(), EnumFacing.EAST))
            {
                for (int j = 0; j < 2; ++j)
                {
                    double d3 = (double)pos.getX() + rand.nextDouble() * 0.10000000149011612D;
                    double d8 = (double)pos.getY() + rand.nextDouble();
                    double d13 = (double)pos.getZ() + rand.nextDouble();
                    worldIn.func_175688_a(EnumParticleTypes.SMOKE_LARGE, d3, d8, d13, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Blocks.FIRE.canCatchFire(worldIn, pos.east(), EnumFacing.WEST))
            {
                for (int k = 0; k < 2; ++k)
                {
                    double d4 = (double)(pos.getX() + 1) - rand.nextDouble() * 0.10000000149011612D;
                    double d9 = (double)pos.getY() + rand.nextDouble();
                    double d14 = (double)pos.getZ() + rand.nextDouble();
                    worldIn.func_175688_a(EnumParticleTypes.SMOKE_LARGE, d4, d9, d14, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Blocks.FIRE.canCatchFire(worldIn, pos.north(), EnumFacing.SOUTH))
            {
                for (int l = 0; l < 2; ++l)
                {
                    double d5 = (double)pos.getX() + rand.nextDouble();
                    double d10 = (double)pos.getY() + rand.nextDouble();
                    double d15 = (double)pos.getZ() + rand.nextDouble() * 0.10000000149011612D;
                    worldIn.func_175688_a(EnumParticleTypes.SMOKE_LARGE, d5, d10, d15, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Blocks.FIRE.canCatchFire(worldIn, pos.south(), EnumFacing.NORTH))
            {
                for (int i1 = 0; i1 < 2; ++i1)
                {
                    double d6 = (double)pos.getX() + rand.nextDouble();
                    double d11 = (double)pos.getY() + rand.nextDouble();
                    double d16 = (double)(pos.getZ() + 1) - rand.nextDouble() * 0.10000000149011612D;
                    worldIn.func_175688_a(EnumParticleTypes.SMOKE_LARGE, d6, d11, d16, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Blocks.FIRE.canCatchFire(worldIn, pos.up(), EnumFacing.DOWN))
            {
                for (int j1 = 0; j1 < 2; ++j1)
                {
                    double d7 = (double)pos.getX() + rand.nextDouble();
                    double d12 = (double)(pos.getY() + 1) - rand.nextDouble() * 0.10000000149011612D;
                    double d17 = (double)pos.getZ() + rand.nextDouble();
                    worldIn.func_175688_a(EnumParticleTypes.SMOKE_LARGE, d7, d12, d17, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        else
        {
            for (int i = 0; i < 3; ++i)
            {
                double d0 = (double)pos.getX() + rand.nextDouble();
                double d1 = (double)pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
                double d2 = (double)pos.getZ() + rand.nextDouble();
                worldIn.func_175688_a(EnumParticleTypes.SMOKE_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }
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

    protected BlockStateContainer func_180661_e()
    {
        return new BlockStateContainer(this, new IProperty[] {AGE, NORTH, EAST, SOUTH, WEST, UP});
    }

    /*================================= Forge Start ======================================*/
    /**
     * Side sensitive version that calls the block function.
     *
     * @param world The current world
     * @param pos Block position
     * @param face The side the fire is coming from
     * @return True if the face can catch fire.
     */
    public boolean canCatchFire(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return world.getBlockState(pos).getBlock().isFlammable(world, pos, face);
    }
    /*================================= Forge Start ======================================*/

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