package com.amuzil.av3.entity.modules.force;

import com.amuzil.av3.api.magus.skill.traits.skilltraits.AngleTrait;
import com.amuzil.av3.api.magus.skill.traits.skilltraits.RangeTrait;
import com.amuzil.av3.api.magus.skill.traits.skilltraits.SpeedTrait;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IForceModule;
import com.amuzil.av3.utils.Constants;
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

        AngleTrait angleTrait = entity.getTrait(Constants.ANGLE, AngleTrait.class);
        SpeedTrait speedTrait = entity.getTrait(Constants.SPEED, SpeedTrait.class);
        RangeTrait rangeTrait = entity.getTrait(Constants.RANGE, RangeTrait.class);
        if (angleTrait == null || speedTrait == null || rangeTrait == null)  {
//            Avatar.LOGGER.warn("OrbitModule missing required traits on entity " + entity);
            return;
        }
        double angle = angleTrait.getDegrees(); // current angle along the circle (start at 0)
        double orbitSpeed = speedTrait.getSpeed(); // in radians per tick (ex: 0.5)
        double orbitRadius = rangeTrait.getRange(); // how far from the owner (ex: 2.0)

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
        id = nbt.getString("ID");
    }
}
