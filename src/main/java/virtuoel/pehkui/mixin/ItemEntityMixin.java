package virtuoel.pehkui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(ItemEntity.class)
public class ItemEntityMixin
{
	@ModifyArg(method = "tryMerge", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getEntitiesByClass(Ljava/lang/Class;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;"))
	private Box pehkui$tryMerge$box(Box box)
	{
		final ItemEntity self = (ItemEntity) (Object) this;
		final Box bounds = self.getBoundingBox();
		
		final double xExpand = box.getLengthX() - bounds.getLengthX();
		final double yExpand = box.getLengthY() - bounds.getLengthY();
		final double zExpand = box.getLengthZ() - bounds.getLengthZ();
		final float widthScale = ScaleUtils.getBoundingBoxWidthScale(self);
		final float heightScale = ScaleUtils.getBoundingBoxHeightScale(self);
		
		return bounds.expand(
			widthScale != 1.0F ? widthScale * xExpand : xExpand,
			heightScale != 1.0F ? heightScale * yExpand : yExpand,
			widthScale != 1.0F ? widthScale * zExpand : zExpand);
	}
}
