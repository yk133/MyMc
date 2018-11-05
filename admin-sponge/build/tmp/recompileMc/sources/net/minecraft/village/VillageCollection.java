package net.minecraft.village;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.WorldSavedData;

public class VillageCollection extends WorldSavedData
{
    private World world;
    /**
     * This is a black hole. You can add data to this list through a public interface, but you can't query that
     * information in any way and it's not used internally either.
     */
    private final List<BlockPos> villagerPositionsList = Lists.<BlockPos>newArrayList();
    private final List<VillageDoorInfo> newDoors = Lists.<VillageDoorInfo>newArrayList();
    private final List<Village> villageList = Lists.<Village>newArrayList();
    private int tickCounter;

    public VillageCollection(String name)
    {
        super(name);
    }

    public VillageCollection(World worldIn)
    {
        super(fileNameForProvider(worldIn.dimension));
        this.world = worldIn;
        this.markDirty();
    }

    public void setWorldsForAll(World worldIn)
    {
        this.world = worldIn;

        for (Village village : this.villageList)
        {
            village.setWorld(worldIn);
        }
    }

    public void addToVillagerPositionList(BlockPos pos)
    {
        if (this.villagerPositionsList.size() <= 64)
        {
            if (!this.positionInList(pos))
            {
                this.villagerPositionsList.add(pos);
            }
        }
    }

    /**
     * Runs a single tick for the village collection
     */
    public void tick()
    {
        ++this.tickCounter;

        for (Village village : this.villageList)
        {
            village.tick(this.tickCounter);
        }

        this.removeAnnihilatedVillages();
        this.dropOldestVillagerPosition();
        this.addNewDoorsToVillageOrCreateVillage();

        if (this.tickCounter % 400 == 0)
        {
            this.markDirty();
        }
    }

    private void removeAnnihilatedVillages()
    {
        Iterator<Village> iterator = this.villageList.iterator();

        while (iterator.hasNext())
        {
            Village village = iterator.next();

            if (village.isAnnihilated())
            {
                iterator.remove();
                this.markDirty();
            }
        }
    }

    /**
     * Get a list of villages.
     */
    public List<Village> getVillageList()
    {
        return this.villageList;
    }

    public Village getNearestVillage(BlockPos doorBlock, int radius)
    {
        Village village = null;
        double d0 = 3.4028234663852886E38D;

        for (Village village1 : this.villageList)
        {
            double d1 = village1.getCenter().distanceSq(doorBlock);

            if (d1 < d0)
            {
                float f = (float)(radius + village1.getVillageRadius());

                if (d1 <= (double)(f * f))
                {
                    village = village1;
                    d0 = d1;
                }
            }
        }

        return village;
    }

    private void dropOldestVillagerPosition()
    {
        if (!this.villagerPositionsList.isEmpty())
        {
            this.addDoorsAround(this.villagerPositionsList.remove(0));
        }
    }

    private void addNewDoorsToVillageOrCreateVillage()
    {
        for (int i = 0; i < this.newDoors.size(); ++i)
        {
            VillageDoorInfo villagedoorinfo = this.newDoors.get(i);
            Village village = this.getNearestVillage(villagedoorinfo.getDoorBlockPos(), 32);

            if (village == null)
            {
                village = new Village(this.world);
                this.villageList.add(village);
                this.markDirty();
            }

            village.addVillageDoorInfo(villagedoorinfo);
        }

        this.newDoors.clear();
    }

