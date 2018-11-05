package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ScorePlayerTeam extends Team
{
    private final Scoreboard scoreboard;
    private final String name;
    /** A set of all team member usernames. */
    private final Set<String> membershipSet = Sets.<String>newHashSet();
    private String displayName;
    private String field_96674_e = "";
    private String field_96671_f = "";
    private boolean allowFriendlyFire = true;
    private boolean canSeeFriendlyInvisibles = true;
    private Team.EnumVisible nameTagVisibility = Team.EnumVisible.ALWAYS;
    private Team.EnumVisible deathMessageVisibility = Team.EnumVisible.ALWAYS;
    private TextFormatting color = TextFormatting.RESET;
    private Team.CollisionRule collisionRule = Team.CollisionRule.ALWAYS;

    public ScorePlayerTeam(Scoreboard scoreboardIn, String name)
    {
        this.scoreboard = scoreboardIn;
        this.name = name;
        this.displayName = name;
    }

    /**
     * Retrieve the name by which this team is registered in the scoreboard
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Gets the display name for this team.
     */
    public String getDisplayName()
    {
        return this.displayName;
    }

    /**
     * Sets the display name for this team.
     */
    public void setDisplayName(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Name cannot be null");
        }
        else
        {
            this.displayName = name;
            this.scoreboard.broadcastTeamInfoUpdate(this);
        }
    }

    /**
     * Gets a collection of all members of this team.
     */
    public Collection<String> getMembershipCollection()
    {
        return this.membershipSet;
    }

    public String func_96668_e()
    {
        return this.field_96674_e;
    }

    public void func_96666_b(String p_96666_1_)
    {
        if (p_96666_1_ == null)
        {
            throw new IllegalArgumentException("Prefix cannot be null");
        }
        else
        {
            this.field_96674_e = p_96666_1_;
            this.scoreboard.broadcastTeamInfoUpdate(this);
        }
    }

    public String func_96663_f()
    {
        return this.field_96671_f;
    }

    public void func_96662_c(String p_96662_1_)
    {
        this.field_96671_f = p_96662_1_;
        this.scoreboard.broadcastTeamInfoUpdate(this);
    }

    public String func_142053_d(String p_142053_1_)
    {
        return this.func_96668_e() + p_142053_1_ + this.func_96663_f();
    }

    public static String func_96667_a(@Nullable Team p_96667_0_, String p_96667_1_)
    {
        return p_96667_0_ == null ? p_96667_1_ : p_96667_0_.func_142053_d(p_96667_1_);
    }

    /**
     * Checks whether friendly fire (PVP between members of the team) is allowed.
     */
    public boolean getAllowFriendlyFire()
    {
        return this.allowFriendlyFire;
    }

    /**
     * Sets whether friendly fire (PVP between members of the team) is allowed.
     */
    public void setAllowFriendlyFire(boolean friendlyFire)
    {
        this.allowFriendlyFire = friendlyFire;
        this.scoreboard.broadcastTeamInfoUpdate(this);
    }

    /**
     * Checks whether members of this team can see other members that are invisible.
     */
    public boolean getSeeFriendlyInvisiblesEnabled()
    {
        return this.canSeeFriendlyInvisibles;
    }

    /**
     * Sets whether members of this team can see other members that are invisible.
     */
    public void setSeeFriendlyInvisiblesEnabled(boolean friendlyInvisibles)
    {
        this.canSeeFriendlyInvisibles = friendlyInvisibles;
        this.scoreboard.broadcastTeamInfoUpdate(this);
    }

    /**
     * Gets the visibility flags for player name tags.
     */
    public Team.EnumVisible getNameTagVisibility()
    {
        return this.nameTagVisibility;
    }

    /**
     * Gets the visibility flags for player death messages.
     */
    public Team.EnumVisible getDeathMessageVisibility()
    {
        return this.deathMessageVisibility;
    }

    /**
     * Sets the visibility flags for player name tags.
     */
    public void setNameTagVisibility(Team.EnumVisible visibility)
    {
        this.nameTagVisibility = visibility;
        this.scoreboard.broadcastTeamInfoUpdate(this);
    }

    /**
     * Sets the visibility flags for player death messages.
     */
    public void setDeathMessageVisibility(Team.EnumVisible visibility)
    {
        this.deathMessageVisibility = visibility;
        this.scoreboard.broadcastTeamInfoUpdate(this);
    }

    /**
     * Gets the rule to be used for handling collisions with members of this team.
     */
    public Team.CollisionRule getCollisionRule()
    {
        return this.collisionRule;
    }

    /**
     * Sets the rule to be used for handling collisions with members of this team.
     */
    public void setCollisionRule(Team.CollisionRule rule)
    {
        this.collisionRule = rule;
        this.scoreboard.broadcastTeamInfoUpdate(this);
    }

    /**
     * Gets a bitmask containing the friendly fire and invisibles flags.
     */
    public int getFriendlyFlags()
    {
        int i = 0;

        if (this.getAllowFriendlyFire())
        {
            i |= 1;
        }

        if (this.getSeeFriendlyInvisiblesEnabled())
        {
            i |= 2;
        }

        return i;
    }

    /**
     * Sets friendly fire and invisibles flags based off of the given bitmask.
     */
    @SideOnly(Side.CLIENT)
    public void setFriendlyFlags(int flags)
    {
        this.setAllowFriendlyFire((flags & 1) > 0);
        this.setSeeFriendlyInvisiblesEnabled((flags & 2) > 0);
    }

    /**
     * Sets the color for this team. The team color is used mainly for team kill objectives and team-specific setDisplay
     * usage; it does _not_ affect all situations (for instance, the prefix is used for the glowing effect).
     */
    public void setColor(TextFormatting color)
    {
        this.color = color;
    }

    /**
     * Gets the color for this team. The team color is used mainly for team kill objectives and team-specific setDisplay
     * usage; it does _not_ affect all situations (for instance, the prefix is used for the glowing effect).
     */
    public TextFormatting getColor()
    {
        return this.color;
    }
}