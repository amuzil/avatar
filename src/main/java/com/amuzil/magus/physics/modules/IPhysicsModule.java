package com.amuzil.magus.physics.modules;

import com.amuzil.magus.physics.core.IPhysicsElement;

public interface IPhysicsModule {

    void preSolve(IPhysicsElement element);

    void solve(IPhysicsElement element);

    void postSolve(IPhysicsElement element);

}
