package net.minecraft.tileentity;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityEndPortal extends TileEntity
{
    @SideOnly(Side.CLIENT)
    public boolean shouldRenderFace(EnumFacing face)
    {
        return face == EnumFacing.UP;
    }
}