package dev.lazurite.rayon.impl.example.entity;

import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.body.EntityRigidBody;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class PhysicsFallingBlock extends FallingBlockEntity implements EntityPhysicsElement
{
	private final EntityRigidBody rigidBody;
	
	public PhysicsFallingBlock(EntityType<? extends FallingBlockEntity> type, Level level)
	{
		super(type, level);
		this.rigidBody = new EntityRigidBody(this);
	}
	
	@Override
	public @Nullable EntityRigidBody getRigidBody()
	{
		return this.rigidBody;
	}
}
