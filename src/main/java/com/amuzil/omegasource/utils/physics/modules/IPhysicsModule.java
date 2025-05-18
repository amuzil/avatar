package com.amuzil.omegasource.utils.physics.modules;

import com.amuzil.omegasource.utils.physics.core.IPhysicsElement;

public interface IPhysicsModule {

    void preSolve(IPhysicsElement element);

    void solve(IPhysicsElement element);

    void postSolve(IPhysicsElement element);

}
