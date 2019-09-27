package br.unisal.aula;

import java.util.ArrayList;
import java.util.List;

public class MundoBuilder {

    private final int largura, altura;
    private Celula[][] mapa;
    private List<Personagem> criaturas = new ArrayList<>();

    public MundoBuilder(int largura, int altura) {
        this.largura = largura;
        this.altura = altura;

    }

    MundoBuilder preencher(char simbolo, boolean bloqueado) {
        mapa = new Celula[largura][altura];
        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++) {
                Ponto2D posicao = new Ponto2D(x, y);
                mapa[x][y] = new Celula(bloqueado, posicao, simbolo);
            }
        }
        return this;
    }

    MundoBuilder criarCaminho(int x, int y, int passos) {
        mapa[x][y].setSimbolo('.');
        mapa[x][y].setBloqueado(false);

        for (int i = 0; i < passos; i++) {
            int direcao = (int) (Math.random() * 1000) % 4;
            if (direcao == 0 && (x + 1) < largura - 1) {
                x++;
            } else if (direcao == 1 && (x - 1) > 0) {
                x--;
            } else if (direcao == 2 && (y + 1) < altura - 1) {
                y++;
            } else if (direcao == 3 && (y - 1) > 0) {
                y--;
            }

            mapa[x][y].setSimbolo('.');
            mapa[x][y].setBloqueado(false);
        }

        return this;
    }

    // Método que adiciona criaturas no mapa
    // @quantidadeCriaturas : número de criaturas que queremos colocar no mapa
    MundoBuilder criarCriaturas(int quantidadeCriaturas) {
        int x, y;
        // Cria N criaturas
        for (int i = 0; i < quantidadeCriaturas; i++) {

            // Impede que uma criatura seja criada em cima de uma parede
            do {
                x = (int) (Math.random() * 1000 % largura);
                y = (int) (Math.random() * 1000 % altura);
            } while (mapa[x][y].isBloqueado());

            // Adiciona a criatura na lista de criaturas
            Criatura zumbi = new Zumbi(new Ponto2D(x, y)); 
            criaturas.add(zumbi);
        }
        return this;
    }

    public Mundo build() {
        return new Mundo(mapa, criaturas);
    }
}
