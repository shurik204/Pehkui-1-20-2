package virtuoel.pehkui.server.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.MixinEnvironment;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.netty.buffer.Unpooled;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import virtuoel.pehkui.Pehkui;
import virtuoel.pehkui.api.PehkuiConfig;
import virtuoel.pehkui.util.CommandUtils;
import virtuoel.pehkui.util.ConfigSyncUtils;
import virtuoel.pehkui.util.I18nUtils;
import virtuoel.pehkui.util.MixinTargetClasses;
import virtuoel.pehkui.util.NbtCompoundExtensions;

public class DebugCommand
{
	public static void register(final CommandDispatcher<ServerCommandSource> commandDispatcher)
	{
		final LiteralArgumentBuilder<ServerCommandSource> builder =
			CommandManager.literal("scale")
			.requires(source -> source.hasPermissionLevel(2));
		
		builder.then(CommandManager.literal("debug")
			.then(ConfigSyncUtils.registerConfigCommands())
		);
		
		if (FabricLoader.getInstance().isDevelopmentEnvironment() || PehkuiConfig.COMMON.enableCommands.get())
		{
			builder
				.then(CommandManager.literal("debug")
					.then(CommandManager.literal("delete_scale_data")
						.then(CommandManager.literal("uuid")
							.then(CommandManager.argument("uuid", StringArgumentType.string())
								.executes(context ->
								{
									final String uuidString = StringArgumentType.getString(context, "uuid");
									
									try
									{
										MARKED_UUIDS.add(UUID.fromString(uuidString));
									}
									catch (IllegalArgumentException e)
									{
										context.getSource().sendError(I18nUtils.translate("commands.pehkui.debug.delete.uuid.invalid", "Invalid UUID \"%s\".", uuidString));
										return 0;
									}
									
									return 1;
								})
							)
						)
						.then(CommandManager.literal("username")
							.then(CommandManager.argument("username", StringArgumentType.string())
								.executes(context ->
								{
									MARKED_USERNAMES.add(StringArgumentType.getString(context, "username").toLowerCase(Locale.ROOT));
									
									return 1;
								})
							)
						)
					)
					.then(CommandManager.literal("garbage_collect")
						.executes(context ->
						{
							context.getSource().sendError(Text.of("NOT UPDATED!"));
//							context.getSource().getPlayerOrThrow().networkHandler.sendPacket(
//								new CustomPayloadS2CPacket(Pehkui.DEBUG_PACKET,
//									new PacketByteBuf(Unpooled.buffer())
//									.writeEnumConstant(DebugPacketType.GARBAGE_COLLECT)
//								)
//							);
							
							System.gc();
							
							return 1;
						})
					)
				);
		}
		
		if (FabricLoader.getInstance().isDevelopmentEnvironment() || PehkuiConfig.COMMON.enableDebugCommands.get())
		{
			builder
				.then(CommandManager.literal("debug")
					.then(CommandManager.literal("run_mixin_tests")
						.executes(DebugCommand::runMixinTests)
					)
					.then(CommandManager.literal("run_tests")
						.executes(DebugCommand::runTests)
					)
				);
		}
		
		commandDispatcher.register(builder);
	}
	
	private static final Collection<UUID> MARKED_UUIDS = new HashSet<>();
	private static final Collection<String> MARKED_USERNAMES = new HashSet<>();
	
	public static boolean unmarkEntityForScaleReset(final Entity entity, final NbtCompound nbt)
	{
		if (entity instanceof PlayerEntity && MARKED_USERNAMES.remove(((PlayerEntity) entity).getGameProfile().getName().toLowerCase(Locale.ROOT)))
		{
			return true;
		}
		
		final NbtCompoundExtensions compound = ((NbtCompoundExtensions) nbt);
		
		return compound.pehkui_containsUuid("UUID") && MARKED_UUIDS.remove(compound.pehkui_getUuid("UUID"));
	}
	
	private static final List<EntityType<? extends Entity>> TYPES = Arrays.asList(
		EntityType.ZOMBIE,
		EntityType.CREEPER,
		EntityType.END_CRYSTAL,
		EntityType.BLAZE
	);
	
