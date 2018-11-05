package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IItemPropertyGetter
{
    @SideOnly(Side.CLIENT)
    float func_185085_a(ItemStack p_185085_1_, @Nullable World p_185085_2_, @Nullable EntityLivingBase p_185085_3_);
}