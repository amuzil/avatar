package com.amuzil.magus.physics.modules;

import com.amuzil.magus.physics.core.ForcePhysicsElement;

public class MoveModule implements IPhysicsModule {
    @Override
    public void preSolve(ForcePhysicsElement element) {

    }

    @Override
    public void solve(ForcePhysicsElement element) {
        // Inserts a new position
        element.insert(element.pos().add(element.vel().scale(1 / 20f)), 0);
    }

    @Override
    public void postSolve(ForcePhysicsElement element) {

    }
}
