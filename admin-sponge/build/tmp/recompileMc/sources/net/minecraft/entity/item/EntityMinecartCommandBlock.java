package net.minecraft.entity.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityMinecartCommandBlock extends EntityMinecart
{
    private static final DataParameter<String> COMMAND = EntityDataManager.<String>createKey(EntityMinecartCommandBlock.class, DataSerializers.STRING);
    private static final DataParameter<ITextComponent> LAST_OUTPUT = EntityDataManager.<ITextComponent>createKey(EntityMinecartCommandBlock.class, DataSerializers.TEXT_COMPONENT);
    private final CommandBlockBaseLogic commandBlockLogic = new CommandBlockBaseLogic()
    {
        public void updateCommand()
        {
            EntityMinecartCommandBlock.this.getDataManager().set(EntityMinecartCommandBlock.COMMAND, this.getCommand());
            EntityMinecartCommandBlock.this.getDataManager().set(EntityMinecartCommandBlock.LAST_OUTPUT, this.getLastOutput());
        }
        @SideOnly(Side.CLIENT)
        public int func_145751_f()
        {
            return 1;
        }
        @SideOnly(Side.CLIENT)
        public void func_145757_a(ByteBuf p_145757_1_)
        {
            p_145757_1_.writeInt(EntityMinecartCommandBlock.this.getEntityId());
        }
        /**
         * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
         * return the coordinates 0, 0, 0
         */
        public BlockPos getPosition()
        {
            return new BlockPos(EntityMinecartCommandBlock.this.posX, EntityMinecartCommandBlock.this.posY + 0.5D, EntityMinecartCommandBlock.this.posZ);
        }
        /**
         * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
         * 0.0D, 0.0D, 0.0D
         */
        public Vec3d getPositionVector()
        {
            return new Vec3d(EntityMinecartCommandBlock.this.posX, EntityMinecartCommandBlock.this.posY, EntityMinecartCommandBlock.this.posZ);
        }
        /**
         * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
         * return the overworld
         */
        public World getEntityWorld()
        {
            return EntityMinecartCommandBlock.this.world;
        }
        public Entity func_174793_f()
        {
            return EntityMinecartCommandBlock.this;
        }
        /**
         * Get the Minecraft server instance
         */
        public MinecraftServer getServer()
        {
            return EntityMinecartCommandBlock.this.world.getServer();
        }
    };
    /** Cooldown before command block logic runs again in ticks */
    private int activatorRailCooldown;

    public EntityMinecartCommandBlock(World worldIn)
    {
        super(worldIn);
    }

    public EntityMinecartCommandBlock(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    public static void func_189670_a(DataFixer p_189670_0_)
    {
        EntityMinecart.func_189669_a(p_189670_0_, EntityMinecartCommandBlock.class);
        p_189670_0_.func_188258_a(FixTypes.ENTITY, new IDataWalker()
        {
            public NBTTagCompound func_188266_a(IDataFixer p_188266_1_, NBTTagCompound p_188266_2_, int p_188266_3_)
            {
                if (TileEntity.func_190559_a(TileEntityCommandBlock.class).equals(new ResourceLocation(p_188266_2_.getString("id"))))
                {
                    p_188266_2_.putString("id", "Control");
                    p_188266_1_.func_188251_a(FixTypes.BLOCK_ENTITY, p_188266_2_, p_188266_3_);
                    p_188266_2_.putString("id", "MinecartCommandBlock");
                }

                return p_188266_2_;
            }
        });
    }

    protected void registerData()
    {
        super.registerData();
        this.getDataManager().register(COMMAND, "");
        this.getDataManager().register(LAST_OUTPUT, new TextComponentString(""));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditional(NBTTagCompound compound)
    {
        super.readAdditional(compound);
        this.commandBlockLogic.read(compound);
        this.getDataManager().set(COMMAND, this.getCommandBlockLogic().getCommand());
        this.getDataManager().set(LAST_OUTPUT, this.getCommandBlockLogic().getLastOutput());
    }

    /**
     * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
     * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
     */
    protected void writeAdditional(NBTTagCompound compound)
    {
        super.writeAdditional(compound);
        this.commandBlockLogic.write(compound);
    }

    public EntityMinecart.Type getMinecartType()
    {
        return EntityMinecart.Type.COMMAND_BLOCK;
    }

    public IBlockState getDefaultDisplayTile()
    {
        return Blocks.COMMAND_BLOCK.getDefaultState();
    }

    public CommandBlockBaseLogic getCommandBlockLogic()
    {
        return this.commandBlockLogic;
    }

    /**
     * Called every tick the minecart is on an activator rail.
     */
    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower)
    {
        if (receivingPower && this.ticksExisted - this.activatorRailCooldown >= 4)
        {
            this.getCommandBlockLogic().trigger(this.world);
            this.activatorRailCooldown = this.ticksExisted;
        }
    }

    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if (super.processInitialInteract(player, hand)) return true;
        this.commandBlockLogic.tryOpenEditCommandBlock(player);
        return false;
    }

    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);

        if (LAST_OUTPUT.equals(key))
        {
            try
            {
                this.commandBlockLogic.setLastOutput((ITextComponent)this.getDataManager().get(LAST_OUTPUT));
            }
            catch (Throwable var3)
            {
                ;
            }
        }
        else if (COMMAND.equals(key))
        {
            this.commandBlockLogic.setCommand((String)this.getDataManager().get(COMMAND));
        }
    }

    public boolean ignoreItemEntityData()
    {
        return true;
    }
}