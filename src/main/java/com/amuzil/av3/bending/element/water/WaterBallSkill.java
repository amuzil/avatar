package com.amuzil.av3.bending.element.water;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.skill.WaterSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IClientModule;
import com.amuzil.av3.entity.api.ICollisionModule;
import com.amuzil.av3.entity.api.IForceModule;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.collision.SimpleKnockbackModule;
import com.amuzil.av3.entity.api.modules.collision.WaterCollisionModule;
import com.amuzil.av3.entity.api.modules.force.GravityModule;
import com.amuzil.av3.entity.api.modules.force.LookModule;
import com.amuzil.av3.entity.api.modules.force.MoveModule;
import com.amuzil.av3.entity.projectile.AvatarWaterProjectile;
import com.amuzil.av3.entity.projectile.AvatarWaterRing;
import com.amuzil.av3.entity.projectile.AvatarWaterShield;
import com.amuzil.av3.utils.Constants;
import com.amuzil.magus.skill.data.SkillData;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.skilltraits.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Set;
import java.util.UUID;

import static com.amuzil.av3.bending.form.BendingForms.STRIKE;


public class WaterBallSkill extends WaterSkill {

    public WaterBallSkill() {
        super(Avatar.MOD_ID, "water_ball");
        addTrait(new DamageTrait(Constants.DAMAGE, 2.0f));
        addTrait(new SizeTrait(Constants.SIZE, 0.3F));
        addTrait(new SizeTrait(Constants.MAX_SIZE, 1.25f));
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 0.2f));
        addTrait(new SpeedTrait(Constants.SPEED, 0.1d));
        addTrait(new TimedTrait(Constants.LIFETIME, 200));
        addTrait(new SpeedTrait(Constants.SPEED_FACTOR, 0.85d));
        addTrait(new StringTrait(Constants.FX, "water1"));
        addTrait(new AngleTrait(Constants.ANGLE, 0));
        addTrait(new RangeTrait(Constants.RANGE, 2.0d));
        addTrait(new FloatTrait(Constants.SOURCE_CONSUMPTION, 1.0f));

        startPaths = SkillPathBuilder.getInstance().add(STRIKE).build();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);

        LivingEntity entity = bender.getEntity();
        ServerLevel level = (ServerLevel) bender.getEntity().level();
        SkillData data = bender.getSkillData(this);

        int lifetime = data.getTrait(Constants.LIFETIME, TimedTrait.class).getTime();
        double speed = data.getTrait(Constants.SPEED, SpeedTrait.class).getSpeed();
        double size = data.getTrait(Constants.SIZE, SizeTrait.class).getSize();

        //  If no available entities for selection, check for water source
        Set<UUID> entityIds = bender.getSelection().entityIds();
        //  Check for water source - if no resource, return
        BlockPos pos = bender.getSelection().blockPos();
        BlockState state = level.getBlockState(pos);


        if (!entityIds.isEmpty()) {
            for (UUID id : entityIds) {
                    if (level.getEntity(id) instanceof AvatarWaterShield || level.getEntity(id) instanceof AvatarWaterRing) {
                        AvatarEntity source = (AvatarEntity) level.getEntity(id);
                        if (source.sourceLevel() <= 0) {
                            source.kill();
                        }
                        else {
                            source.sourceLevel(source.sourceLevel() - 1);
                            if (source.sourceLevel() <= 0) {
                                source.kill();
                            }
                        }
                    // Iterate down
                    break;
                }
            }
        }
        // TODO: CHange this to a general water block config, and then alter block consumption/fluid level based on block
        else if (state.getFluidState().isSource() && state.getBlock() == Blocks.WATER) {
            // Consume the water source block and create the projectile.
            // However, because this abiltiy is so weak, we want to let players consume it
//            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 0);

        }
        else {
            bender.formPath.clear();
            bender.resetSelection();
            data.setSkillState(SkillState.IDLE);
            return;
        }

        AvatarWaterProjectile projectile = new AvatarWaterProjectile(level);
        projectile.setElement(element());
//        projectile.setFX(data.getTrait(Constants.FX, StringTrait.class).getInfo());
        projectile.setOwner(entity);
        projectile.setMaxLifetime(lifetime);
        projectile.setWidth((float) size);
        projectile.setHeight((float) size);
        projectile.setNoGravity(true);
        projectile.setDamageable(false);

        projectile.addTraits(skillData.getTrait(Constants.ANGLE, AngleTrait.class));
        projectile.addTraits(skillData.getTrait(Constants.SPEED, SpeedTrait.class));
        projectile.addTraits(skillData.getTrait(Constants.RANGE, RangeTrait.class));

        projectile.addTraits(data.getTrait(Constants.KNOCKBACK, KnockbackTrait.class));
        projectile.addTraits(new DirectionTrait(Constants.KNOCKBACK_DIRECTION, new Vec3(0, 0.45, 0)));
        projectile.addModule(ModuleRegistry.create(SimpleKnockbackModule.id));
        projectile.addForceModule((IForceModule) ModuleRegistry.create(MoveModule.id));

        // Damage module
        projectile.addTraits(data.getTrait(Constants.DAMAGE, DamageTrait.class));
        projectile.addTraits(data.getTrait(Constants.SIZE, SizeTrait.class));
//        projectile.addModule(ModuleRegistry.create(SimpleDamageModule.id));
        projectile.addTraits(new CollisionTrait(Constants.COLLISION_TYPE, "Blaze", "Fireball", "AbstractArrow", "FireProjectile"));
        projectile.addCollisionModule((ICollisionModule) ModuleRegistry.create(WaterCollisionModule.id));

        // Slow down over time
        projectile.addTraits(data.getTrait(Constants.SPEED_FACTOR, SpeedTrait.class));
//        projectile.addModule(ModuleRegistry.create(ChangeSpeedModule.id));

        // Particle FX module
        projectile.addTraits(data.getTrait(Constants.FX, StringTrait.class));

        projectile.lookDirection(entity.getLookAngle().toVector3f());
        projectile.vfxScale(new Vector3f(2f, 2f, 2f));

        // This needs to happen client and server side...
        projectile.addForceModule((IForceModule) ModuleRegistry.create(LookModule.id));
        projectile.addClientModule((IClientModule) ModuleRegistry.create(LookModule.id));

        projectile.addForceModule((IForceModule) ModuleRegistry.create(GravityModule.id));
        projectile.shoot(entity.position().add(0, entity.getEyeHeight(), 0), entity.getLookAngle(), 0.25, 0);
        projectile.init();

        projectile.setXRot(entity.getXRot());
        projectile.setYRot(entity.getYRot());
        bender.formPath.clear();
        data.setSkillState(SkillState.IDLE);

        bender.getEntity().level().addFreshEntity(projectile);
    }
}
