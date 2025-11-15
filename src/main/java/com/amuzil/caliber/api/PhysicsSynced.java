package com.amuzil.caliber.api;

import com.amuzil.caliber.api.elements.PhysicsElement;
import com.amuzil.caliber.physics.utils.maths.Frame;

public interface PhysicsSynced {
    boolean isPositionDirty();
    boolean arePropertiesDirty();
    boolean isActive();
    PhysicsElement<?> getElement();
    boolean isRigid();
    Frame getFrame();

    void setPropertiesDirty(boolean dirtyProperties);
}
