package com.amuzil.av3.bending.element.earth;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.skill.EarthSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.api.ICollisionModule;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.collision.EarthCollisionModule;
import com.amuzil.av3.entity.api.modules.collision.SimpleDamageModule;
import com.amuzil.av3.entity.api.modules.collision.SimpleKnockbackModule;
import com.amuzil.av3.entity.api.modules.entity.TimeoutModule;
import com.amuzil.av3.entity.construct.AvatarRigidBlock;
import com.amuzil.av3.network.AvatarNetwork;
import com.amuzil.av3.network.packets.client.TriggerFXPacket;
import com.amuzil.av3.utils.Constants;
import com.amuzil.magus.skill.data.SkillData;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.skilltraits.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Set;
import java.util.UUID;

import static com.amuzil.av3.bending.form.BendingForms.STRIKE;


public class EarthTossSkill extends EarthSkill {

    public EarthTossSkill() {
        super(Avatar.MOD_ID, "earth_toss");
        addTrait(new StringTrait(Constants.FX, name()));
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

        Set<UUID> entityIds = bender.getSelection().entityIds();
        if (entityIds.isEmpty()) {
            bender.formPath.clear();
            bender.resetSelection();
            data.setSkillState(SkillState.IDLE);
            return;
        }

        ResourceLocation id = Avatar.id(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());

        for (UUID entityId: entityIds) {

            if (level.getEntity(entityId) instanceof AvatarRigidBlock rigidBlock) {
                rigidBlock.setKinematic(false);
//                rigidBlock.getRigidBody().setGravity(Vector3f.ZERO);
//                rigidBlock.getRigidBody().setProtectGravity(true);
                rigidBlock.getRigidBody().setAngularFactor(1f);
                rigidBlock.getRigidBody().prioritize(null);
                rigidBlock.setOwner(entity);
                rigidBlock.setControlled(false);

                rigidBlock.addTraits(data.getTrait(Constants.KNOCKBACK, KnockbackTrait.class));
                rigidBlock.addTraits(new DirectionTrait(Constants.KNOCKBACK_DIRECTION, new Vec3(0, 0.45, 0)));
                rigidBlock.addModule(ModuleRegistry.create(SimpleKnockbackModule.id));
                rigidBlock.addTraits(data.getTrait(Constants.DAMAGE, DamageTrait.class));
                rigidBlock.addModule(ModuleRegistry.create(SimpleDamageModule.id));
                rigidBlock.addModule(ModuleRegistry.create(TimeoutModule.id));
                rigidBlock.addTraits(new SizeTrait(Constants.SIZE, (float) rigidBlock.getSize().getSize()));
                rigidBlock.addTraits(new CollisionTrait(Constants.COLLISION_TYPE, "Blaze", "Fireball", "AbstractArrow", "FireProjectile"));
                rigidBlock.addCollisionModule((ICollisionModule) ModuleRegistry.create(EarthCollisionModule.id));

                rigidBlock.shoot(entity.position().add(0, entity.getEyeHeight(), 0), entity.getLookAngle(), speed, 0);

                AvatarNetwork.sendToClient(new TriggerFXPacket(id, rigidBlock.getId()), (ServerPlayer) bender.getEntity());
            }
        }

        bender.formPath.clear();
        bender.resetSelection();
        data.setSkillState(SkillState.IDLE);
    }
}