package net.minecraft.client.resources;

import java.util.List;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IReloadableResourceManager extends IResourceManager
{
    void func_110541_a(List<IResourcePack> p_110541_1_);

    void func_110542_a(IResourceManagerReloadListener p_110542_1_);
}