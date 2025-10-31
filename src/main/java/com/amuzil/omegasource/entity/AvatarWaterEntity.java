package com.amuzil.omegasource.entity;

import com.amuzil.omegasource.entity.renderer.sdf.SDFScene;
import com.amuzil.omegasource.entity.renderer.sdf.SignedDistanceFunction;
import com.amuzil.omegasource.entity.renderer.sdf.channels.Channels;
import com.amuzil.omegasource.entity.renderer.sdf.shapes.SDFSphere;
import com.amuzil.omegasource.entity.renderer.sdf.shapes.SDFTorus;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class AvatarWaterEntity extends AvatarEntity implements IHasSDF {
    private SDFScene root;

    public AvatarWaterEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        SDFSphere core = new SDFSphere();
        core.radius = Channels.pulse(1.2f, 0.15f, 0.35f, 0f); // gentle breathing
        core.a.pos = (t,out)->out.set(0,0,0);
        core.a.rot = Channels.spinY(4f); // optional slow spin visual
        core.a.scl = (t,out)->out.set(1,1,1);

        SDFTorus ring = new SDFTorus();
        ring.majorRadius = Channels.constant(2f);
        ring.minorRadius = Channels.constant(0.25f);
        ring.a.pos = (t,out)->out.set(0,0,0);
        ring.a.rot = (t,out)->out.identity();
        ring.a.scl = (t,out)->out.set(1,1,1);

        SDFSphere moon = new SDFSphere();
        moon.radius = Channels.constant(0.45f);
        moon.a.pos = Channels.orbitXZ(new Vector3f(0,0,0), 1.7f, 0.2f); // orbit radius 1.7, 0.2 Hz
        moon.a.rot = (t,out)->out.identity();
        moon.a.scl = (t,out)->out.set(1,1,1);

        root = new SDFScene().add(core).add(ring).add(moon);
        root.unionK = 0.35f;
    }
    public SignedDistanceFunction rootSDF(){ return root; }

}
