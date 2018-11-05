package net.minecraft.client.renderer.block.model;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

@SideOnly(Side.CLIENT)
public class FaceBakery
{
    private static final float SCALE_ROTATION_22_5 = 1.0F / (float)Math.cos(0.39269909262657166D) - 1.0F;
    private static final float SCALE_ROTATION_GENERAL = 1.0F / (float)Math.cos((Math.PI / 4D)) - 1.0F;
    private static final FaceBakery.Rotation[] UV_ROTATIONS = new FaceBakery.Rotation[ModelRotation.values().length * EnumFacing.values().length];
    private static final FaceBakery.Rotation UV_ROTATION_0 = new FaceBakery.Rotation()
    {
        BlockFaceUV makeRotatedUV(float u1, float v1, float u2, float v2)
        {
            return new BlockFaceUV(new float[] {u1, v1, u2, v2}, 0);
        }
    };
    private static final FaceBakery.Rotation UV_ROTATION_270 = new FaceBakery.Rotation()
    {
        BlockFaceUV makeRotatedUV(float u1, float v1, float u2, float v2)
        {
            return new BlockFaceUV(new float[] {v2, 16.0F - u1, v1, 16.0F - u2}, 270);
        }
    };
    private static final FaceBakery.Rotation UV_ROTATION_INVERSE = new FaceBakery.Rotation()
    {
        BlockFaceUV makeRotatedUV(float u1, float v1, float u2, float v2)
        {
            return new BlockFaceUV(new float[] {16.0F - u1, 16.0F - v1, 16.0F - u2, 16.0F - v2}, 0);
        }
    };
    private static final FaceBakery.Rotation UV_ROTATION_90 = new FaceBakery.Rotation()
    {
        BlockFaceUV makeRotatedUV(float u1, float v1, float u2, float v2)
        {
            return new BlockFaceUV(new float[] {16.0F - v1, u2, 16.0F - v2, u1}, 90);
        }
    };

    public BakedQuad func_178414_a(Vector3f p_178414_1_, Vector3f p_178414_2_, BlockPartFace p_178414_3_, TextureAtlasSprite p_178414_4_, EnumFacing p_178414_5_, ModelRotation p_178414_6_, @Nullable BlockPartRotation p_178414_7_, boolean p_178414_8_, boolean p_178414_9_)
    {
        return makeBakedQuad(p_178414_1_, p_178414_2_, p_178414_3_, p_178414_4_, p_178414_5_, (net.minecraftforge.common.model.ITransformation)p_178414_6_, p_178414_7_, p_178414_8_, p_178414_9_);
    }

    public BakedQuad makeBakedQuad(Vector3f p_178414_1_, Vector3f p_178414_2_, BlockPartFace p_178414_3_, TextureAtlasSprite p_178414_4_, EnumFacing p_178414_5_, net.minecraftforge.common.model.ITransformation p_178414_6_, BlockPartRotation p_178414_7_, boolean p_178414_8_, boolean p_178414_9_)
    {
        BlockFaceUV blockfaceuv = p_178414_3_.blockFaceUV;

        if (p_178414_8_)
        {
            blockfaceuv = net.minecraftforge.client.ForgeHooksClient.applyUVLock(p_178414_3_.blockFaceUV, p_178414_5_, p_178414_6_);
        }

        int[] aint = this.makeQuadVertexData(blockfaceuv, p_178414_4_, p_178414_5_, this.func_178403_a(p_178414_1_, p_178414_2_), p_178414_6_, p_178414_7_, false);
        EnumFacing enumfacing = getFacingFromVertexData(aint);

        if (p_178414_7_ == null)
        {
            this.applyFacing(aint, enumfacing);
        }

        net.minecraftforge.client.ForgeHooksClient.fillNormal(aint, enumfacing);
        return new BakedQuad(aint, p_178414_3_.tintIndex, enumfacing, p_178414_4_, p_178414_9_, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM);
    }

    private BlockFaceUV applyUVLock(BlockFaceUV blockFaceUVIn, EnumFacing facing, ModelRotation modelRotationIn)
    {
        return UV_ROTATIONS[getIndex(modelRotationIn, facing)].rotateUV(blockFaceUVIn);
    }

