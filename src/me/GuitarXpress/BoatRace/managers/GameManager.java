package me.guitarxpress.boatrace.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import me.guitarxpress.boatrace.Arena;
import me.guitarxpress.boatrace.BoatRace;
import me.guitarxpress.boatrace.Commands;
import me.guitarxpress.boatrace.enums.Status;
import me.guitarxpress.boatrace.utils.RepeatingTask;

public class GameManager implements Listener {

	private BoatRace plugin;

	private int requiredPlayers = 2;
	private int timeToStart = 3;
	private int WAITTIME;

	private int fadeIn = 2;
	private int stay = 20;
	private int fadeOut = 2;

	private final Material FINISHLINE = Material.BEDROCK;
	private final Material CHECKPOINTONE = Material.WHITE_WOOL;
	private final Material CHECKPOINTTWO = Material.BLACK_WOOL;

	private Map<UUID, Integer> playerLap = new HashMap<>();
	private Map<UUID, Boolean> chkpt1 = new HashMap<>();
	private Map<UUID, Boolean> chkpt2 = new HashMap<>();

	private Map<Arena, Integer> arenaCountdownTimer = new HashMap<>();
	private Map<Arena, Integer> timeToStartMap = new HashMap<>();
	public Map<Arena, Boolean> endCountdown = new HashMap<>();
	public Map<Arena, Integer> endCountdownTask = new HashMap<>();

	private Map<UUID, GameMode> oldGm = new HashMap<>();

	private Location lobbyLoc;
	private List<Arena> arenas = new ArrayList<>();
	private List<String> aNames = new ArrayList<>();

	private Map<UUID, Arena> pArena = new HashMap<>();

	public GameManager(BoatRace plugin) {
		this.plugin = plugin;
	}

	public void registerArena(String name, List<Location> spawns, Status status, int laps, Location corner1,
			Location corner2) {
		arenas.add(new Arena(name, spawns, status, laps, corner1, corner2));
		aNames.add(name);
	}

	public void registerArena(String name, Status status, int laps) {
		arenas.add(new Arena(name, status, laps));
		aNames.add(name);
	}

	public void setSpawns(String arena, List<Location> spawns) {
		getArena(arena).setSpawns(spawns);
	}

	public boolean remove(String arena) {
		if (!exists(arena))
			return false;

		Arena a = getArena(arena);

		arenas.remove(a);
		aNames.remove(arena);
		plugin.getCfg().deleteArena(arena);
		return true;
	}

	public Arena getArena(String name) {
		for (Arena arena : arenas)
			if (arena.getName().equalsIgnoreCase(name))
				return arena;
		return null;
	}

	public boolean exists(String name) {
		return getArena(name) != null;
	}

	public void join(Arena arena, Player p) {
		arena.join(p);
		pArena.put(p.getUniqueId(), arena);
		oldGm.put(p.getUniqueId(), p.getGameMode());
		playerLap.put(p.getUniqueId(), 1);
		
		for (UUID uuid : arena.getPlayers())
			Bukkit.getPlayer(uuid).sendMessage(Commands.prefix() + "§6" + p.getName() + " §ejoined the game. (§b"
					+ arena.getPlayerCount() + "§e/§b4§e)");

		if (arena.getPlayerCount() >= requiredPlayers && arena.getStatus() == Status.JOINABLE)
			startTimer(arena);

		p.teleport(getLobby());
	}

	public void leave(Arena arena, Player p) {
		arena.leave(p);
		pArena.remove(p.getUniqueId());

		if (arena.getStatus() != Status.ENDED)
			for (UUID uuid : arena.getPlayers())
				Bukkit.getPlayer(uuid).sendMessage(Commands.prefix() + "§6" + p.getName() + " §eleft the game.");

		if (p.isInsideVehicle())
			p.getVehicle().remove();
		p.teleport(getLobby());
		p.setGameMode(oldGm.get(p.getUniqueId()));
		oldGm.remove(p.getUniqueId());
		p.removePotionEffect(PotionEffectType.JUMP);
		p.setWalkSpeed(0.2f);
	}

