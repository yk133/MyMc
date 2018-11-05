package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListOps extends UserList<GameProfile, UserListOpsEntry>
{
    public UserListOps(File saveFile)
    {
        super(saveFile);
    }

    protected UserListEntry<GameProfile> createEntry(JsonObject entryData)
    {
        return new UserListOpsEntry(entryData);
    }

    public String[] getKeys()
    {
        String[] astring = new String[this.func_152688_e().size()];
        int i = 0;

        for (UserListOpsEntry userlistopsentry : this.func_152688_e().values())
        {
            astring[i++] = ((GameProfile)userlistopsentry.getValue()).getName();
        }

        return astring;
    }

    public int func_187452_a(GameProfile p_187452_1_)
    {
        UserListOpsEntry userlistopsentry = (UserListOpsEntry)this.getEntry(p_187452_1_);
        return userlistopsentry != null ? userlistopsentry.getPermissionLevel() : 0;
    }

    public boolean bypassesPlayerLimit(GameProfile profile)
    {
        UserListOpsEntry userlistopsentry = (UserListOpsEntry)this.getEntry(profile);
        return userlistopsentry != null ? userlistopsentry.bypassesPlayerLimit() : false;
    }

    /**
     * Gets the key value for the given object
     */
    protected String getObjectKey(GameProfile obj)
    {
        return obj.getId().toString();
    }

    public GameProfile func_152700_a(String p_152700_1_)
    {
        for (UserListOpsEntry userlistopsentry : this.func_152688_e().values())
        {
            if (p_152700_1_.equalsIgnoreCase(((GameProfile)userlistopsentry.getValue()).getName()))
            {
                return (GameProfile)userlistopsentry.getValue();
            }
        }

        return null;
    }
}