package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public abstract class StructureStart
{
    /** List of all StructureComponents that are part of this structure */
    protected List<StructureComponent> components = Lists.<StructureComponent>newLinkedList();
    protected StructureBoundingBox boundingBox;
    private int chunkPosX;
    private int chunkPosZ;

    public StructureStart()
    {
    }

    public StructureStart(int p_i43002_1_, int p_i43002_2_)
    {
        this.chunkPosX = p_i43002_1_;
        this.chunkPosZ = p_i43002_2_;
    }

    public StructureBoundingBox getBoundingBox()
    {
        return this.boundingBox;
    }

    public List<StructureComponent> getComponents()
    {
        return this.components;
    }

    /**
     * Keeps iterating Structure Pieces and spawning them until the checks tell it to stop
     */
    public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb)
    {
        Iterator<StructureComponent> iterator = this.components.iterator();

        while (iterator.hasNext())
        {
            StructureComponent structurecomponent = iterator.next();

            if (structurecomponent.getBoundingBox().intersectsWith(structurebb) && !structurecomponent.addComponentParts(worldIn, rand, structurebb))
            {
                iterator.remove();
            }
        }
    }

    protected void func_75072_c()
    {
        this.boundingBox = StructureBoundingBox.getNewBoundingBox();

        for (StructureComponent structurecomponent : this.components)
        {
            this.boundingBox.expandTo(structurecomponent.getBoundingBox());
        }
    }

    public NBTTagCompound write(int chunkX, int chunkZ)
    {
        if (MapGenStructureIO.getStructureStartName(this) == null) // This is just a more friendly error instead of the 'Null String' below
        {
            throw new RuntimeException("StructureStart \"" + this.getClass().getName() + "\" missing ID Mapping, Modder see MapGenStructureIO");
        }
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.putString("id", MapGenStructureIO.getStructureStartName(this));
        nbttagcompound.putInt("ChunkX", chunkX);
        nbttagcompound.putInt("ChunkZ", chunkZ);
        nbttagcompound.put("BB", this.boundingBox.toNBTTagIntArray());
        NBTTagList nbttaglist = new NBTTagList();

        for (StructureComponent structurecomponent : this.components)
        {
            nbttaglist.func_74742_a(structurecomponent.write());
        }

        nbttagcompound.put("Children", nbttaglist);
        this.writeAdditional(nbttagcompound);
        return nbttagcompound;
    }

    public void writeAdditional(NBTTagCompound tagCompound)
    {
    }

    public void read(World worldIn, NBTTagCompound tagCompound)
    {
        this.chunkPosX = tagCompound.getInt("ChunkX");
        this.chunkPosZ = tagCompound.getInt("ChunkZ");

        if (tagCompound.contains("BB"))
        {
            this.boundingBox = new StructureBoundingBox(tagCompound.getIntArray("BB"));
        }

        NBTTagList nbttaglist = tagCompound.getList("Children", 10);

        for (int i = 0; i < nbttaglist.func_74745_c(); ++i)
        {
            StructureComponent tmp = MapGenStructureIO.getStructureComponent(nbttaglist.getCompound(i), worldIn);
            if (tmp != null) this.components.add(tmp); //Forge: Prevent NPEs further down the line when a componenet can't be loaded.
        }

        this.readAdditional(tagCompound);
    }

    public void readAdditional(NBTTagCompound tagCompound)
    {
    }

    /**
     * offsets the structure Bounding Boxes up to a certain height, typically 63 - 10
     */
    protected void markAvailableHeight(World worldIn, Random rand, int p_75067_3_)
    {
        int i = worldIn.getSeaLevel() - p_75067_3_;
        int j = this.boundingBox.getYSize() + 1;

        if (j < i)
        {
            j += rand.nextInt(i - j);
        }

        int k = j - this.boundingBox.maxY;
        this.boundingBox.offset(0, k, 0);

        for (StructureComponent structurecomponent : this.components)
        {
            structurecomponent.offset(0, k, 0);
        }
    }

    protected void setRandomHeight(World worldIn, Random rand, int p_75070_3_, int p_75070_4_)
    {
        int i = p_75070_4_ - p_75070_3_ + 1 - this.boundingBox.getYSize();
        int j;

        if (i > 1)
        {
            j = p_75070_3_ + rand.nextInt(i);
        }
        else
        {
            j = p_75070_3_;
        }

        int k = j - this.boundingBox.minY;
        this.boundingBox.offset(0, k, 0);

        for (StructureComponent structurecomponent : this.components)
        {
            structurecomponent.offset(0, k, 0);
        }
    }

    /**
     * currently only defined for Villages, returns true if Village has more than 2 non-road components
     */
    public boolean isSizeableStructure()
    {
        return true;
    }

    public boolean func_175788_a(ChunkPos p_175788_1_)
    {
        return true;
    }

    public void notifyPostProcessAt(ChunkPos pair)
    {
    }

    public int getChunkPosX()
    {
        return this.chunkPosX;
    }

    public int getChunkPosZ()
    {
        return this.chunkPosZ;
    }
}