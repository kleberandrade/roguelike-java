package br.unisal.aula.domain;

import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;

public class Zumbi extends Personagem {
    public static final char SIMBOLO = 'Z';
    private static final int[][] MOVES = {{0, 0}, {-1, 0}, {0, -1}, {0, 1}, {1, 0}};
    private final RandomGenerator random;

    public Zumbi(Ponto2D posicao) { this(posicao, new Random()); }

    public Zumbi(Ponto2D posicao, RandomGenerator random) {
        super(posicao, SIMBOLO);
        this.random = Objects.requireNonNull(random);
    }

    @Override public void atualizar(Mundo mundo) {
        int[] move = MOVES[random.nextInt(MOVES.length)];
        mover(mundo, move[0], move[1]);
    }
}
