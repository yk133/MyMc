package net.minecraft.tileentity;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandResultStats;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityCommandBlock extends TileEntity
{
    private boolean powered;
    private boolean auto;
    private boolean conditionMet;
    private boolean sendToClient;
    private final CommandBlockBaseLogic commandBlockLogic = new CommandBlockBaseLogic()
    {
        /**
         * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
         * return the coordinates 0, 0, 0
         */
        public BlockPos getPosition()
        {
            return TileEntityCommandBlock.this.pos;
        }
        /**
         * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
         * 0.0D, 0.0D, 0.0D
         */
        public Vec3d getPositionVector()
        {
            return new Vec3d((double)TileEntityCommandBlock.this.pos.getX() + 0.5D, (double)TileEntityCommandBlock.this.pos.getY() + 0.5D, (double)TileEntityCommandBlock.this.pos.getZ() + 0.5D);
        }
        /**
         * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
         * return the overworld
         */
        public World getEntityWorld()
        {
            return TileEntityCommandBlock.this.getWorld();
        }
        /**
         * Sets the command.
         */
        public void setCommand(String command)
        {
            super.setCommand(command);
            TileEntityCommandBlock.this.markDirty();
        }
        public void updateCommand()
        {
            IBlockState iblockstate = TileEntityCommandBlock.this.world.getBlockState(TileEntityCommandBlock.this.pos);
            TileEntityCommandBlock.this.getWorld().notifyBlockUpdate(TileEntityCommandBlock.this.pos, iblockstate, iblockstate, 3);
        }
        @SideOnly(Side.CLIENT)
        public int func_145751_f()
        {
            return 0;
        }
        @SideOnly(Side.CLIENT)
        public void func_145757_a(ByteBuf p_145757_1_)
        {
            p_145757_1_.writeInt(TileEntityCommandBlock.this.pos.getX());
            p_145757_1_.writeInt(TileEntityCommandBlock.this.pos.getY());
            p_145757_1_.writeInt(TileEntityCommandBlock.this.pos.getZ());
        }
        /**
         * Get the Minecraft server instance
         */
        public MinecraftServer getServer()
        {
            return TileEntityCommandBlock.this.world.getServer();
        }
    };

    public NBTTagCompound write(NBTTagCompound compound)
    {
        super.write(compound);
        this.commandBlockLogic.write(compound);
        compound.putBoolean("powered", this.isPowered());
        compound.putBoolean("conditionMet", this.isConditionMet());
        compound.putBoolean("auto", this.isAuto());
        return compound;
    }

    public void read(NBTTagCompound compound)
    {
        super.read(compound);
        this.commandBlockLogic.read(compound);
        this.powered = compound.getBoolean("powered");
        this.conditionMet = compound.getBoolean("conditionMet");
        this.setAuto(compound.getBoolean("auto"));
    }

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        if (this.isSendToClient())
        {
            this.setSendToClient(false);
            NBTTagCompound nbttagcompound = this.write(new NBTTagCompound());
            return new SPacketUpdateTileEntity(this.pos, 2, nbttagcompound);
        }
        else
        {
            return null;
        }
    }

    public boolean onlyOpsCanSetNbt()
    {
        return true;
    }

    public CommandBlockBaseLogic getCommandBlockLogic()
    {
        return this.commandBlockLogic;
    }

    public CommandResultStats func_175124_c()
    {
        return this.commandBlockLogic.func_175572_n();
    }

    public void setPowered(boolean poweredIn)
    {
        this.powered = poweredIn;
    }

    public boolean isPowered()
    {
        return this.powered;
    }

    public boolean isAuto()
    {
        return this.auto;
    }

    public void setAuto(boolean autoIn)
    {
        boolean flag = this.auto;
        this.auto = autoIn;

        if (!flag && autoIn && !this.powered && this.world != null && this.getMode() != TileEntityCommandBlock.Mode.SEQUENCE)
        {
            Block block = this.func_145838_q();

            if (block instanceof BlockCommandBlock)
            {
                this.setConditionMet();
                this.world.func_175684_a(this.pos, block, block.tickRate(this.world));
            }
        }
    }

    public boolean isConditionMet()
    {
        return this.conditionMet;
    }

    public boolean setConditionMet()
    {
        this.conditionMet = true;

        if (this.isConditional())
        {
            BlockPos blockpos = this.pos.offset(((EnumFacing)this.world.getBlockState(this.pos).get(BlockCommandBlock.FACING)).getOpposite());

            if (this.world.getBlockState(blockpos).getBlock() instanceof BlockCommandBlock)
            {
                TileEntity tileentity = this.world.getTileEntity(blockpos);
                this.conditionMet = tileentity instanceof TileEntityCommandBlock && ((TileEntityCommandBlock)tileentity).getCommandBlockLogic().getSuccessCount() > 0;
            }
            else
            {
                this.conditionMet = false;
            }
        }

        return this.conditionMet;
    }

    public boolean isSendToClient()
    {
        return this.sendToClient;
    }

    public void setSendToClient(boolean p_184252_1_)
    {
        this.sendToClient = p_184252_1_;
    }

    public TileEntityCommandBlock.Mode getMode()
    {
        Block block = this.func_145838_q();

        if (block == Blocks.COMMAND_BLOCK)
        {
            return TileEntityCommandBlock.Mode.REDSTONE;
        }
        else if (block == Blocks.REPEATING_COMMAND_BLOCK)
        {
            return TileEntityCommandBlock.Mode.AUTO;
        }
        else
        {
            return block == Blocks.CHAIN_COMMAND_BLOCK ? TileEntityCommandBlock.Mode.SEQUENCE : TileEntityCommandBlock.Mode.REDSTONE;
        }
    }

    public boolean isConditional()
    {
        IBlockState iblockstate = this.world.getBlockState(this.getPos());
        return iblockstate.getBlock() instanceof BlockCommandBlock ? ((Boolean)iblockstate.get(BlockCommandBlock.CONDITIONAL)).booleanValue() : false;
    }

    /**
     * validates a tile entity
     */
    public void validate()
    {
        this.field_145854_h = null;
        super.validate();
    }

    public static enum Mode
    {
        SEQUENCE,
        AUTO,
        REDSTONE;
    }
}