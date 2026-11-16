package br.unisal.aula;

public class Entidade {

    protected Ponto2D posicao;
    protected char simbolo;

    public Entidade(Ponto2D posicao, char simbolo) {
        this.posicao = posicao;
        this.simbolo = simbolo;
    }

    @Override
    public String toString() {
        return String.valueOf(simbolo);
    }

    public Ponto2D getPosicao() {
        return posicao;
    }

    public void setPosicao(Ponto2D posicao) {
        this.posicao = posicao;
    }

    public char getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(char simbolo) {
        this.simbolo = simbolo;
    }
}
