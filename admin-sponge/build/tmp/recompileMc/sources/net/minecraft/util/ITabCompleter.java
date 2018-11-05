package net.minecraft.util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ITabCompleter
{
    void func_184072_a(String... p_184072_1_);
}