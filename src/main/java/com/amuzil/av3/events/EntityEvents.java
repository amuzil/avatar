package com.amuzil.av3.events;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.mobs.SkyBisonEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = Avatar.MOD_ID)
public class EntityEvents {
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        // Register the attribute supplier for each LivingEntity type you own
        event.put(AvatarEntities.AVATAR_SKYBISON_ENTITY_TYPE.get(), SkyBisonEntity.createAttributes().build());
    }
}
