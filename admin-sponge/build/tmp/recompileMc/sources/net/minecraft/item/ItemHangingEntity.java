package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemHangingEntity extends Item
{
    private final Class <? extends EntityHanging > hangingEntityClass;

    public ItemHangingEntity(Class <? extends EntityHanging > p_i45342_1_)
    {
        this.hangingEntityClass = p_i45342_1_;
        this.func_77637_a(CreativeTabs.DECORATIONS);
    }

    public EnumActionResult func_180614_a(EntityPlayer p_180614_1_, World p_180614_2_, BlockPos p_180614_3_, EnumHand p_180614_4_, EnumFacing p_180614_5_, float p_180614_6_, float p_180614_7_, float p_180614_8_)
    {
        ItemStack itemstack = p_180614_1_.getHeldItem(p_180614_4_);
        BlockPos blockpos = p_180614_3_.offset(p_180614_5_);

        if (p_180614_5_ != EnumFacing.DOWN && p_180614_5_ != EnumFacing.UP && p_180614_1_.canPlayerEdit(blockpos, p_180614_5_, itemstack))
        {
            EntityHanging entityhanging = this.createEntity(p_180614_2_, blockpos, p_180614_5_);

            if (entityhanging != null && entityhanging.onValidSurface())
            {
                if (!p_180614_2_.isRemote)
                {
                    entityhanging.playPlaceSound();
                    p_180614_2_.spawnEntity(entityhanging);
                }

                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }

    @Nullable
    private EntityHanging createEntity(World worldIn, BlockPos pos, EnumFacing clickedSide)
    {
        if (this.hangingEntityClass == EntityPainting.class)
        {
            return new EntityPainting(worldIn, pos, clickedSide);
        }
        else
        {
            return this.hangingEntityClass == EntityItemFrame.class ? new EntityItemFrame(worldIn, pos, clickedSide) : null;
        }
    }
}