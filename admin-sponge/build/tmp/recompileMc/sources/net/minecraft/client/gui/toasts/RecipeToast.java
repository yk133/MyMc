package net.minecraft.client.gui.toasts;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RecipeToast implements IToast
{
    private final List<ItemStack> field_193666_c = Lists.<ItemStack>newArrayList();
    private long firstDrawTime;
    private boolean hasNewOutputs;

    public RecipeToast(ItemStack p_i47489_1_)
    {
        this.field_193666_c.add(p_i47489_1_);
    }

    public IToast.Visibility draw(GuiToast toastGui, long delta)
    {
        if (this.hasNewOutputs)
        {
            this.firstDrawTime = delta;
            this.hasNewOutputs = false;
        }

        if (this.field_193666_c.isEmpty())
        {
            return IToast.Visibility.HIDE;
        }
        else
        {
            toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
            GlStateManager.color3f(1.0F, 1.0F, 1.0F);
            toastGui.drawTexturedModalRect(0, 0, 0, 32, 160, 32);
            toastGui.getMinecraft().fontRenderer.func_78276_b(I18n.format("recipe.toast.title"), 30, 7, -11534256);
            toastGui.getMinecraft().fontRenderer.func_78276_b(I18n.format("recipe.toast.description"), 30, 18, -16777216);
            RenderHelper.enableGUIStandardItemLighting();
            toastGui.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI((EntityLivingBase)null, this.field_193666_c.get((int)(delta * (long)this.field_193666_c.size() / 5000L % (long)this.field_193666_c.size())), 8, 8); //Forge: fix math so that it doesn't divide by 0 when there are more than 5000 recipes
            return delta - this.firstDrawTime >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
        }
    }

    public void func_193664_a(ItemStack p_193664_1_)
    {
        if (this.field_193666_c.add(p_193664_1_))
        {
            this.hasNewOutputs = true;
        }
    }

    public static void addOrUpdate(GuiToast toastGui, IRecipe recipeIn)
    {
        RecipeToast recipetoast = (RecipeToast)toastGui.getToast(RecipeToast.class, NO_TOKEN);

        if (recipetoast == null)
        {
            toastGui.add(new RecipeToast(recipeIn.getRecipeOutput()));
        }
        else
        {
            recipetoast.func_193664_a(recipeIn.getRecipeOutput());
        }
    }
}