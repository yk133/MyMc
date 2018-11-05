package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Quaternion;

@SideOnly(Side.CLIENT)
public class GlStateManager
{
    private static final FloatBuffer BUF_FLOAT_16 = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer BUF_FLOAT_4 = BufferUtils.createFloatBuffer(4);
    private static final GlStateManager.AlphaState field_179160_a = new GlStateManager.AlphaState();
    private static final GlStateManager.BooleanState field_179158_b = new GlStateManager.BooleanState(2896);
    private static final GlStateManager.BooleanState[] field_179159_c = new GlStateManager.BooleanState[8];
    private static final GlStateManager.ColorMaterialState field_179156_d;
    private static final GlStateManager.BlendState BLEND;
    private static final GlStateManager.DepthState DEPTH;
    private static final GlStateManager.FogState FOG;
    private static final GlStateManager.CullState CULL;
    private static final GlStateManager.PolygonOffsetState POLYGON_OFFSET;
    private static final GlStateManager.ColorLogicState COLOR_LOGIC;
    private static final GlStateManager.TexGenState TEX_GEN;
    private static final GlStateManager.ClearState CLEAR;
    private static final GlStateManager.StencilState STENCIL;
    private static final GlStateManager.BooleanState field_179161_n;
    private static int activeTexture;
    private static final GlStateManager.TextureState[] field_179174_p;
    private static int activeShadeModel;
    private static final GlStateManager.BooleanState field_179172_r;
    private static final GlStateManager.ColorMask field_179171_s;
    private static final GlStateManager.Color field_179170_t;

    /**
     * Do not use (see MinecraftForge issue #1637)
     */
    public static void pushLightingAttrib()
    {
        GL11.glPushAttrib(8256);
    }

    /**
     * Do not use (see MinecraftForge issue #1637)
     */
    public static void popAttrib()
    {
        GL11.glPopAttrib();
    }

    public static void disableAlphaTest()
    {
        field_179160_a.test.setDisabled();
    }

    public static void enableAlphaTest()
    {
        field_179160_a.test.setEnabled();
    }

    public static void alphaFunc(int func, float ref)
    {
        if (func != field_179160_a.func || ref != field_179160_a.ref)
        {
            field_179160_a.func = func;
            field_179160_a.ref = ref;
            GL11.glAlphaFunc(func, ref);
        }
    }

    public static void enableLighting()
    {
        field_179158_b.setEnabled();
    }

    public static void disableLighting()
    {
        field_179158_b.setDisabled();
    }

    public static void enableLight(int light)
    {
        field_179159_c[light].setEnabled();
    }

    public static void disableLight(int light)
    {
        field_179159_c[light].setDisabled();
    }

    public static void enableColorMaterial()
    {
        field_179156_d.colorMaterial.setEnabled();
    }

    public static void disableColorMaterial()
    {
        field_179156_d.colorMaterial.setDisabled();
    }

    public static void colorMaterial(int face, int mode)
    {
        if (face != field_179156_d.face || mode != field_179156_d.mode)
        {
            field_179156_d.face = face;
            field_179156_d.mode = mode;
            GL11.glColorMaterial(face, mode);
        }
    }

    public static void lightfv(int light, int pname, FloatBuffer params)
    {
        GL11.glLight(light, pname, params);
    }

    public static void lightModelfv(int pname, FloatBuffer params)
    {
        GL11.glLightModel(pname, params);
    }

    public static void normal3f(float nx, float ny, float nz)
    {
        GL11.glNormal3f(nx, ny, nz);
    }

    public static void disableDepthTest()
    {
        DEPTH.test.setDisabled();
    }

    public static void enableDepthTest()
    {
        DEPTH.test.setEnabled();
    }

    public static void depthFunc(int depthFunc)
    {
        if (depthFunc != DEPTH.func)
        {
            DEPTH.func = depthFunc;
            GL11.glDepthFunc(depthFunc);
        }
    }

    public static void depthMask(boolean flagIn)
    {
        if (flagIn != DEPTH.mask)
        {
            DEPTH.mask = flagIn;
            GL11.glDepthMask(flagIn);
        }
    }

    public static void disableBlend()
    {
        BLEND.blend.setDisabled();
    }

    public static void enableBlend()
    {
        BLEND.blend.setEnabled();
    }

    public static void blendFunc(GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor)
    {
        blendFunc(srcFactor.factor, dstFactor.factor);
    }

    public static void blendFunc(int srcFactor, int dstFactor)
    {
        if (srcFactor != BLEND.srcFactor || dstFactor != BLEND.dstFactor)
        {
            BLEND.srcFactor = srcFactor;
            BLEND.dstFactor = dstFactor;
            GL11.glBlendFunc(srcFactor, dstFactor);
        }
    }

