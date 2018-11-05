package net.minecraft.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.ISaveHandler;

public class WorldServerMulti extends WorldServer
{
    private final WorldServer delegate;
    private IBorderListener borderListener;

    public WorldServerMulti(MinecraftServer server, ISaveHandler saveHandlerIn, int dimensionId, WorldServer delegate, Profiler profilerIn)
    {
        super(server, saveHandlerIn, new DerivedWorldInfo(delegate.getWorldInfo()), dimensionId, profilerIn);
        this.delegate = delegate;
        this.borderListener = new IBorderListener()
        {
            public void onSizeChanged(WorldBorder border, double newSize)
            {
                WorldServerMulti.this.getWorldBorder().setTransition(newSize);
            }
            public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time)
            {
                WorldServerMulti.this.getWorldBorder().setTransition(oldSize, newSize, time);
            }
            public void onCenterChanged(WorldBorder border, double x, double z)
            {
                WorldServerMulti.this.getWorldBorder().setCenter(x, z);
            }
            public void onWarningTimeChanged(WorldBorder border, int newTime)
            {
                WorldServerMulti.this.getWorldBorder().setWarningTime(newTime);
            }
            public void onWarningDistanceChanged(WorldBorder border, int newDistance)
            {
                WorldServerMulti.this.getWorldBorder().setWarningDistance(newDistance);
            }
            public void onDamageAmountChanged(WorldBorder border, double newAmount)
            {
                WorldServerMulti.this.getWorldBorder().setDamageAmount(newAmount);
            }
            public void onDamageBufferChanged(WorldBorder border, double newSize)
            {
                WorldServerMulti.this.getWorldBorder().setDamageBuffer(newSize);
            }
        };
        this.delegate.getWorldBorder().addListener(this.borderListener);
    }

    /**
     * Saves the chunks to disk.
     */
    protected void saveLevel() throws MinecraftException
    {
        this.perWorldStorage.saveAllData();
    }

    public World init()
    {
        this.mapStorage = this.delegate.getMapStorage();
        this.field_96442_D = this.delegate.getScoreboard();
        this.field_184151_B = this.delegate.func_184146_ak();
        this.field_191951_C = this.delegate.func_191952_z();
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

        this.initCapabilities();
        return this;
    }


    @Override
    public void func_73041_k()
    {
        super.func_73041_k();
        this.delegate.getWorldBorder().removeListener(this.borderListener); // Unlink ourselves, to prevent world leak.
    }

    /**
     * Called during saving of a world to give children worlds a chance to save additional data. Only used to save
     * WorldProviderEnd's data in Vanilla.
     */
    public void saveAdditionalData()
    {
        this.dimension.onWorldSave();
    }
}