package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ICondition
{
    ICondition field_188119_a = new ICondition()
    {
        public Predicate<IBlockState> func_188118_a(BlockStateContainer p_188118_1_)
        {
            return new Predicate<IBlockState>()
            {
                public boolean apply(@Nullable IBlockState p_apply_1_)
                {
                    return true;
                }
            };
        }
    };
    ICondition field_188120_b = new ICondition()
    {
        public Predicate<IBlockState> func_188118_a(BlockStateContainer p_188118_1_)
        {
            return new Predicate<IBlockState>()
            {
                public boolean apply(@Nullable IBlockState p_apply_1_)
                {
                    return false;
                }
            };
        }
    };

    Predicate<IBlockState> func_188118_a(BlockStateContainer p_188118_1_);
}