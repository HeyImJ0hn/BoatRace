package me.GuitarXpress.BoatRace;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemManager {

	public static ItemStack playerPos;
	public static ItemStack arenaPos;
	
	public static void init() {
		createPlayerPos();
		createArenaPos();
	}
	
	private static void createArenaPos() {
		ItemStack item = new ItemStack(Material.STICK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6Set Track Boundaries");
		meta.addEnchant(Enchantment.LUCK, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§7Right click on each corner to set boundaries.");
		lore.add(" ");
		lore.add("§6Name");
		lore.add("§eCorner 1");
		lore.add(" ");
		lore.add("§9BoatRace");
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
		lore.add("§7Stand on the spawn position and right click to set player spawnpoint.");
		lore.add(" ");
		lore.add("§6Name");
		lore.add("§ePlayer 1");
		lore.add(" ");
		lore.add("§9BoatRace");
		meta.setLore(lore);
		item.setItemMeta(meta);
		playerPos = item;
	}
	
}
