package virtuoel.pehkui.mixin.compat120plus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin {
//	@ModifyConstant(method = "updatePassengerPosition", constant = @Constant(floatValue = 0.2F))
//	private float pehkui$updatePassengerPosition$frontOffset(float value, Entity passenger)
//	{
//		final float scale = ScaleUtils.getBoundingBoxWidthScale(passenger);
//
//		return scale != 1.0F ? scale * value : value;
//	}
//
//	@ModifyConstant(method = "updatePassengerPosition", constant = @Constant(floatValue = -0.6F))
//	private float pehkui$updatePassengerPosition$backOffset(float value, Entity passenger)
//	{
//		final float scale = ScaleUtils.getBoundingBoxWidthScale(passenger);
//
//		return scale != 1.0F ? scale * value : value;
//	}
}
