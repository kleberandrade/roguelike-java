package br.unisal.aula;

public class Celula extends Entidade {
    
    private boolean bloqueado;

    public Celula(boolean bloqueado, Ponto2D posicao, char simbolo) {
        super(posicao, simbolo);
        this.bloqueado = bloqueado;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }
}
