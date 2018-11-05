package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldInfo
{
    private String versionName;
    private int versionId;
    private boolean versionSnapshot;
    public static final EnumDifficulty DEFAULT_DIFFICULTY = EnumDifficulty.NORMAL;
    /** Holds the seed of the currently world. */
    private long randomSeed;
    private WorldType terrainType = WorldType.DEFAULT;
    private String generatorOptions = "";
    /** The spawn zone position X coordinate. */
    private int spawnX;
    /** The spawn zone position Y coordinate. */
    private int spawnY;
    /** The spawn zone position Z coordinate. */
    private int spawnZ;
    /** Total time for this world. */
    private long gameTime;
    /** The current world time in ticks, ranging from 0 to 23999. */
    private long dayTime;
    /** The last time the player was in this world. */
    private long lastTimePlayed;
    /** The size of entire save of current world on the disk, isn't exactly. */
    private long sizeOnDisk;
    private NBTTagCompound playerTag;
    private int dimension;
    /** The name of the save defined at world creation. */
    private String levelName;
    /** Introduced in beta 1.3, is the save version for future control. */
    private int saveVersion;
    private int clearWeatherTime;
    /** True if it's raining, false otherwise. */
    private boolean raining;
    /** Number of ticks until next rain. */
    private int rainTime;
    /** Is thunderbolts failing now? */
    private boolean thundering;
    /** Number of ticks untils next thunderbolt. */
    private int thunderTime;
    /** The Game Type. */
    private GameType gameType;
    /** Whether the map features (e.g. strongholds) generation is enabled or disabled. */
    private boolean mapFeaturesEnabled;
    /** Hardcore mode flag */
    private boolean hardcore;
    private boolean allowCommands;
    private boolean initialized;
    private EnumDifficulty difficulty;
    private boolean difficultyLocked;
    private double borderCenterX;
    private double borderCenterZ;
    private double borderSize = 6.0E7D;
    private long borderSizeLerpTime;
    private double borderSizeLerpTarget;
    private double borderSafeZone = 5.0D;
    private double borderDamagePerBlock = 0.2D;
    private int borderWarningDistance = 5;
    private int borderWarningTime = 15;
    /**
     * Mapping providing data between different dimensions.
     *  
     * MODDERS: <strong>DO NOT USE</strong>. This map will have a different type of key depending on whether forge is
     * installed or not.
     */
    private final Map<Integer, NBTTagCompound> dimensionData = Maps.newHashMap();
    private GameRules gameRules = new GameRules();
    private java.util.Map<String, net.minecraft.nbt.NBTBase> additionalProperties;

    protected WorldInfo()
    {
    }

    public static void func_189967_a(DataFixer p_189967_0_)
    {
        p_189967_0_.func_188258_a(FixTypes.LEVEL, new IDataWalker()
        {
            public NBTTagCompound func_188266_a(IDataFixer p_188266_1_, NBTTagCompound p_188266_2_, int p_188266_3_)
            {
                if (p_188266_2_.contains("Player", 10))
                {
                    p_188266_2_.put("Player", p_188266_1_.func_188251_a(FixTypes.PLAYER, p_188266_2_.getCompound("Player"), p_188266_3_));
                }

                return p_188266_2_;
            }
        });
    }

    public WorldInfo(NBTTagCompound p_i2157_1_)
    {
        if (p_i2157_1_.contains("Version", 10))
        {
            NBTTagCompound nbttagcompound = p_i2157_1_.getCompound("Version");
            this.versionName = nbttagcompound.getString("Name");
            this.versionId = nbttagcompound.getInt("Id");
            this.versionSnapshot = nbttagcompound.getBoolean("Snapshot");
        }

        this.randomSeed = p_i2157_1_.getLong("RandomSeed");

        if (p_i2157_1_.contains("generatorName", 8))
        {
            String s1 = p_i2157_1_.getString("generatorName");
            this.terrainType = WorldType.byName(s1);

            if (this.terrainType == null)
            {
                this.terrainType = WorldType.DEFAULT;
            }
            else if (this.terrainType.isVersioned())
            {
                int i = 0;

                if (p_i2157_1_.contains("generatorVersion", 99))
                {
                    i = p_i2157_1_.getInt("generatorVersion");
                }

                this.terrainType = this.terrainType.getWorldTypeForGeneratorVersion(i);
            }

            if (p_i2157_1_.contains("generatorOptions", 8))
            {
                this.generatorOptions = p_i2157_1_.getString("generatorOptions");
            }
        }

        this.gameType = GameType.getByID(p_i2157_1_.getInt("GameType"));

        if (p_i2157_1_.contains("MapFeatures", 99))
        {
            this.mapFeaturesEnabled = p_i2157_1_.getBoolean("MapFeatures");
        }
        else
        {
            this.mapFeaturesEnabled = true;
        }

        this.spawnX = p_i2157_1_.getInt("SpawnX");
        this.spawnY = p_i2157_1_.getInt("SpawnY");
        this.spawnZ = p_i2157_1_.getInt("SpawnZ");
        this.gameTime = p_i2157_1_.getLong("Time");

        if (p_i2157_1_.contains("DayTime", 99))
        {
            this.dayTime = p_i2157_1_.getLong("DayTime");
        }
        else
        {
            this.dayTime = this.gameTime;
        }

        this.lastTimePlayed = p_i2157_1_.getLong("LastPlayed");
        this.sizeOnDisk = p_i2157_1_.getLong("SizeOnDisk");
        this.levelName = p_i2157_1_.getString("LevelName");
        this.saveVersion = p_i2157_1_.getInt("version");
        this.clearWeatherTime = p_i2157_1_.getInt("clearWeatherTime");
        this.rainTime = p_i2157_1_.getInt("rainTime");
        this.raining = p_i2157_1_.getBoolean("raining");
        this.thunderTime = p_i2157_1_.getInt("thunderTime");
        this.thundering = p_i2157_1_.getBoolean("thundering");
        this.hardcore = p_i2157_1_.getBoolean("hardcore");

        if (p_i2157_1_.contains("initialized", 99))
        {
            this.initialized = p_i2157_1_.getBoolean("initialized");
        }
        else
        {
            this.initialized = true;
        }

        if (p_i2157_1_.contains("allowCommands", 99))
        {
            this.allowCommands = p_i2157_1_.getBoolean("allowCommands");
        }
        else
        {
            this.allowCommands = this.gameType == GameType.CREATIVE;
        }

        if (p_i2157_1_.contains("Player", 10))
        {
            this.playerTag = p_i2157_1_.getCompound("Player");
            this.dimension = this.playerTag.getInt("Dimension");
        }

        if (p_i2157_1_.contains("GameRules", 10))
        {
            this.gameRules.read(p_i2157_1_.getCompound("GameRules"));
        }

        if (p_i2157_1_.contains("Difficulty", 99))
        {
            this.difficulty = EnumDifficulty.byId(p_i2157_1_.getByte("Difficulty"));
        }

        if (p_i2157_1_.contains("DifficultyLocked", 1))
        {
            this.difficultyLocked = p_i2157_1_.getBoolean("DifficultyLocked");
        }

        if (p_i2157_1_.contains("BorderCenterX", 99))
        {
            this.borderCenterX = p_i2157_1_.getDouble("BorderCenterX");
        }

        if (p_i2157_1_.contains("BorderCenterZ", 99))
        {
            this.borderCenterZ = p_i2157_1_.getDouble("BorderCenterZ");
        }

        if (p_i2157_1_.contains("BorderSize", 99))
        {
            this.borderSize = p_i2157_1_.getDouble("BorderSize");
        }

        if (p_i2157_1_.contains("BorderSizeLerpTime", 99))
        {
            this.borderSizeLerpTime = p_i2157_1_.getLong("BorderSizeLerpTime");
        }

        if (p_i2157_1_.contains("BorderSizeLerpTarget", 99))
        {
            this.borderSizeLerpTarget = p_i2157_1_.getDouble("BorderSizeLerpTarget");
        }

        if (p_i2157_1_.contains("BorderSafeZone", 99))
        {
            this.borderSafeZone = p_i2157_1_.getDouble("BorderSafeZone");
        }

        if (p_i2157_1_.contains("BorderDamagePerBlock", 99))
        {
            this.borderDamagePerBlock = p_i2157_1_.getDouble("BorderDamagePerBlock");
        }

        if (p_i2157_1_.contains("BorderWarningBlocks", 99))
        {
            this.borderWarningDistance = p_i2157_1_.getInt("BorderWarningBlocks");
        }

        if (p_i2157_1_.contains("BorderWarningTime", 99))
        {
            this.borderWarningTime = p_i2157_1_.getInt("BorderWarningTime");
        }

        if (p_i2157_1_.contains("DimensionData", 10))
        {
            NBTTagCompound nbttagcompound1 = p_i2157_1_.getCompound("DimensionData");

            for (String s : nbttagcompound1.keySet())
            {
                this.dimensionData.put(Integer.parseInt(s), nbttagcompound1.getCompound(s));
            }
        }
    }

    public WorldInfo(WorldSettings settings, String name)
    {
        this.populateFromWorldSettings(settings);
        this.levelName = name;
        this.difficulty = DEFAULT_DIFFICULTY;
        this.initialized = false;
    }

    public void populateFromWorldSettings(WorldSettings settings)
    {
        this.randomSeed = settings.getSeed();
        this.gameType = settings.getGameType();
        this.mapFeaturesEnabled = settings.isMapFeaturesEnabled();
        this.hardcore = settings.getHardcoreEnabled();
        this.terrainType = settings.getTerrainType();
        this.generatorOptions = settings.func_82749_j();
        this.allowCommands = settings.areCommandsAllowed();
    }

    public WorldInfo(WorldInfo p_i2159_1_)
    {
        this.randomSeed = p_i2159_1_.randomSeed;
        this.terrainType = p_i2159_1_.terrainType;
        this.generatorOptions = p_i2159_1_.generatorOptions;
        this.gameType = p_i2159_1_.gameType;
        this.mapFeaturesEnabled = p_i2159_1_.mapFeaturesEnabled;
        this.spawnX = p_i2159_1_.spawnX;
        this.spawnY = p_i2159_1_.spawnY;
        this.spawnZ = p_i2159_1_.spawnZ;
        this.gameTime = p_i2159_1_.gameTime;
        this.dayTime = p_i2159_1_.dayTime;
        this.lastTimePlayed = p_i2159_1_.lastTimePlayed;
        this.sizeOnDisk = p_i2159_1_.sizeOnDisk;
        this.playerTag = p_i2159_1_.playerTag;
        this.dimension = p_i2159_1_.dimension;
        this.levelName = p_i2159_1_.levelName;
        this.saveVersion = p_i2159_1_.saveVersion;
        this.rainTime = p_i2159_1_.rainTime;
        this.raining = p_i2159_1_.raining;
        this.thunderTime = p_i2159_1_.thunderTime;
        this.thundering = p_i2159_1_.thundering;
        this.hardcore = p_i2159_1_.hardcore;
        this.allowCommands = p_i2159_1_.allowCommands;
        this.initialized = p_i2159_1_.initialized;
        this.gameRules = p_i2159_1_.gameRules;
        this.difficulty = p_i2159_1_.difficulty;
        this.difficultyLocked = p_i2159_1_.difficultyLocked;
        this.borderCenterX = p_i2159_1_.borderCenterX;
        this.borderCenterZ = p_i2159_1_.borderCenterZ;
        this.borderSize = p_i2159_1_.borderSize;
        this.borderSizeLerpTime = p_i2159_1_.borderSizeLerpTime;
        this.borderSizeLerpTarget = p_i2159_1_.borderSizeLerpTarget;
        this.borderSafeZone = p_i2159_1_.borderSafeZone;
        this.borderDamagePerBlock = p_i2159_1_.borderDamagePerBlock;
        this.borderWarningTime = p_i2159_1_.borderWarningTime;
        this.borderWarningDistance = p_i2159_1_.borderWarningDistance;
    }

    /**
     * Creates a new NBTTagCompound for the world, with the given NBTTag as the "Player"
     */
    public NBTTagCompound cloneNBTCompound(@Nullable NBTTagCompound nbt)
    {
        if (nbt == null)
        {
            nbt = this.playerTag;
        }

        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.updateTagCompound(nbttagcompound, nbt);
        return nbttagcompound;
    }

    private void updateTagCompound(NBTTagCompound nbt, NBTTagCompound playerNbt)
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.putString("Name", "1.12.2");
        nbttagcompound.putInt("Id", 1343);
        nbttagcompound.putBoolean("Snapshot", false);
        nbt.put("Version", nbttagcompound);
        nbt.putInt("DataVersion", 1343);
        nbt.putLong("RandomSeed", this.randomSeed);
        nbt.putString("generatorName", this.terrainType.func_77127_a());
        nbt.putInt("generatorVersion", this.terrainType.getVersion());
        nbt.putString("generatorOptions", this.generatorOptions);
        nbt.putInt("GameType", this.gameType.getID());
        nbt.putBoolean("MapFeatures", this.mapFeaturesEnabled);
        nbt.putInt("SpawnX", this.spawnX);
        nbt.putInt("SpawnY", this.spawnY);
        nbt.putInt("SpawnZ", this.spawnZ);
        nbt.putLong("Time", this.gameTime);
        nbt.putLong("DayTime", this.dayTime);
        nbt.putLong("SizeOnDisk", this.sizeOnDisk);
        nbt.putLong("LastPlayed", MinecraftServer.func_130071_aq());
        nbt.putString("LevelName", this.levelName);
        nbt.putInt("version", this.saveVersion);
        nbt.putInt("clearWeatherTime", this.clearWeatherTime);
        nbt.putInt("rainTime", this.rainTime);
        nbt.putBoolean("raining", this.raining);
        nbt.putInt("thunderTime", this.thunderTime);
        nbt.putBoolean("thundering", this.thundering);
        nbt.putBoolean("hardcore", this.hardcore);
        nbt.putBoolean("allowCommands", this.allowCommands);
        nbt.putBoolean("initialized", this.initialized);
        nbt.putDouble("BorderCenterX", this.borderCenterX);
        nbt.putDouble("BorderCenterZ", this.borderCenterZ);
        nbt.putDouble("BorderSize", this.borderSize);
        nbt.putLong("BorderSizeLerpTime", this.borderSizeLerpTime);
        nbt.putDouble("BorderSafeZone", this.borderSafeZone);
        nbt.putDouble("BorderDamagePerBlock", this.borderDamagePerBlock);
        nbt.putDouble("BorderSizeLerpTarget", this.borderSizeLerpTarget);
        nbt.putDouble("BorderWarningBlocks", (double)this.borderWarningDistance);
        nbt.putDouble("BorderWarningTime", (double)this.borderWarningTime);
        net.minecraftforge.fml.common.FMLCommonHandler.instance().getDataFixer().writeVersionData(nbt);

        if (this.difficulty != null)
        {
            nbt.putByte("Difficulty", (byte)this.difficulty.getId());
        }

        nbt.putBoolean("DifficultyLocked", this.difficultyLocked);
        nbt.put("GameRules", this.gameRules.write());
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        for (Entry<Integer, NBTTagCompound> entry : this.dimensionData.entrySet())
        {
            if (entry.getValue() == null || entry.getValue().func_82582_d()) continue;
            nbttagcompound1.put(String.valueOf(entry.getKey()), entry.getValue());
        }

        nbt.put("DimensionData", nbttagcompound1);

        if (playerNbt != null)
        {
            nbt.put("Player", playerNbt);
        }
    }

    /**
     * Returns the seed of current world.
     */
    public long getSeed()
    {
        return this.randomSeed;
    }

    /**
     * Returns the x spawn position
     */
    public int getSpawnX()
    {
        return this.spawnX;
    }

    /**
     * Return the Y axis spawning point of the player.
     */
    public int getSpawnY()
    {
        return this.spawnY;
    }

    /**
     * Returns the z spawn position
     */
    public int getSpawnZ()
    {
        return this.spawnZ;
    }

    public long getGameTime()
    {
        return this.gameTime;
    }

    /**
     * Get current world time
     */
    public long getDayTime()
    {
        return this.dayTime;
    }

    @SideOnly(Side.CLIENT)
    public long getSizeOnDisk()
    {
        return this.sizeOnDisk;
    }

    /**
     * Returns the player's NBTTagCompound to be loaded
     */
    public NBTTagCompound getPlayerNBTTagCompound()
    {
        return this.playerTag;
    }

    /**
     * Set the x spawn position to the passed in value
     */
    @SideOnly(Side.CLIENT)
    public void setSpawnX(int x)
    {
        this.spawnX = x;
    }

    /**
     * Sets the y spawn position
     */
    @SideOnly(Side.CLIENT)
    public void setSpawnY(int y)
    {
        this.spawnY = y;
    }

    public void setWorldTotalTime(long time)
    {
        this.gameTime = time;
    }

    /**
     * Set the z spawn position to the passed in value
     */
    @SideOnly(Side.CLIENT)
    public void setSpawnZ(int z)
    {
        this.spawnZ = z;
    }

    /**
     * Set current world time
     */
    public void setDayTime(long time)
    {
        this.dayTime = time;
    }

    public void setSpawn(BlockPos spawnPoint)
    {
        this.spawnX = spawnPoint.getX();
        this.spawnY = spawnPoint.getY();
        this.spawnZ = spawnPoint.getZ();
    }

    /**
     * Get current world name
     */
    public String getWorldName()
    {
        return this.levelName;
    }

    public void setWorldName(String worldName)
    {
        this.levelName = worldName;
    }

    /**
     * Returns the save version of this world
     */
    public int getSaveVersion()
    {
        return this.saveVersion;
    }

    /**
     * Sets the save version of the world
     */
    public void setSaveVersion(int version)
    {
        this.saveVersion = version;
    }

    /**
     * Return the last time the player was in this world.
     */
    @SideOnly(Side.CLIENT)
    public long getLastTimePlayed()
    {
        return this.lastTimePlayed;
    }

    public int getClearWeatherTime()
    {
        return this.clearWeatherTime;
    }

    public void setClearWeatherTime(int cleanWeatherTimeIn)
    {
        this.clearWeatherTime = cleanWeatherTimeIn;
    }

    /**
     * Returns true if it is thundering, false otherwise.
     */
    public boolean isThundering()
    {
        return this.thundering;
    }

    /**
     * Sets whether it is thundering or not.
     */
    public void setThundering(boolean thunderingIn)
    {
        this.thundering = thunderingIn;
    }

    /**
     * Returns the number of ticks until next thunderbolt.
     */
    public int getThunderTime()
    {
        return this.thunderTime;
    }

    /**
     * Defines the number of ticks until next thunderbolt.
     */
    public void setThunderTime(int time)
    {
        this.thunderTime = time;
    }

    /**
     * Returns true if it is raining, false otherwise.
     */
    public boolean isRaining()
    {
        return this.raining;
    }

    /**
     * Sets whether it is raining or not.
     */
    public void setRaining(boolean isRaining)
    {
        this.raining = isRaining;
    }

    /**
     * Return the number of ticks until rain.
     */
    public int getRainTime()
    {
        return this.rainTime;
    }

    /**
     * Sets the number of ticks until rain.
     */
    public void setRainTime(int time)
    {
        this.rainTime = time;
    }

    /**
     * Gets the GameType.
     */
    public GameType getGameType()
    {
        return this.gameType;
    }

    /**
     * Get whether the map features (e.g. strongholds) generation is enabled or disabled.
     */
    public boolean isMapFeaturesEnabled()
    {
        return this.mapFeaturesEnabled;
    }

    public void setMapFeaturesEnabled(boolean enabled)
    {
        this.mapFeaturesEnabled = enabled;
    }

    /**
     * Sets the GameType.
     */
    public void setGameType(GameType type)
    {
        this.gameType = type;
    }

    /**
     * Returns true if hardcore mode is enabled, otherwise false
     */
    public boolean isHardcore()
    {
        return this.hardcore;
    }

    public void setHardcore(boolean hardcoreIn)
    {
        this.hardcore = hardcoreIn;
    }

    public WorldType getTerrainType()
    {
        return this.terrainType;
    }

    public void setTerrainType(WorldType type)
    {
        this.terrainType = type;
    }

    public String func_82571_y()
    {
        return this.generatorOptions == null ? "" : this.generatorOptions;
    }

    /**
     * Returns true if commands are allowed on this World.
     */
    public boolean areCommandsAllowed()
    {
        return this.allowCommands;
    }

    public void setAllowCommands(boolean allow)
    {
        this.allowCommands = allow;
    }

    /**
     * Returns true if the World is initialized.
     */
    public boolean isInitialized()
    {
        return this.initialized;
    }

    /**
     * Sets the initialization status of the World.
     */
    public void setServerInitialized(boolean initializedIn)
    {
        this.initialized = initializedIn;
    }

    /**
     * Gets the GameRules class Instance.
     */
    public GameRules getGameRulesInstance()
    {
        return this.gameRules;
    }

    /**
     * Returns the border center X position
     */
    public double getBorderCenterX()
    {
        return this.borderCenterX;
    }

    /**
     * Returns the border center Z position
     */
    public double getBorderCenterZ()
    {
        return this.borderCenterZ;
    }

    public double getBorderSize()
    {
        return this.borderSize;
    }

    /**
     * Sets the border size
     */
    public void setBorderSize(double size)
    {
        this.borderSize = size;
    }

    /**
     * Returns the border lerp time
     */
    public long getBorderLerpTime()
    {
        return this.borderSizeLerpTime;
    }

    /**
     * Sets the border lerp time
     */
    public void setBorderLerpTime(long time)
    {
        this.borderSizeLerpTime = time;
    }

    /**
     * Returns the border lerp target
     */
    public double getBorderLerpTarget()
    {
        return this.borderSizeLerpTarget;
    }

    /**
     * Sets the border lerp target
     */
    public void setBorderLerpTarget(double lerpSize)
    {
        this.borderSizeLerpTarget = lerpSize;
    }

    /**
     * Sets the border center Z position
     */
    public void getBorderCenterZ(double posZ)
    {
        this.borderCenterZ = posZ;
    }

    /**
     * Sets the border center X position
     */
    public void getBorderCenterX(double posX)
    {
        this.borderCenterX = posX;
    }

    /**
     * Returns the border safe zone
     */
    public double getBorderSafeZone()
    {
        return this.borderSafeZone;
    }

    /**
     * Sets the border safe zone
     */
    public void setBorderSafeZone(double amount)
    {
        this.borderSafeZone = amount;
    }

    /**
     * Returns the border damage per block
     */
    public double getBorderDamagePerBlock()
    {
        return this.borderDamagePerBlock;
    }

    /**
     * Sets the border damage per block
     */
    public void setBorderDamagePerBlock(double damage)
    {
        this.borderDamagePerBlock = damage;
    }

    /**
     * Returns the border warning distance
     */
    public int getBorderWarningDistance()
    {
        return this.borderWarningDistance;
    }

    /**
     * Returns the border warning time
     */
    public int getBorderWarningTime()
    {
        return this.borderWarningTime;
    }

    /**
     * Sets the border warning distance
     */
    public void setBorderWarningDistance(int amountOfBlocks)
    {
        this.borderWarningDistance = amountOfBlocks;
    }

    /**
     * Sets the border warning time
     */
    public void setBorderWarningTime(int ticks)
    {
        this.borderWarningTime = ticks;
    }

    public EnumDifficulty getDifficulty()
    {
        return this.difficulty;
    }

    public void setDifficulty(EnumDifficulty newDifficulty)
    {
        net.minecraftforge.common.ForgeHooks.onDifficultyChange(newDifficulty, this.difficulty);
        this.difficulty = newDifficulty;
    }

    public boolean isDifficultyLocked()
    {
        return this.difficultyLocked;
    }

    public void setDifficultyLocked(boolean locked)
    {
        this.difficultyLocked = locked;
    }

    /**
     * Adds this WorldInfo instance to the crash report.
     */
    public void addToCrashReport(CrashReportCategory category)
    {
        category.addDetail("Level seed", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return String.valueOf(WorldInfo.this.getSeed());
            }
        });
        category.addDetail("Level generator", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return String.format("ID %02d - %s, ver %d. Features enabled: %b", WorldInfo.this.terrainType.getId(), WorldInfo.this.terrainType.func_77127_a(), WorldInfo.this.terrainType.getVersion(), WorldInfo.this.mapFeaturesEnabled);
            }
        });
        category.addDetail("Level generator options", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return WorldInfo.this.generatorOptions;
            }
        });
        category.addDetail("Level spawn location", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return CrashReportCategory.getCoordinateInfo(WorldInfo.this.spawnX, WorldInfo.this.spawnY, WorldInfo.this.spawnZ);
            }
        });
        category.addDetail("Level time", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return String.format("%d game time, %d day time", WorldInfo.this.gameTime, WorldInfo.this.dayTime);
            }
        });
        category.addDetail("Level dimension", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return String.valueOf(WorldInfo.this.dimension);
            }
        });
        category.addDetail("Level storage version", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                String s = "Unknown?";

                try
                {
                    switch (WorldInfo.this.saveVersion)
                    {
                        case 19132:
                            s = "McRegion";
                            break;
                        case 19133:
                            s = "Anvil";
                    }
                }
                catch (Throwable var3)
                {
                    ;
                }

                return String.format("0x%05X - %s", WorldInfo.this.saveVersion, s);
            }
        });
        category.addDetail("Level weather", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", WorldInfo.this.rainTime, WorldInfo.this.raining, WorldInfo.this.thunderTime, WorldInfo.this.thundering);
            }
        });
        category.addDetail("Level game mode", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", WorldInfo.this.gameType.getName(), WorldInfo.this.gameType.getID(), WorldInfo.this.hardcore, WorldInfo.this.allowCommands);
            }
        });
    }
    /**
     * Allow access to additional mod specific world based properties
     * Used by FML to store mod list associated with a world, and maybe an id map
     * Used by Forge to store the dimensions available to a world
     * @param additionalProperties
     */
    public void setAdditionalProperties(java.util.Map<String,net.minecraft.nbt.NBTBase> additionalProperties)
    {
        // one time set for this
        if (this.additionalProperties == null)
        {
            this.additionalProperties = additionalProperties;
        }
    }

    public net.minecraft.nbt.NBTBase getAdditionalProperty(String additionalProperty)
    {
        return this.additionalProperties!=null? this.additionalProperties.get(additionalProperty) : null;
    }

    @Deprecated //Use the int version below, and pass in dimension id NOT TYPE id
    public NBTTagCompound getDimensionData(DimensionType dimensionIn)
    {
        return getDimensionData(dimensionIn.getId());
    }
    public NBTTagCompound getDimensionData(int dimensionIn)
    {
        NBTTagCompound nbttagcompound = this.dimensionData.get(dimensionIn);
        return nbttagcompound == null ? new NBTTagCompound() : nbttagcompound;
    }

    @Deprecated //Use the int version below, and pass in dimension id NOT TYPE id
    public void setDimensionData(DimensionType dimensionIn, NBTTagCompound compound)
    {
        this.setDimensionData(dimensionIn.getId(), compound);
    }

    //Dimension numerical ID version of setter, as two dimensions could in theory have the same DimensionType. ID should be grabbed from the world NOT the Type
    public void setDimensionData(int dimensionID, NBTTagCompound compound)
    {
        this.dimensionData.put(dimensionID, compound);
    }

    @SideOnly(Side.CLIENT)
    public int getVersionId()
    {
        return this.versionId;
    }

    @SideOnly(Side.CLIENT)
    public boolean isVersionSnapshot()
    {
        return this.versionSnapshot;
    }

    @SideOnly(Side.CLIENT)
    public String getVersionName()
    {
        return this.versionName;
    }
}