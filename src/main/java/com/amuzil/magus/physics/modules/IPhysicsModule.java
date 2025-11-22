package com.amuzil.magus.physics.modules;

import com.amuzil.magus.physics.core.IForceElement;

public interface IPhysicsModule {

    void preSolve(IForceElement element);

    void solve(IForceElement element);

    void postSolve(IForceElement element);

}
