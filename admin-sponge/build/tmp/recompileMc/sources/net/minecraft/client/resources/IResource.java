package net.minecraft.client.resources;

import java.io.Closeable;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IResource extends Closeable
{
    ResourceLocation func_177241_a();

    InputStream func_110527_b();

    boolean func_110528_c();

    @Nullable
    <T extends IMetadataSection> T func_110526_a(String p_110526_1_);

    String func_177240_d();
}