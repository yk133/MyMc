package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class EnchantmentPredicate
{
    /** The predicate that matches any set of enchantments. */
    public static final EnchantmentPredicate ANY = new EnchantmentPredicate();
    private final Enchantment enchantment;
    private final MinMaxBounds levels;

    public EnchantmentPredicate()
    {
        this.enchantment = null;
        this.levels = MinMaxBounds.field_192516_a;
    }

    public EnchantmentPredicate(@Nullable Enchantment p_i47436_1_, MinMaxBounds p_i47436_2_)
    {
        this.enchantment = p_i47436_1_;
        this.levels = p_i47436_2_;
    }

    public boolean test(Map<Enchantment, Integer> enchantmentsIn)
    {
        if (this.enchantment != null)
        {
            if (!enchantmentsIn.containsKey(this.enchantment))
            {
                return false;
            }

            int i = ((Integer)enchantmentsIn.get(this.enchantment)).intValue();

            if (this.levels != null && !this.levels.func_192514_a((float)i))
            {
                return false;
            }
        }
        else if (this.levels != null)
        {
            for (Integer integer : enchantmentsIn.values())
            {
                if (this.levels.func_192514_a((float)integer.intValue()))
                {
                    return true;
                }
            }

            return false;
        }

        return true;
    }

    public static EnchantmentPredicate deserialize(@Nullable JsonElement element)
    {
        if (element != null && !element.isJsonNull())
        {
            JsonObject jsonobject = JsonUtils.getJsonObject(element, "enchantment");
            Enchantment enchantment = null;

            if (jsonobject.has("enchantment"))
            {
                ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(jsonobject, "enchantment"));
                enchantment = Enchantment.REGISTRY.get(resourcelocation);

                if (enchantment == null)
                {
                    throw new JsonSyntaxException("Unknown enchantment '" + resourcelocation + "'");
                }
            }

            MinMaxBounds minmaxbounds = MinMaxBounds.func_192515_a(jsonobject.get("levels"));
            return new EnchantmentPredicate(enchantment, minmaxbounds);
        }
        else
        {
            return ANY;
        }
    }

    public static EnchantmentPredicate[] deserializeArray(@Nullable JsonElement element)
    {
        if (element != null && !element.isJsonNull())
        {
            JsonArray jsonarray = JsonUtils.getJsonArray(element, "enchantments");
            EnchantmentPredicate[] aenchantmentpredicate = new EnchantmentPredicate[jsonarray.size()];

            for (int i = 0; i < aenchantmentpredicate.length; ++i)
            {
                aenchantmentpredicate[i] = deserialize(jsonarray.get(i));
            }

            return aenchantmentpredicate;
        }
        else
        {
            return new EnchantmentPredicate[0];
        }
    }
}