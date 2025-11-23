package com.amuzil.av3.bending.element.earth;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.skill.EarthSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.collision.SimpleDamageModule;
import com.amuzil.av3.entity.api.modules.collision.SimpleKnockbackModule;
import com.amuzil.av3.entity.construct.AvatarRigidBlock;
import com.amuzil.av3.utils.Constants;
import com.amuzil.magus.skill.data.SkillData;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.skilltraits.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

import static com.amuzil.av3.bending.form.BendingForms.STRIKE;


public class EarthTossSkill extends EarthSkill {

    public EarthTossSkill() {
        super(Avatar.MOD_ID, "earth_toss");
        addTrait(new StringTrait(Constants.FX, "earth_toss"));
        addTrait(new TimedTrait(Constants.LIFETIME, 100));
        addTrait(new DamageTrait(Constants.DAMAGE, 4.5f));
        addTrait(new SpeedTrait(Constants.SPEED, 2.5d));
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 1.5f));

        this.startPaths = SkillPathBuilder.getInstance()
                .add(STRIKE)
                .build();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);
        LivingEntity entity = bender.getEntity();
        ServerLevel level = (ServerLevel) bender.getEntity().level();
        SkillData data = bender.getSkillData(this);

        int lifetime = data.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
        double speed = data.getTrait(Constants.SPEED, SpeedTrait.class).getSpeed();

        List<UUID> entityIds = bender.getSelection().entityIds();
        if (entityIds.isEmpty()) {
            bender.formPath.clear();
            bender.getSelection().reset();
            data.setSkillState(SkillState.IDLE);
            return;
        }

        // TODO: Fix bug with exponential force of mach 2 by making BendingSelection a Data Attachment
        for (UUID entityId: entityIds) {
            if (level.getEntity(entityId) instanceof AvatarRigidBlock rigidBlock) {
                rigidBlock.setFX(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());
                rigidBlock.setKinematic(false);
//                rigidBlock.getRigidBody().setGravity(Vector3f.ZERO);
//                rigidBlock.getRigidBody().setProtectGravity(true);
                rigidBlock.setOwner(entity);
                rigidBlock.setControlled(false);

                rigidBlock.addTraits(data.getTrait(Constants.KNOCKBACK, KnockbackTrait.class));
                rigidBlock.addTraits(new DirectionTrait(Constants.KNOCKBACK_DIRECTION, new Vec3(0, 0.45, 0)));
                rigidBlock.addModule(ModuleRegistry.create(SimpleKnockbackModule.id));
                rigidBlock.addTraits(data.getTrait(Constants.DAMAGE, DamageTrait.class));
                rigidBlock.addModule(ModuleRegistry.create(SimpleDamageModule.id));

                rigidBlock.shoot(entity.position().add(0, entity.getEyeHeight(), 0), entity.getLookAngle(), speed, 0);
            }
        }

        bender.formPath.clear();
        bender.getSelection().reset();
        data.setSkillState(SkillState.IDLE);
    }
}