package com.amuzil.av3.entity.api.modules;

import com.amuzil.av3.bending.element.Element;
import com.amuzil.magus.physics.core.ForceCloud;
import net.minecraft.world.entity.Entity;

public interface IAvatarController {

    ForceCloud forceCloud();

    void setForceCloud(ForceCloud cloud);

    // Methods are here for clarity but anythingt hat extends AvatarEntity will contain them.

    Entity owner();
    void setOwner(Entity entity);

    Element element();
    void setElement(Element element);

    String skillId();
    void setSkillId(String skillId);
}
