package net.thedragonskull.mobessencemod.util;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import static net.thedragonskull.mobessencemod.util.TotemUtils.hasTotemWithEssenceServer;

public class CommonAbilityUtils {

    // CAT & OCELOT
    public static void onCatLand(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!(TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:ocelot")) ||
                TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:cat")))) return;

        DamageSource source = event.getSource();

        if (source.is(DamageTypes.FALL) && player.isCrouching()) {
            event.setAmount(event.getAmount() * 0.5F);
        }
    }

    public static void onCreeperTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Creeper)) return;
        if (!(event.getNewTarget() instanceof Player player)) return;

        if (TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:ocelot")) ||
                TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:cat"))) {
            event.setNewTarget(null);
        }
    }

    // FOX & SNOW FOX
    public static void onFoxLoot(LivingDropsEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof ServerPlayer player)) return;

        if (!(hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:fox")) ||
                hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:snow_fox")))) return;

        Level world = player.level();
        if (world.isDay()) return;

        if (player.getRandom().nextFloat() < 0.25F) {
            for (ItemEntity drop : event.getDrops()) {
                ItemStack extra = drop.getItem().copy();
                world.addFreshEntity(new ItemEntity(world, drop.getX(), drop.getY(), drop.getZ(), extra));
            }
        }
    }

    // POLAR BEAR, STRAY & SNOW GOLEM
    public static void onFreezingHurt(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!(hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:polar_bear")) ||
                hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:stray")) ||
                hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:snow_golem")))) return;

        DamageSource source = event.getSource();

        if (source.is(DamageTypes.FREEZE)) {
            event.setCanceled(true);
        }
    }

    // SKELETON, SKELETON HORSE & STRAY
    public static void onArrowLoose(ArrowLooseEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        if (TotemUtils.hasTotemWithEssenceServer(serverPlayer, ResourceLocation.parse("minecraft:skeleton")) ||
                TotemUtils.hasTotemWithEssenceServer(serverPlayer, ResourceLocation.parse("minecraft:skeleton_horse")) ||
                TotemUtils.hasTotemWithEssenceServer(serverPlayer, ResourceLocation.parse("minecraft:stray"))) {

            int charge = event.getCharge();
            int boostedCharge = (int)(charge * 1.75F);
            event.setCharge(boostedCharge);
        }
    }

    // PILLAGER, EVOKER, & VINDICATOR
    public static void onRavagerTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Ravager)) return;
        if (!(event.getNewTarget() instanceof Player player)) return;

        if (TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:pillager")) ||
                TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:evoker")) ||
                TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:vindicator"))) {
            event.setNewTarget(null);
        }
    }

    // HOGLIN & ZOGLIN
    public static void onHoglinKnockback(LivingKnockBackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!(hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:hoglin")) ||
                hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:zoglin")))) return;

        float reducedStrength = event.getStrength() * 0.6F;
        event.setStrength(reducedStrength);
    }

    public static void onHoglinAttack(LivingAttackEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        if (!(hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:hoglin")) ||
                hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:zoglin")))) return;

        if (event.getSource().is(DamageTypeTags.IS_PROJECTILE)) return;

        if (player.level().getRandom().nextInt(3) == 0) {
            LivingEntity target = event.getEntity();

            double resistance = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            double factor = Math.max(0.0, 1.0 - resistance);

            player.getServer().execute(() -> {
                Vec3 motion = target.getDeltaMovement().add(0, 0.3 * factor, 0);
                target.setDeltaMovement(motion);
                target.hurtMarked = true;

                player.level().playSound(null, player.blockPosition(), SoundEvents.HOGLIN_ATTACK, SoundSource.PLAYERS, 1.0F, 1.0F);
            });
        }
    }

    // DONKEY & MULE
    public static void onItemPickup(EntityItemPickupEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!(hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:donkey")) ||
                hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:mule")))) return;

        ItemStack offhand = player.getOffhandItem();
        if (!(offhand.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock)) return;

        ItemEntity itemEntity = event.getItem();
        ItemStack pickedUp = itemEntity.getItem().copy();

        if (pickedUp.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock) {
            return;
        }

        // --- load inventory ---
        CompoundTag blockEntityTag = BlockItem.getBlockEntityData(offhand);
        if (blockEntityTag == null) blockEntityTag = new CompoundTag();

        NonNullList<ItemStack> virtualInv = NonNullList.withSize(27, ItemStack.EMPTY);
        if (blockEntityTag.contains("Items", Tag.TAG_LIST)) {
            ContainerHelper.loadAllItems(blockEntityTag, virtualInv);
        }

        // --- try insert ---
        boolean changed = false;
        for (int i = 0; i < virtualInv.size() && !pickedUp.isEmpty(); i++) {
            ItemStack stack = virtualInv.get(i);

            if (stack.isEmpty()) {
                virtualInv.set(i, pickedUp.copy());
                pickedUp.setCount(0);
                changed = true;
            } else if (ItemStack.isSameItemSameTags(stack, pickedUp)) {
                int canMove = Math.min(pickedUp.getCount(), stack.getMaxStackSize() - stack.getCount());
                if (canMove > 0) {
                    stack.grow(canMove);
                    pickedUp.shrink(canMove);
                    changed = true;
                }
            }
        }

        if (changed) {
            CompoundTag newTag = new CompoundTag();
            ContainerHelper.saveAllItems(newTag, virtualInv);
            BlockItem.setBlockEntityData(offhand, BlockEntityType.SHULKER_BOX, newTag);

            if (pickedUp.isEmpty()) {
                itemEntity.remove(Entity.RemovalReason.DISCARDED);
            } else {
                itemEntity.getItem().setCount(pickedUp.getCount());
            }

            event.setCanceled(true);
        }
    }

}
