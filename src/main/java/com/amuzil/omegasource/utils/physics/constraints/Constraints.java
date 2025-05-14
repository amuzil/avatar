package com.amuzil.omegasource.utils.physics.constraints;

public class Constraints {

    /**
     * Header byte layout:
     * index 0 = element type (0=gas,1=water,2=solid,...)
     * indices 1+ = bit-packed constraint flags (unlimited flags across multiple bytes)
     */
    public static final int HEADER_LENGTH = 1 + ((ConstraintType.values().length + 7) / 8);
    public static final int TYPE_INDEX = 0;
    public static final int FLAG_OFFSET = 1;

    /**
     * All supported constraint types, each assigned a unique bit index.
     */
    public enum ConstraintType {
        POINT_POINT_DISTANCE(0),
        POINT_EDGE_TRIANGLE_DISTANCE(1),
        EDGE_EDGE_DISTANCE(2),
        VOLUME_PRESERVATION(3),
        SHAPE_MATCHING(4),
        DIHEDRAL_BENDING(5),
        ISOMETRIC_STRAIN(6),
        DENSITY_INCOMPRESSIBILITY(7),
        VISCOSITY_SHEAR(8),
        CONTACT_COLLISION(9),
        HINGE_BALL_JOINT(10),
        ANGULAR_REST_ANGLE(11),
        MOTOR_TARGET_VELOCITY(12),
        PLANAR_CONSTRAINT(13),
        VOLUME_EXPANSION_INFLATABLE(14),
        STRETCH_SHEAR_COSSERAT(15),
        SHAPE_DEPENDENT_FIELD(16);

        private final int bitIndex;

        ConstraintType(int bitIndex) {
            this.bitIndex = bitIndex;
        }

        public int bitIndex() {
            return bitIndex;
        }
    }




}
