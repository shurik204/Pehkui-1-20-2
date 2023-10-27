package virtuoel.pehkui.mixin;

import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin({
//	ArmorStandEntity.class,
	AbstractSkeletonEntity.class,
//	EndermiteEntity.class,
//	PatrolEntity.class,
//	SilverfishEntity.class,
	ZombieEntity.class,
//	AnimalEntity.class,
	PlayerEntity.class
})
public abstract class EntityVehicleHeightOffsetMixin
{
	@Inject(at = @At("RETURN"), method = "getUnscaledRidingOffset", cancellable = true)
	private void pehkui$getHeightOffset(CallbackInfoReturnable<Float> info)
	{
		final Entity self = (Entity) (Object) this;
		final Entity vehicle = self.getVehicle();
		
		if (vehicle != null)
		{
			final float scale = ScaleUtils.getBoundingBoxHeightScale(self);
			final float vehicleScale = ScaleUtils.getBoundingBoxHeightScale(vehicle);
			
			if (scale != 1.0F || vehicleScale != 1.0F) {
				final float offset = info.getReturnValueF();
				final float scaledOffset = offset * scale;


				info.setReturnValue(scaledOffset);
			}
		}
	}
}
