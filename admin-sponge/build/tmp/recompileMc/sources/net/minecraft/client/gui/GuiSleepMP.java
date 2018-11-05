package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSleepMP extends GuiChat
{
    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        super.initGui();
        this.buttons.add(new GuiButton(1, this.width / 2 - 100, this.height - 40, I18n.format("multiplayer.stopSleeping")));
    }

    protected void func_73869_a(char p_73869_1_, int p_73869_2_) throws IOException
    {
        if (p_73869_2_ == 1)
        {
            this.wakeFromSleep();
        }
        else if (p_73869_2_ != 28 && p_73869_2_ != 156)
        {
            super.func_73869_a(p_73869_1_, p_73869_2_);
        }
        else
        {
            String s = this.inputField.getText().trim();

            if (!s.isEmpty())
            {
                this.sendChatMessage(s); // Forge: fix vanilla not adding messages to the sent list while sleeping
            }

            this.inputField.setText("");
            this.mc.ingameGUI.getChatGUI().resetScroll();
        }
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        if (p_146284_1_.id == 1)
        {
            this.wakeFromSleep();
        }
        else
        {
            super.func_146284_a(p_146284_1_);
        }
    }

    private void wakeFromSleep()
    {
        NetHandlerPlayClient nethandlerplayclient = this.mc.player.connection;
        nethandlerplayclient.sendPacket(new CPacketEntityAction(this.mc.player, CPacketEntityAction.Action.STOP_SLEEPING));
    }
}