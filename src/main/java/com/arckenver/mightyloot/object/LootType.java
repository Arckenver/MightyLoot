package com.arckenver.mightyloot.object;

import java.util.Hashtable;
import java.util.Map.Entry;

import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextParseException;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.arckenver.mightyloot.MightyLootPlugin;

public class LootType
{
	private String id;
	private Text display;
	private Hashtable<ItemType, Interval> items;
	
	public LootType(String id, String jsonDisplay)
	{
		this.id = id;
		try
		{
			this.display = TextSerializers.JSON.deserialize(jsonDisplay);
		}
		catch (TextParseException e)
		{
			MightyLootPlugin.getLogger().warn("Invalid json format for loot type display name : " + jsonDisplay);
			this.display = Text.of(id);
		}
		this.items = new Hashtable<ItemType, Interval>();
	}
	
	public String getId()
	{
		return id;
	}
	
	public Text getDisplay()
	{
		return display;
	}
	
	public void addItem(ItemType itemType, int min, int max)
	{
		items.put(itemType, new Interval(min, max));
	}
	
	public Hashtable<ItemType, Interval> getItems()
	{
		return items;
	}
	
	public void fillChest(Inventory inv)
	{
		for (Entry<ItemType, Interval> e : items.entrySet())
		{
			int n = e.getValue().getRandom();
			inv.offer(ItemStack.builder().itemType(e.getKey()).quantity(n).build());
		}
	}
}
