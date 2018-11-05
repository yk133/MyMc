package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemKnowledgeBook extends Item
{
    private static final Logger LOGGER = LogManager.getLogger();

    public ItemKnowledgeBook()
    {
        this.func_77625_d(1);
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (!playerIn.abilities.isCreativeMode)
        {
            playerIn.setHeldItem(handIn, ItemStack.EMPTY);
        }

        if (nbttagcompound != null && nbttagcompound.contains("Recipes", 9))
        {
            if (!worldIn.isRemote)
            {
                NBTTagList nbttaglist = nbttagcompound.getList("Recipes", 8);
                List<IRecipe> list = Lists.<IRecipe>newArrayList();

                for (int i = 0; i < nbttaglist.func_74745_c(); ++i)
                {
                    String s = nbttaglist.getString(i);
                    IRecipe irecipe = CraftingManager.func_193373_a(new ResourceLocation(s));

                    if (irecipe == null)
                    {
                        LOGGER.error("Invalid recipe: " + s);
                        return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
                    }

                    list.add(irecipe);
                }

                playerIn.func_192021_a(list);
                playerIn.addStat(StatList.func_188057_b(this));
            }

            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        }
        else
        {
            LOGGER.error("Tag not valid: " + nbttagcompound);
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
        }
    }
}