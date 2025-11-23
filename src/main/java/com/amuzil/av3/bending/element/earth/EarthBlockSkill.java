package com.amuzil.av3.bending.element.earth;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.skill.EarthSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.construct.AvatarRigidBlock;
import com.amuzil.av3.utils.Constants;
import com.amuzil.magus.skill.data.SkillData;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.magus.skill.traits.skilltraits.StringTrait;
import com.amuzil.magus.skill.traits.skilltraits.TimedTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static com.amuzil.av3.bending.form.BendingForms.BLOCK;
import static com.amuzil.av3.utils.bending.SkillHelper.canEarthBend;
import static com.amuzil.av3.utils.bending.SkillHelper.getRightPivot;


public class EarthBlockSkill extends EarthSkill {

    public EarthBlockSkill() {
        super(Avatar.MOD_ID, "earth_block");
        addTrait(new StringTrait(Constants.FX, "earth_block"));
        addTrait(new TimedTrait(Constants.LIFETIME, 500));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));

        this.startPaths = SkillPathBuilder.getInstance()
                .add(BLOCK)
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

        int lifetime = data.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
        double size = data.getTrait(Constants.SIZE, SizeTrait.class).getSize();

        AvatarRigidBlock rigidBlock = new AvatarRigidBlock(level);
        rigidBlock.setElement(element());
        rigidBlock.setFX(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());
        rigidBlock.setBlockState(blockState);
        rigidBlock.setPos(getRightPivot(entity, 1.0f));
        rigidBlock.getRigidBody().setMass(0f);
        rigidBlock.getRigidBody().setKinematic(true);
        rigidBlock.setOwner(entity);
        rigidBlock.getRigidBody().prioritize((Player) entity);
        rigidBlock.setMaxLifetime(lifetime);
        rigidBlock.setWidth((float) size);
        rigidBlock.setHeight((float) size);
        rigidBlock.setDamageable(false);
        rigidBlock.setControlled(true);

        rigidBlock.init();

        bender.formPath.clear();
        data.setSkillState(SkillState.IDLE);

        bender.getSelection().addEntityId(rigidBlock.getUUID());
        entity.level().addFreshEntity(rigidBlock);
    }
}
