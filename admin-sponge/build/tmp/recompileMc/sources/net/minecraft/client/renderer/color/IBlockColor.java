package net.minecraft.client.renderer.color;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IBlockColor
{
    int func_186720_a(IBlockState p_186720_1_, @Nullable IBlockAccess p_186720_2_, @Nullable BlockPos p_186720_3_, int p_186720_4_);
}