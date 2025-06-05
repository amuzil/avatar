package com.amuzil.omegasource.entity.modules.render;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.StringTrait;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.form.BendingForms;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.AvatarProjectile;
import com.amuzil.omegasource.entity.modules.IRenderModule;
import com.amuzil.omegasource.utils.Constants;
import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import static com.amuzil.omegasource.Avatar.fire_bloom;


public class ParticleModule implements IRenderModule {
    String id = "particle";

    @Override
    public String id() {
        return "";
    }

    @Override
    public void init(AvatarEntity entity) {
        Element element = entity.element();
        Entity owner = entity.owner();
        StringTrait fxName = entity.getTrait("fx", StringTrait.class);

        if (entity.level().isClientSide && fxName != null) {
            FX fx = FXHelper.getFX(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, fxName.getInfo()));
            if (fx != null) {
                EntityEffect entityEffect = new EntityEffect(fx, owner.level(), owner);
                entityEffect.start();
            }
        }
    }

    @Override
    public void tick(AvatarEntity entity) {
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
