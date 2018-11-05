package net.minecraft.init;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.util.ResourceLocation;

public class Blocks
{
    /**
     * During the registration process, contains all blocks that have been registered so far (along with null) to avoid
     * duplicates (and including null).
     */
    private static final Set<Block> CACHE;
    public static final Block AIR;
    public static final Block STONE;
    public static final BlockGrass GRASS;
    public static final Block DIRT;
    public static final Block COBBLESTONE;
    public static final Block field_150344_f;
    public static final Block field_150345_g;
    public static final Block BEDROCK;
    public static final BlockDynamicLiquid field_150358_i;
    public static final BlockStaticLiquid WATER;
    public static final BlockDynamicLiquid field_150356_k;
    public static final BlockStaticLiquid LAVA;
    public static final BlockSand SAND;
    public static final Block GRAVEL;
    public static final Block GOLD_ORE;
    public static final Block IRON_ORE;
    public static final Block COAL_ORE;
    public static final Block field_150364_r;
    public static final Block field_150363_s;
    public static final BlockLeaves field_150362_t;
    public static final BlockLeaves field_150361_u;
    public static final Block SPONGE;
    public static final Block GLASS;
    public static final Block LAPIS_ORE;
    public static final Block LAPIS_BLOCK;
    public static final Block DISPENSER;
    public static final Block SANDSTONE;
    public static final Block field_150323_B;
    public static final Block field_150324_C;
    public static final Block field_150318_D;
    public static final Block DETECTOR_RAIL;
    public static final BlockPistonBase STICKY_PISTON;
    public static final Block field_150321_G;
    public static final BlockTallGrass field_150329_H;
    public static final BlockDeadBush field_150330_I;
    public static final BlockPistonBase PISTON;
    public static final BlockPistonExtension PISTON_HEAD;
    public static final Block field_150325_L;
    public static final BlockPistonMoving field_180384_M;
    public static final BlockFlower field_150327_N;
    public static final BlockFlower field_150328_O;
    public static final BlockBush BROWN_MUSHROOM;
    public static final BlockBush RED_MUSHROOM;
    public static final Block GOLD_BLOCK;
    public static final Block IRON_BLOCK;
    public static final BlockSlab field_150334_T;
    public static final BlockSlab STONE_SLAB;
    public static final Block field_150336_V;
    public static final Block TNT;
    public static final Block BOOKSHELF;
    public static final Block MOSSY_COBBLESTONE;
    public static final Block OBSIDIAN;
    public static final Block TORCH;
    public static final BlockFire FIRE;
    public static final Block SPAWNER;
    public static final Block OAK_STAIRS;
    public static final BlockChest CHEST;
    public static final BlockRedstoneWire REDSTONE_WIRE;
    public static final Block DIAMOND_ORE;
    public static final Block DIAMOND_BLOCK;
    public static final Block CRAFTING_TABLE;
    public static final Block WHEAT;
    public static final Block FARMLAND;
    public static final Block FURNACE;
    public static final Block field_150470_am;
    public static final Block field_150472_an;
    public static final BlockDoor OAK_DOOR;
    public static final BlockDoor SPRUCE_DOOR;
    public static final BlockDoor BIRCH_DOOR;
    public static final BlockDoor JUNGLE_DOOR;
    public static final BlockDoor ACACIA_DOOR;
    public static final BlockDoor DARK_OAK_DOOR;
    public static final Block LADDER;
    public static final Block RAIL;
    public static final Block field_150446_ar;
    public static final Block WALL_SIGN;
    public static final Block LEVER;
    public static final Block STONE_PRESSURE_PLATE;
    public static final BlockDoor IRON_DOOR;
    public static final Block field_150452_aw;
    public static final Block REDSTONE_ORE;
    public static final Block field_150439_ay;
    public static final Block field_150437_az;
    public static final Block REDSTONE_TORCH;
    public static final Block STONE_BUTTON;
    public static final Block field_150431_aC;
    public static final Block ICE;
    public static final Block SNOW;
    public static final BlockCactus CACTUS;
    public static final Block CLAY;
    public static final BlockReed field_150436_aH;
    public static final Block JUKEBOX;
    public static final Block OAK_FENCE;
    public static final Block SPRUCE_FENCE;
    public static final Block BIRCH_FENCE;
    public static final Block JUNGLE_FENCE;
    public static final Block DARK_OAK_FENCE;
    public static final Block ACACIA_FENCE;
    public static final Block PUMPKIN;
    public static final Block NETHERRACK;
    public static final Block SOUL_SAND;
    public static final Block GLOWSTONE;
    public static final BlockPortal NETHER_PORTAL;
    public static final Block field_150428_aP;
    public static final Block CAKE;
    public static final BlockRedstoneRepeater field_150413_aR;
    public static final BlockRedstoneRepeater field_150416_aS;
    public static final Block field_150415_aT;
    public static final Block field_150418_aU;
    public static final Block field_150417_aV;
    public static final Block BROWN_MUSHROOM_BLOCK;
    public static final Block RED_MUSHROOM_BLOCK;
    public static final Block IRON_BARS;
    public static final Block GLASS_PANE;
    public static final Block MELON;
    public static final Block PUMPKIN_STEM;
    public static final Block MELON_STEM;
    public static final Block VINE;
    public static final Block OAK_FENCE_GATE;
    public static final Block SPRUCE_FENCE_GATE;
    public static final Block BIRCH_FENCE_GATE;
    public static final Block JUNGLE_FENCE_GATE;
    public static final Block DARK_OAK_FENCE_GATE;
    public static final Block ACACIA_FENCE_GATE;
    public static final Block BRICK_STAIRS;
    public static final Block STONE_BRICK_STAIRS;
    public static final BlockMycelium MYCELIUM;
    public static final Block field_150392_bi;
    public static final Block field_150385_bj;
    public static final Block NETHER_BRICK_FENCE;
    public static final Block NETHER_BRICK_STAIRS;
    public static final Block NETHER_WART;
    public static final Block ENCHANTING_TABLE;
    public static final Block BREWING_STAND;
    public static final BlockCauldron CAULDRON;
    public static final Block END_PORTAL;
    public static final Block END_PORTAL_FRAME;
    public static final Block END_STONE;
    public static final Block DRAGON_EGG;
    public static final Block REDSTONE_LAMP;
    public static final Block field_150374_bv;
    public static final BlockSlab field_150373_bw;
    public static final BlockSlab field_150376_bx;
    public static final Block COCOA;
    public static final Block SANDSTONE_STAIRS;
    public static final Block EMERALD_ORE;
    public static final Block ENDER_CHEST;
    public static final BlockTripWireHook TRIPWIRE_HOOK;
    public static final Block TRIPWIRE;
    public static final Block EMERALD_BLOCK;
    public static final Block SPRUCE_STAIRS;
    public static final Block BIRCH_STAIRS;
    public static final Block JUNGLE_STAIRS;
    public static final Block COMMAND_BLOCK;
    public static final BlockBeacon BEACON;
    public static final Block COBBLESTONE_WALL;
    public static final Block FLOWER_POT;
    public static final Block CARROTS;
    public static final Block POTATOES;
    public static final Block field_150471_bO;
    public static final BlockSkull field_150465_bP;
    public static final Block ANVIL;
    public static final Block TRAPPED_CHEST;
    public static final Block LIGHT_WEIGHTED_PRESSURE_PLATE;
    public static final Block HEAVY_WEIGHTED_PRESSURE_PLATE;
    public static final BlockRedstoneComparator field_150441_bU;
    public static final BlockRedstoneComparator field_150455_bV;
    public static final BlockDaylightDetector DAYLIGHT_DETECTOR;
    public static final BlockDaylightDetector field_180402_cm;
    public static final Block REDSTONE_BLOCK;
    public static final Block field_150449_bY;
    public static final BlockHopper HOPPER;
    public static final Block QUARTZ_BLOCK;
    public static final Block QUARTZ_STAIRS;
    public static final Block ACTIVATOR_RAIL;
    public static final Block DROPPER;
    public static final Block field_150406_ce;
    public static final Block BARRIER;
    public static final Block IRON_TRAPDOOR;
    public static final Block HAY_BLOCK;
    public static final Block field_150404_cg;
    public static final Block TERRACOTTA;
    public static final Block COAL_BLOCK;
    public static final Block PACKED_ICE;
    public static final Block ACACIA_STAIRS;
    public static final Block DARK_OAK_STAIRS;
    public static final Block SLIME_BLOCK;
    public static final BlockDoublePlant field_150398_cm;
    public static final BlockStainedGlass field_150399_cn;
    public static final BlockStainedGlassPane field_150397_co;
    public static final Block PRISMARINE;
    public static final Block SEA_LANTERN;
    public static final Block field_180393_cK;
    public static final Block field_180394_cL;
    public static final Block RED_SANDSTONE;
    public static final Block RED_SANDSTONE_STAIRS;
    public static final BlockSlab field_180388_cO;
    public static final BlockSlab field_180389_cP;
    public static final Block END_ROD;
    public static final Block CHORUS_PLANT;
    public static final Block CHORUS_FLOWER;
    public static final Block PURPUR_BLOCK;
    public static final Block PURPUR_PILLAR;
    public static final Block PURPUR_STAIRS;
    public static final BlockSlab field_185770_cW;
    public static final BlockSlab PURPUR_SLAB;
    public static final Block field_185772_cY;
    public static final Block BEETROOTS;
    public static final Block GRASS_PATH;
    public static final Block END_GATEWAY;
    public static final Block REPEATING_COMMAND_BLOCK;
    public static final Block CHAIN_COMMAND_BLOCK;
    public static final Block FROSTED_ICE;
    public static final Block field_189877_df;
    public static final Block NETHER_WART_BLOCK;
    public static final Block field_189879_dh;
    public static final Block BONE_BLOCK;
    public static final Block STRUCTURE_VOID;
    public static final Block OBSERVER;
    public static final Block WHITE_SHULKER_BOX;
    public static final Block ORANGE_SHULKER_BOX;
    public static final Block MAGENTA_SHULKER_BOX;
    public static final Block LIGHT_BLUE_SHULKER_BOX;
    public static final Block YELLOW_SHULKER_BOX;
    public static final Block LIME_SHULKER_BOX;
    public static final Block PINK_SHULKER_BOX;
    public static final Block GRAY_SHULKER_BOX;
    public static final Block field_190985_dt;
    public static final Block CYAN_SHULKER_BOX;
    public static final Block PURPLE_SHULKER_BOX;
    public static final Block BLUE_SHULKER_BOX;
    public static final Block BROWN_SHULKER_BOX;
    public static final Block GREEN_SHULKER_BOX;
    public static final Block RED_SHULKER_BOX;
    public static final Block BLACK_SHULKER_BOX;
    public static final Block WHITE_GLAZED_TERRACOTTA;
    public static final Block ORANGE_GLAZED_TERRACOTTA;
    public static final Block MAGENTA_GLAZED_TERRACOTTA;
    public static final Block LIGHT_BLUE_GLAZED_TERRACOTTA;
    public static final Block YELLOW_GLAZED_TERRACOTTA;
    public static final Block LIME_GLAZED_TERRACOTTA;
    public static final Block PINK_GLAZED_TERRACOTTA;
    public static final Block GRAY_GLAZED_TERRACOTTA;
    public static final Block field_192435_dJ;
    public static final Block CYAN_GLAZED_TERRACOTTA;
    public static final Block PURPLE_GLAZED_TERRACOTTA;
    public static final Block BLUE_GLAZED_TERRACOTTA;
    public static final Block BROWN_GLAZED_TERRACOTTA;
    public static final Block GREEN_GLAZED_TERRACOTTA;
    public static final Block RED_GLAZED_TERRACOTTA;
    public static final Block BLACK_GLAZED_TERRACOTTA;
    public static final Block field_192443_dR;
    public static final Block field_192444_dS;
    public static final Block STRUCTURE_BLOCK;

