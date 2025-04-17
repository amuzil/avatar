package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.capability.entity.Magi;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.utils.traits.skilltraits.ColourTrait;
import com.amuzil.omegasource.api.magus.skill.utils.traits.skilltraits.DamageTrait;
import com.amuzil.omegasource.api.magus.skill.utils.traits.skilltraits.KnockbackTrait;
import com.amuzil.omegasource.api.magus.skill.utils.traits.skilltraits.SizeTrait;
import com.amuzil.omegasource.bending.BendingForms;
import com.amuzil.omegasource.bending.BendingSkill;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.entity.ElementProjectile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import static com.amuzil.omegasource.bending.BendingForms.STRIKE;

public class FireStrikeSkill extends BendingSkill {

    public FireStrikeSkill() {
        super(Avatar.MOD_ID, "fire_strike", Elements.FIRE);
        addTrait(new DamageTrait(2.5f, "damage"));
        addTrait(new SizeTrait(1.5F, "size"));
        addTrait(new KnockbackTrait(2f, "knockback"));
        addTrait(new ColourTrait(0, 0, 0, "fire_colour"));
    }

    @Override
    public FormPath getStartPaths() {
        return SkillPathBuilder.getInstance().complex(new ActiveForm(STRIKE, false)).build();
    }

    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        return super.shouldStart(entity, formPath);
    }

    @Override
    public void start(LivingEntity entity) {
        super.start(entity);
        ElementProjectile proj;
        if (!entity.level().isClientSide) {
            proj = ElementProjectile.createElementEntity(STRIKE, Elements.FIRE, (ServerPlayer) entity, (ServerLevel) entity.level());
            proj.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, 1, 1);
            entity.level().addFreshEntity(proj);
        }

        Magi magi = Magi.get(entity);
        if (magi != null) {
            magi.formPath.clear();
            SkillData data = magi.getSkillData(this);
            data.setState(SkillState.STOP);

            resetCooldown(data);
        }

    }

    @Override
    public SkillCategory getCategory() {
        return Elements.FIRE;
    }
}
