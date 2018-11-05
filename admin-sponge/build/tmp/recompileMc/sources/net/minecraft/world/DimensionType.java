package net.minecraft.world;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum DimensionType
{
    OVERWORLD(0, "overworld", "", WorldProviderSurface.class),
    NETHER(-1, "the_nether", "_nether", WorldProviderHell.class),
    THE_END(1, "the_end", "_end", WorldProviderEnd.class);

    private final int id;
    private final String name;
    private final String suffix;
    private final Class <? extends WorldProvider > field_186077_g;
    private boolean shouldLoadSpawn = false;

    private DimensionType(int p_i46672_3_, String p_i46672_4_, String p_i46672_5_, Class <? extends WorldProvider > p_i46672_6_)
    {
        this.id = p_i46672_3_;
        this.name = p_i46672_4_;
        this.suffix = p_i46672_5_;
        this.field_186077_g = p_i46672_6_;
        this.shouldLoadSpawn = p_i46672_3_ == 0;
    }

    public int getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public String getSuffix()
    {
        return this.suffix;
    }

    public WorldProvider create()
    {
        try
        {
            Constructor <? extends WorldProvider > constructor = this.field_186077_g.getConstructor();
            return constructor.newInstance();
        }
        catch (NoSuchMethodException nosuchmethodexception)
        {
            throw new Error("Could not create new dimension", nosuchmethodexception);
        }
        catch (InvocationTargetException invocationtargetexception)
        {
            throw new Error("Could not create new dimension", invocationtargetexception);
        }
        catch (InstantiationException instantiationexception)
        {
            throw new Error("Could not create new dimension", instantiationexception);
        }
        catch (IllegalAccessException illegalaccessexception)
        {
            throw new Error("Could not create new dimension", illegalaccessexception);
        }
    }

    public static DimensionType getById(int id)
    {
        for (DimensionType dimensiontype : values())
        {
            if (dimensiontype.getId() == id)
            {
                return dimensiontype;
            }
        }

        throw new IllegalArgumentException("Invalid dimension id " + id);
    }

    public boolean shouldLoadSpawn(){ return this.shouldLoadSpawn; }
    public DimensionType setLoadSpawn(boolean value) { this.shouldLoadSpawn = value; return this; }

    private static Class<?>[] ENUM_ARGS = {int.class, String.class, String.class, Class.class};
    static { net.minecraftforge.common.util.EnumHelper.testEnum(DimensionType.class, ENUM_ARGS); }
    public static DimensionType register(String name, String suffix, int id, Class<? extends WorldProvider> provider, boolean keepLoaded)
    {
        String enum_name = name.replace(" ", "_").toLowerCase();
        DimensionType ret = net.minecraftforge.common.util.EnumHelper.addEnum(DimensionType.class, enum_name, ENUM_ARGS,
                id, name, suffix, provider);
        return ret.setLoadSpawn(keepLoaded);
    }
    //TODO: Unregister? There is no way to really delete a enum value...

    public static DimensionType byName(String nameIn)
    {
        for (DimensionType dimensiontype : values())
        {
            if (dimensiontype.getName().equals(nameIn))
            {
                return dimensiontype;
            }
        }

        throw new IllegalArgumentException("Invalid dimension " + nameIn);
    }
}