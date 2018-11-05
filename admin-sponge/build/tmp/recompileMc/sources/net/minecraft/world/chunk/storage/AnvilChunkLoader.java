package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilChunkLoader implements IChunkLoader, IThreadedFileIO
{
    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * A map containing chunks to be written to disk (but not those that are currently in the process of being written).
     * Key is the chunk position, value is the NBT to write.
     */
    private final Map<ChunkPos, NBTTagCompound> chunksToSave = Maps.<ChunkPos, NBTTagCompound>newConcurrentMap();
    private final Set<ChunkPos> field_193415_c = Collections.<ChunkPos>newSetFromMap(Maps.newConcurrentMap());
    /** Save directory for chunks using the Anvil format */
    public final File chunkSaveLocation;
    private final DataFixer fixer;
    private boolean flushing;

    public AnvilChunkLoader(File p_i46673_1_, DataFixer p_i46673_2_)
    {
        this.chunkSaveLocation = p_i46673_1_;
        this.fixer = p_i46673_2_;
    }

    @Deprecated // TODO: remove (1.13)
    public boolean chunkExists(World world, int x, int z)
    {
        return isChunkGeneratedAt(x, z);
    }

    @Nullable
    public Chunk func_75815_a(World p_75815_1_, int p_75815_2_, int p_75815_3_) throws IOException
    {
        Object[] data = this.loadChunk__Async(p_75815_1_, p_75815_2_, p_75815_3_);

        if (data != null)
        {
            Chunk chunk = (Chunk) data[0];
            NBTTagCompound nbttagcompound = (NBTTagCompound) data[1];
            this.loadEntities(p_75815_1_, nbttagcompound.getCompound("Level"), chunk);
            return chunk;
        }

        return null;
    }

    @Nullable
    public Object[] loadChunk__Async(World p_75815_1_, int p_75815_2_, int p_75815_3_) throws IOException
    {
        ChunkPos chunkpos = new ChunkPos(p_75815_2_, p_75815_3_);
        NBTTagCompound nbttagcompound = this.chunksToSave.get(chunkpos);

        if (nbttagcompound == null)
        {
            DataInputStream datainputstream = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, p_75815_2_, p_75815_3_);

            if (datainputstream == null)
            {
                return null;
            }

            nbttagcompound = this.fixer.func_188257_a(FixTypes.CHUNK, CompressedStreamTools.read(datainputstream));
        }

        return this.checkedReadChunkFromNBT__Async(p_75815_1_, p_75815_2_, p_75815_3_, nbttagcompound);
    }

    public boolean isChunkGeneratedAt(int x, int z)
    {
        ChunkPos chunkpos = new ChunkPos(x, z);
        NBTTagCompound nbttagcompound = this.chunksToSave.get(chunkpos);
        return nbttagcompound != null ? true : RegionFileCache.chunkExists(this.chunkSaveLocation, x, z);
    }

    /**
     * Wraps readChunkFromNBT. Checks the coordinates and several NBT tags.
     */
    @Nullable
    protected Chunk checkedReadChunkFromNBT(World worldIn, int x, int z, NBTTagCompound compound)
    {
        Object[] data = this.checkedReadChunkFromNBT__Async(worldIn, x, z, compound);
        return data != null ? (Chunk)data[0] : null;
    }

    @Nullable
    protected Object[] checkedReadChunkFromNBT__Async(World worldIn, int x, int z, NBTTagCompound compound)
    {
        if (!compound.contains("Level", 10))
        {
            LOGGER.error("Chunk file at {},{} is missing level data, skipping", Integer.valueOf(x), Integer.valueOf(z));
            return null;
        }
        else
        {
            NBTTagCompound nbttagcompound = compound.getCompound("Level");

            if (!nbttagcompound.contains("Sections", 9))
            {
                LOGGER.error("Chunk file at {},{} is missing block data, skipping", Integer.valueOf(x), Integer.valueOf(z));
                return null;
            }
            else
            {
                Chunk chunk = this.readChunkFromNBT(worldIn, nbttagcompound);

                if (!chunk.isAtLocation(x, z))
                {
                    LOGGER.error("Chunk file at {},{} is in the wrong location; relocating. (Expected {}, {}, got {}, {})", Integer.valueOf(x), Integer.valueOf(z), Integer.valueOf(x), Integer.valueOf(z), Integer.valueOf(chunk.x), Integer.valueOf(chunk.z));
                    nbttagcompound.putInt("xPos", x);
                    nbttagcompound.putInt("zPos", z);

                    // Have to move tile entities since we don't load them at this stage
                    NBTTagList _tileEntities = nbttagcompound.getList("TileEntities", 10);

                    if (_tileEntities != null)
                    {
                        for (int te = 0; te < _tileEntities.func_74745_c(); te++)
                        {
                            NBTTagCompound _nbt = (NBTTagCompound) _tileEntities.getCompound(te);
                            _nbt.putInt("x", x * 16 + (_nbt.getInt("x") - chunk.x * 16));
                            _nbt.putInt("z", z * 16 + (_nbt.getInt("z") - chunk.z * 16));
                        }
                    }

                    chunk = this.readChunkFromNBT(worldIn, nbttagcompound);
                }

                Object[] data = new Object[2];
                data[0] = chunk;
                data[1] = compound;
                // event is fired in ChunkIOProvider.callStage2 since it must be fired after TE's load.
                // MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Load(chunk, par4NBTTagCompound));
                return data;
            }
        }
    }

    public void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException
    {
        worldIn.checkSessionLock();

        try
        {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound.put("Level", nbttagcompound1);
            nbttagcompound.putInt("DataVersion", 1343);
            net.minecraftforge.fml.common.FMLCommonHandler.instance().getDataFixer().writeVersionData(nbttagcompound);
            this.writeChunkToNBT(chunkIn, worldIn, nbttagcompound1);
            net.minecraftforge.common.ForgeChunkManager.storeChunkNBT(chunkIn, nbttagcompound1);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkDataEvent.Save(chunkIn, nbttagcompound));
            this.addChunkToPending(chunkIn.getPos(), nbttagcompound);
        }
        catch (Exception exception)
        {
            LOGGER.error("Failed to save chunk", (Throwable)exception);
        }
    }

    protected void addChunkToPending(ChunkPos pos, NBTTagCompound compound)
    {
        if (!this.field_193415_c.contains(pos))
        {
            this.chunksToSave.put(pos, compound);
        }

        ThreadedFileIOBase.getThreadedIOInstance().queueIO(this);
    }

    /**
     * Writes one queued IO action.
     *  
     * @return true if there are more IO actions to perform afterwards, or false if there are none (and this instance of
     * IThreadedFileIO should be removed from the queued list)
     */
    public boolean writeNextIO()
    {
        if (this.chunksToSave.isEmpty())
        {
            if (this.flushing)
            {
                LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", (Object)this.chunkSaveLocation.getName());
            }

            return false;
        }
        else
        {
            ChunkPos chunkpos = this.chunksToSave.keySet().iterator().next();
            boolean lvt_3_1_;

            try
            {
                this.field_193415_c.add(chunkpos);
                NBTTagCompound nbttagcompound = this.chunksToSave.remove(chunkpos);

                if (nbttagcompound != null)
                {
                    try
                    {
                        this.writeChunkData(chunkpos, nbttagcompound);
                    }
                    catch (Exception exception)
                    {
                        LOGGER.error("Failed to save chunk", (Throwable)exception);
                    }
                }

                lvt_3_1_ = true;
            }
            finally
            {
                this.field_193415_c.remove(chunkpos);
            }

            return lvt_3_1_;
        }
    }

    private void writeChunkData(ChunkPos pos, NBTTagCompound compound) throws IOException
    {
        DataOutputStream dataoutputstream = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, pos.x, pos.z);
        CompressedStreamTools.write(compound, dataoutputstream);
        dataoutputstream.close();
    }

    /**
     * Save extra data associated with this Chunk not normally saved during autosave, only during chunk unload.
     * Currently unused.
     */
    public void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException
    {
    }

    /**
     * Called every World.tick()
     */
    public void chunkTick()
    {
    }

    /**
     * Flushes all pending chunks fully back to disk
     */
    public void flush()
    {
        try
        {
            this.flushing = true;

            while (this.writeNextIO());
        }
        finally
        {
            this.flushing = false;
        }
    }

    public static void func_189889_a(DataFixer p_189889_0_)
    {
        p_189889_0_.func_188258_a(FixTypes.CHUNK, new IDataWalker()
        {
            public NBTTagCompound func_188266_a(IDataFixer p_188266_1_, NBTTagCompound p_188266_2_, int p_188266_3_)
            {
                if (p_188266_2_.contains("Level", 10))
                {
                    NBTTagCompound nbttagcompound = p_188266_2_.getCompound("Level");

                    if (nbttagcompound.contains("Entities", 9))
                    {
                        NBTTagList nbttaglist = nbttagcompound.getList("Entities", 10);

                        for (int i = 0; i < nbttaglist.func_74745_c(); ++i)
                        {
                            nbttaglist.func_150304_a(i, p_188266_1_.func_188251_a(FixTypes.ENTITY, (NBTTagCompound)nbttaglist.func_179238_g(i), p_188266_3_));
                        }
                    }

                    if (nbttagcompound.contains("TileEntities", 9))
                    {
                        NBTTagList nbttaglist1 = nbttagcompound.getList("TileEntities", 10);

                        for (int j = 0; j < nbttaglist1.func_74745_c(); ++j)
                        {
                            nbttaglist1.func_150304_a(j, p_188266_1_.func_188251_a(FixTypes.BLOCK_ENTITY, (NBTTagCompound)nbttaglist1.func_179238_g(j), p_188266_3_));
                        }
                    }
                }

                return p_188266_2_;
            }
        });
    }

    /**
     * Writes the Chunk passed as an argument to the NBTTagCompound also passed, using the World argument to retrieve
     * the Chunk's last update time.
     */
    private void writeChunkToNBT(Chunk chunkIn, World worldIn, NBTTagCompound compound)
    {
        compound.putInt("xPos", chunkIn.x);
        compound.putInt("zPos", chunkIn.z);
        compound.putLong("LastUpdate", worldIn.getGameTime());
        compound.putIntArray("HeightMap", chunkIn.func_177445_q());
        compound.putBoolean("TerrainPopulated", chunkIn.func_177419_t());
        compound.putBoolean("LightPopulated", chunkIn.func_177423_u());
        compound.putLong("InhabitedTime", chunkIn.getInhabitedTime());
        ExtendedBlockStorage[] aextendedblockstorage = chunkIn.getSections();
        NBTTagList nbttaglist = new NBTTagList();
        boolean flag = worldIn.dimension.hasSkyLight();

        for (ExtendedBlockStorage extendedblockstorage : aextendedblockstorage)
        {
            if (extendedblockstorage != Chunk.EMPTY_SECTION)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.putByte("Y", (byte)(extendedblockstorage.getYLocation() >> 4 & 255));
                byte[] abyte = new byte[4096];
                NibbleArray nibblearray = new NibbleArray();
                NibbleArray nibblearray1 = extendedblockstorage.getData().func_186017_a(abyte, nibblearray);
                nbttagcompound.putByteArray("Blocks", abyte);
                nbttagcompound.putByteArray("Data", nibblearray.getData());

                if (nibblearray1 != null)
                {
                    nbttagcompound.putByteArray("Add", nibblearray1.getData());
                }

                nbttagcompound.putByteArray("BlockLight", extendedblockstorage.getBlockLight().getData());

                if (flag)
                {
                    nbttagcompound.putByteArray("SkyLight", extendedblockstorage.getSkyLight().getData());
                }
                else
                {
                    nbttagcompound.putByteArray("SkyLight", new byte[extendedblockstorage.getBlockLight().getData().length]);
                }

                nbttaglist.func_74742_a(nbttagcompound);
            }
        }

        compound.put("Sections", nbttaglist);
        compound.putByteArray("Biomes", chunkIn.func_76605_m());
        chunkIn.setHasEntities(false);
        NBTTagList nbttaglist1 = new NBTTagList();

        for (int i = 0; i < chunkIn.getEntityLists().length; ++i)
        {
            for (Entity entity : chunkIn.getEntityLists()[i])
            {
                NBTTagCompound nbttagcompound2 = new NBTTagCompound();

                try
                {
                if (entity.writeUnlessPassenger(nbttagcompound2))
                {
                    chunkIn.setHasEntities(true);
                    nbttaglist1.func_74742_a(nbttagcompound2);
                }
                }
                catch (Exception e)
                {
                    net.minecraftforge.fml.common.FMLLog.log.error("An Entity type {} has thrown an exception trying to write state. It will not persist. Report this to the mod author",
                            entity.getClass().getName(), e);
                }
            }
        }

        compound.put("Entities", nbttaglist1);
        NBTTagList nbttaglist2 = new NBTTagList();

        for (TileEntity tileentity : chunkIn.getTileEntityMap().values())
        {
            try
            {
            NBTTagCompound nbttagcompound3 = tileentity.write(new NBTTagCompound());
            nbttaglist2.func_74742_a(nbttagcompound3);
            }
            catch (Exception e)
            {
                net.minecraftforge.fml.common.FMLLog.log.error("A TileEntity type {} has throw an exception trying to write state. It will not persist. Report this to the mod author",
                        tileentity.getClass().getName(), e);
            }
        }

        compound.put("TileEntities", nbttaglist2);
        List<NextTickListEntry> list = worldIn.func_72920_a(chunkIn, false);

        if (list != null)
        {
            long j = worldIn.getGameTime();
            NBTTagList nbttaglist3 = new NBTTagList();

            for (NextTickListEntry nextticklistentry : list)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                ResourceLocation resourcelocation = Block.REGISTRY.getKey(nextticklistentry.getTarget());
                nbttagcompound1.putString("i", resourcelocation == null ? "" : resourcelocation.toString());
                nbttagcompound1.putInt("x", nextticklistentry.position.getX());
                nbttagcompound1.putInt("y", nextticklistentry.position.getY());
                nbttagcompound1.putInt("z", nextticklistentry.position.getZ());
                nbttagcompound1.putInt("t", (int)(nextticklistentry.scheduledTime - j));
                nbttagcompound1.putInt("p", nextticklistentry.priority);
                nbttaglist3.func_74742_a(nbttagcompound1);
            }

            compound.put("TileTicks", nbttaglist3);
        }

        if (chunkIn.getCapabilities() != null)
        {
            try
            {
                compound.put("ForgeCaps", chunkIn.getCapabilities().serializeNBT());
            }
            catch (Exception exception)
            {
                net.minecraftforge.fml.common.FMLLog.log.error("A capability provider has thrown an exception trying to write state. It will not persist. Report this to the mod author", exception);
            }
        }
    }

    /**
     * Reads the data stored in the passed NBTTagCompound and creates a Chunk with that data in the passed World.
     * Returns the created Chunk.
     */
    private Chunk readChunkFromNBT(World worldIn, NBTTagCompound compound)
    {
        int i = compound.getInt("xPos");
        int j = compound.getInt("zPos");
        Chunk chunk = new Chunk(worldIn, i, j);
        chunk.func_177420_a(compound.getIntArray("HeightMap"));
        chunk.func_177446_d(compound.getBoolean("TerrainPopulated"));
        chunk.func_177421_e(compound.getBoolean("LightPopulated"));
        chunk.setInhabitedTime(compound.getLong("InhabitedTime"));
        NBTTagList nbttaglist = compound.getList("Sections", 10);
        int k = 16;
        ExtendedBlockStorage[] aextendedblockstorage = new ExtendedBlockStorage[16];
        boolean flag = worldIn.dimension.hasSkyLight();

        for (int l = 0; l < nbttaglist.func_74745_c(); ++l)
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(l);
            int i1 = nbttagcompound.getByte("Y");
            ExtendedBlockStorage extendedblockstorage = new ExtendedBlockStorage(i1 << 4, flag);
            byte[] abyte = nbttagcompound.getByteArray("Blocks");
            NibbleArray nibblearray = new NibbleArray(nbttagcompound.getByteArray("Data"));
            NibbleArray nibblearray1 = nbttagcompound.contains("Add", 7) ? new NibbleArray(nbttagcompound.getByteArray("Add")) : null;
            extendedblockstorage.getData().func_186019_a(abyte, nibblearray, nibblearray1);
            extendedblockstorage.setBlockLight(new NibbleArray(nbttagcompound.getByteArray("BlockLight")));

            if (flag)
            {
                extendedblockstorage.setSkyLight(new NibbleArray(nbttagcompound.getByteArray("SkyLight")));
            }

            extendedblockstorage.recalculateRefCounts();
            aextendedblockstorage[i1] = extendedblockstorage;
        }

        chunk.setSections(aextendedblockstorage);

        if (compound.contains("Biomes", 7))
        {
            chunk.func_76616_a(compound.getByteArray("Biomes"));
        }

        if (chunk.getCapabilities() != null && compound.contains("ForgeCaps")) {
            chunk.getCapabilities().deserializeNBT(compound.getCompound("ForgeCaps"));
        }

        // End this method here and split off entity loading to another method
        return chunk;
    }

    public void loadEntities(World worldIn, NBTTagCompound compound, Chunk chunk)
    {
        NBTTagList nbttaglist1 = compound.getList("Entities", 10);

        for (int j1 = 0; j1 < nbttaglist1.func_74745_c(); ++j1)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist1.getCompound(j1);
            readChunkEntity(nbttagcompound1, worldIn, chunk);
            chunk.setHasEntities(true);
        }

        NBTTagList nbttaglist2 = compound.getList("TileEntities", 10);

        for (int k1 = 0; k1 < nbttaglist2.func_74745_c(); ++k1)
        {
            NBTTagCompound nbttagcompound2 = nbttaglist2.getCompound(k1);
            TileEntity tileentity = TileEntity.func_190200_a(worldIn, nbttagcompound2);

            if (tileentity != null)
            {
                chunk.addTileEntity(tileentity);
            }
        }

        if (compound.contains("TileTicks", 9))
        {
            NBTTagList nbttaglist3 = compound.getList("TileTicks", 10);

            for (int l1 = 0; l1 < nbttaglist3.func_74745_c(); ++l1)
            {
                NBTTagCompound nbttagcompound3 = nbttaglist3.getCompound(l1);
                Block block;

                if (nbttagcompound3.contains("i", 8))
                {
                    block = Block.getBlockFromName(nbttagcompound3.getString("i"));
                }
                else
                {
                    block = Block.getBlockById(nbttagcompound3.getInt("i"));
                }

                worldIn.func_180497_b(new BlockPos(nbttagcompound3.getInt("x"), nbttagcompound3.getInt("y"), nbttagcompound3.getInt("z")), block, nbttagcompound3.getInt("t"), nbttagcompound3.getInt("p"));
            }
        }
    }

    @Nullable
    public static Entity readChunkEntity(NBTTagCompound compound, World worldIn, Chunk chunkIn)
    {
        Entity entity = createEntityFromNBT(compound, worldIn);

        if (entity == null)
        {
            return null;
        }
        else
        {
            chunkIn.addEntity(entity);

            if (compound.contains("Passengers", 9))
            {
                NBTTagList nbttaglist = compound.getList("Passengers", 10);

                for (int i = 0; i < nbttaglist.func_74745_c(); ++i)
                {
                    Entity entity1 = readChunkEntity(nbttaglist.getCompound(i), worldIn, chunkIn);

                    if (entity1 != null)
                    {
                        entity1.startRiding(entity, true);
                    }
                }
            }

            return entity;
        }
    }

    @Nullable
    public static Entity readWorldEntityPos(NBTTagCompound compound, World worldIn, double x, double y, double z, boolean attemptSpawn)
    {
        Entity entity = createEntityFromNBT(compound, worldIn);

        if (entity == null)
        {
            return null;
        }
        else
        {
            entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);

            if (attemptSpawn && !worldIn.spawnEntity(entity))
            {
                return null;
            }
            else
            {
                if (compound.contains("Passengers", 9))
                {
                    NBTTagList nbttaglist = compound.getList("Passengers", 10);

                    for (int i = 0; i < nbttaglist.func_74745_c(); ++i)
                    {
                        Entity entity1 = readWorldEntityPos(nbttaglist.getCompound(i), worldIn, x, y, z, attemptSpawn);

                        if (entity1 != null)
                        {
                            entity1.startRiding(entity, true);
                        }
                    }
                }

                return entity;
            }
        }
    }

    @Nullable
    protected static Entity createEntityFromNBT(NBTTagCompound compound, World worldIn)
    {
        try
        {
            return EntityList.func_75615_a(compound, worldIn);
        }
        catch (RuntimeException var3)
        {
            return null;
        }
    }

    public static void spawnEntity(Entity entityIn, World worldIn)
    {
        if (worldIn.spawnEntity(entityIn) && entityIn.isBeingRidden())
        {
            for (Entity entity : entityIn.getPassengers())
            {
                spawnEntity(entity, worldIn);
            }
        }
    }

    @Nullable
    public static Entity readWorldEntity(NBTTagCompound compound, World worldIn, boolean p_186051_2_)
    {
        Entity entity = createEntityFromNBT(compound, worldIn);

        if (entity == null)
        {
            return null;
        }
        else if (p_186051_2_ && !worldIn.spawnEntity(entity))
        {
            return null;
        }
        else
        {
            if (compound.contains("Passengers", 9))
            {
                NBTTagList nbttaglist = compound.getList("Passengers", 10);

                for (int i = 0; i < nbttaglist.func_74745_c(); ++i)
                {
                    Entity entity1 = readWorldEntity(nbttaglist.getCompound(i), worldIn, p_186051_2_);

                    if (entity1 != null)
                    {
                        entity1.startRiding(entity, true);
                    }
                }
            }

            return entity;
        }
    }

    public int getPendingSaveCount()
    {
        return this.chunksToSave.size();
    }
}