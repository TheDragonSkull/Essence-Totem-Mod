package net.thedragonskull.mobessencemod.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class IllusionDecoyEntity extends ArmorStand {
    private int hitCounter = 0;
    private int lifetimeTicks = 0;

    public IllusionDecoyEntity(EntityType<? extends ArmorStand> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.setInvisible(false);
        this.setInvulnerable(false);
        this.setNoGravity(false);
        this.setCustomName(Component.literal("Substitute"));
        this.setCustomNameVisible(false);
        this.setSilent(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ArmorStand.createLivingAttributes();
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            lifetimeTicks++;
            if (lifetimeTicks >= 600) {
                triggerIllusion(true);
            }
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        triggerIllusion(false);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        hitCounter++;
        if (hitCounter >= 3) {
            triggerIllusion(true);
        }
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("IllusionHits", this.hitCounter);
        tag.putInt("IllusionLifetime", this.lifetimeTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("IllusionHits")) {
            this.hitCounter = tag.getInt("IllusionHits");
        }
        if (tag.contains("IllusionLifetime")) {
            this.lifetimeTicks = tag.getInt("IllusionLifetime");
        }
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        return InteractionResult.FAIL;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean canBeSeenByAnyone() {
        return true;
    }

    @Override
    public boolean shouldDropLoot() {
        return false;
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected void dropAllDeathLoot(DamageSource source) {
    }

    public static ItemStack createPlayerHead(ServerPlayer player) {
        ItemStack head = new ItemStack(Items.PLAYER_HEAD);
        CompoundTag tag = head.getOrCreateTag();
        CompoundTag skullOwner = new CompoundTag();
        skullOwner.putString("Name", player.getGameProfile().getName());
        tag.put("SkullOwner", skullOwner);
        head.setTag(tag);
        return head;
    }

    private void triggerIllusion(boolean remove) {
        if (level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 3; i++) {
                serverLevel.sendParticles(
                        ParticleTypes.POOF,
                        this.getX(),
                        this.getY() + 1.0,
                        this.getZ(),
                        10,
                        0.3, 0.5, 0.3,
                        0.01
                );
            }

            this.level().playSound(null, this.blockPosition(), SoundEvents.ILLUSIONER_PREPARE_MIRROR, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        if (remove) {
            this.discard();
        }
    }
}
