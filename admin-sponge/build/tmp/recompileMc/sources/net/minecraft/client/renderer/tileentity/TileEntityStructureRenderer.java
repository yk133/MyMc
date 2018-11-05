package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityStructureRenderer extends TileEntitySpecialRenderer<TileEntityStructure>
{
    public void func_192841_a(TileEntityStructure p_192841_1_, double p_192841_2_, double p_192841_4_, double p_192841_6_, float p_192841_8_, int p_192841_9_, float p_192841_10_)
    {
        if (Minecraft.getInstance().player.func_189808_dh() || Minecraft.getInstance().player.isSpectator())
        {
            super.func_192841_a(p_192841_1_, p_192841_2_, p_192841_4_, p_192841_6_, p_192841_8_, p_192841_9_, p_192841_10_);
            BlockPos blockpos = p_192841_1_.getPosition();
            BlockPos blockpos1 = p_192841_1_.getStructureSize();

            if (blockpos1.getX() >= 1 && blockpos1.getY() >= 1 && blockpos1.getZ() >= 1)
            {
                if (p_192841_1_.getMode() == TileEntityStructure.Mode.SAVE || p_192841_1_.getMode() == TileEntityStructure.Mode.LOAD)
                {
                    double d0 = 0.01D;
                    double d1 = (double)blockpos.getX();
                    double d2 = (double)blockpos.getZ();
                    double d6 = p_192841_4_ + (double)blockpos.getY() - 0.01D;
                    double d9 = d6 + (double)blockpos1.getY() + 0.02D;
                    double d3;
                    double d4;

                    switch (p_192841_1_.getMirror())
                    {
                        case LEFT_RIGHT:
                            d3 = (double)blockpos1.getX() + 0.02D;
                            d4 = -((double)blockpos1.getZ() + 0.02D);
                            break;
                        case FRONT_BACK:
                            d3 = -((double)blockpos1.getX() + 0.02D);
                            d4 = (double)blockpos1.getZ() + 0.02D;
                            break;
                        default:
                            d3 = (double)blockpos1.getX() + 0.02D;
                            d4 = (double)blockpos1.getZ() + 0.02D;
                    }

                    double d5;
                    double d7;
                    double d8;
                    double d10;

                    switch (p_192841_1_.getRotation())
                    {
                        case CLOCKWISE_90:
                            d5 = p_192841_2_ + (d4 < 0.0D ? d1 - 0.01D : d1 + 1.0D + 0.01D);
                            d7 = p_192841_6_ + (d3 < 0.0D ? d2 + 1.0D + 0.01D : d2 - 0.01D);
                            d8 = d5 - d4;
                            d10 = d7 + d3;
                            break;
                        case CLOCKWISE_180:
                            d5 = p_192841_2_ + (d3 < 0.0D ? d1 - 0.01D : d1 + 1.0D + 0.01D);
                            d7 = p_192841_6_ + (d4 < 0.0D ? d2 - 0.01D : d2 + 1.0D + 0.01D);
                            d8 = d5 - d3;
                            d10 = d7 - d4;
                            break;
                        case COUNTERCLOCKWISE_90:
                            d5 = p_192841_2_ + (d4 < 0.0D ? d1 + 1.0D + 0.01D : d1 - 0.01D);
                            d7 = p_192841_6_ + (d3 < 0.0D ? d2 - 0.01D : d2 + 1.0D + 0.01D);
                            d8 = d5 + d4;
                            d10 = d7 - d3;
                            break;
                        default:
                            d5 = p_192841_2_ + (d3 < 0.0D ? d1 + 1.0D + 0.01D : d1 - 0.01D);
                            d7 = p_192841_6_ + (d4 < 0.0D ? d2 + 1.0D + 0.01D : d2 - 0.01D);
                            d8 = d5 + d3;
                            d10 = d7 + d4;
                    }

                    int i = 255;
                    int j = 223;
                    int k = 127;
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder bufferbuilder = tessellator.getBuffer();
                    GlStateManager.disableFog();
                    GlStateManager.disableLighting();
                    GlStateManager.disableTexture2D();
                    GlStateManager.enableBlend();
                    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    this.setLightmapDisabled(true);

                    if (p_192841_1_.getMode() == TileEntityStructure.Mode.SAVE || p_192841_1_.showsBoundingBox())
                    {
                        this.renderBox(tessellator, bufferbuilder, d5, d6, d7, d8, d9, d10, 255, 223, 127);
                    }

                    if (p_192841_1_.getMode() == TileEntityStructure.Mode.SAVE && p_192841_1_.showsAir())
                    {
                        this.renderInvisibleBlocks(p_192841_1_, p_192841_2_, p_192841_4_, p_192841_6_, blockpos, tessellator, bufferbuilder, true);
                        this.renderInvisibleBlocks(p_192841_1_, p_192841_2_, p_192841_4_, p_192841_6_, blockpos, tessellator, bufferbuilder, false);
                    }

                    this.setLightmapDisabled(false);
                    GlStateManager.lineWidth(1.0F);
                    GlStateManager.enableLighting();
                    GlStateManager.enableTexture2D();
                    GlStateManager.enableDepthTest();
                    GlStateManager.depthMask(true);
                    GlStateManager.enableFog();
                }
            }
        }
    }

    private void renderInvisibleBlocks(TileEntityStructure p_190054_1_, double p_190054_2_, double p_190054_4_, double p_190054_6_, BlockPos p_190054_8_, Tessellator p_190054_9_, BufferBuilder p_190054_10_, boolean p_190054_11_)
    {
        GlStateManager.lineWidth(p_190054_11_ ? 3.0F : 1.0F);
        p_190054_10_.begin(3, DefaultVertexFormats.POSITION_COLOR);
        World world = p_190054_1_.getWorld();
        BlockPos blockpos = p_190054_1_.getPos();
        BlockPos blockpos1 = blockpos.add(p_190054_8_);

        for (BlockPos blockpos2 : BlockPos.getAllInBox(blockpos1, blockpos1.add(p_190054_1_.getStructureSize()).add(-1, -1, -1)))
        {
            IBlockState iblockstate = world.getBlockState(blockpos2);
            boolean flag = iblockstate == Blocks.AIR.getDefaultState();
            boolean flag1 = iblockstate == Blocks.STRUCTURE_VOID.getDefaultState();

            if (flag || flag1)
            {
                float f = flag ? 0.05F : 0.0F;
                double d0 = (double)((float)(blockpos2.getX() - blockpos.getX()) + 0.45F) + p_190054_2_ - (double)f;
                double d1 = (double)((float)(blockpos2.getY() - blockpos.getY()) + 0.45F) + p_190054_4_ - (double)f;
                double d2 = (double)((float)(blockpos2.getZ() - blockpos.getZ()) + 0.45F) + p_190054_6_ - (double)f;
                double d3 = (double)((float)(blockpos2.getX() - blockpos.getX()) + 0.55F) + p_190054_2_ + (double)f;
                double d4 = (double)((float)(blockpos2.getY() - blockpos.getY()) + 0.55F) + p_190054_4_ + (double)f;
                double d5 = (double)((float)(blockpos2.getZ() - blockpos.getZ()) + 0.55F) + p_190054_6_ + (double)f;

                if (p_190054_11_)
                {
                    RenderGlobal.drawBoundingBox(p_190054_10_, d0, d1, d2, d3, d4, d5, 0.0F, 0.0F, 0.0F, 1.0F);
                }
                else if (flag)
                {
                    RenderGlobal.drawBoundingBox(p_190054_10_, d0, d1, d2, d3, d4, d5, 0.5F, 0.5F, 1.0F, 1.0F);
                }
                else
                {
                    RenderGlobal.drawBoundingBox(p_190054_10_, d0, d1, d2, d3, d4, d5, 1.0F, 0.25F, 0.25F, 1.0F);
                }
            }
        }

        p_190054_9_.draw();
    }

    private void renderBox(Tessellator p_190055_1_, BufferBuilder p_190055_2_, double p_190055_3_, double p_190055_5_, double p_190055_7_, double p_190055_9_, double p_190055_11_, double p_190055_13_, int p_190055_15_, int p_190055_16_, int p_190055_17_)
    {
        GlStateManager.lineWidth(2.0F);
        p_190055_2_.begin(3, DefaultVertexFormats.POSITION_COLOR);
        p_190055_2_.pos(p_190055_3_, p_190055_5_, p_190055_7_).color((float)p_190055_16_, (float)p_190055_16_, (float)p_190055_16_, 0.0F).endVertex();
        p_190055_2_.pos(p_190055_3_, p_190055_5_, p_190055_7_).color(p_190055_16_, p_190055_16_, p_190055_16_, p_190055_15_).endVertex();
        p_190055_2_.pos(p_190055_9_, p_190055_5_, p_190055_7_).color(p_190055_16_, p_190055_17_, p_190055_17_, p_190055_15_).endVertex();
        p_190055_2_.pos(p_190055_9_, p_190055_5_, p_190055_13_).color(p_190055_16_, p_190055_16_, p_190055_16_, p_190055_15_).endVertex();
        p_190055_2_.pos(p_190055_3_, p_190055_5_, p_190055_13_).color(p_190055_16_, p_190055_16_, p_190055_16_, p_190055_15_).endVertex();
        p_190055_2_.pos(p_190055_3_, p_190055_5_, p_190055_7_).color(p_190055_17_, p_190055_17_, p_190055_16_, p_190055_15_).endVertex();
        p_190055_2_.pos(p_190055_3_, p_190055_11_, p_190055_7_).color(p_190055_17_, p_190055_16_, p_190055_17_, p_190055_15_).endVertex();
        p_190055_2_.pos(p_190055_9_, p_190055_11_, p_190055_7_).color(p_190055_16_, p_190055_16_, p_190055_16_, p_190055_15_).endVertex();
        p_190055_2_.pos(p_190055_9_, p_190055_11_, p_190055_13_).color(p_190055_16_, p_190055_16_, p_190055_16_, p_190055_15_).endVertex();
        p_190055_2_.pos(p_190055_3_, p_190055_11_, p_190055_13_).color(p_190055_16_, p_190055_16_, p_190055_16_, p_190055_15_).endVertex();
        p_190055_2_.pos(p_190055_3_, p_190055_11_, p_190055_7_).color(p_190055_16_, p_190055_16_, p_190055_16_, p_190055_15_).endVertex();
        p_190055_2_.pos(p_190055_3_, p_190055_11_, p_190055_13_).color(p_190055_16_, p_190055_16_, p_190055_16_, p_190055_15_).endVertex();
        p_190055_2_.pos(p_190055_3_, p_190055_5_, p_190055_13_).color(p_190055_16_, p_190055_16_, p_190055_16_, p_190055_15_).endVertex();
        p_190055_2_.pos(p_190055_9_, p_190055_5_, p_190055_13_).color(p_190055_16_, p_190055_16_, p_190055_16_, p_190055_15_).endVertex();
        p_190055_2_.pos(p_190055_9_, p_190055_11_, p_190055_13_).color(p_190055_16_, p_190055_16_, p_190055_16_, p_190055_15_).endVertex();
        p_190055_2_.pos(p_190055_9_, p_190055_11_, p_190055_7_).color(p_190055_16_, p_190055_16_, p_190055_16_, p_190055_15_).endVertex();
        p_190055_2_.pos(p_190055_9_, p_190055_5_, p_190055_7_).color(p_190055_16_, p_190055_16_, p_190055_16_, p_190055_15_).endVertex();
        p_190055_2_.pos(p_190055_9_, p_190055_5_, p_190055_7_).color((float)p_190055_16_, (float)p_190055_16_, (float)p_190055_16_, 0.0F).endVertex();
        p_190055_1_.draw();
        GlStateManager.lineWidth(1.0F);
    }

    public boolean isGlobalRenderer(TileEntityStructure te)
    {
        return true;
    }
}