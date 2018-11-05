package net.minecraft.client.gui.inventory;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiEditSign extends GuiScreen
{
    /** Reference to the sign object. */
    private final TileEntitySign tileSign;
    /** Counts the number of screen updates. */
    private int updateCounter;
    /** The index of the line that is being edited. */
    private int editLine;
    /** "Done" button for the GUI. */
    private GuiButton doneBtn;

    public GuiEditSign(TileEntitySign teSign)
    {
        this.tileSign = teSign;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttons.clear();
        Keyboard.enableRepeatEvents(true);
        this.doneBtn = this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, I18n.format("gui.done")));
        this.tileSign.setEditable(false);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        NetHandlerPlayClient nethandlerplayclient = this.mc.getConnection();

        if (nethandlerplayclient != null)
        {
            nethandlerplayclient.sendPacket(new CPacketUpdateSign(this.tileSign.getPos(), this.tileSign.signText));
        }

        this.tileSign.setEditable(true);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void tick()
    {
        ++this.updateCounter;
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        if (p_146284_1_.enabled)
        {
            if (p_146284_1_.id == 0)
            {
                this.tileSign.markDirty();
                this.mc.displayGuiScreen((GuiScreen)null);
            }
        }
    }

    protected void func_73869_a(char p_73869_1_, int p_73869_2_) throws IOException
    {
        if (p_73869_2_ == 200)
        {
            this.editLine = this.editLine - 1 & 3;
        }

        if (p_73869_2_ == 208 || p_73869_2_ == 28 || p_73869_2_ == 156)
        {
            this.editLine = this.editLine + 1 & 3;
        }

        String s = this.tileSign.signText[this.editLine].func_150260_c();

        if (p_73869_2_ == 14 && !s.isEmpty())
        {
            s = s.substring(0, s.length() - 1);
        }

        if (ChatAllowedCharacters.isAllowedCharacter(p_73869_1_) && this.fontRenderer.getStringWidth(s + p_73869_1_) <= 90)
        {
            s = s + p_73869_1_;
        }

        this.tileSign.signText[this.editLine] = new TextComponentString(s);

        if (p_73869_2_ == 1)
        {
            this.func_146284_a(this.doneBtn);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format("sign.edit"), this.width / 2, 40, 16777215);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)(this.width / 2), 0.0F, 50.0F);
        float f = 93.75F;
        GlStateManager.scalef(-93.75F, -93.75F, -93.75F);
        GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
        Block block = this.tileSign.func_145838_q();

        if (block == Blocks.field_150472_an)
        {
            float f1 = (float)(this.tileSign.func_145832_p() * 360) / 16.0F;
            GlStateManager.rotatef(f1, 0.0F, 1.0F, 0.0F);
            GlStateManager.translatef(0.0F, -1.0625F, 0.0F);
        }
        else
        {
            int i = this.tileSign.func_145832_p();
            float f2 = 0.0F;

            if (i == 2)
            {
                f2 = 180.0F;
            }

            if (i == 4)
            {
                f2 = 90.0F;
            }

            if (i == 5)
            {
                f2 = -90.0F;
            }

            GlStateManager.rotatef(f2, 0.0F, 1.0F, 0.0F);
            GlStateManager.translatef(0.0F, -1.0625F, 0.0F);
        }

        if (this.updateCounter / 6 % 2 == 0)
        {
            this.tileSign.lineBeingEdited = this.editLine;
        }

        TileEntityRendererDispatcher.instance.render(this.tileSign, -0.5D, -0.75D, -0.5D, 0.0F);
        this.tileSign.lineBeingEdited = -1;
        GlStateManager.popMatrix();
        super.render(mouseX, mouseY, partialTicks);
    }
}