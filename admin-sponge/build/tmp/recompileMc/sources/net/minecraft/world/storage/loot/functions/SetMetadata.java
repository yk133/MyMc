package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetMetadata extends LootFunction
{
    private static final Logger field_186572_a = LogManager.getLogger();
    private final RandomValueRange field_186573_b;

    public SetMetadata(LootCondition[] p_i46621_1_, RandomValueRange p_i46621_2_)
    {
        super(p_i46621_1_);
        this.field_186573_b = p_i46621_2_;
    }

    public ItemStack apply(ItemStack stack, Random rand, LootContext context)
    {
        if (stack.isDamageable())
        {
            field_186572_a.warn("Couldn't set data of loot item {}", (Object)stack);
        }
        else
        {
            stack.func_77964_b(this.field_186573_b.generateInt(rand));
        }

        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<SetMetadata>
        {
            protected Serializer()
            {
                super(new ResourceLocation("set_data"), SetMetadata.class);
            }

            public void serialize(JsonObject object, SetMetadata functionClazz, JsonSerializationContext serializationContext)
            {
                object.add("data", serializationContext.serialize(functionClazz.field_186573_b));
            }

            public SetMetadata deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn)
            {
                return new SetMetadata(conditionsIn, (RandomValueRange)JsonUtils.deserializeClass(object, "data", deserializationContext, RandomValueRange.class));
            }
        }
}