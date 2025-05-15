package com.amuzil.omegasource.utils.physics.constraints;

public class ConstraintUtils {

    public static void enableConstraint(byte[] header, Constraints.ConstraintType type) {
        int byteIndex = Constraints.FLAG_OFFSET + (type.bitIndex() / 8);
        header[byteIndex] |= (byte) (1 << (type.bitIndex() % 8));
    }

    public static void disableConstraint(byte[] header,  Constraints.ConstraintType type) {
        int byteIndex = Constraints.FLAG_OFFSET + (type.bitIndex() / 8);
        header[byteIndex] &= (byte) ~(1 << (type.bitIndex() % 8));
    }

    public static boolean hasConstraint(byte[] header,  Constraints.ConstraintType type) {
        int byteIndex = Constraints.FLAG_OFFSET + (type.bitIndex() / 8);
        return (header[byteIndex] & (1 << (type.bitIndex() % 8))) != 0;
    }
}
