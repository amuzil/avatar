package com.amuzil.av3.bending.element.water;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Elements;
import com.amuzil.av3.bending.form.BendingForms;
import com.amuzil.av3.bending.skill.WaterSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.force.ControlModule;
import com.amuzil.av3.entity.projectile.AvatarWaterRing;
import com.amuzil.av3.entity.projectile.AvatarWaterShield;
import com.amuzil.av3.utils.Constants;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.skilltraits.FloatTrait;
import com.amuzil.magus.skill.traits.skilltraits.SizeTrait;
import net.minecraft.server.level.ServerLevel;

import java.util.Set;
import java.util.UUID;

/**
 * Makes a consumable ring of water around the player that can be used to source other water skills - primarily the ball, stream, and arc skills.
 */
public class WaterRingSkill extends WaterSkill {

    public WaterRingSkill() {
        super(Avatar.MOD_ID, "water_ring");
        addTrait(new FloatTrait(Constants.SOURCE_LEVEL, 5.0f));
        addTrait(new SizeTrait(Constants.SIZE, 2.5f));

        // Shape
        startPaths = SkillPathBuilder.getInstance().add(BendingForms.COMPRESS).build();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);
        ServerLevel level = (ServerLevel) bender.getEntity().level();

        float sourceLevel = skillData.getTrait(Constants.SOURCE_LEVEL, FloatTrait.class).getValue();
        // Check for existing source level based on water ring
        Set<UUID> entityIds = bender.getSelection().entityIds();

        if (!entityIds.isEmpty()) {
            for (UUID uuid : entityIds) {
                if (level.getEntity(uuid) instanceof AvatarWaterShield shield) {
                    sourceLevel = shield.sourceLevel();
                    shield.kill();
                    break;
                }
            }
        }
        AvatarWaterRing ring = new AvatarWaterRing(level);

        ring.maxSource(skillData.getTrait(Constants.SOURCE_LEVEL, FloatTrait.class).getValue());
        ring.sourceLevel(sourceLevel);

        ring.setOwner(bender.getEntity());
        ring.setElement(Elements.WATER);

        // Need to figure out depth, width, and height
        ring.setWidth((float) skillData.getTrait(Constants.SIZE, SizeTrait.class).getSize());
        ring.setHeight((float) skillData.getTrait(Constants.SIZE, SizeTrait.class).getSize() / 8);
        ring.setDepth((float) skillData.getTrait(Constants.SIZE, SizeTrait.class).getSize());

        ring.setPos(bender.getEntity().getX(), bender.getEntity().getY() + bender.getEntity().getBbHeight() / 2, bender.getEntity().getZ());
        ring.lookDirection(bender.getEntity().getLookAngle().toVector3f());
        ring.setControlled(true);
        ring.setNoGravity(true);
        ring.addModule(ModuleRegistry.create(ControlModule.id));

        bender.getSelection().addEntityId(ring.getUUID());
        level.addFreshEntity(ring);
    }

    @Override
    public void run(Bender bender) {
        super.run(bender);
    }

    @Override
    public void stop(Bender bender) {
        super.stop(bender);
    }
}
