package net.minecraft.world.storage;

import java.io.File;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.util.IProgressUpdate;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISaveFormat
{
    @SideOnly(Side.CLIENT)
    String func_154333_a();

    ISaveHandler func_75804_a(String p_75804_1_, boolean p_75804_2_);

    @SideOnly(Side.CLIENT)
    List<WorldSummary> getSaveList() throws AnvilConverterException;

    /**
     * gets if the map is old chunk saving (true) or McRegion (false)
     */
    boolean isOldMapFormat(String saveName);

    @SideOnly(Side.CLIENT)
    void flushCache();

    /**
     * Returns the world's WorldInfo object
     */
    @Nullable
    @SideOnly(Side.CLIENT)
    WorldInfo getWorldInfo(String saveName);

    @SideOnly(Side.CLIENT)
    boolean func_154335_d(String p_154335_1_);

    /**
     * Deletes a world directory.
     */
    @SideOnly(Side.CLIENT)
    boolean deleteWorldDirectory(String saveName);

    /**
     * Renames the world by storing the new name in level.dat. It does *not* rename the directory containing the world
     * data.
     */
    @SideOnly(Side.CLIENT)
    void renameWorld(String dirName, String newName);

    @SideOnly(Side.CLIENT)
    boolean func_154334_a(String p_154334_1_);

    /**
     * converts the map to mcRegion
     */
    boolean convertMapFormat(String filename, IProgressUpdate progressCallback);

    /**
     * Gets a file within the given world.
     *  
     * @param saveName Name of the world
     * @param filePath Path to the file, relative to the world's folder
     */
    File getFile(String saveName, String filePath);

    /**
     * Return whether the given world can be loaded.
     */
    @SideOnly(Side.CLIENT)
    boolean canLoadWorld(String saveName);
}