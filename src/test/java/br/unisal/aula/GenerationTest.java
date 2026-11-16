package br.unisal.aula;

import br.unisal.aula.domain.*;
import br.unisal.aula.generation.MundoBuilder;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GenerationTest {
    private Mundo generated(long seed) {
        return new MundoBuilder(17, 11, new Random(seed)).preencher('#', true)
                .criarCaminho(8, 5, 2000).criarCriaturas(20).build();
    }

    @Test void seedReplaysMapAndZombieSpawns() {
        Mundo a = generated(42), b = generated(42);
        for (int x = 0; x < 17; x++) for (int y = 0; y < 11; y++)
            assertEquals(a.bloqueado(x, y), b.bloqueado(x, y));
        assertEquals(a.getCriaturas().stream().map(Personagem::getPosicao).toList(),
                b.getCriaturas().stream().map(Personagem::getPosicao).toList());
        assertEquals(20, a.getCriaturas().size());
        for (Personagem zombie : a.getCriaturas())
            assertFalse(a.bloqueado(zombie.getPosicao().getX(), zombie.getPosicao().getY()));
    }

    @Test void everyCarvedFloorIsReachableFromThePlayer() {
        for (int seed = 0; seed < 30; seed++) {
            Mundo world = generated(seed);
            Set<Ponto2D> seen = new HashSet<>();
            ArrayDeque<Ponto2D> queue = new ArrayDeque<>();
            queue.add(new Ponto2D(8, 5));
            while (!queue.isEmpty()) {
                Ponto2D p = queue.remove();
                if (world.bloqueado(p.getX(), p.getY()) || !seen.add(p)) continue;
                queue.add(new Ponto2D(p.getX()+1, p.getY()));
                queue.add(new Ponto2D(p.getX()-1, p.getY()));
                queue.add(new Ponto2D(p.getX(), p.getY()+1));
                queue.add(new Ponto2D(p.getX(), p.getY()-1));
            }
            for (int x = 0; x < 17; x++) for (int y = 0; y < 11; y++)
                assertEquals(!world.bloqueado(x, y), seen.contains(new Ponto2D(x, y)));
        }
    }

    @Test void invalidGenerationFailsFastInsteadOfHanging() {
        assertThrows(IllegalArgumentException.class, () -> new MundoBuilder(2, 3));
        assertThrows(IllegalStateException.class, () -> new MundoBuilder(3, 3).build());
        MundoBuilder walls = new MundoBuilder(3, 3).preencher('#', true);
        assertThrows(IllegalStateException.class, () -> walls.criarCriaturas(1));
        assertThrows(IllegalArgumentException.class, () -> walls.criarCriaturas(-1));
        assertThrows(IllegalArgumentException.class, () -> walls.criarCaminho(0, 1, 5));
        assertThrows(IllegalArgumentException.class, () -> walls.criarCaminho(1, 1, -1));
    }

    @Test void smallestMapSupportsStackedZombiesWithoutRetries() {
        Mundo world = new MundoBuilder(3, 3, new Random(1)).preencher('#', true)
                .criarCaminho(1, 1, 0).criarCriaturas(20).build();
        assertEquals(20, world.getCriaturas().size());
        assertTrue(world.getCriaturas().stream().allMatch(z -> z.getPosicao().equals(new Ponto2D(1, 1))));
    }
}
