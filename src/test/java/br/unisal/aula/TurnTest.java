package br.unisal.aula;

import br.unisal.aula.domain.*;
import br.unisal.aula.generation.MundoBuilder;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TurnTest {
    private Mundo floor(List<Personagem> zombies) {
        Celula[][] cells = new Celula[5][5];
        for (int x = 0; x < 5; x++) for (int y = 0; y < 5; y++)
            cells[x][y] = new Celula(false, new Ponto2D(x, y), '.');
        Mundo world = new Mundo(cells, zombies);
        world.setJogador(new Jogador(3, new Ponto2D(2, 2), 'J'));
        return world;
    }

    private Random stay() {
        return new Random() { @Override public int nextInt(int bound) { return 0; } };
    }

    @Test void commandsIgnoreCaseAndWhitespaceAndUnknownInputWaits() {
        Mundo world = floor(List.of());
        world.atualizar(" d ");
        assertEquals(new Ponto2D(3, 2), world.getJogador().getPosicao());
        world.atualizar("W");
        world.atualizar("a");
        world.atualizar("s");
        world.atualizar("unknown");
        assertEquals(new Ponto2D(2, 2), world.getJogador().getPosicao());
        assertEquals(5, world.getJogador().getPassos());
    }

    @Test void zombiesActAfterPlayerAndStackedZombiesEachDealDamage() {
        Mundo world = floor(List.of(new Zumbi(new Ponto2D(3, 2), stay()),
                new Zumbi(new Ponto2D(3, 2), stay())));
        world.atualizar("D");
        assertEquals(1, world.getJogador().getVidas());
        assertTrue(world.getJogador().vivo());
        world.atualizar("");
        assertFalse(world.getJogador().vivo());
        int steps = world.getJogador().getPassos();
        world.atualizar("A");
        assertEquals(steps, world.getJogador().getPassos());
    }

    @Test void zombieInTheSameColumnButAnotherRowDoesNotDealDamage() {
        Mundo world = floor(List.of(new Zumbi(new Ponto2D(2, 3), stay())));
        world.atualizar("");
        assertEquals(3, world.getJogador().getVidas());
    }

    @Test void movingAwayBeforeZombiesActAvoidsDamage() {
        Mundo world = floor(List.of(new Zumbi(new Ponto2D(2, 2), stay())));
        world.atualizar("A");
        assertEquals(3, world.getJogador().getVidas());
    }

    @Test void zombieDirectionsIncludeWaitingAndFourCardinalMoves() {
        int[][] expected = {{2,2},{1,2},{2,1},{2,3},{3,2}};
        for (int direction = 0; direction < 5; direction++) {
            final int selected = direction;
            Zumbi zombie = new Zumbi(new Ponto2D(2, 2), new Random() {
                @Override public int nextInt(int bound) { assertEquals(5, bound); return selected; }
            });
            zombie.atualizar(floor(List.of()));
            assertEquals(new Ponto2D(expected[direction][0], expected[direction][1]), zombie.getPosicao());
        }
    }
}
