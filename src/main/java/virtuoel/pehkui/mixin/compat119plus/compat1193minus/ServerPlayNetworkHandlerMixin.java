package virtuoel.pehkui.mixin.compat119plus.compat1193minus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin
{
	@Redirect(method = "onPlayerInteractEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D"))
	private double pehkui$onPlayerInteractEntity$squaredDistanceTo(Entity entity, Vec3d eyePos)
	{
		final float margin = entity.getTargetingMargin();
		
		Box box = entity.getBoundingBox().expand(margin);
		
		final float interactionWidth = ScaleUtils.getInteractionBoxWidthScale(entity);
		final float interactionHeight = ScaleUtils.getInteractionBoxHeightScale(entity);
		
		if (interactionWidth != 1.0F || interactionHeight != 1.0F)
		{
			final double scaledXLength = box.getLengthX() * 0.5D * (interactionWidth - 1.0F);
			final double scaledYLength = box.getLengthY() * 0.5D * (interactionHeight - 1.0F);
			final double scaledZLength = box.getLengthZ() * 0.5D * (interactionWidth - 1.0F);
			final double scaledMarginWidth = margin * (interactionWidth - 1.0F);
			final double scaledMarginHeight = margin * (interactionHeight - 1.0F);
			
			box = box.expand(scaledXLength + scaledMarginWidth, scaledYLength + scaledMarginHeight, scaledZLength + scaledMarginWidth);
		}
		
		final double nearestX;
		if (eyePos.x < box.minX)
		{
			nearestX = box.minX;
		}
		else if (eyePos.x > box.maxX)
		{
			nearestX = box.maxX;
		}
		else
		{
			nearestX = eyePos.x;
		}
		
		final double nearestY;
		if (eyePos.y < box.minY)
		{
			nearestY = box.minY;
		}
		else if (eyePos.y > box.maxY)
		{
			nearestY = box.maxY;
		}
		else
		{
			nearestY = eyePos.y;
		}
		
		final double nearestZ;
		if (eyePos.z < box.minZ)
		{
			nearestZ = box.minZ;
		}
		else if (eyePos.z > box.maxZ)
		{
			nearestZ = box.maxZ;
		}
		else
		{
			nearestZ = eyePos.z;
		}
		
		return eyePos.squaredDistanceTo(nearestX, nearestY, nearestZ);
	}
}
