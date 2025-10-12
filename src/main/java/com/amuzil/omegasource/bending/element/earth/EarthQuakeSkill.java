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
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;

import static com.amuzil.omegasource.bending.form.BendingForms.*;


public class EarthQuakeSkill extends EarthSkill {

    private HashMap<BlockPos, BlockState> WorldState;
    private int currentQuakeDistance = 0;

    public EarthQuakeSkill() {
        super(Avatar.MOD_ID, "earth_quake");
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 1.5f));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));

        this.startPaths = SkillPathBuilder.getInstance()
                .simple(new ActiveForm(RAISE, true))
                .simple(new ActiveForm(LOWER, true))
                .build();

        this.stopPaths = SkillPathBuilder.getInstance()
                .simple(new ActiveForm(BLOCK, true))
                .build();
    }

    @Override
    public boolean shouldStart(Bender bender, FormPath formPath) {
        return formPath.simple().hashCode() == startPaths().simple().hashCode();
    }

    @Override
    public boolean shouldStop(Bender bender, FormPath formPath) {
        return super.shouldStop(bender, formPath) && formPath.simple().hashCode() == stopPaths().simple().hashCode();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);

        int numRings = 3;

        if (!bender.getEntity().level().isClientSide()) {
            // get player position casting as epicentre.
            BlockPos epicenter = bender.getEntity().blockPosition();

//            epicenter

        } else {

            bender.getEntity().sendSystemMessage(Component.literal("Shit began to happen fam"));
        }

        SkillData data = bender.getSkillData(this);
        data.setSkillState(SkillState.RUN);
    }

    @Override
    public void run(Bender bender) {
        super.run(bender);
        if (!bender.getEntity().level().isClientSide()) {

        } else {
            bender.getEntity().sendSystemMessage(Component.literal("Shit happened fam"));
        }

    }

    @Override
    public void stop(Bender bender) {


        if (!bender.getEntity().level().isClientSide()) {

        } else {
            bender.getEntity().sendSystemMessage(Component.literal("Shit stopped happening fam"));

        }

        // Always call this at the end!
        super.stop(bender);
    }
}
