package me.NoChance.PvPManager.Listeners;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import me.NoChance.PvPManager.PvPlayer;
import me.NoChance.PvPManager.Managers.DependencyManager;
import me.NoChance.PvPManager.Managers.PlayerHandler;
import me.NoChance.PvPManager.Settings.Messages;
import me.NoChance.PvPManager.Utils.CombatUtils;
import me.chancesd.pvpmanager.tasks.RegionCheckTask;
import me.chancesd.pvpmanager.utils.ScheduleUtils;

public class MoveListener1_9 implements Listener {

	private final PlayerHandler ph;
	private final Cache<UUID, Player> cache = CacheBuilder.newBuilder().weakValues().expireAfterWrite(1, TimeUnit.SECONDS).build();
	private final DependencyManager dependencyManager;

	public MoveListener1_9(final PlayerHandler ph) {
		this.ph = ph;
		this.dependencyManager = ph.getPlugin().getDependencyManager();
		final RegionCheckTask regionCheckTask = new RegionCheckTask(ph, dependencyManager);
		Bukkit.getPluginManager().registerEvents(regionCheckTask, ph.getPlugin());
		ScheduleUtils.runPlatformTaskTimer(regionCheckTask, 20, 20);
	}

	@EventHandler(ignoreCancelled = true)
	public final void onPlayerMove(final PlayerMoveEvent event) {
		final Location locTo = event.getTo();
		final Location locFrom = event.getFrom();
		if (locFrom.getBlockX() == locTo.getBlockX() && locFrom.getBlockZ() == locTo.getBlockZ() && locFrom.getBlockY() == locTo.getBlockY())
			return;

		final Player player = event.getPlayer();
		final PvPlayer pvplayer = ph.get(player);
		if (!pvplayer.isInCombat())
			return;

		if (!dependencyManager.canAttackAt(null, locTo) && dependencyManager.canAttackAt(null, locFrom)) {
			final Vector newVel = locFrom.toVector().subtract(locTo.toVector());
			newVel.setY(0).normalize().multiply(1.6).setY(0.5);
			CombatUtils.checkGlide(player);
			player.setVelocity(sanitizeVector(newVel));
			if (!cache.asMap().containsKey(player.getUniqueId())) {
				pvplayer.message(Messages.getPushbackWarning());
				locFrom.getWorld().playEffect(player.getEyeLocation(), Effect.SMOKE, 3);
				cache.put(player.getUniqueId(), player);
			}
		}
	}

	@NotNull
	private Vector sanitizeVector(@NotNull final Vector vel) {
		if (Double.isNaN(vel.getX()))
			vel.setX(0);
		if (Double.isNaN(vel.getZ()))
			vel.setZ(0);
		return vel;
	}

}
