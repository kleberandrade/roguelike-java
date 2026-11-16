package br.unisal.aula.generation;

import br.unisal.aula.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;

/** Carves a connected cave using a random walk; zombies may share floor cells. */
public class MundoBuilder {
    private final int largura, altura;
    private final RandomGenerator random;
    private Celula[][] mapa;
    private final List<Personagem> criaturas = new ArrayList<>();

    public MundoBuilder(int largura, int altura) { this(largura, altura, new Random()); }

    public MundoBuilder(int largura, int altura, RandomGenerator random) {
        if (largura < 3 || altura < 3) throw new IllegalArgumentException("Map must be at least 3 x 3");
        this.largura = largura;
        this.altura = altura;
        this.random = Objects.requireNonNull(random);
    }

    public MundoBuilder preencher(char simbolo, boolean bloqueado) {
        mapa = new Celula[largura][altura];
        criaturas.clear();
        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++)
                mapa[x][y] = new Celula(bloqueado, new Ponto2D(x, y), simbolo);
        }
        return this;
    }

    public MundoBuilder criarCaminho(int x, int y, int passos) {
        requireWalk(x, y, passos);
        abrir(x, y);
        for (int i = 0; i < passos; i++) {
            int direcao = random.nextInt(4);
            if (direcao == 0) x = Math.min(x + 1, largura - 2);
            if (direcao == 1) x = Math.max(x - 1, 1);
            if (direcao == 2) y = Math.min(y + 1, altura - 2);
            if (direcao == 3) y = Math.max(y - 1, 1);
            abrir(x, y);
        }
        return this;
    }

    private void requireWalk(int x, int y, int passos) {
        requireMap();
        if (x <= 0 || y <= 0 || x >= largura - 1 || y >= altura - 1 || passos < 0)
            throw new IllegalArgumentException("Walk must start inside the border, with nonnegative steps");
    }

    private void abrir(int x, int y) {
        mapa[x][y].setSimbolo('.');
        mapa[x][y].setBloqueado(false);
    }

    public MundoBuilder criarCriaturas(int quantidade) {
        requireMap();
        if (quantidade < 0) throw new IllegalArgumentException("Zombie count cannot be negative");
        List<Ponto2D> floors = floorPositions();
        if (quantidade > 0 && floors.isEmpty()) throw new IllegalStateException("Carve floor before spawning zombies");
        for (int i = 0; i < quantidade; i++) {
            Ponto2D p = floors.get(random.nextInt(floors.size()));
            criaturas.add(new Zumbi(new Ponto2D(p.getX(), p.getY()), random));
        }
        return this;
    }

    private List<Ponto2D> floorPositions() {
        List<Ponto2D> floors = new ArrayList<>();
        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++)
                if (!mapa[x][y].isBloqueado()) floors.add(new Ponto2D(x, y));
        }
        return floors;
    }

    private void requireMap() {
        if (mapa == null) throw new IllegalStateException("Fill the map first");
    }

    public Mundo build() {
        requireMap();
        return new Mundo(mapa, criaturas);
    }
}
