package com.amuzil.av3.entity.renderer.sdf;

import com.amuzil.av3.entity.renderer.PointData;

public class SdfShapeRecord {
    public SignedDistanceFunction function;
    public PointData[][][] sdfField;

    public SdfShapeRecord(SignedDistanceFunction function) {
        this.function = function;
    }
}
