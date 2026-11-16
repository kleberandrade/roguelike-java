package br.unisal.aula.domain;

import java.util.List;
import java.util.Objects;

public class Mundo {
    private final Celula[][] mapa;
    private final List<Personagem> criaturas;
    private Jogador jogador;

    public Mundo(Celula[][] mapa, List<Personagem> criaturas) {
        this.mapa = mapa;
        this.criaturas = List.copyOf(criaturas);
    }

    public void setJogador(Jogador jogador) { this.jogador = Objects.requireNonNull(jogador); }
    public Jogador getJogador() { return jogador; }
    public List<Personagem> getCriaturas() { return criaturas; }
    public int getLargura() { return mapa.length; }
    public int getAltura() { return mapa[0].length; }
    public char simbolo(int x, int y) { return mapa[x][y].getSimbolo(); }

    public boolean bloqueado(int x, int y) {
        return x < 0 || y < 0 || x >= getLargura() || y >= getAltura()
                || mapa[x][y].isBloqueado();
    }

    public void atualizar(String comando) {
        if (!jogador.vivo()) return;
        jogador.atualizar(this, comando);
        for (Personagem criatura : criaturas) {
            criatura.atualizar(this);
            if (criatura.getPosicao().equals(jogador.getPosicao())) jogador.tomarDano(1);
        }
    }
}
