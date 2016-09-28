package com.arckenver.mightyloot.listener;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.mightyloot.DataHandler;
import com.arckenver.mightyloot.LanguageHandler;
import com.arckenver.mightyloot.object.Loot;

public class InteractListener
{
	@Listener
	public void onInteract(InteractBlockEvent event, @First Player player)
	{
		World world = player.getWorld();
		if (!event.getTargetBlock().getLocation().isPresent())
		{
			return;
		}
		Location<World> blockLoc = event.getTargetBlock().getLocation().get();
		if (DataHandler.hasLoot(world.getName()))
		{
			Loot loot = DataHandler.getLoot(world.getName());
			if (loot.getLoc().getBlockPosition().equals(blockLoc.getBlockPosition()) || loot.getLoc().getBlockPosition().add(0, -1, 0).equals(blockLoc.getBlockPosition()))
			{
				DataHandler.unregisterLoot(world.getName());
				
				String[] s1 = LanguageHandler.get("BA").split("\\{LOOT\\}");
				String[] s2 = s1[0].split("\\{PLAYER\\}");
				String[] s3 = s1[1].split("\\{PLAYER\\}");
				
				MessageChannel.TO_ALL.send(Text.builder()
						.append(Text.of(TextColors.GOLD, (s2.length > 0) ? s2[0] : ""))
						.append(Text.of(TextColors.YELLOW, (s2.length > 1) ? player.getName() : ""))
						.append(Text.of(TextColors.GOLD, (s2.length > 1) ? s2[1] : ""))
						.append(loot.getType().getDisplay())
						.append(Text.of(TextColors.GOLD, (s3.length > 0) ? s3[0] : ""))
						.append(Text.of(TextColors.YELLOW, (s3.length > 1) ? player.getName() : ""))
						.append(Text.of(TextColors.GOLD, (s3.length > 1) ? s3[1] : ""))
						.build());
			}
		}
	}
}
