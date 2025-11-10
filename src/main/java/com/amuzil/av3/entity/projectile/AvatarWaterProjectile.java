package com.amuzil.av3.entity.projectile;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.IHasSDF;
import com.amuzil.av3.entity.api.IForceModule;
import com.amuzil.av3.entity.modules.ModuleRegistry;
import com.amuzil.av3.entity.modules.force.CurveModule;
import com.amuzil.av3.entity.renderer.sdf.SDFScene;
import com.amuzil.av3.entity.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.entity.renderer.sdf.channels.floats.ConstantFloatChannel;
import com.amuzil.av3.entity.renderer.sdf.channels.floats.PulsingFloatChannel;
import com.amuzil.av3.entity.renderer.sdf.channels.quaternions.SpinYChannel;
import com.amuzil.av3.entity.renderer.sdf.channels.vectors.OrbitXZChannel;
import com.amuzil.av3.entity.renderer.sdf.shapes.SDFSphere;
import com.amuzil.av3.entity.renderer.sdf.shapes.SDFTorus;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class AvatarWaterProjectile extends AvatarProjectile implements IHasSDF {
    private SDFScene root;

    public AvatarWaterProjectile(EntityType<AvatarWaterProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        addForceModule((IForceModule) ModuleRegistry.create(CurveModule.id)); // Can hotswap to OrbitModule.id

        SDFSphere core = new SDFSphere();
        core.radius = new PulsingFloatChannel(1.2f, 0.15f, 0.35f, 0f); // gentle breathing
        core.a.pos = (t,out)->out.set(0,0,0);
        core.a.rot = new SpinYChannel(4f); // optional slow spin visual
        core.a.scl = (t,out)->out.set(1,1,1);

        SDFTorus ring = new SDFTorus();
        ring.majorRadius = new ConstantFloatChannel(2f);
        ring.minorRadius = new ConstantFloatChannel(0.25f);
        ring.a.pos = (t,out)->out.set(0,0,0);
        ring.a.rot = (t,out)->out.identity();
        ring.a.scl = (t,out)->out.set(1,1,1);

        SDFSphere moon = new SDFSphere();
        moon.radius = new ConstantFloatChannel(0.45f);
        moon.a.pos = new OrbitXZChannel(new Vector3f(0,0,0), 1.7f, 0.2f); // orbit radius 1.7, 0.2 Hz
        moon.a.rot = (t,out)->out.identity();
        moon.a.scl = (t,out)->out.set(1,1,1);

        root = new SDFScene().add(core).add(ring).add(moon);
        root.unionK = 0.35f;
    }

    public AvatarWaterProjectile(Level pLevel) {
        this(AvatarEntities.AVATAR_WATER_PROJECTILE_ENTITY_TYPE.get(), pLevel);
    }

    public SignedDistanceFunction rootSDF(){ return root; }

}
