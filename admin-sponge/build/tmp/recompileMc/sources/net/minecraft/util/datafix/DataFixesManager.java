package net.minecraft.util.datafix;

import net.minecraft.block.BlockJukebox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.datafix.fixes.AddBedTileEntity;
import net.minecraft.util.datafix.fixes.ArmorStandSilent;
import net.minecraft.util.datafix.fixes.BannerItemColor;
import net.minecraft.util.datafix.fixes.BedItemColor;
import net.minecraft.util.datafix.fixes.BookPagesStrictJSON;
import net.minecraft.util.datafix.fixes.CookedFishIDTypo;
import net.minecraft.util.datafix.fixes.ElderGuardianSplit;
import net.minecraft.util.datafix.fixes.EntityArmorAndHeld;
import net.minecraft.util.datafix.fixes.EntityHealth;
import net.minecraft.util.datafix.fixes.EntityId;
import net.minecraft.util.datafix.fixes.ForceVBOOn;
import net.minecraft.util.datafix.fixes.HorseSaddle;
import net.minecraft.util.datafix.fixes.HorseSplit;
import net.minecraft.util.datafix.fixes.ItemIntIDToString;
import net.minecraft.util.datafix.fixes.MinecartEntityTypes;
import net.minecraft.util.datafix.fixes.OptionsLowerCaseLanguage;
import net.minecraft.util.datafix.fixes.PaintingDirection;
import net.minecraft.util.datafix.fixes.PotionItems;
import net.minecraft.util.datafix.fixes.PotionWater;
import net.minecraft.util.datafix.fixes.RedundantChanceTags;
import net.minecraft.util.datafix.fixes.RidingToPassengers;
import net.minecraft.util.datafix.fixes.ShulkerBoxEntityColor;
import net.minecraft.util.datafix.fixes.ShulkerBoxItemColor;
import net.minecraft.util.datafix.fixes.ShulkerBoxTileColor;
import net.minecraft.util.datafix.fixes.SignStrictJSON;
import net.minecraft.util.datafix.fixes.SkeletonSplit;
import net.minecraft.util.datafix.fixes.SpawnEggNames;
import net.minecraft.util.datafix.fixes.SpawnerEntityTypes;
import net.minecraft.util.datafix.fixes.StringToUUID;
import net.minecraft.util.datafix.fixes.TileEntityId;
import net.minecraft.util.datafix.fixes.TotemItemRename;
import net.minecraft.util.datafix.fixes.ZombieProfToType;
import net.minecraft.util.datafix.fixes.ZombieSplit;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.storage.WorldInfo;

