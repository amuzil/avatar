package com.amuzil.omegasource.utils.physics.constraints;

import com.amuzil.omegasource.utils.physics.core.ForceCloud;
import com.amuzil.omegasource.utils.physics.core.ForcePoint;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ConstraintManager {

    /**
     * Initial 10 constriants
     *
     * @param cloud
     */
    public static void enforceConstraints(ForceCloud cloud) {
        enforcePointPointDistance(cloud);
        enforcePointEdgeTriangleDistance(cloud);
        enforceEdgeEdgeDistance(cloud);
        enforceVolumePreservation(cloud);
        enforceShapeMatching(cloud);
        enforceDihedralBending(cloud);
        enforceIsometricStrain(cloud);
        enforceDensityIncompressibility(cloud);
        enforceViscosityShear(cloud);
        enforceContactCollision(cloud);
    }

    private static void enforcePointPointDistance(ForceCloud cloud) {
        if (!cloud.constraint(Constraints.ConstraintType.POINT_POINT_DISTANCE)) return;
        List<ForcePoint> pts = cloud.points();
        for (int i = 0; i < pts.size() - 1; i++) {
            ForcePoint a = pts.get(i);
            ForcePoint b = pts.get(i + 1);
            Vec3 pa = a.pos();
            Vec3 pb = b.pos();
            // TODO: apply PBD distance correction between pa and pb
        }
    }

    private static void enforcePointEdgeTriangleDistance(ForceCloud cloud) {
        if (!cloud.constraint(Constraints.ConstraintType.POINT_EDGE_TRIANGLE_DISTANCE)) return;
        // TODO: implement point-edge/triangle constraint
    }

    private static void enforceEdgeEdgeDistance(ForceCloud cloud) {
        if (!cloud.constraint(Constraints.ConstraintType.EDGE_EDGE_DISTANCE)) return;
        // TODO: implement edge-edge distance constraint
    }

    private static void enforceVolumePreservation(ForceCloud cloud) {
        if (!cloud.constraint(Constraints.ConstraintType.VOLUME_PRESERVATION)) return;
        // TODO: implement volume preservation across the cloud
    }

    private static void enforceShapeMatching(ForceCloud cloud) {
        if (!cloud.constraint(Constraints.ConstraintType.SHAPE_MATCHING)) return;
        // TODO: implement shape matching against rest positions
    }

    private static void enforceDihedralBending(ForceCloud cloud) {
        if (!cloud.constraint(Constraints.ConstraintType.DIHEDRAL_BENDING)) return;
        // TODO: implement dihedral/bending constraint for adjacent faces
    }

    private static void enforceIsometricStrain(ForceCloud cloud) {
        if (!cloud.constraint(Constraints.ConstraintType.ISOMETRIC_STRAIN)) return;
        // TODO: implement isometric strain constraint
    }

    private static void enforceDensityIncompressibility(ForceCloud cloud) {
        if (!cloud.constraint(Constraints.ConstraintType.DENSITY_INCOMPRESSIBILITY)) return;
        // TODO: implement density/incompressibility constraint for fluids
    }

    private static void enforceViscosityShear(ForceCloud cloud) {
        if (!cloud.constraint(Constraints.ConstraintType.VISCOSITY_SHEAR)) return;
        // TODO: implement viscosity/shear forces between neighbors
    }

    private static void enforceContactCollision(ForceCloud cloud) {
        if (!cloud.constraint(Constraints.ConstraintType.CONTACT_COLLISION)) return;
        // TODO: implement collision resolution with world geometry
    }
}
