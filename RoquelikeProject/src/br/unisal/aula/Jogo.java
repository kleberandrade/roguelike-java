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
public class Jogo {
    
    private Mundo mundo;
    private Jogador jogador;
    private int largura, altura;
    
    public Jogo(int largura, int altura) {
        this.largura = largura;
        this.altura = altura;
    }
    
    void criarMundo() {
        
        int x = largura / 2;
        int y = altura / 2;
        int passo = 2000;
        
        jogador = new Jogador(3, new Ponto2D(x, y), 'J');
        mundo = new MundoBuilder(largura, altura)
                .preencher('#', true)
                .criarCaminho(x, y, passo)
                .criarCriaturas(20)
                .build();
        
        mundo.setJogador(jogador);
        
    }
    
    public void executar() {
        criarMundo();
        while (jogador.vivo()) {
            mundo.desenhar();
            mundo.atualizar();
        }
    }
}
