package com.amuzil.av3.entity.api.modules.client;

import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IFXModule;
import com.amuzil.av3.entity.construct.AvatarConstruct;
import com.amuzil.av3.entity.projectile.AvatarProjectile;
import com.amuzil.av3.utils.maths.VectorUtils;
import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;


public class PhotonModule implements IFXModule {

    public static String id = PhotonModule.class.getSimpleName();

    public static void startEntityEffect(FX fx, Entity entity) {
        if (fx != null) {
            EntityEffectExecutor entityEffect = new EntityEffectExecutor(fx, entity.level(), entity, EntityEffectExecutor.AutoRotate.NONE);
            Vector3f look = entity.getLookAngle().toVector3f();
            Vector3f scale = new Vector3f(1, 1, 1);
            if (entity instanceof AvatarEntity avatar) {
                scale = avatar.vfxScale();
                look = avatar.lookDirection();
            }
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

            if (entity instanceof AvatarProjectile proj) {
                entityEffect.setScale(proj.width() * scale.x, proj.height() * scale.y, proj.width() * scale.z);
            }
            if (entity instanceof AvatarConstruct construct) {
                entityEffect.setScale(construct.width() * scale.x, construct.height() * scale.y,
                        construct.depth() * scale.z);
            }

            entityEffect.setRotation(VectorUtils.faceDirectionFromLocalY(look));
            entityEffect.setOffset(0, -entity.getBbHeight() / 2, 0);
            entityEffect.start();
        }
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {
    }

    @Override
    public void tick(AvatarEntity entity) {
        // For starting effects per tick
        if (entity.fxLocation() != null) {
            if (entity.oneShotFX()) {
                if (entity.tickCount <= 1) {
                    startEntityEffect(FXHelper.getFX(entity.fxLocation()), entity);
                }
            } else {
                startEntityEffect(FXHelper.getFX(entity.fxLocation()), entity);
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
