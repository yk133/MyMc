package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.glu.GLU;

@SideOnly(Side.CLIENT)
public class ActiveRenderInfo
{
    private static final IntBuffer field_178814_a = GLAllocation.func_74527_f(16);
    /** The current GL modelview matrix */
    private static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer field_178813_c = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer field_178810_d = GLAllocation.createDirectFloatBuffer(3);
    private static Vec3d position = new Vec3d(0.0D, 0.0D, 0.0D);
    /** The X component of the entity's yaw rotation */
    private static float rotationX;
    /** The combined X and Z components of the entity's pitch rotation */
    private static float rotationXZ;
    /** The Z component of the entity's yaw rotation */
    private static float rotationZ;
    /** The Y component (scaled along the Z axis) of the entity's pitch rotation */
    private static float rotationYZ;
    /** The Y component (scaled along the X axis) of the entity's pitch rotation */
    private static float rotationXY;

    public static void func_74583_a(EntityPlayer p_74583_0_, boolean p_74583_1_)
    {
        updateRenderInfo((Entity) p_74583_0_, p_74583_1_);
    }

    public static void updateRenderInfo(Entity p_74583_0_, boolean p_74583_1_)
    {
        GlStateManager.getFloatv(2982, MODELVIEW);
        GlStateManager.getFloatv(2983, field_178813_c);
        GlStateManager.func_187445_a(2978, field_178814_a);
        float f = (float)((field_178814_a.get(0) + field_178814_a.get(2)) / 2);
        float f1 = (float)((field_178814_a.get(1) + field_178814_a.get(3)) / 2);
        GLU.gluUnProject(f, f1, 0.0F, MODELVIEW, field_178813_c, field_178814_a, field_178810_d);
        position = new Vec3d((double)field_178810_d.get(0), (double)field_178810_d.get(1), (double)field_178810_d.get(2));
        int i = p_74583_1_ ? 1 : 0;
        float f2 = p_74583_0_.rotationPitch;
        float f3 = p_74583_0_.rotationYaw;
        rotationX = MathHelper.cos(f3 * 0.017453292F) * (float)(1 - i * 2);
        rotationZ = MathHelper.sin(f3 * 0.017453292F) * (float)(1 - i * 2);
        rotationYZ = -rotationZ * MathHelper.sin(f2 * 0.017453292F) * (float)(1 - i * 2);
        rotationXY = rotationX * MathHelper.sin(f2 * 0.017453292F) * (float)(1 - i * 2);
        rotationXZ = MathHelper.cos(f2 * 0.017453292F);
    }

    public static Vec3d projectViewFromEntity(Entity entityIn, double p_178806_1_)
    {
        double d0 = entityIn.prevPosX + (entityIn.posX - entityIn.prevPosX) * p_178806_1_;
        double d1 = entityIn.prevPosY + (entityIn.posY - entityIn.prevPosY) * p_178806_1_;
        double d2 = entityIn.prevPosZ + (entityIn.posZ - entityIn.prevPosZ) * p_178806_1_;
        double d3 = d0 + position.x;
        double d4 = d1 + position.y;
        double d5 = d2 + position.z;
        return new Vec3d(d3, d4, d5);
    }

    public static IBlockState getBlockStateAtEntityViewpoint(World worldIn, Entity entityIn, float p_186703_2_)
    {
        Vec3d vec3d = projectViewFromEntity(entityIn, (double)p_186703_2_);
        BlockPos blockpos = new BlockPos(vec3d);
        IBlockState iblockstate = worldIn.getBlockState(blockpos);

        if (iblockstate.getMaterial().isLiquid())
        {
            float f = 0.0F;

            if (iblockstate.getBlock() instanceof BlockLiquid)
            {
                f = BlockLiquid.func_149801_b(((Integer)iblockstate.get(BlockLiquid.LEVEL)).intValue()) - 0.11111111F;
            }

            float f1 = (float)(blockpos.getY() + 1) - f;

            if (vec3d.y >= (double)f1)
            {
                iblockstate = worldIn.getBlockState(blockpos.up());
            }
        }

        return iblockstate.getBlock().getStateAtViewpoint(iblockstate, worldIn, blockpos, vec3d);
    }

    public static float getRotationX()
    {
        return rotationX;
    }

    public static float getRotationXZ()
    {
        return rotationXZ;
    }

    public static float getRotationZ()
    {
        return rotationZ;
    }

    public static float getRotationYZ()
    {
        return rotationYZ;
    }

    public static float getRotationXY()
    {
        return rotationXY;
    }

    /* ======================================== FORGE START =====================================*/

    /**
     * Vector from render view entity position (corrected for partialTickTime) to the middle of screen
     */
    public static Vec3d getCameraPosition()
    {
        return position;
    }
}