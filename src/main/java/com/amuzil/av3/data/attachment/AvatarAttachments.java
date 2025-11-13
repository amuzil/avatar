package com.amuzil.av3.data.attachment;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Element;
import com.amuzil.av3.bending.element.Elements;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;


public class AvatarAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Avatar.MOD_ID);

    public static final Supplier<AttachmentType<BenderData>> BENDER_DATA = ATTACHMENT_TYPES.register(
            "bender_data", () -> AttachmentType.serializable(BenderData::new)
                    .sync(BenderData.STREAM_CODEC)
                    .copyOnDeath()
                    .build());

    public static final Supplier<AttachmentType<Element>> ACTIVE_ELEMENT = ATTACHMENT_TYPES.register(
            "active_element", () -> AttachmentType.builder(Elements::random)
                    .serialize(Element.CODEC)
                    .sync(Element.STREAM_CODEC)
                    .copyOnDeath()
                    .build());

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
