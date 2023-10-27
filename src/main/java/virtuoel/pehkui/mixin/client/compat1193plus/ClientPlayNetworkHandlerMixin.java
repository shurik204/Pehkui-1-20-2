package virtuoel.pehkui.mixin.client.compat1193plus;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin
{
	private PlayerEntity oldPlayer;
	@Inject(method = "onPlayerRespawn", at = @At("HEAD"))
	void storeOldPlayer(PlayerRespawnS2CPacket packet, CallbackInfo info)
	{
		oldPlayer = MinecraftClient.getInstance().player;
	}

	@Redirect(method = "onPlayerRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;addEntity(Lnet/minecraft/entity/Entity;)V"))
	void initScales(ClientWorld instance, Entity entity) {
		ScaleUtils.loadScaleOnRespawn((PlayerEntity) entity, oldPlayer, false);
		instance.addEntity(entity);
	}




//	@Inject(method = "onPlayerRespawn(Lnet/minecraft/network/packet/s2c/play/PlayerRespawnS2CPacket;)V", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;init()V"))
//	private void pehkui$onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo info, RegistryKey<World> dimension, RegistryEntry<DimensionType> dimensionType, ClientPlayerEntity oldPlayer, int id, String brand, ClientPlayerEntity newPlayer)
//	{
//		ScaleUtils.loadScaleOnRespawn(newPlayer, oldPlayer, packet.hasFlag((byte) 1));
//	}
}
