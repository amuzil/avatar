package com.amuzil.av3.entity.api.modules.client;

import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IFXModule;
import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;


public class PhotonModule implements IFXModule {

    public static String id = PhotonModule.class.getSimpleName();

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

    public static void startEntityEffect(FX fx, Entity entity) {
        if (fx != null) {
            EntityEffectExecutor entityEffect = new EntityEffectExecutor(fx, entity.level(), entity, EntityEffectExecutor.AutoRotate.NONE);
            Vec3 look = entity.getLookAngle().normalize();

// Effect faces +Y by default
            Vec3 from = new Vec3(0, 1, 0);
            Vec3 to   = look;

// Axis-angle from → to
            Vec3 axis = from.cross(to);
            double dot = from.dot(to);

// Handle parallel / antiparallel
            Quaternionf q;
            if (axis.lengthSqr() < 1e-6) {
                if (dot > 0) {
                    q = new Quaternionf(); // identity
                } else {
                    // 180° flip around any perpendicular axis (X is fine)
                    q = new Quaternionf().rotateAxis((float)Math.PI, 1, 0, 0);
                }
            } else {
                axis = axis.normalize();
                float angle = (float)Math.acos(Mth.clamp(dot, -1.0, 1.0));
                q = new Quaternionf().rotateAxis(angle, (float)axis.x, (float)axis.y, (float)axis.z);
            }

            entityEffect.setRotation(q);
            entityEffect.start();
        }
    }
}
