package com.amuzil.magus.physics.modules;

import com.amuzil.magus.physics.core.IPhysicsElement;

public class MoveModule implements IPhysicsModule {
    @Override
    public void preSolve(IPhysicsElement element) {

    }

    @Override
    public void solve(IPhysicsElement element) {
        // Inserts a new position
        element.insert(element.pos().add(element.vel().scale(1 / 20f)), 0);
    }

    @Override
    public void postSolve(IPhysicsElement element) {

    }
}
