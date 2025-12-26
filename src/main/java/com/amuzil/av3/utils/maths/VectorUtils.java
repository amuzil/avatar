package com.amuzil.av3.utils.maths;

import com.simsilica.mathd.Vec3d;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class VectorUtils {
    /**
     * Sample a 3D vector field at an arbitrary world‐space position, via trilinear interpolation.
     *
     * @param field    the Vec3[sizeX][sizeY][sizeZ] array you built with buildVectorField()
     * @param center   the world‐space center you passed into buildVectorField
     * @param sizeX    field.length
     * @param sizeY    field[0].length
     * @param sizeZ    field[0][0].length
     * @param cellDim  the cell edge length you passed into buildVectorField
     * @param worldPos the position you want to sample at
     * @return interpolated Vec3 value
     */
    public static Vec3 sampleField(Vec3[][][] field,
                                   Vec3 center,
                                   int sizeX, int sizeY, int sizeZ,
                                   double cellDim,
                                   Vec3 worldPos) {
        // 1) Recompute origin from center
        double halfGridX = (sizeX * cellDim) * 0.5;
        double halfGridY = (sizeY * cellDim) * 0.5;
        double halfGridZ = (sizeZ * cellDim) * 0.5;
        double originX = center.x - halfGridX;
        double originY = center.y - halfGridY;
        double originZ = center.z - halfGridZ;

        // 2) Compute local (floating‐point) coordinates in grid space
        double fx = (worldPos.x - originX) / cellDim - 0.5;
        double fy = (worldPos.y - originY) / cellDim - 0.5;
        double fz = (worldPos.z - originZ) / cellDim - 0.5;

        // 3) Clamp to valid index range
        int ix0 = Math.max(0, Math.min(sizeX - 1, (int) Math.floor(fx)));
        int iy0 = Math.max(0, Math.min(sizeY - 1, (int) Math.floor(fy)));
        int iz0 = Math.max(0, Math.min(sizeZ - 1, (int) Math.floor(fz)));
        int ix1 = Math.min(sizeX - 1, ix0 + 1);
        int iy1 = Math.min(sizeY - 1, iy0 + 1);
        int iz1 = Math.min(sizeZ - 1, iz0 + 1);

        double dx = fx - ix0;
        double dy = fy - iy0;
        double dz = fz - iz0;

        // 4) Fetch the eight corners
        Vec3 c000 = field[ix0][iy0][iz0];
        Vec3 c100 = field[ix1][iy0][iz0];
        Vec3 c010 = field[ix0][iy1][iz0];
        Vec3 c110 = field[ix1][iy1][iz0];
        Vec3 c001 = field[ix0][iy0][iz1];
        Vec3 c101 = field[ix1][iy0][iz1];
        Vec3 c011 = field[ix0][iy1][iz1];
        Vec3 c111 = field[ix1][iy1][iz1];

        // 5) Interpolate along X
        Vec3 c00 = lerp(c000, c100, dx);
        Vec3 c10 = lerp(c010, c110, dx);
        Vec3 c01 = lerp(c001, c101, dx);
        Vec3 c11 = lerp(c011, c111, dx);

        // 6) Interpolate along Y
        Vec3 c0 = lerp(c00, c10, dy);
        Vec3 c1 = lerp(c01, c11, dy);

        // 7) Interpolate along Z and return
        return lerp(c0, c1, dz);
    }

    /**
     * Simple linear interpolation between two Vec3s.
     */
    public static Vec3 lerp(Vec3 a, Vec3 b, double t) {
        return new Vec3(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t,
                a.z + (b.z - a.z) * t
        );
    }

    public static Vec3 rotate(Vec3 v, Quaternionf q) {
        Vector3f tmp = new Vector3f((float) v.x, (float) v.y, (float) v.z);
        q.transform(tmp); // rotates in-place
        return new Vec3(tmp.x, tmp.y, tmp.z);
    }

    public static Quaternionf faceDirectionFromLocalY(Vec3 dir) {
        Vec3 d = dir.lengthSqr() < 1e-12 ? new Vec3(0, 1, 0) : dir;//.normalize();

        Vector3f from = new Vector3f(0, 1, 0); // your effect’s forward axis in local space
        Vector3f to   = new Vector3f((float)d.x, (float)d.y, (float)d.z);

        return new Quaternionf().rotationTo(from, to); // normalized + robust
    }

    public static Quaternionf faceDirectionFromLocalY(Vector3f dir) {
        Vector3f d = dir.lengthSquared() < 1e-12 ? new Vector3f(0, 1, 0) : dir;//.normalize();

        Vector3f from = new Vector3f(0, 1, 0); // your effect’s forward axis in local space
        Vector3f to   = new Vector3f((float)d.x, (float)d.y, (float)d.z);

        return new Quaternionf().rotationTo(from, to); // normalized + robust
    }


    public static Vec3 rotateAroundAxisX(Vec3 v, double angle) {
        angle = Math.toRadians(angle);
        double y, z, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        y = v.y * cos - v.z * sin;
        z = v.y * sin + v.z * cos;
        return new Vec3(v.x, y, z);
    }

    public static Vec3 rotateAroundAxisY(Vec3 v, double angle) {
        angle = -angle;
        angle = Math.toRadians(angle);
        double x, z, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        x = v.x * cos + v.z * sin;
        z = v.x * -sin + v.z * cos;
        return new Vec3(x, v.y, z);
    }

    public static Vec3 rotateAroundAxisZ(Vec3 v, double angle) {
        angle = Math.toRadians(angle);
        double x, y, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        x = v.x * cos - v.y * sin;
        y = v.x * sin + v.y * cos;
        return new Vec3(x, y, v.z);
    }

    /**
     * Inverse of calculateViewVector(xRot, yRot).
     *
     * @param dir normalized direction vector (world-space)
     * @param fallbackYRot used when dir is (almost) straight up/down (yaw undefined)
     * @return Vec2(xRot, yRot) in degrees
     */
    public static Vec2 dirToRotations(Vector3f dir, float fallbackYRot) {
        // Pitch (xRot)
        double y = Mth.clamp(dir.y, -1.0, 1.0);
        float xRot = (float) (-Math.toDegrees(Math.asin(y))); // xRot = asin(-y) = -asin(y)

        // Yaw (yRot)
        double cosPitch = Math.sqrt(Math.max(0.0, 1.0 - y * y)); // = cos(xRot) for normalized dir
        float yRot;
        if (cosPitch < 1.0e-6) {
            yRot = fallbackYRot;
        } else {
            // f1 = atan2(vx, vz), and yRot = -deg(f1)
            float f1 = (float) Math.atan2(dir.x, dir.z);
            yRot = (float) -Math.toDegrees(f1);
        }

        // Optional: wrap like MC often does
        xRot = Mth.wrapDegrees(xRot);
        yRot = Mth.wrapDegrees(yRot);

        return new Vec2(xRot, yRot);
    }

    /** Convenience overload: fallback yaw = 0. */
    public static Vec2 dirToRotations(Vector3f dir) {
        return dirToRotations(dir, 0.0f);
    }
}
