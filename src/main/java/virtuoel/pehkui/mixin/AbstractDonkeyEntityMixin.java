package virtuoel.pehkui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(AbstractDonkeyEntity.class)
public abstract class AbstractDonkeyEntityMixin
{
	@Inject(at = @At("RETURN"), method = "getPassengerAttachmentY", cancellable = true)
	private void pehkui$getMountedHeightOffset(CallbackInfoReturnable<Float> info)
	{
		final float scale = ScaleUtils.getBoundingBoxHeightScale((Entity) (Object) this);
		
		if (scale != 1.0F)
		{
			info.setReturnValue((float) (info.getReturnValue() + ((1.0F - scale) * 0.25D)));
		}
	}
}
