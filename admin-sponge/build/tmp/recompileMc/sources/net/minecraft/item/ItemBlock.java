package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlock extends Item
{
    protected final Block block;

    public ItemBlock(Block p_i45328_1_)
    {
        this.block = p_i45328_1_;
    }

    public EnumActionResult func_180614_a(EntityPlayer p_180614_1_, World p_180614_2_, BlockPos p_180614_3_, EnumHand p_180614_4_, EnumFacing p_180614_5_, float p_180614_6_, float p_180614_7_, float p_180614_8_)
    {
        IBlockState iblockstate = p_180614_2_.getBlockState(p_180614_3_);
        Block block = iblockstate.getBlock();

        if (!block.func_176200_f(p_180614_2_, p_180614_3_))
        {
            p_180614_3_ = p_180614_3_.offset(p_180614_5_);
        }

        ItemStack itemstack = p_180614_1_.getHeldItem(p_180614_4_);

        if (!itemstack.isEmpty() && p_180614_1_.canPlayerEdit(p_180614_3_, p_180614_5_, itemstack) && p_180614_2_.func_190527_a(this.block, p_180614_3_, false, p_180614_5_, (Entity)null))
        {
            int i = this.func_77647_b(itemstack.func_77960_j());
            IBlockState iblockstate1 = this.block.getStateForPlacement(p_180614_2_, p_180614_3_, p_180614_5_, p_180614_6_, p_180614_7_, p_180614_8_, i, p_180614_1_, p_180614_4_);

            if (placeBlockAt(itemstack, p_180614_1_, p_180614_2_, p_180614_3_, p_180614_5_, p_180614_6_, p_180614_7_, p_180614_8_, iblockstate1))
            {
                iblockstate1 = p_180614_2_.getBlockState(p_180614_3_);
                SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, p_180614_2_, p_180614_3_, p_180614_1_);
                p_180614_2_.playSound(p_180614_1_, p_180614_3_, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }

    public static boolean setTileEntityNBT(World worldIn, @Nullable EntityPlayer player, BlockPos pos, ItemStack stackIn)
    {
        MinecraftServer minecraftserver = worldIn.getServer();

        if (minecraftserver == null)
        {
            return false;
        }
        else
        {
            NBTTagCompound nbttagcompound = stackIn.getChildTag("BlockEntityTag");

            if (nbttagcompound != null)
            {
                TileEntity tileentity = worldIn.getTileEntity(pos);

                if (tileentity != null)
                {
                    if (!worldIn.isRemote && tileentity.onlyOpsCanSetNbt() && (player == null || !player.func_189808_dh()))
                    {
                        return false;
                    }

                    NBTTagCompound nbttagcompound1 = tileentity.write(new NBTTagCompound());
                    NBTTagCompound nbttagcompound2 = nbttagcompound1.copy();
                    nbttagcompound1.func_179237_a(nbttagcompound);
                    nbttagcompound1.putInt("x", pos.getX());
                    nbttagcompound1.putInt("y", pos.getY());
                    nbttagcompound1.putInt("z", pos.getZ());

                    if (!nbttagcompound1.equals(nbttagcompound2))
                    {
                        tileentity.read(nbttagcompound1);
                        tileentity.markDirty();
                        return true;
                    }
                }
            }

            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean func_179222_a(World p_179222_1_, BlockPos p_179222_2_, EnumFacing p_179222_3_, EntityPlayer p_179222_4_, ItemStack p_179222_5_)
    {
        Block block = p_179222_1_.getBlockState(p_179222_2_).getBlock();

        if (block == Blocks.field_150431_aC && block.func_176200_f(p_179222_1_, p_179222_2_))
        {
            p_179222_3_ = EnumFacing.UP;
        }
        else if (!block.func_176200_f(p_179222_1_, p_179222_2_))
        {
            p_179222_2_ = p_179222_2_.offset(p_179222_3_);
        }

        return p_179222_1_.func_190527_a(this.block, p_179222_2_, false, p_179222_3_, (Entity)null);
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getTranslationKey(ItemStack stack)
    {
        return this.block.getTranslationKey();
    }

    /**
     * Returns the unlocalized name of this item.
     */
    public String getTranslationKey()
    {
        return this.block.getTranslationKey();
    }

    /**
     * gets the CreativeTab this item is displayed on
     */
    public CreativeTabs getGroup()
    {
        return this.block.func_149708_J();
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void fillItemGroup(CreativeTabs group, NonNullList<ItemStack> items)
    {
        if (this.isInGroup(group))
        {
            this.block.fillItemGroup(group, items);
        }
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        this.block.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public Block getBlock()
    {
        return this.getBlockRaw() == null ? null : this.getBlockRaw().delegate.get();
    }

    private Block getBlockRaw()
    {
        return this.block;
    }

    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     *
     * @param stack The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        if (!world.setBlockState(pos, newState, 11)) return false;

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this.block)
        {
            setTileEntityNBT(world, player, pos, stack);
            this.block.onBlockPlacedBy(world, pos, state, player, stack);

            if (player instanceof EntityPlayerMP)
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
        }

        return true;
    }
}