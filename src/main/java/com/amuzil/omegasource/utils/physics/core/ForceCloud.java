package com.amuzil.omegasource.utils.physics.core;

import com.amuzil.omegasource.utils.physics.modules.IPhysicsModule;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ForceCloud extends PhysicsElement {

    private List<ForcePoint> points;

    private Vec3[] normals;

    private Vec3 rotation;

    private List<IPhysicsModule> modules;


    public ForceCloud(int type) {
        super(type);
        this.points = new ArrayList<>();
    }

    public void addPoints(ForcePoint... points) {
        this.points.addAll(List.of(points));
    };

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

    // We only want copies passed
    public void writeHeader() {
        for (ForcePoint point : points())
            System.arraycopy(header, 0, point.header, 0, header.length);
    }

    public void tick() {
        modules.forEach(IPhysicsModule::preSolve);
        modules.forEach(IPhysicsModule::solve);
        modules.forEach(IPhysicsModule::postSolve);
    }
}
