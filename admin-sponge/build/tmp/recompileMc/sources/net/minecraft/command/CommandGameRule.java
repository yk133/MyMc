package net.minecraft.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameRules;

public class CommandGameRule extends CommandBase
{
    public String func_71517_b()
    {
        return "gamerule";
    }

    public int func_82362_a()
    {
        return 2;
    }

    public String func_71518_a(ICommandSender p_71518_1_)
    {
        return "commands.gamerule.usage";
    }

    public void func_184881_a(MinecraftServer p_184881_1_, ICommandSender p_184881_2_, String[] p_184881_3_) throws CommandException
    {
        GameRules gamerules = this.func_184897_a(p_184881_1_);
        String s = p_184881_3_.length > 0 ? p_184881_3_[0] : "";
        String s1 = p_184881_3_.length > 1 ? func_180529_a(p_184881_3_, 1) : "";

        switch (p_184881_3_.length)
        {
            case 0:
                p_184881_2_.sendMessage(new TextComponentString(func_71527_a(gamerules.func_82763_b())));
                break;
            case 1:

                if (!gamerules.func_82765_e(s))
                {
                    throw new CommandException("commands.gamerule.norule", new Object[] {s});
                }

                String s2 = gamerules.func_82767_a(s);
                p_184881_2_.sendMessage((new TextComponentString(s)).appendText(" = ").appendText(s2));
                p_184881_2_.func_174794_a(CommandResultStats.Type.QUERY_RESULT, gamerules.getInt(s));
                break;
            default:

                if (gamerules.func_180264_a(s, GameRules.ValueType.BOOLEAN_VALUE) && !"true".equals(s1) && !"false".equals(s1))
                {
                    throw new CommandException("commands.generic.boolean.invalid", new Object[] {s1});
                }

                gamerules.setOrCreateGameRule(s, s1);
                func_184898_a(gamerules, s, p_184881_1_);
                func_152373_a(p_184881_2_, this, "commands.gamerule.success", new Object[] {s, s1});
        }
    }

    public static void func_184898_a(GameRules p_184898_0_, String p_184898_1_, MinecraftServer p_184898_2_)
    {
        if ("reducedDebugInfo".equals(p_184898_1_))
        {
            byte b0 = (byte)(p_184898_0_.getBoolean(p_184898_1_) ? 22 : 23);

            for (EntityPlayerMP entityplayermp : p_184898_2_.getPlayerList().getPlayers())
            {
                entityplayermp.connection.sendPacket(new SPacketEntityStatus(entityplayermp, b0));
            }
        }
        net.minecraftforge.event.ForgeEventFactory.onGameRuleChange(p_184898_0_, p_184898_1_, p_184898_2_);
    }

    public List<String> func_184883_a(MinecraftServer p_184883_1_, ICommandSender p_184883_2_, String[] p_184883_3_, @Nullable BlockPos p_184883_4_)
    {
        if (p_184883_3_.length == 1)
        {
            return func_71530_a(p_184883_3_, this.func_184897_a(p_184883_1_).func_82763_b());
        }
        else
        {
            if (p_184883_3_.length == 2)
            {
                GameRules gamerules = this.func_184897_a(p_184883_1_);

                if (gamerules.func_180264_a(p_184883_3_[0], GameRules.ValueType.BOOLEAN_VALUE))
                {
                    return func_71530_a(p_184883_3_, new String[] {"true", "false"});
                }

                if (gamerules.func_180264_a(p_184883_3_[0], GameRules.ValueType.FUNCTION))
                {
                    return func_175762_a(p_184883_3_, p_184883_1_.getFunctionManager().getFunctions().keySet());
                }
            }

            return Collections.<String>emptyList();
        }
    }

    private GameRules func_184897_a(MinecraftServer p_184897_1_)
    {
        return p_184897_1_.getWorld(0).getGameRules();
    }
}