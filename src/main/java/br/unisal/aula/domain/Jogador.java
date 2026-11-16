package br.unisal.aula.domain;

import java.util.Locale;

public class Jogador extends Personagem {
    private int vidas;
    private int passos;

    public Jogador(int vidas, Ponto2D posicao, char simbolo) {
        super(posicao, simbolo);
        if (vidas < 1) throw new IllegalArgumentException("Lives must be positive");
        this.vidas = vidas;
    }

    public boolean vivo() { return vidas > 0; }
    public int getVidas() { return vidas; }
    public int getPassos() { return passos; }

    public void tomarDano(int dano) {
        if (dano < 0) throw new IllegalArgumentException("Damage cannot be negative");
        vidas = Math.max(0, vidas - dano);
    }

    public void atualizar(Mundo mundo, String comando) {
        switch (comando.trim().toUpperCase(Locale.ROOT)) {
            case "A" -> mover(mundo, -1, 0);
            case "W" -> mover(mundo, 0, -1);
            case "S" -> mover(mundo, 0, 1);
            case "D" -> mover(mundo, 1, 0);
            default -> { /* Unknown input is a waiting turn, as in the original game. */ }
        }
        passos++;
    }
}
