package com.amuzil.av3.bending.element.water;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Elements;
import com.amuzil.av3.bending.skill.WaterSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.force.ControlModule;
import com.amuzil.av3.entity.projectile.AvatarWaterShield;
import com.amuzil.av3.utils.Constants;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.skilltraits.FloatTrait;
import com.amuzil.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.magus.skill.traits.skilltraits.StringTrait;
import com.amuzil.magus.skill.traits.skilltraits.TimedTrait;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;

import static com.amuzil.av3.bending.form.BendingForms.BLOCK;

// Creates a shield of water around the player that can be used to block incoming attacks. The shield will have a certain amount of health and will break after
// taking enough damage.
// The shield can also be used to block projectiles, and will have a cooldown before it can be used again.
public class WaterShieldSkill extends WaterSkill {

    public WaterShieldSkill() {
        super(Avatar.MOD_ID, "water_shield");
        addTrait(new SizeTrait(Constants.SIZE, 1.5f));
        addTrait(new FloatTrait(Constants.HEALTH, 5.0f));
        addTrait(new TimedTrait(Constants.LIFETIME,  120));
        addTrait(new TimedTrait(Constants.COOLDOWN, 200));
        addTrait(new StringTrait(Constants.FX, "water_shield"));
        addTrait(new FloatTrait(Constants.SOURCE_LEVEL, 5.0f));
        // This is how much source to lose per point of damage taken
        addTrait(new FloatTrait(Constants.SOURCE_CONSUMPTION, 0.5f));
        // Multiplier for damage vs source level loss.
        addTrait(new FloatTrait(Constants.RESISTANCE, 1.0f));
        // Blocks this amount of damage before hurting itself.
        addTrait(new FloatTrait(Constants.DAMAGE_REDUCTION, 0.5f));

        startPaths = SkillPathBuilder.getInstance().add(BLOCK).build();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);
        Level level = bender.getEntity().level();

        int sourceLevel;

        AvatarWaterShield shield = new AvatarWaterShield(level);

        shield.maxHealth(skillData.getTrait(Constants.HEALTH, FloatTrait.class).getValue());
        shield.health(skillData.getTrait(Constants.HEALTH, FloatTrait.class).getValue());

        shield.setOwner(bender.getEntity());
        shield.setElement(Elements.WATER);
        shield.setCollidable(true);

        // Need to figure out depth, width, and height
        shield.setWidth((float) skillData.getTrait(Constants.SIZE, SizeTrait.class).getSize());
        shield.setHeight((float) skillData.getTrait(Constants.SIZE, SizeTrait.class).getSize());
        shield.setDepth((float) skillData.getTrait(Constants.SIZE, SizeTrait.class).getSize() / 4);

        shield.setPos(bender.getEntity().getX(), bender.getEntity().getY() + bender.getEntity().getBbHeight() / 2, bender.getEntity().getZ());
        shield.lookDirection(bender.getEntity().getLookAngle().toVector3f());
        shield.setControlled(true);
        shield.setPhysics(false);
        shield.setNoGravity(true);
        shield.addModule(ModuleRegistry.create(ControlModule.id));
        level.addFreshEntity(shield);
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