    private void addDoorsAround(BlockPos central)
    {
        if (!this.world.func_175697_a(central, 16)) return; // Forge: prevent loading unloaded chunks when checking for doors
        int i = 16;
        int j = 4;
        int k = 16;

        for (int l = -16; l < 16; ++l)
        {
            for (int i1 = -4; i1 < 4; ++i1)
            {
                for (int j1 = -16; j1 < 16; ++j1)
                {
                    BlockPos blockpos = central.add(l, i1, j1);

                    if (this.func_176058_f(blockpos))
                    {
                        VillageDoorInfo villagedoorinfo = this.checkDoorExistence(blockpos);

                        if (villagedoorinfo == null)
                        {
                            this.func_176059_d(blockpos);
                        }
                        else
                        {
                            villagedoorinfo.setLastActivityTimestamp(this.tickCounter);
                        }
                    }
                }
            }
        }
    }

    /**
     * returns the VillageDoorInfo if it exists in any village or in the newDoor list, otherwise returns null
     */
    @Nullable
    private VillageDoorInfo checkDoorExistence(BlockPos doorBlock)
    {
        for (VillageDoorInfo villagedoorinfo : this.newDoors)
        {
            if (villagedoorinfo.getDoorBlockPos().getX() == doorBlock.getX() && villagedoorinfo.getDoorBlockPos().getZ() == doorBlock.getZ() && Math.abs(villagedoorinfo.getDoorBlockPos().getY() - doorBlock.getY()) <= 1)
            {
                return villagedoorinfo;
            }
        }

        for (Village village : this.villageList)
        {
            VillageDoorInfo villagedoorinfo1 = village.getExistedDoor(doorBlock);

            if (villagedoorinfo1 != null)
            {
                return villagedoorinfo1;
            }
        }

        return null;
    }

    private void func_176059_d(BlockPos p_176059_1_)
    {
        EnumFacing enumfacing = BlockDoor.func_176517_h(this.world, p_176059_1_);
        EnumFacing enumfacing1 = enumfacing.getOpposite();
        int i = this.countBlocksCanSeeSky(p_176059_1_, enumfacing, 5);
        int j = this.countBlocksCanSeeSky(p_176059_1_, enumfacing1, i + 1);

        if (i != j)
        {
            this.newDoors.add(new VillageDoorInfo(p_176059_1_, i < j ? enumfacing : enumfacing1, this.tickCounter));
        }
    }

    /**
     * Check five blocks in the direction. The centerPos will not be checked.
     */
    private int countBlocksCanSeeSky(BlockPos centerPos, EnumFacing direction, int limitation)
    {
        int i = 0;

        for (int j = 1; j <= 5; ++j)
        {
            if (this.world.canSeeSky(centerPos.offset(direction, j)))
            {
                ++i;

                if (i >= limitation)
                {
                    return i;
                }
            }
        }

        return i;
    }

    private boolean positionInList(BlockPos pos)
    {
        for (BlockPos blockpos : this.villagerPositionsList)
        {
            if (blockpos.equals(pos))
            {
                return true;
            }
        }

        return false;
    }

    private boolean func_176058_f(BlockPos p_176058_1_)
    {
        IBlockState iblockstate = this.world.getBlockState(p_176058_1_);
        Block block = iblockstate.getBlock();

        if (block instanceof BlockDoor)
        {
            return iblockstate.getMaterial() == Material.WOOD;
        }
        else
        {
            return false;
        }
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public void read(NBTTagCompound nbt)
    {
        this.tickCounter = nbt.getInt("Tick");
        NBTTagList nbttaglist = nbt.getList("Villages", 10);

        for (int i = 0; i < nbttaglist.func_74745_c(); ++i)
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            Village village = new Village();
            village.read(nbttagcompound);
            this.villageList.add(village);
        }
    }

    public NBTTagCompound write(NBTTagCompound compound)
    {
        compound.putInt("Tick", this.tickCounter);
        NBTTagList nbttaglist = new NBTTagList();

        for (Village village : this.villageList)
        {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            village.write(nbttagcompound);
            nbttaglist.func_74742_a(nbttagcompound);
        }

        compound.put("Villages", nbttaglist);
        return compound;
    }

    public static String fileNameForProvider(WorldProvider provider)
    {
        return "villages" + provider.getType().getSuffix();
    }
}