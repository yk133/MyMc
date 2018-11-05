package net.minecraft.client.settings;

import java.util.ArrayList;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HotbarSnapshot extends ArrayList<ItemStack>
{
    public static final int field_192835_a = InventoryPlayer.getHotbarSize();

    public HotbarSnapshot()
    {
        this.ensureCapacity(field_192835_a);

        for (int i = 0; i < field_192835_a; ++i)
        {
            this.add(ItemStack.EMPTY);
        }
    }

    public NBTTagList createTag()
    {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < field_192835_a; ++i)
        {
            nbttaglist.func_74742_a(((ItemStack)this.get(i)).write(new NBTTagCompound()));
        }

        return nbttaglist;
    }

    public void fromTag(NBTTagList tag)
    {
        for (int i = 0; i < field_192835_a; ++i)
        {
            this.set(i, new ItemStack(tag.getCompound(i)));
        }
    }

    public boolean isEmpty()
    {
        for (int i = 0; i < field_192835_a; ++i)
        {
            if (!((ItemStack)this.get(i)).isEmpty())
            {
                return false;
            }
        }

        return true;
    }
}