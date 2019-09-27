/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unisal.aula;

import java.util.Scanner;

public class Jogador extends Personagem {

    private int vidas;
    private int passos;
    private Scanner entrada;

    public Jogador(int vidas, Ponto2D posicao, char simbolo) {
        super(posicao, simbolo);
        this.vidas = vidas;
        this.passos = 0;
        this.entrada = new Scanner(System.in);
    }

    public boolean vivo() {
        return vidas > 0;
    }

    public void tomarDano(int dano) {
        vidas -= dano;
    }

    @Override
    public void atualizar(Mundo mundo) {
        System.out.print("[A/W/S/D]: ");
        String comando = entrada.nextLine();
        if (comando.toUpperCase().trim().equals("A")) {
            mover(mundo, -1, 0);
        }
        if (comando.toUpperCase().trim().equals("W")) {
            mover(mundo, 0, -1);
        }
        if (comando.toUpperCase().trim().equals("S")) {
            mover(mundo, 0, 1);
        }
        if (comando.toUpperCase().trim().equals("D")) {
            mover(mundo, 1, 0);
        }

        passos++;
    }
}
