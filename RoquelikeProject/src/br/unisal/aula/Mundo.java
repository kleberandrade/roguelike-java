/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unisal.aula;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pdjkl
 */
public class Mundo {

    private Celula[][] mapa;
    private List<Personagem> criaturas;
    private Jogador jogador;

    public void setJogador(Jogador jogador) {
        this.jogador = jogador;
    }

    public Mundo(Celula[][] mapa, List<Personagem> criaturas) {
        this.mapa = mapa;
        this.criaturas = criaturas;
    }

    public boolean bloqueado(int x, int y) {
        return mapa[x][y].isBloqueado();
    }

    public void atualizar() {
        jogador.atualizar(this);
        for (Personagem criatura : criaturas) {
            criatura.atualizar(this);
            if (criatura.posicao.getX() == jogador.posicao.getX()
                    && criatura.posicao.getY() == jogador.posicao.getY()) {
                jogador.tomarDano(1);
            }
        }
    }

    public void desenhar() {

        // Criar um mapa de criaturas baseado em suas posições
        Map<String, Personagem> map = new HashMap<>();
        for (Personagem personagem : criaturas) {
            map.put(personagem.posicao.toString(), personagem);
        }

        // Adicionamos também o jogador no mapa
        map.put(jogador.posicao.toString(), jogador);

        for (int y = 0; y < mapa[0].length; y++) {
            for (int x = 0; x < mapa.length; x++) {
                Ponto2D posicao = new Ponto2D(x, y);
                if (map.containsKey(posicao.toString())) {
                    Personagem personagem = (Personagem) map.get(posicao.toString());
                    System.out.print(personagem);
                } else {
                    System.out.print(mapa[x][y].simbolo);
                }
            }
            System.out.println();
        }
    }
}
