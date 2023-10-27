package virtuoel.pehkui.mixin.compat120plus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.ChickenEntity;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(ChickenEntity.class)
public abstract class ChickenEntityMixin
{
	@ModifyConstant(method = "getPassengerAttachmentPos", constant = @Constant(floatValue = -0.1F))
	private float pehkui$updatePassengerPosition$offset(float value, Entity passenger)
	{
		final float scale = ScaleUtils.getBoundingBoxWidthScale(passenger);
		
		return scale != 1.0F ? scale * value : value;
	}
}
