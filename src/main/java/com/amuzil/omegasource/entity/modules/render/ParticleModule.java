package com.amuzil.omegasource.entity.modules.render;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.BooleanTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.LevelTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.StringTrait;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.modules.IRenderModule;
import com.amuzil.omegasource.utils.Constants;
import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import static com.amuzil.omegasource.Avatar.air_perma;
import static com.amuzil.omegasource.Avatar.fire_bloom_perma;


public class ParticleModule implements IRenderModule {
    String id = "particle";

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {
        Element element = entity.element();
        StringTrait fxName = entity.getTrait(Constants.FX, StringTrait.class);

//        if (entity.level().isClientSide && fxName != null) {
//            FX fx = FXHelper.getFX(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, fxName.getInfo()));
//            if (fx != null) {
//                System.out.println("ParticleModule.init " + entity.getId());
//                EntityEffect entityEffect = new EntityEffect(fx, entity.level(), entity);
//                entityEffect.start();
//            }
//        }
    }

    @Override
    public void tick(AvatarEntity entity) {
        // For starting effects per tick
        Element element = entity.element();
        StringTrait fxName = entity.getTrait(Constants.FX, StringTrait.class);
        BooleanTrait booleanTrait = entity.getTrait(Constants.ONE_SHOT, BooleanTrait.class);
        if (!entity.level().isClientSide && fxName != null) {
//            System.out.println("ParticleModule.tick Server-Side: " + !entity.level().isClientSide);
            FX fx = FXHelper.getFX(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, fxName.getInfo()));
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
