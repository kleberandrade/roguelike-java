package br.unisal.aula;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RulesTest {
    @Test void outsideTheMapIsBlockedInsteadOfCrashing() {
        Mundo world = new MundoBuilder(3, 3).preencher('.', false).build();
        assertTrue(world.bloqueado(-1, 1));
        assertTrue(world.bloqueado(1, -1));
        assertTrue(world.bloqueado(3, 1));
        assertTrue(world.bloqueado(1, 3));
        Personagem character = new Personagem(new Ponto2D(0, 0), 'J');
        character.mover(world, -1, 0);
        assertEquals(new Ponto2D(0, 0), character.getPosicao());
    }

    @Test void movesAcrossFloorAndStopsAtWalls() {
        Mundo world = new MundoBuilder(5, 5).preencher('.', false).build();
        Personagem character = new Personagem(new Ponto2D(2, 2), 'J');
        character.mover(world, 1, 0);
        assertEquals(new Ponto2D(3, 2), character.getPosicao());
        Mundo walls = new MundoBuilder(5, 5).preencher('#', true).build();
        character.mover(walls, -1, 0);
        assertEquals(new Ponto2D(3, 2), character.getPosicao());
    }

    @Test void threeHitsEndTheGame() {
        Jogador player = new Jogador(3, new Ponto2D(1, 1), 'J');
        player.tomarDano(2);
        assertTrue(player.vivo());
        player.tomarDano(1);
        assertFalse(player.vivo());
    }

    @Test void carvingKeepsOuterWallsAndOpensTheStartingCell() {
        Mundo world = new MundoBuilder(9, 7).preencher('#', true).criarCaminho(4, 3, 2000).build();
        assertFalse(world.bloqueado(4, 3));
        for (int x = 0; x < 9; x++) {
            assertTrue(world.bloqueado(x, 0));
            assertTrue(world.bloqueado(x, 6));
        }
        for (int y = 0; y < 7; y++) {
            assertTrue(world.bloqueado(0, y));
            assertTrue(world.bloqueado(8, y));
        }
    }
}
