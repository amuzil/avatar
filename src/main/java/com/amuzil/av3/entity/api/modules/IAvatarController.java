package com.amuzil.av3.entity.api.modules;

import com.amuzil.av3.bending.element.Element;
import com.amuzil.magus.physics.core.ForceCloud;
import net.minecraft.world.entity.Entity;

public interface IAvatarController {

    Entity owner();

    void setOwner(Entity entity);

    ForceCloud forceCloud();

    void setForceCloud(ForceCloud cloud);

    String skillId();

    void setSkillId(String skillId);
}
