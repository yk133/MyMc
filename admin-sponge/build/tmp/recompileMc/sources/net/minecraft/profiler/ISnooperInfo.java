package net.minecraft.profiler;

public interface ISnooperInfo
{
    void addServerStatsToSnooper(Snooper playerSnooper);

    void func_70001_b(Snooper p_70001_1_);

    /**
     * Returns whether snooping is enabled or not.
     */
    boolean isSnooperEnabled();
}