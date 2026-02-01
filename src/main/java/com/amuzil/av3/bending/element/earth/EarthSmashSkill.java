package com.amuzil.av3.bending.element.earth;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.skill.EarthSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.construct.AvatarRigidBlock;
import com.amuzil.av3.network.AvatarNetwork;
import com.amuzil.av3.network.packets.client.TriggerFXPacket;
import com.amuzil.av3.utils.Constants;
import com.amuzil.av3.utils.bending.OriginalBlocks;
import com.amuzil.magus.skill.data.SkillData;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.event.SkillTickEvent;
import com.amuzil.magus.skill.traits.skilltraits.KnockbackTrait;
import com.amuzil.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.magus.skill.traits.skilltraits.StringTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static com.amuzil.av3.bending.form.BendingForms.*;


public class EarthSmashSkill extends EarthSkill {
    OriginalBlocks originalBlocks = new OriginalBlocks();
    protected Consumer<SkillTickEvent> cleanupRunnable;
    Consumer<SkillTickEvent> blockQuaker;
    private BlockPos epicenter;
    private int currentQuakeDistance = 0;
    private int ticksPassed = 0;
    private int ticksStopped = 0;
    private Random random = new Random();

    public EarthSmashSkill() {
        super(Avatar.MOD_ID, "earth_smash");
        addTrait(new StringTrait(Constants.FX, "earth_block"));
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 1.5f));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));

        this.startPaths = SkillPathBuilder.getInstance()
                .add(EXPAND)
                .build();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);
        LivingEntity entity = bender.getEntity();
        ServerLevel level = (ServerLevel) bender.getEntity().level();
        SkillData data = bender.getSkillData(this);
        System.out.println("Earth Smash Skill started");
        // Only bend if close enough to selected Earth

        Set<UUID> entityIds = bender.getSelection().entityIds();
        if (entityIds.isEmpty()) {
            bender.formPath.clear();
            bender.resetSelection();
            data.setSkillState(SkillState.IDLE);
            return;
        }

        ResourceLocation id = Avatar.id(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());

        for (UUID entityId: entityIds) {
            if (level.getEntity(entityId) instanceof AvatarRigidBlock rigidBlock) {
                AvatarNetwork.sendToClient(new TriggerFXPacket(id, rigidBlock.getId()), (ServerPlayer) bender.getEntity());
                rigidBlock.remove(Entity.RemovalReason.KILLED);
            }
        }

        bender.formPath.clear();
        bender.resetSelection();
        data.setSkillState(SkillState.IDLE);
    }

//    @Override
//    public void run(Bender bender) {
//        super.run(bender);
//    }

//    @Override
//    public void stop(Bender bender) {
//        super.stop(bender);
//    }

}
