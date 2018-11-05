package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryDefault;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackListEntryServer;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenResourcePacks extends GuiScreen
{
    private final GuiScreen parentScreen;
    private List<ResourcePackListEntry> field_146966_g;
    private List<ResourcePackListEntry> field_146969_h;
    /** List component that contains the available resource packs */
    private GuiResourcePackAvailable availableResourcePacksList;
    /** List component that contains the selected resource packs */
    private GuiResourcePackSelected selectedResourcePacksList;
    private boolean changed;

    public GuiScreenResourcePacks(GuiScreen parentScreenIn)
    {
        this.parentScreen = parentScreenIn;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttons.add(new GuiOptionButton(2, this.width / 2 - 154, this.height - 48, I18n.format("resourcePack.openFolder")));
        this.buttons.add(new GuiOptionButton(1, this.width / 2 + 4, this.height - 48, I18n.format("gui.done")));

        if (!this.changed)
        {
            this.field_146966_g = Lists.<ResourcePackListEntry>newArrayList();
            this.field_146969_h = Lists.<ResourcePackListEntry>newArrayList();
            ResourcePackRepository resourcepackrepository = this.mc.func_110438_M();
            resourcepackrepository.func_110611_a();
            List<ResourcePackRepository.Entry> list = Lists.newArrayList(resourcepackrepository.func_110609_b());
            list.removeAll(resourcepackrepository.func_110613_c());

            for (ResourcePackRepository.Entry resourcepackrepository$entry : list)
            {
                this.field_146966_g.add(new ResourcePackListEntryFound(this, resourcepackrepository$entry));
            }

            ResourcePackRepository.Entry resourcepackrepository$entry2 = resourcepackrepository.func_188565_b();

            if (resourcepackrepository$entry2 != null)
            {
                this.field_146969_h.add(new ResourcePackListEntryServer(this, resourcepackrepository.func_148530_e()));
            }

            for (ResourcePackRepository.Entry resourcepackrepository$entry1 : Lists.reverse(resourcepackrepository.func_110613_c()))
            {
                this.field_146969_h.add(new ResourcePackListEntryFound(this, resourcepackrepository$entry1));
            }

            this.field_146969_h.add(new ResourcePackListEntryDefault(this));
        }

        this.availableResourcePacksList = new GuiResourcePackAvailable(this.mc, 200, this.height, this.field_146966_g);
        this.availableResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
        this.availableResourcePacksList.func_148134_d(7, 8);
        this.selectedResourcePacksList = new GuiResourcePackSelected(this.mc, 200, this.height, this.field_146969_h);
        this.selectedResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 + 4);
        this.selectedResourcePacksList.func_148134_d(7, 8);
    }

    public void func_146274_d() throws IOException
    {
        super.func_146274_d();
        this.selectedResourcePacksList.func_178039_p();
        this.availableResourcePacksList.func_178039_p();
    }

    public boolean func_146961_a(ResourcePackListEntry p_146961_1_)
    {
        return this.field_146969_h.contains(p_146961_1_);
    }

    public List<ResourcePackListEntry> func_146962_b(ResourcePackListEntry p_146962_1_)
    {
        return this.func_146961_a(p_146962_1_) ? this.field_146969_h : this.field_146966_g;
    }

    public List<ResourcePackListEntry> func_146964_g()
    {
        return this.field_146966_g;
    }

    public List<ResourcePackListEntry> func_146963_h()
    {
        return this.field_146969_h;
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        if (p_146284_1_.enabled)
        {
            if (p_146284_1_.id == 2)
            {
                File file1 = this.mc.func_110438_M().func_110612_e();
                OpenGlHelper.func_188786_a(file1);
            }
            else if (p_146284_1_.id == 1)
            {
                if (this.changed)
                {
                    List<ResourcePackRepository.Entry> list = Lists.<ResourcePackRepository.Entry>newArrayList();

                    for (ResourcePackListEntry resourcepacklistentry : this.field_146969_h)
                    {
                        if (resourcepacklistentry instanceof ResourcePackListEntryFound)
                        {
                            list.add(((ResourcePackListEntryFound)resourcepacklistentry).func_148318_i());
                        }
                    }

                    Collections.reverse(list);
                    this.mc.func_110438_M().func_148527_a(list);
                    this.mc.gameSettings.resourcePacks.clear();
                    this.mc.gameSettings.incompatibleResourcePacks.clear();

                    for (ResourcePackRepository.Entry resourcepackrepository$entry : list)
                    {
                        this.mc.gameSettings.resourcePacks.add(resourcepackrepository$entry.func_110515_d());

                        if (resourcepackrepository$entry.func_183027_f() != 3)
                        {
                            this.mc.gameSettings.incompatibleResourcePacks.add(resourcepackrepository$entry.func_110515_d());
                        }
                    }

                    this.mc.gameSettings.saveOptions();
                    this.mc.refreshResources();
                }

                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }

    protected void func_73864_a(int p_73864_1_, int p_73864_2_, int p_73864_3_) throws IOException
    {
        super.func_73864_a(p_73864_1_, p_73864_2_, p_73864_3_);
        this.availableResourcePacksList.func_148179_a(p_73864_1_, p_73864_2_, p_73864_3_);
        this.selectedResourcePacksList.func_148179_a(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    protected void func_146286_b(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        super.func_146286_b(p_146286_1_, p_146286_2_, p_146286_3_);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawBackground(0);
        this.availableResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
        this.selectedResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, I18n.format("resourcePack.title"), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.fontRenderer, I18n.format("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
        super.render(mouseX, mouseY, partialTicks);
    }

    /**
     * Marks the selected resource packs list as changed to trigger a resource reload when the screen is closed
     */
    public void markChanged()
    {
        this.changed = true;
    }
}