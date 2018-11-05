package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public abstract class TileEntityLockable extends TileEntity implements ILockableContainer
{
    private LockCode code = LockCode.EMPTY_CODE;

    public void read(NBTTagCompound compound)
    {
        super.read(compound);
        this.code = LockCode.read(compound);
    }

    public NBTTagCompound write(NBTTagCompound compound)
    {
        super.write(compound);

        if (this.code != null)
        {
            this.code.write(compound);
        }

        return compound;
    }

    public boolean isLocked()
    {
        return this.code != null && !this.code.isEmpty();
    }

    public LockCode getLockCode()
    {
        return this.code;
    }

    public void setLockCode(LockCode code)
    {
        this.code = code;
    }

    public ITextComponent getDisplayName()
    {
        return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.func_70005_c_()) : new TextComponentTranslation(this.func_70005_c_(), new Object[0]));
    }

    private net.minecraftforge.items.IItemHandler itemHandler;

    protected net.minecraftforge.items.IItemHandler createUnSidedHandler()
    {
        return new net.minecraftforge.items.wrapper.InvWrapper(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    @javax.annotation.Nullable
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable net.minecraft.util.EnumFacing facing)
    {
        if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) (itemHandler == null ? (itemHandler = createUnSidedHandler()) : itemHandler);
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @javax.annotation.Nullable net.minecraft.util.EnumFacing facing)
    {
        return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}