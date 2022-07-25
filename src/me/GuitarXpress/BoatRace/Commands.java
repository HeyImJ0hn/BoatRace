package me.guitarxpress.boatrace;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.guitarxpress.boatrace.enums.Status;
import me.guitarxpress.boatrace.events.EditMode;
import me.guitarxpress.boatrace.managers.GameManager;

public class Commands implements CommandExecutor {

	BoatRace plugin;

	public GameManager gm;

	int laps;

	public Commands(BoatRace plugin) {
		this.plugin = plugin;
		gm = plugin.getGameManager();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {

		if (!(sender instanceof Player))
			return true;

		Player player = (Player) sender;

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
							+ "§6/boatrace tracks §7- §eShows available tracks.\n"
							+ "§6/boatrace help §7- §eShows Useful Commands.");
					if (player.hasPermission("br.admin")) {
						player.sendMessage(prefix() + "§6Admin Commands: ");
						player.sendMessage("§6/boatrace add <name> <laps> §7- §eAdds new track.\n"
								+ "§6/boatrace remove <track> §7- §eRemoves specified track.\n"
								+ "§6/boatrace setlobby §7- §eSets BoatRace lobby.\n"
								+ "§6/boatrace edit <track> §7- §eToggles edit mode for specified track.\n"
								+ "§6/boatrace setstatus <track> <status> §7- §eSets track status.\n");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("tracks")) {
					player.sendMessage(prefix() + "§eAvailable Tracks: ");
					for (Arena arena : gm.getArenas()) {
						player.sendMessage("§e" + arena.getName() + " - §6" + arena.getStatus().toString());
					}
					return true;
				}
			}

