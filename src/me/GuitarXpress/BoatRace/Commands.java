package me.GuitarXpress.BoatRace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

public class Commands implements CommandExecutor {

	Main plugin;

	public GameManager gm;

	int laps;

	static Map<UUID, String> pArena = new HashMap<UUID, String>();

	public Commands(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {

		if (!(sender instanceof Player))
			return true;

		Player player = (Player) sender;

		gm = new GameManager(plugin);

		if (cmd.getName().equalsIgnoreCase("boatrace") || cmd.getName().equalsIgnoreCase("btr")) {
			if (args.length == 0) {
				Bukkit.dispatchCommand(player, "boatrace help");
				return true;
			}
			if (args.length == 1) {

				if (args[0].equalsIgnoreCase("help")) {
					player.sendMessage(prefix() + "§6Useful Commands: §e/boatrace §7| §e/btr");
					player.sendMessage("§6/boatrace join <track> §7- §eJoins lobby for specified track.\n"
							+ "§6/boatrace leave §7- §eLeaves current track/lobby.\n"
							+ "§6/boatrace help §7- §eShows Useful Commands.\n");
					if (player.hasPermission("br.admin")) {
						player.sendMessage(prefix() + "§6Admin Commands: ");
						player.sendMessage("§6/boatrace add <name> <laps> §7- §eAdds new track.\n"
								+ "§6/boatrace remove <track> §7- §eRemoves specified track.\n"
								+ "§6/boatrace setlobby §7- §eSets BoatRace lobby.\n"
								+ "§6/boatrace setspawns <track> §7- §eSets player spawnpoints for specified track.\n"
								+ "§6/boatrace setbounds <track> §7- §eSets boundaries for specified track.\n"
								+ "§6/boatrace setstate <track> <state> §7- §eSets track state.\n");
					}
					return true;
				}

				if (args[0].equalsIgnoreCase("tracks")) {
					player.sendMessage(prefix() + "§eAvailable Tracks: " + ArenaManager.getArenaList());
					return true;
				}
			}

			if (player.hasPermission("br.admin")) {
				if (args[0].equalsIgnoreCase("get")) {
					String name = args[1];
					Bukkit.broadcastMessage(ArenaManager.getArena(name).toString());
				}
				if (args[0].equalsIgnoreCase("add")) {
					if (args.length == 1) {
						player.sendMessage(prefix() + "§cMissing Arguments (/boatrace add <name>)");
						return true;
					} else if (args.length >= 4) {
						player.sendMessage(prefix() + "§cToo Many Arguments (/boatrace add <name> <laps>)");
						return true;
					}
					String name = args[1];
					try {
						laps = Integer.parseInt(args[2]);
					} catch (Exception e) {
						if (args.length < 3)
							player.sendMessage(prefix() + "§cMissing Arguments (/boatrace add <name> <laps>)");
						else
							player.sendMessage(prefix() + "§cSecond argument must be a number.");
						return true;
					}
					if (ArenaManager.exists(name)) {
						player.sendMessage(prefix() + "§cRace track already exists!");
						return true;
					}
					if (ArenaManager.getLobby() == null) {
						player.sendMessage(prefix()
								+ "§cPlease set a lobby before adding race tracks with §6/boatrace setlobby§c.");
						return true;
					}

					ArenaManager.registerArena(name, null, STATUS.SETTING_UP, laps, null, null);
					System.out.println(ArenaManager.arenas.toString());
					player.sendMessage(prefix() + "§aAdded race §e" + name + ".");
					player.sendMessage(
							prefix() + "§eStart setting player spawnpoints with §6/boatrace setspawns <race track>§e.");
					player.sendMessage(prefix() + "§eStart track boundaries with §6/boatrace setbounds <race track>§e.");
					player.sendMessage(prefix() + "§eEach track has §62 checkpoints§e and §61 finish line§e.\n"
							+ "§eMark the first checkpoint by placing §fwhite whool§e under the track and §8black wool §efor the second checkpoint.\n"
							+ "§eMark the finish line by placing §8bedrock §eunder the track.");
					return true;
				}
				if (args[0].equalsIgnoreCase("setlobby")) {

					if (args.length == 1) {
						ArenaManager.setLobby(player.getLocation());
						player.sendMessage(prefix() + "§eLobby set.");
						return true;
					} else {
						player.sendMessage(prefix() + "§cToo Many Arguments (/boatrace setlobby)");
						return true;
					}

				}

				if (args[0].equalsIgnoreCase("setspawns")) {
					if (args.length == 1) {
						player.sendMessage(prefix() + "§cMissing Arguments! (/boatrace setspawns <race track>)");
						return true;
					} else if (args.length >= 3) {
						player.sendMessage(prefix() + "§cToo Many Arguments! (/boatrace setspawns <race track>)");
						return true;
					}

					if (args.length == 2) {
						if (ArenaManager.exists(args[1])) {
							ItemStack item = ItemManager.playerPos; // Get spawnpoint item
							ItemMeta meta = item.getItemMeta();
							List<String> lore = item.getItemMeta().getLore();
							lore.set(2, "§6" + args[1]); // Set Arena on item lore
							meta.setLore(lore);
							item.setItemMeta(meta);

							player.getInventory().addItem(item);
							player.sendMessage(
									prefix() + "§6Right Click §eto set Player 1 spawnpoint to your current position.");
							return true;
						} else {
							player.sendMessage(prefix() + "§cInvalid Race!");
							return true;
						}
					}
				}
				if (args[0].equalsIgnoreCase("setbounds")) {
					if (args.length == 1) {
						player.sendMessage(prefix() + "§cMissing Arguments! (/boatrace setbounds <race track>)");
						return true;
					} else if (args.length >= 3) {
						player.sendMessage(prefix() + "§cToo Many Arguments! (/boatrace setbounds <race track>)");
						return true;
					}

					if (args.length == 2) {
						if (ArenaManager.exists(args[1])) {
							ItemStack item = ItemManager.arenaPos; // Get spawnpoint item
							ItemMeta meta = item.getItemMeta();
							List<String> lore = item.getItemMeta().getLore();
							lore.set(2, "§6" + args[1]); // Set Arena on item lore
							meta.setLore(lore);
							item.setItemMeta(meta);

							player.getInventory().addItem(item);
							player.sendMessage(prefix() + "§6Right Click §eto set first corner of track boundaries.");
							return true;
						} else {
							player.sendMessage(prefix() + "§cInvalid Race!");
							return true;
						}
					}
				}
			}

			if (player.hasPermission("br.state")) {
				if (args[0].equalsIgnoreCase("setstate")) {
					if (args.length <= 2) {
						player.sendMessage(prefix() + "§cMissing Arguments! (/boatrace setstate <race track> <state>)");
						return true;
					} else if (args.length >= 4) {
						player.sendMessage(
								prefix() + "§cToo Many Arguments! (/boatrace setstate <race track> <state>)");
						return true;
					}
					if (args.length == 3) {
						if (ArenaManager.exists(args[1])) {
							switch (args[2]) {
							case "setup":
								ArenaManager.getArena(args[1]).setStatus(STATUS.SETTING_UP);
								player.sendMessage(prefix() + "§eSet §6" + args[1] + " §estate to §6Setting Up§e.");
								break;
							case "joinable":
								ArenaManager.getArena(args[1]).setStatus(STATUS.JOINABLE);
								player.sendMessage(prefix() + "§eSet §6" + args[1] + " §estate to §aJoinable§e.");
								break;
							case "started":
								ArenaManager.getArena(args[1]).setStatus(STATUS.JOINABLE);
								player.sendMessage(prefix() + "§eSet §6" + args[1] + " §estate to §aStarted§e.");
								break;
							case "ongoing":
								ArenaManager.getArena(args[1]).setStatus(STATUS.ONGOING);
								player.sendMessage(prefix() + "§eSet §6" + args[1] + " §estate to §6Ongoing§e.");
								break;
							case "cancelled":
								ArenaManager.getArena(args[1]).setStatus(STATUS.CANCELLED);
								player.sendMessage(prefix() + "§eSet §6" + args[1] + " §estate to §cCancelled§e.");
								break;
							case "ended":
								ArenaManager.getArena(args[1]).setStatus(STATUS.ENDED);
								player.sendMessage(prefix() + "§eSet §6" + args[1] + " §estate to §cEnded§e.");
								break;
							case "unavailable":
								ArenaManager.getArena(args[1]).setStatus(STATUS.UNAVAILABLE);
								player.sendMessage(prefix() + "§eSet §6" + args[1] + " §estate to §cUnavailable§e.");
								break;
							default:
								player.sendMessage(prefix()
										+ "§cStates: §esetup, joinable, ongoing, cancelled, ended, unavailable.");
								break;
							}
						} else {
							player.sendMessage(prefix() + "§cInvalid Race!");
							return true;
						}
					}
				}
			}

			if (player.hasPermission("br.use")) {
				if (args[0].equalsIgnoreCase("join")) {
					if (args.length == 1) {
						player.sendMessage(prefix() + "§cPlease specify the race track. (/boatrace join <race track>)");
						return true;
					}

					if (args.length >= 3) {
						player.sendMessage(prefix() + "§cToo Many Arguments! (/boatrace join <race track>)");
						return true;
					}

					if (args.length == 2) {
						if (ArenaManager.exists(args[1])) {
							if (ArenaManager.getArena(args[1]).getStatus() == STATUS.JOINABLE
									|| ArenaManager.getArena(args[1]).getStatus() == STATUS.STARTING) {
								if (ArenaManager.getArena(args[1]).getPlayers().size() < 4) {
									if (ArenaManager.getArena(args[1]).getSpawns() != null) {
										if (pArena.get(player.getUniqueId()) == null) {
											ArenaManager.getArena(args[1]).join(player, args[1]);
											player.sendMessage(prefix() + "§aJoined §e" + args[1]);
											player.teleport(ArenaManager.getLobby());
											gm.checkPlayers(args[1]);
											return true;
										} else {
											player.sendMessage(prefix() + "§cYou're on a race already!");
										}
									} else {
										player.sendMessage(prefix() + "§cSorry! This track has not been setup yet.");
									}
								} else {
									player.sendMessage(prefix() + "§cSorry! §7Game is full!");
									return true;
								}
							} else {
								player.sendMessage(prefix() + "§cUnable to join track: "
										+ ArenaManager.getArena(args[1]).getStatus().toString());
								return true;
							}
						} else {
							player.sendMessage(prefix() + "§cInvalid Track!");
							return true;
						}
					}
				}

				if (args[0].equalsIgnoreCase("leave")) {
					if (args.length >= 1) {
						if (pArena.containsKey(player.getUniqueId())) {
							if (pArena.get(player.getUniqueId()) != null) {
								if (ArenaManager.getArena(pArena.get(player.getUniqueId())).getPlayers().isEmpty()) {
									ArenaManager.getArena(pArena.get(player.getUniqueId())).setStatus(STATUS.JOINABLE);
								}
								ArenaManager.getArena(pArena.get(player.getUniqueId())).leave(player);
								if (player.isInsideVehicle()) {
									player.getVehicle().remove();
								}
								player.sendMessage(prefix() + "§cLeft race.");
								player.teleport(ArenaManager.getLobby());
								player.setGameMode(GameMode.SURVIVAL);
								player.removePotionEffect(PotionEffectType.JUMP);
								player.setWalkSpeed(0.2f);
								return true;
							} else {
								player.sendMessage(prefix() + "§cYou're not inside a boat race!");
								return true;
							}
						} else {
							player.sendMessage(prefix() + "§cYou're not inside a boat race!");
							return true;
						}
					}
				}

			} else {
				player.sendMessage(prefix() + "§cSorry! §7You can't do that!");
				return true;
			}

		}
		return true;
	}

	public String prefix() {
		return "§8[§bBoatRace§8]: ";
	}

}
