package net.minecraft.client.gui.chat;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NarratorChatListener implements IChatListener
{
    public static final NarratorChatListener INSTANCE = new NarratorChatListener();
    private final Narrator narrator = Narrator.getNarrator();

    /**
     * Called whenever this listener receives a chat message, if this listener is registered to the given type in {@link
     * net.minecraft.client.gui.GuiIngame#chatListeners chatListeners}
     */
    public void say(ChatType chatTypeIn, ITextComponent message)
    {
        int i = Minecraft.getInstance().gameSettings.narrator;

        if (i != 0 && this.narrator.active())
        {
            if (i == 1 || i == 2 && chatTypeIn == ChatType.CHAT || i == 3 && chatTypeIn == ChatType.SYSTEM)
            {
                if (message instanceof TextComponentTranslation && "chat.type.text".equals(((TextComponentTranslation)message).getKey()))
                {
                    this.narrator.say((new TextComponentTranslation("chat.type.text.narrate", ((TextComponentTranslation)message).getFormatArgs())).func_150260_c());
                }
                else
                {
                    this.narrator.say(message.func_150260_c());
                }
            }
        }
    }

    public void announceMode(int p_193641_1_)
    {
        this.narrator.clear();
        this.narrator.say((new TextComponentTranslation("options.narrator", new Object[0])).func_150260_c() + " : " + (new TextComponentTranslation(GameSettings.NARRATOR_MODES[p_193641_1_], new Object[0])).func_150260_c());
        GuiToast guitoast = Minecraft.getInstance().getToastGui();

        if (this.narrator.active())
        {
            if (p_193641_1_ == 0)
            {
                SystemToast.addOrUpdate(guitoast, SystemToast.Type.NARRATOR_TOGGLE, new TextComponentTranslation("narrator.toast.disabled", new Object[0]), (ITextComponent)null);
            }
            else
            {
                SystemToast.addOrUpdate(guitoast, SystemToast.Type.NARRATOR_TOGGLE, new TextComponentTranslation("narrator.toast.enabled", new Object[0]), new TextComponentTranslation(GameSettings.NARRATOR_MODES[p_193641_1_], new Object[0]));
            }
        }
        else
        {
            SystemToast.addOrUpdate(guitoast, SystemToast.Type.NARRATOR_TOGGLE, new TextComponentTranslation("narrator.toast.disabled", new Object[0]), new TextComponentTranslation("options.narrator.notavailable", new Object[0]));
        }
    }

    public boolean isActive()
    {
        return this.narrator.active();
    }

    public void clear()
    {
        this.narrator.clear();
    }
}