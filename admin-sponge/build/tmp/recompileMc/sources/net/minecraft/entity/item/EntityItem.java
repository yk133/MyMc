package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityItem extends Entity
{
    private static final Logger field_145803_d = LogManager.getLogger();
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.<ItemStack>createKey(EntityItem.class, DataSerializers.ITEM_STACK);
    /** The age of this EntityItem (used to animate it up and down as well as expire it) */
    private int age;
    private int pickupDelay;
    /** The health of this EntityItem. (For example, damage for tools) */
    private int health;
    private String thrower;
    private String owner;
    /** The EntityItem's random initial float height. */
    public float hoverStart;

    /**
     * The maximum age of this EntityItem.  The item is expired once this is reached.
     */
    public int lifespan = 6000;

    public EntityItem(World worldIn, double x, double y, double z)
    {
        super(worldIn);
        this.health = 5;
        this.hoverStart = (float)(Math.random() * Math.PI * 2.0D);
        this.setSize(0.25F, 0.25F);
        this.setPosition(x, y, z);
        this.rotationYaw = (float)(Math.random() * 360.0D);
        this.motionX = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D));
        this.motionY = 0.20000000298023224D;
        this.motionZ = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D));
    }

    public EntityItem(World worldIn, double x, double y, double z, ItemStack stack)
    {
        this(worldIn, x, y, z);
        this.setItem(stack);
        this.lifespan = (stack.getItem() == null ? 6000 : stack.getItem().getEntityLifespan(stack, worldIn));
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    public EntityItem(World worldIn)
    {
        super(worldIn);
        this.health = 5;
        this.hoverStart = (float)(Math.random() * Math.PI * 2.0D);
        this.setSize(0.25F, 0.25F);
        this.setItem(ItemStack.EMPTY);
    }

    protected void registerData()
    {
        this.getDataManager().register(ITEM, ItemStack.EMPTY);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick()
    {
        if (getItem().getItem().onEntityItemUpdate(this)) return;
        if (this.getItem().isEmpty())
        {
            this.remove();
        }
        else
        {
            super.tick();

            if (this.pickupDelay > 0 && this.pickupDelay != 32767)
            {
                --this.pickupDelay;
            }

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            double d0 = this.motionX;
            double d1 = this.motionY;
            double d2 = this.motionZ;

            if (!this.hasNoGravity())
            {
                this.motionY -= 0.03999999910593033D;
            }

            if (this.world.isRemote)
            {
                this.noClip = false;
            }
            else
            {
                this.noClip = this.pushOutOfBlocks(this.posX, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.posZ);
            }

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            boolean flag = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;

            if (flag || this.ticksExisted % 25 == 0)
            {
                if (this.world.getBlockState(new BlockPos(this)).getMaterial() == Material.LAVA)
                {
                    this.motionY = 0.20000000298023224D;
                    this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                    this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                    this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
                }

                if (!this.world.isRemote)
                {
                    this.searchForOtherItemsNearby();
                }
            }

            float f = 0.98F;

            if (this.onGround)
            {
                BlockPos underPos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
                net.minecraft.block.state.IBlockState underState = this.world.getBlockState(underPos);
                f = underState.getBlock().getSlipperiness(underState, this.world, underPos, this) * 0.98F;
            }

            this.motionX *= (double)f;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= (double)f;

            if (this.onGround)
            {
                this.motionY *= -0.5D;
            }

            if (this.age != -32768)
            {
                ++this.age;
            }

            this.handleWaterMovement();

            if (!this.world.isRemote)
            {
                double d3 = this.motionX - d0;
                double d4 = this.motionY - d1;
                double d5 = this.motionZ - d2;
                double d6 = d3 * d3 + d4 * d4 + d5 * d5;

                if (d6 > 0.01D)
                {
                    this.isAirBorne = true;
                }
            }

            ItemStack item = this.getItem();

            if (!this.world.isRemote && this.age >= lifespan)
            {
                int hook = net.minecraftforge.event.ForgeEventFactory.onItemExpire(this, item);
                if (hook < 0) this.remove();
                else          this.lifespan += hook;
            }
            if (item.isEmpty())
            {
                this.remove();
            }
        }
    }

    /**
     * Looks for other itemstacks nearby and tries to stack them together
     */
    private void searchForOtherItemsNearby()
    {
        for (EntityItem entityitem : this.world.getEntitiesWithinAABB(EntityItem.class, this.getBoundingBox().grow(0.5D, 0.0D, 0.5D)))
        {
            this.combineItems(entityitem);
        }
    }

    /**
     * Tries to merge this item with the item passed as the parameter. Returns true if successful. Either this item or
     * the other item will  be removed from the world.
     */
    private boolean combineItems(EntityItem other)
    {
        if (other == this)
        {
            return false;
        }
        else if (other.isAlive() && this.isAlive())
        {
            ItemStack itemstack = this.getItem();
            ItemStack itemstack1 = other.getItem();

            if (this.pickupDelay != 32767 && other.pickupDelay != 32767)
            {
                if (this.age != -32768 && other.age != -32768)
                {
                    if (itemstack1.getItem() != itemstack.getItem())
                    {
                        return false;
                    }
                    else if (itemstack1.hasTag() ^ itemstack.hasTag())
                    {
                        return false;
                    }
                    else if (itemstack1.hasTag() && !itemstack1.getTag().equals(itemstack.getTag()))
                    {
                        return false;
                    }
                    else if (itemstack1.getItem() == null)
                    {
                        return false;
                    }
                    else if (itemstack1.getItem().func_77614_k() && itemstack1.func_77960_j() != itemstack.func_77960_j())
                    {
                        return false;
                    }
                    else if (itemstack1.getCount() < itemstack.getCount())
                    {
                        return other.combineItems(this);
                    }
                    else if (itemstack1.getCount() + itemstack.getCount() > itemstack1.getMaxStackSize())
                    {
                        return false;
                    }
                    else if (!itemstack.areCapsCompatible(itemstack1))
                    {
                        return false;
                    }
                    else
                    {
                        itemstack1.grow(itemstack.getCount());
                        other.pickupDelay = Math.max(other.pickupDelay, this.pickupDelay);
                        other.age = Math.min(other.age, this.age);
                        other.setItem(itemstack1);
                        this.remove();
                        return true;
                    }
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * sets the age of the item so that it'll despawn one minute after it has been dropped (instead of five). Used when
     * items are dropped from players in creative mode
     */
    public void setAgeToCreativeDespawnTime()
    {
        this.age = 4800;
    }

    /**
     * Returns if this entity is in water and will end up adding the waters velocity to the entity
     */
    public boolean handleWaterMovement()
    {
        if (this.world.func_72918_a(this.getBoundingBox(), Material.WATER, this))
        {
            if (!this.inWater && !this.firstUpdate)
            {
                this.doWaterSplashEffect();
            }

            this.inWater = true;
        }
        else
        {
            this.inWater = false;
        }

        return this.inWater;
    }

    /**
     * Will deal the specified amount of fire damage to the entity if the entity isn't immune to fire damage.
     */
    protected void dealFireDamage(int amount)
    {
        this.attackEntityFrom(DamageSource.IN_FIRE, (float)amount);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.world.isRemote || this.removed) return false; //Forge: Fixes MC-53850
        if (this.isInvulnerableTo(source))
        {
            return false;
        }
        else if (!this.getItem().isEmpty() && this.getItem().getItem() == Items.NETHER_STAR && source.isExplosion())
        {
            return false;
        }
        else
        {
            this.markVelocityChanged();
            this.health = (int)((float)this.health - amount);

            if (this.health <= 0)
            {
                this.remove();
            }

            return false;
        }
    }

    public static void func_189742_a(DataFixer p_189742_0_)
    {
        p_189742_0_.func_188258_a(FixTypes.ENTITY, new ItemStackData(EntityItem.class, new String[] {"Item"}));
    }

    /**
     * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
     * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
     */
    public void writeAdditional(NBTTagCompound compound)
    {
        compound.putShort("Health", (short)this.health);
        compound.putShort("Age", (short)this.age);
        compound.putShort("PickupDelay", (short)this.pickupDelay);
        compound.putInt("Lifespan", lifespan);

        if (this.func_145800_j() != null)
        {
            compound.putString("Thrower", this.thrower);
        }

        if (this.func_145798_i() != null)
        {
            compound.putString("Owner", this.owner);
        }

        if (!this.getItem().isEmpty())
        {
            compound.put("Item", this.getItem().write(new NBTTagCompound()));
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(NBTTagCompound compound)
    {
        this.health = compound.getShort("Health");
        this.age = compound.getShort("Age");

        if (compound.contains("PickupDelay"))
        {
            this.pickupDelay = compound.getShort("PickupDelay");
        }

        if (compound.contains("Owner"))
        {
            this.owner = compound.getString("Owner");
        }

        if (compound.contains("Thrower"))
        {
            this.thrower = compound.getString("Thrower");
        }

        NBTTagCompound nbttagcompound = compound.getCompound("Item");
        this.setItem(new ItemStack(nbttagcompound));

        if (this.getItem().isEmpty())
        {
            this.remove();
        }
        if (compound.contains("Lifespan")) lifespan = compound.getInt("Lifespan");
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer entityIn)
    {
        if (!this.world.isRemote)
        {
            if (this.pickupDelay > 0) return;
            ItemStack itemstack = this.getItem();
            Item item = itemstack.getItem();
            int i = itemstack.getCount();

            int hook = net.minecraftforge.event.ForgeEventFactory.onItemPickup(this, entityIn);
            if (hook < 0) return;
            ItemStack clone = itemstack.copy();

            if (this.pickupDelay <= 0 && (this.owner == null || lifespan - this.age <= 200 || this.owner.equals(entityIn.func_70005_c_())) && (hook == 1 || i <= 0 || entityIn.inventory.addItemStackToInventory(itemstack) || clone.getCount() > this.getItem().getCount()))
            {
                clone.setCount(clone.getCount() - this.getItem().getCount());
                net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerItemPickupEvent(entityIn, this, clone);

                if (itemstack.isEmpty())
                {
                    entityIn.onItemPickup(this, i);
                    this.remove();
                    itemstack.setCount(i);
                }

                entityIn.addStat(StatList.func_188056_d(item), i);
            }
        }
    }

    public String func_70005_c_()
    {
        return this.hasCustomName() ? this.func_95999_t() : I18n.func_74838_a("item." + this.getItem().getTranslationKey());
    }

    /**
     * Returns true if it's possible to attack this entity with an item.
     */
    public boolean canBeAttackedWithItem()
    {
        return false;
    }

    @Nullable
    public Entity changeDimension(int dimensionIn, net.minecraftforge.common.util.ITeleporter teleporter)
    {
        Entity entity = super.changeDimension(dimensionIn, teleporter);

        if (!this.world.isRemote && entity instanceof EntityItem)
        {
            ((EntityItem)entity).searchForOtherItemsNearby();
        }

        return entity;
    }

    /**
     * Gets the item that this entity represents.
     */
    public ItemStack getItem()
    {
        return (ItemStack)this.getDataManager().get(ITEM);
    }

    /**
     * Sets the item that this entity represents.
     */
    public void setItem(ItemStack stack)
    {
        this.getDataManager().set(ITEM, stack);
        this.getDataManager().func_187217_b(ITEM);
    }

    public String func_145798_i()
    {
        return this.owner;
    }

    public void func_145797_a(String p_145797_1_)
    {
        this.owner = p_145797_1_;
    }

    public String func_145800_j()
    {
        return this.thrower;
    }

    public void func_145799_b(String p_145799_1_)
    {
        this.thrower = p_145799_1_;
    }

    @SideOnly(Side.CLIENT)
    public int getAge()
    {
        return this.age;
    }

    public void setDefaultPickupDelay()
    {
        this.pickupDelay = 10;
    }

    public void setNoPickupDelay()
    {
        this.pickupDelay = 0;
    }

    public void setInfinitePickupDelay()
    {
        this.pickupDelay = 32767;
    }

    public void setPickupDelay(int ticks)
    {
        this.pickupDelay = ticks;
    }

    public boolean cannotPickup()
    {
        return this.pickupDelay > 0;
    }

    public void setNoDespawn()
    {
        this.age = -6000;
    }

    public void makeFakeItem()
    {
        this.setInfinitePickupDelay();
        this.age = getItem().getItem().getEntityLifespan(getItem(), world) - 1;
    }
}