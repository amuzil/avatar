package com.amuzil.omegasource.utils.physics.core;

import com.amuzil.omegasource.utils.physics.modules.IPhysicsModule;
import org.joml.Quaterniond;

import java.util.ArrayList;
import java.util.List;

public class ForceCloud extends PhysicsElement {

    private final List<ForcePoint> points;

    private double[] rotation;

    private final List<IPhysicsModule> modules;


    public ForceCloud(int type) {
        super(type);
        this.rotation = new double[4];
        this.points = new ArrayList<>();
        this.modules = new ArrayList<>();
    }

    public void addPoints(ForcePoint... points) {
        this.points.addAll(List.of(points));
    }

    public void addPoints(List<ForcePoint> points) {
        this.points.addAll(points);
    }

    public void deletePoints(ForcePoint... points) {
        this.points.removeAll(List.of(points));
    }

    public void deletePoints(List<ForcePoint> points) {
        this.points.removeAll(points);
    }

    public void clear() {
        this.points.clear();
    }

    public List<ForcePoint> pointsCopy() {
        return new ArrayList<>(this.points);
    }

    public List<ForcePoint> points() {
        return this.points;
    }

    public void mod(IPhysicsModule... modules) {
        this.modules.addAll(List.of(modules));
    }

    public List<IPhysicsModule> modules() {
        return this.modules;
    }

    public void rotate(double[] rotation) {
        // De-reference the original array, we do not want to be affecting it.
        this.rotation = rotation.clone();
    }

    public void rotate(Quaterniond rotation) {
        rotate(new double[]{rotation.x, rotation.y, rotation.z, rotation.w});
    }

    public double[] rotation() {
        return this.rotation;
    }

    public Quaterniond rot() {
        return new Quaterniond(rotation[0], rotation[1], rotation[2], rotation[3]);
    }

    // We only want copies passed
    public void writeHeader() {
        for (ForcePoint point : points())
            System.arraycopy(header, 0, point.header, 0, header.length);
    }

    public void tick() {
        for (IPhysicsModule module : modules) {
            module.preSolve(this);
            module.solve(this);
            module.postSolve(this);
        }
    }
}
