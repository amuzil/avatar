package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.*;
import com.amuzil.omegasource.bending.BendingSkill;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.entity.AvatarProjectile;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import static com.amuzil.omegasource.bending.BendingForms.STRIKE;

public class FireStrikeSkill extends BendingSkill {

    public FireStrikeSkill() {
        super(Avatar.MOD_ID, "fire_strike", Elements.FIRE);
        addTrait(new DamageTrait(2.5f, "damage"));
        addTrait(new SizeTrait(0.125F, "size"));
        addTrait(new SizeTrait(0.75f, "max_size"));
        addTrait(new KnockbackTrait(2f, "knockback"));
        addTrait(new ColourTrait(0, 0, 0, "fire_colour"));
        addTrait(new SpeedTrait(0.125f, "speed"));
        addTrait(new TimedTrait(15, "lifetime"));

        startPaths = SkillPathBuilder.getInstance().complex(new ActiveForm(STRIKE, false)).build();

    }


    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        return super.shouldStart(entity, formPath);
    }

    @Override
    public void start(LivingEntity entity) {
        super.start(entity);

        Bender bender = (Bender) Bender.getBender(entity);
        // Resets data so we can test
//        bender.resetData();
        Level level = entity.level();
        SkillData data = bender.getSkillData(this);

        int lifetime = data.getTrait("lifetime", TimedTrait.class).getTime();
        double speed = data.getTrait("speed", SpeedTrait.class).getSpeed();
        double size = data.getTrait("size", SizeTrait.class).getSize();

        AvatarProjectile projectile = new AvatarProjectile(level);
        projectile.setElement(Elements.FIRE);
        projectile.setOwner(entity);
        projectile.setMaxLifetime(lifetime);
        projectile.setWidth((float) size);
        projectile.setHeight((float) size);
        projectile.setNoGravity(true);

        projectile.addTraits(data.getTrait("max_size", SizeTrait.class));

        projectile.addModule(ModuleRegistry.create("Grow"));


        if (!entity.level().isClientSide) {
//            proj = ElementProjectile.createElementEntity(STRIKE, Elements.FIRE, (ServerPlayer) entity, (ServerLevel) entity.level());



            entity.level().addFreshEntity(projectile);
        }

        projectile.shoot(entity.position().add(0, entity.getEyeHeight(), 0), entity.getLookAngle(), speed, 0);
        projectile.init();
        if (bender != null) {
            bender.formPath.clear();
            data.setState(SkillState.STOP);

            resetCooldown(data);
        }
    }



    @Override
    public SkillCategory getCategory() {
        return Elements.FIRE;
    }

}
