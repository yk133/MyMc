package net.minecraft.client.gui.inventory;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.ITabCompleter;
import net.minecraft.util.TabCompleter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiEditCommandBlockMinecart extends GuiScreen implements ITabCompleter
{
    private GuiTextField field_184088_a;
    private GuiTextField field_184092_f;
    private final CommandBlockBaseLogic commandBlockLogic;
    private GuiButton field_184094_h;
    private GuiButton field_184095_i;
    private GuiButton field_184089_r;
    private boolean field_184090_s;
    private TabCompleter field_184091_t;

    public GuiEditCommandBlockMinecart(CommandBlockBaseLogic p_i46595_1_)
    {
        this.commandBlockLogic = p_i46595_1_;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void tick()
    {
        this.field_184088_a.tick();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.field_184094_h = this.addButton(new GuiButton(0, this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.format("gui.done")));
        this.field_184095_i = this.addButton(new GuiButton(1, this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.format("gui.cancel")));
        this.field_184089_r = this.addButton(new GuiButton(4, this.width / 2 + 150 - 20, 150, 20, 20, "O"));
        this.field_184088_a = new GuiTextField(2, this.fontRenderer, this.width / 2 - 150, 50, 300, 20);
        this.field_184088_a.setMaxStringLength(32500);
        this.field_184088_a.setFocused(true);
        this.field_184088_a.setText(this.commandBlockLogic.getCommand());
        this.field_184092_f = new GuiTextField(3, this.fontRenderer, this.width / 2 - 150, 150, 276, 20);
        this.field_184092_f.setMaxStringLength(32500);
        this.field_184092_f.setEnabled(false);
        this.field_184092_f.setText("-");
        this.field_184090_s = this.commandBlockLogic.shouldTrackOutput();
        this.func_184087_a();
        this.field_184094_h.enabled = !this.field_184088_a.getText().trim().isEmpty();
        this.field_184091_t = new TabCompleter(this.field_184088_a, true)
        {
            @Nullable
            public BlockPos func_186839_b()
            {
                return GuiEditCommandBlockMinecart.this.commandBlockLogic.getPosition();
            }
        };
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void func_146284_a(GuiButton p_146284_1_) throws IOException
    {
        if (p_146284_1_.enabled)
        {
            if (p_146284_1_.id == 1)
            {
                this.commandBlockLogic.setTrackOutput(this.field_184090_s);
                this.mc.displayGuiScreen((GuiScreen)null);
            }
            else if (p_146284_1_.id == 0)
            {
                PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
                packetbuffer.writeByte(this.commandBlockLogic.func_145751_f());
                this.commandBlockLogic.func_145757_a(packetbuffer);
                packetbuffer.writeString(this.field_184088_a.getText());
                packetbuffer.writeBoolean(this.commandBlockLogic.shouldTrackOutput());
                this.mc.getConnection().sendPacket(new CPacketCustomPayload("MC|AdvCmd", packetbuffer));

                if (!this.commandBlockLogic.shouldTrackOutput())
                {
                    this.commandBlockLogic.setLastOutput((ITextComponent)null);
                }

                this.mc.displayGuiScreen((GuiScreen)null);
            }
            else if (p_146284_1_.id == 4)
            {
                this.commandBlockLogic.setTrackOutput(!this.commandBlockLogic.shouldTrackOutput());
                this.func_184087_a();
            }
        }
    }

    protected void func_73869_a(char p_73869_1_, int p_73869_2_) throws IOException
    {
        this.field_184091_t.func_186843_d();

        if (p_73869_2_ == 15)
        {
            this.field_184091_t.func_186841_a();
        }
        else
        {
            this.field_184091_t.func_186842_c();
        }

        this.field_184088_a.func_146201_a(p_73869_1_, p_73869_2_);
        this.field_184092_f.func_146201_a(p_73869_1_, p_73869_2_);
        this.field_184094_h.enabled = !this.field_184088_a.getText().trim().isEmpty();

        if (p_73869_2_ != 28 && p_73869_2_ != 156)
        {
            if (p_73869_2_ == 1)
            {
                this.func_146284_a(this.field_184095_i);
            }
        }
        else
        {
            this.func_146284_a(this.field_184094_h);
        }
    }

    protected void func_73864_a(int p_73864_1_, int p_73864_2_, int p_73864_3_) throws IOException
    {
        super.func_73864_a(p_73864_1_, p_73864_2_, p_73864_3_);
        this.field_184088_a.func_146192_a(p_73864_1_, p_73864_2_, p_73864_3_);
        this.field_184092_f.func_146192_a(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format("advMode.setCommand"), this.width / 2, 20, 16777215);
        this.drawString(this.fontRenderer, I18n.format("advMode.command"), this.width / 2 - 150, 40, 10526880);
        this.field_184088_a.func_146194_f();
        int i = 75;
        int j = 0;
        this.drawString(this.fontRenderer, I18n.format("advMode.nearestPlayer"), this.width / 2 - 140, i + j++ * this.fontRenderer.FONT_HEIGHT, 10526880);
        this.drawString(this.fontRenderer, I18n.format("advMode.randomPlayer"), this.width / 2 - 140, i + j++ * this.fontRenderer.FONT_HEIGHT, 10526880);
        this.drawString(this.fontRenderer, I18n.format("advMode.allPlayers"), this.width / 2 - 140, i + j++ * this.fontRenderer.FONT_HEIGHT, 10526880);
        this.drawString(this.fontRenderer, I18n.format("advMode.allEntities"), this.width / 2 - 140, i + j++ * this.fontRenderer.FONT_HEIGHT, 10526880);
        this.drawString(this.fontRenderer, I18n.format("advMode.self"), this.width / 2 - 140, i + j++ * this.fontRenderer.FONT_HEIGHT, 10526880);

        if (!this.field_184092_f.getText().isEmpty())
        {
            i = i + j * this.fontRenderer.FONT_HEIGHT + 20;
            this.drawString(this.fontRenderer, I18n.format("advMode.previousOutput"), this.width / 2 - 150, i, 10526880);
            this.field_184092_f.func_146194_f();
        }

        super.render(mouseX, mouseY, partialTicks);
    }

    private void func_184087_a()
    {
        if (this.commandBlockLogic.shouldTrackOutput())
        {
            this.field_184089_r.displayString = "O";

            if (this.commandBlockLogic.getLastOutput() != null)
            {
                this.field_184092_f.setText(this.commandBlockLogic.getLastOutput().func_150260_c());
            }
        }
        else
        {
            this.field_184089_r.displayString = "X";
            this.field_184092_f.setText("-");
        }
    }

    public void func_184072_a(String... p_184072_1_)
    {
        this.field_184091_t.func_186840_a(p_184072_1_);
    }
}