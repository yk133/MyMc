package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStainedGlass extends BlockBreakable
{
    public static final PropertyEnum<EnumDyeColor> field_176547_a = PropertyEnum.<EnumDyeColor>create("color", EnumDyeColor.class);

    public BlockStainedGlass(Material p_i45427_1_)
    {
        super(p_i45427_1_, false);
        this.setDefaultState(this.stateContainer.getBaseState().func_177226_a(field_176547_a, EnumDyeColor.WHITE));
        this.func_149647_a(CreativeTabs.BUILDING_BLOCKS);
    }

    public int func_180651_a(IBlockState p_180651_1_)
    {
        return ((EnumDyeColor)p_180651_1_.get(field_176547_a)).func_176765_a();
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void fillItemGroup(CreativeTabs group, NonNullList<ItemStack> items)
    {
        for (EnumDyeColor enumdyecolor : EnumDyeColor.values())
        {
            items.add(new ItemStack(this, 1, enumdyecolor.func_176765_a()));
        }
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     * @deprecated call via {@link IBlockState#getMapColor(IBlockAccess,BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return MapColor.func_193558_a((EnumDyeColor)state.get(field_176547_a));
    }

    /**
     * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
     * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
     */
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    public int func_149745_a(Random p_149745_1_)
    {
        return 0;
    }

    protected boolean canSilkHarvest()
    {
        return true;
    }

    /**
     * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
     */
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public IBlockState func_176203_a(int p_176203_1_)
    {
        return this.getDefaultState().func_177226_a(field_176547_a, EnumDyeColor.func_176764_b(p_176203_1_));
    }

    public void func_176213_c(World p_176213_1_, BlockPos p_176213_2_, IBlockState p_176213_3_)
    {
        if (!p_176213_1_.isRemote)
        {
            BlockBeacon.updateColorAsync(p_176213_1_, p_176213_2_);
        }
    }

    public void func_180663_b(World p_180663_1_, BlockPos p_180663_2_, IBlockState p_180663_3_)
    {
        if (!p_180663_1_.isRemote)
        {
            BlockBeacon.updateColorAsync(p_180663_1_, p_180663_2_);
        }
    }

    public int func_176201_c(IBlockState p_176201_1_)
    {
        return ((EnumDyeColor)p_176201_1_.get(field_176547_a)).func_176765_a();
    }

    protected BlockStateContainer func_180661_e()
    {
        return new BlockStateContainer(this, new IProperty[] {field_176547_a});
    }
}