package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TileEntity implements net.minecraftforge.common.capabilities.ICapabilitySerializable<NBTTagCompound>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final RegistryNamespaced < ResourceLocation, Class <? extends TileEntity >> field_190562_f = new RegistryNamespaced < ResourceLocation, Class <? extends TileEntity >> ();
    /** the instance of the world the tile entity is in. */
    protected World world;
    protected BlockPos pos = BlockPos.ORIGIN;
    protected boolean removed;
    private int field_145847_g = -1;
    protected Block field_145854_h;

    public static void func_190560_a(String p_190560_0_, Class <? extends TileEntity > p_190560_1_)
    {
        field_190562_f.put(new ResourceLocation(p_190560_0_), p_190560_1_);
    }

    @Nullable
    public static ResourceLocation func_190559_a(Class <? extends TileEntity > p_190559_0_)
    {
        return field_190562_f.getKey(p_190559_0_);
    }

    /**
     * Returns the worldObj for this tileEntity.
     */
    public World getWorld()
    {
        return this.world;
    }

    /**
     * Sets the worldObj for this tileEntity.
     */
    public void setWorld(World worldIn)
    {
        this.world = worldIn;
    }

    /**
     * Returns true if the worldObj isn't null.
     */
    public boolean hasWorld()
    {
        return this.world != null;
    }

    public void read(NBTTagCompound compound)
    {
        this.pos = new BlockPos(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
        if (compound.contains("ForgeData")) this.customTileData = compound.getCompound("ForgeData");
        if (this.capabilities != null && compound.contains("ForgeCaps")) this.capabilities.deserializeNBT(compound.getCompound("ForgeCaps"));
    }

    public NBTTagCompound write(NBTTagCompound compound)
    {
        return this.writeInternal(compound);
    }

    private NBTTagCompound writeInternal(NBTTagCompound compound)
    {
        ResourceLocation resourcelocation = field_190562_f.getKey(this.getClass());

        if (resourcelocation == null)
        {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        }
        else
        {
            compound.putString("id", resourcelocation.toString());
            compound.putInt("x", this.pos.getX());
            compound.putInt("y", this.pos.getY());
            compound.putInt("z", this.pos.getZ());
            if (this.customTileData != null) compound.put("ForgeData", this.customTileData);
            if (this.capabilities != null) compound.put("ForgeCaps", this.capabilities.serializeNBT());
            return compound;
        }
    }

    @Nullable
    public static TileEntity func_190200_a(World p_190200_0_, NBTTagCompound p_190200_1_)
    {
        TileEntity tileentity = null;
        String s = p_190200_1_.getString("id");
        Class <? extends TileEntity > oclass = null;

        try
        {
            oclass = (Class)field_190562_f.get(new ResourceLocation(s));

            if (oclass != null)
            {
                tileentity = oclass.newInstance();
            }
        }
        catch (Throwable throwable1)
        {
            LOGGER.error("Failed to create block entity {}", s, throwable1);
            net.minecraftforge.fml.common.FMLLog.log.error("A TileEntity {}({}) has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
                    s, oclass == null ? null : oclass.getName(), throwable1);
        }

        if (tileentity != null)
        {
            try
            {
                tileentity.func_190201_b(p_190200_0_);
                tileentity.read(p_190200_1_);
            }
            catch (Throwable throwable)
            {
                LOGGER.error("Failed to load data for block entity {}", s, throwable);
                net.minecraftforge.fml.common.FMLLog.log.error("A TileEntity {}({}) has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
                        s, oclass.getName(), throwable);
                tileentity = null;
            }
        }
        else
        {
            LOGGER.warn("Skipping BlockEntity with id {}", (Object)s);
        }

        return tileentity;
    }

    protected void func_190201_b(World p_190201_1_)
    {
    }

    public int func_145832_p()
    {
        if (this.field_145847_g == -1)
        {
            IBlockState iblockstate = this.world.getBlockState(this.pos);
            this.field_145847_g = iblockstate.getBlock().func_176201_c(iblockstate);
        }

        return this.field_145847_g;
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty()
    {
        if (this.world != null)
        {
            IBlockState iblockstate = this.world.getBlockState(this.pos);
            this.field_145847_g = iblockstate.getBlock().func_176201_c(iblockstate);
            this.world.markChunkDirty(this.pos, this);

            if (this.func_145838_q() != Blocks.AIR)
            {
                this.world.updateComparatorOutputLevel(this.pos, this.func_145838_q());
            }
        }
    }

    /**
     * Returns the square of the distance between this entity and the passed in coordinates.
     */
    public double getDistanceSq(double x, double y, double z)
    {
        double d0 = (double)this.pos.getX() + 0.5D - x;
        double d1 = (double)this.pos.getY() + 0.5D - y;
        double d2 = (double)this.pos.getZ() + 0.5D - z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 4096.0D;
    }

    public BlockPos getPos()
    {
        return this.pos;
    }

    public Block func_145838_q()
    {
        if (this.field_145854_h == null && this.world != null)
        {
            this.field_145854_h = this.world.getBlockState(this.pos).getBlock();
        }

        return this.field_145854_h;
    }

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return null;
    }

    /**
     * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
     * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
     */
    public NBTTagCompound getUpdateTag()
    {
        return this.writeInternal(new NBTTagCompound());
    }

    public boolean isRemoved()
    {
        return this.removed;
    }

    /**
     * invalidates a tile entity
     */
    public void remove()
    {
        this.removed = true;
    }

    /**
     * validates a tile entity
     */
    public void validate()
    {
        this.removed = false;
    }

    /**
     * See {@link Block#eventReceived} for more information. This must return true serverside before it is called
     * clientside.
     */
    public boolean receiveClientEvent(int id, int type)
    {
        return false;
    }

    public void updateContainingBlockInfo()
    {
        this.field_145854_h = null;
        this.field_145847_g = -1;
    }

    public void addInfoToCrashReport(CrashReportCategory reportCategory)
    {
        reportCategory.addDetail("Name", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return TileEntity.field_190562_f.getKey(TileEntity.this.getClass()) + " // " + TileEntity.this.getClass().getCanonicalName();
            }
        });

        if (this.world != null)
        {
            CrashReportCategory.func_180523_a(reportCategory, this.pos, this.func_145838_q(), this.func_145832_p());
            reportCategory.addDetail("Actual block type", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    int i = Block.func_149682_b(TileEntity.this.world.getBlockState(TileEntity.this.pos).getBlock());

                    try
                    {
                        return String.format("ID #%d (%s // %s // %s)", i, Block.getBlockById(i).getTranslationKey(), Block.getBlockById(i).getClass().getName(), Block.getBlockById(i).getRegistryName());
                    }
                    catch (Throwable var3)
                    {
                        return "ID #" + i;
                    }
                }
            });
            reportCategory.addDetail("Actual block data value", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    IBlockState iblockstate = TileEntity.this.world.getBlockState(TileEntity.this.pos);
                    int i = iblockstate.getBlock().func_176201_c(iblockstate);

                    if (i < 0)
                    {
                        return "Unknown? (Got " + i + ")";
                    }
                    else
                    {
                        String s = String.format("%4s", Integer.toBinaryString(i)).replace(" ", "0");
                        return String.format("%1$d / 0x%1$X / 0b%2$s", i, s);
                    }
                }
            });
        }
    }

    public void setPos(BlockPos posIn)
    {
        this.pos = posIn.toImmutable();
    }

    public boolean onlyOpsCanSetNbt()
    {
        return false;
    }

    @Nullable
    public ITextComponent getDisplayName()
    {
        return null;
    }

    public void rotate(Rotation rotationIn)
    {
    }

    public void mirror(Mirror mirrorIn)
    {
    }

    // -- BEGIN FORGE PATCHES --
    /**
     * Called when you receive a TileEntityData packet for the location this
     * TileEntity is currently in. On the client, the NetworkManager will always
     * be the remote server. On the server, it will be whomever is responsible for
     * sending the packet.
     *
     * @param net The NetworkManager the packet originated from
     * @param pkt The data packet
     */
    public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.SPacketUpdateTileEntity pkt)
    {
    }

    /**
     * Called when the chunk's TE update tag, gotten from {@link #getUpdateTag()}, is received on the client.
     * <p>
     * Used to handle this tag in a special way. By default this simply calls {@link #readFromNBT(NBTTagCompound)}.
     *
     * @param tag The {@link NBTTagCompound} sent from {@link #getUpdateTag()}
     */
    public void handleUpdateTag(NBTTagCompound tag)
    {
        this.read(tag);
    }

    /**
     * Called when the chunk this TileEntity is on is Unloaded.
     */
    public void onChunkUnload()
    {
    }

    private boolean isVanilla = getClass().getName().startsWith("net.minecraft.");
    /**
     * Called from Chunk.setBlockIDWithMetadata and Chunk.fillChunk, determines if this tile entity should be re-created when the ID, or Metadata changes.
     * Use with caution as this will leave straggler TileEntities, or create conflicts with other TileEntities if not used properly.
     *
     * @param world Current world
     * @param pos Tile's world position
     * @param oldState The old ID of the block
     * @param newState The new ID of the block (May be the same)
     * @return true forcing the invalidation of the existing TE, false not to invalidate the existing TE
     */
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return isVanilla ? (oldState.getBlock() != newSate.getBlock()) : oldState != newSate;
    }

    public boolean shouldRenderInPass(int pass)
    {
        return pass == 0;
    }

    /**
     * Sometimes default render bounding box: infinite in scope. Used to control rendering on {@link TileEntitySpecialRenderer}.
     */
    public static final net.minecraft.util.math.AxisAlignedBB INFINITE_EXTENT_AABB = new net.minecraft.util.math.AxisAlignedBB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    /**
     * Return an {@link AxisAlignedBB} that controls the visible scope of a {@link TileEntitySpecialRenderer} associated with this {@link TileEntity}
     * Defaults to the collision bounding box {@link Block#getCollisionBoundingBoxFromPool(World, int, int, int)} associated with the block
     * at this location.
     *
     * @return an appropriately size {@link AxisAlignedBB} for the {@link TileEntity}
     */
    @SideOnly(Side.CLIENT)
    public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox()
    {
        net.minecraft.util.math.AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        Block type = func_145838_q();
        BlockPos pos = getPos();
        if (type == Blocks.ENCHANTING_TABLE)
        {
            bb = new net.minecraft.util.math.AxisAlignedBB(pos, pos.add(1, 1, 1));
        }
        else if (type == Blocks.CHEST || type == Blocks.TRAPPED_CHEST)
        {
            bb = new net.minecraft.util.math.AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 2, 2));
        }
        else if (type == Blocks.STRUCTURE_BLOCK)
        {
            bb = INFINITE_EXTENT_AABB;
        }
        else if (type != null && type != Blocks.BEACON)
        {
            net.minecraft.util.math.AxisAlignedBB cbb = null;
            try
            {
                cbb = world.getBlockState(getPos()).func_185890_d(world, pos).offset(pos);
            }
            catch (Exception e)
            {
                // We have to capture any exceptions that may occur here because BUKKIT servers like to send
                // the tile entity data BEFORE the chunk data, you know, the OPPOSITE of what vanilla does!
                // So we can not GARENTEE that the world state is the real state for the block...
                // So, once again in the long line of US having to accommodate BUKKIT breaking things,
                // here it is, assume that the TE is only 1 cubic block. Problem with this is that it may
                // cause the TileEntity renderer to error further down the line! But alas, nothing we can do.
                cbb = new net.minecraft.util.math.AxisAlignedBB(getPos().add(-1, 0, -1), getPos().add(1, 1, 1));
            }
            if (cbb != null) bb = cbb;
        }
        return bb;
    }

    /**
     * Checks if this tile entity knows how to render its 'breaking' overlay effect.
     * If this returns true, The TileEntitySpecialRenderer will be called again with break progress set.
     * @return True to re-render tile with breaking effect.
     */
    public boolean canRenderBreaking()
    {
        Block block = this.func_145838_q();
        return (block instanceof net.minecraft.block.BlockChest ||
                block instanceof net.minecraft.block.BlockEnderChest ||
                block instanceof net.minecraft.block.BlockSign ||
                block instanceof net.minecraft.block.BlockSkull);
    }

    private NBTTagCompound customTileData;

    /**
     * Gets a {@link NBTTagCompound} that can be used to store custom data for this tile entity.
     * It will be written, and read from disc, so it persists over world saves.
     *
     * @return A compound tag for custom data
     */
    public NBTTagCompound getTileData()
    {
        if (this.customTileData == null)
        {
            this.customTileData = new NBTTagCompound();
        }
        return this.customTileData;
    }

    /**
     * Determines if the player can overwrite the NBT data of this tile entity while they place it using a ItemStack.
     * Added as a fix for MC-75630 - Exploit with signs and command blocks
     * @return True to prevent NBT copy, false to allow.
     */
    public boolean restrictNBTCopy()
    {
        return this instanceof TileEntityCommandBlock ||
               this instanceof TileEntityMobSpawner ||
               this instanceof TileEntitySign;
    }


    /**
     * Called when this is first added to the world (by {@link World#addTileEntity(TileEntity)}).
     * Override instead of adding {@code if (firstTick)} stuff in update.
     */
    public void onLoad()
    {
        // NOOP
    }

    /**
     * If the TileEntitySpecialRenderer associated with this TileEntity can be batched in with another renderers, and won't access the GL state.
     * If TileEntity returns true, then TESR should have the same functionality as (and probably extend) the FastTESR class.
     */
    public boolean hasFastRenderer()
    {
        return false;
    }

    private net.minecraftforge.common.capabilities.CapabilityDispatcher capabilities;
    public TileEntity()
    {
        capabilities = net.minecraftforge.event.ForgeEventFactory.gatherCapabilities(this);
    }

    @Override
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing)
    {
        return capabilities == null ? false : capabilities.hasCapability(capability, facing);
    }

    @Override
    @Nullable
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing)
    {
        return capabilities == null ? null : capabilities.getCapability(capability, facing);
    }

    public void deserializeNBT(NBTTagCompound nbt)
    {
        this.read(nbt);
    }

    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        this.write(ret);
        return ret;
    }

    static
    {
        func_190560_a("furnace", TileEntityFurnace.class);
        func_190560_a("chest", TileEntityChest.class);
        func_190560_a("ender_chest", TileEntityEnderChest.class);
        func_190560_a("jukebox", BlockJukebox.TileEntityJukebox.class);
        func_190560_a("dispenser", TileEntityDispenser.class);
        func_190560_a("dropper", TileEntityDropper.class);
        func_190560_a("sign", TileEntitySign.class);
        func_190560_a("mob_spawner", TileEntityMobSpawner.class);
        func_190560_a("noteblock", TileEntityNote.class);
        func_190560_a("piston", TileEntityPiston.class);
        func_190560_a("brewing_stand", TileEntityBrewingStand.class);
        func_190560_a("enchanting_table", TileEntityEnchantmentTable.class);
        func_190560_a("end_portal", TileEntityEndPortal.class);
        func_190560_a("beacon", TileEntityBeacon.class);
        func_190560_a("skull", TileEntitySkull.class);
        func_190560_a("daylight_detector", TileEntityDaylightDetector.class);
        func_190560_a("hopper", TileEntityHopper.class);
        func_190560_a("comparator", TileEntityComparator.class);
        func_190560_a("flower_pot", TileEntityFlowerPot.class);
        func_190560_a("banner", TileEntityBanner.class);
        func_190560_a("structure_block", TileEntityStructure.class);
        func_190560_a("end_gateway", TileEntityEndGateway.class);
        func_190560_a("command_block", TileEntityCommandBlock.class);
        func_190560_a("shulker_box", TileEntityShulkerBox.class);
        func_190560_a("bed", TileEntityBed.class);
    }
}