    private int[] makeQuadVertexData(BlockFaceUV uvs, TextureAtlasSprite sprite, EnumFacing orientation, float[] posDiv16, ModelRotation rotationIn, @Nullable BlockPartRotation partRotation, boolean shade)
    {
        return makeQuadVertexData(uvs, sprite, orientation, posDiv16, (net.minecraftforge.common.model.ITransformation)rotationIn, partRotation, shade);
    }

    private int[] makeQuadVertexData(BlockFaceUV uvs, TextureAtlasSprite sprite, EnumFacing orientation, float[] posDiv16, net.minecraftforge.common.model.ITransformation rotationIn, BlockPartRotation partRotation, boolean shade)
    {
        int[] aint = new int[28];

        for (int i = 0; i < 4; ++i)
        {
            this.fillVertexData(aint, i, orientation, uvs, posDiv16, sprite, rotationIn, partRotation, shade);
        }

        return aint;
    }

    private int getFaceShadeColor(EnumFacing facing)
    {
        float f = this.getFaceBrightness(facing);
        int i = MathHelper.clamp((int)(f * 255.0F), 0, 255);
        return -16777216 | i << 16 | i << 8 | i;
    }

    private float getFaceBrightness(EnumFacing facing)
    {
        switch (facing)
        {
            case DOWN:
                return 0.5F;
            case UP:
                return 1.0F;
            case NORTH:
            case SOUTH:
                return 0.8F;
            case WEST:
            case EAST:
                return 0.6F;
            default:
                return 1.0F;
        }
    }

    private float[] func_178403_a(Vector3f p_178403_1_, Vector3f p_178403_2_)
    {
        float[] afloat = new float[EnumFacing.values().length];
        afloat[EnumFaceDirection.Constants.WEST_INDEX] = p_178403_1_.x / 16.0F;
        afloat[EnumFaceDirection.Constants.DOWN_INDEX] = p_178403_1_.y / 16.0F;
        afloat[EnumFaceDirection.Constants.NORTH_INDEX] = p_178403_1_.z / 16.0F;
        afloat[EnumFaceDirection.Constants.EAST_INDEX] = p_178403_2_.x / 16.0F;
        afloat[EnumFaceDirection.Constants.UP_INDEX] = p_178403_2_.y / 16.0F;
        afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = p_178403_2_.z / 16.0F;
        return afloat;
    }

    private void fillVertexData(int[] vertexData, int vertexIndex, EnumFacing facing, BlockFaceUV blockFaceUVIn, float[] posDiv16, TextureAtlasSprite sprite, ModelRotation rotationIn, @Nullable BlockPartRotation partRotation, boolean shade)
    {
        fillVertexData(vertexData, vertexIndex, facing, blockFaceUVIn, posDiv16, sprite, (net.minecraftforge.common.model.ITransformation)rotationIn, partRotation, shade);
    }

    private void fillVertexData(int[] vertexData, int vertexIndex, EnumFacing facing, BlockFaceUV blockFaceUVIn, float[] posDiv16, TextureAtlasSprite sprite, net.minecraftforge.common.model.ITransformation rotationIn, BlockPartRotation partRotation, boolean shade)
    {
        EnumFacing enumfacing = rotationIn.rotate(facing);
        int i = shade ? this.getFaceShadeColor(enumfacing) : -1;
        EnumFaceDirection.VertexInformation enumfacedirection$vertexinformation = EnumFaceDirection.getFacing(facing).getVertexInformation(vertexIndex);
        Vector3f vector3f = new Vector3f(posDiv16[enumfacedirection$vertexinformation.xIndex], posDiv16[enumfacedirection$vertexinformation.yIndex], posDiv16[enumfacedirection$vertexinformation.zIndex]);
        this.func_178407_a(vector3f, partRotation);
        int j = this.rotateVertex(vector3f, facing, vertexIndex, rotationIn);
        this.func_178404_a(vertexData, j, vertexIndex, vector3f, i, sprite, blockFaceUVIn);
    }

