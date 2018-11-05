package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConditionAnd implements ICondition
{
    private final Iterable<ICondition> conditions;

    public ConditionAnd(Iterable<ICondition> conditionsIn)
    {
        this.conditions = conditionsIn;
    }

    public Predicate<IBlockState> func_188118_a(final BlockStateContainer p_188118_1_)
    {
        return Predicates.and(Iterables.transform(this.conditions, new Function<ICondition, Predicate<IBlockState>>()
        {
            @Nullable
            public Predicate<IBlockState> apply(@Nullable ICondition p_apply_1_)
            {
                return p_apply_1_ == null ? null : p_apply_1_.func_188118_a(p_188118_1_);
            }
        }));
    }
}