package com.amuzil.omegasource.entity.modules.collision;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.DamageTrait;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.modules.IEntityModule;
import com.amuzil.omegasource.utils.HitDetection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class SimpleDamageModule implements IEntityModule {

    String id = "simple_damage";

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {
        List<LivingEntity> targets = HitDetection.getEntitiesWithinBox(entity, 0.75f, hit ->
                        hit != entity.owner() && (!(hit instanceof AvatarEntity) || ((AvatarEntity) hit).owner() != entity.owner())
                , LivingEntity.class);
        DamageTrait dmg = entity.getTrait("damage", DamageTrait.class);
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
