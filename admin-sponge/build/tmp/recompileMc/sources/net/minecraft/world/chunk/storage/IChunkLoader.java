package net.minecraft.world.chunk.storage;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public interface IChunkLoader
{
    @Nullable
    Chunk func_75815_a(World p_75815_1_, int p_75815_2_, int p_75815_3_) throws IOException;

    void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException;

    /**
     * Save extra data associated with this Chunk not normally saved during autosave, only during chunk unload.
     * Currently unused.
     */
    void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException;

    /**
     * Called every World.tick()
     */
    void chunkTick();

    /**
     * Flushes all pending chunks fully back to disk
     */
    void flush();

    boolean isChunkGeneratedAt(int x, int z);
}