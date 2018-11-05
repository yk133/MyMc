package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWrittenBook extends Item
{
    public ItemWrittenBook()
    {
        this.func_77625_d(1);
    }

    public static boolean validBookTagContents(NBTTagCompound nbt)
    {
        if (!ItemWritableBook.isNBTValid(nbt))
        {
            return false;
        }
        else if (!nbt.contains("title", 8))
        {
            return false;
        }
        else
        {
            String s = nbt.getString("title");
            return s != null && s.length() <= 32 ? nbt.contains("author", 8) : false;
        }
    }

    /**
     * Gets the generation of the book (how many times it has been cloned)
     */
    public static int getGeneration(ItemStack book)
    {
        return book.getTag().getInt("generation");
    }

    public String func_77653_i(ItemStack p_77653_1_)
    {
        if (p_77653_1_.hasTag())
        {
            NBTTagCompound nbttagcompound = p_77653_1_.getTag();
            String s = nbttagcompound.getString("title");

            if (!StringUtils.isNullOrEmpty(s))
            {
                return s;
            }
        }

        return super.func_77653_i(p_77653_1_);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (stack.hasTag())
        {
            NBTTagCompound nbttagcompound = stack.getTag();
            String s = nbttagcompound.getString("author");

            if (!StringUtils.isNullOrEmpty(s))
            {
                tooltip.add(TextFormatting.GRAY + I18n.func_74837_a("book.byAuthor", s));
            }

            tooltip.add(TextFormatting.GRAY + I18n.func_74838_a("book.generation." + nbttagcompound.getInt("generation")));
        }
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (!worldIn.isRemote)
        {
            this.resolveContents(itemstack, playerIn);
        }

        playerIn.openBook(itemstack, handIn);
        playerIn.addStat(StatList.func_188057_b(this));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
    }

    private void resolveContents(ItemStack stack, EntityPlayer player)
    {
        if (stack.getTag() != null)
        {
            NBTTagCompound nbttagcompound = stack.getTag();

            if (!nbttagcompound.getBoolean("resolved"))
            {
                nbttagcompound.putBoolean("resolved", true);

                if (validBookTagContents(nbttagcompound))
                {
                    NBTTagList nbttaglist = nbttagcompound.getList("pages", 8);

                    for (int i = 0; i < nbttaglist.func_74745_c(); ++i)
                    {
                        String s = nbttaglist.getString(i);
                        ITextComponent itextcomponent;

                        try
                        {
                            itextcomponent = ITextComponent.Serializer.fromJsonLenient(s);
                            itextcomponent = TextComponentUtils.func_179985_a(player, itextcomponent, player);
                        }
                        catch (Exception var9)
                        {
                            itextcomponent = new TextComponentString(s);
                        }

                        nbttaglist.func_150304_a(i, new NBTTagString(ITextComponent.Serializer.toJson(itextcomponent)));
                    }

                    nbttagcompound.put("pages", nbttaglist);

                    if (player instanceof EntityPlayerMP && player.getHeldItemMainhand() == stack)
                    {
                        Slot slot = player.openContainer.getSlotFromInventory(player.inventory, player.inventory.currentItem);
                        ((EntityPlayerMP)player).connection.sendPacket(new SPacketSetSlot(0, slot.slotNumber, stack));
                    }
                }
            }
        }
    }

    /**
     * Returns true if this item has an enchantment glint. By default, this returns
     * <code>stack.isItemEnchanted()</code>, but other items can override it (for instance, written books always return
     * true).
     *  
     * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
     * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
     */
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return true;
    }
}