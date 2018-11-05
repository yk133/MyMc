package net.minecraft.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;

public class RayTraceResult
{
    /** Used to determine what sub-segment is hit */
    public int subHit = -1;

    /** Used to add extra hit info */
    public Object hitInfo = null;

    private BlockPos blockPos;
    public RayTraceResult.Type type;
    public EnumFacing sideHit;
    /** The vector position of the hit */
    public Vec3d hitVec;
    /** The hit entity */
    public Entity entity;

    public RayTraceResult(Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn)
    {
        this(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, blockPosIn);
    }

    public RayTraceResult(Vec3d p_i47095_1_, EnumFacing p_i47095_2_)
    {
        this(RayTraceResult.Type.BLOCK, p_i47095_1_, p_i47095_2_, BlockPos.ORIGIN);
    }

    public RayTraceResult(Entity entityIn)
    {
        this(entityIn, new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ));
    }

    public RayTraceResult(RayTraceResult.Type typeIn, Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn)
    {
        this.type = typeIn;
        this.blockPos = blockPosIn;
        this.sideHit = sideHitIn;
        this.hitVec = new Vec3d(hitVecIn.x, hitVecIn.y, hitVecIn.z);
    }

    public RayTraceResult(Entity entityHitIn, Vec3d hitVecIn)
    {
        this.type = RayTraceResult.Type.ENTITY;
        this.entity = entityHitIn;
        this.hitVec = hitVecIn;
    }

    public BlockPos getBlockPos()
    {
        return this.blockPos;
    }

    public String toString()
    {
        return "HitResult{type=" + this.type + ", blockpos=" + this.blockPos + ", f=" + this.sideHit + ", pos=" + this.hitVec + ", entity=" + this.entity + '}';
    }

    public static enum Type
    {
        MISS,
        BLOCK,
        ENTITY;
    }
}