package net.minecraft.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.IAnimals;

public enum EnumCreatureType
{
    MONSTER(IMob.class, 70, Material.AIR, false, false),
    CREATURE(EntityAnimal.class, 10, Material.AIR, true, true),
    AMBIENT(EntityAmbientCreature.class, 15, Material.AIR, true, false),
    WATER_CREATURE(EntityWaterMob.class, 5, Material.WATER, true, false);

    private final Class <? extends IAnimals > baseClass;
    private final int maxNumberOfCreature;
    private final Material field_75603_f;
    /** A flag indicating whether this creature type is peaceful. */
    private final boolean isPeacefulCreature;
    /** Whether this creature type is an animal. */
    private final boolean isAnimal;

    private EnumCreatureType(Class <? extends IAnimals > p_i1596_3_, int p_i1596_4_, Material p_i1596_5_, boolean p_i1596_6_, boolean p_i1596_7_)
    {
        this.baseClass = p_i1596_3_;
        this.maxNumberOfCreature = p_i1596_4_;
        this.field_75603_f = p_i1596_5_;
        this.isPeacefulCreature = p_i1596_6_;
        this.isAnimal = p_i1596_7_;
    }

    public Class <? extends IAnimals > getBaseClass()
    {
        return this.baseClass;
    }

    public int getMaxNumberOfCreature()
    {
        return this.maxNumberOfCreature;
    }

    /**
     * Gets whether or not this creature type is peaceful.
     */
    public boolean getPeacefulCreature()
    {
        return this.isPeacefulCreature;
    }

    /**
     * Return whether this creature type is an animal.
     */
    public boolean getAnimal()
    {
        return this.isAnimal;
    }
}