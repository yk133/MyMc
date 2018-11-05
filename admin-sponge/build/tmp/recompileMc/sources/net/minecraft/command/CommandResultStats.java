package net.minecraft.command;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class CommandResultStats
{
    private static final int field_179676_a = CommandResultStats.Type.values().length;
    private static final String[] field_179674_b = new String[field_179676_a];
    private String[] field_179675_c;
    private String[] field_179673_d;

    public CommandResultStats()
    {
        this.field_179675_c = field_179674_b;
        this.field_179673_d = field_179674_b;
    }

    public void func_184932_a(MinecraftServer p_184932_1_, final ICommandSender p_184932_2_, CommandResultStats.Type p_184932_3_, int p_184932_4_)
    {
        String s = this.field_179675_c[p_184932_3_.func_179636_a()];

        if (s != null)
        {
            ICommandSender icommandsender = new ICommandSender()
            {
                public String func_70005_c_()
                {
                    return p_184932_2_.func_70005_c_();
                }
                public ITextComponent getDisplayName()
                {
                    return p_184932_2_.getDisplayName();
                }
                /**
                 * Send a chat message to the CommandSender
                 */
                public void sendMessage(ITextComponent component)
                {
                    p_184932_2_.sendMessage(component);
                }
                public boolean func_70003_b(int p_70003_1_, String p_70003_2_)
                {
                    return true;
                }
                /**
                 * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the
                 * world, return the coordinates 0, 0, 0
                 */
                public BlockPos getPosition()
                {
                    return p_184932_2_.getPosition();
                }
                /**
                 * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
                 * return 0.0D, 0.0D, 0.0D
                 */
                public Vec3d getPositionVector()
                {
                    return p_184932_2_.getPositionVector();
                }
                /**
                 * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the
                 * world, return the overworld
                 */
                public World getEntityWorld()
                {
                    return p_184932_2_.getEntityWorld();
                }
                public Entity func_174793_f()
                {
                    return p_184932_2_.func_174793_f();
                }
                public boolean func_174792_t_()
                {
                    return p_184932_2_.func_174792_t_();
                }
                public void func_174794_a(CommandResultStats.Type p_174794_1_, int p_174794_2_)
                {
                    p_184932_2_.func_174794_a(p_174794_1_, p_174794_2_);
                }
                /**
                 * Get the Minecraft server instance
                 */
                public MinecraftServer getServer()
                {
                    return p_184932_2_.getServer();
                }
            };
            String s1;

            try
            {
                s1 = CommandBase.func_184891_e(p_184932_1_, icommandsender, s);
            }
            catch (CommandException var12)
            {
                return;
            }

            String s2 = this.field_179673_d[p_184932_3_.func_179636_a()];

            if (s2 != null)
            {
                Scoreboard scoreboard = p_184932_2_.getEntityWorld().getScoreboard();
                ScoreObjective scoreobjective = scoreboard.getObjective(s2);

                if (scoreobjective != null)
                {
                    if (scoreboard.entityHasObjective(s1, scoreobjective))
                    {
                        Score score = scoreboard.getOrCreateScore(s1, scoreobjective);
                        score.setScorePoints(p_184932_4_);
                    }
                }
            }
        }
    }

    public void func_179668_a(NBTTagCompound p_179668_1_)
    {
        if (p_179668_1_.contains("CommandStats", 10))
        {
            NBTTagCompound nbttagcompound = p_179668_1_.getCompound("CommandStats");

            for (CommandResultStats.Type commandresultstats$type : CommandResultStats.Type.values())
            {
                String s = commandresultstats$type.func_179637_b() + "Name";
                String s1 = commandresultstats$type.func_179637_b() + "Objective";

                if (nbttagcompound.contains(s, 8) && nbttagcompound.contains(s1, 8))
                {
                    String s2 = nbttagcompound.getString(s);
                    String s3 = nbttagcompound.getString(s1);
                    func_179667_a(this, commandresultstats$type, s2, s3);
                }
            }
        }
    }

    public void func_179670_b(NBTTagCompound p_179670_1_)
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        for (CommandResultStats.Type commandresultstats$type : CommandResultStats.Type.values())
        {
            String s = this.field_179675_c[commandresultstats$type.func_179636_a()];
            String s1 = this.field_179673_d[commandresultstats$type.func_179636_a()];

            if (s != null && s1 != null)
            {
                nbttagcompound.putString(commandresultstats$type.func_179637_b() + "Name", s);
                nbttagcompound.putString(commandresultstats$type.func_179637_b() + "Objective", s1);
            }
        }

        if (!nbttagcompound.func_82582_d())
        {
            p_179670_1_.put("CommandStats", nbttagcompound);
        }
    }

    public static void func_179667_a(CommandResultStats p_179667_0_, CommandResultStats.Type p_179667_1_, @Nullable String p_179667_2_, @Nullable String p_179667_3_)
    {
        if (p_179667_2_ != null && !p_179667_2_.isEmpty() && p_179667_3_ != null && !p_179667_3_.isEmpty())
        {
            if (p_179667_0_.field_179675_c == field_179674_b || p_179667_0_.field_179673_d == field_179674_b)
            {
                p_179667_0_.field_179675_c = new String[field_179676_a];
                p_179667_0_.field_179673_d = new String[field_179676_a];
            }

            p_179667_0_.field_179675_c[p_179667_1_.func_179636_a()] = p_179667_2_;
            p_179667_0_.field_179673_d[p_179667_1_.func_179636_a()] = p_179667_3_;
        }
        else
        {
            func_179669_a(p_179667_0_, p_179667_1_);
        }
    }

    private static void func_179669_a(CommandResultStats p_179669_0_, CommandResultStats.Type p_179669_1_)
    {
        if (p_179669_0_.field_179675_c != field_179674_b && p_179669_0_.field_179673_d != field_179674_b)
        {
            p_179669_0_.field_179675_c[p_179669_1_.func_179636_a()] = null;
            p_179669_0_.field_179673_d[p_179669_1_.func_179636_a()] = null;
            boolean flag = true;

            for (CommandResultStats.Type commandresultstats$type : CommandResultStats.Type.values())
            {
                if (p_179669_0_.field_179675_c[commandresultstats$type.func_179636_a()] != null && p_179669_0_.field_179673_d[commandresultstats$type.func_179636_a()] != null)
                {
                    flag = false;
                    break;
                }
            }

            if (flag)
            {
                p_179669_0_.field_179675_c = field_179674_b;
                p_179669_0_.field_179673_d = field_179674_b;
            }
        }
    }

    public void func_179671_a(CommandResultStats p_179671_1_)
    {
        for (CommandResultStats.Type commandresultstats$type : CommandResultStats.Type.values())
        {
            func_179667_a(this, commandresultstats$type, p_179671_1_.field_179675_c[commandresultstats$type.func_179636_a()], p_179671_1_.field_179673_d[commandresultstats$type.func_179636_a()]);
        }
    }

    public static enum Type
    {
        SUCCESS_COUNT(0, "SuccessCount"),
        AFFECTED_BLOCKS(1, "AffectedBlocks"),
        AFFECTED_ENTITIES(2, "AffectedEntities"),
        AFFECTED_ITEMS(3, "AffectedItems"),
        QUERY_RESULT(4, "QueryResult");

        final int field_179639_f;
        final String field_179640_g;

        private Type(int p_i46050_3_, String p_i46050_4_)
        {
            this.field_179639_f = p_i46050_3_;
            this.field_179640_g = p_i46050_4_;
        }

        public int func_179636_a()
        {
            return this.field_179639_f;
        }

        public String func_179637_b()
        {
            return this.field_179640_g;
        }

        public static String[] func_179634_c()
        {
            String[] astring = new String[values().length];
            int i = 0;

            for (CommandResultStats.Type commandresultstats$type : values())
            {
                astring[i++] = commandresultstats$type.func_179637_b();
            }

            return astring;
        }

        @Nullable
        public static CommandResultStats.Type func_179635_a(String p_179635_0_)
        {
            for (CommandResultStats.Type commandresultstats$type : values())
            {
                if (commandresultstats$type.func_179637_b().equals(p_179635_0_))
                {
                    return commandresultstats$type;
                }
            }

            return null;
        }
    }
}