package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ItemFishFood extends ItemFood
{
    /** Indicates whether this fish is "cooked" or not. */
    private final boolean cooked;

    public ItemFishFood(boolean p_i45338_1_)
    {
        super(0, 0.0F, false);
        this.cooked = p_i45338_1_;
    }

    public int getHealAmount(ItemStack stack)
    {
        ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(stack);
        return this.cooked && itemfishfood$fishtype.canCook() ? itemfishfood$fishtype.getCookedHealAmount() : itemfishfood$fishtype.getUncookedHealAmount();
    }

    public float getSaturationModifier(ItemStack stack)
    {
        ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(stack);
        return this.cooked && itemfishfood$fishtype.canCook() ? itemfishfood$fishtype.getCookedSaturationModifier() : itemfishfood$fishtype.getUncookedSaturationModifier();
    }

    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player)
    {
        ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(stack);

        if (itemfishfood$fishtype == ItemFishFood.FishType.PUFFERFISH)
        {
            player.func_70690_d(new PotionEffect(MobEffects.POISON, 1200, 3));
            player.func_70690_d(new PotionEffect(MobEffects.HUNGER, 300, 2));
            player.func_70690_d(new PotionEffect(MobEffects.NAUSEA, 300, 1));
        }

        super.onFoodEaten(stack, worldIn, player);
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void fillItemGroup(CreativeTabs group, NonNullList<ItemStack> items)
    {
        if (this.isInGroup(group))
        {
            for (ItemFishFood.FishType itemfishfood$fishtype : ItemFishFood.FishType.values())
            {
                if (!this.cooked || itemfishfood$fishtype.canCook())
                {
                    items.add(new ItemStack(this, 1, itemfishfood$fishtype.func_150976_a()));
                }
            }
        }
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getTranslationKey(ItemStack stack)
    {
        ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(stack);
        return this.getTranslationKey() + "." + itemfishfood$fishtype.func_150972_b() + "." + (this.cooked && itemfishfood$fishtype.canCook() ? "cooked" : "raw");
    }

    public static enum FishType
    {
        COD(0, "cod", 2, 0.1F, 5, 0.6F),
        SALMON(1, "salmon", 2, 0.1F, 6, 0.8F),
        CLOWNFISH(2, "clownfish", 1, 0.1F),
        PUFFERFISH(3, "pufferfish", 1, 0.1F);

        private static final Map<Integer, ItemFishFood.FishType> field_150983_e = Maps.<Integer, ItemFishFood.FishType>newHashMap();
        private final int field_150980_f;
        private final String field_150981_g;
        /** The amount that eating the uncooked version of this fish should heal the player. */
        private final int uncookedHealAmount;
        /** The saturation modifier to apply to the heal amount when the player eats the uncooked version of this fish. */
        private final float uncookedSaturationModifier;
        /** The amount that eating the cooked version of this fish should heal the player. */
        private final int cookedHealAmount;
        /** The saturation modifier to apply to the heal amount when the player eats the cooked version of this fish. */
        private final float cookedSaturationModifier;
        /** Indicates whether this type of fish has "raw" and "cooked" variants */
        private final boolean cookable;

        private FishType(int p_i45336_3_, String p_i45336_4_, int p_i45336_5_, float p_i45336_6_, int p_i45336_7_, float p_i45336_8_)
        {
            this.field_150980_f = p_i45336_3_;
            this.field_150981_g = p_i45336_4_;
            this.uncookedHealAmount = p_i45336_5_;
            this.uncookedSaturationModifier = p_i45336_6_;
            this.cookedHealAmount = p_i45336_7_;
            this.cookedSaturationModifier = p_i45336_8_;
            this.cookable = true;
        }

        private FishType(int p_i45337_3_, String p_i45337_4_, int p_i45337_5_, float p_i45337_6_)
        {
            this.field_150980_f = p_i45337_3_;
            this.field_150981_g = p_i45337_4_;
            this.uncookedHealAmount = p_i45337_5_;
            this.uncookedSaturationModifier = p_i45337_6_;
            this.cookedHealAmount = 0;
            this.cookedSaturationModifier = 0.0F;
            this.cookable = false;
        }

        public int func_150976_a()
        {
            return this.field_150980_f;
        }

        public String func_150972_b()
        {
            return this.field_150981_g;
        }

        /**
         * Gets the amount that eating the uncooked version of this fish should heal the player.
         */
        public int getUncookedHealAmount()
        {
            return this.uncookedHealAmount;
        }

        /**
         * Gets the saturation modifier to apply to the heal amount when the player eats the uncooked version of this
         * fish.
         */
        public float getUncookedSaturationModifier()
        {
            return this.uncookedSaturationModifier;
        }

        /**
         * Gets the amount that eating the cooked version of this fish should heal the player.
         */
        public int getCookedHealAmount()
        {
            return this.cookedHealAmount;
        }

        /**
         * Gets the saturation modifier to apply to the heal amount when the player eats the cooked version of this
         * fish.
         */
        public float getCookedSaturationModifier()
        {
            return this.cookedSaturationModifier;
        }

        /**
         * Gets a value indicating whether this type of fish has "raw" and "cooked" variants.
         */
        public boolean canCook()
        {
            return this.cookable;
        }

        public static ItemFishFood.FishType func_150974_a(int p_150974_0_)
        {
            ItemFishFood.FishType itemfishfood$fishtype = field_150983_e.get(Integer.valueOf(p_150974_0_));
            return itemfishfood$fishtype == null ? COD : itemfishfood$fishtype;
        }

        /**
         * Gets the FishType that corresponds to the given ItemStack, defaulting to COD if the given ItemStack does not
         * actually contain a fish.
         */
        public static ItemFishFood.FishType byItemStack(ItemStack stack)
        {
            return stack.getItem() instanceof ItemFishFood ? func_150974_a(stack.func_77960_j()) : COD;
        }

        static
        {
            for (ItemFishFood.FishType itemfishfood$fishtype : values())
            {
                field_150983_e.put(Integer.valueOf(itemfishfood$fishtype.func_150976_a()), itemfishfood$fishtype);
            }
        }
    }
}