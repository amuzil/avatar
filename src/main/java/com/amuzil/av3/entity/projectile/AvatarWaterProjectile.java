package com.amuzil.av3.entity.projectile;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.api.IForceModule;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.force.CurveModule;
import com.amuzil.av3.entity.api.modules.force.MoveModule;
import com.amuzil.av3.renderer.sdf.IHasSDF;
import com.amuzil.av3.renderer.sdf.SDFScene;
import com.amuzil.av3.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.renderer.sdf.channels.Channels;
import com.amuzil.av3.renderer.sdf.shapes.SDFCapsule;
import com.amuzil.av3.renderer.sdf.shapes.SDFCylinder;
import com.amuzil.av3.renderer.sdf.shapes.SDFSphere;
import com.amuzil.av3.renderer.sdf.shapes.SDFTorus;
import com.amuzil.av3.utils.Constants;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class AvatarWaterProjectile extends AvatarProjectile implements IHasSDF {
    private SDFScene root;

    public AvatarWaterProjectile(EntityType<AvatarWaterProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
//        addForceModule((IForceModule) ModuleRegistry.create(CurveModule.id)); // Can hotswap to OrbitModule.id
        addForceModule((IForceModule) ModuleRegistry.create(MoveModule.id));
        SDFCylinder core = new SDFCylinder();
        core.radius = Channels.constant(0.5f);
//        core.radius = Channels.pulse(2f, 0.15f, 0.35f, 0f); // gentle breathing
//        core.a.pos = (t,out)->out.set(0,0,0);
        core.a.rot = (t,out)->out.identity(); // optional slow spin visual
//        core.a.scl = (t,out)->out.set(1,1,1);

        SDFTorus ring = new SDFTorus();
        ring.majorRadius = Channels.constant(2f);
        ring.minorRadius = Channels.constant(0.5f);
        ring.a.pos = (t,out)->out.set(0,0,0);
        ring.a.rot = (t,out)->out.identity();
        ring.a.scl = (t,out)->out.set(1,1,1);
//
//        SDFSphere moon = new SDFSphere();
//        moon.radius = Channels.constant(0.45f);
//        moon.a.pos = Channels.orbitXZ(new Vector3f(0,0,0), 1.7f, 0.2f); // orbit radius 1.7, 0.2 Hz
//        moon.a.rot = (t,out)->out.identity();
//        moon.a.scl = (t,out)->out.set(1,1,1);

        root = new SDFScene().add(core).add(ring);//.add(moon);
        root.unionK = 0.65f;
    }

    public AvatarWaterProjectile(Level pLevel) {
        this(AvatarEntities.AVATAR_WATER_PROJECTILE_ENTITY_TYPE.get(), pLevel);
    }

    @Override
    public void setDeltaMovement(Vec3 deltaMovement) {
        super.setDeltaMovement(deltaMovement);
    }

    @Override
    public Vec3 getDeltaMovement() {
//        return Vec3.ZERO;
        return super.getDeltaMovement();
    }

    public SignedDistanceFunction rootSDF(){ return root; }

}
