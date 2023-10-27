package virtuoel.pehkui.mixin.compat116minus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(ShulkerEntity.class)
public class ShulkerEntityMixin
{
	@ModifyArg(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/ShulkerEntity;setBoundingBox(Lnet/minecraft/util/math/Box;)V"))
	private Box pehkui$tick$box(Box box)
	{
		final ShulkerEntity entity = (ShulkerEntity) (Object) this;
		
		final Direction facing = entity.getAttachedFace().getOpposite();
		
		final double xLength = box.getLengthX() / -2.0D;
		final double yLength = box.getLengthY() / -2.0D;
		final double zLength = box.getLengthZ() / -2.0D;
		
		final float widthScale = ScaleUtils.getBoundingBoxWidthScale(entity);
		final float heightScale = ScaleUtils.getBoundingBoxHeightScale(entity);
		
		if (widthScale != 1.0F || heightScale != 1.0F)
		{
			final double dX = xLength * (1.0D - widthScale); 
			final double dY = yLength * (1.0D - heightScale); 
			final double dZ = zLength * (1.0D - widthScale); 
			box = box.expand(dX, dY, dZ);
			box = box.offset(dX * facing.getOffsetX(), dY * facing.getOffsetY(), dZ * facing.getOffsetZ());
		}
		
		return box;
	}
}
