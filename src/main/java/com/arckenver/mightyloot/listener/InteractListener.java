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
import com.arckenver.mightyloot.object.Loot;

public class InteractListener
{
	@Listener
	public void onInteract(InteractBlockEvent event, @First Player player)
	{
		World world = player.getWorld();
		Location<World> blockLoc = event.getTargetBlock().getLocation().get();
		if (DataHandler.hasLoot(world.getName()))
		{
			Loot loot = DataHandler.getLoot(world.getName());
			if (loot.getLoc().getBlockPosition().equals(blockLoc.getBlockPosition()) || loot.getLoc().getBlockPosition().add(0, -1, 0).equals(blockLoc.getBlockPosition()))
			{
				DataHandler.unregisterLoot(world.getName());
				MessageChannel.TO_ALL.send(
						Text
						.builder()
						.append(Text.of(TextColors.GOLD, "The "))
						.append(loot.getType().getDisplay())
						.append(Text.of(TextColors.GOLD, " has been found by "))
						.append(Text.of(TextColors.YELLOW, player.getName()))
						.build());
			}
		}
	}
}
