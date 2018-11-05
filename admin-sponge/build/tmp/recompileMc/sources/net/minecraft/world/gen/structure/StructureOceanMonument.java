package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class StructureOceanMonument extends MapGenStructure
{
    private int field_175800_f;
    private int field_175801_g;
    public static final List<Biome> field_175802_d = Arrays.<Biome>asList(Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER);
    public static final List<Biome> field_186134_b = Arrays.<Biome>asList(Biomes.DEEP_OCEAN);
    private static final List<Biome.SpawnListEntry> MONUMENT_ENEMIES = Lists.<Biome.SpawnListEntry>newArrayList();

    public StructureOceanMonument()
    {
        this.field_175800_f = 32;
        this.field_175801_g = 5;
    }

    public StructureOceanMonument(Map<String, String> p_i45608_1_)
    {
        this();

        for (Entry<String, String> entry : p_i45608_1_.entrySet())
        {
            if (((String)entry.getKey()).equals("spacing"))
            {
                this.field_175800_f = MathHelper.getInt(entry.getValue(), this.field_175800_f, 1);
            }
            else if (((String)entry.getKey()).equals("separation"))
            {
                this.field_175801_g = MathHelper.getInt(entry.getValue(), this.field_175801_g, 1);
            }
        }
    }

    public String getStructureName()
    {
        return "Monument";
    }

    protected boolean func_75047_a(int p_75047_1_, int p_75047_2_)
    {
        int i = p_75047_1_;
        int j = p_75047_2_;

        if (p_75047_1_ < 0)
        {
            p_75047_1_ -= this.field_175800_f - 1;
        }

        if (p_75047_2_ < 0)
        {
            p_75047_2_ -= this.field_175800_f - 1;
        }

        int k = p_75047_1_ / this.field_175800_f;
        int l = p_75047_2_ / this.field_175800_f;
        Random random = this.field_75039_c.func_72843_D(k, l, 10387313);
        k = k * this.field_175800_f;
        l = l * this.field_175800_f;
        k = k + (random.nextInt(this.field_175800_f - this.field_175801_g) + random.nextInt(this.field_175800_f - this.field_175801_g)) / 2;
        l = l + (random.nextInt(this.field_175800_f - this.field_175801_g) + random.nextInt(this.field_175800_f - this.field_175801_g)) / 2;

        if (i == k && j == l)
        {
            if (!this.field_75039_c.func_72959_q().func_76940_a(i * 16 + 8, j * 16 + 8, 16, field_186134_b))
            {
                return false;
            }

            boolean flag = this.field_75039_c.func_72959_q().func_76940_a(i * 16 + 8, j * 16 + 8, 29, field_175802_d);

            if (flag)
            {
                return true;
            }
        }

        return false;
    }

    public BlockPos func_180706_b(World p_180706_1_, BlockPos p_180706_2_, boolean p_180706_3_)
    {
        this.field_75039_c = p_180706_1_;
        return func_191069_a(p_180706_1_, this, p_180706_2_, this.field_175800_f, this.field_175801_g, 10387313, true, 100, p_180706_3_);
    }

    protected StructureStart func_75049_b(int p_75049_1_, int p_75049_2_)
    {
        return new StructureOceanMonument.StartMonument(this.field_75039_c, this.field_75038_b, p_75049_1_, p_75049_2_);
    }

    public List<Biome.SpawnListEntry> func_175799_b()
    {
        return MONUMENT_ENEMIES;
    }

    static
    {
        MONUMENT_ENEMIES.add(new Biome.SpawnListEntry(EntityGuardian.class, 1, 2, 4));
    }

    public static class StartMonument extends StructureStart
        {
            private final Set<ChunkPos> processed = Sets.<ChunkPos>newHashSet();
            private boolean wasCreated;

            public StartMonument()
            {
            }

            public StartMonument(World p_i45607_1_, Random p_i45607_2_, int p_i45607_3_, int p_i45607_4_)
            {
                super(p_i45607_3_, p_i45607_4_);
                this.create(p_i45607_1_, p_i45607_2_, p_i45607_3_, p_i45607_4_);
            }

            private void create(World worldIn, Random random, int chunkX, int chunkZ)
            {
                random.setSeed(worldIn.getSeed());
                long i = random.nextLong();
                long j = random.nextLong();
                long k = (long)chunkX * i;
                long l = (long)chunkZ * j;
                random.setSeed(k ^ l ^ worldIn.getSeed());
                int i1 = chunkX * 16 + 8 - 29;
                int j1 = chunkZ * 16 + 8 - 29;
                EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(random);
                this.components.add(new StructureOceanMonumentPieces.MonumentBuilding(random, i1, j1, enumfacing));
                this.func_75072_c();
                this.wasCreated = true;
            }

            /**
             * Keeps iterating Structure Pieces and spawning them until the checks tell it to stop
             */
            public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb)
            {
                if (!this.wasCreated)
                {
                    this.components.clear();
                    this.create(worldIn, rand, this.getChunkPosX(), this.getChunkPosZ());
                }

                super.generateStructure(worldIn, rand, structurebb);
            }

            public boolean func_175788_a(ChunkPos p_175788_1_)
            {
                return this.processed.contains(p_175788_1_) ? false : super.func_175788_a(p_175788_1_);
            }

            public void notifyPostProcessAt(ChunkPos pair)
            {
                super.notifyPostProcessAt(pair);
                this.processed.add(pair);
            }

            public void writeAdditional(NBTTagCompound tagCompound)
            {
                super.writeAdditional(tagCompound);
                NBTTagList nbttaglist = new NBTTagList();

                for (ChunkPos chunkpos : this.processed)
                {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    nbttagcompound.putInt("X", chunkpos.x);
                    nbttagcompound.putInt("Z", chunkpos.z);
                    nbttaglist.func_74742_a(nbttagcompound);
                }

                tagCompound.put("Processed", nbttaglist);
            }

            public void readAdditional(NBTTagCompound tagCompound)
            {
                super.readAdditional(tagCompound);

                if (tagCompound.contains("Processed", 9))
                {
                    NBTTagList nbttaglist = tagCompound.getList("Processed", 10);

                    for (int i = 0; i < nbttaglist.func_74745_c(); ++i)
                    {
                        NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
                        this.processed.add(new ChunkPos(nbttagcompound.getInt("X"), nbttagcompound.getInt("Z")));
                    }
                }
            }
        }
}