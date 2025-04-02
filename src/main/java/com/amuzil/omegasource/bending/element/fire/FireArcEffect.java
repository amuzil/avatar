package com.amuzil.omegasource.bending.element.fire;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.capability.entity.Magi;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.radix.RadixTree;
import com.amuzil.omegasource.api.magus.skill.SkillActive;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.utils.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.utils.traits.skilltraits.StringTrait;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.entity.ElementProjectile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import static com.amuzil.omegasource.bending.BendingForms.*;

public class FireArcEffect extends SkillActive {

    public FireArcEffect() {
        super(Avatar.MOD_ID, "fire_arc_effect", Elements.FIRE);
        addTrait(new StringTrait("skill_state", "start"));
    }

    @Override
    public FormPath getStartPaths() {
        return SkillPathBuilder.getInstance()
                .addForm(new ActiveForm(LOWER, true))
                .build();
    }

    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        boolean shouldStart = false;
        Magi magi = Magi.get(entity);
        if (magi != null) {
            SkillData data = magi.getSkillData(this);
            if (data.getState().equals(SkillState.START)) {
                shouldStart = true;
            }
        }

        return super.shouldStart(entity, formPath);// && shouldStart;
    }

    @Override
    public void start(LivingEntity entity) {
        super.start(entity);
//        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED));
//        entity.level().addFreshEntity(new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level()));
        if (!entity.level().isClientSide) {
            ElementProjectile proj;
            proj = ElementProjectile.createElementEntity(STRIKE, Elements.FIRE, (ServerPlayer) entity, (ServerLevel) entity.level());
            assert proj != null;
            proj.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, 1, 1);
            entity.level().addFreshEntity(proj);
            RadixTree.getLogger().debug("Attempting projectile spawn.");
        }

//        for (SkillTrait trait : getTraits()) {
//            if (trait instanceof StringTrait) {
//                if (trait.getName().equals("skill_state"))
//                    ((StringTrait) trait).setInfo("stop");
//            }
//        }

    }

}
