package com.amuzil.av3.entity.renderer.entity;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.mobs.EntitySkybison;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber()
public class EntityEvents {
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        // Register the attribute supplier for each LivingEntity type you own
        event.put(AvatarEntities.AVATAR_SKYBISON_ENTITY_TYPE.get(), EntitySkybison.createAttributes().build());
    }
}
