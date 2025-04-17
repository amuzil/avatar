package com.amuzil.omegasource.bending;

import com.amuzil.omegasource.api.magus.capability.entity.Data;
import com.amuzil.omegasource.api.magus.capability.entity.Magi;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.element.Elements;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;


public class Bender extends Magi {

    HashMap<String, Element> elements = new HashMap<>();

    public Bender(Data capabilityData, LivingEntity entity) {
        super(capabilityData, entity);
        elements.put(Elements.FIRE.name(), Elements.FIRE);
    }

}
