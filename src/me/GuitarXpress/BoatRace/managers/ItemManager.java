package me.guitarxpress.boatrace.managers;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemManager {

	public static ItemStack playerPos;
	public static ItemStack arenaPos;
	
	public static ItemStack[] editMode = new ItemStack[2];
	
	public static void init() {
		createPlayerPos();
		createArenaPos();
		editMode[0] = playerPos;
		editMode[1] = arenaPos;
	}
	
	private static void createArenaPos() {
		ItemStack item = new ItemStack(Material.STICK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6Set Track Boundaries");
		meta.addEnchant(Enchantment.LUCK, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§6Left Click §7to set corner 1.");
		lore.add("§6Right Click §7to ser corner 2.");
		lore.add("§bBoatRace");
		meta.setLore(lore);
		item.setItemMeta(meta);
		arenaPos = item;
	}

	private static void createPlayerPos() {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6Set Player Position");
		meta.addEnchant(Enchantment.LUCK, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§7Stand on the spawn position and §6Right Click §7to set player spawnpoint.");
		lore.add("§ePlayer 1");
		lore.add("§bBoatRace");
		meta.setLore(lore);
		item.setItemMeta(meta);
		playerPos = item;
	}
	
}