    private void func_178404_a(int[] p_178404_1_, int p_178404_2_, int p_178404_3_, Vector3f p_178404_4_, int p_178404_5_, TextureAtlasSprite p_178404_6_, BlockFaceUV p_178404_7_)
    {
        int i = p_178404_2_ * 7;
        p_178404_1_[i] = Float.floatToRawIntBits(p_178404_4_.x);
        p_178404_1_[i + 1] = Float.floatToRawIntBits(p_178404_4_.y);
        p_178404_1_[i + 2] = Float.floatToRawIntBits(p_178404_4_.z);
        p_178404_1_[i + 3] = p_178404_5_;
        p_178404_1_[i + 4] = Float.floatToRawIntBits(p_178404_6_.getInterpolatedU((double)p_178404_7_.getVertexU(p_178404_3_) * .999 + p_178404_7_.getVertexU((p_178404_3_ + 2) % 4) * .001));
        p_178404_1_[i + 4 + 1] = Float.floatToRawIntBits(p_178404_6_.getInterpolatedV((double)p_178404_7_.getVertexV(p_178404_3_) * .999 + p_178404_7_.getVertexV((p_178404_3_ + 2) % 4) * .001));
    }

    private void func_178407_a(Vector3f p_178407_1_, @Nullable BlockPartRotation p_178407_2_)
    {
        if (p_178407_2_ != null)
        {
            Matrix4f matrix4f = this.func_178411_a();
            Vector3f vector3f = new Vector3f(0.0F, 0.0F, 0.0F);

            switch (p_178407_2_.axis)
            {
                case X:
                    Matrix4f.rotate(p_178407_2_.angle * 0.017453292F, new Vector3f(1.0F, 0.0F, 0.0F), matrix4f, matrix4f);
                    vector3f.set(0.0F, 1.0F, 1.0F);
                    break;
                case Y:
                    Matrix4f.rotate(p_178407_2_.angle * 0.017453292F, new Vector3f(0.0F, 1.0F, 0.0F), matrix4f, matrix4f);
                    vector3f.set(1.0F, 0.0F, 1.0F);
                    break;
                case Z:
                    Matrix4f.rotate(p_178407_2_.angle * 0.017453292F, new Vector3f(0.0F, 0.0F, 1.0F), matrix4f, matrix4f);
                    vector3f.set(1.0F, 1.0F, 0.0F);
            }

            if (p_178407_2_.rescale)
            {
                if (Math.abs(p_178407_2_.angle) == 22.5F)
                {
                    vector3f.scale(SCALE_ROTATION_22_5);
                }
                else
                {
                    vector3f.scale(SCALE_ROTATION_GENERAL);
                }

                Vector3f.add(vector3f, new Vector3f(1.0F, 1.0F, 1.0F), vector3f);
            }
            else
            {
                vector3f.set(1.0F, 1.0F, 1.0F);
            }

            this.func_178406_a(p_178407_1_, new Vector3f(p_178407_2_.origin), matrix4f, vector3f);
        }
    }

    public int func_188011_a(Vector3f p_188011_1_, EnumFacing p_188011_2_, int p_188011_3_, ModelRotation p_188011_4_)
    {
        return rotateVertex(p_188011_1_, p_188011_2_, p_188011_3_, (net.minecraftforge.common.model.ITransformation)p_188011_4_);
    }

    public int rotateVertex(Vector3f p_188011_1_, EnumFacing p_188011_2_, int p_188011_3_, net.minecraftforge.common.model.ITransformation p_188011_4_)
    {
        if (p_188011_4_ == ModelRotation.X0_Y0)
        {
            return p_188011_3_;
        }
        else
        {
            net.minecraftforge.client.ForgeHooksClient.transform(p_188011_1_, p_188011_4_.getMatrix());
            return p_188011_4_.rotate(p_188011_2_, p_188011_3_);
        }
    }

    private void func_178406_a(Vector3f p_178406_1_, Vector3f p_178406_2_, Matrix4f p_178406_3_, Vector3f p_178406_4_)
    {
        Vector4f vector4f = new Vector4f(p_178406_1_.x - p_178406_2_.x, p_178406_1_.y - p_178406_2_.y, p_178406_1_.z - p_178406_2_.z, 1.0F);
        Matrix4f.transform(p_178406_3_, vector4f, vector4f);
        vector4f.x *= p_178406_4_.x;
        vector4f.y *= p_178406_4_.y;
        vector4f.z *= p_178406_4_.z;
        p_178406_1_.set(vector4f.x + p_178406_2_.x, vector4f.y + p_178406_2_.y, vector4f.z + p_178406_2_.z);
    }

    private Matrix4f func_178411_a()
    {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        return matrix4f;
    }

