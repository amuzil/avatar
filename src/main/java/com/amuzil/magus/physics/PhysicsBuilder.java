package com.amuzil.magus.physics;

import com.amuzil.magus.physics.constraints.Constraints;
import com.amuzil.magus.physics.core.ForceCloud;
import com.amuzil.magus.physics.modules.IPhysicsModule;
import net.minecraft.world.phys.Vec3;

public class PhysicsBuilder {
    private static PhysicsBuilder instance;
    int type;
    int points;
    private ForceCloud cloud;
    private int constraints;
    public static final float CELL_SIZE = 0.25f;
    public static final float GRID_SIZE = 32f;

    public static PhysicsBuilder getInstance() {
        if (instance == null) {
            instance = new PhysicsBuilder();
        }
        return instance;
    }

    public PhysicsBuilder start() {
//        cloud = new ForceCloud(0);
        return this;
    }

    public PhysicsBuilder typeCloud(int type) {
        this.type = type;
        cloud.type(type);
        return this;
    }

    public PhysicsBuilder points(int points) {
        this.points = points;
        cloud.addPoints();
        return this;
    }

    public PhysicsBuilder constraints(int constraints) {
        for (Constraints.ConstraintType c : Constraints.ConstraintType.values()) {
            if ((constraints & (1 << c.bitIndex())) != 0) {
                cloud.conOn(c);
            }
        }
        return this;
    }

    public PhysicsBuilder constraints(Constraints.ConstraintType... types) {
        for (Constraints.ConstraintType c : types) {
            cloud.conOn(c);

        }
        return this;
    }

    public PhysicsBuilder modules(IPhysicsModule... modules) {
        cloud.mod(modules);
        return this;
    }

    public PhysicsBuilder pos(Vec3 pos) {
        cloud.insert(pos, 0);
        return this;
    }

    public ForceCloud buildCloud() {
        cloud.writeHeader();
        return cloud;
    }


    public void reset() {
        this.type = -1;
        this.points = -1;
        this.cloud = null;
        this.constraints = -1;
    }
}
