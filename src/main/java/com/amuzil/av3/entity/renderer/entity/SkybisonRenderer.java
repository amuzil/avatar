package com.amuzil.av3.entity.renderer.entity;

import com.amuzil.av3.entity.mobs.EntitySkybison;
import com.amuzil.av3.entity.model.SkybisonModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkybisonRenderer extends GeoEntityRenderer<EntitySkybison> {
    public SkybisonRenderer(EntityRendererProvider.Context context) {
        super(context, new SkybisonModel());
    }
}