package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemOverrideList
{
    public static final ItemOverrideList EMPTY = new ItemOverrideList();
    private final List<ItemOverride> overrides = Lists.<ItemOverride>newArrayList();

    private ItemOverrideList()
    {
    }

    public ItemOverrideList(List<ItemOverride> p_i46570_1_)
    {
        for (int i = p_i46570_1_.size() - 1; i >= 0; --i)
        {
            this.overrides.add(p_i46570_1_.get(i));
        }
    }

    @Nullable
    @Deprecated
    public ResourceLocation func_188021_a(ItemStack p_188021_1_, @Nullable World p_188021_2_, @Nullable EntityLivingBase p_188021_3_)
    {
        if (!this.overrides.isEmpty())
        {
            for (ItemOverride itemoverride : this.overrides)
            {
                if (itemoverride.matchesItemStack(p_188021_1_, p_188021_2_, p_188021_3_))
                {
                    return itemoverride.getLocation();
                }
            }
        }

        return null;
    }

    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
    {
        if (!stack.isEmpty() && stack.getItem().hasCustomProperties())
        {
            ResourceLocation location = func_188021_a(stack, world, entity);
            if (location != null)
            {
                return net.minecraft.client.Minecraft.getInstance().getItemRenderer().getItemModelMesher().getModelManager().getModel(net.minecraftforge.client.model.ModelLoader.getInventoryVariant(location.toString()));
            }
        }
        return originalModel;
    }

    public com.google.common.collect.ImmutableList<ItemOverride> getOverrides()
    {
        return com.google.common.collect.ImmutableList.copyOf(overrides);
    }
}