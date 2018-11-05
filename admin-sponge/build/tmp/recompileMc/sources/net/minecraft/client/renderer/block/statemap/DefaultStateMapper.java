package net.minecraft.client.renderer.block.statemap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DefaultStateMapper extends StateMapperBase
{
    protected ModelResourceLocation func_178132_a(IBlockState p_178132_1_)
    {
        return new ModelResourceLocation(Block.REGISTRY.getKey(p_178132_1_.getBlock()), this.func_178131_a(p_178132_1_.func_177228_b()));
    }
}