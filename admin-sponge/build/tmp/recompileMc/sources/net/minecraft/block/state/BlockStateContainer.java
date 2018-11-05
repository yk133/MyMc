package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MapPopulator;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Cartesian;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStateContainer
{
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
    private static final Function < IProperty<?>, String > field_177626_b = new Function < IProperty<?>, String > ()
    {
        @Nullable
        public String apply(@Nullable IProperty<?> p_apply_1_)
        {
            return p_apply_1_ == null ? "<NULL>" : p_apply_1_.getName();
        }
    };
    private final Block owner;
    private final ImmutableSortedMap < String, IProperty<? >> properties;
    private final ImmutableList<IBlockState> validStates;

    public BlockStateContainer(Block p_i45663_1_, IProperty<?>... p_i45663_2_)
    {
        this(p_i45663_1_, p_i45663_2_, null);
    }

    protected StateImplementation createState(Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties, @Nullable ImmutableMap<net.minecraftforge.common.property.IUnlistedProperty<?>, java.util.Optional<?>> unlistedProperties)
    {
        return new StateImplementation(block, properties);
    }

    protected BlockStateContainer(Block blockIn, IProperty<?>[] properties, ImmutableMap<net.minecraftforge.common.property.IUnlistedProperty<?>, java.util.Optional<?>> unlistedProperties)
    {
        this.owner = blockIn;
        Map < String, IProperty<? >> map = Maps. < String, IProperty<? >> newHashMap();

        for (IProperty<?> iproperty : properties)
        {
            func_185919_a(blockIn, iproperty);
            map.put(iproperty.getName(), iproperty);
        }

        this.properties = ImmutableSortedMap.copyOf(map);
        Map < Map < IProperty<?>, Comparable<? >> , BlockStateContainer.StateImplementation > map2 = Maps. < Map < IProperty<?>, Comparable<? >> , BlockStateContainer.StateImplementation > newLinkedHashMap();
        List<BlockStateContainer.StateImplementation> list1 = Lists.<BlockStateContainer.StateImplementation>newArrayList();

        for (List < Comparable<? >> list : Cartesian.func_179321_a(this.func_177620_e()))
        {
            Map < IProperty<?>, Comparable<? >> map1 = MapPopulator. < IProperty<?>, Comparable<? >> createMap(this.properties.values(), list);
            BlockStateContainer.StateImplementation blockstatecontainer$stateimplementation = createState(blockIn, ImmutableMap.copyOf(map1), unlistedProperties);
            map2.put(map1, blockstatecontainer$stateimplementation);
            list1.add(blockstatecontainer$stateimplementation);
        }

        for (BlockStateContainer.StateImplementation blockstatecontainer$stateimplementation1 : list1)
        {
            blockstatecontainer$stateimplementation1.func_177235_a(map2);
        }

        this.validStates = ImmutableList.<IBlockState>copyOf(list1);
    }

    public static <T extends Comparable<T>> String func_185919_a(Block p_185919_0_, IProperty<T> p_185919_1_)
    {
        String s = p_185919_1_.getName();

        if (!NAME_PATTERN.matcher(s).matches())
        {
            throw new IllegalArgumentException("Block: " + p_185919_0_.getClass() + " has invalidly named property: " + s);
        }
        else
        {
            for (T t : p_185919_1_.getAllowedValues())
            {
                String s1 = p_185919_1_.getName(t);

                if (!NAME_PATTERN.matcher(s1).matches())
                {
                    throw new IllegalArgumentException("Block: " + p_185919_0_.getClass() + " has property: " + s + " with invalidly named value: " + s1);
                }
            }

            return s;
        }
    }

    public ImmutableList<IBlockState> getValidStates()
    {
        return this.validStates;
    }

    private List < Iterable < Comparable<? >>> func_177620_e()
    {
        List < Iterable < Comparable<? >>> list = Lists. < Iterable < Comparable<? >>> newArrayList();
        ImmutableCollection < IProperty<? >> immutablecollection = this.properties.values();
        UnmodifiableIterator unmodifiableiterator = immutablecollection.iterator();

        while (unmodifiableiterator.hasNext())
        {
            IProperty<?> iproperty = (IProperty)unmodifiableiterator.next();
            list.add(((IProperty)iproperty).getAllowedValues());
        }

        return list;
    }

    public IBlockState getBaseState()
    {
        return (IBlockState)this.validStates.get(0);
    }

    public Block getOwner()
    {
        return this.owner;
    }

    public Collection < IProperty<? >> getProperties()
    {
        return this.properties.values();
    }

    public String toString()
    {
        return MoreObjects.toStringHelper(this).add("block", Block.REGISTRY.getKey(this.owner)).add("properties", Iterables.transform(this.properties.values(), field_177626_b)).toString();
    }

    @Nullable
    public IProperty<?> getProperty(String propertyName)
    {
        return (IProperty)this.properties.get(propertyName);
    }

    public static class StateImplementation extends BlockStateBase
        {
            private final Block field_177239_a;
            private final ImmutableMap < IProperty<?>, Comparable<? >> field_177237_b;
            protected ImmutableTable < IProperty<?>, Comparable<?>, IBlockState > field_177238_c;

            protected StateImplementation(Block p_i45660_1_, ImmutableMap < IProperty<?>, Comparable<? >> p_i45660_2_)
            {
                this.field_177239_a = p_i45660_1_;
                this.field_177237_b = p_i45660_2_;
            }

            protected StateImplementation(Block blockIn, ImmutableMap<IProperty<?>, Comparable<?>> propertiesIn, ImmutableTable<IProperty<?>, Comparable<?>, IBlockState> propertyValueTable)
            {
                this.field_177239_a = blockIn;
                this.field_177237_b = propertiesIn;
                this.field_177238_c = propertyValueTable;
            }

            public Collection < IProperty<? >> func_177227_a()
            {
                return Collections. < IProperty<? >> unmodifiableCollection(this.field_177237_b.keySet());
            }

            /**
             * Get the value of the given Property for this BlockState
             */
            public <T extends Comparable<T>> T get(IProperty<T> property)
            {
                Comparable<?> comparable = (Comparable)this.field_177237_b.get(property);

                if (comparable == null)
                {
                    throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.field_177239_a.getStateContainer());
                }
                else
                {
                    return (T)(property.getValueClass().cast(comparable));
                }
            }

            public <T extends Comparable<T>, V extends T> IBlockState func_177226_a(IProperty<T> p_177226_1_, V p_177226_2_)
            {
                Comparable<?> comparable = (Comparable)this.field_177237_b.get(p_177226_1_);

                if (comparable == null)
                {
                    throw new IllegalArgumentException("Cannot set property " + p_177226_1_ + " as it does not exist in " + this.field_177239_a.getStateContainer());
                }
                else if (comparable == p_177226_2_)
                {
                    return this;
                }
                else
                {
                    IBlockState iblockstate = (IBlockState)this.field_177238_c.get(p_177226_1_, p_177226_2_);

                    if (iblockstate == null)
                    {
                        throw new IllegalArgumentException("Cannot set property " + p_177226_1_ + " to " + p_177226_2_ + " on block " + Block.REGISTRY.getKey(this.field_177239_a) + ", it is not an allowed value");
                    }
                    else
                    {
                        return iblockstate;
                    }
                }
            }

            public ImmutableMap < IProperty<?>, Comparable<? >> func_177228_b()
            {
                return this.field_177237_b;
            }

            public Block getBlock()
            {
                return this.field_177239_a;
            }

            public boolean equals(Object p_equals_1_)
            {
                return this == p_equals_1_;
            }

            public int hashCode()
            {
                return this.field_177237_b.hashCode();
            }

            public void func_177235_a(Map < Map < IProperty<?>, Comparable<? >> , BlockStateContainer.StateImplementation > p_177235_1_)
            {
                if (this.field_177238_c != null)
                {
                    throw new IllegalStateException();
                }
                else
                {
                    Table < IProperty<?>, Comparable<?>, IBlockState > table = HashBasedTable. < IProperty<?>, Comparable<?>, IBlockState > create();
                    UnmodifiableIterator unmodifiableiterator = this.field_177237_b.entrySet().iterator();

                    while (unmodifiableiterator.hasNext())
                    {
                        Entry < IProperty<?>, Comparable<? >> entry = (Entry)unmodifiableiterator.next();
                        IProperty<?> iproperty = (IProperty)entry.getKey();

                        for (Comparable<?> comparable : iproperty.getAllowedValues())
                        {
                            if (comparable != entry.getValue())
                            {
                                table.put(iproperty, comparable, p_177235_1_.get(this.func_177236_b(iproperty, comparable)));
                            }
                        }
                    }

                    this.field_177238_c = ImmutableTable.copyOf(table);
                }
            }

            private Map < IProperty<?>, Comparable<? >> func_177236_b(IProperty<?> p_177236_1_, Comparable<?> p_177236_2_)
            {
                Map < IProperty<?>, Comparable<? >> map = Maps. < IProperty<?>, Comparable<? >> newHashMap(this.field_177237_b);
                map.put(p_177236_1_, p_177236_2_);
                return map;
            }

            public Material getMaterial()
            {
                return this.field_177239_a.getMaterial(this);
            }

            public boolean func_185913_b()
            {
                return this.field_177239_a.func_149730_j(this);
            }

            public boolean canEntitySpawn(Entity entityIn)
            {
                return this.field_177239_a.canEntitySpawn(this, entityIn);
            }

            public int func_185891_c()
            {
                return this.field_177239_a.func_149717_k(this);
            }

            public int getLightValue()
            {
                return this.field_177239_a.getLightValue(this);
            }

            @SideOnly(Side.CLIENT)
            public boolean func_185895_e()
            {
                return this.field_177239_a.func_149751_l(this);
            }

            public boolean func_185916_f()
            {
                return this.field_177239_a.func_149710_n(this);
            }

            public MapColor getMapColor(IBlockAccess worldIn, BlockPos pos)
            {
                return this.field_177239_a.getMapColor(this, worldIn, pos);
            }

            /**
             * Returns the blockstate with the given rotation. If inapplicable, returns itself.
             */
            public IBlockState rotate(Rotation rot)
            {
                return this.field_177239_a.rotate(this, rot);
            }

            /**
             * Returns the blockstate mirrored in the given way. If inapplicable, returns itself.
             */
            public IBlockState mirror(Mirror mirrorIn)
            {
                return this.field_177239_a.mirror(this, mirrorIn);
            }

            public boolean isFullCube()
            {
                return this.field_177239_a.isFullCube(this);
            }

            @SideOnly(Side.CLIENT)
            public boolean hasCustomBreakingProgress()
            {
                return this.field_177239_a.hasCustomBreakingProgress(this);
            }

            public EnumBlockRenderType getRenderType()
            {
                return this.field_177239_a.getRenderType(this);
            }

            @SideOnly(Side.CLIENT)
            public int getPackedLightmapCoords(IBlockAccess source, BlockPos pos)
            {
                return this.field_177239_a.getPackedLightmapCoords(this, source, pos);
            }

            @SideOnly(Side.CLIENT)
            public float getAmbientOcclusionLightValue()
            {
                return this.field_177239_a.getAmbientOcclusionLightValue(this);
            }

            public boolean isBlockNormalCube()
            {
                return this.field_177239_a.isBlockNormalCube(this);
            }

            public boolean isNormalCube()
            {
                return this.field_177239_a.isNormalCube(this);
            }

            public boolean canProvidePower()
            {
                return this.field_177239_a.canProvidePower(this);
            }

            public int getWeakPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
            {
                return this.field_177239_a.getWeakPower(this, blockAccess, pos, side);
            }

            public boolean hasComparatorInputOverride()
            {
                return this.field_177239_a.hasComparatorInputOverride(this);
            }

            public int getComparatorInputOverride(World worldIn, BlockPos pos)
            {
                return this.field_177239_a.getComparatorInputOverride(this, worldIn, pos);
            }

            public float getBlockHardness(World worldIn, BlockPos pos)
            {
                return this.field_177239_a.getBlockHardness(this, worldIn, pos);
            }

            public float getPlayerRelativeBlockHardness(EntityPlayer player, World worldIn, BlockPos pos)
            {
                return this.field_177239_a.getPlayerRelativeBlockHardness(this, player, worldIn, pos);
            }

            public int getStrongPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
            {
                return this.field_177239_a.getStrongPower(this, blockAccess, pos, side);
            }

            public EnumPushReaction getPushReaction()
            {
                return this.field_177239_a.getPushReaction(this);
            }

            public IBlockState func_185899_b(IBlockAccess p_185899_1_, BlockPos p_185899_2_)
            {
                return this.field_177239_a.func_176221_a(this, p_185899_1_, p_185899_2_);
            }

            @SideOnly(Side.CLIENT)
            public AxisAlignedBB func_185918_c(World p_185918_1_, BlockPos p_185918_2_)
            {
                return this.field_177239_a.func_180640_a(this, p_185918_1_, p_185918_2_);
            }

            @SideOnly(Side.CLIENT)
            public boolean func_185894_c(IBlockAccess p_185894_1_, BlockPos p_185894_2_, EnumFacing p_185894_3_)
            {
                return this.field_177239_a.shouldSideBeRendered(this, p_185894_1_, p_185894_2_, p_185894_3_);
            }

            public boolean func_185914_p()
            {
                return this.field_177239_a.func_149662_c(this);
            }

            @Nullable
            public AxisAlignedBB func_185890_d(IBlockAccess p_185890_1_, BlockPos p_185890_2_)
            {
                return this.field_177239_a.func_180646_a(this, p_185890_1_, p_185890_2_);
            }

            public void func_185908_a(World p_185908_1_, BlockPos p_185908_2_, AxisAlignedBB p_185908_3_, List<AxisAlignedBB> p_185908_4_, @Nullable Entity p_185908_5_, boolean p_185908_6_)
            {
                this.field_177239_a.func_185477_a(this, p_185908_1_, p_185908_2_, p_185908_3_, p_185908_4_, p_185908_5_, p_185908_6_);
            }

            public AxisAlignedBB func_185900_c(IBlockAccess p_185900_1_, BlockPos p_185900_2_)
            {
                return this.field_177239_a.func_185496_a(this, p_185900_1_, p_185900_2_);
            }

            public RayTraceResult func_185910_a(World p_185910_1_, BlockPos p_185910_2_, Vec3d p_185910_3_, Vec3d p_185910_4_)
            {
                return this.field_177239_a.collisionRayTrace(this, p_185910_1_, p_185910_2_, p_185910_3_, p_185910_4_);
            }

            /**
             * Determines if the block is solid enough on the top side to support other blocks, like redstone
             * components.
             */
            public boolean isTopSolid()
            {
                return this.field_177239_a.isTopSolid(this);
            }

            public Vec3d getOffset(IBlockAccess access, BlockPos pos)
            {
                return this.field_177239_a.getOffset(this, access, pos);
            }

            /**
             * Called on both Client and Server when World#addBlockEvent is called. On the Server, this may perform
             * additional changes to the world, like pistons replacing the block with an extended base. On the client,
             * the update may involve replacing tile entities, playing sounds, or performing other visual actions to
             * reflect the server side changes.
             */
            public boolean onBlockEventReceived(World worldIn, BlockPos pos, int id, int param)
            {
                return this.field_177239_a.eventReceived(this, worldIn, pos, id, param);
            }

            /**
             * Called when a neighboring block was changed and marks that this state should perform any checks during a
             * neighbor change. Cases may include when redstone power is updated, cactus blocks popping off due to a
             * neighboring solid block, etc.
             */
            public void neighborChanged(World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
            {
                this.field_177239_a.neighborChanged(this, worldIn, pos, blockIn, fromPos);
            }

            public boolean causesSuffocation()
            {
                return this.field_177239_a.causesSuffocation(this);
            }

            public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockPos pos, EnumFacing facing)
            {
                return this.field_177239_a.getBlockFaceShape(worldIn, this, pos, facing);
            }

            //Forge Start
            @Override
            public ImmutableTable<IProperty<?>, Comparable<?>, IBlockState> getPropertyValueTable()
            {
                return field_177238_c;
            }

            @Override
            public int getLightOpacity(IBlockAccess world, BlockPos pos)
            {
                return this.field_177239_a.getLightOpacity(this, world, pos);
            }

            @Override
            public int getLightValue(IBlockAccess world, BlockPos pos)
            {
                return this.field_177239_a.getLightValue(this, world, pos);
            }

            @Override
            public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side)
            {
                return this.field_177239_a.isSideSolid(this, world, pos, side);
            }

            @Override
            public boolean doesSideBlockChestOpening(IBlockAccess world, BlockPos pos, EnumFacing side)
            {
                return this.field_177239_a.doesSideBlockChestOpening(this, world, pos, side);
            }

            @Override
            public boolean doesSideBlockRendering(IBlockAccess world, BlockPos pos, EnumFacing side)
            {
                return this.field_177239_a.doesSideBlockRendering(this, world, pos, side);
            }
        }

    /**
     * Forge added class to make building things easier.
     * Will return an instance of BlockStateContainer appropriate for
     * the list of properties passed in.
     *
     * Example usage:
     *
     *   protected BlockStateContainer createBlockState()
     *   {
     *       return (new BlockStateContainer.Builder(this)).add(FACING).add(SOME_UNLISTED).build();
     *   }
     *
     */
    public static class Builder
    {
        private final Block block;
        private final List<IProperty<?>> listed = Lists.newArrayList();
        private final List<net.minecraftforge.common.property.IUnlistedProperty<?>> unlisted = Lists.newArrayList();

        public Builder(Block block)
        {
            this.block = block;
        }

        public Builder add(IProperty<?>... props)
        {
            for (IProperty<?> prop : props)
                this.listed.add(prop);
            return this;
        }

        public Builder add(net.minecraftforge.common.property.IUnlistedProperty<?>... props)
        {
            for (net.minecraftforge.common.property.IUnlistedProperty<?> prop : props)
                this.unlisted.add(prop);
            return this;
        }

        public BlockStateContainer build()
        {
            IProperty<?>[] listed = new IProperty[this.listed.size()];
            listed = this.listed.toArray(listed);
            if (this.unlisted.size() == 0)
                return new BlockStateContainer(this.block, listed);

            net.minecraftforge.common.property.IUnlistedProperty<?>[] unlisted = new net.minecraftforge.common.property.IUnlistedProperty[this.unlisted.size()];
            unlisted = this.unlisted.toArray(unlisted);

            return new net.minecraftforge.common.property.ExtendedBlockState(this.block, listed, unlisted);
        }
    }
}