package net.minecraft.client.resources;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ResourcePackListEntryDefault extends ResourcePackListEntryServer
{
    public ResourcePackListEntryDefault(GuiScreenResourcePacks p_i45052_1_)
    {
        super(p_i45052_1_, Minecraft.getInstance().func_110438_M().field_110620_b);
    }

    protected String getResourcePackName()
    {
        return "Default";
    }

    public boolean func_186768_j()
    {
        return false;
    }
}