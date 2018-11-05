package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelDragonHead;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntitySkullRenderer extends TileEntitySpecialRenderer<TileEntitySkull>
{
    private static final ResourceLocation field_147537_c = new ResourceLocation("textures/entity/skeleton/skeleton.png");
    private static final ResourceLocation field_147534_d = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
    private static final ResourceLocation field_147535_e = new ResourceLocation("textures/entity/zombie/zombie.png");
    private static final ResourceLocation field_147532_f = new ResourceLocation("textures/entity/creeper/creeper.png");
    private static final ResourceLocation field_188191_h = new ResourceLocation("textures/entity/enderdragon/dragon.png");
    private final ModelDragonHead field_188192_i = new ModelDragonHead(0.0F);
    public static TileEntitySkullRenderer instance;
    private final ModelSkeletonHead field_178467_h = new ModelSkeletonHead(0, 0, 64, 32);
    private final ModelSkeletonHead field_178468_i = new ModelHumanoidHead();

    public void func_192841_a(TileEntitySkull p_192841_1_, double p_192841_2_, double p_192841_4_, double p_192841_6_, float p_192841_8_, int p_192841_9_, float p_192841_10_)
    {
        EnumFacing enumfacing = EnumFacing.byIndex(p_192841_1_.func_145832_p() & 7);
        float f = p_192841_1_.getAnimationProgress(p_192841_8_);
        this.func_188190_a((float)p_192841_2_, (float)p_192841_4_, (float)p_192841_6_, enumfacing, (float)(p_192841_1_.func_145906_b() * 360) / 16.0F, p_192841_1_.func_145904_a(), p_192841_1_.getPlayerProfile(), p_192841_9_, f);
    }

    public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        super.setRendererDispatcher(rendererDispatcherIn);
        instance = this;
    }

    public void func_188190_a(float p_188190_1_, float p_188190_2_, float p_188190_3_, EnumFacing p_188190_4_, float p_188190_5_, int p_188190_6_, @Nullable GameProfile p_188190_7_, int p_188190_8_, float p_188190_9_)
    {
        ModelBase modelbase = this.field_178467_h;

        if (p_188190_8_ >= 0)
        {
            this.bindTexture(DESTROY_STAGES[p_188190_8_]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0F, 2.0F, 1.0F);
            GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        }
        else
        {
            switch (p_188190_6_)
            {
                case 0:
                default:
                    this.bindTexture(field_147537_c);
                    break;
                case 1:
                    this.bindTexture(field_147534_d);
                    break;
                case 2:
                    this.bindTexture(field_147535_e);
                    modelbase = this.field_178468_i;
                    break;
                case 3:
                    modelbase = this.field_178468_i;
                    ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();

                    if (p_188190_7_ != null)
                    {
                        Minecraft minecraft = Minecraft.getInstance();
                        Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(p_188190_7_);

                        if (map.containsKey(Type.SKIN))
                        {
                            resourcelocation = minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
                        }
                        else
                        {
                            UUID uuid = EntityPlayer.getUUID(p_188190_7_);
                            resourcelocation = DefaultPlayerSkin.getDefaultSkin(uuid);
                        }
                    }

                    this.bindTexture(resourcelocation);
                    break;
                case 4:
                    this.bindTexture(field_147532_f);
                    break;
                case 5:
                    this.bindTexture(field_188191_h);
                    modelbase = this.field_188192_i;
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        if (p_188190_4_ == EnumFacing.UP)
        {
            GlStateManager.translatef(p_188190_1_ + 0.5F, p_188190_2_, p_188190_3_ + 0.5F);
        }
        else
        {
            switch (p_188190_4_)
            {
                case NORTH:
                    GlStateManager.translatef(p_188190_1_ + 0.5F, p_188190_2_ + 0.25F, p_188190_3_ + 0.74F);
                    break;
                case SOUTH:
                    GlStateManager.translatef(p_188190_1_ + 0.5F, p_188190_2_ + 0.25F, p_188190_3_ + 0.26F);
                    p_188190_5_ = 180.0F;
                    break;
                case WEST:
                    GlStateManager.translatef(p_188190_1_ + 0.74F, p_188190_2_ + 0.25F, p_188190_3_ + 0.5F);
                    p_188190_5_ = 270.0F;
                    break;
                case EAST:
                default:
                    GlStateManager.translatef(p_188190_1_ + 0.26F, p_188190_2_ + 0.25F, p_188190_3_ + 0.5F);
                    p_188190_5_ = 90.0F;
            }
        }

        float f = 0.0625F;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
        GlStateManager.enableAlphaTest();

        if (p_188190_6_ == 3)
        {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        }

        modelbase.render((Entity)null, p_188190_9_, 0.0F, 0.0F, p_188190_5_, 0.0F, 0.0625F);
        GlStateManager.popMatrix();

        if (p_188190_8_ >= 0)
        {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
}