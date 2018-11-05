package net.minecraft.server.dedicated;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.SERVER)
public class DedicatedPlayerList extends PlayerList
{
    private static final Logger LOGGER = LogManager.getLogger();

    public DedicatedPlayerList(DedicatedServer server)
    {
        super(server);
        this.setViewDistance(server.getIntProperty("view-distance", 10));
        this.maxPlayers = server.getIntProperty("max-players", 20);
        this.setWhiteListEnabled(server.getBooleanProperty("white-list", false));

        if (!server.isSinglePlayer())
        {
            this.getBannedPlayers().setLanServer(true);
            this.getBannedIPs().setLanServer(true);
        }

        this.loadPlayerBanList();
        this.savePlayerBanList();
        this.loadIPBanList();
        this.saveIPBanList();
        this.loadOpsList();
        this.readWhiteList();
        this.saveOpsList();

        if (!this.getWhitelistedPlayers().getSaveFile().exists())
        {
            this.saveWhiteList();
        }
    }

    public void setWhiteListEnabled(boolean whitelistEnabled)
    {
        super.setWhiteListEnabled(whitelistEnabled);
        this.getServer().setProperty("white-list", Boolean.valueOf(whitelistEnabled));
        this.getServer().saveProperties();
    }

    public void addOp(GameProfile profile)
    {
        super.addOp(profile);
        this.saveOpsList();
    }

    public void removeOp(GameProfile profile)
    {
        super.removeOp(profile);
        this.saveOpsList();
    }

    public void func_152597_c(GameProfile p_152597_1_)
    {
        super.func_152597_c(p_152597_1_);
        this.saveWhiteList();
    }

    public void func_152601_d(GameProfile p_152601_1_)
    {
        super.func_152601_d(p_152601_1_);
        this.saveWhiteList();
    }

    public void reloadWhitelist()
    {
        this.readWhiteList();
    }

    private void saveIPBanList()
    {
        try
        {
            this.getBannedIPs().writeChanges();
        }
        catch (IOException ioexception)
        {
            LOGGER.warn("Failed to save ip banlist: ", (Throwable)ioexception);
        }
    }

    private void savePlayerBanList()
    {
        try
        {
            this.getBannedPlayers().writeChanges();
        }
        catch (IOException ioexception)
        {
            LOGGER.warn("Failed to save user banlist: ", (Throwable)ioexception);
        }
    }

    private void loadIPBanList()
    {
        try
        {
            this.getBannedIPs().readSavedFile();
        }
        catch (IOException ioexception)
        {
            LOGGER.warn("Failed to load ip banlist: ", (Throwable)ioexception);
        }
    }

    private void loadPlayerBanList()
    {
        try
        {
            this.getBannedPlayers().readSavedFile();
        }
        catch (IOException ioexception)
        {
            LOGGER.warn("Failed to load user banlist: ", (Throwable)ioexception);
        }
    }

    private void loadOpsList()
    {
        try
        {
            this.getOppedPlayers().readSavedFile();
        }
        catch (Exception exception)
        {
            LOGGER.warn("Failed to load operators list: ", (Throwable)exception);
        }
    }

    private void saveOpsList()
    {
        try
        {
            this.getOppedPlayers().writeChanges();
        }
        catch (Exception exception)
        {
            LOGGER.warn("Failed to save operators list: ", (Throwable)exception);
        }
    }

    private void readWhiteList()
    {
        try
        {
            this.getWhitelistedPlayers().readSavedFile();
        }
        catch (Exception exception)
        {
            LOGGER.warn("Failed to load white-list: ", (Throwable)exception);
        }
    }

    private void saveWhiteList()
    {
        try
        {
            this.getWhitelistedPlayers().writeChanges();
        }
        catch (Exception exception)
        {
            LOGGER.warn("Failed to save white-list: ", (Throwable)exception);
        }
    }

    public boolean canJoin(GameProfile profile)
    {
        return !this.isWhiteListEnabled() || this.canSendCommands(profile) || this.getWhitelistedPlayers().isWhitelisted(profile);
    }

    public DedicatedServer getServer()
    {
        return (DedicatedServer)super.getServer();
    }

    public boolean bypassesPlayerLimit(GameProfile profile)
    {
        return this.getOppedPlayers().bypassesPlayerLimit(profile);
    }
}