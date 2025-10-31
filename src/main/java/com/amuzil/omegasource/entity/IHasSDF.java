package com.amuzil.omegasource.entity;

import com.amuzil.omegasource.entity.renderer.sdf.SignedDistanceFunction;
import org.joml.Vector3f;

public interface IHasSDF {
    /** Root SDF in entity-local space. Animate inside this impl if you want. */
    SignedDistanceFunction rootSDF();

    /** Optional: material id function, per-point. */
    default int materialAt(Vector3f p){ return 0; }
}
