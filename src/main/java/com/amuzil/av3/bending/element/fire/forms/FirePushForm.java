package com.amuzil.av3.bending.element.fire.forms;

import com.amuzil.av3.Avatar;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.av3.bending.skill.FireForm;
import com.amuzil.av3.capability.Bender;
import com.amuzil.av3.entity.api.ICollisionModule;
import com.amuzil.av3.entity.modules.ModuleRegistry;
import com.amuzil.av3.entity.modules.collision.FireEffectModule;
import com.amuzil.av3.entity.projectile.AvatarDirectProjectile;
import com.amuzil.av3.utils.Constants;
import com.amuzil.magus.skill.traits.skilltraits.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import static com.amuzil.av3.bending.form.BendingForms.PUSH;


public class FirePushForm extends FireForm {

    public FirePushForm() {
        super(Avatar.MOD_ID, "fire_push");
        addTrait(new SizeTrait(Constants.SIZE, 1.5F));
        addTrait(new TimedTrait(Constants.LIFETIME, 20));
        addTrait(new SpeedTrait(Constants.SPEED, 1.5d));

        startPaths = SkillPathBuilder.getInstance().add(PUSH).build();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);

        LivingEntity benderEntity = bender.getEntity();
        Level level = benderEntity.level();

        int lifetime = skillData.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
        double speed = skillData.getTrait(Constants.SPEED, SpeedTrait.class).getSpeed();
        double size = skillData.getTrait(Constants.SIZE, SizeTrait.class).getSize();

        AvatarDirectProjectile projectile = new AvatarDirectProjectile(level);
        projectile.setElement(element());
        projectile.setOwner(benderEntity);
        projectile.setMaxLifetime(lifetime);
        projectile.setWidth((float) size);
        projectile.setHeight((float) size);
        projectile.setNoGravity(true);
        projectile.setDamageable(false);
        projectile.setHittable(false);

        projectile.addTraits(new StringTrait(Constants.BENDING_FORM, startPaths.get(0).name()));
        projectile.addTraits(skillData.getTrait(Constants.SIZE, SizeTrait.class));
        projectile.addTraits(new CollisionTrait(Constants.COLLISION_TYPE, "Blaze", "Fireball", "AbstractArrow", "FireProjectile"));
        projectile.addCollisionModule((ICollisionModule) ModuleRegistry.create(FireEffectModule.id));

        projectile.shoot(benderEntity.position().add(0, benderEntity.getEyeHeight(), 0), benderEntity.getLookAngle(), speed, 0);
        projectile.init();

        skillData.setSkillState(SkillState.IDLE);
        bender.getEntity().level().addFreshEntity(projectile);
    }
}
