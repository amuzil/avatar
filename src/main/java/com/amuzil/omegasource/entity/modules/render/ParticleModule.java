package com.amuzil.omegasource.entity.modules.render;

import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.BooleanTrait;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.modules.IRenderModule;
import com.amuzil.omegasource.utils.Constants;
import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.minecraft.nbt.CompoundTag;


public class ParticleModule implements IRenderModule {
    String id = "particle";

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {
    }

    @Override
    public void tick(AvatarEntity entity) {
        // For starting effects per tick
        BooleanTrait booleanTrait = entity.getTrait(Constants.ONE_SHOT, BooleanTrait.class);
        if (entity.level().isClientSide && entity.fxLocation() != null) {
            FX fx = FXHelper.getFX(entity.fxLocation());
            if (fx != null) {
                EntityEffect entityEffect = new EntityEffect(fx, entity.level(), entity);
                entityEffect.start();
            }
        }
    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putString("ID", id);
    }

    @Override
    public void load(CompoundTag nbt) {
        this.id = nbt.getString("ID");
    }
}
