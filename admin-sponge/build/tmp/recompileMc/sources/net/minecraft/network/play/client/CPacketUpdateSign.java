package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CPacketUpdateSign implements Packet<INetHandlerPlayServer>
{
    private BlockPos pos;
    private String[] lines;

    public CPacketUpdateSign()
    {
    }

    @SideOnly(Side.CLIENT)
    public CPacketUpdateSign(BlockPos posIn, ITextComponent[] linesIn)
    {
        this.pos = posIn;
        this.lines = new String[] {linesIn[0].func_150260_c(), linesIn[1].func_150260_c(), linesIn[2].func_150260_c(), linesIn[3].func_150260_c()};
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.pos = buf.readBlockPos();
        this.lines = new String[4];

        for (int i = 0; i < 4; ++i)
        {
            this.lines[i] = buf.readString(384);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBlockPos(this.pos);

        for (int i = 0; i < 4; ++i)
        {
            buf.writeString(this.lines[i]);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processUpdateSign(this);
    }

    public BlockPos getPosition()
    {
        return this.pos;
    }

    public String[] getLines()
    {
        return this.lines;
    }
}