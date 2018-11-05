package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockLeaves extends Block implements net.minecraftforge.common.IShearable
{
    public static final PropertyBool field_176237_a = PropertyBool.create("decayable");
    public static final PropertyBool field_176236_b = PropertyBool.create("check_decay");
    protected boolean field_185686_c;
    int[] field_150128_a;

    public BlockLeaves()
    {
        super(Material.LEAVES);
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.DECORATIONS);
        this.func_149711_c(0.2F);
        this.func_149713_g(1);
        this.func_149672_a(SoundType.PLANT);
    }

    public void func_180663_b(World p_180663_1_, BlockPos p_180663_2_, IBlockState p_180663_3_)
    {
        int i = 1;
        int j = 2;
        int k = p_180663_2_.getX();
        int l = p_180663_2_.getY();
        int i1 = p_180663_2_.getZ();

        if (p_180663_1_.isAreaLoaded(new BlockPos(k - 2, l - 2, i1 - 2), new BlockPos(k + 2, l + 2, i1 + 2)))
        {
            for (int j1 = -1; j1 <= 1; ++j1)
            {
                for (int k1 = -1; k1 <= 1; ++k1)
                {
                    for (int l1 = -1; l1 <= 1; ++l1)
                    {
                        BlockPos blockpos = p_180663_2_.add(j1, k1, l1);
                        IBlockState iblockstate = p_180663_1_.getBlockState(blockpos);

                        if (iblockstate.getBlock().isLeaves(iblockstate, p_180663_1_, blockpos))
                        {
                            iblockstate.getBlock().beginLeavesDecay(iblockstate, p_180663_1_, blockpos);
                        }
                    }
                }
            }
        }
    }

    public void func_180650_b(World p_180650_1_, BlockPos p_180650_2_, IBlockState p_180650_3_, Random p_180650_4_)
    {
        if (!p_180650_1_.isRemote)
        {
            if (((Boolean)p_180650_3_.get(field_176236_b)).booleanValue() && ((Boolean)p_180650_3_.get(field_176237_a)).booleanValue())
            {
                int i = 4;
                int j = 5;
                int k = p_180650_2_.getX();
                int l = p_180650_2_.getY();
                int i1 = p_180650_2_.getZ();
                int j1 = 32;
                int k1 = 1024;
                int l1 = 16;

                if (this.field_150128_a == null)
                {
                    this.field_150128_a = new int[32768];
                }

                if (!p_180650_1_.func_175697_a(p_180650_2_, 1)) return; // Forge: prevent decaying leaves from updating neighbors and loading unloaded chunks
                if (p_180650_1_.func_175697_a(p_180650_2_, 6)) // Forge: extend range from 5 to 6 to account for neighbor checks in world.markAndNotifyBlock -> world.updateObservingBlocksAt
                {
                    BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                    for (int i2 = -4; i2 <= 4; ++i2)
                    {
                        for (int j2 = -4; j2 <= 4; ++j2)
                        {
                            for (int k2 = -4; k2 <= 4; ++k2)
                            {
                                IBlockState iblockstate = p_180650_1_.getBlockState(blockpos$mutableblockpos.setPos(k + i2, l + j2, i1 + k2));
                                Block block = iblockstate.getBlock();

                                if (!block.canSustainLeaves(iblockstate, p_180650_1_, blockpos$mutableblockpos.setPos(k + i2, l + j2, i1 + k2)))
                                {
                                    if (block.isLeaves(iblockstate, p_180650_1_, blockpos$mutableblockpos.setPos(k + i2, l + j2, i1 + k2)))
                                    {
                                        this.field_150128_a[(i2 + 16) * 1024 + (j2 + 16) * 32 + k2 + 16] = -2;
                                    }
                                    else
                                    {
                                        this.field_150128_a[(i2 + 16) * 1024 + (j2 + 16) * 32 + k2 + 16] = -1;
                                    }
                                }
                                else
                                {
                                    this.field_150128_a[(i2 + 16) * 1024 + (j2 + 16) * 32 + k2 + 16] = 0;
                                }
                            }
                        }
                    }

                    for (int i3 = 1; i3 <= 4; ++i3)
                    {
                        for (int j3 = -4; j3 <= 4; ++j3)
                        {
                            for (int k3 = -4; k3 <= 4; ++k3)
                            {
                                for (int l3 = -4; l3 <= 4; ++l3)
                                {
                                    if (this.field_150128_a[(j3 + 16) * 1024 + (k3 + 16) * 32 + l3 + 16] == i3 - 1)
                                    {
                                        if (this.field_150128_a[(j3 + 16 - 1) * 1024 + (k3 + 16) * 32 + l3 + 16] == -2)
                                        {
                                            this.field_150128_a[(j3 + 16 - 1) * 1024 + (k3 + 16) * 32 + l3 + 16] = i3;
                                        }

                                        if (this.field_150128_a[(j3 + 16 + 1) * 1024 + (k3 + 16) * 32 + l3 + 16] == -2)
                                        {
                                            this.field_150128_a[(j3 + 16 + 1) * 1024 + (k3 + 16) * 32 + l3 + 16] = i3;
                                        }

                                        if (this.field_150128_a[(j3 + 16) * 1024 + (k3 + 16 - 1) * 32 + l3 + 16] == -2)
                                        {
                                            this.field_150128_a[(j3 + 16) * 1024 + (k3 + 16 - 1) * 32 + l3 + 16] = i3;
                                        }

                                        if (this.field_150128_a[(j3 + 16) * 1024 + (k3 + 16 + 1) * 32 + l3 + 16] == -2)
                                        {
                                            this.field_150128_a[(j3 + 16) * 1024 + (k3 + 16 + 1) * 32 + l3 + 16] = i3;
                                        }

                                        if (this.field_150128_a[(j3 + 16) * 1024 + (k3 + 16) * 32 + (l3 + 16 - 1)] == -2)
                                        {
                                            this.field_150128_a[(j3 + 16) * 1024 + (k3 + 16) * 32 + (l3 + 16 - 1)] = i3;
                                        }

                                        if (this.field_150128_a[(j3 + 16) * 1024 + (k3 + 16) * 32 + l3 + 16 + 1] == -2)
                                        {
                                            this.field_150128_a[(j3 + 16) * 1024 + (k3 + 16) * 32 + l3 + 16 + 1] = i3;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                int l2 = this.field_150128_a[16912];

                if (l2 >= 0)
                {
                    p_180650_1_.setBlockState(p_180650_2_, p_180650_3_.func_177226_a(field_176236_b, Boolean.valueOf(false)), 4);
                }
                else
                {
                    this.func_176235_d(p_180650_1_, p_180650_2_);
                }
            }
        }
    }

    private void func_176235_d(World p_176235_1_, BlockPos p_176235_2_)
    {
        this.func_176226_b(p_176235_1_, p_176235_2_, p_176235_1_.getBlockState(p_176235_2_), 0);
        p_176235_1_.removeBlock(p_176235_2_);
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    @SideOnly(Side.CLIENT)
    public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (worldIn.isRainingAt(pos.up()) && !worldIn.getBlockState(pos.down()).isTopSolid() && rand.nextInt(15) == 1)
        {
            double d0 = (double)((float)pos.getX() + rand.nextFloat());
            double d1 = (double)pos.getY() - 0.05D;
            double d2 = (double)((float)pos.getZ() + rand.nextFloat());
            worldIn.func_175688_a(EnumParticleTypes.DRIP_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    public int func_149745_a(Random p_149745_1_)
    {
        return p_149745_1_.nextInt(20) == 0 ? 1 : 0;
    }

    public Item func_180660_a(IBlockState p_180660_1_, Random p_180660_2_, int p_180660_3_)
    {
        return Item.getItemFromBlock(Blocks.field_150345_g);
    }

    public void func_180653_a(World p_180653_1_, BlockPos p_180653_2_, IBlockState p_180653_3_, float p_180653_4_, int p_180653_5_)
    {
        super.func_180653_a(p_180653_1_, p_180653_2_, p_180653_3_, p_180653_4_, p_180653_5_);
    }

    protected void func_176234_a(World p_176234_1_, BlockPos p_176234_2_, IBlockState p_176234_3_, int p_176234_4_)
    {
    }

    protected int func_176232_d(IBlockState p_176232_1_)
    {
        return 20;
    }

    public boolean func_149662_c(IBlockState p_149662_1_)
    {
        return !this.field_185686_c;
    }

    @SideOnly(Side.CLIENT)
    public void func_150122_b(boolean p_150122_1_)
    {
        this.field_185686_c = p_150122_1_;
    }

    /**
     * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
     * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
     */
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return this.field_185686_c ? BlockRenderLayer.CUTOUT_MIPPED : BlockRenderLayer.SOLID;
    }

    /**
     * @deprecated call via {@link IBlockState#causesSuffocation()} whenever possible. Implementing/overriding is fine.
     */
    public boolean causesSuffocation(IBlockState state)
    {
        return false;
    }

    public abstract BlockPlanks.EnumType func_176233_b(int p_176233_1_);

    @Override public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos){ return true; }
    @Override public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos){ return true; }

    @Override
    public void beginLeavesDecay(IBlockState state, World world, BlockPos pos)
    {
        if (!(Boolean)state.get(field_176236_b))
        {
            world.setBlockState(pos, state.func_177226_a(field_176236_b, true), 4);
        }
    }

    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        Random rand = world instanceof World ? ((World)world).rand : new Random();
        int chance = this.func_176232_d(state);

        if (fortune > 0)
        {
            chance -= 2 << fortune;
            if (chance < 10) chance = 10;
        }

        if (rand.nextInt(chance) == 0)
        {
            ItemStack drop = new ItemStack(func_180660_a(state, rand, fortune), 1, func_180651_a(state));
            if (!drop.isEmpty())
                drops.add(drop);
        }

        chance = 200;
        if (fortune > 0)
        {
            chance -= 10 << fortune;
            if (chance < 40) chance = 40;
        }

        this.captureDrops(true);
        if (world instanceof World)
            this.func_176234_a((World)world, pos, state, chance); // Dammet mojang
        drops.addAll(this.captureDrops(false));
    }


    /**
     * @deprecated call via {@link IBlockState#shouldSideBeRendered(IBlockAccess,BlockPos,EnumFacing)} whenever
     * possible. Implementing/overriding is fine.
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing p_176225_4_)
    {
        return !this.field_185686_c && blockAccess.getBlockState(pos.offset(p_176225_4_)).getBlock() == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, p_176225_4_);
    }
}