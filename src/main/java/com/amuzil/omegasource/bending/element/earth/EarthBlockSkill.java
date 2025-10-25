package com.amuzil.omegasource.bending.element.earth;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.KnockbackTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.omegasource.bending.skill.EarthSkill;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.utils.Constants;
import com.amuzil.omegasource.utils.ship.EarthController;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.world.ServerShipWorld;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import static com.amuzil.omegasource.bending.form.BendingForms.BLOCK;
import static com.amuzil.omegasource.bending.form.BendingForms.STRIKE;
import static com.amuzil.omegasource.utils.ship.VSUtils.assembleEarthShip;
import static com.amuzil.omegasource.utils.ship.VSUtils.controlBlock;


public class EarthBlockSkill extends EarthSkill {

    public EarthBlockSkill() {
        super(Avatar.MOD_ID, "earth_block");
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 1.5f));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));

        this.startPaths = SkillPathBuilder.getInstance()
                .simple(new ActiveForm(BLOCK, true))
                .build();

//        this.runPaths = SkillPathBuilder.getInstance()
//                .simple(new ActiveForm(BLOCK, true))
//                .build();

        this.stopPaths = SkillPathBuilder.getInstance()
                .simple(new ActiveForm(STRIKE, true))
                .build(); // TODO: Make this BLOCK Form so it's like a toggle if already controlling selected Block
    }

    @Override
    public boolean shouldStart(Bender bender, FormPath formPath) {
        return formPath.simple().hashCode() == startPaths().simple().hashCode();
    }

    @Override
    public boolean shouldStop(Bender bender, FormPath formPath) {
        return formPath.simple().hashCode() == stopPaths().simple().hashCode();
    }

    @Override
    public void start(Bender bender) {
        startRun(bender);
        BlockPos shipyardBlockPos = assembleEarthShip(bender);
        if (shipyardBlockPos != null)
            bender.getSelection().setBlockPos(shipyardBlockPos); // Important: Update BlockPos to the shipyard position
        else
            stopRun();
//        if (bender.getEntity() instanceof AbstractClientPlayer benderPlayer) {
//            AnimationStack animationStack = PlayerAnimationAccess.getPlayerAnimLayer(benderPlayer);
////            animationStack.addAnimLayer(null, true);
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
        ServerLevel serverLevel = (ServerLevel) bender.getEntity().level();
        BlockPos blockPos = bender.getSelection().blockPos();
        if (blockPos != null && VSGameUtilsKt.isBlockInShipyard(serverLevel, blockPos)) {
            LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(serverLevel, blockPos);
            ServerShipWorld serverShipWorld = (ServerShipWorld) VSGameUtilsKt.getVsCore().getHooks().getCurrentShipServerWorld();
            if (serverShip != null && serverShipWorld != null) {
                EarthController earthController = EarthController.getOrCreate(serverShip, bender);
                earthController.setControlled(true);
                controlBlock(serverShip, serverShipWorld, serverLevel, bender);
            }
        }
    }

    @Override
    public void stop(Bender bender) {
        super.stop(bender);

        ServerLevel level = (ServerLevel) bender.getEntity().level();
        BlockPos blockPos = bender.getSelection().blockPos();
        bender.getSelection().reset();
        if (blockPos != null && VSGameUtilsKt.isBlockInShipyard(level, blockPos)) {
            LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(level, blockPos);
            if (serverShip != null) {
                EarthController earthController = EarthController.getOrCreate(serverShip, bender);
                if (earthController != null) {
                    earthController.tickCount.set(0);
                    earthController.setControlled(false);
                }
            }
        }
    }
}
