package net.minecraft.client.renderer;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.gson.JsonSyntaxException;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

@SideOnly(Side.CLIENT)
public class EntityRenderer implements IResourceManagerReloadListener
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation RAIN_TEXTURES = new ResourceLocation("textures/environment/rain.png");
    private static final ResourceLocation SNOW_TEXTURES = new ResourceLocation("textures/environment/snow.png");
    public static boolean field_78517_a;
    public static int field_78515_b;
    /** A reference to the Minecraft object. */
    private final Minecraft mc;
    private final IResourceManager resourceManager;
    private final Random random = new Random();
    private float farPlaneDistance;
    public final ItemRenderer itemRenderer;
    private final MapItemRenderer mapItemRenderer;
    /** Entity renderer update count */
    private int rendererUpdateCount;
    /** Pointed entity */
    private Entity pointedEntity;
    private final MouseFilter field_78527_v = new MouseFilter();
    private final MouseFilter field_78526_w = new MouseFilter();
    private final float thirdPersonDistance = 4.0F;
    /** Previous third person distance */
    private float thirdPersonDistancePrev = 4.0F;
    private float field_78496_H;
    private float field_78497_I;
    private float field_78498_J;
    private float field_78499_K;
    private float field_78492_L;
    /** FOV modifier hand */
    private float fovModifierHand;
    /** FOV modifier hand prev */
    private float fovModifierHandPrev;
    private float bossColorModifier;
    private float bossColorModifierPrev;
    private boolean field_78500_U;
    private boolean renderHand = true;
    private boolean drawBlockOutline = true;
    private long timeWorldIcon;
    /** Previous frame time in milliseconds */
    private long prevFrameTime = Minecraft.func_71386_F();
    private long field_78510_Z;
    /** The texture id of the blocklight/skylight texture used for lighting effects */
    private final DynamicTexture lightmapTexture;
    private final int[] field_78504_Q;
    private final ResourceLocation field_110922_T;
    private boolean field_78536_aa;
    private float field_78514_e;
    private float field_175075_L;
    /** Rain sound counter */
    private int rainSoundCounter;
    private final float[] rainXCoords = new float[1024];
    private final float[] rainYCoords = new float[1024];
    private final FloatBuffer field_78521_m = GLAllocation.createDirectFloatBuffer(16);
    private float field_175080_Q;
    private float field_175082_R;
    private float field_175081_S;
    private float field_78535_ad;
    private float field_78539_ae;
    private int field_175079_V;
    private boolean debugView;
    private double cameraZoom = 1.0D;
    private double cameraYaw;
    private double cameraPitch;
    private ItemStack itemActivationItem;
    private int itemActivationTicks;
    private float itemActivationOffX;
    private float itemActivationOffY;
    private ShaderGroup shaderGroup;
    private static final ResourceLocation[] SHADERS_TEXTURES = new ResourceLocation[] {new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json"), new ResourceLocation("shaders/post/creeper.json"), new ResourceLocation("shaders/post/spider.json")};
    public static final int SHADER_COUNT = SHADERS_TEXTURES.length;
    private int shaderIndex;
    private boolean useShader;
    private int frameCount;

    public EntityRenderer(Minecraft mcIn, IResourceManager resourceManagerIn)
    {
        this.shaderIndex = SHADER_COUNT;
        this.mc = mcIn;
        this.resourceManager = resourceManagerIn;
        this.itemRenderer = mcIn.getFirstPersonRenderer();
        this.mapItemRenderer = new MapItemRenderer(mcIn.getTextureManager());
        this.lightmapTexture = new DynamicTexture(16, 16);
        this.field_110922_T = mcIn.getTextureManager().getDynamicTextureLocation("lightMap", this.lightmapTexture);
        this.field_78504_Q = this.lightmapTexture.func_110565_c();
        this.shaderGroup = null;

        for (int i = 0; i < 32; ++i)
        {
            for (int j = 0; j < 32; ++j)
            {
                float f = (float)(j - 16);
                float f1 = (float)(i - 16);
                float f2 = MathHelper.sqrt(f * f + f1 * f1);
                this.rainXCoords[i << 5 | j] = -f1 / f2;
                this.rainYCoords[i << 5 | j] = f / f2;
            }
        }
    }

    public boolean isShaderActive()
    {
        return OpenGlHelper.shadersSupported && this.shaderGroup != null;
    }

    public void stopUseShader()
    {
        if (this.shaderGroup != null)
        {
            this.shaderGroup.func_148021_a();
        }

        this.shaderGroup = null;
        this.shaderIndex = SHADER_COUNT;
    }

    public void switchUseShader()
    {
        this.useShader = !this.useShader;
    }

    /**
     * What shader to use when spectating this entity
     */
    public void loadEntityShader(@Nullable Entity entityIn)
    {
        if (OpenGlHelper.shadersSupported)
        {
            if (this.shaderGroup != null)
            {
                this.shaderGroup.func_148021_a();
            }

            this.shaderGroup = null;

            if (entityIn instanceof EntityCreeper)
            {
                this.loadShader(new ResourceLocation("shaders/post/creeper.json"));
            }
            else if (entityIn instanceof EntitySpider)
            {
                this.loadShader(new ResourceLocation("shaders/post/spider.json"));
            }
            else if (entityIn instanceof EntityEnderman)
            {
                this.loadShader(new ResourceLocation("shaders/post/invert.json"));
            }
            else net.minecraftforge.client.ForgeHooksClient.loadEntityShader(entityIn, this);
        }
    }

    public void loadShader(ResourceLocation resourceLocationIn)
    {
        try
        {
            this.shaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), resourceLocationIn);
            this.shaderGroup.createBindFramebuffers(this.mc.field_71443_c, this.mc.field_71440_d);
            this.useShader = true;
        }
        catch (IOException ioexception)
        {
            LOGGER.warn("Failed to load shader: {}", resourceLocationIn, ioexception);
            this.shaderIndex = SHADER_COUNT;
            this.useShader = false;
        }
        catch (JsonSyntaxException jsonsyntaxexception)
        {
            LOGGER.warn("Failed to load shader: {}", resourceLocationIn, jsonsyntaxexception);
            this.shaderIndex = SHADER_COUNT;
            this.useShader = false;
        }
    }

    public void func_110549_a(IResourceManager p_110549_1_)
    {
        if (this.shaderGroup != null)
        {
            this.shaderGroup.func_148021_a();
        }

        this.shaderGroup = null;

        if (this.shaderIndex == SHADER_COUNT)
        {
            this.loadEntityShader(this.mc.getRenderViewEntity());
        }
        else
        {
            this.loadShader(SHADERS_TEXTURES[this.shaderIndex]);
        }
    }

    /**
     * Updates the entity renderer
     */
    public void tick()
    {
        if (OpenGlHelper.shadersSupported && ShaderLinkHelper.getStaticShaderLinkHelper() == null)
        {
            ShaderLinkHelper.setNewStaticShaderLinkHelper();
        }

        this.updateFovModifierHand();
        this.func_78470_f();
        this.field_78535_ad = this.field_78539_ae;
        this.thirdPersonDistancePrev = 4.0F;

        if (this.mc.gameSettings.smoothCamera)
        {
            float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f1 = f * f * f * 8.0F;
            this.field_78498_J = this.field_78527_v.func_76333_a(this.field_78496_H, 0.05F * f1);
            this.field_78499_K = this.field_78526_w.func_76333_a(this.field_78497_I, 0.05F * f1);
            this.field_78492_L = 0.0F;
            this.field_78496_H = 0.0F;
            this.field_78497_I = 0.0F;
        }
        else
        {
            this.field_78498_J = 0.0F;
            this.field_78499_K = 0.0F;
            this.field_78527_v.func_180179_a();
            this.field_78526_w.func_180179_a();
        }

        if (this.mc.getRenderViewEntity() == null)
        {
            this.mc.setRenderViewEntity(this.mc.player);
        }

        float f3 = this.mc.world.func_175724_o(new BlockPos(this.mc.getRenderViewEntity().getEyePosition(1F))); // Forge: fix MC-51150
        float f4 = (float)this.mc.gameSettings.renderDistanceChunks / 32.0F;
        float f2 = f3 * (1.0F - f4) + f4;
        this.field_78539_ae += (f2 - this.field_78539_ae) * 0.1F;
        ++this.rendererUpdateCount;
        this.itemRenderer.tick();
        this.addRainParticles();
        this.bossColorModifierPrev = this.bossColorModifier;

        if (this.mc.ingameGUI.getBossOverlay().shouldDarkenSky())
        {
            this.bossColorModifier += 0.05F;

            if (this.bossColorModifier > 1.0F)
            {
                this.bossColorModifier = 1.0F;
            }
        }
        else if (this.bossColorModifier > 0.0F)
        {
            this.bossColorModifier -= 0.0125F;
        }

        if (this.itemActivationTicks > 0)
        {
            --this.itemActivationTicks;

            if (this.itemActivationTicks == 0)
            {
                this.itemActivationItem = null;
            }
        }
    }

    public ShaderGroup getShaderGroup()
    {
        return this.shaderGroup;
    }

    public void updateShaderGroupSize(int width, int height)
    {
        if (OpenGlHelper.shadersSupported)
        {
            if (this.shaderGroup != null)
            {
                this.shaderGroup.createBindFramebuffers(width, height);
            }

            this.mc.renderGlobal.createBindEntityOutlineFbs(width, height);
        }
    }

    /**
     * Gets the block or object that is being moused over.
     */
    public void getMouseOver(float partialTicks)
    {
        Entity entity = this.mc.getRenderViewEntity();

        if (entity != null)
        {
            if (this.mc.world != null)
            {
                this.mc.profiler.startSection("pick");
                this.mc.pointedEntity = null;
                double d0 = (double)this.mc.playerController.getBlockReachDistance();
                this.mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
                Vec3d vec3d = entity.getEyePosition(partialTicks);
                boolean flag = false;
                int i = 3;
                double d1 = d0;

                if (this.mc.playerController.extendedReach())
                {
                    d1 = 6.0D;
                    d0 = d1;
                }
                else
                {
                    if (d0 > 3.0D)
                    {
                        flag = true;
                    }
                }

                if (this.mc.objectMouseOver != null)
                {
                    d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3d);
                }

                Vec3d vec3d1 = entity.getLook(1.0F);
                Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
                this.pointedEntity = null;
                Vec3d vec3d3 = null;
                float f = 1.0F;
                List<Entity> list = this.mc.world.getEntitiesInAABBexcluding(entity, entity.getBoundingBox().expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
                {
                    public boolean apply(@Nullable Entity p_apply_1_)
                    {
                        return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
                    }
                }));
                double d2 = d1;

                for (int j = 0; j < list.size(); ++j)
                {
                    Entity entity1 = list.get(j);
                    AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)entity1.getCollisionBorderSize());
                    RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);

                    if (axisalignedbb.contains(vec3d))
                    {
                        if (d2 >= 0.0D)
                        {
                            this.pointedEntity = entity1;
                            vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                            d2 = 0.0D;
                        }
                    }
                    else if (raytraceresult != null)
                    {
                        double d3 = vec3d.distanceTo(raytraceresult.hitVec);

                        if (d3 < d2 || d2 == 0.0D)
                        {
                            if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity1.canRiderInteract())
                            {
                                if (d2 == 0.0D)
                                {
                                    this.pointedEntity = entity1;
                                    vec3d3 = raytraceresult.hitVec;
                                }
                            }
                            else
                            {
                                this.pointedEntity = entity1;
                                vec3d3 = raytraceresult.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }

                if (this.pointedEntity != null && flag && vec3d.distanceTo(vec3d3) > 3.0D)
                {
                    this.pointedEntity = null;
                    this.mc.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, (EnumFacing)null, new BlockPos(vec3d3));
                }

                if (this.pointedEntity != null && (d2 < d1 || this.mc.objectMouseOver == null))
                {
                    this.mc.objectMouseOver = new RayTraceResult(this.pointedEntity, vec3d3);

                    if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame)
                    {
                        this.mc.pointedEntity = this.pointedEntity;
                    }
                }

                this.mc.profiler.endSection();
            }
        }
    }

    /**
     * Update FOV modifier hand
     */
    private void updateFovModifierHand()
    {
        float f = 1.0F;

        if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayer)
        {
            AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)this.mc.getRenderViewEntity();
            f = abstractclientplayer.getFovModifier();
        }

        this.fovModifierHandPrev = this.fovModifierHand;
        this.fovModifierHand += (f - this.fovModifierHand) * 0.5F;

        if (this.fovModifierHand > 1.5F)
        {
            this.fovModifierHand = 1.5F;
        }

        if (this.fovModifierHand < 0.1F)
        {
            this.fovModifierHand = 0.1F;
        }
    }

    private float func_78481_a(float p_78481_1_, boolean p_78481_2_)
    {
        if (this.debugView)
        {
            return 90.0F;
        }
        else
        {
            Entity entity = this.mc.getRenderViewEntity();
            float f = 70.0F;

            if (p_78481_2_)
            {
                f = this.mc.gameSettings.fovSetting;
                f = f * (this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * p_78481_1_);
            }

            if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getHealth() <= 0.0F)
            {
                float f1 = (float)((EntityLivingBase)entity).deathTime + p_78481_1_;
                f /= (1.0F - 500.0F / (f1 + 500.0F)) * 2.0F + 1.0F;
            }

            IBlockState iblockstate = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, p_78481_1_);

            if (iblockstate.getMaterial() == Material.WATER)
            {
                f = f * 60.0F / 70.0F;
            }

            return net.minecraftforge.client.ForgeHooksClient.getFOVModifier(this, entity, iblockstate, p_78481_1_, f);
        }
    }

    private void hurtCameraEffect(float partialTicks)
    {
        if (this.mc.getRenderViewEntity() instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)this.mc.getRenderViewEntity();
            float f = (float)entitylivingbase.hurtTime - partialTicks;

            if (entitylivingbase.getHealth() <= 0.0F)
            {
                float f1 = (float)entitylivingbase.deathTime + partialTicks;
                GlStateManager.rotatef(40.0F - 8000.0F / (f1 + 200.0F), 0.0F, 0.0F, 1.0F);
            }

            if (f < 0.0F)
            {
                return;
            }

            f = f / (float)entitylivingbase.maxHurtTime;
            f = MathHelper.sin(f * f * f * f * (float)Math.PI);
            float f2 = entitylivingbase.attackedAtYaw;
            GlStateManager.rotatef(-f2, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(-f * 14.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(f2, 0.0F, 1.0F, 0.0F);
        }
    }

    /**
     * Updates the bobbing render effect of the player.
     */
    private void applyBobbing(float partialTicks)
    {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
            float f = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
            float f1 = -(entityplayer.distanceWalkedModified + f * partialTicks);
            float f2 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * partialTicks;
            float f3 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * partialTicks;
            GlStateManager.translatef(MathHelper.sin(f1 * (float)Math.PI) * f2 * 0.5F, -Math.abs(MathHelper.cos(f1 * (float)Math.PI) * f2), 0.0F);
            GlStateManager.rotatef(MathHelper.sin(f1 * (float)Math.PI) * f2 * 3.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(Math.abs(MathHelper.cos(f1 * (float)Math.PI - 0.2F) * f2) * 5.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(f3, 1.0F, 0.0F, 0.0F);
        }
    }

    /**
     * sets up player's eye (or camera in third person mode)
     */
    private void orientCamera(float partialTicks)
    {
        Entity entity = this.mc.getRenderViewEntity();
        float f = entity.getEyeHeight();
        double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
        double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + (double)f;
        double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;

        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping())
        {
            f = (float)((double)f + 1.0D);
            GlStateManager.translatef(0.0F, 0.3F, 0.0F);

            if (!this.mc.gameSettings.debugCamEnable)
            {
                BlockPos blockpos = new BlockPos(entity);
                IBlockState iblockstate = this.mc.world.getBlockState(blockpos);
                net.minecraftforge.client.ForgeHooksClient.orientBedCamera(this.mc.world, blockpos, iblockstate, entity);

                GlStateManager.rotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, -1.0F, 0.0F);
                GlStateManager.rotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
            }
        }
        else if (this.mc.gameSettings.thirdPersonView > 0)
        {
            double d3 = (double)(this.thirdPersonDistancePrev + (4.0F - this.thirdPersonDistancePrev) * partialTicks);

            if (this.mc.gameSettings.debugCamEnable)
            {
                GlStateManager.translatef(0.0F, 0.0F, (float)(-d3));
            }
            else
            {
                float f1 = entity.rotationYaw;
                float f2 = entity.rotationPitch;

                if (this.mc.gameSettings.thirdPersonView == 2)
                {
                    f2 += 180.0F;
                }

                double d4 = (double)(-MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
                double d5 = (double)(MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
                double d6 = (double)(-MathHelper.sin(f2 * 0.017453292F)) * d3;

                for (int i = 0; i < 8; ++i)
                {
                    float f3 = (float)((i & 1) * 2 - 1);
                    float f4 = (float)((i >> 1 & 1) * 2 - 1);
                    float f5 = (float)((i >> 2 & 1) * 2 - 1);
                    f3 = f3 * 0.1F;
                    f4 = f4 * 0.1F;
                    f5 = f5 * 0.1F;
                    RayTraceResult raytraceresult = this.mc.world.rayTraceBlocks(new Vec3d(d0 + (double)f3, d1 + (double)f4, d2 + (double)f5), new Vec3d(d0 - d4 + (double)f3 + (double)f5, d1 - d6 + (double)f4, d2 - d5 + (double)f5));

                    if (raytraceresult != null)
                    {
                        double d7 = raytraceresult.hitVec.distanceTo(new Vec3d(d0, d1, d2));

                        if (d7 < d3)
                        {
                            d3 = d7;
                        }
                    }
                }

                if (this.mc.gameSettings.thirdPersonView == 2)
                {
                    GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.rotatef(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotatef(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
                GlStateManager.translatef(0.0F, 0.0F, (float)(-d3));
                GlStateManager.rotatef(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
            }
        }
        else
        {
            GlStateManager.translatef(0.0F, 0.0F, 0.05F);
        }

        if (!this.mc.gameSettings.debugCamEnable)
        {
            float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F;
            float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            float roll = 0.0F;
            if (entity instanceof EntityAnimal)
            {
                EntityAnimal entityanimal = (EntityAnimal)entity;
                yaw = entityanimal.prevRotationYawHead + (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0F;
            }
            IBlockState state = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks);
            net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup event = new net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup(this, entity, state, partialTicks, yaw, pitch, roll);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
            GlStateManager.rotatef(event.getRoll(), 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(event.getPitch(), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(event.getYaw(), 0.0F, 1.0F, 0.0F);
        }

        GlStateManager.translatef(0.0F, -f, 0.0F);
        d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
        d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + (double)f;
        d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
        this.field_78500_U = this.mc.renderGlobal.func_72721_a(d0, d1, d2, partialTicks);
    }

    private void func_78479_a(float p_78479_1_, int p_78479_2_)
    {
        this.farPlaneDistance = (float)(this.mc.gameSettings.renderDistanceChunks * 16);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        float f = 0.07F;

        if (this.mc.gameSettings.field_74337_g)
        {
            GlStateManager.translatef((float)(-(p_78479_2_ * 2 - 1)) * 0.07F, 0.0F, 0.0F);
        }

        if (this.cameraZoom != 1.0D)
        {
            GlStateManager.translatef((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
            GlStateManager.scaled(this.cameraZoom, this.cameraZoom, 1.0D);
        }

        Project.gluPerspective(this.func_78481_a(p_78479_1_, true), (float)this.mc.field_71443_c / (float)this.mc.field_71440_d, 0.05F, this.farPlaneDistance * MathHelper.SQRT_2);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();

        if (this.mc.gameSettings.field_74337_g)
        {
            GlStateManager.translatef((float)(p_78479_2_ * 2 - 1) * 0.1F, 0.0F, 0.0F);
        }

        this.hurtCameraEffect(p_78479_1_);

        if (this.mc.gameSettings.viewBobbing)
        {
            this.applyBobbing(p_78479_1_);
        }

        float f1 = this.mc.player.prevTimeInPortal + (this.mc.player.timeInPortal - this.mc.player.prevTimeInPortal) * p_78479_1_;

        if (f1 > 0.0F)
        {
            int i = 20;

            if (this.mc.player.isPotionActive(MobEffects.NAUSEA))
            {
                i = 7;
            }

            float f2 = 5.0F / (f1 * f1 + 5.0F) - f1 * 0.04F;
            f2 = f2 * f2;
            GlStateManager.rotatef(((float)this.rendererUpdateCount + p_78479_1_) * (float)i, 0.0F, 1.0F, 1.0F);
            GlStateManager.scalef(1.0F / f2, 1.0F, 1.0F);
            GlStateManager.rotatef(-((float)this.rendererUpdateCount + p_78479_1_) * (float)i, 0.0F, 1.0F, 1.0F);
        }

        this.orientCamera(p_78479_1_);

        if (this.debugView)
        {
            switch (this.field_175079_V)
            {
                case 0:
                    GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case 1:
                    GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case 2:
                    GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case 3:
                    GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
                    break;
                case 4:
                    GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            }
        }
    }

    private void func_78476_b(float p_78476_1_, int p_78476_2_)
    {
        if (!this.debugView)
        {
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            float f = 0.07F;

            if (this.mc.gameSettings.field_74337_g)
            {
                GlStateManager.translatef((float)(-(p_78476_2_ * 2 - 1)) * 0.07F, 0.0F, 0.0F);
            }

            Project.gluPerspective(this.func_78481_a(p_78476_1_, false), (float)this.mc.field_71443_c / (float)this.mc.field_71440_d, 0.05F, this.farPlaneDistance * 2.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();

            if (this.mc.gameSettings.field_74337_g)
            {
                GlStateManager.translatef((float)(p_78476_2_ * 2 - 1) * 0.1F, 0.0F, 0.0F);
            }

            GlStateManager.pushMatrix();
            this.hurtCameraEffect(p_78476_1_);

            if (this.mc.gameSettings.viewBobbing)
            {
                this.applyBobbing(p_78476_1_);
            }

            boolean flag = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();

            if (!net.minecraftforge.client.ForgeHooksClient.renderFirstPersonHand(mc.renderGlobal, p_78476_1_, p_78476_2_))
            if (this.mc.gameSettings.thirdPersonView == 0 && !flag && !this.mc.gameSettings.hideGUI && !this.mc.playerController.func_78747_a())
            {
                this.enableLightmap();
                this.itemRenderer.renderItemInFirstPerson(p_78476_1_);
                this.disableLightmap();
            }

            GlStateManager.popMatrix();

            if (this.mc.gameSettings.thirdPersonView == 0 && !flag)
            {
                this.itemRenderer.renderOverlays(p_78476_1_);
                this.hurtCameraEffect(p_78476_1_);
            }

            if (this.mc.gameSettings.viewBobbing)
            {
                this.applyBobbing(p_78476_1_);
            }
        }
    }

    public void disableLightmap()
    {
        GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
        GlStateManager.disableTexture2D();
        GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
    }

    public void enableLightmap()
    {
        GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float f = 0.00390625F;
        GlStateManager.scalef(0.00390625F, 0.00390625F, 0.00390625F);
        GlStateManager.translatef(8.0F, 8.0F, 8.0F);
        GlStateManager.matrixMode(5888);
        this.mc.getTextureManager().bindTexture(this.field_110922_T);
        GlStateManager.texParameteri(3553, 10241, 9729);
        GlStateManager.texParameteri(3553, 10240, 9729);
        GlStateManager.texParameteri(3553, 10242, 10496);
        GlStateManager.texParameteri(3553, 10243, 10496);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
    }

    private void func_78470_f()
    {
        this.field_175075_L = (float)((double)this.field_175075_L + (Math.random() - Math.random()) * Math.random() * Math.random());
        this.field_175075_L = (float)((double)this.field_175075_L * 0.9D);
        this.field_78514_e += this.field_175075_L - this.field_78514_e;
        this.field_78536_aa = true;
    }

    private void func_78472_g(float p_78472_1_)
    {
        if (this.field_78536_aa)
        {
            this.mc.profiler.startSection("lightTex");
            World world = this.mc.world;

            if (world != null)
            {
                float f = world.getSunBrightness(1.0F);
                float f1 = f * 0.95F + 0.05F;

                for (int i = 0; i < 256; ++i)
                {
                    float f2 = world.dimension.getLightBrightnessTable()[i / 16] * f1;
                    float f3 = world.dimension.getLightBrightnessTable()[i % 16] * (this.field_78514_e * 0.1F + 1.5F);

                    if (world.getLastLightningBolt() > 0)
                    {
                        f2 = world.dimension.getLightBrightnessTable()[i / 16];
                    }

                    float f4 = f2 * (f * 0.65F + 0.35F);
                    float f5 = f2 * (f * 0.65F + 0.35F);
                    float f6 = f3 * ((f3 * 0.6F + 0.4F) * 0.6F + 0.4F);
                    float f7 = f3 * (f3 * f3 * 0.6F + 0.4F);
                    float f8 = f4 + f3;
                    float f9 = f5 + f6;
                    float f10 = f2 + f7;
                    f8 = f8 * 0.96F + 0.03F;
                    f9 = f9 * 0.96F + 0.03F;
                    f10 = f10 * 0.96F + 0.03F;

                    if (this.bossColorModifier > 0.0F)
                    {
                        float f11 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * p_78472_1_;
                        f8 = f8 * (1.0F - f11) + f8 * 0.7F * f11;
                        f9 = f9 * (1.0F - f11) + f9 * 0.6F * f11;
                        f10 = f10 * (1.0F - f11) + f10 * 0.6F * f11;
                    }

                    if (world.dimension.getType().getId() == 1)
                    {
                        f8 = 0.22F + f3 * 0.75F;
                        f9 = 0.28F + f6 * 0.75F;
                        f10 = 0.25F + f7 * 0.75F;
                    }

                    float[] colors = {f8, f9, f10};
                    world.dimension.getLightmapColors(p_78472_1_, f, f2, f3, colors);
                    f8 = colors[0]; f9 = colors[1]; f10 = colors[2];

                    // Forge: fix MC-58177
                    f8 = MathHelper.clamp(f8, 0f, 1f);
                    f9 = MathHelper.clamp(f9, 0f, 1f);
                    f10 = MathHelper.clamp(f10, 0f, 1f);

                    if (this.mc.player.isPotionActive(MobEffects.NIGHT_VISION))
                    {
                        float f15 = this.getNightVisionBrightness(this.mc.player, p_78472_1_);
                        float f12 = 1.0F / f8;

                        if (f12 > 1.0F / f9)
                        {
                            f12 = 1.0F / f9;
                        }

                        if (f12 > 1.0F / f10)
                        {
                            f12 = 1.0F / f10;
                        }

                        f8 = f8 * (1.0F - f15) + f8 * f12 * f15;
                        f9 = f9 * (1.0F - f15) + f9 * f12 * f15;
                        f10 = f10 * (1.0F - f15) + f10 * f12 * f15;
                    }

                    if (f8 > 1.0F)
                    {
                        f8 = 1.0F;
                    }

                    if (f9 > 1.0F)
                    {
                        f9 = 1.0F;
                    }

                    if (f10 > 1.0F)
                    {
                        f10 = 1.0F;
                    }

                    float f16 = this.mc.gameSettings.gammaSetting;
                    float f17 = 1.0F - f8;
                    float f13 = 1.0F - f9;
                    float f14 = 1.0F - f10;
                    f17 = 1.0F - f17 * f17 * f17 * f17;
                    f13 = 1.0F - f13 * f13 * f13 * f13;
                    f14 = 1.0F - f14 * f14 * f14 * f14;
                    f8 = f8 * (1.0F - f16) + f17 * f16;
                    f9 = f9 * (1.0F - f16) + f13 * f16;
                    f10 = f10 * (1.0F - f16) + f14 * f16;
                    f8 = f8 * 0.96F + 0.03F;
                    f9 = f9 * 0.96F + 0.03F;
                    f10 = f10 * 0.96F + 0.03F;

                    if (f8 > 1.0F)
                    {
                        f8 = 1.0F;
                    }

                    if (f9 > 1.0F)
                    {
                        f9 = 1.0F;
                    }

                    if (f10 > 1.0F)
                    {
                        f10 = 1.0F;
                    }

                    if (f8 < 0.0F)
                    {
                        f8 = 0.0F;
                    }

                    if (f9 < 0.0F)
                    {
                        f9 = 0.0F;
                    }

                    if (f10 < 0.0F)
                    {
                        f10 = 0.0F;
                    }

                    int j = 255;
                    int k = (int)(f8 * 255.0F);
                    int l = (int)(f9 * 255.0F);
                    int i1 = (int)(f10 * 255.0F);
                    this.field_78504_Q[i] = -16777216 | k << 16 | l << 8 | i1;
                }

                this.lightmapTexture.updateDynamicTexture();
                this.field_78536_aa = false;
                this.mc.profiler.endSection();
            }
        }
    }

    private float getNightVisionBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks)
    {
        int i = entitylivingbaseIn.getActivePotionEffect(MobEffects.NIGHT_VISION).getDuration();
        return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)i - partialTicks) * (float)Math.PI * 0.2F) * 0.3F;
    }

    public void updateCameraAndRender(float partialTicks, long nanoTime)
    {
        boolean flag = Display.isActive();

        if (!flag && this.mc.gameSettings.pauseOnLostFocus && (!this.mc.gameSettings.touchscreen || !Mouse.isButtonDown(1)))
        {
            if (Minecraft.func_71386_F() - this.prevFrameTime > 500L)
            {
                this.mc.displayInGameMenu();
            }
        }
        else
        {
            this.prevFrameTime = Minecraft.func_71386_F();
        }

        this.mc.profiler.startSection("mouse");

        if (flag && Minecraft.IS_RUNNING_ON_MAC && this.mc.field_71415_G && !Mouse.isInsideWindow())
        {
            Mouse.setGrabbed(false);
            Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2 - 20);
            Mouse.setGrabbed(true);
        }

        if (this.mc.field_71415_G && flag)
        {
            this.mc.mouseHelper.func_74374_c();
            this.mc.getTutorial().func_193299_a(this.mc.mouseHelper);
            float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f1 = f * f * f * 8.0F;
            float f2 = (float)this.mc.mouseHelper.field_74377_a * f1;
            float f3 = (float)this.mc.mouseHelper.field_74375_b * f1;
            int i = 1;

            if (this.mc.gameSettings.invertMouse)
            {
                i = -1;
            }

            if (this.mc.gameSettings.smoothCamera)
            {
                this.field_78496_H += f2;
                this.field_78497_I += f3;
                float f4 = partialTicks - this.field_78492_L;
                this.field_78492_L = partialTicks;
                f2 = this.field_78498_J * f4;
                f3 = this.field_78499_K * f4;
                this.mc.player.func_70082_c(f2, f3 * (float)i);
            }
            else
            {
                this.field_78496_H = 0.0F;
                this.field_78497_I = 0.0F;
                this.mc.player.func_70082_c(f2, f3 * (float)i);
            }
        }

        this.mc.profiler.endSection();

        if (!this.mc.skipRenderWorld)
        {
            field_78517_a = this.mc.gameSettings.field_74337_g;
            final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int i1 = scaledresolution.func_78326_a();
            int j1 = scaledresolution.func_78328_b();
            final int k1 = Mouse.getX() * i1 / this.mc.field_71443_c;
            final int l1 = j1 - Mouse.getY() * j1 / this.mc.field_71440_d - 1;
            int i2 = this.mc.gameSettings.limitFramerate;

            if (this.mc.world != null)
            {
                this.mc.profiler.startSection("level");
                int j = Math.min(Minecraft.getDebugFPS(), i2);
                j = Math.max(j, 60);
                long k = System.nanoTime() - nanoTime;
                long l = Math.max((long)(1000000000 / j / 4) - k, 0L);
                this.renderWorld(partialTicks, System.nanoTime() + l);

                if (this.mc.isSingleplayer() && this.timeWorldIcon < Minecraft.func_71386_F() - 1000L)
                {
                    this.timeWorldIcon = Minecraft.func_71386_F();

                    if (!this.mc.getIntegratedServer().isWorldIconSet())
                    {
                        this.createWorldIcon();
                    }
                }

                if (OpenGlHelper.shadersSupported)
                {
                    this.mc.renderGlobal.renderEntityOutlineFramebuffer();

                    if (this.shaderGroup != null && this.useShader)
                    {
                        GlStateManager.matrixMode(5890);
                        GlStateManager.pushMatrix();
                        GlStateManager.loadIdentity();
                        this.shaderGroup.render(partialTicks);
                        GlStateManager.popMatrix();
                    }

                    this.mc.getFramebuffer().bindFramebuffer(true);
                }

                this.field_78510_Z = System.nanoTime();
                this.mc.profiler.endStartSection("gui");

                if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null)
                {
                    GlStateManager.alphaFunc(516, 0.1F);
                    this.func_78478_c();
                    this.renderItemActivation(i1, j1, partialTicks);
                    this.mc.ingameGUI.renderGameOverlay(partialTicks);
                }

                this.mc.profiler.endSection();
            }
            else
            {
                GlStateManager.viewport(0, 0, this.mc.field_71443_c, this.mc.field_71440_d);
                GlStateManager.matrixMode(5889);
                GlStateManager.loadIdentity();
                GlStateManager.matrixMode(5888);
                GlStateManager.loadIdentity();
                this.func_78478_c();
                this.field_78510_Z = System.nanoTime();
                // Forge: Fix MC-112292
                net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher.instance.textureManager = this.mc.getTextureManager();
                // Forge: also fix rendering text before entering world (not part of MC-112292, but the same reason)
                net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher.instance.fontRenderer = this.mc.fontRenderer;
            }

            if (this.mc.currentScreen != null)
            {
                GlStateManager.clear(256);

                try
                {
                    net.minecraftforge.client.ForgeHooksClient.drawScreen(this.mc.currentScreen, k1, l1, this.mc.getTickLength());
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering screen");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Screen render details");
                    crashreportcategory.addDetail("Screen name", new ICrashReportDetail<String>()
                    {
                        public String call() throws Exception
                        {
                            return EntityRenderer.this.mc.currentScreen.getClass().getCanonicalName();
                        }
                    });
                    crashreportcategory.addDetail("Mouse location", new ICrashReportDetail<String>()
                    {
                        public String call() throws Exception
                        {
                            return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", k1, l1, Mouse.getX(), Mouse.getY());
                        }
                    });
                    crashreportcategory.addDetail("Screen size", new ICrashReportDetail<String>()
                    {
                        public String call() throws Exception
                        {
                            return String.format("Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d", scaledresolution.func_78326_a(), scaledresolution.func_78328_b(), EntityRenderer.this.mc.field_71443_c, EntityRenderer.this.mc.field_71440_d, scaledresolution.func_78325_e());
                        }
                    });
                    throw new ReportedException(crashreport);
                }
            }
        }
    }

    private void createWorldIcon()
    {
        if (this.mc.renderGlobal.getRenderedChunks() > 10 && this.mc.renderGlobal.hasNoChunkUpdates() && !this.mc.getIntegratedServer().isWorldIconSet())
        {
            BufferedImage bufferedimage = ScreenShotHelper.func_186719_a(this.mc.field_71443_c, this.mc.field_71440_d, this.mc.getFramebuffer());
            int i = bufferedimage.getWidth();
            int j = bufferedimage.getHeight();
            int k = 0;
            int l = 0;

            if (i > j)
            {
                k = (i - j) / 2;
                i = j;
            }
            else
            {
                l = (j - i) / 2;
            }

            try
            {
                BufferedImage bufferedimage1 = new BufferedImage(64, 64, 1);
                Graphics graphics = bufferedimage1.createGraphics();
                graphics.drawImage(bufferedimage, 0, 0, 64, 64, k, l, k + i, l + i, (ImageObserver)null);
                graphics.dispose();
                ImageIO.write(bufferedimage1, "png", this.mc.getIntegratedServer().getWorldIconFile());
            }
            catch (IOException ioexception)
            {
                LOGGER.warn("Couldn't save auto screenshot", (Throwable)ioexception);
            }
        }
    }

    public void renderStreamIndicator(float partialTicks)
    {
        this.func_78478_c();
    }

    private boolean isDrawBlockOutline()
    {
        if (!this.drawBlockOutline)
        {
            return false;
        }
        else
        {
            Entity entity = this.mc.getRenderViewEntity();
            boolean flag = entity instanceof EntityPlayer && !this.mc.gameSettings.hideGUI;

            if (flag && !((EntityPlayer)entity).abilities.allowEdit)
            {
                ItemStack itemstack = ((EntityPlayer)entity).getHeldItemMainhand();

                if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.type == RayTraceResult.Type.BLOCK)
                {
                    BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
                    Block block = this.mc.world.getBlockState(blockpos).getBlock();

                    if (this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR)
                    {
                        flag = block.hasTileEntity(this.mc.world.getBlockState(blockpos)) && this.mc.world.getTileEntity(blockpos) instanceof IInventory;
                    }
                    else
                    {
                        flag = !itemstack.isEmpty() && (itemstack.func_179544_c(block) || itemstack.func_179547_d(block));
                    }
                }
            }

            return flag;
        }
    }

    public void renderWorld(float partialTicks, long finishTimeNano)
    {
        this.func_78472_g(partialTicks);

        if (this.mc.getRenderViewEntity() == null)
        {
            this.mc.setRenderViewEntity(this.mc.player);
        }

        this.getMouseOver(partialTicks);
        GlStateManager.enableDepthTest();
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(516, 0.5F);
        this.mc.profiler.startSection("center");

        if (this.mc.gameSettings.field_74337_g)
        {
            field_78515_b = 0;
            GlStateManager.colorMask(false, true, true, false);
            this.func_175068_a(0, partialTicks, finishTimeNano);
            field_78515_b = 1;
            GlStateManager.colorMask(true, false, false, false);
            this.func_175068_a(1, partialTicks, finishTimeNano);
            GlStateManager.colorMask(true, true, true, false);
        }
        else
        {
            this.func_175068_a(2, partialTicks, finishTimeNano);
        }

        this.mc.profiler.endSection();
    }

    private void func_175068_a(int p_175068_1_, float p_175068_2_, long p_175068_3_)
    {
        RenderGlobal renderglobal = this.mc.renderGlobal;
        ParticleManager particlemanager = this.mc.particles;
        boolean flag = this.isDrawBlockOutline();
        GlStateManager.enableCull();
        this.mc.profiler.endStartSection("clear");
        GlStateManager.viewport(0, 0, this.mc.field_71443_c, this.mc.field_71440_d);
        this.updateFogColor(p_175068_2_);
        GlStateManager.clear(16640);
        this.mc.profiler.endStartSection("camera");
        this.func_78479_a(p_175068_2_, p_175068_1_);
        ActiveRenderInfo.updateRenderInfo(this.mc.getRenderViewEntity(), this.mc.gameSettings.thirdPersonView == 2); //Forge: MC-46445 Spectator mode particles and sounds computed from where you have been before
        this.mc.profiler.endStartSection("frustum");
        ClippingHelperImpl.getInstance();
        this.mc.profiler.endStartSection("culling");
        ICamera icamera = new Frustum();
        Entity entity = this.mc.getRenderViewEntity();
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)p_175068_2_;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)p_175068_2_;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)p_175068_2_;
        icamera.setPosition(d0, d1, d2);

        if (this.mc.gameSettings.renderDistanceChunks >= 4)
        {
            this.setupFog(-1, p_175068_2_);
            this.mc.profiler.endStartSection("sky");
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(this.func_78481_a(p_175068_2_, true), (float)this.mc.field_71443_c / (float)this.mc.field_71440_d, 0.05F, this.farPlaneDistance * 2.0F);
            GlStateManager.matrixMode(5888);
            renderglobal.func_174976_a(p_175068_2_, p_175068_1_);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(this.func_78481_a(p_175068_2_, true), (float)this.mc.field_71443_c / (float)this.mc.field_71440_d, 0.05F, this.farPlaneDistance * MathHelper.SQRT_2);
            GlStateManager.matrixMode(5888);
        }

        this.setupFog(0, p_175068_2_);
        GlStateManager.shadeModel(7425);

        if (entity.posY + (double)entity.getEyeHeight() < 128.0D)
        {
            this.func_180437_a(renderglobal, p_175068_2_, p_175068_1_, d0, d1, d2);
        }

        this.mc.profiler.endStartSection("prepareterrain");
        this.setupFog(0, p_175068_2_);
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        RenderHelper.disableStandardItemLighting();
        this.mc.profiler.endStartSection("terrain_setup");
        renderglobal.func_174970_a(entity, (double)p_175068_2_, icamera, this.frameCount++, this.mc.player.isSpectator());

        if (p_175068_1_ == 0 || p_175068_1_ == 2)
        {
            this.mc.profiler.endStartSection("updatechunks");
            this.mc.renderGlobal.updateChunks(p_175068_3_);
        }

        this.mc.profiler.endStartSection("terrain");
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.disableAlphaTest();
        renderglobal.func_174977_a(BlockRenderLayer.SOLID, (double)p_175068_2_, p_175068_1_, entity);
        GlStateManager.enableAlphaTest();
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, this.mc.gameSettings.mipmapLevels > 0); // FORGE: fix flickering leaves when mods mess up the blurMipmap settings
        renderglobal.func_174977_a(BlockRenderLayer.CUTOUT_MIPPED, (double)p_175068_2_, p_175068_1_, entity);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        renderglobal.func_174977_a(BlockRenderLayer.CUTOUT, (double)p_175068_2_, p_175068_1_, entity);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.shadeModel(7424);
        GlStateManager.alphaFunc(516, 0.1F);

        if (!this.debugView)
        {
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            RenderHelper.enableStandardItemLighting();
            this.mc.profiler.endStartSection("entities");
            net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
            renderglobal.renderEntities(entity, icamera, p_175068_2_);
            net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
            RenderHelper.disableStandardItemLighting();
            this.disableLightmap();
        }

        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();

        if (flag && this.mc.objectMouseOver != null && !entity.func_70055_a(Material.WATER))
        {
            EntityPlayer entityplayer = (EntityPlayer)entity;
            GlStateManager.disableAlphaTest();
            this.mc.profiler.endStartSection("outline");
            if (!net.minecraftforge.client.ForgeHooksClient.onDrawBlockHighlight(renderglobal, entityplayer, mc.objectMouseOver, 0, p_175068_2_))
            renderglobal.drawSelectionBox(entityplayer, this.mc.objectMouseOver, 0, p_175068_2_);
            GlStateManager.enableAlphaTest();
        }

        if (this.mc.debugRenderer.shouldRender())
        {
            this.mc.debugRenderer.renderDebug(p_175068_2_, p_175068_3_);
        }

        this.mc.profiler.endStartSection("destroyProgress");
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        renderglobal.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), entity, p_175068_2_);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.disableBlend();

        if (!this.debugView)
        {
            this.enableLightmap();
            this.mc.profiler.endStartSection("litParticles");
            particlemanager.renderLitParticles(entity, p_175068_2_);
            RenderHelper.disableStandardItemLighting();
            this.setupFog(0, p_175068_2_);
            this.mc.profiler.endStartSection("particles");
            particlemanager.renderParticles(entity, p_175068_2_);
            this.disableLightmap();
        }

        GlStateManager.depthMask(false);
        GlStateManager.enableCull();
        this.mc.profiler.endStartSection("weather");
        this.renderRainSnow(p_175068_2_);
        GlStateManager.depthMask(true);
        renderglobal.renderWorldBorder(entity, p_175068_2_);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(516, 0.1F);
        this.setupFog(0, p_175068_2_);
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.shadeModel(7425);
        this.mc.profiler.endStartSection("translucent");
        renderglobal.func_174977_a(BlockRenderLayer.TRANSLUCENT, (double)p_175068_2_, p_175068_1_, entity);
        if (!this.debugView) //Only render if render pass 0 happens as well.
        {
            RenderHelper.enableStandardItemLighting();
            this.mc.profiler.endStartSection("entities");
            net.minecraftforge.client.ForgeHooksClient.setRenderPass(1);
            renderglobal.renderEntities(entity, icamera, p_175068_2_);
            // restore blending function changed by RenderGlobal.preRenderDamagedBlocks
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            net.minecraftforge.client.ForgeHooksClient.setRenderPass(-1);
            RenderHelper.disableStandardItemLighting();
        }
        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableFog();

        if (entity.posY + (double)entity.getEyeHeight() >= 128.0D)
        {
            this.mc.profiler.endStartSection("aboveClouds");
            this.func_180437_a(renderglobal, p_175068_2_, p_175068_1_, d0, d1, d2);
        }

        this.mc.profiler.endStartSection("forge_render_last");
        net.minecraftforge.client.ForgeHooksClient.dispatchRenderLast(renderglobal, p_175068_2_);

        this.mc.profiler.endStartSection("hand");

        if (this.renderHand)
        {
            GlStateManager.clear(256);
            this.func_78476_b(p_175068_2_, p_175068_1_);
        }
    }

    private void func_180437_a(RenderGlobal p_180437_1_, float p_180437_2_, int p_180437_3_, double p_180437_4_, double p_180437_6_, double p_180437_8_)
    {
        if (this.mc.gameSettings.shouldRenderClouds() != 0)
        {
            this.mc.profiler.endStartSection("clouds");
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(this.func_78481_a(p_180437_2_, true), (float)this.mc.field_71443_c / (float)this.mc.field_71440_d, 0.05F, this.farPlaneDistance * 4.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.pushMatrix();
            this.setupFog(0, p_180437_2_);
            p_180437_1_.func_180447_b(p_180437_2_, p_180437_3_, p_180437_4_, p_180437_6_, p_180437_8_);
            GlStateManager.disableFog();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(this.func_78481_a(p_180437_2_, true), (float)this.mc.field_71443_c / (float)this.mc.field_71440_d, 0.05F, this.farPlaneDistance * MathHelper.SQRT_2);
            GlStateManager.matrixMode(5888);
        }
    }

    private void addRainParticles()
    {
        float f = this.mc.world.getRainStrength(1.0F);

        if (!this.mc.gameSettings.fancyGraphics)
        {
            f /= 2.0F;
        }

        if (f != 0.0F)
        {
            this.random.setSeed((long)this.rendererUpdateCount * 312987231L);
            Entity entity = this.mc.getRenderViewEntity();
            World world = this.mc.world;
            BlockPos blockpos = new BlockPos(entity);
            int i = 10;
            double d0 = 0.0D;
            double d1 = 0.0D;
            double d2 = 0.0D;
            int j = 0;
            int k = (int)(100.0F * f * f);

            if (this.mc.gameSettings.particleSetting == 1)
            {
                k >>= 1;
            }
            else if (this.mc.gameSettings.particleSetting == 2)
            {
                k = 0;
            }

            for (int l = 0; l < k; ++l)
            {
                BlockPos blockpos1 = world.func_175725_q(blockpos.add(this.random.nextInt(10) - this.random.nextInt(10), 0, this.random.nextInt(10) - this.random.nextInt(10)));
                Biome biome = world.getBiome(blockpos1);
                BlockPos blockpos2 = blockpos1.down();
                IBlockState iblockstate = world.getBlockState(blockpos2);

                if (blockpos1.getY() <= blockpos.getY() + 10 && blockpos1.getY() >= blockpos.getY() - 10 && biome.func_76738_d() && biome.getTemperature(blockpos1) >= 0.15F)
                {
                    double d3 = this.random.nextDouble();
                    double d4 = this.random.nextDouble();
                    AxisAlignedBB axisalignedbb = iblockstate.func_185900_c(world, blockpos2);

                    if (iblockstate.getMaterial() != Material.LAVA && iblockstate.getBlock() != Blocks.field_189877_df)
                    {
                        if (iblockstate.getMaterial() != Material.AIR)
                        {
                            ++j;

                            if (this.random.nextInt(j) == 0)
                            {
                                d0 = (double)blockpos2.getX() + d3;
                                d1 = (double)((float)blockpos2.getY() + 0.1F) + axisalignedbb.maxY - 1.0D;
                                d2 = (double)blockpos2.getZ() + d4;
                            }

                            this.mc.world.func_175688_a(EnumParticleTypes.WATER_DROP, (double)blockpos2.getX() + d3, (double)((float)blockpos2.getY() + 0.1F) + axisalignedbb.maxY, (double)blockpos2.getZ() + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                        }
                    }
                    else
                    {
                        this.mc.world.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, (double)blockpos1.getX() + d3, (double)((float)blockpos1.getY() + 0.1F) - axisalignedbb.minY, (double)blockpos1.getZ() + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    }
                }
            }

            if (j > 0 && this.random.nextInt(3) < this.rainSoundCounter++)
            {
                this.rainSoundCounter = 0;

                if (d1 > (double)(blockpos.getY() + 1) && world.func_175725_q(blockpos).getY() > MathHelper.floor((float)blockpos.getY()))
                {
                    this.mc.world.playSound(d0, d1, d2, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F, 0.5F, false);
                }
                else
                {
                    this.mc.world.playSound(d0, d1, d2, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2F, 1.0F, false);
                }
            }
        }
    }

    /**
     * Render rain and snow
     */
    protected void renderRainSnow(float partialTicks)
    {
        net.minecraftforge.client.IRenderHandler renderer = this.mc.world.dimension.getWeatherRenderer();
        if (renderer != null)
        {
            renderer.render(partialTicks, this.mc.world, mc);
            return;
        }

        float f = this.mc.world.getRainStrength(partialTicks);

        if (f > 0.0F)
        {
            this.enableLightmap();
            Entity entity = this.mc.getRenderViewEntity();
            World world = this.mc.world;
            int i = MathHelper.floor(entity.posX);
            int j = MathHelper.floor(entity.posY);
            int k = MathHelper.floor(entity.posZ);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            GlStateManager.disableCull();
            GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.alphaFunc(516, 0.1F);
            double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
            double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
            double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
            int l = MathHelper.floor(d1);
            int i1 = 5;

            if (this.mc.gameSettings.fancyGraphics)
            {
                i1 = 10;
            }

            int j1 = -1;
            float f1 = (float)this.rendererUpdateCount + partialTicks;
            bufferbuilder.setTranslation(-d0, -d1, -d2);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (int k1 = k - i1; k1 <= k + i1; ++k1)
            {
                for (int l1 = i - i1; l1 <= i + i1; ++l1)
                {
                    int i2 = (k1 - k + 16) * 32 + l1 - i + 16;
                    double d3 = (double)this.rainXCoords[i2] * 0.5D;
                    double d4 = (double)this.rainYCoords[i2] * 0.5D;
                    blockpos$mutableblockpos.setPos(l1, 0, k1);
                    Biome biome = world.getBiome(blockpos$mutableblockpos);

                    if (biome.func_76738_d() || biome.func_76746_c())
                    {
                        int j2 = world.func_175725_q(blockpos$mutableblockpos).getY();
                        int k2 = j - i1;
                        int l2 = j + i1;

                        if (k2 < j2)
                        {
                            k2 = j2;
                        }

                        if (l2 < j2)
                        {
                            l2 = j2;
                        }

                        int i3 = j2;

                        if (j2 < l)
                        {
                            i3 = l;
                        }

                        if (k2 != l2)
                        {
                            this.random.setSeed((long)(l1 * l1 * 3121 + l1 * 45238971 ^ k1 * k1 * 418711 + k1 * 13761));
                            blockpos$mutableblockpos.setPos(l1, k2, k1);
                            float f2 = biome.getTemperature(blockpos$mutableblockpos);

                            if (world.func_72959_q().func_76939_a(f2, j2) >= 0.15F)
                            {
                                if (j1 != 0)
                                {
                                    if (j1 >= 0)
                                    {
                                        tessellator.draw();
                                    }

                                    j1 = 0;
                                    this.mc.getTextureManager().bindTexture(RAIN_TEXTURES);
                                    bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                                }

                                double d5 = -((double)(this.rendererUpdateCount + l1 * l1 * 3121 + l1 * 45238971 + k1 * k1 * 418711 + k1 * 13761 & 31) + (double)partialTicks) / 32.0D * (3.0D + this.random.nextDouble());
                                double d6 = (double)((float)l1 + 0.5F) - entity.posX;
                                double d7 = (double)((float)k1 + 0.5F) - entity.posZ;
                                float f3 = MathHelper.sqrt(d6 * d6 + d7 * d7) / (float)i1;
                                float f4 = ((1.0F - f3 * f3) * 0.5F + 0.5F) * f;
                                blockpos$mutableblockpos.setPos(l1, i3, k1);
                                int j3 = world.getCombinedLight(blockpos$mutableblockpos, 0);
                                int k3 = j3 >> 16 & 65535;
                                int l3 = j3 & 65535;
                                bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)l2, (double)k1 - d4 + 0.5D).tex(0.0D, (double)k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                                bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)l2, (double)k1 + d4 + 0.5D).tex(1.0D, (double)k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                                bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)k2, (double)k1 + d4 + 0.5D).tex(1.0D, (double)l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                                bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)k2, (double)k1 - d4 + 0.5D).tex(0.0D, (double)l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                            }
                            else
                            {
                                if (j1 != 1)
                                {
                                    if (j1 >= 0)
                                    {
                                        tessellator.draw();
                                    }

                                    j1 = 1;
                                    this.mc.getTextureManager().bindTexture(SNOW_TEXTURES);
                                    bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                                }

                                double d8 = (double)(-((float)(this.rendererUpdateCount & 511) + partialTicks) / 512.0F);
                                double d9 = this.random.nextDouble() + (double)f1 * 0.01D * (double)((float)this.random.nextGaussian());
                                double d10 = this.random.nextDouble() + (double)(f1 * (float)this.random.nextGaussian()) * 0.001D;
                                double d11 = (double)((float)l1 + 0.5F) - entity.posX;
                                double d12 = (double)((float)k1 + 0.5F) - entity.posZ;
                                float f6 = MathHelper.sqrt(d11 * d11 + d12 * d12) / (float)i1;
                                float f5 = ((1.0F - f6 * f6) * 0.3F + 0.5F) * f;
                                blockpos$mutableblockpos.setPos(l1, i3, k1);
                                int i4 = (world.getCombinedLight(blockpos$mutableblockpos, 0) * 3 + 15728880) / 4;
                                int j4 = i4 >> 16 & 65535;
                                int k4 = i4 & 65535;
                                bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)l2, (double)k1 - d4 + 0.5D).tex(0.0D + d9, (double)k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                                bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)l2, (double)k1 + d4 + 0.5D).tex(1.0D + d9, (double)k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                                bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)k2, (double)k1 + d4 + 0.5D).tex(1.0D + d9, (double)l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                                bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)k2, (double)k1 - d4 + 0.5D).tex(0.0D + d9, (double)l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                            }
                        }
                    }
                }
            }

            if (j1 >= 0)
            {
                tessellator.draw();
            }

            bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            this.disableLightmap();
        }
    }

    public void func_78478_c()
    {
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        GlStateManager.clear(256);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, scaledresolution.func_78327_c(), scaledresolution.func_78324_d(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
    }

    /**
     * calculates fog and calls glClearColor
     */
    private void updateFogColor(float partialTicks)
    {
        World world = this.mc.world;
        Entity entity = this.mc.getRenderViewEntity();
        float f = 0.25F + 0.75F * (float)this.mc.gameSettings.renderDistanceChunks / 32.0F;
        f = 1.0F - (float)Math.pow((double)f, 0.25D);
        Vec3d vec3d = world.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
        float f1 = (float)vec3d.x;
        float f2 = (float)vec3d.y;
        float f3 = (float)vec3d.z;
        Vec3d vec3d1 = world.getFogColor(partialTicks);
        this.field_175080_Q = (float)vec3d1.x;
        this.field_175082_R = (float)vec3d1.y;
        this.field_175081_S = (float)vec3d1.z;

        if (this.mc.gameSettings.renderDistanceChunks >= 4)
        {
            double d0 = MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) > 0.0F ? -1.0D : 1.0D;
            Vec3d vec3d2 = new Vec3d(d0, 0.0D, 0.0D);
            float f5 = (float)entity.getLook(partialTicks).dotProduct(vec3d2);

            if (f5 < 0.0F)
            {
                f5 = 0.0F;
            }

            if (f5 > 0.0F)
            {
                float[] afloat = world.dimension.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);

                if (afloat != null)
                {
                    f5 = f5 * afloat[3];
                    this.field_175080_Q = this.field_175080_Q * (1.0F - f5) + afloat[0] * f5;
                    this.field_175082_R = this.field_175082_R * (1.0F - f5) + afloat[1] * f5;
                    this.field_175081_S = this.field_175081_S * (1.0F - f5) + afloat[2] * f5;
                }
            }
        }

        this.field_175080_Q += (f1 - this.field_175080_Q) * f;
        this.field_175082_R += (f2 - this.field_175082_R) * f;
        this.field_175081_S += (f3 - this.field_175081_S) * f;
        float f8 = world.getRainStrength(partialTicks);

        if (f8 > 0.0F)
        {
            float f4 = 1.0F - f8 * 0.5F;
            float f10 = 1.0F - f8 * 0.4F;
            this.field_175080_Q *= f4;
            this.field_175082_R *= f4;
            this.field_175081_S *= f10;
        }

        float f9 = world.getThunderStrength(partialTicks);

        if (f9 > 0.0F)
        {
            float f11 = 1.0F - f9 * 0.5F;
            this.field_175080_Q *= f11;
            this.field_175082_R *= f11;
            this.field_175081_S *= f11;
        }

        IBlockState iblockstate = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks);

        if (this.field_78500_U)
        {
            Vec3d vec3d3 = world.getCloudColour(partialTicks);
            this.field_175080_Q = (float)vec3d3.x;
            this.field_175082_R = (float)vec3d3.y;
            this.field_175081_S = (float)vec3d3.z;
        }
        else
        {
            //Forge Moved to Block.
            Vec3d viewport = ActiveRenderInfo.projectViewFromEntity(entity, partialTicks);
            BlockPos viewportPos = new BlockPos(viewport);
            IBlockState viewportState = this.mc.world.getBlockState(viewportPos);
            Vec3d inMaterialColor = viewportState.getBlock().getFogColor(this.mc.world, viewportPos, viewportState, entity, new Vec3d(field_175080_Q, field_175082_R, field_175081_S), partialTicks);
            this.field_175080_Q = (float)inMaterialColor.x;
            this.field_175082_R = (float)inMaterialColor.y;
            this.field_175081_S = (float)inMaterialColor.z;
        }

        float f13 = this.field_78535_ad + (this.field_78539_ae - this.field_78535_ad) * partialTicks;
        this.field_175080_Q *= f13;
        this.field_175082_R *= f13;
        this.field_175081_S *= f13;
        double d1 = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks) * world.dimension.getVoidFogYFactor();

        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(MobEffects.BLINDNESS))
        {
            int i = ((EntityLivingBase)entity).getActivePotionEffect(MobEffects.BLINDNESS).getDuration();

            if (i < 20)
            {
                d1 *= (double)(1.0F - (float)i / 20.0F);
            }
            else
            {
                d1 = 0.0D;
            }
        }

        if (d1 < 1.0D)
        {
            if (d1 < 0.0D)
            {
                d1 = 0.0D;
            }

            d1 = d1 * d1;
            this.field_175080_Q = (float)((double)this.field_175080_Q * d1);
            this.field_175082_R = (float)((double)this.field_175082_R * d1);
            this.field_175081_S = (float)((double)this.field_175081_S * d1);
        }

        if (this.bossColorModifier > 0.0F)
        {
            float f14 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
            this.field_175080_Q = this.field_175080_Q * (1.0F - f14) + this.field_175080_Q * 0.7F * f14;
            this.field_175082_R = this.field_175082_R * (1.0F - f14) + this.field_175082_R * 0.6F * f14;
            this.field_175081_S = this.field_175081_S * (1.0F - f14) + this.field_175081_S * 0.6F * f14;
        }

        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(MobEffects.NIGHT_VISION))
        {
            float f15 = this.getNightVisionBrightness((EntityLivingBase)entity, partialTicks);
            float f6 = 1.0F / this.field_175080_Q;

            if (f6 > 1.0F / this.field_175082_R)
            {
                f6 = 1.0F / this.field_175082_R;
            }

            if (f6 > 1.0F / this.field_175081_S)
            {
                f6 = 1.0F / this.field_175081_S;
            }

            // Forge: fix MC-4647 and MC-10480
            if (Float.isInfinite(f6)) f6 = Math.nextAfter(f6, 0.0);

            this.field_175080_Q = this.field_175080_Q * (1.0F - f15) + this.field_175080_Q * f6 * f15;
            this.field_175082_R = this.field_175082_R * (1.0F - f15) + this.field_175082_R * f6 * f15;
            this.field_175081_S = this.field_175081_S * (1.0F - f15) + this.field_175081_S * f6 * f15;
        }

        if (this.mc.gameSettings.field_74337_g)
        {
            float f16 = (this.field_175080_Q * 30.0F + this.field_175082_R * 59.0F + this.field_175081_S * 11.0F) / 100.0F;
            float f17 = (this.field_175080_Q * 30.0F + this.field_175082_R * 70.0F) / 100.0F;
            float f7 = (this.field_175080_Q * 30.0F + this.field_175081_S * 70.0F) / 100.0F;
            this.field_175080_Q = f16;
            this.field_175082_R = f17;
            this.field_175081_S = f7;
        }

        net.minecraftforge.client.event.EntityViewRenderEvent.FogColors event = new net.minecraftforge.client.event.EntityViewRenderEvent.FogColors(this, entity, iblockstate, partialTicks, this.field_175080_Q, this.field_175082_R, this.field_175081_S);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);

        this.field_175080_Q = event.getRed();
        this.field_175082_R = event.getGreen();
        this.field_175081_S = event.getBlue();

        GlStateManager.clearColor(this.field_175080_Q, this.field_175082_R, this.field_175081_S, 0.0F);
    }

    /**
     * Sets up the fog to be rendered. If the arg passed in is -1 the fog starts at 0 and goes to 80% of far plane
     * distance and is used for sky rendering.
     */
    private void setupFog(int startCoords, float partialTicks)
    {
        Entity entity = this.mc.getRenderViewEntity();
        this.setupFogColor(false);
        GlStateManager.normal3f(0.0F, -1.0F, 0.0F);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        IBlockState iblockstate = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks);
        float hook = net.minecraftforge.client.ForgeHooksClient.getFogDensity(this, entity, iblockstate, partialTicks, 0.1F);
        if (hook >= 0) GlStateManager.fogDensity(hook);
        else
        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(MobEffects.BLINDNESS))
        {
            float f1 = 5.0F;
            int i = ((EntityLivingBase)entity).getActivePotionEffect(MobEffects.BLINDNESS).getDuration();

            if (i < 20)
            {
                f1 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - (float)i / 20.0F);
            }

            GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);

            if (startCoords == -1)
            {
                GlStateManager.fogStart(0.0F);
                GlStateManager.fogEnd(f1 * 0.8F);
            }
            else
            {
                GlStateManager.fogStart(f1 * 0.25F);
                GlStateManager.fogEnd(f1);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance)
            {
                GlStateManager.fogi(34138, 34139);
            }
        }
        else if (this.field_78500_U)
        {
            GlStateManager.fogMode(GlStateManager.FogMode.EXP);
            GlStateManager.fogDensity(0.1F);
        }
        else if (iblockstate.getMaterial() == Material.WATER)
        {
            GlStateManager.fogMode(GlStateManager.FogMode.EXP);

            if (entity instanceof EntityLivingBase)
            {
                if (((EntityLivingBase)entity).isPotionActive(MobEffects.WATER_BREATHING))
                {
                    GlStateManager.fogDensity(0.01F);
                }
                else
                {
                    GlStateManager.fogDensity(0.1F - (float)EnchantmentHelper.getRespirationModifier((EntityLivingBase)entity) * 0.03F);
                }
            }
            else
            {
                GlStateManager.fogDensity(0.1F);
            }
        }
        else if (iblockstate.getMaterial() == Material.LAVA)
        {
            GlStateManager.fogMode(GlStateManager.FogMode.EXP);
            GlStateManager.fogDensity(2.0F);
        }
        else
        {
            float f = this.farPlaneDistance;
            GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);

            if (startCoords == -1)
            {
                GlStateManager.fogStart(0.0F);
                GlStateManager.fogEnd(f);
            }
            else
            {
                GlStateManager.fogStart(f * 0.75F);
                GlStateManager.fogEnd(f);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance)
            {
                GlStateManager.fogi(34138, 34139);
            }

            if (this.mc.world.dimension.doesXZShowFog((int)entity.posX, (int)entity.posZ) || this.mc.ingameGUI.getBossOverlay().shouldCreateFog())
            {
                GlStateManager.fogStart(f * 0.05F);
                GlStateManager.fogEnd(Math.min(f, 192.0F) * 0.5F);
            }
            net.minecraftforge.client.ForgeHooksClient.onFogRender(this, entity, iblockstate, partialTicks, startCoords, f);
        }

        GlStateManager.enableColorMaterial();
        GlStateManager.enableFog();
        GlStateManager.colorMaterial(1028, 4608);
    }

    public void setupFogColor(boolean black)
    {
        if (black)
        {
            GlStateManager.fogfv(2918, this.func_78469_a(0.0F, 0.0F, 0.0F, 1.0F));
        }
        else
        {
            GlStateManager.fogfv(2918, this.func_78469_a(this.field_175080_Q, this.field_175082_R, this.field_175081_S, 1.0F));
        }
    }

    private FloatBuffer func_78469_a(float p_78469_1_, float p_78469_2_, float p_78469_3_, float p_78469_4_)
    {
        this.field_78521_m.clear();
        this.field_78521_m.put(p_78469_1_).put(p_78469_2_).put(p_78469_3_).put(p_78469_4_);
        this.field_78521_m.flip();
        return this.field_78521_m;
    }

    public void resetData()
    {
        this.itemActivationItem = null;
        this.mapItemRenderer.clearLoadedMaps();
    }

    public MapItemRenderer getMapItemRenderer()
    {
        return this.mapItemRenderer;
    }

    public static void drawNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(x, y, z);
        GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scalef(-0.025F, -0.025F, 0.025F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        if (!isSneaking)
        {
            GlStateManager.disableDepthTest();
        }

        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        int i = fontRendererIn.getStringWidth(str) / 2;
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)(-i - 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferbuilder.pos((double)(-i - 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferbuilder.pos((double)(i + 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferbuilder.pos((double)(i + 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();

        if (!isSneaking)
        {
            fontRendererIn.func_78276_b(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, 553648127);
            GlStateManager.enableDepthTest();
        }

        GlStateManager.depthMask(true);
        fontRendererIn.func_78276_b(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, isSneaking ? 553648127 : -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public void displayItemActivation(ItemStack stack)
    {
        this.itemActivationItem = stack;
        this.itemActivationTicks = 40;
        this.itemActivationOffX = this.random.nextFloat() * 2.0F - 1.0F;
        this.itemActivationOffY = this.random.nextFloat() * 2.0F - 1.0F;
    }

    private void renderItemActivation(int widthsp, int heightScaled, float partialTicks)
    {
        if (this.itemActivationItem != null && this.itemActivationTicks > 0)
        {
            int i = 40 - this.itemActivationTicks;
            float f = ((float)i + partialTicks) / 40.0F;
            float f1 = f * f;
            float f2 = f * f1;
            float f3 = 10.25F * f2 * f1 + -24.95F * f1 * f1 + 25.5F * f2 + -13.8F * f1 + 4.0F * f;
            float f4 = f3 * (float)Math.PI;
            float f5 = this.itemActivationOffX * (float)(widthsp / 4);
            float f6 = this.itemActivationOffY * (float)(heightScaled / 4);
            GlStateManager.enableAlphaTest();
            GlStateManager.pushMatrix();
            GlStateManager.pushLightingAttrib();
            GlStateManager.enableDepthTest();
            GlStateManager.disableCull();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.translatef((float)(widthsp / 2) + f5 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), (float)(heightScaled / 2) + f6 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), -50.0F);
            float f7 = 50.0F + 175.0F * MathHelper.sin(f4);
            GlStateManager.scalef(f7, -f7, f7);
            GlStateManager.rotatef(900.0F * MathHelper.abs(MathHelper.sin(f4)), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(6.0F * MathHelper.cos(f * 8.0F), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(6.0F * MathHelper.cos(f * 8.0F), 0.0F, 0.0F, 1.0F);
            this.mc.getItemRenderer().renderItem(this.itemActivationItem, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.enableCull();
            GlStateManager.disableDepthTest();
        }
    }
}