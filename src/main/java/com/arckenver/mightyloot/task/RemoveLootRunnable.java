package com.arckenver.mightyloot.task;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.mightyloot.DataHandler;
import com.arckenver.mightyloot.LanguageHandler;
import com.arckenver.mightyloot.object.Loot;

public class RemoveLootRunnable implements Runnable
{
	private Loot loot;
	
	public RemoveLootRunnable(Loot loot)
	{
		this.loot = loot;
	}
	
	public void run()
	{
		if (DataHandler.removeLoot(loot))
		{
			String[] s1 = LanguageHandler.get("BB").split("\\{LOOT\\}");
			String[] s2 = s1[0].split("\\{WORLD\\}");
			String[] s3 = s1[1].split("\\{WORLD\\}");
			
			MessageChannel.TO_ALL.send(Text.builder()
					.append(Text.of(TextColors.GOLD, (s2.length > 0) ? s2[0] : ""))
					.append(Text.of(TextColors.YELLOW, (s2.length > 1) ? loot.getWorld().getName() : ""))
					.append(Text.of(TextColors.GOLD, (s2.length > 1) ? s2[1] : ""))
					.append(loot.getType().getDisplay())
					.append(Text.of(TextColors.GOLD, (s3.length > 0) ? s3[0] : ""))
					.append(Text.of(TextColors.YELLOW, (s3.length > 1) ? loot.getWorld().getName() : ""))
					.append(Text.of(TextColors.GOLD, (s3.length > 1) ? s3[1] : ""))
					.build());
		}
	}
}
