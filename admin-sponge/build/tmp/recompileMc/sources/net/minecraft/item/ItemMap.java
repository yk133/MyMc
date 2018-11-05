package net.minecraft.item;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMap extends ItemMapBase
{
    protected ItemMap()
    {
        this.func_77627_a(true);
    }

    public static ItemStack func_190906_a(World p_190906_0_, double p_190906_1_, double p_190906_3_, byte p_190906_5_, boolean p_190906_6_, boolean p_190906_7_)
    {
        ItemStack itemstack = new ItemStack(Items.FILLED_MAP, 1, p_190906_0_.getUniqueDataId("map"));
        String s = "map_" + itemstack.func_77960_j();
        MapData mapdata = new MapData(s);
        p_190906_0_.setData(s, mapdata);
        mapdata.scale = p_190906_5_;
        mapdata.calculateMapCenter(p_190906_1_, p_190906_3_, mapdata.scale);
        mapdata.dimension = p_190906_0_.dimension.getDimension();
        mapdata.trackingPosition = p_190906_6_;
        mapdata.unlimitedTracking = p_190906_7_;
        mapdata.markDirty();
        return itemstack;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public static MapData func_150912_a(int p_150912_0_, World p_150912_1_)
    {
        String s = "map_" + p_150912_0_;
        return (MapData)p_150912_1_.func_72943_a(MapData.class, s);
    }

    @Nullable
    public MapData func_77873_a(ItemStack p_77873_1_, World p_77873_2_)
    {
        String s = "map_" + p_77873_1_.func_77960_j();
        MapData mapdata = (MapData)p_77873_2_.func_72943_a(MapData.class, s);

        if (mapdata == null && !p_77873_2_.isRemote)
        {
            p_77873_1_.func_77964_b(p_77873_2_.getUniqueDataId("map"));
            s = "map_" + p_77873_1_.func_77960_j();
            mapdata = new MapData(s);
            mapdata.scale = 3;
            mapdata.calculateMapCenter((double)p_77873_2_.getWorldInfo().getSpawnX(), (double)p_77873_2_.getWorldInfo().getSpawnZ(), mapdata.scale);
            mapdata.dimension = p_77873_2_.dimension.getDimension();
            mapdata.markDirty();
            p_77873_2_.setData(s, mapdata);
        }

        return mapdata;
    }

    public void updateMapData(World worldIn, Entity viewer, MapData data)
    {
        if (worldIn.dimension.getDimension() == data.dimension && viewer instanceof EntityPlayer)
        {
            int i = 1 << data.scale;
            int j = data.xCenter;
            int k = data.zCenter;
            int l = MathHelper.floor(viewer.posX - (double)j) / i + 64;
            int i1 = MathHelper.floor(viewer.posZ - (double)k) / i + 64;
            int j1 = 128 / i;

            if (worldIn.dimension.isNether())
            {
                j1 /= 2;
            }

            MapData.MapInfo mapdata$mapinfo = data.getMapInfo((EntityPlayer)viewer);
            ++mapdata$mapinfo.step;
            boolean flag = false;

            for (int k1 = l - j1 + 1; k1 < l + j1; ++k1)
            {
                if ((k1 & 15) == (mapdata$mapinfo.step & 15) || flag)
                {
                    flag = false;
                    double d0 = 0.0D;

                    for (int l1 = i1 - j1 - 1; l1 < i1 + j1; ++l1)
                    {
                        if (k1 >= 0 && l1 >= -1 && k1 < 128 && l1 < 128)
                        {
                            int i2 = k1 - l;
                            int j2 = l1 - i1;
                            boolean flag1 = i2 * i2 + j2 * j2 > (j1 - 2) * (j1 - 2);
                            int k2 = (j / i + k1 - 64) * i;
                            int l2 = (k / i + l1 - 64) * i;
                            Multiset<MapColor> multiset = HashMultiset.<MapColor>create();
                            Chunk chunk = worldIn.getChunk(new BlockPos(k2, 0, l2));

                            if (!chunk.isEmpty())
                            {
                                int i3 = k2 & 15;
                                int j3 = l2 & 15;
                                int k3 = 0;
                                double d1 = 0.0D;

                                if (worldIn.dimension.isNether())
                                {
                                    int l3 = k2 + l2 * 231871;
                                    l3 = l3 * l3 * 31287121 + l3 * 11;

                                    if ((l3 >> 20 & 1) == 0)
                                    {
                                        multiset.add(Blocks.DIRT.getDefaultState().func_177226_a(BlockDirt.field_176386_a, BlockDirt.DirtType.DIRT).getMapColor(worldIn, BlockPos.ORIGIN), 10);
                                    }
                                    else
                                    {
                                        multiset.add(Blocks.STONE.getDefaultState().func_177226_a(BlockStone.field_176247_a, BlockStone.EnumType.STONE).getMapColor(worldIn, BlockPos.ORIGIN), 100);
                                    }

                                    d1 = 100.0D;
                                }
                                else
                                {
                                    BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                                    for (int i4 = 0; i4 < i; ++i4)
                                    {
                                        for (int j4 = 0; j4 < i; ++j4)
                                        {
                                            int k4 = chunk.func_76611_b(i4 + i3, j4 + j3) + 1;
                                            IBlockState iblockstate = Blocks.AIR.getDefaultState();

                                            if (k4 <= 1)
                                            {
                                                iblockstate = Blocks.BEDROCK.getDefaultState();
                                            }
                                            else
                                            {
                                                label175:
                                                {
                                                    while (true)
                                                    {
                                                        --k4;
                                                        iblockstate = chunk.getBlockState(i4 + i3, k4, j4 + j3);
                                                        blockpos$mutableblockpos.setPos((chunk.x << 4) + i4 + i3, k4, (chunk.z << 4) + j4 + j3);

                                                        if (iblockstate.getMapColor(worldIn, blockpos$mutableblockpos) != MapColor.AIR || k4 <= 0)
                                                        {
                                                            break;
                                                        }
                                                    }

                                                    if (k4 > 0 && iblockstate.getMaterial().isLiquid())
                                                    {
                                                        int l4 = k4 - 1;

                                                        while (true)
                                                        {
                                                            IBlockState iblockstate1 = chunk.getBlockState(i4 + i3, l4--, j4 + j3);
                                                            ++k3;

                                                            if (l4 <= 0 || !iblockstate1.getMaterial().isLiquid())
                                                            {
                                                                break label175;
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            d1 += (double)k4 / (double)(i * i);
                                            multiset.add(iblockstate.getMapColor(worldIn, blockpos$mutableblockpos));
                                        }
                                    }
                                }

                                k3 = k3 / (i * i);
                                double d2 = (d1 - d0) * 4.0D / (double)(i + 4) + ((double)(k1 + l1 & 1) - 0.5D) * 0.4D;
                                int i5 = 1;

                                if (d2 > 0.6D)
                                {
                                    i5 = 2;
                                }

                                if (d2 < -0.6D)
                                {
                                    i5 = 0;
                                }

                                MapColor mapcolor = (MapColor)Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), MapColor.AIR);

                                if (mapcolor == MapColor.WATER)
                                {
                                    d2 = (double)k3 * 0.1D + (double)(k1 + l1 & 1) * 0.2D;
                                    i5 = 1;

                                    if (d2 < 0.5D)
                                    {
                                        i5 = 2;
                                    }

                                    if (d2 > 0.9D)
                                    {
                                        i5 = 0;
                                    }
                                }

                                d0 = d1;

                                if (l1 >= 0 && i2 * i2 + j2 * j2 < j1 * j1 && (!flag1 || (k1 + l1 & 1) != 0))
                                {
                                    byte b0 = data.colors[k1 + l1 * 128];
                                    byte b1 = (byte)(mapcolor.colorIndex * 4 + i5);

                                    if (b0 != b1)
                                    {
                                        data.colors[k1 + l1 * 128] = b1;
                                        data.updateMapData(k1, l1);
                                        flag = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Draws ambiguous landmasses representing unexplored terrain onto a treasure map
     */
    public static void renderBiomePreviewMap(World worldIn, ItemStack map)
    {
        if (map.getItem() instanceof ItemMap)
        {
            MapData mapdata = ((ItemMap) map.getItem()).func_77873_a(map, worldIn);

            if (mapdata != null)
            {
                if (worldIn.dimension.getDimension() == mapdata.dimension)
                {
                    int i = 1 << mapdata.scale;
                    int j = mapdata.xCenter;
                    int k = mapdata.zCenter;
                    Biome[] abiome = worldIn.func_72959_q().func_76931_a((Biome[])null, (j / i - 64) * i, (k / i - 64) * i, 128 * i, 128 * i, false);

                    for (int l = 0; l < 128; ++l)
                    {
                        for (int i1 = 0; i1 < 128; ++i1)
                        {
                            int j1 = l * i;
                            int k1 = i1 * i;
                            Biome biome = abiome[j1 + k1 * 128 * i];
                            MapColor mapcolor = MapColor.AIR;
                            int l1 = 3;
                            int i2 = 8;

                            if (l > 0 && i1 > 0 && l < 127 && i1 < 127)
                            {
                                if (abiome[(l - 1) * i + (i1 - 1) * i * 128 * i].getDepth() >= 0.0F)
                                {
                                    --i2;
                                }

                                if (abiome[(l - 1) * i + (i1 + 1) * i * 128 * i].getDepth() >= 0.0F)
                                {
                                    --i2;
                                }

                                if (abiome[(l - 1) * i + i1 * i * 128 * i].getDepth() >= 0.0F)
                                {
                                    --i2;
                                }

                                if (abiome[(l + 1) * i + (i1 - 1) * i * 128 * i].getDepth() >= 0.0F)
                                {
                                    --i2;
                                }

                                if (abiome[(l + 1) * i + (i1 + 1) * i * 128 * i].getDepth() >= 0.0F)
                                {
                                    --i2;
                                }

                                if (abiome[(l + 1) * i + i1 * i * 128 * i].getDepth() >= 0.0F)
                                {
                                    --i2;
                                }

                                if (abiome[l * i + (i1 - 1) * i * 128 * i].getDepth() >= 0.0F)
                                {
                                    --i2;
                                }

                                if (abiome[l * i + (i1 + 1) * i * 128 * i].getDepth() >= 0.0F)
                                {
                                    --i2;
                                }

                                if (biome.getDepth() < 0.0F)
                                {
                                    mapcolor = MapColor.ADOBE;

                                    if (i2 > 7 && i1 % 2 == 0)
                                    {
                                        l1 = (l + (int)(MathHelper.sin((float)i1 + 0.0F) * 7.0F)) / 8 % 5;

                                        if (l1 == 3)
                                        {
                                            l1 = 1;
                                        }
                                        else if (l1 == 4)
                                        {
                                            l1 = 0;
                                        }
                                    }
                                    else if (i2 > 7)
                                    {
                                        mapcolor = MapColor.AIR;
                                    }
                                    else if (i2 > 5)
                                    {
                                        l1 = 1;
                                    }
                                    else if (i2 > 3)
                                    {
                                        l1 = 0;
                                    }
                                    else if (i2 > 1)
                                    {
                                        l1 = 0;
                                    }
                                }
                                else if (i2 > 0)
                                {
                                    mapcolor = MapColor.BROWN;

                                    if (i2 > 3)
                                    {
                                        l1 = 1;
                                    }
                                    else
                                    {
                                        l1 = 3;
                                    }
                                }
                            }

                            if (mapcolor != MapColor.AIR)
                            {
                                mapdata.colors[l + i1 * 128] = (byte)(mapcolor.colorIndex * 4 + l1);
                                mapdata.updateMapData(l, i1);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        if (!worldIn.isRemote)
        {
            MapData mapdata = this.func_77873_a(stack, worldIn);

            if (entityIn instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)entityIn;
                mapdata.updateVisiblePlayers(entityplayer, stack);
            }

            if (isSelected || entityIn instanceof EntityPlayer && ((EntityPlayer)entityIn).getHeldItemOffhand() == stack)
            {
                this.updateMapData(worldIn, entityIn, mapdata);
            }
        }
    }

    @Nullable
    public Packet<?> getUpdatePacket(ItemStack stack, World worldIn, EntityPlayer player)
    {
        return this.func_77873_a(stack, worldIn).getMapPacket(stack, worldIn, player);
    }

    /**
     * Called when item is crafted/smelted. Used only by maps so far.
     */
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn)
    {
        NBTTagCompound nbttagcompound = stack.getTag();

        if (nbttagcompound != null)
        {
            if (nbttagcompound.contains("map_scale_direction", 99))
            {
                scaleMap(stack, worldIn, nbttagcompound.getInt("map_scale_direction"));
                nbttagcompound.remove("map_scale_direction");
            }
            else if (nbttagcompound.getBoolean("map_tracking_position"))
            {
                func_185064_b(stack, worldIn);
                nbttagcompound.remove("map_tracking_position");
            }
        }
    }

    protected static void scaleMap(ItemStack p_185063_0_, World p_185063_1_, int p_185063_2_)
    {
        MapData mapdata = Items.FILLED_MAP.func_77873_a(p_185063_0_, p_185063_1_);
        p_185063_0_.func_77964_b(p_185063_1_.getUniqueDataId("map"));
        MapData mapdata1 = new MapData("map_" + p_185063_0_.func_77960_j());

        if (mapdata != null)
        {
            mapdata1.scale = (byte)MathHelper.clamp(mapdata.scale + p_185063_2_, 0, 4);
            mapdata1.trackingPosition = mapdata.trackingPosition;
            mapdata1.calculateMapCenter((double)mapdata.xCenter, (double)mapdata.zCenter, mapdata1.scale);
            mapdata1.dimension = mapdata.dimension;
            mapdata1.markDirty();
            p_185063_1_.setData("map_" + p_185063_0_.func_77960_j(), mapdata1);
        }
    }

    protected static void func_185064_b(ItemStack p_185064_0_, World p_185064_1_)
    {
        MapData mapdata = Items.FILLED_MAP.func_77873_a(p_185064_0_, p_185064_1_);
        p_185064_0_.func_77964_b(p_185064_1_.getUniqueDataId("map"));
        MapData mapdata1 = new MapData("map_" + p_185064_0_.func_77960_j());
        mapdata1.trackingPosition = true;

        if (mapdata != null)
        {
            mapdata1.xCenter = mapdata.xCenter;
            mapdata1.zCenter = mapdata.zCenter;
            mapdata1.scale = mapdata.scale;
            mapdata1.dimension = mapdata.dimension;
            mapdata1.markDirty();
            p_185064_1_.setData("map_" + p_185064_0_.func_77960_j(), mapdata1);
        }
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (flagIn.isAdvanced())
        {
            MapData mapdata = worldIn == null ? null : this.func_77873_a(stack, worldIn);

            if (mapdata != null)
            {
                tooltip.add(I18n.func_74837_a("filled_map.scale", 1 << mapdata.scale));
                tooltip.add(I18n.func_74837_a("filled_map.level", mapdata.scale, Integer.valueOf(4)));
            }
            else
            {
                tooltip.add(I18n.func_74838_a("filled_map.unknown"));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static int getColor(ItemStack p_190907_0_)
    {
        NBTTagCompound nbttagcompound = p_190907_0_.getChildTag("display");

        if (nbttagcompound != null && nbttagcompound.contains("MapColor", 99))
        {
            int i = nbttagcompound.getInt("MapColor");
            return -16777216 | i & 16777215;
        }
        else
        {
            return -12173266;
        }
    }
}