package com.amuzil.magus.physics.modules;

import com.amuzil.magus.physics.core.ForcePhysicsElement;

public class LifetimeModule implements IPhysicsModule{
    @Override
    public void preSolve(ForcePhysicsElement element) {
//        if (element instanceof PhysicsElement physics) {
//            if (physics.maxLife() > 0)
//                physics.timeExisted() += 1;
//            if (physics.maxLife() <= physics.timeExisted())
//                physics.se
//        }
    }

    @Override
    public void solve(ForcePhysicsElement element) {

    }

    @Override
    public void postSolve(ForcePhysicsElement element) {

    }
}
