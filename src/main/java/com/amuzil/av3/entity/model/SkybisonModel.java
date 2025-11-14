package com.amuzil.av3.entity.model;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.entity.mobs.EntitySkybison;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SkybisonModel extends DefaultedEntityGeoModel<EntitySkybison> {
    public SkybisonModel() {
        super(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "skybison"));
    }
}