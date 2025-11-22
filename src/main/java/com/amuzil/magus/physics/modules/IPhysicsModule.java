package com.amuzil.magus.physics.modules;

import com.amuzil.magus.physics.core.ForcePhysicsElement;

public interface IPhysicsModule {

    void preSolve(ForcePhysicsElement element);

    void solve(ForcePhysicsElement element);

    void postSolve(ForcePhysicsElement element);

}
