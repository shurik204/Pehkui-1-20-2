package virtuoel.pehkui.util;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.Identifier;
import virtuoel.pehkui.Pehkui;
import virtuoel.pehkui.command.argument.ScaleModifierArgumentType;
import virtuoel.pehkui.command.argument.ScaleOperationArgumentType;
import virtuoel.pehkui.command.argument.ScaleTypeArgumentType;

public class CommandUtils
{
	public static void registerArgumentTypes(ArgumentTypeConsumer consumer)
	{
		consumer.register(Pehkui.id("scale_type").toString(), ScaleTypeArgumentType.class, ScaleTypeArgumentType::scaleType);
		consumer.register(Pehkui.id("scale_modifier").toString(), ScaleModifierArgumentType.class, ScaleModifierArgumentType::scaleModifier);
		consumer.register(Pehkui.id("scale_operation").toString(), ScaleOperationArgumentType.class, ScaleOperationArgumentType::operation);
	}
	
	@FunctionalInterface
	public interface ArgumentTypeConsumer
	{
		<T extends ArgumentType<?>> void register(String id, Class<T> argClass, Supplier<T> supplier);
	}
	
	public static boolean testFloatRange(NumberRange.FloatRange range, float value)
	{
		return range.test(value);
	}
	
	public static <T extends ArgumentType<?>> void registerConstantArgumentType(String id, Class<T> argClass, Supplier<T> supplier)
	{
		ArgumentTypes.register(id, argClass, new ConstantArgumentSerializer<T>(supplier));
	}
	
	public static CompletableFuture<Suggestions> suggestIdentifiersIgnoringNamespace(String namespace, Iterable<Identifier> candidates, SuggestionsBuilder builder)
	{
		forEachMatchingIgnoringNamespace(
			namespace,
			candidates,
			builder.getRemaining().toLowerCase(Locale.ROOT),
			Function.identity(),
			id -> builder.suggest(String.valueOf(id))
		);
		
		return builder.buildFuture();
	}
	
	public static <T> void forEachMatchingIgnoringNamespace(String namespace, Iterable<T> candidates, String string, Function<T, Identifier> idFunc, Consumer<T> action)
	{
		final boolean hasColon = string.indexOf(':') > -1;
		
		Identifier id;
		for (final T object : candidates)
		{
			id = idFunc.apply(object);
			if (hasColon)
			{
				if (wordStartsWith(string, id.toString(), '_'))
				{
					action.accept(object);
				}
			}
			else if (
				wordStartsWith(string, id.getNamespace(), '_') ||
				id.getNamespace().equals(namespace) &&
				wordStartsWith(string, id.getPath(), '_')
			)
			{
				action.accept(object);
			}
		}
	}
	
	public static boolean wordStartsWith(String string, String substring, char wordSeparator)
	{
		for (int i = 0; !substring.startsWith(string, i); i++)
		{
			i = substring.indexOf(wordSeparator, i);
			if (i < 0)
			{
				return false;
			}
		}
		
		return true;
	}
}