public class DataFixesManager
{
    private static void func_188276_a(DataFixer p_188276_0_)
    {
        p_188276_0_.func_188256_a(FixTypes.ENTITY, new EntityArmorAndHeld());
        p_188276_0_.func_188256_a(FixTypes.BLOCK_ENTITY, new SignStrictJSON());
        p_188276_0_.func_188256_a(FixTypes.ITEM_INSTANCE, new ItemIntIDToString());
        p_188276_0_.func_188256_a(FixTypes.ITEM_INSTANCE, new PotionItems());
        p_188276_0_.func_188256_a(FixTypes.ITEM_INSTANCE, new SpawnEggNames());
        p_188276_0_.func_188256_a(FixTypes.ENTITY, new MinecartEntityTypes());
        p_188276_0_.func_188256_a(FixTypes.BLOCK_ENTITY, new SpawnerEntityTypes());
        p_188276_0_.func_188256_a(FixTypes.ENTITY, new StringToUUID());
        p_188276_0_.func_188256_a(FixTypes.ENTITY, new EntityHealth());
        p_188276_0_.func_188256_a(FixTypes.ENTITY, new HorseSaddle());
        p_188276_0_.func_188256_a(FixTypes.ENTITY, new PaintingDirection());
        p_188276_0_.func_188256_a(FixTypes.ENTITY, new RedundantChanceTags());
        p_188276_0_.func_188256_a(FixTypes.ENTITY, new RidingToPassengers());
        p_188276_0_.func_188256_a(FixTypes.ENTITY, new ArmorStandSilent());
        p_188276_0_.func_188256_a(FixTypes.ITEM_INSTANCE, new BookPagesStrictJSON());
        p_188276_0_.func_188256_a(FixTypes.ITEM_INSTANCE, new CookedFishIDTypo());
        p_188276_0_.func_188256_a(FixTypes.ENTITY, new ZombieProfToType());
        p_188276_0_.func_188256_a(FixTypes.OPTIONS, new ForceVBOOn());
        p_188276_0_.func_188256_a(FixTypes.ENTITY, new ElderGuardianSplit());
        p_188276_0_.func_188256_a(FixTypes.ENTITY, new SkeletonSplit());
        p_188276_0_.func_188256_a(FixTypes.ENTITY, new ZombieSplit());
        p_188276_0_.func_188256_a(FixTypes.ENTITY, new HorseSplit());
        p_188276_0_.func_188256_a(FixTypes.BLOCK_ENTITY, new TileEntityId());
        p_188276_0_.func_188256_a(FixTypes.ENTITY, new EntityId());
        p_188276_0_.func_188256_a(FixTypes.ITEM_INSTANCE, new BannerItemColor());
        p_188276_0_.func_188256_a(FixTypes.ITEM_INSTANCE, new PotionWater());
        p_188276_0_.func_188256_a(FixTypes.ENTITY, new ShulkerBoxEntityColor());
        p_188276_0_.func_188256_a(FixTypes.ITEM_INSTANCE, new ShulkerBoxItemColor());
        p_188276_0_.func_188256_a(FixTypes.BLOCK_ENTITY, new ShulkerBoxTileColor());
        p_188276_0_.func_188256_a(FixTypes.OPTIONS, new OptionsLowerCaseLanguage());
        p_188276_0_.func_188256_a(FixTypes.ITEM_INSTANCE, new TotemItemRename());
        p_188276_0_.func_188256_a(FixTypes.CHUNK, new AddBedTileEntity());
        p_188276_0_.func_188256_a(FixTypes.ITEM_INSTANCE, new BedItemColor());
    }

