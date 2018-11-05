package net.minecraft.world;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.pathfinding.PathWorldListener;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class World implements IBlockAccess, net.minecraftforge.common.capabilities.ICapabilityProvider
{
    /**
     * Used in the getEntitiesWithinAABB functions to expand the search area for entities.
     * Modders should change this variable to a higher value if it is less then the radius
     * of one of there entities.
     */
    public static double MAX_ENTITY_RADIUS = 2.0D;

    private int seaLevel = 63;
    protected boolean field_72999_e;
    /** A list of all Entities in all currently-loaded chunks */
    public final List<Entity> loadedEntityList = Lists.<Entity>newArrayList();
    protected final List<Entity> unloadedEntityList = Lists.<Entity>newArrayList();
    /** A list of the loaded tile entities in the world */
    public final List<TileEntity> loadedTileEntityList = Lists.<TileEntity>newArrayList();
    public final List<TileEntity> tickableTileEntities = Lists.<TileEntity>newArrayList();
    /**
     * Tile Entity additions that were deferred because the World was still iterating existing Tile Entities; will be
     * added to the world at the end of the tick.
     */
    private final List<TileEntity> addedTileEntityList = Lists.<TileEntity>newArrayList();
    /**
     * Tile Entity removals that were deferred because the World was still iterating existing Tile Entities; will be
     * removed from the world at the end of the tick.
     */
    private final List<TileEntity> tileEntitiesToBeRemoved = Lists.<TileEntity>newArrayList();
    /** Array list of players in the world. */
    public final List<EntityPlayer> playerEntities = Lists.<EntityPlayer>newArrayList();
    /** a list of all the lightning entities */
    public final List<Entity> weatherEffects = Lists.<Entity>newArrayList();
    protected final IntHashMap<Entity> entitiesById = new IntHashMap<Entity>();
    private final long cloudColour = 16777215L;
    /** How much light is subtracted from full daylight */
    private int skylightSubtracted;
    /**
     * Contains the current Linear Congruential Generator seed for block updates. Used with an A value of 3 and a C
     * value of 0x3c6ef35f, producing a highly planar series of values ill-suited for choosing random blocks in a
     * 16x128x16 field.
     */
    protected int updateLCG = (new Random()).nextInt();
    /** magic number used to generate fast random numbers for 3d distribution within a chunk */
    protected final int DIST_HASH_MAGIC = 1013904223;
    public float prevRainingStrength;
    public float rainingStrength;
    public float prevThunderingStrength;
    public float thunderingStrength;
    /** Decrementing counter from when the last lightning bolt was spawned; used to light up the sky with lightning */
    private int lastLightningBolt;
    /** RNG for World. */
    public final Random rand = new Random();
    /** The WorldProvider instance that World uses. */
    public final WorldProvider dimension;
    protected PathWorldListener pathListener = new PathWorldListener();
    protected List<IWorldEventListener> eventListeners;
    /** Handles chunk operations and caching */
    protected IChunkProvider chunkProvider;
    protected final ISaveHandler saveHandler;
    /** holds information about a world (size on disk, time, spawn point, seed, ...) */
    protected WorldInfo worldInfo;
    protected boolean field_72987_B;
    protected MapStorage mapStorage;
    public VillageCollection villageCollection;
    protected LootTableManager field_184151_B;
    protected AdvancementManager field_191951_C;
    protected FunctionManager field_193036_D;
    public final Profiler profiler;
    private final Calendar field_83016_L;
    protected Scoreboard field_96442_D;
    /**
     * True if the world is a 'slave' client; changes will not be saved or propagated from this world. For example,
     * server worlds have this set to false, client worlds have this set to true.
     */
    public final boolean isRemote;
    /** indicates if enemies are spawned or not */
    protected boolean spawnHostileMobs;
    /** A flag indicating whether we should spawn peaceful mobs. */
    protected boolean spawnPeacefulMobs;
    /**
     * True while the World is ticking {@link #tickableTileEntities}, to prevent CME's if any of those ticks create more
     * tile entities.
     */
    private boolean processingLoadedTiles;
    private final WorldBorder worldBorder;
    /**
     * is a temporary list of blocks and light values used when updating light levels. Holds up to 32x32x32 blocks (the
     * maximum influence of a light source.) Every element is a packed bit value: 0000000000LLLLzzzzzzyyyyyyxxxxxx. The
     * 4-bit L is a light level used when darkening blocks. 6-bit numbers x, y and z represent the block's offset from
     * the original block, plus 32 (i.e. value of 31 would mean a -1 offset
     */
    int[] lightUpdateBlockList;

    public boolean restoringBlockSnapshots = false;
    public boolean captureBlockSnapshots = false;
    public java.util.ArrayList<net.minecraftforge.common.util.BlockSnapshot> capturedBlockSnapshots = new java.util.ArrayList<net.minecraftforge.common.util.BlockSnapshot>();
    private net.minecraftforge.common.capabilities.CapabilityDispatcher capabilities;
    private net.minecraftforge.common.util.WorldCapabilityData capabilityData;

    protected World(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider dimension, Profiler profilerIn, boolean client)
    {
        this.eventListeners = Lists.newArrayList(this.pathListener);
        this.field_83016_L = Calendar.getInstance();
        this.field_96442_D = new Scoreboard();
        this.spawnHostileMobs = true;
        this.spawnPeacefulMobs = true;
        this.lightUpdateBlockList = new int[32768];
        this.saveHandler = saveHandlerIn;
        this.profiler = profilerIn;
        this.worldInfo = info;
        this.dimension = dimension;
        this.isRemote = client;
        this.worldBorder = dimension.createWorldBorder();
        perWorldStorage = new MapStorage((ISaveHandler)null);
    }

    public World init()
    {
        return this;
    }

    public Biome getBiome(final BlockPos pos)
    {
        return this.dimension.getBiomeForCoords(pos);
    }

    public Biome getBiomeForCoordsBody(final BlockPos pos)
    {
        if (this.isBlockLoaded(pos))
        {
            Chunk chunk = this.getChunk(pos);

            try
            {
                return chunk.func_177411_a(pos, this.dimension.func_177499_m());
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting biome");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Coordinates of biome request");
                crashreportcategory.addDetail("Location", new ICrashReportDetail<String>()
                {
                    public String call() throws Exception
                    {
                        return CrashReportCategory.getCoordinateInfo(pos);
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
        else
        {
            return this.dimension.func_177499_m().getBiome(pos, Biomes.PLAINS);
        }
    }

    public BiomeProvider func_72959_q()
    {
        return this.dimension.func_177499_m();
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected abstract IChunkProvider createChunkProvider();

    public void initialize(WorldSettings settings)
    {
        this.worldInfo.setServerInitialized(true);
    }

    @Nullable
    public MinecraftServer getServer()
    {
        return null;
    }

    /**
     * Sets a new spawn location by finding an uncovered block at a random (x,z) location in the chunk.
     */
    @SideOnly(Side.CLIENT)
    public void setInitialSpawnLocation()
    {
        this.setSpawnPoint(new BlockPos(8, 64, 8));
    }

    public IBlockState getGroundAboveSeaLevel(BlockPos pos)
    {
        BlockPos blockpos;

        for (blockpos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ()); !this.isAirBlock(blockpos.up()); blockpos = blockpos.up())
        {
            ;
        }

        return this.getBlockState(blockpos);
    }

    /**
     * Check if the given BlockPos has valid coordinates
     */
    public boolean isValid(BlockPos p_175701_1_)
    {
        return !this.isOutsideBuildHeight(p_175701_1_) && p_175701_1_.getX() >= -30000000 && p_175701_1_.getZ() >= -30000000 && p_175701_1_.getX() < 30000000 && p_175701_1_.getZ() < 30000000;
    }

    public boolean isOutsideBuildHeight(BlockPos p_189509_1_)
    {
        return p_189509_1_.getY() < 0 || p_189509_1_.getY() >= 256;
    }

    /**
     * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
     * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
     */
    public boolean isAirBlock(BlockPos pos)
    {
        return this.getBlockState(pos).getBlock().isAir(this.getBlockState(pos), this, pos);
    }

    public boolean isBlockLoaded(BlockPos pos)
    {
        return this.isBlockLoaded(pos, true);
    }

    public boolean isBlockLoaded(BlockPos pos, boolean allowEmpty)
    {
        return this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, allowEmpty);
    }

    public boolean func_175697_a(BlockPos p_175697_1_, int p_175697_2_)
    {
        return this.isAreaLoaded(p_175697_1_, p_175697_2_, true);
    }

    public boolean isAreaLoaded(BlockPos center, int radius, boolean allowEmpty)
    {
        return this.isAreaLoaded(center.getX() - radius, center.getY() - radius, center.getZ() - radius, center.getX() + radius, center.getY() + radius, center.getZ() + radius, allowEmpty);
    }

    public boolean isAreaLoaded(BlockPos from, BlockPos to)
    {
        return this.isAreaLoaded(from, to, true);
    }

    public boolean isAreaLoaded(BlockPos from, BlockPos to, boolean allowEmpty)
    {
        return this.isAreaLoaded(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ(), allowEmpty);
    }

    public boolean isAreaLoaded(StructureBoundingBox box)
    {
        return this.isAreaLoaded(box, true);
    }

    public boolean isAreaLoaded(StructureBoundingBox box, boolean allowEmpty)
    {
        return this.isAreaLoaded(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, allowEmpty);
    }

    private boolean isAreaLoaded(int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, boolean allowEmpty)
    {
        if (yEnd >= 0 && yStart < 256)
        {
            xStart = xStart >> 4;
            zStart = zStart >> 4;
            xEnd = xEnd >> 4;
            zEnd = zEnd >> 4;

            for (int i = xStart; i <= xEnd; ++i)
            {
                for (int j = zStart; j <= zEnd; ++j)
                {
                    if (!this.isChunkLoaded(i, j, allowEmpty))
                    {
                        return false;
                    }
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    protected abstract boolean isChunkLoaded(int x, int z, boolean allowEmpty);

    public Chunk getChunk(BlockPos pos)
    {
        return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    /**
     * Gets the chunk at the specified location.
     */
    public Chunk getChunk(int chunkX, int chunkZ)
    {
        return this.chunkProvider.provideChunk(chunkX, chunkZ);
    }

    public boolean func_190526_b(int p_190526_1_, int p_190526_2_)
    {
        return this.isChunkLoaded(p_190526_1_, p_190526_2_, false) ? true : this.chunkProvider.isChunkGeneratedAt(p_190526_1_, p_190526_2_);
    }

    /**
     * Sets a block state into this world.Flags are as follows:
     * 1 will cause a block update.
     * 2 will send the change to clients.
     * 4 will prevent the block from being re-rendered.
     * 8 will force any re-renders to run on the main thread instead
     * 16 will prevent neighbor reactions (e.g. fences connecting, observers pulsing).
     * 32 will prevent neighbor reactions from spawning drops.
     * 64 will signify the block is being moved.
     * Flags can be OR-ed
     */
    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags)
    {
        if (this.isOutsideBuildHeight(pos))
        {
            return false;
        }
        else if (!this.isRemote && this.worldInfo.getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES)
        {
            return false;
        }
        else
        {
            Chunk chunk = this.getChunk(pos);

            pos = pos.toImmutable(); // Forge - prevent mutable BlockPos leaks
            net.minecraftforge.common.util.BlockSnapshot blockSnapshot = null;
            if (this.captureBlockSnapshots && !this.isRemote)
            {
                blockSnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(this, pos, flags);
                this.capturedBlockSnapshots.add(blockSnapshot);
            }
            IBlockState oldState = getBlockState(pos);
            int oldLight = oldState.getLightValue(this, pos);
            int oldOpacity = oldState.getLightOpacity(this, pos);

            IBlockState iblockstate = chunk.setBlockState(pos, newState);

            if (iblockstate == null)
            {
                if (blockSnapshot != null) this.capturedBlockSnapshots.remove(blockSnapshot);
                return false;
            }
            else
            {
                if (newState.getLightOpacity(this, pos) != oldOpacity || newState.getLightValue(this, pos) != oldLight)
                {
                    this.profiler.startSection("checkLight");
                    this.checkLight(pos);
                    this.profiler.endSection();
                }

                if (blockSnapshot == null) // Don't notify clients or update physics while capturing blockstates
                {
                    this.markAndNotifyBlock(pos, chunk, iblockstate, newState, flags);
                }
                return true;
            }
        }
    }

    // Split off from original setBlockState(BlockPos, IBlockState, int) method in order to directly send client and physic updates
    public void markAndNotifyBlock(BlockPos pos, @Nullable Chunk chunk, IBlockState iblockstate, IBlockState newState, int flags)
    {
        Block block = newState.getBlock();
        {
            {
                if ((flags & 2) != 0 && (!this.isRemote || (flags & 4) == 0) && (chunk == null || chunk.isPopulated()))
                {
                    this.notifyBlockUpdate(pos, iblockstate, newState, flags);
                }

                if (!this.isRemote && (flags & 1) != 0)
                {
                    this.func_175722_b(pos, iblockstate.getBlock(), true);

                    if (newState.hasComparatorInputOverride())
                    {
                        this.updateComparatorOutputLevel(pos, block);
                    }
                }
                else if (!this.isRemote && (flags & 16) == 0)
                {
                    this.func_190522_c(pos, block);
                }
            }
        }
    }

    public boolean removeBlock(BlockPos pos)
    {
        return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
    }

    /**
     * Sets a block to air, but also plays the sound and particles and can spawn drops
     */
    public boolean destroyBlock(BlockPos pos, boolean dropBlock)
    {
        IBlockState iblockstate = this.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (block.isAir(iblockstate, this, pos))
        {
            return false;
        }
        else
        {
            this.playEvent(2001, pos, Block.func_176210_f(iblockstate));

            if (dropBlock)
            {
                block.func_176226_b(this, pos, iblockstate, 0);
            }

            return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }

    /**
     * Convenience method to update the block on both the client and server
     */
    public boolean setBlockState(BlockPos pos, IBlockState state)
    {
        return this.setBlockState(pos, state, 3);
    }

    /**
     * Flags are as in setBlockState
     */
    public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
    {
        for (int i = 0; i < this.eventListeners.size(); ++i)
        {
            ((IWorldEventListener)this.eventListeners.get(i)).notifyBlockUpdate(this, pos, oldState, newState, flags);
        }
    }

    public void func_175722_b(BlockPos p_175722_1_, Block p_175722_2_, boolean p_175722_3_)
    {
        if (this.worldInfo.getTerrainType() != WorldType.DEBUG_ALL_BLOCK_STATES)
        {
            this.func_175685_c(p_175722_1_, p_175722_2_, p_175722_3_);
        }
    }

    /**
     * Marks a vertical column of blocks dirty, scheduling a render update.
     *  
     * Automatically swaps y1 and y2 if they are backwards.
     */
    public void markBlocksDirtyVertical(int x, int z, int y1, int y2)
    {
        if (y1 > y2)
        {
            int i = y2;
            y2 = y1;
            y1 = i;
        }

        if (this.dimension.hasSkyLight())
        {
            for (int j = y1; j <= y2; ++j)
            {
                this.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x, j, z));
            }
        }

        this.markBlockRangeForRenderUpdate(x, y1, z, x, y2, z);
    }

    public void markBlockRangeForRenderUpdate(BlockPos rangeMin, BlockPos rangeMax)
    {
        this.markBlockRangeForRenderUpdate(rangeMin.getX(), rangeMin.getY(), rangeMin.getZ(), rangeMax.getX(), rangeMax.getY(), rangeMax.getZ());
    }

    /**
     * Notifies all listening IWorldEventListeners of an update within the given bounds.
     */
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        for (int i = 0; i < this.eventListeners.size(); ++i)
        {
            ((IWorldEventListener)this.eventListeners.get(i)).markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
        }
    }

    public void func_190522_c(BlockPos p_190522_1_, Block p_190522_2_)
    {
        this.func_190529_b(p_190522_1_.west(), p_190522_2_, p_190522_1_);
        this.func_190529_b(p_190522_1_.east(), p_190522_2_, p_190522_1_);
        this.func_190529_b(p_190522_1_.down(), p_190522_2_, p_190522_1_);
        this.func_190529_b(p_190522_1_.up(), p_190522_2_, p_190522_1_);
        this.func_190529_b(p_190522_1_.north(), p_190522_2_, p_190522_1_);
        this.func_190529_b(p_190522_1_.south(), p_190522_2_, p_190522_1_);
    }

    public void func_175685_c(BlockPos p_175685_1_, Block p_175685_2_, boolean p_175685_3_)
    {
        if(net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(this, p_175685_1_, this.getBlockState(p_175685_1_), java.util.EnumSet.allOf(EnumFacing.class), p_175685_3_).isCanceled())
            return;

        this.neighborChanged(p_175685_1_.west(), p_175685_2_, p_175685_1_);
        this.neighborChanged(p_175685_1_.east(), p_175685_2_, p_175685_1_);
        this.neighborChanged(p_175685_1_.down(), p_175685_2_, p_175685_1_);
        this.neighborChanged(p_175685_1_.up(), p_175685_2_, p_175685_1_);
        this.neighborChanged(p_175685_1_.north(), p_175685_2_, p_175685_1_);
        this.neighborChanged(p_175685_1_.south(), p_175685_2_, p_175685_1_);

        if (p_175685_3_)
        {
            this.func_190522_c(p_175685_1_, p_175685_2_);
        }
    }

    public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, EnumFacing skipSide)
    {
        java.util.EnumSet<EnumFacing> directions = java.util.EnumSet.allOf(EnumFacing.class);
        directions.remove(skipSide);
        if(net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(this, pos, this.getBlockState(pos), directions, false).isCanceled())
            return;

        if (skipSide != EnumFacing.WEST)
        {
            this.neighborChanged(pos.west(), blockType, pos);
        }

        if (skipSide != EnumFacing.EAST)
        {
            this.neighborChanged(pos.east(), blockType, pos);
        }

        if (skipSide != EnumFacing.DOWN)
        {
            this.neighborChanged(pos.down(), blockType, pos);
        }

        if (skipSide != EnumFacing.UP)
        {
            this.neighborChanged(pos.up(), blockType, pos);
        }

        if (skipSide != EnumFacing.NORTH)
        {
            this.neighborChanged(pos.north(), blockType, pos);
        }

        if (skipSide != EnumFacing.SOUTH)
        {
            this.neighborChanged(pos.south(), blockType, pos);
        }
    }

    public void neighborChanged(BlockPos pos, final Block blockIn, BlockPos fromPos)
    {
        if (!this.isRemote)
        {
            IBlockState iblockstate = this.getBlockState(pos);

            try
            {
                iblockstate.neighborChanged(this, pos, blockIn, fromPos);
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while updating neighbours");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");
                crashreportcategory.addDetail("Source block type", new ICrashReportDetail<String>()
                {
                    public String call() throws Exception
                    {
                        try
                        {
                            return String.format("ID #%d (%s // %s // %s)", Block.func_149682_b(blockIn), blockIn.getTranslationKey(), blockIn.getClass().getName(), blockIn.getRegistryName());
                        }
                        catch (Throwable var2)
                        {
                            return "ID #" + Block.func_149682_b(blockIn);
                        }
                    }
                });
                CrashReportCategory.addBlockInfo(crashreportcategory, pos, iblockstate);
                throw new ReportedException(crashreport);
            }
        }
    }

    public void func_190529_b(BlockPos p_190529_1_, final Block p_190529_2_, BlockPos p_190529_3_)
    {
        if (!this.isRemote)
        {
            IBlockState iblockstate = this.getBlockState(p_190529_1_);

            if (true)
            {
                try
                {
                    iblockstate.getBlock().observedNeighborChange(iblockstate, this, p_190529_1_, p_190529_2_, p_190529_3_);
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while updating neighbours");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");
                    crashreportcategory.addDetail("Source block type", new ICrashReportDetail<String>()
                    {
                        public String call() throws Exception
                        {
                            try
                            {
                                return String.format("ID #%d (%s // %s // %s)", Block.func_149682_b(p_190529_2_), p_190529_2_.getTranslationKey(), p_190529_2_.getClass().getName(), p_190529_2_.getRegistryName());
                            }
                            catch (Throwable var2)
                            {
                                return "ID #" + Block.func_149682_b(p_190529_2_);
                            }
                        }
                    });
                    CrashReportCategory.addBlockInfo(crashreportcategory, p_190529_1_, iblockstate);
                    throw new ReportedException(crashreport);
                }
            }
        }
    }

    public boolean func_175691_a(BlockPos p_175691_1_, Block p_175691_2_)
    {
        return false;
    }

    public boolean canSeeSky(BlockPos pos)
    {
        return this.getChunk(pos).canSeeSky(pos);
    }

    public boolean canBlockSeeSky(BlockPos pos)
    {
        if (pos.getY() >= this.getSeaLevel())
        {
            return this.canSeeSky(pos);
        }
        else
        {
            BlockPos blockpos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());

            if (!this.canSeeSky(blockpos))
            {
                return false;
            }
            else
            {
                for (BlockPos blockpos1 = blockpos.down(); blockpos1.getY() > pos.getY(); blockpos1 = blockpos1.down())
                {
                    IBlockState iblockstate = this.getBlockState(blockpos1);

                    if (iblockstate.getBlock().getLightOpacity(iblockstate, this, blockpos) > 0 && !iblockstate.getMaterial().isLiquid())
                    {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public int func_175699_k(BlockPos p_175699_1_)
    {
        if (p_175699_1_.getY() < 0)
        {
            return 0;
        }
        else
        {
            if (p_175699_1_.getY() >= 256)
            {
                p_175699_1_ = new BlockPos(p_175699_1_.getX(), 255, p_175699_1_.getZ());
            }

            return this.getChunk(p_175699_1_).getLightSubtracted(p_175699_1_, 0);
        }
    }

    public int func_175671_l(BlockPos p_175671_1_)
    {
        return this.func_175721_c(p_175671_1_, true);
    }

    public int func_175721_c(BlockPos p_175721_1_, boolean p_175721_2_)
    {
        if (p_175721_1_.getX() >= -30000000 && p_175721_1_.getZ() >= -30000000 && p_175721_1_.getX() < 30000000 && p_175721_1_.getZ() < 30000000)
        {
            if (p_175721_2_ && this.getBlockState(p_175721_1_).func_185916_f())
            {
                int i1 = this.func_175721_c(p_175721_1_.up(), false);
                int i = this.func_175721_c(p_175721_1_.east(), false);
                int j = this.func_175721_c(p_175721_1_.west(), false);
                int k = this.func_175721_c(p_175721_1_.south(), false);
                int l = this.func_175721_c(p_175721_1_.north(), false);

                if (i > i1)
                {
                    i1 = i;
                }

                if (j > i1)
                {
                    i1 = j;
                }

                if (k > i1)
                {
                    i1 = k;
                }

                if (l > i1)
                {
                    i1 = l;
                }

                return i1;
            }
            else if (p_175721_1_.getY() < 0)
            {
                return 0;
            }
            else
            {
                if (p_175721_1_.getY() >= 256)
                {
                    p_175721_1_ = new BlockPos(p_175721_1_.getX(), 255, p_175721_1_.getZ());
                }

                Chunk chunk = this.getChunk(p_175721_1_);
                return chunk.getLightSubtracted(p_175721_1_, this.skylightSubtracted);
            }
        }
        else
        {
            return 15;
        }
    }

    public BlockPos func_175645_m(BlockPos p_175645_1_)
    {
        return new BlockPos(p_175645_1_.getX(), this.func_189649_b(p_175645_1_.getX(), p_175645_1_.getZ()), p_175645_1_.getZ());
    }

    public int func_189649_b(int p_189649_1_, int p_189649_2_)
    {
        int i;

        if (p_189649_1_ >= -30000000 && p_189649_2_ >= -30000000 && p_189649_1_ < 30000000 && p_189649_2_ < 30000000)
        {
            if (this.isChunkLoaded(p_189649_1_ >> 4, p_189649_2_ >> 4, true))
            {
                i = this.getChunk(p_189649_1_ >> 4, p_189649_2_ >> 4).func_76611_b(p_189649_1_ & 15, p_189649_2_ & 15);
            }
            else
            {
                i = 0;
            }
        }
        else
        {
            i = this.getSeaLevel() + 1;
        }

        return i;
    }

    /**
     * Gets the lowest height of the chunk where sunlight directly reaches
     */
    @Deprecated
    public int getChunksLowestHorizon(int x, int z)
    {
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000)
        {
            if (!this.isChunkLoaded(x >> 4, z >> 4, true))
            {
                return 0;
            }
            else
            {
                Chunk chunk = this.getChunk(x >> 4, z >> 4);
                return chunk.getLowestHeight();
            }
        }
        else
        {
            return this.getSeaLevel() + 1;
        }
    }

    @SideOnly(Side.CLIENT)
    public int getLightFromNeighborsFor(EnumSkyBlock type, BlockPos pos)
    {
        if (!this.dimension.hasSkyLight() && type == EnumSkyBlock.SKY)
        {
            return 0;
        }
        else
        {
            if (pos.getY() < 0)
            {
                pos = new BlockPos(pos.getX(), 0, pos.getZ());
            }

            if (!this.isValid(pos))
            {
                return type.defaultLightValue;
            }
            else if (!this.isBlockLoaded(pos))
            {
                return type.defaultLightValue;
            }
            else if (this.getBlockState(pos).func_185916_f())
            {
                int i1 = this.getLightFor(type, pos.up());
                int i = this.getLightFor(type, pos.east());
                int j = this.getLightFor(type, pos.west());
                int k = this.getLightFor(type, pos.south());
                int l = this.getLightFor(type, pos.north());

                if (i > i1)
                {
                    i1 = i;
                }

                if (j > i1)
                {
                    i1 = j;
                }

                if (k > i1)
                {
                    i1 = k;
                }

                if (l > i1)
                {
                    i1 = l;
                }

                return i1;
            }
            else
            {
                Chunk chunk = this.getChunk(pos);
                return chunk.getLightFor(type, pos);
            }
        }
    }

    public int getLightFor(EnumSkyBlock type, BlockPos pos)
    {
        if (pos.getY() < 0)
        {
            pos = new BlockPos(pos.getX(), 0, pos.getZ());
        }

        if (!this.isValid(pos))
        {
            return type.defaultLightValue;
        }
        else if (!this.isBlockLoaded(pos))
        {
            return type.defaultLightValue;
        }
        else
        {
            Chunk chunk = this.getChunk(pos);
            return chunk.getLightFor(type, pos);
        }
    }

    public void setLightFor(EnumSkyBlock type, BlockPos pos, int lightValue)
    {
        if (this.isValid(pos))
        {
            if (this.isBlockLoaded(pos))
            {
                Chunk chunk = this.getChunk(pos);
                chunk.setLightFor(type, pos, lightValue);
                this.notifyLightSet(pos);
            }
        }
    }

    public void notifyLightSet(BlockPos pos)
    {
        for (int i = 0; i < this.eventListeners.size(); ++i)
        {
            ((IWorldEventListener)this.eventListeners.get(i)).notifyLightSet(pos);
        }
    }

    @SideOnly(Side.CLIENT)
    public int getCombinedLight(BlockPos pos, int lightValue)
    {
        int i = this.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos);
        int j = this.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos);

        if (j < lightValue)
        {
            j = lightValue;
        }

        return i << 20 | j << 4;
    }

    public float func_175724_o(BlockPos p_175724_1_)
    {
        return this.dimension.getLightBrightnessTable()[this.func_175671_l(p_175724_1_)];
    }

    public IBlockState getBlockState(BlockPos pos)
    {
        if (this.isOutsideBuildHeight(pos))
        {
            return Blocks.AIR.getDefaultState();
        }
        else
        {
            Chunk chunk = this.getChunk(pos);
            return chunk.func_177435_g(pos);
        }
    }

    /**
     * Checks whether its daytime by seeing if the light subtracted from the skylight is less than 4
     */
    public boolean isDaytime()
    {
        return this.dimension.isDaytime();
    }

    /**
     * ray traces all blocks, including non-collideable ones
     */
    @Nullable
    public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end)
    {
        return this.func_147447_a(start, end, false, false, false);
    }

    @Nullable
    public RayTraceResult func_72901_a(Vec3d p_72901_1_, Vec3d p_72901_2_, boolean p_72901_3_)
    {
        return this.func_147447_a(p_72901_1_, p_72901_2_, p_72901_3_, false, false);
    }

    @Nullable
    public RayTraceResult func_147447_a(Vec3d p_147447_1_, Vec3d p_147447_2_, boolean p_147447_3_, boolean p_147447_4_, boolean p_147447_5_)
    {
        if (!Double.isNaN(p_147447_1_.x) && !Double.isNaN(p_147447_1_.y) && !Double.isNaN(p_147447_1_.z))
        {
            if (!Double.isNaN(p_147447_2_.x) && !Double.isNaN(p_147447_2_.y) && !Double.isNaN(p_147447_2_.z))
            {
                int i = MathHelper.floor(p_147447_2_.x);
                int j = MathHelper.floor(p_147447_2_.y);
                int k = MathHelper.floor(p_147447_2_.z);
                int l = MathHelper.floor(p_147447_1_.x);
                int i1 = MathHelper.floor(p_147447_1_.y);
                int j1 = MathHelper.floor(p_147447_1_.z);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                IBlockState iblockstate = this.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if ((!p_147447_4_ || iblockstate.func_185890_d(this, blockpos) != Block.field_185506_k) && block.func_176209_a(iblockstate, p_147447_3_))
                {
                    RayTraceResult raytraceresult = iblockstate.func_185910_a(this, blockpos, p_147447_1_, p_147447_2_);

                    if (raytraceresult != null)
                    {
                        return raytraceresult;
                    }
                }

                RayTraceResult raytraceresult2 = null;
                int k1 = 200;

                while (k1-- >= 0)
                {
                    if (Double.isNaN(p_147447_1_.x) || Double.isNaN(p_147447_1_.y) || Double.isNaN(p_147447_1_.z))
                    {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k)
                    {
                        return p_147447_5_ ? raytraceresult2 : null;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l)
                    {
                        d0 = (double)l + 1.0D;
                    }
                    else if (i < l)
                    {
                        d0 = (double)l + 0.0D;
                    }
                    else
                    {
                        flag2 = false;
                    }

                    if (j > i1)
                    {
                        d1 = (double)i1 + 1.0D;
                    }
                    else if (j < i1)
                    {
                        d1 = (double)i1 + 0.0D;
                    }
                    else
                    {
                        flag = false;
                    }

                    if (k > j1)
                    {
                        d2 = (double)j1 + 1.0D;
                    }
                    else if (k < j1)
                    {
                        d2 = (double)j1 + 0.0D;
                    }
                    else
                    {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = p_147447_2_.x - p_147447_1_.x;
                    double d7 = p_147447_2_.y - p_147447_1_.y;
                    double d8 = p_147447_2_.z - p_147447_1_.z;

                    if (flag2)
                    {
                        d3 = (d0 - p_147447_1_.x) / d6;
                    }

                    if (flag)
                    {
                        d4 = (d1 - p_147447_1_.y) / d7;
                    }

                    if (flag1)
                    {
                        d5 = (d2 - p_147447_1_.z) / d8;
                    }

                    if (d3 == -0.0D)
                    {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D)
                    {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D)
                    {
                        d5 = -1.0E-4D;
                    }

                    EnumFacing enumfacing;

                    if (d3 < d4 && d3 < d5)
                    {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        p_147447_1_ = new Vec3d(d0, p_147447_1_.y + d7 * d3, p_147447_1_.z + d8 * d3);
                    }
                    else if (d4 < d5)
                    {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        p_147447_1_ = new Vec3d(p_147447_1_.x + d6 * d4, d1, p_147447_1_.z + d8 * d4);
                    }
                    else
                    {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        p_147447_1_ = new Vec3d(p_147447_1_.x + d6 * d5, p_147447_1_.y + d7 * d5, d2);
                    }

                    l = MathHelper.floor(p_147447_1_.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor(p_147447_1_.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor(p_147447_1_.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    IBlockState iblockstate1 = this.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();

                    if (!p_147447_4_ || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.func_185890_d(this, blockpos) != Block.field_185506_k)
                    {
                        if (block1.func_176209_a(iblockstate1, p_147447_3_))
                        {
                            RayTraceResult raytraceresult1 = iblockstate1.func_185910_a(this, blockpos, p_147447_1_, p_147447_2_);

                            if (raytraceresult1 != null)
                            {
                                return raytraceresult1;
                            }
                        }
                        else
                        {
                            raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, p_147447_1_, enumfacing, blockpos);
                        }
                    }
                }

                return p_147447_5_ ? raytraceresult2 : null;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Plays the specified sound for a player at the center of the given block position.
     */
    public void playSound(@Nullable EntityPlayer player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch)
    {
        this.playSound(player, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, soundIn, category, volume, pitch);
    }

    public void playSound(@Nullable EntityPlayer player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch)
    {
        net.minecraftforge.event.entity.PlaySoundAtEntityEvent event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtEntity(player, soundIn, category, volume, pitch);
        if (event.isCanceled() || event.getSound() == null) return;
        soundIn = event.getSound();
        category = event.getCategory();
        volume = event.getVolume();
        pitch = event.getPitch();

        for (int i = 0; i < this.eventListeners.size(); ++i)
        {
            ((IWorldEventListener)this.eventListeners.get(i)).playSoundToAllNearExcept(player, soundIn, category, x, y, z, volume, pitch);
        }
    }

    public void playSound(double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay)
    {
    }

    public void playRecord(BlockPos pos, @Nullable SoundEvent soundEventIn)
    {
        for (int i = 0; i < this.eventListeners.size(); ++i)
        {
            ((IWorldEventListener)this.eventListeners.get(i)).playRecord(soundEventIn, pos);
        }
    }

    public void func_175688_a(EnumParticleTypes p_175688_1_, double p_175688_2_, double p_175688_4_, double p_175688_6_, double p_175688_8_, double p_175688_10_, double p_175688_12_, int... p_175688_14_)
    {
        this.func_175720_a(p_175688_1_.func_179348_c(), p_175688_1_.func_179344_e(), p_175688_2_, p_175688_4_, p_175688_6_, p_175688_8_, p_175688_10_, p_175688_12_, p_175688_14_);
    }

    public void func_190523_a(int p_190523_1_, double p_190523_2_, double p_190523_4_, double p_190523_6_, double p_190523_8_, double p_190523_10_, double p_190523_12_, int... p_190523_14_)
    {
        for (int i = 0; i < this.eventListeners.size(); ++i)
        {
            ((IWorldEventListener)this.eventListeners.get(i)).func_190570_a(p_190523_1_, false, true, p_190523_2_, p_190523_4_, p_190523_6_, p_190523_8_, p_190523_10_, p_190523_12_, p_190523_14_);
        }
    }

    @SideOnly(Side.CLIENT)
    public void func_175682_a(EnumParticleTypes p_175682_1_, boolean p_175682_2_, double p_175682_3_, double p_175682_5_, double p_175682_7_, double p_175682_9_, double p_175682_11_, double p_175682_13_, int... p_175682_15_)
    {
        this.func_175720_a(p_175682_1_.func_179348_c(), p_175682_1_.func_179344_e() || p_175682_2_, p_175682_3_, p_175682_5_, p_175682_7_, p_175682_9_, p_175682_11_, p_175682_13_, p_175682_15_);
    }

    private void func_175720_a(int p_175720_1_, boolean p_175720_2_, double p_175720_3_, double p_175720_5_, double p_175720_7_, double p_175720_9_, double p_175720_11_, double p_175720_13_, int... p_175720_15_)
    {
        for (int i = 0; i < this.eventListeners.size(); ++i)
        {
            ((IWorldEventListener)this.eventListeners.get(i)).func_180442_a(p_175720_1_, p_175720_2_, p_175720_3_, p_175720_5_, p_175720_7_, p_175720_9_, p_175720_11_, p_175720_13_, p_175720_15_);
        }
    }

    /**
     * adds a lightning bolt to the list of lightning bolts in this world.
     */
    public boolean addWeatherEffect(Entity entityIn)
    {
        this.weatherEffects.add(entityIn);
        return true;
    }

    /**
     * Called when an entity is spawned in the world. This includes players.
     */
    public boolean spawnEntity(Entity entityIn)
    {
        // do not drop any items while restoring blocksnapshots. Prevents dupes
        if (!this.isRemote && (entityIn == null || (entityIn instanceof net.minecraft.entity.item.EntityItem && this.restoringBlockSnapshots))) return false;

        int i = MathHelper.floor(entityIn.posX / 16.0D);
        int j = MathHelper.floor(entityIn.posZ / 16.0D);
        boolean flag = entityIn.forceSpawn;

        if (entityIn instanceof EntityPlayer)
        {
            flag = true;
        }

        if (!flag && !this.isChunkLoaded(i, j, false))
        {
            return false;
        }
        else
        {
            if (entityIn instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)entityIn;
                this.playerEntities.add(entityplayer);
                this.updateAllPlayersSleepingFlag();
            }

            if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinWorldEvent(entityIn, this)) && !flag) return false;

            this.getChunk(i, j).addEntity(entityIn);
            this.loadedEntityList.add(entityIn);
            this.onEntityAdded(entityIn);
            return true;
        }
    }

    public void onEntityAdded(Entity entityIn)
    {
        for (int i = 0; i < this.eventListeners.size(); ++i)
        {
            ((IWorldEventListener)this.eventListeners.get(i)).onEntityAdded(entityIn);
        }
        entityIn.onAddedToWorld();
    }

    public void onEntityRemoved(Entity entityIn)
    {
        for (int i = 0; i < this.eventListeners.size(); ++i)
        {
            ((IWorldEventListener)this.eventListeners.get(i)).onEntityRemoved(entityIn);
        }
        entityIn.onRemovedFromWorld();
    }

    /**
     * Schedule the entity for removal during the next tick. Marks the entity dead in anticipation.
     */
    public void removeEntity(Entity entityIn)
    {
        if (entityIn.isBeingRidden())
        {
            entityIn.removePassengers();
        }

        if (entityIn.isPassenger())
        {
            entityIn.stopRiding();
        }

        entityIn.remove();

        if (entityIn instanceof EntityPlayer)
        {
            this.playerEntities.remove(entityIn);
            this.updateAllPlayersSleepingFlag();
            this.onEntityRemoved(entityIn);
        }
    }

    /**
     * Do NOT use this method to remove normal entities- use normal removeEntity
     */
    public void removeEntityDangerously(Entity entityIn)
    {
        entityIn.setDropItemsWhenDead(false);
        entityIn.remove();

        if (entityIn instanceof EntityPlayer)
        {
            this.playerEntities.remove(entityIn);
            this.updateAllPlayersSleepingFlag();
        }

        int i = entityIn.chunkCoordX;
        int j = entityIn.chunkCoordZ;

        if (entityIn.addedToChunk && this.isChunkLoaded(i, j, true))
        {
            this.getChunk(i, j).removeEntity(entityIn);
        }

        this.loadedEntityList.remove(entityIn);
        this.onEntityRemoved(entityIn);
    }

    /**
     * Add a world event listener
     */
    public void addEventListener(IWorldEventListener listener)
    {
        this.eventListeners.add(listener);
    }

    private boolean func_191504_a(@Nullable Entity p_191504_1_, AxisAlignedBB p_191504_2_, boolean p_191504_3_, @Nullable List<AxisAlignedBB> p_191504_4_)
    {
        int i = MathHelper.floor(p_191504_2_.minX) - 1;
        int j = MathHelper.ceil(p_191504_2_.maxX) + 1;
        int k = MathHelper.floor(p_191504_2_.minY) - 1;
        int l = MathHelper.ceil(p_191504_2_.maxY) + 1;
        int i1 = MathHelper.floor(p_191504_2_.minZ) - 1;
        int j1 = MathHelper.ceil(p_191504_2_.maxZ) + 1;
        WorldBorder worldborder = this.getWorldBorder();
        boolean flag = p_191504_1_ != null && p_191504_1_.isOutsideBorder();
        boolean flag1 = p_191504_1_ != null && this.isInsideWorldBorder(p_191504_1_);
        IBlockState iblockstate = Blocks.STONE.getDefaultState();
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

        if (p_191504_3_ && !net.minecraftforge.event.ForgeEventFactory.gatherCollisionBoxes(this, p_191504_1_, p_191504_2_, p_191504_4_)) return true;
        try
        {
            for (int k1 = i; k1 < j; ++k1)
            {
                for (int l1 = i1; l1 < j1; ++l1)
                {
                    boolean flag2 = k1 == i || k1 == j - 1;
                    boolean flag3 = l1 == i1 || l1 == j1 - 1;

                    if ((!flag2 || !flag3) && this.isBlockLoaded(blockpos$pooledmutableblockpos.setPos(k1, 64, l1)))
                    {
                        for (int i2 = k; i2 < l; ++i2)
                        {
                            if (!flag2 && !flag3 || i2 != l - 1)
                            {
                                if (p_191504_3_)
                                {
                                    if (k1 < -30000000 || k1 >= 30000000 || l1 < -30000000 || l1 >= 30000000)
                                    {
                                        boolean lvt_21_2_ = true;
                                        return lvt_21_2_;
                                    }
                                }
                                else if (p_191504_1_ != null && flag == flag1)
                                {
                                    p_191504_1_.setOutsideBorder(!flag1);
                                }

                                blockpos$pooledmutableblockpos.setPos(k1, i2, l1);
                                IBlockState iblockstate1;

                                if (!p_191504_3_ && !worldborder.contains(blockpos$pooledmutableblockpos) && flag1)
                                {
                                    iblockstate1 = iblockstate;
                                }
                                else
                                {
                                    iblockstate1 = this.getBlockState(blockpos$pooledmutableblockpos);
                                }

                                iblockstate1.func_185908_a(this, blockpos$pooledmutableblockpos, p_191504_2_, p_191504_4_, p_191504_1_, false);

                                if (p_191504_3_ && !net.minecraftforge.event.ForgeEventFactory.gatherCollisionBoxes(this, p_191504_1_, p_191504_2_, p_191504_4_))
                                {
                                    boolean flag5 = true;
                                    return flag5;
                                }
                            }
                        }
                    }
                }
            }
        }
        finally
        {
            blockpos$pooledmutableblockpos.func_185344_t();
        }

        return !p_191504_4_.isEmpty();
    }

    public List<AxisAlignedBB> func_184144_a(@Nullable Entity p_184144_1_, AxisAlignedBB p_184144_2_)
    {
        List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
        this.func_191504_a(p_184144_1_, p_184144_2_, false, list);

        if (p_184144_1_ != null)
        {
            List<Entity> list1 = this.getEntitiesWithinAABBExcludingEntity(p_184144_1_, p_184144_2_.grow(0.25D));

            for (int i = 0; i < list1.size(); ++i)
            {
                Entity entity = list1.get(i);

                if (!p_184144_1_.isRidingSameEntity(entity))
                {
                    AxisAlignedBB axisalignedbb = entity.getCollisionBoundingBox();

                    if (axisalignedbb != null && axisalignedbb.intersects(p_184144_2_))
                    {
                        list.add(axisalignedbb);
                    }

                    axisalignedbb = p_184144_1_.getCollisionBox(entity);

                    if (axisalignedbb != null && axisalignedbb.intersects(p_184144_2_))
                    {
                        list.add(axisalignedbb);
                    }
                }
            }
        }
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.GetCollisionBoxesEvent(this, p_184144_1_, p_184144_2_, list));
        return list;
    }

    /**
     * Remove a world event listener
     */
    public void removeEventListener(IWorldEventListener listener)
    {
        this.eventListeners.remove(listener);
    }

    public boolean isInsideWorldBorder(Entity entityToCheck)
    {
        double d0 = this.worldBorder.minX();
        double d1 = this.worldBorder.minZ();
        double d2 = this.worldBorder.maxX();
        double d3 = this.worldBorder.maxZ();

        if (entityToCheck.isOutsideBorder())
        {
            ++d0;
            ++d1;
            --d2;
            --d3;
        }
        else
        {
            --d0;
            --d1;
            ++d2;
            ++d3;
        }

        return entityToCheck.posX > d0 && entityToCheck.posX < d2 && entityToCheck.posZ > d1 && entityToCheck.posZ < d3;
    }

    public boolean func_184143_b(AxisAlignedBB p_184143_1_)
    {
        return this.func_191504_a((Entity)null, p_184143_1_, true, Lists.newArrayList());
    }

    /**
     * Returns the amount of skylight subtracted for the current time
     */
    public int calculateSkylightSubtracted(float partialTicks)
    {
        float f = dimension.getSunBrightnessFactor(partialTicks);
        f = 1 - f;
        return (int)(f * 11);
    }

    /**
     * The current sun brightness factor for this dimension.
     * 0.0f means no light at all, and 1.0f means maximum sunlight.
     * Highly recommended for sunlight detection like solar panel.
     *
     * @return The current brightness factor
     * */
    public float getSunBrightnessFactor(float partialTicks)
    {
        float f = this.getCelestialAngle(partialTicks);
        float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F);
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        f1 = 1.0F - f1;
        f1 = (float)((double)f1 * (1.0D - (double)(this.getRainStrength(partialTicks) * 5.0F) / 16.0D));
        f1 = (float)((double)f1 * (1.0D - (double)(this.getThunderStrength(partialTicks) * 5.0F) / 16.0D));
        return f1;
    }

    /**
     * Returns the sun brightness - checks time of day, rain and thunder
     */
    @SideOnly(Side.CLIENT)
    public float getSunBrightness(float partialTicks)
    {
        return this.dimension.getSunBrightness(partialTicks);
    }

    @SideOnly(Side.CLIENT)
    public float getSunBrightnessBody(float partialTicks)
    {
        float f = this.getCelestialAngle(partialTicks);
        float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.2F);
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        f1 = 1.0F - f1;
        f1 = (float)((double)f1 * (1.0D - (double)(this.getRainStrength(partialTicks) * 5.0F) / 16.0D));
        f1 = (float)((double)f1 * (1.0D - (double)(this.getThunderStrength(partialTicks) * 5.0F) / 16.0D));
        return f1 * 0.8F + 0.2F;
    }

    /**
     * Calculates the color for the skybox
     */
    @SideOnly(Side.CLIENT)
    public Vec3d getSkyColor(Entity entityIn, float partialTicks)
    {
        return this.dimension.getSkyColor(entityIn, partialTicks);
    }

    @SideOnly(Side.CLIENT)
    public Vec3d getSkyColorBody(Entity entityIn, float partialTicks)
    {
        float f = this.getCelestialAngle(partialTicks);
        float f1 = MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        int i = MathHelper.floor(entityIn.posX);
        int j = MathHelper.floor(entityIn.posY);
        int k = MathHelper.floor(entityIn.posZ);
        BlockPos blockpos = new BlockPos(i, j, k);
        int l = net.minecraftforge.client.ForgeHooksClient.getSkyBlendColour(this, blockpos);
        float f3 = (float)(l >> 16 & 255) / 255.0F;
        float f4 = (float)(l >> 8 & 255) / 255.0F;
        float f5 = (float)(l & 255) / 255.0F;
        f3 = f3 * f1;
        f4 = f4 * f1;
        f5 = f5 * f1;
        float f6 = this.getRainStrength(partialTicks);

        if (f6 > 0.0F)
        {
            float f7 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.6F;
            float f8 = 1.0F - f6 * 0.75F;
            f3 = f3 * f8 + f7 * (1.0F - f8);
            f4 = f4 * f8 + f7 * (1.0F - f8);
            f5 = f5 * f8 + f7 * (1.0F - f8);
        }

        float f10 = this.getThunderStrength(partialTicks);

        if (f10 > 0.0F)
        {
            float f11 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.2F;
            float f9 = 1.0F - f10 * 0.75F;
            f3 = f3 * f9 + f11 * (1.0F - f9);
            f4 = f4 * f9 + f11 * (1.0F - f9);
            f5 = f5 * f9 + f11 * (1.0F - f9);
        }

        if (this.lastLightningBolt > 0)
        {
            float f12 = (float)this.lastLightningBolt - partialTicks;

            if (f12 > 1.0F)
            {
                f12 = 1.0F;
            }

            f12 = f12 * 0.45F;
            f3 = f3 * (1.0F - f12) + 0.8F * f12;
            f4 = f4 * (1.0F - f12) + 0.8F * f12;
            f5 = f5 * (1.0F - f12) + 1.0F * f12;
        }

        return new Vec3d((double)f3, (double)f4, (double)f5);
    }

    /**
     * calls calculateCelestialAngle
     */
    public float getCelestialAngle(float partialTicks)
    {
        return this.dimension.calculateCelestialAngle(this.getDayTime(), partialTicks);
    }

    @SideOnly(Side.CLIENT)
    public int getMoonPhase()
    {
        return this.dimension.getMoonPhase(this.getDayTime());
    }

    /**
     * gets the current fullness of the moon expressed as a float between 1.0 and 0.0, in steps of .25
     */
    public float getCurrentMoonPhaseFactor()
    {
        return dimension.getCurrentMoonPhaseFactor();
    }

    public float getCurrentMoonPhaseFactorBody()
    {
        return WorldProvider.MOON_PHASE_FACTORS[this.dimension.getMoonPhase(this.getDayTime())];
    }

    /**
     * Return getCelestialAngle()*2*PI
     */
    public float getCelestialAngleRadians(float partialTicks)
    {
        float f = this.getCelestialAngle(partialTicks);
        return f * ((float)Math.PI * 2F);
    }

    @SideOnly(Side.CLIENT)
    public Vec3d getCloudColour(float partialTicks)
    {
        return this.dimension.getCloudColor(partialTicks);
    }

    @SideOnly(Side.CLIENT)
    public Vec3d getCloudColorBody(float partialTicks)
    {
        float f = this.getCelestialAngle(partialTicks);
        float f1 = MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        float f2 = 1.0F;
        float f3 = 1.0F;
        float f4 = 1.0F;
        float f5 = this.getRainStrength(partialTicks);

        if (f5 > 0.0F)
        {
            float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
            float f7 = 1.0F - f5 * 0.95F;
            f2 = f2 * f7 + f6 * (1.0F - f7);
            f3 = f3 * f7 + f6 * (1.0F - f7);
            f4 = f4 * f7 + f6 * (1.0F - f7);
        }

        f2 = f2 * (f1 * 0.9F + 0.1F);
        f3 = f3 * (f1 * 0.9F + 0.1F);
        f4 = f4 * (f1 * 0.85F + 0.15F);
        float f9 = this.getThunderStrength(partialTicks);

        if (f9 > 0.0F)
        {
            float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
            float f8 = 1.0F - f9 * 0.95F;
            f2 = f2 * f8 + f10 * (1.0F - f8);
            f3 = f3 * f8 + f10 * (1.0F - f8);
            f4 = f4 * f8 + f10 * (1.0F - f8);
        }

        return new Vec3d((double)f2, (double)f3, (double)f4);
    }

    /**
     * Returns vector(ish) with R/G/B for fog
     */
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float partialTicks)
    {
        float f = this.getCelestialAngle(partialTicks);
        return this.dimension.getFogColor(f, partialTicks);
    }

    public BlockPos func_175725_q(BlockPos p_175725_1_)
    {
        return this.getChunk(p_175725_1_).func_177440_h(p_175725_1_);
    }

    public BlockPos func_175672_r(BlockPos p_175672_1_)
    {
        Chunk chunk = this.getChunk(p_175672_1_);
        BlockPos blockpos;
        BlockPos blockpos1;

        for (blockpos = new BlockPos(p_175672_1_.getX(), chunk.getTopFilledSegment() + 16, p_175672_1_.getZ()); blockpos.getY() >= 0; blockpos = blockpos1)
        {
            blockpos1 = blockpos.down();
            IBlockState state = chunk.func_177435_g(blockpos1);

            if (state.getMaterial().blocksMovement() && !state.getBlock().isLeaves(state, this, blockpos1) && !state.getBlock().isFoliage(this, blockpos1))
            {
                break;
            }
        }

        return blockpos;
    }

    /**
     * How bright are stars in the sky
     */
    @SideOnly(Side.CLIENT)
    public float getStarBrightness(float partialTicks)
    {
        return this.dimension.getStarBrightness(partialTicks);
    }

    @SideOnly(Side.CLIENT)
    public float getStarBrightnessBody(float partialTicks)
    {
        float f = this.getCelestialAngle(partialTicks);
        float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.25F);
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        return f1 * f1 * 0.5F;
    }

    public boolean func_184145_b(BlockPos p_184145_1_, Block p_184145_2_)
    {
        return true;
    }

    public void func_175684_a(BlockPos p_175684_1_, Block p_175684_2_, int p_175684_3_)
    {
    }

    public void func_175654_a(BlockPos p_175654_1_, Block p_175654_2_, int p_175654_3_, int p_175654_4_)
    {
    }

    public void func_180497_b(BlockPos p_180497_1_, Block p_180497_2_, int p_180497_3_, int p_180497_4_)
    {
    }

    /**
     * Updates (and cleans up) entities and tile entities
     */
    public void tickEntities()
    {
        this.profiler.startSection("entities");
        this.profiler.startSection("global");

        for (int i = 0; i < this.weatherEffects.size(); ++i)
        {
            Entity entity = this.weatherEffects.get(i);

            try
            {
                if(entity.updateBlocked) continue;
                ++entity.ticksExisted;
                entity.tick();
            }
            catch (Throwable throwable2)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable2, "Ticking entity");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being ticked");

                if (entity == null)
                {
                    crashreportcategory.addDetail("Entity", "~~NULL~~");
                }
                else
                {
                    entity.fillCrashReport(crashreportcategory);
                }

                if (net.minecraftforge.common.ForgeModContainer.removeErroringEntities)
                {
                    net.minecraftforge.fml.common.FMLLog.log.fatal("{}", crashreport.getCompleteReport());
                    removeEntity(entity);
                }
                else
                throw new ReportedException(crashreport);
            }

            if (entity.removed)
            {
                this.weatherEffects.remove(i--);
            }
        }

        this.profiler.endStartSection("remove");
        this.loadedEntityList.removeAll(this.unloadedEntityList);

        for (int k = 0; k < this.unloadedEntityList.size(); ++k)
        {
            Entity entity1 = this.unloadedEntityList.get(k);
            int j = entity1.chunkCoordX;
            int k1 = entity1.chunkCoordZ;

            if (entity1.addedToChunk && this.isChunkLoaded(j, k1, true))
            {
                this.getChunk(j, k1).removeEntity(entity1);
            }
        }

        for (int l = 0; l < this.unloadedEntityList.size(); ++l)
        {
            this.onEntityRemoved(this.unloadedEntityList.get(l));
        }

        this.unloadedEntityList.clear();
        this.tickPlayers();
        this.profiler.endStartSection("regular");

        for (int i1 = 0; i1 < this.loadedEntityList.size(); ++i1)
        {
            Entity entity2 = this.loadedEntityList.get(i1);
            Entity entity3 = entity2.getRidingEntity();

            if (entity3 != null)
            {
                if (!entity3.removed && entity3.isPassenger(entity2))
                {
                    continue;
                }

                entity2.stopRiding();
            }

            this.profiler.startSection("tick");

            if (!entity2.removed && !(entity2 instanceof EntityPlayerMP))
            {
                try
                {
                    net.minecraftforge.server.timings.TimeTracker.ENTITY_UPDATE.trackStart(entity2);
                    this.tickEntity(entity2);
                    net.minecraftforge.server.timings.TimeTracker.ENTITY_UPDATE.trackEnd(entity2);
                }
                catch (Throwable throwable1)
                {
                    CrashReport crashreport1 = CrashReport.makeCrashReport(throwable1, "Ticking entity");
                    CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Entity being ticked");
                    entity2.fillCrashReport(crashreportcategory1);
                    if (net.minecraftforge.common.ForgeModContainer.removeErroringEntities)
                    {
                        net.minecraftforge.fml.common.FMLLog.log.fatal("{}", crashreport1.getCompleteReport());
                        removeEntity(entity2);
                    }
                    else
                    throw new ReportedException(crashreport1);
                }
            }

            this.profiler.endSection();
            this.profiler.startSection("remove");

            if (entity2.removed)
            {
                int l1 = entity2.chunkCoordX;
                int i2 = entity2.chunkCoordZ;

                if (entity2.addedToChunk && this.isChunkLoaded(l1, i2, true))
                {
                    this.getChunk(l1, i2).removeEntity(entity2);
                }

                this.loadedEntityList.remove(i1--);
                this.onEntityRemoved(entity2);
            }

            this.profiler.endSection();
        }

        this.profiler.endStartSection("blockEntities");

        this.processingLoadedTiles = true; //FML Move above remove to prevent CMEs

        if (!this.tileEntitiesToBeRemoved.isEmpty())
        {
            for (Object tile : tileEntitiesToBeRemoved)
            {
               ((TileEntity)tile).onChunkUnload();
            }

            // forge: faster "contains" makes this removal much more efficient
            java.util.Set<TileEntity> remove = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
            remove.addAll(tileEntitiesToBeRemoved);
            this.tickableTileEntities.removeAll(remove);
            this.loadedTileEntityList.removeAll(remove);
            this.tileEntitiesToBeRemoved.clear();
        }

        Iterator<TileEntity> iterator = this.tickableTileEntities.iterator();

        while (iterator.hasNext())
        {
            TileEntity tileentity = iterator.next();

            if (!tileentity.isRemoved() && tileentity.hasWorld())
            {
                BlockPos blockpos = tileentity.getPos();

                if (this.isBlockLoaded(blockpos, false) && this.worldBorder.contains(blockpos)) //Forge: Fix TE's getting an extra tick on the client side....
                {
                    try
                    {
                        this.profiler.startSection(() ->
                        {
                            return String.valueOf((Object)TileEntity.func_190559_a(tileentity.getClass()));
                        });
                        net.minecraftforge.server.timings.TimeTracker.TILE_ENTITY_UPDATE.trackStart(tileentity);
                        ((ITickable)tileentity).tick();
                        net.minecraftforge.server.timings.TimeTracker.TILE_ENTITY_UPDATE.trackEnd(tileentity);
                        this.profiler.endSection();
                    }
                    catch (Throwable throwable)
                    {
                        CrashReport crashreport2 = CrashReport.makeCrashReport(throwable, "Ticking block entity");
                        CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Block entity being ticked");
                        tileentity.addInfoToCrashReport(crashreportcategory2);
                        if (net.minecraftforge.common.ForgeModContainer.removeErroringTileEntities)
                        {
                            net.minecraftforge.fml.common.FMLLog.log.fatal("{}", crashreport2.getCompleteReport());
                            tileentity.remove();
                            this.removeTileEntity(tileentity.getPos());
                        }
                        else
                        throw new ReportedException(crashreport2);
                    }
                }
            }

            if (tileentity.isRemoved())
            {
                iterator.remove();
                this.loadedTileEntityList.remove(tileentity);

                if (this.isBlockLoaded(tileentity.getPos()))
                {
                    //Forge: Bugfix: If we set the tile entity it immediately sets it in the chunk, so we could be desyned
                    Chunk chunk = this.getChunk(tileentity.getPos());
                    if (chunk.getTileEntity(tileentity.getPos(), net.minecraft.world.chunk.Chunk.EnumCreateEntityType.CHECK) == tileentity)
                        chunk.removeTileEntity(tileentity.getPos());
                }
            }
        }

        this.processingLoadedTiles = false;
        this.profiler.endStartSection("pendingBlockEntities");

        if (!this.addedTileEntityList.isEmpty())
        {
            for (int j1 = 0; j1 < this.addedTileEntityList.size(); ++j1)
            {
                TileEntity tileentity1 = this.addedTileEntityList.get(j1);

                if (!tileentity1.isRemoved())
                {
                    if (!this.loadedTileEntityList.contains(tileentity1))
                    {
                        this.addTileEntity(tileentity1);
                    }

                    if (this.isBlockLoaded(tileentity1.getPos()))
                    {
                        Chunk chunk = this.getChunk(tileentity1.getPos());
                        IBlockState iblockstate = chunk.func_177435_g(tileentity1.getPos());
                        chunk.addTileEntity(tileentity1.getPos(), tileentity1);
                        this.notifyBlockUpdate(tileentity1.getPos(), iblockstate, iblockstate, 3);
                    }
                }
            }

            this.addedTileEntityList.clear();
        }

        this.profiler.endSection();
        this.profiler.endSection();
    }

    protected void tickPlayers()
    {
    }

    public boolean addTileEntity(TileEntity tile)
    {
        // Forge - set the world early as vanilla doesn't set it until next tick
        if (tile.getWorld() != this) tile.setWorld(this);
        // Forge: wait to add new TE if we're currently processing existing ones
        if (processingLoadedTiles) return addedTileEntityList.add(tile);

        boolean flag = this.loadedTileEntityList.add(tile);

        if (flag && tile instanceof ITickable)
        {
            this.tickableTileEntities.add(tile);
        }
        tile.onLoad();

        if (this.isRemote)
        {
            BlockPos blockpos1 = tile.getPos();
            IBlockState iblockstate1 = this.getBlockState(blockpos1);
            this.notifyBlockUpdate(blockpos1, iblockstate1, iblockstate1, 2);
        }

        return flag;
    }

    public void addTileEntities(Collection<TileEntity> tileEntityCollection)
    {
        if (this.processingLoadedTiles)
        {
            for (TileEntity te : tileEntityCollection)
            {
                if (te.getWorld() != this) // Forge - set the world early as vanilla doesn't set it until next tick
                    te.setWorld(this);
            }
            this.addedTileEntityList.addAll(tileEntityCollection);
        }
        else
        {
            for (TileEntity tileentity2 : tileEntityCollection)
            {
                this.addTileEntity(tileentity2);
            }
        }
    }

    /**
     * Forcefully updates the entity.
     */
    public void tickEntity(Entity ent)
    {
        this.tickEntity(ent, true);
    }

    /**
     * Updates the entity in the world if the chunk the entity is in is currently loaded or its forced to update.
     */
    public void tickEntity(Entity entityIn, boolean forceUpdate)
    {
        if (!(entityIn instanceof EntityPlayer))
        {
            int j2 = MathHelper.floor(entityIn.posX);
            int k2 = MathHelper.floor(entityIn.posZ);

            boolean isForced = getPersistentChunks().containsKey(new net.minecraft.util.math.ChunkPos(j2 >> 4, k2 >> 4));
            int range = isForced ? 0 : 32;
            boolean canUpdate = !forceUpdate || this.isAreaLoaded(j2 - range, 0, k2 - range, j2 + range, 0, k2 + range, true);
            if (!canUpdate) canUpdate = net.minecraftforge.event.ForgeEventFactory.canEntityUpdate(entityIn);

            if (!canUpdate)
            {
                return;
            }
        }

        entityIn.lastTickPosX = entityIn.posX;
        entityIn.lastTickPosY = entityIn.posY;
        entityIn.lastTickPosZ = entityIn.posZ;
        entityIn.prevRotationYaw = entityIn.rotationYaw;
        entityIn.prevRotationPitch = entityIn.rotationPitch;

        if (forceUpdate && entityIn.addedToChunk)
        {
            ++entityIn.ticksExisted;

            if (entityIn.isPassenger())
            {
                entityIn.updateRidden();
            }
            else
            {
                if(!entityIn.updateBlocked)
                entityIn.tick();
            }
        }

        this.profiler.startSection("chunkCheck");

        if (Double.isNaN(entityIn.posX) || Double.isInfinite(entityIn.posX))
        {
            entityIn.posX = entityIn.lastTickPosX;
        }

        if (Double.isNaN(entityIn.posY) || Double.isInfinite(entityIn.posY))
        {
            entityIn.posY = entityIn.lastTickPosY;
        }

        if (Double.isNaN(entityIn.posZ) || Double.isInfinite(entityIn.posZ))
        {
            entityIn.posZ = entityIn.lastTickPosZ;
        }

        if (Double.isNaN((double)entityIn.rotationPitch) || Double.isInfinite((double)entityIn.rotationPitch))
        {
            entityIn.rotationPitch = entityIn.prevRotationPitch;
        }

        if (Double.isNaN((double)entityIn.rotationYaw) || Double.isInfinite((double)entityIn.rotationYaw))
        {
            entityIn.rotationYaw = entityIn.prevRotationYaw;
        }

        int i3 = MathHelper.floor(entityIn.posX / 16.0D);
        int j3 = MathHelper.floor(entityIn.posY / 16.0D);
        int k3 = MathHelper.floor(entityIn.posZ / 16.0D);

        if (!entityIn.addedToChunk || entityIn.chunkCoordX != i3 || entityIn.chunkCoordY != j3 || entityIn.chunkCoordZ != k3)
        {
            if (entityIn.addedToChunk && this.isChunkLoaded(entityIn.chunkCoordX, entityIn.chunkCoordZ, true))
            {
                this.getChunk(entityIn.chunkCoordX, entityIn.chunkCoordZ).removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
            }

            if (!entityIn.setPositionNonDirty() && !this.isChunkLoaded(i3, k3, true))
            {
                entityIn.addedToChunk = false;
            }
            else
            {
                this.getChunk(i3, k3).addEntity(entityIn);
            }
        }

        this.profiler.endSection();

        if (forceUpdate && entityIn.addedToChunk)
        {
            for (Entity entity4 : entityIn.getPassengers())
            {
                if (!entity4.removed && entity4.getRidingEntity() == entityIn)
                {
                    this.tickEntity(entity4);
                }
                else
                {
                    entity4.stopRiding();
                }
            }
        }
    }

    public boolean func_72855_b(AxisAlignedBB p_72855_1_)
    {
        return this.func_72917_a(p_72855_1_, (Entity)null);
    }

    public boolean func_72917_a(AxisAlignedBB p_72917_1_, @Nullable Entity p_72917_2_)
    {
        List<Entity> list = this.getEntitiesWithinAABBExcludingEntity((Entity)null, p_72917_1_);

        for (int j2 = 0; j2 < list.size(); ++j2)
        {
            Entity entity4 = list.get(j2);

            if (!entity4.removed && entity4.preventEntitySpawning && entity4 != p_72917_2_ && (p_72917_2_ == null || !entity4.isRidingSameEntity(p_72917_2_))) // Forge: fix MC-103516
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if there are any blocks in the region constrained by an AxisAlignedBB
     */
    public boolean checkBlockCollision(AxisAlignedBB bb)
    {
        int j2 = MathHelper.floor(bb.minX);
        int k2 = MathHelper.ceil(bb.maxX);
        int l2 = MathHelper.floor(bb.minY);
        int i3 = MathHelper.ceil(bb.maxY);
        int j3 = MathHelper.floor(bb.minZ);
        int k3 = MathHelper.ceil(bb.maxZ);
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

        for (int l3 = j2; l3 < k2; ++l3)
        {
            for (int i4 = l2; i4 < i3; ++i4)
            {
                for (int j4 = j3; j4 < k3; ++j4)
                {
                    IBlockState iblockstate1 = this.getBlockState(blockpos$pooledmutableblockpos.setPos(l3, i4, j4));

                    if (iblockstate1.getMaterial() != Material.AIR)
                    {
                        blockpos$pooledmutableblockpos.func_185344_t();
                        return true;
                    }
                }
            }
        }

        blockpos$pooledmutableblockpos.func_185344_t();
        return false;
    }

    /**
     * Checks if any of the blocks within the aabb are liquids.
     */
    public boolean containsAnyLiquid(AxisAlignedBB bb)
    {
        int j2 = MathHelper.floor(bb.minX);
        int k2 = MathHelper.ceil(bb.maxX);
        int l2 = MathHelper.floor(bb.minY);
        int i3 = MathHelper.ceil(bb.maxY);
        int j3 = MathHelper.floor(bb.minZ);
        int k3 = MathHelper.ceil(bb.maxZ);
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

        for (int l3 = j2; l3 < k2; ++l3)
        {
            for (int i4 = l2; i4 < i3; ++i4)
            {
                for (int j4 = j3; j4 < k3; ++j4)
                {
                    IBlockState iblockstate1 = this.getBlockState(blockpos$pooledmutableblockpos.setPos(l3, i4, j4));

                    Boolean result = iblockstate1.getBlock().isAABBInsideLiquid(this, blockpos$pooledmutableblockpos, bb);
                    if (result != null) {
                        if (!result) continue;
                        blockpos$pooledmutableblockpos.func_185344_t();
                        return true;
                    }
                    if (iblockstate1.getMaterial().isLiquid())
                    {
                        blockpos$pooledmutableblockpos.func_185344_t();
                        return true;
                    }
                }
            }
        }

        blockpos$pooledmutableblockpos.func_185344_t();
        return false;
    }

    public boolean isFlammableWithin(AxisAlignedBB bb)
    {
        int j2 = MathHelper.floor(bb.minX);
        int k2 = MathHelper.ceil(bb.maxX);
        int l2 = MathHelper.floor(bb.minY);
        int i3 = MathHelper.ceil(bb.maxY);
        int j3 = MathHelper.floor(bb.minZ);
        int k3 = MathHelper.ceil(bb.maxZ);

        if (this.isAreaLoaded(j2, l2, j3, k2, i3, k3, true))
        {
            BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

            for (int l3 = j2; l3 < k2; ++l3)
            {
                for (int i4 = l2; i4 < i3; ++i4)
                {
                    for (int j4 = j3; j4 < k3; ++j4)
                    {
                        Block block = this.getBlockState(blockpos$pooledmutableblockpos.setPos(l3, i4, j4)).getBlock();

                        if (block == Blocks.FIRE || block == Blocks.field_150356_k || block == Blocks.LAVA)
                        {
                            blockpos$pooledmutableblockpos.func_185344_t();
                            return true;
                        }
                        else if (block.isBurning(this, new BlockPos(l3, i4, j4)))
                        {
                            blockpos$pooledmutableblockpos.func_185344_t();
                            return true;
                        }
                    }
                }
            }

            blockpos$pooledmutableblockpos.func_185344_t();
        }

        return false;
    }

    public boolean func_72918_a(AxisAlignedBB p_72918_1_, Material p_72918_2_, Entity p_72918_3_)
    {
        int j2 = MathHelper.floor(p_72918_1_.minX);
        int k2 = MathHelper.ceil(p_72918_1_.maxX);
        int l2 = MathHelper.floor(p_72918_1_.minY);
        int i3 = MathHelper.ceil(p_72918_1_.maxY);
        int j3 = MathHelper.floor(p_72918_1_.minZ);
        int k3 = MathHelper.ceil(p_72918_1_.maxZ);

        if (!this.isAreaLoaded(j2, l2, j3, k2, i3, k3, true))
        {
            return false;
        }
        else
        {
            boolean flag = false;
            Vec3d vec3d = Vec3d.ZERO;
            BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

            for (int l3 = j2; l3 < k2; ++l3)
            {
                for (int i4 = l2; i4 < i3; ++i4)
                {
                    for (int j4 = j3; j4 < k3; ++j4)
                    {
                        blockpos$pooledmutableblockpos.setPos(l3, i4, j4);
                        IBlockState iblockstate1 = this.getBlockState(blockpos$pooledmutableblockpos);
                        Block block = iblockstate1.getBlock();

                        Boolean result = block.isEntityInsideMaterial(this, blockpos$pooledmutableblockpos, iblockstate1, p_72918_3_, (double)i3, p_72918_2_, false);
                        if (result != null && result == true)
                        {
                            // Forge: When requested call blocks modifyAcceleration method, and more importantly cause this method to return true, which results in an entity being "inWater"
                            flag = true;
                            vec3d = block.func_176197_a(this, blockpos$pooledmutableblockpos, p_72918_3_, vec3d);
                            continue;
                        }
                        else if (result != null && result == false) continue;

                        if (iblockstate1.getMaterial() == p_72918_2_)
                        {
                            double d0 = (double)((float)(i4 + 1) - BlockLiquid.func_149801_b(((Integer)iblockstate1.get(BlockLiquid.LEVEL)).intValue()));

                            if ((double)i3 >= d0)
                            {
                                flag = true;
                                vec3d = block.func_176197_a(this, blockpos$pooledmutableblockpos, p_72918_3_, vec3d);
                            }
                        }
                    }
                }
            }

            blockpos$pooledmutableblockpos.func_185344_t();

            if (vec3d.length() > 0.0D && p_72918_3_.isPushedByWater())
            {
                vec3d = vec3d.normalize();
                double d1 = 0.014D;
                p_72918_3_.motionX += vec3d.x * 0.014D;
                p_72918_3_.motionY += vec3d.y * 0.014D;
                p_72918_3_.motionZ += vec3d.z * 0.014D;
            }

            return flag;
        }
    }

    /**
     * Returns true if the given bounding box contains the given material
     */
    public boolean isMaterialInBB(AxisAlignedBB bb, Material materialIn)
    {
        int j2 = MathHelper.floor(bb.minX);
        int k2 = MathHelper.ceil(bb.maxX);
        int l2 = MathHelper.floor(bb.minY);
        int i3 = MathHelper.ceil(bb.maxY);
        int j3 = MathHelper.floor(bb.minZ);
        int k3 = MathHelper.ceil(bb.maxZ);
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

        for (int l3 = j2; l3 < k2; ++l3)
        {
            for (int i4 = l2; i4 < i3; ++i4)
            {
                for (int j4 = j3; j4 < k3; ++j4)
                {
                    IBlockState iblockstate1 = this.getBlockState(blockpos$pooledmutableblockpos.setPos(l3, i4, j4));
                    Boolean result = iblockstate1.getBlock().isAABBInsideMaterial(this, blockpos$pooledmutableblockpos, bb, materialIn);
                    if (result != null) {
                        if (!result) continue;
                        blockpos$pooledmutableblockpos.func_185344_t();
                        return true;
                    }
                    if (iblockstate1.getMaterial() == materialIn)
                    {
                        blockpos$pooledmutableblockpos.func_185344_t();
                        return true;
                    }
                }
            }
        }

        blockpos$pooledmutableblockpos.func_185344_t();
        return false;
    }

    /**
     * Creates an explosion in the world.
     */
    public Explosion createExplosion(@Nullable Entity entityIn, double x, double y, double z, float strength, boolean damagesTerrain)
    {
        return this.newExplosion(entityIn, x, y, z, strength, false, damagesTerrain);
    }

    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
     */
    public Explosion newExplosion(@Nullable Entity entityIn, double x, double y, double z, float strength, boolean causesFire, boolean damagesTerrain)
    {
        Explosion explosion = new Explosion(this, entityIn, x, y, z, strength, causesFire, damagesTerrain);
        if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this, explosion)) return explosion;
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        return explosion;
    }

    /**
     * Gets the percentage of real blocks within within a bounding box, along a specified vector.
     */
    public float getBlockDensity(Vec3d vec, AxisAlignedBB bb)
    {
        double d0 = 1.0D / ((bb.maxX - bb.minX) * 2.0D + 1.0D);
        double d1 = 1.0D / ((bb.maxY - bb.minY) * 2.0D + 1.0D);
        double d2 = 1.0D / ((bb.maxZ - bb.minZ) * 2.0D + 1.0D);
        double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;

        if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D)
        {
            int j2 = 0;
            int k2 = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float)((double)f + d0))
            {
                for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float)((double)f1 + d1))
                {
                    for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float)((double)f2 + d2))
                    {
                        double d5 = bb.minX + (bb.maxX - bb.minX) * (double)f;
                        double d6 = bb.minY + (bb.maxY - bb.minY) * (double)f1;
                        double d7 = bb.minZ + (bb.maxZ - bb.minZ) * (double)f2;

                        if (this.rayTraceBlocks(new Vec3d(d5 + d3, d6, d7 + d4), vec) == null)
                        {
                            ++j2;
                        }

                        ++k2;
                    }
                }
            }

            return (float)j2 / (float)k2;
        }
        else
        {
            return 0.0F;
        }
    }

    /**
     * Attempts to extinguish a fire
     */
    public boolean extinguishFire(@Nullable EntityPlayer player, BlockPos pos, EnumFacing side)
    {
        pos = pos.offset(side);

        if (this.getBlockState(pos).getBlock() == Blocks.FIRE)
        {
            this.playEvent(player, 1009, pos, 0);
            this.removeBlock(pos);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * This string is 'All: (number of loaded entities)' Viewable by press ing F3
     */
    @SideOnly(Side.CLIENT)
    public String getDebugLoadedEntities()
    {
        return "All: " + this.loadedEntityList.size();
    }

    /**
     * Returns the name of the current chunk provider, by calling chunkprovider.makeString()
     */
    @SideOnly(Side.CLIENT)
    public String getProviderName()
    {
        return this.chunkProvider.makeString();
    }

    @Nullable
    public TileEntity getTileEntity(BlockPos pos)
    {
        if (this.isOutsideBuildHeight(pos))
        {
            return null;
        }
        else
        {
            TileEntity tileentity2 = null;

            if (this.processingLoadedTiles)
            {
                tileentity2 = this.getPendingTileEntityAt(pos);
            }

            if (tileentity2 == null)
            {
                tileentity2 = this.getChunk(pos).getTileEntity(pos, Chunk.EnumCreateEntityType.IMMEDIATE);
            }

            if (tileentity2 == null)
            {
                tileentity2 = this.getPendingTileEntityAt(pos);
            }

            return tileentity2;
        }
    }

    @Nullable
    private TileEntity getPendingTileEntityAt(BlockPos pos)
    {
        for (int j2 = 0; j2 < this.addedTileEntityList.size(); ++j2)
        {
            TileEntity tileentity2 = this.addedTileEntityList.get(j2);

            if (!tileentity2.isRemoved() && tileentity2.getPos().equals(pos))
            {
                return tileentity2;
            }
        }

        return null;
    }

    public void setTileEntity(BlockPos pos, @Nullable TileEntity tileEntityIn)
    {
        pos = pos.toImmutable(); // Forge - prevent mutable BlockPos leaks
        if (!this.isOutsideBuildHeight(pos))
        {
            if (tileEntityIn != null && !tileEntityIn.isRemoved())
            {
                if (this.processingLoadedTiles)
                {
                    tileEntityIn.setPos(pos);
                    if (tileEntityIn.getWorld() != this)
                        tileEntityIn.setWorld(this); // Forge - set the world early as vanilla doesn't set it until next tick
                    Iterator<TileEntity> iterator1 = this.addedTileEntityList.iterator();

                    while (iterator1.hasNext())
                    {
                        TileEntity tileentity2 = iterator1.next();

                        if (tileentity2.getPos().equals(pos))
                        {
                            tileentity2.remove();
                            iterator1.remove();
                        }
                    }

                    this.addedTileEntityList.add(tileEntityIn);
                }
                else
                {
                    Chunk chunk = this.getChunk(pos);
                    if (chunk != null) chunk.addTileEntity(pos, tileEntityIn);
                    this.addTileEntity(tileEntityIn);
                }
            }
        }
    }

    public void removeTileEntity(BlockPos pos)
    {
        TileEntity tileentity2 = this.getTileEntity(pos);

        if (tileentity2 != null && this.processingLoadedTiles)
        {
            tileentity2.remove();
            this.addedTileEntityList.remove(tileentity2);
            if (!(tileentity2 instanceof ITickable)) //Forge: If they are not tickable they wont be removed in the update loop.
                this.loadedTileEntityList.remove(tileentity2);
        }
        else
        {
            if (tileentity2 != null)
            {
                this.addedTileEntityList.remove(tileentity2);
                this.loadedTileEntityList.remove(tileentity2);
                this.tickableTileEntities.remove(tileentity2);
            }

            this.getChunk(pos).removeTileEntity(pos);
        }
        this.updateComparatorOutputLevel(pos, getBlockState(pos).getBlock()); //Notify neighbors of changes
    }

    /**
     * Adds the specified TileEntity to the pending removal list.
     */
    public void markTileEntityForRemoval(TileEntity tileEntityIn)
    {
        this.tileEntitiesToBeRemoved.add(tileEntityIn);
    }

    public boolean isBlockFullCube(BlockPos pos)
    {
        AxisAlignedBB axisalignedbb = this.getBlockState(pos).func_185890_d(this, pos);
        return axisalignedbb != Block.field_185506_k && axisalignedbb.getAverageEdgeLength() >= 1.0D;
    }

    public boolean func_175677_d(BlockPos p_175677_1_, boolean p_175677_2_)
    {
        if (this.isOutsideBuildHeight(p_175677_1_))
        {
            return false;
        }
        else
        {
            Chunk chunk1 = this.chunkProvider.getLoadedChunk(p_175677_1_.getX() >> 4, p_175677_1_.getZ() >> 4);

            if (chunk1 != null && !chunk1.isEmpty())
            {
                IBlockState iblockstate1 = this.getBlockState(p_175677_1_);
                return iblockstate1.getBlock().isNormalCube(iblockstate1, this, p_175677_1_);
            }
            else
            {
                return p_175677_2_;
            }
        }
    }

    /**
     * Called on construction of the World class to setup the initial skylight values
     */
    public void calculateInitialSkylight()
    {
        int j2 = this.calculateSkylightSubtracted(1.0F);

        if (j2 != this.skylightSubtracted)
        {
            this.skylightSubtracted = j2;
        }
    }

    /**
     * first boolean for hostile mobs and second for peaceful mobs
     */
    public void setAllowedSpawnTypes(boolean hostile, boolean peaceful)
    {
        this.spawnHostileMobs = hostile;
        this.spawnPeacefulMobs = peaceful;
        this.dimension.setAllowedSpawnTypes(hostile, peaceful);
    }

    /**
     * Runs a single tick for the world
     */
    public void tick()
    {
        this.tickWeather();
    }

    /**
     * Called from World constructor to set rainingStrength and thunderingStrength
     */
    protected void calculateInitialWeather()
    {
        this.dimension.calculateInitialWeather();
    }

    public void calculateInitialWeatherBody()
    {
        if (this.worldInfo.isRaining())
        {
            this.rainingStrength = 1.0F;

            if (this.worldInfo.isThundering())
            {
                this.thunderingStrength = 1.0F;
            }
        }
    }

    /**
     * Updates all weather states.
     */
    protected void tickWeather()
    {
        this.dimension.updateWeather();
    }

    public void updateWeatherBody()
    {
        if (this.dimension.hasSkyLight())
        {
            if (!this.isRemote)
            {
                boolean flag = this.getGameRules().getBoolean("doWeatherCycle");

                if (flag)
                {
                    int j2 = this.worldInfo.getClearWeatherTime();

                    if (j2 > 0)
                    {
                        --j2;
                        this.worldInfo.setClearWeatherTime(j2);
                        this.worldInfo.setThunderTime(this.worldInfo.isThundering() ? 1 : 2);
                        this.worldInfo.setRainTime(this.worldInfo.isRaining() ? 1 : 2);
                    }

                    int k2 = this.worldInfo.getThunderTime();

                    if (k2 <= 0)
                    {
                        if (this.worldInfo.isThundering())
                        {
                            this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
                        }
                        else
                        {
                            this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
                        }
                    }
                    else
                    {
                        --k2;
                        this.worldInfo.setThunderTime(k2);

                        if (k2 <= 0)
                        {
                            this.worldInfo.setThundering(!this.worldInfo.isThundering());
                        }
                    }

                    int l2 = this.worldInfo.getRainTime();

                    if (l2 <= 0)
                    {
                        if (this.worldInfo.isRaining())
                        {
                            this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
                        }
                        else
                        {
                            this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
                        }
                    }
                    else
                    {
                        --l2;
                        this.worldInfo.setRainTime(l2);

                        if (l2 <= 0)
                        {
                            this.worldInfo.setRaining(!this.worldInfo.isRaining());
                        }
                    }
                }

                this.prevThunderingStrength = this.thunderingStrength;

                if (this.worldInfo.isThundering())
                {
                    this.thunderingStrength = (float)((double)this.thunderingStrength + 0.01D);
                }
                else
                {
                    this.thunderingStrength = (float)((double)this.thunderingStrength - 0.01D);
                }

                this.thunderingStrength = MathHelper.clamp(this.thunderingStrength, 0.0F, 1.0F);
                this.prevRainingStrength = this.rainingStrength;

                if (this.worldInfo.isRaining())
                {
                    this.rainingStrength = (float)((double)this.rainingStrength + 0.01D);
                }
                else
                {
                    this.rainingStrength = (float)((double)this.rainingStrength - 0.01D);
                }

                this.rainingStrength = MathHelper.clamp(this.rainingStrength, 0.0F, 1.0F);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    protected void playMoodSoundAndCheckLight(int x, int z, Chunk chunkIn)
    {
        chunkIn.enqueueRelightChecks();
    }

    protected void tickBlocks()
    {
    }

    public void func_189507_a(BlockPos p_189507_1_, IBlockState p_189507_2_, Random p_189507_3_)
    {
        this.field_72999_e = true;
        p_189507_2_.getBlock().func_180650_b(this, p_189507_1_, p_189507_2_, p_189507_3_);
        this.field_72999_e = false;
    }

    public boolean func_175675_v(BlockPos p_175675_1_)
    {
        return this.func_175670_e(p_175675_1_, false);
    }

    public boolean func_175662_w(BlockPos p_175662_1_)
    {
        return this.func_175670_e(p_175662_1_, true);
    }

    public boolean func_175670_e(BlockPos p_175670_1_, boolean p_175670_2_)
    {
        return this.dimension.canBlockFreeze(p_175670_1_, p_175670_2_);
    }

    public boolean canBlockFreezeBody(BlockPos p_175670_1_, boolean p_175670_2_)
    {
        Biome biome = this.getBiome(p_175670_1_);
        float f = biome.getTemperature(p_175670_1_);

        if (f >= 0.15F)
        {
            return false;
        }
        else
        {
            if (p_175670_1_.getY() >= 0 && p_175670_1_.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, p_175670_1_) < 10)
            {
                IBlockState iblockstate1 = this.getBlockState(p_175670_1_);
                Block block = iblockstate1.getBlock();

                if ((block == Blocks.WATER || block == Blocks.field_150358_i) && ((Integer)iblockstate1.get(BlockLiquid.LEVEL)).intValue() == 0)
                {
                    if (!p_175670_2_)
                    {
                        return true;
                    }

                    boolean flag = this.func_175696_F(p_175670_1_.west()) && this.func_175696_F(p_175670_1_.east()) && this.func_175696_F(p_175670_1_.north()) && this.func_175696_F(p_175670_1_.south());

                    if (!flag)
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    private boolean func_175696_F(BlockPos p_175696_1_)
    {
        return this.getBlockState(p_175696_1_).getMaterial() == Material.WATER;
    }

    public boolean func_175708_f(BlockPos p_175708_1_, boolean p_175708_2_)
    {
        return this.dimension.canSnowAt(p_175708_1_, p_175708_2_);
    }

    public boolean canSnowAtBody(BlockPos p_175708_1_, boolean p_175708_2_)
    {
        Biome biome = this.getBiome(p_175708_1_);
        float f = biome.getTemperature(p_175708_1_);

        if (f >= 0.15F)
        {
            return false;
        }
        else if (!p_175708_2_)
        {
            return true;
        }
        else
        {
            if (p_175708_1_.getY() >= 0 && p_175708_1_.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, p_175708_1_) < 10)
            {
                IBlockState iblockstate1 = this.getBlockState(p_175708_1_);

                if (iblockstate1.getBlock().isAir(iblockstate1, this, p_175708_1_) && Blocks.field_150431_aC.func_176196_c(this, p_175708_1_))
                {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean checkLight(BlockPos pos)
    {
        boolean flag = false;

        if (this.dimension.hasSkyLight())
        {
            flag |= this.checkLightFor(EnumSkyBlock.SKY, pos);
        }

        flag = flag | this.checkLightFor(EnumSkyBlock.BLOCK, pos);
        return flag;
    }

    /**
     * gets the light level at the supplied position
     */
    private int getRawLight(BlockPos pos, EnumSkyBlock lightType)
    {
        if (lightType == EnumSkyBlock.SKY && this.canSeeSky(pos))
        {
            return 15;
        }
        else
        {
            IBlockState iblockstate1 = this.getBlockState(pos);
            int j2 = lightType == EnumSkyBlock.SKY ? 0 : iblockstate1.getBlock().getLightValue(iblockstate1, this, pos);
            int k2 = iblockstate1.getBlock().getLightOpacity(iblockstate1, this, pos);

            if (false) // Forge: fix MC-119932
            {
                k2 = 1;
            }

            if (k2 < 1)
            {
                k2 = 1;
            }

            if (k2 >= 15)
            {
                return j2; // Forge: fix MC-119932
            }
            else if (j2 >= 14)
            {
                return j2;
            }
            else
            {
                BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

                try
                {
                    for (EnumFacing enumfacing : EnumFacing.values())
                    {
                        blockpos$pooledmutableblockpos.setPos(pos).move(enumfacing);
                        int l2 = this.getLightFor(lightType, blockpos$pooledmutableblockpos) - k2;

                        if (l2 > j2)
                        {
                            j2 = l2;
                        }

                        if (j2 >= 14)
                        {
                            int i3 = j2;
                            return i3;
                        }
                    }

                    return j2;
                }
                finally
                {
                    blockpos$pooledmutableblockpos.func_185344_t();
                }
            }
        }
    }

    public boolean checkLightFor(EnumSkyBlock lightType, BlockPos pos)
    {
        if (!this.isAreaLoaded(pos, 16, false))
        {
            return false;
        }
        else
        {
            int updateRange = this.isAreaLoaded(pos, 18, false) ? 17 : 15;
            int j2 = 0;
            int k2 = 0;
            this.profiler.startSection("getBrightness");
            int l2 = this.getLightFor(lightType, pos);
            int i3 = this.getRawLight(pos, lightType);
            int j3 = pos.getX();
            int k3 = pos.getY();
            int l3 = pos.getZ();

            if (i3 > l2)
            {
                this.lightUpdateBlockList[k2++] = 133152;
            }
            else if (i3 < l2)
            {
                this.lightUpdateBlockList[k2++] = 133152 | l2 << 18;

                while (j2 < k2)
                {
                    int i4 = this.lightUpdateBlockList[j2++];
                    int j4 = (i4 & 63) - 32 + j3;
                    int k4 = (i4 >> 6 & 63) - 32 + k3;
                    int l4 = (i4 >> 12 & 63) - 32 + l3;
                    int i5 = i4 >> 18 & 15;
                    BlockPos blockpos1 = new BlockPos(j4, k4, l4);
                    int j5 = this.getLightFor(lightType, blockpos1);

                    if (j5 == i5)
                    {
                        this.setLightFor(lightType, blockpos1, 0);

                        if (i5 > 0)
                        {
                            int k5 = MathHelper.abs(j4 - j3);
                            int l5 = MathHelper.abs(k4 - k3);
                            int i6 = MathHelper.abs(l4 - l3);

                            if (k5 + l5 + i6 < updateRange)
                            {
                                BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

                                for (EnumFacing enumfacing : EnumFacing.values())
                                {
                                    int j6 = j4 + enumfacing.getXOffset();
                                    int k6 = k4 + enumfacing.getYOffset();
                                    int l6 = l4 + enumfacing.getZOffset();
                                    blockpos$pooledmutableblockpos.setPos(j6, k6, l6);
                                    IBlockState bs = this.getBlockState(blockpos$pooledmutableblockpos);
                                    int i7 = Math.max(1, bs.getBlock().getLightOpacity(bs, this, blockpos$pooledmutableblockpos));
                                    j5 = this.getLightFor(lightType, blockpos$pooledmutableblockpos);

                                    if (j5 == i5 - i7 && k2 < this.lightUpdateBlockList.length)
                                    {
                                        this.lightUpdateBlockList[k2++] = j6 - j3 + 32 | k6 - k3 + 32 << 6 | l6 - l3 + 32 << 12 | i5 - i7 << 18;
                                    }
                                }

                                blockpos$pooledmutableblockpos.func_185344_t();
                            }
                        }
                    }
                }

                j2 = 0;
            }

            this.profiler.endSection();
            this.profiler.startSection("checkedPosition < toCheckCount");

            while (j2 < k2)
            {
                int j7 = this.lightUpdateBlockList[j2++];
                int k7 = (j7 & 63) - 32 + j3;
                int l7 = (j7 >> 6 & 63) - 32 + k3;
                int i8 = (j7 >> 12 & 63) - 32 + l3;
                BlockPos blockpos2 = new BlockPos(k7, l7, i8);
                int j8 = this.getLightFor(lightType, blockpos2);
                int k8 = this.getRawLight(blockpos2, lightType);

                if (k8 != j8)
                {
                    this.setLightFor(lightType, blockpos2, k8);

                    if (k8 > j8)
                    {
                        int l8 = Math.abs(k7 - j3);
                        int i9 = Math.abs(l7 - k3);
                        int j9 = Math.abs(i8 - l3);
                        boolean flag = k2 < this.lightUpdateBlockList.length - 6;

                        if (l8 + i9 + j9 < updateRange && flag)
                        {
                            if (this.getLightFor(lightType, blockpos2.west()) < k8)
                            {
                                this.lightUpdateBlockList[k2++] = k7 - 1 - j3 + 32 + (l7 - k3 + 32 << 6) + (i8 - l3 + 32 << 12);
                            }

                            if (this.getLightFor(lightType, blockpos2.east()) < k8)
                            {
                                this.lightUpdateBlockList[k2++] = k7 + 1 - j3 + 32 + (l7 - k3 + 32 << 6) + (i8 - l3 + 32 << 12);
                            }

                            if (this.getLightFor(lightType, blockpos2.down()) < k8)
                            {
                                this.lightUpdateBlockList[k2++] = k7 - j3 + 32 + (l7 - 1 - k3 + 32 << 6) + (i8 - l3 + 32 << 12);
                            }

                            if (this.getLightFor(lightType, blockpos2.up()) < k8)
                            {
                                this.lightUpdateBlockList[k2++] = k7 - j3 + 32 + (l7 + 1 - k3 + 32 << 6) + (i8 - l3 + 32 << 12);
                            }

                            if (this.getLightFor(lightType, blockpos2.north()) < k8)
                            {
                                this.lightUpdateBlockList[k2++] = k7 - j3 + 32 + (l7 - k3 + 32 << 6) + (i8 - 1 - l3 + 32 << 12);
                            }

                            if (this.getLightFor(lightType, blockpos2.south()) < k8)
                            {
                                this.lightUpdateBlockList[k2++] = k7 - j3 + 32 + (l7 - k3 + 32 << 6) + (i8 + 1 - l3 + 32 << 12);
                            }
                        }
                    }
                }
            }

            this.profiler.endSection();
            return true;
        }
    }

    /**
     * Runs through the list of updates to run and ticks them
     */
    public boolean tickPending(boolean p_72955_1_)
    {
        return false;
    }

    @Nullable
    public List<NextTickListEntry> func_72920_a(Chunk p_72920_1_, boolean p_72920_2_)
    {
        return null;
    }

    @Nullable
    public List<NextTickListEntry> func_175712_a(StructureBoundingBox p_175712_1_, boolean p_175712_2_)
    {
        return null;
    }

    /**
     * Will get all entities within the specified AABB excluding the one passed into it. Args: entityToExclude, aabb
     */
    public List<Entity> getEntitiesWithinAABBExcludingEntity(@Nullable Entity entityIn, AxisAlignedBB bb)
    {
        return this.getEntitiesInAABBexcluding(entityIn, bb, EntitySelectors.NOT_SPECTATING);
    }

    /**
     * Gets all entities within the specified AABB excluding the one passed into it.
     */
    public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate <? super Entity > predicate)
    {
        List<Entity> list = Lists.<Entity>newArrayList();
        int j2 = MathHelper.floor((boundingBox.minX - MAX_ENTITY_RADIUS) / 16.0D);
        int k2 = MathHelper.floor((boundingBox.maxX + MAX_ENTITY_RADIUS) / 16.0D);
        int l2 = MathHelper.floor((boundingBox.minZ - MAX_ENTITY_RADIUS) / 16.0D);
        int i3 = MathHelper.floor((boundingBox.maxZ + MAX_ENTITY_RADIUS) / 16.0D);

        for (int j3 = j2; j3 <= k2; ++j3)
        {
            for (int k3 = l2; k3 <= i3; ++k3)
            {
                if (this.isChunkLoaded(j3, k3, true))
                {
                    this.getChunk(j3, k3).getEntitiesWithinAABBForEntity(entityIn, boundingBox, list, predicate);
                }
            }
        }

        return list;
    }

    public <T extends Entity> List<T> getEntities(Class <? extends T > entityType, Predicate <? super T > filter)
    {
        List<T> list = Lists.<T>newArrayList();

        for (Entity entity4 : this.loadedEntityList)
        {
            if (entityType.isAssignableFrom(entity4.getClass()) && filter.apply((T)entity4))
            {
                list.add((T)entity4);
            }
        }

        return list;
    }

    public <T extends Entity> List<T> getPlayers(Class <? extends T > playerType, Predicate <? super T > filter)
    {
        List<T> list = Lists.<T>newArrayList();

        for (Entity entity4 : this.playerEntities)
        {
            if (playerType.isAssignableFrom(entity4.getClass()) && filter.apply((T)entity4))
            {
                list.add((T)entity4);
            }
        }

        return list;
    }

    /**
     * Gets all entities of the specified class type which intersect with the AABB.
     */
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class <? extends T > classEntity, AxisAlignedBB bb)
    {
        return this.<T>getEntitiesWithinAABB(classEntity, bb, EntitySelectors.NOT_SPECTATING);
    }

    public <T extends Entity> List<T> getEntitiesWithinAABB(Class <? extends T > clazz, AxisAlignedBB aabb, @Nullable Predicate <? super T > filter)
    {
        int j2 = MathHelper.floor((aabb.minX - MAX_ENTITY_RADIUS) / 16.0D);
        int k2 = MathHelper.ceil((aabb.maxX + MAX_ENTITY_RADIUS) / 16.0D);
        int l2 = MathHelper.floor((aabb.minZ - MAX_ENTITY_RADIUS) / 16.0D);
        int i3 = MathHelper.ceil((aabb.maxZ + MAX_ENTITY_RADIUS) / 16.0D);
        List<T> list = Lists.<T>newArrayList();

        for (int j3 = j2; j3 < k2; ++j3)
        {
            for (int k3 = l2; k3 < i3; ++k3)
            {
                if (this.isChunkLoaded(j3, k3, true))
                {
                    this.getChunk(j3, k3).getEntitiesOfTypeWithinAABB(clazz, aabb, list, filter);
                }
            }
        }

        return list;
    }

    @Nullable
    public <T extends Entity> T findNearestEntityWithinAABB(Class <? extends T > entityType, AxisAlignedBB aabb, T closestTo)
    {
        List<T> list = this.<T>getEntitiesWithinAABB(entityType, aabb);
        T t = null;
        double d0 = Double.MAX_VALUE;

        for (int j2 = 0; j2 < list.size(); ++j2)
        {
            T t1 = list.get(j2);

            if (t1 != closestTo && EntitySelectors.NOT_SPECTATING.apply(t1))
            {
                double d1 = closestTo.getDistanceSq(t1);

                if (d1 <= d0)
                {
                    t = t1;
                    d0 = d1;
                }
            }
        }

        return t;
    }

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this World.
     */
    @Nullable
    public Entity getEntityByID(int id)
    {
        return this.entitiesById.lookup(id);
    }

    /**
     * Accessor for world Loaded Entity List
     */
    @SideOnly(Side.CLIENT)
    public List<Entity> getLoadedEntityList()
    {
        return this.loadedEntityList;
    }

    public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity)
    {
        if (this.isBlockLoaded(pos))
        {
            this.getChunk(pos).markDirty();
        }
    }

    /**
     * Counts how many entities of an entity class exist in the world.
     */
    public int countEntities(Class<?> entityType)
    {
        int j2 = 0;

        for (Entity entity4 : this.loadedEntityList)
        {
            if ((!(entity4 instanceof EntityLiving) || !((EntityLiving)entity4).isNoDespawnRequired()) && entityType.isAssignableFrom(entity4.getClass()))
            {
                ++j2;
            }
        }

        return j2;
    }

    public void loadEntities(Collection<Entity> entityCollection)
    {
        for (Entity entity4 : entityCollection)
        {
            if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinWorldEvent(entity4, this)))
            {
                loadedEntityList.add(entity4);
                this.onEntityAdded(entity4);
            }
        }
    }

    public void unloadEntities(Collection<Entity> entityCollection)
    {
        this.unloadedEntityList.addAll(entityCollection);
    }

    public boolean func_190527_a(Block p_190527_1_, BlockPos p_190527_2_, boolean p_190527_3_, EnumFacing p_190527_4_, @Nullable Entity p_190527_5_)
    {
        IBlockState iblockstate1 = this.getBlockState(p_190527_2_);
        AxisAlignedBB axisalignedbb = p_190527_3_ ? null : p_190527_1_.getDefaultState().func_185890_d(this, p_190527_2_);

        if (axisalignedbb != Block.field_185506_k && !this.func_72917_a(axisalignedbb.offset(p_190527_2_), p_190527_5_))
        {
            return false;
        }
        else if (iblockstate1.getMaterial() == Material.CIRCUITS && p_190527_1_ == Blocks.ANVIL)
        {
            return true;
        }
        else
        {
            return iblockstate1.getBlock().func_176200_f(this, p_190527_2_) && p_190527_1_.func_176198_a(this, p_190527_2_, p_190527_4_);
        }
    }

    public int getSeaLevel()
    {
        return this.seaLevel;
    }

    /**
     * Warning this value may not be respected in all cases as it is still hardcoded in many places.
     */
    public void setSeaLevel(int seaLevelIn)
    {
        this.seaLevel = seaLevelIn;
    }

    public int getStrongPower(BlockPos pos, EnumFacing direction)
    {
        return this.getBlockState(pos).getStrongPower(this, pos, direction);
    }

    public WorldType getWorldType()
    {
        return this.worldInfo.getTerrainType();
    }

    /**
     * Returns the single highest strong power out of all directions using getStrongPower(BlockPos, EnumFacing)
     */
    public int getStrongPower(BlockPos pos)
    {
        int j2 = 0;
        j2 = Math.max(j2, this.getStrongPower(pos.down(), EnumFacing.DOWN));

        if (j2 >= 15)
        {
            return j2;
        }
        else
        {
            j2 = Math.max(j2, this.getStrongPower(pos.up(), EnumFacing.UP));

            if (j2 >= 15)
            {
                return j2;
            }
            else
            {
                j2 = Math.max(j2, this.getStrongPower(pos.north(), EnumFacing.NORTH));

                if (j2 >= 15)
                {
                    return j2;
                }
                else
                {
                    j2 = Math.max(j2, this.getStrongPower(pos.south(), EnumFacing.SOUTH));

                    if (j2 >= 15)
                    {
                        return j2;
                    }
                    else
                    {
                        j2 = Math.max(j2, this.getStrongPower(pos.west(), EnumFacing.WEST));

                        if (j2 >= 15)
                        {
                            return j2;
                        }
                        else
                        {
                            j2 = Math.max(j2, this.getStrongPower(pos.east(), EnumFacing.EAST));
                            return j2 >= 15 ? j2 : j2;
                        }
                    }
                }
            }
        }
    }

    public boolean isSidePowered(BlockPos pos, EnumFacing side)
    {
        return this.getRedstonePower(pos, side) > 0;
    }

    public int getRedstonePower(BlockPos pos, EnumFacing facing)
    {
        IBlockState iblockstate1 = this.getBlockState(pos);
        return iblockstate1.getBlock().shouldCheckWeakPower(iblockstate1, this, pos, facing) ? this.getStrongPower(pos) : iblockstate1.getWeakPower(this, pos, facing);
    }

    public boolean isBlockPowered(BlockPos pos)
    {
        if (this.getRedstonePower(pos.down(), EnumFacing.DOWN) > 0)
        {
            return true;
        }
        else if (this.getRedstonePower(pos.up(), EnumFacing.UP) > 0)
        {
            return true;
        }
        else if (this.getRedstonePower(pos.north(), EnumFacing.NORTH) > 0)
        {
            return true;
        }
        else if (this.getRedstonePower(pos.south(), EnumFacing.SOUTH) > 0)
        {
            return true;
        }
        else if (this.getRedstonePower(pos.west(), EnumFacing.WEST) > 0)
        {
            return true;
        }
        else
        {
            return this.getRedstonePower(pos.east(), EnumFacing.EAST) > 0;
        }
    }

    /**
     * Checks if the specified block or its neighbors are powered by a neighboring block. Used by blocks like TNT and
     * Doors.
     */
    public int getRedstonePowerFromNeighbors(BlockPos pos)
    {
        int j2 = 0;

        for (EnumFacing enumfacing : EnumFacing.values())
        {
            int k2 = this.getRedstonePower(pos.offset(enumfacing), enumfacing);

            if (k2 >= 15)
            {
                return 15;
            }

            if (k2 > j2)
            {
                j2 = k2;
            }
        }

        return j2;
    }

    /**
     * Gets the closest player to the entity within the specified distance.
     */
    @Nullable
    public EntityPlayer getClosestPlayerToEntity(Entity entityIn, double distance)
    {
        return this.getClosestPlayer(entityIn.posX, entityIn.posY, entityIn.posZ, distance, false);
    }

    @Nullable
    public EntityPlayer getNearestPlayerNotCreative(Entity entityIn, double distance)
    {
        return this.getClosestPlayer(entityIn.posX, entityIn.posY, entityIn.posZ, distance, true);
    }

    @Nullable
    public EntityPlayer getClosestPlayer(double posX, double posY, double posZ, double distance, boolean spectator)
    {
        Predicate<Entity> predicate = spectator ? EntitySelectors.CAN_AI_TARGET : EntitySelectors.NOT_SPECTATING;
        return this.getClosestPlayer(posX, posY, posZ, distance, predicate);
    }

    @Nullable
    public EntityPlayer getClosestPlayer(double x, double y, double z, double distance, Predicate<Entity> predicate)
    {
        double d0 = -1.0D;
        EntityPlayer entityplayer = null;

        for (int j2 = 0; j2 < this.playerEntities.size(); ++j2)
        {
            EntityPlayer entityplayer1 = this.playerEntities.get(j2);

            if (predicate.apply(entityplayer1))
            {
                double d1 = entityplayer1.getDistanceSq(x, y, z);

                if ((distance < 0.0D || d1 < distance * distance) && (d0 == -1.0D || d1 < d0))
                {
                    d0 = d1;
                    entityplayer = entityplayer1;
                }
            }
        }

        return entityplayer;
    }

    public boolean isAnyPlayerWithinRangeAt(double x, double y, double z, double range)
    {
        for (int j2 = 0; j2 < this.playerEntities.size(); ++j2)
        {
            EntityPlayer entityplayer = this.playerEntities.get(j2);

            if (EntitySelectors.NOT_SPECTATING.apply(entityplayer))
            {
                double d0 = entityplayer.getDistanceSq(x, y, z);

                if (range < 0.0D || d0 < range * range)
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Nullable
    public EntityPlayer getNearestAttackablePlayer(Entity entityIn, double maxXZDistance, double maxYDistance)
    {
        return this.getNearestAttackablePlayer(entityIn.posX, entityIn.posY, entityIn.posZ, maxXZDistance, maxYDistance, (Function)null, (Predicate)null);
    }

    @Nullable
    public EntityPlayer getNearestAttackablePlayer(BlockPos pos, double maxXZDistance, double maxYDistance)
    {
        return this.getNearestAttackablePlayer((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), maxXZDistance, maxYDistance, (Function)null, (Predicate)null);
    }

    @Nullable
    public EntityPlayer getNearestAttackablePlayer(double posX, double posY, double posZ, double maxXZDistance, double maxYDistance, @Nullable Function<EntityPlayer, Double> playerToDouble, @Nullable Predicate<EntityPlayer> predicate)
    {
        double d0 = -1.0D;
        EntityPlayer entityplayer = null;

        for (int j2 = 0; j2 < this.playerEntities.size(); ++j2)
        {
            EntityPlayer entityplayer1 = this.playerEntities.get(j2);

            if (!entityplayer1.abilities.disableDamage && entityplayer1.isAlive() && !entityplayer1.isSpectator() && (predicate == null || predicate.apply(entityplayer1)))
            {
                double d1 = entityplayer1.getDistanceSq(posX, entityplayer1.posY, posZ);
                double d2 = maxXZDistance;

                if (entityplayer1.isSneaking())
                {
                    d2 = maxXZDistance * 0.800000011920929D;
                }

                if (entityplayer1.isInvisible())
                {
                    float f = entityplayer1.getArmorVisibility();

                    if (f < 0.1F)
                    {
                        f = 0.1F;
                    }

                    d2 *= (double)(0.7F * f);
                }

                if (playerToDouble != null)
                {
                    d2 *= ((Double)MoreObjects.firstNonNull(playerToDouble.apply(entityplayer1), Double.valueOf(1.0D))).doubleValue();
                }

                d2 = net.minecraftforge.common.ForgeHooks.getPlayerVisibilityDistance(entityplayer1, d2, maxYDistance);

                if ((maxYDistance < 0.0D || Math.abs(entityplayer1.posY - posY) < maxYDistance * maxYDistance) && (maxXZDistance < 0.0D || d1 < d2 * d2) && (d0 == -1.0D || d1 < d0))
                {
                    d0 = d1;
                    entityplayer = entityplayer1;
                }
            }
        }

        return entityplayer;
    }

    /**
     * Find a player by name in this world.
     */
    @Nullable
    public EntityPlayer getPlayerEntityByName(String name)
    {
        for (int j2 = 0; j2 < this.playerEntities.size(); ++j2)
        {
            EntityPlayer entityplayer = this.playerEntities.get(j2);

            if (name.equals(entityplayer.func_70005_c_()))
            {
                return entityplayer;
            }
        }

        return null;
    }

    @Nullable
    public EntityPlayer getPlayerEntityByUUID(UUID uuid)
    {
        for (int j2 = 0; j2 < this.playerEntities.size(); ++j2)
        {
            EntityPlayer entityplayer = this.playerEntities.get(j2);

            if (uuid.equals(entityplayer.getUniqueID()))
            {
                return entityplayer;
            }
        }

        return null;
    }

    /**
     * If on MP, sends a quitting packet.
     */
    @SideOnly(Side.CLIENT)
    public void sendQuittingDisconnectingPacket()
    {
    }

    /**
     * Checks whether the session lock file was modified by another process
     */
    public void checkSessionLock() throws MinecraftException
    {
        this.saveHandler.checkSessionLock();
    }

    @SideOnly(Side.CLIENT)
    public void setTotalWorldTime(long worldTime)
    {
        this.worldInfo.setWorldTotalTime(worldTime);
    }

    /**
     * gets the random world seed
     */
    public long getSeed()
    {
        return this.dimension.getSeed();
    }

    public long getGameTime()
    {
        return this.worldInfo.getGameTime();
    }

    public long getDayTime()
    {
        return this.dimension.getWorldTime();
    }

    /**
     * Sets the world time.
     */
    public void setDayTime(long time)
    {
        this.dimension.setWorldTime(time);
    }

    /**
     * Gets the spawn point in the world
     */
    public BlockPos getSpawnPoint()
    {
        BlockPos blockpos1 = this.dimension.getSpawnPoint();

        if (!this.getWorldBorder().contains(blockpos1))
        {
            blockpos1 = this.func_175645_m(new BlockPos(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
        }

        return blockpos1;
    }

    public void setSpawnPoint(BlockPos pos)
    {
        this.dimension.setSpawnPoint(pos);
    }

    /**
     * spwans an entity and loads surrounding chunks
     */
    @SideOnly(Side.CLIENT)
    public void joinEntityInSurroundings(Entity entityIn)
    {
        int j2 = MathHelper.floor(entityIn.posX / 16.0D);
        int k2 = MathHelper.floor(entityIn.posZ / 16.0D);
        int l2 = 2;

        for (int i3 = -2; i3 <= 2; ++i3)
        {
            for (int j3 = -2; j3 <= 2; ++j3)
            {
                this.getChunk(j2 + i3, k2 + j3);
            }
        }

        if (!this.loadedEntityList.contains(entityIn))
        {
            if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinWorldEvent(entityIn, this)))
            this.loadedEntityList.add(entityIn);
        }
    }

    public boolean isBlockModifiable(EntityPlayer player, BlockPos pos)
    {
        return this.dimension.canMineBlock(player, pos);
    }

    public boolean canMineBlockBody(EntityPlayer player, BlockPos pos)
    {
        return true;
    }

    /**
     * sends a Packet 38 (Entity Status) to all tracked players of that entity
     */
    public void setEntityState(Entity entityIn, byte state)
    {
    }

    /**
     * gets the world's chunk provider
     */
    public IChunkProvider getChunkProvider()
    {
        return this.chunkProvider;
    }

    public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam)
    {
        this.getBlockState(pos).onBlockEventReceived(this, pos, eventID, eventParam);
    }

    /**
     * Returns this world's current save handler
     */
    public ISaveHandler getSaveHandler()
    {
        return this.saveHandler;
    }

    /**
     * Returns the world's WorldInfo object
     */
    public WorldInfo getWorldInfo()
    {
        return this.worldInfo;
    }

    /**
     * Gets the GameRules instance.
     */
    public GameRules getGameRules()
    {
        return this.worldInfo.getGameRulesInstance();
    }

    /**
     * Updates the flag that indicates whether or not all players in the world are sleeping.
     */
    public void updateAllPlayersSleepingFlag()
    {
    }

    public float getThunderStrength(float delta)
    {
        return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * delta) * this.getRainStrength(delta);
    }

    /**
     * Sets the strength of the thunder.
     */
    @SideOnly(Side.CLIENT)
    public void setThunderStrength(float strength)
    {
        this.prevThunderingStrength = strength;
        this.thunderingStrength = strength;
    }

    /**
     * Returns rain strength.
     */
    public float getRainStrength(float delta)
    {
        return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * delta;
    }

    /**
     * Sets the strength of the rain.
     */
    @SideOnly(Side.CLIENT)
    public void setRainStrength(float strength)
    {
        this.prevRainingStrength = strength;
        this.rainingStrength = strength;
    }

    /**
     * Returns true if the current thunder strength (weighted with the rain strength) is greater than 0.9
     */
    public boolean isThundering()
    {
        return (double)this.getThunderStrength(1.0F) > 0.9D;
    }

    /**
     * Returns true if the current rain strength is greater than 0.2
     */
    public boolean isRaining()
    {
        return (double)this.getRainStrength(1.0F) > 0.2D;
    }

    /**
     * Check if precipitation is currently happening at a position
     */
    public boolean isRainingAt(BlockPos position)
    {
        if (!this.isRaining())
        {
            return false;
        }
        else if (!this.canSeeSky(position))
        {
            return false;
        }
        else if (this.func_175725_q(position).getY() > position.getY())
        {
            return false;
        }
        else
        {
            Biome biome = this.getBiome(position);

            if (biome.func_76746_c())
            {
                return false;
            }
            else
            {
                return this.func_175708_f(position, false) ? false : biome.func_76738_d();
            }
        }
    }

    public boolean isBlockinHighHumidity(BlockPos pos)
    {
        return this.dimension.isBlockHighHumidity(pos);
    }

    @Nullable
    public MapStorage getMapStorage()
    {
        return this.mapStorage;
    }

    /**
     * Assigns the given String id to the given MapDataBase using the MapStorage, removing any existing ones of the same
     * id.
     */
    public void setData(String dataID, WorldSavedData worldSavedDataIn)
    {
        this.mapStorage.setData(dataID, worldSavedDataIn);
    }

    @Nullable
    public WorldSavedData func_72943_a(Class <? extends WorldSavedData > p_72943_1_, String p_72943_2_)
    {
        return this.mapStorage.func_75742_a(p_72943_1_, p_72943_2_);
    }

    /**
     * Returns an unique new data id from the MapStorage for the given prefix and saves the idCounts map to the
     * 'idcounts' file.
     */
    public int getUniqueDataId(String key)
    {
        return this.mapStorage.getUniqueDataId(key);
    }

    public void playBroadcastSound(int id, BlockPos pos, int data)
    {
        for (int j2 = 0; j2 < this.eventListeners.size(); ++j2)
        {
            ((IWorldEventListener)this.eventListeners.get(j2)).broadcastSound(id, pos, data);
        }
    }

    public void playEvent(int type, BlockPos pos, int data)
    {
        this.playEvent((EntityPlayer)null, type, pos, data);
    }

    public void playEvent(@Nullable EntityPlayer player, int type, BlockPos pos, int data)
    {
        try
        {
            for (int j2 = 0; j2 < this.eventListeners.size(); ++j2)
            {
                ((IWorldEventListener)this.eventListeners.get(j2)).playEvent(player, type, pos, data);
            }
        }
        catch (Throwable throwable3)
        {
            CrashReport crashreport3 = CrashReport.makeCrashReport(throwable3, "Playing level event");
            CrashReportCategory crashreportcategory3 = crashreport3.makeCategory("Level event being played");
            crashreportcategory3.addDetail("Block coordinates", CrashReportCategory.getCoordinateInfo(pos));
            crashreportcategory3.addDetail("Event source", player);
            crashreportcategory3.addDetail("Event type", Integer.valueOf(type));
            crashreportcategory3.addDetail("Event data", Integer.valueOf(data));
            throw new ReportedException(crashreport3);
        }
    }

    /**
     * Returns maximum world height.
     */
    public int getHeight()
    {
        return this.dimension.getHeight();
    }

    /**
     * Returns current world height.
     */
    public int getActualHeight()
    {
        return this.dimension.getActualHeight();
    }

    public Random func_72843_D(int p_72843_1_, int p_72843_2_, int p_72843_3_)
    {
        long j2 = (long)p_72843_1_ * 341873128712L + (long)p_72843_2_ * 132897987541L + this.getWorldInfo().getSeed() + (long)p_72843_3_;
        this.rand.setSeed(j2);
        return this.rand;
    }

    /**
     * Adds some basic stats of the world to the given crash report.
     */
    public CrashReportCategory fillCrashReport(CrashReport report)
    {
        CrashReportCategory crashreportcategory3 = report.makeCategoryDepth("Affected level", 1);
        crashreportcategory3.addDetail("Level name", this.worldInfo == null ? "????" : this.worldInfo.getWorldName());
        crashreportcategory3.addDetail("All players", new ICrashReportDetail<String>()
        {
            public String call()
            {
                return World.this.playerEntities.size() + " total; " + World.this.playerEntities;
            }
        });
        crashreportcategory3.addDetail("Chunk stats", new ICrashReportDetail<String>()
        {
            public String call()
            {
                return World.this.chunkProvider.makeString();
            }
        });

        try
        {
            this.worldInfo.addToCrashReport(crashreportcategory3);
        }
        catch (Throwable throwable3)
        {
            crashreportcategory3.addCrashSectionThrowable("Level Data Unobtainable", throwable3);
        }

        return crashreportcategory3;
    }

    /**
     * Returns horizon height for use in rendering the sky.
     */
    @SideOnly(Side.CLIENT)
    public double getHorizon()
    {
        return dimension.getHorizon();
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
    {
        for (int j2 = 0; j2 < this.eventListeners.size(); ++j2)
        {
            IWorldEventListener iworldeventlistener = this.eventListeners.get(j2);
            iworldeventlistener.sendBlockBreakProgress(breakerId, pos, progress);
        }
    }

    public Calendar func_83015_S()
    {
        if (this.getGameTime() % 600L == 0L)
        {
            this.field_83016_L.setTimeInMillis(MinecraftServer.func_130071_aq());
        }

        return this.field_83016_L;
    }

    @SideOnly(Side.CLIENT)
    public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, @Nullable NBTTagCompound compound)
    {
    }

    public Scoreboard getScoreboard()
    {
        return this.field_96442_D;
    }

    public void updateComparatorOutputLevel(BlockPos pos, Block blockIn)
    {
        for (EnumFacing enumfacing : EnumFacing.BY_INDEX)
        {
            BlockPos blockpos1 = pos.offset(enumfacing);

            if (this.isBlockLoaded(blockpos1))
            {
                IBlockState iblockstate1 = this.getBlockState(blockpos1);

                iblockstate1.getBlock().onNeighborChange(this, blockpos1, pos);
                if (iblockstate1.getBlock().isNormalCube(iblockstate1, this, blockpos1))
                {
                    blockpos1 = blockpos1.offset(enumfacing);
                    iblockstate1 = this.getBlockState(blockpos1);

                    if (iblockstate1.getBlock().getWeakChanges(this, blockpos1))
                    {
                        iblockstate1.getBlock().onNeighborChange(this, blockpos1, pos);
                    }
                }
            }
        }
    }

    public DifficultyInstance getDifficultyForLocation(BlockPos pos)
    {
        long j2 = 0L;
        float f = 0.0F;

        if (this.isBlockLoaded(pos))
        {
            f = this.getCurrentMoonPhaseFactor();
            j2 = this.getChunk(pos).getInhabitedTime();
        }

        return new DifficultyInstance(this.getDifficulty(), this.getDayTime(), j2, f);
    }

    public EnumDifficulty getDifficulty()
    {
        return this.getWorldInfo().getDifficulty();
    }

    public int getSkylightSubtracted()
    {
        return this.skylightSubtracted;
    }

    public void setSkylightSubtracted(int newSkylightSubtracted)
    {
        this.skylightSubtracted = newSkylightSubtracted;
    }

    @SideOnly(Side.CLIENT)
    public int getLastLightningBolt()
    {
        return this.lastLightningBolt;
    }

    public void setLastLightningBolt(int lastLightningBoltIn)
    {
        this.lastLightningBolt = lastLightningBoltIn;
    }

    public VillageCollection getVillageCollection()
    {
        return this.villageCollection;
    }

    public WorldBorder getWorldBorder()
    {
        return this.worldBorder;
    }

    /**
     * Returns true if the chunk is located near the spawn point
     */
    public boolean isSpawnChunk(int x, int z)
    {
        BlockPos blockpos1 = this.getSpawnPoint();
        int j2 = x * 16 + 8 - blockpos1.getX();
        int k2 = z * 16 + 8 - blockpos1.getZ();
        int l2 = 128;
        return j2 >= -128 && j2 <= 128 && k2 >= -128 && k2 <= 128;
    }

    /* ======================================== FORGE START =====================================*/
    /**
     * Determine if the given block is considered solid on the
     * specified side.  Used by placement logic.
     *
     * @param pos Block Position
     * @param side The Side in question
     * @return True if the side is solid
    */
    public boolean isSideSolid(BlockPos pos, EnumFacing side)
    {
       return isSideSolid(pos, side, false);
    }

    /**
     * Determine if the given block is considered solid on the
     * specified side.  Used by placement logic.
     *
     * @param pos Block Position
     * @param side The Side in question
     * @param _default The default to return if the block doesn't exist.
     * @return True if the side is solid
     */
    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default)
    {
        if (!this.isValid(pos)) return _default;

        Chunk chunk = getChunk(pos);
        if (chunk == null || chunk.isEmpty()) return _default;
        return getBlockState(pos).isSideSolid(this, pos, side);
    }

    /**
     * Get the persistent chunks for this world
     *
     * @return
     */
    public com.google.common.collect.ImmutableSetMultimap<net.minecraft.util.math.ChunkPos, net.minecraftforge.common.ForgeChunkManager.Ticket> getPersistentChunks()
    {
        return net.minecraftforge.common.ForgeChunkManager.getPersistentChunksFor(this);
    }

    public Iterator<Chunk> getPersistentChunkIterable(Iterator<Chunk> chunkIterator)
    {
        return net.minecraftforge.common.ForgeChunkManager.getPersistentChunksIterableFor(this, chunkIterator);
    }
    /**
     * Readded as it was removed, very useful helper function
     *
     * @param pos Block position
     * @return The blocks light opacity
     */
    public int getBlockLightOpacity(BlockPos pos)
    {
        if (!this.isValid(pos)) return 0;
        return getChunk(pos).func_177437_b(pos);
    }

    /**
     * Returns a count of entities that classify themselves as the specified creature type.
     */
    public int countEntities(net.minecraft.entity.EnumCreatureType type, boolean forSpawnCount)
    {
        int count = 0;
        for (int x = 0; x < loadedEntityList.size(); x++)
        {
            if (((Entity)loadedEntityList.get(x)).isCreatureType(type, forSpawnCount))
            {
                count++;
            }
        }
        return count;
    }

    @Deprecated // remove in 1.13
    public void markTileEntitiesInChunkForRemoval(Chunk chunk)
    {
        for (TileEntity tileentity : chunk.getTileEntityMap().values())
        {
            markTileEntityForRemoval(tileentity);
        }
    }

    protected void initCapabilities()
    {
        net.minecraftforge.common.capabilities.ICapabilityProvider parent = dimension.initCapabilities();
        capabilities = net.minecraftforge.event.ForgeEventFactory.gatherCapabilities(this, parent);
        net.minecraftforge.common.util.WorldCapabilityData data = (net.minecraftforge.common.util.WorldCapabilityData)perWorldStorage.func_75742_a(net.minecraftforge.common.util.WorldCapabilityData.class, net.minecraftforge.common.util.WorldCapabilityData.ID);
        if (data == null)
        {
            capabilityData = new net.minecraftforge.common.util.WorldCapabilityData(capabilities);
            perWorldStorage.setData(capabilityData.name, capabilityData);
        }
        else
        {
            capabilityData = data;
            capabilityData.setCapabilities(dimension, capabilities);
        }
    }
    @Override
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capabilities == null ? false : capabilities.hasCapability(capability, facing);
    }
    @Override
    @Nullable
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capabilities == null ? null : capabilities.getCapability(capability, facing);
    }

    protected MapStorage perWorldStorage; //Moved to a getter to simulate final without being final so we can load in subclasses.
    public MapStorage getPerWorldStorage()
    {
        return perWorldStorage;
    }

    public void sendPacketToServer(Packet<?> packetIn)
    {
        throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
    }

    public LootTableManager func_184146_ak()
    {
        return this.field_184151_B;
    }

    @Nullable
    public BlockPos func_190528_a(String p_190528_1_, BlockPos p_190528_2_, boolean p_190528_3_)
    {
        return null;
    }
}