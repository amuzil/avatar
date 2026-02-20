package com.amuzil.av3.bending.element.earth;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.skill.EarthSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.construct.AvatarRigidBlock;
import com.amuzil.av3.utils.Constants;
import com.amuzil.av3.utils.bending.BendingMaterial;
import com.amuzil.av3.utils.bending.RigidBlockFactory;
import com.amuzil.magus.skill.data.SkillData;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.magus.skill.traits.skilltraits.StringTrait;
import com.amuzil.magus.skill.traits.skilltraits.TimedTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static com.amuzil.av3.bending.form.BendingForms.RAISE;
import static com.amuzil.av3.utils.bending.SkillHelper.canEarthBend;


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

        AvatarRigidBlock rigidBlock = RigidBlockFactory.createWall(level, blockState, entity, lifetime, (float) size);
        rigidBlock.setElement(element());
        rigidBlock.setFX(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());
        rigidBlock.init();

        bender.formPath.clear();
        data.setSkillState(SkillState.IDLE);

        bender.getSelection().addEntityId(rigidBlock.getUUID());
        entity.level().addFreshEntity(rigidBlock);
    }
}
