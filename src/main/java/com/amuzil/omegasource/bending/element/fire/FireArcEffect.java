package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.FormPath;
import com.amuzil.omegasource.api.magus.skill.SkillActive;
import com.amuzil.omegasource.api.magus.skill.utils.capability.entity.Magi;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.utils.traits.SkillTrait;
import com.amuzil.omegasource.api.magus.skill.utils.traits.skilltraits.StringTrait;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.bending.form.ActiveForm;
import com.amuzil.omegasource.bending.form.Forms;
import com.amuzil.omegasource.entity.ElementProjectile;
import com.amuzil.omegasource.registry.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import static com.amuzil.omegasource.bending.form.Forms.*;
import static com.amuzil.omegasource.bending.form.Forms.BLOCK;

public class FireArcEffect extends SkillActive {

    public FireArcEffect() {
        super(Avatar.MOD_ID, "fire_arc_effect", Elements.FIRE);
        addTrait(new StringTrait("skill_state", "start"));
    }

    @Override
    public FormPath getStartPaths() {
        return SkillPathBuilder.getInstance()
                .addForm(new ActiveForm(Forms.STRIKE, true))
                .build();
    }

    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        boolean shouldStart = false;
        for (SkillTrait trait : getTraits()) {
            if (trait instanceof StringTrait) {
                if (trait.getName().equals("skill_state"))
                    shouldStart = ((StringTrait) trait).getInfo().equals("start");
            }
        }
        return super.shouldStart(entity, formPath) && shouldStart;
    }

    @Override
    public void start(LivingEntity entity) {
        super.start(entity);
        ElementProjectile projectile;
        if (!entity.level().isClientSide && entity instanceof ServerPlayer) {
                projectile = ElementProjectile.createElementEntity(STRIKE, Elements.FIRE, (ServerPlayer) entity, (ServerLevel) entity.level());
                int entityId = 0;
                assert projectile != null;

                projectile.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, 1, 1);
        } else {
            entity.discard();
            return; // Unhandled Form - Discard and print no effects
        }
        entity.level().addFreshEntity(projectile);
        for (SkillTrait trait : getTraits()) {
            if (trait instanceof StringTrait) {
                if (trait.getName().equals("skill_state"))
                    ((StringTrait) trait).setInfo("stop");
            }
        }

    }

}
