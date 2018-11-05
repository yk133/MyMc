package net.minecraft.client.renderer.block.model;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BuiltInModel implements IBakedModel
{
    private final ItemCameraTransforms cameraTransforms;
    private final ItemOverrideList overrides;

    public BuiltInModel(ItemCameraTransforms p_i46537_1_, ItemOverrideList p_i46537_2_)
    {
        this.cameraTransforms = p_i46537_1_;
        this.overrides = p_i46537_2_;
    }

    public List<BakedQuad> func_188616_a(@Nullable IBlockState p_188616_1_, @Nullable EnumFacing p_188616_2_, long p_188616_3_)
    {
        return Collections.<BakedQuad>emptyList();
    }

    public boolean isAmbientOcclusion()
    {
        return false;
    }

    public boolean isGui3d()
    {
        return true;
    }

    public boolean isBuiltInRenderer()
    {
        return true;
    }

    public TextureAtlasSprite getParticleTexture()
    {
        return null;
    }

    public ItemCameraTransforms getItemCameraTransforms()
    {
        return this.cameraTransforms;
    }

    public ItemOverrideList getOverrides()
    {
        return this.overrides;
    }
}