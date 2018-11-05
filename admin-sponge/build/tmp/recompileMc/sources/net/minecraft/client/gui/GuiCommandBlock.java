package net.minecraft.client.gui;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.ITabCompleter;
import net.minecraft.util.TabCompleter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiCommandBlock extends GuiScreen implements ITabCompleter
{
    private GuiTextField field_146485_f;
    private GuiTextField field_146486_g;
    private final TileEntityCommandBlock commandBlock;
    private GuiButton field_146490_i;
    private GuiButton field_146487_r;
    private GuiButton field_175390_s;
    private GuiButton modeBtn;
    private GuiButton conditionalBtn;
    private GuiButton autoExecBtn;
    private boolean field_175389_t;
    private TileEntityCommandBlock.Mode commandBlockMode = TileEntityCommandBlock.Mode.REDSTONE;
    private TabCompleter field_184083_x;
    private boolean conditional;
    private boolean automatic;

    public GuiCommandBlock(TileEntityCommandBlock commandBlockIn)
    {
        this.commandBlock = commandBlockIn;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void tick()
    {
        this.field_146485_f.tick();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        final CommandBlockBaseLogic commandblockbaselogic = this.commandBlock.getCommandBlockLogic();
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.field_146490_i = this.addButton(new GuiButton(0, this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.format("gui.done")));
        this.field_146487_r = this.addButton(new GuiButton(1, this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.format("gui.cancel")));
        this.field_175390_s = this.addButton(new GuiButton(4, this.width / 2 + 150 - 20, 135, 20, 20, "O"));
        this.modeBtn = this.addButton(new GuiButton(5, this.width / 2 - 50 - 100 - 4, 165, 100, 20, I18n.format("advMode.mode.sequence")));
        this.conditionalBtn = this.addButton(new GuiButton(6, this.width / 2 - 50, 165, 100, 20, I18n.format("advMode.mode.unconditional")));
        this.autoExecBtn = this.addButton(new GuiButton(7, this.width / 2 + 50 + 4, 165, 100, 20, I18n.format("advMode.mode.redstoneTriggered")));
        this.field_146485_f = new GuiTextField(2, this.fontRenderer, this.width / 2 - 150, 50, 300, 20);
        this.field_146485_f.setMaxStringLength(32500);
        this.field_146485_f.setFocused(true);
        this.field_146486_g = new GuiTextField(3, this.fontRenderer, this.width / 2 - 150, 135, 276, 20);
        this.field_146486_g.setMaxStringLength(32500);
        this.field_146486_g.setEnabled(false);
        this.field_146486_g.setText("-");
        this.field_146490_i.enabled = false;
        this.field_175390_s.enabled = false;
        this.modeBtn.enabled = false;
        this.conditionalBtn.enabled = false;
        this.autoExecBtn.enabled = false;
        this.field_184083_x = new TabCompleter(this.field_146485_f, true)
        {
            @Nullable
            public BlockPos func_186839_b()
            {
                return commandblockbaselogic.getPosition();
            }
        };
    }

    public void updateGui()
    {
        CommandBlockBaseLogic commandblockbaselogic = this.commandBlock.getCommandBlockLogic();
        this.field_146485_f.setText(commandblockbaselogic.getCommand());
        this.field_175389_t = commandblockbaselogic.shouldTrackOutput();
        this.commandBlockMode = this.commandBlock.getMode();
        this.conditional = this.commandBlock.isConditional();
        this.automatic = this.commandBlock.isAuto();
        this.func_175388_a();
        this.updateMode();
        this.updateConditional();
        this.updateAutoExec();
        this.field_146490_i.enabled = true;
        this.field_175390_s.enabled = true;
        this.modeBtn.enabled = true;
        this.conditionalBtn.enabled = true;
        this.autoExecBtn.enabled = true;
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
            CommandBlockBaseLogic commandblockbaselogic = this.commandBlock.getCommandBlockLogic();

            if (p_146284_1_.id == 1)
            {
                commandblockbaselogic.setTrackOutput(this.field_175389_t);
                this.mc.displayGuiScreen((GuiScreen)null);
            }
            else if (p_146284_1_.id == 0)
            {
                PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
                commandblockbaselogic.func_145757_a(packetbuffer);
                packetbuffer.writeString(this.field_146485_f.getText());
                packetbuffer.writeBoolean(commandblockbaselogic.shouldTrackOutput());
                packetbuffer.writeString(this.commandBlockMode.name());
                packetbuffer.writeBoolean(this.conditional);
                packetbuffer.writeBoolean(this.automatic);
                this.mc.getConnection().sendPacket(new CPacketCustomPayload("MC|AutoCmd", packetbuffer));

                if (!commandblockbaselogic.shouldTrackOutput())
                {
                    commandblockbaselogic.setLastOutput((ITextComponent)null);
                }

                this.mc.displayGuiScreen((GuiScreen)null);
            }
            else if (p_146284_1_.id == 4)
            {
                commandblockbaselogic.setTrackOutput(!commandblockbaselogic.shouldTrackOutput());
                this.func_175388_a();
            }
            else if (p_146284_1_.id == 5)
            {
                this.nextMode();
                this.updateMode();
            }
            else if (p_146284_1_.id == 6)
            {
                this.conditional = !this.conditional;
                this.updateConditional();
            }
            else if (p_146284_1_.id == 7)
            {
                this.automatic = !this.automatic;
                this.updateAutoExec();
            }
        }
    }

    protected void func_73869_a(char p_73869_1_, int p_73869_2_) throws IOException
    {
        this.field_184083_x.func_186843_d();

        if (p_73869_2_ == 15)
        {
            this.field_184083_x.func_186841_a();
        }
        else
        {
            this.field_184083_x.func_186842_c();
        }

        this.field_146485_f.func_146201_a(p_73869_1_, p_73869_2_);
        this.field_146486_g.func_146201_a(p_73869_1_, p_73869_2_);

        if (p_73869_2_ != 28 && p_73869_2_ != 156)
        {
            if (p_73869_2_ == 1)
            {
                this.func_146284_a(this.field_146487_r);
            }
        }
        else
        {
            this.func_146284_a(this.field_146490_i);
        }
    }

    protected void func_73864_a(int p_73864_1_, int p_73864_2_, int p_73864_3_) throws IOException
    {
        super.func_73864_a(p_73864_1_, p_73864_2_, p_73864_3_);
        this.field_146485_f.func_146192_a(p_73864_1_, p_73864_2_, p_73864_3_);
        this.field_146486_g.func_146192_a(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format("advMode.setCommand"), this.width / 2, 20, 16777215);
        this.drawString(this.fontRenderer, I18n.format("advMode.command"), this.width / 2 - 150, 40, 10526880);
        this.field_146485_f.func_146194_f();
        int i = 75;
        int j = 0;
        this.drawString(this.fontRenderer, I18n.format("advMode.nearestPlayer"), this.width / 2 - 140, i + j++ * this.fontRenderer.FONT_HEIGHT, 10526880);
        this.drawString(this.fontRenderer, I18n.format("advMode.randomPlayer"), this.width / 2 - 140, i + j++ * this.fontRenderer.FONT_HEIGHT, 10526880);
        this.drawString(this.fontRenderer, I18n.format("advMode.allPlayers"), this.width / 2 - 140, i + j++ * this.fontRenderer.FONT_HEIGHT, 10526880);
        this.drawString(this.fontRenderer, I18n.format("advMode.allEntities"), this.width / 2 - 140, i + j++ * this.fontRenderer.FONT_HEIGHT, 10526880);
        this.drawString(this.fontRenderer, I18n.format("advMode.self"), this.width / 2 - 140, i + j++ * this.fontRenderer.FONT_HEIGHT, 10526880);

        if (!this.field_146486_g.getText().isEmpty())
        {
            i = i + j * this.fontRenderer.FONT_HEIGHT + 1;
            this.drawString(this.fontRenderer, I18n.format("advMode.previousOutput"), this.width / 2 - 150, i + 4, 10526880);
            this.field_146486_g.func_146194_f();
        }

        super.render(mouseX, mouseY, partialTicks);
    }

    private void func_175388_a()
    {
        CommandBlockBaseLogic commandblockbaselogic = this.commandBlock.getCommandBlockLogic();

        if (commandblockbaselogic.shouldTrackOutput())
        {
            this.field_175390_s.displayString = "O";

            if (commandblockbaselogic.getLastOutput() != null)
            {
                this.field_146486_g.setText(commandblockbaselogic.getLastOutput().func_150260_c());
            }
        }
        else
        {
            this.field_175390_s.displayString = "X";
            this.field_146486_g.setText("-");
        }
    }

    private void updateMode()
    {
        switch (this.commandBlockMode)
        {
            case SEQUENCE:
                this.modeBtn.displayString = I18n.format("advMode.mode.sequence");
                break;
            case AUTO:
                this.modeBtn.displayString = I18n.format("advMode.mode.auto");
                break;
            case REDSTONE:
                this.modeBtn.displayString = I18n.format("advMode.mode.redstone");
        }
    }

    private void nextMode()
    {
        switch (this.commandBlockMode)
        {
            case SEQUENCE:
                this.commandBlockMode = TileEntityCommandBlock.Mode.AUTO;
                break;
            case AUTO:
                this.commandBlockMode = TileEntityCommandBlock.Mode.REDSTONE;
                break;
            case REDSTONE:
                this.commandBlockMode = TileEntityCommandBlock.Mode.SEQUENCE;
        }
    }

    private void updateConditional()
    {
        if (this.conditional)
        {
            this.conditionalBtn.displayString = I18n.format("advMode.mode.conditional");
        }
        else
        {
            this.conditionalBtn.displayString = I18n.format("advMode.mode.unconditional");
        }
    }

    private void updateAutoExec()
    {
        if (this.automatic)
        {
            this.autoExecBtn.displayString = I18n.format("advMode.mode.autoexec.bat");
        }
        else
        {
            this.autoExecBtn.displayString = I18n.format("advMode.mode.redstoneTriggered");
        }
    }

    public void func_184072_a(String... p_184072_1_)
    {
        this.field_184083_x.func_186840_a(p_184072_1_);
    }
}