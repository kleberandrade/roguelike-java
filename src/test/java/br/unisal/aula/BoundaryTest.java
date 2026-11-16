package br.unisal.aula;

import br.unisal.aula.domain.*;
import br.unisal.aula.generation.MundoBuilder;
import br.unisal.aula.io.ConsoleRenderer;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoundaryTest {
    @Test void zeroDamageIsAllowedAndOverkillStopsAtZero() {
        Jogador player = new Jogador(3, new Ponto2D(1, 1), 'J');
        player.tomarDano(0);
        assertEquals(3, player.getVidas());
        assertThrows(IllegalArgumentException.class, () -> player.tomarDano(-1));
        player.tomarDano(100);
        assertEquals(0, player.getVidas());
        assertFalse(player.vivo());
        assertThrows(IllegalArgumentException.class, () -> new Jogador(0, new Ponto2D(1, 1), 'J'));
    }

    @Test void edgeFloorIsWalkableAndOnlyTheDestinationControlsCollision() {
        Celula[][] cells = new Celula[5][5];
        for (int x = 0; x < 5; x++) for (int y = 0; y < 5; y++)
            cells[x][y] = new Celula((x == 3 && y == 2) || (x == 2 && y == 3), new Ponto2D(x, y), '.');
        Mundo world = new Mundo(cells, List.of());
        assertFalse(world.bloqueado(0, 0));
        assertFalse(world.bloqueado(4, 4));
        Jogador player = new Jogador(3, new Ponto2D(2, 2), 'J');
        world.setJogador(player);
        world.atualizar("D");
        assertEquals(new Ponto2D(2, 2), player.getPosicao());
        assertEquals(1, player.getPassos());
        world.atualizar("A");
        assertEquals(new Ponto2D(1, 2), player.getPosicao());
        world.atualizar("D");
        world.atualizar("S");
        assertEquals(new Ponto2D(2, 2), player.getPosicao());
        world.atualizar("W");
        assertEquals(new Ponto2D(2, 1), player.getPosicao());
    }

    @Test void numericValueDoesNotMakeAnUnknownTwoArgumentOptionValid() {
        assertEquals(2, Main.run(new String[]{"--other", "42"}, new StringReader(""),
                new PrintWriter(new StringWriter())));
        assertEquals(0, Main.run(new String[]{}, new StringReader("q\n"),
                new PrintWriter(new StringWriter())));
    }

    @Test void walkCountAndEachDirectionAreObservable() {
        int[][] destinations = {{3,2}, {1,2}, {2,3}, {2,1}};
        for (int direction = 0; direction < 4; direction++) {
            final int selected = direction;
            Random random = new Random() {
                @Override public int nextInt(int bound) { assertEquals(4, bound); return selected; }
            };
            Mundo world = new MundoBuilder(5, 5, random).preencher('#', true)
                    .criarCaminho(2, 2, 1).build();
            int count = 0;
            for (int x = 0; x < 5; x++) for (int y = 0; y < 5; y++)
                if (!world.bloqueado(x, y)) count++;
            assertEquals(2, count);
            assertFalse(world.bloqueado(destinations[direction][0], destinations[direction][1]));
        }
    }

    @Test void zeroWalkAndZeroSpawnsDoNotAddWork() {
        Mundo world = new MundoBuilder(7, 7).preencher('#', true)
                .criarCaminho(3, 3, 0).criarCriaturas(0).build();
        assertEquals(0, world.getCriaturas().size());
        for (int x = 0; x < 7; x++) for (int y = 0; y < 7; y++)
            assertEquals(x != 3 || y != 3, world.bloqueado(x, y));
        assertEquals(0, new MundoBuilder(3, 3).preencher('#', true).criarCriaturas(0).build().getCriaturas().size());
    }

    @Test void everyBorderIsAnInvalidWalkOrigin() {
        MundoBuilder builder = new MundoBuilder(5, 7).preencher('#', true);
        for (int[] p : new int[][]{{0,3},{4,3},{2,0},{2,6},{-1,3},{5,3},{2,-1},{2,7}})
            assertThrows(IllegalArgumentException.class, () -> builder.criarCaminho(p[0], p[1], 0));
        assertThrows(IllegalStateException.class, () -> new MundoBuilder(5, 5).criarCaminho(2, 2, 0));
        assertThrows(IllegalStateException.class, () -> new MundoBuilder(5, 5).criarCriaturas(0));
    }

    @Test void rendererShowsZombieAwayFromPlayer() {
        Celula[][] cells = new Celula[3][3];
        for (int x = 0; x < 3; x++) for (int y = 0; y < 3; y++)
            cells[x][y] = new Celula(false, new Ponto2D(x, y), '.');
        Mundo world = new Mundo(cells, List.of(new Zumbi(new Ponto2D(0, 0), new Random(1))));
        world.setJogador(new Jogador(3, new Ponto2D(1, 1), 'J'));
        assertEquals("Z..\n.J.\n...\n", new ConsoleRenderer().render(world));
        Ponto2D point = new Ponto2D(1, 1);
        assertFalse(point.equals(null));
        assertFalse(point.equals("1,1"));
        assertEquals(point, new Ponto2D(1, 1));
    }
}
