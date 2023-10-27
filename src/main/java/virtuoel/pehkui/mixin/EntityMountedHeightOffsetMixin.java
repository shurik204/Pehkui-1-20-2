package virtuoel.pehkui.mixin;

import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.SnifferEntity;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin({
	BoatEntity.class,
	RavagerEntity.class,
	SnifferEntity.class,
	SkeletonHorseEntity.class,
	HoglinEntity.class,
	ZoglinEntity.class,
})
public abstract class EntityMountedHeightOffsetMixin
{
	@Inject(at = @At("RETURN"), method = "getPassengerAttachmentPos", cancellable = true)
	private void pehkui$getMountedHeightOffset(CallbackInfoReturnable<Vector3f> info)
	{
		final float scale = ScaleUtils.getBoundingBoxHeightScale((Entity) (Object) this);
		
		if (scale != 1.0F) {
			Vector3f pos = info.getReturnValue();
			pos.y += (float) ((1.0F - scale) * 0.25D);
			info.setReturnValue(pos);
		}
	}
}
