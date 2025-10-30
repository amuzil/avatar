package com.amuzil.omegasource.bending.element.earth;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.KnockbackTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.omegasource.bending.BendingSkill;
import com.amuzil.omegasource.bending.skill.EarthSkill;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.utils.Constants;
import com.amuzil.omegasource.utils.ship.EarthController;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.world.ServerShipWorld;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;

import static com.amuzil.omegasource.bending.form.BendingForms.BLOCK;
import static com.amuzil.omegasource.utils.ship.VSUtils.assembleEarthShip;
import static com.amuzil.omegasource.utils.ship.VSUtils.controlBlock;


public class EarthBlockSkill extends EarthSkill {
    // TODO: Add auto block selection feature
    BlockPos blockPosCache = null;

    public EarthBlockSkill() {
        super(Avatar.MOD_ID, "earth_block");
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 1.5f));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));

        this.startPaths = SkillPathBuilder.getInstance()
                .add(BLOCK)
                .build();
    }

    @Override
    public void start(Bender bender) {
        BlockPos shipyardBlockPos = assembleEarthShip(bender);
        blockPosCache = bender.getSelection().blockPos();
        if (shipyardBlockPos != null) {
            ServerLevel level = (ServerLevel) bender.getEntity().level();
            level.getServer().execute(() -> {
                LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(level, shipyardBlockPos);
                if (serverShip != null) {
                    EarthController earthController = EarthController.getOrCreate(serverShip, bender);
                    List<Skill> activeSkills = bender.activeSkills.values().stream().toList();
                    BendingSkill activeSkill = this;
                    for (Skill skill: activeSkills) {
                        System.out.println(((EarthBlockSkill) skill).blockPosCache + " " + blockPosCache);
                        if (skill instanceof EarthBlockSkill earthBlockSkill
                                && earthBlockSkill.blockPosCache.equals(blockPosCache)) {
                            activeSkill = earthBlockSkill;
                            break;
                        }
                    }
                    if (earthController.isControlled()) {
                        earthController.setControlled(false);
                        activeSkill.stopRun();
                    } else {
                        earthController.setControlled(true);
                        activeSkill.startRun();
                    }
                }
            });
        } else
            stopRun(); // cleanup

//        if (bender.getEntity() instanceof AbstractClientPlayer benderPlayer) {
//            AnimationStack animationStack = PlayerAnimationAccess.getPlayerAnimLayer(benderPlayer);
//            animationStack.addAnimLayer(null, true);
//            var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(benderPlayer).get(
//                    ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "animation"));
//            if (animation != null) {
//                animation.setAnimation(new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "earth_block"))));
//                // You might use  animation.replaceAnimationWithFade(); to create fade effect instead of sudden change
//                // See javadoc for details
//            }
//        }
    }

    @Override
    public void run(Bender bender) {
        super.run(bender);
        ServerLevel level = (ServerLevel) bender.getEntity().level();
        BlockPos blockPos = bender.getSelection().blockPos();
        if (blockPos == null)
            blockPos = blockPosCache;
        if (blockPos != null && VSGameUtilsKt.isBlockInShipyard(level, blockPos)) {
            LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(level, blockPos);
            ServerShipWorld serverShipWorld = (ServerShipWorld) VSGameUtilsKt.getVsCore().getHooks().getCurrentShipServerWorld();
            if (serverShip != null && serverShipWorld != null)
                controlBlock(serverShip, serverShipWorld, level, bender);
        }
    }

    @Override
    public void stop(Bender bender) {
        super.stop(bender);

        ServerLevel level = (ServerLevel) bender.getEntity().level();
        BlockPos blockPos = bender.getSelection().blockPos();
        if (blockPos == null)
            blockPos = blockPosCache;
        if (blockPos != null && VSGameUtilsKt.isBlockInShipyard(level, blockPos)) {
            LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(level, blockPos);
            if (serverShip != null) {
                EarthController earthController = EarthController.getOrCreate(serverShip, bender);
                earthController.stopControl();
            }
        }
    }
}
