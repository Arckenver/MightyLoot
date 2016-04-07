package com.arckenver.mightyloot;

import java.io.File;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import com.arckenver.mightyloot.cmdelement.WorldCommandElement;
import com.arckenver.mightyloot.cmdexecutor.CancelExecutor;
import com.arckenver.mightyloot.cmdexecutor.FindExecutor;
import com.arckenver.mightyloot.cmdexecutor.HuntExecutor;
import com.arckenver.mightyloot.cmdexecutor.MightyLootExecutor;
import com.arckenver.mightyloot.cmdexecutor.SpawnExecutor;
import com.arckenver.mightyloot.listener.InteractListener;
import com.arckenver.mightyloot.object.LootConfig;
import com.arckenver.mightyloot.task.SpawnLootRunnable;
import com.google.inject.Inject;

@Plugin(id="com.arckenver.mightyloot", name="MightyLoot", version="1.0", description="A treasurehunt-like sponge plugin.")
public class MightyLootPlugin
{
	private File rootDir;
	
	public static MightyLootPlugin plugin;
	
	@Inject
	private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private File defaultConfFile;
	
	private Hashtable<LootConfig, Task> spawnTasks;
	
	@Listener
	public void onStart(GameStartingServerEvent event)
	{
		logger.info("Plugin starting...");
		plugin = this;
		
		spawnTasks = new Hashtable<LootConfig, Task>();
		
		rootDir = new File(defaultConfFile.getParentFile(), "mightyloot");
		
		ConfigHandler.init(rootDir);
		ConfigHandler.load();
		
		DataHandler.init();
		
		for (LootConfig lootConfig : ConfigHandler.getLootConfigs())
		{
			newSpawnTask(lootConfig, lootConfig.getFrequency());
		}
		
		CommandSpec spawnCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("mightyloot.command.spawn")
				.arguments(GenericArguments.optional(new WorldCommandElement(Text.of("world"))))
				.executor(new SpawnExecutor())
				.build();
		
		CommandSpec cancelCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("mightyloot.command.cancel")
				.arguments(GenericArguments.optional(new WorldCommandElement(Text.of("world"))))
				.executor(new CancelExecutor())
				.build();
		
		CommandSpec huntCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("mightyloot.command.hunt")
				.arguments()
				.executor(new HuntExecutor())
				.build();
		
		CommandSpec findCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("mightyloot.command.find")
				.arguments(GenericArguments.optional(new WorldCommandElement(Text.of("world"))))
				.executor(new FindExecutor())
				.build();
		
		CommandSpec mightyLootCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("mightyloot.command")
				.arguments()
				.executor(new MightyLootExecutor())
				.child(spawnCmd, "spawn")
				.child(huntCmd, "hunt", "h")
				.child(findCmd, "find")
				.child(cancelCmd, "cancel")
				.build();
		
		Sponge.getCommandManager().register(this, mightyLootCmd, "mightyloot", "ml");
		
		Sponge.getEventManager().registerListeners(this, new InteractListener());
		
		logger.info("Plugin started.");
	}
	
	@Listener
	public void onStop(GameStoppingServerEvent event)
	{
		for (LootConfig lootConfig : getInstance().getSpawnTasks().keySet())
		{
			DataHandler.removeLoot(lootConfig.getWorldName());
			getInstance().cancelSpawnTask(lootConfig);
		}
	}
	
	public Hashtable<LootConfig, Task> getSpawnTasks()
	{
		return spawnTasks;
	}
	
	public void newSpawnTask(LootConfig lootConfig)
	{
		newSpawnTask(lootConfig, 0);
	}
	
	public void newSpawnTask(LootConfig lootConfig, int delay)
	{
		spawnTasks.put(lootConfig, Sponge.getScheduler()
				.createTaskBuilder()
				.execute(new SpawnLootRunnable(lootConfig))
				.interval(lootConfig.getFrequency(), TimeUnit.SECONDS)
				.delay(delay, TimeUnit.SECONDS)
				.name("MightyLoot - SpawnLoot Task - " + lootConfig.getWorldName())
				.submit(this));
	}
	
	public void cancelSpawnTask(LootConfig lootConfig)
	{
		for (Entry<LootConfig, Task> e : spawnTasks.entrySet())
		{
			if (e.getKey().equals(lootConfig))
			{
				e.getValue().cancel();
			}
		}
	}
	
	public static MightyLootPlugin getInstance()
	{
		return plugin;
	}

	public static Logger getLogger()
	{
		return getInstance().logger;
	}
}
