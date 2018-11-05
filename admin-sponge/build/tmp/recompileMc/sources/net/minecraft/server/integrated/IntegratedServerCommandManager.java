package net.minecraft.server.integrated;

import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IntegratedServerCommandManager extends ServerCommandManager
{
    public IntegratedServerCommandManager(IntegratedServer p_i46522_1_)
    {
        super(p_i46522_1_);
    }
}