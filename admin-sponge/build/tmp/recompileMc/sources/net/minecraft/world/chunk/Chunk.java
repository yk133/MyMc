package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkGeneratorDebug;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk implements net.minecraftforge.common.capabilities.ICapabilityProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ExtendedBlockStorage EMPTY_SECTION = null;
    /**
     * Used to store block IDs, block MSBs, Sky-light maps, Block-light maps, and metadata. Each entry corresponds to a
     * logical segment of 16x16x16 blocks, stacked vertically.
     */
    private final ExtendedBlockStorage[] sections;
    /** Contains a 16x16 mapping on the X/Z plane of the biome ID to which each colum belongs. */
    private final byte[] blockBiomeArray;
    private final int[] field_76638_b;
    /** Which columns need their skylightMaps updated. */
    private final boolean[] updateSkylightColumns;
    /** Whether or not this Chunk is currently loaded into the World */
    private boolean loaded;
    /** Reference to the World object. */
    private final World world;
    private final int[] heightMap;
    /** The x coordinate of the chunk. */
    public final int x;
    /** The z coordinate of the chunk. */
    public final int z;
    private boolean isGapLightingUpdated;
    /** A Map of ChunkPositions to TileEntities in this chunk */
    private final Map<BlockPos, TileEntity> tileEntities;
    /** Array of Lists containing the entities in this Chunk. Each List represents a 16 block subchunk. */
    private final ClassInheritanceMultiMap<Entity>[] entityLists;
    private boolean field_76646_k;
    private boolean field_150814_l;
    private boolean ticked;
    /** Set to true if the chunk has been modified and needs to be updated internally. */
    private boolean dirty;
    /** Whether this Chunk has any Entities and thus requires saving on every tick */
    private boolean hasEntities;
    /** The time according to World.worldTime when this chunk was last saved */
    private long lastSaveTime;
    /** Lowest value in the heightmap. */
    private int heightMapMinimum;
    /** the cumulative number of ticks players have been in this chunk */
    private long inhabitedTime;
    /** Contains the current round-robin relight check index, and is implied as the relight check location as well. */
    private int queuedLightChecks;
    /** Queue containing the BlockPos of tile entities queued for creation */
    private final ConcurrentLinkedQueue<BlockPos> tileEntityPosQueue;
    public boolean unloadQueued;

    public Chunk(World p_i1995_1_, int p_i1995_2_, int p_i1995_3_)
    {
        this.sections = new ExtendedBlockStorage[16];
        this.blockBiomeArray = new byte[256];
        this.field_76638_b = new int[256];
        this.updateSkylightColumns = new boolean[256];
        this.tileEntities = Maps.<BlockPos, TileEntity>newHashMap();
        this.queuedLightChecks = 4096;
        this.tileEntityPosQueue = Queues.<BlockPos>newConcurrentLinkedQueue();
        this.entityLists = (ClassInheritanceMultiMap[])(new ClassInheritanceMultiMap[16]);
        this.world = p_i1995_1_;
        this.x = p_i1995_2_;
        this.z = p_i1995_3_;
        this.heightMap = new int[256];

        for (int i = 0; i < this.entityLists.length; ++i)
        {
            this.entityLists[i] = new ClassInheritanceMultiMap(Entity.class);
        }

        Arrays.fill(this.field_76638_b, -999);
        Arrays.fill(this.blockBiomeArray, (byte) - 1);
        capabilities = net.minecraftforge.event.ForgeEventFactory.gatherCapabilities(this);
    }

    public Chunk(World p_i45645_1_, ChunkPrimer p_i45645_2_, int p_i45645_3_, int p_i45645_4_)
    {
        this(p_i45645_1_, p_i45645_3_, p_i45645_4_);
        int i = 256;
        boolean flag = p_i45645_1_.dimension.hasSkyLight();

        for (int j = 0; j < 16; ++j)
        {
            for (int k = 0; k < 16; ++k)
            {
                for (int l = 0; l < 256; ++l)
                {
                    IBlockState iblockstate = p_i45645_2_.func_177856_a(j, l, k);

                    if (iblockstate.getMaterial() != Material.AIR)
                    {
                        int i1 = l >> 4;

                        if (this.sections[i1] == EMPTY_SECTION)
                        {
                            this.sections[i1] = new ExtendedBlockStorage(i1 << 4, flag);
                        }

                        this.sections[i1].set(j, l & 15, k, iblockstate);
                    }
                }
            }
        }
    }

    /**
     * Checks whether the chunk is at the X/Z location specified
     */
    public boolean isAtLocation(int x, int z)
    {
        return x == this.x && z == this.z;
    }

    public int func_177433_f(BlockPos p_177433_1_)
    {
        return this.func_76611_b(p_177433_1_.getX() & 15, p_177433_1_.getZ() & 15);
    }

    public int func_76611_b(int p_76611_1_, int p_76611_2_)
    {
        return this.heightMap[p_76611_2_ << 4 | p_76611_1_];
    }

    @Nullable
    private ExtendedBlockStorage getLastExtendedBlockStorage()
    {
        for (int i = this.sections.length - 1; i >= 0; --i)
        {
            if (this.sections[i] != EMPTY_SECTION)
            {
                return this.sections[i];
            }
        }

        return null;
    }

    /**
     * Returns the topmost ExtendedBlockStorage instance for this Chunk that actually contains a block.
     */
    public int getTopFilledSegment()
    {
        ExtendedBlockStorage extendedblockstorage = this.getLastExtendedBlockStorage();
        return extendedblockstorage == null ? 0 : extendedblockstorage.getYLocation();
    }

    /**
     * Returns the ExtendedBlockStorage array for this Chunk.
     */
    public ExtendedBlockStorage[] getSections()
    {
        return this.sections;
    }

    /**
     * Generates the height map for a chunk from scratch
     */
    @SideOnly(Side.CLIENT)
    protected void generateHeightMap()
    {
        int i = this.getTopFilledSegment();
        this.heightMapMinimum = Integer.MAX_VALUE;

        for (int j = 0; j < 16; ++j)
        {
            for (int k = 0; k < 16; ++k)
            {
                this.field_76638_b[j + (k << 4)] = -999;

                for (int l = i + 16; l > 0; --l)
                {
                    IBlockState iblockstate = this.getBlockState(j, l - 1, k);

                    if (this.getBlockLightOpacity(j, l - 1, k) != 0)
                    {
                        this.heightMap[k << 4 | j] = l;

                        if (l < this.heightMapMinimum)
                        {
                            this.heightMapMinimum = l;
                        }

                        break;
                    }
                }
            }
        }

        this.dirty = true;
    }

    /**
     * Generates the initial skylight map for the chunk upon generation or load.
     */
    public void generateSkylightMap()
    {
        int i = this.getTopFilledSegment();
        this.heightMapMinimum = Integer.MAX_VALUE;

        for (int j = 0; j < 16; ++j)
        {
            for (int k = 0; k < 16; ++k)
            {
                this.field_76638_b[j + (k << 4)] = -999;

                for (int l = i + 16; l > 0; --l)
                {
                    if (this.getBlockLightOpacity(j, l - 1, k) != 0)
                    {
                        this.heightMap[k << 4 | j] = l;

                        if (l < this.heightMapMinimum)
                        {
                            this.heightMapMinimum = l;
                        }

                        break;
                    }
                }

                if (this.world.dimension.hasSkyLight())
                {
                    int k1 = 15;
                    int i1 = i + 16 - 1;

                    while (true)
                    {
                        int j1 = this.getBlockLightOpacity(j, i1, k);

                        if (j1 == 0 && k1 != 15)
                        {
                            j1 = 1;
                        }

                        k1 -= j1;

                        if (k1 > 0)
                        {
                            ExtendedBlockStorage extendedblockstorage = this.sections[i1 >> 4];

                            if (extendedblockstorage != EMPTY_SECTION)
                            {
                                extendedblockstorage.setSkyLight(j, i1 & 15, k, k1);
                                this.world.notifyLightSet(new BlockPos((this.x << 4) + j, i1, (this.z << 4) + k));
                            }
                        }

                        --i1;

                        if (i1 <= 0 || k1 <= 0)
                        {
                            break;
                        }
                    }
                }
            }
        }

        this.dirty = true;
    }

    /**
     * Propagates a given sky-visible block's light value downward and upward to neighboring blocks as necessary.
     */
    private void propagateSkylightOcclusion(int x, int z)
    {
        this.updateSkylightColumns[x + z * 16] = true;
        this.isGapLightingUpdated = true;
    }

    private void recheckGaps(boolean onlyOne)
    {
        this.world.profiler.startSection("recheckGaps");

        if (this.world.func_175697_a(new BlockPos(this.x * 16 + 8, 0, this.z * 16 + 8), 16))
        {
            for (int i = 0; i < 16; ++i)
            {
                for (int j = 0; j < 16; ++j)
                {
                    if (this.updateSkylightColumns[i + j * 16])
                    {
                        this.updateSkylightColumns[i + j * 16] = false;
                        int k = this.func_76611_b(i, j);
                        int l = this.x * 16 + i;
                        int i1 = this.z * 16 + j;
                        int j1 = Integer.MAX_VALUE;

                        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                        {
                            j1 = Math.min(j1, this.world.getChunksLowestHorizon(l + enumfacing.getXOffset(), i1 + enumfacing.getZOffset()));
                        }

                        this.checkSkylightNeighborHeight(l, i1, j1);

                        for (EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL)
                        {
                            this.checkSkylightNeighborHeight(l + enumfacing1.getXOffset(), i1 + enumfacing1.getZOffset(), k);
                        }

                        if (onlyOne)
                        {
                            this.world.profiler.endSection();
                            return;
                        }
                    }
                }
            }

            this.isGapLightingUpdated = false;
        }

        this.world.profiler.endSection();
    }

    /**
     * Checks the height of a block next to a sky-visible block and schedules a lighting update as necessary.
     */
    private void checkSkylightNeighborHeight(int x, int z, int maxValue)
    {
        int i = this.world.func_175645_m(new BlockPos(x, 0, z)).getY();

        if (i > maxValue)
        {
            this.updateSkylightNeighborHeight(x, z, maxValue, i + 1);
        }
        else if (i < maxValue)
        {
            this.updateSkylightNeighborHeight(x, z, i, maxValue + 1);
        }
    }

    private void updateSkylightNeighborHeight(int x, int z, int startY, int endY)
    {
        if (endY > startY && this.world.func_175697_a(new BlockPos(x, 0, z), 16))
        {
            for (int i = startY; i < endY; ++i)
            {
                this.world.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x, i, z));
            }

            this.dirty = true;
        }
    }

    /**
     * Initiates the recalculation of both the block-light and sky-light for a given block inside a chunk.
     */
    private void relightBlock(int x, int y, int z)
    {
        int i = this.heightMap[z << 4 | x] & 255;
        int j = i;

        if (y > i)
        {
            j = y;
        }

        while (j > 0 && this.getBlockLightOpacity(x, j - 1, z) == 0)
        {
            --j;
        }

        if (j != i)
        {
            this.world.markBlocksDirtyVertical(x + this.x * 16, z + this.z * 16, j, i);
            this.heightMap[z << 4 | x] = j;
            int k = this.x * 16 + x;
            int l = this.z * 16 + z;

            if (this.world.dimension.hasSkyLight())
            {
                if (j < i)
                {
                    for (int j1 = j; j1 < i; ++j1)
                    {
                        ExtendedBlockStorage extendedblockstorage2 = this.sections[j1 >> 4];

                        if (extendedblockstorage2 != EMPTY_SECTION)
                        {
                            extendedblockstorage2.setSkyLight(x, j1 & 15, z, 15);
                            this.world.notifyLightSet(new BlockPos((this.x << 4) + x, j1, (this.z << 4) + z));
                        }
                    }
                }
                else
                {
                    for (int i1 = i; i1 < j; ++i1)
                    {
                        ExtendedBlockStorage extendedblockstorage = this.sections[i1 >> 4];

                        if (extendedblockstorage != EMPTY_SECTION)
                        {
                            extendedblockstorage.setSkyLight(x, i1 & 15, z, 0);
                            this.world.notifyLightSet(new BlockPos((this.x << 4) + x, i1, (this.z << 4) + z));
                        }
                    }
                }

                int k1 = 15;

                while (j > 0 && k1 > 0)
                {
                    --j;
                    int i2 = this.getBlockLightOpacity(x, j, z);

                    if (i2 == 0)
                    {
                        i2 = 1;
                    }

                    k1 -= i2;

                    if (k1 < 0)
                    {
                        k1 = 0;
                    }

                    ExtendedBlockStorage extendedblockstorage1 = this.sections[j >> 4];

                    if (extendedblockstorage1 != EMPTY_SECTION)
                    {
                        extendedblockstorage1.setSkyLight(x, j & 15, z, k1);
                    }
                }
            }

            int l1 = this.heightMap[z << 4 | x];
            int j2 = i;
            int k2 = l1;

            if (l1 < i)
            {
                j2 = l1;
                k2 = i;
            }

            if (l1 < this.heightMapMinimum)
            {
                this.heightMapMinimum = l1;
            }

            if (this.world.dimension.hasSkyLight())
            {
                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                {
                    this.updateSkylightNeighborHeight(k + enumfacing.getXOffset(), l + enumfacing.getZOffset(), j2, k2);
                }

                this.updateSkylightNeighborHeight(k, l, j2, k2);
            }

            this.dirty = true;
        }
    }

    public int func_177437_b(BlockPos p_177437_1_)
    {
        return this.func_177435_g(p_177437_1_).getLightOpacity(this.world, p_177437_1_);
    }

    private int getBlockLightOpacity(int x, int y, int z)
    {
        IBlockState state = this.getBlockState(x, y, z); //Forge: Can sometimes be called before we are added to the global world list. So use the less accurate one during that. It'll be recalculated later
        return !loaded ? state.func_185891_c() : state.getLightOpacity(world, new BlockPos(this.x << 4 | x & 15, y, this.z << 4 | z & 15));
    }

    public IBlockState func_177435_g(BlockPos p_177435_1_)
    {
        return this.getBlockState(p_177435_1_.getX(), p_177435_1_.getY(), p_177435_1_.getZ());
    }

    public IBlockState getBlockState(final int x, final int y, final int z)
    {
        if (this.world.getWorldType() == WorldType.DEBUG_ALL_BLOCK_STATES)
        {
            IBlockState iblockstate = null;

            if (y == 60)
            {
                iblockstate = Blocks.BARRIER.getDefaultState();
            }

            if (y == 70)
            {
                iblockstate = ChunkGeneratorDebug.getBlockStateFor(x, z);
            }

            return iblockstate == null ? Blocks.AIR.getDefaultState() : iblockstate;
        }
        else
        {
            try
            {
                if (y >= 0 && y >> 4 < this.sections.length)
                {
                    ExtendedBlockStorage extendedblockstorage = this.sections[y >> 4];

                    if (extendedblockstorage != EMPTY_SECTION)
                    {
                        return extendedblockstorage.get(x & 15, y & 15, z & 15);
                    }
                }

                return Blocks.AIR.getDefaultState();
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting block state");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being got");
                crashreportcategory.addDetail("Location", new ICrashReportDetail<String>()
                {
                    public String call() throws Exception
                    {
                        return CrashReportCategory.getCoordinateInfo(x, y, z);
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
    }

    @Nullable
    public IBlockState setBlockState(BlockPos pos, IBlockState state)
    {
        int i = pos.getX() & 15;
        int j = pos.getY();
        int k = pos.getZ() & 15;
        int l = k << 4 | i;

        if (j >= this.field_76638_b[l] - 1)
        {
            this.field_76638_b[l] = -999;
        }

        int i1 = this.heightMap[l];
        IBlockState iblockstate = this.func_177435_g(pos);

        if (iblockstate == state)
        {
            return null;
        }
        else
        {
            Block block = state.getBlock();
            Block block1 = iblockstate.getBlock();
            int k1 = iblockstate.getLightOpacity(this.world, pos); // Relocate old light value lookup here, so that it is called before TE is removed.
            ExtendedBlockStorage extendedblockstorage = this.sections[j >> 4];
            boolean flag = false;

            if (extendedblockstorage == EMPTY_SECTION)
            {
                if (block == Blocks.AIR)
                {
                    return null;
                }

                extendedblockstorage = new ExtendedBlockStorage(j >> 4 << 4, this.world.dimension.hasSkyLight());
                this.sections[j >> 4] = extendedblockstorage;
                flag = j >= i1;
            }

            extendedblockstorage.set(i, j & 15, k, state);

            //if (block1 != block)
            {
                if (!this.world.isRemote)
                {
                    if (block1 != block) //Only fire block breaks when the block changes.
                    block1.func_180663_b(this.world, pos, iblockstate);
                    TileEntity te = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
                    if (te != null && te.shouldRefresh(this.world, pos, iblockstate, state)) this.world.removeTileEntity(pos);
                }
                else if (block1.hasTileEntity(iblockstate))
                {
                    TileEntity te = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
                    if (te != null && te.shouldRefresh(this.world, pos, iblockstate, state))
                    this.world.removeTileEntity(pos);
                }
            }

            if (extendedblockstorage.get(i, j & 15, k).getBlock() != block)
            {
                return null;
            }
            else
            {
                if (flag)
                {
                    this.generateSkylightMap();
                }
                else
                {
                    int j1 = state.getLightOpacity(this.world, pos);

                    if (j1 > 0)
                    {
                        if (j >= i1)
                        {
                            this.relightBlock(i, j + 1, k);
                        }
                    }
                    else if (j == i1 - 1)
                    {
                        this.relightBlock(i, j, k);
                    }

                    if (j1 != k1 && (j1 < k1 || this.getLightFor(EnumSkyBlock.SKY, pos) > 0 || this.getLightFor(EnumSkyBlock.BLOCK, pos) > 0))
                    {
                        this.propagateSkylightOcclusion(i, k);
                    }
                }

                // If capturing blocks, only run block physics for TE's. Non-TE's are handled in ForgeHooks.onPlaceItemIntoWorld
                if (!this.world.isRemote && block1 != block && (!this.world.captureBlockSnapshots || block.hasTileEntity(state)))
                {
                    block.func_176213_c(this.world, pos, state);
                }

                if (block.hasTileEntity(state))
                {
                    TileEntity tileentity1 = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);

                    if (tileentity1 == null)
                    {
                        tileentity1 = block.createTileEntity(this.world, state);
                        this.world.setTileEntity(pos, tileentity1);
                    }

                    if (tileentity1 != null)
                    {
                        tileentity1.updateContainingBlockInfo();
                    }
                }

                this.dirty = true;
                return iblockstate;
            }
        }
    }

    public int getLightFor(EnumSkyBlock type, BlockPos pos)
    {
        int i = pos.getX() & 15;
        int j = pos.getY();
        int k = pos.getZ() & 15;
        ExtendedBlockStorage extendedblockstorage = this.sections[j >> 4];

        if (extendedblockstorage == EMPTY_SECTION)
        {
            return this.canSeeSky(pos) ? type.defaultLightValue : 0;
        }
        else if (type == EnumSkyBlock.SKY)
        {
            return !this.world.dimension.hasSkyLight() ? 0 : extendedblockstorage.getSkyLight(i, j & 15, k);
        }
        else
        {
            return type == EnumSkyBlock.BLOCK ? extendedblockstorage.getBlockLight(i, j & 15, k) : type.defaultLightValue;
        }
    }

    public void setLightFor(EnumSkyBlock type, BlockPos pos, int value)
    {
        int i = pos.getX() & 15;
        int j = pos.getY();
        int k = pos.getZ() & 15;
        ExtendedBlockStorage extendedblockstorage = this.sections[j >> 4];

        if (extendedblockstorage == EMPTY_SECTION)
        {
            extendedblockstorage = new ExtendedBlockStorage(j >> 4 << 4, this.world.dimension.hasSkyLight());
            this.sections[j >> 4] = extendedblockstorage;
            this.generateSkylightMap();
        }

        this.dirty = true;

        if (type == EnumSkyBlock.SKY)
        {
            if (this.world.dimension.hasSkyLight())
            {
                extendedblockstorage.setSkyLight(i, j & 15, k, value);
            }
        }
        else if (type == EnumSkyBlock.BLOCK)
        {
            extendedblockstorage.setBlockLight(i, j & 15, k, value);
        }
    }

    public int getLightSubtracted(BlockPos pos, int amount)
    {
        int i = pos.getX() & 15;
        int j = pos.getY();
        int k = pos.getZ() & 15;
        ExtendedBlockStorage extendedblockstorage = this.sections[j >> 4];

        if (extendedblockstorage == EMPTY_SECTION)
        {
            return this.world.dimension.hasSkyLight() && amount < EnumSkyBlock.SKY.defaultLightValue ? EnumSkyBlock.SKY.defaultLightValue - amount : 0;
        }
        else
        {
            int l = !this.world.dimension.hasSkyLight() ? 0 : extendedblockstorage.getSkyLight(i, j & 15, k);
            l = l - amount;
            int i1 = extendedblockstorage.getBlockLight(i, j & 15, k);

            if (i1 > l)
            {
                l = i1;
            }

            return l;
        }
    }

    /**
     * Adds an entity to the chunk.
     */
    public void addEntity(Entity entityIn)
    {
        this.hasEntities = true;
        int i = MathHelper.floor(entityIn.posX / 16.0D);
        int j = MathHelper.floor(entityIn.posZ / 16.0D);

        if (i != this.x || j != this.z)
        {
            LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(this.x), Integer.valueOf(this.z), entityIn);
            entityIn.remove();
        }

        int k = MathHelper.floor(entityIn.posY / 16.0D);

        if (k < 0)
        {
            k = 0;
        }

        if (k >= this.entityLists.length)
        {
            k = this.entityLists.length - 1;
        }

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityEvent.EnteringChunk(entityIn, this.x, this.z, entityIn.chunkCoordX, entityIn.chunkCoordZ));
        entityIn.addedToChunk = true;
        entityIn.chunkCoordX = this.x;
        entityIn.chunkCoordY = k;
        entityIn.chunkCoordZ = this.z;
        this.entityLists[k].add(entityIn);
        this.markDirty(); // Forge - ensure chunks are marked to save after an entity add
    }

    /**
     * removes entity using its y chunk coordinate as its index
     */
    public void removeEntity(Entity entityIn)
    {
        this.removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
    }

    /**
     * Removes entity at the specified index from the entity array.
     */
    public void removeEntityAtIndex(Entity entityIn, int index)
    {
        if (index < 0)
        {
            index = 0;
        }

        if (index >= this.entityLists.length)
        {
            index = this.entityLists.length - 1;
        }

        this.entityLists[index].remove(entityIn);
        this.markDirty(); // Forge - ensure chunks are marked to save after entity removals
    }

    public boolean canSeeSky(BlockPos pos)
    {
        int i = pos.getX() & 15;
        int j = pos.getY();
        int k = pos.getZ() & 15;
        return j >= this.heightMap[k << 4 | i];
    }

    @Nullable
    private TileEntity createNewTileEntity(BlockPos pos)
    {
        IBlockState iblockstate = this.func_177435_g(pos);
        Block block = iblockstate.getBlock();
        return !block.hasTileEntity(iblockstate) ? null : block.createTileEntity(this.world, iblockstate);
    }

    @Nullable
    public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType creationMode)
    {
        TileEntity tileentity = this.tileEntities.get(pos);

        if (tileentity != null && tileentity.isRemoved())
        {
            tileEntities.remove(pos);
            tileentity = null;
        }

        if (tileentity == null)
        {
            if (creationMode == Chunk.EnumCreateEntityType.IMMEDIATE)
            {
                tileentity = this.createNewTileEntity(pos);
                this.world.setTileEntity(pos, tileentity);
            }
            else if (creationMode == Chunk.EnumCreateEntityType.QUEUED)
            {
                this.tileEntityPosQueue.add(pos.toImmutable());
            }
        }

        return tileentity;
    }

    public void addTileEntity(TileEntity tileEntityIn)
    {
        this.addTileEntity(tileEntityIn.getPos(), tileEntityIn);

        if (this.loaded)
        {
            this.world.addTileEntity(tileEntityIn);
        }
    }

    public void addTileEntity(BlockPos pos, TileEntity tileEntityIn)
    {
        if (tileEntityIn.getWorld() != this.world) //Forge don't call unless it's changed, could screw up bad mods.
        tileEntityIn.setWorld(this.world);
        tileEntityIn.setPos(pos);

        if (this.func_177435_g(pos).getBlock().hasTileEntity(this.func_177435_g(pos)))
        {
            if (this.tileEntities.containsKey(pos))
            {
                ((TileEntity)this.tileEntities.get(pos)).remove();
            }

            tileEntityIn.validate();
            this.tileEntities.put(pos, tileEntityIn);
        }
    }

    public void removeTileEntity(BlockPos pos)
    {
        if (this.loaded)
        {
            TileEntity tileentity = this.tileEntities.remove(pos);

            if (tileentity != null)
            {
                tileentity.remove();
            }
        }
    }

    /**
     * Called when this Chunk is loaded by the ChunkProvider
     */
    public void onLoad()
    {
        this.loaded = true;
        this.world.addTileEntities(this.tileEntities.values());

        for (ClassInheritanceMultiMap<Entity> classinheritancemultimap : this.entityLists)
        {
            this.world.loadEntities(com.google.common.collect.ImmutableList.copyOf(classinheritancemultimap));
        }
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Load(this));
    }

    /**
     * Called when this Chunk is unloaded by the ChunkProvider
     */
    public void onUnload()
    {
        java.util.Arrays.stream(entityLists).forEach(multimap -> com.google.common.collect.Lists.newArrayList(multimap.getByClass(net.minecraft.entity.player.EntityPlayer.class)).forEach(player -> world.tickEntity(player, false))); // FORGE - Fix for MC-92916
        this.loaded = false;

        for (TileEntity tileentity : this.tileEntities.values())
        {
            this.world.markTileEntityForRemoval(tileentity);
        }

        for (ClassInheritanceMultiMap<Entity> classinheritancemultimap : this.entityLists)
        {
            this.world.unloadEntities(classinheritancemultimap);
        }
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Unload(this));
    }

    /**
     * Sets the isModified flag for this Chunk
     */
    public void markDirty()
    {
        this.dirty = true;
    }

    /**
     * Fills the given list of all entities that intersect within the given bounding box that aren't the passed entity.
     */
    public void getEntitiesWithinAABBForEntity(@Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate <? super Entity > filter)
    {
        int i = MathHelper.floor((aabb.minY - World.MAX_ENTITY_RADIUS) / 16.0D);
        int j = MathHelper.floor((aabb.maxY + World.MAX_ENTITY_RADIUS) / 16.0D);
        i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
        j = MathHelper.clamp(j, 0, this.entityLists.length - 1);

        for (int k = i; k <= j; ++k)
        {
            if (!this.entityLists[k].isEmpty())
            {
                for (Entity entity : this.entityLists[k])
                {
                    if (entity.getBoundingBox().intersects(aabb) && entity != entityIn)
                    {
                        if (filter == null || filter.apply(entity))
                        {
                            listToFill.add(entity);
                        }

                        Entity[] aentity = entity.getParts();

                        if (aentity != null)
                        {
                            for (Entity entity1 : aentity)
                            {
                                if (entity1 != entityIn && entity1.getBoundingBox().intersects(aabb) && (filter == null || filter.apply(entity1)))
                                {
                                    listToFill.add(entity1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets all entities that can be assigned to the specified class.
     */
    public <T extends Entity> void getEntitiesOfTypeWithinAABB(Class <? extends T > entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate <? super T > filter)
    {
        int i = MathHelper.floor((aabb.minY - World.MAX_ENTITY_RADIUS) / 16.0D);
        int j = MathHelper.floor((aabb.maxY + World.MAX_ENTITY_RADIUS) / 16.0D);
        i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
        j = MathHelper.clamp(j, 0, this.entityLists.length - 1);

        for (int k = i; k <= j; ++k)
        {
            for (T t : this.entityLists[k].getByClass(entityClass))
            {
                if (t.getBoundingBox().intersects(aabb) && (filter == null || filter.apply(t)))
                {
                    listToFill.add(t);
                }
            }
        }
    }

    /**
     * Returns true if this Chunk needs to be saved
     */
    public boolean needsSaving(boolean p_76601_1_)
    {
        if (p_76601_1_)
        {
            if (this.hasEntities && this.world.getGameTime() != this.lastSaveTime || this.dirty)
            {
                return true;
            }
        }
        else if (this.hasEntities && this.world.getGameTime() >= this.lastSaveTime + 600L)
        {
            return true;
        }

        return this.dirty;
    }

    public Random func_76617_a(long p_76617_1_)
    {
        return new Random(this.world.getSeed() + (long)(this.x * this.x * 4987142) + (long)(this.x * 5947611) + (long)(this.z * this.z) * 4392871L + (long)(this.z * 389711) ^ p_76617_1_);
    }

    public boolean isEmpty()
    {
        return false;
    }

    public void func_186030_a(IChunkProvider p_186030_1_, IChunkGenerator p_186030_2_)
    {
        Chunk chunk = p_186030_1_.getLoadedChunk(this.x, this.z - 1);
        Chunk chunk1 = p_186030_1_.getLoadedChunk(this.x + 1, this.z);
        Chunk chunk2 = p_186030_1_.getLoadedChunk(this.x, this.z + 1);
        Chunk chunk3 = p_186030_1_.getLoadedChunk(this.x - 1, this.z);

        if (chunk1 != null && chunk2 != null && p_186030_1_.getLoadedChunk(this.x + 1, this.z + 1) != null)
        {
            this.func_186034_a(p_186030_2_);
        }

        if (chunk3 != null && chunk2 != null && p_186030_1_.getLoadedChunk(this.x - 1, this.z + 1) != null)
        {
            chunk3.func_186034_a(p_186030_2_);
        }

        if (chunk != null && chunk1 != null && p_186030_1_.getLoadedChunk(this.x + 1, this.z - 1) != null)
        {
            chunk.func_186034_a(p_186030_2_);
        }

        if (chunk != null && chunk3 != null)
        {
            Chunk chunk4 = p_186030_1_.getLoadedChunk(this.x - 1, this.z - 1);

            if (chunk4 != null)
            {
                chunk4.func_186034_a(p_186030_2_);
            }
        }
    }

    protected void func_186034_a(IChunkGenerator p_186034_1_)
    {
        if (populating != null && net.minecraftforge.common.ForgeModContainer.logCascadingWorldGeneration) logCascadingWorldGeneration();
        ChunkPos prev = populating;
        populating = this.getPos();
        if (this.func_177419_t())
        {
            if (p_186034_1_.func_185933_a(this, this.x, this.z))
            {
                this.markDirty();
            }
        }
        else
        {
            this.func_150809_p();
            p_186034_1_.func_185931_b(this.x, this.z);
            net.minecraftforge.fml.common.registry.GameRegistry.generateWorld(this.x, this.z, this.world, p_186034_1_, this.world.getChunkProvider());
            this.markDirty();
        }
        populating = prev;
    }

    public BlockPos func_177440_h(BlockPos p_177440_1_)
    {
        int i = p_177440_1_.getX() & 15;
        int j = p_177440_1_.getZ() & 15;
        int k = i | j << 4;
        BlockPos blockpos = new BlockPos(p_177440_1_.getX(), this.field_76638_b[k], p_177440_1_.getZ());

        if (blockpos.getY() == -999)
        {
            int l = this.getTopFilledSegment() + 15;
            blockpos = new BlockPos(p_177440_1_.getX(), l, p_177440_1_.getZ());
            int i1 = -1;

            while (blockpos.getY() > 0 && i1 == -1)
            {
                IBlockState iblockstate = this.func_177435_g(blockpos);
                Material material = iblockstate.getMaterial();

                if (!material.blocksMovement() && !material.isLiquid())
                {
                    blockpos = blockpos.down();
                }
                else
                {
                    i1 = blockpos.getY() + 1;
                }
            }

            this.field_76638_b[k] = i1;
        }

        return new BlockPos(p_177440_1_.getX(), this.field_76638_b[k], p_177440_1_.getZ());
    }

    public void tick(boolean skipRecheckGaps)
    {
        if (this.isGapLightingUpdated && this.world.dimension.hasSkyLight() && !skipRecheckGaps)
        {
            this.recheckGaps(this.world.isRemote);
        }

        this.ticked = true;

        if (!this.field_150814_l && this.field_76646_k)
        {
            this.func_150809_p();
        }

        while (!this.tileEntityPosQueue.isEmpty())
        {
            BlockPos blockpos = this.tileEntityPosQueue.poll();

            if (this.getTileEntity(blockpos, Chunk.EnumCreateEntityType.CHECK) == null && this.func_177435_g(blockpos).getBlock().hasTileEntity(this.func_177435_g(blockpos)))
            {
                TileEntity tileentity = this.createNewTileEntity(blockpos);
                this.world.setTileEntity(blockpos, tileentity);
                this.world.markBlockRangeForRenderUpdate(blockpos, blockpos);
            }
        }
    }

    public boolean isPopulated()
    {
        return this.ticked && this.field_76646_k && this.field_150814_l;
    }

    public boolean wasTicked()
    {
        return this.ticked;
    }

    /**
     * Gets a {@link ChunkPos} representing the x and z coordinates of this chunk.
     */
    public ChunkPos getPos()
    {
        return new ChunkPos(this.x, this.z);
    }

    /**
     * Returns whether the ExtendedBlockStorages containing levels (in blocks) from arg 1 to arg 2 are fully empty
     * (true) or not (false).
     */
    public boolean isEmptyBetween(int startY, int endY)
    {
        if (startY < 0)
        {
            startY = 0;
        }

        if (endY >= 256)
        {
            endY = 255;
        }

        for (int i = startY; i <= endY; i += 16)
        {
            ExtendedBlockStorage extendedblockstorage = this.sections[i >> 4];

            if (extendedblockstorage != EMPTY_SECTION && !extendedblockstorage.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    public void setSections(ExtendedBlockStorage[] newStorageArrays)
    {
        if (this.sections.length != newStorageArrays.length)
        {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", Integer.valueOf(newStorageArrays.length), Integer.valueOf(this.sections.length));
        }
        else
        {
            System.arraycopy(newStorageArrays, 0, this.sections, 0, this.sections.length);
        }
    }

    /**
     * Loads this chunk from the given buffer.
     *  
     * @see net.minecraft.network.play.server.SPacketChunkData#getReadBuffer()
     */
    @SideOnly(Side.CLIENT)
    public void read(PacketBuffer buf, int availableSections, boolean fullChunk)
    {
        for(TileEntity tileEntity : tileEntities.values())
        {
            tileEntity.updateContainingBlockInfo();
            tileEntity.func_145832_p();
            tileEntity.func_145838_q();
        }

        boolean flag = this.world.dimension.hasSkyLight();

        for (int i = 0; i < this.sections.length; ++i)
        {
            ExtendedBlockStorage extendedblockstorage = this.sections[i];

            if ((availableSections & 1 << i) == 0)
            {
                if (fullChunk && extendedblockstorage != EMPTY_SECTION)
                {
                    this.sections[i] = EMPTY_SECTION;
                }
            }
            else
            {
                if (extendedblockstorage == EMPTY_SECTION)
                {
                    extendedblockstorage = new ExtendedBlockStorage(i << 4, flag);
                    this.sections[i] = extendedblockstorage;
                }

                extendedblockstorage.getData().read(buf);
                buf.readBytes(extendedblockstorage.getBlockLight().getData());

                if (flag)
                {
                    buf.readBytes(extendedblockstorage.getSkyLight().getData());
                }
            }
        }

        if (fullChunk)
        {
            buf.readBytes(this.blockBiomeArray);
        }

        for (int j = 0; j < this.sections.length; ++j)
        {
            if (this.sections[j] != EMPTY_SECTION && (availableSections & 1 << j) != 0)
            {
                this.sections[j].recalculateRefCounts();
            }
        }

        this.field_150814_l = true;
        this.field_76646_k = true;
        this.generateHeightMap();

        List<TileEntity> invalidList = new java.util.ArrayList<TileEntity>();

        for (TileEntity tileentity : this.tileEntities.values())
        {
            if (tileentity.shouldRefresh(this.world, tileentity.getPos(), tileentity.func_145838_q().func_176203_a(tileentity.func_145832_p()), func_177435_g(tileentity.getPos())))
                invalidList.add(tileentity);
            tileentity.updateContainingBlockInfo();
        }

        for (TileEntity te : invalidList) te.remove();
    }

    public Biome func_177411_a(BlockPos p_177411_1_, BiomeProvider p_177411_2_)
    {
        int i = p_177411_1_.getX() & 15;
        int j = p_177411_1_.getZ() & 15;
        int k = this.blockBiomeArray[j << 4 | i] & 255;

        if (k == 255)
        {
            Biome biome = p_177411_2_.getBiome(p_177411_1_, Biomes.PLAINS);
            k = Biome.getIdForBiome(biome);
            this.blockBiomeArray[j << 4 | i] = (byte)(k & 255);
        }

        Biome biome1 = Biome.getBiome(k);
        return biome1 == null ? Biomes.PLAINS : biome1;
    }

    public byte[] func_76605_m()
    {
        return this.blockBiomeArray;
    }

    public void func_76616_a(byte[] p_76616_1_)
    {
        if (this.blockBiomeArray.length != p_76616_1_.length)
        {
            LOGGER.warn("Could not set level chunk biomes, array length is {} instead of {}", Integer.valueOf(p_76616_1_.length), Integer.valueOf(this.blockBiomeArray.length));
        }
        else
        {
            System.arraycopy(p_76616_1_, 0, this.blockBiomeArray, 0, this.blockBiomeArray.length);
        }
    }

    /**
     * Resets the relight check index to 0 for this Chunk.
     */
    public void resetRelightChecks()
    {
        this.queuedLightChecks = 0;
    }

    /**
     * Called once-per-chunk-per-tick, and advances the round-robin relight check index by up to 8 blocks at a time. In
     * a worst-case scenario, can potentially take up to 25.6 seconds, calculated via (4096/8)/20, to re-check all
     * blocks in a chunk, which may explain lagging light updates on initial world generation.
     */
    public void enqueueRelightChecks()
    {
        if (this.queuedLightChecks < 4096)
        {
            BlockPos blockpos = new BlockPos(this.x << 4, 0, this.z << 4);

            for (int i = 0; i < 8; ++i)
            {
                if (this.queuedLightChecks >= 4096)
                {
                    return;
                }

                int j = this.queuedLightChecks % 16;
                int k = this.queuedLightChecks / 16 % 16;
                int l = this.queuedLightChecks / 256;
                ++this.queuedLightChecks;

                for (int i1 = 0; i1 < 16; ++i1)
                {
                    BlockPos blockpos1 = blockpos.add(k, (j << 4) + i1, l);
                    boolean flag = i1 == 0 || i1 == 15 || k == 0 || k == 15 || l == 0 || l == 15;

                    if (this.sections[j] == EMPTY_SECTION && flag || this.sections[j] != EMPTY_SECTION && this.sections[j].get(k, i1, l).getBlock().isAir(this.sections[j].get(k, i1, l), this.world, blockpos1))
                    {
                        for (EnumFacing enumfacing : EnumFacing.values())
                        {
                            BlockPos blockpos2 = blockpos1.offset(enumfacing);

                            if (this.world.getBlockState(blockpos2).getLightValue(this.world, blockpos2) > 0)
                            {
                                this.world.checkLight(blockpos2);
                            }
                        }

                        this.world.checkLight(blockpos1);
                    }
                }
            }
        }
    }

    public void func_150809_p()
    {
        this.field_76646_k = true;
        this.field_150814_l = true;
        BlockPos blockpos = new BlockPos(this.x << 4, 0, this.z << 4);

        if (this.world.dimension.hasSkyLight())
        {
            if (this.world.isAreaLoaded(blockpos.add(-1, 0, -1), blockpos.add(16, this.world.getSeaLevel(), 16)))
            {
                label44:

                for (int i = 0; i < 16; ++i)
                {
                    for (int j = 0; j < 16; ++j)
                    {
                        if (!this.func_150811_f(i, j))
                        {
                            this.field_150814_l = false;
                            break label44;
                        }
                    }
                }

                if (this.field_150814_l)
                {
                    for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                    {
                        int k = enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 16 : 1;
                        this.world.getChunk(blockpos.offset(enumfacing, k)).func_180700_a(enumfacing.getOpposite());
                    }

                    this.func_177441_y();
                }
            }
            else
            {
                this.field_150814_l = false;
            }
        }
    }

    private void func_177441_y()
    {
        for (int i = 0; i < this.updateSkylightColumns.length; ++i)
        {
            this.updateSkylightColumns[i] = true;
        }

        this.recheckGaps(false);
    }

    private void func_180700_a(EnumFacing p_180700_1_)
    {
        if (this.field_76646_k)
        {
            if (p_180700_1_ == EnumFacing.EAST)
            {
                for (int i = 0; i < 16; ++i)
                {
                    this.func_150811_f(15, i);
                }
            }
            else if (p_180700_1_ == EnumFacing.WEST)
            {
                for (int j = 0; j < 16; ++j)
                {
                    this.func_150811_f(0, j);
                }
            }
            else if (p_180700_1_ == EnumFacing.SOUTH)
            {
                for (int k = 0; k < 16; ++k)
                {
                    this.func_150811_f(k, 15);
                }
            }
            else if (p_180700_1_ == EnumFacing.NORTH)
            {
                for (int l = 0; l < 16; ++l)
                {
                    this.func_150811_f(l, 0);
                }
            }
        }
    }

    private boolean func_150811_f(int p_150811_1_, int p_150811_2_)
    {
        int i = this.getTopFilledSegment();
        boolean flag = false;
        boolean flag1 = false;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos((this.x << 4) + p_150811_1_, 0, (this.z << 4) + p_150811_2_);

        for (int j = i + 16 - 1; j > this.world.getSeaLevel() || j > 0 && !flag1; --j)
        {
            blockpos$mutableblockpos.setPos(blockpos$mutableblockpos.getX(), j, blockpos$mutableblockpos.getZ());
            int k = this.func_177437_b(blockpos$mutableblockpos);

            if (k == 255 && blockpos$mutableblockpos.getY() < this.world.getSeaLevel())
            {
                flag1 = true;
            }

            if (!flag && k > 0)
            {
                flag = true;
            }
            else if (flag && k == 0 && !this.world.checkLight(blockpos$mutableblockpos))
            {
                return false;
            }
        }

        for (int l = blockpos$mutableblockpos.getY(); l > 0; --l)
        {
            blockpos$mutableblockpos.setPos(blockpos$mutableblockpos.getX(), l, blockpos$mutableblockpos.getZ());

            if (this.func_177435_g(blockpos$mutableblockpos).getLightValue(this.world, blockpos$mutableblockpos) > 0)
            {
                this.world.checkLight(blockpos$mutableblockpos);
            }
        }

        return true;
    }

    public boolean isLoaded()
    {
        return this.loaded;
    }

    @SideOnly(Side.CLIENT)
    public void markLoaded(boolean loaded)
    {
        this.loaded = loaded;
    }

    public World getWorld()
    {
        return this.world;
    }

    public int[] func_177445_q()
    {
        return this.heightMap;
    }

    public void func_177420_a(int[] p_177420_1_)
    {
        if (this.heightMap.length != p_177420_1_.length)
        {
            LOGGER.warn("Could not set level chunk heightmap, array length is {} instead of {}", Integer.valueOf(p_177420_1_.length), Integer.valueOf(this.heightMap.length));
        }
        else
        {
            System.arraycopy(p_177420_1_, 0, this.heightMap, 0, this.heightMap.length);
            this.heightMapMinimum = com.google.common.primitives.Ints.min(this.heightMap); // Forge: fix MC-117412
        }
    }

    public Map<BlockPos, TileEntity> getTileEntityMap()
    {
        return this.tileEntities;
    }

    public ClassInheritanceMultiMap<Entity>[] getEntityLists()
    {
        return this.entityLists;
    }

    public boolean func_177419_t()
    {
        return this.field_76646_k;
    }

    public void func_177446_d(boolean p_177446_1_)
    {
        this.field_76646_k = p_177446_1_;
    }

    public boolean func_177423_u()
    {
        return this.field_150814_l;
    }

    public void func_177421_e(boolean p_177421_1_)
    {
        this.field_150814_l = p_177421_1_;
    }

    public void setModified(boolean modified)
    {
        this.dirty = modified;
    }

    public void setHasEntities(boolean hasEntitiesIn)
    {
        this.hasEntities = hasEntitiesIn;
    }

    public void setLastSaveTime(long saveTime)
    {
        this.lastSaveTime = saveTime;
    }

    public int getLowestHeight()
    {
        return this.heightMapMinimum;
    }

    public long getInhabitedTime()
    {
        return this.inhabitedTime;
    }

    public void setInhabitedTime(long newInhabitedTime)
    {
        this.inhabitedTime = newInhabitedTime;
    }

    public static enum EnumCreateEntityType
    {
        IMMEDIATE,
        QUEUED,
        CHECK;
    }

    /* ======================================== FORGE START =====================================*/
    /**
     * Removes the tile entity at the specified position, only if it's
     * marked as invalid.
     */
    public void removeInvalidTileEntity(BlockPos pos)
    {
        if (loaded)
        {
            TileEntity entity = (TileEntity)tileEntities.get(pos);
            if (entity != null && entity.isRemoved())
            {
                tileEntities.remove(pos);
            }
        }
    }

    private static ChunkPos populating = null; // keep track of cascading chunk generation during chunk population

    private void logCascadingWorldGeneration()
    {
        net.minecraftforge.fml.common.ModContainer activeModContainer = net.minecraftforge.fml.common.Loader.instance().activeModContainer();
        String format = "{} loaded a new chunk {} in dimension {} ({}) while populating chunk {}, causing cascading worldgen lag.";

        if (activeModContainer == null) { // vanilla minecraft has problems too (MC-114332), log it at a quieter level.
            net.minecraftforge.fml.common.FMLLog.log.debug(format, "Minecraft", this.getPos(), this.world.dimension.getDimension(), this.world.dimension.getType().getName(), populating);
            net.minecraftforge.fml.common.FMLLog.log.debug("Consider setting 'fixVanillaCascading' to 'true' in the Forge config to fix many cases where this occurs in the base game.");
        } else {
            net.minecraftforge.fml.common.FMLLog.log.warn(format, activeModContainer.getName(), this.getPos(), this.world.dimension.getDimension(), this.world.dimension.getType().getName(), populating);
            net.minecraftforge.fml.common.FMLLog.log.warn("Please report this to the mod's issue tracker. This log can be disabled in the Forge config.");
        }
    }

    private final net.minecraftforge.common.capabilities.CapabilityDispatcher capabilities;
    @Nullable
    public net.minecraftforge.common.capabilities.CapabilityDispatcher getCapabilities()
    {
        return capabilities;
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
}