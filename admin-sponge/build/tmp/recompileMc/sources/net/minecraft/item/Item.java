package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.registry.RegistrySimple;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Item extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<Item>
{
    public static final RegistryNamespaced<ResourceLocation, Item> REGISTRY = net.minecraftforge.registries.GameData.getWrapper(Item.class);
    private static final Map<Block, Item> BLOCK_TO_ITEM = net.minecraftforge.registries.GameData.getBlockItemMap();
    private static final IItemPropertyGetter DAMAGED_GETTER = new IItemPropertyGetter()
    {
        @SideOnly(Side.CLIENT)
        public float func_185085_a(ItemStack p_185085_1_, @Nullable World p_185085_2_, @Nullable EntityLivingBase p_185085_3_)
        {
            return p_185085_1_.isDamaged() ? 1.0F : 0.0F;
        }
    };
    private static final IItemPropertyGetter DAMAGE_GETTER = new IItemPropertyGetter()
    {
        @SideOnly(Side.CLIENT)
        public float func_185085_a(ItemStack p_185085_1_, @Nullable World p_185085_2_, @Nullable EntityLivingBase p_185085_3_)
        {
            return MathHelper.clamp((float)p_185085_1_.getDamage() / (float)p_185085_1_.getMaxDamage(), 0.0F, 1.0F);
        }
    };
    private static final IItemPropertyGetter LEFTHANDED_GETTER = new IItemPropertyGetter()
    {
        @SideOnly(Side.CLIENT)
        public float func_185085_a(ItemStack p_185085_1_, @Nullable World p_185085_2_, @Nullable EntityLivingBase p_185085_3_)
        {
            return p_185085_3_ != null && p_185085_3_.getPrimaryHand() != EnumHandSide.RIGHT ? 1.0F : 0.0F;
        }
    };
    private static final IItemPropertyGetter COOLDOWN_GETTER = new IItemPropertyGetter()
    {
        @SideOnly(Side.CLIENT)
        public float func_185085_a(ItemStack p_185085_1_, @Nullable World p_185085_2_, @Nullable EntityLivingBase p_185085_3_)
        {
            return p_185085_3_ instanceof EntityPlayer ? ((EntityPlayer)p_185085_3_).getCooldownTracker().getCooldown(p_185085_1_.getItem(), 0.0F) : 0.0F;
        }
    };
    private final IRegistry<ResourceLocation, IItemPropertyGetter> properties = new RegistrySimple<ResourceLocation, IItemPropertyGetter>();
    protected static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    private CreativeTabs group;
    /** The RNG used by the Item subclasses. */
    protected static Random random = new Random();
    /** Maximum size of the stack. */
    protected int maxStackSize = 64;
    /** Maximum damage an item can handle. */
    private int maxDamage;
    protected boolean field_77789_bW;
    protected boolean field_77787_bX;
    private Item containerItem;
    /** The unlocalized name of this item. */
    private String translationKey;

    public static int getIdFromItem(Item itemIn)
    {
        return itemIn == null ? 0 : REGISTRY.getId(itemIn);
    }

    public static Item getItemById(int id)
    {
        return REGISTRY.get(id);
    }

    public static Item getItemFromBlock(Block blockIn)
    {
        Item item = BLOCK_TO_ITEM.get(blockIn);
        return item == null ? Items.AIR : item;
    }

    @Nullable
    public static Item func_111206_d(String p_111206_0_)
    {
        Item item = REGISTRY.get(new ResourceLocation(p_111206_0_));

        if (item == null)
        {
            try
            {
                return getItemById(Integer.parseInt(p_111206_0_));
            }
            catch (NumberFormatException var3)
            {
                ;
            }
        }

        return item;
    }

    /**
     * Creates a new override param for item models. See usage in clock, compass, elytra, etc.
     */
    public final void addPropertyOverride(ResourceLocation key, IItemPropertyGetter getter)
    {
        this.properties.put(key, getter);
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public IItemPropertyGetter getPropertyGetter(ResourceLocation key)
    {
        return this.properties.get(key);
    }

    /**
     * Called when an ItemStack with NBT data is read to potentially that ItemStack's NBT data
     */
    public boolean updateItemStackNBT(NBTTagCompound nbt)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasCustomProperties()
    {
        return !this.properties.getKeys().isEmpty();
    }

    public Item()
    {
        this.addPropertyOverride(new ResourceLocation("lefthanded"), LEFTHANDED_GETTER);
        this.addPropertyOverride(new ResourceLocation("cooldown"), COOLDOWN_GETTER);
    }

    public Item func_77625_d(int p_77625_1_)
    {
        this.maxStackSize = p_77625_1_;
        return this;
    }

    public EnumActionResult func_180614_a(EntityPlayer p_180614_1_, World p_180614_2_, BlockPos p_180614_3_, EnumHand p_180614_4_, EnumFacing p_180614_5_, float p_180614_6_, float p_180614_7_, float p_180614_8_)
    {
        return EnumActionResult.PASS;
    }

    public float getDestroySpeed(ItemStack stack, IBlockState state)
    {
        return 1.0F;
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        return stack;
    }

    /**
     * Returns the maximum size of the stack for a specific item.
     */
    @Deprecated // Use ItemStack sensitive version below.
    public int getMaxStackSize()
    {
        return this.maxStackSize;
    }

    public int func_77647_b(int p_77647_1_)
    {
        return 0;
    }

    public boolean func_77614_k()
    {
        return this.field_77787_bX;
    }

    public Item func_77627_a(boolean p_77627_1_)
    {
        this.field_77787_bX = p_77627_1_;
        return this;
    }

    /**
     * Returns the maximum damage an item can take.
     */
    @Deprecated
    public int getMaxDamage()
    {
        return this.maxDamage;
    }

    public Item func_77656_e(int p_77656_1_)
    {
        this.maxDamage = p_77656_1_;

        if (p_77656_1_ > 0)
        {
            this.addPropertyOverride(new ResourceLocation("damaged"), DAMAGED_GETTER);
            this.addPropertyOverride(new ResourceLocation("damage"), DAMAGE_GETTER);
        }

        return this;
    }

    public boolean isDamageable()
    {
        return this.maxDamage > 0 && (!this.field_77787_bX || this.maxStackSize == 1);
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        return false;
    }

    /**
     * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
     */
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving)
    {
        return false;
    }

    /**
     * Check whether this Item can harvest the given Block
     */
    public boolean canHarvestBlock(IBlockState blockIn)
    {
        return false;
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand)
    {
        return false;
    }

    public Item func_77664_n()
    {
        this.field_77789_bW = true;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public boolean func_77662_d()
    {
        return this.field_77789_bW;
    }

    @SideOnly(Side.CLIENT)
    public boolean func_77629_n_()
    {
        return false;
    }

    public Item func_77655_b(String p_77655_1_)
    {
        this.translationKey = p_77655_1_;
        return this;
    }

    public String func_77657_g(ItemStack p_77657_1_)
    {
        return I18n.func_74838_a(this.getTranslationKey(p_77657_1_));
    }

    /**
     * Returns the unlocalized name of this item.
     */
    public String getTranslationKey()
    {
        return "item." + this.translationKey;
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getTranslationKey(ItemStack stack)
    {
        return "item." + this.translationKey;
    }

    public Item func_77642_a(Item p_77642_1_)
    {
        this.containerItem = p_77642_1_;
        return this;
    }

    /**
     * If this function returns true (or the item is damageable), the ItemStack's NBT tag will be sent to the client.
     */
    public boolean shouldSyncTag()
    {
        return true;
    }

    @Nullable
    public Item getContainerItem()
    {
        return this.containerItem;
    }

    /**
     * True if this Item has a container item (a.k.a. crafting result)
     */
    @Deprecated // Use ItemStack sensitive version below.
    public boolean hasContainerItem()
    {
        return this.containerItem != null;
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
    }

    /**
     * Called when item is crafted/smelted. Used only by maps so far.
     */
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn)
    {
    }

    /**
     * Returns {@code} true if this is a complex item.
     */
    public boolean isComplex()
    {
        return false;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getUseAction(ItemStack stack)
    {
        return EnumAction.NONE;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getUseDuration(ItemStack stack)
    {
        return 0;
    }

    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
    {
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
    }

    public String func_77653_i(ItemStack p_77653_1_)
    {
        return I18n.func_74838_a(this.func_77657_g(p_77653_1_) + ".name").trim();
    }

    /**
     * Returns true if this item has an enchantment glint. By default, this returns
     * <code>stack.isItemEnchanted()</code>, but other items can override it (for instance, written books always return
     * true).
     *  
     * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
     * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
     */
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return stack.isEnchanted();
    }

    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity(ItemStack stack)
    {
        return stack.isEnchanted() ? EnumRarity.RARE : EnumRarity.COMMON;
    }

    /**
     * Checks isDamagable and if it cannot be stacked
     */
    public boolean isEnchantable(ItemStack stack)
    {
        return this.getItemStackLimit(stack) == 1 && this.isDamageable();
    }

    protected RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids)
    {
        float f = playerIn.rotationPitch;
        float f1 = playerIn.rotationYaw;
        double d0 = playerIn.posX;
        double d1 = playerIn.posY + (double)playerIn.getEyeHeight();
        double d2 = playerIn.posZ;
        Vec3d vec3d = new Vec3d(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = playerIn.getAttribute(EntityPlayer.REACH_DISTANCE).getValue();
        Vec3d vec3d1 = vec3d.add((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
        return worldIn.func_147447_a(vec3d, vec3d1, useLiquids, !useLiquids, false);
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability()
    {
        return 0;
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void fillItemGroup(CreativeTabs group, NonNullList<ItemStack> items)
    {
        if (this.isInGroup(group))
        {
            items.add(new ItemStack(this));
        }
    }

    protected boolean isInGroup(CreativeTabs group)
    {
        for (CreativeTabs tab : this.getCreativeTabs())
            if (tab == group)
                return true;
        CreativeTabs creativetabs = this.getGroup();
        return creativetabs != null && (group == CreativeTabs.SEARCH || group == creativetabs);
    }

    /**
     * gets the CreativeTab this item is displayed on
     */
    @Nullable
    public CreativeTabs getGroup()
    {
        return this.group;
    }

    public Item func_77637_a(CreativeTabs p_77637_1_)
    {
        this.group = p_77637_1_;
        return this;
    }

    public boolean func_82788_x()
    {
        return false;
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        return false;
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    @Deprecated // Use ItemStack sensitive version below.
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot)
    {
        return HashMultimap.<String, AttributeModifier>create();
    }

    /* ======================================== FORGE START =====================================*/
    /**
     * ItemStack sensitive version of getItemAttributeModifiers
     */
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
    {
        return this.getAttributeModifiers(slot);
    }

    /**
     * Called when a player drops the item into the world,
     * returning false from this will prevent the item from
     * being removed from the players inventory and spawning
     * in the world
     *
     * @param player The player that dropped the item
     * @param item The item stack, before the item is removed.
     */
    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player)
    {
        return true;
    }

    /**
     * Allow the item one last chance to modify its name used for the
     * tool highlight useful for adding something extra that can't be removed
     * by a user in the displayed name, such as a mode of operation.
     *
     * @param item the ItemStack for the item.
     * @param displayName the name that will be displayed unless it is changed in this method.
     */
    public String getHighlightTip( ItemStack item, String displayName )
    {
        return displayName;
    }

    /**
     * This is called when the item is used, before the block is activated.
     * @param stack The Item Stack
     * @param player The Player that used the item
     * @param world The Current World
     * @param pos Target position
     * @param side The side of the target hit
     * @param hand Which hand the item is being held in.
     * @return Return PASS to allow vanilla handling, any other to skip normal code.
     */
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        return EnumActionResult.PASS;
    }

    protected boolean canRepair = true;
    /**
     * Called by CraftingManager to determine if an item is reparable.
     * @return True if reparable
     */
    public boolean isRepairable()
    {
        return canRepair && isDamageable();
    }

    /**
     * Call to disable repair recipes.
     * @return The current Item instance
     */
    public Item setNoRepair()
    {
        canRepair = false;
        return this;
    }

    /**
     * Override this method to change the NBT data being sent to the client.
     * You should ONLY override this when you have no other choice, as this might change behavior client side!
     *
     * Note that this will sometimes be applied multiple times, the following MUST be supported:
     * Item item = stack.getItem();
     * NBTTagCompound nbtShare1 = item.getNBTShareTag(stack);
     * stack.setTagCompound(nbtShare1);
     * NBTTagCompound nbtShare2 = item.getNBTShareTag(stack);
     * assert nbtShare1.equals(nbtShare2);
     *
     * @param stack The stack to send the NBT tag for
     * @return The NBT tag
     */
    @Nullable
    public NBTTagCompound getNBTShareTag(ItemStack stack)
    {
        return stack.getTag();
    }

    /**
     * Override this method to decide what to do with the NBT data received from getNBTShareTag().
     * 
     * @param stack The stack that received NBT
     * @param nbt Received NBT, can be null
     */
    public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        stack.setTag(nbt);
    }

    /**
     * Called before a block is broken.  Return true to prevent default block harvesting.
     *
     * Note: In SMP, this is called on both client and server sides!
     *
     * @param itemstack The current ItemStack
     * @param pos Block's position in world
     * @param player The Player that is wielding the item
     * @return True to prevent harvesting, false to continue as normal
     */
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player)
    {
        return false;
    }

    /**
     * Called each tick while using an item.
     * @param stack The Item being used
     * @param player The Player using the item
     * @param count The amount of time in tick the item has been used for continuously
     */
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
    {
    }

    /**
     * Called when the player Left Clicks (attacks) an entity.
     * Processed before damage is done, if return value is true further processing is canceled
     * and the entity is not attacked.
     *
     * @param stack The Item being used
     * @param player The player that is attacking
     * @param entity The entity being attacked
     * @return True to cancel the rest of the interaction.
     */
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        return false;
    }

    /**
     * ItemStack sensitive version of getContainerItem.
     * Returns a full ItemStack instance of the result.
     *
     * @param itemStack The current ItemStack
     * @return The resulting ItemStack
     */
    public ItemStack getContainerItem(ItemStack itemStack)
    {
        if (!hasContainerItem(itemStack))
        {
            return ItemStack.EMPTY;
        }
        return new ItemStack(getContainerItem());
    }

    /**
     * ItemStack sensitive version of hasContainerItem
     * @param stack The current item stack
     * @return True if this item has a 'container'
     */
    public boolean hasContainerItem(ItemStack stack)
    {
        return hasContainerItem();
    }

    /**
     * Retrieves the normal 'lifespan' of this item when it is dropped on the ground as a EntityItem.
     * This is in ticks, standard result is 6000, or 5 mins.
     *
     * @param itemStack The current ItemStack
     * @param world The world the entity is in
     * @return The normal lifespan in ticks.
     */
    public int getEntityLifespan(ItemStack itemStack, World world)
    {
        return 6000;
    }

    /**
     * Determines if this Item has a special entity for when they are in the world.
     * Is called when a EntityItem is spawned in the world, if true and Item#createCustomEntity
     * returns non null, the EntityItem will be destroyed and the new Entity will be added to the world.
     *
     * @param stack The current item stack
     * @return True of the item has a custom entity, If true, Item#createCustomEntity will be called
     */
    public boolean hasCustomEntity(ItemStack stack)
    {
        return false;
    }

    /**
     * This function should return a new entity to replace the dropped item.
     * Returning null here will not kill the EntityItem and will leave it to function normally.
     * Called when the item it placed in a world.
     *
     * @param world The world object
     * @param location The EntityItem object, useful for getting the position of the entity
     * @param itemstack The current item stack
     * @return A new Entity object to spawn or null
     */
    @Nullable
    public Entity createEntity(World world, Entity location, ItemStack itemstack)
    {
        return null;
    }

    /**
     * Called by the default implemetation of EntityItem's onUpdate method, allowing for cleaner
     * control over the update of the item without having to write a subclass.
     *
     * @param entityItem The entity Item
     * @return Return true to skip any further update code.
     */
    public boolean onEntityItemUpdate(net.minecraft.entity.item.EntityItem entityItem)
    {
        return false;
    }

    /**
     * Gets a list of tabs that items belonging to this class can display on,
     * combined properly with getSubItems allows for a single item to span
     * many sub-items across many tabs.
     *
     * @return A list of all tabs that this item could possibly be one.
     */
    public CreativeTabs[] getCreativeTabs()
    {
        return new CreativeTabs[]{ getGroup() };
    }

    /**
     * Determines the base experience for a player when they remove this item from a furnace slot.
     * This number must be between 0 and 1 for it to be valid.
     * This number will be multiplied by the stack size to get the total experience.
     *
     * @param item The item stack the player is picking up.
     * @return The amount to award for each item.
     */
    public float getSmeltingExperience(ItemStack item)
    {
        return -1; //-1 will default to the old lookups.
    }

    /**
     *
     * Should this item, when held, allow sneak-clicks to pass through to the underlying block?
     *
     * @param world The world
     * @param pos Block position in world
     * @param player The Player that is wielding the item
     * @return
     */
    public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.IBlockAccess world, BlockPos pos, EntityPlayer player)
    {
        return false;
    }

    /**
     * Called to tick armor in the armor slot. Override to do something
     */
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack){}

    /**
     * Determines if the specific ItemStack can be placed in the specified armor slot, for the entity.
     *
     * TODO: Change name to canEquip in 1.13?
     *
     * @param stack The ItemStack
     * @param armorType Armor slot to be verified.
     * @param entity The entity trying to equip the armor
     * @return True if the given ItemStack can be inserted in the slot
     */
    public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity)
    {
        return net.minecraft.entity.EntityLiving.getSlotForItemStack(stack) == armorType;
    }

    /**
     * Override this to set a non-default armor slot for an ItemStack, but
     * <em>do not use this to get the armor slot of said stack; for that, use
     * {@link net.minecraft.entity.EntityLiving#getSlotForItemStack(ItemStack)}.</em>
     *
     * @param stack the ItemStack
     * @return the armor slot of the ItemStack, or {@code null} to let the default
     * vanilla logic as per {@code EntityLiving.getSlotForItemStack(stack)} decide
     */
    @Nullable
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack)
    {
        return null;
    }

    /**
     * Allow or forbid the specific book/item combination as an anvil enchant
     *
     * @param stack The item
     * @param book The book
     * @return if the enchantment is allowed
     */
    public boolean isBookEnchantable(ItemStack stack, ItemStack book)
    {
        return true;
    }

    /**
     * Called by RenderBiped and RenderPlayer to determine the armor texture that
     * should be use for the currently equipped item.
     * This will only be called on instances of ItemArmor.
     *
     * Returning null from this function will use the default value.
     *
     * @param stack ItemStack for the equipped armor
     * @param entity The entity wearing the armor
     * @param slot The slot the armor is in
     * @param type The subtype, can be null or "overlay"
     * @return Path of texture to bind, or null to use default
     */
    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
    {
        return null;
    }

    /**
     * Returns the font renderer used to render tooltips and overlays for this item.
     * Returning null will use the standard font renderer.
     *
     * @param stack The current item stack
     * @return A instance of FontRenderer or null to use default
     */
    @SideOnly(Side.CLIENT)
    @Nullable
    public net.minecraft.client.gui.FontRenderer getFontRenderer(ItemStack stack)
    {
        return null;
    }

    /**
     * Override this method to have an item handle its own armor rendering.
     *
     * @param  entityLiving  The entity wearing the armor
     * @param  itemStack  The itemStack to render the model of
     * @param  armorSlot  The slot the armor is in
     * @param _default Original armor model. Will have attributes set.
     * @return  A ModelBiped to render instead of the default
     */
    @SideOnly(Side.CLIENT)
    @Nullable
    public net.minecraft.client.model.ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, net.minecraft.client.model.ModelBiped _default)
    {
        return null;
    }

    /**
     * Called when a entity tries to play the 'swing' animation.
     *
     * @param entityLiving The entity swinging the item.
     * @param stack The Item stack
     * @return True to cancel any further processing by EntityLiving
     */
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        return false;
    }

    /**
     * Called when the client starts rendering the HUD, for whatever item the player currently has as a helmet.
     * This is where pumpkins would render there overlay.
     *
     * @param stack The ItemStack that is equipped
     * @param player Reference to the current client entity
     * @param resolution Resolution information about the current viewport and configured GUI Scale
     * @param partialTicks Partial ticks for the renderer, useful for interpolation
     */
    @SideOnly(Side.CLIENT)
    public void renderHelmetOverlay(ItemStack stack, EntityPlayer player, net.minecraft.client.gui.ScaledResolution resolution, float partialTicks){}

    /**
     * Return the itemDamage represented by this ItemStack. Defaults to the itemDamage field on ItemStack, but can be overridden here for other sources such as NBT.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    public int getDamage(ItemStack stack)
    {
        return stack.field_77991_e;
    }

    /**
     * This used to be 'display damage' but its really just 'aux' data in the ItemStack, usually shares the same variable as damage.
     * @param stack
     * @return
     */
    public int getMetadata(ItemStack stack)
    {
        return stack.field_77991_e;
    }

    /**
     * Determines if the durability bar should be rendered for this item.
     * Defaults to vanilla stack.isDamaged behavior.
     * But modders can use this for any data they wish.
     *
     * @param stack The current Item Stack
     * @return True if it should render the 'durability' bar.
     */
    public boolean showDurabilityBar(ItemStack stack)
    {
        return stack.isDamaged();
    }

    /**
     * Queries the percentage of the 'Durability' bar that should be drawn.
     *
     * @param stack The current ItemStack
     * @return 0.0 for 100% (no damage / full bar), 1.0 for 0% (fully damaged / empty bar)
     */
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return (double)stack.getDamage() / (double)stack.getMaxDamage();
    }

    /**
     * Returns the packed int RGB value used to render the durability bar in the GUI.
     * Defaults to a value based on the hue scaled based on {@link #getDurabilityForDisplay}, but can be overriden.
     *
     * @param stack Stack to get durability from
     * @return A packed RGB value for the durability colour (0x00RRGGBB)
     */
    public int getRGBDurabilityForDisplay(ItemStack stack)
    {
        return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
    }
    /**
     * Return the maxDamage for this ItemStack. Defaults to the maxDamage field in this item,
     * but can be overridden here for other sources such as NBT.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    public int getMaxDamage(ItemStack stack)
    {
        return getMaxDamage();
    }

    /**
     * Return if this itemstack is damaged. Note only called if {@link #isDamageable()} is true.
     * @param stack the stack
     * @return if the stack is damaged
     */
    public boolean isDamaged(ItemStack stack)
    {
        return stack.field_77991_e > 0;
    }

    /**
     * Set the damage for this itemstack. Note, this method is responsible for zero checking.
     * @param stack the stack
     * @param damage the new damage value
     */
    public void setDamage(ItemStack stack, int damage)
    {
        stack.field_77991_e = damage;

        if (stack.field_77991_e < 0)
        {
            stack.field_77991_e = 0;
        }
    }

    /**
     * Checked from {@link net.minecraft.client.multiplayer.PlayerControllerMP#onPlayerDestroyBlock(BlockPos pos) PlayerControllerMP.onPlayerDestroyBlock()}
     * when a creative player left-clicks a block with this item.
     * Also checked from {@link net.minecraftforge.common.ForgeHooks#onBlockBreakEvent(World, GameType, EntityPlayerMP, BlockPos)  ForgeHooks.onBlockBreakEvent()}
     * to prevent sending an event.
     * @return true if the given player can destroy specified block in creative mode with this item
     */
    public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player)
    {
        return !(this instanceof ItemSword);
    }

    /**
     * ItemStack sensitive version of {@link #canHarvestBlock(IBlockState)}
     * @param state The block trying to harvest
     * @param stack The itemstack used to harvest the block
     * @return true if can harvest the block
     */
    public boolean canHarvestBlock(IBlockState state, ItemStack stack)
    {
        return canHarvestBlock(state);
    }

    /**
     * Gets the maximum number of items that this stack should be able to hold.
     * This is a ItemStack (and thus NBT) sensitive version of Item.getItemStackLimit()
     *
     * @param stack The ItemStack
     * @return The maximum number this item can be stacked to
     */
    public int getItemStackLimit(ItemStack stack)
    {
        return this.getMaxStackSize();
    }

    private java.util.Map<String, Integer> toolClasses = new java.util.HashMap<String, Integer>();
    /**
     * Sets or removes the harvest level for the specified tool class.
     *
     * @param toolClass Class
     * @param level Harvest level:
     *     Wood:    0
     *     Stone:   1
     *     Iron:    2
     *     Diamond: 3
     *     Gold:    0
     */
    public void setHarvestLevel(String toolClass, int level)
    {
        if (level < 0)
            toolClasses.remove(toolClass);
        else
            toolClasses.put(toolClass, level);
    }

    public java.util.Set<String> getToolClasses(ItemStack stack)
    {
        return toolClasses.keySet();
    }

    /**
     * Queries the harvest level of this item stack for the specified tool class,
     * Returns -1 if this tool is not of the specified type
     *
     * @param stack This item stack instance
     * @param toolClass Tool Class
     * @param player The player trying to harvest the given blockstate
     * @param blockState The block to harvest
     * @return Harvest level, or -1 if not the specified tool type.
     */
    public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState)
    {
        Integer ret = toolClasses.get(toolClass);
        return ret == null ? -1 : ret;
    }

    /**
     * ItemStack sensitive version of getItemEnchantability
     *
     * @param stack The ItemStack
     * @return the item echantability value
     */
    public int getItemEnchantability(ItemStack stack)
    {
        return getItemEnchantability();
    }

    /**
     * Checks whether an item can be enchanted with a certain enchantment. This applies specifically to enchanting an item in the enchanting table and is called when retrieving the list of possible enchantments for an item.
     * Enchantments may additionally (or exclusively) be doing their own checks in {@link net.minecraft.enchantment.Enchantment#canApplyAtEnchantingTable(ItemStack)}; check the individual implementation for reference.
     * By default this will check if the enchantment type is valid for this item type.
     * @param stack the item stack to be enchanted
     * @param enchantment the enchantment to be applied
     * @return true if the enchantment can be applied to this item
     */
    public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment)
    {
        return enchantment.type.canEnchantItem(stack.getItem());
    }

    /**
     * Whether this Item can be used as a payment to activate the vanilla beacon.
     * @param stack the ItemStack
     * @return true if this Item can be used
     */
    public boolean isBeaconPayment(ItemStack stack)
    {
        return this == Items.EMERALD || this == Items.DIAMOND || this == Items.GOLD_INGOT || this == Items.IRON_INGOT;
    }

    /**
     * Determine if the player switching between these two item stacks
     * @param oldStack The old stack that was equipped
     * @param newStack The new stack
     * @param slotChanged If the current equipped slot was changed,
     *                    Vanilla does not play the animation if you switch between two
     *                    slots that hold the exact same item.
     * @return True to play the item change animation
     */
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return !oldStack.equals(newStack); //!ItemStack.areItemStacksEqual(oldStack, newStack);
    }

    /**
     * Called when the player is mining a block and the item in his hand changes.
     * Allows to not reset blockbreaking if only NBT or similar changes.
     * @param oldStack The old stack that was used for mining. Item in players main hand
     * @param newStack The new stack
     * @return True to reset block break progress
     */
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack)
    {
        return !(newStack.getItem() == oldStack.getItem() && ItemStack.areItemStackTagsEqual(newStack, oldStack) && (newStack.isDamageable() || newStack.func_77960_j() == oldStack.func_77960_j()));
    }

    /**
     * Called to get the Mod ID of the mod that *created* the ItemStack,
     * instead of the real Mod ID that *registered* it.
     *
     * For example the Forge Universal Bucket creates a subitem for each modded fluid,
     * and it returns the modded fluid's Mod ID here.
     *
     * Mods that register subitems for other mods can override this.
     * Informational mods can call it to show the mod that created the item.
     *
     * @param itemStack the ItemStack to check
     * @return the Mod ID for the ItemStack, or
     *         null when there is no specially associated mod and {@link #getRegistryName()} would return null.
     */
    @Nullable
    public String getCreatorModId(ItemStack itemStack)
    {
        return net.minecraftforge.common.ForgeHooks.getDefaultCreatorModId(itemStack);
    }

    /**
     * Called from ItemStack.setItem, will hold extra data for the life of this ItemStack.
     * Can be retrieved from stack.getCapabilities()
     * The NBT can be null if this is not called from readNBT or if the item the stack is
     * changing FROM is different then this item, or the previous item had no capabilities.
     *
     * This is called BEFORE the stacks item is set so you can use stack.getItem() to see the OLD item.
     * Remember that getItem CAN return null.
     *
     * @param stack The ItemStack
     * @param nbt NBT of this item serialized, or null.
     * @return A holder instance associated with this ItemStack where you can hold capabilities for the life of this item.
     */
    @Nullable
    public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return null;
    }

    public com.google.common.collect.ImmutableMap<String, net.minecraftforge.common.animation.ITimeValue> getAnimationParameters(final ItemStack stack, final World world, final EntityLivingBase entity)
    {
        com.google.common.collect.ImmutableMap.Builder<String, net.minecraftforge.common.animation.ITimeValue> builder = com.google.common.collect.ImmutableMap.builder();
        for(ResourceLocation location : properties.getKeys())
        {
            final IItemPropertyGetter parameter = properties.get(location);
            builder.put(location.toString(), new net.minecraftforge.common.animation.ITimeValue()
            {
                public float apply(float input)
                {
                    return parameter.func_185085_a(stack, world, entity);
                }
            });
        }
        return builder.build();
    }

    /**
     * Can this Item disable a shield
     * @param stack The ItemStack
     * @param shield The shield in question
     * @param entity The EntityLivingBase holding the shield
     * @param attacker The EntityLivingBase holding the ItemStack
     * @retrun True if this ItemStack can disable the shield in question.
     */
    public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker)
    {
        return this instanceof ItemAxe;
    }

    /**
     * Is this Item a shield
     * @param stack The ItemStack
     * @param entity The Entity holding the ItemStack
     * @return True if the ItemStack is considered a shield
     */
    public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity)
    {
        return stack.getItem() == Items.SHIELD;
    }

    /**
     * @return the fuel burn time for this itemStack in a furnace.
     * Return 0 to make it not act as a fuel.
     * Return -1 to let the default vanilla logic decide.
     */
    public int getItemBurnTime(ItemStack itemStack)
    {
        return -1;
    }
    
    /** 
     * Returns an enum constant of type {@code HorseArmorType}.
     * The returned enum constant will be used to determine the armor value and texture of this item when equipped.
     * @param stack the armor stack
     * @return an enum constant of type {@code HorseArmorType}. Return HorseArmorType.NONE if this is not horse armor
     */
    public net.minecraft.entity.passive.HorseArmorType getHorseArmorType(ItemStack stack)
    {
        return net.minecraft.entity.passive.HorseArmorType.getByItem(stack.getItem());
    }
    
    public String getHorseArmorTexture(net.minecraft.entity.EntityLiving wearer, ItemStack stack)
    {
        return getHorseArmorType(stack).getTextureName();
    }
    
    /**
     * Called every tick from {@link EntityHorse#onUpdate()} on the item in the armor slot.
     * @param world the world the horse is in
     * @param horse the horse wearing this armor
     * @param armor the armor itemstack
     */
    public void onHorseArmorTick(World world, net.minecraft.entity.EntityLiving horse, ItemStack armor) {}
    
    @SideOnly(Side.CLIENT)
    @Nullable
    private net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer teisr;
    
    /**
     * @return This Item's renderer, or the default instance if it does not have one.
     */
    @SideOnly(Side.CLIENT)
    public final net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer getTileEntityItemStackRenderer()
    {
    	return teisr != null ? teisr : net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer.instance;
    }
    
    @SideOnly(Side.CLIENT)
    public void setTileEntityItemStackRenderer(@Nullable net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer teisr)
    {
    	this.teisr = teisr;
    }  

    /* ======================================== FORGE END   =====================================*/

    public static void registerItems()
    {
        register(Blocks.AIR, new ItemAir(Blocks.AIR));
        register(Blocks.STONE, (new ItemMultiTexture(Blocks.STONE, Blocks.STONE, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return BlockStone.EnumType.func_176643_a(p_apply_1_.func_77960_j()).func_176644_c();
            }
        })).func_77655_b("stone"));
        register(Blocks.GRASS, new ItemColored(Blocks.GRASS, false));
        register(Blocks.DIRT, (new ItemMultiTexture(Blocks.DIRT, Blocks.DIRT, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return BlockDirt.DirtType.func_176924_a(p_apply_1_.func_77960_j()).func_176927_c();
            }
        })).func_77655_b("dirt"));
        register(Blocks.COBBLESTONE);
        register(Blocks.field_150344_f, (new ItemMultiTexture(Blocks.field_150344_f, Blocks.field_150344_f, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return BlockPlanks.EnumType.func_176837_a(p_apply_1_.func_77960_j()).func_176840_c();
            }
        })).func_77655_b("wood"));
        register(Blocks.field_150345_g, (new ItemMultiTexture(Blocks.field_150345_g, Blocks.field_150345_g, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return BlockPlanks.EnumType.func_176837_a(p_apply_1_.func_77960_j()).func_176840_c();
            }
        })).func_77655_b("sapling"));
        register(Blocks.BEDROCK);
        register(Blocks.SAND, (new ItemMultiTexture(Blocks.SAND, Blocks.SAND, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return BlockSand.EnumType.func_176686_a(p_apply_1_.func_77960_j()).func_176685_d();
            }
        })).func_77655_b("sand"));
        register(Blocks.GRAVEL);
        register(Blocks.GOLD_ORE);
        register(Blocks.IRON_ORE);
        register(Blocks.COAL_ORE);
        register(Blocks.field_150364_r, (new ItemMultiTexture(Blocks.field_150364_r, Blocks.field_150364_r, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return BlockPlanks.EnumType.func_176837_a(p_apply_1_.func_77960_j()).func_176840_c();
            }
        })).func_77655_b("log"));
        register(Blocks.field_150363_s, (new ItemMultiTexture(Blocks.field_150363_s, Blocks.field_150363_s, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return BlockPlanks.EnumType.func_176837_a(p_apply_1_.func_77960_j() + 4).func_176840_c();
            }
        })).func_77655_b("log"));
        register(Blocks.field_150362_t, (new ItemLeaves(Blocks.field_150362_t)).func_77655_b("leaves"));
        register(Blocks.field_150361_u, (new ItemLeaves(Blocks.field_150361_u)).func_77655_b("leaves"));
        register(Blocks.SPONGE, (new ItemMultiTexture(Blocks.SPONGE, Blocks.SPONGE, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return (p_apply_1_.func_77960_j() & 1) == 1 ? "wet" : "dry";
            }
        })).func_77655_b("sponge"));
        register(Blocks.GLASS);
        register(Blocks.LAPIS_ORE);
        register(Blocks.LAPIS_BLOCK);
        register(Blocks.DISPENSER);
        register(Blocks.SANDSTONE, (new ItemMultiTexture(Blocks.SANDSTONE, Blocks.SANDSTONE, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return BlockSandStone.EnumType.func_176673_a(p_apply_1_.func_77960_j()).func_176676_c();
            }
        })).func_77655_b("sandStone"));
        register(Blocks.field_150323_B);
        register(Blocks.field_150318_D);
        register(Blocks.DETECTOR_RAIL);
        register(Blocks.STICKY_PISTON, new ItemPiston(Blocks.STICKY_PISTON));
        register(Blocks.field_150321_G);
        register(Blocks.field_150329_H, (new ItemColored(Blocks.field_150329_H, true)).func_150943_a(new String[] {"shrub", "grass", "fern"}));
        register(Blocks.field_150330_I);
        register(Blocks.PISTON, new ItemPiston(Blocks.PISTON));
        register(Blocks.field_150325_L, (new ItemCloth(Blocks.field_150325_L)).func_77655_b("cloth"));
        register(Blocks.field_150327_N, (new ItemMultiTexture(Blocks.field_150327_N, Blocks.field_150327_N, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return BlockFlower.EnumFlowerType.func_176967_a(BlockFlower.EnumFlowerColor.YELLOW, p_apply_1_.func_77960_j()).func_176963_d();
            }
        })).func_77655_b("flower"));
        register(Blocks.field_150328_O, (new ItemMultiTexture(Blocks.field_150328_O, Blocks.field_150328_O, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return BlockFlower.EnumFlowerType.func_176967_a(BlockFlower.EnumFlowerColor.RED, p_apply_1_.func_77960_j()).func_176963_d();
            }
        })).func_77655_b("rose"));
        register(Blocks.BROWN_MUSHROOM);
        register(Blocks.RED_MUSHROOM);
        register(Blocks.GOLD_BLOCK);
        register(Blocks.IRON_BLOCK);
        register(Blocks.STONE_SLAB, (new ItemSlab(Blocks.STONE_SLAB, Blocks.STONE_SLAB, Blocks.field_150334_T)).func_77655_b("stoneSlab"));
        register(Blocks.field_150336_V);
        register(Blocks.TNT);
        register(Blocks.BOOKSHELF);
        register(Blocks.MOSSY_COBBLESTONE);
        register(Blocks.OBSIDIAN);
        register(Blocks.TORCH);
        register(Blocks.END_ROD);
        register(Blocks.CHORUS_PLANT);
        register(Blocks.CHORUS_FLOWER);
        register(Blocks.PURPUR_BLOCK);
        register(Blocks.PURPUR_PILLAR);
        register(Blocks.PURPUR_STAIRS);
        register(Blocks.PURPUR_SLAB, (new ItemSlab(Blocks.PURPUR_SLAB, Blocks.PURPUR_SLAB, Blocks.field_185770_cW)).func_77655_b("purpurSlab"));
        register(Blocks.SPAWNER);
        register(Blocks.OAK_STAIRS);
        register(Blocks.CHEST);
        register(Blocks.DIAMOND_ORE);
        register(Blocks.DIAMOND_BLOCK);
        register(Blocks.CRAFTING_TABLE);
        register(Blocks.FARMLAND);
        register(Blocks.FURNACE);
        register(Blocks.LADDER);
        register(Blocks.RAIL);
        register(Blocks.field_150446_ar);
        register(Blocks.LEVER);
        register(Blocks.STONE_PRESSURE_PLATE);
        register(Blocks.field_150452_aw);
        register(Blocks.REDSTONE_ORE);
        register(Blocks.REDSTONE_TORCH);
        register(Blocks.STONE_BUTTON);
        register(Blocks.field_150431_aC, new ItemSnow(Blocks.field_150431_aC));
        register(Blocks.ICE);
        register(Blocks.SNOW);
        register(Blocks.CACTUS);
        register(Blocks.CLAY);
        register(Blocks.JUKEBOX);
        register(Blocks.OAK_FENCE);
        register(Blocks.SPRUCE_FENCE);
        register(Blocks.BIRCH_FENCE);
        register(Blocks.JUNGLE_FENCE);
        register(Blocks.DARK_OAK_FENCE);
        register(Blocks.ACACIA_FENCE);
        register(Blocks.PUMPKIN);
        register(Blocks.NETHERRACK);
        register(Blocks.SOUL_SAND);
        register(Blocks.GLOWSTONE);
        register(Blocks.field_150428_aP);
        register(Blocks.field_150415_aT);
        register(Blocks.field_150418_aU, (new ItemMultiTexture(Blocks.field_150418_aU, Blocks.field_150418_aU, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return BlockSilverfish.EnumType.func_176879_a(p_apply_1_.func_77960_j()).func_176882_c();
            }
        })).func_77655_b("monsterStoneEgg"));
        register(Blocks.field_150417_aV, (new ItemMultiTexture(Blocks.field_150417_aV, Blocks.field_150417_aV, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return BlockStoneBrick.EnumType.func_176613_a(p_apply_1_.func_77960_j()).func_176614_c();
            }
        })).func_77655_b("stonebricksmooth"));
        register(Blocks.BROWN_MUSHROOM_BLOCK);
        register(Blocks.RED_MUSHROOM_BLOCK);
        register(Blocks.IRON_BARS);
        register(Blocks.GLASS_PANE);
        register(Blocks.MELON);
        register(Blocks.VINE, new ItemColored(Blocks.VINE, false));
        register(Blocks.OAK_FENCE_GATE);
        register(Blocks.SPRUCE_FENCE_GATE);
        register(Blocks.BIRCH_FENCE_GATE);
        register(Blocks.JUNGLE_FENCE_GATE);
        register(Blocks.DARK_OAK_FENCE_GATE);
        register(Blocks.ACACIA_FENCE_GATE);
        register(Blocks.BRICK_STAIRS);
        register(Blocks.STONE_BRICK_STAIRS);
        register(Blocks.MYCELIUM);
        register(Blocks.field_150392_bi, new ItemLilyPad(Blocks.field_150392_bi));
        register(Blocks.field_150385_bj);
        register(Blocks.NETHER_BRICK_FENCE);
        register(Blocks.NETHER_BRICK_STAIRS);
        register(Blocks.ENCHANTING_TABLE);
        register(Blocks.END_PORTAL_FRAME);
        register(Blocks.END_STONE);
        register(Blocks.field_185772_cY);
        register(Blocks.DRAGON_EGG);
        register(Blocks.REDSTONE_LAMP);
        register(Blocks.field_150376_bx, (new ItemSlab(Blocks.field_150376_bx, Blocks.field_150376_bx, Blocks.field_150373_bw)).func_77655_b("woodSlab"));
        register(Blocks.SANDSTONE_STAIRS);
        register(Blocks.EMERALD_ORE);
        register(Blocks.ENDER_CHEST);
        register(Blocks.TRIPWIRE_HOOK);
        register(Blocks.EMERALD_BLOCK);
        register(Blocks.SPRUCE_STAIRS);
        register(Blocks.BIRCH_STAIRS);
        register(Blocks.JUNGLE_STAIRS);
        register(Blocks.COMMAND_BLOCK);
        register(Blocks.BEACON);
        register(Blocks.COBBLESTONE_WALL, (new ItemMultiTexture(Blocks.COBBLESTONE_WALL, Blocks.COBBLESTONE_WALL, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return BlockWall.EnumType.func_176660_a(p_apply_1_.func_77960_j()).func_176659_c();
            }
        })).func_77655_b("cobbleWall"));
        register(Blocks.field_150471_bO);
        register(Blocks.ANVIL, (new ItemAnvilBlock(Blocks.ANVIL)).func_77655_b("anvil"));
        register(Blocks.TRAPPED_CHEST);
        register(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
        register(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        register(Blocks.DAYLIGHT_DETECTOR);
        register(Blocks.REDSTONE_BLOCK);
        register(Blocks.field_150449_bY);
        register(Blocks.HOPPER);
        register(Blocks.QUARTZ_BLOCK, (new ItemMultiTexture(Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, new String[] {"default", "chiseled", "lines"})).func_77655_b("quartzBlock"));
        register(Blocks.QUARTZ_STAIRS);
        register(Blocks.ACTIVATOR_RAIL);
        register(Blocks.DROPPER);
        register(Blocks.field_150406_ce, (new ItemCloth(Blocks.field_150406_ce)).func_77655_b("clayHardenedStained"));
        register(Blocks.BARRIER);
        register(Blocks.IRON_TRAPDOOR);
        register(Blocks.HAY_BLOCK);
        register(Blocks.field_150404_cg, (new ItemCloth(Blocks.field_150404_cg)).func_77655_b("woolCarpet"));
        register(Blocks.TERRACOTTA);
        register(Blocks.COAL_BLOCK);
        register(Blocks.PACKED_ICE);
        register(Blocks.ACACIA_STAIRS);
        register(Blocks.DARK_OAK_STAIRS);
        register(Blocks.SLIME_BLOCK);
        register(Blocks.GRASS_PATH);
        register(Blocks.field_150398_cm, (new ItemMultiTexture(Blocks.field_150398_cm, Blocks.field_150398_cm, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return BlockDoublePlant.EnumPlantType.func_176938_a(p_apply_1_.func_77960_j()).func_176939_c();
            }
        })).func_77655_b("doublePlant"));
        register(Blocks.field_150399_cn, (new ItemCloth(Blocks.field_150399_cn)).func_77655_b("stainedGlass"));
        register(Blocks.field_150397_co, (new ItemCloth(Blocks.field_150397_co)).func_77655_b("stainedGlassPane"));
        register(Blocks.PRISMARINE, (new ItemMultiTexture(Blocks.PRISMARINE, Blocks.PRISMARINE, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return BlockPrismarine.EnumType.func_176810_a(p_apply_1_.func_77960_j()).func_176809_c();
            }
        })).func_77655_b("prismarine"));
        register(Blocks.SEA_LANTERN);
        register(Blocks.RED_SANDSTONE, (new ItemMultiTexture(Blocks.RED_SANDSTONE, Blocks.RED_SANDSTONE, new ItemMultiTexture.Mapper()
        {
            public String apply(ItemStack p_apply_1_)
            {
                return BlockRedSandstone.EnumType.func_176825_a(p_apply_1_.func_77960_j()).func_176828_c();
            }
        })).func_77655_b("redSandStone"));
        register(Blocks.RED_SANDSTONE_STAIRS);
        register(Blocks.field_180389_cP, (new ItemSlab(Blocks.field_180389_cP, Blocks.field_180389_cP, Blocks.field_180388_cO)).func_77655_b("stoneSlab2"));
        register(Blocks.REPEATING_COMMAND_BLOCK);
        register(Blocks.CHAIN_COMMAND_BLOCK);
        register(Blocks.field_189877_df);
        register(Blocks.NETHER_WART_BLOCK);
        register(Blocks.field_189879_dh);
        register(Blocks.BONE_BLOCK);
        register(Blocks.STRUCTURE_VOID);
        register(Blocks.OBSERVER);
        register(Blocks.WHITE_SHULKER_BOX, new ItemShulkerBox(Blocks.WHITE_SHULKER_BOX));
        register(Blocks.ORANGE_SHULKER_BOX, new ItemShulkerBox(Blocks.ORANGE_SHULKER_BOX));
        register(Blocks.MAGENTA_SHULKER_BOX, new ItemShulkerBox(Blocks.MAGENTA_SHULKER_BOX));
        register(Blocks.LIGHT_BLUE_SHULKER_BOX, new ItemShulkerBox(Blocks.LIGHT_BLUE_SHULKER_BOX));
        register(Blocks.YELLOW_SHULKER_BOX, new ItemShulkerBox(Blocks.YELLOW_SHULKER_BOX));
        register(Blocks.LIME_SHULKER_BOX, new ItemShulkerBox(Blocks.LIME_SHULKER_BOX));
        register(Blocks.PINK_SHULKER_BOX, new ItemShulkerBox(Blocks.PINK_SHULKER_BOX));
        register(Blocks.GRAY_SHULKER_BOX, new ItemShulkerBox(Blocks.GRAY_SHULKER_BOX));
        register(Blocks.field_190985_dt, new ItemShulkerBox(Blocks.field_190985_dt));
        register(Blocks.CYAN_SHULKER_BOX, new ItemShulkerBox(Blocks.CYAN_SHULKER_BOX));
        register(Blocks.PURPLE_SHULKER_BOX, new ItemShulkerBox(Blocks.PURPLE_SHULKER_BOX));
        register(Blocks.BLUE_SHULKER_BOX, new ItemShulkerBox(Blocks.BLUE_SHULKER_BOX));
        register(Blocks.BROWN_SHULKER_BOX, new ItemShulkerBox(Blocks.BROWN_SHULKER_BOX));
        register(Blocks.GREEN_SHULKER_BOX, new ItemShulkerBox(Blocks.GREEN_SHULKER_BOX));
        register(Blocks.RED_SHULKER_BOX, new ItemShulkerBox(Blocks.RED_SHULKER_BOX));
        register(Blocks.BLACK_SHULKER_BOX, new ItemShulkerBox(Blocks.BLACK_SHULKER_BOX));
        register(Blocks.WHITE_GLAZED_TERRACOTTA);
        register(Blocks.ORANGE_GLAZED_TERRACOTTA);
        register(Blocks.MAGENTA_GLAZED_TERRACOTTA);
        register(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
        register(Blocks.YELLOW_GLAZED_TERRACOTTA);
        register(Blocks.LIME_GLAZED_TERRACOTTA);
        register(Blocks.PINK_GLAZED_TERRACOTTA);
        register(Blocks.GRAY_GLAZED_TERRACOTTA);
        register(Blocks.field_192435_dJ);
        register(Blocks.CYAN_GLAZED_TERRACOTTA);
        register(Blocks.PURPLE_GLAZED_TERRACOTTA);
        register(Blocks.BLUE_GLAZED_TERRACOTTA);
        register(Blocks.BROWN_GLAZED_TERRACOTTA);
        register(Blocks.GREEN_GLAZED_TERRACOTTA);
        register(Blocks.RED_GLAZED_TERRACOTTA);
        register(Blocks.BLACK_GLAZED_TERRACOTTA);
        register(Blocks.field_192443_dR, (new ItemCloth(Blocks.field_192443_dR)).func_77655_b("concrete"));
        register(Blocks.field_192444_dS, (new ItemCloth(Blocks.field_192444_dS)).func_77655_b("concrete_powder"));
        register(Blocks.STRUCTURE_BLOCK);
        func_179217_a(256, "iron_shovel", (new ItemSpade(Item.ToolMaterial.IRON)).func_77655_b("shovelIron"));
        func_179217_a(257, "iron_pickaxe", (new ItemPickaxe(Item.ToolMaterial.IRON)).func_77655_b("pickaxeIron"));
        func_179217_a(258, "iron_axe", (new ItemAxe(Item.ToolMaterial.IRON)).func_77655_b("hatchetIron"));
        func_179217_a(259, "flint_and_steel", (new ItemFlintAndSteel()).func_77655_b("flintAndSteel"));
        func_179217_a(260, "apple", (new ItemFood(4, 0.3F, false)).func_77655_b("apple"));
        func_179217_a(261, "bow", (new ItemBow()).func_77655_b("bow"));
        func_179217_a(262, "arrow", (new ItemArrow()).func_77655_b("arrow"));
        func_179217_a(263, "coal", (new ItemCoal()).func_77655_b("coal"));
        func_179217_a(264, "diamond", (new Item()).func_77655_b("diamond").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(265, "iron_ingot", (new Item()).func_77655_b("ingotIron").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(266, "gold_ingot", (new Item()).func_77655_b("ingotGold").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(267, "iron_sword", (new ItemSword(Item.ToolMaterial.IRON)).func_77655_b("swordIron"));
        func_179217_a(268, "wooden_sword", (new ItemSword(Item.ToolMaterial.WOOD)).func_77655_b("swordWood"));
        func_179217_a(269, "wooden_shovel", (new ItemSpade(Item.ToolMaterial.WOOD)).func_77655_b("shovelWood"));
        func_179217_a(270, "wooden_pickaxe", (new ItemPickaxe(Item.ToolMaterial.WOOD)).func_77655_b("pickaxeWood"));
        func_179217_a(271, "wooden_axe", (new ItemAxe(Item.ToolMaterial.WOOD)).func_77655_b("hatchetWood"));
        func_179217_a(272, "stone_sword", (new ItemSword(Item.ToolMaterial.STONE)).func_77655_b("swordStone"));
        func_179217_a(273, "stone_shovel", (new ItemSpade(Item.ToolMaterial.STONE)).func_77655_b("shovelStone"));
        func_179217_a(274, "stone_pickaxe", (new ItemPickaxe(Item.ToolMaterial.STONE)).func_77655_b("pickaxeStone"));
        func_179217_a(275, "stone_axe", (new ItemAxe(Item.ToolMaterial.STONE)).func_77655_b("hatchetStone"));
        func_179217_a(276, "diamond_sword", (new ItemSword(Item.ToolMaterial.DIAMOND)).func_77655_b("swordDiamond"));
        func_179217_a(277, "diamond_shovel", (new ItemSpade(Item.ToolMaterial.DIAMOND)).func_77655_b("shovelDiamond"));
        func_179217_a(278, "diamond_pickaxe", (new ItemPickaxe(Item.ToolMaterial.DIAMOND)).func_77655_b("pickaxeDiamond"));
        func_179217_a(279, "diamond_axe", (new ItemAxe(Item.ToolMaterial.DIAMOND)).func_77655_b("hatchetDiamond"));
        func_179217_a(280, "stick", (new Item()).func_77664_n().func_77655_b("stick").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(281, "bowl", (new Item()).func_77655_b("bowl").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(282, "mushroom_stew", (new ItemSoup(6)).func_77655_b("mushroomStew"));
        func_179217_a(283, "golden_sword", (new ItemSword(Item.ToolMaterial.GOLD)).func_77655_b("swordGold"));
        func_179217_a(284, "golden_shovel", (new ItemSpade(Item.ToolMaterial.GOLD)).func_77655_b("shovelGold"));
        func_179217_a(285, "golden_pickaxe", (new ItemPickaxe(Item.ToolMaterial.GOLD)).func_77655_b("pickaxeGold"));
        func_179217_a(286, "golden_axe", (new ItemAxe(Item.ToolMaterial.GOLD)).func_77655_b("hatchetGold"));
        func_179217_a(287, "string", (new ItemBlockSpecial(Blocks.TRIPWIRE)).func_77655_b("string").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(288, "feather", (new Item()).func_77655_b("feather").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(289, "gunpowder", (new Item()).func_77655_b("sulphur").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(290, "wooden_hoe", (new ItemHoe(Item.ToolMaterial.WOOD)).func_77655_b("hoeWood"));
        func_179217_a(291, "stone_hoe", (new ItemHoe(Item.ToolMaterial.STONE)).func_77655_b("hoeStone"));
        func_179217_a(292, "iron_hoe", (new ItemHoe(Item.ToolMaterial.IRON)).func_77655_b("hoeIron"));
        func_179217_a(293, "diamond_hoe", (new ItemHoe(Item.ToolMaterial.DIAMOND)).func_77655_b("hoeDiamond"));
        func_179217_a(294, "golden_hoe", (new ItemHoe(Item.ToolMaterial.GOLD)).func_77655_b("hoeGold"));
        func_179217_a(295, "wheat_seeds", (new ItemSeeds(Blocks.WHEAT, Blocks.FARMLAND)).func_77655_b("seeds"));
        func_179217_a(296, "wheat", (new Item()).func_77655_b("wheat").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(297, "bread", (new ItemFood(5, 0.6F, false)).func_77655_b("bread"));
        func_179217_a(298, "leather_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.HEAD)).func_77655_b("helmetCloth"));
        func_179217_a(299, "leather_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.CHEST)).func_77655_b("chestplateCloth"));
        func_179217_a(300, "leather_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.LEGS)).func_77655_b("leggingsCloth"));
        func_179217_a(301, "leather_boots", (new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.FEET)).func_77655_b("bootsCloth"));
        func_179217_a(302, "chainmail_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, EntityEquipmentSlot.HEAD)).func_77655_b("helmetChain"));
        func_179217_a(303, "chainmail_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, EntityEquipmentSlot.CHEST)).func_77655_b("chestplateChain"));
        func_179217_a(304, "chainmail_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, EntityEquipmentSlot.LEGS)).func_77655_b("leggingsChain"));
        func_179217_a(305, "chainmail_boots", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, EntityEquipmentSlot.FEET)).func_77655_b("bootsChain"));
        func_179217_a(306, "iron_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, EntityEquipmentSlot.HEAD)).func_77655_b("helmetIron"));
        func_179217_a(307, "iron_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, EntityEquipmentSlot.CHEST)).func_77655_b("chestplateIron"));
        func_179217_a(308, "iron_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, EntityEquipmentSlot.LEGS)).func_77655_b("leggingsIron"));
        func_179217_a(309, "iron_boots", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, EntityEquipmentSlot.FEET)).func_77655_b("bootsIron"));
        func_179217_a(310, "diamond_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, EntityEquipmentSlot.HEAD)).func_77655_b("helmetDiamond"));
        func_179217_a(311, "diamond_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, EntityEquipmentSlot.CHEST)).func_77655_b("chestplateDiamond"));
        func_179217_a(312, "diamond_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, EntityEquipmentSlot.LEGS)).func_77655_b("leggingsDiamond"));
        func_179217_a(313, "diamond_boots", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, EntityEquipmentSlot.FEET)).func_77655_b("bootsDiamond"));
        func_179217_a(314, "golden_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, EntityEquipmentSlot.HEAD)).func_77655_b("helmetGold"));
        func_179217_a(315, "golden_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, EntityEquipmentSlot.CHEST)).func_77655_b("chestplateGold"));
        func_179217_a(316, "golden_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, EntityEquipmentSlot.LEGS)).func_77655_b("leggingsGold"));
        func_179217_a(317, "golden_boots", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, EntityEquipmentSlot.FEET)).func_77655_b("bootsGold"));
        func_179217_a(318, "flint", (new Item()).func_77655_b("flint").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(319, "porkchop", (new ItemFood(3, 0.3F, true)).func_77655_b("porkchopRaw"));
        func_179217_a(320, "cooked_porkchop", (new ItemFood(8, 0.8F, true)).func_77655_b("porkchopCooked"));
        func_179217_a(321, "painting", (new ItemHangingEntity(EntityPainting.class)).func_77655_b("painting"));
        func_179217_a(322, "golden_apple", (new ItemAppleGold(4, 1.2F, false)).setAlwaysEdible().func_77655_b("appleGold"));
        func_179217_a(323, "sign", (new ItemSign()).func_77655_b("sign"));
        func_179217_a(324, "wooden_door", (new ItemDoor(Blocks.OAK_DOOR)).func_77655_b("doorOak"));
        Item item = (new ItemBucket(Blocks.AIR)).func_77655_b("bucket").func_77625_d(16);
        func_179217_a(325, "bucket", item);
        func_179217_a(326, "water_bucket", (new ItemBucket(Blocks.field_150358_i)).func_77655_b("bucketWater").func_77642_a(item));
        func_179217_a(327, "lava_bucket", (new ItemBucket(Blocks.field_150356_k)).func_77655_b("bucketLava").func_77642_a(item));
        func_179217_a(328, "minecart", (new ItemMinecart(EntityMinecart.Type.RIDEABLE)).func_77655_b("minecart"));
        func_179217_a(329, "saddle", (new ItemSaddle()).func_77655_b("saddle"));
        func_179217_a(330, "iron_door", (new ItemDoor(Blocks.IRON_DOOR)).func_77655_b("doorIron"));
        func_179217_a(331, "redstone", (new ItemRedstone()).func_77655_b("redstone"));
        func_179217_a(332, "snowball", (new ItemSnowball()).func_77655_b("snowball"));
        func_179217_a(333, "boat", new ItemBoat(EntityBoat.Type.OAK));
        func_179217_a(334, "leather", (new Item()).func_77655_b("leather").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(335, "milk_bucket", (new ItemBucketMilk()).func_77655_b("milk").func_77642_a(item));
        func_179217_a(336, "brick", (new Item()).func_77655_b("brick").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(337, "clay_ball", (new Item()).func_77655_b("clay").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(338, "reeds", (new ItemBlockSpecial(Blocks.field_150436_aH)).func_77655_b("reeds").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(339, "paper", (new Item()).func_77655_b("paper").func_77637_a(CreativeTabs.MISC));
        func_179217_a(340, "book", (new ItemBook()).func_77655_b("book").func_77637_a(CreativeTabs.MISC));
        func_179217_a(341, "slime_ball", (new Item()).func_77655_b("slimeball").func_77637_a(CreativeTabs.MISC));
        func_179217_a(342, "chest_minecart", (new ItemMinecart(EntityMinecart.Type.CHEST)).func_77655_b("minecartChest"));
        func_179217_a(343, "furnace_minecart", (new ItemMinecart(EntityMinecart.Type.FURNACE)).func_77655_b("minecartFurnace"));
        func_179217_a(344, "egg", (new ItemEgg()).func_77655_b("egg"));
        func_179217_a(345, "compass", (new ItemCompass()).func_77655_b("compass").func_77637_a(CreativeTabs.TOOLS));
        func_179217_a(346, "fishing_rod", (new ItemFishingRod()).func_77655_b("fishingRod"));
        func_179217_a(347, "clock", (new ItemClock()).func_77655_b("clock").func_77637_a(CreativeTabs.TOOLS));
        func_179217_a(348, "glowstone_dust", (new Item()).func_77655_b("yellowDust").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(349, "fish", (new ItemFishFood(false)).func_77655_b("fish").func_77627_a(true));
        func_179217_a(350, "cooked_fish", (new ItemFishFood(true)).func_77655_b("fish").func_77627_a(true));
        func_179217_a(351, "dye", (new ItemDye()).func_77655_b("dyePowder"));
        func_179217_a(352, "bone", (new Item()).func_77655_b("bone").func_77664_n().func_77637_a(CreativeTabs.MISC));
        func_179217_a(353, "sugar", (new Item()).func_77655_b("sugar").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(354, "cake", (new ItemBlockSpecial(Blocks.CAKE)).func_77625_d(1).func_77655_b("cake").func_77637_a(CreativeTabs.FOOD));
        func_179217_a(355, "bed", (new ItemBed()).func_77625_d(1).func_77655_b("bed"));
        func_179217_a(356, "repeater", (new ItemBlockSpecial(Blocks.field_150413_aR)).func_77655_b("diode").func_77637_a(CreativeTabs.REDSTONE));
        func_179217_a(357, "cookie", (new ItemFood(2, 0.1F, false)).func_77655_b("cookie"));
        func_179217_a(358, "filled_map", (new ItemMap()).func_77655_b("map"));
        func_179217_a(359, "shears", (new ItemShears()).func_77655_b("shears"));
        func_179217_a(360, "melon", (new ItemFood(2, 0.3F, false)).func_77655_b("melon"));
        func_179217_a(361, "pumpkin_seeds", (new ItemSeeds(Blocks.PUMPKIN_STEM, Blocks.FARMLAND)).func_77655_b("seeds_pumpkin"));
        func_179217_a(362, "melon_seeds", (new ItemSeeds(Blocks.MELON_STEM, Blocks.FARMLAND)).func_77655_b("seeds_melon"));
        func_179217_a(363, "beef", (new ItemFood(3, 0.3F, true)).func_77655_b("beefRaw"));
        func_179217_a(364, "cooked_beef", (new ItemFood(8, 0.8F, true)).func_77655_b("beefCooked"));
        func_179217_a(365, "chicken", (new ItemFood(2, 0.3F, true)).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.3F).func_77655_b("chickenRaw"));
        func_179217_a(366, "cooked_chicken", (new ItemFood(6, 0.6F, true)).func_77655_b("chickenCooked"));
        func_179217_a(367, "rotten_flesh", (new ItemFood(4, 0.1F, true)).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.8F).func_77655_b("rottenFlesh"));
        func_179217_a(368, "ender_pearl", (new ItemEnderPearl()).func_77655_b("enderPearl"));
        func_179217_a(369, "blaze_rod", (new Item()).func_77655_b("blazeRod").func_77637_a(CreativeTabs.MATERIALS).func_77664_n());
        func_179217_a(370, "ghast_tear", (new Item()).func_77655_b("ghastTear").func_77637_a(CreativeTabs.BREWING));
        func_179217_a(371, "gold_nugget", (new Item()).func_77655_b("goldNugget").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(372, "nether_wart", (new ItemSeeds(Blocks.NETHER_WART, Blocks.SOUL_SAND)).func_77655_b("netherStalkSeeds"));
        func_179217_a(373, "potion", (new ItemPotion()).func_77655_b("potion"));
        Item item1 = (new ItemGlassBottle()).func_77655_b("glassBottle");
        func_179217_a(374, "glass_bottle", item1);
        func_179217_a(375, "spider_eye", (new ItemFood(2, 0.8F, false)).setPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0), 1.0F).func_77655_b("spiderEye"));
        func_179217_a(376, "fermented_spider_eye", (new Item()).func_77655_b("fermentedSpiderEye").func_77637_a(CreativeTabs.BREWING));
        func_179217_a(377, "blaze_powder", (new Item()).func_77655_b("blazePowder").func_77637_a(CreativeTabs.BREWING));
        func_179217_a(378, "magma_cream", (new Item()).func_77655_b("magmaCream").func_77637_a(CreativeTabs.BREWING));
        func_179217_a(379, "brewing_stand", (new ItemBlockSpecial(Blocks.BREWING_STAND)).func_77655_b("brewingStand").func_77637_a(CreativeTabs.BREWING));
        func_179217_a(380, "cauldron", (new ItemBlockSpecial(Blocks.CAULDRON)).func_77655_b("cauldron").func_77637_a(CreativeTabs.BREWING));
        func_179217_a(381, "ender_eye", (new ItemEnderEye()).func_77655_b("eyeOfEnder"));
        func_179217_a(382, "speckled_melon", (new Item()).func_77655_b("speckledMelon").func_77637_a(CreativeTabs.BREWING));
        func_179217_a(383, "spawn_egg", (new ItemMonsterPlacer()).func_77655_b("monsterPlacer"));
        func_179217_a(384, "experience_bottle", (new ItemExpBottle()).func_77655_b("expBottle"));
        func_179217_a(385, "fire_charge", (new ItemFireball()).func_77655_b("fireball"));
        func_179217_a(386, "writable_book", (new ItemWritableBook()).func_77655_b("writingBook").func_77637_a(CreativeTabs.MISC));
        func_179217_a(387, "written_book", (new ItemWrittenBook()).func_77655_b("writtenBook").func_77625_d(16));
        func_179217_a(388, "emerald", (new Item()).func_77655_b("emerald").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(389, "item_frame", (new ItemHangingEntity(EntityItemFrame.class)).func_77655_b("frame"));
        func_179217_a(390, "flower_pot", (new ItemBlockSpecial(Blocks.FLOWER_POT)).func_77655_b("flowerPot").func_77637_a(CreativeTabs.DECORATIONS));
        func_179217_a(391, "carrot", (new ItemSeedFood(3, 0.6F, Blocks.CARROTS, Blocks.FARMLAND)).func_77655_b("carrots"));
        func_179217_a(392, "potato", (new ItemSeedFood(1, 0.3F, Blocks.POTATOES, Blocks.FARMLAND)).func_77655_b("potato"));
        func_179217_a(393, "baked_potato", (new ItemFood(5, 0.6F, false)).func_77655_b("potatoBaked"));
        func_179217_a(394, "poisonous_potato", (new ItemFood(2, 0.3F, false)).setPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0), 0.6F).func_77655_b("potatoPoisonous"));
        func_179217_a(395, "map", (new ItemEmptyMap()).func_77655_b("emptyMap"));
        func_179217_a(396, "golden_carrot", (new ItemFood(6, 1.2F, false)).func_77655_b("carrotGolden").func_77637_a(CreativeTabs.BREWING));
        func_179217_a(397, "skull", (new ItemSkull()).func_77655_b("skull"));
        func_179217_a(398, "carrot_on_a_stick", (new ItemCarrotOnAStick()).func_77655_b("carrotOnAStick"));
        func_179217_a(399, "nether_star", (new ItemSimpleFoiled()).func_77655_b("netherStar").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(400, "pumpkin_pie", (new ItemFood(8, 0.3F, false)).func_77655_b("pumpkinPie").func_77637_a(CreativeTabs.FOOD));
        func_179217_a(401, "fireworks", (new ItemFirework()).func_77655_b("fireworks"));
        func_179217_a(402, "firework_charge", (new ItemFireworkCharge()).func_77655_b("fireworksCharge").func_77637_a(CreativeTabs.MISC));
        func_179217_a(403, "enchanted_book", (new ItemEnchantedBook()).func_77625_d(1).func_77655_b("enchantedBook"));
        func_179217_a(404, "comparator", (new ItemBlockSpecial(Blocks.field_150441_bU)).func_77655_b("comparator").func_77637_a(CreativeTabs.REDSTONE));
        func_179217_a(405, "netherbrick", (new Item()).func_77655_b("netherbrick").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(406, "quartz", (new Item()).func_77655_b("netherquartz").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(407, "tnt_minecart", (new ItemMinecart(EntityMinecart.Type.TNT)).func_77655_b("minecartTnt"));
        func_179217_a(408, "hopper_minecart", (new ItemMinecart(EntityMinecart.Type.HOPPER)).func_77655_b("minecartHopper"));
        func_179217_a(409, "prismarine_shard", (new Item()).func_77655_b("prismarineShard").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(410, "prismarine_crystals", (new Item()).func_77655_b("prismarineCrystals").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(411, "rabbit", (new ItemFood(3, 0.3F, true)).func_77655_b("rabbitRaw"));
        func_179217_a(412, "cooked_rabbit", (new ItemFood(5, 0.6F, true)).func_77655_b("rabbitCooked"));
        func_179217_a(413, "rabbit_stew", (new ItemSoup(10)).func_77655_b("rabbitStew"));
        func_179217_a(414, "rabbit_foot", (new Item()).func_77655_b("rabbitFoot").func_77637_a(CreativeTabs.BREWING));
        func_179217_a(415, "rabbit_hide", (new Item()).func_77655_b("rabbitHide").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(416, "armor_stand", (new ItemArmorStand()).func_77655_b("armorStand").func_77625_d(16));
        func_179217_a(417, "iron_horse_armor", (new Item()).func_77655_b("horsearmormetal").func_77625_d(1).func_77637_a(CreativeTabs.MISC));
        func_179217_a(418, "golden_horse_armor", (new Item()).func_77655_b("horsearmorgold").func_77625_d(1).func_77637_a(CreativeTabs.MISC));
        func_179217_a(419, "diamond_horse_armor", (new Item()).func_77655_b("horsearmordiamond").func_77625_d(1).func_77637_a(CreativeTabs.MISC));
        func_179217_a(420, "lead", (new ItemLead()).func_77655_b("leash"));
        func_179217_a(421, "name_tag", (new ItemNameTag()).func_77655_b("nameTag"));
        func_179217_a(422, "command_block_minecart", (new ItemMinecart(EntityMinecart.Type.COMMAND_BLOCK)).func_77655_b("minecartCommandBlock").func_77637_a((CreativeTabs)null));
        func_179217_a(423, "mutton", (new ItemFood(2, 0.3F, true)).func_77655_b("muttonRaw"));
        func_179217_a(424, "cooked_mutton", (new ItemFood(6, 0.8F, true)).func_77655_b("muttonCooked"));
        func_179217_a(425, "banner", (new ItemBanner()).func_77655_b("banner"));
        func_179217_a(426, "end_crystal", new ItemEndCrystal());
        func_179217_a(427, "spruce_door", (new ItemDoor(Blocks.SPRUCE_DOOR)).func_77655_b("doorSpruce"));
        func_179217_a(428, "birch_door", (new ItemDoor(Blocks.BIRCH_DOOR)).func_77655_b("doorBirch"));
        func_179217_a(429, "jungle_door", (new ItemDoor(Blocks.JUNGLE_DOOR)).func_77655_b("doorJungle"));
        func_179217_a(430, "acacia_door", (new ItemDoor(Blocks.ACACIA_DOOR)).func_77655_b("doorAcacia"));
        func_179217_a(431, "dark_oak_door", (new ItemDoor(Blocks.DARK_OAK_DOOR)).func_77655_b("doorDarkOak"));
        func_179217_a(432, "chorus_fruit", (new ItemChorusFruit(4, 0.3F)).setAlwaysEdible().func_77655_b("chorusFruit").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(433, "chorus_fruit_popped", (new Item()).func_77655_b("chorusFruitPopped").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(434, "beetroot", (new ItemFood(1, 0.6F, false)).func_77655_b("beetroot"));
        func_179217_a(435, "beetroot_seeds", (new ItemSeeds(Blocks.BEETROOTS, Blocks.FARMLAND)).func_77655_b("beetroot_seeds"));
        func_179217_a(436, "beetroot_soup", (new ItemSoup(6)).func_77655_b("beetroot_soup"));
        func_179217_a(437, "dragon_breath", (new Item()).func_77637_a(CreativeTabs.BREWING).func_77655_b("dragon_breath").func_77642_a(item1));
        func_179217_a(438, "splash_potion", (new ItemSplashPotion()).func_77655_b("splash_potion"));
        func_179217_a(439, "spectral_arrow", (new ItemSpectralArrow()).func_77655_b("spectral_arrow"));
        func_179217_a(440, "tipped_arrow", (new ItemTippedArrow()).func_77655_b("tipped_arrow"));
        func_179217_a(441, "lingering_potion", (new ItemLingeringPotion()).func_77655_b("lingering_potion"));
        func_179217_a(442, "shield", (new ItemShield()).func_77655_b("shield"));
        func_179217_a(443, "elytra", (new ItemElytra()).func_77655_b("elytra"));
        func_179217_a(444, "spruce_boat", new ItemBoat(EntityBoat.Type.SPRUCE));
        func_179217_a(445, "birch_boat", new ItemBoat(EntityBoat.Type.BIRCH));
        func_179217_a(446, "jungle_boat", new ItemBoat(EntityBoat.Type.JUNGLE));
        func_179217_a(447, "acacia_boat", new ItemBoat(EntityBoat.Type.ACACIA));
        func_179217_a(448, "dark_oak_boat", new ItemBoat(EntityBoat.Type.DARK_OAK));
        func_179217_a(449, "totem_of_undying", (new Item()).func_77655_b("totem").func_77625_d(1).func_77637_a(CreativeTabs.COMBAT));
        func_179217_a(450, "shulker_shell", (new Item()).func_77655_b("shulkerShell").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(452, "iron_nugget", (new Item()).func_77655_b("ironNugget").func_77637_a(CreativeTabs.MATERIALS));
        func_179217_a(453, "knowledge_book", (new ItemKnowledgeBook()).func_77655_b("knowledgeBook"));
        func_179217_a(2256, "record_13", (new ItemRecord("13", SoundEvents.MUSIC_DISC_13)).func_77655_b("record"));
        func_179217_a(2257, "record_cat", (new ItemRecord("cat", SoundEvents.MUSIC_DISC_CAT)).func_77655_b("record"));
        func_179217_a(2258, "record_blocks", (new ItemRecord("blocks", SoundEvents.MUSIC_DISC_BLOCKS)).func_77655_b("record"));
        func_179217_a(2259, "record_chirp", (new ItemRecord("chirp", SoundEvents.MUSIC_DISC_CHIRP)).func_77655_b("record"));
        func_179217_a(2260, "record_far", (new ItemRecord("far", SoundEvents.MUSIC_DISC_FAR)).func_77655_b("record"));
        func_179217_a(2261, "record_mall", (new ItemRecord("mall", SoundEvents.MUSIC_DISC_MALL)).func_77655_b("record"));
        func_179217_a(2262, "record_mellohi", (new ItemRecord("mellohi", SoundEvents.MUSIC_DISC_MELLOHI)).func_77655_b("record"));
        func_179217_a(2263, "record_stal", (new ItemRecord("stal", SoundEvents.MUSIC_DISC_STAL)).func_77655_b("record"));
        func_179217_a(2264, "record_strad", (new ItemRecord("strad", SoundEvents.MUSIC_DISC_STRAD)).func_77655_b("record"));
        func_179217_a(2265, "record_ward", (new ItemRecord("ward", SoundEvents.MUSIC_DISC_WARD)).func_77655_b("record"));
        func_179217_a(2266, "record_11", (new ItemRecord("11", SoundEvents.MUSIC_DISC_11)).func_77655_b("record"));
        func_179217_a(2267, "record_wait", (new ItemRecord("wait", SoundEvents.MUSIC_DISC_WAIT)).func_77655_b("record"));
    }

    /**
     * Register a default ItemBlock for the given Block.
     */
    private static void register(Block blockIn)
    {
        register(blockIn, new ItemBlock(blockIn));
    }

    /**
     * Register the given Item as the ItemBlock for the given Block.
     */
    protected static void register(Block blockIn, Item itemIn)
    {
        func_179219_a(Block.func_149682_b(blockIn), Block.REGISTRY.getKey(blockIn), itemIn);
        BLOCK_TO_ITEM.put(blockIn, itemIn);
    }

    private static void func_179217_a(int p_179217_0_, String p_179217_1_, Item p_179217_2_)
    {
        func_179219_a(p_179217_0_, new ResourceLocation(p_179217_1_), p_179217_2_);
    }

    private static void func_179219_a(int p_179219_0_, ResourceLocation p_179219_1_, Item p_179219_2_)
    {
        REGISTRY.register(p_179219_0_, p_179219_1_, p_179219_2_);
    }

    @SideOnly(Side.CLIENT)
    public ItemStack getDefaultInstance()
    {
        return new ItemStack(this);
    }

    public static enum ToolMaterial
    {
        WOOD(0, 59, 2.0F, 0.0F, 15),
        STONE(1, 131, 4.0F, 1.0F, 5),
        IRON(2, 250, 6.0F, 2.0F, 14),
        DIAMOND(3, 1561, 8.0F, 3.0F, 10),
        GOLD(0, 32, 12.0F, 0.0F, 22);

        /** The level of material this tool can harvest (3 = DIAMOND, 2 = IRON, 1 = STONE, 0 = WOOD/GOLD) */
        private final int harvestLevel;
        /** The number of uses this material allows. (wood = 59, stone = 131, iron = 250, diamond = 1561, gold = 32) */
        private final int maxUses;
        /** The strength of this tool material against blocks which it is effective against. */
        private final float efficiency;
        /** Damage versus entities. */
        private final float attackDamage;
        /** Defines the natural enchantability factor of the material. */
        private final int enchantability;
        //Added by forge for custom Tool materials.
        private ItemStack repairMaterial = ItemStack.EMPTY;

        private ToolMaterial(int p_i1874_3_, int p_i1874_4_, float p_i1874_5_, float p_i1874_6_, int p_i1874_7_)
        {
            this.harvestLevel = p_i1874_3_;
            this.maxUses = p_i1874_4_;
            this.efficiency = p_i1874_5_;
            this.attackDamage = p_i1874_6_;
            this.enchantability = p_i1874_7_;
        }

        public int func_77997_a()
        {
            return this.maxUses;
        }

        public float func_77998_b()
        {
            return this.efficiency;
        }

        public float func_78000_c()
        {
            return this.attackDamage;
        }

        public int func_77996_d()
        {
            return this.harvestLevel;
        }

        public int func_77995_e()
        {
            return this.enchantability;
        }

        @Deprecated // Use getRepairItemStack below
        public Item func_150995_f()
        {
            if (this == WOOD)
            {
                return Item.getItemFromBlock(Blocks.field_150344_f);
            }
            else if (this == STONE)
            {
                return Item.getItemFromBlock(Blocks.COBBLESTONE);
            }
            else if (this == GOLD)
            {
                return Items.GOLD_INGOT;
            }
            else if (this == IRON)
            {
                return Items.IRON_INGOT;
            }
            else
            {
                return this == DIAMOND ? Items.DIAMOND : null;
            }
        }

        public ToolMaterial setRepairItem(ItemStack stack)
        {
            if (!this.repairMaterial.isEmpty()) throw new RuntimeException("Repair material has already been set");
            if (this == WOOD || this == STONE || this == GOLD || this == IRON || this == DIAMOND) throw new RuntimeException("Can not change vanilla tool repair materials");
            this.repairMaterial = stack;
            return this;
        }

        public ItemStack getRepairItemStack()
        {
            if (!repairMaterial.isEmpty()) return repairMaterial;
            Item ret = this.func_150995_f();
            if (ret != null) repairMaterial = new ItemStack(ret, 1, net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE);
            return repairMaterial;
        }
    }
}