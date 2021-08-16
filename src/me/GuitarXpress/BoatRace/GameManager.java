package me.GuitarXpress.BoatRace;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GameManager implements Listener {

	Main plugin;

	public static int REQUIREDPLAYERS;
	public static int timer;
	public static int WAITTIME;

	int fadeIn = 2;
	int stay = 20;
	int fadeOut = 2;

	static Material FINISHLINE = Material.BEDROCK;
	static Material CHECKPOINTONE = Material.WHITE_WOOL;
	static Material CHECKPOINTTWO = Material.BLACK_WOOL;

	static Map<UUID, Integer> playerLap = new HashMap<UUID, Integer>();
	static Map<UUID, Boolean> chkpt1 = new HashMap<UUID, Boolean>();
	static Map<UUID, Boolean> chkpt2 = new HashMap<UUID, Boolean>();

	String winner;

	public int startingTaskID;
	public int task;

	public GameManager(Main plugin) {
		this.plugin = plugin;
	}

	public void checkPlayers(String arena) {
		if (ArenaManager.getArena(arena).getPlayers().size() >= REQUIREDPLAYERS) {
			for (int i = 0; i < ArenaManager.getArena(arena).getPlayers().size(); i++) {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (ArenaManager.getArena(arena).getPlayers().get(i).equals(p.getUniqueId())) {
						if (ArenaManager.getArena(arena).getStatus() == STATUS.JOINABLE) {
							ArenaManager.getArena(arena).setStatus(STATUS.STARTING);
							Bukkit.broadcastMessage(
									prefix() + "§eA game is about to start on track §6" + arena + "§e.");
							timer = WAITTIME;
							p.sendMessage(prefix() + "§eGame starting in §6" + timer + "§e seconds.");
							task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
								@Override
								public void run() {
									timer--;
									switch (timer) {
									case 5:
										sendStartNoti(arena, 5);
										break;
									case 4:
										sendStartNoti(arena, 4);
										break;
									case 3:
										sendStartNoti(arena, 3);
										break;
									case 2:
										sendStartNoti(arena, 2);
										break;
									case 1:
										sendStartNoti(arena, 1);
										break;
									case 0:
										playerCheck(arena);
										Bukkit.getScheduler().cancelTask(task);
										if (ArenaManager.getArena(arena).getStatus() == STATUS.STARTING) {
											for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
												if (ArenaManager.getArena(arena).getPlayers()
														.contains(pl.getUniqueId())) {
													pl.sendMessage(prefix() + "§eStarting...");
													pl.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999,
															128, true, false));
													pl.setWalkSpeed(0);
												}
											}
											ArenaManager.getArena(arena).setStatus(STATUS.STARTUP);
											startGame(arena);
										} else {
											for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
												if (ArenaManager.getArena(arena).getPlayers()
														.contains(pl.getUniqueId())) {
													pl.sendMessage(
															prefix() + "§cCancelling game start. Not enough players.\n"
																	+ prefix() + "§ePutting you back in queue.");
												}
											}
										}
										break;
									}
								}
							}, 0, 1 * 20);
						} else if (ArenaManager.getArena(arena).getStatus() == STATUS.STARTING) {
							p.sendMessage(prefix() + "§eGame starting in §6" + timer + "§e seconds.");
						}
					}
				}
			}
		}
	}

	public void startGame(String arena) {
		System.out.println(prefix() + "§e" + arena + " started.");
		for (int j = 0; j < ArenaManager.getArena(arena).getPlayers().size(); j++) {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (ArenaManager.getArena(arena).getPlayers().get(j).equals(p.getUniqueId())) {
					p.setGameMode(GameMode.ADVENTURE);
					p.teleport(ArenaManager.getArena(arena).getSpawns().get(j));
					continue;
				}
			}
		}
		setup(arena);
	}

	public void sendStartNoti(String arena, int time) {
		for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
			if (ArenaManager.getArena(arena).getPlayers().contains(pl.getUniqueId())) {
				pl.sendMessage(prefix() + "§eStarting in §6" + time + " §eseconds.");
			}
		}
	}

	public void setup(String arena) {
		for (int i = 0; i < ArenaManager.getArena(arena).getPlayers().size(); i++) {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (!ArenaManager.getArena(arena).getPlayers().isEmpty()) {
					if (ArenaManager.getArena(arena).getPlayers().get(i).equals(p.getUniqueId())) {
						int j = i;
						playerLap.put(p.getUniqueId(), 1);
						chkpt1.put(p.getUniqueId(), false);
						chkpt2.put(p.getUniqueId(), false);
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
								Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
									Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
										Location loc = ArenaManager.getArena(arena).getSpawns().get(j);
										Boat boat = (Boat) loc.getWorld().spawnEntity(loc, EntityType.BOAT);
										if (j == 1)
											boat.setWoodType(TreeSpecies.BIRCH);
										else if (j == 2)
											boat.setWoodType(TreeSpecies.REDWOOD);
										else if (j == 3)
											boat.setWoodType(TreeSpecies.ACACIA);
										for (Entity e : p.getNearbyEntities(1, 1, 1)) {
											if (e instanceof Boat) {
												Boat b = (Boat) e;
												if (b.getPassengers().isEmpty()) {
													b.addPassenger(p);
												}
											}
										}
										if (!ArenaManager.getArena(arena).getPlayers().isEmpty()) {
											ArenaManager.getArena(arena).setStatus(STATUS.ONGOING);
										}
										p.sendTitle("GO!", null, fadeIn, stay, fadeOut);
										p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 100);
									}, 25);
									p.sendTitle(String.valueOf(1), null, fadeIn, stay, fadeOut);
									p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
								}, 25);
								p.sendTitle(String.valueOf(2), null, fadeIn, stay, fadeOut);
								p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
							}, 25);
							p.sendTitle(String.valueOf(3), null, fadeIn, stay, fadeOut);
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
						}, 25);
					}
				}
			}
		}
	}

	public void endGame(String name) {
		Arena arena = ArenaManager.getArena(name);
		System.out.println(prefix() + "§e" + name + " ended.");
		arena.setStatus(STATUS.ENDED);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (arena.getPlayers().contains(p.getUniqueId())) {
					if (arena.getScoreboard().get(0).equals(p.getUniqueId())) {
						winner = p.getDisplayName();
					}
					p.sendMessage(prefix() + winner + " §ewon!");
					arena.leave(p);
					Commands.pArena.put(p.getUniqueId(), null);
					p.setGameMode(GameMode.SURVIVAL);
					p.teleport(ArenaManager.getLobby());
					p.removePotionEffect(PotionEffectType.JUMP);
					p.setWalkSpeed(0.2f);
				}

			}
			arena.clearScore();
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				arena.setStatus(STATUS.JOINABLE);
			}, 2 * 20);
		}, 3 * 20);
	}

	public void playerCheck(String arena) {
		if (ArenaManager.getArena(arena).getPlayers().size() < REQUIREDPLAYERS) {
			System.out.println(prefix() + "§e" + arena + " cancelled.");
			ArenaManager.getArena(arena).setStatus(STATUS.CANCELLED);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				ArenaManager.getArena(arena).setStatus(STATUS.JOINABLE);
			}, 2 * 20);
		}
	}

	public String prefix() {
		return "§8[§bBoatRace§8]: §r";
	}

	// =================== EVENTS ===================
	@EventHandler
	public void onVehicleExit(VehicleExitEvent event) {
		if (!(event.getExited() instanceof Player))
			return;
		Player p = (Player) event.getExited();
		if (Commands.pArena.get(p.getUniqueId()) != null) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				event.getVehicle().addPassenger(p);
			}, 5);
		}
	}

	@EventHandler
	public void onInteraction(PlayerInteractEvent event) {
		if (Commands.pArena.get(event.getPlayer().getUniqueId()) != null)
			event.setCancelled(true);

	}

	@EventHandler
	public void onInteraction(PlayerInteractEntityEvent event) {
		if (Commands.pArena.get(event.getPlayer().getUniqueId()) != null)
			event.setCancelled(true);

	}

}
