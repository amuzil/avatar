package com.amuzil.av3.bending.element.earth;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.skill.EarthSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.construct.AvatarRigidBlock;
import com.amuzil.av3.utils.Constants;
import com.amuzil.av3.utils.bending.BendingMaterial;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.amuzil.magus.skill.data.SkillData;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.magus.skill.traits.skilltraits.StringTrait;
import com.amuzil.magus.skill.traits.skilltraits.TimedTrait;
import com.jme3.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static com.amuzil.av3.bending.form.BendingForms.RAISE;
import static com.amuzil.av3.utils.bending.SkillHelper.canEarthBend;
import static com.amuzil.av3.utils.bending.SkillHelper.getPivot;


public class EarthWallSkill extends EarthSkill {
    // TODO: Create Earth Wall Skill that adjusts shape based on player's look angle
    public EarthWallSkill() {
        super(Avatar.MOD_ID, "earth_wall");
        addTrait(new StringTrait(Constants.FX, "earth_wall"));
        addTrait(new TimedTrait(Constants.LIFETIME, 500));
        addTrait(new SizeTrait(Constants.SIZE, 3.0f));

        this.startPaths = SkillPathBuilder.getInstance()
                .add(RAISE)
                .build();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);
        LivingEntity entity = bender.getEntity();
        if (!canEarthBend(entity)) return; // Can't earth bend if too far from ground
        Level level = bender.getEntity().level();
        SkillData data = bender.getSkillData(this);
        BlockPos blockPos = bender.getEntity().blockPosition().below();
        BlockState blockState = level.getBlockState(blockPos);
        if (!BendingMaterial.isBendable(blockState, element())) return;

        int lifetime = data.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
        double size = data.getTrait(Constants.SIZE, SizeTrait.class).getSize();

        AvatarRigidBlock rigidBlock = new AvatarRigidBlock(level);
        rigidBlock.setElement(element());
        rigidBlock.setFX(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());
        rigidBlock.setBlockState(blockState);
        rigidBlock.getRigidBody().setPhysicsRotation(Convert.toBullet(0, entity.getYRot()));
        rigidBlock.setPos(getPivot(entity, 3f)); // TODO make it spawn at set y position
        rigidBlock.getRigidBody().setMass(90f);
        rigidBlock.getRigidBody().setFriction(0.05f);
        rigidBlock.getRigidBody().setAngularFactor(0f);
        rigidBlock.getRigidBody().setAngularVelocity(new Vector3f(0f, 0f, 0f));
        rigidBlock.setOwner(entity);
//        rigidBlock.setMaxLifetime(lifetime);
        rigidBlock.setWidth((float) size);
        rigidBlock.setHeight((float) size);
        rigidBlock.setDamageable(false);
        rigidBlock.setRigidBodyDirty(true);

        rigidBlock.init();

        bender.formPath.clear();
        data.setSkillState(SkillState.IDLE);

        bender.getSelection().addEntityId(rigidBlock.getUUID());
        entity.level().addFreshEntity(rigidBlock);
    }
}