    public static EnumFacing getFacingFromVertexData(int[] faceData)
    {
        Vector3f vector3f = new Vector3f(Float.intBitsToFloat(faceData[0]), Float.intBitsToFloat(faceData[1]), Float.intBitsToFloat(faceData[2]));
        Vector3f vector3f1 = new Vector3f(Float.intBitsToFloat(faceData[7]), Float.intBitsToFloat(faceData[8]), Float.intBitsToFloat(faceData[9]));
        Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(faceData[14]), Float.intBitsToFloat(faceData[15]), Float.intBitsToFloat(faceData[16]));
        Vector3f vector3f3 = new Vector3f();
        Vector3f vector3f4 = new Vector3f();
        Vector3f vector3f5 = new Vector3f();
        Vector3f.sub(vector3f, vector3f1, vector3f3);
        Vector3f.sub(vector3f2, vector3f1, vector3f4);
        Vector3f.cross(vector3f4, vector3f3, vector3f5);
        float f = (float)Math.sqrt((double)(vector3f5.x * vector3f5.x + vector3f5.y * vector3f5.y + vector3f5.z * vector3f5.z));
        vector3f5.x /= f;
        vector3f5.y /= f;
        vector3f5.z /= f;
        EnumFacing enumfacing = null;
        float f1 = 0.0F;

        for (EnumFacing enumfacing1 : EnumFacing.values())
        {
            Vec3i vec3i = enumfacing1.getDirectionVec();
            Vector3f vector3f6 = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
            float f2 = Vector3f.dot(vector3f5, vector3f6);

            if (f2 >= 0.0F && f2 > f1)
            {
                f1 = f2;
                enumfacing = enumfacing1;
            }
        }

