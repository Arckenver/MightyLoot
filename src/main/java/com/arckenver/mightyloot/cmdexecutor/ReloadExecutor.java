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
import com.arckenver.mightyloot.LanguageHandler;
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
				String[] s1 = LanguageHandler.get("BB").split("\\{LOOT\\}");
				String[] s2 = s1[0].split("\\{WORLD\\}");
				String[] s3 = s1[1].split("\\{WORLD\\}");
				
				MessageChannel.TO_ALL.send(Text.builder()
						.append(Text.of(TextColors.GOLD, (s2.length > 0) ? s2[0] : ""))
						.append(Text.of(TextColors.YELLOW, (s2.length > 1) ? lootConfig.getWorldName() : ""))
						.append(Text.of(TextColors.GOLD, (s2.length > 1) ? s2[1] : ""))
						.append(loot.getType().getDisplay())
						.append(Text.of(TextColors.GOLD, (s3.length > 0) ? s3[0] : ""))
						.append(Text.of(TextColors.YELLOW, (s3.length > 1) ? lootConfig.getWorldName() : ""))
						.append(Text.of(TextColors.GOLD, (s3.length > 1) ? s3[1] : ""))
						.build());
			}
		}
		LanguageHandler.load();
		ConfigHandler.load();
		for (LootConfig lootConfig : ConfigHandler.getLootConfigs())
		{
			MightyLootPlugin.getInstance().newSpawnTask(lootConfig, lootConfig.getFrequency());
		}
		src.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.get("AE")));
		return CommandResult.success();
	}
}
