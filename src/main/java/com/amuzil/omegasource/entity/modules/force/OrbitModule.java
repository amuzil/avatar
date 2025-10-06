package com.amuzil.omegasource.entity.modules.force;

import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.AngleTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.api.IForceModule;
import com.amuzil.omegasource.utils.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;


public class OrbitModule implements IForceModule {

public static String id = OrbitModule.class.getSimpleName();

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {
        if (entity.owner() == null) return;

        double orbitRadius = 2.0; // how far from the owner
        double orbitSpeed = 0.5; // in radians per tick (adjust as needed)
        AngleTrait angleTrait = entity.getTrait(Constants.ANGLE, AngleTrait.class);
        if (angleTrait == null) return;

        double angle = angleTrait.getDegrees(); // current angle along the circle

        // Advance the angle
        angle += orbitSpeed;
        // Optionally wrap around
        if (angle > Math.PI * 2)
            angle -= Math.PI * 2;

        // Owner’s position (center)
        Vec3 center = entity.owner().position();

        // Compute offset
        double offsetX = Math.cos(angle) * orbitRadius;
        double offsetZ = Math.sin(angle) * orbitRadius;
        double offsetY = 1.5;  // or some constant height above/below owner if desired

        // New position = center + offset
        double newX = center.x + offsetX;
        double newY = center.y + offsetY;
        double newZ = center.z + offsetZ;

        angleTrait.setDegrees(angle);
        entity.setPos(newX, newY, newZ);
    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putString("ID", id);
    }

    @Override
    public void load(CompoundTag nbt) {
        this.id = nbt.getString("ID");
    }
}
