package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.INpc;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ReportedException;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.WorldGeneratorBonusChest;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataCallableSave;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldServer extends World implements IThreadListener
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftServer server;
    /** The entity tracker for this server world. */
    private final EntityTracker entityTracker;
    /** The player chunk map for this server world. */
    private final PlayerChunkMap playerChunkMap;
    private final Set<NextTickListEntry> field_73064_N = Sets.<NextTickListEntry>newHashSet();
    private final TreeSet<NextTickListEntry> field_73065_O = new TreeSet<NextTickListEntry>();
    private final Map<UUID, Entity> entitiesByUuid = Maps.<UUID, Entity>newHashMap();
    /** Whether level saving is disabled or not */
    public boolean disableLevelSaving;
    /** is false if there are no players */
    private boolean allPlayersSleeping;
    private int updateEntityTick;
    /** the teleporter to use when the entity is being transferred into the dimension */
    private final Teleporter worldTeleporter;
    private final WorldEntitySpawner entitySpawner = new WorldEntitySpawner();
    protected final VillageSiege villageSiege = new VillageSiege(this);
    private final WorldServer.ServerBlockEventList[] blockEventQueue = new WorldServer.ServerBlockEventList[] {new WorldServer.ServerBlockEventList(), new WorldServer.ServerBlockEventList()};
    private int field_147489_T;
    private final List<NextTickListEntry> pendingBlockTicks = Lists.<NextTickListEntry>newArrayList();

    /** Stores the recently processed (lighting) chunks */
    protected Set<ChunkPos> doneChunks = new java.util.HashSet<ChunkPos>();
    public List<Teleporter> customTeleporters = new ArrayList<Teleporter>();

    public WorldServer(MinecraftServer server, ISaveHandler saveHandlerIn, WorldInfo info, int dimensionId, Profiler profilerIn)
    {
        super(saveHandlerIn, info, net.minecraftforge.common.DimensionManager.createProviderFor(dimensionId), profilerIn, false);
        this.server = server;
        this.entityTracker = new EntityTracker(this);
        this.playerChunkMap = new PlayerChunkMap(this);
        // Guarantee the dimension ID was not reset by the provider
        int providerDim = this.dimension.getDimension();
        this.dimension.setWorld(this);
        this.dimension.setDimension(providerDim);
        this.chunkProvider = this.createChunkProvider();
        perWorldStorage = new MapStorage(new net.minecraftforge.common.WorldSpecificSaveHandler((WorldServer)this, saveHandlerIn));
        this.worldTeleporter = new Teleporter(this);
        this.calculateInitialSkylight();
        this.calculateInitialWeather();
        this.getWorldBorder().setSize(server.getMaxWorldSize());
        net.minecraftforge.common.DimensionManager.setWorld(dimensionId, this, server);
    }

    public World init()
    {
        this.mapStorage = new MapStorage(this.saveHandler);
        String s = VillageCollection.fileNameForProvider(this.dimension);
        VillageCollection villagecollection = (VillageCollection)this.perWorldStorage.func_75742_a(VillageCollection.class, s);

        if (villagecollection == null)
        {
            this.villageCollection = new VillageCollection(this);
            this.perWorldStorage.setData(s, this.villageCollection);
        }
        else
        {
            this.villageCollection = villagecollection;
            this.villageCollection.setWorldsForAll(this);
        }

        this.field_96442_D = new ServerScoreboard(this.server);
        ScoreboardSaveData scoreboardsavedata = (ScoreboardSaveData)this.mapStorage.func_75742_a(ScoreboardSaveData.class, "scoreboard");

        if (scoreboardsavedata == null)
        {
            scoreboardsavedata = new ScoreboardSaveData();
            this.mapStorage.setData("scoreboard", scoreboardsavedata);
        }

        scoreboardsavedata.setScoreboard(this.field_96442_D);
        ((ServerScoreboard)this.field_96442_D).addDirtyRunnable(new WorldSavedDataCallableSave(scoreboardsavedata));
        this.field_184151_B = new LootTableManager(new File(new File(this.saveHandler.getWorldDirectory(), "data"), "loot_tables"));
        this.field_191951_C = new AdvancementManager(new File(new File(this.saveHandler.getWorldDirectory(), "data"), "advancements"));
        this.field_193036_D = new FunctionManager(new File(new File(this.saveHandler.getWorldDirectory(), "data"), "functions"), this.server);
        this.getWorldBorder().setCenter(this.worldInfo.getBorderCenterX(), this.worldInfo.getBorderCenterZ());
        this.getWorldBorder().setDamageAmount(this.worldInfo.getBorderDamagePerBlock());
        this.getWorldBorder().setDamageBuffer(this.worldInfo.getBorderSafeZone());
        this.getWorldBorder().setWarningDistance(this.worldInfo.getBorderWarningDistance());
        this.getWorldBorder().setWarningTime(this.worldInfo.getBorderWarningTime());

        if (this.worldInfo.getBorderLerpTime() > 0L)
        {
            this.getWorldBorder().setTransition(this.worldInfo.getBorderSize(), this.worldInfo.getBorderLerpTarget(), this.worldInfo.getBorderLerpTime());
        }
        else
        {
            this.getWorldBorder().setTransition(this.worldInfo.getBorderSize());
        }

        this.initCapabilities();
        return this;
    }

    /**
     * Runs a single tick for the world
     */
    public void tick()
    {
        super.tick();

        if (this.getWorldInfo().isHardcore() && this.getDifficulty() != EnumDifficulty.HARD)
        {
            this.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
        }

        this.dimension.func_177499_m().func_76938_b();

        if (this.areAllPlayersAsleep())
        {
            if (this.getGameRules().getBoolean("doDaylightCycle"))
            {
                long i = this.getDayTime() + 24000L;
                this.setDayTime(i - i % 24000L);
            }

            this.wakeAllPlayers();
        }

        this.profiler.startSection("mobSpawner");

        if (this.getGameRules().getBoolean("doMobSpawning") && this.worldInfo.getTerrainType() != WorldType.DEBUG_ALL_BLOCK_STATES)
        {
            this.entitySpawner.findChunksForSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs, this.worldInfo.getGameTime() % 400L == 0L);
        }

        this.profiler.endStartSection("chunkSource");
        this.chunkProvider.tick();
        int j = this.calculateSkylightSubtracted(1.0F);

        if (j != this.getSkylightSubtracted())
        {
            this.setSkylightSubtracted(j);
        }

        this.worldInfo.setWorldTotalTime(this.worldInfo.getGameTime() + 1L);

        if (this.getGameRules().getBoolean("doDaylightCycle"))
        {
            this.setDayTime(this.getDayTime() + 1L);
        }

        this.profiler.endStartSection("tickPending");
        this.tickPending(false);
        this.profiler.endStartSection("tickBlocks");
        this.tickBlocks();
        this.profiler.endStartSection("chunkMap");
        this.playerChunkMap.tick();
        this.profiler.endStartSection("village");
        this.villageCollection.tick();
        this.villageSiege.tick();
        this.profiler.endStartSection("portalForcer");
        this.worldTeleporter.removeStalePortalLocations(this.getGameTime());
        for (Teleporter tele : customTeleporters)
        {
            tele.removeStalePortalLocations(getGameTime());
        }
        this.profiler.endSection();
        this.sendQueuedBlockEvents();
    }

    @Nullable
    public Biome.SpawnListEntry getSpawnListEntryForTypeAt(EnumCreatureType creatureType, BlockPos pos)
    {
        List<Biome.SpawnListEntry> list = this.getChunkProvider().getPossibleCreatures(creatureType, pos);
        list = net.minecraftforge.event.ForgeEventFactory.getPotentialSpawns(this, creatureType, pos, list);
        return list != null && !list.isEmpty() ? (Biome.SpawnListEntry)WeightedRandom.getRandomItem(this.rand, list) : null;
    }

    public boolean canCreatureTypeSpawnHere(EnumCreatureType creatureType, Biome.SpawnListEntry spawnListEntry, BlockPos pos)
    {
        List<Biome.SpawnListEntry> list = this.getChunkProvider().getPossibleCreatures(creatureType, pos);
        list = net.minecraftforge.event.ForgeEventFactory.getPotentialSpawns(this, creatureType, pos, list);
        return list != null && !list.isEmpty() ? list.contains(spawnListEntry) : false;
    }

    /**
     * Updates the flag that indicates whether or not all players in the world are sleeping.
     */
    public void updateAllPlayersSleepingFlag()
    {
        this.allPlayersSleeping = false;

        if (!this.playerEntities.isEmpty())
        {
            int i = 0;
            int j = 0;

            for (EntityPlayer entityplayer : this.playerEntities)
            {
                if (entityplayer.isSpectator())
                {
                    ++i;
                }
                else if (entityplayer.isPlayerSleeping())
                {
                    ++j;
                }
            }

            this.allPlayersSleeping = j > 0 && j >= this.playerEntities.size() - i;
        }
    }

    protected void wakeAllPlayers()
    {
        this.allPlayersSleeping = false;

        for (EntityPlayer entityplayer : this.playerEntities.stream().filter(EntityPlayer::isPlayerSleeping).collect(Collectors.toList()))
        {
            entityplayer.wakeUpPlayer(false, false, true);
        }

        if (this.getGameRules().getBoolean("doWeatherCycle"))
        {
            this.resetRainAndThunder();
        }
    }

    /**
     * Clears the current rain and thunder weather states.
     */
    private void resetRainAndThunder()
    {
        this.dimension.resetRainAndThunder();
    }

    /**
     * Checks if all players in this world are sleeping.
     */
    public boolean areAllPlayersAsleep()
    {
        if (this.allPlayersSleeping && !this.isRemote)
        {
            for (EntityPlayer entityplayer : this.playerEntities)
            {
                if (!entityplayer.isSpectator() && !entityplayer.isPlayerFullyAsleep())
                {
                    return false;
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Sets a new spawn location by finding an uncovered block at a random (x,z) location in the chunk.
     */
    @SideOnly(Side.CLIENT)
    public void setInitialSpawnLocation()
    {
        if (this.worldInfo.getSpawnY() <= 0)
        {
            this.worldInfo.setSpawnY(this.getSeaLevel() + 1);
        }

        int i = this.worldInfo.getSpawnX();
        int j = this.worldInfo.getSpawnZ();
        int k = 0;

        while (this.getGroundAboveSeaLevel(new BlockPos(i, 0, j)).getMaterial() == Material.AIR)
        {
            i += this.rand.nextInt(8) - this.rand.nextInt(8);
            j += this.rand.nextInt(8) - this.rand.nextInt(8);
            ++k;

            if (k == 10000)
            {
                break;
            }
        }

        this.worldInfo.setSpawnX(i);
        this.worldInfo.setSpawnZ(j);
    }

    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty)
    {
        return this.getChunkProvider().chunkExists(x, z);
    }

    protected void playerCheckLight()
    {
        this.profiler.startSection("playerCheckLight");

        if (!this.playerEntities.isEmpty())
        {
            int i = this.rand.nextInt(this.playerEntities.size());
            EntityPlayer entityplayer = this.playerEntities.get(i);
            int j = MathHelper.floor(entityplayer.posX) + this.rand.nextInt(11) - 5;
            int k = MathHelper.floor(entityplayer.posY) + this.rand.nextInt(11) - 5;
            int l = MathHelper.floor(entityplayer.posZ) + this.rand.nextInt(11) - 5;
            this.checkLight(new BlockPos(j, k, l));
        }

        this.profiler.endSection();
    }

    protected void tickBlocks()
    {
        this.playerCheckLight();

        if (this.worldInfo.getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES)
        {
            Iterator<Chunk> iterator1 = this.playerChunkMap.getChunkIterator();

            while (iterator1.hasNext())
            {
                ((Chunk)iterator1.next()).tick(false);
            }
        }
        else
        {
            int i = this.getGameRules().getInt("randomTickSpeed");
            boolean flag = this.isRaining();
            boolean flag1 = this.isThundering();
            this.profiler.startSection("pollingChunks");

            for (Iterator<Chunk> iterator = getPersistentChunkIterable(this.playerChunkMap.getChunkIterator()); iterator.hasNext(); this.profiler.endSection())
            {
                this.profiler.startSection("getChunk");
                Chunk chunk = iterator.next();
                int j = chunk.x * 16;
                int k = chunk.z * 16;
                this.profiler.endStartSection("checkNextLight");
                chunk.enqueueRelightChecks();
                this.profiler.endStartSection("tickChunk");
                chunk.tick(false);
                this.profiler.endStartSection("thunder");

                if (this.dimension.canDoLightning(chunk) && flag && flag1 && this.rand.nextInt(100000) == 0)
                {
                    this.updateLCG = this.updateLCG * 3 + 1013904223;
                    int l = this.updateLCG >> 2;
                    BlockPos blockpos = this.adjustPosToNearbyEntity(new BlockPos(j + (l & 15), 0, k + (l >> 8 & 15)));

                    if (this.isRainingAt(blockpos))
                    {
                        DifficultyInstance difficultyinstance = this.getDifficultyForLocation(blockpos);

                        if (this.getGameRules().getBoolean("doMobSpawning") && this.rand.nextDouble() < (double)difficultyinstance.getAdditionalDifficulty() * 0.01D)
                        {
                            EntitySkeletonHorse entityskeletonhorse = new EntitySkeletonHorse(this);
                            entityskeletonhorse.setTrap(true);
                            entityskeletonhorse.setGrowingAge(0);
                            entityskeletonhorse.setPosition((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
                            this.spawnEntity(entityskeletonhorse);
                            this.addWeatherEffect(new EntityLightningBolt(this, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), true));
                        }
                        else
                        {
                            this.addWeatherEffect(new EntityLightningBolt(this, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), false));
                        }
                    }
                }

                this.profiler.endStartSection("iceandsnow");

                if (this.dimension.canDoRainSnowIce(chunk) && this.rand.nextInt(16) == 0)
                {
                    this.updateLCG = this.updateLCG * 3 + 1013904223;
                    int j2 = this.updateLCG >> 2;
                    BlockPos blockpos1 = this.func_175725_q(new BlockPos(j + (j2 & 15), 0, k + (j2 >> 8 & 15)));
                    BlockPos blockpos2 = blockpos1.down();

                    if (this.func_175697_a(blockpos2, 1)) // Forge: check area to avoid loading neighbors in unloaded chunks
                    if (this.func_175662_w(blockpos2))
                    {
                        this.setBlockState(blockpos2, Blocks.ICE.getDefaultState());
                    }

                    if (flag && this.func_175708_f(blockpos1, true))
                    {
                        this.setBlockState(blockpos1, Blocks.field_150431_aC.getDefaultState());
                    }

                    if (flag && this.getBiome(blockpos2).func_76738_d())
                    {
                        this.getBlockState(blockpos2).getBlock().fillWithRain(this, blockpos2);
                    }
                }

                this.profiler.endStartSection("tickBlocks");

                if (i > 0)
                {
                    for (ExtendedBlockStorage extendedblockstorage : chunk.getSections())
                    {
                        if (extendedblockstorage != Chunk.EMPTY_SECTION && extendedblockstorage.needsRandomTick())
                        {
                            for (int i1 = 0; i1 < i; ++i1)
                            {
                                this.updateLCG = this.updateLCG * 3 + 1013904223;
                                int j1 = this.updateLCG >> 2;
                                int k1 = j1 & 15;
                                int l1 = j1 >> 8 & 15;
                                int i2 = j1 >> 16 & 15;
                                IBlockState iblockstate = extendedblockstorage.get(k1, i2, l1);
                                Block block = iblockstate.getBlock();
                                this.profiler.startSection("randomTick");

                                if (block.getTickRandomly())
                                {
                                    block.func_180645_a(this, new BlockPos(k1 + j, i2 + extendedblockstorage.getYLocation(), l1 + k), iblockstate, this.rand);
                                }

                                this.profiler.endSection();
                            }
                        }
                    }
                }
            }

            this.profiler.endSection();
        }
    }

    protected BlockPos adjustPosToNearbyEntity(BlockPos pos)
    {
        BlockPos blockpos = this.func_175725_q(pos);
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockpos, new BlockPos(blockpos.getX(), this.getHeight(), blockpos.getZ()))).grow(3.0D);
        List<EntityLivingBase> list = this.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb, new com.google.common.base.Predicate<EntityLivingBase>()
        {
            public boolean apply(@Nullable EntityLivingBase p_apply_1_)
            {
                return p_apply_1_ != null && p_apply_1_.isAlive() && WorldServer.this.canSeeSky(p_apply_1_.getPosition());
            }
        });

        if (!list.isEmpty())
        {
            return ((EntityLivingBase)list.get(this.rand.nextInt(list.size()))).getPosition();
        }
        else
        {
            if (blockpos.getY() == -1)
            {
                blockpos = blockpos.up(2);
            }

            return blockpos;
        }
    }

    public boolean func_175691_a(BlockPos p_175691_1_, Block p_175691_2_)
    {
        NextTickListEntry nextticklistentry = new NextTickListEntry(p_175691_1_, p_175691_2_);
        return this.pendingBlockTicks.contains(nextticklistentry);
    }

    public boolean func_184145_b(BlockPos p_184145_1_, Block p_184145_2_)
    {
        NextTickListEntry nextticklistentry = new NextTickListEntry(p_184145_1_, p_184145_2_);
        return this.field_73064_N.contains(nextticklistentry);
    }

    public void func_175684_a(BlockPos p_175684_1_, Block p_175684_2_, int p_175684_3_)
    {
        this.func_175654_a(p_175684_1_, p_175684_2_, p_175684_3_, 0);
    }

    public void func_175654_a(BlockPos p_175654_1_, Block p_175654_2_, int p_175654_3_, int p_175654_4_)
    {
        Material material = p_175654_2_.getDefaultState().getMaterial();

        if (this.field_72999_e && material != Material.AIR)
        {
            if (p_175654_2_.func_149698_L())
            {
                //Keeping here as a note for future when it may be restored.
                boolean isForced = getPersistentChunks().containsKey(new ChunkPos(p_175654_1_));
                int range = isForced ? 0 : 8;
                if (this.isAreaLoaded(p_175654_1_.add(-range, -range, -range), p_175654_1_.add(range, range, range)))
                {
                    IBlockState iblockstate = this.getBlockState(p_175654_1_);

                    if (iblockstate.getMaterial() != Material.AIR && iblockstate.getBlock() == p_175654_2_)
                    {
                        iblockstate.getBlock().func_180650_b(this, p_175654_1_, iblockstate, this.rand);
                    }
                }

                return;
            }

            p_175654_3_ = 1;
        }

        NextTickListEntry nextticklistentry = new NextTickListEntry(p_175654_1_, p_175654_2_);

        if (this.isBlockLoaded(p_175654_1_))
        {
            if (material != Material.AIR)
            {
                nextticklistentry.func_77176_a((long)p_175654_3_ + this.worldInfo.getGameTime());
                nextticklistentry.func_82753_a(p_175654_4_);
            }

            if (!this.field_73064_N.contains(nextticklistentry))
            {
                this.field_73064_N.add(nextticklistentry);
                this.field_73065_O.add(nextticklistentry);
            }
        }
    }

    public void func_180497_b(BlockPos p_180497_1_, Block p_180497_2_, int p_180497_3_, int p_180497_4_)
    {
        if (p_180497_2_ == null) return; //Forge: Prevent null blocks from ticking, can happen if blocks are removed in old worlds. TODO: Fix real issue causing block to be null.
        NextTickListEntry nextticklistentry = new NextTickListEntry(p_180497_1_, p_180497_2_);
        nextticklistentry.func_82753_a(p_180497_4_);
        Material material = p_180497_2_.getDefaultState().getMaterial();

        if (material != Material.AIR)
        {
            nextticklistentry.func_77176_a((long)p_180497_3_ + this.worldInfo.getGameTime());
        }

        if (!this.field_73064_N.contains(nextticklistentry))
        {
            this.field_73064_N.add(nextticklistentry);
            this.field_73065_O.add(nextticklistentry);
        }
    }

    /**
     * Updates (and cleans up) entities and tile entities
     */
    public void tickEntities()
    {
        if (this.playerEntities.isEmpty() && getPersistentChunks().isEmpty())
        {
            if (this.updateEntityTick++ >= 300)
            {
                return;
            }
        }
        else
        {
            this.resetUpdateEntityTick();
        }

        this.dimension.tick();
        super.tickEntities();
    }

    protected void tickPlayers()
    {
        super.tickPlayers();
        this.profiler.endStartSection("players");

        for (int i = 0; i < this.playerEntities.size(); ++i)
        {
            Entity entity = this.playerEntities.get(i);
            Entity entity1 = entity.getRidingEntity();

            if (entity1 != null)
            {
                if (!entity1.removed && entity1.isPassenger(entity))
                {
                    continue;
                }

                entity.stopRiding();
            }

            this.profiler.startSection("tick");

            if (!entity.removed)
            {
                try
                {
                    this.tickEntity(entity);
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking player");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Player being ticked");
                    entity.fillCrashReport(crashreportcategory);
                    throw new ReportedException(crashreport);
                }
            }

            this.profiler.endSection();
            this.profiler.startSection("remove");

            if (entity.removed)
            {
                int j = entity.chunkCoordX;
                int k = entity.chunkCoordZ;

                if (entity.addedToChunk && this.isChunkLoaded(j, k, true))
                {
                    this.getChunk(j, k).removeEntity(entity);
                }

                this.loadedEntityList.remove(entity);
                this.onEntityRemoved(entity);
            }

            this.profiler.endSection();
        }
    }

    /**
     * Resets the updateEntityTick field to 0
     */
    public void resetUpdateEntityTick()
    {
        this.updateEntityTick = 0;
    }

    /**
     * Runs through the list of updates to run and ticks them
     */
    public boolean tickPending(boolean p_72955_1_)
    {
        if (this.worldInfo.getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES)
        {
            return false;
        }
        else
        {
            int i = this.field_73065_O.size();

            if (i != this.field_73064_N.size())
            {
                throw new IllegalStateException("TickNextTick list out of synch");
            }
            else
            {
                if (i > 65536)
                {
                    i = 65536;
                }

                this.profiler.startSection("cleaning");

                for (int j = 0; j < i; ++j)
                {
                    NextTickListEntry nextticklistentry = this.field_73065_O.first();

                    if (!p_72955_1_ && nextticklistentry.scheduledTime > this.worldInfo.getGameTime())
                    {
                        break;
                    }

                    this.field_73065_O.remove(nextticklistentry);
                    this.field_73064_N.remove(nextticklistentry);
                    this.pendingBlockTicks.add(nextticklistentry);
                }

                this.profiler.endSection();
                this.profiler.startSection("ticking");
                Iterator<NextTickListEntry> iterator = this.pendingBlockTicks.iterator();

                while (iterator.hasNext())
                {
                    NextTickListEntry nextticklistentry1 = iterator.next();
                    iterator.remove();
                    //Keeping here as a note for future when it may be restored.
                    //boolean isForced = getPersistentChunks().containsKey(new ChunkPos(nextticklistentry.xCoord >> 4, nextticklistentry.zCoord >> 4));
                    //byte b0 = isForced ? 0 : 8;
                    int k = 0;

                    if (this.isAreaLoaded(nextticklistentry1.position.add(0, 0, 0), nextticklistentry1.position.add(0, 0, 0)))
                    {
                        IBlockState iblockstate = this.getBlockState(nextticklistentry1.position);

                        if (iblockstate.getMaterial() != Material.AIR && Block.func_149680_a(iblockstate.getBlock(), nextticklistentry1.getTarget()))
                        {
                            try
                            {
                                iblockstate.getBlock().func_180650_b(this, nextticklistentry1.position, iblockstate, this.rand);
                            }
                            catch (Throwable throwable)
                            {
                                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while ticking a block");
                                CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being ticked");
                                CrashReportCategory.addBlockInfo(crashreportcategory, nextticklistentry1.position, iblockstate);
                                throw new ReportedException(crashreport);
                            }
                        }
                    }
                    else
                    {
                        this.func_175684_a(nextticklistentry1.position, nextticklistentry1.getTarget(), 0);
                    }
                }

                this.profiler.endSection();
                this.pendingBlockTicks.clear();
                return !this.field_73065_O.isEmpty();
            }
        }
    }

    @Nullable
    public List<NextTickListEntry> func_72920_a(Chunk p_72920_1_, boolean p_72920_2_)
    {
        ChunkPos chunkpos = p_72920_1_.getPos();
        int i = (chunkpos.x << 4) - 2;
        int j = i + 16 + 2;
        int k = (chunkpos.z << 4) - 2;
        int l = k + 16 + 2;
        return this.func_175712_a(new StructureBoundingBox(i, 0, k, j, 256, l), p_72920_2_);
    }

    @Nullable
    public List<NextTickListEntry> func_175712_a(StructureBoundingBox p_175712_1_, boolean p_175712_2_)
    {
        List<NextTickListEntry> list = null;

        for (int i = 0; i < 2; ++i)
        {
            Iterator<NextTickListEntry> iterator;

            if (i == 0)
            {
                iterator = this.field_73065_O.iterator();
            }
            else
            {
                iterator = this.pendingBlockTicks.iterator();
            }

            while (iterator.hasNext())
            {
                NextTickListEntry nextticklistentry = iterator.next();
                BlockPos blockpos = nextticklistentry.position;

                if (blockpos.getX() >= p_175712_1_.minX && blockpos.getX() < p_175712_1_.maxX && blockpos.getZ() >= p_175712_1_.minZ && blockpos.getZ() < p_175712_1_.maxZ)
                {
                    if (p_175712_2_)
                    {
                        if (i == 0)
                        {
                            this.field_73064_N.remove(nextticklistentry);
                        }

                        iterator.remove();
                    }

                    if (list == null)
                    {
                        list = Lists.<NextTickListEntry>newArrayList();
                    }

                    list.add(nextticklistentry);
                }
            }
        }

        return list;
    }

    /**
     * Updates the entity in the world if the chunk the entity is in is currently loaded or its forced to update.
     */
    public void tickEntity(Entity entityIn, boolean forceUpdate)
    {
        if (!this.canSpawnAnimals() && (entityIn instanceof EntityAnimal || entityIn instanceof EntityWaterMob))
        {
            entityIn.remove();
        }

        if (!this.canSpawnNPCs() && entityIn instanceof INpc)
        {
            entityIn.remove();
        }

        super.tickEntity(entityIn, forceUpdate);
    }

    private boolean canSpawnNPCs()
    {
        return this.server.getCanSpawnNPCs();
    }

    private boolean canSpawnAnimals()
    {
        return this.server.getCanSpawnAnimals();
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected IChunkProvider createChunkProvider()
    {
        IChunkLoader ichunkloader = this.saveHandler.getChunkLoader(this.dimension);
        return new ChunkProviderServer(this, ichunkloader, this.dimension.createChunkGenerator());
    }

    public boolean isBlockModifiable(EntityPlayer player, BlockPos pos)
    {
        return super.isBlockModifiable(player, pos);
    }
    public boolean canMineBlockBody(EntityPlayer player, BlockPos pos)
    {
        return !this.server.isBlockProtected(this, pos, player) && this.getWorldBorder().contains(pos);
    }

    public void initialize(WorldSettings settings)
    {
        if (!this.worldInfo.isInitialized())
        {
            try
            {
                this.createSpawnPosition(settings);

                if (this.worldInfo.getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES)
                {
                    this.setDebugWorldSettings();
                }

                super.initialize(settings);
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception initializing level");

                try
                {
                    this.fillCrashReport(crashreport);
                }
                catch (Throwable var5)
                {
                    ;
                }

                throw new ReportedException(crashreport);
            }

            this.worldInfo.setServerInitialized(true);
        }
    }

    private void setDebugWorldSettings()
    {
        this.worldInfo.setMapFeaturesEnabled(false);
        this.worldInfo.setAllowCommands(true);
        this.worldInfo.setRaining(false);
        this.worldInfo.setThundering(false);
        this.worldInfo.setClearWeatherTime(1000000000);
        this.worldInfo.setDayTime(6000L);
        this.worldInfo.setGameType(GameType.SPECTATOR);
        this.worldInfo.setHardcore(false);
        this.worldInfo.setDifficulty(EnumDifficulty.PEACEFUL);
        this.worldInfo.setDifficultyLocked(true);
        this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
    }

    /**
     * creates a spawn position at random within 256 blocks of 0,0
     */
    private void createSpawnPosition(WorldSettings settings)
    {
        if (!this.dimension.canRespawnHere())
        {
            this.worldInfo.setSpawn(BlockPos.ORIGIN.up(this.dimension.func_76557_i()));
        }
        else if (this.worldInfo.getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES)
        {
            this.worldInfo.setSpawn(BlockPos.ORIGIN.up());
        }
        else
        {
            if (net.minecraftforge.event.ForgeEventFactory.onCreateWorldSpawn(this, settings)) return;
            this.field_72987_B = true;
            BiomeProvider biomeprovider = this.dimension.func_177499_m();
            List<Biome> list = biomeprovider.getBiomesToSpawnIn();
            Random random = new Random(this.getSeed());
            BlockPos blockpos = biomeprovider.findBiomePosition(0, 0, 256, list, random);
            int i = 8;
            int j = this.dimension.func_76557_i();
            int k = 8;

            if (blockpos != null)
            {
                i = blockpos.getX();
                k = blockpos.getZ();
            }
            else
            {
                LOGGER.warn("Unable to find spawn biome");
            }

            int l = 0;

            while (!this.dimension.func_76566_a(i, k))
            {
                i += random.nextInt(64) - random.nextInt(64);
                k += random.nextInt(64) - random.nextInt(64);
                ++l;

                if (l == 1000)
                {
                    break;
                }
            }

            this.worldInfo.setSpawn(new BlockPos(i, j, k));
            this.field_72987_B = false;

            if (settings.isBonusChestEnabled())
            {
                this.createBonusChest();
            }
        }
    }

    /**
     * Creates the bonus chest in the world.
     */
    protected void createBonusChest()
    {
        WorldGeneratorBonusChest worldgeneratorbonuschest = new WorldGeneratorBonusChest();

        for (int i = 0; i < 10; ++i)
        {
            int j = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
            int k = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
            BlockPos blockpos = this.func_175672_r(new BlockPos(j, 0, k)).up();

            if (worldgeneratorbonuschest.func_180709_b(this, this.rand, blockpos))
            {
                break;
            }
        }
    }

    /**
     * Returns null for anything other than the End
     */
    @Nullable
    public BlockPos getSpawnCoordinate()
    {
        return this.dimension.getSpawnCoordinate();
    }

    /**
     * Saves all chunks to disk while updating progress bar.
     */
    public void saveAllChunks(boolean all, @Nullable IProgressUpdate progressCallback) throws MinecraftException
    {
        ChunkProviderServer chunkproviderserver = this.getChunkProvider();

        if (chunkproviderserver.canSave())
        {
            if (progressCallback != null)
            {
                progressCallback.func_73720_a("Saving level");
            }

            this.saveLevel();

            if (progressCallback != null)
            {
                progressCallback.func_73719_c("Saving chunks");
            }

            chunkproviderserver.saveChunks(all);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Save(this));

            for (Chunk chunk : Lists.newArrayList(chunkproviderserver.getLoadedChunks()))
            {
                if (chunk != null && !this.playerChunkMap.contains(chunk.x, chunk.z))
                {
                    chunkproviderserver.queueUnload(chunk);
                }
            }
        }
    }

    /**
     * Flushes all pending chunks fully back to disk
     */
    public void flushToDisk()
    {
        ChunkProviderServer chunkproviderserver = this.getChunkProvider();

        if (chunkproviderserver.canSave())
        {
            chunkproviderserver.flushToDisk();
        }
    }

    /**
     * Saves the chunks to disk.
     */
    protected void saveLevel() throws MinecraftException
    {
        this.checkSessionLock();

        for (WorldServer worldserver : this.server.worlds)
        {
            if (worldserver instanceof WorldServerMulti)
            {
                ((WorldServerMulti)worldserver).saveAdditionalData();
            }
        }

        this.worldInfo.setBorderSize(this.getWorldBorder().getDiameter());
        this.worldInfo.getBorderCenterX(this.getWorldBorder().getCenterX());
        this.worldInfo.getBorderCenterZ(this.getWorldBorder().getCenterZ());
        this.worldInfo.setBorderSafeZone(this.getWorldBorder().getDamageBuffer());
        this.worldInfo.setBorderDamagePerBlock(this.getWorldBorder().getDamageAmount());
        this.worldInfo.setBorderWarningDistance(this.getWorldBorder().getWarningDistance());
        this.worldInfo.setBorderWarningTime(this.getWorldBorder().getWarningTime());
        this.worldInfo.setBorderLerpTarget(this.getWorldBorder().getTargetSize());
        this.worldInfo.setBorderLerpTime(this.getWorldBorder().getTimeUntilTarget());
        this.saveHandler.saveWorldInfoWithPlayer(this.worldInfo, this.server.getPlayerList().getHostPlayerData());
        this.mapStorage.saveAllData();
        this.perWorldStorage.saveAllData();
    }

    /**
     * Called when an entity is spawned in the world. This includes players.
     */
    public boolean spawnEntity(Entity entityIn)
    {
        return this.canAddEntity(entityIn) ? super.spawnEntity(entityIn) : false;
    }

    public void loadEntities(Collection<Entity> entityCollection)
    {
        for (Entity entity : Lists.newArrayList(entityCollection))
        {
            if (this.canAddEntity(entity) && !net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinWorldEvent(entity, this)))
            {
                this.loadedEntityList.add(entity);
                this.onEntityAdded(entity);
            }
        }
    }

    private boolean canAddEntity(Entity entityIn)
    {
        if (entityIn.removed)
        {
            LOGGER.warn("Tried to add entity {} but it was marked as removed already", (Object)EntityList.func_191301_a(entityIn));
            return false;
        }
        else
        {
            UUID uuid = entityIn.getUniqueID();

            if (this.entitiesByUuid.containsKey(uuid))
            {
                Entity entity = this.entitiesByUuid.get(uuid);

                if (this.unloadedEntityList.contains(entity))
                {
                    this.unloadedEntityList.remove(entity);
                }
                else
                {
                    if (!(entityIn instanceof EntityPlayer))
                    {
                        LOGGER.warn("Keeping entity {} that already exists with UUID {}", EntityList.func_191301_a(entity), uuid.toString());
                        return false;
                    }

                    LOGGER.warn("Force-added player with duplicate UUID {}", (Object)uuid.toString());
                }

                this.removeEntityDangerously(entity);
            }

            return true;
        }
    }

    public void onEntityAdded(Entity entityIn)
    {
        super.onEntityAdded(entityIn);
        this.entitiesById.addKey(entityIn.getEntityId(), entityIn);
        this.entitiesByUuid.put(entityIn.getUniqueID(), entityIn);
        Entity[] aentity = entityIn.getParts();

        if (aentity != null)
        {
            for (Entity entity : aentity)
            {
                this.entitiesById.addKey(entity.getEntityId(), entity);
            }
        }
    }

    public void onEntityRemoved(Entity entityIn)
    {
        super.onEntityRemoved(entityIn);
        this.entitiesById.removeObject(entityIn.getEntityId());
        this.entitiesByUuid.remove(entityIn.getUniqueID());
        Entity[] aentity = entityIn.getParts();

        if (aentity != null)
        {
            for (Entity entity : aentity)
            {
                this.entitiesById.removeObject(entity.getEntityId());
            }
        }
    }

    /**
     * adds a lightning bolt to the list of lightning bolts in this world.
     */
    public boolean addWeatherEffect(Entity entityIn)
    {
        if (super.addWeatherEffect(entityIn))
        {
            this.server.getPlayerList().sendToAllNearExcept((EntityPlayer)null, entityIn.posX, entityIn.posY, entityIn.posZ, 512.0D, this.dimension.getDimension(), new SPacketSpawnGlobalEntity(entityIn));
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * sends a Packet 38 (Entity Status) to all tracked players of that entity
     */
    public void setEntityState(Entity entityIn, byte state)
    {
        this.getEntityTracker().sendToTrackingAndSelf(entityIn, new SPacketEntityStatus(entityIn, state));
    }

    /**
     * gets the world's chunk provider
     */
    public ChunkProviderServer getChunkProvider()
    {
        return (ChunkProviderServer)super.getChunkProvider();
    }

    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
     */
    public Explosion newExplosion(@Nullable Entity entityIn, double x, double y, double z, float strength, boolean causesFire, boolean damagesTerrain)
    {
        Explosion explosion = new Explosion(this, entityIn, x, y, z, strength, causesFire, damagesTerrain);
        if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this, explosion)) return explosion;
        explosion.doExplosionA();
        explosion.doExplosionB(false);

        if (!damagesTerrain)
        {
            explosion.clearAffectedBlockPositions();
        }

        for (EntityPlayer entityplayer : this.playerEntities)
        {
            if (entityplayer.getDistanceSq(x, y, z) < 4096.0D)
            {
                ((EntityPlayerMP)entityplayer).connection.sendPacket(new SPacketExplosion(x, y, z, strength, explosion.getAffectedBlockPositions(), (Vec3d)explosion.getPlayerKnockbackMap().get(entityplayer)));
            }
        }

        return explosion;
    }

    public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam)
    {
        BlockEventData blockeventdata = new BlockEventData(pos, blockIn, eventID, eventParam);

        for (BlockEventData blockeventdata1 : this.blockEventQueue[this.field_147489_T])
        {
            if (blockeventdata1.equals(blockeventdata))
            {
                return;
            }
        }

        this.blockEventQueue[this.field_147489_T].add(blockeventdata);
    }

    private void sendQueuedBlockEvents()
    {
        while (!this.blockEventQueue[this.field_147489_T].isEmpty())
        {
            int i = this.field_147489_T;
            this.field_147489_T ^= 1;

            for (BlockEventData blockeventdata : this.blockEventQueue[i])
            {
                if (this.fireBlockEvent(blockeventdata))
                {
                    this.server.getPlayerList().sendToAllNearExcept((EntityPlayer)null, (double)blockeventdata.getPosition().getX(), (double)blockeventdata.getPosition().getY(), (double)blockeventdata.getPosition().getZ(), 64.0D, this.dimension.getDimension(), new SPacketBlockAction(blockeventdata.getPosition(), blockeventdata.getBlock(), blockeventdata.getEventID(), blockeventdata.getEventParameter()));
                }
            }

            this.blockEventQueue[i].clear();
        }
    }

    private boolean fireBlockEvent(BlockEventData event)
    {
        IBlockState iblockstate = this.getBlockState(event.getPosition());
        return iblockstate.getBlock() == event.getBlock() ? iblockstate.onBlockEventReceived(this, event.getPosition(), event.getEventID(), event.getEventParameter()) : false;
    }

    public void func_73041_k()
    {
        this.saveHandler.flush();
    }

    /**
     * Updates all weather states.
     */
    protected void tickWeather()
    {
        boolean flag = this.isRaining();
        super.tickWeather();

        if (this.prevRainingStrength != this.rainingStrength)
        {
            this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SPacketChangeGameState(7, this.rainingStrength), this.dimension.getDimension());
        }

        if (this.prevThunderingStrength != this.thunderingStrength)
        {
            this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SPacketChangeGameState(8, this.thunderingStrength), this.dimension.getDimension());
        }

        /* The function in use here has been replaced in order to only send the weather info to players in the correct dimension,
         * rather than to all players on the server. This is what causes the client-side rain, as the
         * client believes that it has started raining locally, rather than in another dimension.
         */
        if (flag != this.isRaining())
        {
            if (flag)
            {
                this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SPacketChangeGameState(2, 0.0F), this.dimension.getDimension());
            }
            else
            {
                this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SPacketChangeGameState(1, 0.0F), this.dimension.getDimension());
            }

            this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SPacketChangeGameState(7, this.rainingStrength), this.dimension.getDimension());
            this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SPacketChangeGameState(8, this.thunderingStrength), this.dimension.getDimension());
        }
    }

    @Nullable
    public MinecraftServer getServer()
    {
        return this.server;
    }

    /**
     * Gets the entity tracker for this server world.
     */
    public EntityTracker getEntityTracker()
    {
        return this.entityTracker;
    }

    /**
     * Gets the player chunk map for this server world.
     */
    public PlayerChunkMap getPlayerChunkMap()
    {
        return this.playerChunkMap;
    }

    public Teleporter getDefaultTeleporter()
    {
        return this.worldTeleporter;
    }

    public TemplateManager getStructureTemplateManager()
    {
        return this.saveHandler.getStructureTemplateManager();
    }

    public void func_175739_a(EnumParticleTypes p_175739_1_, double p_175739_2_, double p_175739_4_, double p_175739_6_, int p_175739_8_, double p_175739_9_, double p_175739_11_, double p_175739_13_, double p_175739_15_, int... p_175739_17_)
    {
        this.func_180505_a(p_175739_1_, false, p_175739_2_, p_175739_4_, p_175739_6_, p_175739_8_, p_175739_9_, p_175739_11_, p_175739_13_, p_175739_15_, p_175739_17_);
    }

    public void func_180505_a(EnumParticleTypes p_180505_1_, boolean p_180505_2_, double p_180505_3_, double p_180505_5_, double p_180505_7_, int p_180505_9_, double p_180505_10_, double p_180505_12_, double p_180505_14_, double p_180505_16_, int... p_180505_18_)
    {
        SPacketParticles spacketparticles = new SPacketParticles(p_180505_1_, p_180505_2_, (float)p_180505_3_, (float)p_180505_5_, (float)p_180505_7_, (float)p_180505_10_, (float)p_180505_12_, (float)p_180505_14_, (float)p_180505_16_, p_180505_9_, p_180505_18_);

        for (int i = 0; i < this.playerEntities.size(); ++i)
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)this.playerEntities.get(i);
            this.func_184159_a(entityplayermp, p_180505_2_, p_180505_3_, p_180505_5_, p_180505_7_, spacketparticles);
        }
    }

    public void func_184161_a(EntityPlayerMP p_184161_1_, EnumParticleTypes p_184161_2_, boolean p_184161_3_, double p_184161_4_, double p_184161_6_, double p_184161_8_, int p_184161_10_, double p_184161_11_, double p_184161_13_, double p_184161_15_, double p_184161_17_, int... p_184161_19_)
    {
        Packet<?> packet = new SPacketParticles(p_184161_2_, p_184161_3_, (float)p_184161_4_, (float)p_184161_6_, (float)p_184161_8_, (float)p_184161_11_, (float)p_184161_13_, (float)p_184161_15_, (float)p_184161_17_, p_184161_10_, p_184161_19_);
        this.func_184159_a(p_184161_1_, p_184161_3_, p_184161_4_, p_184161_6_, p_184161_8_, packet);
    }

    private void func_184159_a(EntityPlayerMP p_184159_1_, boolean p_184159_2_, double p_184159_3_, double p_184159_5_, double p_184159_7_, Packet<?> p_184159_9_)
    {
        BlockPos blockpos = p_184159_1_.getPosition();
        double d0 = blockpos.distanceSq(p_184159_3_, p_184159_5_, p_184159_7_);

        if (d0 <= 1024.0D || p_184159_2_ && d0 <= 262144.0D)
        {
            p_184159_1_.connection.sendPacket(p_184159_9_);
        }
    }

    @Nullable
    public Entity getEntityFromUuid(UUID uuid)
    {
        return this.entitiesByUuid.get(uuid);
    }

    public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule)
    {
        return this.server.addScheduledTask(runnableToSchedule);
    }

    public boolean isCallingFromMinecraftThread()
    {
        return this.server.isCallingFromMinecraftThread();
    }

    @Nullable
    public BlockPos func_190528_a(String p_190528_1_, BlockPos p_190528_2_, boolean p_190528_3_)
    {
        return this.getChunkProvider().func_180513_a(this, p_190528_1_, p_190528_2_, p_190528_3_);
    }

    public AdvancementManager func_191952_z()
    {
        return this.field_191951_C;
    }

    public FunctionManager func_193037_A()
    {
        return this.field_193036_D;
    }

    public java.io.File getChunkSaveLocation()
    {
        return ((net.minecraft.world.chunk.storage.AnvilChunkLoader)getChunkProvider().chunkLoader).chunkSaveLocation;
    }

    static class ServerBlockEventList extends ArrayList<BlockEventData>
        {
            private ServerBlockEventList()
            {
            }
        }
}