package net.minecraft.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandBlockData extends CommandBase
{
    public String func_71517_b()
    {
        return "blockdata";
    }

    public int func_82362_a()
    {
        return 2;
    }

    public String func_71518_a(ICommandSender p_71518_1_)
    {
        return "commands.blockdata.usage";
    }

    public void func_184881_a(MinecraftServer p_184881_1_, ICommandSender p_184881_2_, String[] p_184881_3_) throws CommandException
    {
        if (p_184881_3_.length < 4)
        {
            throw new WrongUsageException("commands.blockdata.usage", new Object[0]);
        }
        else
        {
            p_184881_2_.func_174794_a(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
            BlockPos blockpos = func_175757_a(p_184881_2_, p_184881_3_, 0, false);
            World world = p_184881_2_.getEntityWorld();

            if (!world.isBlockLoaded(blockpos))
            {
                throw new CommandException("commands.blockdata.outOfWorld", new Object[0]);
            }
            else
            {
                IBlockState iblockstate = world.getBlockState(blockpos);
                TileEntity tileentity = world.getTileEntity(blockpos);

                if (tileentity == null)
                {
                    throw new CommandException("commands.blockdata.notValid", new Object[0]);
                }
                else
                {
                    NBTTagCompound nbttagcompound = tileentity.write(new NBTTagCompound());
                    NBTTagCompound nbttagcompound1 = nbttagcompound.copy();
                    NBTTagCompound nbttagcompound2;

                    try
                    {
                        nbttagcompound2 = JsonToNBT.getTagFromJson(func_180529_a(p_184881_3_, 3));
                    }
                    catch (NBTException nbtexception)
                    {
                        throw new CommandException("commands.blockdata.tagError", new Object[] {nbtexception.getMessage()});
                    }

                    nbttagcompound.func_179237_a(nbttagcompound2);
                    nbttagcompound.putInt("x", blockpos.getX());
                    nbttagcompound.putInt("y", blockpos.getY());
                    nbttagcompound.putInt("z", blockpos.getZ());

                    if (nbttagcompound.equals(nbttagcompound1))
                    {
                        throw new CommandException("commands.blockdata.failed", new Object[] {nbttagcompound.toString()});
                    }
                    else
                    {
                        tileentity.read(nbttagcompound);
                        tileentity.markDirty();
                        world.notifyBlockUpdate(blockpos, iblockstate, iblockstate, 3);
                        p_184881_2_.func_174794_a(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
                        func_152373_a(p_184881_2_, this, "commands.blockdata.success", new Object[] {nbttagcompound.toString()});
                    }
                }
            }
        }
    }

    public List<String> func_184883_a(MinecraftServer p_184883_1_, ICommandSender p_184883_2_, String[] p_184883_3_, @Nullable BlockPos p_184883_4_)
    {
        return p_184883_3_.length > 0 && p_184883_3_.length <= 3 ? func_175771_a(p_184883_3_, 0, p_184883_4_) : Collections.emptyList();
    }
}