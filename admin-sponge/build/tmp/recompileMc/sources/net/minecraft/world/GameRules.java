package net.minecraft.world;

import java.util.Set;
import java.util.TreeMap;
import net.minecraft.nbt.NBTTagCompound;

public class GameRules
{
    private final TreeMap<String, GameRules.Value> rules = new TreeMap<String, GameRules.Value>();

    public GameRules()
    {
        this.func_180262_a("doFireTick", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("mobGriefing", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("keepInventory", "false", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("doMobSpawning", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("doMobLoot", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("doTileDrops", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("doEntityDrops", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("commandBlockOutput", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("naturalRegeneration", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("doDaylightCycle", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("logAdminCommands", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("showDeathMessages", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("randomTickSpeed", "3", GameRules.ValueType.NUMERICAL_VALUE);
        this.func_180262_a("sendCommandFeedback", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("reducedDebugInfo", "false", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("spectatorsGenerateChunks", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("spawnRadius", "10", GameRules.ValueType.NUMERICAL_VALUE);
        this.func_180262_a("disableElytraMovementCheck", "false", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("maxEntityCramming", "24", GameRules.ValueType.NUMERICAL_VALUE);
        this.func_180262_a("doWeatherCycle", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("doLimitedCrafting", "false", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("maxCommandChainLength", "65536", GameRules.ValueType.NUMERICAL_VALUE);
        this.func_180262_a("announceAdvancements", "true", GameRules.ValueType.BOOLEAN_VALUE);
        this.func_180262_a("gameLoopFunction", "-", GameRules.ValueType.FUNCTION);
    }

    public void func_180262_a(String p_180262_1_, String p_180262_2_, GameRules.ValueType p_180262_3_)
    {
        this.rules.put(p_180262_1_, new GameRules.Value(p_180262_2_, p_180262_3_));
    }

    public void setOrCreateGameRule(String key, String ruleValue)
    {
        GameRules.Value gamerules$value = this.rules.get(key);

        if (gamerules$value != null)
        {
            gamerules$value.func_82757_a(ruleValue);
        }
        else
        {
            this.func_180262_a(key, ruleValue, GameRules.ValueType.ANY_VALUE);
        }
    }

    public String func_82767_a(String p_82767_1_)
    {
        GameRules.Value gamerules$value = this.rules.get(p_82767_1_);
        return gamerules$value != null ? gamerules$value.getString() : "";
    }

    /**
     * Gets the boolean Game Rule value.
     */
    public boolean getBoolean(String name)
    {
        GameRules.Value gamerules$value = this.rules.get(name);
        return gamerules$value != null ? gamerules$value.getBoolean() : false;
    }

    public int getInt(String name)
    {
        GameRules.Value gamerules$value = this.rules.get(name);
        return gamerules$value != null ? gamerules$value.getInt() : 0;
    }

    /**
     * Return the defined game rules as NBT.
     */
    public NBTTagCompound write()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        for (String s : this.rules.keySet())
        {
            GameRules.Value gamerules$value = this.rules.get(s);
            nbttagcompound.putString(s, gamerules$value.getString());
        }

        return nbttagcompound;
    }

    /**
     * Set defined game rules from NBT.
     */
    public void read(NBTTagCompound nbt)
    {
        for (String s : nbt.keySet())
        {
            this.setOrCreateGameRule(s, nbt.getString(s));
        }
    }

    public String[] func_82763_b()
    {
        Set<String> set = this.rules.keySet();
        return (String[])set.toArray(new String[set.size()]);
    }

    public boolean func_82765_e(String p_82765_1_)
    {
        return this.rules.containsKey(p_82765_1_);
    }

    public boolean func_180264_a(String p_180264_1_, GameRules.ValueType p_180264_2_)
    {
        GameRules.Value gamerules$value = this.rules.get(p_180264_1_);
        return gamerules$value != null && (gamerules$value.getType() == p_180264_2_ || p_180264_2_ == GameRules.ValueType.ANY_VALUE);
    }

    static class Value
        {
            private String valueString;
            private boolean valueBoolean;
            private int valueInteger;
            private double valueDouble;
            private final GameRules.ValueType type;

            public Value(String p_i45751_1_, GameRules.ValueType p_i45751_2_)
            {
                this.type = p_i45751_2_;
                this.func_82757_a(p_i45751_1_);
            }

            public void func_82757_a(String p_82757_1_)
            {
                this.valueString = p_82757_1_;
                this.valueBoolean = Boolean.parseBoolean(p_82757_1_);
                this.valueInteger = this.valueBoolean ? 1 : 0;

                try
                {
                    this.valueInteger = Integer.parseInt(p_82757_1_);
                }
                catch (NumberFormatException var4)
                {
                    ;
                }

                try
                {
                    this.valueDouble = Double.parseDouble(p_82757_1_);
                }
                catch (NumberFormatException var3)
                {
                    ;
                }
            }

            /**
             * Gets the GameRule's value as String.
             */
            public String getString()
            {
                return this.valueString;
            }

            /**
             * Gets the GameRule's value as boolean.
             */
            public boolean getBoolean()
            {
                return this.valueBoolean;
            }

            public int getInt()
            {
                return this.valueInteger;
            }

            public GameRules.ValueType getType()
            {
                return this.type;
            }
        }

    public static enum ValueType
    {
        ANY_VALUE,
        BOOLEAN_VALUE,
        NUMERICAL_VALUE,
        FUNCTION;
    }
}