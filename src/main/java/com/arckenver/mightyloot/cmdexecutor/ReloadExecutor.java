package com.arckenver.mightyloot.cmdexecutor;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.mightyloot.ConfigHandler;
import com.arckenver.mightyloot.DataHandler;
import com.arckenver.mightyloot.LanguageHandler;

public class ReloadExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		DataHandler.cancelSpawnTasks();
		DataHandler.removeAllLoots();
		LanguageHandler.load();
		ConfigHandler.load();
		DataHandler.startSpawnTasks();
		src.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.get("AE")));
		return CommandResult.success();
	}
}