    public static void blendFuncSeparate(GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor, GlStateManager.SourceFactor srcFactorAlpha, GlStateManager.DestFactor dstFactorAlpha)
    {
        blendFuncSeparate(srcFactor.factor, dstFactor.factor, srcFactorAlpha.factor, dstFactorAlpha.factor);
    }

    public static void blendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha)
    {
        if (srcFactor != BLEND.srcFactor || dstFactor != BLEND.dstFactor || srcFactorAlpha != BLEND.srcFactorAlpha || dstFactorAlpha != BLEND.dstFactorAlpha)
        {
            BLEND.srcFactor = srcFactor;
            BLEND.dstFactor = dstFactor;
            BLEND.srcFactorAlpha = srcFactorAlpha;
            BLEND.dstFactorAlpha = dstFactorAlpha;
            OpenGlHelper.glBlendFuncSeparate(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
        }
    }

    public static void blendEquation(int blendEquation)
    {
        GL14.glBlendEquation(blendEquation);
    }

    public static void enableOutlineMode(int color)
    {
        BUF_FLOAT_4.put(0, (float)(color >> 16 & 255) / 255.0F);
        BUF_FLOAT_4.put(1, (float)(color >> 8 & 255) / 255.0F);
        BUF_FLOAT_4.put(2, (float)(color >> 0 & 255) / 255.0F);
        BUF_FLOAT_4.put(3, (float)(color >> 24 & 255) / 255.0F);
        texEnvfv(8960, 8705, BUF_FLOAT_4);
        texEnvi(8960, 8704, 34160);
        texEnvi(8960, 34161, 7681);
        texEnvi(8960, 34176, 34166);
        texEnvi(8960, 34192, 768);
        texEnvi(8960, 34162, 7681);
        texEnvi(8960, 34184, 5890);
        texEnvi(8960, 34200, 770);
    }

    public static void disableOutlineMode()
    {
        texEnvi(8960, 8704, 8448);
        texEnvi(8960, 34161, 8448);
        texEnvi(8960, 34162, 8448);
        texEnvi(8960, 34176, 5890);
        texEnvi(8960, 34184, 5890);
        texEnvi(8960, 34192, 768);
        texEnvi(8960, 34200, 770);
    }

    public static void enableFog()
    {
        FOG.fog.setEnabled();
    }

    public static void disableFog()
    {
        FOG.fog.setDisabled();
    }

    public static void fogMode(GlStateManager.FogMode fogMode)
    {
        fogMode(fogMode.capabilityId);
    }

    private static void fogMode(int param)
    {
        if (param != FOG.mode)
        {
            FOG.mode = param;
            GL11.glFogi(GL11.GL_FOG_MODE, param);
        }
    }

    public static void fogDensity(float param)
    {
        if (param != FOG.density)
        {
            FOG.density = param;
            GL11.glFogf(GL11.GL_FOG_DENSITY, param);
        }
    }

    public static void fogStart(float param)
    {
        if (param != FOG.start)
        {
            FOG.start = param;
            GL11.glFogf(GL11.GL_FOG_START, param);
        }
    }

    public static void fogEnd(float param)
    {
        if (param != FOG.end)
        {
            FOG.end = param;
            GL11.glFogf(GL11.GL_FOG_END, param);
        }
    }

    public static void fogfv(int pname, FloatBuffer param)
    {
        GL11.glFog(pname, param);
    }

    public static void fogi(int pname, int param)
    {
        GL11.glFogi(pname, param);
    }

    public static void enableCull()
    {
        CULL.cullFace.setEnabled();
    }

    public static void disableCull()
    {
        CULL.cullFace.setDisabled();
    }

    public static void cullFace(GlStateManager.CullFace cullFace)
    {
        cullFace(cullFace.mode);
    }

    private static void cullFace(int mode)
    {
        if (mode != CULL.mode)
        {
            CULL.mode = mode;
            GL11.glCullFace(mode);
        }
    }

    public static void polygonMode(int face, int mode)
    {
        GL11.glPolygonMode(face, mode);
    }

    public static void enablePolygonOffset()
    {
        POLYGON_OFFSET.fill.setEnabled();
    }

    public static void disablePolygonOffset()
    {
        POLYGON_OFFSET.fill.setDisabled();
    }

    public static void polygonOffset(float factor, float units)
    {
        if (factor != POLYGON_OFFSET.factor || units != POLYGON_OFFSET.units)
        {
            POLYGON_OFFSET.factor = factor;
            POLYGON_OFFSET.units = units;
            GL11.glPolygonOffset(factor, units);
        }
    }

    public static void enableColorLogic()
    {
        COLOR_LOGIC.colorLogicOp.setEnabled();
    }

    public static void disableColorLogic()
    {
        COLOR_LOGIC.colorLogicOp.setDisabled();
    }

    public static void logicOp(GlStateManager.LogicOp logicOperation)
    {
        logicOp(logicOperation.opcode);
    }

    public static void logicOp(int opcode)
    {
        if (opcode != COLOR_LOGIC.opcode)
        {
            COLOR_LOGIC.opcode = opcode;
            GL11.glLogicOp(opcode);
        }
    }

    public static void enableTexGen(GlStateManager.TexGen texGen)
    {
        texGenCoord(texGen).textureGen.setEnabled();
    }

    public static void disableTexGen(GlStateManager.TexGen texGen)
    {
        texGenCoord(texGen).textureGen.setDisabled();
    }

    public static void texGenMode(GlStateManager.TexGen texGen, int mode)
    {
        GlStateManager.TexGenCoord glstatemanager$texgencoord = texGenCoord(texGen);

        if (mode != glstatemanager$texgencoord.mode)
        {
            glstatemanager$texgencoord.mode = mode;
            GL11.glTexGeni(glstatemanager$texgencoord.coord, GL11.GL_TEXTURE_GEN_MODE, mode);
        }
    }

    public static void texGenParam(GlStateManager.TexGen texGen, int pname, FloatBuffer params)
    {
        GL11.glTexGen(texGenCoord(texGen).coord, pname, params);
    }

    private static GlStateManager.TexGenCoord texGenCoord(GlStateManager.TexGen texGen)
    {
        switch (texGen)
        {
            case S:
                return TEX_GEN.s;
            case T:
                return TEX_GEN.t;
            case R:
                return TEX_GEN.r;
            case Q:
                return TEX_GEN.q;
            default:
                return TEX_GEN.s;
        }
    }

    public static void activeTexture(int texture)
    {
        if (activeTexture != texture - OpenGlHelper.GL_TEXTURE0)
        {
            activeTexture = texture - OpenGlHelper.GL_TEXTURE0;
            OpenGlHelper.glActiveTexture(texture);
        }
    }

    public static void enableTexture2D()
    {
        field_179174_p[activeTexture].texture2DState.setEnabled();
    }

    public static void disableTexture2D()
    {
        field_179174_p[activeTexture].texture2DState.setDisabled();
    }

    public static void texEnvfv(int target, int parameterName, FloatBuffer parameters)
    {
        GL11.glTexEnv(target, parameterName, parameters);
    }

    public static void texEnvi(int target, int parameterName, int parameter)
    {
        GL11.glTexEnvi(target, parameterName, parameter);
    }

    public static void texEnvf(int target, int parameterName, float parameter)
    {
        GL11.glTexEnvf(target, parameterName, parameter);
    }

    public static void texParameterf(int target, int parameterName, float parameter)
    {
        GL11.glTexParameterf(target, parameterName, parameter);
    }

    public static void texParameteri(int target, int parameterName, int parameter)
    {
        GL11.glTexParameteri(target, parameterName, parameter);
    }

    public static int glGetTexLevelParameteri(int target, int level, int parameterName)
    {
        return GL11.glGetTexLevelParameteri(target, level, parameterName);
    }

    public static int generateTexture()
    {
        return GL11.glGenTextures();
    }

    public static void deleteTexture(int texture)
    {
        GL11.glDeleteTextures(texture);

        for (GlStateManager.TextureState glstatemanager$texturestate : field_179174_p)
        {
            if (glstatemanager$texturestate.textureName == texture)
            {
                glstatemanager$texturestate.textureName = -1;
            }
        }
    }

    public static void bindTexture(int texture)
    {
        if (texture != field_179174_p[activeTexture].textureName)
        {
            field_179174_p[activeTexture].textureName = texture;
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        }
    }

    public static void texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, @Nullable IntBuffer pixels)
    {
        GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
    }

    public static void func_187414_b(int p_187414_0_, int p_187414_1_, int p_187414_2_, int p_187414_3_, int p_187414_4_, int p_187414_5_, int p_187414_6_, int p_187414_7_, IntBuffer p_187414_8_)
    {
        GL11.glTexSubImage2D(p_187414_0_, p_187414_1_, p_187414_2_, p_187414_3_, p_187414_4_, p_187414_5_, p_187414_6_, p_187414_7_, p_187414_8_);
    }

    public static void func_187443_a(int p_187443_0_, int p_187443_1_, int p_187443_2_, int p_187443_3_, int p_187443_4_, int p_187443_5_, int p_187443_6_, int p_187443_7_)
    {
        GL11.glCopyTexSubImage2D(p_187443_0_, p_187443_1_, p_187443_2_, p_187443_3_, p_187443_4_, p_187443_5_, p_187443_6_, p_187443_7_);
    }

    public static void func_187433_a(int p_187433_0_, int p_187433_1_, int p_187433_2_, int p_187433_3_, IntBuffer p_187433_4_)
    {
        GL11.glGetTexImage(p_187433_0_, p_187433_1_, p_187433_2_, p_187433_3_, p_187433_4_);
    }

    public static void enableNormalize()
    {
        field_179161_n.setEnabled();
    }

    public static void disableNormalize()
    {
        field_179161_n.setDisabled();
    }

    public static void shadeModel(int mode)
    {
        if (mode != activeShadeModel)
        {
            activeShadeModel = mode;
            GL11.glShadeModel(mode);
        }
    }

    public static void enableRescaleNormal()
    {
        field_179172_r.setEnabled();
    }

    public static void disableRescaleNormal()
    {
        field_179172_r.setDisabled();
    }

    public static void viewport(int x, int y, int width, int height)
    {
        GL11.glViewport(x, y, width, height);
    }

    public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha)
    {
        if (red != field_179171_s.red || green != field_179171_s.green || blue != field_179171_s.blue || alpha != field_179171_s.alpha)
        {
            field_179171_s.red = red;
            field_179171_s.green = green;
            field_179171_s.blue = blue;
            field_179171_s.alpha = alpha;
            GL11.glColorMask(red, green, blue, alpha);
        }
    }

    public static void clearDepth(double depth)
    {
        if (depth != CLEAR.depth)
        {
            CLEAR.depth = depth;
            GL11.glClearDepth(depth);
        }
    }

    public static void clearColor(float red, float green, float blue, float alpha)
    {
        if (red != CLEAR.color.red || green != CLEAR.color.green || blue != CLEAR.color.blue || alpha != CLEAR.color.alpha)
        {
            CLEAR.color.red = red;
            CLEAR.color.green = green;
            CLEAR.color.blue = blue;
            CLEAR.color.alpha = alpha;
            GL11.glClearColor(red, green, blue, alpha);
        }
    }

    public static void clear(int mask)
    {
        GL11.glClear(mask);
    }

    public static void matrixMode(int mode)
    {
        GL11.glMatrixMode(mode);
    }

    public static void loadIdentity()
    {
        GL11.glLoadIdentity();
    }

    public static void pushMatrix()
    {
        GL11.glPushMatrix();
    }

    public static void popMatrix()
    {
        GL11.glPopMatrix();
    }

    public static void getFloatv(int pname, FloatBuffer params)
    {
        GL11.glGetFloat(pname, params);
    }

    public static void ortho(double left, double right, double bottom, double top, double zNear, double zFar)
    {
        GL11.glOrtho(left, right, bottom, top, zNear, zFar);
    }

    public static void rotatef(float angle, float x, float y, float z)
    {
        GL11.glRotatef(angle, x, y, z);
    }

    public static void scalef(float x, float y, float z)
    {
        GL11.glScalef(x, y, z);
    }

    public static void scaled(double x, double y, double z)
    {
        GL11.glScaled(x, y, z);
    }

    public static void translatef(float x, float y, float z)
    {
        GL11.glTranslatef(x, y, z);
    }

    public static void translated(double x, double y, double z)
    {
        GL11.glTranslated(x, y, z);
    }

    public static void multMatrixf(FloatBuffer matrix)
    {
        GL11.glMultMatrix(matrix);
    }

    public static void func_187444_a(Quaternion p_187444_0_)
    {
        multMatrixf(func_187418_a(BUF_FLOAT_16, p_187444_0_));
    }

    public static FloatBuffer func_187418_a(FloatBuffer p_187418_0_, Quaternion p_187418_1_)
    {
        p_187418_0_.clear();
        float f = p_187418_1_.x * p_187418_1_.x;
        float f1 = p_187418_1_.x * p_187418_1_.y;
        float f2 = p_187418_1_.x * p_187418_1_.z;
        float f3 = p_187418_1_.x * p_187418_1_.w;
        float f4 = p_187418_1_.y * p_187418_1_.y;
        float f5 = p_187418_1_.y * p_187418_1_.z;
        float f6 = p_187418_1_.y * p_187418_1_.w;
        float f7 = p_187418_1_.z * p_187418_1_.z;
        float f8 = p_187418_1_.z * p_187418_1_.w;
        p_187418_0_.put(1.0F - 2.0F * (f4 + f7));
        p_187418_0_.put(2.0F * (f1 + f8));
        p_187418_0_.put(2.0F * (f2 - f6));
        p_187418_0_.put(0.0F);
        p_187418_0_.put(2.0F * (f1 - f8));
        p_187418_0_.put(1.0F - 2.0F * (f + f7));
        p_187418_0_.put(2.0F * (f5 + f3));
        p_187418_0_.put(0.0F);
        p_187418_0_.put(2.0F * (f2 + f6));
        p_187418_0_.put(2.0F * (f5 - f3));
        p_187418_0_.put(1.0F - 2.0F * (f + f4));
        p_187418_0_.put(0.0F);
        p_187418_0_.put(0.0F);
        p_187418_0_.put(0.0F);
        p_187418_0_.put(0.0F);
        p_187418_0_.put(1.0F);
        p_187418_0_.rewind();
        return p_187418_0_;
    }

    public static void color4f(float colorRed, float colorGreen, float colorBlue, float colorAlpha)
    {
        if (colorRed != field_179170_t.red || colorGreen != field_179170_t.green || colorBlue != field_179170_t.blue || colorAlpha != field_179170_t.alpha)
        {
            field_179170_t.red = colorRed;
            field_179170_t.green = colorGreen;
            field_179170_t.blue = colorBlue;
            field_179170_t.alpha = colorAlpha;
            GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
        }
    }

    public static void color3f(float colorRed, float colorGreen, float colorBlue)
    {
        color4f(colorRed, colorGreen, colorBlue, 1.0F);
    }

    public static void func_187426_b(float p_187426_0_, float p_187426_1_)
    {
        GL11.glTexCoord2f(p_187426_0_, p_187426_1_);
    }

    public static void func_187435_e(float p_187435_0_, float p_187435_1_, float p_187435_2_)
    {
        GL11.glVertex3f(p_187435_0_, p_187435_1_, p_187435_2_);
    }

    public static void resetColor()
    {
        field_179170_t.red = -1.0F;
        field_179170_t.green = -1.0F;
        field_179170_t.blue = -1.0F;
        field_179170_t.alpha = -1.0F;
    }

    public static void normalPointer(int type, int stride, ByteBuffer buffer)
    {
        GL11.glNormalPointer(type, stride, buffer);
    }

    public static void texCoordPointer(int size, int type, int stride, int buffer_offset)
    {
        GL11.glTexCoordPointer(size, type, stride, (long)buffer_offset);
    }

    public static void texCoordPointer(int size, int type, int stride, ByteBuffer buffer)
    {
        GL11.glTexCoordPointer(size, type, stride, buffer);
    }

    public static void vertexPointer(int size, int type, int stride, int buffer_offset)
    {
        GL11.glVertexPointer(size, type, stride, (long)buffer_offset);
    }

    public static void vertexPointer(int size, int type, int stride, ByteBuffer buffer)
    {
        GL11.glVertexPointer(size, type, stride, buffer);
    }

    public static void colorPointer(int size, int type, int stride, int buffer_offset)
    {
        GL11.glColorPointer(size, type, stride, (long)buffer_offset);
    }

    public static void colorPointer(int size, int type, int stride, ByteBuffer buffer)
    {
        GL11.glColorPointer(size, type, stride, buffer);
    }

    public static void disableClientState(int cap)
    {
        GL11.glDisableClientState(cap);
    }

    public static void enableClientState(int cap)
    {
        GL11.glEnableClientState(cap);
    }

    public static void func_187447_r(int p_187447_0_)
    {
        GL11.glBegin(p_187447_0_);
    }

    public static void func_187437_J()
    {
        GL11.glEnd();
    }

    public static void drawArrays(int mode, int first, int count)
    {
        GL11.glDrawArrays(mode, first, count);
    }

    public static void lineWidth(float width)
    {
        GL11.glLineWidth(width);
    }

    public static void callList(int list)
    {
        GL11.glCallList(list);
    }

    public static void deleteLists(int list, int range)
    {
        GL11.glDeleteLists(list, range);
    }

    public static void newList(int list, int mode)
    {
        GL11.glNewList(list, mode);
    }

    public static void endList()
    {
        GL11.glEndList();
    }

    public static int genLists(int range)
    {
        return GL11.glGenLists(range);
    }

    public static void pixelStorei(int parameterName, int param)
    {
        GL11.glPixelStorei(parameterName, param);
    }

    public static void func_187413_a(int p_187413_0_, int p_187413_1_, int p_187413_2_, int p_187413_3_, int p_187413_4_, int p_187413_5_, IntBuffer p_187413_6_)
    {
        GL11.glReadPixels(p_187413_0_, p_187413_1_, p_187413_2_, p_187413_3_, p_187413_4_, p_187413_5_, p_187413_6_);
    }

    public static int getError()
    {
        return GL11.glGetError();
    }

    public static String getString(int name)
    {
        return GL11.glGetString(name);
    }

    public static void func_187445_a(int p_187445_0_, IntBuffer p_187445_1_)
    {
        GL11.glGetInteger(p_187445_0_, p_187445_1_);
    }

    public static int func_187397_v(int p_187397_0_)
    {
        return GL11.glGetInteger(p_187397_0_);
    }

    public static void enableBlendProfile(GlStateManager.Profile profile)
    {
        profile.apply();
    }

    public static void disableBlendProfile(GlStateManager.Profile profile)
    {
        profile.clean();
    }

    static
    {
        for (int i = 0; i < 8; ++i)
        {
            field_179159_c[i] = new GlStateManager.BooleanState(16384 + i);
        }

        field_179156_d = new GlStateManager.ColorMaterialState();
        BLEND = new GlStateManager.BlendState();
        DEPTH = new GlStateManager.DepthState();
        FOG = new GlStateManager.FogState();
        CULL = new GlStateManager.CullState();
        POLYGON_OFFSET = new GlStateManager.PolygonOffsetState();
        COLOR_LOGIC = new GlStateManager.ColorLogicState();
        TEX_GEN = new GlStateManager.TexGenState();
        CLEAR = new GlStateManager.ClearState();
        STENCIL = new GlStateManager.StencilState();
        field_179161_n = new GlStateManager.BooleanState(2977);
        field_179174_p = new GlStateManager.TextureState[8];

        for (int j = 0; j < 8; ++j)
        {
            field_179174_p[j] = new GlStateManager.TextureState();
        }

        activeShadeModel = 7425;
        field_179172_r = new GlStateManager.BooleanState(32826);
        field_179171_s = new GlStateManager.ColorMask();
        field_179170_t = new GlStateManager.Color();
    }

    @SideOnly(Side.CLIENT)
    static class AlphaState
        {
            public GlStateManager.BooleanState test;
            public int func;
            public float ref;

            private AlphaState()
            {
                this.test = new GlStateManager.BooleanState(3008);
                this.func = 519;
                this.ref = -1.0F;
            }
        }

    @SideOnly(Side.CLIENT)
    static class BlendState
        {
            public GlStateManager.BooleanState blend;
            public int srcFactor;
            public int dstFactor;
            public int srcFactorAlpha;
            public int dstFactorAlpha;

            private BlendState()
            {
                this.blend = new GlStateManager.BooleanState(3042);
                this.srcFactor = 1;
                this.dstFactor = 0;
                this.srcFactorAlpha = 1;
                this.dstFactorAlpha = 0;
            }
        }

    @SideOnly(Side.CLIENT)
    static class BooleanState
        {
            private final int capability;
            private boolean currentState;

            public BooleanState(int capabilityIn)
            {
                this.capability = capabilityIn;
            }

            public void setDisabled()
            {
                this.setState(false);
            }

            public void setEnabled()
            {
                this.setState(true);
            }

            public void setState(boolean state)
            {
                if (state != this.currentState)
                {
                    this.currentState = state;

                    if (state)
                    {
                        GL11.glEnable(this.capability);
                    }
                    else
                    {
                        GL11.glDisable(this.capability);
                    }
                }
            }
        }

    @SideOnly(Side.CLIENT)
    static class ClearState
        {
            public double depth;
            public GlStateManager.Color color;

            private ClearState()
            {
                this.depth = 1.0D;
                this.color = new GlStateManager.Color(0.0F, 0.0F, 0.0F, 0.0F);
            }
        }

    @SideOnly(Side.CLIENT)
    static class Color
        {
            public float red;
            public float green;
            public float blue;
            public float alpha;

            public Color()
            {
                this(1.0F, 1.0F, 1.0F, 1.0F);
            }

            public Color(float redIn, float greenIn, float blueIn, float alphaIn)
            {
                this.red = 1.0F;
                this.green = 1.0F;
                this.blue = 1.0F;
                this.alpha = 1.0F;
                this.red = redIn;
                this.green = greenIn;
                this.blue = blueIn;
                this.alpha = alphaIn;
            }
        }

    @SideOnly(Side.CLIENT)
    static class ColorLogicState
        {
            public GlStateManager.BooleanState colorLogicOp;
            public int opcode;

            private ColorLogicState()
            {
                this.colorLogicOp = new GlStateManager.BooleanState(3058);
                this.opcode = 5379;
            }
        }

    @SideOnly(Side.CLIENT)
    static class ColorMask
        {
            public boolean red;
            public boolean green;
            public boolean blue;
            public boolean alpha;

            private ColorMask()
            {
                this.red = true;
                this.green = true;
                this.blue = true;
                this.alpha = true;
            }
        }

    @SideOnly(Side.CLIENT)
    static class ColorMaterialState
        {
            public GlStateManager.BooleanState colorMaterial;
            public int face;
            public int mode;

            private ColorMaterialState()
            {
                this.colorMaterial = new GlStateManager.BooleanState(2903);
                this.face = 1032;
                this.mode = 5634;
            }
        }

    @SideOnly(Side.CLIENT)
    public static enum CullFace
    {
        FRONT(1028),
        BACK(1029),
        FRONT_AND_BACK(1032);

        public final int mode;

        private CullFace(int modeIn)
        {
            this.mode = modeIn;
        }
    }

    @SideOnly(Side.CLIENT)
    static class CullState
        {
            public GlStateManager.BooleanState cullFace;
            public int mode;

            private CullState()
            {
                this.cullFace = new GlStateManager.BooleanState(2884);
                this.mode = 1029;
            }
        }

    @SideOnly(Side.CLIENT)
    static class DepthState
        {
            public GlStateManager.BooleanState test;
            public boolean mask;
            public int func;

            private DepthState()
            {
                this.test = new GlStateManager.BooleanState(2929);
                this.mask = true;
                this.func = 513;
            }
        }

    @SideOnly(Side.CLIENT)
    public static enum DestFactor
    {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_COLOR(768),
        ZERO(0);

        public final int factor;

        private DestFactor(int factorIn)
        {
            this.factor = factorIn;
        }
    }

    @SideOnly(Side.CLIENT)
    public static enum FogMode
    {
        LINEAR(9729),
        EXP(2048),
        EXP2(2049);

        /** The capability ID of this {@link FogMode} */
        public final int capabilityId;

        private FogMode(int capabilityIn)
        {
            this.capabilityId = capabilityIn;
        }
    }

    @SideOnly(Side.CLIENT)
    static class FogState
        {
            public GlStateManager.BooleanState fog;
            public int mode;
            public float density;
            public float start;
            public float end;

            private FogState()
            {
                this.fog = new GlStateManager.BooleanState(2912);
                this.mode = 2048;
                this.density = 1.0F;
                this.end = 1.0F;
            }
        }

    @SideOnly(Side.CLIENT)
    public static enum LogicOp
    {
        AND(5377),
        AND_INVERTED(5380),
        AND_REVERSE(5378),
        CLEAR(5376),
        COPY(5379),
        COPY_INVERTED(5388),
        EQUIV(5385),
        INVERT(5386),
        NAND(5390),
        NOOP(5381),
        NOR(5384),
        OR(5383),
        OR_INVERTED(5389),
        OR_REVERSE(5387),
        SET(5391),
        XOR(5382);

        public final int opcode;

        private LogicOp(int opcodeIn)
        {
            this.opcode = opcodeIn;
        }
    }

    @SideOnly(Side.CLIENT)
    static class PolygonOffsetState
        {
            public GlStateManager.BooleanState fill;
            public GlStateManager.BooleanState line;
            public float factor;
            public float units;

            private PolygonOffsetState()
            {
                this.fill = new GlStateManager.BooleanState(32823);
                this.line = new GlStateManager.BooleanState(10754);
            }
        }

    @SideOnly(Side.CLIENT)
    public static enum Profile
    {
        DEFAULT {
            public void apply()
            {
                GlStateManager.disableAlphaTest();
                GlStateManager.alphaFunc(519, 0.0F);
                GlStateManager.disableLighting();
                GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, RenderHelper.setColorBuffer(0.2F, 0.2F, 0.2F, 1.0F));

                for (int i = 0; i < 8; ++i)
                {
                    GlStateManager.disableLight(i);
                    GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_POSITION, RenderHelper.setColorBuffer(0.0F, 0.0F, 1.0F, 0.0F));

                    if (i == 0)
                    {
                        GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(1.0F, 1.0F, 1.0F, 1.0F));
                        GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(1.0F, 1.0F, 1.0F, 1.0F));
                    }
                    else
                    {
                        GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                        GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    }
                }

                GlStateManager.disableColorMaterial();
                GlStateManager.colorMaterial(1032, 5634);
                GlStateManager.disableDepthTest();
                GlStateManager.depthFunc(513);
                GlStateManager.depthMask(true);
                GlStateManager.disableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GL14.glBlendEquation(GL14.GL_FUNC_ADD);
                GlStateManager.disableFog();
                GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
                GlStateManager.fogDensity(1.0F);
                GlStateManager.fogStart(0.0F);
                GlStateManager.fogEnd(1.0F);
                GL11.glFog(GL11.GL_FOG_COLOR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));

                if (GLContext.getCapabilities().GL_NV_fog_distance)
                {
                    GL11.glFogi(GL11.GL_FOG_MODE, 34140);
                }

                GlStateManager.polygonOffset(0.0F, 0.0F);
                GlStateManager.disableColorLogic();
                GlStateManager.logicOp(5379);
                GlStateManager.disableTexGen(GlStateManager.TexGen.S);
                GlStateManager.texGenMode(GlStateManager.TexGen.S, 9216);
                GlStateManager.texGenParam(GlStateManager.TexGen.S, 9474, RenderHelper.setColorBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                GlStateManager.texGenParam(GlStateManager.TexGen.S, 9217, RenderHelper.setColorBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                GlStateManager.disableTexGen(GlStateManager.TexGen.T);
                GlStateManager.texGenMode(GlStateManager.TexGen.T, 9216);
                GlStateManager.texGenParam(GlStateManager.TexGen.T, 9474, RenderHelper.setColorBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                GlStateManager.texGenParam(GlStateManager.TexGen.T, 9217, RenderHelper.setColorBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                GlStateManager.disableTexGen(GlStateManager.TexGen.R);
                GlStateManager.texGenMode(GlStateManager.TexGen.R, 9216);
                GlStateManager.texGenParam(GlStateManager.TexGen.R, 9474, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
                GlStateManager.texGenParam(GlStateManager.TexGen.R, 9217, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
                GlStateManager.disableTexGen(GlStateManager.TexGen.Q);
                GlStateManager.texGenMode(GlStateManager.TexGen.Q, 9216);
                GlStateManager.texGenParam(GlStateManager.TexGen.Q, 9474, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
                GlStateManager.texGenParam(GlStateManager.TexGen.Q, 9217, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
                GlStateManager.activeTexture(0);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 1000);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, 1000);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MIN_LOD, -1000);
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.0F);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
                GL11.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL11.GL_MODULATE);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC0_RGB, GL11.GL_TEXTURE);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC1_RGB, GL13.GL_PREVIOUS);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC2_RGB, GL13.GL_CONSTANT);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC0_ALPHA, GL11.GL_TEXTURE);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC1_ALPHA, GL13.GL_PREVIOUS);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC2_ALPHA, GL13.GL_CONSTANT);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_RGB, GL11.GL_SRC_ALPHA);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_ALPHA, GL11.GL_SRC_ALPHA);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_ALPHA, GL11.GL_SRC_ALPHA);
                GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL13.GL_RGB_SCALE, 1.0F);
                GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_ALPHA_SCALE, 1.0F);
                GlStateManager.disableNormalize();
                GlStateManager.shadeModel(7425);
                GlStateManager.disableRescaleNormal();
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.clearDepth(1.0D);
                GL11.glLineWidth(1.0F);
                GL11.glNormal3f(0.0F, 0.0F, 1.0F);
                GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
                GL11.glPolygonMode(GL11.GL_BACK, GL11.GL_FILL);
            }

            public void clean()
            {
            }
        },
        PLAYER_SKIN {
            public void apply()
            {
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            }

            public void clean()
            {
                GlStateManager.disableBlend();
            }
        },
        TRANSPARENT_MODEL {
            public void apply()
            {
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.15F);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.alphaFunc(516, 0.003921569F);
            }

            public void clean()
            {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.depthMask(true);
            }
        };

        private Profile()
        {
        }

        public abstract void apply();

        public abstract void clean();
    }

    @SideOnly(Side.CLIENT)
    public static enum SourceFactor
    {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_ALPHA_SATURATE(776),
        SRC_COLOR(768),
        ZERO(0);

        public final int factor;

        private SourceFactor(int factorIn)
        {
            this.factor = factorIn;
        }
    }

    @SideOnly(Side.CLIENT)
    static class StencilFunc
        {
            public int func;
            public int mask;

            private StencilFunc()
            {
                this.func = 519;
                this.mask = -1;
            }
        }

    @SideOnly(Side.CLIENT)
    static class StencilState
        {
            public GlStateManager.StencilFunc func;
            public int mask;
            public int fail;
            public int zfail;
            public int zpass;

            private StencilState()
            {
                this.func = new GlStateManager.StencilFunc();
                this.mask = -1;
                this.fail = 7680;
                this.zfail = 7680;
                this.zpass = 7680;
            }
        }

    @SideOnly(Side.CLIENT)
    public static enum TexGen
    {
        S,
        T,
        R,
        Q;
    }

    @SideOnly(Side.CLIENT)
    static class TexGenCoord
        {
            public GlStateManager.BooleanState textureGen;
            public int coord;
            public int mode = -1;

            public TexGenCoord(int coordIn, int capabilityIn)
            {
                this.coord = coordIn;
                this.textureGen = new GlStateManager.BooleanState(capabilityIn);
            }
        }

    @SideOnly(Side.CLIENT)
    static class TexGenState
        {
            public GlStateManager.TexGenCoord s;
            public GlStateManager.TexGenCoord t;
            public GlStateManager.TexGenCoord r;
            public GlStateManager.TexGenCoord q;

            private TexGenState()
            {
                this.s = new GlStateManager.TexGenCoord(8192, 3168);
                this.t = new GlStateManager.TexGenCoord(8193, 3169);
                this.r = new GlStateManager.TexGenCoord(8194, 3170);
                this.q = new GlStateManager.TexGenCoord(8195, 3171);
            }
        }

    @SideOnly(Side.CLIENT)
    static class TextureState
        {
            public GlStateManager.BooleanState texture2DState;
            public int textureName;

            private TextureState()
            {
                this.texture2DState = new GlStateManager.BooleanState(3553);
            }
        }
}