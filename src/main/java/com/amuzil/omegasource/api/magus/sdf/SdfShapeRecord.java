package com.amuzil.omegasource.api.magus.sdf;

import com.amuzil.omegasource.entity.renderer.PointData;
import com.amuzil.omegasource.entity.renderer.sdf.SignedDistanceFunction;

public class SdfShapeRecord {
    public SignedDistanceFunction function;
    public PointData[][][] sdfField;
}
