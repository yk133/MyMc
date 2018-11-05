package net.minecraft.client.renderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockRendererDispatcher implements IResourceManagerReloadListener
{
    private final BlockModelShapes blockModelShapes;
    private final BlockModelRenderer blockModelRenderer;
    private final ChestRenderer chestRenderer = new ChestRenderer();
    private final BlockFluidRenderer fluidRenderer;

    public BlockRendererDispatcher(BlockModelShapes p_i46577_1_, BlockColors p_i46577_2_)
    {
        this.blockModelShapes = p_i46577_1_;
        this.blockModelRenderer = new net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer(p_i46577_2_);
        this.fluidRenderer = new BlockFluidRenderer(p_i46577_2_);
    }

    public BlockModelShapes getBlockModelShapes()
    {
        return this.blockModelShapes;
    }

    public void renderBlockDamage(IBlockState state, BlockPos pos, TextureAtlasSprite texture, IBlockAccess blockAccess)
    {
        if (state.getRenderType() == EnumBlockRenderType.MODEL)
        {
            state = state.func_185899_b(blockAccess, pos);
            IBakedModel ibakedmodel = this.blockModelShapes.getModel(state);
            IBakedModel ibakedmodel1 = net.minecraftforge.client.ForgeHooksClient.getDamageModel(ibakedmodel, texture, state, blockAccess, pos);
            this.blockModelRenderer.func_178267_a(blockAccess, ibakedmodel1, state, pos, Tessellator.getInstance().getBuffer(), true);
        }
    }

    public boolean func_175018_a(IBlockState p_175018_1_, BlockPos p_175018_2_, IBlockAccess p_175018_3_, BufferBuilder p_175018_4_)
    {
        try
        {
            EnumBlockRenderType enumblockrendertype = p_175018_1_.getRenderType();

            if (enumblockrendertype == EnumBlockRenderType.INVISIBLE)
            {
                return false;
            }
            else
            {
                if (p_175018_3_.getWorldType() != WorldType.DEBUG_ALL_BLOCK_STATES)
                {
                    try
                    {
                        p_175018_1_ = p_175018_1_.func_185899_b(p_175018_3_, p_175018_2_);
                    }
                    catch (Exception var8)
                    {
                        ;
                    }
                }

                switch (enumblockrendertype)
                {
                    case MODEL:
                        IBakedModel model = this.getModelForState(p_175018_1_);
                        p_175018_1_ = p_175018_1_.getBlock().getExtendedState(p_175018_1_, p_175018_3_, p_175018_2_);
                        return this.blockModelRenderer.func_178267_a(p_175018_3_, model, p_175018_1_, p_175018_2_, p_175018_4_, true);
                    case ENTITYBLOCK_ANIMATED:
                        return false;
                    case LIQUID:
                        return this.fluidRenderer.func_178270_a(p_175018_3_, p_175018_1_, p_175018_2_, p_175018_4_);
                    default:
                        return false;
                }
            }
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
            CrashReportCategory.func_180523_a(crashreportcategory, p_175018_2_, p_175018_1_.getBlock(), p_175018_1_.getBlock().func_176201_c(p_175018_1_));
            throw new ReportedException(crashreport);
        }
    }

    public BlockModelRenderer getBlockModelRenderer()
    {
        return this.blockModelRenderer;
    }

    public IBakedModel getModelForState(IBlockState state)
    {
        return this.blockModelShapes.getModel(state);
    }

    @SuppressWarnings("incomplete-switch")
    public void renderBlockBrightness(IBlockState state, float brightness)
    {
        EnumBlockRenderType enumblockrendertype = state.getRenderType();

        if (enumblockrendertype != EnumBlockRenderType.INVISIBLE)
        {
            switch (enumblockrendertype)
            {
                case MODEL:
                    IBakedModel ibakedmodel = this.getModelForState(state);
                    this.blockModelRenderer.renderModelBrightness(ibakedmodel, state, brightness, true);
                    break;
                case ENTITYBLOCK_ANIMATED:
                    this.chestRenderer.renderChestBrightness(state.getBlock(), brightness);
                case LIQUID:
            }
        }
    }

    public void func_110549_a(IResourceManager p_110549_1_)
    {
        this.fluidRenderer.initAtlasSprites();
    }
}