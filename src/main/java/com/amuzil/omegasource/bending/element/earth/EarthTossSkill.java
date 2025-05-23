package com.amuzil.omegasource.bending.element.earth;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.KnockbackTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.omegasource.bending.skill.EarthSkill;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.utils.Constants;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.GameTickForceApplier;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import static com.amuzil.omegasource.bending.form.BendingForms.STRIKE;


public class EarthTossSkill extends EarthSkill {

    public EarthTossSkill() {
        super(Avatar.MOD_ID, "earth_toss");
        addTrait(new KnockbackTrait(1.5f, Constants.KNOCKBACK));
        addTrait(new SizeTrait(1.0f, Constants.SIZE));

        this.startPaths = SkillPathBuilder.getInstance()
                .complex(new ActiveForm(STRIKE, true))
                .build();

//        this.runPaths = SkillPathBuilder.getInstance()
//                .simple(new ActiveForm(STRIKE, true))
//                .build();
    }

    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        return super.shouldStart(entity, formPath);
    }

    @Override
    public void start(LivingEntity entity) {
        super.start(entity);

        Bender bender = (Bender) Bender.getBender(entity);
        if (!entity.level().isClientSide()) {
            ServerLevel level = (ServerLevel) entity.level();
            if (VSGameUtilsKt.isBlockInShipyard(level, bender.blockPos)) {
                LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(level, bender.blockPos);
                if (serverShip != null) {
                    GameTickForceApplier gtfa = serverShip.getAttachment(GameTickForceApplier.class);
                    if (gtfa != null) {
                        tossBlock(entity, gtfa);
                    }
                }
            }
            if (VSGameUtilsKt.isBlockInShipyard(level, bender.blockPos)) {
                LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(level, bender.blockPos);
                if (serverShip != null) {
                    GameTickForceApplier gtfa = serverShip.getAttachment(GameTickForceApplier.class);
                    if (gtfa != null) {
                        tossBlock(entity, gtfa);
                    }
                }
            }
        }

        if (bender != null) {
            SkillData data = bender.getSkillData(this);
            data.setState(SkillState.RUN);
            resetCooldown(data);
        }
    }

    private static void tossBlock(LivingEntity entity, GameTickForceApplier gtfa) {
        Vec3 vec3 = entity.getLookAngle().normalize()
                .add(0, 1, 0)
                .multiply(75000, 10000, 75000);
//        System.out.println("Applying force: " + vec3);
        Vector3d v3d = VectorConversionsMCKt.toJOML(vec3);
        gtfa.applyInvariantForce(v3d);
    }
}
