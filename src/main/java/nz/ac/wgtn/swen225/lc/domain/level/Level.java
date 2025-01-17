package nz.ac.wgtn.swen225.lc.domain.level;

import nz.ac.wgtn.swen225.lc.domain.Entity;
import nz.ac.wgtn.swen225.lc.domain.Game;
import nz.ac.wgtn.swen225.lc.domain.level.characters.Enemy;
import nz.ac.wgtn.swen225.lc.domain.level.characters.Player;
import nz.ac.wgtn.swen225.lc.domain.level.tiles.Tile;
import nz.ac.wgtn.swen225.lc.utils.Vector2D;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Presents a level (or maze/board). Contains what's on the board: tiles (excluding free tiles), enemies, player, etc
 * @author Shiyan Wei
 * Student ID: 300569298
 */
public class Level extends Entity {

    private final int levelNo;
    private final int width;
    private final int height;
    private final int timeoutInSeconds;

    private final Set<Tile> tiles = new HashSet<>();
    private final Set<Enemy> enemies = new HashSet<>();
    private Player player;

    private Game game;

    /**
     * Creates a level
     *
     * @param levelNo level number
     * @param width how many tiles the level has in a row
     * @param height how many tiles the level has in a column
     * @param timeoutInSeconds timeout
     */
    public Level(int levelNo, int width, int height, int timeoutInSeconds) {
        super();
        this.levelNo = levelNo;
        this.width = width;
        this.height = height;
        this.timeoutInSeconds = timeoutInSeconds;
    }
    /**
     * Creates a new game level with the specified properties, ID, and initial entities.
     *
     * @param id The unique identifier for the level.
     * @param levelNo The number of the level.
     * @param width The width of the level in tiles.
     * @param height The height of the level in tiles.
     * @param timeoutInSeconds The time limit for the level in seconds.
     * @param tiles The tiles on the level.
     * @param enemies The enemies present on the level.
     * @param player The player character in the level.
     */
    public Level(int id, int levelNo, int width, int height, int timeoutInSeconds, Set<Tile> tiles,
                 Set<Enemy> enemies, Player player) {
        super(id);
        this.levelNo = levelNo;
        this.width = width;
        this.height = height;
        this.timeoutInSeconds = timeoutInSeconds;
        tiles.forEach(this::addTile);
        enemies.forEach(this::addEnemy);
        setPlayer(player);
    }
    /**
     * Get the number of the level.
     *
     * @return The number of the level.
     */
    public int getLevelNo() {
        return levelNo;
    }
    /**
     * Get the width of the level in tiles.
     *
     * @return The width of the level in tiles.
     */
    public int getWidth() {
        return width;
    }
    /**
     * Get the height of the level in tiles.
     *
     * @return The height of the level in tiles.
     */
    public int getHeight() {
        return height;
    }
    /**
     * Get the time limit for the level in seconds.
     *
     * @return The time limit for the level in seconds.
     */
    public int getTimeoutInSeconds() {
        return timeoutInSeconds;
    }
    /**
     * Get a read-only set of tiles present on the level.
     *
     * @return A read-only set of tiles present on the level.
     */
    public Set<Tile> getTiles() {
        return Set.copyOf(tiles);
    }

    private Tile getTile(Vector2D position) {
        return tiles.stream().filter(t -> Objects.equals(t.getPosition(), position)).findAny().orElse(null);
    }
    /**
     * Add a tile to the level.
     *
     * @param tile The tile to be added to the level.
     */
    public void addTile(Tile tile) {
        tiles.add(tile);
        tile.setLevel(this);
    }
    /**
     * Remove a tile from the level.
     *
     * @param tile The tile to be removed from the level.
     */
    public void removeTile(Tile tile) {
        tiles.remove(tile);
    }
    /**
     * Get a read-only set of enemies present on the level.
     *
     * @return A read-only set of enemies present on the level.
     */
    public Set<Enemy> getEnemies() {
        return Set.copyOf(enemies);
    }
    /**
     * Get the enemies as a map, using their IDs as keys.
     *
     * @return A map of enemies, using their IDs as keys.
     */
    public Map<Integer, Enemy> getEnemiesAsMap() {
        return enemies.stream().collect(Collectors.toMap(Entity::getId, e -> e));
    }
    /**
     * Add an enemy to the level.
     *
     * @param enemy The enemy to be added to the level.
     */
    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        enemy.setLevel(this);
    }

    /**
     * Get the player character in the level.
     *
     * @return The player character in the level.
     */
    public Player getPlayer() {
        return player;
    }
    /**
     * Set the player character in the level.
     *
     * @param player The player character to be set in the level.
     */
    public void setPlayer(Player player) {
        this.player = player;
        player.setLevel(this);
    }
    /**
     * Get the game to which the level belongs.
     *
     * @return The game to which the level belongs.
     */
    public Game getGame() {
        return game;
    }
    /**
     * Set the game to which the level belongs.
     *
     * @param game The game to which the level belongs.
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Moves player with the vector passed in. Invokes {@link Tile#onExit(Player player)} on old tile and
     * {@link Tile#onEnter(Player player)} on new tile.
     * <p>
     * This will not player if movement is null or {@link Vector2D#ZERO}
     * <p>
     * @param movement player movement.
     */
    public void movePlayer(Vector2D movement) {
        if (movement != null && movement != Vector2D.ZERO) {
            var oldPosition = player.getPosition();
            var newPosition = oldPosition.add(movement);

            if (newPosition.x() < 0 || newPosition.x() >= width ||
                    newPosition.y() < 0 || newPosition.y() >= width) {
                throw new IllegalArgumentException("Player went outside the board");
            }

            var oldTile = getTile(oldPosition);
            var newTile = getTile(newPosition);
            if (newTile == null || newTile.isEnterable(player)) {
                if (oldTile != null) {
                    oldTile.onExit(player);
                }
                player.setPosition(newPosition);
                if (newTile != null) {
                    newTile.onEnter(player);
                }
            }
        }
    }

    /**
     * Moves enemy with the vector passed in.
     * <p>
     * This will not enemy if movement is null or {@link Vector2D#ZERO}
     * <p>
     * @param enemy enemy
     * @param movement enemy movement
     */
    public void move(Enemy enemy, Vector2D movement) {
        if (movement != null && movement != Vector2D.ZERO) {
            var newPosition = enemy.getPosition().add(movement);
            if (newPosition.x() < 0 || newPosition.x() >= width ||
                    newPosition.y() < 0 || newPosition.y() >= width) {
                throw new IllegalArgumentException("Enemy went outside the board");
            }
            enemy.setPosition(newPosition);
        }
    }

}
