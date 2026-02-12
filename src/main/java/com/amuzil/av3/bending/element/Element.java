package com.amuzil.av3.bending.element;

import com.amuzil.magus.skill.SkillCategory;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;


public abstract class Element extends SkillCategory {
    public static final Codec<Element> CODEC = Codec.STRING.xmap(
                    id -> Elements.get(ResourceLocation.parse(id)), // encode
                    element -> element.getId().toString());       // decode
    public static final StreamCodec<RegistryFriendlyByteBuf, Element> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(Element.CODEC);

    private final String nickName;
    private final Type type;

    public Element(String modId, String name, Type type) {
        super(modId, name);
        this.nickName = name.replace("bending", "");
        this.type = type;
        Elements.ALL_FOUR.put(nickName, this);
    }

    public String nickName() { return nickName; }

    public Type type() {
        return type;
    }

    @Override
    public String toString() { return String.format("Element[ %s ]", name); }

    public enum Type {
        AIR,
        WATER,
        EARTH,
        FIRE;
    }
}
