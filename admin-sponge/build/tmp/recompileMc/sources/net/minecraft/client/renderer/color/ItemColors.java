package net.minecraft.client.renderer.color;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFireworkCharge;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemColors
{
    // FORGE: Use RegistryDelegates as non-Vanilla item ids are not constant
    private final java.util.Map<net.minecraftforge.registries.IRegistryDelegate<Item>, IItemColor> itemColorMap = com.google.common.collect.Maps.newHashMap();

    public static ItemColors init(final BlockColors colors)
    {
        ItemColors itemcolors = new ItemColors();
        itemcolors.func_186730_a(new IItemColor()
        {
            public int func_186726_a(ItemStack p_186726_1_, int p_186726_2_)
            {
                return p_186726_2_ > 0 ? -1 : ((ItemArmor)p_186726_1_.getItem()).func_82814_b(p_186726_1_);
            }
        }, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS);
        itemcolors.func_186731_a(new IItemColor()
        {
            public int func_186726_a(ItemStack p_186726_1_, int p_186726_2_)
            {
                BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = BlockDoublePlant.EnumPlantType.func_176938_a(p_186726_1_.func_77960_j());
                return blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.GRASS && blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.FERN ? -1 : ColorizerGrass.get(0.5D, 1.0D);
            }
        }, Blocks.field_150398_cm);
        itemcolors.func_186730_a(new IItemColor()
        {
            public int func_186726_a(ItemStack p_186726_1_, int p_186726_2_)
            {
                if (p_186726_2_ != 1)
                {
                    return -1;
                }
                else
                {
                    NBTBase nbtbase = ItemFireworkCharge.func_150903_a(p_186726_1_, "Colors");

                    if (!(nbtbase instanceof NBTTagIntArray))
                    {
                        return 9079434;
                    }
                    else
                    {
                        int[] aint = ((NBTTagIntArray)nbtbase).getIntArray();

                        if (aint.length == 1)
                        {
                            return aint[0];
                        }
                        else
                        {
                            int i = 0;
                            int j = 0;
                            int k = 0;

                            for (int l : aint)
                            {
                                i += (l & 16711680) >> 16;
                                j += (l & 65280) >> 8;
                                k += (l & 255) >> 0;
                            }

                            i = i / aint.length;
                            j = j / aint.length;
                            k = k / aint.length;
                            return i << 16 | j << 8 | k;
                        }
                    }
                }
            }
        }, Items.field_151154_bQ);
        itemcolors.func_186730_a(new IItemColor()
        {
            public int func_186726_a(ItemStack p_186726_1_, int p_186726_2_)
            {
                return p_186726_2_ > 0 ? -1 : PotionUtils.getColor(p_186726_1_);
            }
        }, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
        itemcolors.func_186730_a(new IItemColor()
        {
            public int func_186726_a(ItemStack p_186726_1_, int p_186726_2_)
            {
                EntityList.EntityEggInfo entitylist$entityegginfo = EntityList.field_75627_a.get(ItemMonsterPlacer.func_190908_h(p_186726_1_));

                if (entitylist$entityegginfo == null)
                {
                    return -1;
                }
                else
                {
                    return p_186726_2_ == 0 ? entitylist$entityegginfo.field_75611_b : entitylist$entityegginfo.field_75612_c;
                }
            }
        }, Items.field_151063_bx);
        itemcolors.func_186731_a(new IItemColor()
        {
            public int func_186726_a(ItemStack p_186726_1_, int p_186726_2_)
            {
                IBlockState iblockstate = ((ItemBlock)p_186726_1_.getItem()).getBlock().func_176203_a(p_186726_1_.func_77960_j());
                return colors.getColor(iblockstate, (IBlockAccess)null, (BlockPos)null, p_186726_2_);
            }
        }, Blocks.GRASS, Blocks.field_150329_H, Blocks.VINE, Blocks.field_150362_t, Blocks.field_150361_u, Blocks.field_150392_bi);
        itemcolors.func_186730_a(new IItemColor()
        {
            public int func_186726_a(ItemStack p_186726_1_, int p_186726_2_)
            {
                return p_186726_2_ == 0 ? PotionUtils.getColor(p_186726_1_) : -1;
            }
        }, Items.TIPPED_ARROW);
        itemcolors.func_186730_a(new IItemColor()
        {
            public int func_186726_a(ItemStack p_186726_1_, int p_186726_2_)
            {
                return p_186726_2_ == 0 ? -1 : ItemMap.getColor(p_186726_1_);
            }
        }, Items.FILLED_MAP);
        net.minecraftforge.client.ForgeHooksClient.onItemColorsInit(itemcolors, colors);
        return itemcolors;
    }

    public int getColor(ItemStack stack, int tintIndex)
    {
        IItemColor iitemcolor = this.itemColorMap.get(stack.getItem().delegate);
        return iitemcolor == null ? -1 : iitemcolor.func_186726_a(stack, tintIndex);
    }

    public void func_186731_a(IItemColor p_186731_1_, Block... p_186731_2_)
    {
        for (Block block : p_186731_2_)
        {
            if (block == null) throw new IllegalArgumentException("Block registered to item color handler cannot be null!");
            if (block.getRegistryName() == null) throw new IllegalArgumentException("Block must be registered before assigning color handler.");
            this.itemColorMap.put(Item.getItemFromBlock(block).delegate, p_186731_1_);
        }
    }

    public void func_186730_a(IItemColor p_186730_1_, Item... p_186730_2_)
    {
        for (Item item : p_186730_2_)
        {
            if (item == null) throw new IllegalArgumentException("Item registered to item color handler cannot be null!");
            if (item.getRegistryName() == null) throw new IllegalArgumentException("Item must be registered before assigning color handler.");
            this.itemColorMap.put(item.delegate, p_186730_1_);
        }
    }
}