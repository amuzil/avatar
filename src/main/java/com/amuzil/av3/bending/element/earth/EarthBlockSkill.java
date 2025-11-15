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
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static com.amuzil.av3.bending.form.BendingForms.BLOCK;


public class EarthBlockSkill extends EarthSkill {

    public EarthBlockSkill() {
        super(Avatar.MOD_ID, "earth_block");
        addTrait(new DamageTrait(Constants.DAMAGE, 3.5f));
        addTrait(new TimedTrait(Constants.LIFETIME, 100)); // Ticks not seconds...
        addTrait(new SpeedTrait(Constants.SPEED, 5d));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 1.5f));

        this.startPaths = SkillPathBuilder.getInstance()
                .add(BLOCK)
                .build();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);

        LivingEntity entity = bender.getEntity();
        Level level = bender.getEntity().level();
        SkillData data = bender.getSkillData(this);
        BlockPos blockPos = bender.getEntity().blockPosition().below();
        BlockState blockState = level.getBlockState(blockPos);

        int lifetime = data.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
        double speed = data.getTrait(Constants.SPEED, SpeedTrait.class).getSpeed();
        double size = data.getTrait(Constants.SIZE, SizeTrait.class).getSize();

        AvatarRigidBlock rigidBlock = new AvatarRigidBlock(level);
        rigidBlock.setBlockState(blockState);
        rigidBlock.setPos(entity.position().add(0, entity.getEyeHeight(), 0));
        rigidBlock.getPhysicsBody().setMass(0f);
        rigidBlock.getPhysicsBody().setKinematic(true);
        rigidBlock.setElement(element());
        rigidBlock.setOwner(entity);
        rigidBlock.setMaxLifetime(lifetime);
        rigidBlock.setWidth((float) size);
        rigidBlock.setHeight((float) size);
//        projectile.setNoGravity(true);
        rigidBlock.setDamageable(false);
        rigidBlock.setHittable(true);

        rigidBlock.addTraits(data.getTrait(Constants.KNOCKBACK, KnockbackTrait.class));
        rigidBlock.addTraits(new DirectionTrait(Constants.KNOCKBACK_DIRECTION, new Vec3(0, 0.45, 0)));
        rigidBlock.addModule(ModuleRegistry.create(SimpleKnockbackModule.id));

        // Damage module
        rigidBlock.addTraits(data.getTrait(Constants.DAMAGE, DamageTrait.class));
        rigidBlock.addTraits(data.getTrait(Constants.SIZE, SizeTrait.class));
        rigidBlock.addModule(ModuleRegistry.create(SimpleDamageModule.id));
        rigidBlock.addTraits(new CollisionTrait(Constants.COLLISION_TYPE, "Blaze", "Fireball", "AbstractArrow", "FireProjectile"));
//        projectile.addCollisionModule((ICollisionModule) ModuleRegistry.create(AirCollisionModule.id));

        rigidBlock.shoot(entity.position().add(0, entity.getEyeHeight(), 0), entity.getLookAngle(), speed, 0);
        rigidBlock.init();

        bender.formPath.clear();
        data.setSkillState(SkillState.IDLE);

        bender.getEntity().level().addFreshEntity(rigidBlock);
    }

//    @Override
//    public void run(Bender bender) {
//        super.run(bender);
//    }
//
//    @Override
//    public void stop(Bender bender) {
//        super.stop(bender);
//    }
}
