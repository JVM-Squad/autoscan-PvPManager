package me.chancesd.pvpmanager.integration.hook;

import org.bukkit.entity.Player;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;

import me.chancesd.pvpmanager.integration.BaseDependency;
import me.chancesd.pvpmanager.integration.ForceToggleDependency;
import me.chancesd.pvpmanager.integration.Hook;
import me.chancesd.pvpmanager.player.ProtectionType;

public class TownyHook extends BaseDependency implements ForceToggleDependency {

	private final TownyAPI townyAPI;

	public TownyHook(final Hook hook) {
		super(hook);
		townyAPI = TownyAPI.getInstance();
	}

	@Override
	public boolean shouldDisable(final Player player) {
		final Resident resident = townyAPI.getResident(player);
		return resident != null && resident.hasTown() && resident.getTownOrNull().hasActiveWar();
	}

	@Override
	public boolean shouldDisable(final Player attacker, final Player defender, final ProtectionType reason) {
		return shouldDisable(attacker) && shouldDisable(defender);
	}

}
