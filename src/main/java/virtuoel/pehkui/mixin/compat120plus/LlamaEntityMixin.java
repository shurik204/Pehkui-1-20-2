package virtuoel.pehkui.mixin.compat120plus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.LlamaEntity;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(LlamaEntity.class)
public abstract class LlamaEntityMixin
{
	@ModifyConstant(method = "getPassengerAttachmentPos", constant = @Constant(floatValue = -0.3F))
	private float pehkui$updatePassengerPosition$offset(float value)
	{
		final float scale = ScaleUtils.getBoundingBoxWidthScale((Entity) (Object) this);
		
		return scale != 1.0F ? scale * value : value;
	}
}
