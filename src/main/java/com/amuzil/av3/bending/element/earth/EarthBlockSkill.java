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

import static com.amuzil.av3.bending.form.BendingForms.BLOCK;
import static com.amuzil.av3.utils.bending.SkillHelper.canEarthBend;


public class EarthBlockSkill extends EarthSkill {

    public EarthBlockSkill() {
        super(Avatar.MOD_ID, "earth_block");
        addTrait(new StringTrait(Constants.FX, "earth_block"));
        addTrait(new TimedTrait(Constants.LIFETIME, 500));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));
        addTrait(new TimedTrait(Constants.MAX_RUNTIME, 3));

        this.startPaths = SkillPathBuilder.getInstance()
                .add(BLOCK)
                .build();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);
        LivingEntity entity = bender.getEntity();
        int blockCount = bender.getSelection().entityIds().size();
        int maxBlockCount = skillData.getTrait(Constants.MAX_RUNTIME, TimedTrait.class).getTime();
        if (!canEarthBend(entity)) return; // Can't earth bend if too far from ground
        if (blockCount >= maxBlockCount) return; // Don't go past maxBlockCount
        Level level = bender.getEntity().level();
        SkillData data = bender.getSkillData(this);
        BlockPos blockPos = bender.getEntity().blockPosition().below();
        BlockState blockState = level.getBlockState(blockPos);
        if (!BendingMaterial.isBendable(blockState, element())) return;

        int lifetime = data.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
        double size = data.getTrait(Constants.SIZE, SizeTrait.class).getSize();

        AvatarRigidBlock rigidBlock = RigidBlockFactory.createKinematicBlock(level, blockState, entity, blockCount, lifetime, (float) size);
        rigidBlock.setElement(element());
        rigidBlock.setFX(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());
        rigidBlock.init();
        // Earth Pillar (long boi)
//        MinecraftShape.Compound compoundShape = MinecraftShape.compound(null);
//        CollisionShape shape = rigidBlock.getRigidBody().getCollisionShape();
//        compoundShape.addChildShape(shape, 0, -1, 0);
//        compoundShape.addChildShape(shape, 0,  0, 0);
//        compoundShape.addChildShape(shape, 0,  1, 0);
//        AABB box = Convert.toMinecraft(compoundShape.boundingBox(new Vector3f(), new Quaternion(), new BoundingBox()));
//        rigidBlock.setWidth((float) box.getXsize());
//        rigidBlock.setHeight((float) box.getYsize());
//        rigidBlock.setDepth((float) box.getZsize());
//        rigidBlock.getRigidBody().setCollisionShape(compoundShape);

        bender.formPath.clear();
        data.setSkillState(SkillState.IDLE);
        bender.getSelection().addEntityId(rigidBlock.getUUID());

        entity.level().addFreshEntity(rigidBlock);
    }
}