        if (enumfacing == null)
        {
            return EnumFacing.UP;
        }
        else
        {
            return enumfacing;
        }
    }

    private void applyFacing(int[] p_178408_1_, EnumFacing p_178408_2_)
    {
        int[] aint = new int[p_178408_1_.length];
        System.arraycopy(p_178408_1_, 0, aint, 0, p_178408_1_.length);
        float[] afloat = new float[EnumFacing.values().length];
        afloat[EnumFaceDirection.Constants.WEST_INDEX] = 999.0F;
        afloat[EnumFaceDirection.Constants.DOWN_INDEX] = 999.0F;
        afloat[EnumFaceDirection.Constants.NORTH_INDEX] = 999.0F;
        afloat[EnumFaceDirection.Constants.EAST_INDEX] = -999.0F;
        afloat[EnumFaceDirection.Constants.UP_INDEX] = -999.0F;
        afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = -999.0F;

        for (int i = 0; i < 4; ++i)
        {
            int j = 7 * i;
            float f = Float.intBitsToFloat(aint[j]);
            float f1 = Float.intBitsToFloat(aint[j + 1]);
            float f2 = Float.intBitsToFloat(aint[j + 2]);

            if (f < afloat[EnumFaceDirection.Constants.WEST_INDEX])
            {
                afloat[EnumFaceDirection.Constants.WEST_INDEX] = f;
            }

            if (f1 < afloat[EnumFaceDirection.Constants.DOWN_INDEX])
            {
                afloat[EnumFaceDirection.Constants.DOWN_INDEX] = f1;
            }

            if (f2 < afloat[EnumFaceDirection.Constants.NORTH_INDEX])
            {
                afloat[EnumFaceDirection.Constants.NORTH_INDEX] = f2;
            }

            if (f > afloat[EnumFaceDirection.Constants.EAST_INDEX])
            {
                afloat[EnumFaceDirection.Constants.EAST_INDEX] = f;
            }

            if (f1 > afloat[EnumFaceDirection.Constants.UP_INDEX])
            {
                afloat[EnumFaceDirection.Constants.UP_INDEX] = f1;
            }

            if (f2 > afloat[EnumFaceDirection.Constants.SOUTH_INDEX])
            {
                afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = f2;
            }
        }

        EnumFaceDirection enumfacedirection = EnumFaceDirection.getFacing(p_178408_2_);

        for (int i1 = 0; i1 < 4; ++i1)
        {
            int j1 = 7 * i1;
            EnumFaceDirection.VertexInformation enumfacedirection$vertexinformation = enumfacedirection.getVertexInformation(i1);
            float f8 = afloat[enumfacedirection$vertexinformation.xIndex];
            float f3 = afloat[enumfacedirection$vertexinformation.yIndex];
            float f4 = afloat[enumfacedirection$vertexinformation.zIndex];
            p_178408_1_[j1] = Float.floatToRawIntBits(f8);
            p_178408_1_[j1 + 1] = Float.floatToRawIntBits(f3);
            p_178408_1_[j1 + 2] = Float.floatToRawIntBits(f4);

            for (int k = 0; k < 4; ++k)
            {
                int l = 7 * k;
                float f5 = Float.intBitsToFloat(aint[l]);
                float f6 = Float.intBitsToFloat(aint[l + 1]);
                float f7 = Float.intBitsToFloat(aint[l + 2]);

                if (MathHelper.epsilonEquals(f8, f5) && MathHelper.epsilonEquals(f3, f6) && MathHelper.epsilonEquals(f4, f7))
                {
                    p_178408_1_[j1 + 4] = aint[l + 4];
                    p_178408_1_[j1 + 4 + 1] = aint[l + 4 + 1];
                }
            }
        }
    }

    private static void addUvRotation(ModelRotation p_188013_0_, EnumFacing p_188013_1_, FaceBakery.Rotation p_188013_2_)
    {
        UV_ROTATIONS[getIndex(p_188013_0_, p_188013_1_)] = p_188013_2_;
    }

    private static int getIndex(ModelRotation p_188014_0_, EnumFacing p_188014_1_)
    {
        return ModelRotation.values().length * p_188014_1_.ordinal() + p_188014_0_.ordinal();
    }

    static
    {
        addUvRotation(ModelRotation.X0_Y0, EnumFacing.DOWN, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y0, EnumFacing.EAST, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y0, EnumFacing.NORTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y0, EnumFacing.SOUTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y0, EnumFacing.UP, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y0, EnumFacing.WEST, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y90, EnumFacing.EAST, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y90, EnumFacing.NORTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y90, EnumFacing.SOUTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y90, EnumFacing.WEST, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y180, EnumFacing.EAST, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y180, EnumFacing.NORTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y180, EnumFacing.SOUTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y180, EnumFacing.WEST, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y270, EnumFacing.EAST, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y270, EnumFacing.NORTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y270, EnumFacing.SOUTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y270, EnumFacing.WEST, UV_ROTATION_0);
        addUvRotation(ModelRotation.X90_Y0, EnumFacing.DOWN, UV_ROTATION_0);
        addUvRotation(ModelRotation.X90_Y0, EnumFacing.SOUTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X90_Y90, EnumFacing.DOWN, UV_ROTATION_0);
        addUvRotation(ModelRotation.X90_Y180, EnumFacing.DOWN, UV_ROTATION_0);
        addUvRotation(ModelRotation.X90_Y180, EnumFacing.NORTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X90_Y270, EnumFacing.DOWN, UV_ROTATION_0);
        addUvRotation(ModelRotation.X180_Y0, EnumFacing.DOWN, UV_ROTATION_0);
        addUvRotation(ModelRotation.X180_Y0, EnumFacing.UP, UV_ROTATION_0);
        addUvRotation(ModelRotation.X270_Y0, EnumFacing.SOUTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X270_Y0, EnumFacing.UP, UV_ROTATION_0);
        addUvRotation(ModelRotation.X270_Y90, EnumFacing.UP, UV_ROTATION_0);
        addUvRotation(ModelRotation.X270_Y180, EnumFacing.NORTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X270_Y180, EnumFacing.UP, UV_ROTATION_0);
        addUvRotation(ModelRotation.X270_Y270, EnumFacing.UP, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y270, EnumFacing.UP, UV_ROTATION_270);
        addUvRotation(ModelRotation.X0_Y90, EnumFacing.DOWN, UV_ROTATION_270);
        addUvRotation(ModelRotation.X90_Y0, EnumFacing.WEST, UV_ROTATION_270);
        addUvRotation(ModelRotation.X90_Y90, EnumFacing.WEST, UV_ROTATION_270);
        addUvRotation(ModelRotation.X90_Y180, EnumFacing.WEST, UV_ROTATION_270);
        addUvRotation(ModelRotation.X90_Y270, EnumFacing.NORTH, UV_ROTATION_270);
        addUvRotation(ModelRotation.X90_Y270, EnumFacing.SOUTH, UV_ROTATION_270);
        addUvRotation(ModelRotation.X90_Y270, EnumFacing.WEST, UV_ROTATION_270);
        addUvRotation(ModelRotation.X180_Y90, EnumFacing.UP, UV_ROTATION_270);
        addUvRotation(ModelRotation.X180_Y270, EnumFacing.DOWN, UV_ROTATION_270);
        addUvRotation(ModelRotation.X270_Y0, EnumFacing.EAST, UV_ROTATION_270);
        addUvRotation(ModelRotation.X270_Y90, EnumFacing.EAST, UV_ROTATION_270);
        addUvRotation(ModelRotation.X270_Y90, EnumFacing.NORTH, UV_ROTATION_270);
        addUvRotation(ModelRotation.X270_Y90, EnumFacing.SOUTH, UV_ROTATION_270);
        addUvRotation(ModelRotation.X270_Y180, EnumFacing.EAST, UV_ROTATION_270);
        addUvRotation(ModelRotation.X270_Y270, EnumFacing.EAST, UV_ROTATION_270);
        addUvRotation(ModelRotation.X0_Y180, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X0_Y180, EnumFacing.UP, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X90_Y0, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X90_Y0, EnumFacing.UP, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X90_Y90, EnumFacing.UP, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X90_Y180, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X90_Y180, EnumFacing.UP, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X90_Y270, EnumFacing.UP, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y0, EnumFacing.EAST, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y0, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y0, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y0, EnumFacing.WEST, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y90, EnumFacing.EAST, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y90, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y90, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y90, EnumFacing.WEST, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y180, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y180, EnumFacing.EAST, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y180, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y180, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y180, EnumFacing.UP, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y180, EnumFacing.WEST, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y270, EnumFacing.EAST, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y270, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y270, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y270, EnumFacing.WEST, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X270_Y0, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X270_Y0, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X270_Y90, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X270_Y180, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X270_Y180, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X270_Y270, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X0_Y90, EnumFacing.UP, UV_ROTATION_90);
        addUvRotation(ModelRotation.X0_Y270, EnumFacing.DOWN, UV_ROTATION_90);
        addUvRotation(ModelRotation.X90_Y0, EnumFacing.EAST, UV_ROTATION_90);
        addUvRotation(ModelRotation.X90_Y90, EnumFacing.EAST, UV_ROTATION_90);
        addUvRotation(ModelRotation.X90_Y90, EnumFacing.NORTH, UV_ROTATION_90);
        addUvRotation(ModelRotation.X90_Y90, EnumFacing.SOUTH, UV_ROTATION_90);
        addUvRotation(ModelRotation.X90_Y180, EnumFacing.EAST, UV_ROTATION_90);
        addUvRotation(ModelRotation.X90_Y270, EnumFacing.EAST, UV_ROTATION_90);
        addUvRotation(ModelRotation.X270_Y0, EnumFacing.WEST, UV_ROTATION_90);
        addUvRotation(ModelRotation.X180_Y90, EnumFacing.DOWN, UV_ROTATION_90);
        addUvRotation(ModelRotation.X180_Y270, EnumFacing.UP, UV_ROTATION_90);
        addUvRotation(ModelRotation.X270_Y90, EnumFacing.WEST, UV_ROTATION_90);
        addUvRotation(ModelRotation.X270_Y180, EnumFacing.WEST, UV_ROTATION_90);
        addUvRotation(ModelRotation.X270_Y270, EnumFacing.NORTH, UV_ROTATION_90);
        addUvRotation(ModelRotation.X270_Y270, EnumFacing.SOUTH, UV_ROTATION_90);
        addUvRotation(ModelRotation.X270_Y270, EnumFacing.WEST, UV_ROTATION_90);
    }

    @SideOnly(Side.CLIENT)
    abstract static class Rotation
        {
            private Rotation()
            {
            }

            public BlockFaceUV rotateUV(BlockFaceUV blockFaceUVIn)
            {
                float f = blockFaceUVIn.getVertexU(blockFaceUVIn.getVertexRotatedRev(0));
                float f1 = blockFaceUVIn.getVertexV(blockFaceUVIn.getVertexRotatedRev(0));
                float f2 = blockFaceUVIn.getVertexU(blockFaceUVIn.getVertexRotatedRev(2));
                float f3 = blockFaceUVIn.getVertexV(blockFaceUVIn.getVertexRotatedRev(2));
                return this.makeRotatedUV(f, f1, f2, f3);
            }

            abstract BlockFaceUV makeRotatedUV(float u1, float v1, float u2, float v2);
        }
}