	public void startTimer(Arena arena) {
		arena.setStatus(Status.STARTING);

		if (arena.getPlayerCount() < requiredPlayers)
			Bukkit.broadcastMessage(Commands.prefix() + "§eArena §6" + arena.getName() + " §eis starting in §6"
					+ WAITTIME + "§e seconds. Join now!");

		arenaCountdownTimer.put(arena, WAITTIME);
		new RepeatingTask(plugin, 0, 1 * 20) {

			@Override
			public void run() {
				int time = arenaCountdownTimer.get(arena);
				if ((time % 5 == 0 && time >= 5) || (time > 0 && time < 5)) {
					for (UUID uuid : arena.getPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						player.sendMessage(Commands.prefix() + "§eStarting in §6" + time + "§e.");
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1f);
					}
				} else if (time <= 0 && arena.getPlayerCount() >= requiredPlayers) {
					startCountdown(arena);
					cancel();
				}

				if (arena.getPlayerCount() < requiredPlayers) {
					cancel();
					arena.setStatus(Status.CANCELLED);
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
						arena.setStatus(Status.JOINABLE);
					}, 1 * 20);
				}
				arenaCountdownTimer.put(arena, time - 1);
			}

		};
	}

	public void startCountdown(Arena arena) {
		arena.setStatus(Status.STARTUP);
		timeToStartMap.put(arena, timeToStart);
		setupPlayers(arena);

		new RepeatingTask(plugin, 0, 1 * 20) {
			@Override
			public void run() {
				int timer = timeToStartMap.get(arena);
				switch (timer) {
				case 3:
					sendStartNotification(arena, "3");
					break;
				case 2:
					sendStartNotification(arena, "2");
					break;
				case 1:
					sendStartNotification(arena, "1");
					break;
				case 0:
					sendStartNotification(arena, "GO!");
					if (hasEnoughPlayers(arena)) {
						arena.setStatus(Status.ONGOING);
						start(arena);
					}
					break;
				default:
					if (!hasEnoughPlayers(arena))
						arena.setStatus(Status.JOINABLE);
					cancel();
					break;
				}
				timeToStartMap.put(arena, --timer);
			}

		};
	}

	public void setupPlayers(Arena arena) {
		int i = 0;
		for (UUID uuid : arena.getPlayers()) {
			Player p = Bukkit.getPlayer(uuid);
			playerLap.put(uuid, 1);
			chkpt1.put(uuid, false);
			chkpt2.put(uuid, false);
			createScoreboard(p);
			p.setGameMode(GameMode.ADVENTURE);
			p.teleport(arena.getSpawns().get(i++));
		}
	}

	public void sendStartNotification(Arena arena, String string) {
		for (UUID uuid : arena.getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);
			player.sendTitle("§6" + string, "", fadeIn, stay, fadeOut);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, string.equals("GO!") ? 2f : 1f);
		}
	}

	public boolean hasEnoughPlayers(Arena arena) {
		return arena.getPlayerCount() >= requiredPlayers;
	}

	public void start(Arena arena) {
		int i = 0;
		for (UUID uuid : arena.getPlayers()) {
			Player p = Bukkit.getPlayer(uuid);
			Location loc = arena.getSpawns().get(i);
			Boat boat = (Boat) loc.getWorld().spawnEntity(loc, EntityType.BOAT);
			if (i == 1)
				boat.setWoodType(TreeSpecies.BIRCH);
			else if (i == 2)
				boat.setWoodType(TreeSpecies.DARK_OAK);
			else if (i == 3)
				boat.setWoodType(TreeSpecies.ACACIA);
			i++;
			for (Entity e : p.getNearbyEntities(1, 1, 1)) {
				if (e instanceof Boat) {
					Boat b = (Boat) e;
					if (b.getPassengers().isEmpty())
						b.addPassenger(p);
				}
			}
		}
	}

	public void endGame(String name) {
		Arena arena = getArena(name);
		arena.setStatus(Status.ENDED);

		List<UUID> toRemove = new ArrayList<>();

		for (UUID uuid : arena.getPlayers()) {
			Player p = Bukkit.getPlayer(uuid);
			toRemove.add(uuid);
			p.sendMessage(Commands.prefix() + "§6" +  Bukkit.getPlayer(arena.getScoreboard().get(0)).getName()
					+ " §ewon!");
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}

		for (UUID uuid : toRemove)
			leave(arena, Bukkit.getPlayer(uuid));

		if (endCountdownTask.containsKey(arena)) {
			Bukkit.getScheduler().cancelTask(endCountdownTask.get(arena));
			endCountdownTask.remove(arena);
			endCountdown.remove(arena);
		}
			
		toRemove.clear();
		arena.clearScore();
		arena.setStatus(Status.JOINABLE);
	}

	public void createScoreboard(Player p) {
		Arena arena = getPlayerArena(p);
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective obj = board.registerNewObjective("Scoreboard", "dummy", "§6Laps");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		for (UUID uuid : arena.getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);
			if (!arena.getScoreboard().contains(uuid)) {
				Score score = obj.getScore("§e" + player.getName());
				score.setScore(playerLap.get(uuid));
			}
		}

		p.setScoreboard(board);
	}

	public Map<UUID, Integer> getPlayerLap() {
		return playerLap;
	}

	public void setPlayerLap(Map<UUID, Integer> playerLap) {
		this.playerLap = playerLap;
	}

	public Map<UUID, Boolean> getChkpt1() {
		return chkpt1;
	}

	public void setChkpt1(Map<UUID, Boolean> chkpt1) {
		this.chkpt1 = chkpt1;
	}

	public Map<UUID, Boolean> getChkpt2() {
		return chkpt2;
	}

	public void setChkpt2(Map<UUID, Boolean> chkpt2) {
		this.chkpt2 = chkpt2;
	}

	public void setArenaNames(List<String> names) {
		this.aNames = names;
	}

	public Material getFINISHLINE() {
		return FINISHLINE;
	}

	public Material getCHECKPOINTONE() {
		return CHECKPOINTONE;
	}

	public Material getCHECKPOINTTWO() {
		return CHECKPOINTTWO;
	}

	public void setArenas(List<Arena> arenas) {
		this.arenas = arenas;
	}

	public Arena getPlayerArena(Player p) {
		return pArena.get(p.getUniqueId());
	}

	public boolean isInArena(Player p) {
		return getPlayerArena(p) != null;
	}

	public void setRequiredPlayers(int requiredPlayers) {
		this.requiredPlayers = requiredPlayers;
	}

	public void setWaitTime(int waitTime) {
		this.WAITTIME = waitTime;
	}

	public Location getLobby() {
		return lobbyLoc;
	}

	public void setLobby(Location lobbyLocation) {
		lobbyLoc = lobbyLocation;
	}

	public List<Arena> getArenas() {
		return arenas;
	}

	public List<String> getArenaNames() {
		return aNames;
	}

}