    /**
     * Returns the Block in the blockRegistry with the specified name.
     */
    @Nullable
    private static Block getRegisteredBlock(String blockName)
    {
        Block block = Block.REGISTRY.get(new ResourceLocation(blockName));

        if (!CACHE.add(block))
        {
            throw new IllegalStateException("Invalid Block requested: " + blockName);
        }
        else
        {
            return block;
        }
    }

    static
    {
        if (!Bootstrap.isRegistered())
        {
            throw new RuntimeException("Accessed Blocks before Bootstrap!");
        }
        else
        {
            CACHE = Sets.<Block>newHashSet();
            AIR = getRegisteredBlock("air");
            STONE = getRegisteredBlock("stone");
            GRASS = (BlockGrass)getRegisteredBlock("grass");
            DIRT = getRegisteredBlock("dirt");
            COBBLESTONE = getRegisteredBlock("cobblestone");
            field_150344_f = getRegisteredBlock("planks");
            field_150345_g = getRegisteredBlock("sapling");
            BEDROCK = getRegisteredBlock("bedrock");
            field_150358_i = (BlockDynamicLiquid)getRegisteredBlock("flowing_water");
            WATER = (BlockStaticLiquid)getRegisteredBlock("water");
            field_150356_k = (BlockDynamicLiquid)getRegisteredBlock("flowing_lava");
            LAVA = (BlockStaticLiquid)getRegisteredBlock("lava");
            SAND = (BlockSand)getRegisteredBlock("sand");
            GRAVEL = getRegisteredBlock("gravel");
            GOLD_ORE = getRegisteredBlock("gold_ore");
            IRON_ORE = getRegisteredBlock("iron_ore");
            COAL_ORE = getRegisteredBlock("coal_ore");
            field_150364_r = getRegisteredBlock("log");
            field_150363_s = getRegisteredBlock("log2");
            field_150362_t = (BlockLeaves)getRegisteredBlock("leaves");
            field_150361_u = (BlockLeaves)getRegisteredBlock("leaves2");
            SPONGE = getRegisteredBlock("sponge");
            GLASS = getRegisteredBlock("glass");
            LAPIS_ORE = getRegisteredBlock("lapis_ore");
            LAPIS_BLOCK = getRegisteredBlock("lapis_block");
            DISPENSER = getRegisteredBlock("dispenser");
            SANDSTONE = getRegisteredBlock("sandstone");
            field_150323_B = getRegisteredBlock("noteblock");
            field_150324_C = getRegisteredBlock("bed");
            field_150318_D = getRegisteredBlock("golden_rail");
            DETECTOR_RAIL = getRegisteredBlock("detector_rail");
            STICKY_PISTON = (BlockPistonBase)getRegisteredBlock("sticky_piston");
            field_150321_G = getRegisteredBlock("web");
            field_150329_H = (BlockTallGrass)getRegisteredBlock("tallgrass");
            field_150330_I = (BlockDeadBush)getRegisteredBlock("deadbush");
            PISTON = (BlockPistonBase)getRegisteredBlock("piston");
            PISTON_HEAD = (BlockPistonExtension)getRegisteredBlock("piston_head");
            field_150325_L = getRegisteredBlock("wool");
            field_180384_M = (BlockPistonMoving)getRegisteredBlock("piston_extension");
            field_150327_N = (BlockFlower)getRegisteredBlock("yellow_flower");
            field_150328_O = (BlockFlower)getRegisteredBlock("red_flower");
            BROWN_MUSHROOM = (BlockBush)getRegisteredBlock("brown_mushroom");
            RED_MUSHROOM = (BlockBush)getRegisteredBlock("red_mushroom");
            GOLD_BLOCK = getRegisteredBlock("gold_block");
            IRON_BLOCK = getRegisteredBlock("iron_block");
            field_150334_T = (BlockSlab)getRegisteredBlock("double_stone_slab");
            STONE_SLAB = (BlockSlab)getRegisteredBlock("stone_slab");
            field_150336_V = getRegisteredBlock("brick_block");
            TNT = getRegisteredBlock("tnt");
            BOOKSHELF = getRegisteredBlock("bookshelf");
            MOSSY_COBBLESTONE = getRegisteredBlock("mossy_cobblestone");
            OBSIDIAN = getRegisteredBlock("obsidian");
            TORCH = getRegisteredBlock("torch");
            FIRE = (BlockFire)getRegisteredBlock("fire");
            SPAWNER = getRegisteredBlock("mob_spawner");
            OAK_STAIRS = getRegisteredBlock("oak_stairs");
            CHEST = (BlockChest)getRegisteredBlock("chest");
            REDSTONE_WIRE = (BlockRedstoneWire)getRegisteredBlock("redstone_wire");
            DIAMOND_ORE = getRegisteredBlock("diamond_ore");
            DIAMOND_BLOCK = getRegisteredBlock("diamond_block");
            CRAFTING_TABLE = getRegisteredBlock("crafting_table");
            WHEAT = getRegisteredBlock("wheat");
            FARMLAND = getRegisteredBlock("farmland");
            FURNACE = getRegisteredBlock("furnace");
            field_150470_am = getRegisteredBlock("lit_furnace");
            field_150472_an = getRegisteredBlock("standing_sign");
            OAK_DOOR = (BlockDoor)getRegisteredBlock("wooden_door");
            SPRUCE_DOOR = (BlockDoor)getRegisteredBlock("spruce_door");
            BIRCH_DOOR = (BlockDoor)getRegisteredBlock("birch_door");
            JUNGLE_DOOR = (BlockDoor)getRegisteredBlock("jungle_door");
            ACACIA_DOOR = (BlockDoor)getRegisteredBlock("acacia_door");
            DARK_OAK_DOOR = (BlockDoor)getRegisteredBlock("dark_oak_door");
            LADDER = getRegisteredBlock("ladder");
            RAIL = getRegisteredBlock("rail");
            field_150446_ar = getRegisteredBlock("stone_stairs");
            WALL_SIGN = getRegisteredBlock("wall_sign");
            LEVER = getRegisteredBlock("lever");
            STONE_PRESSURE_PLATE = getRegisteredBlock("stone_pressure_plate");
            IRON_DOOR = (BlockDoor)getRegisteredBlock("iron_door");
            field_150452_aw = getRegisteredBlock("wooden_pressure_plate");
            REDSTONE_ORE = getRegisteredBlock("redstone_ore");
            field_150439_ay = getRegisteredBlock("lit_redstone_ore");
            field_150437_az = getRegisteredBlock("unlit_redstone_torch");
            REDSTONE_TORCH = getRegisteredBlock("redstone_torch");
            STONE_BUTTON = getRegisteredBlock("stone_button");
            field_150431_aC = getRegisteredBlock("snow_layer");
            ICE = getRegisteredBlock("ice");
            SNOW = getRegisteredBlock("snow");
            CACTUS = (BlockCactus)getRegisteredBlock("cactus");
            CLAY = getRegisteredBlock("clay");
            field_150436_aH = (BlockReed)getRegisteredBlock("reeds");
            JUKEBOX = getRegisteredBlock("jukebox");
            OAK_FENCE = getRegisteredBlock("fence");
            SPRUCE_FENCE = getRegisteredBlock("spruce_fence");
            BIRCH_FENCE = getRegisteredBlock("birch_fence");
            JUNGLE_FENCE = getRegisteredBlock("jungle_fence");
            DARK_OAK_FENCE = getRegisteredBlock("dark_oak_fence");
            ACACIA_FENCE = getRegisteredBlock("acacia_fence");
            PUMPKIN = getRegisteredBlock("pumpkin");
            NETHERRACK = getRegisteredBlock("netherrack");
            SOUL_SAND = getRegisteredBlock("soul_sand");
            GLOWSTONE = getRegisteredBlock("glowstone");
            NETHER_PORTAL = (BlockPortal)getRegisteredBlock("portal");
            field_150428_aP = getRegisteredBlock("lit_pumpkin");
            CAKE = getRegisteredBlock("cake");
            field_150413_aR = (BlockRedstoneRepeater)getRegisteredBlock("unpowered_repeater");
            field_150416_aS = (BlockRedstoneRepeater)getRegisteredBlock("powered_repeater");
            field_150415_aT = getRegisteredBlock("trapdoor");
            field_150418_aU = getRegisteredBlock("monster_egg");
            field_150417_aV = getRegisteredBlock("stonebrick");
            BROWN_MUSHROOM_BLOCK = getRegisteredBlock("brown_mushroom_block");
            RED_MUSHROOM_BLOCK = getRegisteredBlock("red_mushroom_block");
            IRON_BARS = getRegisteredBlock("iron_bars");
            GLASS_PANE = getRegisteredBlock("glass_pane");
            MELON = getRegisteredBlock("melon_block");
            PUMPKIN_STEM = getRegisteredBlock("pumpkin_stem");
            MELON_STEM = getRegisteredBlock("melon_stem");
            VINE = getRegisteredBlock("vine");
            OAK_FENCE_GATE = getRegisteredBlock("fence_gate");
            SPRUCE_FENCE_GATE = getRegisteredBlock("spruce_fence_gate");
            BIRCH_FENCE_GATE = getRegisteredBlock("birch_fence_gate");
            JUNGLE_FENCE_GATE = getRegisteredBlock("jungle_fence_gate");
            DARK_OAK_FENCE_GATE = getRegisteredBlock("dark_oak_fence_gate");
            ACACIA_FENCE_GATE = getRegisteredBlock("acacia_fence_gate");
            BRICK_STAIRS = getRegisteredBlock("brick_stairs");
            STONE_BRICK_STAIRS = getRegisteredBlock("stone_brick_stairs");
            MYCELIUM = (BlockMycelium)getRegisteredBlock("mycelium");
            field_150392_bi = getRegisteredBlock("waterlily");
            field_150385_bj = getRegisteredBlock("nether_brick");
            NETHER_BRICK_FENCE = getRegisteredBlock("nether_brick_fence");
            NETHER_BRICK_STAIRS = getRegisteredBlock("nether_brick_stairs");
            NETHER_WART = getRegisteredBlock("nether_wart");
            ENCHANTING_TABLE = getRegisteredBlock("enchanting_table");
            BREWING_STAND = getRegisteredBlock("brewing_stand");
            CAULDRON = (BlockCauldron)getRegisteredBlock("cauldron");
            END_PORTAL = getRegisteredBlock("end_portal");
            END_PORTAL_FRAME = getRegisteredBlock("end_portal_frame");
            END_STONE = getRegisteredBlock("end_stone");
            DRAGON_EGG = getRegisteredBlock("dragon_egg");
            REDSTONE_LAMP = getRegisteredBlock("redstone_lamp");
            field_150374_bv = getRegisteredBlock("lit_redstone_lamp");
            field_150373_bw = (BlockSlab)getRegisteredBlock("double_wooden_slab");
            field_150376_bx = (BlockSlab)getRegisteredBlock("wooden_slab");
            COCOA = getRegisteredBlock("cocoa");
            SANDSTONE_STAIRS = getRegisteredBlock("sandstone_stairs");
            EMERALD_ORE = getRegisteredBlock("emerald_ore");
            ENDER_CHEST = getRegisteredBlock("ender_chest");
            TRIPWIRE_HOOK = (BlockTripWireHook)getRegisteredBlock("tripwire_hook");
            TRIPWIRE = getRegisteredBlock("tripwire");
            EMERALD_BLOCK = getRegisteredBlock("emerald_block");
            SPRUCE_STAIRS = getRegisteredBlock("spruce_stairs");
            BIRCH_STAIRS = getRegisteredBlock("birch_stairs");
            JUNGLE_STAIRS = getRegisteredBlock("jungle_stairs");
            COMMAND_BLOCK = getRegisteredBlock("command_block");
            BEACON = (BlockBeacon)getRegisteredBlock("beacon");
            COBBLESTONE_WALL = getRegisteredBlock("cobblestone_wall");
            FLOWER_POT = getRegisteredBlock("flower_pot");
            CARROTS = getRegisteredBlock("carrots");
            POTATOES = getRegisteredBlock("potatoes");
            field_150471_bO = getRegisteredBlock("wooden_button");
            field_150465_bP = (BlockSkull)getRegisteredBlock("skull");
            ANVIL = getRegisteredBlock("anvil");
            TRAPPED_CHEST = getRegisteredBlock("trapped_chest");
            LIGHT_WEIGHTED_PRESSURE_PLATE = getRegisteredBlock("light_weighted_pressure_plate");
            HEAVY_WEIGHTED_PRESSURE_PLATE = getRegisteredBlock("heavy_weighted_pressure_plate");
            field_150441_bU = (BlockRedstoneComparator)getRegisteredBlock("unpowered_comparator");
            field_150455_bV = (BlockRedstoneComparator)getRegisteredBlock("powered_comparator");
            DAYLIGHT_DETECTOR = (BlockDaylightDetector)getRegisteredBlock("daylight_detector");
            field_180402_cm = (BlockDaylightDetector)getRegisteredBlock("daylight_detector_inverted");
            REDSTONE_BLOCK = getRegisteredBlock("redstone_block");
            field_150449_bY = getRegisteredBlock("quartz_ore");
            HOPPER = (BlockHopper)getRegisteredBlock("hopper");
            QUARTZ_BLOCK = getRegisteredBlock("quartz_block");
            QUARTZ_STAIRS = getRegisteredBlock("quartz_stairs");
            ACTIVATOR_RAIL = getRegisteredBlock("activator_rail");
            DROPPER = getRegisteredBlock("dropper");
            field_150406_ce = getRegisteredBlock("stained_hardened_clay");
            BARRIER = getRegisteredBlock("barrier");
            IRON_TRAPDOOR = getRegisteredBlock("iron_trapdoor");
            HAY_BLOCK = getRegisteredBlock("hay_block");
            field_150404_cg = getRegisteredBlock("carpet");
            TERRACOTTA = getRegisteredBlock("hardened_clay");
            COAL_BLOCK = getRegisteredBlock("coal_block");
            PACKED_ICE = getRegisteredBlock("packed_ice");
            ACACIA_STAIRS = getRegisteredBlock("acacia_stairs");
            DARK_OAK_STAIRS = getRegisteredBlock("dark_oak_stairs");
            SLIME_BLOCK = getRegisteredBlock("slime");
            field_150398_cm = (BlockDoublePlant)getRegisteredBlock("double_plant");
            field_150399_cn = (BlockStainedGlass)getRegisteredBlock("stained_glass");
            field_150397_co = (BlockStainedGlassPane)getRegisteredBlock("stained_glass_pane");
            PRISMARINE = getRegisteredBlock("prismarine");
            SEA_LANTERN = getRegisteredBlock("sea_lantern");
            field_180393_cK = getRegisteredBlock("standing_banner");
            field_180394_cL = getRegisteredBlock("wall_banner");
            RED_SANDSTONE = getRegisteredBlock("red_sandstone");
            RED_SANDSTONE_STAIRS = getRegisteredBlock("red_sandstone_stairs");
            field_180388_cO = (BlockSlab)getRegisteredBlock("double_stone_slab2");
            field_180389_cP = (BlockSlab)getRegisteredBlock("stone_slab2");
            END_ROD = getRegisteredBlock("end_rod");
            CHORUS_PLANT = getRegisteredBlock("chorus_plant");
            CHORUS_FLOWER = getRegisteredBlock("chorus_flower");
            PURPUR_BLOCK = getRegisteredBlock("purpur_block");
            PURPUR_PILLAR = getRegisteredBlock("purpur_pillar");
            PURPUR_STAIRS = getRegisteredBlock("purpur_stairs");
            field_185770_cW = (BlockSlab)getRegisteredBlock("purpur_double_slab");
            PURPUR_SLAB = (BlockSlab)getRegisteredBlock("purpur_slab");
            field_185772_cY = getRegisteredBlock("end_bricks");
            BEETROOTS = getRegisteredBlock("beetroots");
            GRASS_PATH = getRegisteredBlock("grass_path");
            END_GATEWAY = getRegisteredBlock("end_gateway");
            REPEATING_COMMAND_BLOCK = getRegisteredBlock("repeating_command_block");
            CHAIN_COMMAND_BLOCK = getRegisteredBlock("chain_command_block");
            FROSTED_ICE = getRegisteredBlock("frosted_ice");
            field_189877_df = getRegisteredBlock("magma");
            NETHER_WART_BLOCK = getRegisteredBlock("nether_wart_block");
            field_189879_dh = getRegisteredBlock("red_nether_brick");
            BONE_BLOCK = getRegisteredBlock("bone_block");
            STRUCTURE_VOID = getRegisteredBlock("structure_void");
            OBSERVER = getRegisteredBlock("observer");
            WHITE_SHULKER_BOX = getRegisteredBlock("white_shulker_box");
            ORANGE_SHULKER_BOX = getRegisteredBlock("orange_shulker_box");
            MAGENTA_SHULKER_BOX = getRegisteredBlock("magenta_shulker_box");
            LIGHT_BLUE_SHULKER_BOX = getRegisteredBlock("light_blue_shulker_box");
            YELLOW_SHULKER_BOX = getRegisteredBlock("yellow_shulker_box");
            LIME_SHULKER_BOX = getRegisteredBlock("lime_shulker_box");
            PINK_SHULKER_BOX = getRegisteredBlock("pink_shulker_box");
            GRAY_SHULKER_BOX = getRegisteredBlock("gray_shulker_box");
            field_190985_dt = getRegisteredBlock("silver_shulker_box");
            CYAN_SHULKER_BOX = getRegisteredBlock("cyan_shulker_box");
            PURPLE_SHULKER_BOX = getRegisteredBlock("purple_shulker_box");
            BLUE_SHULKER_BOX = getRegisteredBlock("blue_shulker_box");
            BROWN_SHULKER_BOX = getRegisteredBlock("brown_shulker_box");
            GREEN_SHULKER_BOX = getRegisteredBlock("green_shulker_box");
            RED_SHULKER_BOX = getRegisteredBlock("red_shulker_box");
            BLACK_SHULKER_BOX = getRegisteredBlock("black_shulker_box");
            WHITE_GLAZED_TERRACOTTA = getRegisteredBlock("white_glazed_terracotta");
            ORANGE_GLAZED_TERRACOTTA = getRegisteredBlock("orange_glazed_terracotta");
            MAGENTA_GLAZED_TERRACOTTA = getRegisteredBlock("magenta_glazed_terracotta");
            LIGHT_BLUE_GLAZED_TERRACOTTA = getRegisteredBlock("light_blue_glazed_terracotta");
            YELLOW_GLAZED_TERRACOTTA = getRegisteredBlock("yellow_glazed_terracotta");
            LIME_GLAZED_TERRACOTTA = getRegisteredBlock("lime_glazed_terracotta");
            PINK_GLAZED_TERRACOTTA = getRegisteredBlock("pink_glazed_terracotta");
            GRAY_GLAZED_TERRACOTTA = getRegisteredBlock("gray_glazed_terracotta");
            field_192435_dJ = getRegisteredBlock("silver_glazed_terracotta");
            CYAN_GLAZED_TERRACOTTA = getRegisteredBlock("cyan_glazed_terracotta");
            PURPLE_GLAZED_TERRACOTTA = getRegisteredBlock("purple_glazed_terracotta");
            BLUE_GLAZED_TERRACOTTA = getRegisteredBlock("blue_glazed_terracotta");
            BROWN_GLAZED_TERRACOTTA = getRegisteredBlock("brown_glazed_terracotta");
            GREEN_GLAZED_TERRACOTTA = getRegisteredBlock("green_glazed_terracotta");
            RED_GLAZED_TERRACOTTA = getRegisteredBlock("red_glazed_terracotta");
            BLACK_GLAZED_TERRACOTTA = getRegisteredBlock("black_glazed_terracotta");
            field_192443_dR = getRegisteredBlock("concrete");
            field_192444_dS = getRegisteredBlock("concrete_powder");
            STRUCTURE_BLOCK = getRegisteredBlock("structure_block");
            CACHE.clear();
        }
    }
}