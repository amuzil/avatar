package com.amuzil.magus.physics.modules;

import com.amuzil.magus.physics.core.IForceElement;

public class MoveModule implements IPhysicsModule {
    @Override
    public void preSolve(IForceElement element) {

    }

    @Override
    public void solve(IForceElement element) {
        // Inserts a new position
        element.insert(element.pos().add(element.vel().scale(1 / 20f)), 0);
    }

    @Override
    public void postSolve(IForceElement element) {

    }
}
