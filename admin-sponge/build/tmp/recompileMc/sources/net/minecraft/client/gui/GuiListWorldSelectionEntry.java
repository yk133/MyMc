package net.minecraft.client.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class GuiListWorldSelectionEntry implements GuiListExtended.IGuiListEntry
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/world_selection.png");
    private final Minecraft client;
    private final GuiWorldSelection worldSelScreen;
    private final WorldSummary worldSummary;
    private final ResourceLocation iconLocation;
    private final GuiListWorldSelection containingListSel;
    private File iconFile;
    private DynamicTexture icon;
    private long lastClickTime;

    public GuiListWorldSelectionEntry(GuiListWorldSelection listWorldSelIn, WorldSummary worldSummaryIn, ISaveFormat saveFormat)
    {
        this.containingListSel = listWorldSelIn;
        this.worldSelScreen = listWorldSelIn.getGuiWorldSelection();
        this.worldSummary = worldSummaryIn;
        this.client = Minecraft.getInstance();
        this.iconLocation = new ResourceLocation("worlds/" + worldSummaryIn.getFileName() + "/icon");
        this.iconFile = saveFormat.getFile(worldSummaryIn.getFileName(), "icon.png");

        if (!this.iconFile.isFile())
        {
            this.iconFile = null;
        }

        this.func_186769_f();
    }

    public void func_192634_a(int p_192634_1_, int p_192634_2_, int p_192634_3_, int p_192634_4_, int p_192634_5_, int p_192634_6_, int p_192634_7_, boolean p_192634_8_, float p_192634_9_)
    {
        String s = this.worldSummary.getDisplayName();
        String s1 = this.worldSummary.getFileName() + " (" + DATE_FORMAT.format(new Date(this.worldSummary.getLastTimePlayed())) + ")";
        String s2 = "";

        if (StringUtils.isEmpty(s))
        {
            s = I18n.format("selectWorld.world") + " " + (p_192634_1_ + 1);
        }

        if (this.worldSummary.requiresConversion())
        {
            s2 = I18n.format("selectWorld.conversion") + " " + s2;
        }
        else
        {
            s2 = I18n.format("gameMode." + this.worldSummary.getEnumGameType().getName());

            if (this.worldSummary.isHardcoreModeEnabled())
            {
                s2 = TextFormatting.DARK_RED + I18n.format("gameMode.hardcore") + TextFormatting.RESET;
            }

            if (this.worldSummary.getCheatsEnabled())
            {
                s2 = s2 + ", " + I18n.format("selectWorld.cheats");
            }

            String s3 = this.worldSummary.func_186357_i();

            if (this.worldSummary.markVersionInList())
            {
                if (this.worldSummary.askToOpenWorld())
                {
                    s2 = s2 + ", " + I18n.format("selectWorld.version") + " " + TextFormatting.RED + s3 + TextFormatting.RESET;
                }
                else
                {
                    s2 = s2 + ", " + I18n.format("selectWorld.version") + " " + TextFormatting.ITALIC + s3 + TextFormatting.RESET;
                }
            }
            else
            {
                s2 = s2 + ", " + I18n.format("selectWorld.version") + " " + s3;
            }
        }

        this.client.fontRenderer.func_78276_b(s, p_192634_2_ + 32 + 3, p_192634_3_ + 1, 16777215);
        this.client.fontRenderer.func_78276_b(s1, p_192634_2_ + 32 + 3, p_192634_3_ + this.client.fontRenderer.FONT_HEIGHT + 3, 8421504);
        this.client.fontRenderer.func_78276_b(s2, p_192634_2_ + 32 + 3, p_192634_3_ + this.client.fontRenderer.FONT_HEIGHT + this.client.fontRenderer.FONT_HEIGHT + 3, 8421504);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(this.icon != null ? this.iconLocation : ICON_MISSING);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        GlStateManager.disableBlend();

        if (this.client.gameSettings.touchscreen || p_192634_8_)
        {
            this.client.getTextureManager().bindTexture(ICON_OVERLAY_LOCATION);
            Gui.drawRect(p_192634_2_, p_192634_3_, p_192634_2_ + 32, p_192634_3_ + 32, -1601138544);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int j = p_192634_6_ - p_192634_2_;
            int i = j < 32 ? 32 : 0;

            if (this.worldSummary.markVersionInList())
            {
                Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 32.0F, (float)i, 32, 32, 256.0F, 256.0F);

                if (this.worldSummary.askToOpenWorld())
                {
                    Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 96.0F, (float)i, 32, 32, 256.0F, 256.0F);

                    if (j < 32)
                    {
                        this.worldSelScreen.setVersionTooltip(TextFormatting.RED + I18n.format("selectWorld.tooltip.fromNewerVersion1") + "\n" + TextFormatting.RED + I18n.format("selectWorld.tooltip.fromNewerVersion2"));
                    }
                }
                else
                {
                    Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 64.0F, (float)i, 32, 32, 256.0F, 256.0F);

                    if (j < 32)
                    {
                        this.worldSelScreen.setVersionTooltip(TextFormatting.GOLD + I18n.format("selectWorld.tooltip.snapshot1") + "\n" + TextFormatting.GOLD + I18n.format("selectWorld.tooltip.snapshot2"));
                    }
                }
            }
            else
            {
                Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 0.0F, (float)i, 32, 32, 256.0F, 256.0F);
            }
        }
    }

    public boolean func_148278_a(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
    {
        this.containingListSel.selectWorld(p_148278_1_);

        if (p_148278_5_ <= 32 && p_148278_5_ < 32)
        {
            this.joinWorld();
            return true;
        }
        else if (Minecraft.func_71386_F() - this.lastClickTime < 250L)
        {
            this.joinWorld();
            return true;
        }
        else
        {
            this.lastClickTime = Minecraft.func_71386_F();
            return false;
        }
    }

    public void joinWorld()
    {
        if (this.worldSummary.askToOpenWorld())
        {
            this.client.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback()
            {
                public void func_73878_a(boolean p_73878_1_, int p_73878_2_)
                {
                    if (p_73878_1_)
                    {
                        GuiListWorldSelectionEntry.this.loadWorld();
                    }
                    else
                    {
                        GuiListWorldSelectionEntry.this.client.displayGuiScreen(GuiListWorldSelectionEntry.this.worldSelScreen);
                    }
                }
            }, I18n.format("selectWorld.versionQuestion"), I18n.format("selectWorld.versionWarning", this.worldSummary.func_186357_i()), I18n.format("selectWorld.versionJoinButton"), I18n.format("gui.cancel"), 0));
        }
        else
        {
            this.loadWorld();
        }
    }

    public void deleteWorld()
    {
        this.client.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback()
        {
            public void func_73878_a(boolean p_73878_1_, int p_73878_2_)
            {
                if (p_73878_1_)
                {
                    GuiListWorldSelectionEntry.this.client.displayGuiScreen(new GuiScreenWorking());
                    ISaveFormat isaveformat = GuiListWorldSelectionEntry.this.client.getSaveLoader();
                    isaveformat.flushCache();
                    isaveformat.deleteWorldDirectory(GuiListWorldSelectionEntry.this.worldSummary.getFileName());
                    GuiListWorldSelectionEntry.this.containingListSel.refreshList();
                }

                GuiListWorldSelectionEntry.this.client.displayGuiScreen(GuiListWorldSelectionEntry.this.worldSelScreen);
            }
        }, I18n.format("selectWorld.deleteQuestion"), "'" + this.worldSummary.getDisplayName() + "' " + I18n.format("selectWorld.deleteWarning"), I18n.format("selectWorld.deleteButton"), I18n.format("gui.cancel"), 0));
    }

    public void editWorld()
    {
        this.client.displayGuiScreen(new GuiWorldEdit(this.worldSelScreen, this.worldSummary.getFileName()));
    }

    public void recreateWorld()
    {
        this.client.displayGuiScreen(new GuiScreenWorking());
        GuiCreateWorld guicreateworld = new GuiCreateWorld(this.worldSelScreen);
        ISaveHandler isavehandler = this.client.getSaveLoader().func_75804_a(this.worldSummary.getFileName(), false);
        WorldInfo worldinfo = isavehandler.loadWorldInfo();
        isavehandler.flush();

        if (worldinfo != null)
        {
            guicreateworld.recreateFromExistingWorld(worldinfo);
            this.client.displayGuiScreen(guicreateworld);
        }
    }

    private void loadWorld()
    {
        this.client.getSoundHandler().play(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));

        if (this.client.getSaveLoader().canLoadWorld(this.worldSummary.getFileName()))
        {
            net.minecraftforge.fml.client.FMLClientHandler.instance().tryLoadExistingWorld(worldSelScreen, this.worldSummary);
        }
    }

    private void func_186769_f()
    {
        boolean flag = this.iconFile != null && this.iconFile.isFile();

        if (flag)
        {
            BufferedImage bufferedimage;

            try
            {
                bufferedimage = ImageIO.read(this.iconFile);
                Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide");
                Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high");
            }
            catch (Throwable throwable)
            {
                LOGGER.error("Invalid icon for world {}", this.worldSummary.getFileName(), throwable);
                this.iconFile = null;
                return;
            }

            if (this.icon == null)
            {
                this.icon = new DynamicTexture(bufferedimage.getWidth(), bufferedimage.getHeight());
                this.client.getTextureManager().loadTexture(this.iconLocation, this.icon);
            }

            bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), this.icon.func_110565_c(), 0, bufferedimage.getWidth());
            this.icon.updateDynamicTexture();
        }
        else if (!flag)
        {
            this.client.getTextureManager().deleteTexture(this.iconLocation);
            this.icon = null;
        }
    }

    public void func_148277_b(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_)
    {
    }

    public void func_192633_a(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_)
    {
    }
}