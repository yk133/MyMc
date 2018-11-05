package net.minecraft.client.settings;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IntHashMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class KeyBinding implements Comparable<KeyBinding>
{
    private static final Map<String, KeyBinding> KEYBIND_ARRAY = Maps.<String, KeyBinding>newHashMap();
    private static final net.minecraftforge.client.settings.KeyBindingMap HASH = new net.minecraftforge.client.settings.KeyBindingMap();
    private static final Set<String> KEYBIND_SET = Sets.<String>newHashSet();
    /** A map that assigns priorities to all categories, for ordering purposes. */
    private static final Map<String, Integer> CATEGORY_ORDER = Maps.<String, Integer>newHashMap();
    private final String keyDescription;
    private final int keyCodeDefault;
    private final String keyCategory;
    private int keyCode;
    /** Is the key held down? */
    private boolean pressed;
    private int pressTime;

    public static void func_74507_a(int p_74507_0_)
    {
        if (p_74507_0_ != 0)
        {
            KeyBinding keybinding = HASH.lookupActive(p_74507_0_);

            if (keybinding != null)
            {
                ++keybinding.pressTime;
            }
        }
    }

    public static void func_74510_a(int p_74510_0_, boolean p_74510_1_)
    {
        if (p_74510_0_ != 0)
        {
            for (KeyBinding keybinding : HASH.lookupAll(p_74510_0_))

            if (keybinding != null)
            {
                keybinding.pressed = p_74510_1_;
            }
        }
    }

    /**
     * Completely recalculates whether any keybinds are held, from scratch.
     */
    public static void updateKeyBindState()
    {
        for (KeyBinding keybinding : KEYBIND_ARRAY.values())
        {
            try
            {
                func_74510_a(keybinding.keyCode, keybinding.keyCode < 256 && Keyboard.isKeyDown(keybinding.keyCode));
            }
            catch (IndexOutOfBoundsException var3)
            {
                ;
            }
        }
    }

    public static void unPressAllKeys()
    {
        for (KeyBinding keybinding : KEYBIND_ARRAY.values())
        {
            keybinding.unpressKey();
        }
    }

    public static void resetKeyBindingArrayAndHash()
    {
        HASH.clearMap();

        for (KeyBinding keybinding : KEYBIND_ARRAY.values())
        {
            HASH.addKey(keybinding.keyCode, keybinding);
        }
    }

    public static Set<String> func_151467_c()
    {
        return KEYBIND_SET;
    }

    public KeyBinding(String description, int keyCode, String category)
    {
        this.keyDescription = description;
        this.keyCode = keyCode;
        this.keyCodeDefault = keyCode;
        this.keyCategory = category;
        KEYBIND_ARRAY.put(description, this);
        HASH.addKey(keyCode, this);
        KEYBIND_SET.add(category);
    }

    /**
     * Returns true if the key is pressed (used for continuous querying). Should be used in tickers.
     */
    public boolean isKeyDown()
    {
        return this.pressed && getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext());
    }

    public String getKeyCategory()
    {
        return this.keyCategory;
    }

    /**
     * Returns true on the initial key press. For continuous querying use {@link isKeyDown()}. Should be used in key
     * events.
     */
    public boolean isPressed()
    {
        if (this.pressTime == 0)
        {
            return false;
        }
        else
        {
            --this.pressTime;
            return true;
        }
    }

    private void unpressKey()
    {
        this.pressTime = 0;
        this.pressed = false;
    }

    public String getKeyDescription()
    {
        return this.keyDescription;
    }

    public int func_151469_h()
    {
        return this.keyCodeDefault;
    }

    public int func_151463_i()
    {
        return this.keyCode;
    }

    public void func_151462_b(int p_151462_1_)
    {
        this.keyCode = p_151462_1_;
    }

    public int compareTo(KeyBinding p_compareTo_1_)
    {
        if (this.keyCategory.equals(p_compareTo_1_.keyCategory)) return I18n.format(this.keyDescription).compareTo(I18n.format(p_compareTo_1_.keyDescription));
        Integer tCat = CATEGORY_ORDER.get(this.keyCategory);
        Integer oCat = CATEGORY_ORDER.get(p_compareTo_1_.keyCategory);
        if (tCat == null && oCat != null) return 1;
        if (tCat != null && oCat == null) return -1;
        if (tCat == null && oCat == null) return I18n.format(this.keyCategory).compareTo(I18n.format(p_compareTo_1_.keyCategory));
        return  tCat.compareTo(oCat);
    }

    /****************** Forge Start *****************************/
    private net.minecraftforge.client.settings.KeyModifier keyModifierDefault = net.minecraftforge.client.settings.KeyModifier.NONE;
    private net.minecraftforge.client.settings.KeyModifier keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
    private net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext = net.minecraftforge.client.settings.KeyConflictContext.UNIVERSAL;

    /**
     * Convenience constructor for creating KeyBindings with keyConflictContext set.
     */
    public KeyBinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, int keyCode, String category)
    {
        this(description, keyConflictContext, net.minecraftforge.client.settings.KeyModifier.NONE, keyCode, category);
    }

    /**
     * Convenience constructor for creating KeyBindings with keyConflictContext and keyModifier set.
     */
    public KeyBinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, net.minecraftforge.client.settings.KeyModifier keyModifier, int keyCode, String category)
    {
        this.keyDescription = description;
        this.keyCode = keyCode;
        this.keyCodeDefault = keyCode;
        this.keyCategory = category;
        this.keyConflictContext = keyConflictContext;
        this.keyModifier = keyModifier;
        this.keyModifierDefault = keyModifier;
        if (this.keyModifier.matches(keyCode))
        {
            this.keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
        }
        KEYBIND_ARRAY.put(description, this);
        HASH.addKey(keyCode, this);
        KEYBIND_SET.add(category);
    }

    /**
     * Checks that the key conflict context and modifier are active, and that the keyCode matches this binding.
     */
    public boolean isActiveAndMatches(int keyCode)
    {
        return keyCode != 0 && keyCode == this.func_151463_i() && getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext());
    }

    public void setKeyConflictContext(net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext)
    {
        this.keyConflictContext = keyConflictContext;
    }

    public net.minecraftforge.client.settings.IKeyConflictContext getKeyConflictContext()
    {
        return keyConflictContext;
    }

    public net.minecraftforge.client.settings.KeyModifier getKeyModifierDefault()
    {
        return keyModifierDefault;
    }

    public net.minecraftforge.client.settings.KeyModifier getKeyModifier()
    {
        return keyModifier;
    }

    public void setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier keyModifier, int keyCode)
    {
        this.keyCode = keyCode;
        if (keyModifier.matches(keyCode))
        {
            keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
        }
        HASH.removeKey(this);
        this.keyModifier = keyModifier;
        HASH.addKey(keyCode, this);
    }

    public void setToDefault()
    {
        setKeyModifierAndCode(getKeyModifierDefault(), func_151469_h());
    }

    public boolean isSetToDefaultValue()
    {
        return func_151463_i() == func_151469_h() && getKeyModifier() == getKeyModifierDefault();
    }

    /**
     * Returns true when the other keyBinding conflicts with this one
     */
    public boolean conflicts(KeyBinding other)
    {
        if (getKeyConflictContext().conflicts(other.getKeyConflictContext()) || other.getKeyConflictContext().conflicts(getKeyConflictContext()))
        {
            net.minecraftforge.client.settings.KeyModifier keyModifier = getKeyModifier();
            net.minecraftforge.client.settings.KeyModifier otherKeyModifier = other.getKeyModifier();
            if (keyModifier.matches(other.func_151463_i()) || otherKeyModifier.matches(func_151463_i()))
            {
                return true;
            }
            else if (func_151463_i() == other.func_151463_i())
            {
                return keyModifier == otherKeyModifier ||
                        // IN_GAME key contexts have a conflict when at least one modifier is NONE.
                        // For example: If you hold shift to crouch, you can still press E to open your inventory. This means that a Shift+E hotkey is in conflict with E.
                        // GUI and other key contexts do not have this limitation.
                        (getKeyConflictContext().conflicts(net.minecraftforge.client.settings.KeyConflictContext.IN_GAME) &&
                                (keyModifier == net.minecraftforge.client.settings.KeyModifier.NONE || otherKeyModifier == net.minecraftforge.client.settings.KeyModifier.NONE));
            }
        }
        return false;
    }

    /**
     * Returns true when one of the bindings' key codes conflicts with the other's modifier.
     */
    public boolean hasKeyCodeModifierConflict(KeyBinding other)
    {
        if (getKeyConflictContext().conflicts(other.getKeyConflictContext()) || other.getKeyConflictContext().conflicts(getKeyConflictContext()))
        {
            if (getKeyModifier().matches(other.func_151463_i()) || other.getKeyModifier().matches(func_151463_i()))
            {
                return true;
            }
        }
        return false;
    }

    public String getDisplayName()
    {
        return getKeyModifier().getLocalizedComboName(func_151463_i());
    }
    /****************** Forge End *****************************/

    /**
     * Returns a supplier which gets a keybind's current binding (eg, <code>key.forward</code> returns <samp>W</samp> by
     * default), or the keybind's name if no such keybind exists (eg, <code>key.invalid</code> returns
     * <samp>key.invalid</samp>)
     */
    public static Supplier<String> getDisplayString(String key)
    {
        KeyBinding keybinding = KEYBIND_ARRAY.get(key);
        return keybinding == null ? () ->
        {
            return key;
        } : () ->
        {
            return keybinding.getDisplayName();
        };
    }

    static
    {
        CATEGORY_ORDER.put("key.categories.movement", Integer.valueOf(1));
        CATEGORY_ORDER.put("key.categories.gameplay", Integer.valueOf(2));
        CATEGORY_ORDER.put("key.categories.inventory", Integer.valueOf(3));
        CATEGORY_ORDER.put("key.categories.creative", Integer.valueOf(4));
        CATEGORY_ORDER.put("key.categories.multiplayer", Integer.valueOf(5));
        CATEGORY_ORDER.put("key.categories.ui", Integer.valueOf(6));
        CATEGORY_ORDER.put("key.categories.misc", Integer.valueOf(7));
    }
}