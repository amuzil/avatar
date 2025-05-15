package com.amuzil.omegasource.utils.physics.core;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ForceCloud extends PhysicsElement {

    private List<ForcePoint> points;

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

    // We only want coopies passed
    public void writeHeader() {
        for (ForcePoint point : points())
            System.arraycopy(header, 0, point.header, 0, header.length);
    }
}
