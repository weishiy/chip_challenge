package nz.ac.wgtn.swen225.lc.domain;

import nz.ac.wgtn.swen225.lc.domain.events.*;
import nz.ac.wgtn.swen225.lc.domain.level.Level;
import nz.ac.wgtn.swen225.lc.domain.level.characters.Enemy;
import nz.ac.wgtn.swen225.lc.domain.level.tiles.ChipTile;
import nz.ac.wgtn.swen225.lc.persistency.Persistence;
import nz.ac.wgtn.swen225.lc.utils.Vector2D;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Presents a game. Contains methods to update the game's internal state. It will notify other modules about those state
 * changes via Observer pattern
 * @author Shiyan Wei
 * Student ID: 300569298
 */
public class Game extends Entity {

    public static final int FRAME_RATE = 10;

    private Level level;
    private int tickNo = 0;
    private boolean gameOver = false;

    private transient List<GameEventListener> listeners = new ArrayList<>();
    private transient List<GameEventListener> listenersToAdd = new ArrayList<>();
    private transient List<GameEventListener> listenersToRemove = new ArrayList<>();

    public Game() {
        super();
    }
    /**
     * Creates a new game.
     */
    public Game(int id, int tickNo, Level level) {
        super(id);
        this.tickNo = tickNo;
        setLevel(level);
    }

    /**
     * Updates the game (domain) with provided player input (the only allowed input is an one-tile-movement, i.e. one of
     * Vector2D.LEFT, Vector2D.UP, Vector2D.RIGHT, Vector2D.DOWN) and auto enemies inputs
     * <p>
     * Nulls will be converted to Vector2D.ZEROs.
     *
     * @param playerMovement player input
     * @param enemyMovementMap enemies inputs
     */
    public void update(Vector2D playerMovement, Map<Enemy, Vector2D> enemyMovementMap) {
        if (gameOver) {
            throw new IllegalStateException("Game is over");
        }

        // null check and handling
        if (playerMovement == null) {
            playerMovement = Vector2D.ZERO;
        }
        if (enemyMovementMap == null) {
            enemyMovementMap = Map.of();
        }
        var emm = enemyMovementMap.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue() != null ? e.getValue() : Vector2D.ZERO));

        // update player position and enemy position if possible
        level.movePlayer(playerMovement);
        emm.forEach(level::move);
        if (level.getEnemies().stream().anyMatch(e -> e.getPosition().equals(level.getPlayer().getPosition()))) {
            fire(new PlayerDiedEvent(level.getPlayer()));
        }

        // update counters
        tickNo++;
        if (tickNo % FRAME_RATE == 0) {
            fire(new CountDownEvent(getCountDown()));
        }
        fire(new TickEvent(tickNo));
        if (getCountDown() <= 0) {
            fire(new TimeoutEvent());
        }

        // To avoid java.util.ConcurrentModificationException
        listeners.removeAll(listenersToRemove);
        listenersToRemove.clear();
        listeners.addAll(listenersToAdd);
        listenersToAdd.clear();
    }
    /**
     * Gets the current level in the game.
     *
     * @return The current level.
     */
    public Level getLevel() {
        return level;
    }
    /**
     * Sets the current level in the game.
     *
     * @param level The level to set.
     */
    public void setLevel(Level level) {
        this.level = level;
        this.level.setGame(this);
    }
    /**
     * Gets the current tick number.
     *
     * @return The current tick number.
     */
    public int getTickNo() {
        return tickNo;
    }
    /**
     * Sets the current tick number.
     *
     * @param tickNo The tick number to set.
     */
    public void setTickNo(int tickNo) {
        this.tickNo = tickNo;
    }
    /**
     * Checks if the game is over.
     *
     * @return True if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Adds a game event listener to the list of listeners.
     *
     * @param listener The listener to add.
     */
    public void addListener(GameEventListener listener) {
        if (!listeners.contains(listener) && !listenersToAdd.contains(listener)) {
            listenersToAdd.add(listener);
        }
    }
    /**
     * Removes a game event listener from the list of listeners.
     *
     * @param listener The listener to remove.
     */
    public void removeListener(GameEventListener listener) {
        listenersToRemove.add(listener);
    }

    /**
     * Notify all listeners about happening of the game event.
     *
     * @param gameEvent game event to fire
     */
    public void fire(GameEvent gameEvent) {
        if (gameEvent instanceof GameOverEvent) {
            gameOver = true;
        }
        listeners.forEach(l -> l.onGameEvent(gameEvent));
    }
    /**
     * Gets the countdown (time left in the game).
     *
     * @return The countdown in seconds.
     */
    public int getCountDown() {
        return getLevel().getTimeoutInSeconds() - tickNo / FRAME_RATE;
    }
    /**
     * Gets the number of chips left on the level.
     *
     * @return The number of chips remaining.
     */
    public int getChipsLeft() {
        return (int) getLevel().getTiles().stream().filter(t -> t instanceof ChipTile).count();
    }

    /**
     * Gets the list of game event listeners.
     *
     * @return The list of game event listeners.
     */
    public List<GameEventListener> getListeners() {
        return List.copyOf(listeners);
    }

    /**
     * Deprecated. Use {@link Persistence#saveGame(File save, Game game)}, {@link Persistence#loadGame(File save)}
     * instead.
     *
     * @param game
     * @return a deep copy of game
     */
    @Deprecated
    public static Game deepCopyOf(Game game) {
        // make a deep copy with serialization
        try (var bos = new ByteArrayOutputStream()) {
            try (var oos = new ObjectOutputStream(bos)) {
                oos.writeObject(game);
                oos.flush();

                var byteData = bos.toByteArray();
                var bais = new ByteArrayInputStream(byteData);
                return (Game) new ObjectInputStream(bais).readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // As these lists are transient, we need to create them manually.
        this.listeners = new ArrayList<>();
        this.listenersToAdd = new ArrayList<>();
        this.listenersToRemove = new ArrayList<>();
    }
}
