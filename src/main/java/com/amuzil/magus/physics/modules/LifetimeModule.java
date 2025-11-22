package com.amuzil.magus.physics.modules;

import com.amuzil.magus.physics.core.IForceElement;

public class LifetimeModule implements IPhysicsModule{
    @Override
    public void preSolve(IForceElement element) {
//        if (element instanceof PhysicsElement physics) {
//            if (physics.maxLife() > 0)
//                physics.timeExisted() += 1;
//            if (physics.maxLife() <= physics.timeExisted())
//                physics.se
//        }
    }

    @Override
    public void solve(IForceElement element) {

    }

    @Override
    public void postSolve(IForceElement element) {

    }
}
