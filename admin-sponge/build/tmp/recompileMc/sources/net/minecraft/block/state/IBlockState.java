package net.minecraft.block.state;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;

public interface IBlockState extends IBlockBehaviors, IBlockProperties
{
    Collection < IProperty<? >> func_177227_a();

    /**
     * Get the value of the given Property for this BlockState
     */
    <T extends Comparable<T>> T get(IProperty<T> property);

    <T extends Comparable<T>, V extends T> IBlockState func_177226_a(IProperty<T> p_177226_1_, V p_177226_2_);

    /**
     * Create a version of this BlockState with the given property cycled to the next value in order. If the property
     * was at the highest possible value, it is set to the lowest one instead.
     */
    <T extends Comparable<T>> IBlockState cycle(IProperty<T> property);

    ImmutableMap < IProperty<?>, Comparable<? >> func_177228_b();

    Block getBlock();
}