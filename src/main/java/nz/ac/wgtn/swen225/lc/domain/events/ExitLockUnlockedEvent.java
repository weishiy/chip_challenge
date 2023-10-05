package nz.ac.wgtn.swen225.lc.domain.events;

import nz.ac.wgtn.swen225.lc.domain.level.characters.Player;
import nz.ac.wgtn.swen225.lc.domain.level.tiles.ExitLock;

/**
 * Fires when the exit lock is unlocked
 */
public record ExitLockUnlockedEvent(ExitLock exitLock, Player player) implements GameEvent {
}
