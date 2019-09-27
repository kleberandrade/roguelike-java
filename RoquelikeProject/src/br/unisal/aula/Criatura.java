/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unisal.aula;

/**
 *
 * @author pdjkl
 */
public class Criatura extends Personagem {

    private static final int FICAR_PARADO = 0;
    private static final int MOVER_BAIXO = 1;
    private static final int MOVER_CIMA = 2;
    private static final int MOVER_DIREITA = 3;
    private static final int MOVER_ESQUERDA = 4;
    private static final int QUANTIDADE_MOVIMENTOS = 5;

    public Criatura(Ponto2D posicao, char simbolo) {
        super(posicao, simbolo);
    }

    /**
     * Implementação do método abstrato atualizar
     *
     * @param mundo: mundo em que o personagem esta no momento
     */
    @Override
    public void atualizar(Mundo mundo) {

        // criar um número aleatório entre 0 e 100,
        // logo em seguida, calcula o resto da divisão por 5, 
        // ou seja, só podemos ter os seguintes valores: 0, 1, 2, 3 e 4
        int direcao = (int) (Math.random() * 1000 % QUANTIDADE_MOVIMENTOS);

        // desloca a criatura conforme o valor aleatório gerado
        if (direcao == FICAR_PARADO) {
            mover(mundo, 0, 0);
        } else if (direcao == MOVER_BAIXO) {
            mover(mundo, 0, 1);
        } else if (direcao == MOVER_CIMA) {
            mover(mundo, 0, -1);
        } else if (direcao == MOVER_DIREITA) {
            mover(mundo, 1, 0);
        } else if (direcao == MOVER_ESQUERDA) {
            mover(mundo, -1, 0);
        }
    }
}
