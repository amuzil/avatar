package com.amuzil.omegasource.entity.projectile;

import com.amuzil.omegasource.entity.AvatarEntities;
import com.amuzil.omegasource.entity.IHasSDF;
import com.amuzil.omegasource.entity.api.IForceModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.amuzil.omegasource.entity.modules.force.CurveModule;
import com.amuzil.omegasource.entity.renderer.sdf.SignedDistanceFunction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class AvatarWaterProjectile extends AvatarProjectile implements IHasSDF {
    private SignedDistanceFunction root;

    public AvatarWaterProjectile(EntityType<AvatarWaterProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        addForceModule((IForceModule) ModuleRegistry.create(CurveModule.id)); // Can hotswap to OrbitModule.id

//        SignedDistanceFunction body = new SDFSphere(new Vector3f(0,0,0), 1.25f);
//        SignedDistanceFunction dent = new SDFSphere(new Vector3f(1f,0f,0.0f), 1f);
//        SignedDistanceFunction dent2 = new SDFSphere(new Vector3f(1.75f,0f,0.0f), 0.75f);
//        SignedDistanceFunction dent3 = new SDFSphere(new Vector3f(2.25f,0f,0.0f), 0.5f);
//        SignedDistanceFunction dent4 = new SDFSphere(new Vector3f(2.5f,0f,0.0f), 0.25f);
//        root = new SDFSmoothUnion(body, dent, 0.1f);
//        root = new SDFSmoothUnion(root, dent2, 0.1f);
//        root = new SDFSmoothUnion(root, dent3, 0.1f);
//        root = new SDFSmoothUnion(root, dent4, 0.1f);
    }

    public AvatarWaterProjectile(Level pLevel) {
        this(AvatarEntities.AVATAR_WATER_PROJECTILE_ENTITY_TYPE.get(), pLevel);
    }

    public SignedDistanceFunction rootSDF(){ return root; }
}