    public static DataFixer createFixer()
    {
        DataFixer datafixer = new DataFixer(1343);
        datafixer = new net.minecraftforge.common.util.CompoundDataFixer(datafixer);
        WorldInfo.func_189967_a(datafixer);
        EntityPlayerMP.func_191522_a(datafixer);
        EntityPlayer.func_189806_a(datafixer);
        AnvilChunkLoader.func_189889_a(datafixer);
        ItemStack.func_189868_a(datafixer);
        Template.func_191158_a(datafixer);
        Entity.func_190533_a(datafixer);
        EntityArmorStand.func_189805_a(datafixer);
        EntityArrow.func_189658_a(datafixer);
        EntityBat.func_189754_b(datafixer);
        EntityBlaze.func_189761_b(datafixer);
        EntityCaveSpider.func_189775_b(datafixer);
        EntityChicken.func_189789_b(datafixer);
        EntityCow.func_189790_b(datafixer);
        EntityCreeper.func_189762_b(datafixer);
        EntityDonkey.func_190699_b(datafixer);
        EntityDragonFireball.func_189747_a(datafixer);
        EntityElderGuardian.func_190768_b(datafixer);
        EntityDragon.func_189755_b(datafixer);
        EntityEnderman.func_189763_b(datafixer);
        EntityEndermite.func_189764_b(datafixer);
        EntityEvoker.func_190759_b(datafixer);
        EntityFallingBlock.func_189741_a(datafixer);
        EntityFireworkRocket.func_189656_a(datafixer);
        EntityGhast.func_189756_b(datafixer);
        EntityGiantZombie.func_189765_b(datafixer);
        EntityGuardian.func_189766_b(datafixer);
        EntityHorse.func_189803_b(datafixer);
        EntityHusk.func_190740_b(datafixer);
        EntityItem.func_189742_a(datafixer);
        EntityItemFrame.func_189738_a(datafixer);
        EntityLargeFireball.func_189744_a(datafixer);
        EntityMagmaCube.func_189759_b(datafixer);
        EntityMinecartChest.func_189681_a(datafixer);
        EntityMinecartCommandBlock.func_189670_a(datafixer);
        EntityMinecartFurnace.func_189671_a(datafixer);
        EntityMinecartHopper.func_189682_a(datafixer);
        EntityMinecartEmpty.func_189673_a(datafixer);
        EntityMinecartMobSpawner.func_189672_a(datafixer);
        EntityMinecartTNT.func_189674_a(datafixer);
        EntityMule.func_190700_b(datafixer);
        EntityMooshroom.func_189791_c(datafixer);
        EntityOcelot.func_189787_b(datafixer);
        EntityPig.func_189792_b(datafixer);
        EntityPigZombie.func_189781_b(datafixer);
        EntityRabbit.func_189801_b(datafixer);
        EntitySheep.func_189802_b(datafixer);
        EntityShulker.func_189757_b(datafixer);
        EntitySilverfish.func_189767_b(datafixer);
        EntitySkeleton.func_189772_b(datafixer);
        EntitySkeletonHorse.func_190692_b(datafixer);
        EntitySlime.func_189758_c(datafixer);
        EntitySmallFireball.func_189745_a(datafixer);
        EntitySnowman.func_189783_b(datafixer);
        EntitySnowball.func_189662_a(datafixer);
        EntitySpectralArrow.func_189659_b(datafixer);
        EntitySpider.func_189774_d(datafixer);
        EntitySquid.func_189804_b(datafixer);
        EntityStray.func_190728_b(datafixer);
        EntityEgg.func_189664_a(datafixer);
        EntityEnderPearl.func_189663_a(datafixer);
        EntityExpBottle.func_189666_a(datafixer);
        EntityPotion.func_189665_a(datafixer);
        EntityTippedArrow.func_189660_b(datafixer);
        EntityVex.func_190663_b(datafixer);
        EntityVillager.func_189785_b(datafixer);
        EntityIronGolem.func_189784_b(datafixer);
        EntityVindicator.func_190641_b(datafixer);
        EntityWitch.func_189776_b(datafixer);
        EntityWither.func_189782_b(datafixer);
        EntityWitherSkeleton.func_190729_b(datafixer);
        EntityWitherSkull.func_189746_a(datafixer);
        EntityWolf.func_189788_b(datafixer);
        EntityZombie.func_189779_d(datafixer);
        EntityZombieHorse.func_190693_b(datafixer);
        EntityZombieVillager.func_190737_b(datafixer);
        TileEntityPiston.func_189685_a(datafixer);
        TileEntityFlowerPot.func_189699_a(datafixer);
        TileEntityFurnace.func_189676_a(datafixer);
        TileEntityChest.func_189677_a(datafixer);
        TileEntityDispenser.func_189678_a(datafixer);
        TileEntityDropper.func_189679_b(datafixer);
        TileEntityBrewingStand.func_189675_a(datafixer);
        TileEntityHopper.func_189683_a(datafixer);
        BlockJukebox.func_189873_a(datafixer);
        TileEntityMobSpawner.func_189684_a(datafixer);
        TileEntityShulkerBox.func_190593_a(datafixer);
        func_188276_a(datafixer);
        return datafixer;
    }

    public static NBTTagCompound func_188277_a(IDataFixer p_188277_0_, NBTTagCompound p_188277_1_, int p_188277_2_, String p_188277_3_)
    {
        if (p_188277_1_.contains(p_188277_3_, 10))
        {
            p_188277_1_.put(p_188277_3_, p_188277_0_.func_188251_a(FixTypes.ITEM_INSTANCE, p_188277_1_.getCompound(p_188277_3_), p_188277_2_));
        }

        return p_188277_1_;
    }

    public static NBTTagCompound func_188278_b(IDataFixer p_188278_0_, NBTTagCompound p_188278_1_, int p_188278_2_, String p_188278_3_)
    {
        if (p_188278_1_.contains(p_188278_3_, 9))
        {
            NBTTagList nbttaglist = p_188278_1_.getList(p_188278_3_, 10);

            for (int i = 0; i < nbttaglist.func_74745_c(); ++i)
            {
                nbttaglist.func_150304_a(i, p_188278_0_.func_188251_a(FixTypes.ITEM_INSTANCE, nbttaglist.getCompound(i), p_188278_2_));
            }
        }

        return p_188278_1_;
    }
}