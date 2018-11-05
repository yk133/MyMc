package net.minecraft.client.tutorial;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PunchTreeStep implements ITutorialStep
{
    private static final Set<Block> field_193274_a = Sets.newHashSet(Blocks.field_150364_r, Blocks.field_150363_s);
    private static final ITextComponent TITLE = new TextComponentTranslation("tutorial.punch_tree.title", new Object[0]);
    private static final ITextComponent DESCRIPTION = new TextComponentTranslation("tutorial.punch_tree.description", new Object[] {Tutorial.createKeybindComponent("attack")});
    private final Tutorial tutorial;
    private TutorialToast toast;
    private int timeWaiting;
    private int resetCount;

    public PunchTreeStep(Tutorial tutorial)
    {
        this.tutorial = tutorial;
    }

    public void tick()
    {
        ++this.timeWaiting;

        if (this.tutorial.getGameType() != GameType.SURVIVAL)
        {
            this.tutorial.setStep(TutorialSteps.NONE);
        }
        else
        {
            if (this.timeWaiting == 1)
            {
                EntityPlayerSP entityplayersp = this.tutorial.getMinecraft().player;

                if (entityplayersp != null)
                {
                    for (Block block : field_193274_a)
                    {
                        if (entityplayersp.inventory.hasItemStack(new ItemStack(block)))
                        {
                            this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                            return;
                        }
                    }

                    if (FindTreeStep.hasPunchedTreesPreviously(entityplayersp))
                    {
                        this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                        return;
                    }
                }
            }

            if ((this.timeWaiting >= 600 || this.resetCount > 3) && this.toast == null)
            {
                this.toast = new TutorialToast(TutorialToast.Icons.TREE, TITLE, DESCRIPTION, true);
                this.tutorial.getMinecraft().getToastGui().add(this.toast);
            }
        }
    }

    public void onStop()
    {
        if (this.toast != null)
        {
            this.toast.hide();
            this.toast = null;
        }
    }

    /**
     * Called when a player hits block to destroy it.
     */
    public void onHitBlock(WorldClient worldIn, BlockPos pos, IBlockState state, float diggingStage)
    {
        boolean flag = field_193274_a.contains(state.getBlock());

        if (flag && diggingStage > 0.0F)
        {
            if (this.toast != null)
            {
                this.toast.setProgress(diggingStage);
            }

            if (diggingStage >= 1.0F)
            {
                this.tutorial.setStep(TutorialSteps.OPEN_INVENTORY);
            }
        }
        else if (this.toast != null)
        {
            this.toast.setProgress(0.0F);
        }
        else if (flag)
        {
            ++this.resetCount;
        }
    }

    /**
     * Called when the player pick up an ItemStack
     */
    public void handleSetSlot(ItemStack stack)
    {
        for (Block block : field_193274_a)
        {
            if (stack.getItem() == Item.getItemFromBlock(block))
            {
                this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                return;
            }
        }
    }
}