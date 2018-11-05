package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemLead extends Item
{
    public ItemLead()
    {
        this.func_77637_a(CreativeTabs.TOOLS);
    }

    public EnumActionResult func_180614_a(EntityPlayer p_180614_1_, World p_180614_2_, BlockPos p_180614_3_, EnumHand p_180614_4_, EnumFacing p_180614_5_, float p_180614_6_, float p_180614_7_, float p_180614_8_)
    {
        Block block = p_180614_2_.getBlockState(p_180614_3_).getBlock();

        if (!(block instanceof BlockFence))
        {
            return EnumActionResult.PASS;
        }
        else
        {
            if (!p_180614_2_.isRemote)
            {
                attachToFence(p_180614_1_, p_180614_2_, p_180614_3_);
            }

            return EnumActionResult.SUCCESS;
        }
    }

    public static boolean attachToFence(EntityPlayer player, World worldIn, BlockPos fence)
    {
        EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForPosition(worldIn, fence);
        boolean flag = false;
        double d0 = 7.0D;
        int i = fence.getX();
        int j = fence.getY();
        int k = fence.getZ();

        for (EntityLiving entityliving : worldIn.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB((double)i - 7.0D, (double)j - 7.0D, (double)k - 7.0D, (double)i + 7.0D, (double)j + 7.0D, (double)k + 7.0D)))
        {
            if (entityliving.getLeashed() && entityliving.getLeashHolder() == player)
            {
                if (entityleashknot == null)
                {
                    entityleashknot = EntityLeashKnot.createKnot(worldIn, fence);
                }

                entityliving.setLeashHolder(entityleashknot, true);
                flag = true;
            }
        }

        return flag;
    }
}