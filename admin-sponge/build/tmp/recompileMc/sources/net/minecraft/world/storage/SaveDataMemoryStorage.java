package net.minecraft.world.storage;

import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SaveDataMemoryStorage extends MapStorage
{
    public SaveDataMemoryStorage()
    {
        super((ISaveHandler)null);
    }

    @Nullable
    public WorldSavedData func_75742_a(Class <? extends WorldSavedData > p_75742_1_, String p_75742_2_)
    {
        return this.loadedDataMap.get(p_75742_2_);
    }

    /**
     * Assigns the given String id to the given MapDataBase, removing any existing ones of the same id.
     */
    public void setData(String dataIdentifier, WorldSavedData data)
    {
        this.loadedDataMap.put(dataIdentifier, data);
    }

    /**
     * Saves all dirty loaded MapDataBases to disk.
     */
    public void saveAllData()
    {
    }

    /**
     * Returns an unique new data id for the given prefix and saves the idCounts map to the 'idcounts' file.
     */
    public int getUniqueDataId(String key)
    {
        return 0;
    }
}