package br.unisal.aula;

import br.unisal.aula.domain.*;
import br.unisal.aula.generation.MundoBuilder;
import br.unisal.aula.io.ConsoleRenderer;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GenerationBoundaryTest {
    @Test void eachBorderAndOutsideOriginIsRejectedIndependently() {
        int[][] origins = {{0,2},{2,0},{4,2},{2,4},{-1,2},{2,-1},{5,2},{2,5}};
        for (int[] origin : origins) {
            MundoBuilder builder = new MundoBuilder(5, 5).preencher('#', true);
            assertThrows(IllegalArgumentException.class, () -> builder.criarCaminho(origin[0], origin[1], 0));
        }
        assertThrows(IllegalStateException.class, () -> new MundoBuilder(5,5).criarCaminho(2,2,1));
        assertThrows(IllegalStateException.class, () -> new MundoBuilder(5,5).criarCriaturas(1));
    }

    @Test void spawnSamplingIncludesEdgeFloorsAndCreatesIndependentPositions() {
        Random random = new Random() {
            @Override public int nextInt(int bound) { assertEquals(9, bound); return 0; }
        };
        Mundo world = new MundoBuilder(3, 3, random).preencher('.', false).criarCriaturas(2).build();
        assertEquals(new Ponto2D(0,0), world.getCriaturas().get(0).getPosicao());
        world.getCriaturas().get(0).mover(world, 1, 0);
        assertEquals(new Ponto2D(0,0), world.getCriaturas().get(1).getPosicao());
    }

    @Test void defaultGameStartsWithWallsTwentyZombiesAndThreeLives() {
        Mundo world = new Jogo(9, 7, new Random(42)).criarMundo();
        assertEquals(9, world.getLargura());
        assertEquals(7, world.getAltura());
        assertEquals(20, world.getCriaturas().size());
        assertEquals(3, world.getJogador().getVidas());
        assertEquals(new Ponto2D(4,3), world.getJogador().getPosicao());
        for (int x = 0; x < 9; x++) {
            assertTrue(world.bloqueado(x,0));
            assertTrue(world.bloqueado(x,6));
        }
        for (int y = 0; y < 7; y++) {
            assertTrue(world.bloqueado(0,y));
            assertTrue(world.bloqueado(8,y));
        }
    }

    @Test void rendererShowsZombieAndFloorWhenPlayerDoesNotCoverThem() {
        Random random = new Random() { @Override public int nextInt(int bound) { return 0; } };
        Mundo world = new MundoBuilder(3,3,random).preencher('.',false).criarCriaturas(1).build();
        world.setJogador(new Jogador(3, new Ponto2D(1,1),'J'));
        assertEquals("Z..\n.J.\n...\n", new ConsoleRenderer().render(world));
    }
}
