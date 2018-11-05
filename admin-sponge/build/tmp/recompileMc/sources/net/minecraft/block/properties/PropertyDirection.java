package net.minecraft.block.properties;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.util.EnumFacing;

public class PropertyDirection extends PropertyEnum<EnumFacing>
{
    protected PropertyDirection(String name, Collection<EnumFacing> values)
    {
        super(name, EnumFacing.class, values);
    }

    public static PropertyDirection func_177714_a(String p_177714_0_)
    {
        return create(p_177714_0_, Predicates.alwaysTrue());
    }

    /**
     * Create a new PropertyDirection with all directions that match the given Predicate
     */
    public static PropertyDirection create(String name, Predicate<EnumFacing> filter)
    {
        return create(name, Collections2.filter(Lists.newArrayList(EnumFacing.values()), filter));
    }

    /**
     * Create a new PropertyDirection for the given direction values
     */
    public static PropertyDirection create(String name, Collection<EnumFacing> values)
    {
        return new PropertyDirection(name, values);
    }
}