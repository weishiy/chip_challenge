package test.nz.ac.wgtn.swen225.lc.domain;

import nz.ac.wgtn.swen225.lc.domain.Game;
import nz.ac.wgtn.swen225.lc.domain.events.*;
import nz.ac.wgtn.swen225.lc.utils.Vector2D;
import nz.ac.wgtn.swen225.lc.domain.level.Level;
import nz.ac.wgtn.swen225.lc.domain.level.characters.Enemy;
import nz.ac.wgtn.swen225.lc.domain.level.characters.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class GameTest {

    private Game toTest;

    private Level mockLevel;

    @BeforeEach
    public void before() {
        mockLevel = mock(Level.class);
        toTest = new Game(1, 0, mockLevel);
    }

    @Test
    public void testUpdateWithNulls() {
        var mockEnemy1 = mock(Enemy.class);
        var mockEnemy2 = mock(Enemy.class);
        when(mockEnemy1.getPosition()).thenReturn(new Vector2D(1, 1));
        when(mockEnemy2.getPosition()).thenReturn(new Vector2D(5, 5));
        when(mockLevel.getEnemies()).thenReturn(Set.of(mockEnemy1, mockEnemy2));
        var mockPlayer = mock(Player.class);
        when(mockPlayer.getPosition()).thenReturn(new Vector2D(10, 10));
        when(mockLevel.getPlayer()).thenReturn(mockPlayer);

        toTest.update(null, null);

        verify(mockLevel).movePlayer(eq(Vector2D.ZERO));
        verify(mockLevel, never()).move(any(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTimerEvents() throws NoSuchFieldException, IllegalAccessException {
        var listener = mock(GameEventListener.class);
        Field field = toTest.getClass().getDeclaredField("listeners");
        field.setAccessible(true);

        List<GameEventListener> value = (List<GameEventListener>) field.get(toTest);
        value.add(listener);

        when(mockLevel.getTimeoutInSeconds()).thenReturn(1);
        IntStream.range(0, Game.FRAME_RATE).forEach(i -> toTest.update(Vector2D.ZERO, Map.of()));
        // one TickEvent per tick
        verify(listener, times(Game.FRAME_RATE)).onGameEvent(any(TickEvent.class));
        // one CountDownEvent at tick Game.FRAME_RATE
        verify(listener, times(1)).onGameEvent(any(CountDownEvent.class));
        // one TimeoutEvent at tick Game.FRAME_RATE
        verify(listener, times(1)).onGameEvent(any(TimeoutEvent.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void whenPlayCaughtByEnemies_shouldFirePlayDiedEvent() throws NoSuchFieldException, IllegalAccessException {
        var mockPlayer = mock(Player.class);
        when(mockPlayer.getPosition()).thenReturn(new Vector2D(10, 10));
        var mockEnemy = mock(Enemy.class);
        when(mockEnemy.getPosition()).thenReturn(new Vector2D(10, 10));

        when(mockLevel.getPlayer()).thenReturn(mockPlayer);
        when(mockLevel.getEnemies()).thenReturn(Set.of(mockEnemy));

        var listener = mock(GameEventListener.class);
        Field field = toTest.getClass().getDeclaredField("listeners");
        field.setAccessible(true);
        List<GameEventListener> value = (List<GameEventListener>) field.get(toTest);
        value.add(listener);

        toTest.update(Vector2D.ZERO, Map.of());
        verify(listener).onGameEvent(any(PlayerDiedEvent.class));
    }

}
