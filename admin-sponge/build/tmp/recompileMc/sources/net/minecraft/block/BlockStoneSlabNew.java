package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockStoneSlabNew extends BlockSlab
{
    public static final PropertyBool field_176558_b = PropertyBool.create("seamless");
    public static final PropertyEnum<BlockStoneSlabNew.EnumType> field_176559_M = PropertyEnum.<BlockStoneSlabNew.EnumType>create("variant", BlockStoneSlabNew.EnumType.class);

    public BlockStoneSlabNew()
    {
        super(Material.ROCK);
        IBlockState iblockstate = this.stateContainer.getBaseState();

        if (this.func_176552_j())
        {
            iblockstate = iblockstate.func_177226_a(field_176558_b, Boolean.valueOf(false));
        }
        else
        {
            iblockstate = iblockstate.func_177226_a(field_176554_a, BlockSlab.EnumBlockHalf.BOTTOM);
        }

        this.setDefaultState(iblockstate.func_177226_a(field_176559_M, BlockStoneSlabNew.EnumType.RED_SANDSTONE));
        this.func_149647_a(CreativeTabs.BUILDING_BLOCKS);
    }

    public String func_149732_F()
    {
        return I18n.func_74838_a(this.getTranslationKey() + ".red_sandstone.name");
    }

    public Item func_180660_a(IBlockState p_180660_1_, Random p_180660_2_, int p_180660_3_)
    {
        return Item.getItemFromBlock(Blocks.field_180389_cP);
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(Blocks.field_180389_cP, 1, ((BlockStoneSlabNew.EnumType)state.get(field_176559_M)).func_176915_a());
    }

    public String func_150002_b(int p_150002_1_)
    {
        return super.getTranslationKey() + "." + BlockStoneSlabNew.EnumType.func_176916_a(p_150002_1_).func_176918_c();
    }

    public IProperty<?> func_176551_l()
    {
        return field_176559_M;
    }

    public Comparable<?> func_185674_a(ItemStack p_185674_1_)
    {
        return BlockStoneSlabNew.EnumType.func_176916_a(p_185674_1_.func_77960_j() & 7);
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void fillItemGroup(CreativeTabs group, NonNullList<ItemStack> items)
    {
        for (BlockStoneSlabNew.EnumType blockstoneslabnew$enumtype : BlockStoneSlabNew.EnumType.values())
        {
            items.add(new ItemStack(this, 1, blockstoneslabnew$enumtype.func_176915_a()));
        }
    }

    public IBlockState func_176203_a(int p_176203_1_)
    {
        IBlockState iblockstate = this.getDefaultState().func_177226_a(field_176559_M, BlockStoneSlabNew.EnumType.func_176916_a(p_176203_1_ & 7));

        if (this.func_176552_j())
        {
            iblockstate = iblockstate.func_177226_a(field_176558_b, Boolean.valueOf((p_176203_1_ & 8) != 0));
        }
        else
        {
            iblockstate = iblockstate.func_177226_a(field_176554_a, (p_176203_1_ & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
        }

        return iblockstate;
    }

    public int func_176201_c(IBlockState p_176201_1_)
    {
        int i = 0;
        i = i | ((BlockStoneSlabNew.EnumType)p_176201_1_.get(field_176559_M)).func_176915_a();

        if (this.func_176552_j())
        {
            if (((Boolean)p_176201_1_.get(field_176558_b)).booleanValue())
            {
                i |= 8;
            }
        }
        else if (p_176201_1_.get(field_176554_a) == BlockSlab.EnumBlockHalf.TOP)
        {
            i |= 8;
        }

        return i;
    }

    protected BlockStateContainer func_180661_e()
    {
        return this.func_176552_j() ? new BlockStateContainer(this, new IProperty[] {field_176558_b, field_176559_M}) : new BlockStateContainer(this, new IProperty[] {field_176554_a, field_176559_M});
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     * @deprecated call via {@link IBlockState#getMapColor(IBlockAccess,BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return ((BlockStoneSlabNew.EnumType)state.get(field_176559_M)).func_181068_c();
    }

    public int func_180651_a(IBlockState p_180651_1_)
    {
        return ((BlockStoneSlabNew.EnumType)p_180651_1_.get(field_176559_M)).func_176915_a();
    }

    public static enum EnumType implements IStringSerializable
    {
        RED_SANDSTONE(0, "red_sandstone", BlockSand.EnumType.RED_SAND.func_176687_c());

        private static final BlockStoneSlabNew.EnumType[] field_176921_b = new BlockStoneSlabNew.EnumType[values().length];
        private final int field_176922_c;
        private final String field_176919_d;
        private final MapColor field_181069_e;

        private EnumType(int p_i46391_3_, String p_i46391_4_, MapColor p_i46391_5_)
        {
            this.field_176922_c = p_i46391_3_;
            this.field_176919_d = p_i46391_4_;
            this.field_181069_e = p_i46391_5_;
        }

        public int func_176915_a()
        {
            return this.field_176922_c;
        }

        public MapColor func_181068_c()
        {
            return this.field_181069_e;
        }

        public String toString()
        {
            return this.field_176919_d;
        }

        public static BlockStoneSlabNew.EnumType func_176916_a(int p_176916_0_)
        {
            if (p_176916_0_ < 0 || p_176916_0_ >= field_176921_b.length)
            {
                p_176916_0_ = 0;
            }

            return field_176921_b[p_176916_0_];
        }

        public String getName()
        {
            return this.field_176919_d;
        }

        public String func_176918_c()
        {
            return this.field_176919_d;
        }

        static
        {
            for (BlockStoneSlabNew.EnumType blockstoneslabnew$enumtype : values())
            {
                field_176921_b[blockstoneslabnew$enumtype.func_176915_a()] = blockstoneslabnew$enumtype;
            }
        }
    }
}