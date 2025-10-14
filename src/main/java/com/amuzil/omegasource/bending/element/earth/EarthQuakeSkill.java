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
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static com.amuzil.omegasource.bending.form.BendingForms.*;


public class EarthQuakeSkill extends EarthSkill {

    private static final Logger log = LoggerFactory.getLogger(EarthQuakeSkill.class);
    private HashMap<BlockPos, BlockState> WorldState;
    private int currentQuakeDistance = 0;

    public EarthQuakeSkill() {
        super(Avatar.MOD_ID, "earth_quake");
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 1.5f));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));

        this.startPaths = SkillPathBuilder.getInstance()
                // TODO: Remove arc/account for this skill being a motion skill, so no need to log ARC and SHAPE
//                .simple(new ActiveForm(ARC, true))
                .complex(new ActiveForm(RAISE, true))
                .complex(new ActiveForm(LOWER, true))
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
    public boolean shouldRun(Bender bender, FormPath formPath) {
        return bender.getSkillData(this).getSkillState().equals(SkillState.RUN);
    }

    @Override
    public boolean shouldStop(Bender bender, FormPath formPath) {
        return formPath.simple().hashCode() == stopPaths().simple().hashCode();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);
        System.out.println("EarthQuakeSkill started on server");
        int numRings = 3;
        // get player position casting as epicentre.
        BlockPos epicenter = bender.getEntity().blockPosition();
        SkillData data = bender.getSkillData(this);
        data.setSkillState(SkillState.RUN);
        this.listen(); // start running every tick
    }

    @Override
    public void run(Bender bender) {
        super.run(bender);
        System.out.println("EarthQuakeSkill running on server");
    }

    @Override
    public void stop(Bender bender) {
        super.stop(bender);
        System.out.println("EarthQuakeSkill stopped on server");
    }
}
