package net.minecraft.client.resources;

import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ResourcePackListEntryFound extends ResourcePackListEntry
{
    private final ResourcePackRepository.Entry resourcePackEntry;

    public ResourcePackListEntryFound(GuiScreenResourcePacks p_i45053_1_, ResourcePackRepository.Entry p_i45053_2_)
    {
        super(p_i45053_1_);
        this.resourcePackEntry = p_i45053_2_;
    }

    protected void bindResourcePackIcon()
    {
        this.resourcePackEntry.func_110518_a(this.field_148317_a.getTextureManager());
    }

    protected int func_183019_a()
    {
        return this.resourcePackEntry.func_183027_f();
    }

    protected String getResourcePackDescription()
    {
        return this.resourcePackEntry.func_110519_e();
    }

    protected String getResourcePackName()
    {
        return this.resourcePackEntry.func_110515_d();
    }

    public ResourcePackRepository.Entry func_148318_i()
    {
        return this.resourcePackEntry;
    }
}