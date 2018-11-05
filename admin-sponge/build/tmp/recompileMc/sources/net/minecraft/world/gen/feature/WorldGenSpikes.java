package net.minecraft.world.gen.feature;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WorldGenSpikes extends WorldGenerator
{
    private boolean crystalInvulnerable;
    private WorldGenSpikes.EndSpike spike;
    private BlockPos beamTarget;

    public void setSpike(WorldGenSpikes.EndSpike p_186143_1_)
    {
        this.spike = p_186143_1_;
    }

    public void setCrystalInvulnerable(boolean p_186144_1_)
    {
        this.crystalInvulnerable = p_186144_1_;
    }

    public boolean func_180709_b(World p_180709_1_, Random p_180709_2_, BlockPos p_180709_3_)
    {
        if (this.spike == null)
        {
            throw new IllegalStateException("Decoration requires priming with a spike");
        }
        else
        {
            int i = this.spike.getRadius();

            for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(new BlockPos(p_180709_3_.getX() - i, 0, p_180709_3_.getZ() - i), new BlockPos(p_180709_3_.getX() + i, this.spike.getHeight() + 10, p_180709_3_.getZ() + i)))
            {
                if (blockpos$mutableblockpos.distanceSq((double)p_180709_3_.getX(), (double)blockpos$mutableblockpos.getY(), (double)p_180709_3_.getZ()) <= (double)(i * i + 1) && blockpos$mutableblockpos.getY() < this.spike.getHeight())
                {
                    this.func_175903_a(p_180709_1_, blockpos$mutableblockpos, Blocks.OBSIDIAN.getDefaultState());
                }
                else if (blockpos$mutableblockpos.getY() > 65)
                {
                    this.func_175903_a(p_180709_1_, blockpos$mutableblockpos, Blocks.AIR.getDefaultState());
                }
            }

            if (this.spike.isGuarded())
            {
                for (int j = -2; j <= 2; ++j)
                {
                    for (int k = -2; k <= 2; ++k)
                    {
                        if (MathHelper.abs(j) == 2 || MathHelper.abs(k) == 2)
                        {
                            this.func_175903_a(p_180709_1_, new BlockPos(p_180709_3_.getX() + j, this.spike.getHeight(), p_180709_3_.getZ() + k), Blocks.IRON_BARS.getDefaultState());
                            this.func_175903_a(p_180709_1_, new BlockPos(p_180709_3_.getX() + j, this.spike.getHeight() + 1, p_180709_3_.getZ() + k), Blocks.IRON_BARS.getDefaultState());
                            this.func_175903_a(p_180709_1_, new BlockPos(p_180709_3_.getX() + j, this.spike.getHeight() + 2, p_180709_3_.getZ() + k), Blocks.IRON_BARS.getDefaultState());
                        }

                        this.func_175903_a(p_180709_1_, new BlockPos(p_180709_3_.getX() + j, this.spike.getHeight() + 3, p_180709_3_.getZ() + k), Blocks.IRON_BARS.getDefaultState());
                    }
                }
            }

            EntityEnderCrystal entityendercrystal = new EntityEnderCrystal(p_180709_1_);
            entityendercrystal.setBeamTarget(this.beamTarget);
            entityendercrystal.setInvulnerable(this.crystalInvulnerable);
            entityendercrystal.setLocationAndAngles((double)((float)p_180709_3_.getX() + 0.5F), (double)(this.spike.getHeight() + 1), (double)((float)p_180709_3_.getZ() + 0.5F), p_180709_2_.nextFloat() * 360.0F, 0.0F);
            p_180709_1_.spawnEntity(entityendercrystal);
            this.func_175903_a(p_180709_1_, new BlockPos(p_180709_3_.getX(), this.spike.getHeight(), p_180709_3_.getZ()), Blocks.BEDROCK.getDefaultState());
            return true;
        }
    }

    /**
     * Sets the value that will be used in a call to entitycrystal.setBeamTarget.
     * At the moment, WorldGenSpikes.setBeamTarget is only ever called with a value of (0, 128, 0)
     */
    public void setBeamTarget(@Nullable BlockPos pos)
    {
        this.beamTarget = pos;
    }

    public static class EndSpike
        {
            private final int centerX;
            private final int centerZ;
            private final int radius;
            private final int height;
            private final boolean guarded;
            private final AxisAlignedBB topBoundingBox;

            public EndSpike(int p_i47020_1_, int p_i47020_2_, int p_i47020_3_, int p_i47020_4_, boolean p_i47020_5_)
            {
                this.centerX = p_i47020_1_;
                this.centerZ = p_i47020_2_;
                this.radius = p_i47020_3_;
                this.height = p_i47020_4_;
                this.guarded = p_i47020_5_;
                this.topBoundingBox = new AxisAlignedBB((double)(p_i47020_1_ - p_i47020_3_), 0.0D, (double)(p_i47020_2_ - p_i47020_3_), (double)(p_i47020_1_ + p_i47020_3_), 256.0D, (double)(p_i47020_2_ + p_i47020_3_));
            }

            public boolean doesStartInChunk(BlockPos p_186154_1_)
            {
                int i = this.centerX - this.radius;
                int j = this.centerZ - this.radius;
                return p_186154_1_.getX() == (i & -16) && p_186154_1_.getZ() == (j & -16);
            }

            public int getCenterX()
            {
                return this.centerX;
            }

            public int getCenterZ()
            {
                return this.centerZ;
            }

            public int getRadius()
            {
                return this.radius;
            }

            public int getHeight()
            {
                return this.height;
            }

            public boolean isGuarded()
            {
                return this.guarded;
            }

            public AxisAlignedBB getTopBoundingBox()
            {
                return this.topBoundingBox;
            }
        }
}