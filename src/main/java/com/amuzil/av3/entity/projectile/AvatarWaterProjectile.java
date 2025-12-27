package com.amuzil.av3.entity.projectile;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.api.IForceModule;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.force.MoveModule;
import com.amuzil.av3.renderer.sdf.IHasSDF;
import com.amuzil.av3.renderer.sdf.SDFScene;
import com.amuzil.av3.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.renderer.sdf.channels.Channels;
import com.amuzil.av3.renderer.sdf.channels.IVec3Channel;
import com.amuzil.av3.renderer.sdf.shapes.SDFCapsule;
import com.amuzil.av3.renderer.sdf.shapes.SDFSphere;
import com.amuzil.av3.renderer.sdf.shapes.SDFWaterBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.units.qual.C;
import org.joml.Vector3f;

public class AvatarWaterProjectile extends AvatarProjectile implements IHasSDF {
    private SDFScene root;
    private SDFWaterBolt capsule;
    private SDFSphere sphere;

    public AvatarWaterProjectile(EntityType<AvatarWaterProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
//        addForceModule((IForceModule) ModuleRegistry.create(CurveModule.id)); // Can hotswap to OrbitModule.id
        addForceModule((IForceModule) ModuleRegistry.create(MoveModule.id));


        IVec3Channel look = Channels.constantVec3(lookDirection().x, lookDirection().y, lookDirection().z);

        SDFCapsule core = new SDFCapsule();
        core.radius = Channels.constant(0.5f);
//        core.radius = Channels.pulse(2f, 0.15f, 0.35f, 0f); // gentle breathing
//        core.a.pos = (t,out)->out.set(0,0,0);
        core.a.rot = Channels.mul(
                Channels.alignAxisToDir(new Vector3f(0, 1, 0), look),   // aim
                Channels.rollAroundLook(look, 0f)                   // twist
        ); // optional slow spin visual
//        core.a.scl = (t,out)->out.set(1,1,1);

//        SDFTorus ring = new SDFTorus();
//        ring.majorRadius = Channels.constant(0f);
//        ring.minorRadius = Channels.constant(0f);
//        ring.a.pos = (t,out)->out.set(0,0,0);
//        ring.a.rot = (t,out)->out.identity();
//        ring.a.scl = (t,out)->out.set(1,1,1);
//
//        SDFSphere moon = new SDFSphere();
//        moon.radius = Channels.constant(0.45f);
//        moon.a.pos = Channels.orbitXZ(new Vector3f(0,0,0), 1.7f, 0.2f); // orbit radius 1.7, 0.2 Hz
//        moon.a.rot = (t,out)->out.identity();
//        moon.a.scl = (t,out)->out.set(1,1,1);

        root = new SDFScene().add(core);//.add(ring);//.add(moon);
        root.unionK = 0.65f;

    }

    public AvatarWaterProjectile(Level pLevel) {
        this(AvatarEntities.AVATAR_WATER_PROJECTILE_ENTITY_TYPE.get(), pLevel);
    }

    @Override
    public Vec3 getDeltaMovement() {
//        return Vec3.ZERO;
        return super.getDeltaMovement();
    }

    @Override
    public void setDeltaMovement(Vec3 deltaMovement) {
        super.setDeltaMovement(deltaMovement);
    }

    public SignedDistanceFunction rootSDF() {
        float size = 4;
        IVec3Channel look = Channels.constantVec3((float) getDeltaMovement().x,
                (float) getDeltaMovement().y, (float) getDeltaMovement().z);
        IVec3Channel scale = Channels.constantVec3(lookDirection().x * size / 4, lookDirection().y * size / 4, lookDirection().z * size / 4);
        if (capsule == null) {
            capsule = new SDFWaterBolt();
        }
        capsule.headRadius = Channels.constant(0.5f);
        capsule.tailRadius = Channels.constant(0.012f);
        capsule.length = Channels.constant(size);;
        capsule.taperExponent = Channels.constant(2.2f);
        capsule.headTipBlend = Channels.constant(0.3f);
        capsule.headTipLength = Channels.constant(0.4f);

        capsule.a.rot = Channels.alignAxisToDir(
                new Vector3f(0, 1, 0),                  // capsule axis in *local* space
                look  // world-space direction
        );
        if (sphere == null) {
            sphere = new SDFSphere();
        }
        sphere.radius = Channels.constant(0.5f);
        sphere.a.pos = scale;
        root = new SDFScene().add(capsule);//.add(sphere);
        root.unionK = 0.9f;
        return root;
    }


    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick() {
        super.tick();
//        if (!getLookAngle().toVector3f().equals(lookDirection())) {
//            Vec2 rots = VectorUtils.dirToRotations(lookDirection());
//            setXRot(rots.x);
//            setYRot(rots.y);
//        }
    }
}
