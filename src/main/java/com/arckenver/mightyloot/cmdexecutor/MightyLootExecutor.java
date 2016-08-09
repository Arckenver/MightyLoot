package com.arckenver.mightyloot.cmdexecutor;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.mightyloot.LanguageHandler;

public class MightyLootExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		Text.Builder builder = Text.builder()
				.append(Text.of(TextColors.GOLD, "--------{ ", TextColors.YELLOW, "/mightyloot", TextColors.GOLD, " }--------"));
		
		if (src.hasPermission("mightyloot.command.hunt"))
		{
			builder.append(Text.of(TextColors.GOLD, "\n/ml hunt", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.get("CA")));
		}
		if (src.hasPermission("mightyloot.command.find"))
		{
			builder.append(Text.of(TextColors.GOLD, "\n/ml find", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.get("CB")));
		}
		if (src.hasPermission("mightyloot.command.spawn"))
		{
			builder.append(Text.of(TextColors.GOLD, "\n/ml spawn [world]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.get("CC")));
		}
		if (src.hasPermission("mightyloot.command.cancel"))
		{
			builder.append(Text.of(TextColors.GOLD, "\n/ml cancel [world]", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.get("CD")));
		}
		if (src.hasPermission("mightyloot.command.reload"))
		{
			builder.append(Text.of(TextColors.GOLD, "\n/ml reload", TextColors.GRAY, " - ", TextColors.YELLOW, LanguageHandler.get("CE")));
		}
		
		src.sendMessage(builder.build());
		return CommandResult.success();
	}
}
