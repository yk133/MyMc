package net.minecraft.util.datafix.fixes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.datafix.IFixableData;

public class RidingToPassengers implements IFixableData
{
    public int func_188216_a()
    {
        return 135;
    }

    public NBTTagCompound func_188217_a(NBTTagCompound p_188217_1_)
    {
        while (p_188217_1_.contains("Riding", 10))
        {
            NBTTagCompound nbttagcompound = this.func_188220_b(p_188217_1_);
            this.func_188219_a(p_188217_1_, nbttagcompound);
            p_188217_1_ = nbttagcompound;
        }

        return p_188217_1_;
    }

    protected void func_188219_a(NBTTagCompound p_188219_1_, NBTTagCompound p_188219_2_)
    {
        NBTTagList nbttaglist = new NBTTagList();
        nbttaglist.func_74742_a(p_188219_1_);
        p_188219_2_.put("Passengers", nbttaglist);
    }

    protected NBTTagCompound func_188220_b(NBTTagCompound p_188220_1_)
    {
        NBTTagCompound nbttagcompound = p_188220_1_.getCompound("Riding");
        p_188220_1_.remove("Riding");
        return nbttagcompound;
    }
}