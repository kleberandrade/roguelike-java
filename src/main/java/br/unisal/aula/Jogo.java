package br.unisal.aula;

import br.unisal.aula.domain.*;
import br.unisal.aula.generation.MundoBuilder;

import br.unisal.aula.io.ConsoleGame;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.random.RandomGenerator;

/** Composition root for the original 3-life, 20-zombie survival game. */
public class Jogo {
    private final int largura, altura;
    private final RandomGenerator random;

    public Jogo(int largura, int altura) { this(largura, altura, new Random()); }

    public Jogo(int largura, int altura, RandomGenerator random) {
        this.largura = largura;
        this.altura = altura;
        this.random = random;
    }

    public Mundo criarMundo() {
        int x = largura / 2, y = altura / 2;
        Mundo world = new MundoBuilder(largura, altura, random).preencher('#', true)
                .criarCaminho(x, y, 2000).criarCriaturas(20).build();
        world.setJogador(new Jogador(3, new Ponto2D(x, y), 'J'));
        return world;
    }

    public void executar() {
        new ConsoleGame(new InputStreamReader(System.in, StandardCharsets.UTF_8),
                new PrintWriter(System.out, true)).executar(criarMundo());
    }
}
