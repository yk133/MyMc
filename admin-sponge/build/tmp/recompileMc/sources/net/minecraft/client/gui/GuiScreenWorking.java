package net.minecraft.client.gui;

import net.minecraft.util.IProgressUpdate;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenWorking extends GuiScreen implements IProgressUpdate
{
    private String title = "";
    private String stage = "";
    private int progress;
    private boolean doneWorking;

    public void func_73720_a(String p_73720_1_)
    {
        this.func_73721_b(p_73720_1_);
    }

    public void func_73721_b(String p_73721_1_)
    {
        this.title = p_73721_1_;
        this.func_73719_c("Working...");
    }

    public void func_73719_c(String p_73719_1_)
    {
        this.stage = p_73719_1_;
        this.setLoadingProgress(0);
    }

    /**
     * Updates the progress bar on the loading screen to the specified amount.
     */
    public void setLoadingProgress(int progress)
    {
        this.progress = progress;
    }

    public void setDoneWorking()
    {
        this.doneWorking = true;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (this.doneWorking)
        {
            if (!this.mc.isConnectedToRealms())
            {
                this.mc.displayGuiScreen((GuiScreen)null);
            }
        }
        else
        {
            this.drawDefaultBackground();
            this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 70, 16777215);
            this.drawCenteredString(this.fontRenderer, this.stage + " " + this.progress + "%", this.width / 2, 90, 16777215);
            super.render(mouseX, mouseY, partialTicks);
        }
    }
}