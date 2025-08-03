package net.thedragonskull.mobessencemod.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

import java.util.List;

public class DecoyAttackGoal<T extends LivingEntity> extends TargetGoal {
    private final Mob mob;
    private final Class<T> targetClass;
    private T target;

    public DecoyAttackGoal(Mob mob, Class<T> targetClass) {
        super(mob, false);
        this.mob = mob;
        this.targetClass = targetClass;
    }

    @Override
    public boolean canUse() {
        List<T> possibleTargets = this.mob.level()
                .getEntitiesOfClass(targetClass, this.mob.getBoundingBox().inflate(16.0D),
                        (entity) -> entity.isAlive() && entity.isAttackable() && this.mob.hasLineOfSight(entity));

        if (!possibleTargets.isEmpty()) {
            this.target = possibleTargets.get(0);
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.target);
        super.start();
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }
}