			if (player.hasPermission("btr.admin")) {
				// Add
				if (args[0].equalsIgnoreCase("add")) {
					if (args.length == 1) {
						player.sendMessage(prefix() + "§cMissing Arguments (/boatrace add <name> <laps>)");
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

					if (gm.exists(name)) {
						player.sendMessage(prefix() + "§cRace track already exists!");
						return true;
					}

					gm.registerArena(name, Status.SETTING_UP, laps);
					player.sendMessage(prefix() + "§aAdded race §e" + name + ".");
					player.sendMessage(
							prefix() + "§eEdit the arena with §6/boatrace edit <track>§e.");
					player.sendMessage(prefix() + "§eEach track has §62 checkpoints§e and §61 finish line§e.\n"
							+ "§eMark the first checkpoint by placing §fwhite whool§e under the track and §8black wool §efor the second checkpoint.\n"
							+ "§eMark the finish line by placing §8bedrock §eunder the track.");

					// Remove
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (args.length == 1) {
						player.sendMessage(prefix() + "§cMissing Arguments (/boatrace remove <name>)");
						return true;
					} else if (args.length > 2) {
						player.sendMessage(prefix() + "§cToo Many Arguments (/boatrace remove <name>)");
						return true;
					}

					if (gm.exists(args[1])) {
						gm.remove(args[1]);
						player.sendMessage(prefix() + "§aRemoved track §6"+ args[1] + "§a.");
						return true;
					} else {
						player.sendMessage(prefix() + "§cTrack doesn't exist.");
						return true;
					}

					// Set Lobby
				} else if (args[0].equalsIgnoreCase("setlobby")) {
					if (args.length == 1) {
						gm.setLobby(player.getLocation());
						player.sendMessage(prefix() + "§eLobby set.");
						return true;
					} else {
						player.sendMessage(prefix() + "§cToo Many Arguments (/boatrace setlobby)");
						return true;
					}

					// Edit
				} else if (args[0].equalsIgnoreCase("edit")) {
					if (args.length == 1) {
						player.sendMessage(prefix() + "§cMissing Arguments! (/boatrace edit <track>)");
					} else if (args.length >= 3) {
						player.sendMessage(prefix() + "§cToo Many Arguments! (/boatrace edit <track>)");
					}

					if (args.length == 2) {
						if (gm.exists(args[1])) {
							EditMode.toggleEditMode(player, gm.getArena(args[1]));
							player.sendMessage(prefix() + "§eToggled edit mode.");
						} else {
							player.sendMessage(prefix() + "§cInvalid Race!");
						}
					}
				}
			}

			if (player.hasPermission("btr.status")) {
				// Set Status
				if (args[0].equalsIgnoreCase("setstatus")) {
					if (args.length <= 2) {
						player.sendMessage(
								prefix() + "§cMissing Arguments! (/boatrace setstatus <race track> <status>)");
					} else if (args.length >= 4) {
						player.sendMessage(
								prefix() + "§cToo Many Arguments! (/boatrace setstatus <race track> <status>)");
					}
					if (args.length == 3) {
						if (gm.exists(args[1])) {
							switch (args[2]) {
							case "setup":
								gm.getArena(args[1]).setStatus(Status.SETTING_UP);
								player.sendMessage(prefix() + "§eSet §6" + args[1] + " §estatus to §6Setting Up§e.");
								break;
							case "joinable":
								gm.getArena(args[1]).setStatus(Status.JOINABLE);
								player.sendMessage(prefix() + "§eSet §6" + args[1] + " §estatus to §aJoinable§e.");
								break;
							case "started":
								gm.getArena(args[1]).setStatus(Status.JOINABLE);
								player.sendMessage(prefix() + "§eSet §6" + args[1] + " §estatus to §aStarted§e.");
								break;
							case "ongoing":
								gm.getArena(args[1]).setStatus(Status.ONGOING);
								player.sendMessage(prefix() + "§eSet §6" + args[1] + " §estatus to §6Ongoing§e.");
								break;
							case "cancelled":
								gm.getArena(args[1]).setStatus(Status.CANCELLED);
								player.sendMessage(prefix() + "§eSet §6" + args[1] + " §estatus to §cCancelled§e.");
								break;
							case "ended":
								gm.getArena(args[1]).setStatus(Status.ENDED);
								player.sendMessage(prefix() + "§eSet §6" + args[1] + " §estatus to §cEnded§e.");
								break;
							case "unavailable":
								gm.getArena(args[1]).setStatus(Status.UNAVAILABLE);
								player.sendMessage(prefix() + "§eSet §6" + args[1] + " §estatus to §cUnavailable§e.");
								break;
							default:
								player.sendMessage(prefix()
										+ "§cStatus: §esetup, joinable, ongoing, cancelled, ended, unavailable.");
								break;
							}
						} else {
							player.sendMessage(prefix() + "§cInvalid Race!");
						}
					}
				}
			}

			// Yes I know but it's the only way to have specific error messages
			if (player.hasPermission("btr.use")) {
				// Join
				if (args[0].equalsIgnoreCase("join")) {
					if (args.length == 1) {
						player.sendMessage(prefix() + "§cPlease specify the race track. (/boatrace join <race track>)");
					} else if (args.length >= 3) {
						player.sendMessage(prefix() + "§cToo Many Arguments! (/boatrace join <race track>)");
					} else if (args.length == 2) {
						if (gm.getLobby() != null) {
							if (gm.exists(args[1])) {
								if (gm.getArena(args[1]).getStatus() == Status.JOINABLE
										|| gm.getArena(args[1]).getStatus() == Status.STARTING) {
									if (gm.getArena(args[1]).getPlayers().size() < 4) {
										if (gm.getArena(args[1]).getSpawns() != null
												&& gm.getArena(args[1]).getSpawns().size() == 4) {
											if (!gm.isInArena(player)) {
												gm.join(gm.getArena(args[1]), player);
											} else
												player.sendMessage(prefix() + "§cYou're on a race already!");
										} else
											player.sendMessage(
													prefix() + "§cSorry! This track has not been setup yet.");
									} else
										player.sendMessage(prefix() + "§cSorry! §7Game is full!");
								} else
									player.sendMessage(prefix() + "§cCan't join a game in progress.");
							} else
								player.sendMessage(prefix() + "§cInvalid Track!");
						} else
							player.sendMessage(prefix() + "§cLobby not set.");
					}

					// Leave
				} else if (args[0].equalsIgnoreCase("leave")) {
					if (args.length >= 1) {
						if (gm.isInArena(player)) {
							gm.leave(gm.getPlayerArena(player), player);
						} else {
							player.sendMessage(prefix() + "§cYou're not inside a boat race!");
						}
					}
				}
			} else {
				player.sendMessage(prefix() + "§cSorry! §7You can't do that!");
			}

		}
		return true;

	}

	public static String prefix() {
		return "§8[§bBoatRace§8]: §r";
	}

}