	private static int runTests(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		Entity entity = context.getSource().getEntityOrThrow();
		
		Direction dir = entity.getHorizontalFacing();
		Direction opposite = dir.getOpposite();
		
		Direction left = dir.rotateYCounterclockwise();
		Direction right = dir.rotateYClockwise();
		
		int distance = 4;
		int spacing = 2;
		
		int width = ((TYPES.size() - 1) * (spacing + 1)) + 1;
		
		Vec3d pos = entity.getPos();
		BlockPos.Mutable mut = new BlockPos.Mutable(pos.x, pos.y, pos.z).move(dir, distance).move(left, width / 2);
		
		World w = entity.getEntityWorld();
		
		for (EntityType<?> t : TYPES)
		{
			w.setBlockState(mut, Blocks.POLISHED_ANDESITE.getDefaultState());
			final Entity e = t.create(w);
			
			e.updatePositionAndAngles(mut.getX() + 0.5, mut.getY() + 1, mut.getZ() + 0.5, opposite.asRotation(), 0);
			e.refreshPositionAndAngles(mut.getX() + 0.5, mut.getY() + 1, mut.getZ() + 0.5, opposite.asRotation(), 0);
			e.setHeadYaw(opposite.asRotation());
			
			e.addCommandTag("pehkui");
			
			w.spawnEntity(e);
			
			mut.move(right, spacing + 1);
		}
		
		// TODO set command block w/ entity to void and block destroy under player pos
		
		int successes = -1;
		int total = -1;
		
		CommandUtils.sendFeedback(context.getSource(), () -> I18nUtils.translate("commands.pehkui.debug.test.success", "Tests succeeded: %d/%d", successes, total), false);
		
		return 1;
	}
	
	public static enum DebugPacketType
	{
		MIXIN_AUDIT,
		GARBAGE_COLLECT
		;
	}
	
	private static int runMixinTests(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		runMixinClassloadTests(
			t -> CommandUtils.sendFeedback(context.getSource(), () -> t, false),
			false,
			false,
			MixinTargetClasses.Common.CLASSES,
			MixinTargetClasses.Server.CLASSES
		);
		
		runMixinClassloadTests(
			t -> CommandUtils.sendFeedback(context.getSource(), () -> t, false),
			false,
			true,
			MixinTargetClasses.Common.INTERMEDIARY_CLASSES,
			MixinTargetClasses.Server.INTERMEDIARY_CLASSES
		);
		
		final Entity executor = context.getSource().getEntity();
		if (executor instanceof ServerPlayerEntity)
		{
			context.getSource().sendError(Text.of("NOT UPDATED!"));
//			((ServerPlayerEntity) executor).networkHandler.sendPacket(
//				new CustomPayloadS2CPacket(Pehkui.DEBUG_PACKET,
//					new PacketByteBuf(Unpooled.buffer())
//					.writeEnumConstant(DebugPacketType.MIXIN_AUDIT)
//				)
//			);
		}
		
		CommandUtils.sendFeedback(context.getSource(), () -> I18nUtils.translate("commands.pehkui.debug.audit.start", "Starting Mixin environment audit..."), false);
		MixinEnvironment.getCurrentEnvironment().audit();
		CommandUtils.sendFeedback(context.getSource(), () -> I18nUtils.translate("commands.pehkui.debug.audit.end", "Mixin environment audit complete!"), false);
		
		return 1;
	}
	
	public static void runMixinClassloadTests(final Consumer<Text> response, final boolean client, final boolean resolveMappings, final String[]... classes)
	{
		final Collection<String> succeeded = new ArrayList<String>();
		final Collection<String> failed = new ArrayList<String>();
		
		for (final String[] c : classes)
		{
			DebugCommand.classloadMixinTargets(c, resolveMappings, succeeded, failed);
		}
		
		final int successes = succeeded.size();
		final int fails = failed.size();
		final int total = successes + fails;
		
		if (fails > 0)
		{
			response.accept(I18nUtils.translate("commands.pehkui.debug.test.mixin.failed", "Failed classes: %s", "\"" + String.join("\", \"", failed) + "\""));
		}
		
		final String lang = "commands.pehkui.debug.test.mixin.results." + (resolveMappings ? "intermediary" : "named") + (client ? ".client" : ".server");
		final String defaultStr = "%d successes and %d fails out of %d mixined " + (resolveMappings ? "intermediary " : "") + (client ? "client" : "server") + " classes";
		response.accept(I18nUtils.translate(lang, defaultStr, successes, fails, total));
	}
	
	public static void classloadMixinTargets(final String[] classes, final boolean resolveMappings, final Collection<String> succeeded, final Collection<String> failed)
	{
		for (String name : classes)
		{
			name = name.replace('/', '.');
			
			if (resolveMappings)
			{
				name = FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", name);
			}
			
			try
			{
				Class.forName(name);
				succeeded.add(name);
			}
			catch (Exception e)
			{
				failed.add(name);
			}
		}
	}
}
