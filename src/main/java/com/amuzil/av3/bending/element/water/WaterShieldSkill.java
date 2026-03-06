package com.amuzil.av3.bending.element.water;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Elements;
import com.amuzil.av3.bending.skill.WaterSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.force.ControlModule;
import com.amuzil.av3.entity.projectile.AvatarWaterBoundProjectile;
import com.amuzil.av3.utils.Constants;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.skilltraits.FloatTrait;
import com.amuzil.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.magus.skill.traits.skilltraits.StringTrait;
import com.amuzil.magus.skill.traits.skilltraits.TimedTrait;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;

import static com.amuzil.av3.bending.form.BendingForms.BLOCK;

// Creates a shield of water around the player that can be used to block incoming attacks. The shield will have a certain amount of health and will break after
// taking enough damage.
// The shield can also be used to block projectiles, and will have a cooldown before it can be used again.
public class WaterShieldSkill extends WaterSkill {

    public WaterShieldSkill() {
        super(Avatar.MOD_ID, "water_shield");
        addTrait(new SizeTrait(Constants.SIZE, 0.75f));
        addTrait(new FloatTrait(Constants.HEALTH, 5.0f));
        addTrait(new TimedTrait(Constants.LIFETIME,  120));
        addTrait(new TimedTrait(Constants.COOLDOWN, 200));
        addTrait(new StringTrait(Constants.FX, "water_shield"));

        startPaths = SkillPathBuilder.getInstance().add(BLOCK).build();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);
        Level level = bender.getEntity().level();
        AvatarWaterBoundProjectile shield = new AvatarWaterBoundProjectile(level);
        shield.setPos(bender.getEntity().getX(), bender.getEntity().getY() + bender.getEntity().getBbHeight() / 2, bender.getEntity().getZ());
        shield.maxHealth(skillData.getTrait(Constants.HEALTH, FloatTrait.class).getValue());
        shield.health(skillData.getTrait(Constants.HEALTH, FloatTrait.class).getValue());
        shield.addModule(ModuleRegistry.create(ControlModule.id));
        shield.setOwner(bender.getEntity());
        shield.setElement(Elements.WATER);
        shield.setCollidable(true);
        // Need to figure out depth, width, and height
        shield.setWidth((float) skillData.getTrait(Constants.SIZE, SizeTrait.class).getSize());
        shield.setHeight((float) skillData.getTrait(Constants.SIZE, SizeTrait.class).getSize());

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
