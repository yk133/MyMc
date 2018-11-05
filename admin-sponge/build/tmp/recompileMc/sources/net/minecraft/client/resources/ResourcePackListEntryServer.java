package net.minecraft.client.resources;

import com.google.gson.JsonParseException;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class ResourcePackListEntryServer extends ResourcePackListEntry
{
    private static final Logger field_148322_c = LogManager.getLogger();
    private final IResourcePack field_148320_d;
    private final ResourceLocation field_148321_e;

    public ResourcePackListEntryServer(GuiScreenResourcePacks p_i46594_1_, IResourcePack p_i46594_2_)
    {
        super(p_i46594_1_);
        this.field_148320_d = p_i46594_2_;
        DynamicTexture dynamictexture;

        try
        {
            dynamictexture = new DynamicTexture(p_i46594_2_.func_110586_a());
        }
        catch (IOException var5)
        {
            dynamictexture = TextureUtil.field_111001_a;
        }

        this.field_148321_e = this.field_148317_a.getTextureManager().getDynamicTextureLocation("texturepackicon", dynamictexture);
    }

    protected int func_183019_a()
    {
        return 3;
    }

    protected String getResourcePackDescription()
    {
        try
        {
            PackMetadataSection packmetadatasection = (PackMetadataSection)this.field_148320_d.func_135058_a(this.field_148317_a.func_110438_M().field_110621_c, "pack");

            if (packmetadatasection != null)
            {
                return packmetadatasection.func_152805_a().getFormattedText();
            }
        }
        catch (JsonParseException jsonparseexception)
        {
            field_148322_c.error("Couldn't load metadata info", (Throwable)jsonparseexception);
        }
        catch (IOException ioexception)
        {
            field_148322_c.error("Couldn't load metadata info", (Throwable)ioexception);
        }

        return TextFormatting.RED + "Missing " + "pack.mcmeta" + " :(";
    }

    protected boolean func_148309_e()
    {
        return false;
    }

    protected boolean func_148308_f()
    {
        return false;
    }

    protected boolean func_148314_g()
    {
        return false;
    }

    protected boolean func_148307_h()
    {
        return false;
    }

    protected String getResourcePackName()
    {
        return "Server";
    }

    protected void bindResourcePackIcon()
    {
        this.field_148317_a.getTextureManager().bindTexture(this.field_148321_e);
    }

    protected boolean func_148310_d()
    {
        return false;
    }

    public boolean func_186768_j()
    {
        return true;
    }
}