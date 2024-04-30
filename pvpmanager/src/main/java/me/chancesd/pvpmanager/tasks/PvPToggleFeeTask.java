package me.chancesd.pvpmanager.tasks;

import me.chancesd.pvpmanager.manager.PlayerManager;
import me.chancesd.pvpmanager.player.CombatPlayer;

public class PvPToggleFeeTask implements Runnable {

	private final PlayerManager ph;

	public PvPToggleFeeTask(final PlayerManager ph) {
		this.ph = ph;
	}

	@Override
	public final void run() {
		for (final CombatPlayer p : ph.getPlayers().values()) {
			if (!p.hasPvPEnabled()) {
				p.applyPvPDisabledFee();
			}
		}
	}

}
