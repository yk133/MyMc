package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderAbstractHorse extends RenderLiving<AbstractHorse>
{
    private static final Map < Class<?>, ResourceLocation > field_191359_a = Maps. < Class<?>, ResourceLocation > newHashMap();
    private final float scale;

    public RenderAbstractHorse(RenderManager p_i47212_1_)
    {
        this(p_i47212_1_, 1.0F);
    }

    public RenderAbstractHorse(RenderManager p_i47213_1_, float p_i47213_2_)
    {
        super(p_i47213_1_, new ModelHorse(), 0.75F);
        this.scale = p_i47213_2_;
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(AbstractHorse entitylivingbaseIn, float partialTickTime)
    {
        GlStateManager.scalef(this.scale, this.scale, this.scale);
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(AbstractHorse entity)
    {
        return field_191359_a.get(entity.getClass());
    }

    static
    {
        field_191359_a.put(EntityDonkey.class, new ResourceLocation("textures/entity/horse/donkey.png"));
        field_191359_a.put(EntityMule.class, new ResourceLocation("textures/entity/horse/mule.png"));
        field_191359_a.put(EntityZombieHorse.class, new ResourceLocation("textures/entity/horse/horse_zombie.png"));
        field_191359_a.put(EntitySkeletonHorse.class, new ResourceLocation("textures/entity/horse/horse_skeleton.png"));
    }
}