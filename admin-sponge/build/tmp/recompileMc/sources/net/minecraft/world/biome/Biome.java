package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenDoublePlant;
import net.minecraft.world.gen.feature.WorldGenSwamp;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Biome extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<Biome>
{
    private static final Logger LOGGER = LogManager.getLogger();
    protected static final IBlockState field_185365_a = Blocks.STONE.getDefaultState();
    protected static final IBlockState field_185366_b = Blocks.AIR.getDefaultState();
    protected static final IBlockState field_185367_c = Blocks.BEDROCK.getDefaultState();
    protected static final IBlockState field_185368_d = Blocks.GRAVEL.getDefaultState();
    protected static final IBlockState field_185369_e = Blocks.RED_SANDSTONE.getDefaultState();
    protected static final IBlockState field_185370_f = Blocks.SANDSTONE.getDefaultState();
    protected static final IBlockState field_185371_g = Blocks.ICE.getDefaultState();
    protected static final IBlockState field_185372_h = Blocks.WATER.getDefaultState();
    public static final ObjectIntIdentityMap<Biome> MUTATION_TO_BASE_ID_MAP = new ObjectIntIdentityMap<Biome>();
    protected static final NoiseGeneratorPerlin TEMPERATURE_NOISE = new NoiseGeneratorPerlin(new Random(1234L), 1);
    protected static final NoiseGeneratorPerlin INFO_NOISE = new NoiseGeneratorPerlin(new Random(2345L), 1);
    protected static final WorldGenDoublePlant field_180280_ag = new WorldGenDoublePlant();
    protected static final WorldGenTrees field_76757_N = new WorldGenTrees(false);
    protected static final WorldGenBigTree field_76758_O = new WorldGenBigTree(false);
    protected static final WorldGenSwamp field_76763_Q = new WorldGenSwamp();
    public static final RegistryNamespaced<ResourceLocation, Biome> REGISTRY = net.minecraftforge.registries.GameData.getWrapper(Biome.class);
    private final String field_76791_y;
    /** The base height of this biome. Default 0.1. */
    private final float depth;
    /** The variation from the base height of the biome. Default 0.3. */
    private final float scale;
    /** The temperature of this biome. */
    private final float temperature;
    /** The rainfall in this biome. */
    private final float downfall;
    /** Color tint applied to water depending on biome */
    private final int waterColor;
    private final boolean field_76766_R;
    private final boolean field_76765_S;
    /** The unique identifier of the biome for which this is a mutation of. */
    @Nullable
    private final String parent;
    public IBlockState field_76752_A = Blocks.GRASS.getDefaultState();
    public IBlockState field_76753_B = Blocks.DIRT.getDefaultState();
    public BiomeDecorator field_76760_I;
    protected List<Biome.SpawnListEntry> field_76761_J = Lists.<Biome.SpawnListEntry>newArrayList();
    protected List<Biome.SpawnListEntry> field_76762_K = Lists.<Biome.SpawnListEntry>newArrayList();
    protected List<Biome.SpawnListEntry> field_76755_L = Lists.<Biome.SpawnListEntry>newArrayList();
    protected List<Biome.SpawnListEntry> field_82914_M = Lists.<Biome.SpawnListEntry>newArrayList();
    // Forge: Stores the spawnable lists for non-vanilla EnumCreatureTypes. Can't be an EnumMap as that doesn't handle new enum values being added after it's created.
    protected java.util.Map<EnumCreatureType, List<Biome.SpawnListEntry>> modSpawnableLists = com.google.common.collect.Maps.newHashMap();

    public static int getIdForBiome(Biome biome)
    {
        return REGISTRY.getId(biome);
    }

    @Nullable
    public static Biome getBiomeForId(int id)
    {
        return REGISTRY.get(id);
    }

    @Nullable
    public static Biome getMutationForBiome(Biome biome)
    {
        return MUTATION_TO_BASE_ID_MAP.getByValue(getIdForBiome(biome));
    }

    public Biome(Biome.BiomeProperties p_i46713_1_)
    {
        this.field_76791_y = p_i46713_1_.field_185412_a;
        this.depth = p_i46713_1_.field_185413_b;
        this.scale = p_i46713_1_.field_185414_c;
        this.temperature = p_i46713_1_.field_185415_d;
        this.downfall = p_i46713_1_.field_185416_e;
        this.waterColor = p_i46713_1_.field_185417_f;
        this.field_76766_R = p_i46713_1_.field_185418_g;
        this.field_76765_S = p_i46713_1_.field_185419_h;
        this.parent = p_i46713_1_.field_185420_i;
        this.field_76760_I = this.func_76729_a();
        this.field_76762_K.add(new Biome.SpawnListEntry(EntitySheep.class, 12, 4, 4));
        this.field_76762_K.add(new Biome.SpawnListEntry(EntityPig.class, 10, 4, 4));
        this.field_76762_K.add(new Biome.SpawnListEntry(EntityChicken.class, 10, 4, 4));
        this.field_76762_K.add(new Biome.SpawnListEntry(EntityCow.class, 8, 4, 4));
        this.field_76761_J.add(new Biome.SpawnListEntry(EntitySpider.class, 100, 4, 4));
        this.field_76761_J.add(new Biome.SpawnListEntry(EntityZombie.class, 95, 4, 4));
        this.field_76761_J.add(new Biome.SpawnListEntry(EntityZombieVillager.class, 5, 1, 1));
        this.field_76761_J.add(new Biome.SpawnListEntry(EntitySkeleton.class, 100, 4, 4));
        this.field_76761_J.add(new Biome.SpawnListEntry(EntityCreeper.class, 100, 4, 4));
        this.field_76761_J.add(new Biome.SpawnListEntry(EntitySlime.class, 100, 4, 4));
        this.field_76761_J.add(new Biome.SpawnListEntry(EntityEnderman.class, 10, 1, 4));
        this.field_76761_J.add(new Biome.SpawnListEntry(EntityWitch.class, 5, 1, 1));
        this.field_76755_L.add(new Biome.SpawnListEntry(EntitySquid.class, 10, 4, 4));
        this.field_82914_M.add(new Biome.SpawnListEntry(EntityBat.class, 10, 8, 8));
        this.addDefaultFlowers();
    }

    public BiomeDecorator func_76729_a()
    {
        return getModdedBiomeDecorator(new BiomeDecorator());
    }

    public boolean isMutation()
    {
        return this.parent != null;
    }

    public WorldGenAbstractTree func_150567_a(Random p_150567_1_)
    {
        return (WorldGenAbstractTree)(p_150567_1_.nextInt(10) == 0 ? field_76758_O : field_76757_N);
    }

    public WorldGenerator func_76730_b(Random p_76730_1_)
    {
        return new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
    }

    public BlockFlower.EnumFlowerType func_180623_a(Random p_180623_1_, BlockPos p_180623_2_)
    {
        return p_180623_1_.nextInt(3) > 0 ? BlockFlower.EnumFlowerType.DANDELION : BlockFlower.EnumFlowerType.POPPY;
    }

    /**
     * takes temperature, returns color
     */
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float currentTemperature)
    {
        currentTemperature = currentTemperature / 3.0F;
        currentTemperature = MathHelper.clamp(currentTemperature, -1.0F, 1.0F);
        return MathHelper.hsvToRGB(0.62222224F - currentTemperature * 0.05F, 0.5F + currentTemperature * 0.1F, 1.0F);
    }

    /**
     * Returns the correspondent list of the EnumCreatureType informed.
     */
    public List<Biome.SpawnListEntry> getSpawns(EnumCreatureType creatureType)
    {
        switch (creatureType)
        {
            case MONSTER:
                return this.field_76761_J;
            case CREATURE:
                return this.field_76762_K;
            case WATER_CREATURE:
                return this.field_76755_L;
            case AMBIENT:
                return this.field_82914_M;
            default:
                // Forge: Return a non-empty list for non-vanilla EnumCreatureTypes
                if (!this.modSpawnableLists.containsKey(creatureType)) this.modSpawnableLists.put(creatureType, Lists.<Biome.SpawnListEntry>newArrayList());
                return this.modSpawnableLists.get(creatureType);
        }
    }

    public boolean func_76746_c()
    {
        return this.func_150559_j();
    }

    public boolean func_76738_d()
    {
        return this.func_150559_j() ? false : this.field_76765_S;
    }

    /**
     * Checks to see if the rainfall level of the biome is extremely high
     */
    public boolean isHighHumidity()
    {
        return this.getDownfall() > 0.85F;
    }

    /**
     * returns the chance a creature has to spawn.
     */
    public float getSpawningChance()
    {
        return 0.1F;
    }

    /**
     * Gets the current temperature at the given location, based off of the default for this biome, the elevation of the
     * position, and {@linkplain #TEMPERATURE_NOISE} some random perlin noise.
     */
    public final float getTemperature(BlockPos pos)
    {
        if (pos.getY() > 64)
        {
            float f = (float)(TEMPERATURE_NOISE.getValue((double)((float)pos.getX() / 8.0F), (double)((float)pos.getZ() / 8.0F)) * 4.0D);
            return this.getDefaultTemperature() - (f + (float)pos.getY() - 64.0F) * 0.05F / 30.0F;
        }
        else
        {
            return this.getDefaultTemperature();
        }
    }

    public void func_180624_a(World p_180624_1_, Random p_180624_2_, BlockPos p_180624_3_)
    {
        this.field_76760_I.func_180292_a(p_180624_1_, p_180624_2_, this, p_180624_3_);
    }

    public void func_180622_a(World p_180622_1_, Random p_180622_2_, ChunkPrimer p_180622_3_, int p_180622_4_, int p_180622_5_, double p_180622_6_)
    {
        this.func_180628_b(p_180622_1_, p_180622_2_, p_180622_3_, p_180622_4_, p_180622_5_, p_180622_6_);
    }

    @SideOnly(Side.CLIENT)
    public int getGrassColor(BlockPos pos)
    {
        double d0 = (double)MathHelper.clamp(this.getTemperature(pos), 0.0F, 1.0F);
        double d1 = (double)MathHelper.clamp(this.getDownfall(), 0.0F, 1.0F);
        return getModdedBiomeGrassColor(ColorizerGrass.get(d0, d1));
    }

    public final void func_180628_b(World p_180628_1_, Random p_180628_2_, ChunkPrimer p_180628_3_, int p_180628_4_, int p_180628_5_, double p_180628_6_)
    {
        int i = p_180628_1_.getSeaLevel();
        IBlockState iblockstate = this.field_76752_A;
        IBlockState iblockstate1 = this.field_76753_B;
        int j = -1;
        int k = (int)(p_180628_6_ / 3.0D + 3.0D + p_180628_2_.nextDouble() * 0.25D);
        int l = p_180628_4_ & 15;
        int i1 = p_180628_5_ & 15;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int j1 = 255; j1 >= 0; --j1)
        {
            if (j1 <= p_180628_2_.nextInt(5))
            {
                p_180628_3_.func_177855_a(i1, j1, l, field_185367_c);
            }
            else
            {
                IBlockState iblockstate2 = p_180628_3_.func_177856_a(i1, j1, l);

                if (iblockstate2.getMaterial() == Material.AIR)
                {
                    j = -1;
                }
                else if (iblockstate2.getBlock() == Blocks.STONE)
                {
                    if (j == -1)
                    {
                        if (k <= 0)
                        {
                            iblockstate = field_185366_b;
                            iblockstate1 = field_185365_a;
                        }
                        else if (j1 >= i - 4 && j1 <= i + 1)
                        {
                            iblockstate = this.field_76752_A;
                            iblockstate1 = this.field_76753_B;
                        }

                        if (j1 < i && (iblockstate == null || iblockstate.getMaterial() == Material.AIR))
                        {
                            if (this.getTemperature(blockpos$mutableblockpos.setPos(p_180628_4_, j1, p_180628_5_)) < 0.15F)
                            {
                                iblockstate = field_185371_g;
                            }
                            else
                            {
                                iblockstate = field_185372_h;
                            }
                        }

                        j = k;

                        if (j1 >= i - 1)
                        {
                            p_180628_3_.func_177855_a(i1, j1, l, iblockstate);
                        }
                        else if (j1 < i - 7 - k)
                        {
                            iblockstate = field_185366_b;
                            iblockstate1 = field_185365_a;
                            p_180628_3_.func_177855_a(i1, j1, l, field_185368_d);
                        }
                        else
                        {
                            p_180628_3_.func_177855_a(i1, j1, l, iblockstate1);
                        }
                    }
                    else if (j > 0)
                    {
                        --j;
                        p_180628_3_.func_177855_a(i1, j1, l, iblockstate1);

                        if (j == 0 && iblockstate1.getBlock() == Blocks.SAND && k > 1)
                        {
                            j = p_180628_2_.nextInt(4) + Math.max(0, j1 - 63);
                            iblockstate1 = iblockstate1.get(BlockSand.field_176504_a) == BlockSand.EnumType.RED_SAND ? field_185369_e : field_185370_f;
                        }
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public int getFoliageColor(BlockPos pos)
    {
        double d0 = (double)MathHelper.clamp(this.getTemperature(pos), 0.0F, 1.0F);
        double d1 = (double)MathHelper.clamp(this.getDownfall(), 0.0F, 1.0F);
        return getModdedBiomeFoliageColor(ColorizerFoliage.get(d0, d1));
    }

    public Class <? extends Biome > func_150562_l()
    {
        return this.getClass();
    }

    public Biome.TempCategory getTempCategory()
    {
        if ((double)this.getDefaultTemperature() < 0.2D)
        {
            return Biome.TempCategory.COLD;
        }
        else
        {
            return (double)this.getDefaultTemperature() < 1.0D ? Biome.TempCategory.MEDIUM : Biome.TempCategory.WARM;
        }
    }

    /**
     * return the biome specified by biomeID, or 0 (ocean) if out of bounds
     */
    @Nullable
    public static Biome getBiome(int id)
    {
        return getBiome(id, (Biome)null);
    }

    public static Biome getBiome(int biomeId, Biome fallback)
    {
        Biome biome = getBiomeForId(biomeId);
        return biome == null ? fallback : biome;
    }

    public boolean func_185352_i()
    {
        return false;
    }

    public final float getDepth()
    {
        return this.depth;
    }

    /**
     * Gets a floating point representation of this biome's rainfall
     */
    public final float getDownfall()
    {
        return this.downfall;
    }

    @SideOnly(Side.CLIENT)
    public final String func_185359_l()
    {
        return this.field_76791_y;
    }

    public final float getScale()
    {
        return this.scale;
    }

    /**
     * Gets the constant default temperature for this biome.
     */
    public final float getDefaultTemperature()
    {
        return this.temperature;
    }

    @SideOnly(Side.CLIENT)
    public final int getWaterColor()
    {
        return getWaterColorMultiplier();
    }

    public final boolean func_150559_j()
    {
        return this.field_76766_R;
    }

    /* ========================================= FORGE START ======================================*/
    protected List<FlowerEntry> flowers = new java.util.ArrayList<FlowerEntry>();

    public BiomeDecorator getModdedBiomeDecorator(BiomeDecorator original)
    {
        return new net.minecraftforge.event.terraingen.DeferredBiomeDecorator(original);
    }

    public int getWaterColorMultiplier()
    {
        net.minecraftforge.event.terraingen.BiomeEvent.GetWaterColor event = new net.minecraftforge.event.terraingen.BiomeEvent.GetWaterColor(this, waterColor);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        return event.getNewColor();
    }

    public int getModdedBiomeGrassColor(int original)
    {
        net.minecraftforge.event.terraingen.BiomeEvent.GetGrassColor event = new net.minecraftforge.event.terraingen.BiomeEvent.GetGrassColor(this, original);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        return event.getNewColor();
    }

    public int getModdedBiomeFoliageColor(int original)
    {
        net.minecraftforge.event.terraingen.BiomeEvent.GetFoliageColor event = new net.minecraftforge.event.terraingen.BiomeEvent.GetFoliageColor(this, original);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        return event.getNewColor();
    }

    /**
     * Weighted random holder class used to hold possible flowers
     * that can spawn in this biome when bonemeal is used on grass.
     */
    public static class FlowerEntry extends WeightedRandom.Item
    {
        public final net.minecraft.block.state.IBlockState state;
        public FlowerEntry(net.minecraft.block.state.IBlockState state, int weight)
        {
            super(weight);
            this.state = state;
        }
    }

    /**
     * Adds the default flowers, as of 1.7, it is 2 yellow, and 1 red. I chose 10 to allow some wiggle room in the numbers.
     */
    public void addDefaultFlowers()
    {
        addFlower(Blocks.field_150327_N.getDefaultState().func_177226_a(Blocks.field_150327_N.func_176494_l(), BlockFlower.EnumFlowerType.DANDELION), 20);
        addFlower(Blocks.field_150328_O.getDefaultState().func_177226_a(Blocks.field_150328_O.func_176494_l(), BlockFlower.EnumFlowerType.POPPY), 10);
    }

    /** Register a new plant to be planted when bonemeal is used on grass.
     * @param state The block to place.
     * @param weight The weight of the plant, where red flowers are
     *               10 and yellow flowers are 20.
     */
    public void addFlower(IBlockState state, int weight)
    {
        this.flowers.add(new FlowerEntry(state, weight));
    }

    public void plantFlower(World world, Random rand, BlockPos pos)
    {
        if (flowers.isEmpty()) return;
        FlowerEntry flower = (FlowerEntry)WeightedRandom.getRandomItem(rand, flowers);
        if (flower == null || flower.state == null ||
            (flower.state.getBlock() instanceof net.minecraft.block.BlockBush &&
              !((net.minecraft.block.BlockBush)flower.state.getBlock()).func_180671_f(world, pos, flower.state)))
        {
            return;
        }

        world.setBlockState(pos, flower.state, 3);
    }

    /* ========================================= FORGE END ======================================*/

    /**
     * Registers all of the vanilla biomes.
     */
    public static void registerBiomes()
    {
        register(0, "ocean", new BiomeOcean((new Biome.BiomeProperties("Ocean")).func_185398_c(-1.0F).func_185400_d(0.1F)));
        register(1, "plains", new BiomePlains(false, (new Biome.BiomeProperties("Plains")).func_185398_c(0.125F).func_185400_d(0.05F).func_185410_a(0.8F).func_185395_b(0.4F)));
        register(2, "desert", new BiomeDesert((new Biome.BiomeProperties("Desert")).func_185398_c(0.125F).func_185400_d(0.05F).func_185410_a(2.0F).func_185395_b(0.0F).func_185396_a()));
        register(3, "extreme_hills", new BiomeHills(BiomeHills.Type.NORMAL, (new Biome.BiomeProperties("Extreme Hills")).func_185398_c(1.0F).func_185400_d(0.5F).func_185410_a(0.2F).func_185395_b(0.3F)));
        register(4, "forest", new BiomeForest(BiomeForest.Type.NORMAL, (new Biome.BiomeProperties("Forest")).func_185410_a(0.7F).func_185395_b(0.8F)));
        register(5, "taiga", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new Biome.BiomeProperties("Taiga")).func_185398_c(0.2F).func_185400_d(0.2F).func_185410_a(0.25F).func_185395_b(0.8F)));
        register(6, "swampland", new BiomeSwamp((new Biome.BiomeProperties("Swampland")).func_185398_c(-0.2F).func_185400_d(0.1F).func_185410_a(0.8F).func_185395_b(0.9F).func_185402_a(14745518)));
        register(7, "river", new BiomeRiver((new Biome.BiomeProperties("River")).func_185398_c(-0.5F).func_185400_d(0.0F)));
        register(8, "hell", new BiomeHell((new Biome.BiomeProperties("Hell")).func_185410_a(2.0F).func_185395_b(0.0F).func_185396_a()));
        register(9, "sky", new BiomeEnd((new Biome.BiomeProperties("The End")).func_185396_a()));
        register(10, "frozen_ocean", new BiomeOcean((new Biome.BiomeProperties("FrozenOcean")).func_185398_c(-1.0F).func_185400_d(0.1F).func_185410_a(0.0F).func_185395_b(0.5F).func_185411_b()));
        register(11, "frozen_river", new BiomeRiver((new Biome.BiomeProperties("FrozenRiver")).func_185398_c(-0.5F).func_185400_d(0.0F).func_185410_a(0.0F).func_185395_b(0.5F).func_185411_b()));
        register(12, "ice_flats", new BiomeSnow(false, (new Biome.BiomeProperties("Ice Plains")).func_185398_c(0.125F).func_185400_d(0.05F).func_185410_a(0.0F).func_185395_b(0.5F).func_185411_b()));
        register(13, "ice_mountains", new BiomeSnow(false, (new Biome.BiomeProperties("Ice Mountains")).func_185398_c(0.45F).func_185400_d(0.3F).func_185410_a(0.0F).func_185395_b(0.5F).func_185411_b()));
        register(14, "mushroom_island", new BiomeMushroomIsland((new Biome.BiomeProperties("MushroomIsland")).func_185398_c(0.2F).func_185400_d(0.3F).func_185410_a(0.9F).func_185395_b(1.0F)));
        register(15, "mushroom_island_shore", new BiomeMushroomIsland((new Biome.BiomeProperties("MushroomIslandShore")).func_185398_c(0.0F).func_185400_d(0.025F).func_185410_a(0.9F).func_185395_b(1.0F)));
        register(16, "beaches", new BiomeBeach((new Biome.BiomeProperties("Beach")).func_185398_c(0.0F).func_185400_d(0.025F).func_185410_a(0.8F).func_185395_b(0.4F)));
        register(17, "desert_hills", new BiomeDesert((new Biome.BiomeProperties("DesertHills")).func_185398_c(0.45F).func_185400_d(0.3F).func_185410_a(2.0F).func_185395_b(0.0F).func_185396_a()));
        register(18, "forest_hills", new BiomeForest(BiomeForest.Type.NORMAL, (new Biome.BiomeProperties("ForestHills")).func_185398_c(0.45F).func_185400_d(0.3F).func_185410_a(0.7F).func_185395_b(0.8F)));
        register(19, "taiga_hills", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new Biome.BiomeProperties("TaigaHills")).func_185410_a(0.25F).func_185395_b(0.8F).func_185398_c(0.45F).func_185400_d(0.3F)));
        register(20, "smaller_extreme_hills", new BiomeHills(BiomeHills.Type.EXTRA_TREES, (new Biome.BiomeProperties("Extreme Hills Edge")).func_185398_c(0.8F).func_185400_d(0.3F).func_185410_a(0.2F).func_185395_b(0.3F)));
        register(21, "jungle", new BiomeJungle(false, (new Biome.BiomeProperties("Jungle")).func_185410_a(0.95F).func_185395_b(0.9F)));
        register(22, "jungle_hills", new BiomeJungle(false, (new Biome.BiomeProperties("JungleHills")).func_185398_c(0.45F).func_185400_d(0.3F).func_185410_a(0.95F).func_185395_b(0.9F)));
        register(23, "jungle_edge", new BiomeJungle(true, (new Biome.BiomeProperties("JungleEdge")).func_185410_a(0.95F).func_185395_b(0.8F)));
        register(24, "deep_ocean", new BiomeOcean((new Biome.BiomeProperties("Deep Ocean")).func_185398_c(-1.8F).func_185400_d(0.1F)));
        register(25, "stone_beach", new BiomeStoneBeach((new Biome.BiomeProperties("Stone Beach")).func_185398_c(0.1F).func_185400_d(0.8F).func_185410_a(0.2F).func_185395_b(0.3F)));
        register(26, "cold_beach", new BiomeBeach((new Biome.BiomeProperties("Cold Beach")).func_185398_c(0.0F).func_185400_d(0.025F).func_185410_a(0.05F).func_185395_b(0.3F).func_185411_b()));
        register(27, "birch_forest", new BiomeForest(BiomeForest.Type.BIRCH, (new Biome.BiomeProperties("Birch Forest")).func_185410_a(0.6F).func_185395_b(0.6F)));
        register(28, "birch_forest_hills", new BiomeForest(BiomeForest.Type.BIRCH, (new Biome.BiomeProperties("Birch Forest Hills")).func_185398_c(0.45F).func_185400_d(0.3F).func_185410_a(0.6F).func_185395_b(0.6F)));
        register(29, "roofed_forest", new BiomeForest(BiomeForest.Type.ROOFED, (new Biome.BiomeProperties("Roofed Forest")).func_185410_a(0.7F).func_185395_b(0.8F)));
        register(30, "taiga_cold", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new Biome.BiomeProperties("Cold Taiga")).func_185398_c(0.2F).func_185400_d(0.2F).func_185410_a(-0.5F).func_185395_b(0.4F).func_185411_b()));
        register(31, "taiga_cold_hills", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new Biome.BiomeProperties("Cold Taiga Hills")).func_185398_c(0.45F).func_185400_d(0.3F).func_185410_a(-0.5F).func_185395_b(0.4F).func_185411_b()));
        register(32, "redwood_taiga", new BiomeTaiga(BiomeTaiga.Type.MEGA, (new Biome.BiomeProperties("Mega Taiga")).func_185410_a(0.3F).func_185395_b(0.8F).func_185398_c(0.2F).func_185400_d(0.2F)));
        register(33, "redwood_taiga_hills", new BiomeTaiga(BiomeTaiga.Type.MEGA, (new Biome.BiomeProperties("Mega Taiga Hills")).func_185398_c(0.45F).func_185400_d(0.3F).func_185410_a(0.3F).func_185395_b(0.8F)));
        register(34, "extreme_hills_with_trees", new BiomeHills(BiomeHills.Type.EXTRA_TREES, (new Biome.BiomeProperties("Extreme Hills+")).func_185398_c(1.0F).func_185400_d(0.5F).func_185410_a(0.2F).func_185395_b(0.3F)));
        register(35, "savanna", new BiomeSavanna((new Biome.BiomeProperties("Savanna")).func_185398_c(0.125F).func_185400_d(0.05F).func_185410_a(1.2F).func_185395_b(0.0F).func_185396_a()));
        register(36, "savanna_rock", new BiomeSavanna((new Biome.BiomeProperties("Savanna Plateau")).func_185398_c(1.5F).func_185400_d(0.025F).func_185410_a(1.0F).func_185395_b(0.0F).func_185396_a()));
        register(37, "mesa", new BiomeMesa(false, false, (new Biome.BiomeProperties("Mesa")).func_185410_a(2.0F).func_185395_b(0.0F).func_185396_a()));
        register(38, "mesa_rock", new BiomeMesa(false, true, (new Biome.BiomeProperties("Mesa Plateau F")).func_185398_c(1.5F).func_185400_d(0.025F).func_185410_a(2.0F).func_185395_b(0.0F).func_185396_a()));
        register(39, "mesa_clear_rock", new BiomeMesa(false, false, (new Biome.BiomeProperties("Mesa Plateau")).func_185398_c(1.5F).func_185400_d(0.025F).func_185410_a(2.0F).func_185395_b(0.0F).func_185396_a()));
        register(127, "void", new BiomeVoid((new Biome.BiomeProperties("The Void")).func_185396_a()));
        register(129, "mutated_plains", new BiomePlains(true, (new Biome.BiomeProperties("Sunflower Plains")).func_185399_a("plains").func_185398_c(0.125F).func_185400_d(0.05F).func_185410_a(0.8F).func_185395_b(0.4F)));
        register(130, "mutated_desert", new BiomeDesert((new Biome.BiomeProperties("Desert M")).func_185399_a("desert").func_185398_c(0.225F).func_185400_d(0.25F).func_185410_a(2.0F).func_185395_b(0.0F).func_185396_a()));
        register(131, "mutated_extreme_hills", new BiomeHills(BiomeHills.Type.MUTATED, (new Biome.BiomeProperties("Extreme Hills M")).func_185399_a("extreme_hills").func_185398_c(1.0F).func_185400_d(0.5F).func_185410_a(0.2F).func_185395_b(0.3F)));
        register(132, "mutated_forest", new BiomeForest(BiomeForest.Type.FLOWER, (new Biome.BiomeProperties("Flower Forest")).func_185399_a("forest").func_185400_d(0.4F).func_185410_a(0.7F).func_185395_b(0.8F)));
        register(133, "mutated_taiga", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new Biome.BiomeProperties("Taiga M")).func_185399_a("taiga").func_185398_c(0.3F).func_185400_d(0.4F).func_185410_a(0.25F).func_185395_b(0.8F)));
        register(134, "mutated_swampland", new BiomeSwamp((new Biome.BiomeProperties("Swampland M")).func_185399_a("swampland").func_185398_c(-0.1F).func_185400_d(0.3F).func_185410_a(0.8F).func_185395_b(0.9F).func_185402_a(14745518)));
        register(140, "mutated_ice_flats", new BiomeSnow(true, (new Biome.BiomeProperties("Ice Plains Spikes")).func_185399_a("ice_flats").func_185398_c(0.425F).func_185400_d(0.45000002F).func_185410_a(0.0F).func_185395_b(0.5F).func_185411_b()));
        register(149, "mutated_jungle", new BiomeJungle(false, (new Biome.BiomeProperties("Jungle M")).func_185399_a("jungle").func_185398_c(0.2F).func_185400_d(0.4F).func_185410_a(0.95F).func_185395_b(0.9F)));
        register(151, "mutated_jungle_edge", new BiomeJungle(true, (new Biome.BiomeProperties("JungleEdge M")).func_185399_a("jungle_edge").func_185398_c(0.2F).func_185400_d(0.4F).func_185410_a(0.95F).func_185395_b(0.8F)));
        register(155, "mutated_birch_forest", new BiomeForestMutated((new Biome.BiomeProperties("Birch Forest M")).func_185399_a("birch_forest").func_185398_c(0.2F).func_185400_d(0.4F).func_185410_a(0.6F).func_185395_b(0.6F)));
        register(156, "mutated_birch_forest_hills", new BiomeForestMutated((new Biome.BiomeProperties("Birch Forest Hills M")).func_185399_a("birch_forest_hills").func_185398_c(0.55F).func_185400_d(0.5F).func_185410_a(0.6F).func_185395_b(0.6F)));
        register(157, "mutated_roofed_forest", new BiomeForest(BiomeForest.Type.ROOFED, (new Biome.BiomeProperties("Roofed Forest M")).func_185399_a("roofed_forest").func_185398_c(0.2F).func_185400_d(0.4F).func_185410_a(0.7F).func_185395_b(0.8F)));
        register(158, "mutated_taiga_cold", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new Biome.BiomeProperties("Cold Taiga M")).func_185399_a("taiga_cold").func_185398_c(0.3F).func_185400_d(0.4F).func_185410_a(-0.5F).func_185395_b(0.4F).func_185411_b()));
        register(160, "mutated_redwood_taiga", new BiomeTaiga(BiomeTaiga.Type.MEGA_SPRUCE, (new Biome.BiomeProperties("Mega Spruce Taiga")).func_185399_a("redwood_taiga").func_185398_c(0.2F).func_185400_d(0.2F).func_185410_a(0.25F).func_185395_b(0.8F)));
        register(161, "mutated_redwood_taiga_hills", new BiomeTaiga(BiomeTaiga.Type.MEGA_SPRUCE, (new Biome.BiomeProperties("Redwood Taiga Hills M")).func_185399_a("redwood_taiga_hills").func_185398_c(0.2F).func_185400_d(0.2F).func_185410_a(0.25F).func_185395_b(0.8F)));
        register(162, "mutated_extreme_hills_with_trees", new BiomeHills(BiomeHills.Type.MUTATED, (new Biome.BiomeProperties("Extreme Hills+ M")).func_185399_a("extreme_hills_with_trees").func_185398_c(1.0F).func_185400_d(0.5F).func_185410_a(0.2F).func_185395_b(0.3F)));
        register(163, "mutated_savanna", new BiomeSavannaMutated((new Biome.BiomeProperties("Savanna M")).func_185399_a("savanna").func_185398_c(0.3625F).func_185400_d(1.225F).func_185410_a(1.1F).func_185395_b(0.0F).func_185396_a()));
        register(164, "mutated_savanna_rock", new BiomeSavannaMutated((new Biome.BiomeProperties("Savanna Plateau M")).func_185399_a("savanna_rock").func_185398_c(1.05F).func_185400_d(1.2125001F).func_185410_a(1.0F).func_185395_b(0.0F).func_185396_a()));
        register(165, "mutated_mesa", new BiomeMesa(true, false, (new Biome.BiomeProperties("Mesa (Bryce)")).func_185399_a("mesa").func_185410_a(2.0F).func_185395_b(0.0F).func_185396_a()));
        register(166, "mutated_mesa_rock", new BiomeMesa(false, true, (new Biome.BiomeProperties("Mesa Plateau F M")).func_185399_a("mesa_rock").func_185398_c(0.45F).func_185400_d(0.3F).func_185410_a(2.0F).func_185395_b(0.0F).func_185396_a()));
        register(167, "mutated_mesa_clear_rock", new BiomeMesa(false, false, (new Biome.BiomeProperties("Mesa Plateau M")).func_185399_a("mesa_clear_rock").func_185398_c(0.45F).func_185400_d(0.3F).func_185410_a(2.0F).func_185395_b(0.0F).func_185396_a()));
    }

    /**
     * Registers a new biome into the registry.
     */
    public static void register(int id, String name, Biome biome)
    {
        REGISTRY.register(id, new ResourceLocation(name), biome);

        if (biome.isMutation())
        {
            MUTATION_TO_BASE_ID_MAP.put(biome, getIdForBiome(REGISTRY.get(new ResourceLocation(biome.parent))));
        }
    }

    public static class BiomeProperties
        {
            private final String field_185412_a;
            private float field_185413_b = 0.1F;
            private float field_185414_c = 0.2F;
            private float field_185415_d = 0.5F;
            private float field_185416_e = 0.5F;
            private int field_185417_f = 16777215;
            private boolean field_185418_g;
            private boolean field_185419_h = true;
            @Nullable
            private String field_185420_i;

            public BiomeProperties(String p_i47073_1_)
            {
                this.field_185412_a = p_i47073_1_;
            }

            public Biome.BiomeProperties func_185410_a(float p_185410_1_)
            {
                if (p_185410_1_ > 0.1F && p_185410_1_ < 0.2F)
                {
                    throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
                }
                else
                {
                    this.field_185415_d = p_185410_1_;
                    return this;
                }
            }

            public Biome.BiomeProperties func_185395_b(float p_185395_1_)
            {
                this.field_185416_e = p_185395_1_;
                return this;
            }

            public Biome.BiomeProperties func_185398_c(float p_185398_1_)
            {
                this.field_185413_b = p_185398_1_;
                return this;
            }

            public Biome.BiomeProperties func_185400_d(float p_185400_1_)
            {
                this.field_185414_c = p_185400_1_;
                return this;
            }

            public Biome.BiomeProperties func_185396_a()
            {
                this.field_185419_h = false;
                return this;
            }

            public Biome.BiomeProperties func_185411_b()
            {
                this.field_185418_g = true;
                return this;
            }

            public Biome.BiomeProperties func_185402_a(int p_185402_1_)
            {
                this.field_185417_f = p_185402_1_;
                return this;
            }

            public Biome.BiomeProperties func_185399_a(String p_185399_1_)
            {
                this.field_185420_i = p_185399_1_;
                return this;
            }
        }

    public static class SpawnListEntry extends WeightedRandom.Item
        {
            public Class <? extends EntityLiving > field_76300_b;
            public int minGroupCount;
            public int maxGroupCount;

            public SpawnListEntry(Class <? extends EntityLiving > p_i1970_1_, int p_i1970_2_, int p_i1970_3_, int p_i1970_4_)
            {
                super(p_i1970_2_);
                this.field_76300_b = p_i1970_1_;
                this.minGroupCount = p_i1970_3_;
                this.maxGroupCount = p_i1970_4_;
            }

            public String toString()
            {
                return this.field_76300_b.getSimpleName() + "*(" + this.minGroupCount + "-" + this.maxGroupCount + "):" + this.itemWeight;
            }

            public EntityLiving newInstance(World world) throws Exception
            {
                net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.EntityRegistry.getEntry(this.field_76300_b);
                if (entry != null) return (EntityLiving) entry.newInstance(world);
                return this.field_76300_b.getConstructor(World.class).newInstance(world);
            }
        }

    public static enum TempCategory
    {
        OCEAN,
        COLD,
        MEDIUM,
        WARM;
    }
}