package net.minecraft.client.renderer.color;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IItemColor
{
    int func_186726_a(ItemStack p_186726_1_, int p_186726_2_);
}