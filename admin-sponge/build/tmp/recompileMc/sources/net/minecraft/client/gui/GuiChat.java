package net.minecraft.client.gui;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ITabCompleter;
import net.minecraft.util.TabCompleter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class GuiChat extends GuiScreen implements ITabCompleter
{
    private static final Logger field_146408_f = LogManager.getLogger();
    private String historyBuffer = "";
    /**
     * keeps position of which chat message you will select when you press up, (does not increase for duplicated
     * messages sent immediately after each other)
     */
    private int sentHistoryCursor = -1;
    private TabCompleter field_184096_i;
    /** Chat entry field */
    protected GuiTextField inputField;
    /** is the text that appears when you press the chat key and the input box appears pre-filled */
    private String defaultInputFieldText = "";

    public GuiChat()
    {
    }

    public GuiChat(String defaultText)
    {
        this.defaultInputFieldText = defaultText;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        this.inputField = new GuiTextField(0, this.fontRenderer, 4, this.height - 12, this.width - 4, 12);
        this.inputField.setMaxStringLength(256);
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputField.setFocused(true);
        this.inputField.setText(this.defaultInputFieldText);
        this.inputField.setCanLoseFocus(false);
        this.field_184096_i = new GuiChat.ChatTabCompleter(this.inputField);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        this.mc.ingameGUI.getChatGUI().resetScroll();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void tick()
    {
        this.inputField.tick();
    }

    protected void func_73869_a(char p_73869_1_, int p_73869_2_) throws IOException
    {
        this.field_184096_i.func_186843_d();

        if (p_73869_2_ == 15)
        {
            this.field_184096_i.func_186841_a();
        }
        else
        {
            this.field_184096_i.func_186842_c();
        }

        if (p_73869_2_ == 1)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
        }
        else if (p_73869_2_ != 28 && p_73869_2_ != 156)
        {
            if (p_73869_2_ == 200)
            {
                this.getSentHistory(-1);
            }
            else if (p_73869_2_ == 208)
            {
                this.getSentHistory(1);
            }
            else if (p_73869_2_ == 201)
            {
                this.mc.ingameGUI.getChatGUI().func_146229_b(this.mc.ingameGUI.getChatGUI().getLineCount() - 1);
            }
            else if (p_73869_2_ == 209)
            {
                this.mc.ingameGUI.getChatGUI().func_146229_b(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1);
            }
            else
            {
                this.inputField.func_146201_a(p_73869_1_, p_73869_2_);
            }
        }
        else
        {
            String s = this.inputField.getText().trim();

            if (!s.isEmpty())
            {
                this.sendChatMessage(s);
            }

            this.mc.displayGuiScreen((GuiScreen)null);
        }
    }

    public void func_146274_d() throws IOException
    {
        super.func_146274_d();
        int i = Mouse.getEventDWheel();

        if (i != 0)
        {
            if (i > 1)
            {
                i = 1;
            }

            if (i < -1)
            {
                i = -1;
            }

            if (!isShiftKeyDown())
            {
                i *= 7;
            }

            this.mc.ingameGUI.getChatGUI().func_146229_b(i);
        }
    }

    protected void func_73864_a(int p_73864_1_, int p_73864_2_, int p_73864_3_) throws IOException
    {
        if (p_73864_3_ == 0)
        {
            ITextComponent itextcomponent = this.mc.ingameGUI.getChatGUI().func_146236_a(Mouse.getX(), Mouse.getY());

            if (itextcomponent != null && this.handleComponentClick(itextcomponent))
            {
                return;
            }
        }

        this.inputField.func_146192_a(p_73864_1_, p_73864_2_, p_73864_3_);
        super.func_73864_a(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    /**
     * Sets the text of the chat
     */
    protected void setText(String newChatText, boolean shouldOverwrite)
    {
        if (shouldOverwrite)
        {
            this.inputField.setText(newChatText);
        }
        else
        {
            this.inputField.writeText(newChatText);
        }
    }

    /**
     * input is relative and is applied directly to the sentHistoryCursor so -1 is the previous message, 1 is the next
     * message from the current cursor position
     */
    public void getSentHistory(int msgPos)
    {
        int i = this.sentHistoryCursor + msgPos;
        int j = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        i = MathHelper.clamp(i, 0, j);

        if (i != this.sentHistoryCursor)
        {
            if (i == j)
            {
                this.sentHistoryCursor = j;
                this.inputField.setText(this.historyBuffer);
            }
            else
            {
                if (this.sentHistoryCursor == j)
                {
                    this.historyBuffer = this.inputField.getText();
                }

                this.inputField.setText((String)this.mc.ingameGUI.getChatGUI().getSentMessages().get(i));
                this.sentHistoryCursor = i;
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
        this.inputField.func_146194_f();
        ITextComponent itextcomponent = this.mc.ingameGUI.getChatGUI().func_146236_a(Mouse.getX(), Mouse.getY());

        if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null)
        {
            this.handleComponentHover(itextcomponent, mouseX, mouseY);
        }

        super.render(mouseX, mouseY, partialTicks);
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    public void func_184072_a(String... p_184072_1_)
    {
        this.field_184096_i.func_186840_a(p_184072_1_);
    }

    @SideOnly(Side.CLIENT)
    public static class ChatTabCompleter extends TabCompleter
        {
            private final Minecraft field_186853_g = Minecraft.getInstance();

            public ChatTabCompleter(GuiTextField p_i46749_1_)
            {
                super(p_i46749_1_, false);
            }

            public void func_186841_a()
            {
                super.func_186841_a();

                if (this.field_186849_f.size() > 1)
                {
                    StringBuilder stringbuilder = new StringBuilder();

                    for (String s : this.field_186849_f)
                    {
                        if (stringbuilder.length() > 0)
                        {
                            stringbuilder.append(", ");
                        }

                        stringbuilder.append(s);
                    }

                    this.field_186853_g.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(stringbuilder.toString()), 1);
                }
            }

            @Nullable
            public BlockPos func_186839_b()
            {
                BlockPos blockpos = null;

                if (this.field_186853_g.objectMouseOver != null && this.field_186853_g.objectMouseOver.type == RayTraceResult.Type.BLOCK)
                {
                    blockpos = this.field_186853_g.objectMouseOver.getBlockPos();
                }

                return blockpos;
            }
        }
}