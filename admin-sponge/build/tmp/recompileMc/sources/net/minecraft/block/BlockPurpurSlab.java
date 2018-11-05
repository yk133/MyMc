package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockPurpurSlab extends BlockSlab
{
    public static final PropertyEnum<BlockPurpurSlab.Variant> field_185678_d = PropertyEnum.<BlockPurpurSlab.Variant>create("variant", BlockPurpurSlab.Variant.class);

    public BlockPurpurSlab()
    {
        super(Material.ROCK, MapColor.MAGENTA);
        IBlockState iblockstate = this.stateContainer.getBaseState();

        if (!this.func_176552_j())
        {
            iblockstate = iblockstate.func_177226_a(field_176554_a, BlockSlab.EnumBlockHalf.BOTTOM);
        }

        this.setDefaultState(iblockstate.func_177226_a(field_185678_d, BlockPurpurSlab.Variant.DEFAULT));
        this.func_149647_a(CreativeTabs.BUILDING_BLOCKS);
    }

    public Item func_180660_a(IBlockState p_180660_1_, Random p_180660_2_, int p_180660_3_)
    {
        return Item.getItemFromBlock(Blocks.PURPUR_SLAB);
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(Blocks.PURPUR_SLAB);
    }

    public IBlockState func_176203_a(int p_176203_1_)
    {
        IBlockState iblockstate = this.getDefaultState().func_177226_a(field_185678_d, BlockPurpurSlab.Variant.DEFAULT);

        if (!this.func_176552_j())
        {
            iblockstate = iblockstate.func_177226_a(field_176554_a, (p_176203_1_ & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
        }

        return iblockstate;
    }

    public int func_176201_c(IBlockState p_176201_1_)
    {
        int i = 0;

        if (!this.func_176552_j() && p_176201_1_.get(field_176554_a) == BlockSlab.EnumBlockHalf.TOP)
        {
            i |= 8;
        }

        return i;
    }

    protected BlockStateContainer func_180661_e()
    {
        return this.func_176552_j() ? new BlockStateContainer(this, new IProperty[] {field_185678_d}) : new BlockStateContainer(this, new IProperty[] {field_176554_a, field_185678_d});
    }

    public String func_150002_b(int p_150002_1_)
    {
        return super.getTranslationKey();
    }

    public IProperty<?> func_176551_l()
    {
        return field_185678_d;
    }

    public Comparable<?> func_185674_a(ItemStack p_185674_1_)
    {
        return BlockPurpurSlab.Variant.DEFAULT;
    }

    public static class Double extends BlockPurpurSlab
        {
            public boolean func_176552_j()
            {
                return true;
            }
        }

    public static class Half extends BlockPurpurSlab
        {
            public boolean func_176552_j()
            {
                return false;
            }
        }

    public static enum Variant implements IStringSerializable
    {
        DEFAULT;

        public String getName()
        {
            return "default";
        }
    }
}