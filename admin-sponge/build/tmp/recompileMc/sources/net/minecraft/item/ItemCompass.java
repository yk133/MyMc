package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCompass extends Item
{
    public ItemCompass()
    {
        this.addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            double rotation;
            @SideOnly(Side.CLIENT)
            double rota;
            @SideOnly(Side.CLIENT)
            long lastUpdateTick;
            @SideOnly(Side.CLIENT)
            public float func_185085_a(ItemStack p_185085_1_, @Nullable World p_185085_2_, @Nullable EntityLivingBase p_185085_3_)
            {
                if (p_185085_3_ == null && !p_185085_1_.isOnItemFrame())
                {
                    return 0.0F;
                }
                else
                {
                    boolean flag = p_185085_3_ != null;
                    Entity entity = (Entity)(flag ? p_185085_3_ : p_185085_1_.getItemFrame());

                    if (p_185085_2_ == null)
                    {
                        p_185085_2_ = entity.world;
                    }

                    double d0;

                    if (p_185085_2_.dimension.isSurfaceWorld())
                    {
                        double d1 = flag ? (double)entity.rotationYaw : this.getFrameRotation((EntityItemFrame)entity);
                        d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
                        double d2 = this.getSpawnToAngle(p_185085_2_, entity) / (Math.PI * 2D);
                        d0 = 0.5D - (d1 - 0.25D - d2);
                    }
                    else
                    {
                        d0 = Math.random();
                    }

                    if (flag)
                    {
                        d0 = this.wobble(p_185085_2_, d0);
                    }

                    return MathHelper.positiveModulo((float)d0, 1.0F);
                }
            }
            @SideOnly(Side.CLIENT)
            private double wobble(World worldIn, double p_185093_2_)
            {
                if (worldIn.getGameTime() != this.lastUpdateTick)
                {
                    this.lastUpdateTick = worldIn.getGameTime();
                    double d0 = p_185093_2_ - this.rotation;
                    d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
                    this.rota += d0 * 0.1D;
                    this.rota *= 0.8D;
                    this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
                }

                return this.rotation;
            }
            @SideOnly(Side.CLIENT)
            private double getFrameRotation(EntityItemFrame p_185094_1_)
            {
                return (double)MathHelper.wrapDegrees(180 + p_185094_1_.facingDirection.getHorizontalIndex() * 90);
            }
            @SideOnly(Side.CLIENT)
            private double getSpawnToAngle(World p_185092_1_, Entity p_185092_2_)
            {
                BlockPos blockpos = p_185092_1_.getSpawnPoint();
                return Math.atan2((double)blockpos.getZ() - p_185092_2_.posZ, (double)blockpos.getX() - p_185092_2_.posX);
            }
        });
    }
}