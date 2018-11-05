package net.minecraft.client.gui;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiRepair extends GuiContainer implements IContainerListener
{
    private static final ResourceLocation ANVIL_RESOURCE = new ResourceLocation("textures/gui/container/anvil.png");
    /** The same reference as {@link GuiContainer#field_147002_h}, downcasted to {@link ContainerRepair}. */
    private final ContainerRepair anvil;
    private GuiTextField nameField;
    private final InventoryPlayer playerInventory;

    public GuiRepair(InventoryPlayer inventoryIn, World worldIn)
    {
        super(new ContainerRepair(inventoryIn, worldIn, Minecraft.getInstance().player));
        this.playerInventory = inventoryIn;
        this.anvil = (ContainerRepair)this.inventorySlots;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.nameField = new GuiTextField(0, this.fontRenderer, i + 62, j + 24, 103, 12);
        this.nameField.setTextColor(-1);
        this.nameField.setDisabledTextColour(-1);
        this.nameField.setEnableBackgroundDrawing(false);
        this.nameField.setMaxStringLength(35);
        this.inventorySlots.removeListener(this);
        this.inventorySlots.addListener(this);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        this.inventorySlots.removeListener(this);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.fontRenderer.func_78276_b(I18n.format("container.repair"), 60, 6, 4210752);

        if (this.anvil.maximumCost > 0)
        {
            int i = 8453920;
            boolean flag = true;
            String s = I18n.format("container.repair.cost", this.anvil.maximumCost);

            if (this.anvil.maximumCost >= 40 && !this.mc.player.abilities.isCreativeMode)
            {
                s = I18n.format("container.repair.expensive");
                i = 16736352;
            }
            else if (!this.anvil.getSlot(2).getHasStack())
            {
                flag = false;
            }
            else if (!this.anvil.getSlot(2).canTakeStack(this.playerInventory.player))
            {
                i = 16736352;
            }

            if (flag)
            {
                int j = -16777216 | (i & 16579836) >> 2 | i & -16777216;
                int k = this.xSize - 8 - this.fontRenderer.getStringWidth(s);
                int l = 67;

                if (this.fontRenderer.func_82883_a())
                {
                    drawRect(k - 3, 65, this.xSize - 7, 77, -16777216);
                    drawRect(k - 2, 66, this.xSize - 8, 76, -12895429);
                }
                else
                {
                    this.fontRenderer.func_78276_b(s, k, 68, j);
                    this.fontRenderer.func_78276_b(s, k + 1, 67, j);
                    this.fontRenderer.func_78276_b(s, k + 1, 68, j);
                }

                this.fontRenderer.func_78276_b(s, k, 67, i);
            }
        }

        GlStateManager.enableLighting();
    }

    protected void func_73869_a(char p_73869_1_, int p_73869_2_) throws IOException
    {
        if (this.nameField.func_146201_a(p_73869_1_, p_73869_2_))
        {
            this.func_147090_g();
        }
        else
        {
            super.func_73869_a(p_73869_1_, p_73869_2_);
        }
    }

    private void func_147090_g()
    {
        String s = this.nameField.getText();
        Slot slot = this.anvil.getSlot(0);

        if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && s.equals(slot.getStack().func_82833_r()))
        {
            s = "";
        }

        this.anvil.updateItemName(s);
        this.mc.player.connection.sendPacket(new CPacketCustomPayload("MC|ItemName", (new PacketBuffer(Unpooled.buffer())).writeString(s)));
    }

    protected void func_73864_a(int p_73864_1_, int p_73864_2_, int p_73864_3_) throws IOException
    {
        super.func_73864_a(p_73864_1_, p_73864_2_, p_73864_3_);
        this.nameField.func_146192_a(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.nameField.func_146194_f();
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ANVIL_RESOURCE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(i + 59, j + 20, 0, this.ySize + (this.anvil.getSlot(0).getHasStack() ? 0 : 16), 110, 16);

        if ((this.anvil.getSlot(0).getHasStack() || this.anvil.getSlot(1).getHasStack()) && !this.anvil.getSlot(2).getHasStack())
        {
            this.drawTexturedModalRect(i + 99, j + 45, this.xSize, 0, 28, 21);
        }
    }

    /**
     * update the crafting window inventory with the items in the list
     */
    public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList)
    {
        this.sendSlotContents(containerToSend, 0, containerToSend.getSlot(0).getStack());
    }

    /**
     * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
     * contents of that slot.
     */
    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack)
    {
        if (slotInd == 0)
        {
            this.nameField.setText(stack.isEmpty() ? "" : stack.func_82833_r());
            this.nameField.setEnabled(!stack.isEmpty());

            if (!stack.isEmpty())
            {
                this.func_147090_g();
            }
        }
    }

    /**
     * Sends two ints to the client-side Container. Used for furnace burning time, smelting progress, brewing progress,
     * and enchanting level. Normally the first int identifies which variable to update, and the second contains the new
     * value. Both are truncated to shorts in non-local SMP.
     */
    public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue)
    {
    }

    public void sendAllWindowProperties(Container containerIn, IInventory inventory)
    {
    }
}