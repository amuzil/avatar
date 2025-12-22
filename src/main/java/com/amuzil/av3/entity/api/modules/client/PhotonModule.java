package com.amuzil.av3.entity.api.modules.client;

import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IFXModule;
import com.amuzil.av3.utils.maths.VectorUtils;
import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;


public class PhotonModule implements IFXModule {

    public static String id = PhotonModule.class.getSimpleName();
    private Vec3 lookDir = new Vec3(0, 1, 0);
    public static void startEntityEffect(FX fx, Entity entity, Vec3 lookDir) {
        if (fx != null) {
            EntityEffectExecutor entityEffect = new EntityEffectExecutor(fx, entity.level(), entity, EntityEffectExecutor.AutoRotate.NONE);
            Vec3 look = entity.getLookAngle().normalize();
            Vec3 pos = entity.position();
            pos = VectorUtils.rotateAroundAxisX(pos, entity.getXRot() + 90);
            pos = VectorUtils.rotateAroundAxisY(pos, entity.getYRot());
// Effect faces +Y by default
//            Vec3 from = new Vec3(0, 1, 0);
//            Vec3 to   = look;
//
//// Axis-angle from → to
//            Vec3 axis = from.cross(to);
//            double dot = from.dot(to);
//
//// Handle parallel / antiparallel
//            Quaternionf q;
//            if (axis.lengthSqr() < 1e-6) {
//                if (dot > 0) {
//                    q = new Quaternionf(); // identity
//                } else {
//                    // 180° flip around any perpendicular axis (X is fine)
//                    q = new Quaternionf().rotateAxis((float)Math.PI, 1, 0, 0);
//                }
//            } else {
//                axis = axis.normalize();
//                float angle = (float)Math.acos(Mth.clamp(dot, -1.0, 1.0));
//                q = new Quaternionf().rotateAxis(angle, (float)axis.x, (float)axis.y, (float)axis.z);
//            }
//
            entityEffect.setRotation(VectorUtils.faceDirectionFromLocalY(lookDir));
            entityEffect.start();
        }
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {
        lookDir = entity.getOwner().getLookAngle();
    }

    @Override
    public void tick(AvatarEntity entity) {
        // For starting effects per tick
        if (entity.fxLocation() != null) {
            if (entity.oneShotFX()) {
                if (entity.tickCount <= 1) {
                    startEntityEffect(FXHelper.getFX(entity.fxLocation()), entity, lookDir);
                }
            } else {
                startEntityEffect(FXHelper.getFX(entity.fxLocation()), entity, lookDir);
            }
        }
    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putString("ID", id);
    }

    @Override
    public void load(CompoundTag nbt) {
        id = nbt.getString("ID");
    }
}
