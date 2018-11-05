package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class WorldGenerator
{
    /**
     * Sets wither or not the generator should notify blocks of blocks it changes. When the world is first generated,
     * this is false, when saplings grow, this is true.
     */
    private final boolean doBlockNotify;

    public WorldGenerator()
    {
        this(false);
    }

    public WorldGenerator(boolean notify)
    {
        this.doBlockNotify = notify;
    }

    public abstract boolean func_180709_b(World p_180709_1_, Random p_180709_2_, BlockPos p_180709_3_);

    public void func_175904_e()
    {
    }

    protected void func_175903_a(World p_175903_1_, BlockPos p_175903_2_, IBlockState p_175903_3_)
    {
        if (this.doBlockNotify)
        {
            p_175903_1_.setBlockState(p_175903_2_, p_175903_3_, 3);
        }
        else
        {
            int flag = net.minecraftforge.common.ForgeModContainer.fixVanillaCascading ? 2| 16 : 2; //Forge: With bit 5 unset, it will notify neighbors and load adjacent chunks.
            p_175903_1_.setBlockState(p_175903_2_, p_175903_3_, flag);
        }
    }
}