package nz.ac.wgtn.swen225.lc.renderer;

import nz.ac.wgtn.swen225.lc.domain.Vector2D;
import nz.ac.wgtn.swen225.lc.domain.level.Level;
import nz.ac.wgtn.swen225.lc.domain.level.characters.Enemy;
import nz.ac.wgtn.swen225.lc.domain.level.characters.Player;
import nz.ac.wgtn.swen225.lc.domain.level.tiles.Tile;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Helper interface for making tiles for the maze.
 */
interface Sprites {
    /**
     * Creates a board representing the tiles in the level.
     *
     * @param level The level to represent.
     * @return 2D array of JComponents. The first dimension is <code>level.getWidth()</code> and the
     * second dimension is <code>level.getHeight()</code>.
     */
    static JComponent[][] makeBoard(final Level level) {
        Set<Tile> tiles = level.getTiles();
        BiFunction<Integer, Integer, JComponent> getTile = (x, y) -> tiles.stream()
                //Gets first tile that matches coordinates
                .filter(t -> {
                    Vector2D position = t.getPosition();
                    return position.x() == x && position.y() == y;
                })
                //If tile present, calls `makeTile`, else, calls `emptyTile`.
                .findFirst().map(Sprites::makeTile).orElseGet(Sprites::emptyTile);

        int width = level.getWidth();
        int height = level.getHeight();

        var board = new JComponent[width][height];
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                board[x][y] = getTile.apply(x, y);
            }
        }

        return board;
    }

    /**
     * From the given <code>Tile</code>, constructs a new <code>JComponent</code> representing it.
     *
     * <p>The specific subclass of <code>tile</code> provides information on what the tile will
     * appear as.
     *
     * @param tile The tile to make a component of.
     * @return A representation of the tile.
     * @throws IllegalArgumentException If <code>tile</code> doesn't match any known subclass of
     *                                  <code>Tile</code>.
     * @throws NullPointerException     If <code>tile</code> is null.
     */
    static JComponent makeTile(Tile tile) throws IllegalArgumentException {
        Objects.requireNonNull(tile);
        //TODO: Stub, final version should assign the label the image associated with the Tile.
        return new JLabel(tile.getClass().getName()) {
            {
                setBorder(BorderFactory.createLineBorder(Color.BLUE));
                setToolTipText(getText());
            }
        };
    }

    /**
     * Creates component representing an empty tile.
     *
     * @return A component representing an empty tile.
     */
    static JComponent emptyTile() {
        //TODO: add image
        return new JLabel("EMPTY TILE") {
            {
                setBorder(BorderFactory.createLineBorder(Color.YELLOW));
                setToolTipText(getText());
            }
        };
    }

    /**
     * A component that represents the player.
     */
    class PlayerComponent extends JLabel {

        //TODO: Icon made manually, should be taken from image
        /**
         * Default icon of the player.
         */
        private static final Icon DEFAULT_PLAYER_ICON = new Icon() {
            public static final int VERTICAL_BORDER = 2;
            public static final int HORIZONTAL_BORDER = 4;
            private static final int WIDTH = 32;
            private static final int HEIGHT = 32;

            @Override
            public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
                Graphics2D g2d = (Graphics2D) g.create();

                g2d.setColor(Color.YELLOW);

                g2d.fillOval(HORIZONTAL_BORDER, VERTICAL_BORDER, WIDTH - 2 * HORIZONTAL_BORDER,
                        HEIGHT - 2 * VERTICAL_BORDER);

                g2d.dispose();
            }

            @Override
            public int getIconWidth() {
                return WIDTH;
            }

            @Override
            public int getIconHeight() {
                return HEIGHT;
            }
        };
        /**
         * The player being represented.
         */
        private final Player player;

        PlayerComponent(final Player player) {
            this.player = player;

            setOpaque(false);
            setIcon(DEFAULT_PLAYER_ICON);
        }

        /**
         * Interrogates the player's state to see if it should change.
         */
        public void update() {
            //TODO: stub
        }
    }

    public class EnemyComponent extends JLabel {
        public EnemyComponent(final Enemy enemy) {
        //TODO:stub
        }
    }
}
