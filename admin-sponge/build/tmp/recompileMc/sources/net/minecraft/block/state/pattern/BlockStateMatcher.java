package net.minecraft.block.state.pattern;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

public class BlockStateMatcher implements Predicate<IBlockState>
{
    public static final Predicate<IBlockState> ANY = new Predicate<IBlockState>()
    {
        public boolean apply(@Nullable IBlockState p_apply_1_)
        {
            return true;
        }
    };
    private final BlockStateContainer blockstate;
    private final Map < IProperty<?>, Predicate<? >> propertyPredicates = Maps. < IProperty<?>, Predicate<? >> newHashMap();

    private BlockStateMatcher(BlockStateContainer blockStateIn)
    {
        this.blockstate = blockStateIn;
    }

    public static BlockStateMatcher forBlock(Block blockIn)
    {
        return new BlockStateMatcher(blockIn.getStateContainer());
    }

    public boolean apply(@Nullable IBlockState p_apply_1_)
    {
        if (p_apply_1_ != null && p_apply_1_.getBlock().equals(this.blockstate.getOwner()))
        {
            if (this.propertyPredicates.isEmpty())
            {
                return true;
            }
            else
            {
                for (Entry < IProperty<?>, Predicate<? >> entry : this.propertyPredicates.entrySet())
                {
                    if (!this.matches(p_apply_1_, (IProperty)entry.getKey(), (Predicate)entry.getValue()))
                    {
                        return false;
                    }
                }

                return true;
            }
        }
        else
        {
            return false;
        }
    }

    protected <T extends Comparable<T>> boolean matches(IBlockState blockState, IProperty<T> property, Predicate<T> predicate)
    {
        return predicate.apply(blockState.get(property));
    }

    public <V extends Comparable<V>> BlockStateMatcher func_177637_a(IProperty<V> p_177637_1_, Predicate <? extends V > p_177637_2_)
    {
        if (!this.blockstate.getProperties().contains(p_177637_1_))
        {
            throw new IllegalArgumentException(this.blockstate + " cannot support property " + p_177637_1_);
        }
        else
        {
            this.propertyPredicates.put(p_177637_1_, p_177637_2_);
            return this;
        }
    }
}