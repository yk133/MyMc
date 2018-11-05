package net.minecraft.block.state;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBlockProperties
{
    Material getMaterial();

    boolean func_185913_b();

    boolean canEntitySpawn(Entity entityIn);

    @Deprecated //Forge location aware version below
    int func_185891_c();
    int getLightOpacity(IBlockAccess world, BlockPos pos);

    @Deprecated //Forge location aware version below
    int getLightValue();
    int getLightValue(IBlockAccess world, BlockPos pos);

    @SideOnly(Side.CLIENT)
    boolean func_185895_e();

    boolean func_185916_f();

    MapColor getMapColor(IBlockAccess worldIn, BlockPos pos);

    /**
     * Returns the blockstate with the given rotation. If inapplicable, returns itself.
     */
    IBlockState rotate(Rotation rot);

    /**
     * Returns the blockstate mirrored in the given way. If inapplicable, returns itself.
     */
    IBlockState mirror(Mirror mirrorIn);

    boolean isFullCube();

    @SideOnly(Side.CLIENT)
    boolean hasCustomBreakingProgress();

    EnumBlockRenderType getRenderType();

    @SideOnly(Side.CLIENT)
    int getPackedLightmapCoords(IBlockAccess source, BlockPos pos);

    @SideOnly(Side.CLIENT)
    float getAmbientOcclusionLightValue();

    boolean isBlockNormalCube();

    boolean isNormalCube();

    boolean canProvidePower();

    int getWeakPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side);

    boolean hasComparatorInputOverride();

    int getComparatorInputOverride(World worldIn, BlockPos pos);

    float getBlockHardness(World worldIn, BlockPos pos);

    float getPlayerRelativeBlockHardness(EntityPlayer player, World worldIn, BlockPos pos);

    int getStrongPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side);

    EnumPushReaction getPushReaction();

    IBlockState func_185899_b(IBlockAccess p_185899_1_, BlockPos p_185899_2_);

    @SideOnly(Side.CLIENT)
    AxisAlignedBB func_185918_c(World p_185918_1_, BlockPos p_185918_2_);

    @SideOnly(Side.CLIENT)
    boolean func_185894_c(IBlockAccess p_185894_1_, BlockPos p_185894_2_, EnumFacing p_185894_3_);

    boolean func_185914_p();

    @Nullable
    AxisAlignedBB func_185890_d(IBlockAccess p_185890_1_, BlockPos p_185890_2_);

    void func_185908_a(World p_185908_1_, BlockPos p_185908_2_, AxisAlignedBB p_185908_3_, List<AxisAlignedBB> p_185908_4_, @Nullable Entity p_185908_5_, boolean p_185908_6_);

    AxisAlignedBB func_185900_c(IBlockAccess p_185900_1_, BlockPos p_185900_2_);

    RayTraceResult func_185910_a(World p_185910_1_, BlockPos p_185910_2_, Vec3d p_185910_3_, Vec3d p_185910_4_);

    /**
     * Determines if the block is solid enough on the top side to support other blocks, like redstone components.
     */
    @Deprecated // Forge: Use isSideSolid(IBlockAccess, BlockPos, EnumFacing.UP) instead
    boolean isTopSolid();

    //Forge added functions
    boolean doesSideBlockRendering(IBlockAccess world, BlockPos pos, EnumFacing side);
    boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side);
    boolean doesSideBlockChestOpening(IBlockAccess world, BlockPos pos, EnumFacing side);

    Vec3d getOffset(IBlockAccess access, BlockPos pos);

    boolean causesSuffocation();

    BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockPos pos, EnumFacing facing);
}