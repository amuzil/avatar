package com.amuzil.av3.entity.model;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.entity.mobs.SkyBisonEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SkyBisonModel extends DefaultedEntityGeoModel<SkyBisonEntity> {
    public SkyBisonModel() {
        super(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "skybison"));
    }
}