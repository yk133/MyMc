package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityFallingBlock extends Entity
{
    private IBlockState fallTile;
    public int fallTime;
    public boolean shouldDropItem = true;
    private boolean dontSetBlock;
    private boolean hurtEntities;
    private int fallHurtMax = 40;
    private float fallHurtAmount = 2.0F;
    public NBTTagCompound tileEntityData;
    protected static final DataParameter<BlockPos> ORIGIN = EntityDataManager.<BlockPos>createKey(EntityFallingBlock.class, DataSerializers.BLOCK_POS);

    public EntityFallingBlock(World worldIn)
    {
        super(worldIn);
    }

    public EntityFallingBlock(World worldIn, double x, double y, double z, IBlockState fallingBlockState)
    {
        super(worldIn);
        this.fallTile = fallingBlockState;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        this.setPosition(x, y + (double)((1.0F - this.height) / 2.0F), z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.setOrigin(new BlockPos(this));
    }

    /**
     * Returns true if it's possible to attack this entity with an item.
     */
    public boolean canBeAttackedWithItem()
    {
        return false;
    }

    public void setOrigin(BlockPos p_184530_1_)
    {
        this.dataManager.set(ORIGIN, p_184530_1_);
    }

    @SideOnly(Side.CLIENT)
    public BlockPos getOrigin()
    {
        return (BlockPos)this.dataManager.get(ORIGIN);
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void registerData()
    {
        this.dataManager.register(ORIGIN, BlockPos.ORIGIN);
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.removed;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick()
    {
        Block block = this.fallTile.getBlock();

        if (this.fallTile.getMaterial() == Material.AIR)
        {
            this.remove();
        }
        else
        {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            if (this.fallTime++ == 0)
            {
                BlockPos blockpos = new BlockPos(this);

                if (this.world.getBlockState(blockpos).getBlock() == block)
                {
                    this.world.removeBlock(blockpos);
                }
                else if (!this.world.isRemote)
                {
                    this.remove();
                    return;
                }
            }

            if (!this.hasNoGravity())
            {
                this.motionY -= 0.03999999910593033D;
            }

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

            if (!this.world.isRemote)
            {
                BlockPos blockpos1 = new BlockPos(this);
                boolean flag = this.fallTile.getBlock() == Blocks.field_192444_dS;
                boolean flag1 = flag && this.world.getBlockState(blockpos1).getMaterial() == Material.WATER;
                double d0 = this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;

                if (flag && d0 > 1.0D)
                {
                    RayTraceResult raytraceresult = this.world.func_72901_a(new Vec3d(this.prevPosX, this.prevPosY, this.prevPosZ), new Vec3d(this.posX, this.posY, this.posZ), true);

                    if (raytraceresult != null && this.world.getBlockState(raytraceresult.getBlockPos()).getMaterial() == Material.WATER)
                    {
                        blockpos1 = raytraceresult.getBlockPos();
                        flag1 = true;
                    }
                }

                if (!this.onGround && !flag1)
                {
                    if (this.fallTime > 100 && !this.world.isRemote && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || this.fallTime > 600)
                    {
                        if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops"))
                        {
                            this.entityDropItem(new ItemStack(block, 1, block.func_180651_a(this.fallTile)), 0.0F);
                        }

                        this.remove();
                    }
                }
                else
                {
                    IBlockState iblockstate = this.world.getBlockState(blockpos1);

                    if (this.world.isAirBlock(new BlockPos(this.posX, this.posY - 0.009999999776482582D, this.posZ))) //Forge: Don't indent below.
                    if (!flag1 && BlockFalling.canFallThrough(this.world.getBlockState(new BlockPos(this.posX, this.posY - 0.009999999776482582D, this.posZ))))
                    {
                        this.onGround = false;
                        return;
                    }

                    this.motionX *= 0.699999988079071D;
                    this.motionZ *= 0.699999988079071D;
                    this.motionY *= -0.5D;

                    if (iblockstate.getBlock() != Blocks.field_180384_M)
                    {
                        this.remove();

                        if (!this.dontSetBlock)
                        {
                            if (this.world.func_190527_a(block, blockpos1, true, EnumFacing.UP, (Entity)null) && (flag1 || !BlockFalling.canFallThrough(this.world.getBlockState(blockpos1.down()))) && this.world.setBlockState(blockpos1, this.fallTile, 3))
                            {
                                if (block instanceof BlockFalling)
                                {
                                    ((BlockFalling)block).onEndFalling(this.world, blockpos1, this.fallTile, iblockstate);
                                }

                                if (this.tileEntityData != null && block.hasTileEntity(this.fallTile))
                                {
                                    TileEntity tileentity = this.world.getTileEntity(blockpos1);

                                    if (tileentity != null)
                                    {
                                        NBTTagCompound nbttagcompound = tileentity.write(new NBTTagCompound());

                                        for (String s : this.tileEntityData.keySet())
                                        {
                                            NBTBase nbtbase = this.tileEntityData.get(s);

                                            if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s))
                                            {
                                                nbttagcompound.put(s, nbtbase.copy());
                                            }
                                        }

                                        tileentity.read(nbttagcompound);
                                        tileentity.markDirty();
                                    }
                                }
                            }
                            else if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops"))
                            {
                                this.entityDropItem(new ItemStack(block, 1, block.func_180651_a(this.fallTile)), 0.0F);
                            }
                        }
                        else if (block instanceof BlockFalling)
                        {
                            ((BlockFalling)block).onBroken(this.world, blockpos1);
                        }
                    }
                }
            }

            this.motionX *= 0.9800000190734863D;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= 0.9800000190734863D;
        }
    }

    public void fall(float distance, float damageMultiplier)
    {
        Block block = this.fallTile.getBlock();

        if (this.hurtEntities)
        {
            int i = MathHelper.ceil(distance - 1.0F);

            if (i > 0)
            {
                List<Entity> list = Lists.newArrayList(this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox()));
                boolean flag = block == Blocks.ANVIL;
                DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;

                for (Entity entity : list)
                {
                    entity.attackEntityFrom(damagesource, (float)Math.min(MathHelper.floor((float)i * this.fallHurtAmount), this.fallHurtMax));
                }

                if (flag && (double)this.rand.nextFloat() < 0.05000000074505806D + (double)i * 0.05D)
                {
                    int j = ((Integer)this.fallTile.get(BlockAnvil.field_176505_b)).intValue();
                    ++j;

                    if (j > 2)
                    {
                        this.dontSetBlock = true;
                    }
                    else
                    {
                        this.fallTile = this.fallTile.func_177226_a(BlockAnvil.field_176505_b, Integer.valueOf(j));
                    }
                }
            }
        }
    }

    public static void func_189741_a(DataFixer p_189741_0_)
    {
    }

    /**
     * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
     * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
     */
    protected void writeAdditional(NBTTagCompound compound)
    {
        Block block = this.fallTile != null ? this.fallTile.getBlock() : Blocks.AIR;
        ResourceLocation resourcelocation = Block.REGISTRY.getKey(block);
        compound.putString("Block", resourcelocation == null ? "" : resourcelocation.toString());
        compound.putByte("Data", (byte)block.func_176201_c(this.fallTile));
        compound.putInt("Time", this.fallTime);
        compound.putBoolean("DropItem", this.shouldDropItem);
        compound.putBoolean("HurtEntities", this.hurtEntities);
        compound.putFloat("FallHurtAmount", this.fallHurtAmount);
        compound.putInt("FallHurtMax", this.fallHurtMax);

        if (this.tileEntityData != null)
        {
            compound.put("TileEntityData", this.tileEntityData);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditional(NBTTagCompound compound)
    {
        int i = compound.getByte("Data") & 255;

        if (compound.contains("Block", 8))
        {
            this.fallTile = Block.getBlockFromName(compound.getString("Block")).func_176203_a(i);
        }
        else if (compound.contains("TileID", 99))
        {
            this.fallTile = Block.getBlockById(compound.getInt("TileID")).func_176203_a(i);
        }
        else
        {
            this.fallTile = Block.getBlockById(compound.getByte("Tile") & 255).func_176203_a(i);
        }

        this.fallTime = compound.getInt("Time");
        Block block = this.fallTile.getBlock();

        if (compound.contains("HurtEntities", 99))
        {
            this.hurtEntities = compound.getBoolean("HurtEntities");
            this.fallHurtAmount = compound.getFloat("FallHurtAmount");
            this.fallHurtMax = compound.getInt("FallHurtMax");
        }
        else if (block == Blocks.ANVIL)
        {
            this.hurtEntities = true;
        }

        if (compound.contains("DropItem", 99))
        {
            this.shouldDropItem = compound.getBoolean("DropItem");
        }

        if (compound.contains("TileEntityData", 10))
        {
            this.tileEntityData = compound.getCompound("TileEntityData");
        }

        if (block == null || block.getDefaultState().getMaterial() == Material.AIR)
        {
            this.fallTile = Blocks.SAND.getDefaultState();
        }
    }

    public void setHurtEntities(boolean hurtEntitiesIn)
    {
        this.hurtEntities = hurtEntitiesIn;
    }

    public void fillCrashReport(CrashReportCategory category)
    {
        super.fillCrashReport(category);

        if (this.fallTile != null)
        {
            Block block = this.fallTile.getBlock();
            category.addDetail("Immitating block ID", Integer.valueOf(Block.func_149682_b(block)));
            category.addDetail("Immitating block data", Integer.valueOf(block.func_176201_c(this.fallTile)));
        }
    }

    @SideOnly(Side.CLIENT)
    public World getWorldObj()
    {
        return this.world;
    }

    /**
     * Return whether this entity should be rendered as on fire.
     */
    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire()
    {
        return false;
    }

    @Nullable
    public IBlockState func_175131_l()
    {
        return this.fallTile;
    }

    public boolean ignoreItemEntityData()
    {
        return true;
    }
}