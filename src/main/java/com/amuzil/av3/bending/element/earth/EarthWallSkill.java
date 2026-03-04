package com.amuzil.av3.bending.element.earth;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.skill.EarthSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.construct.AvatarRigidBlock;
import com.amuzil.av3.utils.Constants;
import com.amuzil.av3.utils.bending.BendingMaterial;
import com.amuzil.av3.utils.bending.RigidBlockFactory;
import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.magus.skill.data.SkillData;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.magus.skill.traits.skilltraits.StringTrait;
import com.amuzil.magus.skill.traits.skilltraits.TimedTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import static com.amuzil.av3.bending.form.BendingForms.RAISE;
import static com.amuzil.av3.utils.bending.SkillHelper.canEarthBend;


public class EarthWallSkill extends EarthSkill {
    // TODO: Create Earth Wall Skill that adjusts shape based on player's look angle
    public EarthWallSkill() {
        super(Avatar.MOD_ID, "earth_wall");
        addTrait(new StringTrait(Constants.FX, "earth_wall"));
        addTrait(new TimedTrait(Constants.LIFETIME, 500));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));

        this.startPaths = SkillPathBuilder.getInstance()
                .add(RAISE)
                .build();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);
        LivingEntity entity = bender.getEntity();
        if (!canEarthBend(entity)) return; // Can't earth bend if too far from ground
        Level level = bender.getEntity().level();
        SkillData data = bender.getSkillData(this);
        BlockPos blockPos = bender.getEntity().blockPosition().below();
        BlockState blockState = level.getBlockState(blockPos);
        if (!BendingMaterial.isBendable(blockState, element())) return;

        int lifetime = data.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
        double size = data.getTrait(Constants.SIZE, SizeTrait.class).getSize();

        // --- Spawn 3x3 grid ---
        int rows = 3; int cols = 3;
        AvatarRigidBlock[][] grid = new AvatarRigidBlock[rows][cols];
        MinecraftSpace space = MinecraftSpace.get(level);

        // TODO: Only spawn if ground is within certain distance of player, otherwise don't spawn
        //  Also, if player is looking at a Minecraft Block Wall, don't spawn.
        //  Maybe make way to only spawn the blocks of the wall that aren't clipped in an existing wall
        // Find ground position in front of player
        Vec3 lookFlat = new Vec3(entity.getLookAngle().x, 0, entity.getLookAngle().z).normalize();
        Vec3 spawnPos = entity.position().add(lookFlat.scale(3.0)); // 3 blocks in front, at player's feet level

        // Raycast downward to find ground
        Vec3 rayStart = spawnPos.add(0, 5, 0);  // start above
        Vec3 rayEnd = spawnPos.add(0, -5, 0);   // end below

        BlockHitResult hit = level.clip(new ClipContext(
                rayStart,
                rayEnd,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                entity
        ));

        double groundY;
        if (hit.getType() != HitResult.Type.MISS) {
            groundY = hit.getLocation().y;
        } else {
            // Fallback: scan downward manually
            BlockPos checkPos = BlockPos.containing(spawnPos);
            while (checkPos.getY() > level.getMinBuildHeight() && level.getBlockState(checkPos).isAir()) {
                checkPos = checkPos.below();
            }
            groundY = checkPos.getY() + 1;
        }

        // Center of wall should be half its height above ground
        // rows * size = total wall height, half of that is the center offset
        Vec3 center = new Vec3(spawnPos.x, groundY + (rows * size / 2.0), spawnPos.z);
        Vec3 look = entity.getLookAngle().normalize();
        Vec3 right = look.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 up = new Vec3(0, 1, 0);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double offsetX = (col - 1) * size;
                double offsetY = (row - 1) * size;

                Vec3 pos = center
                        .add(right.scale(offsetX))
                        .add(up.scale(offsetY));

                AvatarRigidBlock block = RigidBlockFactory.createBlock(level, blockState, entity, lifetime, (float) size);
                block.setElement(element());
                block.setFX(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());
                block.setPos(pos);
                block.init();

                space.addCollisionObject(block.getRigidBody());
                bender.getSelection().addEntityId(block.getUUID());
                level.addFreshEntity(block);
                grid[row][col] = block;
            }
        }

        // --- Glue neighbors together ---
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Glue to right neighbor
                if (col + 1 < cols) {
                    RigidBlockFactory.createGlueJoint(space, grid[row][col], grid[row][col + 1]);
                }
                // Glue to upper neighbor
                if (row + 1 < rows) {
                    RigidBlockFactory.createGlueJoint(space, grid[row][col], grid[row + 1][col]);
                }
            }
        }

        bender.formPath.clear();
        data.setSkillState(SkillState.IDLE);
//        bender.getSelection().addEntityId(rigidBlock.getUUID());
//        entity.level().addFreshEntity(rigidBlock);
    }
}
