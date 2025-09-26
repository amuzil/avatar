package com.amuzil.omegasource.api.magus.skill.traits.entitytraits;

import com.amuzil.omegasource.api.magus.skill.traits.DataTrait;
import com.amuzil.omegasource.utils.maths.Easings;
import com.amuzil.omegasource.utils.maths.Point;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Generic point holder class. Could be used for anything:
 * Pathing, Bézier curve interpolation, coordinates for spawning objects, data storage, etc
 */
public class PointsTrait implements DataTrait {
    private String name;
    private boolean isDirty = false;

    private final List<Point> points;

    public PointsTrait(String name, Point... points) {
        this.points = List.of(points);
        this.name = name;
    }

    public PointsTrait(String name) {
        this.points = new ArrayList<>();
        this.name = name;
    }

    public PointsTrait(String name, List<Point> points) {
        this.name = name;
        this.points = points;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void markDirty() {
        this.isDirty = true;
    }

    @Override
    public void markClean() {
        this.isDirty = false;
    }

    @Override
    public boolean isDirty() {
        return this.isDirty;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("TraitID", name);
        tag.putBoolean("IsDirty", isDirty);
        ListTag list = new ListTag();
        for (Point p : points) {
            CompoundTag pTag = new CompoundTag();
            pTag.putDouble("x", p.x());
            pTag.putDouble("y", p.y());
            list.add(pTag);
        }
        tag.put("Points", list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.name = nbt.getString("TraitID");
        this.isDirty = nbt.getBoolean("IsDirty");
        this.points.clear();
        ListTag list = nbt.getList("Points", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag pTag = (CompoundTag) t;
            double x = pTag.getDouble("x");
            double y = pTag.getDouble("y");
            points.add(new Point(x, y));
        }
    }

    /**
     * @return unmodifiable view of control points
     */
    public List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public float evaluate(float t) {
        List<Float> xs = new ArrayList<>(), ys = new ArrayList<>();
        for (Point p : points) {
            xs.add((float) p.x());
            ys.add((float) p.y());
        }
        return Easings.bezier(xs, ys, t);
    }

}
