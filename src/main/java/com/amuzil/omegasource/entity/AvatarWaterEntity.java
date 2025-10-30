package com.amuzil.omegasource.entity;

import com.amuzil.omegasource.entity.renderer.sdf.SignedDistanceFunction;
import com.amuzil.omegasource.entity.renderer.sdf.operators.SDFSmoothUnion;
import com.amuzil.omegasource.entity.renderer.sdf.operators.SDFSubtract;
import com.amuzil.omegasource.entity.renderer.sdf.shapes.SDFSphere;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class AvatarWaterEntity extends AvatarEntity implements IHasSDF {
    private SignedDistanceFunction root;
    public AvatarWaterEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
    public SignedDistanceFunction rootSDF(){ return root; }
}
