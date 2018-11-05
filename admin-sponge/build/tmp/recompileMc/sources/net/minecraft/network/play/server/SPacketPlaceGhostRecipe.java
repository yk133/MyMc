package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SPacketPlaceGhostRecipe implements Packet<INetHandlerPlayClient>
{
    private int field_194314_a;
    private IRecipe recipe;

    public SPacketPlaceGhostRecipe()
    {
    }

    public SPacketPlaceGhostRecipe(int p_i47615_1_, IRecipe p_i47615_2_)
    {
        this.field_194314_a = p_i47615_1_;
        this.recipe = p_i47615_2_;
    }

    @SideOnly(Side.CLIENT)
    public IRecipe func_194311_a()
    {
        return this.recipe;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.field_194314_a = buf.readByte();
        this.recipe = CraftingManager.func_193374_a(buf.readVarInt());
    }

    @SideOnly(Side.CLIENT)
    public int func_194313_b()
    {
        return this.field_194314_a;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.field_194314_a);
        buf.writeVarInt(CraftingManager.func_193375_a(this.recipe));
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handlePlaceGhostRecipe(this);
    }
}