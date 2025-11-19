package com.amuzil.av3.entity.api.modules.collision;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.ICollisionModule;
import com.amuzil.av3.utils.Constants;
import com.amuzil.av3.utils.modules.HitDetection;
import com.amuzil.magus.skill.traits.skilltraits.DamageTrait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;


public class SimpleDamageModule implements ICollisionModule {

    public static String id = SimpleDamageModule.class.getSimpleName();

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {
        List<LivingEntity> targets = HitDetection.getEntitiesWithinBox(entity, 0.75f,
                hit -> hit != entity.owner() && (!(hit instanceof AvatarEntity) || ((AvatarEntity) hit).owner() != entity.owner()),
                LivingEntity.class);
        DamageTrait dmg = entity.getTrait(Constants.DAMAGE, DamageTrait.class);
        if (dmg == null) {
            Avatar.LOGGER.warn("No damage trait set for SimpleDamage module. Please remove the module or add the trait to the entity.");
            return;
        }
        float damage = (float) dmg.getDamage();
        for (LivingEntity hit : targets) {
           hit.hurt(entity.damageSources().dragonBreath(), damage);
        }
    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putString("ID", id);
    }

    @Override
    public void load(CompoundTag nbt) {
        id = nbt.getString("ID");
    }
}
