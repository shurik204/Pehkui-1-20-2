package virtuoel.pehkui.mixin.compat116plus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin
{
	@Unique BlockPos pehkui$initialClimbingPos = null;
	
	@Inject(method = "isClimbing()Z", at = @At(value = "RETURN"), cancellable = true)
	private void pehkui$isClimbing(CallbackInfoReturnable<Boolean> info)
	{
		final LivingEntity self = (LivingEntity) (Object) this;
		
		if (pehkui$initialClimbingPos != null || info.getReturnValueZ() || self.isSpectator())
		{
			return;
		}
		
		final float width = ScaleUtils.getBoundingBoxWidthScale(self);
		
		if (width > 1.0F)
		{
			final Box bounds = self.getBoundingBox();
			
			final double halfUnscaledXLength = (bounds.getLengthX() / width) / 2.0D;
			final int minX = MathHelper.floor(bounds.minX + halfUnscaledXLength);
			final int maxX = MathHelper.floor(bounds.maxX - halfUnscaledXLength);
			
			final int minY = MathHelper.floor(bounds.minY);
			
			final double halfUnscaledZLength = (bounds.getLengthZ() / width) / 2.0D;
			final int minZ = MathHelper.floor(bounds.minZ + halfUnscaledZLength);
			final int maxZ = MathHelper.floor(bounds.maxZ - halfUnscaledZLength);
			
			pehkui$initialClimbingPos = self.getBlockPos();
			
			for (final BlockPos pos : BlockPos.iterate(minX, minY, minZ, maxX, minY, maxZ))
			{
				setPosDirectly(pos);
				if (self.isClimbing())
				{
					info.setReturnValue(true);
					break;
				}
			}
			
			setPosDirectly(pehkui$initialClimbingPos);
			pehkui$initialClimbingPos = null;
		}
	}
}
