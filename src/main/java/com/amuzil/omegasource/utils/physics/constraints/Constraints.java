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
     * A majority of these are *not* necessary for server side collision! Especially the advanced constraints.
     */
    public enum ConstraintType {

        /** Simple Constraints **/

        // Fixed length between particles. Essential for ropes, whips, arcs; can be used for solids, but primarily liquids.
                // Metadata: # of connections, then ids, then resting distance, then break strength/threshold, then stiffness
        POINT_POINT_DISTANCE(0),

        // Keeping a point near an edge or face - surface tension.
        POINT_EDGE_TRIANGLE_DISTANCE(1),

        // Lightning bolt forks, helps with collision between differing line-segments
        EDGE_EDGE_DISTANCE(2),

        // Maintains inner volume. Primary for ForceClouds rather than points; very, very useful for liquids.
        VOLUME_PRESERVATION(3),

        // Matches a given shape. Self explanatory. Use with special effects to get elasticity; can be used to just re-form.
        SHAPE_MATCHING(4),

        // Angular stiffness between adjacent faces; maintains curve/edges of relevant structures.
        DIHEDRAL_BENDING(5),

        // Limits stretch/compression - good for things that shatter or burst (different data values for ice vs water)
        ISOMETRIC_STRAIN(6),

        // Tries to keep ForcePoints at a specific density.
        DENSITY_INCOMPRESSIBILITY(7),

        // Fluid viscosity. Supports different currents, turbulence, etc. Essentially a fancy form of curl noise, but with
        // collision.
        VISCOSITY_SHEAR(8),

        // Self explanatory. Ensures the ForcePoint can collide with MC objects.
        CONTACT_COLLISION(9),

        /** Advanced Constraints **/

        // Rotation around an axis or point
        HINGE_BALL_JOINT(10),

        // Specific rest angle between triplets. Similar to bending, but marginally more precise, and designed for sharp
        // edges.
        ANGULAR_REST_ANGLE(11),

        // Adjust ForcePoints towards a desired velocity or angle. Good for oscillation while performing another motion.
        // Flame rings, etc.
        MOTOR_TARGET_VELOCITY(12),

        // Aims to move towards a plane. Good for walls/surfaces.
        PLANAR_CONSTRAINT(13),

        // Similar to volume preservation but supports differing sizes.
        VOLUME_EXPANSION_INFLATABLE(14),

        // Similar to segment constraints, but also resist twisting/suport it - really precise flame tornadoes.
        STRETCH_SHEAR_COSSERAT(15),

        // Ensures ForcePoints follow another given vector field.... A bit redundant, but good if you have a spline or VectorField from external software and want to
        // use it with the physics system.
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
