package com.arckenver.mightyloot.cmdexecutor;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.mightyloot.ConfigHandler;
import com.arckenver.mightyloot.DataHandler;
import com.arckenver.mightyloot.MightyLootPlugin;
import com.arckenver.mightyloot.object.Loot;
import com.arckenver.mightyloot.object.LootConfig;

public class ReloadExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		for (LootConfig lootConfig : MightyLootPlugin.getInstance().getSpawnTasks().keySet())
		{
			MightyLootPlugin.getInstance().cancelSpawnTask(lootConfig);
			Loot loot = DataHandler.removeLoot(lootConfig.getWorldName());
			if (loot != null)
			{
				MessageChannel.TO_ALL.send(Text.builder()
						.append(Text.of(TextColors.GOLD, "The last "))
						.append(loot.getType().getDisplay())
						.append(Text.of(TextColors.GOLD, " in "))
						.append(Text.of(TextColors.YELLOW, lootConfig.getWorldName()))
						.append(Text.of(TextColors.GOLD, " has now vanished !"))
						.build());
			}
		}
		ConfigHandler.load();
		for (LootConfig lootConfig : ConfigHandler.getLootConfigs())
		{
			MightyLootPlugin.getInstance().newSpawnTask(lootConfig, lootConfig.getFrequency());
		}
		src.sendMessage(Text.of(TextColors.AQUA, "Config file has been reloaded, see console if an error occured"));
		return CommandResult.success();
	}
}
