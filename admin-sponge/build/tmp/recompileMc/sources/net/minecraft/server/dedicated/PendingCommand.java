package net.minecraft.server.dedicated;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class PendingCommand
{
    /** The command string. */
    public final String command;
    public final ICommandSender sender;

    public PendingCommand(String p_i1491_1_, ICommandSender p_i1491_2_)
    {
        this.command = p_i1491_1_;
        this.sender = p_i1491_2_;
    }
}