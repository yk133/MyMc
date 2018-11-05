package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelManager implements IResourceManagerReloadListener
{
    private IRegistry<ModelResourceLocation, IBakedModel> modelRegistry;
    private final TextureMap texMap;
    private final BlockModelShapes modelProvider;
    private IBakedModel defaultModel;

    public ModelManager(TextureMap textures)
    {
        this.texMap = textures;
        this.modelProvider = new BlockModelShapes(this);
    }

    public void func_110549_a(IResourceManager p_110549_1_)
    {
        net.minecraftforge.client.model.ModelLoader modelbakery = new net.minecraftforge.client.model.ModelLoader(p_110549_1_, this.texMap, this.modelProvider);
        this.modelRegistry = modelbakery.setupModelRegistry();
        this.defaultModel = this.modelRegistry.get(ModelBakery.MODEL_MISSING);
        net.minecraftforge.client.ForgeHooksClient.onModelBake(this, this.modelRegistry, modelbakery);
        this.modelProvider.reloadModels();
    }

    public IBakedModel getModel(ModelResourceLocation modelLocation)
    {
        if (modelLocation == null)
        {
            return this.defaultModel;
        }
        else
        {
            IBakedModel ibakedmodel = this.modelRegistry.get(modelLocation);
            return ibakedmodel == null ? this.defaultModel : ibakedmodel;
        }
    }

    public IBakedModel getMissingModel()
    {
        return this.defaultModel;
    }

    public TextureMap func_174952_b()
    {
        return this.texMap;
    }

    public BlockModelShapes getBlockModelShapes()
    {
        return this.modelProvider;
    }
}