package net.minecraft.stats;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RecipeBook
{
    protected final BitSet recipes = new BitSet();
    /** Recipes the player has not yet seen, so the GUI can play an animation */
    protected final BitSet newRecipes = new BitSet();
    protected boolean isGuiOpen;
    protected boolean isFilteringCraftable;

    public void copyFrom(RecipeBook that)
    {
        this.recipes.clear();
        this.newRecipes.clear();
        this.recipes.or(that.recipes);
        this.newRecipes.or(that.newRecipes);
    }

    public void unlock(IRecipe recipe)
    {
        if (!recipe.isDynamic())
        {
            this.recipes.set(func_194075_d(recipe));
        }
    }

    public boolean isUnlocked(@Nullable IRecipe recipe)
    {
        return this.recipes.get(func_194075_d(recipe));
    }

    public void lock(IRecipe recipe)
    {
        int i = func_194075_d(recipe);
        this.recipes.clear(i);
        this.newRecipes.clear(i);
    }

    @Deprecated //DO NOT USE
    protected static int func_194075_d(@Nullable IRecipe p_194075_0_)
    {
        int ret = CraftingManager.field_193380_a.getId(p_194075_0_);
        if (ret == -1)
        {
            ret = ((net.minecraftforge.registries.ForgeRegistry<IRecipe>)net.minecraftforge.fml.common.registry.ForgeRegistries.RECIPES).getID(p_194075_0_.getRegistryName());
            if (ret == -1)
                throw new IllegalArgumentException(String.format("Attempted to get the ID for a unknown recipe: %s Name: %s", p_194075_0_, p_194075_0_.getRegistryName()));
        }
        return ret;
    }

    @SideOnly(Side.CLIENT)
    public boolean isNew(IRecipe recipe)
    {
        return this.newRecipes.get(func_194075_d(recipe));
    }

    public void markSeen(IRecipe recipe)
    {
        this.newRecipes.clear(func_194075_d(recipe));
    }

    public void markNew(IRecipe recipe)
    {
        this.newRecipes.set(func_194075_d(recipe));
    }

    @SideOnly(Side.CLIENT)
    public boolean isGuiOpen()
    {
        return this.isGuiOpen;
    }

    public void setGuiOpen(boolean open)
    {
        this.isGuiOpen = open;
    }

    @SideOnly(Side.CLIENT)
    public boolean isFilteringCraftable()
    {
        return this.isFilteringCraftable;
    }

    public void setFilteringCraftable(boolean shouldFilter)
    {
        this.isFilteringCraftable = shouldFilter;
    }
}