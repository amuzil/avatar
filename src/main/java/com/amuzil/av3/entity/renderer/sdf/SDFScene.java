package com.amuzil.av3.entity.renderer.sdf;

import com.amuzil.av3.entity.renderer.sdf.operators.SDFSmoothUnion;
import org.joml.Vector3f;

import java.util.ArrayList;

public class SDFScene implements SignedDistanceFunction {
    private final ArrayList<SignedDistanceFunction> nodes = new ArrayList<>();
    public float unionK = 0.4f; // smoothness

    public SDFScene add(SignedDistanceFunction s){ nodes.add(s); return this; }

    @Override
    public float sd(Vector3f pWorld, float t) {
        if (nodes.isEmpty()) return 1e9f;
        SignedDistanceFunction d = nodes.get(0);
//        float d = nodes.get(0).sd(pWorld, t);
        for (int i=1;i<nodes.size();i++) {
            SignedDistanceFunction di = nodes.get(i);
//            float di = nodes.get(i).sd(pWorld, t);
            d = new SDFSmoothUnion(d, di, unionK);
        }
        return d.sd(pWorld, t);
    }
}
