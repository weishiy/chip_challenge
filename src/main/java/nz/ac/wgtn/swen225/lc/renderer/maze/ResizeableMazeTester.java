package nz.ac.wgtn.swen225.lc.renderer.maze;

import nz.ac.wgtn.swen225.lc.domain.level.Level;
import nz.ac.wgtn.swen225.lc.renderer.TestingLevels;

import javax.swing.*;
import java.awt.*;

/**
 * Entry point for displaying ResizeableMaze.
 *
 * @author Jeremy Kanal-Scott 300624019
 */
public final class ResizeableMazeTester {

    private ResizeableMazeTester() { //empty
    }

    /**
     * Entry point.
     *
     * @param args Not used.
     */
    public static void main(final String[] args) {
        final Level level = getLevel(args);

        SwingUtilities.invokeLater(() -> {
            var mazeFrame = new JFrame();
            mazeFrame.setLayout(new BorderLayout());
            mazeFrame.setLocationByPlatform(true);
            mazeFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            var maze = new ResizeableMaze(null);
            maze.setLevel(level);
            mazeFrame.add(maze, BorderLayout.CENTER);


            final var minDimension = new Dimension(100, 100);
            mazeFrame.setMinimumSize(minDimension);
            mazeFrame.setVisible(true);

            final int delay = 1000 / 120;

            new Timer(delay, e -> maze.render()).start();
        });
    }

    private static Level getLevel(final String[] args) {
        Level level;
        if (args.length == 1) {
            final String letter = args[0];
            if (letter.equalsIgnoreCase("A")) {
                level = new TestingLevels.LevelA();
            } else if (letter.equalsIgnoreCase("B")) {
                level = new TestingLevels.LevelB();
            } else {
                //Default case.
                level = new TestingLevels.LevelA();
            }
        } else {
            //Default case.
            level = new TestingLevels.LevelA();
        }
        return level;
    }

}
